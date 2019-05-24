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
public class RoleAuthenticationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TokenService tokenService;

    private IntegrationTest integrationTest;

    private final String rolesQuery = "query {roles {id name people{id}}}";

    private final String roleQuery = "query {role(id: 5) {id name}}";

    private final String roleByNameQuery = "query {roleByName(name: \"aa\") {id name}}";

    private final String createRoleQuery = "mutation {createRole(role: {}) {id name}}";

    private final String updateRoleQuery = "mutation {updateRole(role: {}) {id name}}";

    private final String deleteRoleQuery = "mutation {deleteRole(id: 5) {id name}}";

    private final String rolePageQuery = "query {rolePage(pageDataRequest: {}) {totalElements}}";

    @Before
    public void setup() throws Exception {
        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void rolesNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(rolesQuery, null);

        assertNull(data.get("roles"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void rolesTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(rolesQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void rolesNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(rolesQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("roles"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void roleNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(roleQuery, null);

        assertNull(data.get("role"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void roleTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(roleQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void roleNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(roleQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("role"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void roleByNameNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(roleByNameQuery, null);

        assertNull(data.get("roleByName"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void roleByNameTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(roleByNameQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void roleByNameNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(roleByNameQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("roleByName"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void createRoleNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(createRoleQuery, null);

        assertNull(data.get("createRole"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void createRoleTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(createRoleQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void createRoleNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(createRoleQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("createRole"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void updateRoleNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(updateRoleQuery, null);

        assertNull(data.get("updateRole"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void updateRoleTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(updateRoleQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void updateRoleNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(updateRoleQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("updateRole"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void deleteRoleNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(deleteRoleQuery, null);

        assertNull(data.get("deleteRole"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void deleteRoleTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(deleteRoleQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void deleteRoleNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(deleteRoleQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("deleteRole"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void rolePageNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(rolePageQuery, null);

        assertNull(data.get("rolePage"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void rolePageTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(rolePageQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void rolePageNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(rolePageQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("rolePage"));
    }
}