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
 */
public class ScenarioContext {

    private static final Logger log = new Logger(ScenarioContext.class);
    private final Map<String, Object> context = new HashMap<>();

    public void set(String key, Object value) {
        log.debug("ScenarioContext.set: " + key + " = " + value);
        context.put(key, value);
    }

    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) context.get(key);
    }

    /**
     * Type-safe get — throws {@link FrameworkException} when the key is absent or the stored
     * value cannot be cast to {@code type}, giving a clear failure message instead of a
     * silent {@code null} or a confusing {@link ClassCastException} deep in a step.
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

    @SuppressWarnings("unchecked")
    public <T> T getOrDefault(String key, T defaultValue) {
        return (T) context.getOrDefault(key, defaultValue);
    }

    public boolean contains(String key) {
        return context.containsKey(key);
    }

    public void remove(String key) {
        log.debug("ScenarioContext.remove: " + key);
        context.remove(key);
    }

    public void clear() {
        log.debug("ScenarioContext.clear");
        context.clear();
    }

    public int size() {
        return context.size();
    }

    public boolean isEmpty() {
        return context.isEmpty();
    }

    public Set<String> keys() {
        return Collections.unmodifiableSet(context.keySet());
    }
}
