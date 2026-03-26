# ─────────────────────────────────────────────────────────────────────────────
# TEMPLATE — example.feature
#
# PURPOSE  : Shows how to write a Cucumber feature file in this framework.
# LOCATION : src/test/resources/features/ui/  (copy this file there and rename it)
# EXCLUDES : This file lives in  src/test/resources/templates/  — OUTSIDE the
#            `src/test/resources/features/` directory that CucumberRunner scans,
#            so it is NEVER picked up or executed during test runs.
#
# HOW TO USE THIS TEMPLATE
# ────────────────────────
#  1. Copy this file to  src/test/resources/features/ui/  (or /api/ or /e2e/)
#  2. Rename it — e.g. dashboard.feature
#  3. Update the Feature name, description, and all Scenario names
#  4. Replace every step line with steps that match your step definitions
#  5. Apply the correct tags (see the tag reference below)
#
# STEP MATCHING
# ─────────────
# Each line under a Scenario must match the @Given/@When/@Then annotation
# string in a steps class EXACTLY (including capitalisation and parameter syntax).
# If a step has no matching definition the runner marks it as UNDEFINED (yellow)
# and the scenario is skipped.
#
# TAG REFERENCE  (defined in data/Tags.java)
# ──────────────
# @smoke        → runs on every push — fast, critical-path only
# @regression   → full suite, PR gate
# @ui           → Selenium / browser test (driver is started by DriverHooks)
# @api          → REST / GraphQL test (NO browser launched)
# @authenticated → triggers AuthHooks — signs in via GraphQL before the scenario
# @critical     → must pass before deploy
# @wip          → excluded from CI runner; use while actively developing a scenario
# @e2e          → multi-page user journey
#
# RULE: every Scenario MUST have at least one tag.
# ─────────────────────────────────────────────────────────────────────────────

# ── Feature-level tags apply to ALL scenarios below ───────────────────────────
# Put tags shared by every scenario here — individual scenarios inherit them AND
# can add more. Avoid putting @smoke here unless every scenario is smoke-worthy.
@regression @ui
Feature: Example Page Search
  # A short, plain-English description of what this feature covers.
  # Visible in the Allure report and the Cucumber HTML report.
  # Optional but recommended for context.

  # ── Background ─────────────────────────────────────────────────────────────
  # Steps in Background run before EVERY scenario in this file.
  # Use it to navigate to the starting page so each scenario begins in the same state.
  # Keep Background lean — one or two steps maximum.

  Background:
    Given I open the example page

  # ── Simple scenario ────────────────────────────────────────────────────────
  # Tags on individual scenarios are ADDED to the feature-level tags.
  # This scenario runs in both smoke and regression modes.

  @smoke @critical
  Scenario: Page loads successfully
    Then the example page is loaded

  # ── Scenario with multiple assertions ─────────────────────────────────────
  # Use soft.assertThat() in the step definitions so all checks run even if
  # one fails — the scenario reports all failures, not just the first one.

  @smoke
  Scenario: Page heading contains expected text
    Then the example page is loaded
    And  the page heading should contain "Champion Search"

  # ── When / Then flow ──────────────────────────────────────────────────────
  # A typical action → assertion scenario: user does something, then we verify the result.

  @regression
  Scenario: Searching by champion name returns results
    When I search for "Ahri" and submit
    Then there should be at least 1 results

  # ── Negative / edge-case scenario ─────────────────────────────────────────
  # Test that the UI handles bad or missing data gracefully.

  @regression
  Scenario: Searching with a nonsense query shows the empty state
    When I search for "zzznomatch999" and submit
    Then there should be exactly 0 results
    And  the empty state message should be visible
    And  the empty state message should contain "No results found"

  # ── Scenario Outline (data-driven) ────────────────────────────────────────
  # Use a Scenario Outline + Examples table when the SAME flow needs to run
  # with multiple sets of inputs. Cucumber generates one scenario per table row.
  #
  # Placeholders in angle brackets  <column>  are substituted from the table.
  # Column names must match the parameter names in the step definition strings.

  @regression
  Scenario Outline: Searching for popular champions returns results
    When I search for "<champion>" and submit
    Then there should be at least 1 results

    Examples:
      | champion  |
      | Ahri      |
      | Jinx      |
      | Thresh    |
      | Yasuo     |

  # ── Authenticated scenario ─────────────────────────────────────────────────
  # @authenticated triggers AuthHooks, which calls the GraphQL signIn mutation
  # BEFORE the scenario runs and injects session cookies into the browser.
  # Use this whenever the feature you are testing requires a logged-in user.
  # Pair with @wip while credentials are being confirmed in CI.

  @regression @authenticated @wip
  Scenario: Logged-in user sees personalised results
    When  I search for "Ahri" and submit
    Then  there should be at least 1 results

  # ── Saving state across steps ──────────────────────────────────────────────
  # Use ScenarioContext to pass a value from a When step to a Then step.
  # The key is an inline string — use it like a variable name.

  @regression
  Scenario: Resetting filters restores the original result count
    When  I save the current result count as "beforeFilter"
    # ... (apply a filter step would go here once you write it)
    Then  the result count for "beforeFilter" should be 0

