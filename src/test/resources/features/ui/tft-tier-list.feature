# TFT Tier List page scenarios — verifies the tier list redirects correctly,
# loads comp content, unit links, and sub-tab navigation links.
# NOTE: /tft/tier-list redirects to /tft/tier-list/team-comps.
# Sub-tabs (Champions, Items, Traits, Augments) use /tft/tier-list/ prefix.

@regression @ui @tft
Feature: TFT Tier List Page

  @smoke @regression @ui @tft @critical
  Scenario: TFT Tier List page loads successfully
    Given I open the TFT Tier List page
    Then  the TFT Tier List page is loaded

  @regression @ui @tft
  Scenario: TFT Tier List URL contains tier-list after redirect
    Given I open the TFT Tier List page
    Then  the current URL should contain "tier-list"

  @regression @ui @tft
  Scenario: TFT Tier List heading mentions tier list or comps
    Given I open the TFT Tier List page
    Then  the TFT Tier List heading should contain "Tier"

  @regression @ui @tft
  Scenario: TFT Tier List contains champion unit links
    Given I open the TFT Tier List page
    Then  there should be at least 10 unit links on the TFT Tier List

  @regression @ui @tft @navigation
  Scenario: TFT Tier List has sub-tab navigation links
    Given I open the TFT Tier List page
    Then  the TFT Tier List should have team comp links


