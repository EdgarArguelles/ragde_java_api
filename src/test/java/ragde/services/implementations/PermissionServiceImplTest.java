package ragde.services.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ragde.exceptions.RagdeDontFoundException;
import ragde.exceptions.RagdeValidationException;
import ragde.models.Permission;
import ragde.models.Role;
import ragde.pojos.pages.PageDataRequest;
import ragde.repositories.PermissionRepository;
import ragde.repositories.RoleRepository;
import ragde.security.pojos.LoggedUser;
import ragde.services.PermissionService;

import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PermissionServiceImplTest {

    @Autowired
    private PermissionService permissionService;

    @MockBean
    private PermissionRepository permissionRepository;

    @MockBean
    private RoleRepository roleRepository;

    @BeforeEach
    public void setup() {
        final LoggedUser user = new LoggedUser();
        user.setPermissions(Set.of("VIEW_ROLES", "CREATE_ROLES", "REMOVE_ROLES"));
        final List<GrantedAuthority> authorities = user.getPermissions().stream().map(p -> (GrantedAuthority) () -> "ROLE_" + p).collect(Collectors.toList());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, authorities));
    }

    /**
     * Should call findAll function
     */
    @Test
    public void findAll() {
        final List<Permission> permissionsMocked = Arrays.asList(new Permission("ID1"), new Permission("ID2"), null, new Permission("ID4"));
        permissionsMocked.get(0).setRoles(List.of(new Role("ROLE1")));
        permissionsMocked.get(1).setRoles(Collections.emptyList());
        given(permissionRepository.findAll()).willReturn(permissionsMocked);

        final List<Permission> permissionsExpected = Arrays.asList(new Permission("ID1"), new Permission("ID2"), null, new Permission("ID4"));
        permissionsExpected.get(0).setRoles(List.of(new Role("ROLE1")));
        permissionsExpected.get(1).setRoles(Collections.emptyList());

        final List<Permission> permissionsResult = permissionService.findAll();

        assertSame(permissionsMocked, permissionsResult);
        assertNotSame(permissionsExpected, permissionsResult);
        assertEquals(permissionsExpected, permissionsResult);
        verify(permissionRepository, times(1)).findAll();
    }

    /**
     * Should throw RagdeDontFoundException
     */
    @Test
    public void findByIdWhenDontFound() {
        final String ID = "ID";
        given(permissionRepository.findById(ID)).willReturn(Optional.empty());

        assertThrows(RagdeDontFoundException.class, () -> permissionService.findById(ID));
    }

    /**
     * Should call findById function
     */
    @Test
    public void findById() {
        final String ID = "ID";
        final Permission permissionMocked = new Permission(ID);
        given(permissionRepository.findById(ID)).willReturn(Optional.of(permissionMocked));

        final Permission permissionExpected = new Permission(ID);

        final Permission permissionResult = permissionService.findById(ID);

        assertSame(permissionMocked, permissionResult);
        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        verify(permissionRepository, times(1)).findById(ID);
    }

    /**
     * Should call findByName function
     */
    @Test
    public void findByName() {
        final String NAME = "test";
        final Permission permissionMocked = new Permission(NAME, null);
        given(permissionRepository.findByName(NAME)).willReturn(permissionMocked);

        final Permission permissionExpected = new Permission(NAME, null);

        final Permission permissionResult = permissionService.findByName(NAME);

        assertSame(permissionMocked, permissionResult);
        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        verify(permissionRepository, times(1)).findByName(NAME);
    }

    /**
     * Should throw ConstraintViolationException when invalid
     */
    @Test
    public void saveInvalid() {
        assertThrows(ConstraintViolationException.class, () -> permissionService.save(new Permission()));
    }

    /**
     * Should throw RagdeValidationException when name duplicated
     */
    @Test
    public void saveDuplicate() {
        final String NAME = "test";
        final Permission permission = new Permission(NAME, "123");
        given(permissionRepository.findByName(NAME)).willReturn(permission);

        assertThrows(RagdeValidationException.class, () -> permissionService.save(permission));
    }

    /**
     * Should return a permission when save successfully
     */
    @Test
    public void saveSuccessfully() {
        final String NAME = "test";
        final String DESC = "desc";
        final Permission permission = new Permission(NAME, DESC);
        given(permissionRepository.findByName(NAME)).willReturn(null);
        given(permissionRepository.save(permission)).willReturn(permission);

        final Permission permissionExpected = new Permission(NAME, DESC);

        final Permission permissionResult = permissionService.save(permission);

        assertSame(permission, permissionResult);
        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        verify(permissionRepository, times(1)).findByName(NAME);
        verify(permissionRepository, times(1)).save(permission);
    }

    /**
     * Should throw ConstraintViolationException when invalid
     */
    @Test
    public void updateInvalid() {
        assertThrows(ConstraintViolationException.class, () -> permissionService.update(new Permission()));
    }

    /**
     * Should throw RagdeDontFoundException when permission doesn't exist
     */
    @Test
    public void updateDontFound() {
        final String ID = "ID";
        final Permission permission = new Permission("ABC", "123");
        permission.setId(ID);
        given(permissionRepository.findById(ID)).willReturn(Optional.empty());

        assertThrows(RagdeDontFoundException.class, () -> permissionService.update(permission));
    }

    /**
     * Should return a permission when update successfully
     */
    @Test
    public void updateSuccessfully() {
        final String ID = "ID";
        final String NAME_PERMISSION = "name after";
        final String NAME_ORIGINAL = "name before";
        final String DESC_PERMISSION = "desc after";
        final String DESC_ORIGINAL = "desc before";
        final List<Role> ROLES_PERMISSION = List.of(new Role("ID1"));
        final List<Role> ROLES_ORIGINAL = List.of(new Role("ID2"), new Role("ID3"));
        final Permission permission = new Permission(NAME_PERMISSION, DESC_PERMISSION);
        permission.setId(ID);
        permission.setRoles(ROLES_PERMISSION);
        final Permission permissionOriginal = new Permission(NAME_ORIGINAL, DESC_ORIGINAL);
        permissionOriginal.setId(ID);
        permissionOriginal.setRoles(ROLES_ORIGINAL);
        //only change desc
        final Permission permissionMocked = new Permission(NAME_ORIGINAL, DESC_PERMISSION);
        permissionMocked.setId(ID);
        permissionMocked.setRoles(ROLES_ORIGINAL);
        given(permissionRepository.findById(ID)).willReturn(Optional.of(permissionOriginal));
        given(permissionRepository.save(permissionOriginal)).willReturn(permissionMocked);

        final Permission permissionExpected = new Permission(NAME_ORIGINAL, DESC_PERMISSION);
        permissionExpected.setId(ID);
        permissionExpected.setRoles(ROLES_ORIGINAL);

        final Permission permissionResult = permissionService.update(permission);

        assertSame(permissionMocked, permissionResult);
        assertNotSame(permission, permissionResult);
        assertNotSame(permissionOriginal, permissionResult);
        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        verify(permissionRepository, times(1)).findById(ID);
        verify(permissionRepository, times(1)).save(permissionOriginal);
    }

    /**
     * Should throw RagdeDontFoundException when permission doesn't exist
     */
    @Test
    public void deleteDontFound() {
        final String ID = "ID";
        given(permissionRepository.findById(ID)).willReturn(Optional.empty());

        assertThrows(RagdeDontFoundException.class, () -> permissionService.delete(ID));
    }

    /**
     * Should throw RagdeValidationException when permission is being used
     */
    @Test
    public void deleteUsed() {
        final String ID = "ID";
        final String NAME = "test";
        final String DESC = "desc";
        final Permission permission = new Permission(NAME, DESC);
        permission.setId(ID);
        permission.setRoles(List.of(new Role("ID1")));
        given(permissionRepository.findById(ID)).willReturn(Optional.of(permission));

        assertThrows(RagdeValidationException.class, () -> permissionService.delete(ID));
    }

    /**
     * Should return a permission when delete successfully
     */
    @Test
    public void deleteSuccessfully() {
        final String ID = "ID";
        final String NAME = "test";
        final String DESC = "desc";
        final Permission permission = new Permission(NAME, DESC);
        permission.setId(ID);
        given(permissionRepository.findById(ID)).willReturn(Optional.of(permission));
        doNothing().when(permissionRepository).delete(permission);

        final Permission permissionExpected = new Permission(NAME, DESC);
        permissionExpected.setId(ID);

        final Permission permissionResult = permissionService.delete(ID);

        assertSame(permission, permissionResult);
        assertNotSame(permissionExpected, permissionResult);
        assertEquals(permissionExpected, permissionResult);
        verify(permissionRepository, times(1)).findById(ID);
        verify(permissionRepository, times(1)).delete(permission);
    }

    /**
     * Should throw ConstraintViolationException when invalid
     */
    @Test
    public void pageInvalid() {
        assertThrows(ConstraintViolationException.class, () -> permissionService.page(new PageDataRequest()));
    }

    /**
     * Should call page function
     */
    @Test
    public void page() {
        final PageDataRequest pageDataRequest = new PageDataRequest(0, 1, null, null, null);
        final Page<Permission> permissionsMocked = new PageImpl<>(List.of(new Permission("ID1"), new Permission("ID2")));
        given(permissionRepository.page(pageDataRequest)).willReturn(permissionsMocked);

        final Page<Permission> permissionsExpected = new PageImpl<>(List.of(new Permission("ID1"), new Permission("ID2")));

        final Page<Permission> permissionsResult = permissionService.page(pageDataRequest);

        assertSame(permissionsMocked, permissionsResult);
        assertNotSame(permissionsExpected, permissionsResult);
        assertEquals(permissionsExpected, permissionsResult);
        verify(permissionRepository, times(1)).page(pageDataRequest);
    }

    /**
     * Should call findByPermissions function
     */
    @Test
    public void getRolesWhenNull() {
        final Permission permission = new Permission();
        final List<Role> rolesMocked = List.of(new Role("R1"), new Role("R2"));
        given(roleRepository.findByPermissions(permission)).willReturn(rolesMocked);

        final List<Role> rolesExpected = List.of(new Role("R1"), new Role("R2"));

        final List<Role> rolesResult = permissionService.getRoles(permission);

        assertSame(rolesMocked, rolesResult);
        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        verify(roleRepository, times(1)).findByPermissions(permission);
    }

    /**
     * Should not call findByPermissions function
     */
    @Test
    public void getRoles() {
        final Permission permission = new Permission();
        permission.setRoles(List.of(new Role("R1"), new Role("R2")));

        final List<Role> rolesExpected = List.of(new Role("R1"), new Role("R2"));

        final List<Role> rolesResult = permissionService.getRoles(permission);

        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        verify(roleRepository, never()).findByPermissions(permission);
    }
}