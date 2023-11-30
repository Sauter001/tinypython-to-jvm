import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.ParseTreeWalker;

public class Main {
    public static void main(String[] args) throws Exception {
        tinyPythonLexer lexer = new tinyPythonLexer(CharStreams.fromFileName("Test.tpy"));
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        tinyPythonParser parser = new tinyPythonParser(tokens);
        ParseTree tree = parser.program();

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new tinyPythonCompiler(), tree);
    }
}