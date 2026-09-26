package com.automation.framework.infrastructure.api;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

public final class ApiClient {

    private final HttpClient httpClient;
    private final URI baseUri;
    private final Duration requestTimeout;

    public ApiClient(String baseUrl) {
        this(baseUrl, Duration.ofSeconds(30));
    }

    public ApiClient(String baseUrl, Duration requestTimeout) {
        if (baseUrl == null || baseUrl.isBlank()) {
            throw new IllegalArgumentException("API base URL must not be blank.");
        }
        if (requestTimeout == null || requestTimeout.isZero() || requestTimeout.isNegative()) {
            throw new IllegalArgumentException("Request timeout must be positive.");
        }

        String normalizedBaseUrl = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        this.baseUri = URI.create(normalizedBaseUrl);
        this.requestTimeout = requestTimeout;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(requestTimeout)
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
    }

    public ApiResponse get(String path, String bearerToken, Map<String, String> headers)
            throws IOException, InterruptedException {
        return send("GET", path, null, bearerToken, headers);
    }

    public ApiResponse postJson(
            String path, String jsonBody, String bearerToken, Map<String, String> headers)
            throws IOException, InterruptedException {
        if (jsonBody == null) {
            throw new IllegalArgumentException("JSON request body must not be null.");
        }
        return send("POST", path, jsonBody, bearerToken, headers);
    }

    public ApiResponse send(
            String method,
            String path,
            String body,
            String bearerToken,
            Map<String, String> headers) throws IOException, InterruptedException {
        if (method == null || method.isBlank()) {
            throw new IllegalArgumentException("HTTP method must not be blank.");
        }
        if (path == null || path.isBlank()) {
            throw new IllegalArgumentException("API path must not be blank.");
        }

        URI requestUri = baseUri.resolve(path);
        HttpRequest.Builder request = HttpRequest.newBuilder(requestUri)
                .timeout(requestTimeout)
                .header("Accept", "application/json");

        if (headers != null) {
            headers.forEach(request::setHeader);
        }
        if (bearerToken != null && !bearerToken.isBlank()) {
            request.setHeader("Authorization", "Bearer " + bearerToken);
        }

        if (body == null) {
            request.method(method.toUpperCase(), HttpRequest.BodyPublishers.noBody());
        } else {
            request.setHeader("Content-Type", "application/json");
            request.method(method.toUpperCase(), HttpRequest.BodyPublishers.ofString(body));
        }

        HttpResponse<String> response = httpClient.send(
                request.build(), HttpResponse.BodyHandlers.ofString());
        return new ApiResponse(
                response.statusCode(), response.headers().map(), response.body());
    }
}
