# Authentication API scenarios — verifies sign-in via the GraphQL signIn mutation.
# Auth is cookie-based; the signIn mutation returns Boolean (true/false).
# Mirrors mobalytics-graphql-auth.spec.ts from the Playwright framework.

@api @auth @smoke
Feature: Authentication API

  @smoke @critical
  Scenario: Configured test credentials are present in the environment
    Then the configured test credentials should be available

  @smoke @critical
  Scenario: Successful API sign-in returns true
    Given I am authenticated via API
    Then an auth token should be stored in the scenario context

  @regression
  Scenario: Sign-in with valid credentials sets session cookies
    Given I am authenticated via API
    Then an auth token should be stored in the scenario context

  @regression
  Scenario: API sign-out clears the stored session marker
    Given I am authenticated via API
    Then an auth token should be stored in the scenario context
    When I log out via API
    Then the auth token should no longer be present

  @regression @authenticated
  Scenario: Authenticated account query returns uid and email
    Given I am authenticated via API
    When I query the current account via GraphQL
    Then the response status code should be 200
    And  the response JSON path "data.account.uid" should not be null
    And  the response JSON path "data.account.email" should not be null

  @regression @authenticated
  Scenario: Account email matches the sign-in credential
    Given I am authenticated via API
    When I query the current account via GraphQL
    Then the response status code should be 200
    And  the account email should match the configured test email

  @regression
  Scenario: Sign-in with invalid credentials returns false or errors
    When I sign in via API with email "wrong@example.com" and password "wrongpassword"
    Then the sign-in should have failed