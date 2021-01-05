package ragde.exceptions;

import org.junit.jupiter.api.Test;
import ragde.pojos.responses.error.nesteds.NestedError;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class RagdeValidationExceptionTest {

    /**
     * Should create basic constructor
     */
    @Test
    public void constructorBasic() {
        final String MESSAGE = "test";
        final RagdeException exception = new RagdeValidationException(MESSAGE);

        assertSame(MESSAGE, exception.getMessage());
        assertNull(exception.getNestedErrors());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String MESSAGE = "test";
        final List<NestedError> NESTED_ERRORS = Collections.emptyList();
        final RagdeException exception = new RagdeValidationException(MESSAGE, NESTED_ERRORS);

        assertSame(MESSAGE, exception.getMessage());
        assertSame(NESTED_ERRORS, exception.getNestedErrors());
    }
}