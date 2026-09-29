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