package ragde.models;

import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@ToString(callSuper = true, of = "name")
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "role")
@GraphQLType(description = "Role that could be associated with one or more users")
public class Role extends Model {

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false, unique = true)
    @Getter
    @Setter
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    @Getter
    @Setter
    private String description;

    // in @ManyToMany the Owner Entity must use Set to notify MySQL that new relational table will have a combine Primary Key
    // if List is used instead the new relational table won't have a combine Primary key so data could be duplicated
    @ManyToMany(fetch = FetchType.LAZY)
    @DBRef // all foreign keys need @DBRef to notify Mongo about relationship and ownership
    @Getter
    @Setter
    private Set<Permission> permissions;

    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<Person> people;

    public Role(String id) {
        this.id = id;
    }

    public Role(String name, String description, Set<Permission> permissions) {
        this.name = name;
        this.description = description;
        this.permissions = permissions;
    }
}