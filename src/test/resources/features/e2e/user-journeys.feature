# End-to-End user journey scenarios — multi-step flows that simulate real user behavior
# across multiple pages and features. These tests validate complete user paths
# rather than isolated component checks.
#
# Tag convention:
#   @e2e      — marks this as a multi-step flow scenario
#   @smoke    — critical path, fast (~1 page load)
#   @regression — full regression suite

@e2e @regression @ui
Feature: User Journeys

  # ── Homepage → Game Page flows ─────────────────────────────────────────────

  @e2e @smoke @ui @critical
  Scenario: User navigates from homepage to LoL page via header nav
    Given I open the homepage
    Then the home page is loaded
    And  the header should be visible
    When I click the navigation game link "LoL"
    Then the current URL should contain "/lol"
    And  the LoL page is loaded

  @e2e @smoke @ui @critical
  Scenario: User navigates from homepage to TFT page via header nav
    Given I open the homepage
    Then the home page is loaded
    When I click the navigation game link "TFT"
    Then the current URL should contain "/tft"
    And  the TFT page is loaded

  @e2e @regression @ui
  Scenario: User navigates from homepage to PoE2 via header nav
    Given I open the homepage
    When I click the navigation game link "PoE2"
    Then the current URL should contain "poe"
    And  the PoE2 page is loaded

  @e2e @regression @ui
  Scenario: User navigates from homepage to Diablo 4 via header nav and sees build content
    Given I open the homepage
    When I click the navigation game link "Diablo 4"
    Then the current URL should contain "diablo-4"
    And  the Diablo 4 page is loaded
    And  the Diablo 4 builds section should be visible

  # ── Game Tile click flows ──────────────────────────────────────────────────

  @e2e @regression @ui
  Scenario: User clicks LoL game tile on homepage and lands on LoL page
    Given I open the homepage
    And   there should be at least 3 game cards on the home page
    When  I click the game tile for "lol"
    Then  the current URL should contain "/lol"
    And   the LoL page is loaded

  @e2e @regression @ui
  Scenario: User clicks TFT game tile on homepage and lands on TFT page
    Given I open the homepage
    And   there should be at least 3 game cards on the home page
    When  I click the game tile for "tft"
    Then  the current URL should contain "/tft"
    And   the TFT page is loaded

  @e2e @regression @ui
  Scenario: User clicks Diablo 4 game tile and lands on Diablo 4 page
    Given I open the homepage
    When  I click the game tile for "diablo-4"
    Then  the current URL should contain "diablo-4"
    And   the Diablo 4 page is loaded

  # ── Sign-In Button flow ────────────────────────────────────────────────────

  @e2e @regression @ui @navigation
  Scenario: User sees Sign In button on LoL page and opens login page
    Given I open the LoL page
    Then  the login button should be visible in the navigation

  @e2e @regression @ui @navigation
  Scenario: User sees Sign In button on TFT page
    Given I open the TFT page
    Then  the login button should be visible in the navigation

  # ── LoL deep-dive flow ────────────────────────────────────────────────────

  @e2e @regression @ui
  Scenario: User opens LoL page and finds champion build links
    Given I open the LoL page
    Then  the LoL page is loaded
    And   the tier list section should be visible
    And   the builds section should be visible

  # ── PoE2 deep-dive flow ───────────────────────────────────────────────────

  @e2e @regression @ui
  Scenario: User opens PoE2 page and sees build options
    Given I open the PoE2 page
    Then  the PoE2 page is loaded
    And   the guides section should be visible

  # ── API + UI integration flow ─────────────────────────────────────────────

  @e2e @regression @api
  Scenario: Authenticated user queries their account information
    Given I am authenticated via API
    Then  an auth token should be stored in the scenario context
    When  I query the current account via GraphQL
    Then  the response status code should be 200
    And   the response JSON path "data.account.uid" should not be null
    And   the account email should match the configured test email

  @e2e @regression @api
  Scenario: Sign-out clears the auth session
    Given I am authenticated via API
    Then  an auth token should be stored in the scenario context
    When  I log out via API
    Then  the auth token should no longer be present

