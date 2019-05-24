package ragde.integration_test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONObject;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ragde.pojos.responses.error.nesteds.NestedError;
import ragde.pojos.responses.error.nesteds.ValidationNestedError;
import ragde.repositories.*;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.TokenService;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SuppressWarnings("unchecked")
public class IntegrationTest {

    private final MockMvc mvc;

    private final ObjectMapper mapper;

    private final TokenService tokenService;

    private final String NEW_TOKEN = "new";

    public static final String ALL_PERMISSIONS_TOKEN = "user with all permissions";

    public static final String NOT_PERMISSION_TOKEN = "not permissions";

    public static final String GRAPH_QL_PATH = "/graphql";

    public IntegrationTest(MockMvc mvc, ObjectMapper mapper, TokenService tokenService) throws Exception {
        this.mvc = mvc;
        this.mapper = mapper;
        this.tokenService = tokenService;

        given(tokenService.refreshToken()).willReturn(NEW_TOKEN);
        given(tokenService.getLoggedUser(ALL_PERMISSIONS_TOKEN)).willReturn(
                new LoggedUser("ID", "FN", null, "R", Set.of("VIEW_ROLES", "CREATE_ROLES",
                        "REMOVE_ROLES", "VIEW_USERS", "CREATE_USERS", "REMOVE_USERS"))
        );
        given(tokenService.getLoggedUser(NOT_PERMISSION_TOKEN)).willReturn(
                new LoggedUser(null, null, null, null, Collections.emptySet()));
    }

    /**
     * Clean all database entries
     */
    public static void cleanAllData(
            AuthenticationRepository authenticationRepository,
            AuthProviderRepository authProviderRepository,
            PersonRepository personRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository
    ) {
        authenticationRepository.deleteAll();
        authProviderRepository.deleteAll();
        personRepository.deleteAll();
        roleRepository.deleteAll();
        permissionRepository.deleteAll();
    }

    /**
     * Perform a graphQL query with invalid token
     *
     * @param query graphQL query to be performed
     */
    public void failGraphQLTokenInvalid(String query) throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(GRAPH_QL_PATH)
                .content(new JSONObject(Map.of("query", query)).toString())
                .header("Authorization", "Bearer Invalid")
                .contentType(MediaType.APPLICATION_JSON);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isUnauthorized())
                .andReturn().getResponse().getContentAsString();

        assertEquals("", bodyResult);
        verify(tokenService, times(1)).getLoggedUser("Invalid");
    }

    /**
     * Perform a graphQL query without authorization
     *
     * @param query graphQL query to be performed
     * @param token authorization token to be used in the header
     * @return generated response
     */
    public Map failGraphQLAccessDenied(String query, String token) throws Exception {
        return failGraphQL(query, "Access is denied", token);
    }

    /**
     * Perform a graphQL query with error
     *
     * @param query graphQL query to be performed
     * @param error expected error message
     * @param token authorization token to be used in the header
     * @return generated response
     */
    public Map failGraphQL(String query, String error, String token) throws Exception {
        MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(GRAPH_QL_PATH)
                .content(new JSONObject(Map.of("query", query)).toString())
                .contentType(MediaType.APPLICATION_JSON);

        if (token != null) {
            builder = builder.header("Authorization", "Bearer " + token);
        }

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Map mapResult = mapper.readValue(bodyResult, HashMap.class);
        final Map data = (Map) mapResult.get("data");
        final List<Map> errors = (List) mapResult.get("errors");
        final Map<String, Map<String, String>> extensions = (Map) errors.get(0).get("extensions");

        if (error.equals("Access is denied")) {
            assertEquals("Access is denied.", errors.get(0).get("message"));
            assertEquals("FORBIDDEN", extensions.get("errorType"));
            assertEquals(403, extensions.get("errorCode"));
            assertEquals(error, extensions.get("error").get("devMessage"));
        } else if (extensions.get("error").get("devMessage") != null) {
            assertEquals("An error has occurred.", errors.get(0).get("message"));
            assertEquals("INTERNAL_SERVER_ERROR", extensions.get("errorType"));
            assertEquals(500, extensions.get("errorCode"));
            assertTrue(extensions.get("error").get("devMessage").contains(error));
        } else {
            assertEquals(error, errors.get(0).get("message"));
            assertEquals(error, extensions.get("error").get("message"));
        }

        if (token != null) {
            verify(tokenService, times(1)).getLoggedUser(token);
        } else {
            verify(tokenService, never()).getLoggedUser(any());
        }

        return data;
    }

    /**
     * Perform a graphQL query with authorization
     *
     * @param query graphQL query to be performed
     * @param token authorization token to be used in the header
     * @return generated response
     */
    public Map performGraphQL(String query, String token) throws Exception {
        final MockHttpServletRequestBuilder builder = MockMvcRequestBuilders.post(GRAPH_QL_PATH)
                .content(new JSONObject(Map.of("query", query)).toString())
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON);

        final String bodyResult = mvc.perform(builder)
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        final Map mapResult = mapper.readValue(bodyResult, HashMap.class);

        verify(tokenService, times(1)).getLoggedUser(token);

        return mapResult;
    }

    /**
     * Validate an object and get it's validation errors
     *
     * @param object object to be validated
     * @return list of NestedError
     */
    public static List<NestedError> getValidationErrors(Object object) {
        final ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        final Validator validator = factory.getValidator();
        final Set<ConstraintViolation<Object>> violations = validator.validate(object);

        final List<NestedError> collect = violations.stream()
                .map(violation -> new ValidationNestedError(violation.getPropertyPath().toString(), violation.getMessage()))
                .sorted(Comparator.comparing(ValidationNestedError::getField).thenComparing(ValidationNestedError::getMessage))
                .collect(Collectors.toList());

        return collect;
    }
}