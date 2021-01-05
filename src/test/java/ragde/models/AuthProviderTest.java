package ragde.models;

import org.junit.jupiter.api.Test;
import ragde.integration_test.IntegrationTest;
import ragde.pojos.responses.error.nesteds.NestedError;
import ragde.pojos.responses.error.nesteds.ValidationNestedError;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class AuthProviderTest {

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final AuthProvider authProvider = new AuthProvider();

        assertNull(authProvider.getId());
        assertNull(authProvider.getName());
        assertNull(authProvider.getDescription());
        assertNull(authProvider.getAuthKey());
        assertNull(authProvider.getAuthSecret());
        assertNull(authProvider.getAuthentications());
    }

    /**
     * Should create Id constructor
     */
    @Test
    public void constructorId() {
        final String ID = "ID";
        final AuthProvider authProvider = new AuthProvider(ID);

        assertSame(ID, authProvider.getId());
        assertNull(authProvider.getName());
        assertNull(authProvider.getDescription());
        assertNull(authProvider.getAuthKey());
        assertNull(authProvider.getAuthSecret());
        assertNull(authProvider.getAuthentications());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String NAME = "name";
        final String DESCRIPTION = "description";
        final String AUTH_KEY = "key";
        final String AUTH_SECRET = "secret";
        final AuthProvider authProvider = new AuthProvider(NAME, DESCRIPTION, AUTH_KEY, AUTH_SECRET);

        assertNull(authProvider.getId());
        assertSame(NAME, authProvider.getName());
        assertSame(DESCRIPTION, authProvider.getDescription());
        assertSame(AUTH_KEY, authProvider.getAuthKey());
        assertSame(AUTH_SECRET, authProvider.getAuthSecret());
        assertNull(authProvider.getAuthentications());
    }

    /**
     * Should set and get id
     */
    @Test
    public void setGetID() {
        final AuthProvider authProvider = new AuthProvider();
        final String ID = "ID";
        authProvider.setId(ID);

        assertSame(ID, authProvider.getId());
    }

    /**
     * Should set and get name
     */
    @Test
    public void setGetName() {
        final AuthProvider authProvider = new AuthProvider();
        final String NAME = "name";
        authProvider.setName(NAME);

        assertSame(NAME, authProvider.getName());
    }

    /**
     * Should set and get description
     */
    @Test
    public void setGetDescription() {
        final AuthProvider authProvider = new AuthProvider();
        final String DESCRIPTION = "description";
        authProvider.setDescription(DESCRIPTION);

        assertSame(DESCRIPTION, authProvider.getDescription());
    }

    /**
     * Should set and get authKey
     */
    @Test
    public void setGetAuthKey() {
        final AuthProvider authProvider = new AuthProvider();
        final String AUTH_KEY = "key";
        authProvider.setAuthKey(AUTH_KEY);

        assertSame(AUTH_KEY, authProvider.getAuthKey());
    }

    /**
     * Should set and get authSecret
     */
    @Test
    public void setGetAuthSecret() {
        final AuthProvider authProvider = new AuthProvider();
        final String AUTH_SECRET = "secret";
        authProvider.setAuthSecret(AUTH_SECRET);

        assertSame(AUTH_SECRET, authProvider.getAuthSecret());
    }

    /**
     * Should set and get authentications
     */
    @Test
    public void setGetAuthentications() {
        final AuthProvider authProvider = new AuthProvider();
        final List<Authentication> AUTHENTICATION = List.of(new Authentication("A1"), new Authentication("A2"));
        authProvider.setAuthentications(AUTHENTICATION);

        assertSame(AUTHENTICATION, authProvider.getAuthentications());
    }

    /**
     * Should get toString
     */
    @Test
    public void toStringValid() {
        final String ID = "ID";
        final String NAME = "name";
        final AuthProvider authProvider = new AuthProvider(ID);
        authProvider.setName(NAME);

        assertEquals("AuthProvider(super=Model(id=" + ID + "), name=" + NAME + ")", authProvider.toString());
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final AuthProvider authProvider = new AuthProvider("ID");

        assertTrue(authProvider.equals(authProvider));
        assertFalse(authProvider.equals(null));
        assertFalse(authProvider.equals(new String()));
    }

    /**
     * Should fail equals due ID
     */
    @Test
    public void noEqualsID() {
        final AuthProvider authProvider1 = new AuthProvider("N", "D", "AK", "AS");
        authProvider1.setId("ID");
        authProvider1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProvider2 = new AuthProvider("N", "D", "AK", "AS");
        authProvider2.setId("ID1");
        authProvider2.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProviderNull = new AuthProvider("N", "D", "AK", "AS");
        authProviderNull.setId(null);
        authProviderNull.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(authProvider1, authProvider2);
        assertNotEquals(authProvider1, authProviderNull);
        assertNotEquals(authProviderNull, authProvider1);
    }

    /**
     * Should fail equals due name
     */
    @Test
    public void noEqualsName() {
        final AuthProvider authProvider1 = new AuthProvider("N", "D", "AK", "AS");
        authProvider1.setId("ID");
        authProvider1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProvider2 = new AuthProvider("N1", "D", "AK", "AS");
        authProvider2.setId("ID");
        authProvider2.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProviderNull = new AuthProvider(null, "D", "AK", "AS");
        authProviderNull.setId("ID");
        authProviderNull.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(authProvider1, authProvider2);
        assertNotEquals(authProvider1, authProviderNull);
        assertNotEquals(authProviderNull, authProvider1);
    }

    /**
     * Should fail equals due description
     */
    @Test
    public void noEqualsDescription() {
        final AuthProvider authProvider1 = new AuthProvider("N", "D", "AK", "AS");
        authProvider1.setId("ID");
        authProvider1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProvider2 = new AuthProvider("N", "D1", "AK", "AS");
        authProvider2.setId("ID");
        authProvider2.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProviderNull = new AuthProvider("N", null, "AK", "AS");
        authProviderNull.setId("ID");
        authProviderNull.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(authProvider1, authProvider2);
        assertNotEquals(authProvider1, authProviderNull);
        assertNotEquals(authProviderNull, authProvider1);
    }

    /**
     * Should fail equals due authKey
     */
    @Test
    public void noEqualsAuthKey() {
        final AuthProvider authProvider1 = new AuthProvider("N", "D", "AK", "AS");
        authProvider1.setId("ID");
        authProvider1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProvider2 = new AuthProvider("N", "D", "AK1", "AS");
        authProvider2.setId("ID");
        authProvider2.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProviderNull = new AuthProvider("N", "D", null, "AS");
        authProviderNull.setId("ID");
        authProviderNull.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(authProvider1, authProvider2);
        assertNotEquals(authProvider1, authProviderNull);
        assertNotEquals(authProviderNull, authProvider1);
    }

    /**
     * Should fail equals due authSecret
     */
    @Test
    public void noEqualsAuthSecret() {
        final AuthProvider authProvider1 = new AuthProvider("N", "D", "AK", "AS");
        authProvider1.setId("ID");
        authProvider1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProvider2 = new AuthProvider("N", "D", "AK", "AS1");
        authProvider2.setId("ID");
        authProvider2.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProviderNull = new AuthProvider("N", "D", "AK", null);
        authProviderNull.setId("ID");
        authProviderNull.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(authProvider1, authProvider2);
        assertNotEquals(authProvider1, authProviderNull);
        assertNotEquals(authProviderNull, authProvider1);
    }

    /**
     * Should fail equals due authentications
     */
    @Test
    public void noEqualsAuthentications() {
        final AuthProvider authProvider1 = new AuthProvider("N", "D", "AK", "AS");
        authProvider1.setId("ID");
        authProvider1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProvider2 = new AuthProvider("N", "D", "AK", "AS");
        authProvider2.setId("ID");
        authProvider2.setAuthentications(List.of(new Authentication("A1")));
        final AuthProvider authProviderNull = new AuthProvider("N", "D", "AK", "AS");
        authProviderNull.setId("ID");
        authProviderNull.setAuthentications(null);

        assertNotEquals(authProvider1, authProvider2);
        assertNotEquals(authProvider1, authProviderNull);
        assertNotEquals(authProviderNull, authProvider1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final AuthProvider authProvider1 = new AuthProvider("N", "D", "AK", "AS");
        authProvider1.setId("ID");
        authProvider1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProvider2 = new AuthProvider("N", "D", "AK", "AS");
        authProvider2.setId("ID");
        authProvider2.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final AuthProvider authProviderNull1 = new AuthProvider();
        final AuthProvider authProviderNull2 = new AuthProvider();

        assertNotSame(authProvider1, authProvider2);
        assertEquals(authProvider1, authProvider2);
        assertNotSame(authProviderNull1, authProviderNull2);
        assertEquals(authProviderNull1, authProviderNull2);
    }

    /**
     * Should get 2 errors when parameters null
     */
    @Test
    public void validateWhenNull() {
        final AuthProvider a = new AuthProvider();
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("description", "must not be null"),
                new ValidationNestedError("name", "must not be null")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(a);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 4 errors when parameters empty
     */
    @Test
    public void validateWhenEmpty() {
        final AuthProvider a = new AuthProvider("", "", "", "");
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("authKey", "size must be between 1 and 255"),
                new ValidationNestedError("authSecret", "size must be between 1 and 255"),
                new ValidationNestedError("description", "size must be between 1 and 255"),
                new ValidationNestedError("name", "size must be between 1 and 255")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(a);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 4 errors when parameters are bigger than max
     */
    @Test
    public void validateWhenMax() {
        final StringBuffer longText = new StringBuffer();
        IntStream.range(0, 256).forEach(i -> longText.append("a"));
        final AuthProvider a = new AuthProvider(longText.toString(), longText.toString(), longText.toString(), longText.toString());
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("authKey", "size must be between 1 and 255"),
                new ValidationNestedError("authSecret", "size must be between 1 and 255"),
                new ValidationNestedError("description", "size must be between 1 and 255"),
                new ValidationNestedError("name", "size must be between 1 and 255")
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
        final AuthProvider a = new AuthProvider("A", "B", "C", "D");
        final List<NestedError> nestedErrorsExpected = Collections.emptyList();
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(a);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }
}