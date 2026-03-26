# Writing Your First Test — Onboarding Guide

Welcome! This guide walks you through adding a brand-new end-to-end test to the framework from scratch.
You don't need to understand every file in the project — just follow the steps below and you'll have
a working test in about 15 minutes.

---

## How the framework works (the 30-second version)

Every test in this project is made of exactly **three pieces** that talk to each other:

```
  ┌─────────────────────────────────────────────────────────────────┐
  │  example.feature          (WHAT to test — plain English)        │
  │    ↓                                                            │
  │  ExampleSteps.java        (bridge — maps English → Java)        │
  │    ↓                                                            │
  │  ExamplePage.java         (HOW to interact with the browser)    │
  └─────────────────────────────────────────────────────────────────┘
```

| File | Your job | Rule |
|---|---|---|
| **Feature file** | Describe what the user does and what you expect | Plain English only, no Java |
| **Steps class** | Connect each sentence to a Java method | Thin — just delegate to the page |
| **Page Object** | Drive the browser (click, type, read) | No assertions here |

> **Golden rule:** steps *orchestrate*, pages *interact*, base classes *wait*.

---

## Before you start

Make sure the project runs on your machine:

```bash
cp .env.example .env          # create your local config (only needed once)
# fill in TEST_USER_LOGIN and TEST_USER_PASSWORD in .env

mvn test -Dcucumber.filter.tags="@smoke" -DHEADLESS=false   # smoke suite, browser visible
```

If the smoke suite passes you're ready to write your own test.

---

## Step 1 — Write the feature file first

Start here. The feature file is plain English — no Java required.
It forces you to think about **what** you want to test before worrying about **how**.

Create your file in:
```
src/test/resources/features/ui/my-page.feature
```

```gherkin
# Short comment explaining what this feature covers.

@regression @ui
Feature: My Page

  # Background runs before EVERY scenario in this file.
  # Use it to navigate to the starting page.
  Background:
    Given I open my page

  # The most important scenario — does the page even load?
  @smoke @critical
  Scenario: Page loads successfully
    Then my page is loaded

  # A normal content check.
  @regression
  Scenario: Page heading shows the right text
    Then the heading should contain "Expected Text"

  # Run the same scenario with different data using a table.
  @regression
  Scenario Outline: Multiple search terms all return results
    When  I search for "<term>" and submit
    Then  there should be at least 1 results

    Examples:
      | term    |
      | Ahri    |
      | Thresh  |
```

### What's happening here

- **`@regression @ui`** on the `Feature` line apply to *every* scenario below.
- Individual scenarios can add more tags (`@smoke`, `@critical`).
- **`Background`** is like a `@Before` for the feature file — it runs before each scenario.
- **`Scenario Outline`** + **`Examples`** generates one independent scenario per table row.
- Every scenario must have **at least one tag** — the runner uses tags to decide what to run.

### Tag reference

| Tag | When to use |
|---|---|
| `@smoke` | Fast, must-pass check — runs on every push |
| `@regression` | Full suite — required before merging to main |
| `@ui` | Any test that opens a browser |
| `@api` | REST / GraphQL test — no browser is launched |
| `@authenticated` | The scenario needs a logged-in user (AuthHooks handles sign-in automatically) |
| `@critical` | Must pass before deploy |
| `@wip` | Excluded from CI — use while you're still writing the scenario |

---

## Step 2 — Create the Page Object

The Page Object encapsulates everything about **one page of the application**.
It knows which elements are on the page and how to interact with them.
It has **zero assertions** — that is the steps class's job.

Create your file in:
```
src/main/java/com/yehorychev/selenium/pages/MyPage.java
```

