package ragde.factories.implementations;

import com.querydsl.core.types.Predicate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ragde.factories.PageFactory;
import ragde.models.QPerson;
import ragde.pojos.pages.FilterRequest;
import ragde.pojos.pages.PageDataRequest;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PageFactoryImplTest {

    @Autowired
    private PageFactory pageFactory;

    /**
     * Should use default sort when sort list is null
     */
    @Test
    public void pageRequestWhenSortListNull() {
        final Integer PAGE = 2;
        final Integer SIZE = 5;
        final List<String> SORT = null;
        final PageDataRequest pageDataRequest = new PageDataRequest(PAGE, SIZE, PageDataRequest.SORT_DIRECTION.DESC, SORT, null);
        final PageRequest pageRequestExpected = PageRequest.of(PAGE, SIZE, Sort.by(Sort.Direction.ASC, "id"));

        final PageRequest pageRequestResult = pageFactory.pageRequest(pageDataRequest);

        assertNotSame(pageRequestExpected, pageRequestResult);
        assertEquals(pageRequestExpected, pageRequestResult);
    }

    /**
     * Should use default sort when sort list is empty
     */
    @Test
    public void pageRequestWhenSortListEmpty() {
        final Integer PAGE = 2;
        final Integer SIZE = 5;
        final List<String> SORT = Collections.emptyList();
        final PageDataRequest pageDataRequest = new PageDataRequest(PAGE, SIZE, PageDataRequest.SORT_DIRECTION.DESC, SORT, null);
        final PageRequest pageRequestExpected = PageRequest.of(PAGE, SIZE, Sort.by(Sort.Direction.ASC, "id"));

        final PageRequest pageRequestResult = pageFactory.pageRequest(pageDataRequest);

        assertNotSame(pageRequestExpected, pageRequestResult);
        assertEquals(pageRequestExpected, pageRequestResult);
    }

    /**
     * Should use sort when sort list is present
     */
    @Test
    public void pageRequestWhenSortList() {
        final Integer PAGE = 2;
        final Integer SIZE = 5;
        final List<String> SORT = List.of("sort1", "sort2");
        final PageDataRequest pageDataRequest = new PageDataRequest(PAGE, SIZE, PageDataRequest.SORT_DIRECTION.DESC, SORT, null);
        final PageRequest pageRequestExpected = PageRequest.of(PAGE, SIZE, Sort.by(Sort.Direction.DESC, SORT.toArray(new String[0])));

        final PageRequest pageRequestResult = pageFactory.pageRequest(pageDataRequest);

        assertNotSame(pageRequestExpected, pageRequestResult);
        assertEquals(pageRequestExpected, pageRequestResult);
    }

    /**
     * Should return null when filtersRequest is null
     */
    @Test
    public void getSpecificationsNullWhenFiltersNull() {
        final Specification specificationsResult = pageFactory.getSpecifications(null);

        assertNull(specificationsResult);
    }

    /**
     * Should return null when filtersRequest is empty
     */
    @Test
    public void getSpecificationsNullWhenFiltersEmpty() {
        final Specification specificationsResult = pageFactory.getSpecifications(Collections.emptyList());

        assertNull(specificationsResult);
    }

    /**
     * Should get Specifications when success
     */
    @Test
    public void getSpecifications() {
        final List<FilterRequest> filtersRequest = List.of(
                new FilterRequest("field1", "value1", FilterRequest.OPERATIONS.EQ),
                new FilterRequest("field2", "2002-04-20T12:30:52Z", FilterRequest.OPERATIONS.EQ),
                new FilterRequest("field3", "2010-11-23", FilterRequest.OPERATIONS.NE)
        );

        final Specification specifications = pageFactory.getSpecifications(filtersRequest);

        assertNotNull(specifications);
    }

    /**
     * Should get default Predicate when filtersRequest is null
     */
    @Test
    public void getPredicateNullWhenFiltersNull() {
        final String predicateExpected = "true = true";
        final Predicate predicate = pageFactory.getPredicate(null, QPerson.person);

        assertEquals(predicateExpected, predicate.toString());
    }

    /**
     * Should get default Predicate when filtersRequest is empty
     */
    @Test
    public void getPredicateNullWhenFiltersEmpty() {
        final String predicateExpected = "true = true";
        final Predicate predicate = pageFactory.getPredicate(Collections.emptyList(), QPerson.person);

        assertEquals(predicateExpected, predicate.toString());
    }

    /**
     * Should get Predicate when success
     */
    @Test
    public void getPredicate() {
        final String predicateExpected = "person.name = value1 && person.createdAt = 2002-04-20T12:30:52 && person.birthday != 2010-11-23";
        final List<FilterRequest> filtersRequest = List.of(
                new FilterRequest("name", "value1", FilterRequest.OPERATIONS.EQ),
                new FilterRequest("createdAt", "2002-04-20T12:30:52Z", FilterRequest.OPERATIONS.EQ),
                new FilterRequest("birthday", "2010-11-23", FilterRequest.OPERATIONS.NE)
        );

        final Predicate predicate = pageFactory.getPredicate(filtersRequest, QPerson.person);

        assertEquals(predicateExpected, predicate.toString());
    }
}