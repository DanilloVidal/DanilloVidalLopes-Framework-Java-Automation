package com.automation.framework.presentation.pages.dbs;
import com.automation.framework.infrastructure.config.CredentialsProvider;
import com.automation.framework.infrastructure.config.LoginCredentials;
import com.automation.framework.infrastructure.driver.DriverFactory;
import com.automation.framework.presentation.actions.dbs.DbsAction;
import com.automation.framework.presentation.data.dbs.PoultrySampleDefaults;
import com.automation.framework.presentation.data.dbs.RegisterSampleFields;
import com.automation.framework.presentation.data.dbs.SampleContext;
import com.automation.framework.presentation.data.dbs.SwineSampleDefaults;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

public class DbsPage {

    private static final String DBS_URL = "https://dbs.test.productsupport.dsm.com/";
    private static final String FARM_CONFIRMATION_XPATH =
            "/html/body/div[1]/main/dsm-grid/div[2]/div/div[1]/dsm-radio-button-group[1]/dsm-radio-button[1]//label";
    private static final String FARM_TESTING_OPTION_XPATH =
            "/html/body/div[1]/main/dsm-grid/div[2]/div/div[1]/dsm-radio-button-group[2]/dsm-radio-button[1]//label/div[1]";
    private static final String NEXT_BUTTON_XPATH =
            "/html/body/div[1]/main/dsm-grid/div[4]/dsm-button";
    private static final String PLUS_BUTTON_XPATH =
            "/html/body/div[1]/main/dsm-grid/div[3]/div/div/div/div[2]/dsm-stepper//div/dsm-button[2]//button";
    private static final String MINUS_BUTTON_XPATH =
            "/html/body/div[1]/main/dsm-grid/div[3]/div/div/div/div[2]/dsm-stepper//div/dsm-button[1]/dsm-icon//div/svg/path";
    private static final String ORDER_CONFIRM_BUTTON_XPATH =
            "/html/body/div[1]/main/dsm-grid/dsm-modal/dsm-button[2]";

    private final DbsAction actions;
    private final WebDriver driver;
    private final By acceptCookiesButton = By.cssSelector("button.disclosureAcceptAll");
    private final By acceptCookiesFullPath = By.xpath(
            "/html/body/div[4]/div/div/div[2]//div/div/div[1]/div/div[5]/button[1]");
    private final By acceptCookiesByText = By.xpath(
            "//button[normalize-space()='Eu concordo']");
    private final By acceptCookiesContainer = By.xpath("/html/body/div[4]/div/div/div[2]//div");
    private static final String COOKIE_SETTINGS_KEY = "uc_settings";
    private static final String COOKIE_SETTINGS_VALUE = """
            {"controllerId":"2b931d23ade9b4df5c64e6e32f63f18e7d8dd75e273af45b956fd85a7531c58a",
            "id":"X8TnJNGW8qNbD0","language":"en","services":[],"version":"7.6.54"}
            """.replace("\n", "").replace("\r", "");
    private final By loginEmailInput = By.id("login-email");
    private final By loginPasswordInput = By.id("login-password");
    private final By loginSubmitButton = By.cssSelector("button[type='submit']");
    private final By activeHomeLink = By.cssSelector("a[aria-current='page'][href='/']");
    private final By homeHeading = By.xpath(
            "//*[self::h1 or self::h2 or self::h3][contains(normalize-space(), 'SciTell') and contains(normalize-space(), 'DBS Analytics')]");
    private final By orderNewKitButton = By.xpath(
            "/html/body/div[1]/main/dsm-grid/div[1]/div[1]/div[2]/div/dsm-button[1]//button");

