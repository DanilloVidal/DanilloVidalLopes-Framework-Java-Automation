package com.automation.framework.presentation.data.dbs;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// Guarda os valores preenchidos na sample do cenário atual (por thread),
// para que outros steps (ex.: validação no backend) possam consultá-los.
public final class SampleContext {

    private static final ThreadLocal<String> animalType = new ThreadLocal<>();
    private static final ThreadLocal<Map<String, String>> fields =
            ThreadLocal.withInitial(LinkedHashMap::new);

    private SampleContext() {
    }

    public static void start(String type) {
        animalType.set(type);
        fields.get().clear();
    }

    public static String animalType() {
        return animalType.get();
    }

    public static void put(String fieldLabel, String value) {
        fields.get().put(fieldLabel, value);
    }

    public static String get(String fieldLabel) {
        return fields.get().get(fieldLabel);
    }

    // Atalho para o card ID gerado no cenário (usado em consultas futuras, ex.: backend)
    public static String cardId() {
        return get(RegisterSampleFields.DBS_SAMPLE_CARD_ID);
    }

    // Atalho para o House number gerado no cenário de poultry
    public static String houseNumber() {
        return get(RegisterSampleFields.HOUSE_NUMBER);
    }

    // Atalho para o Pen/Barn ID gerado no cenário de ruminant
    public static String penBarnId() {
        return get(RegisterSampleFields.PEN_BARN_ID);
    }

    // Atalho para o Number of lactations sorteado no cenário de ruminant
    public static String numberOfLactations() {
        return get(RegisterSampleFields.NUMBER_OF_LACTATIONS);
    }

    public static Map<String, String> values() {
        return Collections.unmodifiableMap(fields.get());
    }

    public static void clear() {
        animalType.remove();
        fields.remove();
    }
}
