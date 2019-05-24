package ragde.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ragde.models.AuthProvider;

public interface MongoAuthProviderRepository extends MongoRepository<AuthProvider, String>, QuerydslPredicateExecutor<AuthProvider> {

    //custom mongo query for AuthProvider
}