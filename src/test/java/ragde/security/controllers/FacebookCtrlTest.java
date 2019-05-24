package ragde.security.controllers;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.social.facebook.api.impl.FacebookTemplate;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.test.context.junit4.SpringRunner;
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
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class FacebookCtrlTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    @Qualifier("facebookProvider")
    private OAuthProvider facebookProvider;

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
        final String CALLBACK = "http://localhost:80/oauth/facebook/callback";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/oauth/facebook/signin?state=" + STATE)
                        .contentType(MediaType.APPLICATION_JSON);

        final String authorizeUrl = "http://www.fake.com";
        given(facebookProvider.getAuthorizeUrl(CALLBACK, STATE)).willReturn(authorizeUrl);

        mvc.perform(builder)
                .andExpect(status().isFound());

        verify(facebookProvider, times(1)).getAuthorizeUrl(CALLBACK, STATE);
    }

    /**
     * Should not call services
     */
    @Test
    public void callbackWhenNotCode() throws Exception {
        final String STATE = "state";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/oauth/facebook/callback?state=" + STATE)
                        .contentType(MediaType.APPLICATION_JSON);

        mvc.perform(builder)
                .andExpect(status().isFound());

        verify(facebookProvider, never()).getAccessGrant(any(), any());
        verify(oAuthService, never()).parseFacebookUser(any());
        verify(tokenService, never()).createToken(any());
        verify(tokenService, never()).getLoggedUser(any());
        verify(template, never()).convertAndSend(any(), any(Object.class));
    }

    /**
     * Should call services
     */
    @Test
    public void callback() throws Exception {
        final String CODE = "code";
        final String STATE = "state";
        final String CALLBACK = "http://localhost:80/oauth/facebook/callback";
        final MockHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.get("/oauth/facebook/callback?code=" + CODE + "&state=" + STATE)
                        .contentType(MediaType.APPLICATION_JSON);

        final AccessGrant accessGrant = new AccessGrant("token");
        final LoggedUser loggedUser = new LoggedUser("id", "R1");
        final String token = "TOKEN";
        final Map<String, Object> map = Map.of("loggedUser", loggedUser);
        given(facebookProvider.getAccessGrant(CALLBACK, CODE)).willReturn(accessGrant);
        given(oAuthService.parseFacebookUser(any(FacebookTemplate.class))).willReturn(loggedUser);
        given(tokenService.createToken(loggedUser)).willReturn(token);
        given(tokenService.getLoggedUser(token)).willReturn(loggedUser);

        mvc.perform(builder)
                .andExpect(status().isFound());

        verify(facebookProvider, times(1)).getAccessGrant(CALLBACK, CODE);
        verify(oAuthService, times(1)).parseFacebookUser(any(FacebookTemplate.class));
        verify(tokenService, times(1)).createToken(loggedUser);
        verify(tokenService, times(1)).getLoggedUser(token);
        verify(template, times(1)).convertAndSend("/oauth/response/" + STATE, map);
    }
}