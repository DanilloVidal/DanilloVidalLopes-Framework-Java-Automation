package com.automation.framework.presentation.data.dsm.analyticalservices;

// Atributo "name" de cada campo do formulário "Create a sample" do Analytical Services.
// A tela é React Aria e não tem ids fixos (os ids são gerados a cada render), mas cada campo
// mantém um <select>/<input name="..."> escondido com nome estável, que usamos como "id".
// O comentário ao lado mostra o label exibido na tela (em inglês).
public final class CreateSampleFields {

    // Step 1: General sample information
    public static final String DSM_FIRMENICH_UNIT = "dsmUnitId";            // dsm-firmenich unit *
    public static final String CUSTOMER = "customerId";                     // Customer *
    public static final String ANALYSIS_TYPE = "analysisTypeId";            // Analysis type *
    public static final String SPECIES = "speciesId";                       // Species *
    public static final String SUB_SPECIES = "subSpeciesId";                // Sub-species *
    public static final String SUB_SPECIES_OTHER = "subSpeciesOther";       // Sub-species - other * (Sub-species = Other)
    public static final String PHASE = "phaseId";                           // Phase
    public static final String SAMPLE_TYPE = "sampleTypeId";                // Sample type *
    public static final String SAMPLE_TYPE_OTHER = "productTypeOther";      // Sample type - other * (Sample type = Other)
    public static final String SAMPLE_NAME = "name";                        // Sample name * (Mycotoxin)
    public static final String PURPOSE_OF_ANALYSIS = "purposeId";           // Purpose of analysis * (Mycotoxin)
    public static final String END_CUSTOMER = "isEndCustomer";              // Is this a service requested for an end customer? *
    public static final String END_CUSTOMER_NAME = "endCustomerName";       // End customer name * (End customer = Yes)

    // Step 2: Detailed sample information (Mycotoxin)
    public static final String HARVEST_YEAR = "harvestYear";                // Harvest year / Production year *
    public static final String COUNTRY_OF_ORIGIN = "countryOfOriginId";     // Country of origin *
    public static final String STATE_PROVINCE = "stateProvince";            // State/Province (habilita após o país)
    public static final String POST_CODE_OF_ORIGIN = "postCodeOrigin";      // Post code of origin
    public static final String HARVEST_MONTH = "harvestMonth";              // Harvest month / Production month
    public static final String SAMPLE_NUMBER = "sampleNumber";              // Sample number/LOT number
    public static final String CLINICAL_SIGNS_QUESTION = "isClinicalSigns"; // Is there clinical signs?
    public static final String CLINICAL_SIGNS_OTHER = "clinicalSignOther";  // Clinical signs - other (Clinical signs inclui Other)

    // Campos sem atributo name: localizados pela classe do componente
    public static final String SAMPLING_DATE_CSS = ".sci-date-picker-input input[type='date']";     // Sampling date
    public static final String CLINICAL_SIGNS_CSS = ".sci-autocomplete input[role='combobox']";      // Clinical signs * (Is there clinical signs? = Yes)

    private CreateSampleFields() {
    }
}
