package ragde.exceptions.handlers;

import graphql.execution.DataFetcherExceptionHandler;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import ragde.exceptions.RagdeAuthenticationException;
import ragde.exceptions.RagdeException;
import ragde.exceptions.RagdeInternalException;
import ragde.exceptions.RagdeValidationException;
import ragde.pojos.responses.error.nesteds.NestedError;
import ragde.pojos.responses.error.nesteds.ValidationNestedError;

import javax.validation.ConstraintViolationException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class GraphQLExceptionsHandler implements DataFetcherExceptionHandler {

    @Override
    public void accept(DataFetcherExceptionHandlerParameters handlerParameters) {
        Throwable exception = handlerParameters.getException();

        RagdeException ragdeException;
        if (exception instanceof RagdeException) {
            ragdeException = (RagdeException) exception;
        } else {
            ragdeException = new RagdeInternalException("An error has occurred.");
            if (exception instanceof AccessDeniedException) {
                ragdeException = new RagdeAuthenticationException("Access is denied.");
            }
            if (exception instanceof ConstraintViolationException) {
                List<NestedError> nestedErrors = ((ConstraintViolationException) exception).getConstraintViolations().stream()
                        .map(violation -> {
                            String field = violation.getPropertyPath().toString();
                            field = field.substring(field.lastIndexOf(".") + 1);
                            return new ValidationNestedError(field, violation.getMessage());
                        })
                        .collect(Collectors.toList());
                ragdeException = new RagdeValidationException("Some data aren't valid.", nestedErrors);
            }

            ragdeException.setDevMessage(exception.getMessage());
        }

        ragdeException.setPath(handlerParameters.getPath().toList());
        handlerParameters.getExecutionContext().addError(ragdeException);
    }
}