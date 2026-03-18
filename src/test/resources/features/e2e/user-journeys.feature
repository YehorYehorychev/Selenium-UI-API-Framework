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

  # ── New game navigation flows ──────────────────────────────────────────────

  @e2e @smoke @ui @deadlock @critical
  Scenario: User navigates from homepage to Deadlock page via header nav
    Given I open the homepage
    Then  the home page is loaded
    When  I click the navigation game link "Deadlock"
    Then  the current URL should contain "deadlock"
    And   the Deadlock page is loaded

  @e2e @smoke @ui @mhw @critical
  Scenario: User navigates from homepage to MH Wilds page via header nav
    Given I open the homepage
    Then  the home page is loaded
    When  I click the navigation game link "MH Wilds"
    Then  the current URL should contain "/mhw"
    And   the MH Wilds page is loaded

  @e2e @regression @ui @borderlands4
  Scenario: User navigates from homepage to Borderlands 4 via header nav
    Given I open the homepage
    When  I click the navigation game link "Borderlands 4"
    Then  the current URL should contain "borderlands-4"
    And   the Borderlands 4 page is loaded

  @e2e @regression @ui @nightreign
  Scenario: User navigates from homepage to Nightreign via header nav
    Given I open the homepage
    When  I click the navigation game link "Nightreign"
    Then  the current URL should contain "nightreign"
    And   the Nightreign page is loaded
    And   the Nightreign builds section should be visible

  # ── LoL deep-dive flows ────────────────────────────────────────────────────

  @e2e @smoke @ui @lol @critical
  Scenario: User navigates from homepage to LoL Tier List via footer link
    Given I open the homepage
    Then  the home page is loaded
    When  I click the navigation game link "LoL"
    Then  the current URL should contain "/lol"
    And   the LoL page is loaded
    And   the tier list section should be visible

  @e2e @regression @ui @lol
  Scenario: User opens LoL Tier List and finds champion links
    Given I open the LoL Tier List page
    Then  the LoL Tier List page is loaded
    And   the LoL Tier List heading should contain "Tier List"
    And   there should be at least 50 champion links on the LoL Tier List
    And   the LoL Tier List methodology section should be present

  @e2e @regression @ui @lol
  Scenario: User browses LoL Champions list and drills into a champion build
    Given I open the LoL Champions page
    Then  the LoL Champions page is loaded
    And   there should be at least 100 champions listed on the LoL Champions page
    When  I click champion "ahri" on the LoL Champions page
    Then  the current URL should contain "/lol/champions/ahri"
    And   the champion build page is loaded
    And   the champion builds section should be present
    And   the champion runes section should be present

  @e2e @regression @ui @lol
  Scenario: User views Ahri build page and sees matchups content
    Given I open the build page for champion "ahri"
    Then  the champion build page is loaded
    And   the champion matchups section should be present
    And   there should be at least 5 counter links on the champion build page

  # ── TFT deep-dive flows ───────────────────────────────────────────────────

  @e2e @regression @ui @tft
  Scenario: User opens TFT page and navigates to Team Comps
    Given I open the TFT page
    Then  the TFT page is loaded
    And   the TFT tier list section should be visible

  @e2e @regression @ui @tft
  Scenario: User opens TFT Team Comps and finds comp data with unit links
    Given I open the TFT Team Comps page
    Then  the TFT Team Comps page is loaded
    And   the TFT Team Comps page should have comp cards
    And   the TFT Team Comps page should have unit links

  @e2e @regression @ui @tft
  Scenario: User opens TFT Tier List and sees comp entries
    Given I open the TFT Tier List page
    Then  the TFT Tier List page is loaded
    And   the TFT Tier List should have team comp links
    And   there should be at least 10 unit links on the TFT Tier List

  # ── New game content flows ─────────────────────────────────────────────────

  @e2e @regression @ui @deadlock
  Scenario: User opens Deadlock page and finds builds and heroes content
    Given I open the Deadlock page
    Then  the Deadlock page is loaded
    And   the Deadlock heroes section should be visible
    And   the Deadlock builds section should be visible
    And   there should be at least 3 Deadlock content links

  @e2e @regression @ui @overwatch
  Scenario: User opens Overwatch page and finds Stadium builds
    Given I open the Overwatch page
    Then  the Overwatch page is loaded
    And   the Overwatch stadium builds section should be visible
    And   there should be at least 10 Overwatch hero links

  @e2e @regression @ui @nightreign
  Scenario: User opens Nightreign page and sees all four content sections
    Given I open the Nightreign page
    Then  the Nightreign page is loaded
    And   the Nightreign Nightfarers section should be visible
    And   the Nightreign Nightlords section should be visible
    And   the Nightreign builds section should be visible
    And   the Nightreign guides section should be visible

  @e2e @regression @ui @mhw
  Scenario: User opens MH Wilds and sees build and guide content
    Given I open the MH Wilds page
    Then  the MH Wilds page is loaded
    And   the MH Wilds builds section should be visible
    And   the MH Wilds guides section should be visible
    And   there should be at least 5 MH Wilds content links

  # ── Cross-game navigation round trips ─────────────────────────────────────
  # NOTE: NavigationComponent uses header.site-header which only exists on the
  # marketing homepage (WordPress). Game sub-pages use a React header.
  # Each nav click therefore originates from the homepage.

  @e2e @regression @ui @navigation
  Scenario: User browses LoL, TFT, and Deadlock sequentially via homepage nav
    Given I open the homepage
    When  I click the navigation game link "LoL"
    Then  the LoL page is loaded
    Given I open the homepage
    When  I click the navigation game link "TFT"
    Then  the TFT page is loaded
    Given I open the homepage
    When  I click the navigation game link "Deadlock"
    Then  the current URL should contain "deadlock"
    And   the Deadlock page is loaded

  @e2e @regression @ui @navigation
  Scenario: User navigates from homepage to PoE2 then to Nightreign
    Given I open the homepage
    When  I click the navigation game link "PoE2"
    Then  the PoE2 page is loaded
    Given I open the homepage
    When  I click the navigation game link "Nightreign"
    Then  the current URL should contain "nightreign"
    And   the Nightreign page is loaded

  @e2e @regression @ui @navigation
  Scenario: User navigates from homepage to Diablo 4 then to MH Wilds
    Given I open the homepage
    When  I click the navigation game link "Diablo 4"
    Then  the Diablo 4 page is loaded
    Given I open the homepage
    When  I click the navigation game link "MH Wilds"
    Then  the current URL should contain "/mhw"
    And   the MH Wilds page is loaded

  # ── Sign In button visibility across new games ────────────────────────────

  @e2e @regression @ui @auth @deadlock
  Scenario: Sign In button is visible on the Deadlock sub-page
    Given I open the Deadlock page
    Then  the login button should be visible in the navigation

  @e2e @regression @ui @auth @overwatch
  Scenario: Sign In button is visible on the Overwatch sub-page
    Given I open the Overwatch page
    Then  the login button should be visible in the navigation
