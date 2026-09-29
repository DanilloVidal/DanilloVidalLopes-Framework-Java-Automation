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

    public static Map<String, String> values() {
        return Collections.unmodifiableMap(fields.get());
    }

    public static void clear() {
        animalType.remove();
        fields.remove();
    }
}
