# Smoke feature — verifies that browser launches and the home page is accessible.
# Uses the HomePage Page Object.

@smoke
Feature: Mobalytics Home Page

  Background:
    Given the browser is open on the home page

  @smoke @navigation
  Scenario: Home page loads successfully
    Then the home page is loaded
    And  the header should be visible
    And  the page title should contain "Mobalytics"

  @smoke @navigation
  Scenario: Home page URL is correct
    Then the current URL should contain "mobalytics.gg"

  @smoke @navigation
  Scenario: Hero heading is displayed
    Then the hero heading should contain "gamers"

  @smoke @navigation
  Scenario Outline: Game nav links are present in the header
    Then the nav game "<game>" should be present

    Examples:
      | game     |
      | LoL      |
      | TFT      |
      | Diablo 4 |

