package libs;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class Parser {
    private Tokenizer tokenizer;
    private JsonObject jsonOutput = new JsonObject();
    private JsonObject shapesObject = new JsonObject();
    private JsonObject groupsObject = new JsonObject();
    private JsonObject animatesObject = new JsonObject();

    // Regex expressions
    String isName = "[A-Za-z]+";
    String positiveNum = "^[0-9]*[1-9][0-9]*$";
    String naturalNum = "[0-9]+";
    String wholeNum = "^[-+]?\\d*$";
    String colorString = "black|blue|gray|green|purple|red|white|yellow|orange|brown|^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$";
    String isShape = "circle|rectangle|triangle";
    String isRotation = "ccw|cw";
    String isLoop = "inf|[0-9]+";

    public static Parser getParser(Tokenizer tokenizer) {
        return new Parser(tokenizer);
    }

    private Parser(Tokenizer tokenizer) {
        this.tokenizer = tokenizer;
    }

    public JsonObject parse() throws Exception{
        if (tokenizer.checkToken("Canvas")) {
            tokenizer.getAndCheckNext("Canvas");
            jsonOutput.put("Canvas", parseCanvas());
        }

        while(tokenizer.moreTokens()) {
            if (tokenizer.checkToken("Shape")) {
                if (uniqueNameInShape()) {
                    String name = getName();
                    shapesObject.put(name, parseShape(name));
                }
            } else if (tokenizer.checkToken("Line")) {
                if (uniqueNameInShape()) {
                    String name = getName();
                    shapesObject.put(name, parseLine(name));
                }
            } else if (tokenizer.checkToken("Group")) {
                if (uniqueNameInShape() && uniqueNameInGroup()) {
                    String name = getName();
                    groupsObject.put(name, parseGroup(name));
                }
            } else if (tokenizer.checkToken("Animate")) {
                tokenizer.getNext();
                if (nameInShapeOrGroup()) {
                    String name = getName();
                    animatesObject.put(name, parseAnimate(name));
                }
            } else {
                String msg = "Declaration type " + tokenizer.getNext() + " not found, must be one of: Canvas, Shape, Line, Group, Animate";
                writeErrorToHTML(msg);
                throw new Exception(msg);
            }
        }


        jsonOutput.put("Geometry", shapesObject);
        jsonOutput.put("Group", groupsObject);
        jsonOutput.put("Animate",animatesObject);

        return jsonOutput;
    }

    // Checks Name is String
    private String getName() throws Exception {
        try {
            return tokenizer.getAndCheckNext(isName);
        } catch (TokenException e) {
            String msg = "Invalid or missing Name: expected string, got " + e.getInput();
            writeErrorToHTML(msg);
            throw new Exception (msg);
        }
    }

    // Discard declaration token (Shape/Group/Animate) and checks if name is unique in shapeObject, if false returns false
    private boolean uniqueNameInShape() throws Exception{
        tokenizer.getNext();
        if (shapesObject.containsKey(tokenizer.checkNext())) {
            String msg = "Invalid Name: name " + tokenizer.checkNext() + " is not unique, existing Shape name";
            writeErrorToHTML(msg);
            throw new Exception(msg);
        } else {
            return true;
        }
    }

    // Checks if group name is unique in groupObject
    private boolean uniqueNameInGroup() throws Exception {
        if (groupsObject.containsKey(tokenizer.checkNext())) {
            String msg = "Invalid Name: name " + tokenizer.checkNext() + " is not unique, existing Group name";
            writeErrorToHTML(msg);
            throw new Exception(msg);
        } else {
            return true;
        }
    }

    // Checks if name is in Shape or Group, if yes returns true
    private boolean nameInShapeOrGroup() throws Exception{
        if (shapesObject.containsKey(tokenizer.checkNext()) || groupsObject.containsKey(tokenizer.checkNext())) {
            return true;
        } else {
            String msg = "Invalid Group: name of " +tokenizer.getAndCheckNext(isName) + " in Group has not been declared";
            writeErrorToHTML(msg);
            throw new Exception(msg);
        }
    }

    // Checks next token against regex, returns int
    private int getAndCheckNextReturnInt (String regex) throws TokenException {
        return Integer.parseInt(tokenizer.getAndCheckNext(regex));
    }


    // Returns object with canvas parameters as keys, tokens as value
    private JsonObject parseCanvas() throws Exception{
        JsonObject canvas = new JsonObject();
        try {
            tokenizer.getAndCheckNext("\\{");
            tokenizer.getAndCheckNext("color=");
            canvas.put("color", tokenizer.getAndCheckNext(colorString));
            tokenizer.getAndCheckNext("\\}");
        } catch (TokenException e) {
            errorMessage(e, "Canvas", "Canvas");
        }
        return canvas;
    }

    // Returns object with shape parameters as keys, tokens as value
    private JsonObject parseShape(String name) throws Exception{
        JsonObject shape = new JsonObject();
        try {
            tokenizer.getAndCheckNext("\\{");
            shape.put("shape", tokenizer.getAndCheckNext(isShape));
            tokenizer.getAndCheckNext(",");
            shape.put("width", getAndCheckNextReturnInt(positiveNum));
            tokenizer.getAndCheckNext(",");
            shape.put("height", getAndCheckNextReturnInt(positiveNum));
            tokenizer.getAndCheckNext(",");
            shape.put("x", getAndCheckNextReturnInt(naturalNum));
            tokenizer.getAndCheckNext(",");
            shape.put("y", getAndCheckNextReturnInt(naturalNum));
            if(tokenizer.checkToken(",") && tokenizer.checkNextNext("z-index=")) {
                tokenizer.getNext();
                tokenizer.getAndCheckNext("z-index=");
                shape.put("z-index", getAndCheckNextReturnInt(wholeNum));
            }
            if(tokenizer.checkToken(",") && tokenizer.checkNextNext("color=")) {
                tokenizer.getNext();
                tokenizer.getAndCheckNext("color=");
                shape.put("color", tokenizer.getAndCheckNext(colorString));
            }
            if(tokenizer.checkToken(",") && tokenizer.checkNextNext("angle=")) {
                tokenizer.getNext();
                tokenizer.getAndCheckNext("angle=");
                shape.put("z-index", Integer.parseInt(tokenizer.getAndCheckNext(-360, 360)));
            }
            tokenizer.getAndCheckNext("\\}");
        } catch (TokenException e) {
            errorMessage(e, "Shape", name);
        } catch (IndexOutOfBoundsException e) {
            String msg = "Invalid Shape" + name + ": " + e.getMessage() + " is outside of range";
            writeErrorToHTML(msg);
            throw new Exception (msg);
        }
        return shape;
    }

    // Returns object with line parameters as keys, tokens as value
    private JsonObject parseLine(String name) throws Exception {
        JsonObject line = new JsonObject();
        line.put("shape", "line");
        try {
            tokenizer.getAndCheckNext("\\{");
            line.put("width", getAndCheckNextReturnInt(positiveNum));
            tokenizer.getAndCheckNext(",");
            line.put("x", getAndCheckNextReturnInt(naturalNum));
            tokenizer.getAndCheckNext(",");
            line.put("y", getAndCheckNextReturnInt(naturalNum));
            if(tokenizer.checkToken(",") && tokenizer.checkNextNext("color=")) {
                tokenizer.getNext();
                tokenizer.getAndCheckNext("color=");
                line.put("color", tokenizer.getAndCheckNext(colorString));
            }
            if(tokenizer.checkToken(",") && tokenizer.checkNextNext("z-index=")) {
                tokenizer.getNext();
                tokenizer.getAndCheckNext("z-index=");
                line.put("z-index", getAndCheckNextReturnInt(wholeNum));
            }
            if(tokenizer.checkToken(",") && tokenizer.checkNextNext("angle=")) {
                tokenizer.getNext();
                tokenizer.getAndCheckNext("angle=");
                line.put("angle", Integer.parseInt(tokenizer.getAndCheckNext(-360, 360)));
            }
            tokenizer.getAndCheckNext("\\}");
        } catch (TokenException e) {
            errorMessage(e, "Line", name);
        }
        return line;
    }

    // Returns group array with names of declared shapes/lines
    private JsonArray parseGroup(String name) throws Exception {
        JsonArray group = new JsonArray();
        try {
            tokenizer.getAndCheckNext("\\{");
            if(nameInShapeOrGroup()) {
                group.add(tokenizer.getAndCheckNext(isName));
            }
            while(!tokenizer.checkToken("\\}")) {
                tokenizer.getAndCheckNext(",");
                if(nameInShapeOrGroup()) {
                    group.add(tokenizer.getAndCheckNext(isName));
                }
            }
            tokenizer.getAndCheckNext("\\}");
        } catch (TokenException e) {
            errorMessage(e, "Group", name);
        }
        return group;
    }

    // Returns object with numeric increments as keys, animate parameters in object as value
    private JsonObject parseAnimate(String name) throws Exception {
        JsonObject animate = new JsonObject();
        int key = 0;
        try {
            tokenizer.getAndCheckNext("\\{");
            animate.put(String.valueOf(key), parseAnimateAt(name));
            key++;
            while (tokenizer.checkToken(",")) {
                tokenizer.getNext();
                if (tokenizer.checkToken("\\[")) {
                    animate.put(String.valueOf(key), parseAnimateAt(name));
                    key++;
                } else if (tokenizer.checkToken("}")) {
                    writeErrorToHTML("parse animate token exception");
                    throw new TokenException("none",",");
                } else {
                    break;
                }
            }
            if (tokenizer.checkToken("loop=")) {
                tokenizer.getAndCheckNext("loop=");
                animate.put("loop", tokenizer.getAndCheckNext(isLoop));
            }
            tokenizer.getAndCheckNext("\\}");
        } catch (TokenException e) {
            errorMessage(e, "Animate", name);
        }
        return animate;
    }

    private JsonObject parseAnimateAt(String name) throws Exception {
        JsonObject animateAt = new JsonObject();
        int start;
        int end;
        try {
            tokenizer.getAndCheckNext("\\[");
            animateAt.put("x", getAndCheckNextReturnInt(wholeNum));
            tokenizer.getAndCheckNext(",");
            animateAt.put("y", getAndCheckNextReturnInt(wholeNum));
            tokenizer.getAndCheckNext(",");
            start = getAndCheckNextReturnInt(naturalNum);
            animateAt.put("start", start);
            tokenizer.getAndCheckNext(",");
            end = getAndCheckNextReturnInt(positiveNum);
            if (start < end) {
                animateAt.put("end", end);
            } else {
                String msg = "Invalid Animate: end time " + end + " is not greater than start time " + start;
                writeErrorToHTML(msg);
                throw new Exception(msg);
            }
            if (tokenizer.checkToken(",")) {
                tokenizer.getNext();
                tokenizer.getAndCheckNext("rotation=");
                animateAt.put("rotation", tokenizer.getAndCheckNext(isRotation));
            }
            tokenizer.getAndCheckNext("\\]");
        } catch (TokenException e) {
            errorMessage(e, "Animate", name);
        }
        return animateAt;
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

    private void errorMessage(TokenException e, String component, String name) throws Exception{
        String rule = e.getRule();
        String input = e.getInput();
        String msg;
        if (rule.equals(positiveNum)) {
            msg = "Invalid " + component + ". "+ name +": expected positive whole number, got " + input;
            writeErrorToHTML(msg);
            throw new Exception (msg);
        } else if (rule.equals(colorString)) {
            msg = "Invalid " + component + ". " + name + ": valid colors are black, blue, gray, green, purple, red, white, yellow, orange, brown or hex code; got " + input;
            writeErrorToHTML(msg);
            throw new Exception (msg);
        } else if (rule.equals(isName)) {
            msg = "Invalid " + component + ". " + name + ": expected string, got " + input;
            writeErrorToHTML(msg);
            throw new Exception (msg);
        } else if (rule.equals(isShape)) {
            msg = "Invalid " + component+ ". "+ name + ": available shapes are circle, rectangle, and triangle; got " + e.getInput();
            writeErrorToHTML(msg);
            throw new Exception (msg);
        } else if (rule.equals(wholeNum)) {
            msg = "Invalid " + component + ". "+ name + ": expected whole number, got " + input;
            writeErrorToHTML(msg);
            throw new Exception (msg);
        } else if (rule.equals(naturalNum)) {
            msg = "Invalid " + component + ". "+ name + ": expected 0 or positive whole number, got " + input;
            writeErrorToHTML(msg);
            throw new Exception (msg);
        } else if (rule.equals(isRotation)) {
            msg = "Invalid " + component + ". "+ name + ": expected ccw or cw, got " + input;
            writeErrorToHTML(msg);
            throw new Exception (msg);
        } else {
            switch(component) {
                case "Canvas":
                    msg = "Invalid " + component+ ": correct format as follows, Canvas {color= COLOR}";
                    writeErrorToHTML(msg);
                    throw new Exception (msg);
                case "Shape" :
                    msg = "Invalid " + component+ " "+ name + ": correct format as follows, Shape NAME {SHAPETYPE, HEIGHT, WIDTH, POSX, POSY, z-index= NUMBER, color= COLOR, angle= ANGLE}. Optional inputs: z-index, color, angle";
                    writeErrorToHTML(msg);
                    throw new Exception (msg);
                case "Line" :
                    msg = "Invalid " + component+ " "+ name + ": correct format as follows, Line NAME {WIDTH, POSX, POSY, color= COLOR, angle= ANGLE}. Optional inputs: color, angle";
                    writeErrorToHTML(msg);
                    throw new Exception (msg);
                case "Group":
                    msg = "Invalid " + component+ " "+ name + ": correct format as follows, Group NAME {NAME, NAME, ...}";
                    writeErrorToHTML(msg);
                    throw new Exception (msg);
                case "Animate":
                    msg = "Invalid " + component+ " "+ name + ": correct format as follows, Animate NAME {[X, Y, START, END, rotation= ROTATION],..., loop= LOOP}. Optional inputs: rotation, loop";
                    writeErrorToHTML(msg);
                    throw new Exception (msg);
            }
        }
    }
}
