# Selenium Cucumber Java Test Framework

**Version**: 1.0-SNAPSHOT  
**Last Updated**: March 2026

> **Enterprise-grade test automation framework** built on Selenium 4, Cucumber 7, TestNG, and PicoContainer. Implements Page Object Model, Component Pattern, parallel execution, PicoContainer dependency injection, and Allure reporting — structured in 7 architectural layers.

---

## 🚀 Quick Start

### Prerequisites
- **Java 17+** (JDK 21 LTS or 25 recommended)
- **Maven 3.8+**
- **Chrome / Firefox / Edge** browser installed
- **Allure CLI** (optional, for local report viewing)

### Installation

```bash
git clone <repository-url>
cd selenium-ui-api
mvn clean install -DskipTests
```

### Environment Configuration

Copy the example template and fill in your credentials:
```bash
cp .env.example .env
```

Edit `.env`:
```dotenv
# Base URLs
BASE_URL=https://mobalytics.gg
API_BASE_URL=https://account.mobalytics.gg

# Browser (chrome | firefox | edge)
BROWSER=chrome
HEADLESS=true

# Test Credentials — required for @api and @authenticated tests
TEST_USER_LOGIN=your-email@example.com
TEST_USER_PASSWORD=your-password

# Admin Credentials — optional, for admin-scope tests
ADMIN_USER_LOGIN=admin@example.com
ADMIN_USER_PASSWORD=admin-password
```

> ⚠️ **Never commit `.env`** — it is already in `.gitignore`.

### Configuration Priority

Values are resolved in this order (highest wins):
1. **System environment variables** (`export BASE_URL=...`)
2. **`.env` file** in project root
3. **`src/main/resources/config.properties`**
4. **Hard-coded defaults** in `TestConfig.java`

---

## 🏗️ Project Structure

