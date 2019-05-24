package ragde.security.pojos;

import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Account Credentials pojo
 */
@NoArgsConstructor
@EqualsAndHashCode
@GraphQLType(description = "Account's Credentials")
public class AccountCredentials {

    @NotNull
    @Size(min = 1, max = 255)
    @Getter
    private String username;

    @NotNull
    @Size(min = 1, max = 255)
    @Getter
    private String password;

    /**
     * Create an instance
     *
     * @param username credentials username
     * @param password credentials password
     */
    public AccountCredentials(String username, String password) {
        this.username = username;
        this.password = password;
    }
}