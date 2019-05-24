package ragde.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ragde.models.AuthProvider;
import ragde.models.Authentication;
import ragde.models.Person;
import ragde.models.Role;
import ragde.repositories.*;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.TokenService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.any;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("unchecked")
public class SecurityIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private AuthProviderRepository authProviderRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    private List<Person> dbPeople;

    private List<Role> dbRoles;

    private IntegrationTest integrationTest;

    private final String TOKEN = "token";

    private final String VIEW_ROLES_TOKEN = "view";

    private final String CHANGE_ROLE_TOKEN = "change";

    @Before
    public void setup() throws Exception {
        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
        IntegrationTest.cleanAllData(authenticationRepository, authProviderRepository, personRepository, roleRepository, permissionRepository);

        dbRoles = List.of(
                new Role("R1", "D1", null),
                new Role("R2", "D2", null)
        );
        roleRepository.saveAll(dbRoles);

        dbPeople = List.of(
                new Person("N1", "LN1", LocalDate.now(), 1, "A", null, Collections.emptySet()),
                new Person("N2", "LN2", LocalDate.now(), 2, "B", null, new HashSet<>(dbRoles))
        );
        personRepository.saveAll(dbPeople);

        final AuthProvider provider = authProviderRepository.save(new AuthProvider("test", "desc", null, null));

        authenticationRepository.save(new Authentication("user1", DigestUtils.sha512Hex("pass1"), provider, dbPeople.get(0)));
        authenticationRepository.save(new Authentication("user2", DigestUtils.sha512Hex("pass2"), provider, dbPeople.get(1)));

        given(tokenService.getLoggedUser(VIEW_ROLES_TOKEN)).willReturn(
                new LoggedUser("123", null, null, null, Set.of("VIEW_ROLES")));
        given(tokenService.getLoggedUser(CHANGE_ROLE_TOKEN)).willReturn(
                new LoggedUser(dbPeople.get(1).getId(), null, null, null, Set.of("VIEW_ROLES")));
        given(tokenService.createToken(any(LoggedUser.class))).willReturn(TOKEN);
        given(tokenService.getLoggedUser(TOKEN)).willReturn(new LoggedUser("mockID", "mockRole"));
    }

    /**
     * Should return an error response
     */
    @Test
    public void loginNotData() throws Exception {
        final String query = "query {login {id token}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument credentials"));
    }

    /**
     * Should return a 404 error response
     */
    @Test
    public void loginInvalid() throws Exception {
        final String query = "query {login(credentials: {}) {id token}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("login"));
        assertEquals(404, extensions.get("errorCode"));
        assertEquals("Credentials incorrect.", error.get("message"));
    }

    /**
     * Should return a 404 error response
     */
    @Test
    public void loginInvalidUsername() throws Exception {
        final String query = "query {login(credentials: {username: \"A\" password: \"B\"}) {id token}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("login"));
        assertEquals(404, extensions.get("errorCode"));
        assertEquals("Credentials incorrect.", error.get("message"));
    }

    /**
     * Should return a 404 error response
     */
    @Test
    public void loginInvalidPassword() throws Exception {
        final String query = "query {login(credentials: {username: \"user1\" password: \"B\"}) {id token}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("login"));
        assertEquals(404, extensions.get("errorCode"));
        assertEquals("Credentials incorrect.", error.get("message"));
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void loginNotRoles() throws Exception {
        final String query = "query {login(credentials: {username: \"user1\" password: \"pass1\"}) {id token}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("login"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("User doesn't have Roles associated.", error.get("message"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void login() throws Exception {
        final String query = "query {login(credentials: {username: \"user2\" password: \"pass2\"}) {id token}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map login = data.get("login");

        assertNull(mapResult.get("errors"));
        assertEquals("mockID", login.get("id"));
        assertEquals(TOKEN, login.get("token"));
    }

    /**
     * Should return an error response
     */
    @Test
    public void changeRoleNotRoleId() throws Exception {
        final String query = "query {changeRole {id token}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument roleId"));
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void changeRoleInvalidUser() throws Exception {
        final String query = "query {changeRole(roleId: \"invalid\") {id token}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("changeRole"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("User doesn't have personal information associated.", error.get("message"));
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void changeRoleInvalidRole() throws Exception {
        final String query = "query {changeRole(roleId: \"invalid\") {id token}}";
        final Map mapResult = integrationTest.performGraphQL(query, CHANGE_ROLE_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("changeRole"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("User doesn't have the requested Role.", error.get("message"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void changeRole() throws Exception {
        final String query = "query {changeRole(roleId: \"" + dbRoles.get(1).getId() + "\") {id token}}";
        final Map mapResult = integrationTest.performGraphQL(query, CHANGE_ROLE_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map changeRole = data.get("changeRole");

        assertNull(mapResult.get("errors"));
        assertEquals("mockID", changeRole.get("id"));
        assertEquals(TOKEN, changeRole.get("token"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void ping() throws Exception {
        final String query = "query {ping {id token}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map ping = data.get("ping");

        assertNull(mapResult.get("errors"));
        assertEquals("123", ping.get("id"));
        assertEquals(TOKEN, ping.get("token"));
    }
}