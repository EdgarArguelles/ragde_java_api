package ragde.integration_test.authentications;

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
import ragde.integration_test.IntegrationTest;
import ragde.security.services.TokenService;

import java.util.Map;

import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PermissionAuthenticationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TokenService tokenService;

    private IntegrationTest integrationTest;

    private final String permissionsQuery = "query {permissions {id name roles{id}}}";

    private final String permissionQuery = "query {permission(id: 5) {id name}}";

    private final String permissionByNameQuery = "query {permissionByName(name: \"aa\") {id name}}";

    private final String createPermissionQuery = "mutation {createPermission(permission: {}) {id name}}";

    private final String updatePermissionQuery = "mutation {updatePermission(permission: {}) {id name}}";

    private final String deletePermissionQuery = "mutation {deletePermission(id: 5) {id name}}";

    private final String permissionPageQuery = "query {permissionPage(pageDataRequest: {}) {totalElements}}";

    @Before
    public void setup() throws Exception {
        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void permissionsNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(permissionsQuery, null);

        assertNull(data.get("permissions"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void permissionsTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(permissionsQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void permissionsNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(permissionsQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("permissions"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void permissionNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(permissionQuery, null);

        assertNull(data.get("permission"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void permissionTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(permissionQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void permissionNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(permissionQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("permission"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void permissionByNameNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(permissionByNameQuery, null);

        assertNull(data.get("permissionByName"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void permissionByNameTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(permissionByNameQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void permissionByNameNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(permissionByNameQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("permissionByName"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void createPermissionNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(createPermissionQuery, null);

        assertNull(data.get("createPermission"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void createPermissionTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(createPermissionQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void createPermissionNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(createPermissionQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("createPermission"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void updatePermissionNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(updatePermissionQuery, null);

        assertNull(data.get("updatePermission"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void updatePermissionTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(updatePermissionQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void updatePermissionNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(updatePermissionQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("updatePermission"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void deletePermissionNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(deletePermissionQuery, null);

        assertNull(data.get("deletePermission"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void deletePermissionTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(deletePermissionQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void deletePermissionNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(deletePermissionQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("deletePermission"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void permissionPageNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(permissionPageQuery, null);

        assertNull(data.get("permissionPage"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void permissionPageTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(permissionPageQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void permissionPageNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(permissionPageQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("permissionPage"));
    }
}