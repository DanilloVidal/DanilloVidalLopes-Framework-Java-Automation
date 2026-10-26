package com.automation.framework.presentation.pages.dsm.dbs;

import com.automation.framework.presentation.actions.dsm.dbs.DbsAction;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;

// Fluxo "Order new kit": escolha da farm/purpose, quantidade de kits e confirmação do pedido
public class DbsOrderKitPage {

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

    private final DbsAction actions;
    private final WebDriver driver;
    private final DbsPage homePage;

    public DbsOrderKitPage(WebDriver driver) {
        this.driver = driver;
        this.actions = new DbsAction(driver, 10);
        this.homePage = new DbsPage(driver);
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
            if (isOrderSuccessPopupVisible() || homePage.isOnHomePage()) {
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

    private String normalizeText(String text) {
        return text == null ? "" : text.replaceAll("\\s+", " ").trim().toLowerCase();
    }
}
