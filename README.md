# Selenium Cucumber Java Test Framework

## 🚀 Quick Start

### 1. Prerequisites
- Java 17+ (JDK 25 recommended)
- Maven 3.8+
- Chrome/Firefox/Edge browser installed

### 2. Installation

```bash
# Clone the repository
git clone <repository-url>
cd selenium-ui-api

# Install dependencies
mvn clean install -DskipTests
```

### 3. Environment Configuration

#### **Create `.env` file (REQUIRED for API tests)**

Copy the example template:
```bash
cp .env.example .env
```

Edit `.env` and add your test credentials:
```dotenv
# Base URLs
BASE_URL=https://mobalytics.gg
API_BASE_URL=https://account.mobalytics.gg

# Browser
BROWSER=chrome
HEADLESS=true

# Test Credentials (REQUIRED for API tests)
TEST_USER_LOGIN=your-email@example.com
TEST_USER_PASSWORD=your-password
```

> ⚠️ **IMPORTANT**: Never commit `.env` to Git! It's already in `.gitignore`.

#### Configuration Priority

The framework resolves configuration values in this order (highest priority first):
1. **System environment variables** (e.g., `export BASE_URL=...`)
2. **`.env` file** in project root
3. **`config.properties`** in `src/main/resources/`
4. **Hard-coded defaults** in `TestConfig.java`

This allows you to:
- Use `.env` for local development
- Override with CI/CD environment variables
- Keep sensible defaults for quick starts

---

## 🏗️ Project Structure

```
selenium-ui-api/
├── .env                        # Your local credentials (gitignored)
├── .env.example                # Template for .env
├── pom.xml                     # Maven dependencies
├── src/
│   ├── main/java/com/yehorychev/selenium/
│   │   ├── config/
│   │   │   ├── TestConfig.java        # Loads .env + config.properties
│   │   │   └── DriverConfig.java      # WebDriver setup
│   │   ├── data/
│   │   │   ├── TestData.java          # Credentials, URLs, UI strings
│   │   │   ├── Tags.java              # Cucumber tags (@smoke, @api, etc.)
│   │   │   └── GraphqlQueries.java    # GraphQL query constants
│   │   ├── driver/
│   │   │   └── DriverManager.java     # ThreadLocal WebDriver
│   │   ├── errors/
│   │   │   ├── TestDataException.java
│   │   │   ├── ApiException.java
│   │   │   └── AuthenticationException.java
│   │   ├── helpers/
│   │   │   ├── Logger.java            # SLF4J wrapper
│   │   │   └── AuthHelper.java        # API login + WebDriver injection
│   │   ├── pages/
│   │   │   ├── BasePage.java          # Page Object base class
│   │   │   └── HomePage.java          # Example page
│   │   └── utils/
│   │       ├── WaitUtils.java         # Advanced wait patterns
│   │       ├── ScreenshotUtils.java   # AShot screenshots
│   │       └── TestDataUtils.java     # Faker data generators
│   └── test/java/yehorychev/selenium/
│       ├── context/
│       │   ├── DriverContext.java     # WebDriver lifecycle (PicoContainer)
│       │   ├── ApiContext.java        # RestAssured wrapper
│       │   └── ScenarioContext.java   # Cross-step state storage
│       ├── hooks/
│       │   └── Hooks.java             # @Before/@After Cucumber hooks
│       ├── steps/
│       │   └── SmokeSteps.java        # Step definitions
│       └── runner/
│           └── CucumberRunner.java    # TestNG + Cucumber integration
└── src/test/resources/
    └── features/
        └── smoke.feature              # BDD scenarios
```

---

## 🧪 Running Tests

### Run all tests
```bash
mvn clean test
```

### Run specific tags
```bash
# Smoke tests only
mvn test -Dcucumber.filter.tags="@smoke"

# UI tests only
mvn test -Dcucumber.filter.tags="@ui"

# API tests only (requires credentials in .env)
mvn test -Dcucumber.filter.tags="@api"

# Critical tests
mvn test -Dcucumber.filter.tags="@critical"
```

### Run with specific browser
```bash
mvn test -DBROWSER=firefox
mvn test -DBROWSER=edge
```

### Run in non-headless mode (see the browser)
```bash
mvn test -DHEADLESS=false
```

### Generate Allure report
```bash
mvn clean test
mvn allure:serve
```

---

