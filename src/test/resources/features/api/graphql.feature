# GraphQL API scenarios — verifies the account.mobalytics.gg GraphQL endpoint.
# Mirrors mobalytics-graphql-account.spec.ts from the Playwright framework.

@api @smoke
Feature: GraphQL API

  @smoke @critical
  Scenario: GraphQL endpoint is reachable and returns 200
    When I run the GraphQL health check
    Then the response status code should be 200
    And  the response should match the "graphql-health-check" schema
    And  the response time should be within SLA

  @regression
  Scenario: GraphQL health check returns __typename field
    When I run the GraphQL health check
    Then the response status code should be 200
    And  the response body should contain "__typename"

  @regression @authenticated
  Scenario: Authenticated account query returns all required fields
    Given I am authenticated via API
    When I query the current account via GraphQL
    Then the response status code should be 200
    And  the response JSON path "data.account.uid" should not be null
    And  the response JSON path "data.account.email" should not be null
    And  the response JSON path "data.account.login" should not be null
    And  the response should match the "account-query" schema

  @regression @authenticated
  Scenario: Account uid is a non-empty identifier
    Given I am authenticated via API
    When I query the current account via GraphQL
    Then the response status code should be 200
    And  the account uid should be a valid identifier

  @regression @authenticated
  Scenario: Partial account query returns only requested fields
    Given I am authenticated via API
    When I query the account with partial field selection
    Then the response status code should be 200
    And  the response JSON path "data.account.uid" should not be null
    And  the response JSON path "data.account.email" should not be null

  @regression
  Scenario: Unauthenticated account query is rejected
    When I query the current account via GraphQL
    Then the response status code should be 200
    And  the unauthenticated account response should be rejected
