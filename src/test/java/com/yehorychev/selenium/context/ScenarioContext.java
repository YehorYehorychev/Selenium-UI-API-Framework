package com.yehorychev.selenium.context;

import com.yehorychev.selenium.errors.FrameworkException;
import com.yehorychev.selenium.helpers.Logger;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Cross-step scenario state storage — injected via PicoContainer.
 * Share data between steps with {@code set(key, value)} / {@code get(key)}.
 *
 * <p>Use {@link com.yehorychev.selenium.context.ScenarioContextKeys} constants for all
 * framework-level keys to avoid typos and enable IDE navigation.
 * Feature-driven dynamic keys (e.g. from Gherkin parameters) may use inline strings.
 *
 * <h3>Usage</h3>
 * <pre>{@code
 * // In a Given step:
 * scenarioContext.set(ScenarioContextKeys.LAST_RESPONSE, response);
 *
 * // In a Then step:
 * Response r = scenarioContext.get(ScenarioContextKeys.LAST_RESPONSE, Response.class);
 * }</pre>
 */
public class ScenarioContext {

    private static final Logger log = new Logger(ScenarioContext.class);
    private final Map<String, Object> context = new HashMap<>();

    /**
     * Stores {@code value} under {@code key}, overwriting any previous value.
     * Logs the operation at DEBUG level.
     */
    public void set(String key, Object value) {
        log.debug("ScenarioContext.set: " + key + " = " + value);
        context.put(key, value);
    }

    /**
     * Retrieves the value stored under {@code key} with an unchecked cast to {@code T}.
     * Returns {@code null} if the key is absent.
     *
     * <p>Prefer {@link #get(String, Class)} for type-safe retrieval that fails fast with a
     * clear error message when the key is absent or has the wrong type.
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) context.get(key);
    }

    /**
     * Type-safe get — throws {@link com.yehorychev.selenium.errors.FrameworkException} when
     * the key is absent or the stored value cannot be cast to {@code type}, giving a clear
     * failure message instead of a silent {@code null} or a confusing
     * {@link ClassCastException} deep in a step.
     *
     * @param key  the context key (prefer {@link ScenarioContextKeys} constants)
     * @param type the expected type
     * @param <T>  inferred type
     * @return the stored value cast to {@code T}
     * @throws com.yehorychev.selenium.errors.FrameworkException if absent or wrong type
     */
    public <T> T get(String key, Class<T> type) {
        Object value = context.get(key);
        if (value == null) {
            throw new FrameworkException(
                    "ScenarioContext key '" + key + "' is not present. "
                    + "Make sure the step that sets this value runs before the one that reads it.");
        }
        if (!type.isInstance(value)) {
            throw new FrameworkException(
                    "ScenarioContext key '" + key + "' holds a " + value.getClass().getName()
                    + " but was expected to be " + type.getName() + ".");
        }
        return type.cast(value);
    }

    /**
     * Retrieves the value stored under {@code key}, or {@code defaultValue} if absent.
     * Uses an unchecked cast — prefer {@link #get(String, Class)} when type safety matters.
     */
    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        return (T) context.getOrDefault(key, defaultValue);
    }

    /** Returns {@code true} if a value is stored under {@code key}. */
    public boolean contains(String key) {
        return context.containsKey(key);
    }

    /** Removes the value stored under {@code key}. No-op if the key is absent. */
    public void remove(String key) {
        log.debug("ScenarioContext.remove: " + key);
        context.remove(key);
    }

    /**
     * Removes all stored values. Called automatically by {@code ScenarioContext}'s own
     * PicoContainer lifecycle — <em>do not call manually from step definitions</em>.
     */
    public void clear() {
        log.debug("ScenarioContext.clear");
        context.clear();
    }

    /** Returns the number of key/value pairs currently stored. */
    public int size() {
        return context.size();
    }

    /** Returns {@code true} when no key/value pairs are stored. */
    public boolean isEmpty() {
        return context.isEmpty();
    }

    /**
     * Returns an unmodifiable snapshot of all keys currently stored.
     * Useful for debugging step definitions that read unexpected keys.
     */
    public Set<String> keys() {
        return Collections.unmodifiableSet(context.keySet());
    }
}
