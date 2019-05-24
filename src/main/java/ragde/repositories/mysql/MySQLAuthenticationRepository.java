package ragde.repositories.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ragde.models.Authentication;

public interface MySQLAuthenticationRepository extends JpaRepository<Authentication, String>, JpaSpecificationExecutor<Authentication>, QuerydslPredicateExecutor<Authentication> {

    //custom mysql query for Authentication
}