package ragde.repositories.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ragde.models.Permission;

public interface MySQLPermissionRepository extends JpaRepository<Permission, String>, JpaSpecificationExecutor<Permission>, QuerydslPredicateExecutor<Permission> {

    //custom mysql query for Permission
}