## 🔑 Authentication (API Tests)

### Method 1: Via `.env` file (Recommended)
```dotenv
TEST_USER_LOGIN=your-email@example.com
TEST_USER_PASSWORD=your-password
```

### Method 2: Via system environment variables
```bash
export TEST_USER_LOGIN=your-email@example.com
export TEST_USER_PASSWORD=your-password
mvn test
```

### Method 3: Via Maven command line
```bash
mvn test -DTEST_USER_LOGIN=your-email@example.com -DTEST_USER_PASSWORD=your-password
```

### Using AuthHelper in tests

```java
// In a Cucumber hook or step definition
@Before("@authenticated")
public void authenticateUser() {
    // This will use credentials from .env
    AuthHelper.loginAndInject(
        driverContext.getDriver(),
        TestData.Credentials.LOGIN,
        TestData.Credentials.PASSWORD
    );
}

// Or just get the token for API tests
Map<String, String> authData = AuthHelper.loginViaApi();
String token = authData.get("token");
```

### Skipping API tests when credentials are missing

```java
@Before("@api")
public void checkCredentials() {
    Assume.assumeTrue(
        "API credentials not configured. Set TEST_USER_LOGIN and TEST_USER_PASSWORD in .env",
        TestData.Credentials.areConfigured()
    );
}
```

---

## 📊 Test Data Management

### Static test data (TestData.java)
```java
// URL patterns
String loginUrl = TestConfig.BASE_URL + TestData.UrlPatterns.LOGIN;

// UI strings for assertions
String expectedTitle = TestData.UiStrings.HOME_PAGE_TITLE;

// Special timeouts
long uploadTimeout = TestData.Timeouts.FILE_UPLOAD_MS;
```

### Dynamic test data (Faker)
```java
String email = TestDataUtils.randomEmail();
String password = TestDataUtils.randomPassword();
String username = TestDataUtils.randomUsername();
String gamerTag = TestDataUtils.randomGamerTag();
```

### Cross-step scenario state
```java
// In one step
scenarioContext.set("userId", "12345");

// In another step
String userId = scenarioContext.get("userId");
```

---

## 🧩 Cucumber Tags Reference

| Tag | Description |
|-----|-------------|
| `@smoke` | Critical path tests (run on every commit) |
| `@regression` | Full regression suite |
| `@ui` | Browser-based tests |
| `@api` | REST/GraphQL API tests |
| `@critical` | Must-pass before deployment |
| `@navigation` | Page navigation tests |
| `@auth` | Authentication flows |
| `@authenticated` | Tests requiring logged-in user |
| `@wip` | Work in progress (excluded from CI) |
| `@flaky` | Known unstable tests |

Combine tags:
```bash
mvn test -Dcucumber.filter.tags="@smoke and @ui"
mvn test -Dcucumber.filter.tags="@regression and not @flaky"
```

---

## 🔧 Troubleshooting

### "TestDataException: Required test data is missing: TEST_USER_LOGIN"

**Solution**: Create `.env` file with credentials:
```bash
cp .env.example .env
# Edit .env and add your TEST_USER_LOGIN and TEST_USER_PASSWORD
```

### "Cannot find symbol: Dotenv"

**Solution**: Maven dependencies not downloaded. Run:
```bash
mvn dependency:resolve
```

### Tests fail with "session not created" error

**Solution**: Update WebDriverManager cache:
```bash
mvn test -Dwdm.forceCache=false
```

### Allure report not generating

**Solution**: Install Allure command-line:
```bash
# macOS
brew install allure

# Then generate report
mvn allure:serve
```

---

## 📝 Best Practices

1. **Never commit `.env`** — it's already gitignored
2. **Use `.env.example`** as documentation for required variables
3. **Use `TestData.Credentials.areConfigured()`** to skip API tests gracefully when credentials are missing
4. **Tag all scenarios** with appropriate tags (`@smoke`, `@ui`, `@api`, etc.)
5. **Keep Page Objects thin** — delegate complex waits to `WaitUtils`
6. **Use `AuthHelper.loginAndInject()`** to skip UI login in authenticated tests

---

## 🤝 Contributing

1. Create feature branch: `git checkout -b feature/my-feature`
2. Add tests with appropriate tags
3. Ensure all tests pass: `mvn clean test`
4. Update `.env.example` if adding new environment variables
5. Create pull request

