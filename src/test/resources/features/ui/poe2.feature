# Path of Exile 2 page scenarios — verifies the PoE2 section
# loads correctly and key UI elements are present.

@regression @ui
Feature: Path of Exile 2 Page

  @regression @ui @critical
  Scenario: PoE2 page loads successfully
    Given I open the PoE2 page
    Then the PoE2 page is loaded

  @regression @ui
  Scenario: PoE2 page URL is correct
    Given I open the PoE2 page
    Then the current URL should contain "poe"

