package ragde.repositories.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ragde.models.Role;

public interface MySQLRoleRepository extends JpaRepository<Role, String>, JpaSpecificationExecutor<Role>, QuerydslPredicateExecutor<Role> {

    //custom mysql query for Role
}