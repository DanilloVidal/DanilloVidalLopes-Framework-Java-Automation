package com.automation.framework.infrastructure.api;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

import java.util.stream.Collectors;

public final class BrowserSession {

    private BrowserSession() {
    }

    public static String getStorageValue(
            WebDriver driver, Storage storage, String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Storage key must not be blank.");
        }
        Object value = ((JavascriptExecutor) driver).executeScript(
                "return window[arguments[0]].getItem(arguments[1]);",
                storage.getJavaScriptName(), key);
        return value == null ? null : value.toString();
    }

    public static String getBearerTokenFromStorage(
            WebDriver driver, Storage storage, String key) {
        String token = getStorageValue(driver, storage, key);
        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "No authentication token was found in " + storage + " for the configured key.");
        }
        return token;
    }

    public static String getCookieHeader(WebDriver driver) {
        return driver.manage().getCookies().stream()
                .map(BrowserSession::toCookiePair)
                .collect(Collectors.joining("; "));
    }

    private static String toCookiePair(Cookie cookie) {
        return cookie.getName() + "=" + cookie.getValue();
    }

    public enum Storage {
        LOCAL("localStorage"),
        SESSION("sessionStorage");

        private final String javaScriptName;

        Storage(String javaScriptName) {
            this.javaScriptName = javaScriptName;
        }

        private String getJavaScriptName() {
            return javaScriptName;
        }
    }
}
