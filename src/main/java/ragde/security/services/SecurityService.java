package ragde.security.services;

import ragde.security.pojos.AccountCredentials;
import ragde.security.pojos.LoggedUser;

import javax.validation.Valid;
import java.io.IOException;

/**
 * Deals with security process
 */
public interface SecurityService {

    /**
     * Try to authenticate an user
     *
     * @param credentials to authenticate.
     * @return LoggedUser instance associated with credentials
     * @throws IOException if error generating token
     */
    LoggedUser authenticate(@Valid AccountCredentials credentials) throws IOException;

    /**
     * Creates a new LoggedUser instance with the requested role
     *
     * @param roleId requested role id.
     * @return new LoggedUser instance
     * @throws IOException if error generating token
     */
    LoggedUser changeRole(String roleId) throws IOException;

    /**
     * Gets info from logged user (LoggedUser instance)
     *
     * @return LoggedUser instance
     */
    LoggedUser getLoggedUser();

    /**
     * Hashes a value
     *
     * @param value value to be hashed.
     * @return hashed value
     */
    String hashValue(String value);
}