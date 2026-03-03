# Smoke feature — verifies that browser launches and the home page is accessible.
# Analogue of playwright's smoke.spec.ts

@smoke
Feature: Mobalytics Home Page

  Background:
    Given the browser is open on the home page

  @smoke @navigation
  Scenario: Home page loads successfully
    Then the page title should contain "Mobalytics"

  @smoke @navigation
  Scenario: Home page URL is correct
    Then the current URL should contain "mobalytics.gg"

