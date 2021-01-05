package ragde.security.services.implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ragde.exceptions.RagdeDontFoundException;
import ragde.exceptions.RagdeValidationException;
import ragde.models.Authentication;
import ragde.models.Person;
import ragde.models.Role;
import ragde.repositories.AuthenticationRepository;
import ragde.repositories.PersonRepository;
import ragde.security.factories.LoggedUserFactory;
import ragde.security.pojos.AccountCredentials;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.SecurityService;
import ragde.security.services.TokenService;

import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SecurityServiceImplTest {

    @Autowired
    private SecurityService securityService;

    @MockBean
    private AuthenticationRepository authenticationRepository;

    @MockBean
    private PersonRepository personRepository;

    @MockBean
    private LoggedUserFactory loggedUserFactory;

    @MockBean
    private TokenService tokenService;

    /**
     * Should throw RagdeDontFoundException when authentication null
     */
    @Test
    public void authenticateAuthenticationNull() {
        final String USERNAME = "user";
        final AccountCredentials credentials = new AccountCredentials(USERNAME, null);
        given(authenticationRepository.findByUsername(USERNAME)).willReturn(null);

        assertThrows(RagdeDontFoundException.class, () -> securityService.authenticate(credentials));
    }

    /**
     * Should throw RagdeDontFoundException when password incorrect
     */
    @Test
    public void authenticatePasswordIncorrect() {
        final String USERNAME = "user";
        final String PASSWORD = "pass";
        final AccountCredentials credentials = new AccountCredentials(USERNAME, PASSWORD);
        final Authentication authentication = new Authentication(USERNAME, PASSWORD, null, null);
        given(authenticationRepository.findByUsername(USERNAME)).willReturn(authentication);

        assertThrows(RagdeDontFoundException.class, () -> securityService.authenticate(credentials));
    }

    /**
     * Should return a LoggedUser when credentials correct
     */
    @Test
    public void authenticateCorrect() throws IOException {
        final String USERNAME = "user";
        final String PASSWORD = "pass";
        final AccountCredentials credentials = new AccountCredentials(USERNAME, PASSWORD);
        final Person person = new Person("P1");
        person.setName("Name");
        person.setLastName("Last Name");
        person.setRoles(Set.of(new Role("R1")));
        final Authentication authentication = new Authentication(USERNAME, DigestUtils.sha512Hex(PASSWORD), null, person);
        given(authenticationRepository.findByUsername(USERNAME)).willReturn(authentication);

        final String TOKEN = "token";
        final LoggedUser loggedUser = new LoggedUser("P1", "Name Last Name", "I", "R1", Collections.emptySet());
        final LoggedUser loggedUserExpected = new LoggedUser("P1", "Name Last Name", "I", "R1", Collections.emptySet());
        loggedUserExpected.setToken(TOKEN);
        given(loggedUserFactory.loggedUser(person)).willReturn(loggedUser);
        given(tokenService.createToken(loggedUser)).willReturn(TOKEN);
        given(tokenService.getLoggedUser(TOKEN)).willReturn(loggedUser);

        final LoggedUser loggedUserResult = securityService.authenticate(credentials);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(authenticationRepository, times(1)).findByUsername(USERNAME);
        verify(loggedUserFactory, times(1)).loggedUser(person);
        verify(tokenService, times(1)).createToken(loggedUser);
        verify(tokenService, times(1)).getLoggedUser(TOKEN);
    }

    /**
     * Should throw RagdeValidationException when not context
     */
    @Test
    public void changeRoleNotContext() {
        SecurityContextHolder.getContext().setAuthentication(null);
        final String ROLE_ID = "R1";

        assertThrows(RagdeValidationException.class, () -> securityService.changeRole(ROLE_ID));
    }

    /**
     * Should return a LoggedUser when correct and person is Null
     */
    @Test
    public void changeRoleCorrectWhenNull() throws IOException {
        final String PERSON_ID = "P1";
        final LoggedUser userMocked = new LoggedUser(PERSON_ID, "ROLE");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userMocked, null));

        final String ROLE_ID = "R2";
        given(personRepository.findById(PERSON_ID)).willReturn(Optional.empty());

        final String TOKEN = "token";
        final LoggedUser loggedUser = new LoggedUser("P1", "Name Last Name", "I", "R2", Collections.emptySet());
        final LoggedUser loggedUserExpected = new LoggedUser("P1", "Name Last Name", "I", "R2", Collections.emptySet());
        loggedUserExpected.setToken(TOKEN);
        given(loggedUserFactory.loggedUser(null, ROLE_ID)).willReturn(loggedUser);
        given(tokenService.createToken(loggedUser)).willReturn(TOKEN);
        given(tokenService.getLoggedUser(TOKEN)).willReturn(loggedUser);

        final LoggedUser loggedUserResult = securityService.changeRole(ROLE_ID);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(personRepository, times(1)).findById(PERSON_ID);
        verify(loggedUserFactory, times(1)).loggedUser(null, ROLE_ID);
        verify(tokenService, times(1)).createToken(loggedUser);
        verify(tokenService, times(1)).getLoggedUser(TOKEN);
    }

    /**
     * Should return a LoggedUser when correct
     */
    @Test
    public void changeRoleCorrect() throws IOException {
        final String PERSON_ID = "P1";
        final LoggedUser userMocked = new LoggedUser(PERSON_ID, "ROLE");
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userMocked, null));

        final String ROLE_ID = "R2";
        final Person person = new Person("P1");
        person.setName("Name");
        person.setLastName("Last Name");
        person.setRoles(Set.of(new Role("R1"), new Role("R2"), new Role("R3")));
        given(personRepository.findById(PERSON_ID)).willReturn(Optional.of(person));

        final String TOKEN = "token";
        final LoggedUser loggedUser = new LoggedUser("P1", "Name Last Name", "I", "R2", Collections.emptySet());
        final LoggedUser loggedUserExpected = new LoggedUser("P1", "Name Last Name", "I", "R2", Collections.emptySet());
        loggedUserExpected.setToken(TOKEN);
        given(loggedUserFactory.loggedUser(person, ROLE_ID)).willReturn(loggedUser);
        given(tokenService.createToken(loggedUser)).willReturn(TOKEN);
        given(tokenService.getLoggedUser(TOKEN)).willReturn(loggedUser);

        final LoggedUser loggedUserResult = securityService.changeRole(ROLE_ID);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(personRepository, times(1)).findById(PERSON_ID);
        verify(loggedUserFactory, times(1)).loggedUser(person, ROLE_ID);
        verify(tokenService, times(1)).createToken(loggedUser);
        verify(tokenService, times(1)).getLoggedUser(TOKEN);
    }

    /**
     * Should return null when not context
     */
    @Test
    public void getLoggedUserNotContext() {
        SecurityContextHolder.getContext().setAuthentication(null);

        final LoggedUser userResult = securityService.getLoggedUser();

        assertNull(userResult);
    }

    /**
     * Should return null when context invalid
     */
    @Test
    public void getLoggedUserInvalid() {
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(new Person("ID"), null));

        final LoggedUser userResult = securityService.getLoggedUser();

        assertNull(userResult);
    }

    /**
     * Should return a LoggedUser when context valid
     */
    @Test
    public void getLoggedUserCorrect() throws JsonProcessingException {
        final LoggedUser userMocked = new LoggedUser("ID", "ROLE");

        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(userMocked, null));

        final String TOKEN = "token";
        final LoggedUser userExpected = new LoggedUser("ID", "ROLE");
        userExpected.setToken(TOKEN);
        given(tokenService.createToken(userMocked)).willReturn(TOKEN);

        final LoggedUser userResult = securityService.getLoggedUser();

        assertSame(userMocked, userResult);
        assertNotSame(userExpected, userResult);
        assertEquals(userExpected, userResult);
        verify(tokenService, times(1)).createToken(userMocked);
    }

    /**
     * Should hash value
     */
    @Test
    public void hashValue() {
        final String VALUE = "test";
        final String HASHED = DigestUtils.sha512Hex(VALUE);

        final String RESULT = securityService.hashValue(VALUE);

        assertNotSame(HASHED, RESULT);
        assertEquals(HASHED, RESULT);
    }
}