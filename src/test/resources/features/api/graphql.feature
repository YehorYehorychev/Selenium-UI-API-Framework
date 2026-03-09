# GraphQL API scenarios — verifies public and authenticated GraphQL endpoints.
# These scenarios use ApiContext + GraphqlQueries and skip the browser entirely.

@api @smoke
Feature: GraphQL API

  @api @smoke @critical
  Scenario: GraphQL endpoint is reachable and returns 200 for a public query
    When I query the list of supported games via GraphQL
    Then the response status code should be 200

  @api @regression
  Scenario: GetGames query returns a data field
    When I query the list of supported games via GraphQL
    Then the response status code should be 200
    And  the response body should contain "data"

  @api @regression @auth @authenticated
  Scenario: GetCurrentUser query returns user data for an authenticated session
    Given I am authenticated via API
    When I query the current user via GraphQL
    Then the response status code should be 200
    And  the response JSON path "data.currentUser.email" should not be null

