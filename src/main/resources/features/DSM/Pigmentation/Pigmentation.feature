Feature: Pigmentation calculator

  @Pigmentation
  Scenario: Pigmentation Login Page
    Given I open the Pigmentation page
    Then I should see the Pigmentation Microsoft login page
    When I do a Pigmentation login
    Then I should see the Pigmentation home page
    And I save the Pigmentation login session

  @PigmentationSession
  Scenario: Pigmentation Login with saved session
    Given Im on Pigmentation home Page

  @PigmentationCalculator
  Scenario: Pigmentation open the calculator
    Given Im on Pigmentation home Page
    When I start the Pigmentation calculator
    Then I should see the Pigmentation calculator
