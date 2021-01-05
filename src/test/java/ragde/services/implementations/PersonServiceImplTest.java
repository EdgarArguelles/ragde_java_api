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
import ragde.models.Authentication;
import ragde.models.Person;
import ragde.models.Role;
import ragde.pojos.pages.PageDataRequest;
import ragde.repositories.AuthenticationRepository;
import ragde.repositories.PersonRepository;
import ragde.security.pojos.LoggedUser;
import ragde.services.PersonService;

import javax.validation.ConstraintViolationException;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PersonServiceImplTest {

    @Autowired
    private PersonService personService;

    @MockBean
    private PersonRepository personRepository;

    @MockBean
    private AuthenticationRepository authenticationRepository;

    @BeforeEach
    public void setup() {
        final LoggedUser user = new LoggedUser();
        user.setPermissions(Set.of("VIEW_USERS", "CREATE_USERS", "REMOVE_USERS"));
        final List<GrantedAuthority> authorities = user.getPermissions().stream().map(p -> (GrantedAuthority) () -> "ROLE_" + p).collect(Collectors.toList());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, authorities));
    }

    /**
     * Should call findAll function
     */
    @Test
    public void findAll() {
        final List<Person> peopleMocked = Arrays.asList(new Person("ID1"), new Person("ID2"), null, new Person("ID4"));
        peopleMocked.get(0).setAuthentications(List.of(new Authentication("A1")));
        peopleMocked.get(0).setRoles(Set.of(new Role("R1"), new Role("R2")));
        peopleMocked.get(1).setAuthentications(Collections.emptyList());
        peopleMocked.get(1).setRoles(Collections.emptySet());
        given(personRepository.findAll()).willReturn(peopleMocked);

        final List<Person> peopleExpected = Arrays.asList(new Person("ID1"), new Person("ID2"), null, new Person("ID4"));
        peopleExpected.get(0).setAuthentications(List.of(new Authentication("A1")));
        peopleExpected.get(0).setRoles(Set.of(new Role("R1"), new Role("R2")));
        peopleExpected.get(1).setAuthentications(Collections.emptyList());
        peopleExpected.get(1).setRoles(Collections.emptySet());

        final List<Person> peopleResult = personService.findAll();

        assertSame(peopleMocked, peopleResult);
        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        verify(personRepository, times(1)).findAll();
    }

    /**
     * Should throw RagdeDontFoundException
     */
    @Test
    public void findByIdWhenDontFound() {
        final String ID = "ID";
        given(personRepository.findById(ID)).willReturn(Optional.empty());

        assertThrows(RagdeDontFoundException.class, () -> personService.findById(ID));
    }

    /**
     * Should call findById function
     */
    @Test
    public void findById() {
        final String ID = "ID";
        final Person personMocked = new Person(ID);
        personMocked.setRoles(Set.of(new Role("R1"), new Role("R2")));
        given(personRepository.findById(ID)).willReturn(Optional.of(personMocked));

        final Person personExpected = new Person(ID);
        personExpected.setRoles(Set.of(new Role("R1"), new Role("R2")));

        final Person personResult = personService.findById(ID);

        assertSame(personMocked, personResult);
        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        verify(personRepository, times(1)).findById(ID);
    }

    /**
     * Should throw ConstraintViolationException when invalid
     */
    @Test
    public void saveInvalid() {
        assertThrows(ConstraintViolationException.class, () -> personService.save(new Person()));
    }

    /**
     * Should throw RagdeValidationException when Civil Status is invalid
     */
    @Test
    public void saveCivilStatusInvalid() {
        final Person person = new Person("name", "last", LocalDate.now(), -1, Person.SEX.M, null, null);

        assertThrows(RagdeValidationException.class, () -> personService.save(person));
    }

    /**
     * Should throw RagdeValidationException when Sex is invalid
     */
    @Test
    public void saveSexInvalid() {
        final Person person = new Person("name", "last", LocalDate.now(), Person.CIVIL_STATUS.SINGLE, "A", null, null);

        assertThrows(RagdeValidationException.class, () -> personService.save(person));
    }

    /**
     * Should return a person when save successfully
     */
    @Test
    public void saveSuccessfully() {
        final String NAME = "test";
        final String LAST_NAME = "last name";
        final LocalDate BIRTHDAY = LocalDate.now();
        final Integer CIVIL_STATUS = Person.CIVIL_STATUS.SINGLE;
        final String SEX = Person.SEX.M;
        final String EMAIL = "test@test.com";
        final Set<Role> ROLES = Set.of(new Role("R1"), new Role("R2"));
        final Person person = new Person(NAME, LAST_NAME, BIRTHDAY, CIVIL_STATUS, SEX, EMAIL, ROLES);
        given(personRepository.save(person)).willReturn(person);

        final Person personExpected = new Person(NAME, LAST_NAME, BIRTHDAY, CIVIL_STATUS, SEX, EMAIL, ROLES);

        final Person personResult = personService.save(person);

        assertSame(person, personResult);
        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        verify(personRepository, times(1)).save(person);
    }

    /**
     * Should throw ConstraintViolationException when invalid
     */
    @Test
    public void updateInvalid() {
        assertThrows(ConstraintViolationException.class, () -> personService.update(new Person()));
    }

    /**
     * Should throw RagdeValidationException when Civil Status is invalid
     */
    @Test
    public void updateCivilStatusInvalid() {
        final Person person = new Person("name", "last", LocalDate.now(), -1, Person.SEX.M, null, null);

        assertThrows(RagdeValidationException.class, () -> personService.update(person));
    }

    /**
     * Should throw RagdeValidationException when Sex is invalid
     */
    @Test
    public void updateSexInvalid() {
        final Person person = new Person("name", "last", LocalDate.now(), Person.CIVIL_STATUS.SINGLE, "A", null, null);

        assertThrows(RagdeValidationException.class, () -> personService.update(person));
    }

    /**
     * Should throw RagdeDontFoundException when person doesn't exist
     */
    @Test
    public void updateDontFound() {
        final String ID = "ID";
        final Person person = new Person("name", "last", LocalDate.now(), Person.CIVIL_STATUS.SINGLE, Person.SEX.M, null, null);
        person.setId(ID);
        person.setCivilStatus(Person.CIVIL_STATUS.SINGLE);
        person.setSex(Person.SEX.M);
        given(personRepository.findById(ID)).willReturn(Optional.empty());

        assertThrows(RagdeDontFoundException.class, () -> personService.update(person));
    }

    /**
     * Should return a person when update successfully
     */
    @Test
    public void updateSuccessfully() {
        final String ID = "ID";
        final String NAME_PERSON = "name after";
        final String NAME_ORIGINAL = "name before";
        final String LAST_NAME_PERSON = "last name after";
        final String LAST_NAME_ORIGINAL = "last name before";
        final LocalDate BIRTHDAY_PERSON = LocalDate.now();
        final LocalDate BIRTHDAY_ORIGINAL = LocalDate.now();
        final Integer CIVIL_STATUS_PERSON = Person.CIVIL_STATUS.SINGLE;
        final Integer CIVIL_STATUS_ORIGINAL = Person.CIVIL_STATUS.MARRIED;
        final String SEX_PERSON = Person.SEX.M;
        final String SEX_ORIGINAL = Person.SEX.F;
        final String EMAIL_PERSON = "after@aaa";
        final String EMAIL_ORIGINAL = "before@aaa";
        final Set<Role> ROLES_PERSON = Set.of(new Role("R1"));
        final Set<Role> ROLES_ORIGINAL = Set.of(new Role("R2"), new Role("R3"));
        final List<Authentication> AUTHENTICATIONS_PERSON = List.of(new Authentication("A1"));
        final List<Authentication> AUTHENTICATIONS_ORIGINAL = List.of(new Authentication("A2"), new Authentication("A3"));
        final Person person = new Person(NAME_PERSON, LAST_NAME_PERSON, BIRTHDAY_PERSON, CIVIL_STATUS_PERSON, SEX_PERSON, EMAIL_PERSON, ROLES_PERSON);
        person.setId(ID);
        person.setAuthentications(AUTHENTICATIONS_PERSON);
        final Person personOriginal = new Person(NAME_ORIGINAL, LAST_NAME_ORIGINAL, BIRTHDAY_ORIGINAL, CIVIL_STATUS_ORIGINAL, SEX_ORIGINAL, EMAIL_ORIGINAL, ROLES_ORIGINAL);
        personOriginal.setId(ID);
        personOriginal.setAuthentications(AUTHENTICATIONS_ORIGINAL);
        //only change name, last name, birthday, civil status, sex and email
        final Person personMocked = new Person(NAME_PERSON, LAST_NAME_PERSON, BIRTHDAY_PERSON, CIVIL_STATUS_PERSON, SEX_PERSON, EMAIL_PERSON, ROLES_PERSON);
        personMocked.setId(ID);
        personMocked.setAuthentications(AUTHENTICATIONS_ORIGINAL);
        given(personRepository.findById(ID)).willReturn(Optional.of(personOriginal));
        given(personRepository.save(personOriginal)).willReturn(personMocked);

        final Person personExpected = new Person(NAME_PERSON, LAST_NAME_PERSON, BIRTHDAY_PERSON, CIVIL_STATUS_PERSON, SEX_PERSON, EMAIL_PERSON, ROLES_PERSON);
        personExpected.setId(ID);
        personExpected.setAuthentications(AUTHENTICATIONS_ORIGINAL);

        final Person personResult = personService.update(person);

        assertSame(personMocked, personResult);
        assertNotSame(person, personResult);
        assertNotSame(personOriginal, personResult);
        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        verify(personRepository, times(1)).findById(ID);
        verify(personRepository, times(1)).save(personOriginal);
    }

    /**
     * Should throw RagdeDontFoundException when person doesn't exist
     */
    @Test
    public void deleteDontFound() {
        final String ID = "ID";
        given(personRepository.findById(ID)).willReturn(Optional.empty());

        assertThrows(RagdeDontFoundException.class, () -> personService.delete(ID));
    }

    /**
     * Should throw RagdeValidationException when person is being used
     */
    @Test
    public void deleteUsed() {
        final String ID = "ID";
        final Person person = new Person(ID);
        person.setAuthentications(List.of(new Authentication("A1")));
        given(personRepository.findById(ID)).willReturn(Optional.of(person));

        assertThrows(RagdeValidationException.class, () -> personService.delete(ID));
    }

    /**
     * Should return a person when delete successfully
     */
    @Test
    public void deleteSuccessfully() {
        final String ID = "ID";
        final String NAME = "test";
        final String LAST_NAME = "last name";
        final LocalDate BIRTHDAY = LocalDate.now();
        final Integer CIVIL_STATUS = Person.CIVIL_STATUS.SINGLE;
        final String SEX = Person.SEX.M;
        final String EMAIL = "test@test.com";
        final Set<Role> ROLES = Set.of(new Role("R1"), new Role("R2"));
        final Person person = new Person(NAME, LAST_NAME, BIRTHDAY, CIVIL_STATUS, SEX, EMAIL, ROLES);
        person.setId(ID);
        given(personRepository.findById(ID)).willReturn(Optional.of(person));
        doNothing().when(personRepository).delete(person);

        final Person personExpected = new Person(NAME, LAST_NAME, BIRTHDAY, CIVIL_STATUS, SEX, EMAIL, null);
        personExpected.setId(ID);

        final Person personResult = personService.delete(ID);

        assertSame(person, personResult);
        assertNotSame(personExpected, personResult);
        assertEquals(personExpected, personResult);
        verify(personRepository, times(1)).findById(ID);
        verify(personRepository, times(1)).delete(person);
    }

    /**
     * Should throw ConstraintViolationException when invalid
     */
    @Test
    public void pageInvalid() {
        assertThrows(ConstraintViolationException.class, () -> personService.page(new PageDataRequest()));
    }

    /**
     * Should call page function
     */
    @Test
    public void page() {
        final PageDataRequest pageDataRequest = new PageDataRequest(0, 1, null, null, null);
        final Page<Person> peopleMocked = new PageImpl<>(List.of(new Person("ID1"), new Person("ID2")));
        given(personRepository.page(pageDataRequest)).willReturn(peopleMocked);

        final Page<Person> peopleExpected = new PageImpl<>(List.of(new Person("ID1"), new Person("ID2")));
        final Page<Person> peopleResult = personService.page(pageDataRequest);

        assertSame(peopleMocked, peopleResult);
        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
        verify(personRepository, times(1)).page(pageDataRequest);
    }

    /**
     * Should call findByPerson function
     */
    @Test
    public void getAuthenticationsWhenNull() {
        final Person person = new Person();
        final List<Authentication> authenticationsMocked = List.of(new Authentication("A1"), new Authentication("A2"));
        given(authenticationRepository.findByPerson(person)).willReturn(authenticationsMocked);

        final List<Authentication> authenticationsExpected = List.of(new Authentication("A1"), new Authentication("A2"));

        final List<Authentication> authenticationsResult = personService.getAuthentications(person);

        assertSame(authenticationsMocked, authenticationsResult);
        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        verify(authenticationRepository, times(1)).findByPerson(person);
    }

    /**
     * Should not call findByPerson function
     */
    @Test
    public void getAuthentications() {
        final Person person = new Person();
        person.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));

        final List<Authentication> authenticationsExpected = List.of(new Authentication("A1"), new Authentication("A2"));

        final List<Authentication> authenticationsResult = personService.getAuthentications(person);

        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        verify(authenticationRepository, never()).findByPerson(person);
    }
}