package ragde.repositories.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ragde.models.Permission;

public interface MongoPermissionRepository extends MongoRepository<Permission, String>, QuerydslPredicateExecutor<Permission> {

    //custom mongo query for Permission
}