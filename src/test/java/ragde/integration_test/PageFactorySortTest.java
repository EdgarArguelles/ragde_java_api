package ragde.integration_test;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.test.context.junit4.SpringRunner;
import ragde.factories.PageFactory;
import ragde.models.Person;
import ragde.models.QPerson;
import ragde.pojos.pages.PageDataRequest;
import ragde.repositories.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotSame;

@RunWith(SpringRunner.class)
@SpringBootTest
@SuppressWarnings("unchecked")
public class PageFactorySortTest {

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
        pageDataRequest = new PageDataRequest(0, 100, null, null, null);
        IntegrationTest.cleanAllData(authenticationRepository, authProviderRepository, personRepository, roleRepository, permissionRepository);

        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        dateTime1 = LocalDate.parse("1986-04-08", formatter);
        dateTime2 = LocalDate.parse("1986-04-09", formatter);
        dateTime3 = LocalDate.parse("1987-02-02", formatter);

        final List<Person> people = List.of(
                new Person("3", "last name 1", dateTime3, 3, Person.SEX.M, null, null),
                new Person("5", "last name 2", dateTime1, 1, Person.SEX.F, "aa2@a.com", null),
                new Person("5", "alast name 2", dateTime3, 12, Person.SEX.F, "aa2@a.com", null),
                new Person("5", "alast name 2", dateTime3, 3, Person.SEX.F, "aa2@a.com", null),
                new Person("5", "alast name 2", dateTime3, 3, Person.SEX.F, null, null),
                new Person("5", "alast name 2", dateTime3, 3, Person.SEX.M, "aa1@a.com", null),
                new Person("5", "alast name 2", dateTime2, 105, Person.SEX.F, "aa2@a.com", null),
                new Person(" 12", "last name 3", dateTime1, 12, Person.SEX.M, "aa3@a.com", null),
                new Person(" ", "last name 3", dateTime3, 12, Person.SEX.M, "aa3@a.com", null),
                new Person("12", "last name 3", dateTime1, 12, Person.SEX.M, "aa3@a.com", null),
                new Person("12 ", "last name 3", dateTime1, 12, Person.SEX.M, "aa3@a.com", null)
        );
        personRepository.saveAll(people);
    }

    /**
     * Should sort ASC list with Specifications and Predicate
     */
    @Test
    public void sortAscWithSpecificationsPredicate() {
        pageDataRequest = new PageDataRequest(0, 100, PageDataRequest.SORT_DIRECTION.ASC, List.of("name", "lastName", "birthday", "civilStatus", "email"), null);

        final List<Person> peopleExpected = List.of(
                new Person(" ", "last name 3", dateTime3, 12, Person.SEX.M, "aa3@a.com", null),
                new Person(" 12", "last name 3", dateTime1, 12, Person.SEX.M, "aa3@a.com", null),
                new Person("12", "last name 3", dateTime1, 12, Person.SEX.M, "aa3@a.com", null),
                new Person("12 ", "last name 3", dateTime1, 12, Person.SEX.M, "aa3@a.com", null),
                new Person("3", "last name 1", dateTime3, 3, Person.SEX.M, null, null),
                new Person("5", "alast name 2", dateTime2, 105, Person.SEX.F, "aa2@a.com", null),
                new Person("5", "alast name 2", dateTime3, 3, Person.SEX.F, null, null),
                new Person("5", "alast name 2", dateTime3, 3, Person.SEX.M, "aa1@a.com", null),
                new Person("5", "alast name 2", dateTime3, 3, Person.SEX.F, "aa2@a.com", null),
                new Person("5", "alast name 2", dateTime3, 12, Person.SEX.F, "aa2@a.com", null),
                new Person("5", "last name 2", dateTime1, 1, Person.SEX.F, "aa2@a.com", null)
        );

        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);
    }

    /**
     * Should sort DESC list with Specifications and Predicate
     */
    @Test
    public void sortDescWithSpecificationsPredicate() {
        pageDataRequest = new PageDataRequest(0, 100, PageDataRequest.SORT_DIRECTION.DESC, List.of("name", "lastName", "birthday", "civilStatus", "email"), null);

        final List<Person> peopleExpected = List.of(
                new Person("5", "last name 2", dateTime1, 1, Person.SEX.F, "aa2@a.com", null),
                new Person("5", "alast name 2", dateTime3, 12, Person.SEX.F, "aa2@a.com", null),
                new Person("5", "alast name 2", dateTime3, 3, Person.SEX.F, "aa2@a.com", null),
                new Person("5", "alast name 2", dateTime3, 3, Person.SEX.M, "aa1@a.com", null),
                new Person("5", "alast name 2", dateTime3, 3, Person.SEX.F, null, null),
                new Person("5", "alast name 2", dateTime2, 105, Person.SEX.F, "aa2@a.com", null),
                new Person("3", "last name 1", dateTime3, 3, Person.SEX.M, null, null),
                new Person("12 ", "last name 3", dateTime1, 12, Person.SEX.M, "aa3@a.com", null),
                new Person("12", "last name 3", dateTime1, 12, Person.SEX.M, "aa3@a.com", null),
                new Person(" 12", "last name 3", dateTime1, 12, Person.SEX.M, "aa3@a.com", null),
                new Person(" ", "last name 3", dateTime3, 12, Person.SEX.M, "aa3@a.com", null)
        );

        testSpecifications(peopleExpected);
        testPredicate(peopleExpected);
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