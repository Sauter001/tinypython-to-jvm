import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.io.*;
import java.util.HashMap;
import java.util.Stack;

public class tinyPythonCompiler extends tinyPythonBaseListener {
    ParseTreeProperty<String> convertedProperty = new ParseTreeProperty<>(); // 바뀐 출력을 저장하는 property
    private final Stack<HashMap<String, Symbol>> symbolTable = new Stack<>();

    // 생성자
    public tinyPythonCompiler() {
        symbolTable.push(new HashMap<>()); // 초기 symbol 추가
        symbolTable.peek().put("args", new Symbol("args", 0));
    }

    @Override
    public void exitProgram(tinyPythonParser.ProgramContext ctx) throws IOException {
        super.exitProgram(ctx);

        // 기본 Java byte code 시작 부분
        final String BASE_JVM =
                ".class public Test\n" +
                        ".super java/lang/Object\n\n" +
                        "; standard initializer\n" +
                        ".method public <init>()V\n" +
                        "aload_0\n" +
                        "invokenonvirtual java/lang/Object/<init>()V\n" +
                        "return\n" +
                        ".end method\n\n";
        convertedProperty.put(ctx, convertedProperty.get(ctx.file_input()));

        String program = BASE_JVM + convertedProperty.get(ctx.file_input());

        File outputFile = new File("Test.j");
        if (!outputFile.exists()) outputFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(program);
        writer.flush();
        writer.close();
    }

    @Override
    public void exitFile_input(tinyPythonParser.File_inputContext ctx) throws IOException {
        super.exitFile_input(ctx);

        // TODO: defs 부분 구성
        String DEFS_SNIPPET = convertedProperty.get(ctx.defs());

        // main method의 header
        final String MAIN_SNIPPET =
                ".method public static main([Ljava/lang/String;)V\n" +
                        "    .limit stack 16\n" + // stack과 local은 기본적으로 16으로 할당
                        "    .limit locals 16\n";

        // 입력의 각 statement 변환
        StringBuilder mainBody = new StringBuilder();
        System.out.println("<Main body>");
        for (int i = 0; i < ctx.stmt().size(); ++i) {
            System.out.println(convertedProperty.get(ctx.stmt(i)));
            mainBody.append(convertedProperty.get(ctx.stmt(i)));
            convertedProperty.put(ctx, convertedProperty.get(ctx.stmt(i)));
        }

        // 전체 JVM 코드 구성하기
        String wholeProgram = DEFS_SNIPPET + MAIN_SNIPPET + mainBody + "return\n.end method";
        convertedProperty.put(ctx, wholeProgram);

        // main symbol stack pop
        if (!symbolTable.isEmpty()) symbolTable.pop();
    }


    @Override
    public void exitStmt(tinyPythonParser.StmtContext ctx) {
        super.exitStmt(ctx);
        if (ctx.simple_stmt() != null)
            // simple statement 인 경우
            // 변환된 simple statement format 저장
            convertedProperty.put(ctx, convertedProperty.get(ctx.simple_stmt()));
        else if (ctx.compound_stmt() != null)
            // compound statement 인 경우
            // 변환된 compound statement format 저장
            convertedProperty.put(ctx, convertedProperty.get(ctx.compound_stmt()));
    }

    @Override
    public void exitSimple_stmt(tinyPythonParser.Simple_stmtContext ctx) {
        super.exitSimple_stmt(ctx);
        // 변환된 small statement format 저장
        convertedProperty.put(ctx, convertedProperty.get(ctx.small_stmt()));
    }

    @Override
    public void exitCompound_stmt(tinyPythonParser.Compound_stmtContext ctx) {
        super.exitCompound_stmt(ctx);
        if (ctx.if_stmt() != null) {
            convertedProperty.put(ctx, convertedProperty.get(ctx.if_stmt())); // 변환된 if문 format 저장
        } else if (ctx.while_stmt() != null) {
            convertedProperty.put(ctx, convertedProperty.get(ctx.while_stmt()));
        }
    }

