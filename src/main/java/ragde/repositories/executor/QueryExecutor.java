package ragde.repositories.executor;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import ragde.pojos.pages.PageDataRequest;

public interface QueryExecutor<T> {

    /**
     * Determine the Executor that will be used, it could be JpaSpecificationExecutor or QueryDslPredicateExecutor
     */
    Class DEFAULT_EXECUTOR = JpaSpecificationExecutor.class;

    /**
     * Retrieves all requested entities.
     *
     * @param pageDataRequest Page data.
     * @return list of entities with metadata.
     */
    Page<T> page(PageDataRequest pageDataRequest);
}