package ragde.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import ragde.models.AuthProvider;
import ragde.models.Authentication;
import ragde.models.Person;
import ragde.repositories.*;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.TokenService;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.given;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@SuppressWarnings("unchecked")
public class AuthProviderIntegrationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TokenService tokenService;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private AuthProviderRepository authProviderRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    private List<AuthProvider> dbAuthProviders;

    private List<Authentication> dbAuthentication;

    private IntegrationTest integrationTest;

    private final String CREATE_USERS_TOKEN = "create";

    @Before
    public void setup() throws Exception {
        given(tokenService.getLoggedUser(CREATE_USERS_TOKEN)).willReturn(
                new LoggedUser(null, null, null, null, Set.of("CREATE_USERS")));

        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
        IntegrationTest.cleanAllData(authenticationRepository, authProviderRepository, personRepository, roleRepository, permissionRepository);

        dbAuthProviders = List.of(
                new AuthProvider("N1", "D1", "AK1", "AS1"),
                new AuthProvider("N2", "D2", "AK2", "AS2"),
                new AuthProvider("N3", "D3", "AK3", "AS3"),
                new AuthProvider("N4", "D4", "AK4", "AS4")
        );
        authProviderRepository.saveAll(dbAuthProviders);

        final Person person = personRepository.save(new Person("N", "LN", LocalDate.now(), Person.CIVIL_STATUS.SINGLE, Person.SEX.M, null, Collections.emptySet()));
        final Person person2 = personRepository.save(new Person("N2", "LN2", LocalDate.now(), Person.CIVIL_STATUS.SINGLE, Person.SEX.M, null, Collections.emptySet()));

        dbAuthentication = List.of(
                new Authentication("A1", "PASS", dbAuthProviders.get(3), person),
                new Authentication("A2", "PASS", dbAuthProviders.get(3), person2));
        authenticationRepository.saveAll(dbAuthentication);
    }

    /**
     * Should return an error response
     */
    @Test
    public void validateGraphQLIgnore() throws Exception {
        final String query = "query {authProviders {id name authKey authSecret}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final List<Map<String, String>> errors = (List) mapResult.get("errors");

        assertNull(mapResult.get("data"));
        assertTrue(errors.get(0).get("message").contains("Field 'authKey' in type 'AuthProvider' is undefined"));
        assertTrue(errors.get(1).get("message").contains("Field 'authSecret' in type 'AuthProvider' is undefined"));
    }

    /**
     * Should return a success response
     */
    @Test
    public void authProviders() throws Exception {
        final String query = "query {authProviders {id name authentications{id username}}}";
        final Map mapResult = integrationTest.performGraphQL(query, CREATE_USERS_TOKEN);
        final Map<String, List> data = (Map) mapResult.get("data");
        final List<Map> authProviders = data.get("authProviders");
        final List<Map> authentications = (List) authProviders.get(3).get("authentications");

        assertNull(mapResult.get("errors"));
        assertEquals(4, authProviders.size());
        assertEquals("N1", authProviders.get(0).get("name"));
        assertEquals("N2", authProviders.get(1).get("name"));
        assertEquals("N3", authProviders.get(2).get("name"));
        assertEquals("N4", authProviders.get(3).get("name"));
        assertEquals("A1", authentications.get(0).get("username"));
        assertEquals("A2", authentications.get(1).get("username"));
    }
}