package yehorychev.selenium.context;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.helpers.Logger;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import java.util.Map;

/**
 * REST API context for Cucumber scenarios — wraps RestAssured {@link RequestSpecification}.
 *
 * <p>Injected via PicoContainer into API-focused step definitions. Pre-configures
 * base URI, headers, logging, and provides convenience wrappers for GET / POST / PUT / DELETE.
 *
 * <p>Analogous to Playwright's {@code request} fixture or Axios/fetch client instances.
 *
 * <p>Usage in step definitions:
 * <pre>{@code
 *   public class ApiSteps {
 *       private final ApiContext api;
 *
 *       public ApiSteps(ApiContext api) {
 *           this.api = api;
 *       }
 *
 *       @When("I fetch user profile via API")
 *       public void iFetchUserProfile() {
 *           Response response = api.get("/api/users/me");
 *           // assertions...
 *       }
 *   }
 * }</pre>
 */
public class ApiContext {

    private static final Logger log = new Logger(ApiContext.class);

    private final RequestSpecification requestSpec;

    // ── Constructor ──────────────────────────────────────────────────────────

    /**
     * Initializes the API context with sensible defaults from {@link TestConfig}.
     */
    public ApiContext() {
        log.debug("Initialising ApiContext");
        this.requestSpec = RestAssured.given()
                .baseUri(TestConfig.API_BASE_URL)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .log().ifValidationFails();
    }

    // ── Accessors ────────────────────────────────────────────────────────────

    /**
     * Returns the underlying {@link RequestSpecification} for advanced customisation.
     *
     * @return RestAssured request spec
     */
    public RequestSpecification getSpec() {
        return requestSpec;
    }

    /**
     * Clones the current spec and adds an authorization header.
     *
     * @param token bearer token
     * @return new {@link RequestSpecification} with the auth header
     */
    public RequestSpecification withAuth(String token) {
        return requestSpec.auth().oauth2(token);
    }

    /**
     * Clones the current spec and adds custom headers.
     *
     * @param headers map of header names to values
     * @return new {@link RequestSpecification} with the headers
     */
    public RequestSpecification withHeaders(Map<String, String> headers) {
        return requestSpec.headers(headers);
    }

    // ── HTTP verbs ───────────────────────────────────────────────────────────

    /**
     * Sends a GET request to the given endpoint.
     *
     * @param endpoint relative or absolute API path
     * @return {@link Response} object for assertions
     */
    public Response get(String endpoint) {
        log.step("GET " + endpoint);
        return requestSpec.get(endpoint);
    }

    /**
     * Sends a POST request with a JSON body.
     *
     * @param endpoint relative or absolute API path
     * @param body     request body (will be serialised to JSON)
     * @return {@link Response} object for assertions
     */
    public Response post(String endpoint, Object body) {
        log.step("POST " + endpoint);
        return requestSpec.body(body).post(endpoint);
    }

    /**
     * Sends a PUT request with a JSON body.
     *
     * @param endpoint relative or absolute API path
     * @param body     request body (will be serialised to JSON)
     * @return {@link Response} object for assertions
     */
    public Response put(String endpoint, Object body) {
        log.step("PUT " + endpoint);
        return requestSpec.body(body).put(endpoint);
    }

    /**
     * Sends a PATCH request with a JSON body.
     *
     * @param endpoint relative or absolute API path
     * @param body     request body (will be serialised to JSON)
     * @return {@link Response} object for assertions
     */
    public Response patch(String endpoint, Object body) {
        log.step("PATCH " + endpoint);
        return requestSpec.body(body).patch(endpoint);
    }

    /**
     * Sends a DELETE request.
     *
     * @param endpoint relative or absolute API path
     * @return {@link Response} object for assertions
     */
    public Response delete(String endpoint) {
        log.step("DELETE " + endpoint);
        return requestSpec.delete(endpoint);
    }

    // ── GraphQL ──────────────────────────────────────────────────────────────

    /**
     * Sends a GraphQL query or mutation to {@code /api/graphql/v1/query}.
     *
     * @param query     GraphQL query string (use constants from {@link com.yehorychev.selenium.data.GraphqlQueries})
     * @param variables map of variable names to values (nullable)
     * @return {@link Response} object for assertions
     */
    public Response graphql(String query, Map<String, Object> variables) {
        log.step("GraphQL query");
        Map<String, Object> body = variables != null
                ? Map.of("query", query, "variables", variables)
                : Map.of("query", query);
        return post("/api/graphql/v1/query", body);
    }

    /**
     * Sends a GraphQL query without variables.
     *
     * @param query GraphQL query string
     * @return {@link Response} object for assertions
     */
    public Response graphql(String query) {
        return graphql(query, null);
    }
}

