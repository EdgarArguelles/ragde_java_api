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
import ragde.models.Role;
import ragde.repositories.*;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.TokenService;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("unchecked")
public class PermissionIntegrationTest {

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

    private List<Role> dbRoles;

    private List<Permission> dbPermissions;

    private IntegrationTest integrationTest;

    private final String VIEW_ROLES_TOKEN = "view";

    private final String CREATE_ROLES_TOKEN = "create";

    private final String REMOVE_ROLES_TOKEN = "remove";

    @BeforeEach
    public void setup() throws Exception {
        given(tokenService.getLoggedUser(VIEW_ROLES_TOKEN)).willReturn(
                new LoggedUser(null, null, null, null, Set.of("VIEW_ROLES")));
        given(tokenService.getLoggedUser(CREATE_ROLES_TOKEN)).willReturn(
                new LoggedUser(null, null, null, null, Set.of("CREATE_ROLES")));
        given(tokenService.getLoggedUser(REMOVE_ROLES_TOKEN)).willReturn(
                new LoggedUser(null, null, null, null, Set.of("REMOVE_ROLES")));

        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
        IntegrationTest.cleanAllData(authenticationRepository, authProviderRepository, personRepository, roleRepository, permissionRepository);

        dbPermissions = List.of(
                new Permission("N", "D"),
                new Permission("N2", "D2")
        );
        permissionRepository.saveAll(dbPermissions);

        dbRoles = List.of(new Role("NR", "DR", Set.of(dbPermissions.get(1))));
        roleRepository.saveAll(dbRoles);
    }

    /**
     * Should return a success response
     */
    @Test
    public void permissions() throws Exception {
        final String query = "query {permissions {id name roles{id name}}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map<String, List> data = (Map) mapResult.get("data");
        final List<Map> permissions = data.get("permissions");
        final List<Map> roles = (List) permissions.get(1).get("roles");

        assertNull(mapResult.get("errors"));
        assertEquals(2, permissions.size());
        assertEquals("N", permissions.get(0).get("name"));
        assertEquals("N2", permissions.get(1).get("name"));
        assertEquals("NR", roles.get(0).get("name"));
    }