```
selenium-ui-api/
├── .env                              # Local credentials (gitignored)
├── .env.example                      # Template — copy and fill in
├── pom.xml
│
├── src/main/java/com/yehorychev/selenium/
│   │
│   ├── config/                       # Layer 1 — Core Infrastructure
│   │   ├── TestConfig.java           # Singleton config: env → .env → props → defaults
│   │   └── DriverConfig.java         # WebDriver factory (Chrome / Firefox / Edge)
│   │
│   ├── errors/                       # Layer 1 — Typed exception hierarchy
│   │   ├── FrameworkException.java   # Base runtime exception (catch-all)
│   │   ├── PageLoadException.java    # Thrown by BasePage.open() on page-load timeout
│   │   ├── ElementNotFoundException.java  # Thrown by waitForVisible/waitForPresent on timeout
│   │   ├── NavigationException.java  # Thrown by assertNavigatesTo(), waitForUrl(), waitForTitle()
│   │   ├── AuthenticationException.java  # Thrown by AuthHelper on sign-in / sign-out failure
│   │   ├── ApiException.java         # Thrown on REST/GraphQL call failure
│   │   └── TestDataException.java    # Thrown when required env var / data is missing
│   │
│   ├── helpers/                      # Layer 1 — Core helpers
│   │   ├── Logger.java               # SLF4J wrapper with step() / info() / debug()
│   │   └── AuthHelper.java           # GraphQL sign-in + WebDriver cookie injection
│   │
│   ├── driver/
│   │   └── DriverManager.java        # ThreadLocal<WebDriver> lifecycle
│   │
│   ├── pages/                        # Layer 2 — Page Object Model
│   │   ├── BasePage.java             # Abstract base: waits, clicks, assertions, screenshots
│   │   ├── HomePage.java
│   │   ├── LolPage.java
│   │   └── Poe2Page.java
│   │
│   ├── components/                   # Layer 3 — Component Pattern
│   │   ├── BaseComponent.java        # Abstract base: scoped element lookups, short/long waits
│   │   ├── NavigationComponent.java  # Header navigation — game links, logo, sign-in button
│   │   ├── HeroComponent.java        # Hero section
│   │   ├── GameCardsComponent.java   # Game cards grid
│   │   ├── FeaturesComponent.java    # Features section
│   │   └── FooterComponent.java      # Footer
│   │
│   ├── data/                         # Layer 4 — Test Data
│   │   ├── TestData.java             # Credentials, URL patterns, UI strings, timeouts
│   │   ├── Tags.java                 # Cucumber tag constants (@smoke, @api, @critical…)
│   │   └── GraphqlQueries.java       # GraphQL query / mutation strings
│   │
│   └── utils/
│       ├── WaitUtils.java            # Fluent waits, retry, polling — typed errors on timeout
│       ├── ScreenshotUtils.java      # AShot: viewport / full-page / element + Allure attach
│       └── TestDataUtils.java        # Faker-based random data generators
│
├── src/main/resources/
│   └── config.properties             # Fallback configuration values
│
├── src/test/java/com/yehorychev/selenium/
│   │
│   ├── context/                      # Layer 5 — PicoContainer DI
│   │   ├── DriverContext.java        # WebDriver lifecycle (setUp / tearDown / getDriver)
│   │   ├── ApiContext.java           # RestAssured wrapper + CookieFilter (session sharing)
│   │   └── ScenarioContext.java      # Cross-step key/value store
│   │
│   ├── hooks/                        # Layer 6 — Cucumber lifecycle hooks
│   │   ├── DriverHooks.java          # @Before/@After — driver start/quit + failure screenshot
│   │   ├── ApiHooks.java             # @Before/@After("@api") — RestAssured init/reset
│   │   ├── AuthHooks.java            # @Before("@authenticated") — sign-in + cookie inject
│   │   ├── RetryHook.java            # @Before/@After — retry attempt tracking + Allure flaky labels
│   │   └── AllureEnvironmentHook.java  # @BeforeAll — writes environment.properties to allure-results
│   │
│   ├── steps/                        # Layer 7 — Step Definitions
│   │   ├── CommonSteps.java          # Shared: open homepage, URL/title assertions
│   │   ├── HomePageSteps.java        # Homepage-specific steps
│   │   ├── LolSteps.java             # LoL page steps
│   │   ├── Poe2Steps.java            # PoE2 page steps
│   │   ├── NavigationSteps.java      # Navigation component steps
│   │   ├── ApiSteps.java             # REST/GraphQL assertion steps
│   │   └── AuthSteps.java            # Sign-in / sign-out steps
│   │
│   └── runner/
│       ├── CucumberRunner.java       # TestNG + Cucumber runner (parallel, retry wired)
│       └── RetryAnalyzer.java        # TestNG IRetryAnalyzer — re-runs failures up to RETRY_COUNT
│
└── src/test/resources/
    ├── testng.xml                    # TestNG suite — parallel="methods" thread-count="4"
    ├── allure.properties             # Allure results directory configuration
    ├── logback-test.xml              # Logback: per-class log suppression + MDC scenario name
    └── features/
        ├── ui/
        │   ├── homepage.feature      # Home page UI scenarios
        │   ├── navigation.feature    # Navigation component scenarios
        │   ├── lol.feature           # LoL page scenarios
        │   └── poe2.feature          # PoE2 page scenarios
        └── api/
            ├── auth-api.feature      # Authentication API scenarios
            └── graphql.feature       # GraphQL query scenarios
```

### Framework Stats

| Metric | Value |
|--------|-------|
| Java classes (main) | 28 |
| Java classes (test) | 17 |
| Feature files | 6 |
| Test scenarios | 32 |
| Custom exceptions | 7 (all wired) |
| Parallel threads | 4 (configurable) |

---

## ▶️ Running Tests

### Quick Commands

```bash
# Run all tests
mvn clean test

# Run smoke tests (fastest, critical path)
mvn test -Dcucumber.filter.tags="@smoke"

# Run with visible browser (debug)
mvn test -DHEADLESS=false -Dcucumber.filter.tags="@smoke"

# Run and open Allure report
mvn clean test && mvn allure:serve
```

### Run by Tag

```bash
mvn test -Dcucumber.filter.tags="@ui"           # UI tests only
mvn test -Dcucumber.filter.tags="@api"           # API tests only (requires .env credentials)
mvn test -Dcucumber.filter.tags="@auth"          # Auth flow tests
mvn test -Dcucumber.filter.tags="@authenticated" # Tests requiring a signed-in session
mvn test -Dcucumber.filter.tags="@critical"      # Must-pass before deploy
mvn test -Dcucumber.filter.tags="@regression"    # Full regression suite
```

### Combine Tags

