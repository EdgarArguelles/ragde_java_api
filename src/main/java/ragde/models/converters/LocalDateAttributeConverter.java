package ragde.models.converters;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneOffset;

/**
 * Indicate how to convert LocalDate to Date in order to be saved in database
 */
@Converter(autoApply = true)
public class LocalDateAttributeConverter implements AttributeConverter<LocalDate, Date> {

    @Override
    public Date convertToDatabaseColumn(LocalDate locDate) {
        if (locDate == null) {
            return null;
        }

        java.util.Date date = java.util.Date.from(locDate.atStartOfDay(ZoneOffset.UTC).toInstant());
        return Date.valueOf(date.toInstant().atZone(ZoneOffset.UTC).toLocalDate());
    }

    @Override
    public LocalDate convertToEntityAttribute(Date sqlDate) {
        if (sqlDate == null) {
            return null;
        }

        return new java.util.Date(sqlDate.getTime()).toInstant().atZone(ZoneOffset.UTC).toLocalDate();
    }
}