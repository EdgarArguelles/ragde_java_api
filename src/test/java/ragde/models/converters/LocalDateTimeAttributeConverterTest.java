package ragde.models.converters;

import org.junit.jupiter.api.Test;

import javax.persistence.AttributeConverter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import static org.junit.jupiter.api.Assertions.*;

public class LocalDateTimeAttributeConverterTest {

    private AttributeConverter<LocalDateTime, Timestamp> attributeConverter = new LocalDateTimeAttributeConverter();

    /**
     * Should get null when date is null
     */
    @Test
    public void convertToDatabaseColumnWhenNull() {
        final Timestamp dateResult = attributeConverter.convertToDatabaseColumn(null);

        assertNull(dateResult);
    }

    /**
     * Should convert date
     */
    @Test
    public void convertToDatabaseColumnWhenNotNull() {
        final LocalDateTime locDateTime = LocalDateTime.now(ZoneOffset.UTC);
        final Timestamp dateExpected = Timestamp.from(locDateTime.atZone(ZoneOffset.UTC).toInstant());

        final Timestamp dateResult = attributeConverter.convertToDatabaseColumn(locDateTime);

        assertNotSame(dateExpected, dateResult);
        assertEquals(dateExpected, dateResult);
    }

    /**
     * Should get null when date is null
     */
    @Test
    public void convertToEntityAttributeWhenNull() {
        final LocalDateTime dateResult = attributeConverter.convertToEntityAttribute(null);

        assertNull(dateResult);
    }

    /**
     * Should convert date
     */
    @Test
    public void convertToEntityAttributeWhenNotNull() {
        final Timestamp sqlTimestamp = new Timestamp(1L);
        final LocalDateTime dateExpected = new java.util.Date(sqlTimestamp.getTime()).toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();

        final LocalDateTime dateResult = attributeConverter.convertToEntityAttribute(sqlTimestamp);

        assertNotSame(dateExpected, dateResult);
        assertEquals(dateExpected, dateResult);
    }
}