package com.automation.framework.presentation.data.dbs;

// Valores padrão usados para registrar uma sample de swine
public final class SwineSampleDefaults {

    public static final String ANIMAL_TYPE = "swine";
    public static final String CUSTOMER = "DANONE, S.A";
    public static final String FARM = "Automation";
    public static final String BARN_NAME = "Automation Test";
    public static final String HOUSING = "Outdoor";
    public static final String PRODUCTION_SYSTEM = "Farrow to finish";
    public static final String PURPOSE_OF_ANALYSIS = "Customer service";
    public static final String END_CUSTOMER = "Yes";
    public static final String END_CUSTOMER_NAME = "Automation Test";

    // Animal information
    public static final String PHYSIOLOGICAL_STAGE = "Piglets";
    public static final String AVERAGE_WEIGHT = "10-15 kg / 20-30 lbs";
    public static final String REASON_FOR_ANALYSIS = "Immunity";
    public static final String SPECIFIC_PROBLEM_AREA = "Mortality";
    public static final String SEX = "Mixed";
    public static final String GENETICS_SUPPLIER = "DanBred";
    public static final String GENETICS_LINE = "Duroc";

    // Feed information
    public static final String VITAMIN_D3 = "66";
    public static final String VITAMIN_25_OH_LEVEL = "123";
    // Valores calculados pela tela a partir de Vitamin D3 = 66 e Vitamin 25-OH level = 123
    public static final String EXPECTED_VITAMIN_25_OH_D3 = "4920";
    public static final String EXPECTED_TOTAL_VITAMIN_D3_IN_DIET = "4986";
    public static final String TOTAL_CALCIUM = "2";
    public static final String TOTAL_PHOSPHORUS = "1";
    public static final String ABOVE_RANGE_VALUE = "3";
    public static final String BELOW_RANGE_VALUE = "-99";
    public static final String RANGE_MESSAGE = "Please, provide a number between the range of 0 to 2";
    public static final String PHYTASE = "99";
    public static final String PHYTASE_UNIT = "OTU";

    // Register cards
    public static final String VERAX_SAMPLING_SESSION = "Yes";
    public static final String ANIMAL_DETAILS = "Test Test";
    public static final String INCREASED_MORTALITY = "Yes";
    public static final String ADDITIONAL_NOTES = """
            I watch how the Moon
            Sits in the sky on a dark night
            Shining with the light from the Sun
            The Sun doesn't give light to the Moon, assuming
            The Moon is gonna owe it one
            It makes me think of how you act to me, you do
            Favors then rapidly
            Just turn around and start asking me about
            Things that you want back from me

            I'm sick of the tension, sick of the hunger
            Sick of you acting like I owe you this
            Find another place to feed your greed
            While I find a place to rest

            I wanna be in another place
            I hate when you say you don't understand
            (You'll see it's not meant to be)
            I wanna be in the energy
            Not with the enemy, a place for my head""";

    private SwineSampleDefaults() {
    }
}
