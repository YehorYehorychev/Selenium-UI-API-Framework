package com.yehorychev.selenium.context;

import org.assertj.core.api.AbstractBooleanAssert;
import org.assertj.core.api.AbstractIntegerAssert;
import org.assertj.core.api.AbstractListAssert;
import org.assertj.core.api.AbstractLongAssert;
import org.assertj.core.api.AbstractStringAssert;
import org.assertj.core.api.ObjectAssert;
import org.assertj.core.api.SoftAssertions;

import java.util.List;

/**
 * Per-scenario AssertJ soft-assertion container injected by PicoContainer.
 *
 * <p>Accumulates assertion failures during a scenario without throwing immediately;
 * all failures are reported together by {@link com.yehorychev.selenium.hooks.SoftAssertionsHook}
 * at the end of every scenario — no manual cleanup required.
 *
 * <h3>Usage in a step class</h3>
 * <pre>{@code
 * public class MyPageSteps {
 *     private final MyPage myPage;
 *     private final ScenarioSoftAssertions soft;
 *
 *     public MyPageSteps(DriverContext ctx, ScenarioSoftAssertions soft) {
 *         this.myPage = new MyPage(ctx.getDriver());
 *         this.soft   = soft;
 *     }
 *
 *     @Then("the page details are correct")
 *     public void thePageDetailsAreCorrect() {
 *         soft.assertThat(myPage.getTitle()).contains("Mobalytics");
 *         soft.assertThat(myPage.isLoaded()).isTrue();
 *         soft.assertThat(myPage.getCardCount()).isGreaterThan(0);
 *         // step completes even if individual checks fail — all reported at scenario end
 *     }
 * }
 * }</pre>
 *
 * <p>For assertion types not covered by the convenience overloads below,
 * use {@link #getSoftAssertions()} to access the full AssertJ API.
 */
public final class ScenarioSoftAssertions {

    private final SoftAssertions softly = new SoftAssertions();

    // ── Convenience assertThat overloads ─────────────────────────────────────

    /**
     * Soft-asserts on a {@link String}. Chain fluent AssertJ conditions on the returned object.
     * Failures are collected and reported together at scenario end by
     * {@link com.yehorychev.selenium.hooks.SoftAssertionsHook}.
     */
    public AbstractStringAssert<?> assertThat(String actual) {
        return softly.assertThat(actual);
    }

    /**
     * Soft-asserts on a primitive {@code boolean}.
     * Typical usage: {@code soft.assertThat(page.isLoaded()).isTrue();}
     */
    public AbstractBooleanAssert<?> assertThat(boolean actual) {
        return softly.assertThat(actual);
    }

    /**
     * Soft-asserts on a boxed {@link Boolean}.
     * Handles {@code null} correctly — use {@code isNull()} or {@code isTrue()} / {@code isFalse()}.
     */
    public AbstractBooleanAssert<?> assertThat(Boolean actual) {
        return softly.assertThat(actual);
    }

    /**
     * Soft-asserts on a primitive {@code int}.
     * Typical usage: {@code soft.assertThat(page.getCardCount()).isGreaterThan(0);}
     */
    public AbstractIntegerAssert<?> assertThat(int actual) {
        return softly.assertThat(actual);
    }

    /** Soft-asserts on a boxed {@link Integer}. */
    public AbstractIntegerAssert<?> assertThat(Integer actual) {
        return softly.assertThat(actual);
    }

    /** Soft-asserts on a primitive {@code long}. */
    public AbstractLongAssert<?> assertThat(long actual) {
        return softly.assertThat(actual);
    }

    /** Soft-asserts on a boxed {@link Long}. */
    public AbstractLongAssert<?> assertThat(Long actual) {
        return softly.assertThat(actual);
    }

    /**
     * Soft-asserts on a {@link List}.
     * Typical usage: {@code soft.assertThat(titles).isNotEmpty().contains("LoL");}
     */
    public <T> AbstractListAssert<?, List<? extends T>, T, ObjectAssert<T>> assertThat(List<? extends T> actual) {
        return softly.assertThat(actual);
    }

    /** Fallback for any type not covered by the specific overloads above. */
    public <T> ObjectAssert<T> assertThat(T actual) {
        return softly.assertThat(actual);
    }

    // ── Full API access ───────────────────────────────────────────────────────

    /**
     * Returns the underlying {@link SoftAssertions} for assertion types not covered
     * by the convenience overloads (e.g. {@code Path}, {@code Optional}, {@code Map}).
     */
    public SoftAssertions getSoftAssertions() {
        return softly;
    }

    // ── Framework lifecycle ───────────────────────────────────────────────────

    /**
     * Evaluates all accumulated assertions and throws {@code AssertionError} listing
     * every failure if any were recorded. Called automatically by
     * {@link com.yehorychev.selenium.hooks.SoftAssertionsHook} — do not call from step classes.
     */
    public void assertAll() {
        softly.assertAll();
    }
}
