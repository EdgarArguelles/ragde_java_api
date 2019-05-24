package ragde.services.implementations;

import io.leangen.graphql.annotations.*;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ragde.exceptions.RagdeDontFoundException;
import ragde.exceptions.RagdeValidationException;
import ragde.models.Person;
import ragde.models.Role;
import ragde.pojos.pages.PageDataRequest;
import ragde.repositories.PersonRepository;
import ragde.repositories.RoleRepository;
import ragde.services.RoleService;

import java.util.List;

@GraphQLApi
@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PersonRepository personRepository;

    @Override
    @PreAuthorize("hasRole('VIEW_ROLES')")
    @GraphQLQuery(name = "roles", description = "Find all roles")
    public List<Role> findAll() {
        return roleRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('VIEW_ROLES')")
    @GraphQLQuery(name = "role", description = "Find a role by ID")
    public Role findById(@GraphQLId @GraphQLNonNull @GraphQLArgument(name = "id", description = "Role's ID") String id) {
        return roleRepository.findById(id).orElseThrow(() -> new RagdeDontFoundException("Data don't found."));
    }

    @Override
    @PreAuthorize("hasRole('VIEW_ROLES')")
    @GraphQLQuery(name = "roleByName", description = "Find a role by Name")
    public Role findByName(@GraphQLNonNull @GraphQLArgument(name = "name", description = "Role's Name") String name) {
        return roleRepository.findByName(name);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('CREATE_ROLES')")
    @GraphQLMutation(name = "createRole", description = "Create a new role")
    public Role save(@GraphQLNonNull @GraphQLArgument(name = "role", description = "New role") Role role) {
        if (findByName(role.getName()) != null) {
            throw new RagdeValidationException("Role name '" + role.getName() + "' is already used.");
        }

        return roleRepository.save(role);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('CREATE_ROLES')")
    @GraphQLMutation(name = "updateRole", description = "Update a role")
    public Role update(@GraphQLNonNull @GraphQLArgument(name = "role", description = "Role's new values") Role role) {
        Role original = findById(role.getId());
        original.setDescription(role.getDescription());
        original.setPermissions(role.getPermissions());

        return roleRepository.save(original);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('REMOVE_ROLES')")
    @GraphQLMutation(name = "deleteRole", description = "Delete a role")
    public Role delete(@GraphQLId @GraphQLNonNull @GraphQLArgument(name = "id", description = "Role's ID") String id) {
        Role role = findById(id);
        if (role.getPeople() != null && !role.getPeople().isEmpty()) {
            throw new RagdeValidationException("There are some people using the Role '" + role.getName() + "'.");
        }

        roleRepository.delete(role);
        role.setPermissions(null);
        return role;
    }

    @Override
    @PreAuthorize("hasRole('VIEW_ROLES')")
    @GraphQLQuery(name = "rolePage", description = "Page all roles")
    public Page<Role> page(@GraphQLNonNull @GraphQLArgument(name = "pageDataRequest", description = "Filter, limit and sort data") PageDataRequest pageDataRequest) {
        return roleRepository.page(pageDataRequest);
    }

    @Override
    @PreAuthorize("hasRole('VIEW_ROLES') and hasRole('VIEW_USERS')")
    @GraphQLQuery(name = "people", description = "People where this Role is present")
    public List<Person> getPeople(@GraphQLContext Role role) {
        if (role.getPeople() == null) {
            role.setPeople(personRepository.findByRoles(role));
        }

        return role.getPeople();
    }
}