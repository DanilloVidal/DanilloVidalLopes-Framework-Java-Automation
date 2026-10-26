package com.automation.framework.presentation.pages.dsm.dbs;

import com.automation.framework.infrastructure.config.CredentialsProvider;
import com.automation.framework.infrastructure.config.LoginCredentials;
import com.automation.framework.infrastructure.driver.DriverFactory;
import com.automation.framework.presentation.actions.dsm.dbs.DbsAction;
import com.automation.framework.presentation.data.dsm.dbs.SampleContext;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import java.util.List;

// Login, cookies e home do DBS (botões que abrem os fluxos de Order new kit e Register samples)
public class DbsPage {

    private static final String DBS_URL = "https://dbs.test.productsupport.dsm.com/";

    private final DbsAction actions;
    private final WebDriver driver;
    private final By acceptCookiesButton = By.cssSelector("button.disclosureAcceptAll");
    private final By acceptCookiesFullPath = By.xpath(
            "/html/body/div[4]/div/div/div[2]//div/div/div[1]/div/div[5]/button[1]");
    private final By acceptCookiesByText = By.xpath(
            "//button[normalize-space()='Eu concordo']");
    private final By acceptCookiesContainer = By.xpath("/html/body/div[4]/div/div/div[2]//div");
    private static final String COOKIE_SETTINGS_KEY = "uc_settings";
    private static final String COOKIE_SETTINGS_VALUE = """
            {"controllerId":"2b931d23ade9b4df5c64e6e32f63f18e7d8dd75e273af45b956fd85a7531c58a",
            "id":"X8TnJNGW8qNbD0","language":"en","services":[],"version":"7.6.54"}
            """.replace("\n", "").replace("\r", "");
    private final By loginEmailInput = By.id("login-email");
    private final By loginPasswordInput = By.id("login-password");
    private final By loginSubmitButton = By.cssSelector("button[type='submit']");
    private final By activeHomeLink = By.cssSelector("a[aria-current='page'][href='/']");
    private final By homeHeading = By.xpath(
            "//*[self::h1 or self::h2 or self::h3][contains(normalize-space(), 'SciTell') and contains(normalize-space(), 'DBS Analytics')]");

