// package e imports...
package com.automation.framework.presentation.steps.exemple;

import com.automation.framework.infrastructure.driver.DriverFactory;
import com.automation.framework.presentation.pages.exemple.GooglePage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static java.lang.Thread.sleep;

public class GoogleSteps {

    // Declara a variável para o Page Object
    private GooglePage googlePage;

    // Construtor para inicializar os Page Objects
    public GoogleSteps() {
        this.googlePage = new GooglePage(DriverFactory.get());
    }

    @Given("I open the Google page")
    public void iOpenTheGooglePage() {
        // Chama o método do Page Object para navegar até a página
        googlePage.navigateToGoogle();
    }

    @When("I type {string} in the search bar")
    public void iTypeInTheSearchBar(String text) {
        // Chama o método de busca do Page Object
        googlePage.search(text);
    }

    @Then("I should see the results page for {string}")
    public void iShouldSeeTheResultsPageFor(String text) throws InterruptedException {
        // Aqui viria a lógica para validar se a página de resultados foi exibida
        // Ex: Assert.assertTrue(googlePage.isResultPageDisplayedFor(text));
        sleep(2000); // Apenas para simular uma espera, não recomendado em testes reais
        System.out.println("Passo de validação para: " + text);
    }
}