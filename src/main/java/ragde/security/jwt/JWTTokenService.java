package ragde.security.jwt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ragde.security.pojos.LoggedUser;
import ragde.security.services.SecurityService;
import ragde.security.services.TokenService;

import java.io.IOException;
import java.util.Date;

@Service
public class JWTTokenService implements TokenService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Integer expirationTime;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private SecurityService securityService;

    @Override
    public String createToken(LoggedUser loggedUser) throws JsonProcessingException {
        if (loggedUser == null || loggedUser.getToken() != null) {
            return null;
        }

        String json = mapper.writeValueAsString(loggedUser);

        String token = Jwts.builder()
                .setSubject(json)
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
        return token;
    }

    @Override
    public String refreshToken() throws JsonProcessingException {
        LoggedUser loggedUser = securityService.getLoggedUser();
        return createToken(loggedUser);
    }

    @Override
    public LoggedUser getLoggedUser(String token) throws IOException {
        String userString = Jwts.parser()
                .setSigningKey(secret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
        if (userString != null) {
            return mapper.readValue(userString, LoggedUser.class);
        } else {
            return null;
        }
    }
}