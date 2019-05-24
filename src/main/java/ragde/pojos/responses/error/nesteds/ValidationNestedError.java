package ragde.pojos.responses.error.nesteds;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Validation Error pojo
 */
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ValidationNestedError extends NestedError {

    @Getter
    private String field;

    /**
     * Create an instance
     *
     * @param field   field that causes the error
     * @param message message displayed to users
     */
    public ValidationNestedError(String field, String message) {
        super(message);
        this.field = field;
    }
}