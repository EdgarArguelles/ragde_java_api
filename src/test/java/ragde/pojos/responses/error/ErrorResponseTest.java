package ragde.pojos.responses.error;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ragde.pojos.responses.error.nesteds.NestedError;
import ragde.pojos.responses.error.nesteds.ValidationNestedError;

import java.io.IOException;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ErrorResponseTest {

    @Autowired
    private ObjectMapper mapper;

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final ErrorResponse response = new ErrorResponse();

        assertNull(response.getError());
    }

    /**
     * Should create Message constructor
     */
    @Test
    public void constructorMessage() {
        final String MESSAGE = "test";
        final ErrorResponse responseExpected = new ErrorResponse(MESSAGE);

        final ErrorResponse responseResult = new ErrorResponse(MESSAGE);

        assertNotSame(responseExpected, responseResult);
        assertNotSame(responseExpected.getError(), responseResult.getError());
        assertEquals(responseExpected, responseResult);
    }

    /**
     * Should create Dev Message constructor
     */
    @Test
    public void constructorDevMessage() {
        final String MESSAGE = "test";
        final String DEV_MESSAGE = "dev test";
        final ErrorResponse responseExpected = new ErrorResponse(MESSAGE, DEV_MESSAGE);

        final ErrorResponse responseResult = new ErrorResponse(MESSAGE, DEV_MESSAGE);

        assertNotSame(responseExpected, responseResult);
        assertNotSame(responseExpected.getError(), responseResult.getError());
        assertEquals(responseExpected, responseResult);
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String MESSAGE = "test";
        final String DEV_MESSAGE = "dev test";
        final List<NestedError> NESTED_ERRORS = List.of(new ValidationNestedError("F", "M"), new ValidationNestedError("F2", "M2"));
        final ErrorResponse responseExpected = new ErrorResponse(MESSAGE, DEV_MESSAGE, NESTED_ERRORS);

        final ErrorResponse responseResult = new ErrorResponse(MESSAGE, DEV_MESSAGE, NESTED_ERRORS);

        assertNotSame(responseExpected, responseResult);
        assertNotSame(responseExpected.getError(), responseResult.getError());
        assertEquals(responseExpected, responseResult);
    }

    /**
     * Should serialize and deserialize
     */
    @Test
    public void serialize() throws IOException {
        final String MESSAGE = "test";
        final String DEV_MESSAGE = "dev test";
        final List<NestedError> NESTED_ERRORS = List.of(new ValidationNestedError("F", "M"), new ValidationNestedError("F2", "M2"));
        final ErrorResponse responseExpected = new ErrorResponse(MESSAGE, DEV_MESSAGE, NESTED_ERRORS);

        final String json = mapper.writeValueAsString(responseExpected);
        final ErrorResponse responseResult = mapper.readValue(json, ErrorResponse.class);

        assertNotSame(responseExpected, responseResult);
        assertNotSame(responseExpected.getError(), responseResult.getError());
        assertEquals(responseExpected, responseResult);
    }

    /**
     * Should ignore null value on json
     */
    @Test
    public void JsonNotIncludeNull() throws JsonProcessingException {
        final ErrorResponse response = new ErrorResponse("test");
        final ErrorResponse responseFull = new ErrorResponse("M", "DM", List.of(new ValidationNestedError("F", "M")));

        final String json = mapper.writeValueAsString(response);
        final String jsonFull = mapper.writeValueAsString(responseFull);

        assertFalse(json.contains("devMessage"));
        assertFalse(json.contains("nestedErrors"));
        assertTrue(jsonFull.contains("devMessage"));
        assertTrue(jsonFull.contains("nestedErrors"));
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final ErrorResponse response = new ErrorResponse("test");
        final Object error = response.getError();

        assertTrue(response.equals(response));
        assertFalse(response.equals(null));
        assertFalse(response.equals(new String()));
        assertTrue(error.equals(error));
        assertFalse(error.equals(null));
        assertFalse(error.equals(new String()));
    }

    /**
     * Should fail equals due Error
     */
    @Test
    public void noEqualsError() {
        final ErrorResponse response1 = new ErrorResponse("M", "DM");
        final ErrorResponse response2 = new ErrorResponse("M");
        final ErrorResponse responseNull = new ErrorResponse();

        assertNotEquals(response1, response2);
        assertNotEquals(response1, responseNull);
        assertNotEquals(responseNull, response1);
    }

    /**
     * Should fail equals due message
     */
    @Test
    public void noEqualsMessage() {
        final ErrorResponse response1 = new ErrorResponse("M", "DM", List.of(new ValidationNestedError("F", "M"), new ValidationNestedError("F2", "M2")));
        final ErrorResponse response2 = new ErrorResponse("M1", "DM", List.of(new ValidationNestedError("F", "M"), new ValidationNestedError("F2", "M2")));
        final ErrorResponse responseNull = new ErrorResponse(null, "DM", List.of(new ValidationNestedError("F", "M"), new ValidationNestedError("F2", "M2")));

        assertNotEquals(response1, response2);
        assertNotEquals(response1, responseNull);
        assertNotEquals(responseNull, response1);
    }

    /**
     * Should fail equals due devMessage
     */
    @Test
    public void noEqualsDevMessage() {
        final ErrorResponse response1 = new ErrorResponse("M", "DM", List.of(new ValidationNestedError("F", "M"), new ValidationNestedError("F2", "M2")));
        final ErrorResponse response2 = new ErrorResponse("M", "DM1", List.of(new ValidationNestedError("F", "M"), new ValidationNestedError("F2", "M2")));
        final ErrorResponse responseNull = new ErrorResponse("M", null, List.of(new ValidationNestedError("F", "M"), new ValidationNestedError("F2", "M2")));

        assertNotEquals(response1, response2);
        assertNotEquals(response1, responseNull);
        assertNotEquals(responseNull, response1);
    }

    /**
     * Should fail equals due nestedErrors
     */
    @Test
    public void noEqualsNestedErrors() {
        final ErrorResponse response1 = new ErrorResponse("M", "DM", List.of(new ValidationNestedError("F", "M"), new ValidationNestedError("F2", "M2")));
        final ErrorResponse response2 = new ErrorResponse("M", "DM", List.of(new ValidationNestedError("F", "M")));
        final ErrorResponse responseNull = new ErrorResponse("M", "DM", null);

        assertNotEquals(response1, response2);
        assertNotEquals(response1, responseNull);
        assertNotEquals(responseNull, response1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final ErrorResponse response1 = new ErrorResponse("M", "DM", List.of(new ValidationNestedError("F", "M"), new ValidationNestedError("F2", "M2")));
        final ErrorResponse response2 = new ErrorResponse("M", "DM", List.of(new ValidationNestedError("F", "M"), new ValidationNestedError("F2", "M2")));
        final ErrorResponse responseErrorNull1 = new ErrorResponse(null, null, null);
        final ErrorResponse responseErrorNull2 = new ErrorResponse(null, null, null);
        final ErrorResponse responseNull1 = new ErrorResponse();
        final ErrorResponse responseNull2 = new ErrorResponse();

        assertNotSame(response1, response2);
        assertNotSame(response1.getError(), response2.getError());
        assertEquals(response1, response2);
        assertNotSame(responseErrorNull1, responseErrorNull2);
        assertNotSame(responseErrorNull1.getError(), responseErrorNull2.getError());
        assertEquals(responseErrorNull1, responseErrorNull2);
        assertNotSame(responseNull1, responseNull2);
        assertEquals(responseNull1, responseNull2);
    }
}