# TFT Team Comps page scenarios — verifies the team compositions page loads,
# comp entries (via /tft/comps-guide/ links) are visible, unit links are present,
# and the tier list navigation link exists.
# NOTE: Individual comp entries are linked as /tft/comps-guide/{slug}.

@regression @ui @tft
Feature: TFT Team Comps Page

  @smoke @regression @ui @tft @critical
  Scenario: TFT Team Comps page loads successfully
    Given I open the TFT Team Comps page
    Then  the TFT Team Comps page is loaded

  @regression @ui @tft
  Scenario: TFT Team Comps page URL is correct
    Given I open the TFT Team Comps page
    Then  the current URL should contain "/tft/team-comps"

  @regression @ui @tft
  Scenario: TFT Team Comps heading mentions team comps
    Given I open the TFT Team Comps page
    Then  the TFT Team Comps heading should contain "Comps"

  @regression @ui @tft
  Scenario: TFT Team Comps page has comp entry links
    Given I open the TFT Team Comps page
    Then  the TFT Team Comps page should have comp cards

  @regression @ui @tft
  Scenario: TFT Team Comps page has unit links
    Given I open the TFT Team Comps page
    Then  the TFT Team Comps page should have unit links

  @regression @ui @tft @navigation
  Scenario: TFT Team Comps page contains a link to the TFT Tier List
    Given I open the TFT Team Comps page
    Then  the TFT Team Comps page should have a tier list link


