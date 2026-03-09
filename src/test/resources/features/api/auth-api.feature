# Authentication API scenarios — verifies login, token issuance, and logout
# via the REST API without opening a browser.

@api @auth @smoke
Feature: Authentication API

  @api @auth @smoke @critical
  Scenario: Configured test credentials are present in the environment
    Then the configured test credentials should be available

  @api @auth @smoke @critical
  Scenario: Successful API login returns an auth token
    Given I am authenticated via API
    Then an auth token should be stored in the scenario context

  @api @auth @regression
  Scenario: API logout clears the stored auth token
    Given I am authenticated via API
    Then an auth token should be stored in the scenario context
    When I log out via API
    Then the auth token should no longer be present

