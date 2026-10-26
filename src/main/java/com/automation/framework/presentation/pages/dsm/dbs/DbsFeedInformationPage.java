package com.automation.framework.presentation.pages.dsm.dbs;

import com.automation.framework.presentation.data.dsm.dbs.PoultrySampleDefaults;
import com.automation.framework.presentation.data.dsm.dbs.RegisterSampleFields;
import com.automation.framework.presentation.data.dsm.dbs.RuminantSampleDefaults;
import com.automation.framework.presentation.data.dsm.dbs.SampleContext;
import com.automation.framework.presentation.data.dsm.dbs.SwineSampleDefaults;
import org.openqa.selenium.WebDriver;
import java.util.List;

// Register samples - etapa 3: "Feed information"
public class DbsFeedInformationPage extends DbsRegisterFormPage {

    public DbsFeedInformationPage(WebDriver driver) {
        super(driver);
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

    // Testa acima e abaixo da faixa permitida e termina com um valor válido
    private void fillRangeField(String fieldLabel, String validValue) {
        typeRegisterField(fieldLabel, SwineSampleDefaults.ABOVE_RANGE_VALUE);
        validateFieldMessage(fieldLabel, SwineSampleDefaults.RANGE_MESSAGE);
        typeRegisterField(fieldLabel, SwineSampleDefaults.BELOW_RANGE_VALUE);
        validateFieldMessage(fieldLabel, SwineSampleDefaults.RANGE_MESSAGE);
        typeRegisterField(fieldLabel, validValue);
        validateFieldHasNoError(fieldLabel);
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

    // Fluxo completo da etapa "Feed information" de ruminant com os valores padrão
    public void fillRuminantFeedInformation() {
        selectAllMultiSelectOptions(RegisterSampleFields.FEED_DETAILS);
        unselectAllMultiSelectOptions(RegisterSampleFields.FEED_DETAILS);
        checkEachMultiSelectOptionOneByOne(RegisterSampleFields.FEED_DETAILS);
        selectAllMultiSelectOptions(RegisterSampleFields.FEED_DETAILS);
        clickRegisterNext();
        validateRequiredFieldMessage(RegisterSampleFields.VITAMIN_D3);
        validateRequiredFieldMessage(RegisterSampleFields.ACTIVE_25_OH_D3_LEVEL);
        typeRegisterField(RegisterSampleFields.VITAMIN_D3, RuminantSampleDefaults.VITAMIN_D3);
        validateFieldValue(RegisterSampleFields.TOTAL_VITAMIN_D3_IN_DIET, RuminantSampleDefaults.VITAMIN_D3);
        validateFieldIsEmpty(RegisterSampleFields.VITAMIN_D3_EQUIVALENCE);
        typeRegisterField(RegisterSampleFields.ACTIVE_25_OH_D3_LEVEL, RuminantSampleDefaults.ACTIVE_25_OH_D3_LEVEL);
        validateFieldValue(RegisterSampleFields.VITAMIN_D3_EQUIVALENCE,
                RuminantSampleDefaults.EXPECTED_VITAMIN_D3_EQUIVALENCE);
        validateFieldValue(RegisterSampleFields.TOTAL_VITAMIN_D3_IN_DIET,
                RuminantSampleDefaults.EXPECTED_TOTAL_VITAMIN_D3_IN_DIET);
        selectRegisterOption(RegisterSampleFields.VITAMIN_D3_UNIT, RuminantSampleDefaults.VITAMIN_D3_UNIT);
        typeRegisterField(RegisterSampleFields.TOTAL_CALCIUM, RuminantSampleDefaults.TOTAL_CALCIUM);
        validateFieldHasNoError(RegisterSampleFields.TOTAL_CALCIUM);
        typeRegisterField(RegisterSampleFields.TOTAL_PHOSPHORUS, RuminantSampleDefaults.TOTAL_PHOSPHORUS);
        validateFieldHasNoError(RegisterSampleFields.TOTAL_PHOSPHORUS);
    }

    // ---------------------------------------------------------------------
    // Feed details (multi select com checkbox)
    // Cada ação abre a lista, clica e fecha clicando fora, como um usuário faria
    // ---------------------------------------------------------------------

    // "Select all" funciona como liga/desliga: só clica se ainda houver opção desmarcada
    public void selectAllMultiSelectOptions(String fieldLabel) {
        if (!allMultiSelectOptionsAre(fieldLabel, true)) {
            actions.clickMultiSelectOption(fieldLabel, "Select all");
        }
        if (!allMultiSelectOptionsAre(fieldLabel, true)) {
            // Com seleção parcial o primeiro clique pode ter limpado tudo: clica de novo para marcar todas
            actions.clickMultiSelectOption(fieldLabel, "Select all");
        }
        validateAllMultiSelectOptions(fieldLabel, true);
        actions.closeMultiSelect(fieldLabel);
        SampleContext.put(fieldLabel, actions.getMultiSelectSelectedText(fieldLabel));
        logMultiSelectState(fieldLabel, "Select all");
    }

    // Só clica se houver alguma opção marcada; com seleção parcial, marca todas e depois desmarca
    public void unselectAllMultiSelectOptions(String fieldLabel) {
        if (!allMultiSelectOptionsAre(fieldLabel, false)) {
            if (!allMultiSelectOptionsAre(fieldLabel, true)) {
                actions.clickMultiSelectOption(fieldLabel, "Select all");
            }
            if (!allMultiSelectOptionsAre(fieldLabel, false)) {
                actions.clickMultiSelectOption(fieldLabel, "Select all");
            }
        }
        validateAllMultiSelectOptions(fieldLabel, false);
        actions.closeMultiSelect(fieldLabel);
        SampleContext.put(fieldLabel, actions.getMultiSelectSelectedText(fieldLabel));
        logMultiSelectState(fieldLabel, "Unselect all");
    }

    // Marca uma opção por vez (abre, marca, fecha, tira print) e desmarca em seguida
    public void checkEachMultiSelectOptionOneByOne(String fieldLabel) {
        List<String> options = actions.getMultiSelectOptions(fieldLabel);
        actions.closeMultiSelect(fieldLabel);
        for (String option : options) {
            actions.clickMultiSelectOption(fieldLabel, option);
            validateMultiSelectOption(fieldLabel, option, true);
            actions.closeMultiSelect(fieldLabel);
            logMultiSelectState(fieldLabel, "Checked " + option);

            actions.clickMultiSelectOption(fieldLabel, option);
            validateMultiSelectOption(fieldLabel, option, false);
            actions.closeMultiSelect(fieldLabel);
            logMultiSelectState(fieldLabel, "Unchecked " + option);
        }
    }

    private void validateMultiSelectOption(String fieldLabel, String option, boolean expectedSelected) {
        if (actions.isMultiSelectOptionSelected(fieldLabel, option) != expectedSelected) {
            throw new AssertionError("Option '" + option + "' in '" + fieldLabel + "' should be "
                    + (expectedSelected ? "selected" : "unselected"));
        }
    }

    private boolean allMultiSelectOptionsAre(String fieldLabel, boolean selected) {
        for (String option : actions.getMultiSelectOptions(fieldLabel)) {
            if (actions.isMultiSelectOptionSelected(fieldLabel, option) != selected) {
                return false;
            }
        }
        return true;
    }

    private void validateAllMultiSelectOptions(String fieldLabel, boolean expectedSelected) {
        for (String option : actions.getMultiSelectOptions(fieldLabel)) {
            validateMultiSelectOption(fieldLabel, option, expectedSelected);
        }
    }

    // Registra no log o que ficou selecionado e salva um print na pasta de evidência do cenário
    private void logMultiSelectState(String fieldLabel, String action) {
        String selected = actions.getMultiSelectSelectedText(fieldLabel);
        System.out.println("[DBS] " + fieldLabel + " | " + action + " | selected: "
                + (selected.isBlank() ? "(none)" : selected));
        java.nio.file.Path folder =
                com.automation.framework.infrastructure.report.CucumberStepListener.currentEvidenceFolder();
        if (folder != null) {
            String fileName = (fieldLabel + "_" + action).replaceAll("[^a-zA-Z0-9]+", "_") + ".png";
            actions.takeScreenshotAsFile(folder.resolve(fileName).toString());
        }
    }
}
