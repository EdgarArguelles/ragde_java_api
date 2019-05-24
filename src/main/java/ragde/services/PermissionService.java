package ragde.services;

import ragde.models.Permission;
import ragde.models.Role;

import java.util.List;

public interface PermissionService extends JpaService<Permission> {

    /**
     * Retrieves an entity by its name (name is an unique value).
     *
     * @param name value to search.
     * @return the entity with the given name or null if none found
     */
    Permission findByName(String name);

    /**
     * GraphQL function to load Permission's Roles (only needed with mongo or jpa which doesn't implement bi-directional relationship)
     *
     * @param permission permission where related data is loaded
     * @return Permission's Roles list
     */
    List<Role> getRoles(Permission permission);
}