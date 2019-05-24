package ragde.exceptions;

public class RagdeInternalException extends RagdeException {

    /**
     * Constructs a new exception with the specified user readable message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public RagdeInternalException(String message) {
        super(message);
    }
}