    /**
     * Should return an error response
     */
    @Test
    public void permissionNotID() throws Exception {
        final String query = "query {permission {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument id"));
    }

    /**
     * Should return a 404 error response
     */
    @Test
    public void permissionNotFound() throws Exception {
        final String query = "query {permission(id: 123456) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("permission"));
        assertEquals(404, extensions.get("errorCode"));
        assertEquals("Data don't found.", error.get("message"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void permission() throws Exception {
        final String query = "query {permission(id: " + dbPermissions.get(1).getId() + ") {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map permission = data.get("permission");

        assertNull(mapResult.get("errors"));
        assertEquals("N2", permission.get("name"));
    }

    /**
     * Should return an error response
     */
    @Test
    public void permissionByNameNotName() throws Exception {
        final String query = "query {permissionByName {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument name"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void permissionByNameNotFound() throws Exception {
        final String query = "query {permissionByName(name: \"123456\") {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");

        assertNull(mapResult.get("errors"));
        assertNull(data.get("permissionByName"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void permissionByName() throws Exception {
        final String query = "query {permissionByName(name: \"N2\") {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map permission = data.get("permissionByName");

        assertNull(mapResult.get("errors"));
        assertEquals("N2", permission.get("name"));
    }

    /**
     * Should return an error response
     */
    @Test
    public void createPermissionNotData() throws Exception {
        final String query = "mutation {createPermission {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument permission"));

        // not inserted in data base
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void createPermissionInvalid() throws Exception {
        final String query = "mutation {createPermission(permission: {}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("createPermission"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Some data aren't valid.", error.get("message"));

        // not inserted in data base
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void createPermissionDuplicated() throws Exception {
        final String query = "mutation {createPermission(permission: {name: \"N2\" description: \"Desc\"}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("createPermission"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Permission name 'N2' is already used.", error.get("message"));

        // not inserted in data base
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should return a success response
     */
    @Test
    public void createPermission() throws Exception {
        final String query = "mutation {createPermission(permission: {name: \"N3\" description: \"Desc\"}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map permission = data.get("createPermission");

        assertNull(mapResult.get("errors"));
        assertEquals("N3", permission.get("name"));

        // inserted in data base
        assertEquals(permissionRepository.count(), 3);
    }

    /**
     * Should return an error response
     */
    @Test
    public void updatePermissionNotData() throws Exception {
        final String query = "mutation {updatePermission {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument permission"));

        // not updated in data base
        final List<Permission> newDBPermissions = permissionRepository.findAll();
        newDBPermissions.forEach(pe -> pe.setRoles(null));
        assertNotSame(dbPermissions, newDBPermissions);
        assertEquals(dbPermissions, newDBPermissions);
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void updatePermissionInvalid() throws Exception {
        final String query = "mutation {updatePermission(permission: {}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("updatePermission"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Some data aren't valid.", error.get("message"));

        // not updated in data base
        final List<Permission> newDBPermissions = permissionRepository.findAll();
        newDBPermissions.forEach(pe -> pe.setRoles(null));
        assertNotSame(dbPermissions, newDBPermissions);
        assertEquals(dbPermissions, newDBPermissions);
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should return a 404 error response
     */
    @Test
    public void updatePermissionNotFound() throws Exception {
        final String query = "mutation {updatePermission(permission: {id: \"123456\" name: \"N3\" description: \"Desc\"}) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("updatePermission"));
        assertEquals(404, extensions.get("errorCode"));
        assertEquals("Data don't found.", error.get("message"));

        // not updated in data base
        final List<Permission> newDBPermissions = permissionRepository.findAll();
        newDBPermissions.forEach(pe -> pe.setRoles(null));
        assertNotSame(dbPermissions, newDBPermissions);
        assertEquals(dbPermissions, newDBPermissions);
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should return a success response
     */
    @Test
    public void updatePermission() throws Exception {
        final String query = "mutation {updatePermission(permission: {id: \"" + dbPermissions.get(1).getId() + "\" name: \"N3\" description: \"Desc\"}) {id name description}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map permission = data.get("updatePermission");

        assertNull(mapResult.get("errors"));
        assertEquals("N2", permission.get("name"));
        assertEquals("Desc", permission.get("description"));

        // updated in data base
        final List<Permission> newDBPermissions = permissionRepository.findAll();
        newDBPermissions.forEach(pe -> pe.setRoles(null));
        assertNotSame(dbPermissions, newDBPermissions);
        assertNotEquals(dbPermissions, newDBPermissions);
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should return an error response
     */
    @Test
    public void deletePermissionNotID() throws Exception {
        final String query = "mutation {deletePermission {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, REMOVE_ROLES_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument id"));

        // not deleted in data base
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should return a 404 error response
     */
    @Test
    public void deletePermissionNotFound() throws Exception {
        final String query = "mutation {deletePermission(id: 123456) {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, REMOVE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("deletePermission"));
        assertEquals(404, extensions.get("errorCode"));
        assertEquals("Data don't found.", error.get("message"));

        // not deleted in data base
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void deletePermissionWhenUsed() throws Exception {
        final String query = "mutation {deletePermission(id: " + dbPermissions.get(1).getId() + ") {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, REMOVE_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("deletePermission"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("There are some roles using the Permission 'N2'.", error.get("message"));

        // not deleted in data base
        assertEquals(permissionRepository.count(), 2);
    }

    /**
     * Should return a success response
     */
    @Test
    public void deletePermission() throws Exception {
        final String query = "mutation {deletePermission(id: " + dbPermissions.get(0).getId() + ") {id name}}";
        final Map mapResult = integrationTest.performGraphQL(query, REMOVE_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map permission = data.get("deletePermission");

        assertNull(mapResult.get("errors"));
        assertEquals("N", permission.get("name"));

        // deleted in data base
        assertEquals(permissionRepository.count(), 1);
    }

    /**
     * Should return an error response
     */
    @Test
    public void permissionPageNotData() throws Exception {
        final String query = "query {permissionPage {totalElements}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Missing field argument pageDataRequest"));
    }

    /**
     * Should return a 400 error response
     */
    @Test
    public void permissionPageInvalid() throws Exception {
        final String query = "query {permissionPage(pageDataRequest: {}) {totalElements}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map extensions = (Map) errors.get(0).get("extensions");
        final Map error = (Map) extensions.get("error");

        assertNull(data.get("permissionPage"));
        assertEquals(400, extensions.get("errorCode"));
        assertEquals("Some data aren't valid.", error.get("message"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void permissionPage() throws Exception {
        final String query = "query {permissionPage(pageDataRequest: {page: 0, size: 1}) {totalElements content{id}}}";
        final Map mapResult = integrationTest.performGraphQL(query, VIEW_ROLES_TOKEN);
        final Map<String, Map> data = (Map) mapResult.get("data");
        final Map permissionPage = data.get("permissionPage");
        final List permissions = (List) permissionPage.get("content");

        assertNull(mapResult.get("errors"));
        assertEquals(2, permissionPage.get("totalElements"));
        assertEquals(1, permissions.size());
    }
}