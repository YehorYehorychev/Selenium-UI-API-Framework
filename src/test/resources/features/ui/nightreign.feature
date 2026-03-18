# Elden Ring Nightreign page scenarios — verifies the Nightreign section loads correctly,
# all four content sections (Nightfarers, Nightlords, Builds, Guides) are present,
# and sufficient content links exist.

@regression @ui @nightreign
Feature: Elden Ring Nightreign Page

  @smoke @regression @ui @nightreign @critical
  Scenario: Nightreign page loads successfully
    Given I open the Nightreign page
    Then  the Nightreign page is loaded

  @regression @ui @nightreign
  Scenario: Nightreign page URL is correct
    Given I open the Nightreign page
    Then  the current URL should contain "nightreign"

  @regression @ui @nightreign
  Scenario: Nightreign page heading mentions Nightreign
    Given I open the Nightreign page
    Then  the Nightreign page heading should contain "Nightreign"

  @regression @ui @nightreign
  Scenario: Nightreign Nightfarers section is visible
    Given I open the Nightreign page
    Then  the Nightreign Nightfarers section should be visible

  @regression @ui @nightreign
  Scenario: Nightreign Nightlords section is visible
    Given I open the Nightreign page
    Then  the Nightreign Nightlords section should be visible

  @regression @ui @nightreign
  Scenario: Nightreign builds section is visible
    Given I open the Nightreign page
    Then  the Nightreign builds section should be visible

  @regression @ui @nightreign
  Scenario: Nightreign guides section is visible
    Given I open the Nightreign page
    Then  the Nightreign guides section should be visible

  @regression @ui @nightreign
  Scenario: Nightreign page has sufficient content links
    Given I open the Nightreign page
    Then  there should be at least 10 Nightreign content links

