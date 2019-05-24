package ragde.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ragde.models.*;
import ragde.repositories.*;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.TokenService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("unchecked")
public class PersonIntegrationTest {

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

    private List<Person> dbPeople;

    private List<Role> dbRoles;

    private List<Permission> dbPermissions;

    private IntegrationTest integrationTest;

    private final String VIEW_USERS_TOKEN = "view";

    private final String CREATE_USERS_TOKEN = "create";

    private final String REMOVE_USERS_TOKEN = "remove";

    @Before
    public void setup() throws Exception {
        given(tokenService.getLoggedUser(VIEW_USERS_TOKEN)).willReturn(
                new LoggedUser(null, null, null, null, Set.of("VIEW_USERS")));
        given(tokenService.getLoggedUser(CREATE_USERS_TOKEN)).willReturn(
                new LoggedUser(null, null, null, null, Set.of("CREATE_USERS")));
        given(tokenService.getLoggedUser(REMOVE_USERS_TOKEN)).willReturn(
                new LoggedUser(null, null, null, null, Set.of("REMOVE_USERS")));

        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
        IntegrationTest.cleanAllData(authenticationRepository, authProviderRepository, personRepository, roleRepository, permissionRepository);

        dbPermissions = List.of(new Permission("P1", "D1"));
        permissionRepository.saveAll(dbPermissions);

        dbRoles = List.of(
                new Role("R1", "D1", Collections.emptySet()),
                new Role("R2", "D2", new HashSet<>(dbPermissions))
        );
        roleRepository.saveAll(dbRoles);

        dbPeople = List.of(
                new Person("N1", "LN1", LocalDate.now(), 1, "A", "aa@aa.com", Collections.emptySet()),
                new Person("N2", "LN2", LocalDate.now(), 2, "B", "a@a.com", Set.of(dbRoles.get(1))),
                new Person("N3", "LN3", LocalDate.now(), 3, "C", null, new HashSet<>(dbRoles))
        );
        personRepository.saveAll(dbPeople);

        AuthProvider authProvider = authProviderRepository.save(new AuthProvider("N", "D", "AK", "AS"));
        dbAuthentications = List.of(new Authentication("user", "123", authProvider, dbPeople.get(1)));
        authenticationRepository.saveAll(dbAuthentications);
    }

    /**
     * Should return a success response
     */
    @Test
    public void people() throws Exception {
        final String query = "query {people {id fullName authentications{id username}}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final Map<String, List> data = (Map) mapResult.get("data");
        final List<Map> people = data.get("people");
        final List<Map> authentications = (List) people.get(1).get("authentications");

        assertNull(mapResult.get("errors"));
        assertEquals(3, people.size());
        assertEquals("N1 LN1", people.get(0).get("fullName"));
        assertEquals("N2 LN2", people.get(1).get("fullName"));
        assertEquals("N3 LN3", people.get(2).get("fullName"));
        assertEquals("user", authentications.get(0).get("username"));
    }

