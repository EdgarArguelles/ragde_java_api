package ragde.repositories.implementations;

import com.querydsl.core.types.Predicate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ragde.factories.PageFactory;
import ragde.models.Person;
import ragde.models.QPerson;
import ragde.pojos.pages.PageDataRequest;
import ragde.repositories.PersonRepository;
import ragde.repositories.executor.QueryExecutor;

public class PersonRepositoryImpl implements QueryExecutor<Person> {

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private PageFactory pageFactory;

    @Override
    @SuppressWarnings("unchecked")
    public Page<Person> page(PageDataRequest pageDataRequest) {
        if (DEFAULT_EXECUTOR == QuerydslPredicateExecutor.class || !(personRepository instanceof JpaSpecificationExecutor)) {
            Predicate predicate = pageFactory.getPredicate(pageDataRequest.getFilters(), QPerson.person);
            if (predicate != null) {
                return personRepository.findAll(predicate, pageFactory.pageRequest(pageDataRequest));
            } else {
                return personRepository.findAll(pageFactory.pageRequest(pageDataRequest));
            }
        }

        return personRepository.findAll(pageFactory.getSpecifications(pageDataRequest.getFilters()), pageFactory.pageRequest(pageDataRequest));
    }
}