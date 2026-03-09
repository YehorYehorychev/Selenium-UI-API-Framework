# Selenium Cucumber Java Test Framework

**Version**: 1.0-SNAPSHOT  
**Last Updated**: March 2026

> **Enterprise-grade test automation framework** built on Selenium 4, Cucumber 7, TestNG, and PicoContainer. Implements Page Object Model, Component Pattern, parallel execution, PicoContainer dependency injection, and Allure reporting — structured in 6 architectural layers.

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
│   ├── components/                  
│   │   ├── BaseComponent.java       # Component base class (scoped element lookups)
│   │   ├── NavigationComponent.java # Header navigation
│   │   ├── HeroComponent.java       # Hero section
│   │   ├── FooterComponent.java     # Footer section
│   │   ├── GameCardsComponent.java  # Game cards grid
│   │   └── FeaturesComponent.java   # Features section
│   │
│   ├── data/
│   │   ├── TestData.java            # Static test data (Credentials, URLs, UI strings)
│   │   ├── Tags.java                # Cucumber tags (@smoke, @api, @critical, etc.)
│   │   └── GraphqlQueries.java      # GraphQL query constants
│   │
│   ├── driver/
│   │   └── DriverManager.java       # ThreadLocal WebDriver lifecycle
│   │
│   ├── errors/                       # Layer 1 — Custom exception hierarchy
│   │   ├── FrameworkException.java   # Base runtime exception
│   │   ├── PageLoadException.java
│   │   ├── ElementNotFoundException.java
│   │   ├── NavigationException.java
│   │   ├── AuthenticationException.java
│   │   ├── ApiException.java
│   │   └── TestDataException.java
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
│       ├── WaitUtils.java            # Fluent waits, retry, polling
│       ├── ScreenshotUtils.java      # AShot: viewport / full-page / element + Allure attach
│       └── TestDataUtils.java        # Faker-based random data generators
│
├── src/main/resources/
│   ├── config.properties             # Fallback configuration values
│   └── simplelogger.properties       # SLF4J Simple logger settings
│
├── src/test/java/yehorychev/selenium/
│   ├── context/                      
│   │   ├── DriverContext.java        # WebDriver lifecycle + thread-local access
│   │   ├── ApiContext.java           # RestAssured wrapper
│   │   └── ScenarioContext.java      # Cross-step state storage
│   │
│   ├── hooks/                        
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
| Custom exceptions | 7 |
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
# UI tests only
mvn test -Dcucumber.filter.tags="@ui"

# API tests only (requires .env credentials)
mvn test -Dcucumber.filter.tags="@api"

# Auth-specific tests
mvn test -Dcucumber.filter.tags="@auth"

# Tests requiring a logged-in user
mvn test -Dcucumber.filter.tags="@authenticated"

# Critical tests — must pass before deploy
mvn test -Dcucumber.filter.tags="@critical"

# Regression suite
mvn test -Dcucumber.filter.tags="@regression"
```

### Combine Tags

```bash
# Smoke AND UI
mvn test -Dcucumber.filter.tags="@smoke and @ui"

# Regression but NOT flaky
mvn test -Dcucumber.filter.tags="@regression and not @flaky"

# Critical OR smoke
mvn test -Dcucumber.filter.tags="@critical or @smoke"
```

### Browser & Execution Options

```bash
# Firefox
mvn test -DBROWSER=firefox

# Edge
mvn test -DBROWSER=edge

# Non-headless (visible browser)
mvn test -DHEADLESS=false

# Single thread (for debugging)
mvn test -DPARALLEL_THREADS=1

# 8 threads (powerful CI machine)
mvn test -DPARALLEL_THREADS=8
```

### Allure Reports

```bash
# Generate and open in browser
mvn allure:serve

# Generate to target/allure-report/
mvn allure:report

# Open previously generated report
allure open target/allure-report
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

### Tags

| Tag | Behaviour |
|-----|-----------|
| `@api` | `ApiHooks` sets up RestAssured before the scenario |
| `@authenticated` | `AuthHooks` signs in via API and injects cookies before the scenario |

### Using AuthHelper directly

```java
// Sign in and inject cookies into WebDriver (for UI authenticated tests)
AuthHelper.loginAndInject(driver);

// Sign in and get auth data (for custom flows)
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
| `@wip` | Work in progress — excluded from CI |
| `@flaky` | Known unstable — excluded from main runs |

---

## 📸 Screenshots

### Automatic capture

Screenshots are taken **automatically on failure** by `DriverHooks`:
- Full-page screenshot via AShot — attached to Allure report
- Attached under the failed scenario with the scenario name

### Manual capture

```java
// In a Page Object or step definition

