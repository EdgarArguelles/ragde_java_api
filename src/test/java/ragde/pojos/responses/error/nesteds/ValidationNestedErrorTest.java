package ragde.pojos.responses.error.nesteds;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ValidationNestedErrorTest {

    @Autowired
    private ObjectMapper mapper;

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final NestedError nestedError = new ValidationNestedError();
        final ValidationNestedError validationNestedError = (ValidationNestedError) nestedError;

        assertNull(validationNestedError.getMessage());
        assertNull(validationNestedError.getField());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String MESSAGE = "test";
        final String FIELD = "field";
        final NestedError nestedError = new ValidationNestedError(FIELD, MESSAGE);
        final ValidationNestedError validationNestedError = (ValidationNestedError) nestedError;

        assertSame(MESSAGE, validationNestedError.getMessage());
        assertSame(FIELD, validationNestedError.getField());
    }

    /**
     * Should serialize and deserialize
     */
    @Test
    public void serialize() throws IOException {
        final String MESSAGE = "test";
        final String FIELD = "field";
        final NestedError nestedErrorExpected = new ValidationNestedError(FIELD, MESSAGE);

        final String json = mapper.writeValueAsString(nestedErrorExpected);
        final NestedError nestedErrorResult = mapper.readValue(json, ValidationNestedError.class);

        assertNotSame(nestedErrorExpected, nestedErrorResult);
        assertEquals(nestedErrorExpected, nestedErrorResult);
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final NestedError nestedError = new ValidationNestedError("F", "M");

        assertTrue(nestedError.equals(nestedError));
        assertFalse(nestedError.equals(null));
        assertFalse(nestedError.equals(new String()));
    }

    /**
     * Should fail equals due field
     */
    @Test
    public void noEqualsField() {
        final NestedError nestedError1 = new ValidationNestedError("F", "M");
        final NestedError nestedError2 = new ValidationNestedError("F1", "M");
        final NestedError nestedErrorNull = new ValidationNestedError(null, "M");

        assertNotEquals(nestedError1, nestedError2);
        assertNotEquals(nestedError1, nestedErrorNull);
        assertNotEquals(nestedErrorNull, nestedError1);
    }

    /**
     * Should fail equals due message
     */
    @Test
    public void noEqualsMessage() {
        final NestedError nestedError1 = new ValidationNestedError("F", "M");
        final NestedError nestedError2 = new ValidationNestedError("F", "M1");
        final NestedError nestedErrorNull = new ValidationNestedError("F", null);

        assertNotEquals(nestedError1, nestedError2);
        assertNotEquals(nestedError1, nestedErrorNull);
        assertNotEquals(nestedErrorNull, nestedError1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final NestedError nestedError1 = new ValidationNestedError("F", "M");
        final NestedError nestedError2 = new ValidationNestedError("F", "M");
        final NestedError nestedErrorNull1 = new ValidationNestedError();
        final NestedError nestedErrorNull2 = new ValidationNestedError();

        assertNotSame(nestedError1, nestedError2);
        assertEquals(nestedError1, nestedError2);
        assertNotSame(nestedErrorNull1, nestedErrorNull2);
        assertEquals(nestedErrorNull1, nestedErrorNull2);
    }
}