package ragde.security.oauth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.HttpClientErrorException;
import ragde.models.AuthProvider;
import ragde.repositories.AuthProviderRepository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class FacebookProviderTest {

    private final String authKeyExpected = "Auth Key";

    private final String authSecretExpected = "Auth Key";

    @Autowired
    @Qualifier("facebookProvider")
    private OAuthProvider facebookProvider;

    @MockBean
    private AuthProviderRepository authProviderRepository;

    @BeforeEach
    public void setup() throws Exception {
        final AuthProvider authProvider = new AuthProvider("A1");
        authProvider.setAuthKey(authKeyExpected);
        authProvider.setAuthSecret(authSecretExpected);
        given(authProviderRepository.findByName("FACEBOOK")).willReturn(authProvider);
    }

    /**
     * Should get authKey
     */
    @Test
    public void getApiKey() {
        final String authKeyResult = facebookProvider.getApiKey();
        final String authKeyResult2 = facebookProvider.getApiKey();

        assertSame(authKeyExpected, authKeyResult);
        assertSame(authKeyExpected, authKeyResult2);
    }

    /**
     * Should get authSecret
     */
    @Test
    public void getApiSecret() {
        final String authSecretResult = facebookProvider.getApiSecret();
        final String authSecretResult2 = facebookProvider.getApiSecret();

        assertSame(authSecretExpected, authSecretResult);
        assertSame(authSecretExpected, authSecretResult2);
    }

    /**
     * Should get authorizeUrl
     */
    @Test
    public void getAuthorizeUrl() {
        final String callback = "callback";
        final String state = "state";

        final OAuth2Parameters oAuth2Parameters = new OAuth2Parameters();
        oAuth2Parameters.setRedirectUri(callback);
        oAuth2Parameters.setScope("email");
        oAuth2Parameters.setState(state);
        final FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(authKeyExpected, authSecretExpected);
        final String authorizeUrlExpected = connectionFactory.getOAuthOperations().buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, oAuth2Parameters);

        final String authorizeUrlResult = facebookProvider.getAuthorizeUrl(callback, state);

        assertNotSame(authorizeUrlExpected, authorizeUrlResult);
        assertEquals(authorizeUrlExpected, authorizeUrlResult);
    }

    /**
     * Should throw HttpClientErrorException
     */
    @Test
    public void getAccessGrant() {
        final String callback = "callback";
        final String authorizationCode = "code";

        assertThrows(HttpClientErrorException.class, () -> facebookProvider.getAccessGrant(callback, authorizationCode));
    }
}