package libs;

public class TokenException extends Exception{
    private String rule;
    private String input;

    public TokenException(String rule, String input) {
        this.rule = rule;
        this.input = input;
    }

    public String getRule() {
        return this.rule;
    }

    public String getInput() {
        return this.input;
    }
}
