package ragde.services;

import ragde.models.AuthProvider;
import ragde.models.Authentication;

import java.util.List;

public interface AuthProviderService {

    /**
     * Retrieves all entities.
     *
     * @return list of entities.
     */
    List<AuthProvider> findAll();

    /**
     * GraphQL function to load Provider's Authentications (only needed with mongo or jpa which doesn't implement bi-directional relationship)
     *
     * @param authProvider provider where related data is loaded
     * @return Provider's Authentications list
     */
    List<Authentication> getAuthentications(AuthProvider authProvider);
}