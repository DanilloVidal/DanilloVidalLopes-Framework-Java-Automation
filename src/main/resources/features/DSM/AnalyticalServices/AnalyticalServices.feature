Feature: Analytical Services

  @AnalyticalServices
  Scenario: Analytical Services Login Page
    Given I open the Analytical Services page
    When I click to sign in with a DSM account
    Then I should see the Microsoft login page
    When I do an Analytical Services login
    Then I should see the Analytical Services home page

  @AnalyticalServices @Mycotoxin
  Scenario: Create a Mycotoxin sample - Poultry Broiler happy path
    Given Im on Analytical Services home Page
    When I change the Analytical Services language to "en"
    And I click the Create sample button
    Then I should be on "Step 1"

    # Step 1: General sample information
    When I select "LATAM - Brazil" in the dsm-firmenich unit field
    And I select "ABATEDOURO COROAVES LTDA. - VBR0000117 - 22/1210/02/VBR0000117" in the sample Customer field
    And I select "Mycotoxin" in the Analysis type field
    And I select "Poultry" in the Species field
    Then the "subSpeciesId" field should have the options "Breeder Pullet;Broiler;Broiler breeders;Duck;Layer;Layer breeders;Layer Pullet;Other;Turkey;Turkey Breeder;Turkey Breeder Pullet"
    When I select "Broiler" in the sample Sub-species field
    Then the "phaseId" field should have the options "All phases;Finisher;Grower;Pre-starter;Starter;Withdraw"
    When I select "Grower" in the Phase field
    And I select "Feed - Poultry" in the Sample type field
    And I type "QA AUTOMATION Mycotoxin Broiler" in the Sample name field
    And I select "Customer Service" in the sample Purpose of analysis field
    Then the "endCustomerName" field should not be displayed
    When I select "Yes" in the End customer field
    Then the "endCustomerName" field should be displayed
    When I type "QA End Customer" in the End customer name field
    And I click Next step
    Then I should be on "Step 2"

    # Step 2: Detailed sample information
    When I select "2026" in the Harvest year field
    And I select "Brazil" in the Country of origin field
    And I select "São Paulo" in the State-Province field
    And I type "01000-000" in the Post code of origin field
    And I select "September" in the Harvest month field
    And I type "LOT-QA-001" in the Sample number field
    And I set "2026-10-01" in the Sampling date field
    And I select "Yes" in the Is there clinical signs field
    And I choose "Lameness" in the Clinical signs field
    And I choose "Other" in the Clinical signs field
    And I type "QA other sign" in the Clinical signs - other field
    And I click Next step
    Then I should be on "Step 3"

    # Step 3: Analysis information
    When I check the analyte "Deoxynivalenol" from the laboratory "dsm-firmenich Brazil CQ2"
    Then only analytes from the laboratory "dsm-firmenich Brazil CQ2" should be selectable
    When I check the analyte "Total aflatoxins" from the laboratory "dsm-firmenich Brazil CQ2"

    # PAROU AQUI: o botão "Save sample" ainda falha no ambiente (2026-10-06).
    # O POST /api/samples responde 500 {"errorCode":"SERVICE_CATALOGUE_ERROR",
    # "message":"service-catalogue rejected the request"} e a tela mostra
    # "Could not create the sample. Your answers were kept — try again."
    # Descomentar os dois steps abaixo quando o backend for corrigido.
    And I click Save sample
    Then the sample should be saved
