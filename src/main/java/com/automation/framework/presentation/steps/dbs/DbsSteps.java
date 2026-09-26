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