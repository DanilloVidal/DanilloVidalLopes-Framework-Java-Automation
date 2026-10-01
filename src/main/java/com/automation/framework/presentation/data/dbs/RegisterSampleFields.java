package com.automation.framework.presentation.data.dbs;

// Labels dos campos do formulário "Register samples", exatamente como aparecem na tela
public final class RegisterSampleFields {

    public static final String CUSTOMER = "Customer";
    public static final String FARM = "Farm";
    public static final String BARN_NAME = "Barn name";
    public static final String HOUSE_NUMBER = "House number";
    public static final String PEN_BARN_ID = "Pen/Barn ID";
    public static final String HOUSING = "Housing";
    public static final String PRODUCTION_SYSTEM = "Production system";
    public static final String PRODUCTION_SYSTEM_OTHER = "Production system (other)";
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

    // Animal information (ruminant)
    public static final String BREEDS = "Breeds";
    public static final String ANIMAL_CATEGORY = "Animal category";
    public static final String NUMBER_OF_LACTATIONS = "Number of lactations";
    public static final String PROBLEM_AREA_OF_INTEREST = "Problem area of interest";
    public static final String CLINICAL_PRODUCTION_CHALLENGES = "Clinical/Production challenges";

    // Feed information (ruminant)
    public static final String FEED_DETAILS = "Feed details";
    // O select de unidade do Vitamin D3 não tem label na tela, por isso usa o data-testid
    public static final String VITAMIN_D3_UNIT = "select-d3Unit";
    public static final String ACTIVE_25_OH_D3_LEVEL = "Active 25-OH D3 level";
    public static final String VITAMIN_D3_EQUIVALENCE = "Vitamin D3 equivalence";

    // Animal information (poultry)
    public static final String SUB_SPECIES = "Sub-species";
    public static final String ANY_CHALLENGE_IN_THE_FLOCK = "Any challenge in the flock?";
    public static final String CLINICAL_CHALLENGE_UNIT = "Clinical challenge unit";
    public static final String GENETIC = "Genetic";
    public static final String BREED_AND_STRAIN = "Breed and strain";
    public static final String FLOCK_PERFORMANCE_AT_SAMPLING = "Flock performance at sampling";
    public static final String FLOCK_PERFORMANCE_AT_SAMPLE_UNIT = "Flock performance at sample unit";

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

    // Feed information (poultry)
    public static final String TOTAL_VITAMIN_D3_IN_THE_DIET = "Total vitamin D3 in the diet";
    public static final String ADDED_25_OH_D3 = "Added 25-OH-D3";
    public static final String PHYTASE_INCLUSION = "Phytase inclusion (FTU, FYT/ton)";
    public static final String FEEDING_PHASE = "Feeding phase (e.g. starter, grower)";
    public static final String AGE_25_OH_D3_WAS_INCLUDED = "At what age 25-OH-D3 was included?";
    public static final String TYPE_OF_DIET = "Type of diet";
    public static final String TYPE_OF_DIET_SPECIFICATION = "Please specify the type of diet";

    // Register cards
    public static final String SAMPLE_COLLECTION_DATE = "Sample collection date";
    public static final String VERAX_SAMPLING_SESSION = "Is this analysis linked to a Verax™ sampling session?";
    public static final String ADDITIONAL_NOTES = "Additional notes";
    public static final String DBS_SAMPLE_CARD_ID = "DBS sample card ID";
    public static final String ANIMAL_DETAILS = "Animal details";
    public static final String INCREASED_MORTALITY = "Has there been increased mortality in this group?";
    public static final String AGE_IN_DAYS = "Age (in days)";

    // Modais do envio da sample
    public static final String SUBMIT_CONFIRMATION_MODAL = "Would you like to submit this sample cards?";
    public static final String SAMPLES_REGISTERED_MODAL = "Samples registered";
    // Formato aceito pelo campo de data (dica da tela: "Type date as DD/MM/YYYY")
    public static final String DATE_FORMAT = "dd/MM/yyyy";

    private RegisterSampleFields() {
    }
}
