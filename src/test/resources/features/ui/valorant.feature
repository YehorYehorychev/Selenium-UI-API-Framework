# Valorant page scenarios — verifies the Valorant section
# loads correctly and key UI elements are present.
# NOTE: /valorant redirects to /valorant/search — URL check uses "valorant".
# H1 text: "THE MOST COMPREHENSIVE AGENT STATS..." — contains "AGENT".

@regression @ui
Feature: Valorant Page

  @smoke @regression @ui @critical
  Scenario: Valorant page loads successfully
    Given I open the Valorant page
    Then the Valorant page is loaded

  @regression @ui
  Scenario: Valorant page heading contains agent content
    Given I open the Valorant page
    Then the Valorant page heading should contain "agent"

  @regression @ui
  Scenario: Valorant page URL is correct
    Given I open the Valorant page
    Then the current URL should contain "valorant"