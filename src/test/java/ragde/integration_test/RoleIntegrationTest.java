package ragde.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import ragde.models.Permission;
import ragde.models.Person;
import ragde.models.Role;
import ragde.repositories.*;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.TokenService;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("unchecked")
public class RoleIntegrationTest {

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

    private List<Permission> dbPermissions;

    private IntegrationTest integrationTest;

    private final String VIEW_ROLES_TOKEN = "view";

    private final String CREATE_ROLES_TOKEN = "create";

    private final String REMOVE_ROLES_TOKEN = "remove";

    @BeforeEach
    public void setup() throws Exception {
        given(tokenService.getLoggedUser(VIEW_ROLES_TOKEN)).willReturn(
                new LoggedUser(null, null, null, null, Set.of("VIEW_ROLES", "VIEW_USERS")));
        given(tokenService.getLoggedUser(CREATE_ROLES_TOKEN)).willReturn(
                new LoggedUser(null, null, null, null, Set.of("CREATE_ROLES")));
        given(tokenService.getLoggedUser(REMOVE_ROLES_TOKEN)).willReturn(
                new LoggedUser(null, null, null, null, Set.of("REMOVE_ROLES")));

        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
        IntegrationTest.cleanAllData(authenticationRepository, authProviderRepository, personRepository, roleRepository, permissionRepository);

        dbPermissions = List.of(
                new Permission("P1", "D1"),
                new Permission("P2", "D2")
        );
        permissionRepository.saveAll(dbPermissions);

        dbRoles = List.of(
                new Role("R1", "D1", Collections.emptySet()),
                new Role("R2", "D2", Set.of(dbPermissions.get(1))),
                new Role("R3", "D3", new HashSet<>(dbPermissions))
        );
        roleRepository.saveAll(dbRoles);

        dbPeople = List.of(new Person("N", "LN", LocalDate.now(), 1, "A", null, Set.of(dbRoles.get(2))));
        personRepository.saveAll(dbPeople);
    }

    /**
     * Should return a success response
     */
    @Test
    public void roles() throws Exception {
        final String query = "query {roles {id name people{id fullName}}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map<String, List> data = (Map) mapResult.get("data");
        final List<Map> roles = data.get("roles");
        final List<Map> people = (List) roles.get(2).get("people");

        assertNull(mapResult.get("errors"));
        assertEquals(3, roles.size());
        assertEquals("R1", roles.get(0).get("name"));
        assertEquals("R2", roles.get(1).get("name"));
        assertEquals("R3", roles.get(2).get("name"));
        assertEquals("N LN", people.get(0).get("fullName"));
    }

