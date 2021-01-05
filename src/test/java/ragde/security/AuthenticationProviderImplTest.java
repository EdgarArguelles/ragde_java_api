package ragde.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.TokenService;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthenticationProviderImplTest {

    @Autowired
    private AuthenticationProvider authenticationProvider;

    @MockBean
    private TokenService tokenService;

    /**
     * Should return null when token invalid
     */
    @Test
    public void authenticateTokenInvalid() throws IOException {
        final String TOKEN = "token";
        final Authentication authentication = new UsernamePasswordAuthenticationToken(TOKEN, null);

        given(tokenService.getLoggedUser(TOKEN)).willReturn(null);

        final Authentication authenticateResult = authenticationProvider.authenticate(authentication);

        assertNull(authenticateResult);
        verify(tokenService, times(1)).getLoggedUser(TOKEN);
    }

    /**
     * Should create valid UsernamePasswordAuthenticationToken when token valid without permissions
     */
    @Test
    public void authenticateTokenValidWithNotPermissions() throws IOException {
        final String TOKEN = "token";
        final LoggedUser userMocked = new LoggedUser("ID", "ROLE");
        final Authentication authentication = new UsernamePasswordAuthenticationToken(TOKEN, null);

        given(tokenService.getLoggedUser(TOKEN)).willReturn(userMocked);

        final Authentication authenticateExpected = new UsernamePasswordAuthenticationToken(userMocked, null, Collections.emptyList());

        final Authentication authenticateResult = authenticationProvider.authenticate(authentication);

        assertNotSame(authentication, authenticateResult);
        assertNotSame(authenticateExpected, authenticateResult);
        assertSame(authenticateExpected.getPrincipal(), authenticateResult.getPrincipal());
        assertEquals(authenticateExpected.getAuthorities().size(), authenticateResult.getAuthorities().size());
        assertEquals(0, authenticateResult.getAuthorities().size());
        verify(tokenService, times(1)).getLoggedUser(TOKEN);
    }

    /**
     * Should create valid UsernamePasswordAuthenticationToken when token valid with permissions
     */
    @Test
    public void authenticateTokenValidWithPermissions() throws IOException {
        final String TOKEN = "token";
        final LoggedUser userMocked = new LoggedUser("ID", "ROLE");
        userMocked.setPermissions(Set.of("PERMISSION1", "PERMISSION2"));
        final Authentication authentication = new UsernamePasswordAuthenticationToken(TOKEN, null);

        given(tokenService.getLoggedUser(TOKEN)).willReturn(userMocked);

        final List<GrantedAuthority> authorities = userMocked.getPermissions().stream().map(p -> (GrantedAuthority) () -> "ROLE_" + p).collect(Collectors.toList());
        final Authentication authenticateExpected = new UsernamePasswordAuthenticationToken(userMocked, null, authorities);

        final Authentication authenticateResult = authenticationProvider.authenticate(authentication);

        assertNotSame(authentication, authenticateResult);
        assertNotSame(authenticateExpected, authenticateResult);
        assertSame(authenticateExpected.getPrincipal(), authenticateResult.getPrincipal());
        assertEquals(authenticateExpected.getAuthorities().size(), authenticateResult.getAuthorities().size());
        assertEquals(2, authenticateResult.getAuthorities().size());
        verify(tokenService, times(1)).getLoggedUser(TOKEN);
    }

    /**
     * Should return true
     */
    @Test
    public void supports() {
        final boolean result = authenticationProvider.supports(null);

        assertTrue(result);
    }
}