# GraphQL error-handling scenarios — verifies that the API returns well-formed error
# responses for malformed or invalid requests (protocol-level behaviour).

@api @regression
Feature: GraphQL Error Handling

  @api @regression
  Scenario: Querying a non-existent field returns HTTP 422 with a descriptive error
    When I send a POST request to "/api/graphql/v1/query" with body:
      """
      {"query": "query { nonExistentField }"}
      """
    Then the response status code should be 422
    And  the response body should contain "nonExistentField"

  @api @regression
  Scenario: Missing required argument returns HTTP 422
    When I send a POST request to "/api/graphql/v1/query" with body:
      """
      {"query": "mutation { signIn(email: \"test@test.com\") }"}
      """
    Then the response status code should be 422
    And  the response body should contain "errors"

  @api @regression
  Scenario: Submitting an empty query string returns an error response
    When I send a POST request to "/api/graphql/v1/query" with body:
      """
      {"query": ""}
      """
    Then the response body should contain "errors"

  @api @regression
  Scenario: GraphQL introspection is disabled
    When I send a POST request to "/api/graphql/v1/query" with body:
      """
      {"query": "{ __schema { queryType { name } } }"}
      """
    Then the response status code should be 200
    And  the response body should contain "introspection disabled"

  @api @regression
  Scenario: Providing an unknown mutation name returns HTTP 422 with a suggestion
    When I send a POST request to "/api/graphql/v1/query" with body:
      """
      {"query": "mutation { updateAccount(login: \"x\") { uid } }"}
      """
    Then the response status code should be 422
    And  the response body should contain "updateAccountInfo"

