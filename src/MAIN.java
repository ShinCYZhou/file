import java.io.InputStream;
import java.io.FileInputStream;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;
import org.antlr.v4.gui.TreeViewer;

import java.util.Arrays;
import javax.swing.*;



public class MAIN {
    private ParseTreeWalker walker;

    public static void main(String[] args) throws Exception {
		
        //read the java file,args are the parameters
        String inputFile;
        if (args.length > 0) {
            inputFile = args[0];
        }
        else {
            inputFile="";
            System.out.println("Please enter the inputFile name!.");
        }
        InputStream s = new FileInputStream(inputFile);
        System.out.println(inputFile + " is successfully loaded.");

        // processing
        ANTLRInputStream input = new ANTLRInputStream(s);
        MiniJavaLexer lexer = new MiniJavaLexer(input);//lexical analysis
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        MiniJavaParser parser = new MiniJavaParser(tokens);//parsing analysis
        ParseTree tree = parser.prog();//build AST

        // Semantic check
        ParseTreeWalker w = new ParseTreeWalker();
        FirstTraverse first = new FirstTraverse();
        w.walk(first, tree);
        SecondTraverse second = new SecondTraverse(first.getGlobalScope(), first.getLocalScopes(), first.getClassScopes());
        w.walk(second, tree);


        // show AST in GUI
        // reference:  https://www.programcreek.com/java-api-examples/index.php?api=org.antlr.v4.runtime.tree.gui.TreeViewer
        JFrame frame = new JFrame("AST");
        JPanel panel = new JPanel();
        TreeViewer viewer = new TreeViewer(Arrays.asList(parser.getRuleNames()), tree);
        viewer.setScale(1.0);
        panel.add(viewer);
        //scroll bar
        JScrollPane scrollPanel = new JScrollPane(panel, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        frame.getContentPane().add(scrollPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setSize(1800, 800);
        frame.setVisible(true);
    }
}
