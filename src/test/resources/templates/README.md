# Test Authoring Templates

This directory contains **beginner-friendly templates** that show exactly how to write a new test
in this framework. None of the files here are ever executed — they exist purely as documented
examples you can copy, rename, and fill in with real locators and logic.

---

## Where the templates live

| Template file | Location | What it teaches |
|---|---|---|
| `ExamplePage.java` | `src/main/java/.../pages/templates/` | How to build a Page Object |
| `ExampleComponent.java` | `src/main/java/.../components/templates/` | How to build a reusable Component |
| `ExampleSteps.java` | `src/test/java/.../templates/` | How to write step definitions |
| `example.feature` *(this dir)* | `src/test/resources/templates/` | How to write a feature file |

> **Why aren't these executed?**
> - `CucumberRunner` scans `src/test/resources/features/` for feature files — this
>   `templates/` folder sits outside that path, so `example.feature` is never picked up.
> - `CucumberRunner` loads glue from `com.yehorychev.selenium.steps` and `…hooks` —
>   `ExampleSteps.java` lives in `com.yehorychev.selenium.templates`, so it is never
>   registered as a step definition.

---

## Quick-start checklist: adding a new UI test in 5 steps

### 1 — Create the Page Object

```
src/main/java/com/yehorychev/selenium/pages/MyPage.java
```

Copy `ExamplePage.java`, remove `.templates` from the package, and follow the inline comments:

```java
package com.yehorychev.selenium.pages;   // ← updated package

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MyPage extends BasePage {

    private static final By PAGE_HEADING = By.cssSelector("h1.my-heading");

    public MyPage(WebDriver driver) {
        super(driver);
    }

    public void open() {
        open(TestConfig.BASE_URL + "/my-path");
        waitForVisible(PAGE_HEADING);
    }

    public boolean isLoaded()      { return isVisible(PAGE_HEADING); }
    public String  getHeadingText(){ return getText(PAGE_HEADING);   }
}
```

**Locator priority (best → worst):**
1. `[data-testid="..."]` — explicit, stable, change-proof
2. `By.id("...")` — fast, unique
3. `By.cssSelector("tag.class")` — readable, widely supported
4. `By.xpath("//button[normalize-space(.)='Submit']")` — use only when CSS cannot express it
5. ❌ Avoid hashed class names (`class="a3f_x"`) — they change on every deploy

---

### 2 — Create a Component (only if needed)

```
src/main/java/com/yehorychev/selenium/components/MyComponent.java
```

Components are for **reusable UI sections** (nav bar, footer, modal, filter panel) that appear
on multiple pages. If the UI section only appears on one page, keep it inside the page object.

```java
public class MyComponent extends BaseComponent {
    private static final By ROOT = By.cssSelector("section.my-section");
    private static final By ITEM = By.cssSelector("li.item");

    public MyComponent(WebDriver driver) {
        super(driver, ROOT);              // ← root locator scopes all inner searches
    }

    public int getItemCount() {
        return findElements(ITEM).size(); // ← scoped to ROOT automatically
    }
}
```

---

### 3 — Create the step definition class

```
src/test/java/com/yehorychev/selenium/steps/MyPageSteps.java
```

Copy `ExampleSteps.java`, update the package and imports, then follow the inline comments.

```java
package com.yehorychev.selenium.steps;   // ← updated package

import com.yehorychev.selenium.context.DriverContext;
import com.yehorychev.selenium.context.ScenarioSoftAssertions;
import com.yehorychev.selenium.pages.MyPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

@Feature("UI — My Page")
@Story("My Feature")
public class MyPageSteps {

    private final MyPage myPage;
    private final ScenarioSoftAssertions soft;

    // PicoContainer wires these automatically — never use `new` for context objects
    public MyPageSteps(DriverContext ctx, ScenarioSoftAssertions soft) {
        this.myPage = new MyPage(ctx.getDriver());
        this.soft   = soft;
    }

    @Given("I open my page")
    public void iOpenMyPage() { myPage.open(); }

    @Then("my page is loaded")
    public void myPageIsLoaded() {
        assertTrue(myPage.isLoaded(), "Expected the page heading to be visible");
    }

    @Then("the heading should contain {string}")
    public void theHeadingShouldContain(String expected) {
        soft.assertThat(myPage.getHeadingText())
                .as("Heading should contain \"%s\"", expected)
                .containsIgnoringCase(expected);
    }
}
```

