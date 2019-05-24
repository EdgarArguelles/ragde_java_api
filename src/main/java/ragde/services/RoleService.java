package ragde.services;

import ragde.models.Person;
import ragde.models.Role;

import java.util.List;

public interface RoleService extends JpaService<Role> {

    /**
     * Retrieves an entity by its name (name is an unique value).
     *
     * @param name value to search.
     * @return the entity with the given name or null if none found
     */
    Role findByName(String name);

    /**
     * GraphQL function to load Role's People (only needed with mongo or jpa which doesn't implement bi-directional relationship)
     *
     * @param role role where related data is loaded
     * @return Role's People list
     */
    List<Person> getPeople(Role role);
}