```bash
mvn test -Dcucumber.filter.tags="@smoke and @ui"
mvn test -Dcucumber.filter.tags="@regression and not @flaky"
mvn test -Dcucumber.filter.tags="@critical or @smoke"
```

### Browser & Execution Options

```bash
mvn test -DBROWSER=firefox
mvn test -DBROWSER=edge
mvn test -DHEADLESS=false
mvn test -DPARALLEL_THREADS=1   # sequential, good for debugging
mvn test -DPARALLEL_THREADS=8   # 8 threads for powerful CI machines
```

### Allure Reports

```bash
mvn allure:serve            # generate + open in browser (live)
mvn allure:report           # generate to target/allure-report/
allure open target/allure-report  # open a previously generated report
```

---

## 🔑 Authentication

### How it works

Authentication uses the GraphQL `signIn` mutation on `account.mobalytics.gg`. The server sets an HTTP-only session cookie — there is no token in the response body. The `CookieFilter` in `ApiContext` automatically propagates the session cookie to all subsequent GraphQL calls within the same scenario.

Session cookies are domain-scoped to `account.mobalytics.gg`. `AuthHelper.injectAuthIntoDriver()` navigates to that domain before injecting cookies, then redirects the browser to `BASE_URL` — ensuring the session is correctly established in the WebDriver.

### Provide credentials

**Option 1 — `.env` file (recommended for local)**:
```dotenv
TEST_USER_LOGIN=your-email@example.com
TEST_USER_PASSWORD=your-password
```

**Option 2 — environment variables (CI/CD)**:
```bash
export TEST_USER_LOGIN=your-email@example.com
export TEST_USER_PASSWORD=your-password
mvn test
```

**Option 3 — Maven CLI**:
```bash
mvn test -DTEST_USER_LOGIN=email@example.com -DTEST_USER_PASSWORD=secret
```

### Hook behaviour by tag

| Tag | Hook | What it does |
|-----|------|-------------|
| `@api` | `ApiHooks.setUpApi` | Configures RestAssured base URI and logging filters |
| `@authenticated` | `AuthHooks.setUpAuthentication` | Signs in via GraphQL, injects session cookies into WebDriver |

### Using AuthHelper directly

```java
// Sign in and inject session cookies into WebDriver
Map<String, String> authData = AuthHelper.loginViaApi();
AuthHelper.injectAuthIntoDriver(driver, authData);

// Or in one call using credentials from TestData
AuthHelper.loginAndInject(driver);
```

---

## 🔁 Retry Mechanism

Failed scenarios are automatically retried up to `RETRY_COUNT` times (default: `1`). Two components cooperate:

- **`RetryAnalyzer`** — `IRetryAnalyzer` wired into `CucumberRunner`; re-executes failed TestNG methods up to the configured limit
- **`RetryHook`** — Cucumber `@Before`/`@After` hook; tracks attempt count in `ScenarioContext` and labels retried-but-passed scenarios as `flaky` in Allure

```bash
mvn test -DRETRY_COUNT=0   # disable retries
mvn test -DRETRY_COUNT=3   # allow up to 3 retries
```

Flaky scenarios (passed on retry) are automatically tagged in the Allure report with `flaky=true` so instability is visible without digging into logs.

---

## 🧩 Cucumber Tags Reference

| Tag | Description |
|-----|-------------|
| `@smoke` | Critical path — run on every commit |
| `@regression` | Full regression suite |
| `@ui` | Browser-based tests |
| `@api` | REST / GraphQL API tests |
| `@critical` | Must pass before deployment |
| `@navigation` | Navigation / routing tests |
| `@auth` | Authentication flow tests |
| `@authenticated` | Requires a signed-in user session |
| `@wip` | Work in progress — excluded from CI runs |
| `@flaky` | Known unstable — excluded from main runs |

> **Note**: scenarios tagged `@wip` are excluded by `CucumberRunner` (`tags = "not @wip"`).  
> To run WIP scenarios locally: `mvn test -Dcucumber.filter.tags="@wip"`.

---

## 🚨 Error Handling

The framework uses a typed exception hierarchy rooted at `FrameworkException`. All exceptions are `RuntimeException` subclasses — no checked exceptions to declare or catch unless you need to.

### Exception hierarchy

