package ragde.factories.implementations;

import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.*;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ragde.exceptions.RagdeValidationException;
import ragde.factories.PageFactory;
import ragde.pojos.pages.FilterRequest;
import ragde.pojos.pages.PageDataRequest;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Root;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@SuppressWarnings("unchecked")
public class PageFactoryImpl implements PageFactory {

    private final String DATE_TIME_PATTERN = "yyyy-MM-dd'T'HH:mm:ss'Z'";

    private final String DATE_PATTERN = "yyyy-MM-dd";

    @Override
    public PageRequest pageRequest(PageDataRequest pageDataRequest) {
        Sort sort = new Sort(Sort.Direction.ASC, "id");

        if (pageDataRequest.getSort() != null && !pageDataRequest.getSort().isEmpty()) {
            Sort.Direction direction = pageDataRequest.getDirection() != null ? getDirection(pageDataRequest.getDirection()) : null;
            sort = new Sort(direction, pageDataRequest.getSort());
        }

        return PageRequest.of(pageDataRequest.getPage(), pageDataRequest.getSize(), sort);
    }

    @Override
    public Specification getSpecifications(List<FilterRequest> filtersRequest) {
        if (filtersRequest == null) {
            return null;
        }

        final List<Specification> specifications = new ArrayList<>();
        filtersRequest.forEach(fr -> specifications.add((root, query, cb) -> getPredicate(fr, root, cb)));

        Specification where = null;
        for (Specification s : specifications) {
            if (where == null) {
                where = Specification.where(s);
            } else {
                where = where.and(s);
            }
        }

        return where;
    }

    @Override
    public Predicate getPredicate(List<FilterRequest> filtersRequest, EntityPathBase entityPathBase) {
        if (filtersRequest == null) {
            return null;
        }

        BooleanExpression expression = null;
        for (FilterRequest fr : filtersRequest) {
            if (expression == null) {
                expression = getBooleanExpression(fr, entityPathBase);
            } else {
                expression = expression.and(getBooleanExpression(fr, entityPathBase));
            }
        }
        return expression;
    }

