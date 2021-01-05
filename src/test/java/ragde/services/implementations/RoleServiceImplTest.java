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
import ragde.models.Person;
import ragde.models.Role;
import ragde.pojos.pages.PageDataRequest;
import ragde.repositories.PersonRepository;
import ragde.repositories.RoleRepository;
import ragde.security.pojos.LoggedUser;
import ragde.services.RoleService;

import javax.validation.ConstraintViolationException;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class RoleServiceImplTest {

    @Autowired
    private RoleService roleService;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private PersonRepository personRepository;

    @BeforeEach
    public void setup() {
        final LoggedUser user = new LoggedUser();
        user.setPermissions(Set.of("VIEW_ROLES", "CREATE_ROLES", "REMOVE_ROLES", "VIEW_USERS"));
        final List<GrantedAuthority> authorities = user.getPermissions().stream().map(p -> (GrantedAuthority) () -> "ROLE_" + p).collect(Collectors.toList());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, authorities));
    }

    /**
     * Should call findAll function
     */
    @Test
    public void findAll() {
        final List<Role> rolesMocked = Arrays.asList(new Role("ID1"), new Role("ID2"), null, new Role("ID4"));
        rolesMocked.get(0).setPeople(List.of(new Person("Per1")));
        rolesMocked.get(0).setPermissions(Set.of(new Permission("P1"), new Permission("P2")));
        rolesMocked.get(1).setPeople(Collections.emptyList());
        rolesMocked.get(1).setPermissions(Collections.emptySet());
        given(roleRepository.findAll()).willReturn(rolesMocked);

        final List<Role> rolesExpected = Arrays.asList(new Role("ID1"), new Role("ID2"), null, new Role("ID4"));
        rolesExpected.get(0).setPeople(List.of(new Person("Per1")));
        rolesExpected.get(0).setPermissions(Set.of(new Permission("P1"), new Permission("P2")));
        rolesExpected.get(1).setPeople(Collections.emptyList());
        rolesExpected.get(1).setPermissions(Collections.emptySet());

        final List<Role> rolesResult = roleService.findAll();

        assertSame(rolesMocked, rolesResult);
        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        verify(roleRepository, times(1)).findAll();
    }

    /**
     * Should throw RagdeDontFoundException
     */
    @Test
    public void findByIdWhenDontFound() {
        final String ID = "ID";
        given(roleRepository.findById(ID)).willReturn(Optional.empty());

        assertThrows(RagdeDontFoundException.class, () -> roleService.findById(ID));
    }

    /**
     * Should call findById function
     */
    @Test
    public void findById() {
        final String ID = "ID";
        final Role roleMocked = new Role(ID);
        roleMocked.setPermissions(Set.of(new Permission("P1"), new Permission("P2")));
        given(roleRepository.findById(ID)).willReturn(Optional.of(roleMocked));

        final Role roleExpected = new Role(ID);
        roleExpected.setPermissions(Set.of(new Permission("P1"), new Permission("P2")));

        final Role roleResult = roleService.findById(ID);

        assertSame(roleMocked, roleResult);
        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        verify(roleRepository, times(1)).findById(ID);
    }

    /**
     * Should call findByName function
     */
    @Test
    public void findByName() {
        final String NAME = "test";
        final Role roleMocked = new Role(NAME, null, null);
        given(roleRepository.findByName(NAME)).willReturn(roleMocked);

        final Role roleExpected = new Role(NAME, null, null);

        final Role roleResult = roleService.findByName(NAME);

        assertSame(roleMocked, roleResult);
        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        verify(roleRepository, times(1)).findByName(NAME);
    }

    /**
     * Should throw ConstraintViolationException when invalid
     */
    @Test
    public void saveInvalid() {
        assertThrows(ConstraintViolationException.class, () -> roleService.save(new Role()));
    }

    /**
     * Should throw RagdeValidationException when name duplicated
     */
    @Test
    public void saveDuplicate() {
        final String NAME = "test";
        final Role role = new Role(NAME, "123", null);
        given(roleRepository.findByName(NAME)).willReturn(role);

        assertThrows(RagdeValidationException.class, () -> roleService.save(role));
    }

    /**
     * Should return a role when save successfully
     */
    @Test
    public void saveSuccessfully() {
        final String NAME = "test";
        final String DESC = "desc";
        final Set<Permission> PERMISSIONS = Set.of(new Permission("P1"), new Permission("P2"));
        final Role role = new Role(NAME, DESC, PERMISSIONS);
        given(roleRepository.findByName(NAME)).willReturn(null);
        given(roleRepository.save(role)).willReturn(role);

        final Role roleExpected = new Role(NAME, DESC, PERMISSIONS);

        final Role roleResult = roleService.save(role);

        assertSame(role, roleResult);
        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        verify(roleRepository, times(1)).findByName(NAME);
        verify(roleRepository, times(1)).save(role);
    }

    /**
     * Should throw ConstraintViolationException when invalid
     */
    @Test
    public void updateInvalid() {
        assertThrows(ConstraintViolationException.class, () -> roleService.update(new Role()));
    }

    /**
     * Should throw RagdeDontFoundException when role doesn't exist
     */
    @Test
    public void updateDontFound() {
        final String ID = "ID";
        final Role role = new Role("abc", "123", null);
        role.setId(ID);
        given(roleRepository.findById(ID)).willReturn(Optional.empty());

        assertThrows(RagdeDontFoundException.class, () -> roleService.update(role));
    }

    /**
     * Should return a role when update successfully
     */
    @Test
    public void updateSuccessfully() {
        final String ID = "ID";
        final String NAME_ROLE = "name after";
        final String NAME_ORIGINAL = "name before";
        final String DESC_ROLE = "desc after";
        final String DESC_ORIGINAL = "desc before";
        final Set<Permission> PERMISSIONS_ROLE = Set.of(new Permission("P1"));
        final Set<Permission> PERMISSIONS_ORIGINAL = Set.of(new Permission("P2"), new Permission("P3"));
        final List<Person> PEOPLE_ROLE = List.of(new Person("Per1"));
        final List<Person> PEOPLE_ORIGINAL = List.of(new Person("Per2"), new Person("Per3"));
        final Role role = new Role(NAME_ROLE, DESC_ROLE, PERMISSIONS_ROLE);
        role.setId(ID);
        role.setPeople(PEOPLE_ROLE);
        final Role roleOriginal = new Role(NAME_ORIGINAL, DESC_ORIGINAL, PERMISSIONS_ORIGINAL);
        roleOriginal.setId(ID);
        roleOriginal.setPeople(PEOPLE_ORIGINAL);
        //only change desc
        final Role roleMocked = new Role(NAME_ORIGINAL, DESC_ROLE, PERMISSIONS_ROLE);
        roleMocked.setId(ID);
        roleMocked.setPeople(PEOPLE_ORIGINAL);
        given(roleRepository.findById(ID)).willReturn(Optional.of(roleOriginal));
        given(roleRepository.save(roleOriginal)).willReturn(roleMocked);

        final Role roleExpected = new Role(NAME_ORIGINAL, DESC_ROLE, PERMISSIONS_ROLE);
        roleExpected.setId(ID);
        roleExpected.setPeople(PEOPLE_ORIGINAL);

        final Role roleResult = roleService.update(role);

        assertSame(roleMocked, roleResult);
        assertNotSame(role, roleResult);
        assertNotSame(roleOriginal, roleResult);
        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        verify(roleRepository, times(1)).findById(ID);
        verify(roleRepository, times(1)).save(roleOriginal);
    }

    /**
     * Should throw RagdeDontFoundException when role doesn't exist
     */
    @Test
    public void deleteDontFound() {
        final String ID = "ID";
        given(roleRepository.findById(ID)).willReturn(Optional.empty());

        assertThrows(RagdeDontFoundException.class, () -> roleService.delete(ID));
    }

    /**
     * Should throw RagdeValidationException when role is being used
     */
    @Test
    public void deleteUsed() {
        final String ID = "ID";
        final Role role = new Role(ID);
        role.setPeople(List.of(new Person("Per1")));
        given(roleRepository.findById(ID)).willReturn(Optional.of(role));

        assertThrows(RagdeValidationException.class, () -> roleService.delete(ID));
    }

    /**
     * Should return a role when delete successfully
     */
    @Test
    public void deleteSuccessfully() {
        final String ID = "ID";
        final String NAME = "test";
        final String DESC = "desc";
        final Set<Permission> PERMISSIONS = Set.of(new Permission("P1"), new Permission("P2"));
        final Role role = new Role(NAME, DESC, PERMISSIONS);
        role.setId(ID);
        given(roleRepository.findById(ID)).willReturn(Optional.of(role));
        doNothing().when(roleRepository).delete(role);

        //clean permissions
        final Role roleExpected = new Role(NAME, DESC, null);
        roleExpected.setId(ID);

        final Role roleResult = roleService.delete(ID);

        assertSame(role, roleResult);
        assertNotSame(roleExpected, roleResult);
        assertEquals(roleExpected, roleResult);
        verify(roleRepository, times(1)).findById(ID);
        verify(roleRepository, times(1)).delete(role);
    }

    /**
     * Should throw ConstraintViolationException when invalid
     */
    @Test
    public void pageInvalid() {
        assertThrows(ConstraintViolationException.class, () -> roleService.page(new PageDataRequest()));
    }

    /**
     * Should call page function
     */
    @Test
    public void page() {
        final PageDataRequest pageDataRequest = new PageDataRequest(0, 1, null, null, null);
        final Page<Role> rolesMocked = new PageImpl<>(List.of(new Role("ID1"), new Role("ID2")));
        given(roleRepository.page(pageDataRequest)).willReturn(rolesMocked);

        final Page<Role> rolesExpected = new PageImpl<>(List.of(new Role("ID1"), new Role("ID2")));

        final Page<Role> rolesResult = roleService.page(pageDataRequest);

        assertSame(rolesMocked, rolesResult);
        assertNotSame(rolesExpected, rolesResult);
        assertEquals(rolesExpected, rolesResult);
        verify(roleRepository, times(1)).page(pageDataRequest);
    }

    /**
     * Should call findByRoles function
     */
    @Test
    public void getPeopleWhenNull() {
        final Role role = new Role();
        final List<Person> peopleMocked = List.of(new Person("P1"), new Person("P2"));
        given(personRepository.findByRoles(role)).willReturn(peopleMocked);

        final List<Person> peopleExpected = List.of(new Person("P1"), new Person("P2"));

        final List<Person> peopleResult = roleService.getPeople(role);

        assertSame(peopleMocked, peopleResult);
        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        verify(personRepository, times(1)).findByRoles(role);
    }

    /**
     * Should not call findByRoles function
     */
    @Test
    public void getPeople() {
        final Role role = new Role();
        role.setPeople(List.of(new Person("P1"), new Person("P2")));

        final List<Person> peopleExpected = List.of(new Person("P1"), new Person("P2"));

        final List<Person> peopleResult = roleService.getPeople(role);

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        verify(personRepository, never()).findByRoles(role);
    }
}