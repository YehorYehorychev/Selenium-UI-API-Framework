package com.yehorychev.selenium.helpers;

import com.yehorychev.selenium.config.TestConfig;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.RestAssuredConfig;

/**
 * Factory for a RestAssured {@link RestAssuredConfig} with connect/socket timeouts
 * driven by {@link TestConfig#API_TIMEOUT_MS}.
 *
 * <p>Uses {@link HttpClientConfig#setParam} rather than {@code httpClientFactory} to avoid
 * a RestAssured/Groovy {@code ClassCastException} that occurs when a custom
 * {@code HttpClientBuilder}-produced {@code InternalHttpClient} is cast to the legacy
 * {@code AbstractHttpClient} inside RestAssured's Groovy DSL. The {@code setParam} approach
 * configures RestAssured's own {@code DefaultHttpClient}, which is safe for both
 * fresh {@code RestAssured.given()} chains and stored {@code RequestSpecification} instances.
 *
 * <p>Shared by {@link AuthHelper} (sign-in / sign-out mutations) and
 * {@code ApiContext} (all scenario-level REST/GraphQL requests) so the timeout
 * policy is defined in exactly one place.
 */
public final class ApiClientConfig {

    private ApiClientConfig() {
    }

    /**
     * Returns a {@link RestAssuredConfig} with connection and socket timeouts set to
     * {@link TestConfig#API_TIMEOUT_MS}.
     */
    public static RestAssuredConfig withTimeouts() {
        int timeoutMs = (int) TestConfig.API_TIMEOUT_MS;
        return RestAssuredConfig.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.connection.timeout", timeoutMs)
                        .setParam("http.socket.timeout", timeoutMs));
    }
}



