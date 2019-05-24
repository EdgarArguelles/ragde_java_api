package ragde.models.converters;

import org.junit.Test;

import javax.persistence.AttributeConverter;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneOffset;

import static org.junit.Assert.*;

public class LocalDateAttributeConverterTest {

    private AttributeConverter<LocalDate, Date> attributeConverter = new LocalDateAttributeConverter();

    /**
     * Should get null when date is null
     */
    @Test
    public void convertToDatabaseColumnWhenNull() {
        final Date dateResult = attributeConverter.convertToDatabaseColumn(null);

        assertNull(dateResult);
    }

    /**
     * Should convert date
     */
    @Test
    public void convertToDatabaseColumnWhenNotNull() {
        final LocalDate locDate = LocalDate.now(ZoneOffset.UTC);
        final java.util.Date date = java.util.Date.from(locDate.atStartOfDay(ZoneOffset.UTC).toInstant());
        final Date dateExpected = Date.valueOf(date.toInstant().atZone(ZoneOffset.UTC).toLocalDate());

        final Date dateResult = attributeConverter.convertToDatabaseColumn(locDate);

        assertNotSame(dateExpected, dateResult);
        assertEquals(dateExpected, dateResult);
    }

    /**
     * Should get null when date is null
     */
    @Test
    public void convertToEntityAttributeWhenNull() {
        final LocalDate dateResult = attributeConverter.convertToEntityAttribute(null);

        assertNull(dateResult);
    }

    /**
     * Should convert date
     */
    @Test
    public void convertToEntityAttributeWhenNotNull() {
        final Date sqlDate = new Date(1L);
        final LocalDate dateExpected = new java.util.Date(sqlDate.getTime()).toInstant().atZone(ZoneOffset.UTC).toLocalDate();

        final LocalDate dateResult = attributeConverter.convertToEntityAttribute(sqlDate);

        assertNotSame(dateExpected, dateResult);
        assertEquals(dateExpected, dateResult);
    }
}