package ragde.models;

import org.junit.jupiter.api.Test;
import ragde.integration_test.IntegrationTest;
import ragde.pojos.responses.error.nesteds.NestedError;
import ragde.pojos.responses.error.nesteds.ValidationNestedError;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

public class RoleTest {

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final Role role = new Role();

        assertNull(role.getId());
        assertNull(role.getName());
        assertNull(role.getDescription());
        assertNull(role.getPermissions());
        assertNull(role.getPeople());
    }

    /**
     * Should create Id constructor
     */
    @Test
    public void constructorId() {
        final String ID = "ID";
        final Role role = new Role(ID);

        assertSame(ID, role.getId());
        assertNull(role.getName());
        assertNull(role.getDescription());
        assertNull(role.getPermissions());
        assertNull(role.getPeople());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String NAME = "name";
        final String DESCRIPTION = "description";
        final Set<Permission> PERMISSIONS = Set.of(new Permission("P1"), new Permission("P2"));
        final Role role = new Role(NAME, DESCRIPTION, PERMISSIONS);

        assertNull(role.getId());
        assertSame(NAME, role.getName());
        assertSame(DESCRIPTION, role.getDescription());
        assertSame(PERMISSIONS, role.getPermissions());
        assertNull(role.getPeople());
    }

    /**
     * Should set and get id
     */
    @Test
    public void setGetID() {
        final Role role = new Role();
        final String ID = "ID";
        role.setId(ID);

        assertSame(ID, role.getId());
    }

    /**
     * Should set and get name
     */
    @Test
    public void setGetName() {
        final Role role = new Role();
        final String NAME = "name";
        role.setName(NAME);

        assertSame(NAME, role.getName());
    }

    /**
     * Should set and get description
     */
    @Test
    public void setGetDescription() {
        final Role role = new Role();
        final String DESCRIPTION = "description";
        role.setDescription(DESCRIPTION);

        assertSame(DESCRIPTION, role.getDescription());
    }

    /**
     * Should set and get permissions
     */
    @Test
    public void setGetPermissions() {
        final Role role = new Role();
        final Set<Permission> PERMISSIONS = Set.of(new Permission("P1"), new Permission("P2"));
        role.setPermissions(PERMISSIONS);

        assertSame(PERMISSIONS, role.getPermissions());
    }

    /**
     * Should set and get people
     */
    @Test
    public void setGetPeople() {
        final Role role = new Role();
        final List<Person> PEOPLE = List.of(new Person("Per1"), new Person("Per2"));
        role.setPeople(PEOPLE);

        assertSame(PEOPLE, role.getPeople());
    }

    /**
     * Should get toString
     */
    @Test
    public void toStringValid() {
        final String ID = "ID";
        final String NAME = "name";
        final Role role = new Role(ID);
        role.setName(NAME);

        assertEquals("Role(super=Model(id=" + ID + "), name=" + NAME + ")", role.toString());
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final Role role = new Role("ID");

        assertTrue(role.equals(role));
        assertFalse(role.equals(null));
        assertFalse(role.equals(new String()));
    }

    /**
     * Should fail equals due ID
     */
    @Test
    public void noEqualsID() {
        final Role role1 = new Role("N", "D", Set.of(new Permission("P1"), new Permission("P2")));
        role1.setId("ID");
        role1.setPeople(List.of(new Person("Per1"), new Person("Per2")));
        final Role role2 = new Role("N", "D", Set.of(new Permission("P1"), new Permission("P2")));
        role2.setId("ID2");
        role2.setPeople(List.of(new Person("Per1"), new Person("Per2")));
        final Role roleNull = new Role("N", "D", Set.of(new Permission("P1"), new Permission("P2")));
        roleNull.setId(null);
        roleNull.setPeople(List.of(new Person("Per1"), new Person("Per2")));

        assertNotEquals(role1, role2);
        assertNotEquals(role1, roleNull);
        assertNotEquals(roleNull, role1);
    }

    /**
     * Should fail equals due name
     */
    @Test
    public void noEqualsName() {
        final Role role1 = new Role("N", "D", Set.of(new Permission("P1"), new Permission("P2")));
        role1.setId("ID");
        role1.setPeople(List.of(new Person("Per1"), new Person("Per2")));
        final Role role2 = new Role("N1", "D", Set.of(new Permission("P1"), new Permission("P2")));
        role2.setId("ID");
        role2.setPeople(List.of(new Person("Per1"), new Person("Per2")));
        final Role roleNull = new Role(null, "D", Set.of(new Permission("P1"), new Permission("P2")));
        roleNull.setId("ID");
        roleNull.setPeople(List.of(new Person("Per1"), new Person("Per2")));

        assertNotEquals(role1, role2);
        assertNotEquals(role1, roleNull);
        assertNotEquals(roleNull, role1);
    }

    /**
     * Should fail equals due description
     */
    @Test
    public void noEqualsDescription() {
        final Role role1 = new Role("N", "D", Set.of(new Permission("P1"), new Permission("P2")));
        role1.setId("ID");
        role1.setPeople(List.of(new Person("Per1"), new Person("Per2")));
        final Role role2 = new Role("N", "D1", Set.of(new Permission("P1"), new Permission("P2")));
        role2.setId("ID");
        role2.setPeople(List.of(new Person("Per1"), new Person("Per2")));
        final Role roleNull = new Role("N", null, Set.of(new Permission("P1"), new Permission("P2")));
        roleNull.setId("ID");
        roleNull.setPeople(List.of(new Person("Per1"), new Person("Per2")));

        assertNotEquals(role1, role2);
        assertNotEquals(role1, roleNull);
        assertNotEquals(roleNull, role1);
    }

    /**
     * Should fail equals due permissions
     */
    @Test
    public void noEqualsPermissions() {
        final Role role1 = new Role("N", "D", Set.of(new Permission("P1"), new Permission("P2")));
        role1.setId("ID");
        role1.setPeople(List.of(new Person("Per1"), new Person("Per2")));
        final Role role2 = new Role("N", "D", Set.of(new Permission("P1")));
        role2.setId("ID");
        role2.setPeople(List.of(new Person("Per1"), new Person("Per2")));
        final Role roleNull = new Role("N", "D", null);
        roleNull.setId("ID");
        roleNull.setPeople(List.of(new Person("Per1"), new Person("Per2")));

        assertNotEquals(role1, role2);
        assertNotEquals(role1, roleNull);
        assertNotEquals(roleNull, role1);
    }

    /**
     * Should fail equals due people
     */
    @Test
    public void noEqualsPeople() {
        final Role role1 = new Role("N", "D", Set.of(new Permission("P1"), new Permission("P2")));
        role1.setId("ID");
        role1.setPeople(List.of(new Person("Per1"), new Person("Per2")));
        final Role role2 = new Role("N", "D", Set.of(new Permission("P1"), new Permission("P2")));
        role2.setId("ID");
        role2.setPeople(List.of(new Person("Per1")));
        final Role roleNull = new Role("N", "D", Set.of(new Permission("P1"), new Permission("P2")));
        roleNull.setId("ID");
        roleNull.setPeople(null);

        assertNotEquals(role1, role2);
        assertNotEquals(role1, roleNull);
        assertNotEquals(roleNull, role1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final Role role1 = new Role("N", "D", Set.of(new Permission("P1"), new Permission("P2")));
        role1.setId("ID");
        role1.setPeople(List.of(new Person("Per1"), new Person("Per2")));
        final Role role2 = new Role("N", "D", Set.of(new Permission("P1"), new Permission("P2")));
        role2.setId("ID");
        role2.setPeople(List.of(new Person("Per1"), new Person("Per2")));
        final Role roleNull1 = new Role();
        final Role roleNull2 = new Role();

        assertNotSame(role1, role2);
        assertEquals(role1, role2);
        assertNotSame(roleNull1, roleNull2);
        assertEquals(roleNull1, roleNull2);
    }

    /**
     * Should get 2 errors when parameters null
     */
    @Test
    public void validateWhenNull() {
        final Role r = new Role();
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("description", "must not be null"),
                new ValidationNestedError("name", "must not be null")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(r);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 2 errors when parameters empty
     */
    @Test
    public void validateWhenEmpty() {
        final Role r = new Role("", "", null);
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("description", "size must be between 1 and 255"),
                new ValidationNestedError("name", "size must be between 1 and 255")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(r);

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
        final Role r = new Role(longText.toString(), longText.toString(), null);
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("description", "size must be between 1 and 255"),
                new ValidationNestedError("name", "size must be between 1 and 255")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(r);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 0 error when correct
     */
    @Test
    public void validateWhenOK() {
        final Role r = new Role("A", "B", null);
        final List<NestedError> nestedErrorsExpected = Collections.emptyList();
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(r);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }
}