package com.vantair.api.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * API metadata shown at the top of Swagger UI (/swagger-ui.html).
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI vantairOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Vantair API")
                .version("1.0.0")
                .description("""
                        REST backend for the Vantair e-commerce storefront.

                        Covers catalog, coupons, customer accounts, the order + delivery-OTP
                        lifecycle, and the admin panel.

                        NOTE: authentication is not wired yet — every endpoint is open, and the
                        admin endpoints must be secured before any real deployment.""")
                .contact(new Contact().name("Vantair").email("vantair@zohomail.in"))
                .license(new License().name("Proprietary")));
    }
}
