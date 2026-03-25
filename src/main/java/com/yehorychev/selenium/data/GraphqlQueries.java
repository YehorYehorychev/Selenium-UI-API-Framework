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

    public static final String SIGN_UP = """
            mutation SignUp($email: String!, $password: String!, $name: String!, $continueFrom: String) {
              signUp(email: $email, password: $password, name: $name, continueFrom: $continueFrom)
            }
            """;

    public static final String REQUEST_PASSWORD_RESET = """
            mutation RequestPasswordReset($email: String!, $redirectUrl: String!) {
              requestPasswordReset(email: $email, lang: "en", game: LOL, redirectUrl: $redirectUrl)
            }
            """;

    public static final String RESET_PASSWORD = """
            mutation ResetPassword($token: String!, $password1: String!) {
              resetPassword(token: $token, password1: $password1)
            }
            """;

    public static final String UPDATE_ACCOUNT_INFO = """
            mutation UpdateAccountInfo($login: String) {
              updateAccountInfo(login: $login) {
                uid
                login
                email
              }
            }
            """;

    public static final String UPDATE_PASSWORD = """
            mutation UpdatePassword($oldPassword: String!, $newPassword: String!) {
              updatePassword(oldPassword: $oldPassword, newPassword: $newPassword)
            }
            """;
}