    public DbsPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new DbsAction(driver, 10);
    }

    public void navigateToDbs() {
        DriverFactory.get().get(DBS_URL);
        actions.sleep(5000);
        acceptCookies();
    }

    public void refreshPage() {
        driver.navigate().refresh();
        actions.sleep(3000);
    }

    private void injectCookieConsent() {
        ((JavascriptExecutor) driver).executeScript(
                "window.localStorage.setItem(arguments[0], arguments[1]);",
                COOKIE_SETTINGS_KEY, COOKIE_SETTINGS_VALUE);
    }

    private void acceptCookies() {
        injectCookieConsent();
        driver.switchTo().defaultContent();
        if (acceptCookiesInCurrentContext()) {
            driver.switchTo().defaultContent();
            actions.sleep(1000);
        } else {
            for (WebElement frame : driver.findElements(By.cssSelector("iframe, frame"))) {
                try {
                    driver.switchTo().frame(frame);
                } catch (StaleElementReferenceException ignored) {
                    continue;
                }
                if (acceptCookiesInCurrentContext()) {
                    driver.switchTo().defaultContent();
                    actions.sleep(1000);
                    break;
                }
                driver.switchTo().parentFrame();
            }
        }

        driver.switchTo().defaultContent();
        forceCloseCookieOverlay();
        ((JavascriptExecutor) driver).executeScript("""
                const selectors = [
                    '.truste_overlay',
                    '.truste_cm_outerdiv',
                    '.truste_box_overlay',
                    '[id^="pop-outerdiv"]',
                    '[id^="pop-div"]',
                    '#cookieConsentDescription'
                ];
                document.querySelectorAll(selectors.join(',')).forEach((element) => {
                    element.remove();
                });
                document.body.style.removeProperty('overflow');
                document.documentElement.style.removeProperty('overflow');
                """);
        actions.sleep(1000);
    }

    private boolean acceptCookiesInCurrentContext() {
        for (By locator : List.of(acceptCookiesFullPath, acceptCookiesByText, acceptCookiesButton)) {
            for (WebElement button : driver.findElements(locator)) {
                if (button.isDisplayed() && button.isEnabled()
                        && tryCookieActions(button)) {
                    return true;
                }
            }
        }

        for (WebElement container : driver.findElements(acceptCookiesContainer)) {
            if (container.isDisplayed()) {
                container.sendKeys(Keys.ENTER);
                if (!isCookieButtonVisible()) {
                    return true;
                }
                container.sendKeys(Keys.SPACE);
                if (!isCookieButtonVisible()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean tryCookieActions(WebElement button) {
        try {
            button.click();
        } catch (ElementClickInterceptedException ignored) {
        }
        if (!isCookieButtonVisible()) {
            return true;
        }

        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", button);
        if (!isCookieButtonVisible()) {
            return true;
        }

        button.sendKeys(Keys.ENTER);
        if (!isCookieButtonVisible()) {
            return true;
        }

        button.sendKeys(Keys.SPACE);
        if (!isCookieButtonVisible()) {
            return true;
        }
        return forceCloseCookieOverlay();
    }

    private boolean isCookieButtonVisible() {
        return driver.findElements(acceptCookiesByText).stream()
                .anyMatch(element -> element.isDisplayed());
    }

    private boolean forceCloseCookieOverlay() {
        Object closed = ((JavascriptExecutor) driver).executeScript("""
                const overlays = document.querySelectorAll(
                    '.truste_cm_outerdiv, [id^="pop-outerdiv"], [role="dialog"]');
                let closed = false;
                overlays.forEach((overlay) => {
                    if (overlay.offsetParent !== null) {
                        overlay.style.display = 'none';
                        overlay.style.pointerEvents = 'none';
                        closed = true;
                    }
                });
                document.querySelectorAll('button.disclosureAcceptAll').forEach((button) => {
                    if (button.offsetParent !== null) {
                        let parent = button.parentElement;
                        for (let index = 0; index < 6 && parent; index++) {
                            if (parent.id.startsWith('pop-outerdiv')
                                || String(parent.className).includes('outerdiv')
                                || parent.getAttribute('role') === 'dialog') {
                                parent.style.display = 'none';
                                parent.style.pointerEvents = 'none';
                                closed = true;
                                break;
                            }
                            parent = parent.parentElement;
                        }
                    }
                });
                return closed;
                """);
        return Boolean.TRUE.equals(closed);
    }

    public void doDbsLogin() {
        LoginCredentials credentials = CredentialsProvider.dbs();
        doDbsLogin(credentials.username(), credentials.password());
        actions.sleep(10000);
        acceptCookies();
        actions.sleep(10);
    }

    public void doDbsLogin(String username, String password) {
        actions.sendKeys(loginEmailInput, username);
        actions.sendKeys(loginPasswordInput, password);
        actions.jsClick(loginSubmitButton);
    }

    public void validateHomePage() {
        if (!actions.isDisplayed(activeHomeLink) && !actions.isDisplayed(homeHeading)) {
            throw new AssertionError("DBS home page was not displayed. Current URL: "
                    + driver.getCurrentUrl());
        }
    }

    public boolean isOnHomePage() {
        return driver.getCurrentUrl().startsWith(DBS_URL)
                && actions.isDisplayed(activeHomeLink);
    }

    // URL base do DBS, para os testes de segurança montarem rotas internas (ex.: DBS_URL + "samples")
    public String baseUrl() {
        return DBS_URL;
    }

    // Checagens rápidas (sem espera) usadas pelos testes de segurança
    public boolean isAuthenticated() {
        return !driver.findElements(activeHomeLink).isEmpty();
    }

    public boolean isLoginFormVisible() {
        return !driver.findElements(loginEmailInput).isEmpty();
    }

    public void clickOrderNewKit() {
        actions.sleep(5000);
        actions.clickDsmButton("Order new kit", false);
        actions.sleep(2000);
    }

    public void clickRegisterSamples(String animalType) {
        SampleContext.start(animalType.trim().toLowerCase());
        actions.sleep(5000);
        actions.clickDsmButton("Register " + animalType.trim().toLowerCase() + " samples", false);
        actions.sleep(2000);
    }
}
