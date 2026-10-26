package com.automation.framework.presentation.steps.dsm.analyticalservices;

import com.automation.framework.infrastructure.driver.DriverFactory;
import com.automation.framework.presentation.data.dsm.analyticalservices.CreateSampleFields;
import com.automation.framework.presentation.pages.dsm.analyticalservices.AnalyticalServicesPage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.util.Arrays;
import java.util.List;

public class AnalyticalServicesSteps {

    private final AnalyticalServicesPage analyticalServicesPage;

    public AnalyticalServicesSteps() {
        this.analyticalServicesPage = new AnalyticalServicesPage(DriverFactory.get());
    }

    @Given("I open the Analytical Services page")
    public void iOpenTheAnalyticalServicesPage() {
        analyticalServicesPage.navigateToAnalyticalServices();
    }

    @When("I click to sign in with a DSM account")
    public void iClickToSignInWithADsmAccount() {
        analyticalServicesPage.clickDsmAccountLogin();
    }

    @Then("I should see the Microsoft login page")
    public void iShouldSeeTheMicrosoftLoginPage() {
        analyticalServicesPage.validateMicrosoftLoginPage();
    }

    @When("I do an Analytical Services login")
    public void iDoAnAnalyticalServicesLogin() {
        analyticalServicesPage.doAnalyticalServicesLogin();
    }

    @Then("I should see the Analytical Services home page")
    public void iShouldSeeTheAnalyticalServicesHomePage() {
        analyticalServicesPage.validateHomePage();
    }

    @Given("Im on Analytical Services home Page")
    public void imOnAnalyticalServicesHomePage() {
        analyticalServicesPage.navigateToAnalyticalServices();
        analyticalServicesPage.clickDsmAccountLogin();
        analyticalServicesPage.doAnalyticalServicesLogin();
        analyticalServicesPage.validateHomePage();
    }

    // languageKey: en, es ou pt
    @When("I change the Analytical Services language to {string}")
    public void iChangeTheAnalyticalServicesLanguage(String languageKey) {
        analyticalServicesPage.selectLanguage(languageKey);
    }

    @When("I click the Create sample button")
    public void iClickTheCreateSampleButton() {
        analyticalServicesPage.openCreateSample();
    }

    // Create a sample: um step por campo (facilita colocar breakpoint)

    // Step 1: General sample information
    @When("I select {string} in the dsm-firmenich unit field")
    public void iSelectOptionDsmFirmenichUnit(String option) {
        analyticalServicesPage.selectSampleOption(CreateSampleFields.DSM_FIRMENICH_UNIT, option);
    }

    @When("I select {string} in the sample Customer field")
    public void iSelectOptionCustomer(String option) {
        analyticalServicesPage.selectSampleOption(CreateSampleFields.CUSTOMER, option);
    }

    @When("I select {string} in the Analysis type field")
    public void iSelectOptionAnalysisType(String option) {
        analyticalServicesPage.selectSampleOption(CreateSampleFields.ANALYSIS_TYPE, option);
    }

    @When("I select {string} in the Species field")
    public void iSelectOptionSpecies(String option) {
        analyticalServicesPage.selectSampleOption(CreateSampleFields.SPECIES, option);
    }

    @When("I select {string} in the sample Sub-species field")
    public void iSelectOptionSubSpecies(String option) {
        analyticalServicesPage.selectSampleOption(CreateSampleFields.SUB_SPECIES, option);
    }

    @When("I type {string} in the Sub-species - other field")
    public void iTypeSubSpeciesOther(String text) {
        analyticalServicesPage.typeSampleField(CreateSampleFields.SUB_SPECIES_OTHER, text);
    }

    @When("I select {string} in the Phase field")
    public void iSelectOptionPhase(String option) {
        analyticalServicesPage.selectSampleOption(CreateSampleFields.PHASE, option);
    }

    @When("I select {string} in the Sample type field")
    public void iSelectOptionSampleType(String option) {
        analyticalServicesPage.selectSampleOption(CreateSampleFields.SAMPLE_TYPE, option);
    }

    @When("I type {string} in the Sample type - other field")
    public void iTypeSampleTypeOther(String text) {
        analyticalServicesPage.typeSampleField(CreateSampleFields.SAMPLE_TYPE_OTHER, text);
    }

    @When("I type {string} in the Sample name field")
    public void iTypeSampleName(String text) {
        analyticalServicesPage.typeSampleField(CreateSampleFields.SAMPLE_NAME, text);
    }

    @When("I select {string} in the sample Purpose of analysis field")
    public void iSelectOptionPurposeOfAnalysis(String option) {
        analyticalServicesPage.selectSampleOption(CreateSampleFields.PURPOSE_OF_ANALYSIS, option);
    }

    @When("I select {string} in the End customer field")
    public void iSelectOptionEndCustomer(String option) {
        analyticalServicesPage.selectSampleOption(CreateSampleFields.END_CUSTOMER, option);
    }

