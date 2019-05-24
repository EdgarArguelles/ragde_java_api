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
import ragde.models.Permission;
import ragde.models.Role;
import ragde.pojos.pages.PageDataRequest;
import ragde.repositories.PermissionRepository;
import ragde.repositories.RoleRepository;
import ragde.services.PermissionService;

import java.util.List;

@GraphQLApi
@Service
public class PermissionServiceImpl implements PermissionService {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Override
    @PreAuthorize("hasRole('VIEW_ROLES')")
    @GraphQLQuery(name = "permissions", description = "Find all permissions")
    public List<Permission> findAll() {
        return permissionRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('VIEW_ROLES')")
    @GraphQLQuery(name = "permission", description = "Find a permission by ID")
    public Permission findById(@GraphQLId @GraphQLNonNull @GraphQLArgument(name = "id", description = "Permission's ID") String id) {
        return permissionRepository.findById(id).orElseThrow(() -> new RagdeDontFoundException("Data don't found."));
    }

    @Override
    @PreAuthorize("hasRole('VIEW_ROLES')")
    @GraphQLQuery(name = "permissionByName", description = "Find a permission by Name")
    public Permission findByName(@GraphQLNonNull @GraphQLArgument(name = "name", description = "Permission's Name") String name) {
        return permissionRepository.findByName(name);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('CREATE_ROLES')")
    @GraphQLMutation(name = "createPermission", description = "Create a new permission")
    public Permission save(@GraphQLNonNull @GraphQLArgument(name = "permission", description = "New permission") Permission permission) {
        if (findByName(permission.getName()) != null) {
            throw new RagdeValidationException("Permission name '" + permission.getName() + "' is already used.");
        }

        return permissionRepository.save(permission);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('CREATE_ROLES')")
    @GraphQLMutation(name = "updatePermission", description = "Update a permission")
    public Permission update(@GraphQLNonNull @GraphQLArgument(name = "permission", description = "Permission's new values") Permission permission) {
        Permission original = findById(permission.getId());
        original.setDescription(permission.getDescription());

        return permissionRepository.save(original);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('REMOVE_ROLES')")
    @GraphQLMutation(name = "deletePermission", description = "Delete a permission")
    public Permission delete(@GraphQLId @GraphQLNonNull @GraphQLArgument(name = "id", description = "Permission's ID") String id) {
        Permission permission = findById(id);
        if (permission.getRoles() != null && !permission.getRoles().isEmpty()) {
            throw new RagdeValidationException("There are some roles using the Permission '" + permission.getName() + "'.");
        }

        permissionRepository.delete(permission);
        return permission;
    }

    @Override
    @PreAuthorize("hasRole('VIEW_ROLES')")
    @GraphQLQuery(name = "permissionPage", description = "Page all permissions")
    public Page<Permission> page(@GraphQLNonNull @GraphQLArgument(name = "pageDataRequest", description = "Filter, limit and sort data") PageDataRequest pageDataRequest) {
        return permissionRepository.page(pageDataRequest);
    }

    @Override
    @PreAuthorize("hasRole('VIEW_ROLES')")
    @GraphQLQuery(name = "roles", description = "Roles where this Permission is present")
    public List<Role> getRoles(@GraphQLContext Permission permission) {
        if (permission.getRoles() == null) {
            permission.setRoles(roleRepository.findByPermissions(permission));
        }

        return permission.getRoles();
    }
}