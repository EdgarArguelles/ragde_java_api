package ragde.services.implementations;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ragde.models.AuthProvider;
import ragde.models.Authentication;
import ragde.repositories.AuthProviderRepository;
import ragde.repositories.AuthenticationRepository;
import ragde.security.pojos.LoggedUser;
import ragde.services.AuthProviderService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthProviderServiceImplTest {

    @Autowired
    private AuthProviderService authProviderService;

    @MockBean
    private AuthProviderRepository authProviderRepository;

    @MockBean
    private AuthenticationRepository authenticationRepository;

    @BeforeEach
    public void setup() {
        final LoggedUser user = new LoggedUser();
        user.setPermissions(Set.of("CREATE_USERS"));
        final List<GrantedAuthority> authorities = user.getPermissions().stream().map(p -> (GrantedAuthority) () -> "ROLE_" + p).collect(Collectors.toList());
        SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(user, null, authorities));
    }

    /**
     * Should call findAll function
     */
    @Test
    public void findAll() {
        final List<AuthProvider> authProvidersMocked = Arrays.asList(
                new AuthProvider("ID1"), new AuthProvider("ID2"), null, new AuthProvider("ID4"));
        given(authProviderRepository.findAll()).willReturn(authProvidersMocked);

        final List<AuthProvider> authProvidersExpected = Arrays.asList(
                new AuthProvider("ID1"), new AuthProvider("ID2"), null, new AuthProvider("ID4"));

        final List<AuthProvider> authProvidersResult = authProviderService.findAll();

        assertSame(authProvidersMocked, authProvidersResult);
        assertNotSame(authProvidersExpected, authProvidersResult);
        assertEquals(authProvidersExpected, authProvidersResult);
        verify(authProviderRepository, times(1)).findAll();
    }

    /**
     * Should call findByAuthProvider function
     */
    @Test
    public void getAuthenticationsWhenNull() {
        final AuthProvider authProvider = new AuthProvider();
        final List<Authentication> authenticationsMocked = List.of(new Authentication("A1"), new Authentication("A2"));
        given(authenticationRepository.findByAuthProvider(authProvider)).willReturn(authenticationsMocked);

        final List<Authentication> authenticationsExpected = List.of(new Authentication("A1"), new Authentication("A2"));

        final List<Authentication> authenticationsResult = authProviderService.getAuthentications(authProvider);

        assertSame(authenticationsMocked, authenticationsResult);
        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        verify(authenticationRepository, times(1)).findByAuthProvider(authProvider);
    }

    /**
     * Should not call findByAuthProvider function
     */
    @Test
    public void getAuthentications() {
        final AuthProvider authProvider = new AuthProvider();
        authProvider.setAuthentications(List.of(new Authentication("A1"), new Authentication("A2")));

        final List<Authentication> authenticationsExpected = List.of(new Authentication("A1"), new Authentication("A2"));

        final List<Authentication> authenticationsResult = authProviderService.getAuthentications(authProvider);

        assertNotSame(authenticationsExpected, authenticationsResult);
        assertEquals(authenticationsExpected, authenticationsResult);
        verify(authenticationRepository, never()).findByAuthProvider(authProvider);
    }
}