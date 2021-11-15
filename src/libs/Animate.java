package libs;

import java.util.ArrayList;
import java.util.List;

public class Animate {
    String name;
    List<Animation> animations = new ArrayList<Animation>();
    String iteration_count;
    public Animate(String name, ArrayList<Animation> animations, String iteration_count) {
        this.name = name;
        this.animations = animations;
        this.iteration_count = (String) iteration_count;
    }

    public String getCSS(int index) {
        String output= "." + name + "{\n" +
            "\t position: absolute;\n" +
            "\t opacity: 0;\n" +
            "\t" + getAnimationNames(index) + "\n" +
                "\t" + "animation-fill-mode: forwards; \n" +
                "\t" + "animation-timing-function: linear; \n" +
            "\t" + getAnimationDelay() + "\n" +
            "\t" + getAnimationIteration() + "\n" +
            "\t" + getAnimationDuration() + "\n}" + "\n\n" +
                getKeyFrames(index);
        return output;
    }

    private String getKeyFrames(int index) {
        String output = "";
        for (int i = index; i < animations.size() + index; i++) {
            Animation currentAnimation = animations.get(i - index);
            int offset = (currentAnimation.rotation.equals("cw")) ? 90 : -90;
            if (currentAnimation.rotation.equals("")) offset = 0;
            output = output + "@keyframes animation" + i + " {\n" +
                    "\t 0% {opacity: 1} \n" +
                    "\t 25% {opacity: 1; transform: translateX(" + currentAnimation.x / 4 + "px) translateY(" + currentAnimation.y / 4 + "px)" +
                             " rotate(" + (currentAnimation.currentAngle+offset) + "deg)}\n" +
                    "\t 50% {opacity: 1; transform: translateX(" + currentAnimation.x / 2 + "px) translateY(" + currentAnimation.y / 2 + "px)" +
                    " rotate(" + (currentAnimation.currentAngle+2*offset) + "deg)}\n" +
                    "\t 75% {opacity: 1; transform: translateX(" + 3*currentAnimation.x / 4 + "px) translateY(" + 3*currentAnimation.y / 4 + "px)" +
                    " rotate(" + (currentAnimation.currentAngle+3*offset) + "deg)}\n" +
                    "\t 100% {opacity: 1; transform: translateX(" + currentAnimation.x + "px) translateY(" + currentAnimation.y + "px)" +
                    " rotate(" + (currentAnimation.currentAngle+4*offset) + "deg)}\n";
                    output = output + "}\n";
        }
        return output;
    }

    private String getAnimationIteration() {
        String output = "animation-iteration-count: ";
        String word = "";
           if (iteration_count.equals("inf")) {
               word = "infinite";
           } else if (iteration_count.equals("")) {
               word = "1";
           } else {
               word = iteration_count;
           }
           output += stringMultiply(word + ", ",animations.size()-1) + word + ";";
        return output;
    }
    private String getAnimationNames(int index) {
        String output = "animation-name: ";
        for (int i = index; i < animations.size()+index; i++) {
            if (animations.size()+index-i==1) {
                output = output + "animation" + i;
            } else {
                output = output + "animation" + i + ", ";
            }
        }
        return output + ";";
    }

    private String getAnimationDelay() {
        String output = "animation-delay: ";
        for (int i = 0; i < animations.size(); i++) {
            if (animations.size()-i==1) {
                output = output + animations.get(i).start + "s";
            } else {
                output = output + animations.get(i).start + "s" + ", ";
            }
        }
        return output + ";";
    }

    private String getAnimationDuration() {
        String output = "animation-duration: ";
        for (int i = 0; i < animations.size(); i++) {
            if (animations.size()-i==1) {
                output = output + (animations.get(i).end - animations.get(i).start) + "s";
            } else {
                output = output + (animations.get(i).end - animations.get(i).start) + "s" + ", ";
            }
        }
        return output + ";";
    }
    private String stringMultiply(String s, int n){
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < n; i++){
            sb.append(s);
        }
        return sb.toString();
    }
}
