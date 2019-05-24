package ragde.exceptions.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.ProviderNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ragde.pojos.responses.error.ErrorResponse;

/**
 * Handle all exceptions that happens in @Controller
 */
@ControllerAdvice
public class ControllerExceptionsHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity handleException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse("An error has occurred.", e.getMessage(), null), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity handleAccessDeniedException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse("Access is denied.", null, null), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(ProviderNotFoundException.class)
    public ResponseEntity handleProviderNotFoundException(Exception e) {
        return new ResponseEntity<>(new ErrorResponse("User is not authenticated.", null, null), HttpStatus.UNAUTHORIZED);
    }
}