package com.automation.framework.presentation.pages.dsm.dbs;

import com.automation.framework.presentation.data.dsm.dbs.PoultrySampleDefaults;
import com.automation.framework.presentation.data.dsm.dbs.RegisterSampleFields;
import com.automation.framework.presentation.data.dsm.dbs.RuminantSampleDefaults;
import com.automation.framework.presentation.data.dsm.dbs.SampleContext;
import com.automation.framework.presentation.data.dsm.dbs.SwineSampleDefaults;
import org.openqa.selenium.WebDriver;

// Register samples - etapa 1: "Farm information"
public class DbsFarmInformationPage extends DbsRegisterFormPage {

    public DbsFarmInformationPage(WebDriver driver) {
        super(driver);
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

    // Fluxo completo da etapa "Farm information" de ruminant com os valores padrão
    public void fillRuminantFarmInformation() {
        clickRegisterNext();
        validateRequiredFieldMessage(RegisterSampleFields.FARM);
        validateRequiredFieldMessage(RegisterSampleFields.HOUSING);
        validateRequiredFieldMessage(RegisterSampleFields.PURPOSE_OF_ANALYSIS);
        validateRequiredFieldMessage(RegisterSampleFields.END_CUSTOMER);
        selectRegisterOption(RegisterSampleFields.FARM, RuminantSampleDefaults.FARM);
        typeRandomPenBarnId();
        chooseRegisterRadio(RegisterSampleFields.HOUSING, RuminantSampleDefaults.HOUSING);
        selectRegisterOption(RegisterSampleFields.PURPOSE_OF_ANALYSIS, RuminantSampleDefaults.PURPOSE_OF_ANALYSIS);
        selectRegisterOption(RegisterSampleFields.END_CUSTOMER, RuminantSampleDefaults.END_CUSTOMER);
        clickRegisterNext();
        validateRequiredFieldMessage(RegisterSampleFields.END_CUSTOMER_NAME);
        typeRegisterField(RegisterSampleFields.END_CUSTOMER_NAME, RuminantSampleDefaults.END_CUSTOMER_NAME);
        validateFieldIsNotEmpty(RegisterSampleFields.END_CUSTOMER_NAME);
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

    // Gera um Pen/Barn ID aleatório de 6 dígitos e salva no SampleContext (RegisterSampleFields.PEN_BARN_ID)
    public String typeRandomPenBarnId() {
        String penBarnId = randomNumber(6);
        typeRegisterField(RegisterSampleFields.PEN_BARN_ID, penBarnId);
        SampleContext.put(RegisterSampleFields.PEN_BARN_ID, penBarnId);
        System.out.println("[DBS] Pen/Barn ID: " + penBarnId);
        return penBarnId;
    }
}
