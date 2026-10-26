package com.automation.framework.presentation.pages.dsm.analyticalservices;

import com.automation.framework.infrastructure.config.CredentialsProvider;
import com.automation.framework.infrastructure.config.LoginCredentials;
import com.automation.framework.presentation.actions.dsm.analyticalservices.AnalyticalServicesAction;
import com.automation.framework.presentation.data.dsm.analyticalservices.AnalyticalSampleContext;
import com.automation.framework.presentation.data.dsm.analyticalservices.CreateSampleFields;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

import java.time.Duration;
import java.util.List;

public class AnalyticalServicesPage {

    private static final String ANALYTICAL_SERVICES_URL =
            "https://d19cdp44ho0x6d.cloudfront.net/login";
    private static final String ANALYTICAL_SERVICES_HOST = "d19cdp44ho0x6d.cloudfront.net";
    private static final String LOGIN_PATH = "/login";
    private static final String MICROSOFT_LOGIN_HOST = "login.microsoftonline.com";

    // Botão "Entrar com conta DSM" da tela de login do site
    private final By dsmAccountLoginButton = By.xpath(
            "//button[contains(normalize-space(.), 'DSM')] | //a[contains(normalize-space(.), 'DSM')]");

    // Login corporativo da Microsoft (o site redireciona para ele)
    private final By microsoftEmailInput = By.name("loginfmt");
    private final By microsoftPasswordInput = By.name("passwd");
    private final By microsoftSubmitButton = By.id("idSIButton9");
    // Tela "Continuar conectado?": o botão "Não" evita guardar a sessão entre execuções
    private final By microsoftStaySignedInNoButton = By.id("idBackButton");

    // Seletor de idioma do topo ("Global/PT", "Global/EN"...)
    private final By languageTrigger = By.cssSelector(".sci-nav-bar__language-trigger");

    // Textos dos botões (a automação usa a tela em inglês)
    private static final String CREATE_SAMPLE_BUTTON = "Create sample";
    private static final String NEXT_STEP_BUTTON = "Next step";
    private static final String BACK_BUTTON = "Back";
    private static final String SAVE_SAMPLE_BUTTON = "Save sample";
    private static final String STEP_1 = "Step 1";
    private static final String NEW_SAMPLE_PATH = "/samples/new";
    private static final String SAVE_ERROR_MESSAGE = "Could not create the sample";

    private final AnalyticalServicesAction actions;
    private final WebDriver driver;

