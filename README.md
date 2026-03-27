# Selenium Cucumber Java — Test Framework

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

Selenium 4 · Cucumber 7 · TestNG · PicoContainer · Allure  
Covers UI (browser) and API (REST / GraphQL) testing of [mobalytics.gg](https://mobalytics.gg).  
Open-sourced for learning purposes — feel free to use it as a starting point for your own automation projects.

---

## 🔧 Prerequisites

| Requirement | Version | Notes |
|-------------|---------|-------|
| JDK | 25 (LTS) | [Download](https://www.oracle.com/in/java/technologies/downloads/#java25) — verify with `java -version` |
| Maven | 3.8+ | [Download](https://maven.apache.org/download.cgi) — verify with `mvn -version` |
| Chrome or Firefox | Latest stable | **Browser driver is downloaded automatically** by Selenium Manager (bundled with Selenium 4) — no manual setup required |
| Docker | Any latest version | [Download](https://docs.docker.com/desktop/setup/install/mac-install/) - verify with `docker --version` | Optional — only needed for the Selenium Grid mode |
| Allure CLI | 2.x | Optional — needed to open the HTML report locally (`brew install allure` on macOS) |

> **macOS + Safari:** works out of the box — enable Remote Automation in Safari → Develop menu first.

---

## 🚀 Quick Start

```bash
# 1. Clone and build
git clone <repository-url>
cd selenium-ui-api
mvn clean install -DskipTests

# 2. Set up credentials (only once)
cp .env.example .env
# fill in TEST_USER_LOGIN and TEST_USER_PASSWORD in .env

# 3. Run smoke suite
mvn test -Dcucumber.filter.tags="@smoke"
```

---

## 📖 Writing your first test

See the **[Onboarding Guide](src/test/resources/templates/onboarding.md)** — a step-by-step walkthrough that takes you from zero to a working test in ~15 minutes.

### Template files

Ready-to-copy examples are spread across their natural source locations:

| File | Find it at | Copy to | What it shows |
|------|------------|---------|---------------|
| [`ExamplePage.java`](src/main/java/com/yehorychev/selenium/pages/templates/ExamplePage.java) | `src/main/java/.../pages/templates/` | `src/main/java/.../pages/` | Page Object patterns |
| [`ExampleComponent.java`](src/main/java/com/yehorychev/selenium/components/templates/ExampleComponent.java) | `src/main/java/.../components/templates/` | `src/main/java/.../components/` | Component patterns |
| [`ExampleSteps.java`](src/test/java/com/yehorychev/selenium/templates/ExampleSteps.java) | `src/test/java/.../templates/` | `src/test/java/.../steps/` | Step definition patterns |
| [`example.feature`](src/test/resources/templates/example.feature) | `src/test/resources/templates/` | `src/test/resources/features/ui/` | Feature file patterns |

> These files are never executed — they only exist as documented examples.

---

## ▶️ Running Tests

```bash
# All tests
mvn clean test

# By tag
mvn test -Dcucumber.filter.tags="@smoke"
mvn test -Dcucumber.filter.tags="@api"
mvn test -Dcucumber.filter.tags="@regression and not @flaky"

# Debug — browser visible
mvn test -DHEADLESS=false -Dcucumber.filter.tags="@smoke"

# Sequential (easier log reading)
mvn test -DPARALLEL_THREADS=1

# Run + open Allure report
mvn clean test && mvn allure:serve
```

### Selenium Grid (Docker)

```bash
docker compose -f docker-compose.selenium-grid.yml up -d

REMOTE_ENABLED=true BROWSER=chrome mvn test -Dcucumber.filter.tags="@smoke"
REMOTE_ENABLED=true BROWSER=firefox mvn clean test

docker compose -f docker-compose.selenium-grid.yml down
```

---

## 🏷️ Tag Reference

| Tag | When to use |
|-----|-------------|
| `@smoke` | Fast critical-path check — runs on every push |
| `@regression` | Full suite — PR gate to main |
| `@ui` | Browser test (driver is started automatically) |
| `@api` | REST / GraphQL test — no browser launched |
| `@authenticated` | Needs a logged-in user — `AuthHooks` signs in automatically |
| `@critical` | Must pass before deploy |
| `@wip` | Excluded from CI — use while developing a new scenario |

Every scenario must have **at least one tag**.

---

## 🏗️ Project Structure

```
src/main/java/.../
├── config/       # TestConfig, DriverConfig (WebDriver factory)
├── driver/       # DriverManager — ThreadLocal<WebDriver> for parallel safety
├── errors/       # Typed exceptions: FrameworkException and subclasses
├── pages/        # Page Objects (extend BasePage)
├── components/   # Reusable UI sections (extend BaseComponent)
├── data/         # TestData, Tags, GraphqlQueries constants
├── helpers/      # AuthHelper, ApiClientConfig, Logger
└── utils/        # WaitUtils, WaitFactory, ScreenshotUtils, AllureUtils, LocatorUtils

src/test/java/.../
├── context/      # DriverContext, ApiContext, ScenarioContext, ScenarioSoftAssertions
├── hooks/        # Cucumber lifecycle hooks (driver, auth, API, retry, soft assertions)
├── runner/       # CucumberRunner, RetryAnalyzer, DynamicThreadListener
├── steps/        # Step definitions
└── templates/    # ExampleSteps.java — copy-paste template (never executed)

src/test/resources/
├── features/     # .feature files (ui/, api/, e2e/)
├── schemas/      # JSON Schema files for API validation
└── templates/    # ← onboarding guide + example.feature template
```

---

## ⚙️ Configuration

Copy `.env.example` → `.env` and set your values. Key variables:

| Variable | Default | Description |
|----------|---------|-------------|
| `BASE_URL` | `https://mobalytics.gg` | App URL |
| `BROWSER` | `chrome` | `chrome`, `firefox`, `safari` |
| `HEADLESS` | `true` | `false` shows a real browser window |
| `TEST_USER_LOGIN` | — | Test account email |
| `TEST_USER_PASSWORD` | — | Test account password |
| `PARALLEL_THREADS` | `4` | Thread count |
| `RETRY_COUNT` | `1` | Retry attempts per failed scenario |

---

## 📋 Core Rules

- **No `Thread.sleep()`** — use `WaitUtils` or `BasePage` wait helpers
- **No `new` in steps/hooks** — PicoContainer injects everything via constructor
- **No assertions in Page Objects** — pages interact, steps assert
- **No static mutable state** — use `ScenarioContext` to share data between steps
- **Soft assertions** — use `ScenarioSoftAssertions`; never call `assertAll()` manually

---

## 📄 License

Released under the [MIT License](LICENSE). Free to use, fork, and share for learning and educational purposes.

---

## 👤 Author

**Yehor Yehorychev** — [LinkedIn](https://www.linkedin.com/in/egor-egorychev/)
