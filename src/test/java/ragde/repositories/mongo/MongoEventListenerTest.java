package ragde.repositories.mongo;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.mapping.event.AbstractMongoEventListener;
import org.springframework.data.mongodb.core.mapping.event.BeforeConvertEvent;
import org.springframework.test.context.junit4.SpringRunner;
import ragde.models.Person;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(SpringRunner.class)
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