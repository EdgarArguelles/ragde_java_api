package ragde.exceptions.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ragde.pojos.responses.error.ErrorResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class ControllerExceptionsHandlerTest {

    @Autowired
    private ControllerExceptionsHandler controllerExceptionsHandler;

    /**
     * Should handle Exception
     */
    @Test
    public void handleException() {
        final Exception exception = new RuntimeException("test");
        final ErrorResponse errorResponseExpected = new ErrorResponse("An error has occurred.", "test", null);

        final ResponseEntity responseResult = controllerExceptionsHandler.handleException(exception);
        final ErrorResponse errorResponse = (ErrorResponse) responseResult.getBody();

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, responseResult.getStatusCode());
        assertNotSame(errorResponseExpected, errorResponse);
        assertEquals(errorResponseExpected, errorResponse);
    }

    /**
     * Should handle AccessDeniedException
     */
    @Test
    public void handleAccessDeniedException() {
        final Exception exception = new AccessDeniedException("test");
        final ErrorResponse errorResponseExpected = new ErrorResponse("Access is denied.", null, null);

        final ResponseEntity responseResult = controllerExceptionsHandler.handleAccessDeniedException(exception);
        final ErrorResponse errorResponse = (ErrorResponse) responseResult.getBody();

        assertEquals(HttpStatus.FORBIDDEN, responseResult.getStatusCode());
        assertNotSame(errorResponseExpected, errorResponse);
        assertEquals(errorResponseExpected, errorResponse);
    }

    /**
     * Should handle ProviderNotFoundException
     */
    @Test
    public void handleProviderNotFoundException() {
        final Exception exception = new ProviderNotFoundException("test");
        final ErrorResponse errorResponseExpected = new ErrorResponse("User is not authenticated.", null, null);

        final ResponseEntity responseResult = controllerExceptionsHandler.handleProviderNotFoundException(exception);
        final ErrorResponse errorResponse = (ErrorResponse) responseResult.getBody();

        assertEquals(HttpStatus.UNAUTHORIZED, responseResult.getStatusCode());
        assertNotSame(errorResponseExpected, errorResponse);
        assertEquals(errorResponseExpected, errorResponse);
    }
}