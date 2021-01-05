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
public class AuthProviderAuthenticationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TokenService tokenService;

    private IntegrationTest integrationTest;

    private final String authProvidersQuery = "query {authProviders {id name authentications{id}}}";

    @BeforeEach
    public void setup() throws Exception {
        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void authProvidersNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(authProvidersQuery, null);

        assertNull(data.get("authProviders"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void authProvidersTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(authProvidersQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void authProvidersPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(authProvidersQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("authProviders"));
    }
}