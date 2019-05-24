package ragde.repositories;

import ragde.models.AuthProvider;
import ragde.repositories.mysql.MySQLAuthProviderRepository;

public interface AuthProviderRepository extends MySQLAuthProviderRepository {

    //generic query not depends of mongo or sql

    /**
     * Retrieves an entity by its name (name is an unique value).
     *
     * @param name value to search.
     * @return the entity with the given name or null if none found
     */
    AuthProvider findByName(String name);
}