// Full-page (scrolls entire page via AShot)
takeScreenshot("before-checkout");

// Viewport only (fast)
ScreenshotUtils.attachViewport(driver, "quick-snapshot");

// Specific element
ScreenshotUtils.attachElement(driver, element, "submit-button");

// Save to disk (no Allure attachment)
Path file = ScreenshotUtils.saveViewport(driver, "target/screenshots", "debug");
```

### Why only on failure by default?

- Full-page AShot captures take 2–5 seconds
- Smaller Allure reports are easier to navigate
- Failures are the only time screenshots are truly needed

---

## 📊 Test Data

### Static data (`TestData.java`)

```java
TestData.Credentials.LOGIN           // TEST_USER_LOGIN env var
TestData.Credentials.PASSWORD        // TEST_USER_PASSWORD env var
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
// Store a value in one step
scenarioContext.set("userId", "abc-123");

// Read it in another step
String id = scenarioContext.get("userId");
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
| **1 — Core** | `TestConfig`, `DriverConfig`, `Logger`, `errors/*` | Config, logging, exception hierarchy |
| **2 — Pages** | `BasePage`, `HomePage`, `LolPage`, `Poe2Page` | Page Object Model |
| **3 — Components** | `BaseComponent`, `NavigationComponent`, … | Reusable page sections |
| **4 — Data & Helpers** | `TestData`, `Tags`, `GraphqlQueries`, `AuthHelper` | Static data, query constants, auth |
| **5 — DI Context** | `DriverContext`, `ApiContext`, `ScenarioContext` | PicoContainer per-scenario injection |
| **6 — Hooks** | `DriverHooks`, `ApiHooks`, `AuthHooks` | Cucumber lifecycle management |
| **7 — Steps** | `CommonSteps`, `HomePageSteps`, `ApiSteps`, … | Gherkin step implementations |

### Key patterns

- **ThreadLocal WebDriver** — `DriverManager` ensures each parallel thread has its own isolated driver instance
- **CookieFilter in ApiContext** — session cookies from `signIn` are automatically sent on all subsequent requests within the same scenario
- **PicoContainer DI** — all context objects are injected per-scenario, no static state in tests
- **Explicit waits everywhere** — zero `Thread.sleep()` calls in the codebase

---

## 🔧 Troubleshooting

### `TestDataException: Required test data is missing: TEST_USER_LOGIN`

API tests need credentials. Create `.env`:
```bash
cp .env.example .env
# add TEST_USER_LOGIN and TEST_USER_PASSWORD
```

---

### `Cannot find symbol: Dotenv` / `package io.github.cdimascio does not exist`

```bash
mvn dependency:resolve
mvn clean compile
# If still failing, clear Maven cache:
rm -rf ~/.m2/repository/io/github/cdimascio/dotenv-java
mvn clean install -DskipTests
```

---

### WebDriver version mismatch / `session not created`

```bash
# Force WebDriverManager to re-download
mvn test -Dwdm.forceCache=false

# Or clear cache manually
rm -rf ~/.cache/selenium/
mvn test
```

---

### Tests pass locally but fail in CI

| Cause | Fix |
|-------|-----|
| Browser not installed | `sudo apt-get install -y google-chrome-stable` |
| Headless not enabled | `export HEADLESS=true` |
| Timeouts too short | `export DEFAULT_TIMEOUT=30000` |
| Resource contention | `export PARALLEL_THREADS=2` |

---

### `NoSuchElementException` / element not found

```bash
# Increase timeout
export DEFAULT_TIMEOUT=30000

# Run with visible browser to inspect
mvn test -DHEADLESS=false -Dcucumber.filter.tags="@smoke"

# Enable debug logging
export LOG_LEVEL=DEBUG
mvn test
```

---

### Allure report not generating

```bash
# macOS
brew install allure

# Then serve the report
mvn allure:serve
```

---

### Java version compatibility issues

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
2. Add private `By` locator constants
3. Create `src/test/java/.../steps/MyPageSteps.java`
4. Add `src/test/resources/features/ui/mypage.feature`

### Adding a new component

1. Create `src/main/java/.../components/MyComponent.java` extending `BaseComponent`
2. Pass the root locator to `super(driver, rootLocator)`
3. Use `findElement(By)` / `findElements(By)` — they are already scoped to the root

### Coding standards

- ✅ JavaDoc on all `public` methods
- ✅ `private static final By` for all locators
- ✅ Explicit waits — no `Thread.sleep()`
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
