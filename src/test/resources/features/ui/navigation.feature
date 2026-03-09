# Navigation component scenarios — verifies the site header, logo,
# game links, and login button across the Mobalytics site.

@ui @navigation
Feature: Site Navigation

  Background:
    Given I open the homepage

  @smoke @ui @navigation @critical
  Scenario: Site logo is visible in the header
    Then the site logo should be visible

  @ignore @ui @navigation
  Scenario: Login button is present in the navigation
    Then the login button should be visible in the navigation

  @regression @ui @navigation
  Scenario Outline: Game navigation links are accessible
    Then the navigation should contain game link "<game>"

    Examples:
      | game     |
      | LoL      |
      | TFT      |
      | PoE2     |

  @regression @ui @navigation
  Scenario: Clicking a game link navigates to the correct URL
    When I click the navigation game link "LoL"
    Then the current URL should contain "lol"

