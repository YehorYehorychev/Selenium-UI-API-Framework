package com.yehorychev.selenium.data;

/**
 * GraphQL query and mutation constants targeting https://account.mobalytics.gg/api/graphql/v1/query.
 */
public final class GraphqlQueries {

    private GraphqlQueries() {
    }

    public static final String HEALTH_CHECK = """
            query HealthCheck {
              __typename
            }
            """;

    public static final String SIGN_IN = """
            mutation SignIn($email: String!, $password: String!, $continueFrom: String) {
              signIn(email: $email, password: $password, continueFrom: $continueFrom)
            }
            """;

    public static final String SIGN_OUT = """
            mutation SignOut {
              signOut
            }
            """;

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

    public static final String ACCOUNT_QUERY_PARTIAL = """
            query {
              account {
                uid
                email
              }
            }
            """;
}
