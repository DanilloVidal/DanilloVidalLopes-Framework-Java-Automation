package com.automation.framework.presentation.pages.dbs;

import com.automation.framework.infrastructure.config.CredentialsProvider;
import com.automation.framework.infrastructure.config.LoginCredentials;
import com.automation.framework.infrastructure.driver.DriverFactory;
import com.automation.framework.presentation.actions.dbs.DbsActions;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

public class DbsPage {

    private static final String DBS_URL = "https://dbs.test.productsupport.dsm.com/";
    private static final String FARM_CONFIRMATION_XPATH =
            "/html/body/div[1]/main/dsm-grid/div[2]/div/div[1]/dsm-radio-button-group[1]/dsm-radio-button[1]//label";
    private static final String FARM_TESTING_OPTION_XPATH =
            "/html/body/div[1]/main/dsm-grid/div[2]/div/div[1]/dsm-radio-button-group[2]/dsm-radio-button[1]//label/div[1]";
    private static final String NEXT_BUTTON_XPATH =
            "/html/body/div[1]/main/dsm-grid/div[4]/dsm-button";
    private static final String PLUS_BUTTON_XPATH =
            "/html/body/div[1]/main/dsm-grid/div[3]/div/div/div/div[2]/dsm-stepper//div/dsm-button[2]//button";
    private static final String MINUS_BUTTON_XPATH =
            "/html/body/div[1]/main/dsm-grid/div[3]/div/div/div/div[2]/dsm-stepper//div/dsm-button[1]/dsm-icon//div/svg/path";
    private static final String ORDER_CONFIRM_BUTTON_XPATH =
            "/html/body/div[1]/main/dsm-grid/dsm-modal/dsm-button[2]";

    private final DbsActions actions;
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
    private final By orderNewKitButton = By.xpath(
            "/html/body/div[1]/main/dsm-grid/div[1]/div[1]/div[2]/div/dsm-button[1]//button");

    public DbsPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new DbsActions(driver, 10);
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
                .anyMatch(WebElement::isDisplayed);
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

    public void clickOrderNewKit() {
        actions.sleep(5000);
        actions.clickDsmButton("Order new kit", false);
        actions.sleep(2000);
    }

    public void selectFarm(String farm) {
        actions.sleep(10000);
        actions.selectDsmOption("On which farm will you be testing?", farm);
    }

    public void clickFarmConfirmation() {
        actions.clickDsmByXpath(FARM_CONFIRMATION_XPATH);
    }

    public void validateFarm(String farm) {
        String selectedFarm = (String) ((JavascriptExecutor) driver).executeScript("""
                const findElements = (root, selector) => {
                    const elements = [...root.querySelectorAll(selector)];
                    for (const element of root.querySelectorAll('*')) {
                        if (element.shadowRoot) {
                            elements.push(...findElements(element.shadowRoot, selector));
                        }
                    }
                    return elements;
                };
                return findElements(document, 'dsm-select')
                    .map((element) => element.shadowRoot?.textContent || element.textContent || '')
                    .join(' ');
                """);
        if (selectedFarm == null || !normalizeText(selectedFarm).contains(normalizeText(farm))) {
            throw new AssertionError("Selected farm did not match: " + farm);
        }
    }

    public void clickFarmTestingOption() {
        actions.clickDsmByXpath(FARM_TESTING_OPTION_XPATH);
    }

    public void selectAnalysisPurpose(String purpose) {
        actions.clickDsmByXpath("//*[@id=\"root\"]/main/dsm-grid/div[2]/div/div[2]/div/dsm-select//div[2]");
        actions.selectDsmOption("Purpose of analysis", purpose, true);
    }

    public void clickNext() {
        actions.sleep(5000);
        actions.clickDsmByXpath(NEXT_BUTTON_XPATH);
        actions.sleep(2000);
    }

    public void increaseKitQuantity(int times) {
        actions.clickDsmQuantityButton(PLUS_BUTTON_XPATH, times);
    }

    public void decreaseKitQuantity(int times) {
        actions.clickDsmQuantityButton(MINUS_BUTTON_XPATH, times);
    }

    public void setKitQuantity(String quantity) {
        actions.setQuantity(quantity);
    }

    public void clickPlaceOrder() {
        actions.clickDsmButton("Place order", false);
        actions.sleep(2000);
    }

    public void validateOrderReview() {
        String modalText = (String) ((JavascriptExecutor) driver).executeScript("""
                const textOf = (node) => {
                    if (!node) return '';
                    if (node.nodeType === Node.TEXT_NODE) return node.nodeValue || '';
                    if (node.nodeType !== Node.ELEMENT_NODE
                            && node.nodeType !== Node.DOCUMENT_FRAGMENT_NODE) return '';
                    if (node.nodeType === Node.ELEMENT_NODE
                            && node.tagName.toLowerCase() === 'slot') {
                        const assigned = node.assignedNodes({flatten: true});
                        return assigned.length
                            ? assigned.map(textOf).join(' ')
                            : node.textContent || '';
                    }
                    return [...node.childNodes].map(textOf).join(' ');
                };
                const modal = [...document.querySelectorAll('dsm-modal')]
                    .find((element) => element.offsetParent !== null);
                return modal ? textOf(modal.shadowRoot || modal) : '';
                """);
        if (modalText == null || modalText.isBlank()) {
            throw new AssertionError("DBS order review text was not displayed.");
        }
    }

    public void confirmOrder() {
        actions.clickDsmModalButton(ORDER_CONFIRM_BUTTON_XPATH);
        actions.sleep(2000);
    }

    public void switchToOrderModalIframe(String xpath) {
        actions.switchToFrameByXpath(xpath);
    }

    public void switchToMainDocument() {
        actions.switchToDefaultContent();
    }

    public void validateOrderSuccess() {
        for (int attempt = 0; attempt < 20; attempt++) {
            if (isOrderSuccessPopupVisible() || hasReturnedToDbsHome()) {
                return;
            }
            actions.sleep(500);
        }
        throw new AssertionError("DBS order success message was not displayed. Current URL: "
                + driver.getCurrentUrl());
    }

    private boolean isOrderSuccessPopupVisible() {
        String pageText = (String) ((JavascriptExecutor) driver).executeScript("""
                const roots = [document];
                let text = '';
                while (roots.length) {
                    const root = roots.pop();
                    text += ' ' + (root.body?.innerText || root.textContent || '');
                    for (const element of root.querySelectorAll('*')) {
                        if (element.shadowRoot) roots.push(element.shadowRoot);
                    }
                }
                return text;
                """);
        String normalized = normalizeText(pageText);
        return normalized.contains("success") && normalized.contains("order");
    }

    private boolean hasReturnedToDbsHome() {
        return driver.getCurrentUrl().startsWith(DBS_URL)
                && actions.isDisplayed(activeHomeLink);
    }

    private String normalizeText(String text) {
        return text == null ? "" : text.replaceAll("\\s+", " ").trim().toLowerCase();
    }
}