```
FrameworkException  (base — catch everything with one handler)
├── PageLoadException          — page did not load within timeout
├── ElementNotFoundException   — element not found / not visible within timeout
├── NavigationException        — URL or page title did not match expected pattern / timeout
├── AuthenticationException    — GraphQL signIn / signOut returned false or failed
├── ApiException               — REST / GraphQL call failed unexpectedly
└── TestDataException          — required env var or test data is missing
```

### What throws what

| Method | Thrown exception |
|--------|-----------------|
| `BasePage.open(url)` | `PageLoadException(url, timeoutMs)` |
| `BasePage.waitForVisible(by)` | `ElementNotFoundException(selector, timeoutMs)` |
| `BasePage.waitForPresent(by)` | `ElementNotFoundException(selector, timeoutMs)` |
| `BasePage.assertNavigatesTo(pattern)` | `PageLoadException` (null URL) · `NavigationException` (mismatch) |
| `WaitUtils.waitForUrl(driver, fragment)` | `NavigationException(actualUrl, fragment)` on timeout |
| `WaitUtils.waitForTitle(driver, fragment)` | `NavigationException(actualTitle, fragment)` on timeout |
| `AuthHelper.loginViaApi()` | `AuthenticationException` |
| `AuthHelper.logoutViaApi()` | `AuthenticationException` if signOut returns false or HTTP error |

### Catching framework errors

```java
// Catch ALL framework errors in one handler
try {
    homePage.open("https://mobalytics.gg");
    homePage.waitForVisible(By.id("hero"));
} catch (FrameworkException e) {
    log.error("Framework failure: " + e.getMessage());
}

// Element not found — message includes selector + wait duration
try {
    basePage.waitForVisible(By.id("submit"));
} catch (ElementNotFoundException e) {
    // "Element not found: "By.id: submit" (waited 15000ms)"
    System.out.println(e.getMessage());
}

// Navigation failure — message includes actual URL + expected pattern
try {
    WaitUtils.waitForUrl(driver, "/dashboard");
} catch (NavigationException e) {
    // "Navigation failed — actual URL: "https://mobalytics.gg/login",
    //  expected to match: "/dashboard""
    System.out.println(e.getMessage());
}
```

---

## 📸 Screenshots

### Automatic capture

Screenshots are taken **automatically on failure** by `DriverHooks`:
- Full-page screenshot via AShot — attached to the Allure report
- Named `failure-<scenario_name>` and visible directly in the failed scenario

### Manual capture

```java
// Full-page (scrolls entire page via AShot)
takeScreenshot("before-checkout");

// Viewport only (fast)
ScreenshotUtils.attachViewport(driver, "quick-snapshot");

// Specific element
ScreenshotUtils.attachElement(driver, element, "submit-button");

// Save to disk (no Allure attachment)
Path file = ScreenshotUtils.saveViewport(driver, "target/screenshots", "debug");
```

---

## 📊 Test Data

### Static data (`TestData.java`)

```java
TestData.Credentials.LOGIN           // TEST_USER_LOGIN env var
TestData.Credentials.PASSWORD        // TEST_USER_PASSWORD env var
TestData.Credentials.ADMIN_LOGIN     // ADMIN_USER_LOGIN env var (optional)
TestData.Credentials.ADMIN_PASSWORD  // ADMIN_USER_PASSWORD env var (optional)
TestData.Credentials.areConfigured() // false → skip API tests gracefully

TestData.UrlPatterns.HOME            // "/"
TestData.UrlPatterns.LOL             // "/lol"
TestData.UiStrings.HOME_PAGE_TITLE   // "Mobalytics"
TestData.Timeouts.FILE_UPLOAD_MS     // 30_000
```

### Dynamic data (`TestDataUtils.java`)

```java
TestDataUtils.randomEmail()     // faker-generated unique email
TestDataUtils.randomPassword()  // secure random password
TestDataUtils.randomUsername()  // unique username
TestDataUtils.randomGamerTag()  // gaming-style tag
```

### Cross-step state (`ScenarioContext`)

```java
scenarioContext.set("userId", "abc-123");   // store in one step
String id = scenarioContext.get("userId");  // read in another step
```

---

## 🧩 Components

Components represent reusable page sections (header, footer, modals). Each component is **scoped to its root element** — all `findElement()` calls search within that root, improving stability and encapsulation.

