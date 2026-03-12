package com.yehorychev.selenium.data;

/**
 * GraphQL query and mutation constants for API tests.
 *
 * All queries target <a href="https://account.mobalytics.gg/api/graphql/v1/query">...</a>.
 * This is the accounts service — it handles auth and user account operations.
 *
 * Usage:
 *   Response response = api.graphql(GraphqlQueries.HEALTH_CHECK);
 *   Response response = api.graphql(GraphqlQueries.SIGN_IN, vars);
 */
public final class GraphqlQueries {

    private GraphqlQueries() {
    }

    // ── Health check ──────────────────────────────────────────────────────────

    /**
     * Introspects the root query type name — works without authentication.
     * Used as a lightweight health check to verify the endpoint is reachable.
     *
     * Returns: __typename (always "Query")
     */
    public static final String HEALTH_CHECK = """
            query HealthCheck {
              __typename
            }
            """;

    // ── Authentication mutations ──────────────────────────────────────────────

    /**
     * Signs in with email, password and optional continueFrom redirect URL.
     * Sets a session cookie in the response. Returns Boolean (true on success).
     *
     * Variables: $email: String!, $password: String!, $continueFrom: String
     */
    public static final String SIGN_IN = """
            mutation SignIn($email: String!, $password: String!, $continueFrom: String) {
              signIn(email: $email, password: $password, continueFrom: $continueFrom)
            }
            """;

    /**
     * Signs out the currently authenticated user.
     * Returns Boolean (true on success).
     */
    public static final String SIGN_OUT = """
            mutation SignOut {
              signOut
            }
            """;

    // ── Account queries ───────────────────────────────────────────────────────

    /**
     * Fetches all fields for the currently authenticated user's account.
     * Requires a valid session cookie obtained via SIGN_IN.
     *
     * Returns: uid, email, login, level, referrerCode, referralStatus
     */
    public static final String ACCOUNT_QUERY = """
            query {
              account {
                uid
                email
                login
                level
                referrerCode
                referralStatus
              }
            }
            """;

    /**
     * Fetches only uid and email — partial field selection.
     * Used to verify GraphQL partial query support.
     */
    public static final String ACCOUNT_QUERY_PARTIAL = """
            query {
              account {
                uid
                email
              }
            }
            """;

    /*
     The game data queries (games list, summoner stats, user profile by ID) live on
     a separate game-data service, NOT on the accounts GraphQL endpoint tested here.

     GET_CURRENT_USER is the one exception: it IS available on this service and now
     points to the real account query.
     */

    /**
     * Fetches the current authenticated user's full account — real query on the accounts service.
     * Requires a valid session cookie obtained via SIGN_IN.
     */
    public static final String GET_CURRENT_USER = ACCOUNT_QUERY;

    /**
     * Placeholder: the games catalogue lives on a separate game-data service.
     * Falls back to a health check until game-data API testing is added (Phase 4).
     */
    public static final String GET_GAMES = HEALTH_CHECK;

    /**
     * Placeholder: summoner stats live on a separate game-data service.
     * Falls back to a health check until game-data API testing is added (Phase 4).
     */
    public static final String GET_SUMMONER_STATS = HEALTH_CHECK;

    /**
     * Placeholder: user-profile-by-ID lives on a separate game-data service.
     * Falls back to a health check until game-data API testing is added (Phase 4).
     */
    public static final String GET_USER_PROFILE = HEALTH_CHECK;
}
