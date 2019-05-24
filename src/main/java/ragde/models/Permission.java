package ragde.models;

import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@NoArgsConstructor
@ToString(callSuper = true, of = "name")
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "permission")
@GraphQLType(description = "Permission that could be included in one or more Roles")
public class Permission extends Model {

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

    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<Role> roles;

    public Permission(String id) {
        this.id = id;
    }

    public Permission(String name, String description) {
        this.name = name;
        this.description = description;
    }
}