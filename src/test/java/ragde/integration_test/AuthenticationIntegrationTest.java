package ragde.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ragde.models.*;
import ragde.repositories.*;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.TokenService;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("unchecked")
public class AuthenticationIntegrationTest {

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

    private List<Authentication> dbAuthentications;

    private List<AuthProvider> dbAuthProviders;

    private List<Person> dbPeople;

    private IntegrationTest integrationTest;

    private final String VIEW_USERS_TOKEN = "view";

    private final String CREATE_USERS_TOKEN = "create";

    private final String REMOVE_USERS_TOKEN = "remove";

    @BeforeEach
    public void setup() throws Exception {
        given(tokenService.getLoggedUser(VIEW_USERS_TOKEN)).willReturn(
                new LoggedUser(null, null, null, null, Set.of("VIEW_USERS")));
        given(tokenService.getLoggedUser(CREATE_USERS_TOKEN)).willReturn(
                new LoggedUser(null, null, null, null, Set.of("CREATE_USERS")));
        given(tokenService.getLoggedUser(REMOVE_USERS_TOKEN)).willReturn(
                new LoggedUser(null, null, null, null, Set.of("REMOVE_USERS")));

        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
        IntegrationTest.cleanAllData(authenticationRepository, authProviderRepository, personRepository, roleRepository, permissionRepository);

        final List<Permission> dbPermissions = List.of(
                new Permission("P1", "D1"),
                new Permission("P2", "D2")
        );
        permissionRepository.saveAll(dbPermissions);

        final List<Role> dbRoles = List.of(
                new Role("R1", "D1", new HashSet<>(dbPermissions)),
                new Role("R2", "D2", Set.of(dbPermissions.get(0)))
        );
        roleRepository.saveAll(dbRoles);

        dbPeople = List.of(
                new Person("N1", "LN1", LocalDate.now(), 1, "A", null, new HashSet<>(dbRoles)),
                new Person("N2", "LN2", LocalDate.now(), 2, "B", "a@a.com", Set.of(dbRoles.get(0)))
        );
        personRepository.saveAll(dbPeople);

        dbAuthProviders = List.of(
                new AuthProvider("N1", "D1", "AK1", "AS1"),
                new AuthProvider("N2", "U2", "AK2", "AS2"),
                new AuthProvider("LOCAL", "D3", "AK3", "AS3")
        );
        authProviderRepository.saveAll(dbAuthProviders);

        dbAuthentications = List.of(
                new Authentication("user1", "123", dbAuthProviders.get(0), dbPeople.get(0)),
                new Authentication("user2", null, dbAuthProviders.get(0), dbPeople.get(1)),
                new Authentication("user3", "123", dbAuthProviders.get(2), dbPeople.get(1))
        );
        authenticationRepository.saveAll(dbAuthentications);
    }

