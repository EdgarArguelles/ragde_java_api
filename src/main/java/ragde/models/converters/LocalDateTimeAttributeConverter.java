package ragde.models.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Indicate how to convert LocalDateTime to Timestamp in order to be saved in database
 */
@Converter(autoApply = true)
public class LocalDateTimeAttributeConverter implements AttributeConverter<LocalDateTime, Timestamp> {

    @Override
    public Timestamp convertToDatabaseColumn(LocalDateTime locDateTime) {
        return locDateTime == null ? null : Timestamp.from(locDateTime.atZone(ZoneOffset.UTC).toInstant());
    }

    @Override
    public LocalDateTime convertToEntityAttribute(Timestamp sqlTimestamp) {
        if (sqlTimestamp == null) {
            return null;
        }

        return new Date(sqlTimestamp.getTime()).toInstant().atZone(ZoneOffset.UTC).toLocalDateTime();
    }
}