package ragde.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import javax.servlet.Filter;
import java.util.List;

/**
 * Configure application security
 * <p>
 * By default Spring Security has the follow Header configuration:
 * <p>
 * Cache-Control: no-cache, no-store, max-age=0, must-revalidate
 * Expires: 0
 * Pragma: no-cache
 * Transfer-Encoding: chunked
 * X-Content-Type-Options: nosniff
 * X-Frame-Options: DENY
 * X-XSS-Protection: 1; mode=block
 */
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    public static final List<String> ALLOWED_ORIGINS = List.of(
            "http://localhost:8080", "http://localhost:9000");

    @Autowired
    private AuthenticationEntryPoint authenticationEntryPoint;

    @Autowired
    private Filter authenticationTokenFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // Enable CORS configuration
        http.cors();

        http
                // Since you are not relying on cookies, you don't need to protect against cross site requests (CSRF)
                // (e.g. it would not be possible to <iframe> your site, generate a POST request and re-use the
                // existing authentication cookie because there will be none).
                .csrf().disable()

                // There is no need to keep a session store, the token is a self-contanined entity that conveys all the user information.
                // The rest of the state lives in cookies or local storage on the client side.
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

                // How to handle authentication errors
                .exceptionHandling().authenticationEntryPoint(authenticationEntryPoint).and()

                // Set security rules
                .authorizeRequests()
                .antMatchers(HttpMethod.GET, "/", "/index.html", "/autoclose.html", "/privacypolicy.html").permitAll()
                .antMatchers(HttpMethod.GET, "/swagger-ui/**", "/v3/**").permitAll()
                .antMatchers(HttpMethod.GET, "/h2-console/**").permitAll()
                .antMatchers(HttpMethod.GET, "/oauth/**").permitAll()
                .antMatchers(HttpMethod.GET, "/info/**").permitAll()
                .antMatchers(HttpMethod.GET, "/gui").permitAll()
                .antMatchers(HttpMethod.POST, "/graphql").permitAll()
                .anyRequest().authenticated()
                .and()

                // Add an AuthenticationTokenFilter to validate token before Spring security validates
                .addFilterBefore(authenticationTokenFilter, UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration corsConfiguration = new CorsConfiguration().applyPermitDefaultValues();
        corsConfiguration.setAllowedOrigins(ALLOWED_ORIGINS);
        corsConfiguration.setAllowedMethods(List.of("HEAD", "GET", "POST"));
        corsConfiguration.setAllowCredentials(true);
        source.registerCorsConfiguration("/**", corsConfiguration);
        return source;
    }
}