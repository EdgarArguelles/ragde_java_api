package ragde.security.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.social.google.api.impl.GoogleTemplate;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;
import ragde.security.oauth.OAuthProvider;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.OAuthService;
import ragde.security.services.TokenService;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping(value = "/oauth/google")
public class GoogleCtrl {

    @Autowired
    @Qualifier("googleProvider")
    private OAuthProvider googleProvider;

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private SimpMessagingTemplate template;

    @GetMapping(value = "/signin")
    public ModelAndView signin(@RequestParam String state, HttpServletRequest request) {
        String authorizeUrl = googleProvider.getAuthorizeUrl(getCallback(request), state);
        return new ModelAndView(new RedirectView(authorizeUrl, true, true, true));
    }

    @GetMapping(value = "/callback")
    public RedirectView callback(@RequestParam String code, @RequestParam String state, HttpServletRequest request) throws IOException {
        AccessGrant accessGrant = googleProvider.getAccessGrant(getCallback(request), code);
        LoggedUser loggedUser = oAuthService.parseGoogleUser(new GoogleTemplate(accessGrant.getAccessToken()));
        String token = tokenService.createToken(loggedUser);
        loggedUser = tokenService.getLoggedUser(token);
        loggedUser.setToken(token);

        template.convertAndSend("/oauth/response/" + state, Map.of("loggedUser", loggedUser));
        return new RedirectView(getRootPath(request) + "/autoclose.html");
    }

    private String getCallback(HttpServletRequest request) {
        return getRootPath(request) + "/oauth/google/callback";
    }

    private String getRootPath(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort() + request.getContextPath();
    }
}