    /**
     * Should return an error response
     */
    @Test
    public void personNotID() throws Exception {
        final String query = "query {person {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument id"));
    }

    /**
     * Should return a 404 error response
     */
    @Test
    public void personNotFound() throws Exception {
        final String query = "query {person(id: 123456) {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("person"));
        assertEquals(404, extensions.get("errorCode"));
        assertEquals("Data don't found.", error.get("message"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void person() throws Exception {
        final String query = "query {person(id: " + dbPeople.get(1).getId() + ") {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map person = data.get("person");

        assertNull(mapResult.get("errors"));
        assertEquals("N2 LN2", person.get("fullName"));
    }

    /**
     * Should return an error response
     */
    @Test
    public void createPersonNotData() throws Exception {
        final String query = "mutation {createPerson {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument person"));

        // not inserted in data base
        assertEquals(personRepository.count(), 3);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void createPersonInvalid() throws Exception {
        final String query = "mutation {createPerson(person: {}) {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("createPerson"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Some data aren't valid.", error.get("message"));

        // not inserted in data base
        assertEquals(personRepository.count(), 3);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void createPersonInvalidCivilStatusAndSex() throws Exception {
        final String query = "mutation {createPerson(person: {name: \"N4\" lastName: \"LN4\" birthday: \"2050-08-28\" civilStatus: 0 sex: \"X\"}) {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");
        final List<Map> nestedErrors = (List) error.get("nestedErrors");

        assertNull(data.get("createPerson"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Some data aren't valid.", error.get("message"));
        assertEquals("'0' is not a valid Civil Status value, it only allows [1, 2]", nestedErrors.get(0).get("message"));
        assertEquals("'X' is not a valid Sex value, it only allows [M, F]", nestedErrors.get(1).get("message"));

        // not inserted in data base
        assertEquals(personRepository.count(), 3);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return an error response
     */
    @Test
    public void createPersonInvalidRoleID() throws Exception {
        final String query = "mutation {createPerson(person: {name: \"N4\" lastName: \"LN4\" birthday: \"2050-08-28\" civilStatus: 1 sex: \"M\" roles: {id: \"invalid\"}}) {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map<String, Map<String, String>> extensions = (Map) errors.get(0).get("extensions");

        assertNull(data.get("createPerson"));
        assertEquals("INTERNAL_SERVER_ERROR", extensions.get("errorType"));
        assertEquals(500, extensions.get("errorCode"));
        assertTrue(extensions.get("error").get("devMessage").contains("could not execute statement"));

        // not inserted in data base
        assertEquals(personRepository.count(), 3);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return an error response
     */
    @Test
    public void createPersonNotRoleID() throws Exception {
        final String query = "mutation {createPerson(person: {name: \"N4\" lastName: \"LN4\" birthday: \"2050-08-28\" civilStatus: 1 sex: \"M\" roles: {name: \"invalid\"}}) {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map<String, Map<String, String>> extensions = (Map) errors.get(0).get("extensions");

        assertNull(data.get("createPerson"));
        assertEquals("INTERNAL_SERVER_ERROR", extensions.get("errorType"));
        assertEquals(500, extensions.get("errorCode"));
        assertTrue(extensions.get("error").get("devMessage").contains("object references an unsaved"));

        // not inserted in data base
        assertEquals(personRepository.count(), 3);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return a success response
     */
    @Test
    public void createPersonNullRoles() throws Exception {
        final String query = "mutation {createPerson(person: {name: \"N4\" lastName: \"LN4\" birthday: \"2050-08-28\" civilStatus: 1 sex: \"M\"}) {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map person = data.get("createPerson");

        assertNull(mapResult.get("errors"));
        assertEquals("N4 LN4", person.get("fullName"));

        // inserted in data base
        assertEquals(personRepository.count(), 4);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return a success response
     */
    @Test
    public void createPersonEmptyRoles() throws Exception {
        final String query = "mutation {createPerson(person: {name: \"N4\" lastName: \"LN4\" birthday: \"2050-08-28\" civilStatus: 1 sex: \"M\" roles: []}) {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map person = data.get("createPerson");

        assertNull(mapResult.get("errors"));
        assertEquals("N4 LN4", person.get("fullName"));

        // inserted in data base
        assertEquals(personRepository.count(), 4);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return a success response
     */
    @Test
    public void createPerson() throws Exception {
        final String query = "mutation {createPerson(person: {name: \"N4\" lastName: \"LN4\" birthday: \"2050-08-28\" civilStatus: 1 sex: \"M\" roles: [{id: \"" + dbRoles.get(1).getId() + "\"}]}) {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map person = data.get("createPerson");

        assertNull(mapResult.get("errors"));
        assertEquals("N4 LN4", person.get("fullName"));

        // inserted in data base
        assertEquals(personRepository.count(), 4);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return an error response
     */
    @Test
    public void updatePersonNotData() throws Exception {
        final String query = "mutation {updatePerson {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument person"));

        // not updated in data base
        validatePeopleNotEdited();
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void updatePersonInvalid() throws Exception {
        final String query = "mutation {updatePerson(person: {}) {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("updatePerson"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Some data aren't valid.", error.get("message"));

        // not updated in data base
        validatePeopleNotEdited();
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void updatePersonInvalidCivilStatusAndSex() throws Exception {
        final String query = "mutation {updatePerson(person: {name: \"N4\" lastName: \"LN4\" birthday: \"2050-08-28\" civilStatus: 0 sex: \"X\"}) {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");
        final List<Map> nestedErrors = (List) error.get("nestedErrors");

        assertNull(data.get("updatePerson"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Some data aren't valid.", error.get("message"));
        assertEquals("'0' is not a valid Civil Status value, it only allows [1, 2]", nestedErrors.get(0).get("message"));
        assertEquals("'X' is not a valid Sex value, it only allows [M, F]", nestedErrors.get(1).get("message"));

        // not updated in data base
        validatePeopleNotEdited();
    }

    /**
     * Should return a 404 error response
     */
    @Test
    public void updatePersonNotFound() throws Exception {
        final String query = "mutation {updatePerson(person: {id: \"12345\" name: \"N4\" lastName: \"LN4\" birthday: \"2050-08-28\" civilStatus: 1 sex: \"M\"}) {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("updatePerson"));
        assertEquals(404, extensions.get("errorCode"));
        assertEquals("Data don't found.", error.get("message"));

        // not updated in data base
        validatePeopleNotEdited();
    }

    /**
     * Should return an error response
     */
    @Test
    public void updatePersonInvalidRoleID() throws Exception {
        final String query = "mutation {updatePerson(person: {id: \"" + dbPeople.get(0).getId() + "\" name: \"N4\" lastName: \"LN4\" birthday: \"2050-08-28\" civilStatus: 1 sex: \"M\" roles: [{id: \"invalid\"}]}) {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map<String, Map<String, String>> extensions = (Map) errors.get(0).get("extensions");

        assertNull(data.get("updatePerson"));
        assertEquals("INTERNAL_SERVER_ERROR", extensions.get("errorType"));
        assertEquals(500, extensions.get("errorCode"));
        assertTrue(extensions.get("error").get("devMessage").contains("could not extract ResultSet"));

        // not updated in data base
        validatePeopleNotEdited();
    }

    /**
     * Should return an error response
     */
    @Test
    public void updatePersonNotRoleID() throws Exception {
        final String query = "mutation {updatePerson(person: {id: \"" + dbPeople.get(0).getId() + "\" name: \"N4\" lastName: \"LN4\" birthday: \"2050-08-28\" civilStatus: 1 sex: \"M\" roles: [{name: \"invalid\"}]}) {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map<String, Map<String, String>> extensions = (Map) errors.get(0).get("extensions");

        assertNull(data.get("updatePerson"));
        assertEquals("INTERNAL_SERVER_ERROR", extensions.get("errorType"));
        assertEquals(500, extensions.get("errorCode"));
        assertTrue(extensions.get("error").get("devMessage").contains("object references an unsaved"));

        // not updated in data base
        validatePeopleNotEdited();
    }

    /**
     * Should return a success response
     */
    @Test
    public void updatePersonNullRoles() throws Exception {
        final String query = "mutation {updatePerson(person: {id: \"" + dbPeople.get(0).getId() + "\" name: \"N4\" lastName: \"LN4\" birthday: \"2050-08-28\" civilStatus: 2 sex: \"M\" email: \"eee@eee.com\"}) {id fullName birthday civilStatus sex email roles {id}}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map person = data.get("updatePerson");

        assertNull(mapResult.get("errors"));
        assertEquals("N4 LN4", person.get("fullName"));
        assertEquals("2050-08-28", person.get("birthday"));
        assertEquals(2, person.get("civilStatus"));
        assertEquals("M", person.get("sex"));
        assertEquals("eee@eee.com", person.get("email"));
        assertNull(person.get("roles"));

        // updated in data base
        validatePeopleEdited();
    }

    /**
     * Should return a success response
     */
    @Test
    public void updatePersonEmptyRoles() throws Exception {
        final String query = "mutation {updatePerson(person: {id: \"" + dbPeople.get(0).getId() + "\" name: \"N4\" lastName: \"LN4\" birthday: \"2050-08-28\" civilStatus: 2 sex: \"M\" email: \"eee@eee.com\" roles: []}) {id fullName birthday civilStatus sex email roles {id}}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map person = data.get("updatePerson");
        final List<Map> roles = (List) person.get("roles");

        assertNull(mapResult.get("errors"));
        assertEquals("N4 LN4", person.get("fullName"));
        assertEquals("2050-08-28", person.get("birthday"));
        assertEquals(2, person.get("civilStatus"));
        assertEquals("M", person.get("sex"));
        assertEquals("eee@eee.com", person.get("email"));
        assertEquals(0, roles.size());

        // updated in data base
        validatePeopleEdited();
    }

    /**
     * Should return a success response
     */
    @Test
    public void updatePerson() throws Exception {
        final String query = "mutation {updatePerson(person: {id: \"" + dbPeople.get(0).getId() + "\" name: \"N4\" lastName: \"LN4\" birthday: \"2050-08-28\" civilStatus: 2 sex: \"M\" email: \"eee@eee.com\" roles: [{id: \"" + dbRoles.get(1).getId() + "\"}]}) {id fullName birthday civilStatus sex email roles {id}}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map person = data.get("updatePerson");
        final List<Map> roles = (List) person.get("roles");

        assertNull(mapResult.get("errors"));
        assertEquals("N4 LN4", person.get("fullName"));
        assertEquals("2050-08-28", person.get("birthday"));
        assertEquals(2, person.get("civilStatus"));
        assertEquals("M", person.get("sex"));
        assertEquals("eee@eee.com", person.get("email"));
        assertEquals(1, roles.size());
        assertEquals(dbRoles.get(1).getId(), roles.get(0).get("id"));

        // updated in data base
        validatePeopleEdited();
    }

    /**
     * Should return an error response
     */
    @Test
    public void deletePersonNotID() throws Exception {
        final String query = "mutation {deletePerson {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, REMOVE_USERS_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument id"));

        // not deleted in data base
        assertEquals(personRepository.count(), 3);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return a 404 error response
     */
    @Test
    public void deletePersonNotFound() throws Exception {
        final String query = "mutation {deletePerson(id: 123456) {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, REMOVE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("deletePerson"));
        assertEquals(404, extensions.get("errorCode"));
        assertEquals("Data don't found.", error.get("message"));

        // not deleted in data base
        assertEquals(personRepository.count(), 3);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void deletePersonWhenUsed() throws Exception {
        final String query = "mutation {deletePerson(id: " + dbPeople.get(1).getId() + ") {id fullName}}";
        final Map mapResult = integrationTest.performGraphQL(query, REMOVE_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("deletePerson"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Person 'N2 LN2' has one or more authentications associated.", error.get("message"));

        // not deleted in data base
        assertEquals(personRepository.count(), 3);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return a success response
     */
    @Test
    public void deletePerson() throws Exception {
        final String query = "mutation {deletePerson(id: " + dbPeople.get(2).getId() + ") {id fullName roles {id}}}";
        final Map mapResult = integrationTest.performGraphQL(query, REMOVE_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map person = data.get("deletePerson");

        assertNull(mapResult.get("errors"));
        assertEquals("N3 LN3", person.get("fullName"));
        assertNull(person.get("roles"));

        // deleted in data base
        assertEquals(personRepository.count(), 2);
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return an error response
     */
    @Test
    public void personPageNotData() throws Exception {
        final String query = "query {personPage {totalElements}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument pageDataRequest"));
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void personPageInvalid() throws Exception {
        final String query = "query {personPage(pageDataRequest: {}) {totalElements}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("personPage"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Some data aren't valid.", error.get("message"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void personPage() throws Exception {
        final String query = "query {personPage(pageDataRequest: {page: 0, size: 1}) {totalElements content{id}}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_USERS_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map personPage = data.get("personPage");
        final List people = (List) personPage.get("content");

        assertNull(mapResult.get("errors"));
        assertEquals(3, personPage.get("totalElements"));
        assertEquals(1, people.size());
    }

    private void validatePeopleEdited() {
        final List<Person> newDBPeople = personRepository.findAll();
        newDBPeople.forEach(p -> {
            p.setAuthentications(null);
            p.setRoles(null);
        });
        dbPeople.forEach(p -> p.setRoles(null));
        assertNotSame(dbPeople, newDBPeople);
        assertNotEquals(dbPeople, newDBPeople);
        assertEquals(personRepository.count(), 3);

        validateRolesNotEdited();
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
        assertEquals(personRepository.count(), 3);

        validateRolesNotEdited();
    }

    private void validateRolesNotEdited() {
        final List<Role> newDBRoles = roleRepository.findAll();
        newDBRoles.forEach(r -> {
            r.setPeople(null);
            r.setPermissions(null);
        });
        dbRoles.forEach(r -> r.setPermissions(null));
        assertNotSame(dbRoles, newDBRoles);
        assertEquals(dbRoles, newDBRoles);
        assertEquals(roleRepository.count(), 2);
    }
}