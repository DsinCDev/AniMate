package libs;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Executor {
    JsonObject object;
    ArrayList<Shape> shapes = new ArrayList<>();
    Map<String, ArrayList<String>> group = new HashMap<>();
    ArrayList<Animate> animates = new ArrayList<>();
    Canvas canvas;
    public Executor(JsonObject object) {
        this.object = object;
        getCanvas();
        getGroups();
        getShapes();
        getAnimates();
    }

    public void draw() throws IOException {
        try{
            FileWriter myWriterHTML = new FileWriter("animation.html");
            FileWriter myWriterCSS = new FileWriter("style.css");
            myWriterHTML.write("<link rel=\"stylesheet\" href=\"style.css\">\n");
            myWriterCSS.write(canvas.getCSS());
            for(Shape s: this.shapes) {
                myWriterHTML.write(s.getHTML()+"\n");
                myWriterCSS.write(s.getCSS()+"\n");
            }
            int index = 0;
            for(Animate a: this.animates) {
                myWriterCSS.write(a.getCSS(index)+"\n");
                index += a.animations.size();
            }
            myWriterHTML.close();
            myWriterCSS.close();

            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        File htmlFile = new File("animation.html");
        Desktop.getDesktop().browse(htmlFile.toURI());

    }

    private Boolean containsLine(Animate a) {
        for (Shape s: this.shapes) {
            if (a.name.equals(s.name) && s.shape.equals("line")) {
                return true;
            }
        }
        return false;
    }

    private void getCanvas() {
        JsonObject canvasObj = (JsonObject) object.get("Canvas");
        Canvas c = new Canvas(
                canvasObj.get("color").toString()
        );
        this.canvas = c;
    }

    private void getAnimates() {
        JsonObject animateObj = (JsonObject) object.get("Animate");
        for (Object key : animateObj.keySet()) {
            JsonObject animationObj = (JsonObject) animateObj.get(key);
            String name = key.toString();
            ArrayList<Animation> aniList = new ArrayList<>();
            Animation a = new Animation(0,0,0,0,"", 0);
            int animationSize = animationObj.keySet().size();
            if (animationObj.get("loop") != null) animationSize-=1;
            for (int i = 0; i < animationSize; i++)  {
                JsonObject singleAnimation = (JsonObject) animationObj.get(Integer.toString(i));
                String rotation = "";
                if (singleAnimation.get("rotation") != null) rotation = singleAnimation.get("rotation").toString();
                 a = new Animation(
                        Integer.parseInt(singleAnimation.get("x").toString()),
                        Integer.parseInt(singleAnimation.get("y").toString()),
                        Integer.parseInt(singleAnimation.get("start").toString()),
                        Integer.parseInt(singleAnimation.get("end").toString()),
                        rotation,
                         shapeRotation(name)
                );
                aniList.add(a);
            }
            String loop = "";
            if (animationObj.get("loop") != null) loop = animationObj.get("loop").toString().trim();
            Animate as = new Animate(name,aniList,loop);
            animates.add(as);
            for (String g: this.group.keySet()) {
                String vals = this.group.get(g).toString();
                if (name.equals(vals.substring(1,vals.length()-1))) {
                    for (Shape sh: this.shapes) {
                        if (g.equals(sh.name) && sh.shape.equals("line")) {
                            ArrayList<Animation> lineList = new ArrayList<>();
                            Animation linea = new Animation(a.x,a.y,a.start,a.end,a.rotation,sh.rotation);
                            lineList.add(linea);
                            Animate lineAni = new Animate(sh.name,lineList,loop);
                            animates.add(lineAni);
                        }
                    }
                }
            }
        }
    }

    private void getGroups() {
        JsonObject groupObj = (JsonObject) object.get("Group");
        for(Object key:groupObj.keySet()){
            JsonArray groupArr = (JsonArray)groupObj.get(key);
            for (Object g : groupArr) {
                if (this.group.containsKey(g.toString())) {
                    ArrayList<String> group = this.group.get(g.toString());
                    group.add(key.toString());
                    this.group.put(g.toString(), group);
                } else {
                    ArrayList<String> group = new ArrayList<>();
                    group.add(key.toString());

                    this.group.put(g.toString(), group);
                }
            }
        }
    }

    private void getShapes() {
        JsonObject shapesObj = (JsonObject) object.get("Geometry");

        for(Object key:shapesObj.keySet()){
            JsonObject shapeObj = (JsonObject) shapesObj.get(key);
            String className = "";

            // add other classes to name
            if (this.group.containsKey(key.toString())) {
                ArrayList<String> classes = this.group.get(key.toString());

                for (String s : classes) {
                    className += " " + s;
                }
            }
            // check for optional vals
            Integer zindex = 0;
            Integer rotation = 0;
            Integer height = 0;
            if (shapeObj.get("angle") != null) {
                rotation = Integer.parseInt(shapeObj.get("angle").toString());
            }

            if (shapeObj.get("z-index") != null) {
                zindex = Integer.parseInt(shapeObj.get("z-index").toString());
            }

            if (shapeObj.get("height") != null) {
                height = Integer.parseInt(shapeObj.get("height").toString());
            }
         //   System.out.println(className);
            Shape shape = new Shape(
                    key.toString(),
                    className,
                    shapeObj.get("shape").toString(),
                    Integer.parseInt(shapeObj.get("width").toString()),
                    height,
                    zindex,
                    Integer.parseInt(shapeObj.get("x").toString()),
                    Integer.parseInt(shapeObj.get("y").toString()),
                    rotation,
                    shapeObj.get("color").toString()
            );
            this.shapes.add(shape);
        }

    }

    public int shapeRotation(String name) {
        for (Shape s: shapes) {
            if (s.name.equals(name) && s.shape.equals("line")) {
                return s.rotation;
            }
        }
        return 0;
    }
}