```java
package com.yehorychev.selenium.pages;

import com.yehorychev.selenium.config.TestConfig;
import com.yehorychev.selenium.data.TestData;
import com.yehorychev.selenium.pages.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MyPage extends BasePage {

    // ── Locators ─────────────────────────────────────────────────────────────
    // Always private static final.
    // Name them after what they REPRESENT, not what they look like in the DOM.

    private static final By PAGE_HEADING  = By.cssSelector("h1.page-title");
    private static final By SEARCH_INPUT  = By.cssSelector("[data-testid='search-input']");
    private static final By SUBMIT_BUTTON = By.xpath(
            "//button[@type='submit' and normalize-space(.)='Search']");
    private static final By RESULT_ITEMS  = By.cssSelector("ul.results li");

    // ── Constructor ───────────────────────────────────────────────────────────

    public MyPage(WebDriver driver) {
        super(driver);    // always call super — BasePage sets up waits and helpers
    }

    // ── Navigation ────────────────────────────────────────────────────────────

    public void open() {
        log.step("Opening My Page");
        // Add your path to TestData.UrlPatterns, then use it here:
        //   open(TestConfig.BASE_URL + TestData.UrlPatterns.MY_PAGE);
        open(TestConfig.BASE_URL + TestData.UrlPatterns.HOME); // ← replace with your path
        waitForVisible(PAGE_HEADING);   // wait until the page is actually ready
    }

    // ── State checks ──────────────────────────────────────────────────────────

    // isVisible()  → 3-second probe, returns false instead of throwing
    // isPresent()  → same but checks DOM presence (element may be hidden)
    // Use these in assertions; use waitForVisible() before interactions.

    public boolean isLoaded() {
        return isVisible(PAGE_HEADING);
    }

    // ── Interactions ──────────────────────────────────────────────────────────

    // type()  → waits for visibility, clears the field, then types
    // click() → waits for the element to be clickable, then clicks
    // Never call sendKeys() or element.click() directly.

    public void enterSearchQuery(String query) {
        log.step("Entering search query: " + query);
        type(SEARCH_INPUT, query);
    }

    public void clickSubmit() {
        log.step("Clicking submit");
        click(SUBMIT_BUTTON);
    }

    public void search(String query) {
        enterSearchQuery(query);
        clickSubmit();
    }

    // ── Data extraction ───────────────────────────────────────────────────────

    // getText()    → waits for visibility, returns trimmed text
    // waitForAll() → waits for ≥1 match, returns the full list

    public String getHeadingText() {
        return getText(PAGE_HEADING);
    }

    public int getResultCount() {
        return waitForAll(RESULT_ITEMS).size();
    }
}
```

### What's happening here

- `BasePage` gives you `click()`, `type()`, `getText()`, `isVisible()`, `waitForAll()`,
  and many more — you never touch `WebDriver` directly in a page method.
- `log.step("...")` writes a human-readable label to the Allure report.
- `waitForVisible(PAGE_HEADING)` at the end of `open()` acts as a page-ready gate —
  the next step won't run until the page has actually loaded.

### Locator priority — which strategy to pick

| Priority | Example | When to use |
|---|---|---|
| 1 ✅ Best | `[data-testid="search-btn"]` | When devs add `data-testid` — most stable |
| 2 | `By.id("email")` | When there's a unique, stable `id` |
| 3 | `By.cssSelector("button.submit")` | Most readable general-purpose option |
| 4 | `By.xpath("//button[.='Search']")` | When CSS can't find it by text |
| 5 ❌ Avoid | `By.cssSelector(".a3f_x8")` | Hashed/generated class names break on every deploy |

> **How to find a locator:** open DevTools in Chrome (`F12`), hover over the element,
> right-click → Copy → Copy selector. Then clean it up to remove brittle parts.

---

## Step 3 — Create the Steps class

The Steps class is the **bridge** between the Gherkin sentences in your feature file
and the Java methods in your page object. Keep it thin — one step = one page method call.

Create your file in:
```
src/test/java/com/yehorychev/selenium/steps/MyPageSteps.java
```

```java
package com.yehorychev.selenium.steps;

import com.yehorychev.selenium.context.DriverContext;
import com.yehorychev.selenium.context.ScenarioSoftAssertions;
import com.yehorychev.selenium.pages.MyPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;

import static org.testng.Assert.assertTrue;

@Feature("UI — My Page")    // top-level group in the Allure report
@Story("My Feature")        // sub-group under the Feature
public class MyPageSteps {

    private final MyPage myPage;
    private final ScenarioSoftAssertions soft;

    // PicoContainer reads the constructor and injects the right objects automatically.
    // Never call `new DriverContext()` yourself — always let PicoContainer do it.
    // ⚠️ Never add a no-arg constructor — it breaks PicoContainer silently.
    public MyPageSteps(DriverContext driverContext, ScenarioSoftAssertions soft) {
        this.myPage = new MyPage(driverContext.getDriver());
        this.soft = soft;
    }

    // ── Given steps — set up the starting state ───────────────────────────────

    @Given("I open my page")
    public void iOpenMyPage() {
        myPage.open();
    }

    // ── When steps — actions the user performs ────────────────────────────────

    // {string} is a Cucumber built-in type — the quoted value from the feature file.
    // {int}    is a Cucumber built-in type — an unquoted integer from the feature file.

    @When("I search for {string} and submit")
    public void iSearchForAndSubmit(String query) {
        myPage.search(query);
    }

    // ── Then steps — assertions ───────────────────────────────────────────────

    // Use assertTrue() for a single blocking check — failure stops the scenario immediately.
    // Use soft.assertThat() when you want ALL checks in a step to run even if one fails.

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

    @Then("there should be at least {int} results")
    public void thereShouldBeAtLeastResults(int minimum) {
        int actual = myPage.getResultCount();
        soft.assertThat(actual)
                .as("Expected at least %d results but found %d", minimum, actual)
                .isGreaterThanOrEqualTo(minimum);
    }
}
```

### What's happening here

**PicoContainer dependency injection** — you declare what you need as constructor parameters
and the framework wires everything for you, once per scenario:

