package ragde.security.services.implementations;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.UserOperations;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.oauth2.OAuth2Operations;
import org.springframework.social.google.api.oauth2.UserInfo;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ragde.models.AuthProvider;
import ragde.models.Authentication;
import ragde.models.Person;
import ragde.models.Role;
import ragde.repositories.AuthProviderRepository;
import ragde.repositories.AuthenticationRepository;
import ragde.repositories.PersonRepository;
import ragde.repositories.RoleRepository;
import ragde.security.factories.LoggedUserFactory;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.OAuthService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class OAuthServiceImplTest {

    @Autowired
    private OAuthService oAuthService;

    @MockBean
    private AuthenticationRepository authenticationRepository;

    @MockBean
    private PersonRepository personRepository;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private AuthProviderRepository authProviderRepository;

    @MockBean
    private LoggedUserFactory loggedUserFactory;

    /**
     * Should return a LoggedUser when Authentication is not null
     */
    @Test
    public void parseFacebookUserWithAuthentication() {
        final String PROFILE_ID = "ID";
        final User profile = Mockito.mock(User.class);
        given(profile.getId()).willReturn(PROFILE_ID);

        final byte[] image = new byte[20];
        final UserOperations userOperations = Mockito.mock(UserOperations.class);
        given(userOperations.getUserProfileImage()).willReturn(image);

        final FacebookTemplate template = Mockito.mock(FacebookTemplate.class);
        final String[] fields = {"id", "picture", "first_name", "last_name", "gender", "email", "birthday", "relationship_status"};
        given(template.fetchObject("me", User.class, fields)).willReturn(profile);
        given(template.userOperations()).willReturn(userOperations);

        final Person person = new Person("P1");
        final Authentication authentication = new Authentication(null, null, null, person);
        given(authenticationRepository.findByUsername(PROFILE_ID)).willReturn(authentication);
        given(loggedUserFactory.loggedUser(eq(person), eq(null), any(String.class))).willReturn(new LoggedUser("LU1", "Role"));
        final LoggedUser loggedUserExpected = new LoggedUser("LU1", "Role");

        final LoggedUser loggedUserResult = oAuthService.parseFacebookUser(template);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(profile, times(1)).getId();
        verify(userOperations, times(1)).getUserProfileImage();
        verify(template, times(1)).fetchObject("me", User.class, fields);
        verify(template, times(1)).userOperations();
        verify(authenticationRepository, times(1)).findByUsername(PROFILE_ID);
        verify(loggedUserFactory, times(1)).loggedUser(eq(person), eq(null), any(String.class));
    }

    /**
     * Should return a LoggedUser and create an Authentication with default values when Authentication is null
     */
    @Test
    public void parseFacebookUserWithNullAuthenticationDefaultValues() {
        final String PROFILE_ID = "ID";
        final User profile = Mockito.mock(User.class);
        given(profile.getId()).willReturn(PROFILE_ID);
        given(profile.getFirstName()).willReturn(null);
        given(profile.getLastName()).willReturn(null);
        given(profile.getBirthday()).willReturn(null);
        given(profile.getRelationshipStatus()).willReturn(null);
        given(profile.getGender()).willReturn(null);
        given(profile.getEmail()).willReturn(null);

        final Person person = new Person("P1");
        final Role role = new Role("R1");
        given(roleRepository.findByName("USER")).willReturn(role);
        given(personRepository.save(any(Person.class))).willReturn(person);

        final AuthProvider provider = new AuthProvider("AP1");
        given(authProviderRepository.findByName("FACEBOOK")).willReturn(provider);

        final Authentication authentication = new Authentication(PROFILE_ID, null, provider, person);
        given(authenticationRepository.save(authentication)).willReturn(authentication);

        final byte[] image = new byte[20];
        final UserOperations userOperations = Mockito.mock(UserOperations.class);
        given(userOperations.getUserProfileImage()).willReturn(image);

        final FacebookTemplate template = Mockito.mock(FacebookTemplate.class);
        final String[] fields = {"id", "picture", "first_name", "last_name", "gender", "email", "birthday", "relationship_status"};
        given(template.fetchObject("me", User.class, fields)).willReturn(profile);
        given(template.userOperations()).willReturn(userOperations);

        given(authenticationRepository.findByUsername(PROFILE_ID)).willReturn(null);
        given(loggedUserFactory.loggedUser(eq(person), eq(null), any(String.class))).willReturn(new LoggedUser("LU1", "Role"));
        final LoggedUser loggedUserExpected = new LoggedUser("LU1", "Role");

        final LoggedUser loggedUserResult = oAuthService.parseFacebookUser(template);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(profile, times(2)).getId();
        verify(profile, times(1)).getFirstName();
        verify(profile, times(1)).getLastName();
        verify(profile, times(1)).getGender();
        verify(profile, times(1)).getEmail();
        verify(userOperations, times(1)).getUserProfileImage();
        verify(template, times(1)).fetchObject("me", User.class, fields);
        verify(template, times(1)).userOperations();
        verify(authenticationRepository, times(1)).findByUsername(PROFILE_ID);
        verify(authenticationRepository, times(1)).save(authentication);
        verify(roleRepository, times(1)).findByName("USER");
        verify(personRepository, times(1)).save(any(Person.class));
        verify(authProviderRepository, times(1)).findByName("FACEBOOK");
        verify(loggedUserFactory, times(1)).loggedUser(eq(person), eq(null), any(String.class));
    }

    /**
     * Should return a LoggedUser and create an Authentication when Authentication is null
     */
    @Test
    public void parseFacebookUserWithNullAuthentication() {
        final String PROFILE_ID = "ID";
        final String NAME = "name 1";
        final String LAST_NAME = "last name";
        final String BIRTHDAY = "05/25/1987";
        final String STATUS = "Married";
        final String GENDER = "gender";
        final String EMAIL = "email";
        final User profile = Mockito.mock(User.class);
        given(profile.getId()).willReturn(PROFILE_ID);
        given(profile.getFirstName()).willReturn(NAME);
        given(profile.getLastName()).willReturn(LAST_NAME);
        given(profile.getBirthday()).willReturn(BIRTHDAY);
        given(profile.getRelationshipStatus()).willReturn(STATUS);
        given(profile.getGender()).willReturn(GENDER);
        given(profile.getEmail()).willReturn(EMAIL);

        final Person person = new Person("P1");
        final Role role = new Role("R1");
        given(roleRepository.findByName("USER")).willReturn(role);
        given(personRepository.save(any(Person.class))).willReturn(person);

        final AuthProvider provider = new AuthProvider("AP1");
        given(authProviderRepository.findByName("FACEBOOK")).willReturn(provider);

        final Authentication authentication = new Authentication(PROFILE_ID, null, provider, person);
        given(authenticationRepository.save(authentication)).willReturn(authentication);

        final byte[] image = new byte[20];
        final UserOperations userOperations = Mockito.mock(UserOperations.class);
        given(userOperations.getUserProfileImage()).willReturn(image);

        final FacebookTemplate template = Mockito.mock(FacebookTemplate.class);
        final String[] fields = {"id", "picture", "first_name", "last_name", "gender", "email", "birthday", "relationship_status"};
        given(template.fetchObject("me", User.class, fields)).willReturn(profile);
        given(template.userOperations()).willReturn(userOperations);

        given(authenticationRepository.findByUsername(PROFILE_ID)).willReturn(null);
        given(loggedUserFactory.loggedUser(eq(person), eq(null), any(String.class))).willReturn(new LoggedUser("LU1", "Role"));
        final LoggedUser loggedUserExpected = new LoggedUser("LU1", "Role");

        final LoggedUser loggedUserResult = oAuthService.parseFacebookUser(template);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(profile, times(2)).getId();
        verify(profile, times(2)).getFirstName();
        verify(profile, times(2)).getLastName();
        verify(profile, times(2)).getGender();
        verify(profile, times(1)).getEmail();
        verify(userOperations, times(1)).getUserProfileImage();
        verify(template, times(1)).fetchObject("me", User.class, fields);
        verify(template, times(1)).userOperations();
        verify(authenticationRepository, times(1)).findByUsername(PROFILE_ID);
        verify(authenticationRepository, times(1)).save(authentication);
        verify(roleRepository, times(1)).findByName("USER");
        verify(personRepository, times(1)).save(any(Person.class));
        verify(authProviderRepository, times(1)).findByName("FACEBOOK");
        verify(loggedUserFactory, times(1)).loggedUser(eq(person), eq(null), any(String.class));
    }

    /**
     * Should return a LoggedUser when Authentication is not null
     */
    @Test
    public void parseGoogleUserWithAuthentication() {
        final String PROFILE_ID = "ID";
        final String IMAGEN = "imagen";
        final String IMAGEN_RESULT = "imagen?sz=100";
        final UserInfo profile = Mockito.mock(UserInfo.class);
        given(profile.getId()).willReturn(PROFILE_ID);
        given(profile.getPicture()).willReturn(IMAGEN);

        final OAuth2Operations oAuth2Operations = Mockito.mock(OAuth2Operations.class);
        final GoogleTemplate template = Mockito.mock(GoogleTemplate.class);
        given(template.oauth2Operations()).willReturn(oAuth2Operations);
        given(oAuth2Operations.getUserinfo()).willReturn(profile);

        final Person person = new Person("P1");
        final Authentication authentication = new Authentication(null, null, null, person);
        given(authenticationRepository.findByUsername(PROFILE_ID)).willReturn(authentication);
        given(loggedUserFactory.loggedUser(person, null, IMAGEN_RESULT)).willReturn(new LoggedUser("LU1", "Role"));
        final LoggedUser loggedUserExpected = new LoggedUser("LU1", "Role");

        final LoggedUser loggedUserResult = oAuthService.parseGoogleUser(template);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(profile, times(1)).getId();
        verify(profile, times(1)).getPicture();
        verify(template, times(1)).oauth2Operations();
        verify(oAuth2Operations, times(1)).getUserinfo();
        verify(authenticationRepository, times(1)).findByUsername(PROFILE_ID);
        verify(loggedUserFactory, times(1)).loggedUser(person, null, IMAGEN_RESULT);
    }

    /**
     * Should return a LoggedUser and create an Authentication with default values when Authentication is null
     */
    @Test
    public void parseGoogleUserWithNullAuthenticationDefaultValues() {
        final String PROFILE_ID = "ID";
        final String IMAGEN = "imagen";
        final String IMAGEN_RESULT = "imagen?sz=100";
        final UserInfo profile = Mockito.mock(UserInfo.class);
        given(profile.getId()).willReturn(PROFILE_ID);
        given(profile.getGivenName()).willReturn(null);
        given(profile.getFamilyName()).willReturn(null);
        given(profile.getGender()).willReturn(null);
        given(profile.getEmail()).willReturn(null);
        given(profile.getPicture()).willReturn(IMAGEN);

        final Person person = new Person("P1");
        final Role role = new Role("R1");
        given(roleRepository.findByName("USER")).willReturn(role);
        given(personRepository.save(any(Person.class))).willReturn(person);

        final AuthProvider provider = new AuthProvider("AP1");
        final Authentication authentication = new Authentication(PROFILE_ID, null, provider, person);
        given(authProviderRepository.findByName("GOOGLE")).willReturn(provider);
        given(authenticationRepository.save(authentication)).willReturn(authentication);

        final OAuth2Operations oAuth2Operations = Mockito.mock(OAuth2Operations.class);
        final GoogleTemplate template = Mockito.mock(GoogleTemplate.class);
        given(template.oauth2Operations()).willReturn(oAuth2Operations);
        given(oAuth2Operations.getUserinfo()).willReturn(profile);

        given(authenticationRepository.findByUsername(PROFILE_ID)).willReturn(null);
        given(loggedUserFactory.loggedUser(person, null, IMAGEN_RESULT)).willReturn(new LoggedUser("LU1", "Role"));
        final LoggedUser loggedUserExpected = new LoggedUser("LU1", "Role");

        final LoggedUser loggedUserResult = oAuthService.parseGoogleUser(template);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(profile, times(2)).getId();
        verify(profile, times(1)).getGivenName();
        verify(profile, times(1)).getFamilyName();
        verify(profile, times(1)).getGender();
        verify(profile, times(1)).getEmail();
        verify(profile, times(1)).getPicture();
        verify(template, times(1)).oauth2Operations();
        verify(oAuth2Operations, times(1)).getUserinfo();
        verify(roleRepository, times(1)).findByName("USER");
        verify(personRepository, times(1)).save(any(Person.class));
        verify(authProviderRepository, times(1)).findByName("GOOGLE");
        verify(authenticationRepository, times(1)).save(authentication);
        verify(authenticationRepository, times(1)).findByUsername(PROFILE_ID);
        verify(loggedUserFactory, times(1)).loggedUser(person, null, IMAGEN_RESULT);
    }

    /**
     * Should return a LoggedUser and create an Authentication when Authentication is null
     */
    @Test
    public void parseGoogleUserWithNullAuthentication() {
        final String PROFILE_ID = "ID";
        final String NAME = "name 1";
        final String LAST_NAME = "last name";
        final String GENDER = "gender";
        final String EMAIL = "email";
        final String IMAGEN = "imagen";
        final String IMAGEN_RESULT = "imagen?sz=100";
        final UserInfo profile = Mockito.mock(UserInfo.class);
        given(profile.getId()).willReturn(PROFILE_ID);
        given(profile.getGivenName()).willReturn(NAME);
        given(profile.getFamilyName()).willReturn(LAST_NAME);
        given(profile.getGender()).willReturn(GENDER);
        given(profile.getEmail()).willReturn(EMAIL);
        given(profile.getPicture()).willReturn(IMAGEN);

        final Person person = new Person("P1");
        final Role role = new Role("R1");
        given(roleRepository.findByName("USER")).willReturn(role);
        given(personRepository.save(any(Person.class))).willReturn(person);

        final AuthProvider provider = new AuthProvider("AP1");
        final Authentication authentication = new Authentication(PROFILE_ID, null, provider, person);
        given(authProviderRepository.findByName("GOOGLE")).willReturn(provider);
        given(authenticationRepository.save(authentication)).willReturn(authentication);

        final OAuth2Operations oAuth2Operations = Mockito.mock(OAuth2Operations.class);
        final GoogleTemplate template = Mockito.mock(GoogleTemplate.class);
        given(template.oauth2Operations()).willReturn(oAuth2Operations);
        given(oAuth2Operations.getUserinfo()).willReturn(profile);

        given(authenticationRepository.findByUsername(PROFILE_ID)).willReturn(null);
        given(loggedUserFactory.loggedUser(person, null, IMAGEN_RESULT)).willReturn(new LoggedUser("LU1", "Role"));
        final LoggedUser loggedUserExpected = new LoggedUser("LU1", "Role");

        final LoggedUser loggedUserResult = oAuthService.parseGoogleUser(template);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(profile, times(2)).getId();
        verify(profile, times(2)).getGivenName();
        verify(profile, times(2)).getFamilyName();
        verify(profile, times(2)).getGender();
        verify(profile, times(1)).getEmail();
        verify(profile, times(1)).getPicture();
        verify(template, times(1)).oauth2Operations();
        verify(oAuth2Operations, times(1)).getUserinfo();
        verify(roleRepository, times(1)).findByName("USER");
        verify(personRepository, times(1)).save(any(Person.class));
        verify(authProviderRepository, times(1)).findByName("GOOGLE");
        verify(authenticationRepository, times(1)).save(authentication);
        verify(authenticationRepository, times(1)).findByUsername(PROFILE_ID);
        verify(loggedUserFactory, times(1)).loggedUser(person, null, IMAGEN_RESULT);
    }
}