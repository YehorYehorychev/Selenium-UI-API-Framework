# Teamfight Tactics page scenarios — verifies the TFT section
# loads correctly and key UI elements are present.

@regression @ui
Feature: Teamfight Tactics Page

  @smoke @regression @ui @critical
  Scenario: TFT page loads successfully
    Given I open the TFT page
    Then the TFT page is loaded

  @regression @ui
  Scenario: TFT page heading contains expected text
    Given I open the TFT page
    Then the TFT page heading should contain "Teamfight"

  @regression @ui
  Scenario: TFT page URL is correct
    Given I open the TFT page
    Then the current URL should contain "/tft"

  @regression @ui
  Scenario: TFT tier list section is visible
    Given I open the TFT page
    Then the TFT tier list section should be visible

