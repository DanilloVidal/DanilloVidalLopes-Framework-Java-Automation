package com.automation.framework.presentation.data.dbs;

// Labels dos campos do formulário "Register samples", exatamente como aparecem na tela
public final class RegisterSampleFields {

    public static final String CUSTOMER = "Customer";
    public static final String FARM = "Farm";
    public static final String BARN_NAME = "Barn name";
    public static final String HOUSING = "Housing";
    public static final String PRODUCTION_SYSTEM = "Production system";
    public static final String PURPOSE_OF_ANALYSIS = "Purpose of analysis";
    public static final String END_CUSTOMER = "Is this sample for an End Customer?";
    public static final String END_CUSTOMER_NAME = "End Customer name";

    // Animal information
    public static final String SPECIES = "Species";
    public static final String PHYSIOLOGICAL_STAGE = "Physiological stage";
    public static final String AVERAGE_WEIGHT = "Average weight";
    public static final String REASON_FOR_ANALYSIS = "Reason for analysis";
    public static final String SPECIFIC_PROBLEM_AREA = "Specific problem area of interest";
    public static final String SEX = "Sex";
    public static final String GENETICS_SUPPLIER = "Genetics supplier";
    public static final String GENETICS_LINE = "Genetics line";

    // Feed information
    public static final String VITAMIN_D3 = "Vitamin D3";
    public static final String VITAMIN_25_OH_LEVEL = "Vitamin 25-OH level";
    public static final String VITAMIN_25_OH_D3 = "25-OH-D3";
    public static final String TOTAL_VITAMIN_D3_IN_DIET = "Total vitamin D3 level in the diet";
    public static final String TOTAL_CALCIUM = "Total Calcium";
    public static final String TOTAL_PHOSPHORUS = "Total Phosphorus";
    public static final String PHYTASE = "Phytase";
    // O select de unidade do Phytase não tem label na tela, por isso usa o data-testid
    public static final String PHYTASE_UNIT = "select-phytaseUnit";

    // Register cards
    public static final String SAMPLE_COLLECTION_DATE = "Sample collection date";
    public static final String VERAX_SAMPLING_SESSION = "Is this analysis linked to a Verax™ sampling session?";
    public static final String ADDITIONAL_NOTES = "Additional notes";
    public static final String DBS_SAMPLE_CARD_ID = "DBS sample card ID";
    public static final String ANIMAL_DETAILS = "Animal details";
    public static final String INCREASED_MORTALITY = "Has there been increased mortality in this group?";

    // Modais do envio da sample
    public static final String SUBMIT_CONFIRMATION_MODAL = "Would you like to submit this sample cards?";
    public static final String SAMPLES_REGISTERED_MODAL = "Samples registered";
    // Formato aceito pelo campo de data (dica da tela: "Type date as DD/MM/YYYY")
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    private RegisterSampleFields() {
    }
}
