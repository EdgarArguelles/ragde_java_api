package ragde.models;

import io.leangen.graphql.annotations.GraphQLIgnore;
import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@NoArgsConstructor
@ToString(callSuper = true, of = "username")
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "authentication", uniqueConstraints = {@UniqueConstraint(columnNames = {"auth_provider_id", "person_id"})})
@GraphQLType(description = "Authentication associated with Person and AuthProvider")
public class Authentication extends Model {

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false, unique = true)
    @Getter
    @Setter
    private String username;

    @Size(min = 3, max = 255)
    @Column()
    @Setter
    private String password;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auth_provider_id", nullable = false)
    @DBRef // all foreign keys need @DBRef to notify Mongo about relationship and ownership
    @Getter
    @Setter
    private AuthProvider authProvider;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "person_id", nullable = false)
    @DBRef // all foreign keys need @DBRef to notify Mongo about relationship and ownership
    @Getter
    @Setter
    private Person person;

    public Authentication(String id) {
        this.id = id;
    }

    public Authentication(String username, String password, AuthProvider authProvider, Person person) {
        this.username = username;
        this.password = password;
        this.authProvider = authProvider;
        this.person = person;
    }

    @GraphQLIgnore
    public String getPassword() {
        return password;
    }
}