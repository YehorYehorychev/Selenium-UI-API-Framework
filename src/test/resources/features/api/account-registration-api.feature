# Account Registration API scenarios — verifies the signUp GraphQL mutation.
# Tests cover input validation errors only; no happy-path account creation
# to avoid polluting the production database with test accounts.

@api @registration
Feature: Account Registration API

  @regression
  Scenario: Sign up with an invalid email format returns EMAIL_INVALID error
    When I sign up via API with email "not-a-valid-email", password "ValidPass123!" and name "Test User"
    Then the response status code should be 200
    And  the sign-up should have failed with error "EMAIL_INVALID"

  @regression
  Scenario: Sign up with a too-short password returns PASSWORD_TOO_SHORT error
    When I sign up via API with email "shortpwdtest@outlook.com", password "abc" and name "Test User"
    Then the response status code should be 200
    And  the sign-up should have failed with error "PASSWORD_TOO_SHORT"

  @regression
  Scenario: Sign up with an already-registered email returns AUTH_EMAIL_ALREADY_EXISTS error
    When I sign up via API with the configured test email, password "SomePass123!" and name "Duplicate"
    Then the response status code should be 200
    And  the sign-up should have failed with error "AUTH_EMAIL_ALREADY_EXISTS"

  @regression
  Scenario Outline: Sign up with various invalid email formats returns EMAIL_INVALID
    When I sign up via API with email "<email>", password "ValidPass1!" and name "Test"
    Then the response status code should be 200
    And  the sign-up should have failed with error "EMAIL_INVALID"

    Examples:
      | email              |
      | plainaddress       |
      | @missinglocal.com  |
      | missingtld@        |
      | double@@domain.com |
