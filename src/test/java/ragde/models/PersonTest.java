package ragde.models;

import org.junit.Test;
import ragde.integration_test.IntegrationTest;
import ragde.pojos.responses.error.nesteds.NestedError;
import ragde.pojos.responses.error.nesteds.ValidationNestedError;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

import static org.junit.Assert.*;

public class PersonTest {

    /**
     * Should have CIVIL_STATUS and SEX constants
     */
    @Test
    public void constants() {
        final Integer SINGLE = 1;
        final Integer MARRIED = 2;
        final String M = "M";
        final String F = "F";

        assertEquals(SINGLE, Person.CIVIL_STATUS.SINGLE);
        assertEquals(MARRIED, Person.CIVIL_STATUS.MARRIED);
        assertEquals(M, Person.SEX.M);
        assertEquals(F, Person.SEX.F);
    }

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final Person person = new Person();

        assertNull(person.getId());
        assertNull(person.getName());
        assertNull(person.getLastName());
        assertNull(person.getBirthday());
        assertNull(person.getCivilStatus());
        assertNull(person.getSex());
        assertNull(person.getEmail());
        assertNull(person.getRoles());
        assertNull(person.getAuthentications());
    }

    /**
     * Should create Id constructor
     */
    @Test
    public void constructorId() {
        final String ID = "ID";
        final Person person = new Person(ID);

        assertSame(ID, person.getId());
        assertNull(person.getName());
        assertNull(person.getLastName());
        assertNull(person.getBirthday());
        assertNull(person.getCivilStatus());
        assertNull(person.getSex());
        assertNull(person.getEmail());
        assertNull(person.getRoles());
        assertNull(person.getAuthentications());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String NAME = "name";
        final String LAST_NAME = "last name";
        final LocalDate BIRTHDAY = LocalDate.now();
        final Integer CIVIL_STATUS = 1;
        final String SEX = "A";
        final String EMAIL = "emailtest";
        final Set<Role> ROLES = Set.of(new Role("R1"), new Role("R2"));
        final Person person = new Person(NAME, LAST_NAME, BIRTHDAY, CIVIL_STATUS, SEX, EMAIL, ROLES);

        assertNull(person.getId());
        assertSame(NAME, person.getName());
        assertSame(LAST_NAME, person.getLastName());
        assertSame(BIRTHDAY, person.getBirthday());
        assertSame(CIVIL_STATUS, person.getCivilStatus());
        assertSame(SEX, person.getSex());
        assertSame(EMAIL, person.getEmail());
        assertSame(ROLES, person.getRoles());
        assertNull(person.getAuthentications());
    }

    /**
     * Should set and get id
     */
    @Test
    public void setGetID() {
        final Person person = new Person();
        final String ID = "ID";
        person.setId(ID);

        assertSame(ID, person.getId());
    }

    /**
     * Should set and get name
     */
    @Test
    public void setGetName() {
        final Person person = new Person();
        final String NAME = "name";
        person.setName(NAME);

        assertSame(NAME, person.getName());
    }

    /**
     * Should set and get lastName
     */
    @Test
    public void setGetLastName() {
        final Person person = new Person();
        final String LAST_NAME = "last name";
        person.setLastName(LAST_NAME);

        assertSame(LAST_NAME, person.getLastName());
    }

    /**
     * Should set and get birthday
     */
    @Test
    public void setGetBirthday() {
        final Person person = new Person();
        final LocalDate BIRTHDAY = LocalDate.now();
        person.setBirthday(BIRTHDAY);

        assertSame(BIRTHDAY, person.getBirthday());
    }

    /**
     * Should set and get civilStatus
     */
    @Test
    public void setGetCivilStatus() {
        final Person person = new Person();
        final Integer CIVIL_STATUS = 1;
        person.setCivilStatus(CIVIL_STATUS);

        assertSame(CIVIL_STATUS, person.getCivilStatus());
    }

    /**
     * Should set and get sex
     */
    @Test
    public void setGetSex() {
        final Person person = new Person();
        final String SEX = "A";
        person.setSex(SEX);

        assertSame(SEX, person.getSex());
    }

    /**
     * Should set and get email
     */
    @Test
    public void setGetEmail() {
        final Person person = new Person();
        final String EMAIL = "emailtest";
        person.setEmail(EMAIL);

        assertSame(EMAIL, person.getEmail());
    }

    /**
     * Should set and get roles
     */
    @Test
    public void setGetRoles() {
        final Person person = new Person();
        final Set<Role> ROLES = Set.of(new Role("R1"), new Role("R2"));
        person.setRoles(ROLES);

        assertSame(ROLES, person.getRoles());
    }

    /**
     * Should set and get authentications
     */
    @Test
    public void setGetAuthentications() {
        final Person person = new Person();
        final List<Authentication> AUTHENTICATIONS = List.of(new Authentication("A1"), new Authentication("A2"));
        person.setAuthentications(AUTHENTICATIONS);

        assertSame(AUTHENTICATIONS, person.getAuthentications());
    }

    /**
     * Should get toString
     */
    @Test
    public void toStringValid() {
        final String ID = "ID";
        final String NAME = "name";
        final String LAST_NAME = "last name";
        final Person person = new Person(ID);
        person.setName(NAME);
        person.setLastName(LAST_NAME);

        assertEquals("Person(super=Model(id=" + ID + "), name=" + NAME + ", lastName=" + LAST_NAME + ")", person.toString());
    }

    /**
     * Should get Full Name
     */
    @Test
    public void getFullName() {
        final String NAME = "name";
        final String LAST_NAME = "last name";
        final Person person = new Person();
        person.setName(NAME);
        person.setLastName(LAST_NAME);

        assertEquals(NAME + " " + LAST_NAME, person.getFullName());
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final Person person = new Person("ID");

        assertTrue(person.equals(person));
        assertFalse(person.equals(null));
        assertFalse(person.equals(new String()));
    }

    /**
     * Should fail equals due ID
     */
    @Test
    public void noEqualsID() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1.setId("ID");
        person1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person2.setId("ID2");
        person2.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1Null.setId(null);
        person1Null.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due name
     */
    @Test
    public void noEqualsName() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1.setId("ID");
        person1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N1", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person2.setId("ID");
        person2.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person(null, "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1Null.setId("ID");
        person1Null.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due lastName
     */
    @Test
    public void noEqualsLastName() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1.setId("ID");
        person1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN2", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person2.setId("ID");
        person2.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person("N", null, BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1Null.setId("ID");
        person1Null.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due birthday
     */
    @Test
    public void noEqualsBirthday() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final LocalDate BIRTHDAY2 = BIRTHDAY.plusDays(1);
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1.setId("ID");
        person1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY2, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person2.setId("ID");
        person2.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person("N", "LN", null, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1Null.setId("ID");
        person1Null.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due civilStatus
     */
    @Test
    public void noEqualsCivilStatus() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1.setId("ID");
        person1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY, 2, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person2.setId("ID");
        person2.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person("N", "LN", BIRTHDAY, null, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1Null.setId("ID");
        person1Null.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due sex
     */
    @Test
    public void noEqualsSex() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1.setId("ID");
        person1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY, 1, "B", "E", Set.of(new Role("R1"), new Role("R2")));
        person2.setId("ID");
        person2.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person("N", "LN", BIRTHDAY, 1, null, "E", Set.of(new Role("R1"), new Role("R2")));
        person1Null.setId("ID");
        person1Null.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due email
     */
    @Test
    public void noEqualsEmail() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1.setId("ID");
        person1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY, 1, "A", "E2", Set.of(new Role("R1"), new Role("R2")));
        person2.setId("ID");
        person2.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person("N", "LN", BIRTHDAY, 1, "A", null, Set.of(new Role("R1"), new Role("R2")));
        person1Null.setId("ID");
        person1Null.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due roles
     */
    @Test
    public void noEqualsRoles() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1.setId("ID");
        person1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1")));
        person2.setId("ID");
        person2.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person1Null = new Person("N", "LN", BIRTHDAY, 1, "A", "E", null);
        person1Null.setId("ID");
        person1Null.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should fail equals due authentications
     */
    @Test
    public void noEqualsAuthentications() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1.setId("ID");
        person1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person2.setId("ID");
        person2.setAuthentications(List.of(new Authentication("A1")));
        final Person person1Null = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1Null.setId("ID");
        person1Null.setAuthentications(null);

        assertNotEquals(person1, person2);
        assertNotEquals(person1, person1Null);
        assertNotEquals(person1Null, person1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final LocalDate BIRTHDAY = LocalDate.now();
        final Person person1 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person1.setId("ID");
        person1.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person person2 = new Person("N", "LN", BIRTHDAY, 1, "A", "E", Set.of(new Role("R1"), new Role("R2")));
        person2.setId("ID");
        person2.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));
        final Person personNull1 = new Person();
        final Person personNull2 = new Person();

        assertNotSame(person1, person2);
        assertEquals(person1, person2);
        assertNotSame(personNull1, personNull2);
        assertEquals(personNull1, personNull2);
    }

    /**
     * Should get 5 errors when parameters null
     */
    @Test
    public void validateWhenNull() {
        final Person p = new Person();
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("birthday", "must not be null"),
                new ValidationNestedError("civilStatus", "must not be null"),
                new ValidationNestedError("lastName", "must not be null"),
                new ValidationNestedError("name", "must not be null"),
                new ValidationNestedError("sex", "must not be null")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(p);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 5 errors when parameters empty
     */
    @Test
    public void validateWhenEmpty() {
        final Person p = new Person("", "", LocalDate.now(), 0, "", "aa", Collections.emptySet());
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("email", "must be a well-formed email address"),
                new ValidationNestedError("email", "size must be between 3 and 255"),
                new ValidationNestedError("lastName", "size must be between 1 and 255"),
                new ValidationNestedError("name", "size must be between 1 and 255"),
                new ValidationNestedError("sex", "size must be between 1 and 1")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(p);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 5 errors when parameters are bigger than max
     */
    @Test
    public void validateWhenMax() {
        final StringBuffer longText = new StringBuffer();
        IntStream.range(0, 256).forEach(i -> longText.append("a"));
        final Person p = new Person(longText.toString(), longText.toString(), LocalDate.now(), 0, "SS", longText.toString(), Collections.emptySet());
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("email", "must be a well-formed email address"),
                new ValidationNestedError("email", "size must be between 3 and 255"),
                new ValidationNestedError("lastName", "size must be between 1 and 255"),
                new ValidationNestedError("name", "size must be between 1 and 255"),
                new ValidationNestedError("sex", "size must be between 1 and 1")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(p);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 1 error when email is incorrect
     */
    @Test
    public void validateWhenEmail() {
        final Person p = new Person("N", "L", LocalDate.now(), 0, "S", "invalid", Collections.emptySet());
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("email", "must be a well-formed email address")
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
        final Person p = new Person("N", "L", LocalDate.now(), 0, "S", "aa@aa", Collections.emptySet());
        final List<NestedError> nestedErrorsExpected = Collections.emptyList();
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(p);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }
}