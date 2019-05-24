package ragde.repositories;

import ragde.models.Permission;
import ragde.models.Role;
import ragde.repositories.executor.QueryExecutor;
import ragde.repositories.mysql.MySQLRoleRepository;

import java.util.List;

public interface RoleRepository extends MySQLRoleRepository, QueryExecutor<Role> {

    //generic query not depends of mongo or sql

    /**
     * Retrieves an entity by its name (name is an unique value).
     *
     * @param name value to search.
     * @return the entity with the given name or null if none found
     */
    Role findByName(String name);

    /**
     * Find all Roles associated with the Permission (not needed with JPA because SQL allows bi-directional relationship).
     *
     * @param permission value to search.
     * @return associated roles list
     */
    List<Role> findByPermissions(Permission permission);
}