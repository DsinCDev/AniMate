package libs;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// basic code structure and helper functions has been borrowed from the class examples and exercise 1
public class SimpleTokenizer implements Tokenizer {

    public static Tokenizer createSimpleTokenizer(String filename,List<String> fixedLiterals, List<String> literalsWithSpace, List<String> separators, List<String> userPatterns) throws Exception {
        return new SimpleTokenizer(filename, fixedLiterals, literalsWithSpace, separators, userPatterns);
    }

    private String inputProgram;
    private List<String> fixedLiterals;
    private List<String> literalsWithSpace;
    private List<String> separators;
    private List<String> userPatterns;
    private String[] tokens;


    private int currentToken = 0;

    private SimpleTokenizer(String filename, List<String> fixedLiterals, List<String> literalsWithSpace, List<String> separators, List<String> userPatterns) throws Exception {
        this.fixedLiterals = fixedLiterals;
        this.literalsWithSpace = literalsWithSpace;
        this.separators = separators;
        this.userPatterns = userPatterns;
        try {
            inputProgram = Files.readString(Paths.get(filename));
        } catch (IOException e) {
            System.out.println("Didn't find file");
            System.exit(0);
        }
        tokenize();
    }

    //modifies: this.tokens
    //effects: will result in a list of tokens (sitting at this.tokens) that has no spaces around tokens.
    private void tokenize () throws Exception {
        String tokenizedProgram = inputProgram;
        if (inputProgram == null | inputProgram.length() == 0) {
            writeErrorToHTML("Input is empty");
            throw new Exception("Input is empty");
        }

        List<String> tempTokens = new ArrayList<>();

        // build a regex expression based on separators given
        // in this lang, we want to keep all separators as tokens except for \n
        StringBuilder splitRegex = new StringBuilder();
        for(String s: separators) {
            // if it's the start, don't add | at the beginning
            if (splitRegex.toString().equals("")) {
                // if it's \n, we don't want to keep it so don't lookahead + lookbehind in regex
                if (s.equals("\\n")) {
                    splitRegex.append("("+ s + ")");
                } else {
                    splitRegex.append("((?<=").append("\\").append(s).append(")|(?=").append("\\").append(s).append("))");
                }
            } else {
                if (s.equals("\\n")) {
                    splitRegex.append("|(" + s + ")");
                } else {
                    splitRegex.append("|((?<=").append("\\").append(s).append(")|(?=").append("\\").append(s).append("))");
                }

            }
        }

        // split here on our separators
        tokens = tokenizedProgram.split(splitRegex.toString());

        // trim whitespace around tokens
        for (int i = 0; i < tokens.length; i++) {
            tokens[i] = tokens[i].trim();
        }

        // cycle through all substrings/tokens
        for (int i = 0; i < tokens.length; i++) {
            // keep consuming tokens from string until empty
            while (!tokens[i].equals("")) {
                boolean tokenConsumed = false;
                // if starts with fixed literal, move it into temptokens list and keep going
                for (String f: fixedLiterals) {
                    if (tokens[i].startsWith(f)) {
                        String tokenLeft = tokens[i].substring(f.length());
                        // check if it's a literal that requires a space after it
                        if (literalsWithSpace.indexOf(f) != -1) {
                            // if the rest of the token doesn't start with a space, don't consume fixedliteral
                            if (!tokenLeft.startsWith(" ")) {
                                break;
                            }
                        }
                        tempTokens.add(f);
                        // any extra whitespace between tokens is ignored, so we trim it
                        tokens[i] = tokenLeft.trim();
                        tokenConsumed = true;
                        break;
                    }
                }
                if (tokenConsumed) {
                    continue;
                }
                // if it starts with user input (matches one of patterns in list)
                for (String s: userPatterns) {
                    Pattern p = Pattern.compile(s);
                    Matcher m = p.matcher(tokens[i]);

                    if (m.lookingAt()) {
                        tempTokens.add(m.group());
                        tokens[i] = tokens[i].substring(m.group().length());
                        tokenConsumed = true;
                        break;
                    }
                }
                // if token hasn't been consumed aka not an accepted token
                if (!tokenConsumed) {
                    writeErrorToHTML("Not a valid input: " + tokens[i]);
                    throw new Exception("Not a valid input: " + tokens[i]);
                }
            }
        }

        // transfer tempTokens to tokens[]
        tokens = new String[tempTokens.size()];
        for (int i = 0; i < tempTokens.size(); i++) {
            tokens[i] = tempTokens.get(i);
        }

//        System.out.println(Arrays.asList(tokens));
    }

    private void writeErrorToHTML(String msg) {
        try{
            FileWriter myWriterHTML = new FileWriter("animation.html");
            myWriterHTML.write(msg);
            myWriterHTML.close();
            File htmlFile = new File("animation.html");
            Desktop.getDesktop().browse(htmlFile.toURI());
        } catch (IOException err) {
            System.out.println("An error occurred.");
            err.printStackTrace();
        }
    }

    @Override
    public String checkNext(){
        String token="";
        if (currentToken<tokens.length){
            token = tokens[currentToken];
        }
        else
            token="NO_MORE_TOKENS";
        return token;
    }

    @Override
    public String getNext(){
        String token="";
        if (currentToken<tokens.length){
            token = tokens[currentToken];
            currentToken++;
        }
        else
            token="NULLTOKEN";
        return token;
    }


    @Override
    public boolean checkToken(String regexp){
        String s = checkNext();
//        System.out.println("comparing: |"+s+"|  to  |"+regexp+"|");
        return (s.matches(regexp));
    }


    @Override
    public String getAndCheckNext(String regexp) throws TokenException {
        String s = getNext();
        if (!s.matches(regexp)) {
            System.out.println("Unexpected next token for Parsing! Expected something matching: " + regexp + " but got: " + s);
            writeErrorToHTML("Unexpected next token for Parsing! Expected something matching: " + regexp + " but got: " + s);
            throw new TokenException(regexp,s);
        }
      //  System.out.println("matched: "+s+"  to  "+regexp);
        return s;
    }

    @Override
    public String getAndCheckNext(int min, int max) throws IndexOutOfBoundsException {
        String s = getNext();
        int inputNum = Integer.parseInt(s);
        if ((inputNum < min) || (inputNum > max)) {
            writeErrorToHTML("Index out of bounds: " + s);
            throw new IndexOutOfBoundsException(s);
        }
//        System.out.println("matched: "+s+"  between " + min + " and " + max);
        return s;
    }

    @Override
    public boolean checkNextNext(String name) throws TokenException {
        if (currentToken + 1 <tokens.length) {
            String token = tokens[currentToken + 1];
            if (token.equals(name)) {
                return true;
            } else {
                return false;
            }
        } else {
            writeErrorToHTML("token exception check next next");
            throw new TokenException(" ", " ");
        }
    }

    @Override
    public boolean moreTokens(){
        return currentToken<tokens.length;
    }

}
