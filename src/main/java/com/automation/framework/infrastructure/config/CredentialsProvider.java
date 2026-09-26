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

    private static String required(String systemProperty, String environmentVariable) {
        String value = System.getProperty(systemProperty);
        if (value == null || value.isBlank()) {
            value = System.getenv(environmentVariable);
        }
        if (value == null || value.isBlank()) {
            value = localCredentials.getProperty(systemProperty);
        }
        if (value == null || value.isBlank()) {
            throw new IllegalStateException(
                    "Missing credential. Configure system property '" + systemProperty
                            + "', environment variable '" + environmentVariable
                            + "', or credentials.local.properties.");
        }
        return value;
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
