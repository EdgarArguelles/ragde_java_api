package ragde.security.services.implementations;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.User;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.google.api.oauth2.UserInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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

import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.Date;
import java.util.Set;

@Service
public class OAuthServiceImpl implements OAuthService {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthProviderRepository authProviderRepository;

    @Autowired
    private LoggedUserFactory loggedUserFactory;

    @Override
    @Transactional
    public LoggedUser parseFacebookUser(FacebookTemplate template) {
        String[] fields = {"id", "picture", "first_name", "last_name", "gender", "email", "birthday", "relationship_status"};
        User profile = template.fetchObject("me", User.class, fields);
        String id = profile.getId();
        Authentication authentication = authenticationRepository.findByUsername(id);
        if (authentication == null) {
            authentication = createNewAuthentication(profile);
        }

        byte[] image = template.userOperations().getUserProfileImage();
        StringBuilder imageString = new StringBuilder();
        imageString.append("data:image/png;base64,");
        imageString.append(Base64.getEncoder().encodeToString(image));
        return loggedUserFactory.loggedUser(authentication.getPerson(), null, imageString.toString());
    }

    @Override
    @Transactional
    public LoggedUser parseGoogleUser(GoogleTemplate template) {
        UserInfo profile = template.oauth2Operations().getUserinfo();
        String id = profile.getId();
        Authentication authentication = authenticationRepository.findByUsername(id);
        if (authentication == null) {
            authentication = createNewAuthentication(profile);
        }

        String image = profile.getPicture().concat("?sz=100");
        return loggedUserFactory.loggedUser(authentication.getPerson(), null, image);
    }

    /**
     * Create a new Authentication using Facebook profile
     *
     * @param profile Facebook profile data
     * @return new Authentication created
     */
    private Authentication createNewAuthentication(User profile) {
        Person person = createNewPerson(profile);
        AuthProvider provider = authProviderRepository.findByName("FACEBOOK");

        Authentication authentication = new Authentication(profile.getId(), null, provider, person);
        return authenticationRepository.save(authentication);
    }

    /**
     * Create a new Person using Facebook profile
     *
     * @param profile Facebook profile data
     * @return new Person created
     */
    private Person createNewPerson(User profile) {
        String firstName = profile.getFirstName() != null ? profile.getFirstName() : "";
        String lastName = profile.getLastName() != null ? profile.getLastName() : "";
        String profileBirthday = profile.getBirthday();
        String relationshipStatus = profile.getRelationshipStatus() != null ? profile.getRelationshipStatus() : "single";
        String gender = profile.getGender() != null ? profile.getGender() : "male";
        String email = profile.getEmail();

        LocalDate birthday = profileBirthday != null ? LocalDate.parse(profileBirthday, DateTimeFormatter.ofPattern("MM/dd/yyyy")) : LocalDate.now();
        return saveNewPerson(firstName, lastName, birthday, relationshipStatus, gender, email);
    }

    /**
     * Create a new Authentication using Google profile
     *
     * @param profile Google profile data
     * @return new Authentication created
     */
    private Authentication createNewAuthentication(UserInfo profile) {
        Person person = createNewPerson(profile);
        AuthProvider provider = authProviderRepository.findByName("GOOGLE");

        Authentication authentication = new Authentication(profile.getId(), null, provider, person);
        return authenticationRepository.save(authentication);
    }

    /**
     * Create a new Person using Google profile
     *
     * @param profile Google profile data
     * @return new Person created
     */
    private Person createNewPerson(UserInfo profile) {
        String givenName = profile.getGivenName() != null ? profile.getGivenName() : "";
        String familyName = profile.getFamilyName() != null ? profile.getFamilyName() : "";
        Date profileBirthday = null;
        String relationshipStatus = "single";
        String gender = profile.getGender() != null ? profile.getGender() : "male";
        String email = profile.getEmail();

        LocalDate birthday = profileBirthday != null ? profileBirthday.toInstant().atZone(ZoneOffset.UTC).toLocalDate() : LocalDate.now();
        return saveNewPerson(givenName, familyName, birthday, relationshipStatus, gender, email);
    }

    /**
     * Save a new Person
     *
     * @param firstName          Person first name
     * @param lastName           Person last name
     * @param birthday           Person birthday
     * @param relationshipStatus Person relationship status
     * @param gender             Person gender
     * @param email              Person email
     * @return new Person created
     */
    private Person saveNewPerson(String firstName, String lastName, LocalDate birthday, String relationshipStatus, String gender, String email) {
        Integer civilStatus = relationshipStatus.equalsIgnoreCase("MARRIED") ? Person.CIVIL_STATUS.MARRIED : Person.CIVIL_STATUS.SINGLE;
        String sex = gender.equalsIgnoreCase("male") ? Person.SEX.M : Person.SEX.F;
        Role role = roleRepository.findByName("USER");
        Person person = new Person(firstName, lastName, birthday, civilStatus, sex, email, Set.of(role));

        return personRepository.save(person);
    }
}