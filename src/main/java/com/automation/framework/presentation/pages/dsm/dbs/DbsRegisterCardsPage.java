package com.automation.framework.presentation.pages.dsm.dbs;

import com.automation.framework.presentation.data.dsm.dbs.PoultrySampleDefaults;
import com.automation.framework.presentation.data.dsm.dbs.RegisterSampleFields;
import com.automation.framework.presentation.data.dsm.dbs.RuminantSampleDefaults;
import com.automation.framework.presentation.data.dsm.dbs.SampleContext;
import com.automation.framework.presentation.data.dsm.dbs.SwineSampleDefaults;
import org.openqa.selenium.WebDriver;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

// Register samples - etapa 4: "Register cards" e o envio da sample (modais de confirmação e sucesso)
public class DbsRegisterCardsPage extends DbsRegisterFormPage {

    private final DbsPage homePage;

    public DbsRegisterCardsPage(WebDriver driver) {
        super(driver);
        this.homePage = new DbsPage(driver);
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

    // Fluxo da etapa "Register cards" de ruminant com os valores padrão
    public void fillRuminantRegisterCards() {
        clickRegisterSample();
        validateRequiredFieldMessage(RegisterSampleFields.SAMPLE_COLLECTION_DATE);
        validateRequiredFieldMessage(RegisterSampleFields.DBS_SAMPLE_CARD_ID);
        typeRegisterField(RegisterSampleFields.SAMPLE_COLLECTION_DATE, RuminantSampleDefaults.OUT_OF_RANGE_COLLECTION_DATE);
        validateSampleCollectionDateRangeMessage();
        typeCurrentDate(RegisterSampleFields.SAMPLE_COLLECTION_DATE);
        validateFieldIsCurrentDate(RegisterSampleFields.SAMPLE_COLLECTION_DATE);
        typeRandomCardId();
        typeRegisterField(RegisterSampleFields.ANIMAL_DETAILS, RuminantSampleDefaults.ANIMAL_DETAILS);
        typeRegisterField(RegisterSampleFields.ADDITIONAL_NOTES, RuminantSampleDefaults.ADDITIONAL_NOTES);
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

    // A tela aceita datas entre hoje - 1 mês e hoje + 1 mês.
    // Ex. (hoje = 30/09/2026): "Collection date must be between Aug 30, 2026 and Oct 30, 2026 (DD/MM/YYYY)"
    public void validateSampleCollectionDateRangeMessage() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy", Locale.ENGLISH);
        LocalDate today = LocalDate.now();
        String expected = "Collection date must be between " + today.minusMonths(1).format(formatter)
                + " and " + today.plusMonths(1).format(formatter);
        validateFieldMessage(RegisterSampleFields.SAMPLE_COLLECTION_DATE, expected);
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

    public void clickRegisterSample() {
        actions.clickDsmButton("Register sample", false);
        actions.sleep(2000);
    }

    // Envia a sample (swine e poultry): confere o modal, cancela, reenvia, confirma e volta para a home
    public void submitSample() {
        clickRegisterSample();
        validateSubmitConfirmationValues();
        cancelSubmitConfirmation();
        clickRegisterSample();
        confirmSubmitConfirmation();
        validateSamplesRegisteredMessage();
        closeSamplesRegisteredMessage();
        homePage.validateHomePage();
    }

    // No ruminant não existe o modal de confirmação: o Register sample já registra a sample
    public void submitRuminantSample() {
        clickRegisterSample();
        validateSamplesRegisteredMessage();
        closeSamplesRegisteredMessage();
        homePage.validateHomePage();
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
}
