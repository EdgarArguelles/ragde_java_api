package ragde.models;

import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.DBRef;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@NoArgsConstructor
@ToString(callSuper = true, of = {"name", "lastName"})
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "person")
@GraphQLType(description = "User's personal information")
public class Person extends Model {

    public interface CIVIL_STATUS {
        Integer SINGLE = 1;
        Integer MARRIED = 2;
    }

    public interface SEX {
        String M = "M";
        String F = "F";
    }

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    @Getter
    @Setter
    private String name;

    @NotNull
    @Size(min = 1, max = 255)
    @Column(nullable = false)
    @Getter
    @Setter
    private String lastName;

    @NotNull
    @Column(nullable = false)
    @Getter
    @Setter
    private LocalDate birthday;

    @NotNull
    @Column(nullable = false, columnDefinition = "smallint")
    @Getter
    @Setter
    private Integer civilStatus;

    @NotNull
    @Size(min = 1, max = 1)
    @Column(nullable = false, length = 1)
    @Getter
    @Setter
    private String sex;

    @Email
    @Size(min = 3, max = 255)
    @Column()
    @Getter
    @Setter
    private String email;

    // in @ManyToMany the Owner Entity must use Set to notify MySQL that new relational table will have a combine Primary Key
    // if List is used instead the new relational table won't have a combine Primary key so data could be duplicated
    @ManyToMany(fetch = FetchType.LAZY)
    @DBRef // all foreign keys need @DBRef to notify Mongo about relationship and ownership
    @Getter
    @Setter
    private Set<Role> roles;

    @OneToMany(mappedBy = "person", fetch = FetchType.LAZY)
    @Getter
    @Setter
    private List<Authentication> authentications;

    public Person(String id) {
        this.id = id;
    }

    public Person(String name, String lastName, LocalDate birthday, Integer civilStatus, String sex, String email, Set<Role> roles) {
        this.name = name;
        this.lastName = lastName;
        this.birthday = birthday;
        this.civilStatus = civilStatus;
        this.sex = sex;
        this.email = email;
        this.roles = roles;
    }

    public String getFullName() {
        return name + " " + lastName;
    }
}