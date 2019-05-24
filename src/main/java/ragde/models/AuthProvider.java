package ragde.models;

import io.leangen.graphql.annotations.GraphQLIgnore;
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
@Table(name = "auth_provider")
@GraphQLType(description = "Authentication Provider like Google or Facebook")
public class AuthProvider extends Model {

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

    @Size(min = 1, max = 255)
    @Column()
    @Setter
    private String authKey;

    @Size(min = 1, max = 255)
    @Column()
    @Setter
    private String authSecret;

    @OneToMany(mappedBy = "authProvider", fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<Authentication> authentications;

    public AuthProvider(String id) {
        this.id = id;
    }

    public AuthProvider(String name, String description, String authKey, String authSecret) {
        this.name = name;
        this.description = description;
        this.authKey = authKey;
        this.authSecret = authSecret;
    }

    @GraphQLIgnore
    public String getAuthKey() {
        return authKey;
    }

    @GraphQLIgnore
    public String getAuthSecret() {
        return authSecret;
    }
}