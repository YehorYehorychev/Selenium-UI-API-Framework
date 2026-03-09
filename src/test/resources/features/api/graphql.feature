# GraphQL API scenarios — verifies public and authenticated GraphQL endpoints.
# These scenarios use ApiContext + GraphqlQueries and skip the browser entirely.

@api @smoke
Feature: GraphQL API

  @api @smoke @critical
  Scenario: GraphQL endpoint is reachable and returns 200 for a public query
    When I query the list of supported games via GraphQL
    Then the response status code should be 200

  @api @regression
  Scenario: GraphQL health check returns __typename field
    When I query the list of supported games via GraphQL
    Then the response status code should be 200
    And  the response body should contain "__typename"

  @api @regression @auth
  Scenario: Authenticated GraphQL query succeeds after sign-in
    Given I am authenticated via API
    When I query the list of supported games via GraphQL
    Then the response status code should be 200
    And  the response body should contain "__typename"
