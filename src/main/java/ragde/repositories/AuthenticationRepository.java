package ragde.repositories;

import ragde.models.AuthProvider;
import ragde.models.Authentication;
import ragde.models.Person;
import ragde.repositories.executor.QueryExecutor;
import ragde.repositories.mysql.MySQLAuthenticationRepository;

import java.util.List;

public interface AuthenticationRepository extends MySQLAuthenticationRepository, QueryExecutor<Authentication> {

    //generic query not depends of mongo or sql

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

    /**
     * Find all Authentications associated with the Person (not needed with JPA because SQL allows bi-directional relationship).
     *
     * @param person value to search.
     * @return associated authentications list
     */
    List<Authentication> findByPerson(Person person);

    /**
     * Find all Authentications associated with the AuthProvider (not needed with JPA because SQL allows bi-directional relationship).
     *
     * @param authProvider value to search.
     * @return associated authentications list
     */
    List<Authentication> findByAuthProvider(AuthProvider authProvider);
}