Both `BasePage` and `BaseComponent` provide:
- `wait` — 15s `WebDriverWait` for explicit element interactions
- `shortWait` — 3s `WebDriverWait` used by `isVisible()` so absence checks don't stall the suite

### When to use

✅ **Components** — header, footer, navigation, modals, sidebars (shared across pages)  
✅ **Page Objects** — page-specific workflows and unique sections

### Usage

```java
NavigationComponent nav = new NavigationComponent(driver);
nav.clickGameLink("LoL");
assertTrue(nav.isLogoVisible());
List<String> games = nav.getAvailableGames(); // returns only game nav links (not logos/social)

FooterComponent footer = new FooterComponent(driver);
footer.clickPrivacyLink();
```

---

## 📈 Allure Reporting

Allure reports include:

- **Environment widget** — browser, headless mode, base URL, thread count, retry count (written by `AllureEnvironmentHook` before the suite starts)
- **Feature / Story grouping** — all step classes are annotated with `@Feature` / `@Story`
- **Flaky labels** — scenarios that pass on retry are automatically tagged `flaky=true` by `RetryHook`
- **Failure screenshots** — full-page AShot screenshot attached to every failed scenario by `DriverHooks`
- **Step timeline** — every `log.step()` call creates a named Allure step

```bash
mvn clean test && mvn allure:serve
```

---

## ⚡ Architecture Highlights

| Layer | Classes | Purpose |
|-------|---------|---------|
| **1 — Core** | `TestConfig`, `DriverConfig`, `Logger`, `errors/*` | Config, logging, typed exception hierarchy |
| **2 — Pages** | `BasePage`, `HomePage`, `LolPage`, `Poe2Page` | Page Object Model with typed error propagation |
| **3 — Components** | `BaseComponent`, `NavigationComponent`, … | Reusable page sections scoped to root element |
| **4 — Data & Helpers** | `TestData`, `Tags`, `GraphqlQueries`, `AuthHelper` | Static data, query constants, auth |
| **5 — DI Context** | `DriverContext`, `ApiContext`, `ScenarioContext` | PicoContainer per-scenario injection |
| **6 — Hooks** | `DriverHooks`, `ApiHooks`, `AuthHooks`, `RetryHook`, `AllureEnvironmentHook` | Cucumber lifecycle management |
| **7 — Steps** | `CommonSteps`, `HomePageSteps`, `ApiSteps`, … | Gherkin step implementations |

### Key design decisions

| Decision | Rationale |
|----------|-----------|
| **No implicit waits** | `DriverConfig` omits `implicitlyWait()` — mixing implicit + explicit waits doubles effective timeouts |
| **Typed exceptions on every timeout** | `waitForVisible`/`waitForPresent` → `ElementNotFoundException`; `waitForUrl`/`waitForTitle`/`assertNavigatesTo` → `NavigationException` — every failure carries selector + duration context |
| **Short + long wait in pages and components** | `shortWait` (3s) for `isVisible()` checks; `wait` (15s) for interactions — absence checks never stall the suite |
| **ThreadLocal WebDriver** | Each parallel thread owns its own driver; `getDriver()` throws `IllegalStateException` with a clear message if called before `setUp()` |
| **CookieFilter in ApiContext** | Session cookies from `signIn` are automatically forwarded on all subsequent requests in the same scenario |
| **PicoContainer DI** | All context objects are injected per-scenario — zero static state in tests |
| **`replaceFiltersWith()` in ApiHooks** | Prevents duplicate logging filters accumulating when `@Before` of scenario N+1 runs before `@After` of scenario N in a parallel suite |
| **Cookie domain correctness in AuthHelper** | `injectAuthIntoDriver()` navigates to `account.mobalytics.gg` before injecting cookies — session cookies are domain-scoped and must be set on the correct domain |
| **No `@wip` in CI** | `CucumberRunner` uses `tags = "not @wip"` consistent with `Tags.WIP` constant |

### Hook execution order

