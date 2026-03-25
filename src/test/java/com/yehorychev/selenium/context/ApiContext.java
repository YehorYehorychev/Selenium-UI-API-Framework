package com.yehorychev.selenium.context;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.data.TestData;
import com.yehorychev.selenium.errors.ApiException;
import com.yehorychev.selenium.helpers.ApiClientConfig;
import com.yehorychev.selenium.helpers.Logger;
import io.restassured.RestAssured;
import io.restassured.filter.cookie.CookieFilter;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
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
                .config(ApiClientConfig.withTimeouts())
                .filter(new CookieFilter())
                .log().ifValidationFails();

        if (!TestConfig.HEADLESS) {
            requestSpec.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        }
    }

    public RequestSpecification getSpec() {
        return requestSpec;
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
        log.step("POST " + TestData.UrlPatterns.API_GRAPHQL);
        return requestSpec.body(body).post(TestData.UrlPatterns.API_GRAPHQL);
    }

    public Response graphql(String query) {
        return graphql(query, null);
    }

    /**
     * Asserts that {@code response} has a 2xx status code.
     * Throws {@link ApiException} (classified in Allure {@code categories.json} as an API failure)
     * for any 4xx / 5xx response.
     *
     * <p>Call this from step definitions that expect a successful response so that API
     * failures surface as typed {@code ApiException}s rather than silent assertion gaps.
     *
     * @param response the RestAssured response to validate
     * @param endpoint the endpoint path used for the request (included in the exception message)
     * @throws ApiException if the HTTP status is 400 or above
     */
    public void assertSuccessful(Response response, String endpoint) {
        int status = response.getStatusCode();
        if (status >= 400) {
            throw new ApiException(status, response.getBody().asString(), endpoint);
        }
    }
}
