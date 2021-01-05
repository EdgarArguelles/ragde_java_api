package ragde.exceptions.handlers;

import graphql.GraphQLError;
import graphql.execution.DataFetcherExceptionHandlerParameters;
import graphql.execution.ExecutionContext;
import graphql.execution.ExecutionPath;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ragde.exceptions.*;
import ragde.pojos.responses.error.nesteds.NestedError;
import ragde.pojos.responses.error.nesteds.ValidationNestedError;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class GraphQLExceptionsHandlerTest {

    @Autowired
    private GraphQLExceptionsHandler graphQLExceptionsHandler;

    @Captor
    private ArgumentCaptor<GraphQLError> captor;

    /**
     * Should create a RagdeDontFoundException
     */
    @Test
    public void acceptRagdeDontFoundException() {
        final List<Object> path = List.of("test 1", "test 2");
        final ExecutionContext executionContextMock = mock(ExecutionContext.class);
        final ExecutionPath pathMock = mock(ExecutionPath.class);
        final Throwable exception = new RagdeDontFoundException("error");
        final DataFetcherExceptionHandlerParameters handlerParameters = new DataFetcherExceptionHandlerParameters(executionContextMock, null, null, null, null, pathMock, exception);
        final RagdeException exceptionExpected = new RagdeDontFoundException("error");
        exceptionExpected.setPath(path);
        when(pathMock.toList()).thenReturn(path);

        graphQLExceptionsHandler.accept(handlerParameters);

        verify(executionContextMock, times(1)).addError(captor.capture());
        captor.getAllValues().forEach(value -> {
            assertNotSame(exceptionExpected, value);
            assertEquals(exceptionExpected, value);
            assertEquals(exceptionExpected.getMessage(), value.getMessage());
            assertEquals(exceptionExpected.getClass().getName(), value.getClass().getName());
        });
    }

    /**
     * Should create a RagdeInternalException
     */
    @Test
    public void acceptRagdeInternalException() {
        final List<Object> path = List.of("test 1", "test 2");
        final ExecutionContext executionContextMock = mock(ExecutionContext.class);
        final ExecutionPath pathMock = mock(ExecutionPath.class);
        final Throwable exception = new RuntimeException("error");
        final DataFetcherExceptionHandlerParameters handlerParameters = new DataFetcherExceptionHandlerParameters(executionContextMock, null, null, null, null, pathMock, exception);
        final RagdeException exceptionExpected = new RagdeInternalException("An error has occurred.");
        exceptionExpected.setPath(path);
        exceptionExpected.setDevMessage("error");
        when(pathMock.toList()).thenReturn(path);

        graphQLExceptionsHandler.accept(handlerParameters);

        verify(executionContextMock, times(1)).addError(captor.capture());
        captor.getAllValues().forEach(value -> {
            assertNotSame(exceptionExpected, value);
            assertEquals(exceptionExpected, value);
            assertEquals(exceptionExpected.getMessage(), value.getMessage());
            assertEquals(exceptionExpected.getClass().getName(), value.getClass().getName());
        });
    }

    /**
     * Should create a RagdeAuthenticationException
     */
    @Test
    public void acceptRagdeAuthenticationException() {
        final List<Object> path = List.of("test 1", "test 2");
        final ExecutionContext executionContextMock = mock(ExecutionContext.class);
        final ExecutionPath pathMock = mock(ExecutionPath.class);
        final Throwable exception = new AccessDeniedException("error");
        final DataFetcherExceptionHandlerParameters handlerParameters = new DataFetcherExceptionHandlerParameters(executionContextMock, null, null, null, null, pathMock, exception);
        final RagdeException exceptionExpected = new RagdeAuthenticationException("Access is denied.");
        exceptionExpected.setPath(path);
        exceptionExpected.setDevMessage("error");
        when(pathMock.toList()).thenReturn(path);

        graphQLExceptionsHandler.accept(handlerParameters);

        verify(executionContextMock, times(1)).addError(captor.capture());
        captor.getAllValues().forEach(value -> {
            assertNotSame(exceptionExpected, value);
            assertEquals(exceptionExpected, value);
            assertEquals(exceptionExpected.getMessage(), value.getMessage());
            assertEquals(exceptionExpected.getClass().getName(), value.getClass().getName());
        });
    }

    /**
     * Should create a RagdeValidationException
     */
    @Test
    public void acceptRagdeValidationException() {
        final ConstraintViolation violation1Mock = mock(ConstraintViolation.class);
        final Path path1Mock = mock(Path.class);
        when(path1Mock.toString()).thenReturn("test.test.username");
        when(violation1Mock.getPropertyPath()).thenReturn(path1Mock);
        when(violation1Mock.getMessage()).thenReturn("error 1");

        final ConstraintViolationException exceptionMock = mock(ConstraintViolationException.class);
        when(exceptionMock.getConstraintViolations()).thenReturn(Set.of(violation1Mock));
        when(exceptionMock.getMessage()).thenReturn("error");

        final List<NestedError> nestedErrors = List.of(new ValidationNestedError("username", "error 1"));
        final List<Object> path = List.of("test 1", "test 2");
        final ExecutionContext executionContextMock = mock(ExecutionContext.class);
        final ExecutionPath pathMock = mock(ExecutionPath.class);
        final DataFetcherExceptionHandlerParameters handlerParameters = new DataFetcherExceptionHandlerParameters(executionContextMock, null, null, null, null, pathMock, exceptionMock);
        final RagdeException exceptionExpected = new RagdeValidationException("Some data aren't valid.", nestedErrors);
        exceptionExpected.setPath(path);
        exceptionExpected.setDevMessage("error");
        when(pathMock.toList()).thenReturn(path);

        graphQLExceptionsHandler.accept(handlerParameters);

        verify(executionContextMock, times(1)).addError(captor.capture());
        captor.getAllValues().forEach(value -> {
            assertNotSame(exceptionExpected, value);
            assertEquals(exceptionExpected, value);
            assertEquals(exceptionExpected.getMessage(), value.getMessage());
            assertEquals(exceptionExpected.getClass().getName(), value.getClass().getName());
        });
    }
}