    /**
     * Should return an error response
     */
    @Test
    public void roleNotID() throws Exception {
        final String query = "query {role {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument id"));
    }

    /**
     * Should return a 404 error response
     */
    @Test
    public void roleNotFound() throws Exception {
        final String query = "query {role(id: 123456) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("role"));
        assertEquals(404, extensions.get("errorCode"));
        assertEquals("Data don't found.", error.get("message"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void role() throws Exception {
        final String query = "query {role(id: " + dbRoles.get(1).getId() + ") {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map role = data.get("role");

        assertNull(mapResult.get("errors"));
        assertEquals("R2", role.get("name"));
    }

    /**
     * Should return an error response
     */
    @Test
    public void roleByNameNotName() throws Exception {
        final String query = "query {roleByName {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument name"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void roleByNameNotFound() throws Exception {
        final String query = "query {roleByName(name: \"123456\") {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");

        assertNull(mapResult.get("errors"));
        assertNull(data.get("roleByName"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void roleByName() throws Exception {
        final String query = "query {roleByName(name: \"R2\") {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map role = data.get("roleByName");

        assertNull(mapResult.get("errors"));
        assertEquals("R2", role.get("name"));
    }

    /**
     * Should return an error response
     */
    @Test
    public void createRoleNotData() throws Exception {
        final String query = "mutation {createRole {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument role"));

        // not inserted in data base
        assertEquals(roleRepository.count(), 3);
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void createRoleInvalid() throws Exception {
        final String query = "mutation {createRole(role: {}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("createRole"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Some data aren't valid.", error.get("message"));

        // not inserted in data base
        assertEquals(roleRepository.count(), 3);
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void createRoleDuplicated() throws Exception {
        final String query = "mutation {createRole(role: {name: \"R3\" description: \"Desc\"}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("createRole"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Role name 'R3' is already used.", error.get("message"));

        // not inserted in data base
        assertEquals(roleRepository.count(), 3);
    }

    /**
     * Should return an error response
     */
    @Test
    public void createRoleInvalidPermissionID() throws Exception {
        final String query = "mutation {createRole(role: {name: \"R4\" description: \"Desc\" permissions: [{id: \"invalid\"}]}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map<String, Map<String, String>> extensions = (Map) errors.get(0).get("extensions");

        assertNull(data.get("createRole"));
        assertEquals("INTERNAL_SERVER_ERROR", extensions.get("errorType"));
        assertEquals(500, extensions.get("errorCode"));
        assertTrue(extensions.get("error").get("devMessage").contains("could not execute statement"));

        // not inserted in data base
        assertEquals(roleRepository.count(), 3);
    }

    /**
     * Should return an error response
     */
    @Test
    public void createRoleNotPermissionID() throws Exception {
        final String query = "mutation {createRole(role: {name: \"R4\" description: \"Desc\" permissions: [{name: \"invalid\"}]}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map<String, Map<String, String>> extensions = (Map) errors.get(0).get("extensions");

        assertNull(data.get("createRole"));
        assertEquals("INTERNAL_SERVER_ERROR", extensions.get("errorType"));
        assertEquals(500, extensions.get("errorCode"));
        assertTrue(extensions.get("error").get("devMessage").contains("object references an unsaved"));

        // not inserted in data base
        assertEquals(roleRepository.count(), 3);
    }

    /**
     * Should return a success response
     */
    @Test
    public void createRoleNullPermissions() throws Exception {
        final String query = "mutation {createRole(role: {name: \"R4\" description: \"Desc\"}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map role = data.get("createRole");

        assertNull(mapResult.get("errors"));
        assertEquals("R4", role.get("name"));

        // inserted in data base
        assertEquals(roleRepository.count(), 4);
    }

    /**
     * Should return a success response
     */
    @Test
    public void createRoleEmptyPermissions() throws Exception {
        final String query = "mutation {createRole(role: {name: \"R4\" description: \"Desc\" permissions: []}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map role = data.get("createRole");

        assertNull(mapResult.get("errors"));
        assertEquals("R4", role.get("name"));

        // inserted in data base
        assertEquals(roleRepository.count(), 4);
    }

    /**
     * Should return a success response
     */
    @Test
    public void createRole() throws Exception {
        final String query = "mutation {createRole(role: {name: \"R4\" description: \"Desc\" permissions: [{id: \"" + dbPermissions.get(0).getId() + "\"}]}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map role = data.get("createRole");

        assertNull(mapResult.get("errors"));
        assertEquals("R4", role.get("name"));

        // inserted in data base
        assertEquals(roleRepository.count(), 4);
    }

    /**
     * Should return an error response
     */
    @Test
    public void updateRoleNotData() throws Exception {
        final String query = "mutation {updateRole {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument role"));

        // not updated in data base
        validateRolesNotEdited();
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void updateRoleInvalid() throws Exception {
        final String query = "mutation {updateRole(role: {}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("updateRole"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Some data aren't valid.", error.get("message"));

        // not updated in data base
        validateRolesNotEdited();
    }

    /**
     * Should return a 404 error response
     */
    @Test
    public void updateRoleNotFound() throws Exception {
        final String query = "mutation {updateRole(role: {id: \"123456\" name: \"N3\" description: \"Desc\"}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("updateRole"));
        assertEquals(404, extensions.get("errorCode"));
        assertEquals("Data don't found.", error.get("message"));

        // not updated in data base
        validateRolesNotEdited();
    }

    /**
     * Should return an error response
     */
    @Test
    public void updateRoleInvalidPermissionID() throws Exception {
        final String query = "mutation {updateRole(role: {id: \"" + dbRoles.get(1).getId() + "\" name: \"R3\" description: \"Desc\" permissions: [{id: \"invalid\"}]}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map<String, Map<String, String>> extensions = (Map) errors.get(0).get("extensions");

        assertNull(data.get("updateRole"));
        assertEquals("INTERNAL_SERVER_ERROR", extensions.get("errorType"));
        assertEquals(500, extensions.get("errorCode"));
        assertTrue(extensions.get("error").get("devMessage").contains("could not extract ResultSet"));

        // not updated in data base
        validateRolesNotEdited();
    }

    /**
     * Should return an error response
     */
    @Test
    public void updateRoleNotPermissionID() throws Exception {
        final String query = "mutation {updateRole(role: {id: \"" + dbRoles.get(1).getId() + "\" name: \"R3\" description: \"Desc\" permissions: [{name: \"invalid\"}]}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map<String, Map<String, String>> extensions = (Map) errors.get(0).get("extensions");

        assertNull(data.get("updateRole"));
        assertEquals("INTERNAL_SERVER_ERROR", extensions.get("errorType"));
        assertEquals(500, extensions.get("errorCode"));
        assertTrue(extensions.get("error").get("devMessage").contains("object references an unsaved"));

        // not updated in data base
        validateRolesNotEdited();
    }

    /**
     * Should return a success response
     */
    @Test
    public void updateRoleNullPermissions() throws Exception {
        final String query = "mutation {updateRole(role: {id: \"" + dbRoles.get(1).getId() + "\" name: \"R3\" description: \"Desc\"}) {id name description permissions {id}}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map role = data.get("updateRole");

        assertNull(mapResult.get("errors"));
        assertEquals("R2", role.get("name"));
        assertEquals("Desc", role.get("description"));
        assertNull(role.get("permissions"));

        // updated in data base
        validateRolesEdited();
    }

    /**
     * Should return a success response
     */
    @Test
    public void updateRoleEmptyPermissions() throws Exception {
        final String query = "mutation {updateRole(role: {id: \"" + dbRoles.get(1).getId() + "\" name: \"R3\" description: \"Desc\" permissions: []}) {id name description permissions {id}}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map role = data.get("updateRole");
        final List<Map> permissions = (List) role.get("permissions");

        assertNull(mapResult.get("errors"));
        assertEquals("R2", role.get("name"));
        assertEquals("Desc", role.get("description"));
        assertEquals(0, permissions.size());

        // updated in data base
        validateRolesEdited();
    }

    /**
     * Should return a success response
     */
    @Test
    public void updateRole() throws Exception {
        final String query = "mutation {updateRole(role: {id: \"" + dbRoles.get(1).getId() + "\" name: \"R3\" description: \"Desc\" permissions: [{id: \"" + dbPermissions.get(0).getId() + "\"}]}) {id name description permissions {id}}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map role = data.get("updateRole");
        final List<Map> permissions = (List) role.get("permissions");

        assertNull(mapResult.get("errors"));
        assertEquals("R2", role.get("name"));
        assertEquals("Desc", role.get("description"));
        assertEquals(1, permissions.size());
        assertEquals(dbPermissions.get(0).getId(), permissions.get(0).get("id"));

        // updated in data base
        validateRolesEdited();
    }

    /**
     * Should return an error response
     */
    @Test
    public void deleteRoleNotID() throws Exception {
        final String query = "mutation {deleteRole {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, REMOVE_ROLES_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument id"));

        // not deleted in data base
        assertEquals(roleRepository.count(), 3);
    }

    /**
     * Should return a 404 error response
     */
    @Test
    public void deleteRoleNotFound() throws Exception {
        final String query = "mutation {deleteRole(id: 123456) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, REMOVE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("deleteRole"));
        assertEquals(404, extensions.get("errorCode"));
        assertEquals("Data don't found.", error.get("message"));

        // not deleted in data base
        assertEquals(roleRepository.count(), 3);
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void deleteRoleWhenUsed() throws Exception {
        final String query = "mutation {deleteRole(id: " + dbRoles.get(2).getId() + ") {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, REMOVE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("deleteRole"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("There are some people using the Role 'R3'.", error.get("message"));

        // not deleted in data base
        assertEquals(roleRepository.count(), 3);
    }

    /**
     * Should return a success response
     */
    @Test
    public void deleteRole() throws Exception {
        final String query = "mutation {deleteRole(id: " + dbRoles.get(1).getId() + ") {id name permissions {id}}}";
        final Map mapResult = integrationTest.performGraphQL(query, REMOVE_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map role = data.get("deleteRole");

        assertNull(mapResult.get("errors"));
        assertEquals("R2", role.get("name"));
        assertNull(role.get("permissions"));

        // deleted in data base
        assertEquals(roleRepository.count(), 2);
    }

    /**
     * Should return an error response
     */
    @Test
    public void rolePageNotData() throws Exception {
        final String query = "query {rolePage {totalElements}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument pageDataRequest"));
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void rolePageInvalid() throws Exception {
        final String query = "query {rolePage(pageDataRequest: {}) {totalElements}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("rolePage"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Some data aren't valid.", error.get("message"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void rolePage() throws Exception {
        final String query = "query {rolePage(pageDataRequest: {page: 0, size: 1}) {totalElements content{id}}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map rolePage = data.get("rolePage");
        final List roles = (List) rolePage.get("content");

        assertNull(mapResult.get("errors"));
        assertEquals(3, rolePage.get("totalElements"));
        assertEquals(1, roles.size());
    }

    private void validateRolesEdited() {
        final List<Role> newDBRoles = roleRepository.findAll();
        newDBRoles.forEach(r -> {
            r.setPeople(null);
            r.setPermissions(null);
        });
        dbRoles.forEach(r -> r.setPermissions(null));
        assertNotSame(dbRoles, newDBRoles);
        assertNotEquals(dbRoles, newDBRoles);
        assertEquals(roleRepository.count(), 3);

        validatePermissionsNotEdited();
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
        assertEquals(roleRepository.count(), 3);

        validatePermissionsNotEdited();
    }

    private void validatePermissionsNotEdited() {
        final List<Permission> newDBPermissions = permissionRepository.findAll();
        newDBPermissions.forEach(p -> p.setRoles(null));
        assertNotSame(dbPermissions, newDBPermissions);
        assertEquals(dbPermissions, newDBPermissions);
        assertEquals(permissionRepository.count(), 2);
    }
}