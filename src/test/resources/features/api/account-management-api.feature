# Account Management API scenarios — verifies updateAccountInfo and updatePassword mutations.
# Unauthenticated tests verify that protected mutations return UNAUTHENTICATED.
# The authenticated update test re-applies the current login to stay idempotent.

@api @account
Feature: Account Management API

  @regression
  Scenario: Update account info without authentication returns UNAUTHENTICATED error
    When I update my account login to "somelogin"
    Then the response status code should be 200
    And  the response body should contain "UNAUTHENTICATED"
    And  the response should match the "graphql-errors" schema

  @regression
  Scenario: Update password without authentication returns UNAUTHENTICATED error
    When I update my password from "OldPassword1!" to "NewPassword2!"
    Then the response status code should be 200
    And  the response body should contain "UNAUTHENTICATED"
    And  the response should match the "graphql-errors" schema

  @regression @authenticated
  Scenario: Authenticated user can update account login — idempotent re-apply of current value
    Given I am authenticated via API
    When I query the current account via GraphQL
    And  I save response JSON path "data.account.login" as "currentLogin"
    And  I update my account login to the saved context value "currentLogin"
    Then the response status code should be 200
    And  the account info update should have succeeded
    And  the response JSON path "data.updateAccountInfo.uid" should not be null
    And  the response JSON path "data.updateAccountInfo.login" should not be null
    And  the response JSON path "data.updateAccountInfo.email" should not be null
