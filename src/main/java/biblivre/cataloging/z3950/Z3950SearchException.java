package biblivre.cataloging.z3950;

public class Z3950SearchException extends Exception {
    public Z3950SearchException(String message) {
        super(message);
    }

    public Z3950SearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
