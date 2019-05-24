package ragde.repositories.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ragde.models.Person;

public interface MySQLPersonRepository extends JpaRepository<Person, String>, JpaSpecificationExecutor<Person>, QuerydslPredicateExecutor<Person> {

    //custom mysql query for Person
}