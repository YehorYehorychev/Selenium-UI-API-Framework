# LoL Tier List page scenarios — verifies the Tier List page loads correctly,
# champion links are present, methodology section exists, and patch info is visible.

@regression @ui @lol
Feature: LoL Tier List Page

  @smoke @regression @ui @lol @critical
  Scenario: LoL Tier List page loads successfully
    Given I open the LoL Tier List page
    Then  the LoL Tier List page is loaded

  @regression @ui @lol
  Scenario: LoL Tier List URL is correct
    Given I open the LoL Tier List page
    Then  the current URL should contain "/lol/tier-list"

  @regression @ui @lol
  Scenario: LoL Tier List heading mentions tier list content
    Given I open the LoL Tier List page
    Then  the LoL Tier List heading should contain "Tier List"

  @regression @ui @lol
  Scenario: LoL Tier List displays many champion links
    Given I open the LoL Tier List page
    Then  there should be at least 50 champion links on the LoL Tier List

  @regression @ui @lol
  Scenario: LoL Tier List methodology section is present
    Given I open the LoL Tier List page
    Then  the LoL Tier List methodology section should be present

  @regression @ui @lol
  Scenario: LoL Tier List patch information is visible
    Given I open the LoL Tier List page
    Then  the LoL Tier List patch info should be present

  @regression @ui @lol
  Scenario: LoL Tier List filter button is present
    Given I open the LoL Tier List page
    Then  the LoL Tier List filter button should be present

