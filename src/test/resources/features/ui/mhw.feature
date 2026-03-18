# Monster Hunter Wilds page scenarios — verifies the MHW section loads correctly,
# builds and guides sections are present, and enough content links exist.

@regression @ui @mhw
Feature: Monster Hunter Wilds Page

  @smoke @regression @ui @mhw @critical
  Scenario: MH Wilds page loads successfully
    Given I open the MH Wilds page
    Then  the MH Wilds page is loaded

  @regression @ui @mhw
  Scenario: MH Wilds page URL is correct
    Given I open the MH Wilds page
    Then  the current URL should contain "/mhw"

  @regression @ui @mhw
  Scenario: MH Wilds page heading mentions Wilds
    Given I open the MH Wilds page
    Then  the MH Wilds page heading should contain "Wilds"

  @regression @ui @mhw
  Scenario: MH Wilds builds section is visible
    Given I open the MH Wilds page
    Then  the MH Wilds builds section should be visible

  @regression @ui @mhw
  Scenario: MH Wilds guides section is visible
    Given I open the MH Wilds page
    Then  the MH Wilds guides section should be visible

  @regression @ui @mhw
  Scenario: MH Wilds page has sufficient content links
    Given I open the MH Wilds page
    Then  there should be at least 5 MH Wilds content links

