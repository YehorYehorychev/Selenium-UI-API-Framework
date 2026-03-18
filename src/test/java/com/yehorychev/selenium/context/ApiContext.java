package com.yehorychev.selenium.context;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.helpers.Logger;
import io.restassured.RestAssured;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

/**
 * REST API context injected via PicoContainer — wraps RestAssured with session cookie sharing.
 * A {@link CookieFilter} is attached so cookies from signIn propagate to all subsequent requests.
 */
public class ApiContext {

    private static final Logger log = new Logger(ApiContext.class);

    private final RequestSpecification requestSpec;

    public ApiContext() {
        log.debug("Initialising ApiContext");
        this.requestSpec = RestAssured.given()
                .baseUri(TestConfig.API_BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(new CookieFilter())
                .log().ifValidationFails();
    }

    public RequestSpecification getSpec() {
        return requestSpec;
    }

    public RequestSpecification withAuth(String token) {
        return requestSpec.auth().oauth2(token);
    }

    public RequestSpecification withHeaders(Map<String, String> headers) {
        return requestSpec.headers(headers);
    }

    public String getBaseUri() {
        return TestConfig.API_BASE_URL;
    }

    public Response get(String endpoint) {
        log.step("GET " + endpoint);
        return requestSpec.get(endpoint);
    }

    public Response post(String endpoint, Object body) {
        log.step("POST " + endpoint);
        return requestSpec.body(body).post(endpoint);
    }

    public Response put(String endpoint, Object body) {
        log.step("PUT " + endpoint);
        return requestSpec.body(body).put(endpoint);
    }

    public Response patch(String endpoint, Object body) {
        log.step("PATCH " + endpoint);
        return requestSpec.body(body).patch(endpoint);
    }

    public Response delete(String endpoint) {
        log.step("DELETE " + endpoint);
        return requestSpec.delete(endpoint);
    }

    public Response graphql(String query, Map<String, Object> variables) {
        log.debug("GraphQL query body prepared");
        Map<String, Object> body = variables != null
                ? Map.of("query", query, "variables", variables)
                : Map.of("query", query);
        log.step("POST /api/graphql/v1/query");
        return requestSpec.body(body).post("/api/graphql/v1/query");
    }

    public Response graphql(String query) {
        return graphql(query, null);
    }
}
