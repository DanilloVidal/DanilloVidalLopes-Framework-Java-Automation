package com.automation.framework.presentation.pages.dbs;

import com.automation.framework.infrastructure.config.CredentialsProvider;
import com.automation.framework.infrastructure.config.LoginCredentials;
import com.automation.framework.infrastructure.driver.DriverFactory;
import com.automation.framework.presentation.actions.dbs.DbsActions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriverException;

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

    private void acceptCookies() {
        if (acceptCookiesInCurrentContext()) {
            return;
        }
        for (WebElement frame : driver.findElements(By.cssSelector("iframe, frame"))) {
            try {
                driver.switchTo().frame(frame);
                if (acceptCookiesInCurrentContext()) {
                    return;
                }
            } catch (WebDriverException ignored) {
                // Cookie consent may be hosted in a cross-origin frame.
            } finally {
                driver.switchTo().defaultContent();
            }
        }
        forceCloseCookieOverlay();
    }

    private boolean acceptCookiesInCurrentContext() {
        for (By locator : List.of(acceptCookiesFullPath, acceptCookiesByText, acceptCookiesButton)) {
            for (WebElement button : driver.findElements(locator)) {
                if (button.isDisplayed() && button.isEnabled()) {
                    actions.click(button);
                    return true;
                }
            }
        }
        return false;
    }

    private boolean forceCloseCookieOverlay() {
        return Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript("""
                const buttons = [...document.querySelectorAll('button')];
                const button = buttons.find((element) => {
                    const text = (element.innerText || element.textContent || '').trim();
                    return /accept all|eu concordo|aceitar todos/i.test(text)
                        && element.getClientRects().length > 0;
                });
                if (!button) return false;
                button.click();
                return true;
                """));
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
