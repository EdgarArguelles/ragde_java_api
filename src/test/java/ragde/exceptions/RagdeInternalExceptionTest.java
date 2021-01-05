package ragde.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class RagdeInternalExceptionTest {

    /**
     * Should create basic constructor
     */
    @Test
    public void constructorBasic() {
        final String MESSAGE = "test";
        final RagdeException exception = new RagdeInternalException(MESSAGE);

        assertSame(MESSAGE, exception.getMessage());
        assertNull(exception.getNestedErrors());
    }
}