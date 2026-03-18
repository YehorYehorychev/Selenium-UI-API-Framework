# Borderlands 4 page scenarios — verifies the Borderlands 4 section loads correctly
# and key content sections are present.

@regression @ui @borderlands4
Feature: Borderlands 4 Page

  @smoke @regression @ui @borderlands4 @critical
  Scenario: Borderlands 4 page loads successfully
    Given I open the Borderlands 4 page
    Then  the Borderlands 4 page is loaded

  @regression @ui @borderlands4
  Scenario: Borderlands 4 page URL is correct
    Given I open the Borderlands 4 page
    Then  the current URL should contain "borderlands-4"

  @regression @ui @borderlands4
  Scenario: Borderlands 4 page heading mentions Borderlands
    Given I open the Borderlands 4 page
    Then  the Borderlands 4 page heading should contain "Borderlands"

  @regression @ui @borderlands4
  Scenario: Borderlands 4 builds section is visible
    Given I open the Borderlands 4 page
    Then  the Borderlands 4 builds section should be visible

  @regression @ui @borderlands4
  Scenario: Borderlands 4 page has sufficient content links
    Given I open the Borderlands 4 page
    Then  there should be at least 5 Borderlands 4 content links

