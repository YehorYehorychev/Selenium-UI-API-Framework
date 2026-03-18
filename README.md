# Selenium Cucumber Java Test Framework

**Version**: 1.0-SNAPSHOT  
**Last Updated**: March 2026

Enterprise-grade test automation framework built on Selenium 4, Cucumber 7, TestNG, and PicoContainer. Implements Page Object Model, Component Pattern, parallel execution, dependency injection, and Allure reporting.

## 📖 Framework Overview

This framework is designed for testing the Mobalytics.gg website. It uses BDD (Behavior-Driven Development) with Cucumber for writing human-readable test scenarios. Selenium handles browser interactions, while TestNG manages test execution and parallelization. PicoContainer provides dependency injection for clean, maintainable code.

Key principles:
- **No Thread.sleep()**: Uses explicit waits instead.
- **Typed Exceptions**: Specific errors for better debugging.
- **Thread-Safe**: Parallel execution with ThreadLocal drivers.
- **Clean Code**: No redundant comments, self-explanatory methods.

## 🚀 Quick Start

### Prerequisites
- Java 25
- Maven 3.8+
- Chrome, Firefox, or Edge browser
- Allure CLI (for reports)

### Installation
```bash
git clone <repository-url>
cd selenium-ui-api
mvn clean install -DskipTests
```

### Environment Configuration
Copy `.env.example` to `.env` and fill in your details:
```dotenv
BASE_URL=https://mobalytics.gg
API_BASE_URL=https://account.mobalytics.gg
BROWSER=chrome
HEADLESS=true
TEST_USER_LOGIN=your-email@example.com
TEST_USER_PASSWORD=your-password
```

## 🏗️ Project Structure

The framework follows a 7-layer architecture for separation of concerns:

```
selenium-ui-api/
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
│   │   ├── LoginPage.java
│   │   ├── LolPage.java
│   │   ├── Poe2Page.java
│   │   ├── TftPage.java
│   │   ├── ValorantPage.java
│   │   └── Diablo4Page.java
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
├── src/test/java/yehorychev/selenium/
│   ├── context/                      
│   │   ├── DriverContext.java        # WebDriver lifecycle + thread-local access
│   │   ├── ApiContext.java           # RestAssured wrapper
│   │   └── ScenarioContext.java      # Cross-step state storage
│   │
│   ├── hooks/                        
│   │   ├── DriverHooks.java          # @Before/@After — driver start/quit + failure screenshot
│   │   ├── ApiHooks.java             # @Before/@After("@api") — RestAssured init/reset
│   │   ├── AuthHooks.java            # @Before("@authenticated") — sign-in + cookie inject
│   │   ├── RetryHook.java            # @Before/@After — retry attempt tracking + Allure flaky labels
│   │   └── AllureEnvironmentHook.java  # @BeforeAll — writes environment.properties to allure-results
│   │
│   ├── steps/                        # Layer 7 — Step Definitions
│   │   ├── CommonSteps.java          # Shared: open homepage, URL/title assertions
│   │   ├── HomePageSteps.java        # Homepage-specific steps
│   │   ├── LoginSteps.java           # Login page steps
│   │   ├── LolSteps.java             # LoL page steps
│   │   ├── Poe2Steps.java            # PoE2 page steps
│   │   ├── TftSteps.java             # TFT page steps
│   │   ├── ValorantSteps.java        # Valorant page steps
│   │   ├── Diablo4Steps.java         # Diablo 4 page steps
│   │   ├── NavigationSteps.java      # Navigation component steps
│   │   ├── ApiSteps.java             # REST/GraphQL assertion steps
│   │   └── AuthSteps.java            # Sign-in / sign-out steps
│   │
│   └── runner/
│       ├── CucumberRunner.java       # TestNG + Cucumber runner (parallel, retry wired)
│       └── RetryAnalyzer.java        # TestNG IRetryAnalyzer — re-runs failures up to RETRY_COUNT
│
└── src/test/resources/
    ├── features/        # Cucumber feature files (ui/, api/, e2e/)
    ├── config.properties # Default config values
    ├── testng.xml       # TestNG suite configuration
    └── allure.properties # Allure report settings
```

### Key Classes
- **BasePage**: Common page methods (click, type, waitForVisible)
- **BaseComponent**: Scoped element interactions for reusable sections
- **DriverContext**: Holds WebDriver instance, injected per scenario
- **ScenarioContext**: Key-value store for sharing data between steps
- **AuthHelper**: Handles GraphQL authentication and cookie injection

## 🔑 Key Features

- **7-Layer Architecture**: Organized into Core, Pages, Components, Data, DI Context, Hooks, Steps
- **Page Object Model**: Each page class encapsulates locators and actions
- **Component Pattern**: Reusable UI sections like navigation or footers
- **Parallel Execution**: Runs tests in multiple threads safely
- **Dependency Injection**: PicoContainer wires contexts automatically
- **Typed Exceptions**: Specific errors (e.g., ElementNotFoundException) for easy debugging
- **Authentication**: Automatic sign-in via GraphQL with session cookies
- **Retry Mechanism**: Retries failed tests and marks flaky ones
- **Allure Reporting**: Beautiful reports with screenshots and step details
- **Cucumber Tags**: Filter tests by type (@smoke, @ui, @authenticated)

## ▶️ Running Tests

```bash
# Run all tests
mvn clean test

# Run only smoke tests (fast, critical path)
mvn test -Dcucumber.filter.tags="@smoke"

# Run with visible browser for debugging
mvn test -DHEADLESS=false -Dcucumber.filter.tags="@smoke"

# Generate and open Allure report
mvn clean test && mvn allure:serve

# Run in sequential mode (1 thread)
mvn test -DPARALLEL_THREADS=1

# Disable retries
mvn test -DRETRY_COUNT=0
```

## 🔑 Authentication

For tests requiring a logged-in user:
1. Set credentials in `.env` or environment variables
2. Tag scenarios with `@authenticated`
3. The framework automatically signs in via GraphQL and injects cookies

## 🧩 Cucumber Tags

Organize and filter tests using tags:
- `@smoke`: Critical user journeys (run on every push)
- `@regression`: Full test suite (run before releases)
- `@ui`: Browser-based tests
- `@api`: REST/GraphQL API tests (no browser)
- `@authenticated`: Requires user login
- `@critical`: Must pass for deployment
- `@wip`: Work in progress (excluded from CI)
- `@flaky`: Known unstable tests

## 📝 Writing Tests

### Adding a New Page
1. Create `NewPage.java` extending `BasePage`
2. Define locators as `private static final By`
3. Implement page-specific methods
4. Create `NewPageSteps.java` with `@Feature` and `@Story` annotations
5. Inject `DriverContext` in constructor
6. Write `.feature` file in `src/test/resources/features/ui/`

### Adding a New Component
1. Create `NewComponent.java` extending `BaseComponent`
2. Pass root locator to `super(driver, By.cssSelector("..."))`
3. Use `findElement(By)` for scoped interactions

### Example Feature File
```gherkin
@ui @smoke
Feature: User can search champions on LoL page

  Scenario: Search for a champion
    Given I open the LoL page
    When I search for champion "Ahri"
    Then there should be at least 1 champion card displayed
```

## 🤝 Contributing

- Follow the architecture: Keep layers separate
- Use explicit waits: No `Thread.sleep()`
- Add JavaDoc for public methods
- Handle exceptions: Catch `FrameworkException` for all framework errors
- Test locally: Run smoke tests before pushing

For questions, check the code or ask the team!
