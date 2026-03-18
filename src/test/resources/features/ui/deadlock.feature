# Deadlock page scenarios — verifies the Deadlock section loads correctly,
# heroes and builds sections are present, and the sign-in button is accessible.

@regression @ui @deadlock
Feature: Deadlock Page

  @smoke @regression @ui @deadlock @critical
  Scenario: Deadlock page loads successfully
    Given I open the Deadlock page
    Then  the Deadlock page is loaded

  @regression @ui @deadlock
  Scenario: Deadlock page URL is correct
    Given I open the Deadlock page
    Then  the current URL should contain "deadlock"

  @regression @ui @deadlock
  Scenario: Deadlock page heading contains hero content
    Given I open the Deadlock page
    Then  the Deadlock page heading should contain "Deadlock"

  @regression @ui @deadlock
  Scenario: Deadlock page has a heroes section
    Given I open the Deadlock page
    Then  the Deadlock heroes section should be visible

  @regression @ui @deadlock
  Scenario: Deadlock page has a featured builds section
    Given I open the Deadlock page
    Then  the Deadlock builds section should be visible

  @regression @ui @deadlock
  Scenario: Deadlock page has content links for heroes and builds
    Given I open the Deadlock page
    Then  there should be at least 3 Deadlock content links

  @regression @ui @deadlock @auth
  Scenario: Sign In button is visible on the Deadlock page
    Given I open the Deadlock page
    Then  the sign in button should be visible on the Deadlock page

