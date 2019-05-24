package ragde.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.TokenService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Custom AuthenticationProvider
 */
@Component
public class AuthenticationProviderImpl implements AuthenticationProvider {

    @Autowired
    private TokenService tokenService;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        try {
            String token = (String) authentication.getPrincipal();
            LoggedUser user = tokenService.getLoggedUser(token);
            user.setPermissions(user.getPermissions() != null ? user.getPermissions() : Collections.emptySet());
            List<GrantedAuthority> authorities = user.getPermissions().stream().map(p -> (GrantedAuthority) () -> "ROLE_" + p).collect(Collectors.toList());
            return new UsernamePasswordAuthenticationToken(user, null, authorities);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return true;
    }
}