    /**
     * Should return an error response
     */
    @Test
    public void validateGraphQLIgnore() throws Exception {
        final String query = "query {authentications {id username password}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Field 'password' in type 'Authentication' is undefined"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void authentications() throws Exception {
        final String query = "query {authentications {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final Map<String, List> data = (Map) mapResult.get("data");
        final List<Map> authentications = data.get("authentications");

        assertNull(mapResult.get("errors"));
        assertEquals(3, authentications.size());
        assertEquals("user1", authentications.get(0).get("username"));
        assertEquals("user2", authentications.get(1).get("username"));
        assertEquals("user3", authentications.get(2).get("username"));
    }

    /**
     * Should return an error response
     */
    @Test
    public void authenticationNotID() throws Exception {
        final String query = "query {authentication {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument id"));
    }

    /**
     * Should return a 404 error response
     */
    @Test
    public void authenticationNotFound() throws Exception {
        final String query = "query {authentication(id: 123456) {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("authentication"));
        assertEquals(404, extensions.get("errorCode"));
        assertEquals("Data don't found.", error.get("message"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void authentication() throws Exception {
        final String query = "query {authentication(id: " + dbAuthentications.get(1).getId() + ") {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map authentication = data.get("authentication");

        assertNull(mapResult.get("errors"));
        assertEquals("user2", authentication.get("username"));
    }

    /**
     * Should return an error response
     */
    @Test
    public void authenticationByUsernameNotUsername() throws Exception {
        final String query = "query {authenticationByUsername {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument username"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void authenticationByUsernameNotFound() throws Exception {
        final String query = "query {authenticationByUsername(username: \"123456\") {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");

        assertNull(mapResult.get("errors"));
        assertNull(data.get("authenticationByUsername"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void authenticationByUsername() throws Exception {
        final String query = "query {authenticationByUsername(username: \"user2\") {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map authentication = data.get("authenticationByUsername");

        assertNull(mapResult.get("errors"));
        assertEquals("user2", authentication.get("username"));
    }

    /**
     * Should return an error response
     */
    @Test
    public void authenticationByAuthProviderAndPersonNotData() throws Exception {
        final String query = "query {authenticationByAuthProviderAndPerson {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument authProvider"));
        assertTrue(errors.get(1).get("message").contains("Missing field argument person"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void authenticationByAuthProviderAndPersonNotFound() throws Exception {
        final String query = "query {authenticationByAuthProviderAndPerson(authProvider: {id:\"123456\"} person: {id:\"123456\"}) {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");

        assertNull(mapResult.get("errors"));
        assertNull(data.get("authenticationByAuthProviderAndPerson"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void authenticationByAuthProviderAndPerson() throws Exception {
        final String query = "query {authenticationByAuthProviderAndPerson(authProvider: {id:\"" + dbAuthProviders.get(0).getId() + "\"} person: {id:\"" + dbPeople.get(0).getId() + "\"}) {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map authentication = data.get("authenticationByAuthProviderAndPerson");

        assertNull(mapResult.get("errors"));
        assertEquals("user1", authentication.get("username"));
    }

    /**
     * Should return an error response
     */
    @Test
    public void createAuthenticationNotData() throws Exception {
        final String query = "mutation {createAuthentication {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument authentication"));

        // not inserted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);

        // authProviders not edited
        validateAuthProvidersNotEdited();
        // people not edited
        validatePeopleNotEdited();
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void createAuthenticationInvalid() throws Exception {
        final String query = "mutation {createAuthentication(authentication: {}) {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("createAuthentication"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Some data aren't valid.", error.get("message"));

        // not inserted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);

        // authProviders not edited
        validateAuthProvidersNotEdited();
        // people not edited
        validatePeopleNotEdited();
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void createAuthenticationPasswordNull() throws Exception {
        final String query = "mutation {createAuthentication(authentication: {username: \"user4\" authProvider: {} person: {}}) {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("createAuthentication"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Password can't not be null.", error.get("message"));

        // not inserted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);

        // authProviders not edited
        validateAuthProvidersNotEdited();
        // people not edited
        validatePeopleNotEdited();
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void createAuthenticationUsernameDuplicated() throws Exception {
        final String query = "mutation {createAuthentication(authentication: {username: \"user2\" password: \"123\" authProvider: {} person: {}}) {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("createAuthentication"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Username 'user2' is already used by another user.", error.get("message"));

        // not inserted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);

        // authProviders not edited
        validateAuthProvidersNotEdited();
        // people not edited
        validatePeopleNotEdited();
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void createAuthenticationPersonAndProviderDuplicated() throws Exception {
        final String query = "mutation {createAuthentication(authentication: {username: \"user4\" password: \"123\" authProvider: {} person: {id: \"" + dbPeople.get(1).getId() + "\"}}) {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("createAuthentication"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("'N2 LN2' already has an Authorization with provider 'LOCAL'.", error.get("message"));

        // not inserted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);

        // authProviders not edited
        validateAuthProvidersNotEdited();
        // people not edited
        validatePeopleNotEdited();
    }

    /**
     * Should return an error response
     */
    @Test
    public void createAuthenticationInvalidPersonID() throws Exception {
        final String query = "mutation {createAuthentication(authentication: {username: \"user4\" password: \"123\" authProvider: {} person: {id: \"invalid\"}}) {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map<String, Map<String, String>> extensions = (Map) errors.get(0).get("extensions");

        assertNull(data.get("createAuthentication"));
        assertEquals("INTERNAL_SERVER_ERROR", extensions.get("errorType"));
        assertEquals(500, extensions.get("errorCode"));
        assertTrue(extensions.get("error").get("devMessage").contains("could not extract ResultSet"));

        // not inserted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);

        // authProviders not edited
        validateAuthProvidersNotEdited();
        // people not edited
        validatePeopleNotEdited();
    }

    /**
     * Should return an error response
     */
    @Test
    public void createAuthenticationNotPersonID() throws Exception {
        final String query = "mutation {createAuthentication(authentication: {username: \"user4\" password: \"123\" authProvider: {} person: {name: \"invalid\"}}) {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map<String, Map<String, String>> extensions = (Map) errors.get(0).get("extensions");

        assertNull(data.get("createAuthentication"));
        assertEquals("INTERNAL_SERVER_ERROR", extensions.get("errorType"));
        assertEquals(500, extensions.get("errorCode"));
        assertTrue(extensions.get("error").get("devMessage").contains("object references an unsaved"));

        // not inserted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);

        // authProviders not edited
        validateAuthProvidersNotEdited();
        // people not edited
        validatePeopleNotEdited();
    }

    /**
     * Should return a success response
     */
    @Test
    public void createAuthentication() throws Exception {
        final String query = "mutation {createAuthentication(authentication: {username: \"user4\" password: \"123\" authProvider: {} person: {id: \"" + dbPeople.get(0).getId() + "\"}}) {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map authentication = data.get("createAuthentication");

        assertNull(mapResult.get("errors"));
        assertEquals("user4", authentication.get("username"));
        assertEquals(DigestUtils.sha512Hex("123"), authenticationRepository.findByUsername("user4").getPassword());

        // inserted in data base
        assertEquals(authenticationRepository.count(), 4);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);

        // authProviders not edited
        validateAuthProvidersNotEdited();
        // people not edited
        validatePeopleNotEdited();
    }

    /**
     * Should return an error response
     */
    @Test
    public void updateAuthenticationNotData() throws Exception {
        final String query = "mutation {updateAuthentication {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument authentication"));

        // not updated in data base
        validateAuthenticationsNotEdited();
        validateAuthProvidersNotEdited();
        validatePeopleNotEdited();
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void updateAuthenticationInvalid() throws Exception {
        final String query = "mutation {updateAuthentication(authentication: {}) {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("updateAuthentication"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Some data aren't valid.", error.get("message"));

        // not updated in data base
        validateAuthenticationsNotEdited();
        validateAuthProvidersNotEdited();
        validatePeopleNotEdited();
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void updateAuthenticationPasswordNull() throws Exception {
        final String query = "mutation {updateAuthentication(authentication: {username: \"user4\" authProvider: {} person: {}}) {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("updateAuthentication"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Password can't not be null.", error.get("message"));

        // not updated in data base
        validateAuthenticationsNotEdited();
        validateAuthProvidersNotEdited();
        validatePeopleNotEdited();
    }

    /**
     * Should return a 404 error response
     */
    @Test
    public void updateAuthenticationNotFound() throws Exception {
        final String query = "mutation {updateAuthentication(authentication: {id: \"12345\" username: \"user4\" password: \"123\" authProvider: {} person: {}}) {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("updateAuthentication"));
        assertEquals(404, extensions.get("errorCode"));
        assertEquals("Data don't found.", error.get("message"));

        // not updated in data base
        validateAuthenticationsNotEdited();
        validateAuthProvidersNotEdited();
        validatePeopleNotEdited();
    }

    /**
     * Should return a success response
     */
    @Test
    public void updateAuthentication() throws Exception {
        final String query = "mutation {updateAuthentication(authentication: {id: \"" + dbAuthentications.get(1).getId() + "\" username: \"user4\" password: \"ABC\" authProvider: {} person: {}}) {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map authentication = data.get("updateAuthentication");

        assertNull(mapResult.get("errors"));
        assertEquals("user2", authentication.get("username"));
        assertEquals(DigestUtils.sha512Hex("ABC"), authenticationRepository.findByUsername("user2").getPassword());

        // updated in data base
        validateAuthenticationsNotEdited(); // because is only possible to update password, and password is returned as null, apparently nothing change
        validateAuthProvidersNotEdited();
        validatePeopleNotEdited();
    }

    /**
     * Should return an error response
     */
    @Test
    public void deleteAuthenticationNotID() throws Exception {
        final String query = "mutation {deleteAuthentication {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, REMOVE_USERS_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument id"));

        // not deleted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);
    }

    /**
     * Should return a 404 error response
     */
    @Test
    public void deleteAuthenticationNotFound() throws Exception {
        final String query = "mutation {deleteAuthentication(id: 123456) {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, REMOVE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("deleteAuthentication"));
        assertEquals(404, extensions.get("errorCode"));
        assertEquals("Data don't found.", error.get("message"));

        // not deleted in data base
        assertEquals(authenticationRepository.count(), 3);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);
    }

    /**
     * Should return a success response
     */
    @Test
    public void deleteAuthentication() throws Exception {
        final String query = "mutation {deleteAuthentication(id: " + dbAuthentications.get(1).getId() + ") {id username}}";
        final Map mapResult = integrationTest.performGraphQL(query, REMOVE_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map authentication = data.get("deleteAuthentication");

        assertNull(mapResult.get("errors"));
        assertEquals("user2", authentication.get("username"));

        // deleted in data base
        assertEquals(authenticationRepository.count(), 2);
        assertEquals(authProviderRepository.count(), 3);
        assertEquals(personRepository.count(), 2);
    }

    /**
     * Should return an error response
     */
    @Test
    public void authenticationPageNotData() throws Exception {
        final String query = "query {authenticationPage {totalElements}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument pageDataRequest"));
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void authenticationPageInvalid() throws Exception {
        final String query = "query {authenticationPage(pageDataRequest: {}) {totalElements}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("authenticationPage"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Some data aren't valid.", error.get("message"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void authenticationPage() throws Exception {
        final String query = "query {authenticationPage(pageDataRequest: {page: 0, size: 1}) {totalElements content{id}}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map authenticationPage = data.get("authenticationPage");
        final List authentications = (List) authenticationPage.get("content");

        assertNull(mapResult.get("errors"));
        assertEquals(3, authenticationPage.get("totalElements"));
        assertEquals(1, authentications.size());
    }

    private void validateAuthenticationsNotEdited() {
        List<Authentication> newDBAuthentications = authenticationRepository.findAll();
        newDBAuthentications.forEach(a -> {
            a.setPassword(null);
            a.setPerson(null);
            a.setAuthProvider(null);
        });
        dbAuthentications.forEach(a -> {
            a.setPassword(null);
            a.setPerson(null);
            a.setAuthProvider(null);
        });
        // sorted is needed when model is edited to pass isEquals conditions
        newDBAuthentications = newDBAuthentications.stream().sorted(Comparator.comparing(Authentication::getId)).collect(Collectors.toList());
        dbAuthentications = dbAuthentications.stream().sorted(Comparator.comparing(Authentication::getId)).collect(Collectors.toList());
        assertNotSame(dbAuthentications, newDBAuthentications);
        assertEquals(dbAuthentications, newDBAuthentications);
        assertEquals(authenticationRepository.count(), 3);
    }

    private void validateAuthProvidersNotEdited() {
        final List<AuthProvider> newDBAuthProviders = authProviderRepository.findAll();
        newDBAuthProviders.forEach(a -> {
            a.setAuthKey(null);
            a.setAuthSecret(null);
            a.setAuthentications(null);
        });
        dbAuthProviders.forEach(a -> {
            String value = null;
            a.setAuthKey(value);
            a.setAuthSecret(value);
            a.setAuthentications(null);
        });
        assertNotSame(dbAuthProviders, newDBAuthProviders);
        assertEquals(dbAuthProviders, newDBAuthProviders);
        assertEquals(authProviderRepository.count(), 3);
    }

    private void validatePeopleNotEdited() {
        final List<Person> newDBPeople = personRepository.findAll();
        newDBPeople.forEach(p -> {
            p.setAuthentications(null);
            p.setRoles(null);
        });
        dbPeople.forEach(p -> p.setRoles(null));
        assertNotSame(dbPeople, newDBPeople);
        assertEquals(dbPeople, newDBPeople);
        assertEquals(personRepository.count(), 2);
    }
}