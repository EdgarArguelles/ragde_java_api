package ragde.integration_test.authentications;

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
import ragde.integration_test.IntegrationTest;
import ragde.security.services.TokenService;

import java.util.Map;

import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class PersonAuthenticationTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private TokenService tokenService;

    private IntegrationTest integrationTest;

    private final String peopleQuery = "query {people {id fullName authentications{id}}}";

    private final String personQuery = "query {person(id: 5) {id fullName}}";

    private final String createPersonQuery = "mutation {createPerson(person: {}) {id fullName}}";

    private final String updatePersonQuery = "mutation {updatePerson(person: {}) {id fullName}}";

    private final String deletePersonQuery = "mutation {deletePerson(id: 5) {id fullName}}";

    private final String personPageQuery = "query {personPage(pageDataRequest: {}) {totalElements}}";

    @Before
    public void setup() throws Exception {
        integrationTest = new IntegrationTest(mvc, mapper, tokenService);
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void peopleNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(peopleQuery, null);

        assertNull(data.get("people"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void peopleTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(peopleQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void peopleNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(peopleQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("people"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void personNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(personQuery, null);

        assertNull(data.get("person"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void personTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(personQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void personNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(personQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("person"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void createPersonNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(createPersonQuery, null);

        assertNull(data.get("createPerson"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void createPersonTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(createPersonQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void createPersonNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(createPersonQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("createPerson"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void updatePersonNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(updatePersonQuery, null);

        assertNull(data.get("updatePerson"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void updatePersonTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(updatePersonQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void updatePersonNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(updatePersonQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("updatePerson"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void deletePersonNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(deletePersonQuery, null);

        assertNull(data.get("deletePerson"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void deletePersonTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(deletePersonQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void deletePersonNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(deletePersonQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("deletePerson"));
    }

    /**
     * Should return an UNAUTHORIZED error response when not token
     */
    @Test
    public void personPageNotToken() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(personPageQuery, null);

        assertNull(data.get("personPage"));
    }

    /**
     * Should return an UNAUTHORIZED error response when token invalid
     */
    @Test
    public void personPageTokenInvalid() throws Exception {
        integrationTest.failGraphQLTokenInvalid(personPageQuery);
    }

    /**
     * Should return an UNAUTHORIZED error response when not permissions
     */
    @Test
    public void personPageNotPermission() throws Exception {
        final Map data = integrationTest.failGraphQLAccessDenied(personPageQuery, IntegrationTest.NOT_PERMISSION_TOKEN);

        assertNull(data.get("personPage"));
    }
}