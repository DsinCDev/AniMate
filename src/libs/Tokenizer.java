package libs;
// this code is borrowed from class examples
public interface Tokenizer {
    String checkNext();

    String getNext();

    boolean checkToken(String regexp);

    String getAndCheckNext(String regexp) throws TokenException;

    String getAndCheckNext(int min, int max) throws IndexOutOfBoundsException;

    boolean checkNextNext(String regex) throws  TokenException;

    boolean moreTokens();
}
