package libs;

public class Canvas {
    String color;

    public Canvas(String color) {
        this.color = color;
    }
    public String getCSS() {
        return "body { background-color: " + color +  "}\n";
    }
}
