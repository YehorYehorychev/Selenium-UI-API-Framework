# Path of Exile 2 page scenarios — verifies the PoE2 section
# loads correctly and key UI elements are present.

@regression @ui
Feature: Path of Exile 2 Page

  @smoke @regression @ui @critical
  Scenario: PoE2 page loads successfully
    Given I open the PoE2 page
    Then the PoE2 page is loaded

  @regression @ui
  Scenario: PoE2 page heading contains expected text
    Given I open the PoE2 page
    Then the PoE2 page heading should contain "Exile"

  @regression @ui
  Scenario: PoE2 page URL is correct
    Given I open the PoE2 page
    Then the current URL should contain "poe"

  @regression @ui
  Scenario: Class selector is visible on the PoE2 page
    Given I open the PoE2 page
    Then the class selector should be visible

  @regression @ui
  Scenario: Guides section is visible on the PoE2 page
    Given I open the PoE2 page
    Then the guides section should be visible

  @regression @ui @search
  Scenario: Build links are present on the PoE2 page
    Given I open the PoE2 page
    Then there should be at least 3 build cards displayed