    public AnalyticalServicesPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new AnalyticalServicesAction(driver, 20);
    }

    public void navigateToAnalyticalServices() {
        driver.get(ANALYTICAL_SERVICES_URL);
        actions.sleep(3000);
    }

    public void clickDsmAccountLogin() {
        actions.click(dsmAccountLoginButton);
    }

    public void validateMicrosoftLoginPage() {
        actions.waitForVisible(microsoftEmailInput, Duration.ofSeconds(30));
        if (!driver.getCurrentUrl().contains(MICROSOFT_LOGIN_HOST)) {
            throw new AssertionError("Microsoft login page was not displayed. Current URL: "
                    + driver.getCurrentUrl());
        }
    }

    public void doAnalyticalServicesLogin() {
        LoginCredentials credentials = CredentialsProvider.analyticalServices();
        doAnalyticalServicesLogin(credentials.username(), credentials.password());
    }

    public void doAnalyticalServicesLogin(String username, String password) {
        validateMicrosoftLoginPage();
        actions.sendKeys(microsoftEmailInput, username);
        actions.click(microsoftSubmitButton);

        actions.waitForVisible(microsoftPasswordInput, Duration.ofSeconds(30));
        actions.sendKeys(microsoftPasswordInput, password);
        actions.click(microsoftSubmitButton);
        actions.sleep(3000);

        if (actions.isDisplayed(microsoftStaySignedInNoButton)) {
            actions.click(microsoftStaySignedInNoButton);
        }
        actions.sleep(5000);
    }

    public void validateHomePage() {
        for (int attempt = 0; attempt < 30; attempt++) {
            String url = driver.getCurrentUrl();
            if (url.contains(ANALYTICAL_SERVICES_HOST) && !url.contains(LOGIN_PATH)) {
                return;
            }
            actions.sleep(1000);
        }
        throw new AssertionError("Analytical Services home page was not displayed. Current URL: "
                + driver.getCurrentUrl());
    }

    // ---------------------------------------------------------------------
    // Idioma e navegação
    // ---------------------------------------------------------------------

    // languageKey: en, es ou pt (data-key das opções do seletor "Global/XX")
    public void selectLanguage(String languageKey) {
        actions.click(languageTrigger);
        actions.click(By.cssSelector("[role='option'][data-key='" + languageKey + "']"));
        actions.sleep(1500);
    }

    public void openCreateSample() {
        actions.clickByText(CREATE_SAMPLE_BUTTON);
        actions.waitForStep(STEP_1);
        AnalyticalSampleContext.start();
    }

    // ---------------------------------------------------------------------
    // Create a sample: campos (o fieldName é o name de CreateSampleFields)
    // ---------------------------------------------------------------------

    public void selectSampleOption(String fieldName, String option) {
        actions.selectFieldOption(fieldName, option);
        String selected = actions.getFieldSelectedText(fieldName);
        if (!selected.equals(option)) {
            throw new AssertionError("Field '" + fieldName + "' shows '" + selected
                    + "' instead of '" + option + "'.");
        }
        AnalyticalSampleContext.put(fieldName, option);
    }

    public void typeSampleField(String fieldName, String text) {
        actions.typeField(fieldName, text);
        AnalyticalSampleContext.put(fieldName, text);
    }

    public void setSamplingDate(String isoDate) {
        actions.setSamplingDate(CreateSampleFields.SAMPLING_DATE_CSS, isoDate);
        AnalyticalSampleContext.put("samplingDate", isoDate);
    }

    public void chooseClinicalSign(String clinicalSign) {
        actions.chooseReactAriaSearchOption(
                By.cssSelector(CreateSampleFields.CLINICAL_SIGNS_CSS), clinicalSign);
        String current = AnalyticalSampleContext.get("clinicalSigns");
        AnalyticalSampleContext.put("clinicalSigns",
                current == null ? clinicalSign : current + ", " + clinicalSign);
    }

    public void validateFieldOptions(String fieldName, List<String> expectedOptions) {
        List<String> options = actions.getFieldOptions(fieldName);
        if (!options.equals(expectedOptions)) {
            throw new AssertionError("Field '" + fieldName + "' options differ."
                    + "\nExpected: " + expectedOptions + "\nActual:   " + options);
        }
    }

    public void validateFieldDisplayed(String fieldName, boolean expectedDisplayed) {
        boolean displayed = actions.isFieldDisplayed(fieldName);
        if (displayed != expectedDisplayed) {
            throw new AssertionError("Field '" + fieldName + "' should "
                    + (expectedDisplayed ? "" : "not ") + "be displayed.");
        }
    }

    // ---------------------------------------------------------------------
    // Wizard
    // ---------------------------------------------------------------------

    public void clickNextStep() {
        String before = actions.getStepTitle();
        actions.clickWizardButton(NEXT_STEP_BUTTON);
        for (int attempt = 0; attempt < 10 && actions.getStepTitle().equals(before); attempt++) {
            actions.sleep(500);
        }
        if (actions.getStepTitle().equals(before)) {
            throw new AssertionError("The wizard did not leave '" + before
                    + "'. Check the mandatory fields.");
        }
    }

    public void clickSaveSample() {
        actions.clickWizardButton(SAVE_SAMPLE_BUTTON);
    }

    // A tela mostra esta mensagem quando o POST /api/samples falha (os dados são mantidos)
    public void validateSampleWasSaved() {
        // O botão fica com spinner enquanto o POST está em andamento: espera sair da tela ou dar erro
        for (int attempt = 0; attempt < 60; attempt++) {
            if (!driver.getCurrentUrl().endsWith(NEW_SAMPLE_PATH)
                    || actions.isTextDisplayed(SAVE_ERROR_MESSAGE)) {
                break;
            }
            actions.sleep(1000);
        }
        if (actions.isTextDisplayed(SAVE_ERROR_MESSAGE)) {
            throw new AssertionError("The sample was not saved: '" + SAVE_ERROR_MESSAGE + "'");
        }
        if (driver.getCurrentUrl().endsWith(NEW_SAMPLE_PATH)) {
            throw new AssertionError("The page stayed on " + NEW_SAMPLE_PATH
                    + " after Save sample.");
        }
    }

    public void clickBack() {
        actions.clickWizardButton(BACK_BUTTON);
    }

    public void validateStep(String stepPrefix) {
        actions.waitForStep(stepPrefix);
    }

    // ---------------------------------------------------------------------
    // Step 3: Analysis information
    // ---------------------------------------------------------------------

    public void validateAnalyteTable(List<String> expectedRows) {
        actions.waitForAnalyteTable();
        List<String> rows = actions.getAnalyteRows();
        if (!rows.equals(expectedRows)) {
            throw new AssertionError("Step 3 analyte table differs."
                    + "\nExpected: " + expectedRows + "\nActual:   " + rows);
        }
    }

    public void checkAnalyte(String analyte, String laboratory) {
        actions.waitForAnalyteTable();
        actions.checkAnalyte(analyte, laboratory);
        if (!actions.isAnalyteChecked(analyte, laboratory)) {
            throw new AssertionError("Analyte was not checked: " + laboratory + " | " + analyte);
        }
        AnalyticalSampleContext.put("Analyte: " + laboratory + " | " + analyte, "checked");
    }

    public void selectAnalyteUnit(String analyte, String laboratory, String unit) {
        actions.selectAnalyteUnit(analyte, laboratory, unit);
        AnalyticalSampleContext.put("Unit: " + laboratory + " | " + analyte, unit);
    }

    // Regra da tela: todos os analitos selecionados precisam ser do mesmo laboratório
    public void validateOtherLaboratoriesDisabled(String laboratory) {
        List<String> enabledFromOtherLabs = actions.getAnalyteRows().stream()
                .map(row -> row.split(" \\| "))
                .filter(cells -> !cells[0].equals(laboratory))
                .filter(cells -> actions.isAnalyteEnabled(cells[1], cells[0]))
                .map(cells -> cells[0] + " | " + cells[1])
                .toList();
        if (!enabledFromOtherLabs.isEmpty()) {
            throw new AssertionError("Analytes from other laboratories should be disabled: "
                    + enabledFromOtherLabs);
        }
    }
}
