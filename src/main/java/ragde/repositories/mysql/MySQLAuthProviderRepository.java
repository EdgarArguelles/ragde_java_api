package ragde.repositories.mysql;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import ragde.models.AuthProvider;

public interface MySQLAuthProviderRepository extends JpaRepository<AuthProvider, String>, JpaSpecificationExecutor<AuthProvider>, QuerydslPredicateExecutor<AuthProvider> {

    //custom mysql query for AuthProvider
}