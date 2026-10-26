package com.automation.framework.presentation.data.dsm.analyticalservices;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

// Guarda os valores preenchidos na sample do cenário atual (por thread),
// para que outros steps (ex.: validação da sample salva) possam consultá-los.
public final class AnalyticalSampleContext {

    private static final ThreadLocal<Map<String, String>> fields =
            ThreadLocal.withInitial(LinkedHashMap::new);

    private AnalyticalSampleContext() {
    }

    public static void start() {
        fields.get().clear();
    }

    // A chave é o name do campo (CreateSampleFields) ou "Analyte: <laboratório> | <analito>"
    public static void put(String field, String value) {
        fields.get().put(field, value);
    }

    public static String get(String field) {
        return fields.get().get(field);
    }

    public static String sampleName() {
        return get(CreateSampleFields.SAMPLE_NAME);
    }

    public static Map<String, String> values() {
        return Collections.unmodifiableMap(fields.get());
    }

    public static void clear() {
        fields.remove();
    }
}
