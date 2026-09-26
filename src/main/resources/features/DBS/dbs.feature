Feature: DBS

  @regressao @DBS
Scenario: DBS Login Page
  Given I open the DBS page
  When I do a dbs login
  Then I should see the DBS home page


  Scenario: DBS Order new kits
    Given Im on DBS home Page
    When I click the Order new kit button
    And I select the farm "big chicken farm edited"
    And I click Yes for the farm
    Then the selected farm should be "big chicken farm edited"
    When I select the farm testing option
    And I select the analysis purpose "Customer service"
    And I click Next
    And I click the plus button 7 times
    And I click the minus button 4 times
    And I enter the kit quantity "5"
    And I click Place order
    Then the order review text should be displayed
    When I confirm the order
    Then I should see the order success message
