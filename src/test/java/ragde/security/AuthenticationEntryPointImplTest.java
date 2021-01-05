package ragde.security;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static org.mockito.Mockito.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class AuthenticationEntryPointImplTest {

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    /**
     * Should call sendError function
     */
    @Test
    public void commence() throws IOException, ServletException {
        final Integer statusServletResponse = HttpServletResponse.SC_UNAUTHORIZED;
        final String msg = "Unauthorized";
        final HttpServletResponse response = mock(HttpServletResponse.class);
        doNothing().when(response).sendError(statusServletResponse, msg);

        authenticationEntryPoint.commence(null, response, null);

        verify(response, times(1)).sendError(statusServletResponse, msg);
    }
}