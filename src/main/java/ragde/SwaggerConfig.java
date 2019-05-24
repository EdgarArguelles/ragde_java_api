package ragde;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import springfox.bean.validators.configuration.BeanValidatorPluginsConfiguration;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Collections;

/**
 * Configure Swagger REST Documentation
 */
@Configuration
@EnableSwagger2
@Import({BeanValidatorPluginsConfiguration.class}) //swagger use javax.validation in doc
public class SwaggerConfig {

    @Value("${api-version}")
    private String API_VERSION;

    @Bean
    public Docket api() {
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage("ragde.controllers"))
                .build()
                .useDefaultResponseMessages(false)
                .apiInfo(apiInfo());
    }

    private ApiInfo apiInfo() {
        ApiInfo apiInfo = new ApiInfo(
                "RAGDE API",
                "Java REST API used by RAGDE.",
                API_VERSION,
                null,
                new Contact("Edgar Arguelles", null, null),
                null,
                null,
                Collections.emptyList());
        return apiInfo;
    }
}