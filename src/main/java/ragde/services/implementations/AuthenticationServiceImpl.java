package ragde.services.implementations;

import io.leangen.graphql.annotations.*;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ragde.exceptions.RagdeDontFoundException;
import ragde.exceptions.RagdeValidationException;
import ragde.models.AuthProvider;
import ragde.models.Authentication;
import ragde.models.Person;
import ragde.pojos.pages.PageDataRequest;
import ragde.repositories.AuthProviderRepository;
import ragde.repositories.AuthenticationRepository;
import ragde.security.services.SecurityService;
import ragde.services.AuthenticationService;

import java.util.List;

@GraphQLApi
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private AuthProviderRepository authProviderRepository;

    @Autowired
    private SecurityService securityService;

    @Override
    @PreAuthorize("hasRole('VIEW_USERS')")
    @GraphQLQuery(name = "authentications", description = "Find all authentications")
    public List<Authentication> findAll() {
        return authenticationRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('VIEW_USERS')")
    @GraphQLQuery(name = "authentication", description = "Find an authentication by ID")
    public Authentication findById(@GraphQLId @GraphQLNonNull @GraphQLArgument(name = "id", description = "Authentication's ID") String id) {
        return authenticationRepository.findById(id).orElseThrow(() -> new RagdeDontFoundException("Data don't found."));
    }

    @Override
    @PreAuthorize("hasRole('VIEW_USERS')")
    @GraphQLQuery(name = "authenticationByUsername", description = "Find an authentication by Username")
    public Authentication findByUsername(@GraphQLNonNull @GraphQLArgument(name = "username", description = "Authentication's Username") String username) {
        return authenticationRepository.findByUsername(username);
    }

    @Override
    @PreAuthorize("hasRole('VIEW_USERS')")
    @GraphQLQuery(name = "authenticationByAuthProviderAndPerson", description = "Find an authentication by AuthProvider and Person")
    public Authentication findByAuthProviderAndPerson(@GraphQLNonNull @GraphQLArgument(name = "authProvider", description = "Authentication's Provider") AuthProvider authProvider,
                                                      @GraphQLNonNull @GraphQLArgument(name = "person", description = "Authentication's Person") Person person) {
        return authenticationRepository.findByAuthProviderAndPerson(authProvider, person);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('CREATE_USERS')")
    @GraphQLMutation(name = "createAuthentication", description = "Create a new authentication")
    public Authentication save(@GraphQLNonNull @GraphQLArgument(name = "authentication", description = "New authentication") Authentication authentication) {
        if (authentication.getPassword() == null) {
            throw new RagdeValidationException("Password can't not be null.");
        }

        if (findByUsername(authentication.getUsername()) != null) {
            throw new RagdeValidationException("Username '" + authentication.getUsername() + "' is already used by another user.");
        }

        // this service only can save with "LOCAL" Provider
        authentication.setAuthProvider(authProviderRepository.findByName("LOCAL"));
        Authentication duplicated = findByAuthProviderAndPerson(authentication.getAuthProvider(), authentication.getPerson());
        if (duplicated != null) {
            throw new RagdeValidationException("'" + duplicated.getPerson().getFullName() + "' already has an Authorization with provider '" + duplicated.getAuthProvider().getName() + "'.");
        }

        authentication.setPassword(securityService.hashValue(authentication.getPassword()));
        return authenticationRepository.save(authentication);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('CREATE_USERS')")
    @GraphQLMutation(name = "updateAuthentication", description = "Update an authentication")
    public Authentication update(@GraphQLNonNull @GraphQLArgument(name = "authentication", description = "Authentication's new values") Authentication authentication) {
        if (authentication.getPassword() == null) {
            throw new RagdeValidationException("Password can't not be null.");
        }

        Authentication original = findById(authentication.getId());
        original.setPassword(securityService.hashValue(authentication.getPassword()));
        return authenticationRepository.save(original);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('REMOVE_USERS')")
    @GraphQLMutation(name = "deleteAuthentication", description = "Delete an authentication")
    public Authentication delete(@GraphQLId @GraphQLNonNull @GraphQLArgument(name = "id", description = "Authentication's ID") String id) {
        Authentication authentication = findById(id);
        authenticationRepository.delete(authentication);
        return authentication;
    }

    @Override
    @PreAuthorize("hasRole('VIEW_USERS')")
    @GraphQLQuery(name = "authenticationPage", description = "Page all authentications")
    public Page<Authentication> page(@GraphQLNonNull @GraphQLArgument(name = "pageDataRequest", description = "Filter, limit and sort data") PageDataRequest pageDataRequest) {
        return authenticationRepository.page(pageDataRequest);
    }
}