    public DbsPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new DbsAction(driver, 10);
    }

    public void navigateToDbs() {
        DriverFactory.get().get(DBS_URL);
        actions.sleep(5000);
        acceptCookies();
    }

    public void refreshPage() {
        driver.navigate().refresh();
        actions.sleep(3000);
    }

    private void injectCookieConsent() {
        ((JavascriptExecutor) driver).executeScript(
                "window.localStorage.setItem(arguments[0], arguments[1]);",
                COOKIE_SETTINGS_KEY, COOKIE_SETTINGS_VALUE);
    }

    private void acceptCookies() {
        injectCookieConsent();
        driver.switchTo().defaultContent();
        if (acceptCookiesInCurrentContext()) {
            driver.switchTo().defaultContent();
            actions.sleep(1000);
        } else {
            for (WebElement frame : driver.findElements(By.cssSelector("iframe, frame"))) {
                try {
                    driver.switchTo().frame(frame);
                } catch (StaleElementReferenceException ignored) {
                    continue;
                }
                if (acceptCookiesInCurrentContext()) {
                    driver.switchTo().defaultContent();
                    actions.sleep(1000);
                    break;
                }
                driver.switchTo().parentFrame();
            }
        }

        driver.switchTo().defaultContent();
        forceCloseCookieOverlay();
        ((JavascriptExecutor) driver).executeScript("""
                const selectors = [
                    '.truste_overlay',
                    '.truste_cm_outerdiv',
                    '.truste_box_overlay',
                    '[id^="pop-outerdiv"]',
                    '[id^="pop-div"]',
                    '#cookieConsentDescription'
                ];
                document.querySelectorAll(selectors.join(',')).forEach((element) => {
                    element.remove();
                });
                document.body.style.removeProperty('overflow');
                document.documentElement.style.removeProperty('overflow');
                """);
        actions.sleep(1000);
    }

    private boolean acceptCookiesInCurrentContext() {
        for (By locator : List.of(acceptCookiesFullPath, acceptCookiesByText, acceptCookiesButton)) {
            for (WebElement button : driver.findElements(locator)) {
                if (button.isDisplayed() && button.isEnabled()
                        && tryCookieActions(button)) {
                    return true;
                }
            }
        }

        for (WebElement container : driver.findElements(acceptCookiesContainer)) {
            if (container.isDisplayed()) {
                container.sendKeys(Keys.ENTER);
                if (!isCookieButtonVisible()) {
                    return true;
                }
                container.sendKeys(Keys.SPACE);
                if (!isCookieButtonVisible()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryCookieActions(WebElement button) {
        try {
            button.click();
        } catch (ElementClickInterceptedException ignored) {
        }
        if (!isCookieButtonVisible()) {
            return true;
        }

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        if (!isCookieButtonVisible()) {
            return true;
        }

        button.sendKeys(Keys.ENTER);
        if (!isCookieButtonVisible()) {
            return true;
        }

        button.sendKeys(Keys.SPACE);
        if (!isCookieButtonVisible()) {
            return true;
        }
        return forceCloseCookieOverlay();
    }

    private boolean isCookieButtonVisible() {
        return driver.findElements(acceptCookiesByText).stream()
                .anyMatch(WebElement::isDisplayed);
    }

    private boolean forceCloseCookieOverlay() {
        Object closed = ((JavascriptExecutor) driver).executeScript("""
                const overlays = document.querySelectorAll(
                    '.truste_cm_outerdiv, [id^="pop-outerdiv"], [role="dialog"]');
                let closed = false;
                overlays.forEach((overlay) => {
                    if (overlay.offsetParent !== null) {
                        overlay.style.display = 'none';
                        overlay.style.pointerEvents = 'none';
                        closed = true;
                    }
                });
                document.querySelectorAll('button.disclosureAcceptAll').forEach((button) => {
                    if (button.offsetParent !== null) {
                        let parent = button.parentElement;
                        for (let index = 0; index < 6 && parent; index++) {
                            if (parent.id.startsWith('pop-outerdiv')
                                || String(parent.className).includes('outerdiv')
                                || parent.getAttribute('role') === 'dialog') {
                                parent.style.display = 'none';
                                parent.style.pointerEvents = 'none';
                                closed = true;
                                break;
                            }
                            parent = parent.parentElement;
                        }
                    }
                });
                return closed;
                """);
        return Boolean.TRUE.equals(closed);
    }

    public void doDbsLogin() {
        LoginCredentials credentials = CredentialsProvider.dbs();
        doDbsLogin(credentials.username(), credentials.password());
        actions.sleep(10000);
        acceptCookies();
        actions.sleep(10);
    }

    public void doDbsLogin(String username, String password) {
        actions.sendKeys(loginEmailInput, username);
        actions.sendKeys(loginPasswordInput, password);
        actions.jsClick(loginSubmitButton);
    }

    public void validateHomePage() {
        if (!actions.isDisplayed(activeHomeLink) && !actions.isDisplayed(homeHeading)) {
            throw new AssertionError("DBS home page was not displayed. Current URL: "
                    + driver.getCurrentUrl());
        }
    }

    public void clickOrderNewKit() {
        actions.sleep(5000);
        actions.clickDsmButton("Order new kit", false);
        actions.sleep(2000);
    }

    public void clickRegisterSamples(String animalType) {
        SampleContext.start(animalType.trim().toLowerCase());
        actions.sleep(5000);
        actions.clickDsmButton("Register " + animalType.trim().toLowerCase() + " samples", false);
        actions.sleep(2000);
    }

    // Register samples form: campos localizados pelo label exibido na tela.
    // Cada valor preenchido fica salvo no SampleContext para validações posteriores.
    public void selectRegisterOption(String fieldLabel, String option) {
        actions.selectDsmOptionByLabel(fieldLabel, option);
        SampleContext.put(fieldLabel, option);
    }

    public void typeRegisterField(String fieldLabel, String text) {
        actions.typeDsmInputByLabel(fieldLabel, text);
        SampleContext.put(fieldLabel, text);
    }

    public void chooseRegisterRadio(String groupLabel, String option) {
        actions.chooseDsmRadioByLabel(groupLabel, option);
        SampleContext.put(groupLabel, option);
    }

    // Fluxo completo da etapa "Farm information" de swine com os valores padrão,
    // para ser encapsulado em um único step no futuro
    public void fillSwineFarmInformation() {
        // Customer não existe para o usuário atual da automação
        // selectRegisterOption(RegisterSampleFields.CUSTOMER, SwineSampleDefaults.CUSTOMER);
        selectRegisterOption(RegisterSampleFields.FARM, SwineSampleDefaults.FARM);
        typeRegisterField(RegisterSampleFields.BARN_NAME, SwineSampleDefaults.BARN_NAME);
        chooseRegisterRadio(RegisterSampleFields.HOUSING, SwineSampleDefaults.HOUSING);
        selectRegisterOption(RegisterSampleFields.PRODUCTION_SYSTEM, SwineSampleDefaults.PRODUCTION_SYSTEM);
        selectRegisterOption(RegisterSampleFields.PURPOSE_OF_ANALYSIS, SwineSampleDefaults.PURPOSE_OF_ANALYSIS);
        clickRegisterNext();
        validateRequiredFieldMessage(RegisterSampleFields.END_CUSTOMER);
        selectRegisterOption(RegisterSampleFields.END_CUSTOMER, SwineSampleDefaults.END_CUSTOMER);
        clickRegisterNext();
        validateRequiredFieldMessage(RegisterSampleFields.END_CUSTOMER_NAME);
        typeRegisterField(RegisterSampleFields.END_CUSTOMER_NAME, SwineSampleDefaults.END_CUSTOMER_NAME);
        validateFieldIsNotEmpty(RegisterSampleFields.END_CUSTOMER_NAME);
    }

    // Fluxo completo da etapa "Farm information" de poultry com os valores padrão
    public void fillPoultryFarmInformation() {
        clickRegisterNext();
        validateRequiredFieldMessage(RegisterSampleFields.FARM);
        validateRequiredFieldMessage(RegisterSampleFields.PURPOSE_OF_ANALYSIS);
        validateRequiredFieldMessage(RegisterSampleFields.END_CUSTOMER);
        // Customer não existe para o usuário atual da automação
        // selectRegisterOption(RegisterSampleFields.CUSTOMER, PoultrySampleDefaults.CUSTOMER);
        selectRegisterOption(RegisterSampleFields.FARM, PoultrySampleDefaults.FARM);
        typeRandomHouseNumber();
        chooseRegisterRadio(RegisterSampleFields.HOUSING, PoultrySampleDefaults.HOUSING);
        selectRegisterOption(RegisterSampleFields.PRODUCTION_SYSTEM, PoultrySampleDefaults.PRODUCTION_SYSTEM);
        typeRegisterField(RegisterSampleFields.PRODUCTION_SYSTEM_OTHER, PoultrySampleDefaults.PRODUCTION_SYSTEM_OTHER);
        selectRegisterOption(RegisterSampleFields.PURPOSE_OF_ANALYSIS, PoultrySampleDefaults.PURPOSE_OF_ANALYSIS);
        selectRegisterOption(RegisterSampleFields.END_CUSTOMER, PoultrySampleDefaults.END_CUSTOMER);
    }

    // Fluxo completo da etapa "Animal information" de poultry com os valores padrão
    public void fillPoultryAnimalInformation() {
        clickRegisterNext();
        validateRequiredFieldMessage(RegisterSampleFields.SUB_SPECIES);
        validateRequiredFieldMessage(RegisterSampleFields.SEX);
        validateRequiredFieldMessage(RegisterSampleFields.GENETIC);
        validateRequiredFieldMessage(RegisterSampleFields.BREED_AND_STRAIN);
        selectRegisterOption(RegisterSampleFields.SUB_SPECIES, PoultrySampleDefaults.SUB_SPECIES);
        clickRegisterNext();
        validateFieldIsMarkedAsRequired(RegisterSampleFields.ANY_CHALLENGE_IN_THE_FLOCK);
        selectRegisterOption(RegisterSampleFields.ANY_CHALLENGE_IN_THE_FLOCK,
                PoultrySampleDefaults.ANY_CHALLENGE_IN_THE_FLOCK);
        typeRegisterField(RegisterSampleFields.CLINICAL_CHALLENGE_UNIT, PoultrySampleDefaults.CLINICAL_CHALLENGE_UNIT);
        chooseRegisterRadio(RegisterSampleFields.SEX, PoultrySampleDefaults.SEX);
        selectRegisterOption(RegisterSampleFields.GENETIC, PoultrySampleDefaults.GENETIC);
        typeRegisterField(RegisterSampleFields.BREED_AND_STRAIN, PoultrySampleDefaults.BREED_AND_STRAIN);
        selectRegisterOption(RegisterSampleFields.FLOCK_PERFORMANCE_AT_SAMPLING,
                PoultrySampleDefaults.FLOCK_PERFORMANCE_AT_SAMPLING);
        typeRegisterField(RegisterSampleFields.FLOCK_PERFORMANCE_AT_SAMPLE_UNIT,
                PoultrySampleDefaults.FLOCK_PERFORMANCE_AT_SAMPLE_UNIT);
    }

    // Fluxo completo da etapa "Feed information" de poultry com os valores padrão
    public void fillPoultryFeedInformation() {
        clickRegisterNext();
        validateRequiredFieldMessage(RegisterSampleFields.TOTAL_VITAMIN_D3_IN_THE_DIET);
        validateRequiredFieldMessage(RegisterSampleFields.ADDED_25_OH_D3);
        validateRequiredFieldMessage(RegisterSampleFields.FEEDING_PHASE);

        typeRegisterField(RegisterSampleFields.TOTAL_VITAMIN_D3_IN_THE_DIET,
                PoultrySampleDefaults.TOTAL_VITAMIN_D3_IN_THE_DIET);
        validateFieldValue(RegisterSampleFields.TOTAL_VITAMIN_D3_IN_DIET,
                PoultrySampleDefaults.TOTAL_VITAMIN_D3_IN_THE_DIET);
        validateFieldIsEmpty(RegisterSampleFields.VITAMIN_25_OH_D3);
        typeRegisterField(RegisterSampleFields.ADDED_25_OH_D3, PoultrySampleDefaults.ADDED_25_OH_D3);
        validateFieldValue(RegisterSampleFields.VITAMIN_25_OH_D3, PoultrySampleDefaults.EXPECTED_VITAMIN_25_OH_D3);
        validateFieldValue(RegisterSampleFields.TOTAL_VITAMIN_D3_IN_DIET,
                PoultrySampleDefaults.EXPECTED_TOTAL_VITAMIN_D3_IN_DIET);

        fillRangeField(RegisterSampleFields.TOTAL_CALCIUM,
                PoultrySampleDefaults.CALCIUM_ABOVE_RANGE, PoultrySampleDefaults.CALCIUM_MAX_MESSAGE,
                PoultrySampleDefaults.CALCIUM_BELOW_RANGE, PoultrySampleDefaults.CALCIUM_MIN_MESSAGE,
                PoultrySampleDefaults.TOTAL_CALCIUM);
        fillRangeField(RegisterSampleFields.TOTAL_PHOSPHORUS,
                PoultrySampleDefaults.PHOSPHORUS_ABOVE_RANGE, PoultrySampleDefaults.PHOSPHORUS_MAX_MESSAGE,
                PoultrySampleDefaults.PHOSPHORUS_BELOW_RANGE, PoultrySampleDefaults.PHOSPHORUS_MIN_MESSAGE,
                PoultrySampleDefaults.TOTAL_PHOSPHORUS);

        typeRegisterField(RegisterSampleFields.PHYTASE_INCLUSION, PoultrySampleDefaults.PHYTASE_INCLUSION);
        selectRegisterOption(RegisterSampleFields.PHYTASE_UNIT, PoultrySampleDefaults.PHYTASE_UNIT);
        selectRegisterOption(RegisterSampleFields.FEEDING_PHASE, PoultrySampleDefaults.FEEDING_PHASE);

        fillRangeField(RegisterSampleFields.AGE_25_OH_D3_WAS_INCLUDED,
                PoultrySampleDefaults.AGE_ABOVE_RANGE, PoultrySampleDefaults.AGE_MAX_MESSAGE,
                PoultrySampleDefaults.AGE_BELOW_RANGE, PoultrySampleDefaults.AGE_MIN_MESSAGE,
                PoultrySampleDefaults.AGE_25_OH_D3_WAS_INCLUDED);

        selectRegisterOption(RegisterSampleFields.TYPE_OF_DIET, PoultrySampleDefaults.TYPE_OF_DIET);
        clickRegisterNext();
        validateRequiredFieldMessage(RegisterSampleFields.TYPE_OF_DIET_SPECIFICATION);
        typeRegisterField(RegisterSampleFields.TYPE_OF_DIET_SPECIFICATION,
                PoultrySampleDefaults.TYPE_OF_DIET_SPECIFICATION);
    }

    // Testa acima e abaixo da faixa (cada um com sua mensagem) e termina com um valor válido
    private void fillRangeField(String fieldLabel, String aboveValue, String aboveMessage,
                                String belowValue, String belowMessage, String validValue) {
        typeRegisterField(fieldLabel, aboveValue);
        validateFieldMessage(fieldLabel, aboveMessage);
        typeRegisterField(fieldLabel, belowValue);
        validateFieldMessage(fieldLabel, belowMessage);
        typeRegisterField(fieldLabel, validValue);
        validateFieldHasNoError(fieldLabel);
    }

    // Para campos cuja mensagem de erro não segue o padrão "... is a required field"
    // (ex.: "Any challenge in the flock?" exibe só a própria pergunta), valida o estado inválido do campo
    public void validateFieldIsMarkedAsRequired(String fieldLabel) {
        String text = "";
        for (int attempt = 0; attempt < 10; attempt++) {
            text = actions.getDsmFieldTextByLabel(fieldLabel);
            if (text.startsWith("[invalid]")) {
                return;
            }
            actions.sleep(500);
        }
        throw new AssertionError("Field should be marked as required (invalid): " + fieldLabel
                + ". Field text: " + text);
    }

    // Fluxo completo da etapa "Animal information" de swine com os valores padrão
    public void fillSwineAnimalInformation() {
        selectRegisterOption(RegisterSampleFields.PHYSIOLOGICAL_STAGE, SwineSampleDefaults.PHYSIOLOGICAL_STAGE);
        validateFieldIsEmpty(RegisterSampleFields.AVERAGE_WEIGHT);
        validateFieldIsDisabled(RegisterSampleFields.SPECIFIC_PROBLEM_AREA);
        clickRegisterNext();
        // As mensagens são validadas juntas: escolher o Average weight limpa o erro do Reason
        validateRequiredFieldMessage(RegisterSampleFields.AVERAGE_WEIGHT);
        validateRequiredFieldMessage(RegisterSampleFields.REASON_FOR_ANALYSIS);
        validateRequiredFieldMessage(RegisterSampleFields.SEX);
        validateRequiredFieldMessage(RegisterSampleFields.GENETICS_SUPPLIER);
        validateRequiredFieldMessage(RegisterSampleFields.GENETICS_LINE);
        selectRegisterOption(RegisterSampleFields.AVERAGE_WEIGHT, SwineSampleDefaults.AVERAGE_WEIGHT);
        selectRegisterOption(RegisterSampleFields.REASON_FOR_ANALYSIS, SwineSampleDefaults.REASON_FOR_ANALYSIS);
        validateFieldIsEnabled(RegisterSampleFields.SPECIFIC_PROBLEM_AREA);
        validateFieldIsEmpty(RegisterSampleFields.SPECIFIC_PROBLEM_AREA);
        clickRegisterNext();
        validateRequiredFieldMessage(RegisterSampleFields.SPECIFIC_PROBLEM_AREA);
        selectRegisterOption(RegisterSampleFields.SPECIFIC_PROBLEM_AREA, SwineSampleDefaults.SPECIFIC_PROBLEM_AREA);
        chooseRegisterRadio(RegisterSampleFields.SEX, SwineSampleDefaults.SEX);
        selectRegisterOption(RegisterSampleFields.GENETICS_SUPPLIER, SwineSampleDefaults.GENETICS_SUPPLIER);
        validateFieldIsEmpty(RegisterSampleFields.GENETICS_LINE);
        selectRegisterOption(RegisterSampleFields.GENETICS_LINE, SwineSampleDefaults.GENETICS_LINE);
    }

    // Fluxo completo da etapa "Feed information" de swine com os valores padrão
    public void fillSwineFeedInformation() {
        typeRegisterField(RegisterSampleFields.VITAMIN_D3, SwineSampleDefaults.VITAMIN_D3);
        validateFieldValue(RegisterSampleFields.TOTAL_VITAMIN_D3_IN_DIET, SwineSampleDefaults.VITAMIN_D3);
        typeRegisterField(RegisterSampleFields.VITAMIN_25_OH_LEVEL, SwineSampleDefaults.VITAMIN_25_OH_LEVEL);
        validateFieldValue(RegisterSampleFields.VITAMIN_25_OH_D3, SwineSampleDefaults.EXPECTED_VITAMIN_25_OH_D3);
        validateFieldValue(RegisterSampleFields.TOTAL_VITAMIN_D3_IN_DIET,
                SwineSampleDefaults.EXPECTED_TOTAL_VITAMIN_D3_IN_DIET);
        fillRangeField(RegisterSampleFields.TOTAL_CALCIUM, SwineSampleDefaults.TOTAL_CALCIUM);
        fillRangeField(RegisterSampleFields.TOTAL_PHOSPHORUS, SwineSampleDefaults.TOTAL_PHOSPHORUS);
        typeRegisterField(RegisterSampleFields.PHYTASE, SwineSampleDefaults.PHYTASE);
        selectRegisterOption(RegisterSampleFields.PHYTASE_UNIT, SwineSampleDefaults.PHYTASE_UNIT);
    }

    // Fluxo da etapa "Register cards" de swine com os valores padrão
    public void fillSwineRegisterCards() {
        clickRegisterSample();
        validateRequiredFieldMessage(RegisterSampleFields.SAMPLE_COLLECTION_DATE);
        validateRequiredFieldMessage(RegisterSampleFields.DBS_SAMPLE_CARD_ID);
        typeCurrentDate(RegisterSampleFields.SAMPLE_COLLECTION_DATE);
        validateFieldIsCurrentDate(RegisterSampleFields.SAMPLE_COLLECTION_DATE);
        chooseRegisterRadio(RegisterSampleFields.VERAX_SAMPLING_SESSION, SwineSampleDefaults.VERAX_SAMPLING_SESSION);
        typeRandomCardId();
        typeRegisterField(RegisterSampleFields.ANIMAL_DETAILS, SwineSampleDefaults.ANIMAL_DETAILS);
        chooseRegisterRadio(RegisterSampleFields.INCREASED_MORTALITY, SwineSampleDefaults.INCREASED_MORTALITY);
        typeRegisterField(RegisterSampleFields.ADDITIONAL_NOTES, SwineSampleDefaults.ADDITIONAL_NOTES);
    }

    // Fluxo da etapa "Register cards" de poultry com os valores padrão
    public void fillPoultryRegisterCards() {
        clickRegisterSample();
        validateRequiredFieldMessage(RegisterSampleFields.SAMPLE_COLLECTION_DATE);
        validateRequiredFieldMessage(RegisterSampleFields.VERAX_SAMPLING_SESSION);
        validateRequiredFieldMessage(RegisterSampleFields.DBS_SAMPLE_CARD_ID);
        validateRequiredFieldMessage(RegisterSampleFields.AGE_IN_DAYS);
        typeRegisterField(RegisterSampleFields.SAMPLE_COLLECTION_DATE, PoultrySampleDefaults.OUT_OF_RANGE_COLLECTION_DATE);
        validateSampleCollectionDateRangeMessage();
        typeCurrentDate(RegisterSampleFields.SAMPLE_COLLECTION_DATE);
        validateFieldIsCurrentDate(RegisterSampleFields.SAMPLE_COLLECTION_DATE);
        chooseRegisterRadio(RegisterSampleFields.VERAX_SAMPLING_SESSION, PoultrySampleDefaults.VERAX_SAMPLING_SESSION);
        typeRandomCardId();
        typeRegisterField(RegisterSampleFields.AGE_IN_DAYS, PoultrySampleDefaults.AGE_IN_DAYS);
        typeRegisterField(RegisterSampleFields.ADDITIONAL_NOTES, PoultrySampleDefaults.ADDITIONAL_NOTES);
    }

    // A tela aceita datas entre hoje - 1 mês e hoje + 1 mês.
    // Ex. (hoje = 30/09/2026): "Collection date must be between Aug 30, 2026 and Oct 30, 2026 (DD/MM/YYYY)"
    public void validateSampleCollectionDateRangeMessage() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        LocalDate today = LocalDate.now();
        String expected = "Collection date must be between " + today.minusMonths(1).format(formatter)
                + " and " + today.plusMonths(1).format(formatter);
        validateFieldMessage(RegisterSampleFields.SAMPLE_COLLECTION_DATE, expected);
    }

    // Envia a sample: confere o modal, cancela, reenvia, confirma e volta para a home
    public void submitSample() {
        submitSwineSample();
    }

    // Envia a sample: confere o modal, cancela, reenvia e confirma
    public void submitSwineSample() {
        clickRegisterSample();
        validateSubmitConfirmationValues();
        cancelSubmitConfirmation();
        clickRegisterSample();
        confirmSubmitConfirmation();
        validateSamplesRegisteredMessage();
        closeSamplesRegisteredMessage();
        validateHomePage();
    }

    // Gera um card ID aleatório de 6 dígitos; se o app disser que o card já foi usado, gera outro.
    // O valor fica salvo no SampleContext (RegisterSampleFields.DBS_SAMPLE_CARD_ID) para consultas futuras.
    public String typeRandomCardId() {
        for (int attempt = 0; attempt < 5; attempt++) {
            String cardId = randomNumber(6);
            typeRegisterField(RegisterSampleFields.DBS_SAMPLE_CARD_ID, cardId);
            actions.sleep(1500);
            if (!normalizeText(actions.getDsmFieldTextByLabel(RegisterSampleFields.DBS_SAMPLE_CARD_ID))
                    .contains("already been used")) {
                // Salva o card aceito (o typeRegisterField já grava, mas deixamos explícito)
                SampleContext.put(RegisterSampleFields.DBS_SAMPLE_CARD_ID, cardId);
                System.out.println("[DBS] DBS sample card ID: " + cardId);
                return cardId;
            }
        }
        throw new AssertionError("Could not generate an unused DBS sample card ID after 5 attempts");
    }

    // Número aleatório com a quantidade de dígitos informada (sem zero à esquerda)
    public String randomNumber(int digits) {
        int min = (int) Math.pow(10, digits - 1);
        int max = (int) Math.pow(10, digits);
        return String.valueOf(ThreadLocalRandom.current().nextInt(min, max));
    }

    // Gera um House number aleatório de 6 dígitos e salva no SampleContext
    // (RegisterSampleFields.HOUSE_NUMBER) para validar na confirmação
    public String typeRandomHouseNumber() {
        String houseNumber = randomNumber(6);
        typeRegisterField(RegisterSampleFields.HOUSE_NUMBER, houseNumber);
        SampleContext.put(RegisterSampleFields.HOUSE_NUMBER, houseNumber);
        System.out.println("[DBS] House number: " + houseNumber);
        return houseNumber;
    }

    public void clickRegisterSample() {
        actions.clickDsmButton("Register sample", false);
        actions.sleep(2000);
    }

    // Confere se o modal de confirmação mostra os valores preenchidos (salvos no SampleContext)
    public void validateSubmitConfirmationValues() {
        String modalText = waitForOpenModal(RegisterSampleFields.SUBMIT_CONFIRMATION_MODAL, 10);
        System.out.println("[DBS] Sample values saved: " + SampleContext.values());
        // Campos exibidos no modal em todas as samples
        List<String> requiredFields = List.of(
                RegisterSampleFields.FARM,
                RegisterSampleFields.SAMPLE_COLLECTION_DATE,
                RegisterSampleFields.PURPOSE_OF_ANALYSIS,
                RegisterSampleFields.END_CUSTOMER,
                RegisterSampleFields.DBS_SAMPLE_CARD_ID);
        // Campos que dependem do animal (swine: Physiological stage / poultry: Sub-species)
        // ou do fluxo (End Customer name só existe quando End Customer = Yes)
        List<String> optionalFields = List.of(
                RegisterSampleFields.PHYSIOLOGICAL_STAGE,
                RegisterSampleFields.SUB_SPECIES,
                RegisterSampleFields.END_CUSTOMER_NAME);
        String normalizedModal = normalizeText(modalText);
        if (!normalizedModal.contains(normalizeText(SampleContext.animalType()))) {
            throw new AssertionError("Submit confirmation does not show the species: " + SampleContext.animalType());
        }
        for (String field : requiredFields) {
            if (SampleContext.get(field) == null) {
                throw new AssertionError("No value was saved in SampleContext for field: " + field);
            }
            validateModalShowsValue(normalizedModal, modalText, field);
        }
        for (String field : optionalFields) {
            if (SampleContext.get(field) != null) {
                validateModalShowsValue(normalizedModal, modalText, field);
            }
        }
    }

    private void validateModalShowsValue(String normalizedModal, String modalText, String field) {
        String expected = SampleContext.get(field);
        if (!normalizedModal.contains(normalizeText(expected))) {
            throw new AssertionError("Submit confirmation does not show " + field + " = '" + expected
                    + "'. Modal text: " + modalText);
        }
    }

    public void cancelSubmitConfirmation() {
        actions.clickDsmButton("Cancel", false);
        waitForModalToClose(RegisterSampleFields.SUBMIT_CONFIRMATION_MODAL);
    }

    public void confirmSubmitConfirmation() {
        waitForOpenModal(RegisterSampleFields.SUBMIT_CONFIRMATION_MODAL, 10);
        actions.clickDsmButton("Submit", false);
    }

    public void validateSamplesRegisteredMessage() {
        String modalText = waitForOpenModal(RegisterSampleFields.SAMPLES_REGISTERED_MODAL, 30);
        if (!normalizeText(modalText).contains("registered successfully")) {
            throw new AssertionError("Samples registered message was not displayed. Modal text: " + modalText);
        }
    }

    public void closeSamplesRegisteredMessage() {
        actions.clickDsmButton("Close", false);
        waitForModalToClose(RegisterSampleFields.SAMPLES_REGISTERED_MODAL);
        actions.sleep(3000);
    }

    private String waitForOpenModal(String header, int timeoutInSeconds) {
        for (int attempt = 0; attempt < timeoutInSeconds * 2; attempt++) {
            String text = actions.getOpenDsmModalText(header);
            if (text != null) {
                return text;
            }
            actions.sleep(500);
        }
        throw new AssertionError("Modal was not displayed: " + header);
    }

    private void waitForModalToClose(String header) {
        for (int attempt = 0; attempt < 20; attempt++) {
            if (actions.getOpenDsmModalText(header) == null) {
                return;
            }
            actions.sleep(500);
        }
        throw new AssertionError("Modal did not close: " + header);
    }

    public String currentDate() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(RegisterSampleFields.DATE_FORMAT));
    }

    public void typeCurrentDate(String fieldLabel) {
        typeRegisterField(fieldLabel, currentDate());
    }

    public void validateFieldIsCurrentDate(String fieldLabel) {
        validateFieldValue(fieldLabel, currentDate());
    }

    // Testa acima e abaixo da faixa permitida e termina com um valor válido
    private void fillRangeField(String fieldLabel, String validValue) {
        typeRegisterField(fieldLabel, SwineSampleDefaults.ABOVE_RANGE_VALUE);
        validateFieldMessage(fieldLabel, SwineSampleDefaults.RANGE_MESSAGE);
        typeRegisterField(fieldLabel, SwineSampleDefaults.BELOW_RANGE_VALUE);
        validateFieldMessage(fieldLabel, SwineSampleDefaults.RANGE_MESSAGE);
        typeRegisterField(fieldLabel, validValue);
        validateFieldHasNoError(fieldLabel);
    }

    public void validateFieldValue(String fieldLabel, String expected) {
        String value = "";
        for (int attempt = 0; attempt < 10; attempt++) {
            value = actions.getDsmFieldValueByLabel(fieldLabel);
            if (value.trim().equals(expected.trim())) {
                return;
            }
            actions.sleep(500);
        }
        throw new AssertionError("Field '" + fieldLabel + "' should be '" + expected
                + "' but was '" + value + "'");
    }

    public void validateFieldMessage(String fieldLabel, String expectedMessage) {
        String expected = normalizeText(expectedMessage);
        for (int attempt = 0; attempt < 10; attempt++) {
            if (normalizeText(actions.getDsmFieldTextByLabel(fieldLabel)).contains(expected)) {
                return;
            }
            actions.sleep(500);
        }
        throw new AssertionError("Message '" + expectedMessage + "' was not displayed for field: "
                + fieldLabel + ". Field text: " + actions.getDsmFieldTextByLabel(fieldLabel));
    }

    public void validateFieldHasNoError(String fieldLabel) {
        String text = "";
        for (int attempt = 0; attempt < 10; attempt++) {
            text = actions.getDsmFieldTextByLabel(fieldLabel);
            if (!text.startsWith("[invalid]")) {
                return;
            }
            actions.sleep(500);
        }
        throw new AssertionError("Field should not show an error: " + fieldLabel + ". Field text: " + text);
    }

    public void clickRegisterNext() {
        actions.clickDsmButton("Next", false);
        actions.sleep(1000);
    }

    // Aceita qualquer mensagem "... is a required field" exibida no próprio campo
    // (ex.: o campo "Average weight" exibe "Specific physiological stage is a required field")
    public void validateRequiredFieldMessage(String fieldLabel) {
        String expected = "is a required field";
        for (int attempt = 0; attempt < 10; attempt++) {
            if (normalizeText(actions.getDsmFieldTextByLabel(fieldLabel)).contains(expected)) {
                return;
            }
            actions.sleep(500);
        }
        throw new AssertionError("Required message was not displayed for field: " + fieldLabel
                + ". Field text: " + actions.getDsmFieldTextByLabel(fieldLabel));
    }

    public void validateFieldIsNotEmpty(String fieldLabel) {
        if (actions.getDsmFieldValueByLabel(fieldLabel).isBlank()) {
            throw new AssertionError("Field should not be empty: " + fieldLabel);
        }
    }

    public void validateFieldIsEmpty(String fieldLabel) {
        String value = actions.getDsmFieldValueByLabel(fieldLabel);
        if (!value.isBlank()) {
            throw new AssertionError("Field should be empty: " + fieldLabel + ". Value: " + value);
        }
    }

    public void validateFieldIsDisabled(String fieldLabel) {
        if (!actions.isDsmFieldDisabledByLabel(fieldLabel)) {
            throw new AssertionError("Field should be disabled: " + fieldLabel);
        }
    }

    public void validateFieldIsEnabled(String fieldLabel) {
        for (int attempt = 0; attempt < 10; attempt++) {
            if (!actions.isDsmFieldDisabledByLabel(fieldLabel)) {
                return;
            }
            actions.sleep(500);
        }
        throw new AssertionError("Field should be enabled: " + fieldLabel);
    }

    public void selectFarm(String farm) {
        actions.sleep(10000);
        actions.selectDsmOption("On which farm will you be testing?", farm);
    }

    public void clickFarmConfirmation() {
        actions.clickDsmByXpath(FARM_CONFIRMATION_XPATH);
    }

    public void validateFarm(String farm) {
        String selectedFarm = (String) ((JavascriptExecutor) driver).executeScript("""
                const findElements = (root, selector) => {
                    const elements = [...root.querySelectorAll(selector)];
                    for (const element of root.querySelectorAll('*')) {
                        if (element.shadowRoot) {
                            elements.push(...findElements(element.shadowRoot, selector));
                        }
                    }
                    return elements;
                };
                return findElements(document, 'dsm-select')
                    .map((element) => element.shadowRoot?.textContent || element.textContent || '')
                    .join(' ');
                """);
        if (selectedFarm == null || !normalizeText(selectedFarm).contains(normalizeText(farm))) {
            throw new AssertionError("Selected farm did not match: " + farm);
        }
    }

    public void clickFarmTestingOption() {
        actions.clickDsmByXpath(FARM_TESTING_OPTION_XPATH);
    }

    public void selectAnalysisPurpose(String purpose) {
        actions.clickDsmByXpath("//*[@id=\"root\"]/main/dsm-grid/div[2]/div/div[2]/div/dsm-select//div[2]");
        actions.selectDsmOption("Purpose of analysis", purpose, true);
    }

    public void clickNext() {
        actions.sleep(5000);
        actions.clickDsmByXpath(NEXT_BUTTON_XPATH);
        actions.sleep(2000);
    }

    public void increaseKitQuantity(int times) {
        actions.clickDsmQuantityButton(PLUS_BUTTON_XPATH, times);
    }

    public void decreaseKitQuantity(int times) {
        actions.clickDsmQuantityButton(MINUS_BUTTON_XPATH, times);
    }

    public void setKitQuantity(String quantity) {
        actions.setQuantity(quantity);
    }

    public void clickPlaceOrder() {
        actions.clickDsmButton("Place order", false);
        actions.sleep(2000);
    }

    public void validateOrderReview() {
        String modalText = (String) ((JavascriptExecutor) driver).executeScript("""
                const textOf = (node) => {
                    if (!node) return '';
                    if (node.nodeType === Node.TEXT_NODE) return node.nodeValue || '';
                    if (node.nodeType !== Node.ELEMENT_NODE
                            && node.nodeType !== Node.DOCUMENT_FRAGMENT_NODE) return '';
                    if (node.nodeType === Node.ELEMENT_NODE
                            && node.tagName.toLowerCase() === 'slot') {
                        const assigned = node.assignedNodes({flatten: true});
                        return assigned.length
                            ? assigned.map(textOf).join(' ')
                            : node.textContent || '';
                    }
                    return [...node.childNodes].map(textOf).join(' ');
                };
                const modal = [...document.querySelectorAll('dsm-modal')]
                    .find((element) => element.offsetParent !== null);
                return modal ? textOf(modal.shadowRoot || modal) : '';
                """);
        if (modalText == null || modalText.isBlank()) {
            throw new AssertionError("DBS order review text was not displayed.");
        }
    }

    public void confirmOrder() {
        actions.clickDsmModalButton(ORDER_CONFIRM_BUTTON_XPATH);
        actions.sleep(2000);
    }

    public void switchToOrderModalIframe(String xpath) {
        actions.switchToFrameByXpath(xpath);
    }

    public void switchToMainDocument() {
        actions.switchToDefaultContent();
    }

    public void validateOrderSuccess() {
        for (int attempt = 0; attempt < 20; attempt++) {
            if (isOrderSuccessPopupVisible() || hasReturnedToDbsHome()) {
                return;
            }
            actions.sleep(500);
        }
        throw new AssertionError("DBS order success message was not displayed. Current URL: "
                + driver.getCurrentUrl());
    }

    private boolean isOrderSuccessPopupVisible() {
        String pageText = (String) ((JavascriptExecutor) driver).executeScript("""
                const roots = [document];
                let text = '';
                while (roots.length) {
                    const root = roots.pop();
                    text += ' ' + (root.body?.innerText || root.textContent || '');
                    for (const element of root.querySelectorAll('*')) {
                        if (element.shadowRoot) roots.push(element.shadowRoot);
                    }
                }
                return text;
                """);
        String normalized = normalizeText(pageText);
        return normalized.contains("success") && normalized.contains("order");
    }

    private boolean hasReturnedToDbsHome() {
        return driver.getCurrentUrl().startsWith(DBS_URL)
                && actions.isDisplayed(activeHomeLink);
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.replaceAll("\\s+", " ").trim().toLowerCase();
    }
}
