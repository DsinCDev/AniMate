package libs;
// object for animation
public class Animation {
    int x;
    int y;
    int start;
    int end;
    String rotation;
    int currentAngle;
    public Animation(int x, int y, int start, int end, String rotation, int angle) {
        this.x = x;
        this.y = y;
        this.start = start;
        this.end = end;
        this.rotation = rotation;
        this.currentAngle = angle;
    }
}
