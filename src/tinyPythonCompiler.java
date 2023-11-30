import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.io.*;

public class tinyPythonCompiler extends tinyPythonBaseListener {
    ParseTreeProperty<String> convertedProperty = new ParseTreeProperty<>(); // 바뀐 출력을 저장하는 property

    @Override
    public void exitFile_input(tinyPythonParser.File_inputContext ctx) {
        super.exitFile_input(ctx);

        for (int i = 0; i < ctx.stmt().size(); ++i) {
            System.out.println(convertedProperty.get(ctx.stmt(i)));
            convertedProperty.put(ctx, convertedProperty.get(ctx.stmt(i)));
        }

    }

    @Override
    public void exitProgram(tinyPythonParser.ProgramContext ctx) throws IOException {
        super.exitProgram(ctx);
        final String BASE_JVM =
                ".class public Sum\n" +
                        ".super java/lang/Object\n" +
                        ".method public <init>()V\n" +
                        "    aload_0\n" +
                        "    invokenonvirtual java/lang/Object/<init>()V\n" +
                        "    return\n" +
                        ".end method\n";
        System.out.println(ctx.getText());

        File outputFile = new File("Test.j");
        if (!outputFile.exists()) outputFile.createNewFile();
        BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
        writer.write(BASE_JVM + convertedProperty.get(ctx.file_input()) + ".end method\n");
        writer.flush();
        writer.close();
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
}
