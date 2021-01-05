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
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class SecurityServiceAuthenticationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TokenService tokenService;

    private IntegrationTest integrationTest;

    private final String loginQuery = "query {login(credentials: {}) {id fullName}}";

    private final String changeRoleQuery = "query {changeRole(roleId: \"55\") {id fullName}}";

    private final String pingQuery = "query {ping {id fullName}}";

    @BeforeEach
    public void setup() throws Exception {
        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
    }

    /**
     * Should return a ValidationException error response when not token
     */
    @Test
    public void loginNotToken() throws Exception {
        final Map data = integrationTest.failGraphQL(loginQuery, "Credentials incorrect.", null);

        assertNull(data.get("login"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void loginTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(loginQuery);
    }

    /**
     * Should return a ValidationException error response when not permissions
     */
    @Test
    public void loginNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQL(loginQuery, "Credentials incorrect.", IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("login"));
    }

    /**
     * Should return a ValidationException error response when not token
     */
    @Test
    public void changeRoleNotToken() throws Exception {
        final Map data = integrationTest.failGraphQL(changeRoleQuery, "There isn't any logged user.", null);

        assertNull(data.get("changeRole"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void changeRoleTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(changeRoleQuery);
    }

    /**
     * Should return a ValidationException error response when not permissions
     */
    @Test
    public void changeRoleNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQL(changeRoleQuery, "The given id must not be null", IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("changeRole"));
    }

    /**
     * Should throw AssertionError when not token
     */
    @Test
    public void pingNotToken() {
        assertThrows(AssertionError.class, () -> integrationTest.performGraphQL(pingQuery, null));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void pingTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(pingQuery);
    }

    /**
     * Should return ping null when not permissions
     */
    @Test
    public void pingNotPermission() throws Exception {
        final Map data = integrationTest.performGraphQL(pingQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("ping"));
    }
}