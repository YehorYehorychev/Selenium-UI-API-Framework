# Login page UI scenarios — verifies the sign-in form is accessible,
# input fields are functional, and invalid credentials produce the correct feedback.
#
# NOTE: Scenarios tagged @authenticated require TEST_USER_LOGIN and TEST_USER_PASSWORD
# to be configured in .env. They are tagged @wip until credentials are confirmed in CI.

@regression @ui @auth
Feature: Login Page

  @smoke @regression @ui @auth @critical
  Scenario: Login page loads successfully
    Given I open the login page
    Then the login page is loaded

  @regression @ui @auth
  Scenario: Sign-in with invalid credentials shows an error message
    Given I open the login page
    When I enter email "invalid@example.com" and password "wrongpassword123"
    And  I click the sign in button
    Then I should see a login error message

  @regression @ui @auth
  Scenario: Sign-in with empty fields shows a validation error
    Given I open the login page
    When I click the sign in button
    Then I should see a login error message

  @regression @ui @auth @authenticated @wip
  Scenario: Successful sign-in with valid credentials logs the user in
    Given I open the login page
    When I log in with valid credentials
    Then I should be logged in

