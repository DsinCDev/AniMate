package ui;

import com.github.cliftonlabs.json_simple.JsonObject;
import libs.Executor;
import libs.Parser;
import libs.SimpleTokenizer;
import libs.Tokenizer;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;



public class Main {

    public static void main(String[] args) throws IOException, URISyntaxException {
        List<String> fixedLiterals = Arrays.asList("Canvas", "Shape", "Line", "Group", "Animate", "color=", "z-index=", "rectangle", "circle", "triangle", "hexagon",
                "angle=", "loop=", "rotation=", "ccw", "cw", "[", "]", "{", "}", ",");
        // some literals require a space after them to separate them from names
        List<String> literalsWithSpace = Arrays.asList("Shape", "Line", "Group", "Animate");
        // separators that we don't want to keep as fixed literals should be put at the front of list
        List<String> separators = Arrays.asList("\\n", ",", "{", "}", "[", "]");
        // patterns to match against user defined input
        // whitespace before and after inputs will be trimmed before pattern matching
        // $ is included in patterns to ensure that inputs like "aaaa aaa" are not accepted as 2 different tokens b/c spaces aren't allowed inside our user literals
        // and all user literals are immediately followed by a separator
        List<String> userPatterns = Arrays.asList("([a-zA-z]+)$", "^-?([0-9]+)$", "^#([a-fA-F0-9]{6})$");

        JsonObject parserOutput = new JsonObject();
        try {
            Tokenizer tokenizer = SimpleTokenizer.createSimpleTokenizer("input.tcts", fixedLiterals, literalsWithSpace, separators, userPatterns);
            Parser p = Parser.getParser(tokenizer);
            parserOutput = p.parse();
           // System.out.println("hold");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
            System.exit(1);
        }

            Executor executor = new Executor(parserOutput);
            executor.draw();

    }

}