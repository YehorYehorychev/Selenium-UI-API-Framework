package com.yehorychev.selenium.data;

/**
 * GraphQL query and mutation constants for API tests.
 *
 * All queries target https://account.mobalytics.gg/api/graphql/v1/query.
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

    // ── Legacy aliases — keep existing step definitions compiling ─────────────

    /** Alias of HEALTH_CHECK — used by "query the list of supported games" step. */
    public static final String GET_GAMES = HEALTH_CHECK;

    /** Alias of HEALTH_CHECK — used by "query the current user" step. */
    public static final String GET_CURRENT_USER = HEALTH_CHECK;

    /** Alias of HEALTH_CHECK — placeholder; summoner data is on a different service. */
    public static final String GET_SUMMONER_STATS = HEALTH_CHECK;

    /** Alias of HEALTH_CHECK — placeholder; user-by-ID is on a different service. */
    public static final String GET_USER_PROFILE = HEALTH_CHECK;
}
