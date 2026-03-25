# Selenium Cucumber Java Test Framework

**Version**: 1.0-SNAPSHOT  
**Last Updated**: March 2026

Enterprise-grade test automation framework built on Selenium 4, Cucumber 7, TestNG, and PicoContainer.  
Covers both **UI** (browser) and **API** (REST / GraphQL) testing of [mobalytics.gg](https://mobalytics.gg) with parallel execution, dependency injection, and Allure reporting.

---

## 📖 Framework Overview

The framework uses BDD with Cucumber for human-readable scenarios. Selenium handles browser interactions, TestNG manages parallel execution, PicoContainer provides per-scenario dependency injection, and RestAssured powers the API layer.

**Core principles**
- **No `Thread.sleep()`** — explicit waits via `WaitUtils` / `BasePage` helpers
- **No global RestAssured state** — each scenario owns an isolated `RequestSpecification`; `ApiHooks` never touches `RestAssured.*` statics
- **Typed exceptions** — `FrameworkException` hierarchy for structured failure classification
- **Thread-safe** — `ThreadLocal<WebDriver>` in `DriverManager`; 4 parallel threads by default
- **Soft assertions** — `ScenarioSoftAssertions` accumulates failures; `SoftAssertionsHook` evaluates them after the scenario

---

## 🚀 Quick Start

### Prerequisites
- Java 25
- Maven 3.8+
- Chrome, Firefox, or Safari (Edge via remote grid only)
- Allure CLI (for reports)

### Installation
```bash
git clone <repository-url>
cd selenium-ui-api
mvn clean install -DskipTests
```

### Environment Configuration
```bash
cp .env.example .env   # then fill in credentials
```

Key variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `BASE_URL` | `https://mobalytics.gg` | Main application URL |
| `API_BASE_URL` | `https://account.mobalytics.gg` | GraphQL / REST API base |
| `BROWSER` | `chrome` | `chrome`, `firefox`, `safari` |
| `HEADLESS` | `true` | `false` shows a real browser window |
| `TEST_USER_LOGIN` | — | Test account email |
| `TEST_USER_PASSWORD` | — | Test account password |
| `DEFAULT_TIMEOUT` | `15000` | Explicit wait timeout (ms) |
| `API_TIMEOUT` | `10000` | Connect + socket timeout for RestAssured (ms) |
| `PARALLEL_THREADS` | `4` | Thread count |
| `RETRY_COUNT` | `1` | Retry attempts per failed scenario |

---

## 🏗️ Project Structure

```
selenium-ui-api/
├── src/main/java/com/yehorychev/selenium/
│   ├── config/          # TestConfig (env resolution), DriverConfig (local + grid WebDriver factory)
│   ├── data/            # TestData (credentials, URL patterns), Tags, GraphqlQueries
│   ├── driver/          # DriverManager — ThreadLocal<WebDriver>
│   ├── errors/          # FrameworkException hierarchy (ApiException, AuthenticationException, …)
│   ├── helpers/         # AuthHelper (GraphQL sign-in/out + cookie injection)
│   │                    # ApiClientConfig (shared RestAssured timeout config)
│   │                    # Logger (SLF4J wrapper with MDC)
│   ├── pages/           # BasePage + page-specific classes (HomePage, LoginPage, LolPage, …)
│   ├── components/      # BaseComponent + reusable UI sections (NavigationComponent, …)
│   └── utils/           # WaitUtils, WaitFactory, LocatorUtils, ScreenshotUtils
│
├── src/test/java/com/yehorychev/selenium/
│   ├── context/         # PicoContainer DI objects (per scenario):
│   │                    #   DriverContext, ApiContext, ScenarioContext,
│   │                    #   ScenarioContextKeys, ScenarioSoftAssertions
│   ├── hooks/           # Cucumber lifecycle:
│   │                    #   DriverHooks, ApiHooks, AuthHooks,
│   │                    #   SoftAssertionsHook, RetryHook, AllureEnvironmentHook
│   ├── runner/          # CucumberRunner (parallel + retry), RetryAnalyzer
│   └── steps/           # Step definitions — UI (LoginSteps, LolSteps, …)
│                        #                  — API (ApiSteps, AuthSteps, RegistrationSteps, …)
│
└── src/test/resources/
    ├── features/
    │   ├── api/          # 6 API feature files (GraphQL, auth, registration, …)
    │   ├── ui/           # 18 UI feature files (homepage, LoL, TFT, Valorant, …)
    │   └── e2e/          # Cross-layer user journeys
    ├── schemas/          # JSON Schema files for API response contract validation
    │   ├── graphql-health-check.json
    │   ├── account-query.json
    │   └── graphql-errors.json
    ├── config.properties # Default config values (lowest priority)
    ├── testng.xml        # TestNG suite — thread count must stay in sync with pom.xml
    ├── allure.properties # Allure results / report directories
    ├── categories.json   # Allure failure classification (ApiException, AuthException, …)
    └── logback-test.xml  # SLF4J / Logback config for test output
```

---

## 🔑 Key Features

| Feature | Detail |
|---------|--------|
| **7-layer architecture** | Config → Pages → Components → Data → DI Context → Hooks → Steps |
| **Page Object Model** | Locators as `private static final By`; actions encapsulated in page methods |
| **Component pattern** | `BaseComponent` scopes `findElement` to a root locator |
| **Parallel execution** | 4 threads, `ThreadLocal<WebDriver>`, isolated `ApiContext` per scenario |
| **Dependency injection** | PicoContainer — no `new` in steps or hooks, no static state |
| **API testing** | RestAssured + `CookieFilter` — session cookies auto-forwarded across requests |
| **JSON schema validation** | `matchesJsonSchemaInClasspath` on every critical response shape |
| **SLA assertions** | `response.getTime()` soft-asserted against `API_TIMEOUT_MS` |
| **Typed exceptions** | `ApiException`, `AuthenticationException`, `ElementNotFoundException`, … |
| **Soft assertions** | `ScenarioSoftAssertions` — accumulate, never stop early; evaluated in `@After` |
| **Authentication** | `AuthHelper.loginViaApi()` → GraphQL `signIn` → cookies injected into driver |
| **Retry + flaky labels** | `RetryHook` + `RetryAnalyzer` — configurable via `RETRY_COUNT` |
| **Allure reporting** | Screenshots, steps, features, stories, Allure categories |

---

## 🌐 API Testing

### Architecture

```
ApiContext (PicoContainer — per scenario)
  └── RequestSpecification
        ├── baseUri    = TestConfig.API_BASE_URL
        ├── CookieFilter  ← session cookies propagate automatically
        ├── timeouts   = ApiClientConfig.withTimeouts()
        └── RequestLoggingFilter / ResponseLoggingFilter  (when HEADLESS=false)
```

`ApiContext` is injected by PicoContainer into any step class that declares it. Each scenario gets a **fresh, isolated instance** — no shared state between parallel threads.

### GraphQL

All queries and mutations live in `GraphqlQueries.java` as text-block constants.  
The `ApiContext.graphql(query, variables)` method wraps the `{"query":…,"variables":…}` envelope automatically.

```java
// step definition example
Response response = api.graphql(GraphqlQueries.ACCOUNT_QUERY);
```

The GraphQL endpoint (`TestData.UrlPatterns.API_GRAPHQL`) is the single source of truth — never hardcoded in helpers or steps.

### JSON Schema Validation

Response contracts are enforced with JSON Schema (Draft-07) via RestAssured's `matchesJsonSchemaInClasspath`:

| Schema file | Validates |
|-------------|-----------|
| `graphql-health-check.json` | `data.__typename` health-check response |
| `account-query.json` | Full account response (`uid`, `email`, `login`, `level`, …) |
| `graphql-errors.json` | Any error response — `errors[*].message` array shape |

Use the reusable Cucumber step to wire validation into any scenario:
```gherkin
And  the response should match the "account-query" schema
And  the response should match the "graphql-errors" schema
```

### SLA Assertion

```gherkin
And  the response time should be within SLA
```

Soft-asserts that `response.getTime() < API_TIMEOUT_MS`. Accumulates alongside other failures — never stops the scenario early.

### API Feature Coverage

| Feature file | Tag | Scenarios |
|-------------|-----|-----------|
| `graphql.feature` | `@api @smoke` | Health check (schema + SLA), account query (schema), uid validation, partial query, unauthenticated rejection |
| `auth-api.feature` | `@api @auth @smoke` | Credentials check, sign-in, sign-out, authenticated queries, invalid credentials |
| `account-registration-api.feature` | `@api @registration` | Email/password validation errors (all with `graphql-errors` schema) |
| `account-management-api.feature` | `@api @account` | Unauthenticated mutations (schema), authenticated login update |
| `password-reset-api.feature` | `@api @password-reset` | Reset request, token errors, password rules (schema on errors) |
| `graphql-error-handling-api.feature` | `@api @regression` | Malformed queries, missing args, introspection disabled, unknown mutations (schema on 422s) |

---

## ▶️ Running Tests

### Local (no Docker required)

```bash
# All tests
mvn clean test

# Smoke — critical path, fastest
mvn test -Dcucumber.filter.tags="@smoke"

# API tests only
mvn test -Dcucumber.filter.tags="@api"

# API smoke only
mvn test -Dcucumber.filter.tags="@api and @smoke"

# Regression gate
mvn test -Dcucumber.filter.tags="@regression and not @flaky"

# Debug with visible browser
mvn test -DHEADLESS=false -Dcucumber.filter.tags="@smoke"

# Sequential (1 thread — easier log reading)
mvn test -DPARALLEL_THREADS=1

# Disable retries
mvn test -DRETRY_COUNT=0

# Run then open Allure report
mvn clean test && mvn allure:serve
```

### Via Selenium Grid (recommended for CI / cross-browser)

```bash
# Start the grid (hub + Chromium + Firefox — ARM64 images)
docker compose -f docker-compose.selenium-grid.yml up -d

# Verify nodes at http://localhost:4444/ui

# Smoke on Chromium
REMOTE_ENABLED=true BROWSER=chrome mvn test -Dcucumber.filter.tags="@smoke"

# Full regression on Firefox
REMOTE_ENABLED=true BROWSER=firefox mvn clean test

# Stop the grid
docker compose -f docker-compose.selenium-grid.yml down
```

> **Apple Silicon note:** `selenium/node-chromium` (ARM64) registers as `chrome` in the grid — transparent to test code.

### Reports

Allure results always land in `target/allure-results` regardless of local or grid execution.

```bash
mvn allure:serve    # generate + open live report
mvn allure:report   # generate static HTML → target/allure-report/index.html
```

---

## 🔐 Authentication

`@authenticated` scenarios are handled automatically by `AuthHooks`:

1. `AuthHelper.loginViaApi()` — fires the `signIn` GraphQL mutation
2. Session cookies are extracted from the response
3. `AuthHelper.injectAuthIntoDriver()` — navigates to the account domain, sets cookies, navigates to `BASE_URL`
4. `AuthHooks.tearDown()` — calls `signOut` mutation and clears browser cookies

For API-only `@authenticated` scenarios the browser step is skipped (`DriverContext.isReady() == false`); `ApiContext`'s `CookieFilter` handles session propagation automatically.

---

## 🧩 Tag System

| Tag | When to use |
|-----|-------------|
| `@smoke` | Critical path — runs on every push |
| `@regression` | Full suite — PR gate to main/dev |
| `@ui` | Selenium/browser tests |
| `@api` | REST/GraphQL tests (no browser launched) |
| `@authenticated` | Triggers `AuthHooks` — signs in via GraphQL, injects cookies |
| `@critical` | Must-pass before deploy |
| `@flaky` | Known unstable — excluded from regression gate |
| `@wip` | Work in progress — excluded from CI runner |

**Tag inheritance**: tags on a `Feature` line cascade to all scenarios within it. Only add scenario-level tags that are additional to the feature tags.

---

## 📝 Writing Tests

### Adding a New UI Page

1. `src/main/java/.../pages/MyPage.java` — extends `BasePage`, locators as `private static final By`
2. `src/test/java/.../steps/MyPageSteps.java` — inject via `DriverContext`, annotate with `@Feature` / `@Story`
3. `src/test/resources/features/ui/mypage.feature` — tag with `@ui` + scope tags

```java
public class MyPageSteps {
    private final MyPage myPage;
    public MyPageSteps(DriverContext ctx) { this.myPage = new MyPage(ctx.getDriver()); }

    @When("the user clicks the submit button")
    public void theUserClicksSubmit() { myPage.clickSubmit(); }
}
```

### Adding a New UI Component

1. `src/main/java/.../components/MyComponent.java` — extends `BaseComponent`
2. Pass root locator: `super(driver, By.cssSelector("section.my-component"))`
3. Use `findElement(By)` — automatically scoped to the root

### Adding a New API Step

```java
@Feature("API — My Domain")
@Story("My Mutation")
public class MyDomainSteps {
    private final ApiContext api;
    private final ScenarioContext scenarioContext;
    private final ScenarioSoftAssertions soft;

    public MyDomainSteps(ApiContext api, ScenarioContext scenarioContext, ScenarioSoftAssertions soft) {
        this.api = api;
        this.scenarioContext = scenarioContext;
        this.soft = soft;
    }

    @When("I call my mutation with {string}")
    public void iCallMyMutation(String input) {
        Map<String, Object> vars = Map.of("input", input);
        Response response = api.graphql(GraphqlQueries.MY_MUTATION, vars);
        scenarioContext.set(ScenarioContextKeys.LAST_RESPONSE, response);
    }
}
```

Then in the feature file:
```gherkin
@api @my-domain
Feature: My Domain API

  @regression
  Scenario: My mutation succeeds
    When I call my mutation with "value"
    Then the response status code should be 200
    And  the response should match the "my-mutation-schema" schema
    And  the response time should be within SLA
```

### Adding a JSON Schema

Create `src/test/resources/schemas/my-response.json` (JSON Schema Draft-07):

```json
{
  "$schema": "http://json-schema.org/draft-07/schema#",
  "type": "object",
  "required": ["data"],
  "properties": {
    "data": {
      "type": "object",
      "required": ["myField"],
      "properties": {
        "myField": { "type": "string", "minLength": 1 }
      }
    }
  }
}
```

Then use it in any scenario:
```gherkin
And  the response should match the "my-response" schema
```

---

## 🤝 Contributing

- Follow the architecture: Keep layers separate
- Use explicit waits: No `Thread.sleep()`
- Add JavaDoc for public methods
- Handle exceptions: Catch `FrameworkException` for all framework errors
- Test locally: Run smoke tests before pushing
