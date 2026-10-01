package com.automation.framework.presentation.data.dbs;

// Valores padrão usados para registrar uma sample de poultry
public final class PoultrySampleDefaults {

    public static final String ANIMAL_TYPE = "poultry";
    // Customer não existe para o usuário atual da automação
    public static final String CUSTOMER = "TEST NODE";
    public static final String FARM = "Test Poultry";
    // House number é gerado aleatoriamente e salvo no SampleContext
    public static final String HOUSING = "Outdoor / Open-sided";
    public static final String PRODUCTION_SYSTEM = "Other";
    public static final String PRODUCTION_SYSTEM_OTHER = "Bisteca";
    public static final String PURPOSE_OF_ANALYSIS = "Scientific projects";
    public static final String END_CUSTOMER = "No";

    // Animal information
    public static final String SUB_SPECIES = "Broiler Breeders";
    public static final String ANY_CHALLENGE_IN_THE_FLOCK = "Fertility";
    public static final String CLINICAL_CHALLENGE_UNIT = "55";
    public static final String SEX = "As hatched";
    public static final String GENETIC = "Cobb";
    public static final String BREED_AND_STRAIN = "null";
    public static final String FLOCK_PERFORMANCE_AT_SAMPLING = "Average egg weight";
    public static final String FLOCK_PERFORMANCE_AT_SAMPLE_UNIT = "88";

    // Feed information
    public static final String TOTAL_VITAMIN_D3_IN_THE_DIET = "55";
    public static final String ADDED_25_OH_D3 = "7890";
    // Valores calculados pela tela a partir de 55 e 7890
    public static final String EXPECTED_VITAMIN_25_OH_D3 = "315600";
    public static final String EXPECTED_TOTAL_VITAMIN_D3_IN_DIET = "315655";

    public static final String CALCIUM_ABOVE_RANGE = "77";
    public static final String CALCIUM_BELOW_RANGE = "00";
    public static final String TOTAL_CALCIUM = "5";
    public static final String CALCIUM_MAX_MESSAGE = "Total Calcium must be less than or equal to 6";
    public static final String CALCIUM_MIN_MESSAGE = "Total Calcium must be greater than or equal to 0.25";

    public static final String PHOSPHORUS_ABOVE_RANGE = "123";
    public static final String PHOSPHORUS_BELOW_RANGE = "000000000000.22";
    // Digitado com vírgula de propósito: a tela aceita e converte para 0.29
    public static final String TOTAL_PHOSPHORUS = "0,29";
    public static final String PHOSPHORUS_MAX_MESSAGE = "Total Phosphorus must be less than or equal to 6";
    public static final String PHOSPHORUS_MIN_MESSAGE = "Total Phosphorus must be greater than or equal to 0.25";

    public static final String PHYTASE_INCLUSION = "1231";
    public static final String PHYTASE_UNIT = "FYT";
    public static final String FEEDING_PHASE = "Grower";

    public static final String AGE_ABOVE_RANGE = "9999";
    public static final String AGE_BELOW_RANGE = "000";
    public static final String AGE_25_OH_D3_WAS_INCLUDED = "963";
    public static final String AGE_MAX_MESSAGE = "Value must be less than or equal to 999";
    public static final String AGE_MIN_MESSAGE = "Value must be greater than or equal to 1";

    public static final String TYPE_OF_DIET = "Other";
    public static final String TYPE_OF_DIET_SPECIFICATION = "Hakuna matata";

    // Register cards
    // Data fora da faixa permitida (início da 2ª Guerra Mundial) para validar a mensagem de faixa
    public static final String OUT_OF_RANGE_COLLECTION_DATE = "01/09/1939";
    public static final String VERAX_SAMPLING_SESSION = "No";
    public static final String AGE_IN_DAYS = "12";
    public static final String ADDITIONAL_NOTES = """
            Just a scar somewhere down inside of me
            Something I cannot repair
            Even though it will always be
            I pretend it isn't there (this is how it feel)
            I'm trapped in yesterday (just a memory)
            Where the pain is all I know (this is all I know)
            And I'll never break away (can't break free)
            'Cause when I'm alone

            I'm lost in these memories
            Living behind my own illusion
            Lost all my dignity
            Living inside my own confusion""";

    private PoultrySampleDefaults() {
    }
}
