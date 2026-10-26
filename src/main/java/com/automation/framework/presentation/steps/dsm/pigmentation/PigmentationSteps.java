package com.automation.framework.presentation.steps.dsm.pigmentation;

import com.automation.framework.infrastructure.driver.DriverFactory;
import com.automation.framework.presentation.pages.dsm.pigmentation.PigmentationPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class PigmentationSteps {

    private final PigmentationPage pigmentationPage;

    public PigmentationSteps() {
        this.pigmentationPage = new PigmentationPage(DriverFactory.get());
    }

    @Given("I open the Pigmentation page")
    public void iOpenThePigmentationPage() {
        pigmentationPage.navigateToPigmentation();
    }

    @Then("I should see the Pigmentation Microsoft login page")
    public void iShouldSeeThePigmentationMicrosoftLoginPage() {
        pigmentationPage.validateMicrosoftLoginPage();
    }

    @When("I do a Pigmentation login")
    public void iDoAPigmentationLogin() {
        pigmentationPage.doPigmentationLogin();
    }

    @Then("I should see the Pigmentation home page")
    public void iShouldSeeThePigmentationHomePage() {
        pigmentationPage.validateHomePage();
    }

    @When("I start the Pigmentation calculator")
    public void iStartThePigmentationCalculator() {
        pigmentationPage.startCalculator();
    }

    @Then("I should see the Pigmentation calculator")
    public void iShouldSeeThePigmentationCalculator() {
        pigmentationPage.validateCalculatorPage();
    }

    @When("I save the Pigmentation login session")
    public void iSaveThePigmentationLoginSession() {
        pigmentationPage.saveLoginSession();
    }

    // Usa a sessão salva (cookies + storage); se tiver expirado faz o login completo e salva de novo
    @Given("Im on Pigmentation home Page")
    public void imOnPigmentationHomePage() {
        pigmentationPage.doPigmentationLoginReusingSession();
        pigmentationPage.validateHomePage();
    }
}
