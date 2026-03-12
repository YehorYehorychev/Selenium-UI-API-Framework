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
 * REST API context for Cucumber scenarios — wraps RestAssured RequestSpecification.
 *
 * Injected via PicoContainer into API-focused step definitions.
 * Pre-configures base URI, content type, and logging for every request.
 * Uses a CookieFilter so session cookies from signIn are automatically sent on all
 * subsequent requests within the same scenario (cookie jar pattern).
 * Provides convenience wrappers for GET / POST / PUT / PATCH / DELETE / GraphQL.
 */
public class ApiContext {

    private static final Logger log = new Logger(ApiContext.class);

    private final RequestSpecification requestSpec;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Initializes the API context with defaults from TestConfig.
     * A CookieFilter is added so session cookies (e.g. from signIn) are
     * automatically propagated to all subsequent requests.
     */
    public ApiContext() {
        log.debug("Initialising ApiContext");
        this.requestSpec = RestAssured.given()
                .baseUri(TestConfig.API_BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .filter(new CookieFilter())
                .log().ifValidationFails();
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    /**
     * Returns the underlying RequestSpecification for advanced customisation.
     *
     * @return RestAssured request spec
     */
    public RequestSpecification getSpec() {
        return requestSpec;
    }

    /**
     * Returns a spec clone with a Bearer token authorization header.
     *
     * @param token bearer token
     * @return new RequestSpecification with the auth header
     */
    public RequestSpecification withAuth(String token) {
        return requestSpec.auth().oauth2(token);
    }

    /**
     * Returns a spec clone with additional custom headers.
     *
     * @param headers map of header names to values
     * @return new RequestSpecification with the headers
     */
    public RequestSpecification withHeaders(Map<String, String> headers) {
        return requestSpec.headers(headers);
    }

    /**
     * Returns the configured base URI for this API context.
     *
     * @return base URI string
     */
    public String getBaseUri() {
        return TestConfig.API_BASE_URL;
    }

    // ── HTTP methods ─────────────────────────────────────────────────────────

    /**
     * Sends a GET request.
     *
     * @param endpoint relative or absolute API path
     * @return Response for assertions
     */
    public Response get(String endpoint) {
        log.step("GET " + endpoint);
        return requestSpec.get(endpoint);
    }

    /**
     * Sends a POST request with a JSON body.
     *
     * @param endpoint relative or absolute API path
     * @param body     request body (serialised to JSON)
     * @return Response for assertions
     */
    public Response post(String endpoint, Object body) {
        log.step("POST " + endpoint);
        return requestSpec.body(body).post(endpoint);
    }

    /**
     * Sends a PUT request with a JSON body.
     *
     * @param endpoint relative or absolute API path
     * @param body     request body (serialised to JSON)
     * @return Response for assertions
     */
    public Response put(String endpoint, Object body) {
        log.step("PUT " + endpoint);
        return requestSpec.body(body).put(endpoint);
    }

    /**
     * Sends a PATCH request with a JSON body.
     *
     * @param endpoint relative or absolute API path
     * @param body     request body (serialised to JSON)
     * @return Response for assertions
     */
    public Response patch(String endpoint, Object body) {
        log.step("PATCH " + endpoint);
        return requestSpec.body(body).patch(endpoint);
    }

    /**
     * Sends a DELETE request.
     *
     * @param endpoint relative or absolute API path
     * @return Response for assertions
     */
    public Response delete(String endpoint) {
        log.step("DELETE " + endpoint);
        return requestSpec.delete(endpoint);
    }

    // ── GraphQL ──────────────────────────────────────────────────────────────

    /**
     * Sends a GraphQL query or mutation to /api/graphql/v1/query.
     * Session cookies from a previous signIn are automatically included via CookieFilter.
     *
     * @param query     GraphQL query or mutation string
     * @param variables variable map (nullable)
     * @return Response for assertions
     */
    public Response graphql(String query, Map<String, Object> variables) {
        log.debug("GraphQL query body prepared");
        Map<String, Object> body = variables != null
                ? Map.of("query", query, "variables", variables)
                : Map.of("query", query);
        log.step("POST /api/graphql/v1/query");
        return requestSpec.body(body).post("/api/graphql/v1/query");
    }

    /**
     * Sends a GraphQL query without variables.
     *
     * @param query GraphQL query or mutation string
     * @return Response for assertions
     */
    public Response graphql(String query) {
        return graphql(query, null);
    }
}

