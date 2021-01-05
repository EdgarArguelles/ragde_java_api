package ragde.exceptions;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;

public class RagdeAuthenticationExceptionTest {

    /**
     * Should create basic constructor
     */
    @Test
    public void constructorBasic() {
        final String MESSAGE = "test";
        final RagdeException exception = new RagdeAuthenticationException(MESSAGE);

        assertSame(MESSAGE, exception.getMessage());
        assertNull(exception.getNestedErrors());
    }
}