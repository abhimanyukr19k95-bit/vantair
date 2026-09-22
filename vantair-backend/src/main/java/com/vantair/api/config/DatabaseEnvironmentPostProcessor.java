package com.vantair.api.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

/**
 * Normalizes PostgreSQL datasource URLs from hosting environments (like Render.com)
 * where the database URL is provided in the format {@code postgres://...} or {@code postgresql://...}
 * into the standard JDBC format {@code jdbc:postgresql://...}.
 */
public class DatabaseEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String dbUrl = environment.getProperty("SPRING_DATASOURCE_URL");
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = environment.getProperty("DATABASE_URL");
        }
        if (dbUrl == null || dbUrl.isBlank()) {
            dbUrl = environment.getProperty("spring.datasource.url");
        }

        if (dbUrl != null && (dbUrl.startsWith("postgres://") || dbUrl.startsWith("postgresql://"))) {
            try {
                URI uri = new URI(dbUrl);
                String host = uri.getHost();
                int port = uri.getPort() == -1 ? 5432 : uri.getPort();
                String path = uri.getPath();
                String dbName = (path != null && path.length() > 1) ? path.substring(1) : "vantair";

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + dbName;
                Map<String, Object> props = new HashMap<>();
                props.put("spring.datasource.url", jdbcUrl);

                if (uri.getUserInfo() != null) {
                    String[] userInfo = uri.getUserInfo().split(":");
                    if (userInfo.length > 0 && (environment.getProperty("spring.datasource.username") == null
                            || environment.getProperty("spring.datasource.username").isBlank())) {
                        props.put("spring.datasource.username", userInfo[0]);
                    }
                    if (userInfo.length > 1 && (environment.getProperty("spring.datasource.password") == null
                            || environment.getProperty("spring.datasource.password").isBlank())) {
                        props.put("spring.datasource.password", userInfo[1]);
                    }
                }

                environment.getPropertySources().addFirst(new MapPropertySource("renderDbProps", props));
            } catch (Exception ignored) {
                // Fallback to original configuration if URI parsing fails
            }
        }
    }
}
