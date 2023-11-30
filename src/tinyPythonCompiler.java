import org.antlr.v4.runtime.tree.ParseTreeProperty;

import java.io.*;

public class tinyPythonCompiler extends tinyPythonBaseListener {
    ParseTreeProperty<String> convertedProperty = new ParseTreeProperty<>(); // 바뀐 출력을 저장하는 property

    @Override
    public void exitProgram(tinyPythonParser.ProgramContext ctx) throws IOException {
            super.exitProgram(ctx);
            System.out.println(ctx.getText());

            File outputFile = new File("Test.j");
            if (!outputFile.exists()) outputFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputFile));
            writer.write(ctx.getText());
            writer.flush();
            writer.close();
    }
}
