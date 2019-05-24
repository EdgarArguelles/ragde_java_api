package ragde.services.implementations;

import io.leangen.graphql.annotations.*;
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ragde.exceptions.RagdeDontFoundException;
import ragde.exceptions.RagdeValidationException;
import ragde.models.Authentication;
import ragde.models.Person;
import ragde.pojos.pages.PageDataRequest;
import ragde.pojos.responses.error.nesteds.NestedError;
import ragde.pojos.responses.error.nesteds.ValidationNestedError;
import ragde.repositories.AuthenticationRepository;
import ragde.repositories.PersonRepository;
import ragde.services.PersonService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@GraphQLApi
@Service
public class PersonServiceImpl implements PersonService {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AuthenticationRepository authenticationRepository;

    @Override
    @PreAuthorize("hasRole('VIEW_USERS')")
    @GraphQLQuery(name = "people", description = "Find all people")
    public List<Person> findAll() {
        return personRepository.findAll();
    }

    @Override
    @PreAuthorize("hasRole('VIEW_USERS')")
    @GraphQLQuery(name = "person", description = "Find a person by ID")
    public Person findById(@GraphQLId @GraphQLNonNull @GraphQLArgument(name = "id", description = "Person's ID") String id) {
        return personRepository.findById(id).orElseThrow(() -> new RagdeDontFoundException("Data don't found."));
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('CREATE_USERS')")
    @GraphQLMutation(name = "createPerson", description = "Create a new person")
    public Person save(@GraphQLNonNull @GraphQLArgument(name = "person", description = "New person") Person person) {
        validateData(person);
        return personRepository.save(person);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('CREATE_USERS')")
    @GraphQLMutation(name = "updatePerson", description = "Update a person")
    public Person update(@GraphQLNonNull @GraphQLArgument(name = "person", description = "Person's new values") Person person) {
        validateData(person);

        Person original = findById(person.getId());
        original.setName(person.getName());
        original.setLastName(person.getLastName());
        original.setBirthday(person.getBirthday());
        original.setCivilStatus(person.getCivilStatus());
        original.setSex(person.getSex());
        original.setEmail(person.getEmail());
        original.setRoles(person.getRoles());

        return personRepository.save(original);
    }

    @Override
    @Transactional
    @PreAuthorize("hasRole('REMOVE_USERS')")
    @GraphQLMutation(name = "deletePerson", description = "Delete a person")
    public Person delete(@GraphQLId @GraphQLNonNull @GraphQLArgument(name = "id", description = "Person's ID") String id) {
        Person person = findById(id);
        if (person.getAuthentications() != null && !person.getAuthentications().isEmpty()) {
            throw new RagdeValidationException("Person '" + person.getFullName() + "' has one or more authentications associated.");
        }
        personRepository.delete(person);
        person.setRoles(null);
        return person;
    }

    @Override
    @PreAuthorize("hasRole('VIEW_USERS')")
    @GraphQLQuery(name = "personPage", description = "Page all people")
    public Page<Person> page(@GraphQLNonNull @GraphQLArgument(name = "pageDataRequest", description = "Filter, limit and sort data") PageDataRequest pageDataRequest) {
        return personRepository.page(pageDataRequest);
    }

    @Override
    @PreAuthorize("hasRole('VIEW_USERS')")
    @GraphQLQuery(name = "authentications", description = "Authentications where this Person is present")
    public List<Authentication> getAuthentications(@GraphQLContext Person person) {
        if (person.getAuthentications() == null) {
            person.setAuthentications(authenticationRepository.findByPerson(person));
        }

        return person.getAuthentications();
    }

    /**
     * Validates data integrity
     *
     * @param person entity to be validated
     * @throws RagdeValidationException
     */
    private void validateData(Person person) throws RagdeValidationException {
        List<NestedError> nestedErrors = new ArrayList<>();
        nestedErrors.add(validateCivilStatus(person.getCivilStatus()));
        nestedErrors.add(validateSex(person.getSex()));

        // remove null from list
        nestedErrors.removeAll(Collections.singleton(null));
        if (!nestedErrors.isEmpty()) {
            throw new RagdeValidationException("Some data aren't valid.", nestedErrors);
        }
    }

    /**
     * Validates if civil status is an allowed value
     *
     * @param civilStatus value to be validated
     * @return NestedError or null if value is valid
     */
    private NestedError validateCivilStatus(Integer civilStatus) {
        List<Integer> allowed = new ArrayList<>();

        //iterate all interface properties
        List.of(Person.CIVIL_STATUS.class.getDeclaredFields()).forEach((field -> {
            try {
                allowed.add((Integer) field.get(Integer.class));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }));

        if (!allowed.contains(civilStatus)) {
            return new ValidationNestedError("civilStatus", "'" + civilStatus + "' is not a valid Civil Status value, it only allows " + Arrays.toString(allowed.toArray()));
        }
        return null;
    }

    /**
     * Validates if sex is an allowed value
     *
     * @param sex value to be validated
     * @return NestedError or null if value is valid
     */
    private NestedError validateSex(String sex) {
        List<String> allowed = new ArrayList<>();

        //iterate all interface properties
        List.of(Person.SEX.class.getDeclaredFields()).forEach((field -> {
            try {
                allowed.add((String) field.get(String.class));
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }));

        if (!allowed.contains(sex)) {
            return new ValidationNestedError("sex", "'" + sex + "' is not a valid Sex value, it only allows " + Arrays.toString(allowed.toArray()));
        }
        return null;
    }
}