| Constructor parameter | What you get |
|---|---|
| `DriverContext driverContext` | Access to the WebDriver (`driverContext.getDriver()`) |
| `ApiContext api` | A configured RestAssured spec for API calls |
| `ScenarioContext scenarioContext` | A key/value store to share data between step classes |
| `ScenarioSoftAssertions soft` | Accumulates assertion failures — all reported at scenario end |

**Hard vs. soft assertions:**

| When to use | How |
|---|---|
| One critical check — failure means the scenario can't continue (e.g. page didn't load) | `assertTrue(condition, "message")` |
| Multiple related checks in one step — want all results even if some fail | `soft.assertThat(actual).isEqualTo(expected)` |

> `soft.assertAll()` is called automatically by `SoftAssertionsHook` at the end of every scenario.
> **Never call it yourself.**

---

## Step 4 — Create a Component (only when needed)

You only need a Component when a UI section **appears on more than one page**
(e.g. a navigation bar, a footer, a modal dialog, a filter sidebar).
If the element only lives on a single page, put it directly in the page object.

```
src/main/java/com/yehorychev/selenium/components/MyComponent.java
```

```java
package com.yehorychev.selenium.components;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class MyComponent extends BaseComponent {

    // Inner locators are scoped to the root automatically.
    private static final By ITEM = By.cssSelector("li.item");

    public MyComponent(WebDriver driver) {
        // Pass the outermost wrapper element of this section as the root.
        // All findElement() / click() / getText() calls search WITHIN this root.
        super(driver, By.cssSelector("section.my-section"));
    }

    public int getItemCount() {
        return findElements(ITEM).size();   // only counts items inside section.my-section
    }
}
```

Then use it in your steps class alongside your page object:

```java
public MyPageSteps(DriverContext driverContext, ScenarioSoftAssertions soft) {
    this.myPage    = new MyPage(driverContext.getDriver());
    this.mySection = new MyComponent(driverContext.getDriver());
    this.soft      = soft;
}
```

---

## Step 5 — Run your test

```bash
# Run only your new feature — replace @wip with your tag or use the feature path
mvn test -Dcucumber.filter.tags="@wip" -DHEADLESS=false

# Run the full smoke suite with the browser visible (good for debugging)
mvn test -Dcucumber.filter.tags="@smoke" -DHEADLESS=false

# Run headless (CI mode — faster)
mvn test -Dcucumber.filter.tags="@smoke"

# Open the Allure visual report after a run
mvn allure:serve
```

> **Tip:** always start with `@wip` on your new scenario while writing it.
> That way it's excluded from CI and won't block other people's builds.
> Remove the tag when the scenario is stable.

---

## What to do when a test fails

1. **Run with `HEADLESS=false`** — watch the browser and see exactly where it gets stuck.
2. **Check the Allure report** — `mvn allure:serve` shows the failure screenshot, step log, and page source.
3. **Check the locator** — open DevTools in Chrome, use `document.querySelector("your-selector")` in the Console tab to verify it finds the right element.
4. **Is the element there but not visible?** — use `isPresent()` instead of `isVisible()`.
5. **Is the element there but not yet rendered?** — you may need an extra `waitForVisible()` call.
6. **Still stuck?** — add `log.step("...")` calls around the failing line and re-run to narrow it down.

---

## Common mistakes

| ❌ Don't do this | ✅ Do this instead | Why |
|---|---|---|
| `Thread.sleep(3000)` | `waitForVisible(locator)` | Sleeps are fragile and slow; explicit waits are smarter |
| `driver.findElement(x).click()` | `click(x)` | `click()` waits for the element to be clickable first |
| `new DriverContext()` in a steps class | Constructor injection | PicoContainer owns the lifecycle — don't break it |
| Assertions inside a Page Object | Keep assertions in steps only | Pages should be reusable; assertions belong to test logic |
| `static` mutable fields in steps | `ScenarioContext.set(key, value)` | Static state breaks parallel test execution |
| No tag on a scenario | Add at least `@regression` | Untagged scenarios can't be filtered and may run unexpectedly |
| `soft.assertAll()` at the end of a step | Don't call it | `SoftAssertionsHook` calls it automatically |

---

## Template files in this folder

These files are **never executed** — they exist only as documented code examples.
Copy them, rename them, and fill in your own locators and logic.

| File | Copy to | What it shows |
|---|---|---|
| `ExamplePage.java` | `src/main/java/.../pages/` | Every Page Object pattern |
| `ExampleComponent.java` | `src/main/java/.../components/` | Every Component pattern |
| `ExampleSteps.java` | `src/test/java/.../steps/` | Every Steps class pattern |
| `example.feature` | `src/test/resources/features/ui/` | Every feature file pattern |

> Both `ExampleSteps.java` and `example.feature` are excluded from test runs by design:
> the runner only scans `features/` for feature files and `…selenium.steps` for glue —
> the `templates/` folder and package sit outside both paths.

