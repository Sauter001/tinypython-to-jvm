import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.io.*;
import java.util.HashMap;
import java.util.Stack;

public class tinyPythonCompiler extends tinyPythonBaseListener {
    ParseTreeProperty<String> convertedProperty = new ParseTreeProperty<>(); // 바뀐 출력을 저장하는 property
    private final Stack<HashMap<String, Symbol>> symbolTable = new Stack<>(); //
    private final Stack<String[]> loopState = new Stack<>(); // 이중 반복을 고려하기 위해
    private final int BASE_SIZE = 16; // stack, local 변수 배열의 기본 크기
    private int labelIdx = 0; // branch 처리를 위한 label 에 부여할 번호


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
        // Test.j에 java bytecode로 변환된 텍스트를 쓴다.
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

        String DEFS_SNIPPET = convertedProperty.get(ctx.defs());

        // main method의 header
        final String MAIN_SNIPPET =
                ".method public static main([Ljava/lang/String;)V\n" +
                        "    .limit stack " + BASE_SIZE + "\n" + // stack과 local은 기본적으로 16으로 할당
                        "    .limit locals " + BASE_SIZE + "\n";

        // 입력의 각 statement 변환
        StringBuilder mainBody = new StringBuilder();
        System.out.println("<Main body>");
        // 변환된 stmt 모두 결합
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
        // compound statement는 if문과 while문이 있다.
        if (ctx.if_stmt() != null) {
            convertedProperty.put(ctx, convertedProperty.get(ctx.if_stmt())); // 변환된 if문 format 저장
        } else if (ctx.while_stmt() != null) {
            convertedProperty.put(ctx, convertedProperty.get(ctx.while_stmt())); // 변환된 while format 저장
        }
    }

    /**
     * small statement 종료 시 처리되는 메서드
     */
    @Override
    public void exitSmall_stmt(tinyPythonParser.Small_stmtContext ctx) {
        super.exitSmall_stmt(ctx);
        // small statement가 assignment 문에 해당하는 경우 변환된 format 저장
        if (ctx.assignment_stmt() != null) {
            convertedProperty.put(ctx, convertedProperty.get(ctx.assignment_stmt()));
        } else if (ctx.print_stmt() != null) {
            // small statement가 print문에 해당하는 경우 동일 과정
            convertedProperty.put(ctx, convertedProperty.get(ctx.print_stmt()));
        } else if (ctx.flow_stmt() != null) {
            // small statement가 흐름 제어문에 해당하는 경우
            convertedProperty.put(ctx, convertedProperty.get(ctx.flow_stmt()));
        } else if (ctx.return_stmt() != null) {
            // small statement가 return문에 해당하는 경우
            convertedProperty.put(ctx, convertedProperty.get(ctx.return_stmt()));
        }
    }

    // 할당 연산 처리
    @Override
    public void exitAssignment_stmt(tinyPythonParser.Assignment_stmtContext ctx) {
        super.exitAssignment_stmt(ctx);
        // 예시 a = 10
        String ident = ctx.NAME().getText(); // a: 변수 이름
        String expr = convertedProperty.get(ctx.expr()); // expression

        // ident가 새로운 변수인지 판단
        boolean isNewIdentifier = !symbolTable.peek().containsKey(ident);
        if (isNewIdentifier) {
            int index = symbolTable.peek().size(); // index 증가
            symbolTable.peek().put(ident, new Symbol("int", index)); // 정수 추가
        }
        // 할당문에 대한 bytecode 생성 및 저장
        String code = expr + "istore " + (symbolTable.peek().get(ident).getIndex()) + "\n";
        convertedProperty.put(ctx, code);
    }

    @Override
    public void exitExpr(tinyPythonParser.ExprContext ctx) {
        super.exitExpr(ctx);
        String code = "";

        if (ctx.NUMBER() != null) {
            code = "ldc " + ctx.NUMBER().getText() + "\n"; // 숫자 literal일 때 처리
        } else if (ctx.NAME() != null) {
            // Name이 있는 것은 함수 호출의 의미 또는 변수 가져오는 경우
            String ident = ctx.NAME().getText();
            if (ctx.opt_paren().CLOSE_PAREN() != null) {
                // identifier에 ()이 포함되면 함수 호출
                code = makeFunctionCall(ident, ctx.opt_paren());
            } else {
                // 단순히 변수를 가져오는 경우
                Symbol symbol = symbolTable.peek().get(ident); // identifier에 든 값을 operand stack에 로드
                if (symbol != null) {
                    code = "iload " + symbol.getIndex() + "\n";
                } else {
                    System.out.println("undefined identifier");
                }
            }
        } else if (ctx.expr().size() == 1) {
            // 단항 연산
            code = convertedProperty.get(ctx.expr(0));
        } else if (ctx.expr().size() == 2) {
            // 이항 연산
            String lExpr = convertedProperty.get(ctx.expr(0));
            String rExpr = convertedProperty.get(ctx.expr(1));
            String op = ctx.getChild(1).getText(); // 연산자
            code = lExpr + rExpr + convertOpToInst(op); // 연산자 op에 따른 명령어
        }

        convertedProperty.put(ctx, code);
    }

    // 연산자를 기준으로 bytecode로 변환한다
    private String convertOpToInst(String op) {

        if (op.equals("+")) {
            return "iadd\n";
        } else if (op.equals("-")) {
            return "isub\n";
        }
        return "";
    }

    private String makeFunctionCall(String ident, tinyPythonParser.Opt_parenContext optParen) {
        int numOfArgs = optParen.expr().size();
        StringBuilder code = new StringBuilder();
        String signature = "";

        // 인자 전달이 없는 경우 expr 변환 과정 생략한다
        // 인지가 있으면 해당 인자만큼 I를 추가한다. (인자는 int 뿐임)
        if (numOfArgs > 0) {
            for (tinyPythonParser.ExprContext expr : optParen.expr()) {
                code.append(convertedProperty.get(expr));
                signature += "I";
            }
        }

        // signature 정의
        signature = ident + "(" + signature + ")I"; // 기본 return type는 int라고 가정
        code.append("invokestatic Test/").append(signature).append("\n");
        return code.toString();
    }

    // 출력 처리
    @Override
    public void exitPrint_stmt(tinyPythonParser.Print_stmtContext ctx) {
        super.exitPrint_stmt(ctx);
        // System.out 인스턴스 가져오기
        final String getPrintStreamInst = "getstatic java/lang/System/out Ljava/io/PrintStream;\n";
        final String printArg = convertedProperty.get(ctx.print_arg()); // 출력 인자값 가져오기
        String getDoPrintInst;

        if (ctx.print_arg().expr() != null) {
            // 인자가 expression인 경우
            // 인자를 정수로 받는 println 호출
            getDoPrintInst = "invokevirtual java/io/PrintStream/println(I)V\n";
        } else {
            // 인자가 string인 경우
            // 인자를 string으로 받는 println 호출
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
        StringBuilder defs = new StringBuilder();

        for (int i = 0; i < ctx.def_stmt().size(); ++i) {
            // 각 함수 정의가 만든 bytecode 추가
            defs.append(convertedProperty.get(ctx.def_stmt(i)));
        }

        convertedProperty.put(ctx, defs.toString());
    }

    // 함수 정의 시작 시
    @Override
    public void enterDef_stmt(tinyPythonParser.Def_stmtContext ctx) {
        super.enterDef_stmt(ctx);

        String funcName = ctx.NAME().getText(); // 함수명
        System.out.println("<Def " + funcName + ">");
        symbolTable.push(new HashMap<>()); // 새 symbol table 추가

        // 새 symbol에 인자 정보 추가
        HashMap<String, Symbol> localSymbol = symbolTable.peek();

        if (!ctx.args().NAME().isEmpty()) {
            // parameter가 있는 경우 현재 symbol 테이블에 추가한다.
            for (int i = 0; i < ctx.args().NAME().size(); ++i) {
                localSymbol.put(ctx.args().NAME(i).getText(), new Symbol("int", i));
            }
        }
    }

    // 함수 정의 처리 끝날 시
    @Override
    public void exitDef_stmt(tinyPythonParser.Def_stmtContext ctx) {
        super.exitDef_stmt(ctx);

        String funcName = ctx.NAME().getText(); // 함수명
        String code = convertedProperty.get(ctx.suite()); // 함수의 body
        String numOfArgs = ""; // 인자 수

        // 함수 인자수 표기
        for (int i = 0; i < ctx.args().NAME().size(); ++i) {
            numOfArgs += "I";
        }

        // 함수 정의에 대한 bytecode 생성함
        // static method만 존재 및 int만 반환한다고 가정함
        String definition = ".method public static " + funcName + "(" + numOfArgs + ")I\n " +
                "    .limit stack " + BASE_SIZE + "\n" +
                "    .limit locals " + BASE_SIZE + "\n" +
                code +
                ".end method\n\n";

        symbolTable.pop(); // symbol table 종료
        convertedProperty.put(ctx, definition); // 생성된 bytecode 저장
        System.out.println("<End def>\n");
    }

    // return문 처리
    @Override
    public void exitReturn_stmt(tinyPythonParser.Return_stmtContext ctx) {
        super.exitReturn_stmt(ctx);
        String expr = convertedProperty.get(ctx.expr());
        String code = expr + "ireturn\n"; // return type은 정수형만 한다고 가정
        convertedProperty.put(ctx, code);
    }

    // suite 처리
    @Override
    public void exitSuite(tinyPythonParser.SuiteContext ctx) {
        super.exitSuite(ctx);
        System.out.println("[Suite ends]");
        if (ctx.simple_stmt() != null) {
            // simple statement 처리
            convertedProperty.put(ctx, convertedProperty.get(ctx.simple_stmt()));
        } else {
            // 여러 statement가 결합된 코드의 처리
            StringBuilder code = new StringBuilder();
            for (tinyPythonParser.StmtContext stmt : ctx.stmt()) {
                code.append(convertedProperty.get(stmt)); // bytecode로 변환된 statements 추가
            }
            convertedProperty.put(ctx, code.toString());
        }
    }

    // 조건문에 대한 처리
    @Override
    public void exitIf_stmt(tinyPythonParser.If_stmtContext ctx) {
        super.exitIf_stmt(ctx);

        String ifTest = convertedProperty.get(ctx.test(0)); // if test 결과
        String ifSuite = convertedProperty.get(ctx.suite(0)); // if에서 처리되는 suite
        String escLabel = "Esc" + labelIdx; // 탈출 라벨
        String nextBranch = "Branch" + (labelIdx++); // elif, else로 분기시 사용되는 라벨
        StringBuilder code = new StringBuilder();

        // if문 bytecode 변환
        code.append(ifTest).append(nextBranch).append("\n") // 조건 확인
                .append(ifSuite) // if 만족시 실행부분
                .append("goto ").append(escLabel).append("\n") // body 실행 후 탈출하는 라벨
                .append(nextBranch).append(":\n"); // 다음 branch 이동

        // test의 수가 suite의 수보다 적으면 else가 들어갔다는 의미이다.
        boolean isElseIncluded = ctx.test().size() < ctx.suite().size();

        // elif 처리
        for (int i = 1; i < ctx.test().size(); ++i) {
            String elifTest = convertedProperty.get(ctx.test(i)); //elif test 결과
            String elifSuite = convertedProperty.get(ctx.suite(i)); // elif에서 처리되는 suite
            nextBranch = (i == ctx.test().size() - 1 && !isElseIncluded) ? escLabel : "Branch" + (labelIdx++);

            // elif 조건 및 body 처리
            code.append(elifTest).append(nextBranch).append("\n")
                    .append(elifSuite)
                    .append("goto ").append(escLabel).append("\n")
                    .append(nextBranch).append(":\n");
        }

        // else 처리
        if (isElseIncluded) {
            String elseSuiteCode = convertedProperty.get(ctx.suite(ctx.suite().size() - 1));
            code.append(elseSuiteCode);
        }

        // end label 처리
        code.append(escLabel).append(":\n");
        convertedProperty.put(ctx, code.toString());
    }

    // 비교 결과인 test의 처리
    @Override
    public void exitTest(tinyPythonParser.TestContext ctx) {
        super.exitTest(ctx);
        String e1 = convertedProperty.get(ctx.expr(0)); // 비교 대상인 첫번째 expression
        String e2 = convertedProperty.get(ctx.expr(1)); // 비교 대상인 두번째 expression
        String op = ctx.comp_op().getText(); // 비교 연산자
        String inst;
        StringBuilder code = new StringBuilder();

        code.append(e1).append(e2); // 비교할 두 변환된 expression 추가

        // 조견 연산자 따른 branch 구문 추가
        // 이때 표시된 연산자와 정반대의 기능을 하는 것으로 변환한다.
        switch (op) {
            case "==" -> inst = "if_icmpne ";
            case "!=" -> inst = "if_icmpeq ";
            case "<" -> inst = "if_icmpge ";
            case ">" -> inst = "if_icmple ";
            case "<=" -> inst = "if_icmpgt ";
            case ">=" -> inst = "if_icmplt ";
            default -> inst = "";
        }
        code.append(inst);
        convertedProperty.put(ctx, code.toString());
    }

    // While loop의 시작 처리
    @Override
    public void enterWhile_stmt(tinyPythonParser.While_stmtContext ctx) {
        super.enterWhile_stmt(ctx);
        // 새 loop 라벨 생성
        String headLabel = "LoopHead" + labelIdx;
        String endLabel = "LoopEnd" + (labelIdx++);
        loopState.push(new String[]{headLabel, endLabel}); // 새 loop label 추가
    }

    // while loop의 종료 처리
    @Override
    public void exitWhile_stmt(tinyPythonParser.While_stmtContext ctx) {
        super.exitWhile_stmt(ctx);

        System.out.println("[While loop]");
        // 현재 loop의 시작, 종료 레이블 가져오기
        String headLabel = loopState.peek()[0];
        String endLabel = loopState.peek()[1];

        String loopTest = convertedProperty.get(ctx.test());
        String loopSuite = convertedProperty.get(ctx.suite());
        StringBuilder code = new StringBuilder();
        code.append(headLabel).append(":\n")
                .append(loopTest).append(endLabel).append("\n")
                .append(loopSuite)
                .append("goto ").append(headLabel).append("\n")// Continue loop
                .append(endLabel).append(":\n");

        convertedProperty.put(ctx, code.toString());
    }

    // flow statement 처리
    @Override
    public void exitFlow_stmt(tinyPythonParser.Flow_stmtContext ctx) {
        super.exitFlow_stmt(ctx);
        // flow 문은 break이나 continue 문일 수 있다.
        if (ctx.break_stmt() != null) {
            // break문일 때 처리
            convertedProperty.put(ctx, convertedProperty.get(ctx.break_stmt()));
        } else if (ctx.continue_stmt() != null) {
            // continue문일 때 처리
            convertedProperty.put(ctx, convertedProperty.get(ctx.continue_stmt()));
        }
    }

    // break 처리
    @Override
    public void exitBreak_stmt(tinyPythonParser.Break_stmtContext ctx) {
        super.exitBreak_stmt(ctx);
        System.out.println("[break 진입] & " + loopState.isEmpty());
        if (!loopState.isEmpty()) {
            String endLabel = loopState.peek()[1]; // 현재 반복의 종료 label로 점프한다.
            convertedProperty.put(ctx, "goto " + endLabel + "\n");
        } else {
            convertedProperty.put(ctx, "");
        }
    }

    // continue 처리
    @Override
    public void exitContinue_stmt(tinyPythonParser.Continue_stmtContext ctx) {
        super.exitContinue_stmt(ctx);
        if (!loopState.isEmpty()) {
            String headLabel = loopState.peek()[0];
            convertedProperty.put(ctx, "goto " + headLabel + "\n");
        } else {
            convertedProperty.put(ctx, "");
        }
    }
}
