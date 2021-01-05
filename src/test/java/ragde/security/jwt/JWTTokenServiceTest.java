package ragde.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ragde.security.pojos.AccountCredentials;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.SecurityService;
import ragde.security.services.TokenService;

import java.io.IOException;
import java.util.Date;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class JWTTokenServiceTest {

    @Autowired
    private TokenService tokenService;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Integer expirationTime;

    @Autowired
    private ObjectMapper mapper;

    @MockBean
    private SecurityService securityService;

    /**
     * Should return null when LoggedUser null
     */
    @Test
    public void createTokenWhenNull() throws JsonProcessingException {
        final String token = tokenService.createToken(null);

        assertNull(token);
    }

    /**
     * Should return null when LoggedUser has token
     */
    @Test
    public void createTokenWhenToken() throws JsonProcessingException {
        final LoggedUser loggedUser = new LoggedUser("ID", "Full Name", "I", "ROLE", Set.of("P1", "P2", "P3"));
        loggedUser.setToken("token");

        final String token = tokenService.createToken(loggedUser);

        assertNull(token);
    }

    /**
     * Should return a token when LoggedUser not null
     */
    @Test
    public void createTokenWhenNotNull() throws IOException {
        final LoggedUser loggedUser = new LoggedUser("ID", "Full Name", "I", "ROLE", Set.of("P1", "P2", "P3"));

        final String jsonExpected = mapper.writeValueAsString(loggedUser);

        final String tokenResult = tokenService.createToken(loggedUser);

        final String jsonResult = getJson(tokenResult);
        final LoggedUser loggedUserResult = mapper.readValue(jsonResult, LoggedUser.class);

        assertNotSame(jsonExpected, jsonResult);
        assertEquals(jsonExpected, jsonResult);
        assertNotSame(loggedUser, loggedUserResult);
        assertEquals(loggedUser, loggedUserResult);
    }

    /**
     * Should return null when getLoggedUser null
     */
    @Test
    public void refreshTokenWhenNull() throws JsonProcessingException {
        given(securityService.getLoggedUser()).willReturn(null);

        final String token = tokenService.refreshToken();

        assertNull(token);
        verify(securityService, times(1)).getLoggedUser();
    }

    /**
     * Should return a token when getLoggedUser not null
     */
    @Test
    public void refreshTokenWhenNotNull() throws IOException {
        final String ID = "ID";
        final String ROLE = "ROLE";
        final LoggedUser loggedUserMocked = new LoggedUser(ID, ROLE);
        given(securityService.getLoggedUser()).willReturn(loggedUserMocked);

        final LoggedUser loggedUserExpected = new LoggedUser(ID, ROLE);

        final String jsonExpected = mapper.writeValueAsString(loggedUserExpected);

        final String tokenResult = tokenService.refreshToken();

        final String jsonResult = getJson(tokenResult);
        final LoggedUser loggedUserResult = mapper.readValue(jsonResult, LoggedUser.class);

        assertNotSame(jsonExpected, jsonResult);
        assertEquals(jsonExpected, jsonResult);
        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
        verify(securityService, times(1)).getLoggedUser();
    }

    /**
     * Should get a LoggedUser with null values when token invalid
     */
    @Test
    public void getLoggedUserWhenInvalid() throws IOException {
        final String json = mapper.writeValueAsString(new AccountCredentials());
        final String token = Jwts.builder()
                .setSubject(json)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        final LoggedUser loggedUserResult = tokenService.getLoggedUser(token);

        assertNull(loggedUserResult.getId());
        assertNull(loggedUserResult.getFullName());
        assertNull(loggedUserResult.getRole());
        assertNull(loggedUserResult.getPermissions());
    }

    /**
     * Should return null when token's claim is null
     */
    @Test
    public void getLoggedUserWhenNull() throws IOException {
        final String token = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        final LoggedUser loggedUserResult = tokenService.getLoggedUser(token);

        assertNull(loggedUserResult);
    }

    /**
     * Should return a LoggedUser when token valid
     */
    @Test
    public void getLoggedUserWhenValid() throws IOException {
        final LoggedUser loggedUserExpected = new LoggedUser("ID", "full name", "I", "ROLE", Set.of("P1", "P2"));
        final String json = mapper.writeValueAsString(loggedUserExpected);
        final String token = Jwts.builder()
                .setSubject(json)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();

        final LoggedUser loggedUserResult = tokenService.getLoggedUser(token);

        assertNotSame(loggedUserExpected, loggedUserResult);
        assertEquals(loggedUserExpected, loggedUserResult);
    }

    private String getJson(String token) {
        return Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }
}