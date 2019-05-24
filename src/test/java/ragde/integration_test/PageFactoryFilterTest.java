package ragde.integration_test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;
import ragde.exceptions.RagdeValidationException;
import ragde.factories.PageFactory;
import ragde.models.Person;
import ragde.models.QPerson;
import ragde.pojos.pages.FilterRequest;
import ragde.pojos.pages.PageDataRequest;
import ragde.repositories.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(SpringRunner.class)
@SpringBootTest
@SuppressWarnings("unchecked")
public class PageFactoryFilterTest {

    private final String TEST_BIRTHDAY1 = "1986-04-08";

    private final String TEST_BIRTHDAY2 = "1986-04-09";

    private final String TEST_BIRTHDAY3 = "1987-02-02";

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Autowired
    private AuthProviderRepository authProviderRepository;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Autowired
    private PageFactory pageFactory;

    private PageDataRequest pageDataRequest;

    private LocalDate dateTime1;

    private LocalDate dateTime2;

    private LocalDate dateTime3;

    @Before
    public void setup() {
        pageDataRequest = new PageDataRequest(0, 100, PageDataRequest.SORT_DIRECTION.ASC, List.of("lastName", "civilStatus"), new ArrayList<>());
        IntegrationTest.cleanAllData(authenticationRepository, authProviderRepository, personRepository, roleRepository, permissionRepository);

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dateTime1 = LocalDate.parse(TEST_BIRTHDAY1, formatter);
        dateTime2 = LocalDate.parse(TEST_BIRTHDAY2, formatter);
        dateTime3 = LocalDate.parse(TEST_BIRTHDAY3, formatter);

        final List<Person> people = List.of(
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null),
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("5", "alast name 2", dateTime3, 5, Person.SEX.F, "a2@a.com", null),
                new Person("12", "last name 3", dateTime1, 12, Person.SEX.M, "a3@a.com", null)
        );
        personRepository.saveAll(people);
    }

    /**
     * Should get the second element with page 1 using Specifications and Predicate
     */
    @Test
    public void pageAndSizePage1() {
        pageDataRequest = new PageDataRequest(1, 1, PageDataRequest.SORT_DIRECTION.ASC, List.of("lastName", "civilStatus"), new ArrayList<>());

        final List<Person> peopleExpected = List.of(new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null));
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);
    }

    /**
     * Should get the third element with page 2 using Specifications and Predicate
     */
    @Test
    public void pageAndSizePage2() {
        pageDataRequest = new PageDataRequest(2, 1, PageDataRequest.SORT_DIRECTION.ASC, List.of("lastName", "civilStatus"), new ArrayList<>());

        final List<Person> peopleExpected = List.of(new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null));
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);
    }

    /**
     * Should get empty list with page 100 using Specifications and Predicate
     */
    @Test
    public void pageAndSizePage100() {
        pageDataRequest = new PageDataRequest(100, 1, PageDataRequest.SORT_DIRECTION.ASC, List.of("lastName", "civilStatus"), new ArrayList<>());

        final List<Person> peopleExpected = Collections.emptyList();

        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);
    }

    /**
     * Should get all elements without filter using Specifications and Predicate
     */
    @Test
    public void notFilter() {
        final List<Person> peopleExpected = List.of(
                new Person("5", "alast name 2", dateTime3, 5, Person.SEX.F, "a2@a.com", null),
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null),
                new Person("12", "last name 3", dateTime1, 12, Person.SEX.M, "a3@a.com", null)
        );

        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);
    }

    /**
     * Should throw InvalidDataAccessApiUsageException when field is invalid using Specifications
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void invalidFieldSpecifications() {
        pageDataRequest.getFilters().add(new FilterRequest("invalid", "5", FilterRequest.OPERATIONS.EQ));

        personRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw RagdeValidationException when field is invalid using Predicate
     */
    @Test(expected = RagdeValidationException.class)
    public void invalidFieldPredicate() {
        pageDataRequest.getFilters().add(new FilterRequest("invalid", "5", FilterRequest.OPERATIONS.EQ));

        personRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw InvalidDataAccessApiUsageException when Number is invalid using Specifications
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void invalidNumberSpecifications() {
        pageDataRequest.getFilters().add(new FilterRequest("civilStatus", "ABC", FilterRequest.OPERATIONS.EQ));

        personRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw RagdeValidationException when Number is invalid using Predicate
     */
    @Test(expected = RagdeValidationException.class)
    public void invalidNumberPredicate() {
        pageDataRequest.getFilters().add(new FilterRequest("civilStatus", "ABC", FilterRequest.OPERATIONS.EQ));

        personRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw InvalidDataAccessApiUsageException when LocalDate is invalid using Specifications
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void invalidLocalDateSpecifications() {
        pageDataRequest.getFilters().add(new FilterRequest("birthday", "ABC", FilterRequest.OPERATIONS.EQ));

        personRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw RagdeValidationException when LocalDate is invalid using Predicate
     */
    @Test(expected = RagdeValidationException.class)
    public void invalidLocalDatePredicate() {
        pageDataRequest.getFilters().add(new FilterRequest("birthday", "ABC", FilterRequest.OPERATIONS.EQ));

        personRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw InvalidDataAccessApiUsageException when LocalDateTime is invalid using Specifications
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void invalidLocalDateTimeSpecifications() {
        pageDataRequest.getFilters().add(new FilterRequest("createdAt", "ABC", FilterRequest.OPERATIONS.EQ));

        personRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw RagdeValidationException when LocalDateTime is invalid using Predicate
     */
    @Test(expected = RagdeValidationException.class)
    public void invalidLocalDateTimePredicate() {
        pageDataRequest.getFilters().add(new FilterRequest("createdAt", "ABC", FilterRequest.OPERATIONS.EQ));

        personRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should evaluate EQ filter with String, number, Date and DateTime using Specifications and Predicate
     */
    @Test
    public void eqFilter() {
        // only EQ test uses separately Specifications and Predicate to validate EQ is used as default when operation ins null or invalid
        pageDataRequest.getFilters().add(new FilterRequest("name", "3", FilterRequest.OPERATIONS.EQ));
        List<Person> peopleExpected = List.of(
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("birthday", TEST_BIRTHDAY2, FilterRequest.OPERATIONS.EQ));
        peopleExpected = List.of(
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("civilStatus", "6", FilterRequest.OPERATIONS.EQ));
        peopleExpected = List.of(new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null));
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("createdAt", "2000-10-25T19:23:55Z", FilterRequest.OPERATIONS.EQ));
        testSpecifications(Collections.emptyList());
        testPredicate(Collections.emptyList());
    }

    /**
     * Should evaluate NE filter with String, number, Date and DateTime using Specifications and Predicate
     */
    @Test
    public void neFilter() {
        pageDataRequest.getFilters().add(new FilterRequest("name", "12", FilterRequest.OPERATIONS.NE));
        List<Person> peopleExpected = List.of(
                new Person("5", "alast name 2", dateTime3, 5, Person.SEX.F, "a2@a.com", null),
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("birthday", TEST_BIRTHDAY3, FilterRequest.OPERATIONS.NE));
        peopleExpected = List.of(
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("civilStatus", "6", FilterRequest.OPERATIONS.NE));
        peopleExpected = List.of(
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("createdAt", "2000-10-25T19:23:55Z", FilterRequest.OPERATIONS.NE));
        peopleExpected = List.of(
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);
    }

    /**
     * Should evaluate GT filter with String, number, Date and DateTime using Specifications and Predicate
     */
    @Test
    public void gtFilter() {
        pageDataRequest.getFilters().add(new FilterRequest("name", "12", FilterRequest.OPERATIONS.GT));
        List<Person> peopleExpected = List.of(
                new Person("5", "alast name 2", dateTime3, 5, Person.SEX.F, "a2@a.com", null),
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("birthday", TEST_BIRTHDAY1, FilterRequest.OPERATIONS.GT));
        peopleExpected = List.of(
                new Person("5", "alast name 2", dateTime3, 5, Person.SEX.F, "a2@a.com", null),
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("civilStatus", "5", FilterRequest.OPERATIONS.GT));
        peopleExpected = List.of(
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("createdAt", "2000-10-25T19:23:55Z", FilterRequest.OPERATIONS.GT));
        peopleExpected = List.of(
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);
    }

    /**
     * Should evaluate GET filter with String, number, Date and DateTime using Specifications and Predicate
     */
    @Test
    public void getFilter() {
        pageDataRequest.getFilters().add(new FilterRequest("name", "3", FilterRequest.OPERATIONS.GET));
        List<Person> peopleExpected = List.of(
                new Person("5", "alast name 2", dateTime3, 5, Person.SEX.F, "a2@a.com", null),
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("birthday", TEST_BIRTHDAY2, FilterRequest.OPERATIONS.GET));
        peopleExpected = List.of(
                new Person("5", "alast name 2", dateTime3, 5, Person.SEX.F, "a2@a.com", null),
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("civilStatus", "6", FilterRequest.OPERATIONS.GET));
        peopleExpected = List.of(
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("createdAt", "2000-10-25T19:23:55Z", FilterRequest.OPERATIONS.GET));
        peopleExpected = List.of(
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);
    }

    /**
     * Should evaluate LT filter with String, number, Date and DateTime using Specifications and Predicate
     */
    @Test
    public void ltFilter() {
        pageDataRequest.getFilters().add(new FilterRequest("name", "5", FilterRequest.OPERATIONS.LT));
        List<Person> peopleExpected = List.of(
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null),
                new Person("12", "last name 3", dateTime1, 12, Person.SEX.M, "a3@a.com", null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("birthday", TEST_BIRTHDAY2, FilterRequest.OPERATIONS.LT));
        peopleExpected = List.of(
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("12", "last name 3", dateTime1, 12, Person.SEX.M, "a3@a.com", null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("civilStatus", "12", FilterRequest.OPERATIONS.LT));
        peopleExpected = List.of(
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("createdAt", "3000-10-25T19:23:55Z", FilterRequest.OPERATIONS.LT));
        peopleExpected = List.of(
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);
    }

    /**
     * Should evaluate LET filter with String, number, Date and DateTime using Specifications and Predicate
     */
    @Test
    public void letFilter() {
        pageDataRequest.getFilters().add(new FilterRequest("name", "3", FilterRequest.OPERATIONS.LET));
        List<Person> peopleExpected = List.of(
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null),
                new Person("12", "last name 3", dateTime1, 12, Person.SEX.M, "a3@a.com", null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("birthday", TEST_BIRTHDAY1, FilterRequest.OPERATIONS.LET));
        peopleExpected = List.of(
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("12", "last name 3", dateTime1, 12, Person.SEX.M, "a3@a.com", null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("civilStatus", "3", FilterRequest.OPERATIONS.LET));
        peopleExpected = List.of(new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null));
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);

        pageDataRequest.getFilters().add(new FilterRequest("createdAt", "3000-10-25T19:23:55Z", FilterRequest.OPERATIONS.LET));
        peopleExpected = List.of(new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null));
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);
    }

    /**
     * Should evaluate LIKE filter with String using Specifications and Predicate
     */
    @Test
    public void likeFilter() {
        pageDataRequest.getFilters().add(new FilterRequest("lastName", "ame 1", FilterRequest.OPERATIONS.LIKE));
        List<Person> peopleExpected = List.of(
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);
    }

    /**
     * Should throw InvalidDataAccessApiUsageException when try LIKE with Number using Specifications
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void likeFilterNumberSpecifications() {
        pageDataRequest.getFilters().add(new FilterRequest("civilStatus", "1", FilterRequest.OPERATIONS.LIKE));

        personRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw RagdeValidationException when try LIKE with Number using Predicate
     */
    @Test(expected = RagdeValidationException.class)
    public void likeFilterNumberPredicate() {
        pageDataRequest.getFilters().add(new FilterRequest("civilStatus", "1", FilterRequest.OPERATIONS.LIKE));

        personRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw InvalidDataAccessApiUsageException when try LIKE with LocalDate using Specifications
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void likeFilterLocalDateSpecifications() {
        pageDataRequest.getFilters().add(new FilterRequest("birthday", TEST_BIRTHDAY1, FilterRequest.OPERATIONS.LIKE));

        personRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw RagdeValidationException when try LIKE with LocalDate using Predicate
     */
    @Test(expected = RagdeValidationException.class)
    public void likeFilterLocalDatePredicate() {
        pageDataRequest.getFilters().add(new FilterRequest("birthday", TEST_BIRTHDAY1, FilterRequest.OPERATIONS.LIKE));

        personRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw InvalidDataAccessApiUsageException when try LIKE with LocalDateTime using Specifications
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void likeFilterLocalDateTimeSpecifications() {
        pageDataRequest.getFilters().add(new FilterRequest("createdAt", "2000-10-25T19:23:55Z", FilterRequest.OPERATIONS.LIKE));

        personRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw RagdeValidationException when try LIKE with LocalDateTime using Predicate
     */
    @Test(expected = RagdeValidationException.class)
    public void likeFilterLocalDateTimePredicate() {
        pageDataRequest.getFilters().add(new FilterRequest("createdAt", "2000-10-25T19:23:55Z", FilterRequest.OPERATIONS.LIKE));

        personRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should evaluate STARTS_WITH filter with String using Specifications and Predicate
     */
    @Test
    public void startsWithFilter() {
        pageDataRequest.getFilters().add(new FilterRequest("lastName", "last n", FilterRequest.OPERATIONS.STARTS_WITH));
        List<Person> peopleExpected = List.of(
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null),
                new Person("3", "last name 11", dateTime2, 12, Person.SEX.M, null, null),
                new Person("12", "last name 3", dateTime1, 12, Person.SEX.M, "a3@a.com", null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);
    }

    /**
     * Should throw InvalidDataAccessApiUsageException when try STARTS_WITH with Number using Specifications
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void startsWithFilterNumberSpecifications() {
        pageDataRequest.getFilters().add(new FilterRequest("civilStatus", "1", FilterRequest.OPERATIONS.STARTS_WITH));

        personRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw RagdeValidationException when try STARTS_WITH with Number using Predicate
     */
    @Test(expected = RagdeValidationException.class)
    public void startsWithFilterNumberPredicate() {
        pageDataRequest.getFilters().add(new FilterRequest("civilStatus", "1", FilterRequest.OPERATIONS.STARTS_WITH));

        personRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw InvalidDataAccessApiUsageException when try STARTS_WITH with LocalDate using Specifications
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void startsWithFilterLocalDateSpecifications() {
        pageDataRequest.getFilters().add(new FilterRequest("birthday", TEST_BIRTHDAY1, FilterRequest.OPERATIONS.STARTS_WITH));

        personRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw RagdeValidationException when try STARTS_WITH with LocalDate using Predicate
     */
    @Test(expected = RagdeValidationException.class)
    public void startsWithFilterLocalDatePredicate() {
        pageDataRequest.getFilters().add(new FilterRequest("birthday", TEST_BIRTHDAY1, FilterRequest.OPERATIONS.STARTS_WITH));

        personRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw InvalidDataAccessApiUsageException when try STARTS_WITH with LocalDateTime using Specifications
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void startsWithFilterLocalDateTimeSpecifications() {
        pageDataRequest.getFilters().add(new FilterRequest("createdAt", "2000-10-25T19:23:55Z", FilterRequest.OPERATIONS.STARTS_WITH));

        personRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw RagdeValidationException when try STARTS_WITH with LocalDateTime using Predicate
     */
    @Test(expected = RagdeValidationException.class)
    public void startsWithFilterLocalDateTimePredicate() {
        pageDataRequest.getFilters().add(new FilterRequest("createdAt", "2000-10-25T19:23:55Z", FilterRequest.OPERATIONS.STARTS_WITH));

        personRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should evaluate ENDS_WITH filter with String using Specifications and Predicate
     */
    @Test
    public void endsWithFilter() {
        pageDataRequest.getFilters().add(new FilterRequest("lastName", "ame 1", FilterRequest.OPERATIONS.ENDS_WITH));
        List<Person> peopleExpected = List.of(
                new Person("3", "last name 1", dateTime1, 3, Person.SEX.M, null, null),
                new Person("3", "last name 1", dateTime2, 6, Person.SEX.M, null, null)
        );
        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);
    }

    /**
     * Should throw InvalidDataAccessApiUsageException when try ENDS_WITH with Number using Specifications
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void endsWithFilterNumberSpecifications() {
        pageDataRequest.getFilters().add(new FilterRequest("civilStatus", "1", FilterRequest.OPERATIONS.ENDS_WITH));

        personRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw RagdeValidationException when try ENDS_WITH with Number using Predicate
     */
    @Test(expected = RagdeValidationException.class)
    public void endsWithFilterNumberPredicate() {
        pageDataRequest.getFilters().add(new FilterRequest("civilStatus", "1", FilterRequest.OPERATIONS.ENDS_WITH));

        personRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw InvalidDataAccessApiUsageException when try ENDS_WITH with LocalDate using Specifications
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void endsWithFilterLocalDateSpecifications() {
        pageDataRequest.getFilters().add(new FilterRequest("birthday", TEST_BIRTHDAY1, FilterRequest.OPERATIONS.ENDS_WITH));

        personRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw RagdeValidationException when try ENDS_WITH with LocalDate using Predicate
     */
    @Test(expected = RagdeValidationException.class)
    public void endsWithFilterLocalDatePredicate() {
        pageDataRequest.getFilters().add(new FilterRequest("birthday", TEST_BIRTHDAY1, FilterRequest.OPERATIONS.ENDS_WITH));

        personRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw InvalidDataAccessApiUsageException when try ENDS_WITH with LocalDateTime using Specifications
     */
    @Test(expected = InvalidDataAccessApiUsageException.class)
    public void endsWithFilterLocalDateTimeSpecifications() {
        pageDataRequest.getFilters().add(new FilterRequest("createdAt", "2000-10-25T19:23:55Z", FilterRequest.OPERATIONS.ENDS_WITH));

        personRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * Should throw RagdeValidationException when try ENDS_WITH with LocalDateTime using Predicate
     */
    @Test(expected = RagdeValidationException.class)
    public void endsWithFilterLocalDateTimePredicate() {
        pageDataRequest.getFilters().add(new FilterRequest("createdAt", "2000-10-25T19:23:55Z", FilterRequest.OPERATIONS.ENDS_WITH));

        personRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person), pageFactory.pageRequest(pageDataRequest));
    }

    /**
     * test Specifications
     *
     * @param peopleExpected expected list
     */
    private void testSpecifications(List<Person> peopleExpected) {
        final Page<Person> page = personRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
        final List<Person> peopleResult = cleanContent(page);

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
    }

    /**
     * test Predicate
     *
     * @param peopleExpected expected list
     */
    private void testPredicate(List<Person> peopleExpected) {
        final Page<Person> page = personRepository.findAll(pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person), pageFactory.pageRequest(pageDataRequest));
        final List<Person> peopleResult = cleanContent(page);

        assertNotSame(peopleExpected, peopleResult);
        assertEquals(peopleExpected, peopleResult);
    }

    /**
     * Clean values that only DB knows like ID, created_at and updated_at
     *
     * @param page page to be cleaned
     * @return clean list
     */
    private List<Person> cleanContent(Page<Person> page) {
        return page.getContent().stream()
                .map(p -> new Person(p.getName(), p.getLastName(), p.getBirthday(), p.getCivilStatus(), p.getSex(), p.getEmail(), null))
                .collect(Collectors.toList());
    }
}