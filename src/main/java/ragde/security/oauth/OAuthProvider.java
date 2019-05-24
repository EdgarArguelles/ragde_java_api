package ragde.security.oauth;

import org.springframework.social.oauth2.AccessGrant;

/**
 * Provide functionality for working with several OAuth providers
 */
public interface OAuthProvider {

    /**
     * Get Api Key of OAuth provider
     *
     * @return Api Key
     */
    String getApiKey();

    /**
     * Get Api Secret of OAuth provider
     *
     * @return Api Secret
     */
    String getApiSecret();

    /**
     * Construct the URL to redirect the user to for authorization. Use of implicit grant
     * is discouraged unless there is no other option available. Use
     *
     * @param callback authorization callback url
     * @param state    an opaque key that must be included in the provider's authorization callback
     * @return the absolute authorize URL to redirect the user to for authorization
     */
    String getAuthorizeUrl(String callback, String state);

    /**
     * Exchange the authorization code for an access grant.
     *
     * @param callback          authorization callback url
     * @param authorizationCode the authorization code returned by the provider upon user authorization
     * @return the access grant.
     */
    AccessGrant getAccessGrant(String callback, String authorizationCode);
}