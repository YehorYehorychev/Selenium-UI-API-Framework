# Homepage UI scenarios — verifies the Mobalytics home page loads correctly,
# hero content is present, and navigation links work as expected.

@smoke @ui
Feature: Mobalytics Home Page

  Background:
    Given I open the homepage

  @smoke @ui @navigation @critical
  Scenario: Home page loads and hero section is visible
    Then the home page is loaded
    And  the header should be visible
    And  the page title should contain "Mobalytics"

  @smoke @ui @navigation
  Scenario: Home page URL is correct
    Then the current URL should contain "mobalytics.gg"

  @smoke @ui
  Scenario: Hero heading contains expected text
    Then the hero heading should contain "gamers"

  @regression @ui @navigation
  Scenario Outline: Game navigation links are present in the header
    Then the nav game "<game>" should be present

    Examples:
      | game     |
      | LoL      |
      | TFT      |
      | Diablo 4 |

  @regression @ui
  Scenario: Download CTA button has a valid href
    Then the download CTA href should not be empty

