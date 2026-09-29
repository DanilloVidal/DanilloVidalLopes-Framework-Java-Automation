// package e imports...
package com.automation.framework.presentation.steps.dbs;

import com.automation.framework.infrastructure.driver.DriverFactory;
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

    // Register samples form: steps genéricos, o campo é informado pelo label da tela
    @When("I select {string} in the {string} field")
    public void iSelectOptionInTheField(String option, String fieldLabel) {
        dbsPage.selectRegisterOption(fieldLabel, option);
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