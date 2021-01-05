package ragde.security.controllers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ragde.security.oauth.OAuthProvider;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.OAuthService;
import ragde.security.services.TokenService;

import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.verify;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class GoogleCtrlTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    @Qualifier("googleProvider")
    private OAuthProvider googleProvider;

    @MockBean
    private OAuthService oAuthService;

    @MockBean
    private TokenService tokenService;

    @MockBean
    private SimpMessagingTemplate template;

    /**
     * Should call services
     */
    @Test
    public void signin() throws Exception {
        final String STATE = "state";
        final String CALLBACK = "http://localhost:80/oauth/google/callback";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/oauth/google/signin?state=" + STATE)
                        .contentType(MediaType.APPLICATION_JSON);

        final String authorizeUrl = "http://www.fake.com";
        given(googleProvider.getAuthorizeUrl(CALLBACK, STATE)).willReturn(authorizeUrl);

        mvc.perform(builder)
                .andExpect(status().isFound());

        verify(googleProvider, times(1)).getAuthorizeUrl(CALLBACK, STATE);
    }

    /**
     * Should call services
     */
    @Test
    public void callback() throws Exception {
        final String CODE = "code";
        final String STATE = "state";
        final String CALLBACK = "http://localhost:80/oauth/google/callback";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/oauth/google/callback?code=" + CODE + "&state=" + STATE)
                        .contentType(MediaType.APPLICATION_JSON);

        final AccessGrant accessGrant = new AccessGrant("token");
        final LoggedUser loggedUser = new LoggedUser("id", "R1");
        final String token = "TOKEN";
        final Map<String, Object> map = Map.of("loggedUser", loggedUser);
        given(googleProvider.getAccessGrant(CALLBACK, CODE)).willReturn(accessGrant);
        given(oAuthService.parseGoogleUser(any(GoogleTemplate.class))).willReturn(loggedUser);
        given(tokenService.createToken(loggedUser)).willReturn(token);
        given(tokenService.getLoggedUser(token)).willReturn(loggedUser);

        mvc.perform(builder)
                .andExpect(status().isFound());

        verify(googleProvider, times(1)).getAccessGrant(CALLBACK, CODE);
        verify(oAuthService, times(1)).parseGoogleUser(any(GoogleTemplate.class));
        verify(tokenService, times(1)).createToken(loggedUser);
        verify(tokenService, times(1)).getLoggedUser(token);
        verify(template, times(1)).convertAndSend("/oauth/response/" + STATE, map);
    }
}