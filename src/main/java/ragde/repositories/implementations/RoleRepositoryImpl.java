package ragde.repositories.implementations;

import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ragde.factories.PageFactory;
import ragde.models.QRole;
import ragde.models.Role;
import ragde.pojos.pages.PageDataRequest;
import ragde.repositories.RoleRepository;
import ragde.repositories.executor.QueryExecutor;

public class RoleRepositoryImpl implements QueryExecutor<Role> {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PageFactory pageFactory;

    @Override
    @SuppressWarnings("unchecked")
    public Page<Role> page(PageDataRequest pageDataRequest) {
        if (DEFAULT_EXECUTOR == QuerydslPredicateExecutor.class || !(roleRepository instanceof JpaSpecificationExecutor)) {
            Predicate predicate = pageFactory.getPredicate(pageDataRequest.getFilters(), QRole.role);
            if (predicate != null) {
                return roleRepository.findAll(predicate, pageFactory.pageRequest(pageDataRequest));
            } else {
                return roleRepository.findAll(pageFactory.pageRequest(pageDataRequest));
            }
        }

        return roleRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }
}