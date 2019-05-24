package ragde.security.factories.implementations;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import ragde.exceptions.RagdeValidationException;
import ragde.models.Permission;
import ragde.models.Person;
import ragde.models.Role;
import ragde.security.factories.LoggedUserFactory;
import ragde.security.pojos.LoggedUser;

import java.util.Collections;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(SpringRunner.class)
@SpringBootTest
public class LoggedUserFactoryImplTest {

    @Autowired
    private LoggedUserFactory loggedUserFactory;

    /**
     * Should throw RagdeValidationException when person null
     */
    @Test(expected = RagdeValidationException.class)
    public void loggedUserPersonNull() {
        loggedUserFactory.loggedUser(null);
    }

    /**
     * Should throw RagdeValidationException when person's Roles is null
     */
    @Test(expected = RagdeValidationException.class)
    public void loggedUserRolesNull() {
        final Person person = new Person("P1");
        loggedUserFactory.loggedUser(person);
    }

    /**
     * Should throw RagdeValidationException when person's Roles is empty
     */
    @Test(expected = RagdeValidationException.class)
    public void loggedUserRolesEmpty() {
        final Person person = new Person("P1");
        person.setRoles(Collections.emptySet());
        loggedUserFactory.loggedUser(person);
    }

    /**
     * Should throw RagdeValidationException when requested role id doesn't belong to person
     */
    @Test(expected = RagdeValidationException.class)
    public void loggedUserIncorrectRoleId() {
        final String ROLE_ID = "R0";
        final Person person = new Person("P1");
        person.setRoles(Set.of(new Role("R1"), new Role("R2"), new Role("R3")));

        loggedUserFactory.loggedUser(person, ROLE_ID);
    }

    /**
     * Should return a LoggedUser with Permissions null
     */
    @Test
    public void loggedUserWithPermissionsNull() {
        final Person person = new Person("P1");
        person.setName("Name");
        person.setLastName("Last Name");
        person.setRoles(Set.of(new Role("R1")));

        final LoggedUser loggedUserExpected = new LoggedUser("P1", "Name Last Name", null, "R1", Collections.emptySet());
        final LoggedUser loggedUserResult = loggedUserFactory.loggedUser(person);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
    }

    /**
     * Should return a LoggedUser with Permissions empty
     */
    @Test
    public void loggedUserWithPermissionsEmpty() {
        final Role role = new Role("R1");
        role.setPermissions(Collections.emptySet());
        final Person person = new Person("P1");
        person.setName("Name");
        person.setLastName("Last Name");
        person.setRoles(Set.of(role));

        final LoggedUser loggedUserExpected = new LoggedUser("P1", "Name Last Name", null, "R1", Collections.emptySet());
        final LoggedUser loggedUserResult = loggedUserFactory.loggedUser(person);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
    }

    /**
     * Should return a LoggedUser with Permissions
     */
    @Test
    public void loggedUserWithPermissions() {
        final Role role = new Role("R1");
        role.setPermissions(Set.of(new Permission("PP1", "D"), new Permission("PP2", "D2"), new Permission("PP1", "D3")));
        final Person person = new Person("P1");
        person.setName("Name");
        person.setLastName("Last Name");
        person.setRoles(Set.of(role));

        final LoggedUser loggedUserExpected = new LoggedUser("P1", "Name Last Name", null, "R1", Set.of("PP1", "PP2"));
        final LoggedUser loggedUserResult = loggedUserFactory.loggedUser(person);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
    }

    /**
     * Should return a LoggedUser with Permissions
     */
    @Test
    public void loggedUserWithRole() {
        final String ROLE_ID = "R1";
        final Role role = new Role("R1");
        role.setPermissions(Set.of(new Permission("PP1", "D"), new Permission("PP2", "D2"), new Permission("PP1", "D3")));
        final Person person = new Person("P1");
        person.setName("Name");
        person.setLastName("Last Name");
        person.setRoles(Set.of(role));

        final LoggedUser loggedUserExpected = new LoggedUser("P1", "Name Last Name", null, "R1", Set.of("PP1", "PP2"));
        final LoggedUser loggedUserResult = loggedUserFactory.loggedUser(person, ROLE_ID);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
    }

    /**
     * Should return a LoggedUser with Permissions
     */
    @Test
    public void loggedUserWithImage() {
        final String IMAGE = "image";
        final String ROLE_ID = "R1";
        final Role role = new Role("R1");
        role.setPermissions(Set.of(new Permission("PP1", "D"), new Permission("PP2", "D2"), new Permission("PP1", "D3")));
        final Person person = new Person("P1");
        person.setName("Name");
        person.setLastName("Last Name");
        person.setRoles(Set.of(role));

        final LoggedUser loggedUserExpected = new LoggedUser("P1", "Name Last Name", IMAGE, "R1", Set.of("PP1", "PP2"));
        final LoggedUser loggedUserResult = loggedUserFactory.loggedUser(person, ROLE_ID, IMAGE);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
    }
}