package ragde.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ragde.models.Authentication;

public interface MongoAuthenticationRepository extends MongoRepository<Authentication, String>, QuerydslPredicateExecutor<Authentication> {

    //custom mongo query for Authentication
}