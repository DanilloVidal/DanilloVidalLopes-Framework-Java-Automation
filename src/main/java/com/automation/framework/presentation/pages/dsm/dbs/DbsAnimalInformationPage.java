package com.automation.framework.presentation.pages.dsm.dbs;

import com.automation.framework.presentation.data.dsm.dbs.PoultrySampleDefaults;
import com.automation.framework.presentation.data.dsm.dbs.RegisterSampleFields;
import com.automation.framework.presentation.data.dsm.dbs.RuminantSampleDefaults;
import com.automation.framework.presentation.data.dsm.dbs.SwineSampleDefaults;
import org.openqa.selenium.WebDriver;
import java.util.concurrent.ThreadLocalRandom;

// Register samples - etapa 2: "Animal information"
public class DbsAnimalInformationPage extends DbsRegisterFormPage {

    public DbsAnimalInformationPage(WebDriver driver) {
        super(driver);
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

    // Fluxo completo da etapa "Animal information" de ruminant com os valores padrão
    public void fillRuminantAnimalInformation() {
        clickRegisterNext();
        validateRequiredFieldMessage(RegisterSampleFields.SUB_SPECIES);
        validateRequiredFieldMessage(RegisterSampleFields.SEX);
        selectRegisterOption(RegisterSampleFields.SUB_SPECIES, RuminantSampleDefaults.SUB_SPECIES);
        clickRegisterNext();
        validateRequiredFieldMessage(RegisterSampleFields.ANIMAL_CATEGORY);
        selectRegisterOption(RegisterSampleFields.BREEDS, RuminantSampleDefaults.BREEDS);
        selectRegisterOption(RegisterSampleFields.ANIMAL_CATEGORY, RuminantSampleDefaults.ANIMAL_CATEGORY);
        chooseRegisterRadio(RegisterSampleFields.SEX, RuminantSampleDefaults.SEX);
        selectRandomNumberOfLactations();
        clickRegisterNext();
        validateFieldIsMarkedAsRequired(RegisterSampleFields.PROBLEM_AREA_OF_INTEREST);
        selectRegisterOption(RegisterSampleFields.PROBLEM_AREA_OF_INTEREST,
                RuminantSampleDefaults.PROBLEM_AREA_OF_INTEREST);
        selectRegisterOption(RegisterSampleFields.CLINICAL_PRODUCTION_CHALLENGES,
                RuminantSampleDefaults.CLINICAL_PRODUCTION_CHALLENGES);
    }

    // Sorteia um Number of lactations de 1 a 10 e salva no SampleContext
    // (RegisterSampleFields.NUMBER_OF_LACTATIONS) para uso futuro
    public String selectRandomNumberOfLactations() {
        String lactations = String.valueOf(ThreadLocalRandom.current().nextInt(1, 11));
        selectRegisterOption(RegisterSampleFields.NUMBER_OF_LACTATIONS, lactations);
        System.out.println("[DBS] Number of lactations: " + lactations);
        return lactations;
    }
}
