package ragde.pojos.pages;

import org.junit.Test;
import ragde.integration_test.IntegrationTest;
import ragde.pojos.responses.error.nesteds.NestedError;
import ragde.pojos.responses.error.nesteds.ValidationNestedError;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class FilterRequestTest {

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final FilterRequest filterRequest = new FilterRequest();

        assertNull(filterRequest.getField());
        assertNull(filterRequest.getValue());
        assertNull(filterRequest.getOperation());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String FIELD = "test";
        final String VALUE = "value";
        final FilterRequest.OPERATIONS OPERATION = FilterRequest.OPERATIONS.EQ;
        final FilterRequest filterRequest = new FilterRequest(FIELD, VALUE, OPERATION);

        assertSame(FIELD, filterRequest.getField());
        assertSame(VALUE, filterRequest.getValue());
        assertSame(OPERATION, filterRequest.getOperation());
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final FilterRequest filterRequest = new FilterRequest("F", "V", FilterRequest.OPERATIONS.EQ);

        assertTrue(filterRequest.equals(filterRequest));
        assertFalse(filterRequest.equals(null));
        assertFalse(filterRequest.equals(new String()));
    }

    /**
     * Should fail equals due field
     */
    @Test
    public void noEqualsField() {
        final FilterRequest filterRequest1 = new FilterRequest("Field1", "value", FilterRequest.OPERATIONS.EQ);
        final FilterRequest filterRequest2 = new FilterRequest("Field2", "value", FilterRequest.OPERATIONS.EQ);
        final FilterRequest filterRequestNull = new FilterRequest(null, "value", FilterRequest.OPERATIONS.EQ);

        assertNotEquals(filterRequest1, filterRequest2);
        assertNotEquals(filterRequest1, filterRequestNull);
        assertNotEquals(filterRequestNull, filterRequest1);
    }

    /**
     * Should fail equals due value
     */
    @Test
    public void noEqualsValue() {
        final FilterRequest filterRequest1 = new FilterRequest("Field1", "value", FilterRequest.OPERATIONS.EQ);
        final FilterRequest filterRequest2 = new FilterRequest("Field1", "value2", FilterRequest.OPERATIONS.EQ);
        final FilterRequest filterRequestNull = new FilterRequest("Field1", null, FilterRequest.OPERATIONS.EQ);

        assertNotEquals(filterRequest1, filterRequest2);
        assertNotEquals(filterRequest1, filterRequestNull);
        assertNotEquals(filterRequestNull, filterRequest1);
    }

    /**
     * Should fail equals due operation
     */
    @Test
    public void noEqualsOperation() {
        final FilterRequest filterRequest1 = new FilterRequest("Field1", "value", FilterRequest.OPERATIONS.EQ);
        final FilterRequest filterRequest2 = new FilterRequest("Field1", "value", FilterRequest.OPERATIONS.GET);
        final FilterRequest filterRequestNull = new FilterRequest("Field1", "value", null);

        assertNotEquals(filterRequest1, filterRequest2);
        assertNotEquals(filterRequest1, filterRequestNull);
        assertNotEquals(filterRequestNull, filterRequest1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final FilterRequest filterRequest1 = new FilterRequest("Field1", "value", FilterRequest.OPERATIONS.EQ);
        final FilterRequest filterRequest2 = new FilterRequest("Field1", "value", FilterRequest.OPERATIONS.EQ);
        final FilterRequest filterRequestNull1 = new FilterRequest();
        final FilterRequest filterRequestNull2 = new FilterRequest();

        assertNotSame(filterRequest1, filterRequest2);
        assertEquals(filterRequest1, filterRequest2);
        assertNotSame(filterRequestNull1, filterRequestNull2);
        assertEquals(filterRequestNull1, filterRequestNull2);
    }

    /**
     * Should get 3 errors when parameters null
     */
    @Test
    public void validateWhenNull() {
        final FilterRequest f = new FilterRequest();
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("field", "must not be null"),
                new ValidationNestedError("operation", "must not be null"),
                new ValidationNestedError("value", "must not be null")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(f);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 1 error when parameters empty
     */
    @Test
    public void validateWhenEmpty() {
        final FilterRequest f = new FilterRequest("", "", FilterRequest.OPERATIONS.EQ);
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("field", "size must be between 1 and 255")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(f);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 1 error when parameters are bigger than max
     */
    @Test
    public void validateWhenMax() {
        final StringBuffer longText = new StringBuffer();
        IntStream.range(0, 256).forEach(i -> longText.append("a"));
        final FilterRequest f = new FilterRequest(longText.toString(), longText.toString(), FilterRequest.OPERATIONS.EQ);
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("field", "size must be between 1 and 255")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(f);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 0 error when correct
     */
    @Test
    public void validateWhenOK() {
        final FilterRequest f = new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ);
        final List<NestedError> nestedErrorsExpected = Collections.emptyList();
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(f);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }
}