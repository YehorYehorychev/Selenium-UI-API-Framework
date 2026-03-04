package com.yehorychev.selenium.data;

/**
 * GraphQL query and mutation constants for API tests.
 *
 * <p>All queries are formatted for readability and can be passed directly to
 * {@link io.restassured.RestAssured} or a GraphQL client library.
 *
 * <p>Usage:
 * <pre>{@code
 *   String query = GraphqlQueries.GET_USER_PROFILE;
 *   Response response = RestAssured.given()
 *       .body(Map.of("query", query, "variables", Map.of("userId", "123")))
 *       .post("/api/graphql/v1/query");
 * }</pre>
 */
public final class GraphqlQueries {

    private GraphqlQueries() {}

    // ── User queries ──────────────────────────────────────────────────────────

    /**
     * Fetches the current authenticated user's profile.
     *
     * <p>Returns: {@code id, username, email, createdAt}
     */
    public static final String GET_CURRENT_USER = """
        query GetCurrentUser {
          currentUser {
            id
            username
            email
            createdAt
          }
        }
        """;

    /**
     * Fetches a user profile by user ID.
     *
     * <p>Variables: {@code $userId: ID!}
     * <p>Returns: {@code id, username, email, avatar, bio}
     */
    public static final String GET_USER_PROFILE = """
        query GetUserProfile($userId: ID!) {
          user(id: $userId) {
            id
            username
            email
            avatar
            bio
          }
        }
        """;

    // ── Authentication mutations ──────────────────────────────────────────────

    /**
     * Performs a login mutation via GraphQL.
     *
     * <p>Variables: {@code $email: String!, $password: String!}
     * <p>Returns: {@code token, user { id, username, email }}
     */
    public static final String LOGIN = """
        mutation Login($email: String!, $password: String!) {
          login(email: $email, password: $password) {
            token
            user {
              id
              username
              email
            }
          }
        }
        """;

    /**
     * Performs a logout mutation (invalidates token server-side).
     *
     * <p>Returns: {@code success: Boolean}
     */
    public static final String LOGOUT = """
        mutation Logout {
          logout {
            success
          }
        }
        """;

    // ── Game data queries ─────────────────────────────────────────────────────

    /**
     * Fetches a list of supported games.
     *
     * <p>Returns: {@code id, name, slug, iconUrl}
     */
    public static final String GET_GAMES = """
        query GetGames {
          games {
            id
            name
            slug
            iconUrl
          }
        }
        """;

    /**
     * Fetches detailed statistics for a summoner.
     *
     * <p>Variables: {@code $summonerName: String!, $region: String!}
     * <p>Returns: summoner stats including rank, winrate, KDA, etc.
     */
    public static final String GET_SUMMONER_STATS = """
        query GetSummonerStats($summonerName: String!, $region: String!) {
          summoner(name: $summonerName, region: $region) {
            id
            name
            level
            profileIconId
            stats {
              rank
              tier
              wins
              losses
              winrate
              kda
            }
          }
        }
        """;
}

