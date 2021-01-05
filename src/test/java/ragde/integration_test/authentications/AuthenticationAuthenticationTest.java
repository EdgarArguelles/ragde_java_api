package ragde.integration_test.authentications;

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
import ragde.integration_test.IntegrationTest;
import ragde.security.services.TokenService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class AuthenticationAuthenticationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TokenService tokenService;

    private IntegrationTest integrationTest;

    private final String authenticationsQuery = "query {authentications {id username}}";

    private final String authenticationQuery = "query {authentication(id: 5) {id username}}";

    private final String authenticationByUsernameQuery = "query {authenticationByUsername(username: \"aa\") {id username}}";

    private final String authenticationByAuthProviderAndPersonQuery = "query {authenticationByAuthProviderAndPerson(authProvider: {} person: {}) {id username}}";

    private final String createAuthenticationQuery = "mutation {createAuthentication(authentication: {}) {id username}}";

    private final String updateAuthenticationQuery = "mutation {updateAuthentication(authentication: {}) {id username}}";

    private final String deleteAuthenticationQuery = "mutation {deleteAuthentication(id: 5) {id username}}";

    private final String authenticationPageQuery = "query {authenticationPage(pageDataRequest: {}) {totalElements}}";

    @BeforeEach
    public void setup() throws Exception {
        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void authenticationsNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(authenticationsQuery, null);

        assertNull(data.get("authentications"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void authenticationsTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(authenticationsQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void authenticationsNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(authenticationsQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("authentications"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void authenticationNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(authenticationQuery, null);

        assertNull(data.get("authentication"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void authenticationTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(authenticationQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void authenticationNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(authenticationQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("authentication"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void authenticationByUsernameNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(authenticationByUsernameQuery, null);

        assertNull(data.get("authenticationByUsername"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void authenticationByUsernameTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(authenticationByUsernameQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void authenticationByUsernameNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(authenticationByUsernameQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("authenticationByUsername"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void authenticationByAuthProviderAndPersonNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(authenticationByAuthProviderAndPersonQuery, null);

        assertNull(data.get("authenticationByAuthProviderAndPerson"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void authenticationByAuthProviderAndPersonTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(authenticationByAuthProviderAndPersonQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void authenticationByAuthProviderAndPersonNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(authenticationByAuthProviderAndPersonQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("authenticationByAuthProviderAndPerson"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void createAuthenticationNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(createAuthenticationQuery, null);

        assertNull(data.get("createAuthentication"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void createAuthenticationTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(createAuthenticationQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void createAuthenticationNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(createAuthenticationQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("createAuthentication"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void updateAuthenticationNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(updateAuthenticationQuery, null);

        assertNull(data.get("updateAuthentication"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void updateAuthenticationTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(updateAuthenticationQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void updateAuthenticationNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(updateAuthenticationQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("updateAuthentication"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void deleteAuthenticationNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(deleteAuthenticationQuery, null);

        assertNull(data.get("deleteAuthentication"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void deleteAuthenticationTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(deleteAuthenticationQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void deleteAuthenticationNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(deleteAuthenticationQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("deleteAuthentication"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void authenticationPageNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(authenticationPageQuery, null);

        assertNull(data.get("authenticationPage"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void authenticationPageTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(authenticationPageQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void authenticationPageNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(authenticationPageQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("authenticationPage"));
    }
}