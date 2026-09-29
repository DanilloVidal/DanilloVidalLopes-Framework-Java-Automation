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
    And I select "Automation" in the Farm field
    And I enter "Automation Test" in the "Barn name" field
    And I choose "Outdoor" in the "Housing" option
    And I select "Farrow to finish" in the Production system field
    And I select "Customer service" in the Purpose of analysis field
    And I click Next on the register form
    And the "Is this sample for an End Customer?" field should show the required message
    And I select "Yes" in the End Customer field
    And I click Next on the register form
    And the "End Customer name" field should show the required message
    And I enter "Automation Test" in the "End Customer name" field
    And the "End Customer name" field should not be empty
    # Animal information
    And I click Next on the register form
    And I select "Piglets" in the Physiological stage field
    And the "Average weight" field should be empty
    And the "Specific problem area of interest" field should be disabled
    And I click Next on the register form
    And the "Average weight" field should show the required message
    And the "Reason for analysis" field should show the required message
    And the "Sex" field should show the required message
    And the "Genetics supplier" field should show the required message
    And the "Genetics line" field should show the required message
    And I select "10-15 kg / 20-30 lbs" in the Average weight field
    And I select "Immunity" in the Reason for analysis field
    And the "Specific problem area of interest" field should be enabled
    And the "Specific problem area of interest" field should be empty
    And I click Next on the register form
    And the "Specific problem area of interest" field should show the required message
    And I select "Mortality" in the Specific problem area of interest field
    And I choose "Mixed" in the "Sex" option
    And I select "DanBred" in the Genetics supplier field
    And the "Genetics line" field should be empty
    And I select "Duroc" in the Genetics line field
    # Feed information
    And I click Next on the register form
    And I enter "66" in the Vitamin D3 field
    And the Total vitamin D3 level in the diet field should be "66"
    And I enter "123" in the Vitamin 25-OH level field
    And the 25-OH-D3 field should be "4920"
    And the Total vitamin D3 level in the diet field should be "4986"
    And I enter "3" in the Total Calcium field
    And the Total Calcium field should show the message "Please, provide a number between the range of 0 to 2"
    And I enter "-99" in the Total Calcium field
    And the Total Calcium field should show the message "Please, provide a number between the range of 0 to 2"
    And I enter "2" in the Total Calcium field
    And the Total Calcium field should not show an error message
    And I enter "3" in the Total Phosphorus field
    And the Total Phosphorus field should show the message "Please, provide a number between the range of 0 to 2"
    And I enter "-99" in the Total Phosphorus field
    And the Total Phosphorus field should show the message "Please, provide a number between the range of 0 to 2"
    And I enter "1" in the Total Phosphorus field
    And the Total Phosphorus field should not show an error message
    And I enter "99" in the Phytase field
    And I select "OTU" in the Phytase unit field
    # Register cards
    And I click Next on the register form
    And I click the Register sample button
    And the "Sample collection date" field should show the required message
    And the "DBS sample card ID" field should show the required message
    And I enter the current date in the Sample collection date field
    And the Sample collection date field should be the current date
    And I choose "Yes" in the Verax sampling session option
    And I enter a random card number in the DBS sample card ID field
    And I enter "Test Test" in the Animal details field
    And I choose "Yes" in the increased mortality option
    And I enter the following text in the Additional notes field:
      """
      I watch how the Moon
      Sits in the sky on a dark night
      Shining with the light from the Sun
      The Sun doesn't give light to the Moon, assuming
      The Moon is gonna owe it one
      It makes me think of how you act to me, you do
      Favors then rapidly
      Just turn around and start asking me about
      Things that you want back from me

      I'm sick of the tension, sick of the hunger
      Sick of you acting like I owe you this
      Find another place to feed your greed
      While I find a place to rest

      I wanna be in another place
      I hate when you say you don't understand
      (You'll see it's not meant to be)
      I wanna be in the energy
      Not with the enemy, a place for my head
      """
    # Envio
    And I click the Register sample button
    And the submit confirmation should show the sample values
    And I click Cancel on the submit confirmation
    And I click the Register sample button
    And I click Submit on the submit confirmation
    And the Samples registered message should be displayed
    And I click Close on the Samples registered message
    Then I should see the DBS home page


  Scenario: DBS Register poultry samples
    Given Im on DBS home Page
    When I click the Register "poultry" samples button


  Scenario: DBS Register ruminant samples
    Given Im on DBS home Page
    When I click the Register "ruminant" samples button
