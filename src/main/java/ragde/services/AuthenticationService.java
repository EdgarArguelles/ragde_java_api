package ragde.services;

import ragde.models.AuthProvider;
import ragde.models.Authentication;
import ragde.models.Person;

public interface AuthenticationService extends JpaService<Authentication> {

    /**
     * Retrieves an entity by its username (username is an unique value).
     *
     * @param username value to search.
     * @return the entity with the given username or null if none found
     */
    Authentication findByUsername(String username);

    /**
     * Retrieves an entity by its authProvider and person (authProvider/person relations is unique).
     *
     * @param authProvider value to search.
     * @param person       value to search.
     * @return the entity with the given authProvider and person or null if none found
     */
    Authentication findByAuthProviderAndPerson(AuthProvider authProvider, Person person);
}