    /**
     * small statement 종료 시 처리되는 메서드
     */
    @Override
    public void exitSmall_stmt(tinyPythonParser.Small_stmtContext ctx) {
        super.exitSmall_stmt(ctx);
        // small statement가 return 문에 해당하는 경우 변환된 format 저장
        if (ctx.assignment_stmt() != null) {
            convertedProperty.put(ctx, convertedProperty.get(ctx.assignment_stmt()));
        } else if (ctx.print_stmt() != null) {
            convertedProperty.put(ctx, convertedProperty.get(ctx.print_stmt()));
        } else if (ctx.flow_stmt() != null) {
            convertedProperty.put(ctx, convertedProperty.get(ctx.flow_stmt()));
        } else if (ctx.return_stmt() != null) {
            convertedProperty.put(ctx, convertedProperty.get(ctx.return_stmt()));
        }
    }

    // 할당 연산 처리
    @Override
    public void exitAssignment_stmt(tinyPythonParser.Assignment_stmtContext ctx) {
        super.exitAssignment_stmt(ctx);
        // 예시 a = 10
        String ident = ctx.NAME().getText(); // a
        String expr = convertedProperty.get(ctx.expr()); // expression

        // ident가 새로운 변수인지 판단
        boolean isNewIdentifier = !symbolTable.peek().containsKey(ident);
        if (isNewIdentifier) {
            int index = symbolTable.peek().size(); // index 증가
            symbolTable.peek().put(ident, new Symbol("int", index)); // 정수 추가
        }

        String code = expr + "istore " + (symbolTable.peek().get(ident).getIndex()) + "\n";
        convertedProperty.put(ctx, code);
    }

    @Override
    public void exitExpr(tinyPythonParser.ExprContext ctx) {
        super.exitExpr(ctx);
        String code = "";

        if (ctx.NUMBER() != null) {
            code = "ldc " + ctx.NUMBER().getText() + "\n";

        } else if (ctx.NAME() != null) {
            // Name이 있는 것은 함수 호출의 의미 또는 변수 가져오는 경우
            String ident = ctx.NAME().getText();
            if (ctx.opt_paren().CLOSE_PAREN() != null) {
                // identifier에 ()이 포함되면 함수 호출
                code = makeFunctionCall(ident, ctx.opt_paren());
            } else {
                // 변수 가져오는 경우
                Symbol symbol = symbolTable.peek().get(ident); // identifier에 든 값 로드
                if (symbol != null) {
                    code = "iload " + symbol.getIndex() + "\n";
                } else {
                    System.out.println("undefined identifier");
                }
            }
        } else if (ctx.expr().size() == 1) {
            // 단항 연선
            code = convertedProperty.get(ctx.expr(0));
        } else if (ctx.expr().size() == 2) {
            // 이항 연산
            String lExpr = convertedProperty.get(ctx.expr(0));
            String rExpr = convertedProperty.get(ctx.expr(1));
            String op = ctx.getChild(1).getText(); // 연산자
            code = lExpr + rExpr + convertOpToInst(op);
        }

        convertedProperty.put(ctx, code);
    }

    private String convertOpToInst(String op) {
        if (op.equals("+")) {
            return "iadd\n";
        } else if (op.equals("-")) {
            return "isub\n";
        }
        return "";
    }

    private String makeFunctionCall(String ident, tinyPythonParser.Opt_parenContext optParen) {
        return "";
    }

    // 출력 처리
    @Override
    public void exitPrint_stmt(tinyPythonParser.Print_stmtContext ctx) {
        super.exitPrint_stmt(ctx);
        final String getPrintStreamInst = "getstatic java/lang/System/out Ljava/io/PrintStream;\n";
        final String printArg = convertedProperty.get(ctx.print_arg());
        String getDoPrintInst;

        if (ctx.print_arg().expr() != null) {
            getDoPrintInst = "invokevirtual java/io/PrintStream/println(I)V\n";
        } else {
            getDoPrintInst = "invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n";
        }

        convertedProperty.put(ctx, getPrintStreamInst + printArg + getDoPrintInst);
    }

    // 프린트할 인자에 대한 처리
    @Override
    public void exitPrint_arg(tinyPythonParser.Print_argContext ctx) {
        super.exitPrint_arg(ctx);
        if (ctx.expr() != null) {
            // expression을 출력하는 경우
            convertedProperty.put(ctx, convertedProperty.get(ctx.expr()));
        } else {
            // string을 출력하는 경우
            String str = ctx.STRING().getText(); // string literal 가져오기
            String code = "ldc " + str + "\n";
            convertedProperty.put(ctx, code);
        }
    }

    // 함수 정의 부분 처리
    @Override
    public void exitDefs(tinyPythonParser.DefsContext ctx) {
        super.exitDefs(ctx);
        convertedProperty.put(ctx, "\n");
    }
}
