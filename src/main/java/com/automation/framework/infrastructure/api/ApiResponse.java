package com.automation.framework.infrastructure.api;

import java.util.List;
import java.util.Map;

public record ApiResponse(int statusCode, Map<String, List<String>> headers, String body) {

    public void assertStatus(int expectedStatus) {
        if (statusCode != expectedStatus) {
            throw new AssertionError(
                    "Expected API status " + expectedStatus + " but received " + statusCode
                            + ". Response body: " + body);
        }
    }

    public void assertSuccessful() {
        if (statusCode < 200 || statusCode >= 300) {
            throw new AssertionError(
                    "Expected a successful API response but received status " + statusCode
                            + ". Response body: " + body);
        }
    }

    public void assertBodyContains(String expectedText) {
        if (expectedText == null || !body.contains(expectedText)) {
            throw new AssertionError(
                    "Expected API response body to contain [" + expectedText
                            + "]. Actual response body: " + body);
        }
    }
}
