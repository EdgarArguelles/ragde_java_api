package ragde.security.pojos;

import org.junit.jupiter.api.Test;
import ragde.integration_test.IntegrationTest;
import ragde.pojos.responses.error.nesteds.NestedError;
import ragde.pojos.responses.error.nesteds.ValidationNestedError;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class AccountCredentialsTest {

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final AccountCredentials credentials = new AccountCredentials();

        assertNull(credentials.getUsername());
        assertNull(credentials.getPassword());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String USERNAME = "test";
        final String PASSWORD = "pass";
        final AccountCredentials credentials = new AccountCredentials(USERNAME, PASSWORD);

        assertSame(USERNAME, credentials.getUsername());
        assertSame(PASSWORD, credentials.getPassword());
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final AccountCredentials credentials = new AccountCredentials("U1", "P1");

        assertTrue(credentials.equals(credentials));
        assertFalse(credentials.equals(null));
        assertFalse(credentials.equals(new String()));
    }

    /**
     * Should fail equals due username
     */
    @Test
    public void noEqualsUsername() {
        final AccountCredentials credentials1 = new AccountCredentials("U1", "P1");
        final AccountCredentials credentials2 = new AccountCredentials("U2", "P1");
        final AccountCredentials credentialsNull = new AccountCredentials(null, "P1");

        assertNotEquals(credentials1, credentials2);
        assertNotEquals(credentials1, credentialsNull);
        assertNotEquals(credentialsNull, credentials1);
    }

    /**
     * Should fail equals due password
     */
    @Test
    public void noEqualsPassword() {
        final AccountCredentials credentials1 = new AccountCredentials("U1", "P1");
        final AccountCredentials credentials2 = new AccountCredentials("U1", "P2");
        final AccountCredentials credentialsNull = new AccountCredentials("U1", null);

        assertNotEquals(credentials1, credentials2);
        assertNotEquals(credentials1, credentialsNull);
        assertNotEquals(credentialsNull, credentials1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final AccountCredentials credentials1 = new AccountCredentials("U1", "P1");
        final AccountCredentials credentials2 = new AccountCredentials("U1", "P1");
        final AccountCredentials credentialsNull1 = new AccountCredentials();
        final AccountCredentials credentialsNull2 = new AccountCredentials();

        assertNotSame(credentials1, credentials2);
        assertEquals(credentials1, credentials2);
        assertNotSame(credentialsNull1, credentialsNull2);
        assertEquals(credentialsNull1, credentialsNull2);
    }

    /**
     * Should get 2 errors when parameters null
     */
    @Test
    public void validateWhenNull() {
        final AccountCredentials a = new AccountCredentials();
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("password", "must not be null"),
                new ValidationNestedError("username", "must not be null")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(a);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 2 errors when parameters empty
     */
    @Test
    public void validateWhenEmpty() {
        final AccountCredentials a = new AccountCredentials("", "");
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("password", "size must be between 1 and 255"),
                new ValidationNestedError("username", "size must be between 1 and 255")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(a);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 2 errors when parameters are bigger than max
     */
    @Test
    public void validateWhenMax() {
        final StringBuffer longText = new StringBuffer();
        IntStream.range(0, 256).forEach(i -> longText.append("a"));
        final AccountCredentials a = new AccountCredentials(longText.toString(), longText.toString());
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("password", "size must be between 1 and 255"),
                new ValidationNestedError("username", "size must be between 1 and 255")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(a);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 0 error when correct
     */
    @Test
    public void validateWhenOK() {
        final AccountCredentials a = new AccountCredentials("A", "B");
        final List<NestedError> nestedErrorsExpected = Collections.emptyList();
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(a);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }
}