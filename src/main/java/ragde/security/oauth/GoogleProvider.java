package ragde.security.oauth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.google.connect.GoogleConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Component;
import ragde.models.AuthProvider;
import ragde.repositories.AuthProviderRepository;

@Component
public class GoogleProvider implements OAuthProvider {

    private static OAuth2Operations oAuthOperations;

    private static AuthProvider authProvider;

    @Autowired
    private AuthProviderRepository authProviderRepository;

    @Override
    public String getApiKey() {
        return getAuthProvider().getAuthKey();
    }

    @Override
    public String getApiSecret() {
        return getAuthProvider().getAuthSecret();
    }

    @Override
    public String getAuthorizeUrl(String callback, String state) {
        OAuth2Parameters oAuth2Parameters = new OAuth2Parameters();
        oAuth2Parameters.setRedirectUri(callback);
        oAuth2Parameters.setScope("https://www.googleapis.com/auth/userinfo.profile https://www.googleapis.com/auth/userinfo.email");
        oAuth2Parameters.setState(state);
        return getOAuthOperations().buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, oAuth2Parameters);
    }

    @Override
    public AccessGrant getAccessGrant(String callback, String authorizationCode) {
        return getOAuthOperations().exchangeForAccess(authorizationCode, callback, null);
    }

    private OAuth2Operations getOAuthOperations() {
        if (oAuthOperations == null) {
            GoogleConnectionFactory connectionFactory = new GoogleConnectionFactory(getApiKey(), getApiSecret());
            oAuthOperations = connectionFactory.getOAuthOperations();
        }

        return oAuthOperations;
    }

    private AuthProvider getAuthProvider() {
        if (authProvider == null) {
            authProvider = authProviderRepository.findByName("GOOGLE");
        }

        return authProvider;
    }
}