#### PicoContainer injection cheat-sheet

| You need | Add this constructor parameter |
|---|---|
| A WebDriver / browser | `DriverContext driverContext` |
| A REST API client | `ApiContext api` |
| Cross-step state storage | `ScenarioContext scenarioContext` |
| Soft (non-stopping) assertions | `ScenarioSoftAssertions soft` |

> ⚠️ **Never** add a no-arg constructor — it breaks PicoContainer silently.

#### Hard vs. soft assertions

| Use | When |
|---|---|
| `assertTrue(condition, "message")` | The test cannot continue if this fails (e.g. page didn't load) |
| `soft.assertThat(value).isEqualTo(expected)` | Multiple checks in one step — all run even if one fails |

---

### 4 — Create the feature file

```
src/test/resources/features/ui/my-page.feature
```

Copy `example.feature`, place it in `features/ui/` (or `features/api/` / `features/e2e/`),
and adapt it. Every scenario **must** have at least one tag.

```gherkin
@regression @ui
Feature: My Page

  Background:
    Given I open my page

  @smoke @critical
  Scenario: Page loads successfully
    Then my page is loaded

  @regression
  Scenario: Heading shows correct text
    Then the heading should contain "Expected Text"

  @regression
  Scenario Outline: Multiple data sets
    Then the heading should contain "<text>"

    Examples:
      | text          |
      | Expected Text |
      | Other Text    |
```

#### Tag reference

| Tag | Meaning |
|---|---|
| `@smoke` | Fast critical-path check — runs on every push |
| `@regression` | Full suite — PR gate |
| `@ui` | Selenium test — DriverHooks starts a browser |
| `@api` | REST/GraphQL test — no browser launched |
| `@authenticated` | AuthHooks signs in via GraphQL before the scenario |
| `@critical` | Must pass before deploy |
| `@wip` | Excluded from CI — use while actively writing a scenario |

---

### 5 — Run only your new test

```bash
# Run just your new scenarios by tag
mvn test -Dcucumber.filter.tags="@smoke" -DHEADLESS=false

# Run with the browser visible (great for debugging)
mvn test -Dcucumber.filter.tags="@regression" -DHEADLESS=false

# Generate and open the Allure report after the run
mvn allure:serve
```

---

## Framework mental model

```
feature file  →  steps class  →  page/component  →  BasePage / BaseComponent
   (Gherkin)     (glue layer)     (UI actions)         (wait helpers, driver)
```

- **Feature file** — describes WHAT to test in plain English.
- **Steps class** — bridges Gherkin to Java; stays thin (delegate to page objects).
- **Page / Component** — encapsulates HOW to interact with the UI; no assertions here.
- **BasePage / BaseComponent** — provides `click()`, `type()`, `isVisible()`, waits, etc.

> The golden rule: **steps orchestrate, pages interact, base classes wait.**

---

## Common mistakes to avoid

| ❌ Don't | ✅ Do instead |
|---|---|
| `Thread.sleep(3000)` | `waitForVisible(locator)` or `isVisible(locator)` |
| `driver.findElement(locator).click()` | `click(locator)` (includes wait) |
| `new DriverContext()` in a steps class | Let PicoContainer inject it via constructor |
| Assertions inside a page object | Keep assertions in steps only |
| Mutable `static` fields in steps | Use `ScenarioContext` for cross-step state |
| No tags on a scenario | Add at least `@regression` or `@smoke` |
| `soft.assertAll()` at the end of a step | `SoftAssertionsHook` calls it automatically |

