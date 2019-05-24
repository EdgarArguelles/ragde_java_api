package ragde.models;

import org.junit.Test;
import ragde.integration_test.IntegrationTest;
import ragde.pojos.responses.error.nesteds.NestedError;
import ragde.pojos.responses.error.nesteds.ValidationNestedError;

import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class PermissionTest {

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final Permission permission = new Permission();

        assertNull(permission.getId());
        assertNull(permission.getName());
        assertNull(permission.getDescription());
        assertNull(permission.getRoles());
    }

    /**
     * Should create Id constructor
     */
    @Test
    public void constructorId() {
        final String ID = "ID";
        final Permission permission = new Permission(ID);

        assertSame(ID, permission.getId());
        assertNull(permission.getName());
        assertNull(permission.getDescription());
        assertNull(permission.getRoles());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String NAME = "name";
        final String DESCRIPTION = "description";
        final Permission permission = new Permission(NAME, DESCRIPTION);

        assertNull(permission.getId());
        assertSame(NAME, permission.getName());
        assertSame(DESCRIPTION, permission.getDescription());
        assertNull(permission.getRoles());
    }

    /**
     * Should set and get id
     */
    @Test
    public void setGetID() {
        final Permission permission = new Permission();
        final String ID = "ID";
        permission.setId(ID);

        assertSame(ID, permission.getId());
    }

    /**
     * Should set and get name
     */
    @Test
    public void setGetName() {
        final Permission permission = new Permission();
        final String NAME = "name";
        permission.setName(NAME);

        assertSame(NAME, permission.getName());
    }

    /**
     * Should set and get description
     */
    @Test
    public void setGetDescription() {
        final Permission permission = new Permission();
        final String DESCRIPTION = "description";
        permission.setDescription(DESCRIPTION);

        assertSame(DESCRIPTION, permission.getDescription());
    }

    /**
     * Should set and get roles
     */
    @Test
    public void setGetRoles() {
        final Permission permission = new Permission();
        final List<Role> ROLES = List.of(new Role("R1"), new Role("R2"));
        permission.setRoles(ROLES);

        assertSame(ROLES, permission.getRoles());
    }

    /**
     * Should get toString
     */
    @Test
    public void toStringValid() {
        final String ID = "ID";
        final String NAME = "name";
        final Permission permission = new Permission(ID);
        permission.setName(NAME);

        assertEquals("Permission(super=Model(id=" + ID + "), name=" + NAME + ")", permission.toString());
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final Permission permission = new Permission("ID");

        assertTrue(permission.equals(permission));
        assertFalse(permission.equals(null));
        assertFalse(permission.equals(new String()));
    }

    /**
     * Should fail equals due ID
     */
    @Test
    public void noEqualsID() {
        final Permission permission1 = new Permission("N", "D");
        permission1.setId("ID");
        permission1.setRoles(List.of(new Role("R1"), new Role("R2")));
        final Permission permission2 = new Permission("N", "D");
        permission2.setId("ID2");
        permission2.setRoles(List.of(new Role("R1"), new Role("R2")));
        final Permission permissionNull = new Permission("N", "D");
        permissionNull.setId(null);
        permissionNull.setRoles(List.of(new Role("R1"), new Role("R2")));

        assertNotEquals(permission1, permission2);
        assertNotEquals(permission1, permissionNull);
        assertNotEquals(permissionNull, permission1);
    }

    /**
     * Should fail equals due name
     */
    @Test
    public void noEqualsName() {
        final Permission permission1 = new Permission("N", "D");
        permission1.setId("ID");
        permission1.setRoles(List.of(new Role("R1"), new Role("R2")));
        final Permission permission2 = new Permission("N1", "D");
        permission2.setId("ID");
        permission2.setRoles(List.of(new Role("R1"), new Role("R2")));
        final Permission permissionNull = new Permission(null, "D");
        permissionNull.setId("ID");
        permissionNull.setRoles(List.of(new Role("R1"), new Role("R2")));

        assertNotEquals(permission1, permission2);
        assertNotEquals(permission1, permissionNull);
        assertNotEquals(permissionNull, permission1);
    }

    /**
     * Should fail equals due description
     */
    @Test
    public void noEqualsDescription() {
        final Permission permission1 = new Permission("N", "D");
        permission1.setId("ID");
        permission1.setRoles(List.of(new Role("R1"), new Role("R2")));
        final Permission permission2 = new Permission("N", "D2");
        permission2.setId("ID");
        permission2.setRoles(List.of(new Role("R1"), new Role("R2")));
        final Permission permissionNull = new Permission("N", null);
        permissionNull.setId("ID");
        permissionNull.setRoles(List.of(new Role("R1"), new Role("R2")));

        assertNotEquals(permission1, permission2);
        assertNotEquals(permission1, permissionNull);
        assertNotEquals(permissionNull, permission1);
    }

    /**
     * Should fail equals due roles
     */
    @Test
    public void noEqualsRoles() {
        final Permission permission1 = new Permission("N", "D");
        permission1.setId("ID");
        permission1.setRoles(List.of(new Role("R1"), new Role("R2")));
        final Permission permission2 = new Permission("N", "D");
        permission2.setId("ID");
        permission2.setRoles(List.of(new Role("R1")));
        final Permission permissionNull = new Permission("N", "D");
        permissionNull.setId("ID");
        permissionNull.setRoles(null);

        assertNotEquals(permission1, permission2);
        assertNotEquals(permission1, permissionNull);
        assertNotEquals(permissionNull, permission1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final Permission permission1 = new Permission("N", "D");
        permission1.setId("ID");
        permission1.setRoles(List.of(new Role("R1"), new Role("R2")));
        final Permission permission2 = new Permission("N", "D");
        permission2.setId("ID");
        permission2.setRoles(List.of(new Role("R1"), new Role("R2")));
        final Permission permissionNull1 = new Permission();
        final Permission permissionNull2 = new Permission();

        assertNotSame(permission1, permission2);
        assertEquals(permission1, permission2);
        assertNotSame(permissionNull1, permissionNull2);
        assertEquals(permissionNull1, permissionNull2);
    }

    /**
     * Should get 2 errors when parameters null
     */
    @Test
    public void validateWhenNull() {
        final Permission p = new Permission();
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("description", "must not be null"),
                new ValidationNestedError("name", "must not be null")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(p);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 2 errors when parameters empty
     */
    @Test
    public void validateWhenEmpty() {
        final Permission p = new Permission("", "");
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("description", "size must be between 1 and 255"),
                new ValidationNestedError("name", "size must be between 1 and 255")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(p);

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
        final Permission p = new Permission(longText.toString(), longText.toString());
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("description", "size must be between 1 and 255"),
                new ValidationNestedError("name", "size must be between 1 and 255")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(p);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 0 error when correct
     */
    @Test
    public void validateWhenOK() {
        final Permission p = new Permission("A", "B");
        final List<NestedError> nestedErrorsExpected = Collections.emptyList();
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(p);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }
}