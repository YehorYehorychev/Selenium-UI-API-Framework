# Authentication API scenarios — verifies sign-in via the GraphQL signIn mutation.
# Auth is cookie-based; no token is returned in the response body.

@api @auth @smoke
Feature: Authentication API

  @api @auth @smoke @critical
  Scenario: Configured test credentials are present in the environment
    Then the configured test credentials should be available

  @api @auth @smoke @critical
  Scenario: Successful API sign-in sets the session marker in scenario context
    Given I am authenticated via API
    Then an auth token should be stored in the scenario context

  @api @auth @regression
  Scenario: API sign-out clears the stored session marker
    Given I am authenticated via API
    Then an auth token should be stored in the scenario context
    When I log out via API
    Then the auth token should no longer be present
