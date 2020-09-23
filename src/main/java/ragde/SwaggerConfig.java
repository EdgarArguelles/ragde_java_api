package ragde;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configure Swagger REST Documentation
 */
@Configuration
public class SwaggerConfig {

    @Value("${api-version}")
    private String API_VERSION;

    @Bean
    public OpenAPI api() {
        return new OpenAPI()
                .info(new Info()
                        .version(API_VERSION)
                        .title("RAGDE API")
                        .description("Java REST API used by RAGDE.")
                        .contact(new Contact().name("Edgar Arguelles"))
                );
    }
}