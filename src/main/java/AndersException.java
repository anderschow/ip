/**
 * Represents an invalid command entered by an Anders user.
 */
public class AndersException extends Exception {
    /**
     * Creates an exception with a user-facing error message.
     *
     * @param message the explanation shown to the user
     */
    public AndersException(String message) {
        super(message);
    }
}
