package ragde.security.pojos;

import org.junit.Test;

import java.util.Set;

import static org.junit.Assert.*;

public class LoggedUserTest {

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final LoggedUser user = new LoggedUser();

        assertNull(user.getId());
        assertNull(user.getFullName());
        assertNull(user.getImage());
        assertNull(user.getRole());
        assertNull(user.getToken());
        assertNull(user.getPermissions());
    }

    /**
     * Should create id and role constructor
     */
    @Test
    public void constructorIdRole() {
        final String ID = "ID";
        final String ROLE = "ROLE";
        final LoggedUser user = new LoggedUser(ID, ROLE);

        assertSame(ID, user.getId());
        assertNull(user.getFullName());
        assertNull(user.getImage());
        assertSame(ROLE, user.getRole());
        assertNull(user.getToken());
        assertNull(user.getPermissions());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final String ID = "ID";
        final String FULL_NAME = "full name";
        final String IMAGE = "image";
        final String ROLE = "ROLE";
        final Set<String> PERMISSIONS = Set.of("PER1", "PER2");
        final LoggedUser user = new LoggedUser(ID, FULL_NAME, IMAGE, ROLE, PERMISSIONS);

        assertSame(ID, user.getId());
        assertSame(FULL_NAME, user.getFullName());
        assertSame(IMAGE, user.getImage());
        assertSame(ROLE, user.getRole());
        assertNull(user.getToken());
        assertSame(PERMISSIONS, user.getPermissions());
    }

    /**
     * Should set and get token
     */
    @Test
    public void setGetToken() {
        final LoggedUser user = new LoggedUser();
        final String TOKEN = "token";
        user.setToken(TOKEN);

        assertSame(TOKEN, user.getToken());
    }

    /**
     * Should set and get permissions
     */
    @Test
    public void setGetPermissions() {
        final LoggedUser user = new LoggedUser();
        final Set<String> PERMISSIONS = Set.of("PER1", "PER2");
        user.setPermissions(PERMISSIONS);

        assertSame(PERMISSIONS, user.getPermissions());
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final LoggedUser user = new LoggedUser("ID", "Role");

        assertTrue(user.equals(user));
        assertFalse(user.equals(null));
        assertFalse(user.equals(new String()));
    }

    /**
     * Should fail equals due id
     */
    @Test
    public void noEqualsID() {
        final LoggedUser user1 = new LoggedUser("ID1", "full", "image", "ROLE", Set.of("PER1", "PER2"));
        final LoggedUser user2 = new LoggedUser("ID2", "full", "image", "ROLE", Set.of("PER1", "PER2"));
        final LoggedUser userNull = new LoggedUser(null, "full", "image", "ROLE", Set.of("PER1", "PER2"));

        assertNotEquals(user1, user2);
        assertNotEquals(user1, userNull);
        assertNotEquals(userNull, user1);
    }

    /**
     * Should fail equals due fullName
     */
    @Test
    public void noEqualsFullName() {
        final LoggedUser user1 = new LoggedUser("ID1", "full", "image", "ROLE", Set.of("PER1", "PER2"));
        final LoggedUser user2 = new LoggedUser("ID1", "full2", "image", "ROLE", Set.of("PER1", "PER2"));
        final LoggedUser userNull = new LoggedUser("ID1", null, "image", "ROLE", Set.of("PER1", "PER2"));

        assertNotEquals(user1, user2);
        assertNotEquals(user1, userNull);
        assertNotEquals(userNull, user1);
    }

    /**
     * Should fail equals due image
     */
    @Test
    public void noEqualsImage() {
        final LoggedUser user1 = new LoggedUser("ID1", "full", "image", "ROLE", Set.of("PER1", "PER2"));
        final LoggedUser user2 = new LoggedUser("ID1", "full", "image2", "ROLE", Set.of("PER1", "PER2"));
        final LoggedUser userNull = new LoggedUser("ID1", "full", null, "ROLE", Set.of("PER1", "PER2"));

        assertNotEquals(user1, user2);
        assertNotEquals(user1, userNull);
        assertNotEquals(userNull, user1);
    }

    /**
     * Should fail equals due role
     */
    @Test
    public void noEqualsRole() {
        final LoggedUser user1 = new LoggedUser("ID1", "full", "image", "ROLE", Set.of("PER1", "PER2"));
        final LoggedUser user2 = new LoggedUser("ID1", "full", "image", "ROLE2", Set.of("PER1", "PER2"));
        final LoggedUser userNull = new LoggedUser("ID1", "full", "image", null, Set.of("PER1", "PER2"));

        assertNotEquals(user1, user2);
        assertNotEquals(user1, userNull);
        assertNotEquals(userNull, user1);
    }

    /**
     * Should fail equals due token
     */
    @Test
    public void noEqualsToken() {
        final LoggedUser user1 = new LoggedUser("ID1", "full", "image", "ROLE", Set.of("PER1", "PER2"));
        user1.setToken("token1");
        final LoggedUser user2 = new LoggedUser("ID1", "full", "image", "ROLE", Set.of("PER1", "PER2"));
        user1.setToken("token2");
        final LoggedUser userNull = new LoggedUser("ID1", "full", "image", "ROLE", Set.of("PER1", "PER2"));

        assertNotEquals(user1, user2);
        assertNotEquals(user1, userNull);
        assertNotEquals(userNull, user1);
    }

    /**
     * Should fail equals due permissions
     */
    @Test
    public void noEqualsPermissions() {
        final LoggedUser user1 = new LoggedUser("ID1", "full", "image", "ROLE", Set.of("PER1", "PER2"));
        final LoggedUser user2 = new LoggedUser("ID1", "full", "image", "ROLE", Set.of("PER1"));
        final LoggedUser userNull = new LoggedUser("ID1", "full", "image", "ROLE", null);

        assertNotEquals(user1, user2);
        assertNotEquals(user1, userNull);
        assertNotEquals(userNull, user1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final LoggedUser user1 = new LoggedUser("ID1", "full", "image", "ROLE", Set.of("PER1", "PER2"));
        final LoggedUser user2 = new LoggedUser("ID1", "full", "image", "ROLE", Set.of("PER1", "PER2"));
        user1.setToken("token1");
        user2.setToken("token1");
        final LoggedUser userNull1 = new LoggedUser();
        final LoggedUser userNull2 = new LoggedUser();

        assertNotSame(user1, user2);
        assertEquals(user1, user2);
        assertNotSame(userNull1, userNull2);
        assertEquals(userNull1, userNull2);
    }
}