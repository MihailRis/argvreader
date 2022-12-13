package mihailris.argv;

public class ArgvParsingException extends RuntimeException {
    public ArgvParsingException(String s) {
        super(s);
    }

    public ArgvParsingException(String s, Throwable throwable) {
        super(s, throwable);
    }
}
