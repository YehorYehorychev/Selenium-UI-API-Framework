package com.yehorychev.selenium.data;

/**
 * GraphQL query and mutation constants for API tests.
 *
 * All queries are formatted as text blocks and can be passed directly
 * to ApiContext.graphql() or RestAssured.
 *
 * Usage:
 *   Response response = api.graphql(GraphqlQueries.GET_CURRENT_USER);
 *   Response response = api.graphql(GraphqlQueries.GET_USER_PROFILE, Map.of("userId", "123"));
 */
public final class GraphqlQueries {

    private GraphqlQueries() {
    }

    // ── User queries ──────────────────────────────────────────────────────────

    /**
     * Fetches the current authenticated user's profile.
     *
     * Returns: id, username, email, createdAt
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
     * Variables: $userId: ID!
     * Returns: id, username, email, avatar, bio
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
     * Variables: $email: String!, $password: String!
     * Returns: token, user { id, username, email }
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
     * Returns: success: Boolean
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
     * Returns: id, name, slug, iconUrl
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
     * Variables: $summonerName: String!, $region: String!
     * Returns: summoner stats including rank, winrate, KDA, etc.
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
