package ragde;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import ragde.models.*;
import ragde.repositories.*;
import ragde.security.services.SecurityService;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Populates data tables at application start
 */
@Component
public class DataLoader {

    @Autowired
    private AuthProviderRepository authProviderRepository;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private SecurityService securityService;

    @Value("${data-loader}")
    private Boolean loadData;

    @PostConstruct
    private void setupDatabase() {
        try {
            if (loadData && authProviderRepository.count() == 0) {
                insertData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void insertData() throws Exception {
        List<AuthProvider> authProviders = List.of(
                new AuthProvider("LOCAL", "Provide access with local username and password", null, null),
                new AuthProvider("FACEBOOK", "Provide access with Facebook account", "1967174556827438", "97eb0ef378c93e2156f6d695d41cd7f9"),
                new AuthProvider("GOOGLE", "Provide access with Google account", "681252099587-sc8d7fg8rnsrn8a2e07k3e9qn4v6qfoq.apps.googleusercontent.com", "AbjKPXKlZOmasIiJm1MqXsPx")
        );
        authProviderRepository.saveAll(authProviders);

        Set<Permission> allPermissions = Set.of(
                new Permission("CREATE_USERS", "Allows to create and edit users and people"),
                new Permission("REMOVE_USERS", "Allows to delete users and people"),
                new Permission("VIEW_USERS", "Allows to view users and people"),
                new Permission("CREATE_ROLES", "Allows to create and edit roles and permissions"),
                new Permission("REMOVE_ROLES", "Allows to delete roles and permissions"),
                new Permission("VIEW_ROLES", "Allows to view roles and permissions")
        );
        permissionRepository.saveAll(allPermissions);

        Set<Permission> userPermissions = allPermissions.stream().filter(p -> List.of("VIEW_USERS", "VIEW_ROLES").contains(p.getName())).collect(Collectors.toSet());
        Set<Role> allRoles = Set.of(
                new Role("ADMIN", "User with all permissions", allPermissions),
                new Role("USER", "User that only cans view", userPermissions)
        );
        roleRepository.saveAll(allRoles);

        Set<Role> userRoles = allRoles.stream().filter(p -> List.of("USER").contains(p.getName())).collect(Collectors.toSet());
        Set<Role> adminRoles = allRoles.stream().filter(p -> List.of("ADMIN").contains(p.getName())).collect(Collectors.toSet());
        List<Person> people = List.of(
                new Person("name 1", "last name 1", LocalDate.now(), Person.CIVIL_STATUS.SINGLE, Person.SEX.M, null, allRoles),
                new Person("name 2", "last name 2", LocalDate.now(), Person.CIVIL_STATUS.MARRIED, Person.SEX.F, "a2@a.com", userRoles),
                new Person("name 3", "last name 3", LocalDate.now(), Person.CIVIL_STATUS.SINGLE, Person.SEX.M, null, adminRoles)
        );
        personRepository.saveAll(people);

        String password = securityService.hashValue("123");
        List<Authentication> authentications = List.of(
                new Authentication("user1", password, authProviders.get(0), people.get(0)),
                new Authentication("user2", password, authProviders.get(0), people.get(1)),
                new Authentication("user3", password, authProviders.get(0), people.get(2))
        );
        authenticationRepository.saveAll(authentications);
    }
}