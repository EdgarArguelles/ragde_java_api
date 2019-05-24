package ragde.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ragde.models.Role;

public interface MongoRoleRepository extends MongoRepository<Role, String>, QuerydslPredicateExecutor<Role> {

    //custom mongo query for Role
}