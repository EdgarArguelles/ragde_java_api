package ragde.exceptions;

import ragde.pojos.responses.error.nesteds.NestedError;

import java.util.List;

public class RagdeValidationException extends RagdeException {

    /**
     * Constructs a new exception with the specified user readable message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     */
    public RagdeValidationException(String message) {
        super(message);
    }

    /**
     * Constructs a new exception with the specified user readable message.  The
     * cause is not initialized, and may subsequently be initialized by
     * a call to {@link #initCause}.
     *
     * @param message      the detail message. The detail message is saved for later retrieval by the {@link #getMessage()} method.
     * @param nestedErrors nested errors displayed to users
     */
    public RagdeValidationException(String message, List<NestedError> nestedErrors) {
        super(message, nestedErrors);
    }
}