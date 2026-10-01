// package e imports...
package com.automation.framework.presentation.steps.dbs;

import com.automation.framework.infrastructure.driver.DriverFactory;
import com.automation.framework.presentation.data.dbs.RegisterSampleFields;
import com.automation.framework.presentation.pages.dbs.DbsPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class DbsSteps {

    // Declara a variável para o Page Object
    private DbsPage dbsPage;

    // Construtor para inicializar os Page Objects
    public DbsSteps() {
        this.dbsPage = new DbsPage(DriverFactory.get());
    }

    @Given("I open the DBS page")
    public void iOpenTheDbsPage() {
        // Chama o método do Page Object para navegar até a página
        dbsPage.navigateToDbs();
    }

    @When("I do a dbs login")
    public void iDoLoginDbs() {
        // Chama para realizar login
        dbsPage.doDbsLogin();
    }

    @Then("I should see the DBS home page")
    public void iShouldSeeTheDbsHomePage() {
        dbsPage.validateHomePage();
    }


    @Given("Im on DBS home Page")
    public void ImOnDbsHomePage() {
        dbsPage.navigateToDbs();
        dbsPage.doDbsLogin();
        dbsPage.validateHomePage();
    }


    @When("I click the Order new kit button")
    public void iClickTheOrderNewKitButton() {
        dbsPage.clickOrderNewKit();
    }

    @When("I click the Register {string} samples button")
    public void iClickTheRegisterSamplesButton(String animalType) {
        // animalType: swine, poultry ou ruminant
        dbsPage.clickRegisterSamples(animalType);
    }

    // Register samples form: um step de select por campo (facilita colocar breakpoint)

    // Farm information
    @When("I select {string} in the Customer field")
    public void iSelectOptionCustomer(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.CUSTOMER, option);
    }

    @When("I select {string} in the Farm field")
    public void iSelectOptionFarm(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.FARM, option);
    }

    @When("I select {string} in the Production system field")
    public void iSelectOptionProductionSystem(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.PRODUCTION_SYSTEM, option);
    }

    @When("I select {string} in the Purpose of analysis field")
    public void iSelectOptionPurposeOfAnalysis(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.PURPOSE_OF_ANALYSIS, option);
    }

    @When("I select {string} in the End Customer field")
    public void iSelectOptionEndCustomer(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.END_CUSTOMER, option);
    }

    // Gera um número aleatório de 6 dígitos e salva no SampleContext (HOUSE_NUMBER)
    @When("I enter a random number in the House number field")
    public void iEnterRandomValueHouseNumber() {
        dbsPage.typeRandomHouseNumber();
    }

    @When("I choose {string} in the Housing option")
    public void iChooseOptionHousing(String option) {
        dbsPage.chooseRegisterRadio(RegisterSampleFields.HOUSING, option);
    }

    // Os parênteses precisam de escape na Cucumber Expression
    @When("I enter {string} in the Production system \\(other) field")
    public void iEnterValueProductionSystemOther(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.PRODUCTION_SYSTEM_OTHER, value);
    }

    // Versão encapsulada: preenche toda a etapa "Farm information" de poultry
    // com os valores de PoultrySampleDefaults
    @When("I fill the poultry farm information")
    public void iFillThePoultryFarmInformation() {
        dbsPage.fillPoultryFarmInformation();
    }

    // Farm information (ruminant)
    // Gera um número aleatório de 6 dígitos e salva no SampleContext (PEN_BARN_ID).
    // A "/" precisa de escape na Cucumber Expression (sem escape ela vira alternativa "Pen" ou "Barn")
    @When("I enter a random number in the Pen\\/Barn ID field")
    public void iEnterRandomValuePenBarnId() {
        dbsPage.typeRandomPenBarnId();
    }

    // Versão encapsulada: preenche toda a etapa "Farm information" de ruminant
    // com os valores de RuminantSampleDefaults
    @When("I fill the ruminant farm information")
    public void iFillTheRuminantFarmInformation() {
        dbsPage.fillRuminantFarmInformation();
    }

    // Animal information (ruminant)
    @When("I select {string} in the Breeds field")
    public void iSelectOptionBreeds(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.BREEDS, option);
    }

    @When("I select {string} in the Animal category field")
    public void iSelectOptionAnimalCategory(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.ANIMAL_CATEGORY, option);
    }

    // Sorteia um valor de 1 a 10 e salva no SampleContext (NUMBER_OF_LACTATIONS)
    @When("I select a random value in the Number of lactations field")
    public void iSelectRandomValueNumberOfLactations() {
        dbsPage.selectRandomNumberOfLactations();
    }

    @Then("the Problem area of interest field should be marked as required")
    public void theFieldShouldBeMarkedAsRequiredProblemAreaOfInterest() {
        dbsPage.validateFieldIsMarkedAsRequired(RegisterSampleFields.PROBLEM_AREA_OF_INTEREST);
    }

    @When("I select {string} in the Problem area of interest field")
    public void iSelectOptionProblemAreaOfInterest(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.PROBLEM_AREA_OF_INTEREST, option);
    }

    // A "/" precisa de escape na Cucumber Expression
    @When("I select {string} in the Clinical\\/Production challenges field")
    public void iSelectOptionClinicalProductionChallenges(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.CLINICAL_PRODUCTION_CHALLENGES, option);
    }

    // Feed information (ruminant) - Feed details é um multi select com checkbox
    @When("I select all options in the Feed details field")
    public void iSelectAllOptionsFeedDetails() {
        dbsPage.selectAllMultiSelectOptions(RegisterSampleFields.FEED_DETAILS);
    }

    @When("I unselect all options in the Feed details field")
    public void iUnselectAllOptionsFeedDetails() {
        dbsPage.unselectAllMultiSelectOptions(RegisterSampleFields.FEED_DETAILS);
    }

    // Marca e desmarca cada opção, uma por vez, tirando print de cada estado
    @When("I check and uncheck each option of the Feed details field one by one")
    public void iCheckEachOptionOneByOneFeedDetails() {
        dbsPage.checkEachMultiSelectOptionOneByOne(RegisterSampleFields.FEED_DETAILS);
    }

    @When("I enter {string} in the Active 25-OH D3 level field")
    public void iEnterValueActive25OhD3Level(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.ACTIVE_25_OH_D3_LEVEL, value);
    }

    @Then("the Vitamin D3 equivalence field should be {string}")
    public void theFieldValueShouldBeVitaminD3Equivalence(String expected) {
        dbsPage.validateFieldValue(RegisterSampleFields.VITAMIN_D3_EQUIVALENCE, expected);
    }

    @Then("the Vitamin D3 equivalence field should be empty")
    public void theFieldShouldBeEmptyVitaminD3Equivalence() {
        dbsPage.validateFieldIsEmpty(RegisterSampleFields.VITAMIN_D3_EQUIVALENCE);
    }

    @When("I select {string} in the Vitamin D3 unit field")
    public void iSelectOptionVitaminD3Unit(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.VITAMIN_D3_UNIT, option);
    }

    // Versões encapsuladas do ruminant (valores de RuminantSampleDefaults)
    @When("I fill the ruminant animal information")
    public void iFillTheRuminantAnimalInformation() {
        dbsPage.fillRuminantAnimalInformation();
    }

    @When("I fill the ruminant feed information")
    public void iFillTheRuminantFeedInformation() {
        dbsPage.fillRuminantFeedInformation();
    }

    @When("I fill the ruminant register cards")
    public void iFillTheRuminantRegisterCards() {
        dbsPage.fillRuminantRegisterCards();
    }

    // No ruminant não existe o modal de confirmação: registra direto e valida a mensagem de sucesso
    @When("I submit the ruminant sample")
    public void iSubmitTheRuminantSample() {
        dbsPage.submitRuminantSample();
    }

    // Animal information (poultry)
    @When("I select {string} in the Sub-species field")
    public void iSelectOptionSubSpecies(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.SUB_SPECIES, option);
    }

    @When("I select {string} in the Any challenge in the flock field")
    public void iSelectOptionAnyChallengeInTheFlock(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.ANY_CHALLENGE_IN_THE_FLOCK, option);
    }

    @Then("the Any challenge in the flock field should be marked as required")
    public void theFieldShouldBeMarkedAsRequiredAnyChallengeInTheFlock() {
        dbsPage.validateFieldIsMarkedAsRequired(RegisterSampleFields.ANY_CHALLENGE_IN_THE_FLOCK);
    }

    @When("I enter {string} in the Clinical challenge unit field")
    public void iEnterValueClinicalChallengeUnit(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.CLINICAL_CHALLENGE_UNIT, value);
    }

    @When("I choose {string} in the Sex option")
    public void iChooseOptionSex(String option) {
        dbsPage.chooseRegisterRadio(RegisterSampleFields.SEX, option);
    }

    @When("I select {string} in the Genetic field")
    public void iSelectOptionGenetic(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.GENETIC, option);
    }

    @When("I enter {string} in the Breed and strain field")
    public void iEnterValueBreedAndStrain(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.BREED_AND_STRAIN, value);
    }

    @When("I select {string} in the Flock performance at sampling field")
    public void iSelectOptionFlockPerformanceAtSampling(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.FLOCK_PERFORMANCE_AT_SAMPLING, option);
    }

    @When("I enter {string} in the Flock performance at sample unit field")
    public void iEnterValueFlockPerformanceAtSampleUnit(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.FLOCK_PERFORMANCE_AT_SAMPLE_UNIT, value);
    }

    // Versão encapsulada: preenche toda a etapa "Animal information" de poultry
    // com os valores de PoultrySampleDefaults
    @When("I fill the poultry animal information")
    public void iFillThePoultryAnimalInformation() {
        dbsPage.fillPoultryAnimalInformation();
    }

    // Feed information (poultry)
    @When("I enter {string} in the Total vitamin D3 in the diet field")
    public void iEnterValueTotalVitaminD3InTheDiet(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.TOTAL_VITAMIN_D3_IN_THE_DIET, value);
    }

    @When("I enter {string} in the Added 25-OH-D3 field")
    public void iEnterValueAdded25OhD3(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.ADDED_25_OH_D3, value);
    }

    @Then("the 25-OH-D3 field should be empty")
    public void theFieldShouldBeEmptyVitamin25OhD3() {
        dbsPage.validateFieldIsEmpty(RegisterSampleFields.VITAMIN_25_OH_D3);
    }

    @When("I enter {string} in the Phytase inclusion field")
    public void iEnterValuePhytaseInclusion(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.PHYTASE_INCLUSION, value);
    }

    @When("I select {string} in the Feeding phase field")
    public void iSelectOptionFeedingPhase(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.FEEDING_PHASE, option);
    }

    @When("I enter {string} in the At what age 25-OH-D3 was included field")
    public void iEnterValueAge25OhD3WasIncluded(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.AGE_25_OH_D3_WAS_INCLUDED, value);
    }

    @Then("the At what age 25-OH-D3 was included field should show the message {string}")
    public void theFieldShouldShowTheMessageAge25OhD3WasIncluded(String message) {
        dbsPage.validateFieldMessage(RegisterSampleFields.AGE_25_OH_D3_WAS_INCLUDED, message);
    }

    @Then("the At what age 25-OH-D3 was included field should not show an error message")
    public void theFieldShouldNotShowAnErrorAge25OhD3WasIncluded() {
        dbsPage.validateFieldHasNoError(RegisterSampleFields.AGE_25_OH_D3_WAS_INCLUDED);
    }

    @When("I select {string} in the Type of diet field")
    public void iSelectOptionTypeOfDiet(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.TYPE_OF_DIET, option);
    }

    @Then("the Type of diet specification field should show the required message")
    public void theFieldShouldShowTheRequiredMessageTypeOfDietSpecification() {
        dbsPage.validateRequiredFieldMessage(RegisterSampleFields.TYPE_OF_DIET_SPECIFICATION);
    }

    @When("I enter {string} in the Type of diet specification field")
    public void iEnterValueTypeOfDietSpecification(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.TYPE_OF_DIET_SPECIFICATION, value);
    }

    // Versão encapsulada: preenche toda a etapa "Feed information" de poultry
    // com os valores de PoultrySampleDefaults
    @When("I fill the poultry feed information")
    public void iFillThePoultryFeedInformation() {
        dbsPage.fillPoultryFeedInformation();
    }

    // Register cards (poultry)
    @When("I enter {string} in the Sample collection date field")
    public void iEnterValueSampleCollectionDate(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.SAMPLE_COLLECTION_DATE, value);
    }

    // A faixa é calculada a partir da data atual (hoje - 1 mês até hoje + 1 mês)
    @Then("the Sample collection date field should show the date range message")
    public void theFieldShouldShowTheDateRangeMessageSampleCollectionDate() {
        dbsPage.validateSampleCollectionDateRangeMessage();
    }

    @Then("the Verax sampling session field should show the required message")
    public void theFieldShouldShowTheRequiredMessageVeraxSamplingSession() {
        dbsPage.validateRequiredFieldMessage(RegisterSampleFields.VERAX_SAMPLING_SESSION);
    }

    // Os parênteses precisam de escape na Cucumber Expression
    @When("I enter {string} in the Age \\(in days) field")
    public void iEnterValueAgeInDays(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.AGE_IN_DAYS, value);
    }

    // Versão encapsulada: preenche toda a etapa "Register cards" de poultry
    // com os valores de PoultrySampleDefaults
    @When("I fill the poultry register cards")
    public void iFillThePoultryRegisterCards() {
        dbsPage.fillPoultryRegisterCards();
    }

    // Versão encapsulada: confere o modal, cancela, reenvia, confirma e volta para a home
    @When("I submit the poultry sample")
    public void iSubmitThePoultrySample() {
        dbsPage.submitSample();
    }

    // Animal information
    @When("I select {string} in the Physiological stage field")
    public void iSelectOptionPhysiologicalStage(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.PHYSIOLOGICAL_STAGE, option);
    }

    @When("I select {string} in the Average weight field")
    public void iSelectOptionAverageWeight(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.AVERAGE_WEIGHT, option);
    }

    @When("I select {string} in the Reason for analysis field")
    public void iSelectOptionReasonForAnalysis(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.REASON_FOR_ANALYSIS, option);
    }

    @When("I select {string} in the Specific problem area of interest field")
    public void iSelectOptionSpecificProblemArea(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.SPECIFIC_PROBLEM_AREA, option);
    }

    @When("I select {string} in the Genetics supplier field")
    public void iSelectOptionGeneticsSupplier(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.GENETICS_SUPPLIER, option);
    }

    @When("I select {string} in the Genetics line field")
    public void iSelectOptionGeneticsLine(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.GENETICS_LINE, option);
    }

    // Feed information
    @When("I enter {string} in the Vitamin D3 field")
    public void iEnterValueVitaminD3(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.VITAMIN_D3, value);
    }

    @When("I enter {string} in the Vitamin 25-OH level field")
    public void iEnterValueVitamin25OhLevel(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.VITAMIN_25_OH_LEVEL, value);
    }

    @When("I enter {string} in the Total Calcium field")
    public void iEnterValueTotalCalcium(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.TOTAL_CALCIUM, value);
    }

    @When("I enter {string} in the Total Phosphorus field")
    public void iEnterValueTotalPhosphorus(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.TOTAL_PHOSPHORUS, value);
    }

    @When("I enter {string} in the Phytase field")
    public void iEnterValuePhytase(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.PHYTASE, value);
    }

    @When("I select {string} in the Phytase unit field")
    public void iSelectOptionPhytaseUnit(String option) {
        dbsPage.selectRegisterOption(RegisterSampleFields.PHYTASE_UNIT, option);
    }

    @Then("the 25-OH-D3 field should be {string}")
    public void theFieldValueShouldBeVitamin25OhD3(String expected) {
        dbsPage.validateFieldValue(RegisterSampleFields.VITAMIN_25_OH_D3, expected);
    }

    @Then("the Total vitamin D3 level in the diet field should be {string}")
    public void theFieldValueShouldBeTotalVitaminD3InDiet(String expected) {
        dbsPage.validateFieldValue(RegisterSampleFields.TOTAL_VITAMIN_D3_IN_DIET, expected);
    }

    @Then("the Total Calcium field should show the message {string}")
    public void theFieldShouldShowTheMessageTotalCalcium(String message) {
        dbsPage.validateFieldMessage(RegisterSampleFields.TOTAL_CALCIUM, message);
    }

    @Then("the Total Calcium field should not show an error message")
    public void theFieldShouldNotShowAnErrorTotalCalcium() {
        dbsPage.validateFieldHasNoError(RegisterSampleFields.TOTAL_CALCIUM);
    }

    @Then("the Total Phosphorus field should show the message {string}")
    public void theFieldShouldShowTheMessageTotalPhosphorus(String message) {
        dbsPage.validateFieldMessage(RegisterSampleFields.TOTAL_PHOSPHORUS, message);
    }

    @Then("the Total Phosphorus field should not show an error message")
    public void theFieldShouldNotShowAnErrorTotalPhosphorus() {
        dbsPage.validateFieldHasNoError(RegisterSampleFields.TOTAL_PHOSPHORUS);
    }

    // Register cards
    @When("I enter the current date in the Sample collection date field")
    public void iEnterTheCurrentDateSampleCollectionDate() {
        dbsPage.typeCurrentDate(RegisterSampleFields.SAMPLE_COLLECTION_DATE);
    }

    @Then("the Sample collection date field should be the current date")
    public void theFieldShouldBeTheCurrentDateSampleCollectionDate() {
        dbsPage.validateFieldIsCurrentDate(RegisterSampleFields.SAMPLE_COLLECTION_DATE);
    }

    @When("I choose {string} in the Verax sampling session option")
    public void iChooseOptionVeraxSamplingSession(String option) {
        dbsPage.chooseRegisterRadio(RegisterSampleFields.VERAX_SAMPLING_SESSION, option);
    }

    // Gera um número aleatório de 6 dígitos e salva no SampleContext (DBS_SAMPLE_CARD_ID)
    @When("I enter a random card number in the DBS sample card ID field")
    public void iEnterRandomValueDbsSampleCardId() {
        dbsPage.typeRandomCardId();
    }

    @When("I enter {string} in the Animal details field")
    public void iEnterValueAnimalDetails(String value) {
        dbsPage.typeRegisterField(RegisterSampleFields.ANIMAL_DETAILS, value);
    }

    @When("I choose {string} in the increased mortality option")
    public void iChooseOptionIncreasedMortality(String option) {
        dbsPage.chooseRegisterRadio(RegisterSampleFields.INCREASED_MORTALITY, option);
    }

    @When("I enter the following text in the Additional notes field:")
    public void iEnterTextAdditionalNotes(String text) {
        dbsPage.typeRegisterField(RegisterSampleFields.ADDITIONAL_NOTES, text);
    }

    @When("I click the Register sample button")
    public void iClickTheRegisterSampleButton() {
        dbsPage.clickRegisterSample();
    }

    @Then("the submit confirmation should show the sample values")
    public void theSubmitConfirmationShouldShowTheSampleValues() {
        dbsPage.validateSubmitConfirmationValues();
    }

    @When("I click Cancel on the submit confirmation")
    public void iClickCancelOnTheSubmitConfirmation() {
        dbsPage.cancelSubmitConfirmation();
    }

    @When("I click Submit on the submit confirmation")
    public void iClickSubmitOnTheSubmitConfirmation() {
        dbsPage.confirmSubmitConfirmation();
    }

    @Then("the Samples registered message should be displayed")
    public void theSamplesRegisteredMessageShouldBeDisplayed() {
        dbsPage.validateSamplesRegisteredMessage();
    }

    @When("I click Close on the Samples registered message")
    public void iClickCloseOnTheSamplesRegisteredMessage() {
        dbsPage.closeSamplesRegisteredMessage();
    }

    // Versão encapsulada: preenche toda a etapa "Register cards" de swine
    // com os valores de SwineSampleDefaults
    @When("I fill the swine register cards")
    public void iFillTheSwineRegisterCards() {
        dbsPage.fillSwineRegisterCards();
    }

    // Versão encapsulada: confere o modal, cancela, reenvia, confirma e volta para a home
    @When("I submit the swine sample")
    public void iSubmitTheSwineSample() {
        dbsPage.submitSwineSample();
    }

    // Versão encapsulada: preenche toda a etapa "Feed information" de swine
    // com os valores de SwineSampleDefaults
    @When("I fill the swine feed information")
    public void iFillTheSwineFeedInformation() {
        dbsPage.fillSwineFeedInformation();
    }

    @When("I enter {string} in the {string} field")
    public void iEnterTextInTheField(String text, String fieldLabel) {
        dbsPage.typeRegisterField(fieldLabel, text);
    }

    @When("I choose {string} in the {string} option")
    public void iChooseRadioInTheOption(String option, String groupLabel) {
        dbsPage.chooseRegisterRadio(groupLabel, option);
    }

    @When("I click Next on the register form")
    public void iClickNextOnTheRegisterForm() {
        dbsPage.clickRegisterNext();
    }

    @Then("the {string} field should show the required message")
    public void theFieldShouldShowTheRequiredMessage(String fieldLabel) {
        dbsPage.validateRequiredFieldMessage(fieldLabel);
    }

    @Then("the {string} field should not be empty")
    public void theFieldShouldNotBeEmpty(String fieldLabel) {
        dbsPage.validateFieldIsNotEmpty(fieldLabel);
    }

    // Versão encapsulada: preenche toda a etapa "Farm information" de swine
    // com os valores de SwineSampleDefaults
    @When("I fill the swine farm information")
    public void iFillTheSwineFarmInformation() {
        dbsPage.fillSwineFarmInformation();
    }

    @Then("the {string} field should be empty")
    public void theFieldShouldBeEmpty(String fieldLabel) {
        dbsPage.validateFieldIsEmpty(fieldLabel);
    }

    @Then("the {string} field should be disabled")
    public void theFieldShouldBeDisabled(String fieldLabel) {
        dbsPage.validateFieldIsDisabled(fieldLabel);
    }

    @Then("the {string} field should be enabled")
    public void theFieldShouldBeEnabled(String fieldLabel) {
        dbsPage.validateFieldIsEnabled(fieldLabel);
    }

    // Versão encapsulada: preenche toda a etapa "Animal information" de swine
    // com os valores de SwineSampleDefaults
    @When("I fill the swine animal information")
    public void iFillTheSwineAnimalInformation() {
        dbsPage.fillSwineAnimalInformation();
    }

    @When("I select the farm {string}")
    public void iSelectTheFarm(String farmName) {
        dbsPage.selectFarm(farmName);
    }

    @When("I click Yes for the farm")
    public void iClickYesForTheFarm() {
        dbsPage.clickFarmConfirmation();
    }

    @Then("the selected farm should be {string}")
    public void theSelectedFarmShouldBe(String farmName) {
        dbsPage.validateFarm(farmName);
    }

    @When("I select the farm testing option")
    public void iSelectTheFarmTestingOption() {
        dbsPage.clickFarmTestingOption();
    }

    @When("I select the analysis purpose {string}")
    public void iSelectTheAnalysisPurpose(String purpose) {

        dbsPage.selectAnalysisPurpose(purpose);
    }

    @When("I click Next")
    public void iClickNext() {
        dbsPage.clickNext();
    }

    @When("I click the plus button {int} times")
    public void iClickThePlusButtonTimes(int times) {
              dbsPage.increaseKitQuantity(times);

    }

    @When("I click the minus button {int} times")
    public void iClickTheMinusButtonTimes(int times) {
        dbsPage.decreaseKitQuantity(times);
    }

    @When("I enter the kit quantity {string}")
    public void iEnterTheKitQuantity(String quantity) {
        dbsPage.setKitQuantity(quantity);
    }

    @When("I click Place order")
    public void iClickPlaceOrder() {
        dbsPage.clickPlaceOrder();
    }

    @Then("the order review text should be displayed")
    public void theOrderReviewTextShouldBeDisplayed() {
        dbsPage.validateOrderReview();
    }

    @When("I confirm the order")
    public void iConfirmTheOrder() {
        dbsPage.confirmOrder();
    }

    @Then("I should see the order success message")
    public void iShouldSeeTheOrderSuccessMessage() {
        dbsPage.validateOrderSuccess();
    }

}