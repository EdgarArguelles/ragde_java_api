package ragde.models;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class ModelTest {

    private Person person;

    @Before
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