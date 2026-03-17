# Diablo 4 page scenarios — verifies the Diablo 4 section
# loads correctly and key UI elements are present.
# NOTE: canonical URL is /diablo-4 (not /d4).

@regression @ui
Feature: Diablo 4 Page

  @smoke @regression @ui @critical
  Scenario: Diablo 4 page loads successfully
    Given I open the Diablo 4 page
    Then the Diablo 4 page is loaded

  @regression @ui
  Scenario: Diablo 4 page heading contains expected text
    Given I open the Diablo 4 page
    Then the Diablo 4 page heading should contain "Diablo 4"

  @regression @ui
  Scenario: Diablo 4 page URL is correct
    Given I open the Diablo 4 page
    Then the current URL should contain "diablo-4"

  @regression @ui
  Scenario: Diablo 4 builds section is visible
    Given I open the Diablo 4 page
    Then the Diablo 4 builds section should be visible

  @regression @ui
  Scenario: Diablo 4 guides section is visible
    Given I open the Diablo 4 page
    Then the Diablo 4 guides section should be visible
