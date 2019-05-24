package ragde.services.implementations;

import io.leangen.graphql.annotations.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import ragde.models.AuthProvider;
import ragde.models.Authentication;
import ragde.repositories.AuthProviderRepository;
import ragde.repositories.AuthenticationRepository;
import ragde.services.AuthProviderService;

import java.util.List;

@GraphQLApi
@Service
public class AuthProviderServiceImpl implements AuthProviderService {

    @Autowired
    private AuthProviderRepository authProviderRepository;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Override
    @PreAuthorize("hasRole('CREATE_USERS')")
    @GraphQLQuery(name = "authProviders", description = "Find all authentication providers")
    public List<AuthProvider> findAll() {
        return authProviderRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('CREATE_USERS')")
    @GraphQLQuery(name = "authentications", description = "Authentications where this Provider is present")
    public List<Authentication> getAuthentications(@GraphQLContext AuthProvider authProvider) {
        if (authProvider.getAuthentications() == null) {
            authProvider.setAuthentications(authenticationRepository.findByAuthProvider(authProvider));
        }

        return authProvider.getAuthentications();
    }
}