    @When("I type {string} in the End customer name field")
    public void iTypeEndCustomerName(String text) {
        analyticalServicesPage.typeSampleField(CreateSampleFields.END_CUSTOMER_NAME, text);
    }

    // Step 2: Detailed sample information
    @When("I select {string} in the Harvest year field")
    public void iSelectOptionHarvestYear(String option) {
        analyticalServicesPage.selectSampleOption(CreateSampleFields.HARVEST_YEAR, option);
    }

    @When("I select {string} in the Country of origin field")
    public void iSelectOptionCountryOfOrigin(String option) {
        analyticalServicesPage.selectSampleOption(CreateSampleFields.COUNTRY_OF_ORIGIN, option);
    }

    @When("I select {string} in the State-Province field")
    public void iSelectOptionStateProvince(String option) {
        analyticalServicesPage.selectSampleOption(CreateSampleFields.STATE_PROVINCE, option);
    }

    @When("I type {string} in the Post code of origin field")
    public void iTypePostCodeOfOrigin(String text) {
        analyticalServicesPage.typeSampleField(CreateSampleFields.POST_CODE_OF_ORIGIN, text);
    }

    @When("I select {string} in the Harvest month field")
    public void iSelectOptionHarvestMonth(String option) {
        analyticalServicesPage.selectSampleOption(CreateSampleFields.HARVEST_MONTH, option);
    }

    @When("I type {string} in the Sample number field")
    public void iTypeSampleNumber(String text) {
        analyticalServicesPage.typeSampleField(CreateSampleFields.SAMPLE_NUMBER, text);
    }

    // Data no formato yyyy-mm-dd (a tela não aceita data futura)
    @When("I set {string} in the Sampling date field")
    public void iSetSamplingDate(String isoDate) {
        analyticalServicesPage.setSamplingDate(isoDate);
    }

    @When("I select {string} in the Is there clinical signs field")
    public void iSelectOptionIsThereClinicalSigns(String option) {
        analyticalServicesPage.selectSampleOption(
                CreateSampleFields.CLINICAL_SIGNS_QUESTION, option);
    }

    @When("I choose {string} in the Clinical signs field")
    public void iChooseClinicalSign(String clinicalSign) {
        analyticalServicesPage.chooseClinicalSign(clinicalSign);
    }

    @When("I type {string} in the Clinical signs - other field")
    public void iTypeClinicalSignsOther(String text) {
        analyticalServicesPage.typeSampleField(CreateSampleFields.CLINICAL_SIGNS_OTHER, text);
    }

    // Wizard
    @When("I click Next step")
    public void iClickNextStep() {
        analyticalServicesPage.clickNextStep();
    }

    @When("I click Save sample")
    public void iClickSaveSample() {
        analyticalServicesPage.clickSaveSample();
    }

    @Then("the sample should be saved")
    public void theSampleShouldBeSaved() {
        analyticalServicesPage.validateSampleWasSaved();
    }

    @When("I click Back")
    public void iClickBack() {
        analyticalServicesPage.clickBack();
    }

    @Then("I should be on {string}")
    public void iShouldBeOnStep(String stepPrefix) {
        analyticalServicesPage.validateStep(stepPrefix);
    }

    // Validações de opções e campos condicionais.
    // As opções esperadas vêm separadas por ";" (ex.: "Carp;Catfish;Other")
    @Then("the {string} field should have the options {string}")
    public void theFieldShouldHaveTheOptions(String fieldName, String options) {
        analyticalServicesPage.validateFieldOptions(fieldName, splitList(options));
    }

    @Then("the {string} field should be displayed")
    public void theFieldShouldBeDisplayed(String fieldName) {
        analyticalServicesPage.validateFieldDisplayed(fieldName, true);
    }

    @Then("the {string} field should not be displayed")
    public void theFieldShouldNotBeDisplayed(String fieldName) {
        analyticalServicesPage.validateFieldDisplayed(fieldName, false);
    }

    // Step 3: Analysis information
    @When("I check the analyte {string} from the laboratory {string}")
    public void iCheckTheAnalyte(String analyte, String laboratory) {
        analyticalServicesPage.checkAnalyte(analyte, laboratory);
    }

    @When("I select the unit {string} for the analyte {string} from the laboratory {string}")
    public void iSelectTheAnalyteUnit(String unit, String analyte, String laboratory) {
        analyticalServicesPage.selectAnalyteUnit(analyte, laboratory, unit);
    }

    @Then("only analytes from the laboratory {string} should be selectable")
    public void onlyAnalytesFromTheLaboratoryShouldBeSelectable(String laboratory) {
        analyticalServicesPage.validateOtherLaboratoriesDisabled(laboratory);
    }

    // Linhas no formato "Laboratório | Analito | Unidade", separadas por ";"
    @Then("the analyte table should be {string}")
    public void theAnalyteTableShouldBe(String rows) {
        analyticalServicesPage.validateAnalyteTable(splitList(rows));
    }

    private static List<String> splitList(String values) {
        return Arrays.stream(values.split(";")).map(v -> v.trim()).filter(v -> !v.isEmpty())
                .toList();
    }
}
