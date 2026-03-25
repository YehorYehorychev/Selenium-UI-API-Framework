package com.yehorychev.selenium.hooks;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.helpers.Logger;
import io.cucumber.java.After;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import org.slf4j.MDC;

/**
 * API lifecycle hooks — sets up MDC logging context before @api scenarios and clears it after.
 *
 * <p>Global {@code RestAssured.*} state is intentionally NOT modified here: each scenario owns
 * an isolated {@link com.yehorychev.selenium.context.ApiContext} with its own
 * {@code RequestSpecification} (including {@code baseUri}, filters, and timeouts), so there is
 * nothing to configure globally.  Calling {@code RestAssured.reset()} or assigning
 * {@code RestAssured.baseURI} from a hook would create a parallel-safety race condition with the
 * 4 concurrent test threads sharing a single static RestAssured instance.
 */
public class ApiHooks {

    private static final Logger log = new Logger(ApiHooks.class);

    @Before(value = "@api", order = 1)
    public void setUpApi(Scenario scenario) {
        MDC.put("scenario", scenario.getName());
        MDC.put("scenarioId", scenario.getId());
        log.step("▶ [API] Setting up scenario: " + scenario.getName());
        log.info("RestAssured ready — baseURI: " + TestConfig.API_BASE_URL);
    }

    @After(value = "@api", order = 5)
    public void tearDownApi(Scenario scenario) {
        log.step("■ [API] Scenario complete: "
                + scenario.getName() + " — " + scenario.getStatus());
        // No RestAssured.reset() — each scenario owns its own RequestSpecification instance.
        MDC.clear();
    }
}