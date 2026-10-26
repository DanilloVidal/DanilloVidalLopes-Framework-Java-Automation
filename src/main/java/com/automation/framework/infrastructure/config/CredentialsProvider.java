package com.automation.framework.infrastructure.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

public final class CredentialsProvider {

    private static final Properties localCredentials = loadLocalCredentials();

    private CredentialsProvider() {
    }

    public static LoginCredentials dbs() {
        return new LoginCredentials(
                required("dbs.username", "DBS_USERNAME"),
                required("dbs.password", "DBS_PASSWORD"));
    }

    // Login corporativo (Microsoft) usado pelo Analytical Services
    public static LoginCredentials analyticalServices() {
        return new LoginCredentials(
                required("analyticalservices.username", "ANALYTICAL_SERVICES_USERNAME"),
                required("analyticalservices.password", "ANALYTICAL_SERVICES_PASSWORD"));
    }

    // Login corporativo (Microsoft) usado pelo Pigmentation calculator.
    // É o mesmo usuário do Analytical Services: usa as chaves pigmentation.* se existirem,
    // senão cai nas analyticalservices.*
    public static LoginCredentials pigmentation() {
        String username = optional("pigmentation.username", "PIGMENTATION_USERNAME");
        String password = optional("pigmentation.password", "PIGMENTATION_PASSWORD");
        if (username == null || password == null) {
            return analyticalServices();
        }
        return new LoginCredentials(username, password);
    }

    private static String required(String systemProperty, String environmentVariable) {
        String value = optional(systemProperty, environmentVariable);
        if (value == null) {
            throw new IllegalStateException(
                    "Missing credential. Configure system property '" + systemProperty
                            + "', environment variable '" + environmentVariable
                            + "', or credentials.local.properties.");
        }
        return value;
    }

    // Procura na ordem: system property, variável de ambiente, credentials.local.properties
    private static String optional(String systemProperty, String environmentVariable) {
        String value = System.getProperty(systemProperty);
        if (value == null || value.isBlank()) {
            value = System.getenv(environmentVariable);
        }
        if (value == null || value.isBlank()) {
            value = localCredentials.getProperty(systemProperty);
        }
        return value == null || value.isBlank() ? null : value;
    }

    private static Properties loadLocalCredentials() {
        Properties properties = new Properties();
        Path credentialsFile = Path.of("credentials.local.properties");

        if (Files.notExists(credentialsFile)) {
            return properties;
        }

        try (InputStream input = Files.newInputStream(credentialsFile)) {
            properties.load(input);
            return properties;
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Unable to read credentials.local.properties.", exception);
        }
    }
}
