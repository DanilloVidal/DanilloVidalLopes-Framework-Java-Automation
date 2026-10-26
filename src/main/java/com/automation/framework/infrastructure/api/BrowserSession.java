package com.automation.framework.infrastructure.api;

import org.openqa.selenium.Cookie;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chromium.ChromiumDriver;
import org.openqa.selenium.json.Json;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    // Campos aceitos pelo Network.setCookies do CDP; o resto do getAllCookies (size, session...) é descartado
    private static final List<String> CDP_COOKIE_FIELDS =
            List.of("name", "value", "domain", "path", "secure", "httpOnly", "sameSite", "expires");

    /**
     * Salva os cookies (no Chrome/Edge de todos os domínios, inclusive o login da Microsoft)
     * e o localStorage/sessionStorage da origem atual num JSON, para reaproveitar o login.
     * O driver precisa estar numa página da origem que se quer salvar.
     */
    public static void saveSession(WebDriver driver, Path file) {
        Map<String, Object> session = new LinkedHashMap<>();
        session.put("savedAt", System.currentTimeMillis());
        session.put("cookies", getAllCookies(driver));
        session.put("localStorage", getStorage(driver, Storage.LOCAL));
        session.put("sessionStorage", getStorage(driver, Storage.SESSION));
        try {
            Files.createDirectories(file.toAbsolutePath().getParent());
            Files.writeString(file, new Json().toJson(session), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not save browser session to " + file, e);
        }
    }

    /**
     * Restaura uma sessão salva por saveSession. Retorna false se o arquivo não existir.
     * O driver precisa estar numa página da mesma origem da sessão salva (o storage é por origem);
     * depois de restaurar, navegue para o site para ele ler a sessão.
     */
    @SuppressWarnings("unchecked")
    public static boolean restoreSession(WebDriver driver, Path file) {
        if (!Files.exists(file)) {
            return false;
        }
        Map<String, Object> session;
        try {
            session = new Json().toType(Files.readString(file, StandardCharsets.UTF_8), Json.MAP_TYPE);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not read browser session from " + file, e);
        }
        setAllCookies(driver, (List<Map<String, Object>>) session.getOrDefault("cookies", List.of()));
        setStorage(driver, Storage.LOCAL,
                (Map<String, Object>) session.getOrDefault("localStorage", Map.of()));
        setStorage(driver, Storage.SESSION,
                (Map<String, Object>) session.getOrDefault("sessionStorage", Map.of()));
        return true;
    }

    public static void deleteSession(Path file) {
        try {
            Files.deleteIfExists(file);
        } catch (IOException e) {
            throw new UncheckedIOException("Could not delete browser session " + file, e);
        }
    }

    // Chrome/Edge: o CDP devolve os cookies de todos os domínios; nos outros, só os do domínio atual
    @SuppressWarnings("unchecked")
    private static List<Map<String, Object>> getAllCookies(WebDriver driver) {
        List<Map<String, Object>> cookies = new ArrayList<>();
        if (driver instanceof ChromiumDriver chromium) {
            Map<String, Object> result = chromium.executeCdpCommand("Network.getAllCookies", Map.of());
            for (Map<String, Object> cookie : (List<Map<String, Object>>) result.get("cookies")) {
                Map<String, Object> param = new LinkedHashMap<>();
                for (String field : CDP_COOKIE_FIELDS) {
                    if (cookie.get(field) != null) {
                        param.put(field, cookie.get(field));
                    }
                }
                // Cookie de sessão vem com expires = -1; sem o campo ele continua sendo de sessão
                if (Boolean.TRUE.equals(cookie.get("session"))) {
                    param.remove("expires");
                }
                cookies.add(param);
            }
            return cookies;
        }
        for (Cookie cookie : driver.manage().getCookies()) {
            Map<String, Object> param = new LinkedHashMap<>();
            param.put("name", cookie.getName());
            param.put("value", cookie.getValue());
            param.put("domain", cookie.getDomain());
            param.put("path", cookie.getPath());
            param.put("secure", cookie.isSecure());
            param.put("httpOnly", cookie.isHttpOnly());
            if (cookie.getExpiry() != null) {
                param.put("expires", cookie.getExpiry().getTime() / 1000);
            }
            cookies.add(param);
        }
        return cookies;
    }

    private static void setAllCookies(WebDriver driver, List<Map<String, Object>> cookies) {
        if (driver instanceof ChromiumDriver chromium) {
            chromium.executeCdpCommand("Network.setCookies", Map.of("cookies", cookies));
            return;
        }
        for (Map<String, Object> cookie : cookies) {
            Cookie.Builder builder = new Cookie.Builder(
                    (String) cookie.get("name"), (String) cookie.get("value"))
                    .domain((String) cookie.get("domain"))
                    .path((String) cookie.get("path"))
                    .isSecure(Boolean.TRUE.equals(cookie.get("secure")))
                    .isHttpOnly(Boolean.TRUE.equals(cookie.get("httpOnly")));
            if (cookie.get("expires") instanceof Number expires) {
                builder.expiresOn(new Date((long) (expires.doubleValue() * 1000)));
            }
            try {
                driver.manage().addCookie(builder.build());
            } catch (RuntimeException ignored) {
                // Fora do Chromium só dá para gravar cookies do domínio atual
            }
        }
    }

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getStorage(WebDriver driver, Storage storage) {
        Object value = ((JavascriptExecutor) driver).executeScript(
                "return Object.assign({}, window[arguments[0]]);", storage.getJavaScriptName());
        return value == null ? Map.of() : (Map<String, Object>) value;
    }

    private static void setStorage(WebDriver driver, Storage storage, Map<String, Object> values) {
        ((JavascriptExecutor) driver).executeScript(
                "const s = window[arguments[0]]; const v = arguments[1];"
                        + " Object.keys(v).forEach(k => s.setItem(k, v[k]));",
                storage.getJavaScriptName(), values);
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
