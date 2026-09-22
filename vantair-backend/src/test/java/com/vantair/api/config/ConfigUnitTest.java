package com.vantair.api.config;

import static org.assertj.core.api.Assertions.assertThat;

import io.swagger.v3.oas.models.OpenAPI;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

/** Plain unit tests for the small config/util classes. */
class ConfigUnitTest {

    @Test
    void apiException_factoriesSetStatusAndMessage() {
        assertThat(ApiException.notFound("nf").getStatus()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(ApiException.badRequest("br").getStatus()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(ApiException.conflict("cf").getStatus()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(ApiException.unauthorized("ua").getStatus()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(ApiException.notFound("nf").getMessage()).isEqualTo("nf");
    }

    @Test
    void openApiConfig_buildsInfoBlock() {
        OpenAPI api = new OpenApiConfig().vantairOpenApi();
        assertThat(api.getInfo().getTitle()).isEqualTo("Vantair API");
        assertThat(api.getInfo().getVersion()).isEqualTo("1.0.0");
        assertThat(api.getInfo().getContact().getEmail()).isEqualTo("vantair@zohomail.in");
        assertThat(api.getInfo().getLicense().getName()).isEqualTo("Proprietary");
    }

    @Test
    void globalExceptionHandler_apiException_buildsBody() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResponseEntity<Map<String, Object>> resp =
                handler.handleApi(ApiException.notFound("missing"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(resp.getBody())
                .containsEntry("status", 404)
                .containsEntry("error", "Not Found")
                .containsEntry("message", "missing")
                .containsKey("timestamp");
    }

    @Test
    void globalExceptionHandler_generic_is500() {
        GlobalExceptionHandler handler = new GlobalExceptionHandler();
        ResponseEntity<Map<String, Object>> resp =
                handler.handleGeneric(new RuntimeException("boom"));

        assertThat(resp.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(resp.getBody()).containsEntry("message", "boom");
    }
}
