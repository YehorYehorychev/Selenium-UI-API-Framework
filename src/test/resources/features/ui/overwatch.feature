# Overwatch page scenarios — verifies the Overwatch section loads correctly,
# stadium builds section is present, sufficient hero links exist,
# and the sign-in button is visible.

@regression @ui @overwatch
Feature: Overwatch Page

  @smoke @regression @ui @overwatch @critical
  Scenario: Overwatch page loads successfully
    Given I open the Overwatch page
    Then  the Overwatch page is loaded

  @regression @ui @overwatch
  Scenario: Overwatch page URL is correct
    Given I open the Overwatch page
    Then  the current URL should contain "overwatch"

  @regression @ui @overwatch
  Scenario: Overwatch page heading mentions Overwatch
    Given I open the Overwatch page
    Then  the Overwatch page heading should contain "Overwatch"

  @regression @ui @overwatch
  Scenario: Overwatch stadium builds section is visible
    Given I open the Overwatch page
    Then  the Overwatch stadium builds section should be visible

  @regression @ui @overwatch
  Scenario: Overwatch page has many hero guide links
    Given I open the Overwatch page
    Then  there should be at least 10 Overwatch hero links

  @regression @ui @overwatch @auth
  Scenario: Sign In button is visible on the Overwatch page
    Given I open the Overwatch page
    Then  the sign in button should be visible on the Overwatch page

