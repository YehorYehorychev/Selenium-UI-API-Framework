package yehorychev.selenium.context;

import com.yehorychev.selenium.helpers.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Cross-step scenario state storage for Cucumber scenarios.
 *
 * <p>Injected via PicoContainer into step definitions to share data between steps
 * within the same scenario (e.g. storing a user ID from a registration step to use
 * in a verification step).
 *
 * <p>Analogous to closure-captured variables or Playwright fixture state that is
 * shared across test hooks and steps.
 *
 * <p>Usage:
 * <pre>{@code
 *   public class RegistrationSteps {
 *       private final ScenarioContext scenarioContext;
 *
 *       public RegistrationSteps(ScenarioContext scenarioContext) {
 *           this.scenarioContext = scenarioContext;
 *       }
 *
 *       @When("I register a new user")
 *       public void iRegisterANewUser() {
 *           String email = TestDataUtils.randomEmail();
 *           scenarioContext.set("registeredEmail", email);
 *           // perform registration...
 *       }
 *
 *       @Then("the user should receive a confirmation email")
 *       public void theUserShouldReceiveConfirmationEmail() {
 *           String email = scenarioContext.get("registeredEmail");
 *           // verify email sent...
 *       }
 *   }
 * }</pre>
 */
public class ScenarioContext {

    private static final Logger log = new Logger(ScenarioContext.class);

    private final Map<String, Object> context = new HashMap<>();

    // ── State management ─────────────────────────────────────────────────────

    /**
     * Stores a key-value pair in the scenario context.
     *
     * @param key   unique key for the value (e.g. {@code "userId"}, {@code "authToken"})
     * @param value the value to store (any object)
     */
    public void set(String key, Object value) {
        log.debug("ScenarioContext.set: " + key + " = " + value);
        context.put(key, value);
    }

    /**
     * Retrieves a value from the scenario context by key.
     *
     * @param key the key previously set via {@link #set(String, Object)}
     * @param <T> expected return type
     * @return the stored value, or {@code null} if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) context.get(key);
    }

    /**
     * Retrieves a value from the scenario context, returning a default if absent.
     *
     * @param key          the key previously set via {@link #set(String, Object)}
     * @param defaultValue value to return if the key is not found
     * @param <T>          expected return type
     * @return the stored value, or {@code defaultValue} if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        return (T) context.getOrDefault(key, defaultValue);
    }

    /**
     * Checks if the context contains the given key.
     *
     * @param key the key to check
     * @return {@code true} if the key exists
     */
    public boolean contains(String key) {
        return context.containsKey(key);
    }

    /**
     * Removes a key-value pair from the context.
     *
     * @param key the key to remove
     */
    public void remove(String key) {
        log.debug("ScenarioContext.remove: " + key);
        context.remove(key);
    }

    /**
     * Clears all scenario context state.
     * Typically called from an {@code @After} hook if needed.
     */
    public void clear() {
        log.debug("ScenarioContext.clear");
        context.clear();
    }

    /**
     * Returns the number of stored key-value pairs.
     *
     * @return context map size
     */
    public int size() {
        return context.size();
    }

    /**
     * Returns {@code true} if the context is empty.
     *
     * @return empty status
     */
    public boolean isEmpty() {
        return context.isEmpty();
    }

    /**
     * Returns a read-only view of all stored keys.
     *
     * @return set of context keys
     */
    public java.util.Set<String> keys() {
        return java.util.Collections.unmodifiableSet(context.keySet());
    }
}

