package ragde.repositories;

import ragde.models.Permission;
import ragde.repositories.executor.QueryExecutor;
import ragde.repositories.mysql.MySQLPermissionRepository;

public interface PermissionRepository extends MySQLPermissionRepository, QueryExecutor<Permission> {

    //generic query not depends of mongo or sql

    /**
     * Retrieves an entity by its name (name is an unique value).
     *
     * @param name value to search.
     * @return the entity with the given name or null if none found
     */
    Permission findByName(String name);
}