| Order | Hook | Fires |
|-------|------|-------|
| `@Before -10` | `RetryHook.trackAttempt` | Always first — records attempt number in ScenarioContext |
| `@Before 0` | `DriverHooks.setUp` | UI scenarios — starts WebDriver, sets MDC |
| `@Before 1` | `ApiHooks.setUpApi` | `@api` scenarios — configures RestAssured |
| `@Before 2` | `AuthHooks.setUpAuthentication` | `@authenticated` — signs in, injects cookies |
| `@After 10` | `DriverHooks.captureFailure` | UI scenarios — takes failure screenshot |
| `@After 5` | `ApiHooks.tearDownApi` | `@api` — resets RestAssured, clears MDC |
| `@After 3` | `AuthHooks.tearDown` | `@authenticated` — signs out |
| `@After 0` | `DriverHooks.tearDown` | UI scenarios — quits WebDriver |
| `@After 20` | `RetryHook.recordOutcome` | Always last — labels flaky scenarios in Allure |

---

## 🔧 Troubleshooting

### `TestDataException: Required test data is missing: TEST_USER_LOGIN`
```bash
cp .env.example .env   # then add TEST_USER_LOGIN and TEST_USER_PASSWORD
```

### `ElementNotFoundException: Element not found: "By.id: submit" (waited 15000ms)`
```bash
export DEFAULT_TIMEOUT=30000        # increase timeout
mvn test -DHEADLESS=false           # run with visible browser to inspect
```

### `NavigationException: Navigation failed — actual URL: "..." expected to match: "..."`
```bash
# Verify BASE_URL and API_BASE_URL are correct in .env
# Run with visible browser to see where navigation actually lands
mvn test -DHEADLESS=false
```

### `PageLoadException: Page "https://..." did not finish loading within 30000ms`
```bash
export NAVIGATION_TIMEOUT=60000
mvn test
```

### `Cannot find symbol: Dotenv` / `package io.github.cdimascio does not exist`
```bash
mvn dependency:resolve
mvn clean compile
# If still failing, clear the Maven cache entry:
rm -rf ~/.m2/repository/io/github/cdimascio/dotenv-java
mvn clean install -DskipTests
```

### WebDriver version mismatch / `session not created`
```bash
mvn test -Dwdm.forceCache=false     # force WebDriverManager to re-download
rm -rf ~/.cache/selenium/ && mvn test  # or clear the cache entirely
```

### Tests pass locally but fail in CI

| Cause | Fix |
|-------|-----|
| Browser not installed | `sudo apt-get install -y google-chrome-stable` |
| Headless not enabled | `export HEADLESS=true` |
| Timeouts too short | `export DEFAULT_TIMEOUT=30000` |
| Resource contention | `export PARALLEL_THREADS=2` |
| Credentials missing | Set `TEST_USER_LOGIN` and `TEST_USER_PASSWORD` as CI secrets |

### Allure report not generating
```bash
brew install allure      # macOS
mvn allure:serve
```

### Java version compatibility
The framework targets Java 25. To downgrade to Java 21 LTS, update `pom.xml`:
```xml
<maven.compiler.source>21</maven.compiler.source>
<maven.compiler.target>21</maven.compiler.target>
<release>21</release>
```

---

## 🤝 Contributing

### Adding a new page

1. Create `src/main/java/.../pages/MyPage.java` extending `BasePage`
2. Add `private static final By` locator constants
3. Create `src/test/java/com/yehorychev/selenium/steps/MyPageSteps.java`
4. Add `src/test/resources/features/ui/mypage.feature`

### Adding a new component

1. Create `src/main/java/.../components/MyComponent.java` extending `BaseComponent`
2. Pass the root locator to `super(driver, rootLocator)`
3. Use `findElement(By)` / `findElements(By)` — scoped to the root automatically
4. Use `shortWait` for `isVisible()` checks, `wait` for full interactions

### Coding standards

- ✅ JavaDoc on all `public` methods
- ✅ `private static final By` for all locators
- ✅ Explicit waits only — no `Thread.sleep()` in page logic
- ✅ Catch `TimeoutException` and re-throw as a typed `FrameworkException` subclass
- ✅ Methods under 20 lines
- ✅ Tag every Cucumber scenario (`@smoke`, `@ui`, `@api`, etc.)
- ✅ Update `.env.example` when adding new env variables

### Pull request checklist

- [ ] `mvn clean test` passes locally (32 scenarios, 0 failures)
- [ ] New scenarios tagged appropriately
- [ ] `.env.example` updated if new env vars added
- [ ] README updated if new features added

---


**Framework Version**: 1.0-SNAPSHOT  
**Last Updated**: March 2026  
**Test result**: ✅ 32 / 32 scenarios pass
