# Navigation component scenarios — verifies the site header, logo,
# game links, and login button across the Mobalytics site.

@ui @navigation
Feature: Site Navigation

  Background:
    Given I open the homepage

  @smoke @ui @navigation @critical
  Scenario: Site logo is visible in the header
    Then the site logo should be visible

  @smoke @ui @navigation @critical
  Scenario: Sign In button is visible on a game sub-page
    When I navigate to the LoL page
    Then the login button should be visible in the navigation

  @regression @ui @navigation
  Scenario Outline: Game navigation links are accessible
    Then the navigation should contain game link "<game>"

    Examples:
      | game          |
      | LoL           |
      | TFT           |
      | PoE2          |
      | Diablo 4      |
      | Borderlands 4 |
      | Nightreign    |
      | Deadlock      |
      | MH Wilds      |

  @regression @ui @navigation
  Scenario Outline: Clicking a game link navigates to the correct URL
    When I click the navigation game link "<game>"
    Then the current URL should contain "<urlFragment>"

    Examples:
      | game          | urlFragment         |
      | LoL           | /lol                |
      | TFT           | /tft                |
      | PoE2          | poe                 |
      | Diablo 4      | diablo-4            |
      | Borderlands 4 | borderlands-4       |
      | Nightreign    | elden-ring-nightreign |
      | Deadlock      | deadlock            |
      | MH Wilds      | /mhw                |
