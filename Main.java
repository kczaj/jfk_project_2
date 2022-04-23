// Intro to ANTLR+LLVM
// sawickib, 2014-04-26

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class Main {
    public static void main(String[] args) throws Exception {
        ANTLRFileStream input = new ANTLRFileStream(args[0]);

        CzajmalLexer lexer = new CzajmalLexer(input);

        CommonTokenStream tokens = new CommonTokenStream(lexer);
        CzajmalParser parser = new CzajmalParser(tokens);

        ParseTree tree = parser.prog();

        //System.out.println(tree.toStringTree(parser));

        ParseTreeWalker walker = new ParseTreeWalker();
        walker.walk(new LLVMActions(), tree);

    }
}
