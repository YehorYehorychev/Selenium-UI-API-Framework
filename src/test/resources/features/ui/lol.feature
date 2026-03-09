# League of Legends page scenarios — verifies the LoL section
# loads correctly and key UI elements are present.

@regression @ui
Feature: League of Legends Page

  @regression @ui @critical
  Scenario: LoL page loads successfully
    Given I open the LoL page
    Then the LoL page is loaded

  @regression @ui
  Scenario: LoL page heading contains expected text
    Given I open the LoL page
    Then the LoL page heading should contain "League"

  @regression @ui
  Scenario: LoL page URL is correct
    Given I open the LoL page
    Then the current URL should contain "/lol"

