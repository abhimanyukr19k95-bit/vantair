package com.vantair.api.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Opens up CORS so the static Vantair storefront (served from disk or a dev
 * server) can call the API from the browser. Origins are configurable via
 * {@code vantair.cors.allowed-origins}.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${vantair.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/api/**")
                .allowedOrigins(allowedOrigins)
                .allowedMethods("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                .allowedHeaders("*");
    }
}
