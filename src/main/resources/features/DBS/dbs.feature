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
    # Farm information
    And I click Next on the register form
    And the "Farm" field should show the required message
    And the "Purpose of analysis" field should show the required message
    And the "Is this sample for an End Customer?" field should show the required message
   # And I select "TEST NODE" in the Customer field
    And I select "Test Poultry" in the Farm field
    And I enter a random number in the House number field
    And I choose "Outdoor / Open-sided" in the Housing option
    And I select "Other" in the Production system field
    And I enter "Bisteca" in the Production system (other) field
    And I select "Scientific projects" in the Purpose of analysis field
    And I select "No" in the End Customer field
    # Animal information
    And I click Next on the register form
    And I click Next on the register form
    And the "Sub-species" field should show the required message
    And the "Sex" field should show the required message
    And the "Genetic" field should show the required message
    And the "Breed and strain" field should show the required message
    And I select "Broiler Breeders" in the Sub-species field
    And I click Next on the register form
    And the Any challenge in the flock field should be marked as required
    And I select "Fertility" in the Any challenge in the flock field
    And I enter "55" in the Clinical challenge unit field
    And I choose "As hatched" in the Sex option
    And I select "Cobb" in the Genetic field
    And I enter "null" in the Breed and strain field
    And I select "Average egg weight" in the Flock performance at sampling field
    And I enter "88" in the Flock performance at sample unit field
    # Feed information
    And I click Next on the register form
    And I click Next on the register form
    And the "Total vitamin D3 in the diet" field should show the required message
    And the "Added 25-OH-D3" field should show the required message
    And the "Feeding phase (e.g. starter, grower)" field should show the required message
    And I enter "55" in the Total vitamin D3 in the diet field
    And the Total vitamin D3 level in the diet field should be "55"
    And the 25-OH-D3 field should be empty
    And I enter "7890" in the Added 25-OH-D3 field
    And the 25-OH-D3 field should be "315600"
    And the Total vitamin D3 level in the diet field should be "315655"
    And I enter "77" in the Total Calcium field
    And the Total Calcium field should show the message "Total Calcium must be less than or equal to 6"
    And I enter "00" in the Total Calcium field
    And the Total Calcium field should show the message "Total Calcium must be greater than or equal to 0.25"
    And I enter "5" in the Total Calcium field
    And the Total Calcium field should not show an error message
    And I enter "123" in the Total Phosphorus field
    And the Total Phosphorus field should show the message "Total Phosphorus must be less than or equal to 6"
    And I enter "000000000000.22" in the Total Phosphorus field
    And the Total Phosphorus field should show the message "Total Phosphorus must be greater than or equal to 0.25"
    And I enter "0,29" in the Total Phosphorus field
    And the Total Phosphorus field should not show an error message
    And I enter "1231" in the Phytase inclusion field
    And I select "FYT" in the Phytase unit field
    And I select "Grower" in the Feeding phase field
    And I enter "9999" in the At what age 25-OH-D3 was included field
    And the At what age 25-OH-D3 was included field should show the message "Value must be less than or equal to 999"
    And I enter "000" in the At what age 25-OH-D3 was included field
    And the At what age 25-OH-D3 was included field should show the message "Value must be greater than or equal to 1"
    And I enter "963" in the At what age 25-OH-D3 was included field
    And the At what age 25-OH-D3 was included field should not show an error message
    And I select "Other" in the Type of diet field
    And I click Next on the register form
    And the Type of diet specification field should show the required message
    And I enter "Hakuna matata" in the Type of diet specification field
    # Register cards
    And I click Next on the register form
    And I click the Register sample button
    And the "Sample collection date" field should show the required message
    And the Verax sampling session field should show the required message
    And the "DBS sample card ID" field should show the required message
    And the "Age (in days)" field should show the required message
    And I enter "01/09/1939" in the Sample collection date field
    And the Sample collection date field should show the date range message
    And I enter the current date in the Sample collection date field
    And the Sample collection date field should be the current date
    And I choose "No" in the Verax sampling session option
    And I enter a random card number in the DBS sample card ID field
    And I enter "12" in the Age (in days) field
    And I enter the following text in the Additional notes field:
      """
      Just a scar somewhere down inside of me
      Something I cannot repair
      Even though it will always be
      I pretend it isn't there (this is how it feel)
      I'm trapped in yesterday (just a memory)
      Where the pain is all I know (this is all I know)
      And I'll never break away (can't break free)
      'Cause when I'm alone

      I'm lost in these memories
      Living behind my own illusion
      Lost all my dignity
      Living inside my own confusion
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


  Scenario: DBS Register ruminant samples
    Given Im on DBS home Page
    When I click the Register "ruminant" samples button
