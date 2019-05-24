package ragde.security.pojos;

import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;

/**
 * Essential Logged User info pojo
 */
@NoArgsConstructor
@EqualsAndHashCode
@GraphQLType(description = "Logged User's basic information")
public class LoggedUser {

    @Getter
    private String id;

    @Getter
    private String fullName;

    @Getter
    private String image;

    @Getter
    private String role;

    @Getter
    @Setter
    private String token;

    @Getter
    @Setter
    private Set<String> permissions;

    /**
     * Create an instance
     *
     * @param id   person data base id
     * @param role role data base id (In which facet is the user)
     */
    public LoggedUser(String id, String role) {
        this(id, null, null, role, null);
    }

    /**
     * Create an instance
     *
     * @param id          person data base id
     * @param fullName    display name
     * @param image       image to display
     * @param role        role data base id (In which facet is the user)
     * @param permissions permissions name list
     */
    public LoggedUser(String id, String fullName, String image, String role, Set<String> permissions) {
        this.id = id;
        this.fullName = fullName;
        this.image = image;
        this.role = role;
        this.permissions = permissions;
    }
}