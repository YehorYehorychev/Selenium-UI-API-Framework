# Password Reset API scenarios — verifies requestPasswordReset and resetPassword mutations.
# requestPasswordReset intentionally returns true for any email (valid or not)
# to prevent user enumeration — this is the expected security behaviour.

@api @password-reset
Feature: Password Reset API

  @api @password-reset @smoke
  Scenario: Password reset request returns HTTP 200 for a known email
    When I request a password reset for email "test@example.com"
    Then the response status code should be 200

  @api @password-reset @regression
  Scenario: Password reset request returns true for a registered email
    When I request a password reset for email "test@example.com"
    Then the response status code should be 200
    And  the password reset request should have returned true

  @api @password-reset @regression
  Scenario: Password reset request returns true for a non-existent email (no user enumeration)
    When I request a password reset for email "definitely_not_registered_xyz99@mailinator.com"
    Then the response status code should be 200
    And  the password reset request should have returned true

  @api @password-reset @regression
  Scenario: Reset password with an invalid token returns UNDEFINED_TOKEN_OPERATION error
    When I reset my password using token "invalid_token_xyz123456" and new password "ValidNewPass1!"
    Then the response status code should be 200
    And  the response body should contain "UNDEFINED_TOKEN_OPERATION"

  @api @password-reset @regression
  Scenario: Reset password with a too-short password returns INPUT_INVALID error
    When I reset my password using token "anytoken12345678" and new password "ab"
    Then the response status code should be 200
    And  the response body should contain "INPUT_INVALID"

  @api @password-reset @regression
  Scenario: Update password without authentication returns UNAUTHENTICATED error
    When I update my password from "OldPassword1!" to "NewPassword2!"
    Then the response status code should be 200
    And  the response body should contain "UNAUTHENTICATED"

