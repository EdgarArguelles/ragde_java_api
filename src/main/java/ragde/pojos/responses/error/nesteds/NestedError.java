package ragde.pojos.responses.error.nesteds;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Nested Error
 */
@NoArgsConstructor
@EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = ValidationNestedError.class)})
public abstract class NestedError {

    @Getter
    private String message;

    /**
     * Create a nested error instance
     *
     * @param message nested message displayed to users
     */
    public NestedError(String message) {
        this.message = message;
    }
}