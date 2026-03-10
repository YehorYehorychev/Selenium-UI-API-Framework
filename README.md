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
│   │   ├── ElementNotFoundException.java  # Thrown by waitForVisible/Present/Url/Title
│   │   ├── NavigationException.java  # Thrown by assertNavigatesTo() on URL mismatch
│   │   ├── AuthenticationException.java  # Thrown by AuthHelper on sign-in failure
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
│   │   ├── BaseComponent.java        # Abstract base: scoped element lookups
│   │   ├── NavigationComponent.java  # Header navigation
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
│   ├── config.properties             # Fallback configuration values
│   └── simplelogger.properties       # SLF4J Simple logger settings
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
│   │   └── AuthHooks.java            # @Before("@authenticated") — sign-in + cookie inject
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
│       └── CucumberRunner.java       # TestNG + Cucumber runner (parallel)
│
└── src/test/resources/
    ├── testng.xml                    # TestNG suite — parallel="methods" thread-count="4"
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
| Java classes (test) | 14 |
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
| `@api` | `ApiHooks.setUpApi` | Configures RestAssured base URI and logging |
| `@authenticated` | `AuthHooks.setUpAuthentication` | Signs in via GraphQL, injects session cookies |

### Using AuthHelper directly

```java
// Sign in and inject session cookies into WebDriver
Map<String, String> authData = AuthHelper.loginViaApi();
AuthHelper.injectAuthIntoDriver(driver, authData);
```

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
├── NavigationException        — current URL did not match expected pattern
├── AuthenticationException    — GraphQL signIn returned false / failed
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
| `WaitUtils.waitForUrl(driver, fragment)` | `ElementNotFoundException` with descriptive message on timeout |
| `WaitUtils.waitForTitle(driver, fragment)` | `ElementNotFoundException` with descriptive message on timeout |
| `AuthHelper.loginViaApi()` | `AuthenticationException` |

### Catching framework errors

```java
// Catch ALL framework errors in one handler
try {
    homePage.open("https://mobalytics.gg");
    homePage.waitForVisible(By.id("hero"));
} catch (FrameworkException e) {
    log.error("Framework failure: " + e.getMessage());
}

// Catch a specific type — message includes selector + wait time
try {
    basePage.waitForVisible(By.id("submit"));
} catch (ElementNotFoundException e) {
    // "Element not found: "By.id: submit" (waited 15000ms)"
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
TestDataUtils.randomUsername()  // unique username (bug-fixed: no double-call)
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

### When to use

✅ **Components** — header, footer, navigation, modals, sidebars (shared across pages)  
✅ **Page Objects** — page-specific workflows and unique sections

### Usage

```java
NavigationComponent nav = new NavigationComponent(driver);
nav.clickGameLink("LoL");
assertTrue(nav.isLogoVisible());

FooterComponent footer = new FooterComponent(driver);
footer.clickPrivacyLink();
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
| **6 — Hooks** | `DriverHooks`, `ApiHooks`, `AuthHooks` | Cucumber lifecycle management |
| **7 — Steps** | `CommonSteps`, `HomePageSteps`, `ApiSteps`, … | Gherkin step implementations |

### Key design decisions

| Decision | Rationale |
|----------|-----------|
| **No implicit waits** | `DriverConfig` omits `implicitlyWait()` — mixing implicit + explicit waits doubles effective timeouts |
| **Typed exceptions on every timeout** | `waitForVisible`, `waitForPresent`, `waitForUrl`, `open()` all throw typed exceptions with selector + duration context |
| **ThreadLocal WebDriver** | Each parallel thread owns its own driver; `getDriver()` throws `IllegalStateException` with a clear message if called before `setUp()` |
| **CookieFilter in ApiContext** | Session cookies from `signIn` are automatically forwarded on all subsequent requests in the same scenario |
| **PicoContainer DI** | All context objects are injected per-scenario — zero static state in tests |
| **No `@wip` in CI** | `CucumberRunner` uses `tags = "not @wip"` consistent with `Tags.WIP` constant |

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

## 📋 Improvement History

### Phase 1 — Code Quality & Bug Fixes
- `DriverConfig`: removed implicit wait anti-pattern
- `DriverContext`: added missing source file (existed only as compiled `.class`)
- `BasePage`: fixed `assertNavigatesTo()` null guard; fixed `isPresent()` always-true condition
- `WaitUtils`: fixed `waitForAjax()` `ClassCastException` + null safety on `executeScript` result
- `ScreenshotUtils`: fixed `Files.walk()` resource leak with try-with-resources
- `AuthHelper`: added connection/socket timeouts to prevent indefinite hangs
- `TestDataUtils`: fixed `randomUsername()` double-call bug + `StringBuilder` in loop
- `simplelogger.properties`: fixed garbled encoding in comment header
- `testng.xml`: added `parallel="methods"` to properly enable parallel execution

### Phase 2 — Architecture & Design Patterns
- **Package consistency**: renamed `yehorychev.selenium.*` → `com.yehorychev.selenium.*` across all 14 test sources
- `testng.xml`: updated `CucumberRunner` class reference to correct package
- `CucumberRunner`: fixed tag `"not @ignore"` → `"not @wip"` to align with `Tags.WIP`
- `TestConfig`: added `ADMIN_USER_LOGIN` / `ADMIN_USER_PASSWORD` via standard `resolveOptional()` chain
- `TestData.Credentials`: replaced raw `System.getenv()` calls with `TestConfig` constants
- `NavigationComponent`: removed duplicate `NAV_LINKS` constant (identical to `GAME_LINKS`)
- `HomePage`: fixed `getSocialLinkCount()` to use `findElements()` instead of `waitForAll()`

### Phase 3 — Error Handling & Type Safety
- `BasePage.open()`: throws `PageLoadException` on navigation timeout
- `BasePage.waitForVisible()`: throws `ElementNotFoundException(selector, timeoutMs)` on timeout
- `BasePage.waitForPresent()`: throws `ElementNotFoundException(selector, timeoutMs)` on timeout
- `BasePage.assertNavigatesTo()`: throws `PageLoadException` (null URL) or `NavigationException` (mismatch)
- `WaitUtils.waitForUrl()`: throws `ElementNotFoundException` with descriptive message on timeout
- `WaitUtils.waitForTitle()`: throws `ElementNotFoundException` with descriptive message on timeout
- All 7 custom exception classes now **fully wired** — zero dead code in `errors/`

---

**Framework Version**: 1.0-SNAPSHOT  
**Last Updated**: March 2026  
**Test result**: ✅ 32 / 32 scenarios pass
