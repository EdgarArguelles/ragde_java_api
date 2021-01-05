package ragde.models;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ModelTest {

    private Person person;

    @BeforeEach
    public void setup() {
        person = new Person();
    }

    /**
     * Should set createdAt and updatedAt
     */
    @Test
    public void createdAt() {
        person.createdAt();

        assertNull(person.getId());
        assertNotNull(person.getCreatedAt());
        assertNotNull(person.getUpdatedAt());
    }

    /**
     * Should set updatedAt
     */
    @Test
    public void updatedAt() {
        person.updatedAt();

        assertNull(person.getId());
        assertNull(person.getCreatedAt());
        assertNotNull(person.getUpdatedAt());
    }
}