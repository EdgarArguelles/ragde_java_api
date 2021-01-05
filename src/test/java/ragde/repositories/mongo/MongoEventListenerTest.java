package ragde.repositories.mongo;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ragde.models.Person;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@SuppressWarnings("unchecked")
public class MongoEventListenerTest {

    @Autowired
    private AbstractMongoEventListener mongoEventListener;

    /**
     * Should set createdAt and updatedAt
     */
    @Test
    public void onBeforeConvertWithNullId() {
        final Person person = new Person();
        mongoEventListener.onBeforeConvert(new BeforeConvertEvent(person, "person"));

        assertNotNull(person.getCreatedAt());
        assertNotNull(person.getUpdatedAt());
    }

    /**
     * Should set updatedAt
     */
    @Test
    public void onBeforeConvertWithId() {
        final Person person = new Person("P1");
        mongoEventListener.onBeforeConvert(new BeforeConvertEvent(person, "person"));

        assertNull(person.getCreatedAt());
        assertNotNull(person.getUpdatedAt());
    }
}