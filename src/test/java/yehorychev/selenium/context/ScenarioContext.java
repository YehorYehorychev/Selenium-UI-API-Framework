package yehorychev.selenium.context;

import com.yehorychev.selenium.helpers.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Cross-step scenario state storage — shares data between steps in the same scenario.
 *
 * Injected via PicoContainer into step definitions. Typical use: store a value
 * produced in one step (e.g. a user ID) and read it in a later step.
 *
 * Usage:
 *   scenarioContext.set("registeredEmail", email);   // in a @When step
 *   String email = scenarioContext.get("registeredEmail"); // in a @Then step
 */
public class ScenarioContext {

    private static final Logger log = new Logger(ScenarioContext.class);

    private final Map<String, Object> context = new HashMap<>();

    // ── State management ─────────────────────────────────────────────────────

    /**
     * Stores a value under the given key.
     *
     * @param key   unique identifier (e.g. "userId", "authToken")
     * @param value any object to store
     */
    public void set(String key, Object value) {
        log.debug("ScenarioContext.set: " + key + " = " + value);
        context.put(key, value);
    }

    /**
     * Retrieves a stored value by key. Returns null if not found.
     *
     * @param key the key used in set()
     * @param <T> expected return type
     * @return the stored value, or null
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) context.get(key);
    }

    /**
     * Retrieves a stored value, returning defaultValue if the key is absent.
     *
     * @param key          the key used in set()
     * @param defaultValue fallback value
     * @param <T>          expected return type
     * @return the stored value, or defaultValue
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        return (T) context.getOrDefault(key, defaultValue);
    }

    /**
     * Returns true if the context contains the given key.
     *
     * @param key key to check
     * @return true if present
     */
    public boolean contains(String key) {
        return context.containsKey(key);
    }

    /**
     * Removes a key-value pair from the context.
     *
     * @param key key to remove
     */
    public void remove(String key) {
        log.debug("ScenarioContext.remove: " + key);
        context.remove(key);
    }

    /**
     * Clears all stored state. Useful in @After hooks for explicit cleanup.
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
     * Returns true if no values are stored.
     *
     * @return empty status
     */
    public boolean isEmpty() {
        return context.isEmpty();
    }

    /**
     * Returns a read-only view of all stored keys.
     *
     * @return unmodifiable set of keys
     */
    public java.util.Set<String> keys() {
        return java.util.Collections.unmodifiableSet(context.keySet());
    }
}
