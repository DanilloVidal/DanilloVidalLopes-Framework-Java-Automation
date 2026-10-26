package com.automation.framework.presentation.pages.dsm.dbs;

import com.automation.framework.presentation.actions.dsm.dbs.DbsAction;
import com.automation.framework.presentation.data.dsm.dbs.SampleContext;
import org.openqa.selenium.WebDriver;
import java.util.concurrent.ThreadLocalRandom;

// Base das etapas do formulário "Register samples" (Farm, Animal, Feed e Register cards).
// Reúne o que é comum a todas as etapas: preencher campos pelo label, botão Next e validações de campo.
public class DbsRegisterFormPage {

    protected final WebDriver driver;
    protected final DbsAction actions;

    public DbsRegisterFormPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new DbsAction(driver, 10);
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

    // Número aleatório com a quantidade de dígitos informada (sem zero à esquerda)
    public String randomNumber(int digits) {
        int min = (int) Math.pow(10, digits - 1);
        int max = (int) Math.pow(10, digits);
        return String.valueOf(ThreadLocalRandom.current().nextInt(min, max));
    }

    protected String normalizeText(String text) {
        return text == null ? "" : text.replaceAll("\\s+", " ").trim().toLowerCase();
    }
}
