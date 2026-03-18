# LoL Champion Build page scenarios — verifies that individual champion pages
# load correctly, contain builds, runes, matchup content, and counter links.

@regression @ui @lol
Feature: LoL Champion Build Page

  @smoke @regression @ui @lol @critical
  Scenario: Ahri build page loads successfully
    Given I open the build page for champion "ahri"
    Then  the champion build page is loaded

  @regression @ui @lol
  Scenario: Ahri build page URL is correct
    Given I open the build page for champion "ahri"
    Then  the current URL should contain "/lol/champions/ahri"

  @regression @ui @lol
  Scenario: Champion build page heading contains champion name
    Given I open the build page for champion "ahri"
    Then  the champion build heading should contain "Ahri"

  @regression @ui @lol
  Scenario: Champion build page has a Builds section
    Given I open the build page for champion "ahri"
    Then  the champion builds section should be present

  @regression @ui @lol
  Scenario: Champion build page has a Runes section
    Given I open the build page for champion "ahri"
    Then  the champion runes section should be present

  @regression @ui @lol
  Scenario: Champion build page has a Matchups section
    Given I open the build page for champion "ahri"
    Then  the champion matchups section should be present

  @regression @ui @lol
  Scenario: Champion build page has counter links
    Given I open the build page for champion "ahri"
    Then  there should be at least 5 counter links on the champion build page

  @regression @ui @lol
  Scenario: Champion build page has role build variant links
    Given I open the build page for champion "ahri"
    Then  there should be at least 1 role build links on the champion build page

  @regression @ui @lol
  Scenario Outline: Various champion build pages load successfully
    Given I open the build page for champion "<champion>"
    Then  the champion build page is loaded
    And   the current URL should contain "/lol/champions/<champion>"

    Examples:
      | champion |
      | jinx     |
      | yasuo    |
      | thresh   |

