package ragde.repositories.implementations;

import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ragde.factories.PageFactory;
import ragde.models.Authentication;
import ragde.models.QAuthentication;
import ragde.pojos.pages.PageDataRequest;
import ragde.repositories.AuthenticationRepository;
import ragde.repositories.executor.QueryExecutor;

public class AuthenticationRepositoryImpl implements QueryExecutor<Authentication> {

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private PageFactory pageFactory;

    @Override
    @SuppressWarnings("unchecked")
    public Page<Authentication> page(PageDataRequest pageDataRequest) {
        if (DEFAULT_EXECUTOR == QuerydslPredicateExecutor.class || !(authenticationRepository instanceof JpaSpecificationExecutor)) {
            Predicate predicate = pageFactory.getPredicate(pageDataRequest.getFilters(), QAuthentication.authentication);
            if (predicate != null) {
                return authenticationRepository.findAll(predicate, pageFactory.pageRequest(pageDataRequest));
            } else {
                return authenticationRepository.findAll(pageFactory.pageRequest(pageDataRequest));
            }
        }

        return authenticationRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }
}