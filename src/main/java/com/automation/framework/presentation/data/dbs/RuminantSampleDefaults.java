package com.automation.framework.presentation.data.dbs;

// Valores padrão usados para registrar uma sample de ruminant
public final class RuminantSampleDefaults {

    public static final String ANIMAL_TYPE = "ruminant";
    // Única fazenda de ruminant disponível para o usuário atual da automação
    public static final String FARM = "Ruminant Farm Test edited";
    // Pen/Barn ID é gerado aleatoriamente e salvo no SampleContext
    public static final String HOUSING = "Mixed";
    public static final String PURPOSE_OF_ANALYSIS = "Global survey";
    public static final String END_CUSTOMER = "Yes";
    public static final String END_CUSTOMER_NAME = "Automation Test";

    // Animal information
    public static final String SUB_SPECIES = "Dairy cow";
    public static final String BREEDS = "Holstein";
    public static final String ANIMAL_CATEGORY = "Lactating cow";
    public static final String SEX = "Female";
    // Number of lactations é sorteado de 1 a 10 e salvo no SampleContext
    public static final String PROBLEM_AREA_OF_INTEREST = "Nutrition";
    public static final String CLINICAL_PRODUCTION_CHALLENGES = "Ca/P metabolism";

    // Feed information (Feed details: todas as opções marcadas no final)
    public static final String VITAMIN_D3 = "22";
    public static final String ACTIVE_25_OH_D3_LEVEL = "1992";
    // Valores calculados pela tela a partir de 22 e 1992 (unidades padrão IU/animal/day e mg/animal/day)
    public static final String EXPECTED_VITAMIN_D3_EQUIVALENCE = "79680000";
    public static final String EXPECTED_TOTAL_VITAMIN_D3_IN_DIET = "79680022";
    public static final String VITAMIN_D3_UNIT = "IU/kg total feed";
    public static final String TOTAL_CALCIUM = "2024";
    public static final String TOTAL_PHOSPHORUS = "1988";

    // Register cards
    // Data fora da faixa permitida (fim da 2ª Guerra Mundial) para validar a mensagem de faixa
    public static final String OUT_OF_RANGE_COLLECTION_DATE = "02/09/1945";
    public static final String ANIMAL_DETAILS = "Test Test";
    public static final String ADDITIONAL_NOTES = """
            (When this began) I had nothing to say
            And I'd get lost in the nothingness inside of me
            (I was confused) and I let it all out to find
            That I'm not the only person with these things in mind

            (Inside of me) but all the vacancy the words revealed
            Is the only real thing that I've got left to feel
            (Nothing to lose) just stuck, hollow and alone
            And the fault is my own, and the fault is my own

            I wanna heal, I wanna feel
            What I thought was never real
            I wanna let go of the pain I've held so long
            (Erase all the pain till it's gone)

            I wanna heal, I wanna feel
            Like I'm close to something real
            I wanna find something I've wanted all along
            Somewhere I belong

            And I've got nothing to say""";

    private RuminantSampleDefaults() {
    }
}
