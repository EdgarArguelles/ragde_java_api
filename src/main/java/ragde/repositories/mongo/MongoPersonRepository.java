package ragde.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ragde.models.Person;

public interface MongoPersonRepository extends MongoRepository<Person, String>, QuerydslPredicateExecutor<Person> {

    //custom mongo query for Person
}