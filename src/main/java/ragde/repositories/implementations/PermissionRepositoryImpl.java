package ragde.repositories.implementations;

import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ragde.factories.PageFactory;
import ragde.models.Permission;
import ragde.models.QPermission;
import ragde.pojos.pages.PageDataRequest;
import ragde.repositories.PermissionRepository;
import ragde.repositories.executor.QueryExecutor;

public class PermissionRepositoryImpl implements QueryExecutor<Permission> {

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PageFactory pageFactory;

    @Override
    @SuppressWarnings("unchecked")
    public Page<Permission> page(PageDataRequest pageDataRequest) {
        if (DEFAULT_EXECUTOR == QuerydslPredicateExecutor.class || !(permissionRepository instanceof JpaSpecificationExecutor)) {
            Predicate predicate = pageFactory.getPredicate(pageDataRequest.getFilters(), QPermission.permission);
            if (predicate != null) {
                return permissionRepository.findAll(predicate, pageFactory.pageRequest(pageDataRequest));
            } else {
                return permissionRepository.findAll(pageFactory.pageRequest(pageDataRequest));
            }
        }

        return permissionRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }
}