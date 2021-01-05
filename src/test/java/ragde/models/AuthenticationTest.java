package ragde.models;

import org.junit.jupiter.api.Test;
import ragde.integration_test.IntegrationTest;
import ragde.pojos.responses.error.nesteds.NestedError;
import ragde.pojos.responses.error.nesteds.ValidationNestedError;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class AuthenticationTest {

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final Authentication authentication = new Authentication();

        assertNull(authentication.getId());
        assertNull(authentication.getUsername());
        assertNull(authentication.getPassword());
        assertNull(authentication.getAuthProvider());
        assertNull(authentication.getPerson());
    }

    /**
     * Should create Id constructor
     */
    @Test
    public void constructorId() {
        final String ID = "ID";
        final Authentication authentication = new Authentication(ID);

        assertSame(ID, authentication.getId());
        assertNull(authentication.getUsername());
        assertNull(authentication.getPassword());
        assertNull(authentication.getAuthProvider());
        assertNull(authentication.getPerson());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String USERNAME = "username";
        final String PASSWORD = "password";
        final AuthProvider AUTH_PROVIDER = new AuthProvider("AP1");
        final Person PERSON = new Person("P1");
        final Authentication authentication = new Authentication(USERNAME, PASSWORD, AUTH_PROVIDER, PERSON);

        assertNull(authentication.getId());
        assertSame(USERNAME, authentication.getUsername());
        assertSame(PASSWORD, authentication.getPassword());
        assertSame(AUTH_PROVIDER, authentication.getAuthProvider());
        assertSame(PERSON, authentication.getPerson());
    }

    /**
     * Should set and get id
     */
    @Test
    public void setGetID() {
        final Authentication authentication = new Authentication();
        final String ID = "ID";
        authentication.setId(ID);

        assertSame(ID, authentication.getId());
    }

    /**
     * Should set and get username
     */
    @Test
    public void setGetUsername() {
        final Authentication authentication = new Authentication();
        final String USERNAME = "username";
        authentication.setUsername(USERNAME);

        assertSame(USERNAME, authentication.getUsername());
    }

    /**
     * Should set and get password
     */
    @Test
    public void setGetPassword() {
        final Authentication authentication = new Authentication();
        final String PASSWORD = "password";
        authentication.setPassword(PASSWORD);

        assertSame(PASSWORD, authentication.getPassword());
    }

    /**
     * Should set and get authProvider
     */
    @Test
    public void setGetAuthProvider() {
        final Authentication authentication = new Authentication();
        final AuthProvider AUTH_PROVIDER = new AuthProvider("AP1");
        authentication.setAuthProvider(AUTH_PROVIDER);

        assertSame(AUTH_PROVIDER, authentication.getAuthProvider());
    }

    /**
     * Should set and get person
     */
    @Test
    public void setGetPerson() {
        final Authentication authentication = new Authentication();
        final Person PERSON = new Person("P1");
        authentication.setPerson(PERSON);

        assertSame(PERSON, authentication.getPerson());
    }

    /**
     * Should get toString
     */
    @Test
    public void toStringValid() {
        final String ID = "ID";
        final String USERNAME = "username";
        final Authentication authentication = new Authentication(ID);
        authentication.setUsername(USERNAME);

        assertEquals("Authentication(super=Model(id=" + ID + "), username=" + USERNAME + ")", authentication.toString());
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final Authentication authentication = new Authentication("ID");

        assertTrue(authentication.equals(authentication));
        assertFalse(authentication.equals(null));
        assertFalse(authentication.equals(new String()));
    }

    /**
     * Should fail equals due ID
     */
    @Test
    public void noEqualsID() {
        final Authentication authentication1 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication1.setId("ID");
        final Authentication authentication2 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication2.setId("ID2");
        final Authentication authenticationNull = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authenticationNull.setId(null);

        assertNotEquals(authentication1, authentication2);
        assertNotEquals(authentication1, authenticationNull);
        assertNotEquals(authenticationNull, authentication1);
    }

    /**
     * Should fail equals due username
     */
    @Test
    public void noEqualsUsername() {
        final Authentication authentication1 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication1.setId("ID");
        final Authentication authentication2 = new Authentication("U2", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication2.setId("ID");
        final Authentication authenticationNull = new Authentication(null, "P", new AuthProvider("AP1"), new Person("P1"));
        authenticationNull.setId("ID");

        assertNotEquals(authentication1, authentication2);
        assertNotEquals(authentication1, authenticationNull);
        assertNotEquals(authenticationNull, authentication1);
    }

    /**
     * Should fail equals due password
     */
    @Test
    public void noEqualsPassword() {
        final Authentication authentication1 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication1.setId("ID");
        final Authentication authentication2 = new Authentication("U", "P1", new AuthProvider("AP1"), new Person("P1"));
        authentication2.setId("ID");
        final Authentication authenticationNull = new Authentication("U", null, new AuthProvider("AP1"), new Person("P1"));
        authenticationNull.setId("ID");

        assertNotEquals(authentication1, authentication2);
        assertNotEquals(authentication1, authenticationNull);
        assertNotEquals(authenticationNull, authentication1);
    }

    /**
     * Should fail equals due authProvider
     */
    @Test
    public void noEqualsAuthProvider() {
        final Authentication authentication1 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication1.setId("ID");
        final Authentication authentication2 = new Authentication("U", "P", new AuthProvider("AP2"), new Person("P1"));
        authentication2.setId("ID");
        final Authentication authenticationNull = new Authentication("U", "P", null, new Person("P1"));
        authenticationNull.setId("ID");

        assertNotEquals(authentication1, authentication2);
        assertNotEquals(authentication1, authenticationNull);
        assertNotEquals(authenticationNull, authentication1);
    }

    /**
     * Should fail equals due person
     */
    @Test
    public void noEqualsPerson() {
        final Authentication authentication1 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication1.setId("ID");
        final Authentication authentication2 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P2"));
        authentication2.setId("ID");
        final Authentication authenticationNull = new Authentication("U", "P", new AuthProvider("AP1"), null);
        authenticationNull.setId("ID");

        assertNotEquals(authentication1, authentication2);
        assertNotEquals(authentication1, authenticationNull);
        assertNotEquals(authenticationNull, authentication1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final Authentication authentication1 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication1.setId("ID");
        final Authentication authentication2 = new Authentication("U", "P", new AuthProvider("AP1"), new Person("P1"));
        authentication2.setId("ID");
        final Authentication authenticationNull1 = new Authentication();
        final Authentication authenticationNull2 = new Authentication();

        assertNotSame(authentication1, authentication2);
        assertEquals(authentication1, authentication2);
        assertNotSame(authenticationNull1, authenticationNull2);
        assertEquals(authenticationNull1, authenticationNull2);
    }

    /**
     * Should get 3 errors when parameters null
     */
    @Test
    public void validateWhenNull() {
        final Authentication a = new Authentication();
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("authProvider", "must not be null"),
                new ValidationNestedError("person", "must not be null"),
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
        final Authentication a = new Authentication("", "PP", new AuthProvider("AP"), new Person("P"));
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("password", "size must be between 3 and 255"),
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
        final Authentication a = new Authentication(longText.toString(), longText.toString(), new AuthProvider("AP"), new Person("P"));
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("password", "size must be between 3 and 255"),
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
        final Authentication a = new Authentication("A", "BBB", new AuthProvider("AP"), new Person("P"));
        final List<NestedError> nestedErrorsExpected = Collections.emptyList();
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(a);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }
}