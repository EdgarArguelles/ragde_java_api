package ragde.security.services.implementations;

import io.leangen.graphql.annotations.GraphQLArgument;
import io.leangen.graphql.annotations.GraphQLNonNull;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import ragde.exceptions.RagdeDontFoundException;
import ragde.exceptions.RagdeValidationException;
import ragde.models.Person;
import ragde.repositories.AuthenticationRepository;
import ragde.repositories.PersonRepository;
import ragde.security.factories.LoggedUserFactory;
import ragde.security.pojos.AccountCredentials;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.SecurityService;
import ragde.security.services.TokenService;

import java.io.IOException;

@GraphQLApi
@Service
public class SecurityServiceImpl implements SecurityService {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private LoggedUserFactory loggedUserFactory;

    @Autowired
    private TokenService tokenService;

    @Override
    @GraphQLQuery(name = "login", description = "Login with valid credentials")
    public LoggedUser authenticate(@GraphQLNonNull @GraphQLArgument(name = "credentials", description = "Authentication's data") AccountCredentials credentials) throws IOException {
        ragde.models.Authentication authentication = authenticationRepository.findByUsername(credentials.getUsername());
        if (authentication == null) {
            throw new RagdeDontFoundException("Credentials incorrect.");
        }

        String password = hashValue(credentials.getPassword());
        if (!authentication.getPassword().equals(password)) {
            throw new RagdeDontFoundException("Credentials incorrect.");
        }

        LoggedUser loggedUser = loggedUserFactory.loggedUser(authentication.getPerson());
        String token = tokenService.createToken(loggedUser);
        loggedUser = tokenService.getLoggedUser(token);
        loggedUser.setToken(token);

        return loggedUser;
    }

    @Override
    @GraphQLQuery(name = "changeRole", description = "Switch to another user's role")
    public LoggedUser changeRole(@GraphQLNonNull @GraphQLArgument(name = "roleId", description = "New Role's ID") String roleId) throws IOException {
        LoggedUser loggedUser = getLoggedUser();
        if (loggedUser == null) {
            throw new RagdeValidationException("There isn't any logged user.");
        }

        Person person = personRepository.findById(loggedUser.getId()).orElse(null);
        loggedUser = loggedUserFactory.loggedUser(person, roleId);
        String token = tokenService.createToken(loggedUser);
        loggedUser = tokenService.getLoggedUser(token);
        loggedUser.setToken(token);

        return loggedUser;
    }

    @Override
    @GraphQLQuery(name = "ping", description = "Ping with server to refresh token")
    public LoggedUser getLoggedUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            LoggedUser loggedUser = (LoggedUser) authentication.getPrincipal();
            String newToken = tokenService.createToken(loggedUser);
            loggedUser.setToken(newToken);
            return loggedUser;
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String hashValue(String value) {
        return DigestUtils.sha512Hex(value);
    }
}