    /**
     * Create a Predicate from FilterRequest
     *
     * @param filterRequest Filter data
     * @param root          A root type in the from clause
     * @param cb            Used to construct criteria queries
     * @return Predicate generated
     */
    private javax.persistence.criteria.Predicate getPredicate(FilterRequest filterRequest, Root root, CriteriaBuilder cb) {
        String stringValue = filterRequest.getValue();
        LocalDateTime dateTimeValue = null;
        LocalDate dateValue = null;
        try {
            dateTimeValue = LocalDateTime.parse(stringValue, DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        } catch (Exception e) {
        }
        try {
            dateValue = LocalDate.parse(stringValue, DateTimeFormatter.ofPattern(DATE_PATTERN));
        } catch (Exception e) {
        }

        Object value = dateTimeValue != null ? dateTimeValue
                : dateValue != null ? dateValue
                : stringValue;

        switch (filterRequest.getOperation()) {
            case EQ:
                return cb.equal(root.get(filterRequest.getField()), value);
            case NE:
                return cb.notEqual(root.get(filterRequest.getField()), value);
            case GT:
                if (dateTimeValue != null) {
                    return cb.greaterThan(root.get(filterRequest.getField()), dateTimeValue);
                }
                if (dateValue != null) {
                    return cb.greaterThan(root.get(filterRequest.getField()), dateValue);
                }
                return cb.greaterThan(root.get(filterRequest.getField()), stringValue);
            case GET:
                if (dateTimeValue != null) {
                    return cb.greaterThanOrEqualTo(root.get(filterRequest.getField()), dateTimeValue);
                }
                if (dateValue != null) {
                    return cb.greaterThanOrEqualTo(root.get(filterRequest.getField()), dateValue);
                }
                return cb.greaterThanOrEqualTo(root.get(filterRequest.getField()), stringValue);
            case LT:
                if (dateTimeValue != null) {
                    return cb.lessThan(root.get(filterRequest.getField()), dateTimeValue);
                }
                if (dateValue != null) {
                    return cb.lessThan(root.get(filterRequest.getField()), dateValue);
                }
                return cb.lessThan(root.get(filterRequest.getField()), stringValue);
            case LET:
                if (dateTimeValue != null) {
                    return cb.lessThanOrEqualTo(root.get(filterRequest.getField()), dateTimeValue);
                }
                if (dateValue != null) {
                    return cb.lessThanOrEqualTo(root.get(filterRequest.getField()), dateValue);
                }
                return cb.lessThanOrEqualTo(root.get(filterRequest.getField()), stringValue);
            case STARTS_WITH:
                return cb.like(root.get(filterRequest.getField()), stringValue + "%");
            case ENDS_WITH:
                return cb.like(root.get(filterRequest.getField()), "%" + stringValue);
            case LIKE:
            default:
                return cb.like(root.get(filterRequest.getField()), "%" + stringValue + "%");
        }
    }

    /**
     * Create a BooleanExpression from FilterRequest
     *
     * @param filterRequest  Filter data
     * @param entityPathBase QEntity base to generate BooleanExpression
     * @return BooleanExpression generated
     */
    private BooleanExpression getBooleanExpression(FilterRequest filterRequest, EntityPathBase entityPathBase) {
        try {
            Class type = entityPathBase.getClass().getDeclaredField(filterRequest.getField()).getType();
            PathBuilder entityPath = new PathBuilder(entityPathBase.getClass(), entityPathBase.toString());
            if (type == NumberPath.class) {
                return getNumberExpression(filterRequest, entityPath);
            }
            if (type == DateTimePath.class) {
                return getDateTimeExpression(filterRequest, entityPath);
            }
            if (type == DatePath.class) {
                return getDateExpression(filterRequest, entityPath);
            }
            return getStringExpression(filterRequest, entityPath);
        } catch (Exception e) {
            throw new RagdeValidationException(e.getMessage());
        }
    }

    /**
     * Create a BooleanExpression from FilterRequest when field is String
     *
     * @param filterRequest Filter data
     * @param entityPath    PathBuilder base to generate BooleanExpression
     * @return BooleanExpression generated
     */
    private BooleanExpression getStringExpression(FilterRequest filterRequest, PathBuilder entityPath) {
        StringPath expression = entityPath.getString(filterRequest.getField());
        switch (filterRequest.getOperation()) {
            case EQ:
                return expression.eq(filterRequest.getValue());
            case NE:
                return expression.ne(filterRequest.getValue());
            case GT:
                return expression.gt(filterRequest.getValue());
            case GET:
                return expression.goe(filterRequest.getValue());
            case LT:
                return expression.lt(filterRequest.getValue());
            case LET:
                return expression.loe(filterRequest.getValue());
            case STARTS_WITH:
                return expression.like(filterRequest.getValue() + "%");
            case ENDS_WITH:
                return expression.like("%" + filterRequest.getValue());
            case LIKE:
            default:
                return expression.like("%" + filterRequest.getValue() + "%");
        }
    }

    /**
     * Create a BooleanExpression from FilterRequest when field is Number
     *
     * @param filterRequest Filter data
     * @param entityPath    PathBuilder base to generate BooleanExpression
     * @return BooleanExpression generated
     */
    private BooleanExpression getNumberExpression(FilterRequest filterRequest, PathBuilder entityPath) {
        NumberPath expression = entityPath.getNumber(filterRequest.getField(), Number.class);
        Number numberValue = Double.parseDouble(filterRequest.getValue());
        switch (filterRequest.getOperation()) {
            case EQ:
                return expression.eq(numberValue);
            case NE:
                return expression.ne(numberValue);
            case GT:
                return expression.gt(numberValue);
            case GET:
                return expression.goe(numberValue);
            case LT:
                return expression.lt(numberValue);
            case LET:
                return expression.loe(numberValue);
            case STARTS_WITH:
            case ENDS_WITH:
            case LIKE:
            default:
                throw new RagdeValidationException("Number type doesn't allow like operations.");
        }
    }

    /**
     * Create a BooleanExpression from FilterRequest when field is DateTime
     *
     * @param filterRequest Filter data
     * @param entityPath    PathBuilder base to generate BooleanExpression
     * @return BooleanExpression generated
     */
    private BooleanExpression getDateTimeExpression(FilterRequest filterRequest, PathBuilder entityPath) {
        DateTimePath expression = entityPath.getDateTime(filterRequest.getField(), LocalDateTime.class);
        LocalDateTime dateTimeValue = LocalDateTime.parse(filterRequest.getValue(), DateTimeFormatter.ofPattern(DATE_TIME_PATTERN));
        switch (filterRequest.getOperation()) {
            case EQ:
                return expression.eq(dateTimeValue);
            case NE:
                return expression.ne(dateTimeValue);
            case GT:
                return expression.gt(dateTimeValue);
            case GET:
                return expression.goe(dateTimeValue);
            case LT:
                return expression.lt(dateTimeValue);
            case LET:
                return expression.loe(dateTimeValue);
            case STARTS_WITH:
            case ENDS_WITH:
            case LIKE:
            default:
                throw new RagdeValidationException("DateTime type doesn't allow like operations.");
        }
    }

    /**
     * Create a BooleanExpression from FilterRequest when field is Date
     *
     * @param filterRequest Filter data
     * @param entityPath    PathBuilder base to generate BooleanExpression
     * @return BooleanExpression generated
     */
    private BooleanExpression getDateExpression(FilterRequest filterRequest, PathBuilder entityPath) {
        DatePath expression = entityPath.getDate(filterRequest.getField(), LocalDate.class);
        LocalDate dateValue = LocalDate.parse(filterRequest.getValue(), DateTimeFormatter.ofPattern(DATE_PATTERN));
        switch (filterRequest.getOperation()) {
            case EQ:
                return expression.eq(dateValue);
            case NE:
                return expression.ne(dateValue);
            case GT:
                return expression.gt(dateValue);
            case GET:
                return expression.goe(dateValue);
            case LT:
                return expression.lt(dateValue);
            case LET:
                return expression.loe(dateValue);
            case STARTS_WITH:
            case ENDS_WITH:
            case LIKE:
            default:
                throw new RagdeValidationException("Date type doesn't allow like operations.");
        }
    }

    /**
     * Parse Sort Direction
     *
     * @param direction page sort direction (could be ASC or DESC)
     * @return Sort Direction or null if value was invalid
     */
    private Sort.Direction getDirection(PageDataRequest.SORT_DIRECTION direction) {
        switch (direction) {
            case ASC:
                return Sort.Direction.ASC;
            case DESC:
                return Sort.Direction.DESC;
            default:
                return null;
        }
    }
}