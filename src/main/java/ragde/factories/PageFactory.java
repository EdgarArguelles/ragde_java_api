package ragde.factories;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.EntityPathBase;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import ragde.pojos.pages.FilterRequest;
import ragde.pojos.pages.PageDataRequest;

import java.util.List;

/**
 * Create Page instances
 */
public interface PageFactory {

    /**
     * Create a PageRequest from a PageDataRequest
     *
     * @param pageDataRequest PageDataRequest data
     * @return PageRequest created
     */
    PageRequest pageRequest(PageDataRequest pageDataRequest);

    /**
     * Create a Specifications instance from a FilterRequest list
     *
     * @param filtersRequest list of FilterRequest data
     * @return Specifications created
     */
    Specification getSpecifications(List<FilterRequest> filtersRequest);

    /**
     * Create a Predicate instance from a FilterRequest list
     *
     * @param filtersRequest list of FilterRequest data
     * @param entityPathBase QEntity base to generate Predicate
     * @return Predicate created
     */
    Predicate getPredicate(List<FilterRequest> filtersRequest, EntityPathBase entityPathBase);
}