package libs;

public class Shape {
    int width;
    int height;
    int zIndex;
    String color;
    int rotation;
    int x;
    int y;
    String shape;
    String name;
    String classes;

    public Shape(String name, String classes, String shape, int width, int height, int zIndex, int x, int y, int rotation, String color) {
        this.width = width;
        this.height = height;
        this.zIndex = zIndex;
        this.color = color;
        this.x = x;
        this.y = y;
        this.rotation = rotation;
        this.shape = shape;
        this.name = name;
        this.classes = classes;
    }

    public String getHTML() {
        if (!this.shape.equals("line")) {
            return "<div class='" + this.name + this.classes + "'></div>";
        } else {
            return "<div class='" + this.name + "'></div>";
        }
    }

    public String getCSS() {
        switch (this.shape) {
            case "circle":
                return "."+this.name+"{\n" +
                            "\twidth: "+this.width+"px;\n" +
                            "\theight: "+this.height+"px;\n" +
                            "\tbackground-color: "+this.color+";\n" +
                            "\tborder-radius: 50%;\n" +
                            "\ttransform: rotate("+this.rotation+"deg);\n" +
                            "\ttop: "+this.y+"px;\n" +
                            "\tleft: "+this.x+"px;\n" +
                            "\tz-index: " +this.zIndex+";\n"+
                            "\tposition: absolute;\n" +
                        "}";
            case "rectangle":
                return "."+this.name+"{\n" +
                        "\twidth: "+this.width+"px;\n" +
                        "\theight: "+this.height+"px;\n" +
                        "\tbackground-color: "+this.color+";\n" +
                        "\ttransform: rotate("+this.rotation+"deg);\n" +
                        "\ttop: "+this.y+"px;\n" +
                        "\tleft: "+this.x+"px;\n" +
                        "\tz-index: " +this.zIndex+";\n"+
                        "\tposition: absolute;\n" +
                        "}";
            case "line":
                return "."+this.name+"{\n" +
                        "\twidth: "+this.width+"px;\n" +
                        "\theight: 2px;\n" +
                        "\tbackground-color: "+this.color+";\n" +
                        "\ttransform: rotate("+this.rotation+"deg);\n" +
                        "\ttop: "+this.y+"px;\n" +
                        "\tleft: "+this.x+"px;\n" +
                        "\tz-index: " +this.zIndex+";\n"+
                        "\tposition: absolute;\n" +
                        "\ttransform-origin: top left;\n"+
                        "}";
            case "triangle":
                return "."+this.name+"{\n" +
                        "\twidth: 0;\n" +
                        "\theight: 0;\n" +
                        "\ttop: "+this.y+"px;\n" +
                        "\tleft: "+this.x+"px;\n" +
                        "\tborder-left: "+this.width/2+"px solid transparent;\n" +
                        "\tborder-right: "+this.width/2+"px solid transparent;\n" +
                        "\tborder-bottom: "+this.height+"px solid "+this.color+";\n" +
                        "}\n\n";
            default:
                throw new RuntimeException("Invalid Shape");
        }
    }

}
