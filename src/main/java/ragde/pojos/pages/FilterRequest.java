package ragde.pojos.pages;

import io.leangen.graphql.annotations.GraphQLEnumValue;
import io.leangen.graphql.annotations.types.GraphQLType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * Filter Request pojo
 */
@NoArgsConstructor
@EqualsAndHashCode
@GraphQLType(description = "Pagination's Filter data")
public class FilterRequest {

    public enum OPERATIONS {
        @GraphQLEnumValue(description = "Equal to") EQ,
        @GraphQLEnumValue(description = "Not Equal to") NE,
        @GraphQLEnumValue(description = "Greater than") GT,
        @GraphQLEnumValue(description = "Greater or Equal than") GET,
        @GraphQLEnumValue(description = "Lesser than") LT,
        @GraphQLEnumValue(description = "Lesser or Equal than") LET,
        @GraphQLEnumValue(description = "Contains") LIKE,
        @GraphQLEnumValue(description = "Starts with") STARTS_WITH,
        @GraphQLEnumValue(description = "Ends with") ENDS_WITH
    }

    @NotNull
    @Size(min = 1, max = 255)
    @Getter
    private String field;

    @NotNull
    @Getter
    private String value;

    @NotNull
    @Getter
    private OPERATIONS operation;

    /**
     * Create an instance
     *
     * @param field     field to be filtered
     * @param value     field value
     * @param operation operation to be performed
     */
    public FilterRequest(String field, String value, OPERATIONS operation) {
        this.field = field;
        this.value = value;
        this.operation = operation;
    }
}