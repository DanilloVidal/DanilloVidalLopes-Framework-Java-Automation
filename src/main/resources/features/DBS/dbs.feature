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


  Scenario: DBS Register swine samples
    Given Im on DBS home Page
    When I click the Register "swine" samples button
   # And I select "DANONE, S.A" in the "Customer" field
    And I select "Automation" in the "Farm" field
    And I enter "Automation Test" in the "Barn name" field
    And I choose "Outdoor" in the "Housing" option
    And I select "Farrow to finish" in the "Production system" field
    And I select "Customer service" in the "Purpose of analysis" field
    And I click Next on the register form
    And the "Is this sample for an End Customer?" field should show the required message
    And I select "Yes" in the "Is this sample for an End Customer?" field
    And I click Next on the register form
    And the "End Customer name" field should show the required message
    And I enter "Automation Test" in the "End Customer name" field
    And the "End Customer name" field should not be empty


  Scenario: DBS Register poultry samples
    Given Im on DBS home Page
    When I click the Register "poultry" samples button


  Scenario: DBS Register ruminant samples
    Given Im on DBS home Page
    When I click the Register "ruminant" samples button
