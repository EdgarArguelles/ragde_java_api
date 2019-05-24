package ragde.pojos.responses.error;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ragde.pojos.responses.error.nesteds.NestedError;

import java.util.List;

/**
 * Error Response pojo
 */
@NoArgsConstructor
@EqualsAndHashCode
public class ErrorResponse {

    @Getter
    private Error error;

    /**
     * Create an instance without specific developer message
     *
     * @param message message displayed to users
     */
    public ErrorResponse(String message) {
        this(message, null);
    }

    /**
     * Create an instance with specific developer message
     *
     * @param message    message displayed to users
     * @param devMessage message displayed to developers
     */
    public ErrorResponse(String message, String devMessage) {
        this(message, devMessage, null);
    }

    /**
     * Create an instance with specific developer message and nested errors
     *
     * @param message      message displayed to users
     * @param devMessage   message displayed to developers
     * @param nestedErrors nested errors displayed to users
     */
    public ErrorResponse(String message, String devMessage, List<NestedError> nestedErrors) {
        this.error = new Error(message, devMessage, nestedErrors);
    }

    @NoArgsConstructor
    @EqualsAndHashCode
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private class Error {

        @Getter
        private String message;

        @Getter
        private String devMessage;

        @Getter
        private List<NestedError> nestedErrors;

        public Error(String message, String devMessage, List<NestedError> nestedErrors) {
            this.message = message;
            this.devMessage = devMessage;
            this.nestedErrors = nestedErrors;
        }
    }
}