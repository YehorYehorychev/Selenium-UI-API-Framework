# LoL Champions list page scenarios — verifies all champions are listed,
# search input is available, and individual champion pages are accessible.

@regression @ui @lol
Feature: LoL Champions List Page

  @smoke @regression @ui @lol @critical
  Scenario: LoL Champions page loads successfully
    Given I open the LoL Champions page
    Then  the LoL Champions page is loaded

  @regression @ui @lol
  Scenario: LoL Champions page URL is correct
    Given I open the LoL Champions page
    Then  the current URL should contain "/lol/champions"

  @regression @ui @lol
  Scenario: LoL Champions page heading mentions champions
    Given I open the LoL Champions page
    Then  the LoL Champions heading should contain "Champions"

  @regression @ui @lol @search
  Scenario: LoL Champions page lists a large number of champions
    Given I open the LoL Champions page
    Then  there should be at least 100 champions listed on the LoL Champions page

  @regression @ui @lol @search
  Scenario: LoL Champions page search input is visible
    Given I open the LoL Champions page
    Then  the LoL Champions search input should be visible

  @regression @ui @lol
  Scenario: Known champion Ahri is present on the champions page
    Given I open the LoL Champions page
    Then  champion "ahri" should be present on the LoL Champions page

  @regression @ui @lol
  Scenario Outline: Key champions are listed on the LoL Champions page
    Given I open the LoL Champions page
    Then  champion "<champion>" should be present on the LoL Champions page

    Examples:
      | champion |
      | jinx     |
      | yasuo    |
      | lux      |
      | thresh   |

