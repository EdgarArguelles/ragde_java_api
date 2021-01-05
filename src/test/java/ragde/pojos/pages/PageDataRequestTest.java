package ragde.pojos.pages;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ragde.integration_test.IntegrationTest;
import ragde.pojos.responses.error.nesteds.NestedError;
import ragde.pojos.responses.error.nesteds.ValidationNestedError;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class PageDataRequestTest {

    @Autowired
    private ObjectMapper mapper;

    /**
     * Should create default constructor
     */
    @Test
    public void constructorDefault() {
        final PageDataRequest pageDataRequest = new PageDataRequest();

        assertNull(pageDataRequest.getPage());
        assertNull(pageDataRequest.getSize());
        assertNull(pageDataRequest.getDirection());
        assertNull(pageDataRequest.getSort());
        assertNull(pageDataRequest.getFilters());
    }

    /**
     * Should create complete constructor
     */
    @Test
    public void constructorComplete() {
        final Integer PAGE = 1;
        final Integer SIZE = 2;
        final PageDataRequest.SORT_DIRECTION DIRECTION = PageDataRequest.SORT_DIRECTION.ASC;
        final List<String> SORT = List.of("S1", "S2");
        final List<FilterRequest> FILTERS = List.of(new FilterRequest("FR1", null, null), new FilterRequest("FR2", null, null));
        final PageDataRequest pageDataRequest = new PageDataRequest(PAGE, SIZE, DIRECTION, SORT, FILTERS);

        assertSame(PAGE, pageDataRequest.getPage());
        assertSame(SIZE, pageDataRequest.getSize());
        assertSame(DIRECTION, pageDataRequest.getDirection());
        assertSame(SORT, pageDataRequest.getSort());
        assertSame(FILTERS, pageDataRequest.getFilters());
    }

    /**
     * Should serialize and deserialize
     */
    @Test
    public void serialize() throws IOException {
        final Integer PAGE = 1;
        final Integer SIZE = 2;
        final PageDataRequest.SORT_DIRECTION DIRECTION = PageDataRequest.SORT_DIRECTION.ASC;
        final List<String> SORT = List.of("S1", "S2");
        final List<FilterRequest> FILTERS = List.of(new FilterRequest("FR1", "V1", FilterRequest.OPERATIONS.EQ), new FilterRequest("FR2", "V2", FilterRequest.OPERATIONS.ENDS_WITH));
        final PageDataRequest pageDataRequestExpected = new PageDataRequest(PAGE, SIZE, DIRECTION, SORT, FILTERS);

        final String json = mapper.writeValueAsString(pageDataRequestExpected);
        final PageDataRequest pageDataRequestResult = mapper.readValue(json, PageDataRequest.class);

        assertNotSame(pageDataRequestExpected, pageDataRequestResult);
        assertEquals(pageDataRequestExpected, pageDataRequestResult);
    }

    /**
     * Should ignore null value on json
     */
    @Test
    public void JsonNotIncludeNull() throws JsonProcessingException {
        final PageDataRequest pageDataRequest = new PageDataRequest(1, 2, null, null, null);
        final PageDataRequest pageDataRequestFull = new PageDataRequest(1, 2, PageDataRequest.SORT_DIRECTION.ASC, List.of("A"), List.of(new FilterRequest("F", "V", FilterRequest.OPERATIONS.EQ)));

        final String json = mapper.writeValueAsString(pageDataRequest);
        final String jsonFull = mapper.writeValueAsString(pageDataRequestFull);

        assertFalse(json.contains("direction"));
        assertFalse(json.contains("sort"));
        assertFalse(json.contains("filter"));
        assertTrue(jsonFull.contains("direction"));
        assertTrue(jsonFull.contains("sort"));
        assertTrue(jsonFull.contains("filter"));
    }

    /**
     * Should equals instances
     */
    @Test
    public void equalsInstance() {
        final PageDataRequest pageDataRequest = new PageDataRequest(1, 2, PageDataRequest.SORT_DIRECTION.ASC, null, null);

        assertTrue(pageDataRequest.equals(pageDataRequest));
        assertFalse(pageDataRequest.equals(null));
        assertFalse(pageDataRequest.equals(new String()));
    }

    /**
     * Should fail equals due page
     */
    @Test
    public void noEqualsPage() {
        final PageDataRequest pageDataRequest1 = new PageDataRequest(1, 2, PageDataRequest.SORT_DIRECTION.ASC, List.of("A", "B"), List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ), new FilterRequest("D", "E", FilterRequest.OPERATIONS.EQ)));
        final PageDataRequest pageDataRequest2 = new PageDataRequest(11, 2, PageDataRequest.SORT_DIRECTION.ASC, List.of("A", "B"), List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ), new FilterRequest("D", "E", FilterRequest.OPERATIONS.EQ)));
        final PageDataRequest pageDataRequestNull = new PageDataRequest(null, 2, PageDataRequest.SORT_DIRECTION.ASC, List.of("A", "B"), List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ), new FilterRequest("D", "E", FilterRequest.OPERATIONS.EQ)));

        assertNotEquals(pageDataRequest1, pageDataRequest2);
        assertNotEquals(pageDataRequest1, pageDataRequestNull);
        assertNotEquals(pageDataRequestNull, pageDataRequest1);
    }

    /**
     * Should fail equals due size
     */
    @Test
    public void noEqualsSize() {
        final PageDataRequest pageDataRequest1 = new PageDataRequest(1, 2, PageDataRequest.SORT_DIRECTION.ASC, List.of("A", "B"), List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ), new FilterRequest("D", "E", FilterRequest.OPERATIONS.EQ)));
        final PageDataRequest pageDataRequest2 = new PageDataRequest(1, 21, PageDataRequest.SORT_DIRECTION.ASC, List.of("A", "B"), List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ), new FilterRequest("D", "E", FilterRequest.OPERATIONS.EQ)));
        final PageDataRequest pageDataRequestNull = new PageDataRequest(1, null, PageDataRequest.SORT_DIRECTION.ASC, List.of("A", "B"), List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ), new FilterRequest("D", "E", FilterRequest.OPERATIONS.EQ)));

        assertNotEquals(pageDataRequest1, pageDataRequest2);
        assertNotEquals(pageDataRequest1, pageDataRequestNull);
        assertNotEquals(pageDataRequestNull, pageDataRequest1);
    }

    /**
     * Should fail equals due direction
     */
    @Test
    public void noEqualsDirection() {
        final PageDataRequest pageDataRequest1 = new PageDataRequest(1, 2, PageDataRequest.SORT_DIRECTION.ASC, List.of("A", "B"), List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ), new FilterRequest("D", "E", FilterRequest.OPERATIONS.EQ)));
        final PageDataRequest pageDataRequest2 = new PageDataRequest(1, 2, PageDataRequest.SORT_DIRECTION.DESC, List.of("A", "B"), List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ), new FilterRequest("D", "E", FilterRequest.OPERATIONS.EQ)));
        final PageDataRequest pageDataRequestNull = new PageDataRequest(1, 2, null, List.of("A", "B"), List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ), new FilterRequest("D", "E", FilterRequest.OPERATIONS.EQ)));

        assertNotEquals(pageDataRequest1, pageDataRequest2);
        assertNotEquals(pageDataRequest1, pageDataRequestNull);
        assertNotEquals(pageDataRequestNull, pageDataRequest1);
    }

    /**
     * Should fail equals due sort
     */
    @Test
    public void noEqualsSort() {
        final PageDataRequest pageDataRequest1 = new PageDataRequest(1, 2, PageDataRequest.SORT_DIRECTION.ASC, List.of("A", "B"), List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ), new FilterRequest("D", "E", FilterRequest.OPERATIONS.EQ)));
        final PageDataRequest pageDataRequest2 = new PageDataRequest(1, 2, PageDataRequest.SORT_DIRECTION.ASC, List.of("A"), List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ), new FilterRequest("D", "E", FilterRequest.OPERATIONS.EQ)));
        final PageDataRequest pageDataRequestNull = new PageDataRequest(1, 2, PageDataRequest.SORT_DIRECTION.ASC, null, List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ), new FilterRequest("D", "E", FilterRequest.OPERATIONS.EQ)));

        assertNotEquals(pageDataRequest1, pageDataRequest2);
        assertNotEquals(pageDataRequest1, pageDataRequestNull);
        assertNotEquals(pageDataRequestNull, pageDataRequest1);
    }

    /**
     * Should fail equals due filters
     */
    @Test
    public void noEqualsFilters() {
        final PageDataRequest pageDataRequest1 = new PageDataRequest(1, 2, PageDataRequest.SORT_DIRECTION.ASC, List.of("A", "B"), List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ), new FilterRequest("D", "E", FilterRequest.OPERATIONS.EQ)));
        final PageDataRequest pageDataRequest2 = new PageDataRequest(1, 2, PageDataRequest.SORT_DIRECTION.ASC, List.of("A", "B"), List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ)));
        final PageDataRequest pageDataRequestNull = new PageDataRequest(1, 2, PageDataRequest.SORT_DIRECTION.ASC, List.of("A", "B"), null);

        assertNotEquals(pageDataRequest1, pageDataRequest2);
        assertNotEquals(pageDataRequest1, pageDataRequestNull);
        assertNotEquals(pageDataRequestNull, pageDataRequest1);
    }

    /**
     * Should be equals
     */
    @Test
    public void testEquals() {
        final PageDataRequest pageDataRequest1 = new PageDataRequest(1, 2, PageDataRequest.SORT_DIRECTION.ASC, List.of("A", "B"), List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ), new FilterRequest("D", "E", FilterRequest.OPERATIONS.EQ)));
        final PageDataRequest pageDataRequest2 = new PageDataRequest(1, 2, PageDataRequest.SORT_DIRECTION.ASC, List.of("A", "B"), List.of(new FilterRequest("A", "B", FilterRequest.OPERATIONS.EQ), new FilterRequest("D", "E", FilterRequest.OPERATIONS.EQ)));
        final PageDataRequest pageDataRequestNull1 = new PageDataRequest();
        final PageDataRequest pageDataRequestNull2 = new PageDataRequest();

        assertNotSame(pageDataRequest1, pageDataRequest2);
        assertEquals(pageDataRequest1, pageDataRequest2);
        assertNotSame(pageDataRequestNull1, pageDataRequestNull2);
        assertEquals(pageDataRequestNull1, pageDataRequestNull2);
    }

    /**
     * Should get 2 errors when parameters null
     */
    @Test
    public void validateWhenNull() {
        final PageDataRequest p = new PageDataRequest();
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("page", "must not be null"),
                new ValidationNestedError("size", "must not be null")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(p);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 2 errors when parameters has Min errors
     */
    @Test
    public void validateWhenMin() {
        final PageDataRequest p = new PageDataRequest(-1, 0, PageDataRequest.SORT_DIRECTION.ASC, null, Collections.emptyList());
        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("page", "must be greater than or equal to 0"),
                new ValidationNestedError("size", "must be greater than or equal to 1")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(p);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 5 errors when FilterRequest has errors
     */
    @Test
    public void validateWhenFilterRequestError() {
        final StringBuffer longText = new StringBuffer();
        IntStream.range(0, 256).forEach(i -> longText.append("a"));
        final List<FilterRequest> filters = List.of(
                new FilterRequest("F", "V", FilterRequest.OPERATIONS.EQ),
                new FilterRequest(),
                new FilterRequest("", "", FilterRequest.OPERATIONS.EQ),
                new FilterRequest(longText.toString(), "", FilterRequest.OPERATIONS.EQ)
        );
        final PageDataRequest p = new PageDataRequest(0, 1, null, null, filters);

        final List<NestedError> nestedErrorsExpected = List.of(
                new ValidationNestedError("filters[1].field", "must not be null"),
                new ValidationNestedError("filters[1].operation", "must not be null"),
                new ValidationNestedError("filters[1].value", "must not be null"),
                new ValidationNestedError("filters[2].field", "size must be between 1 and 255"),
                new ValidationNestedError("filters[3].field", "size must be between 1 and 255")
        );
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(p);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }

    /**
     * Should get 0 error when correct
     */
    @Test
    public void validateWhenOK() {
        final PageDataRequest p = new PageDataRequest(0, 1, PageDataRequest.SORT_DIRECTION.ASC, null, Collections.emptyList());
        final List<NestedError> nestedErrorsExpected = Collections.emptyList();
        final List<NestedError> nestedErrorsResult = IntegrationTest.getValidationErrors(p);

        assertNotSame(nestedErrorsExpected, nestedErrorsResult);
        assertEquals(nestedErrorsExpected, nestedErrorsResult);
    }
}