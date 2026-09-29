package com.automation.framework.presentation.actions.dbs;

import com.automation.framework.presentation.actions.Actions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;

// Ações específicas das telas do DBS (dependem dos XPaths e da ordem dos campos do DBS).
// Ações genéricas de componentes DSM ficam em Actions.
public class DbsAction extends Actions {

    private final WebDriver driver;

    public DbsAction(WebDriver driver, int timeoutInSeconds) {
        super(driver, timeoutInSeconds);
        this.driver = driver;
    }

    public void clickDsmByXpath(String xpath) {
        boolean clicked = Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript("""
                const findElements = (root, selector) => {
                    const elements = [...root.querySelectorAll(selector)];
                    for (const element of root.querySelectorAll('*')) {
                        if (element.shadowRoot) {
                            elements.push(...findElements(element.shadowRoot, selector));
                        }
                    }
                    return elements;
                };
                if (arguments[0].includes('dsm-radio-button-group')) {
                    const groupMatch = arguments[0].match(/dsm-radio-button-group\\[(\\d+)\\]/);
                    const radioMatch = arguments[0].match(/dsm-radio-button\\[(\\d+)\\]/);
                    const groups = findElements(document, 'dsm-radio-button-group');
                    const group = groups[Number(groupMatch[1]) - 1];
                    const radios = group
                        ? findElements(group, 'dsm-radio-button')
                        : [];
                    const radio = radios[Number(radioMatch[1]) - 1];
                    const control = radio?.shadowRoot?.querySelector('label, input, button')
                        || radio;
                    if (!control) {
                        return false;
                    }
                    control.click();
                    return true;
                }
                if (arguments[0].includes('/dsm-button')) {
                    const buttonHosts = findElements(document, 'dsm-button')
                        .filter((element) => element.offsetParent !== null);
                    const button = buttonHosts[buttonHosts.length - 1];
                    const control = button?.shadowRoot?.querySelector('button') || button;
                    if (!control) {
                        return false;
                    }
                    control.click();
                    return true;
                }
                const selects = findElements(document, 'dsm-select')
                    .filter((element) => element.offsetParent !== null);
                const selectIndex = arguments[0].includes('/div[2]/div/dsm-select') ? 1 : 0;
                const select = selects[selectIndex];
                if (!select) {
                    return false;
                }
                const control = select.shadowRoot?.querySelector(
                    '.select__placeholder, .select__arrow, '
                    + '[role="button"][aria-controls], [role="button"].select, '
                    + 'button, input, div[role="button"], div[tabindex]');
                if (!control) {
                    return false;
                }
                control.scrollIntoView({block: 'center'});
                control.click();
                return true;
                """, xpath));
        if (!clicked) {
            throw new NoSuchElementException("DBS element was not found for XPath: " + xpath);
        }
    }

    public void clickDsmQuantityButton(String xpath, int times) {
        for (int index = 0; index < times; index++) {
            clickDsmByXpath(xpath);
        }
    }

    public void selectDsmOption(String label, String option) {
        selectDsmOption(label, option, false);
    }

    public void selectDsmOption(String label, String option, boolean alreadyOpened) {
        int selectIndex = label.startsWith("On which") ? 0 : 1;
        boolean formLoaded = false;
        for (int attempt = 0; attempt < 60; attempt++) {
            formLoaded = Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript("""
                    const findElements = (root, selector) => {
                        const elements = [...root.querySelectorAll(selector)];
                        for (const element of root.querySelectorAll('*')) {
                            if (element.shadowRoot) {
                                elements.push(...findElements(element.shadowRoot, selector));
                            }
                        }
                        return elements;
                    };
                    return findElements(document, 'dsm-select').length > arguments[0];
                    """, selectIndex));
            if (formLoaded) {
                break;
            }
            sleep(1000);
        }
        if (!formLoaded) {
            throw new AssertionError("Order form did not load the select fields. Current URL: "
                    + driver.getCurrentUrl());
        }

        boolean opened = alreadyOpened || Boolean.TRUE.equals(((JavascriptExecutor) driver)
                .executeScript("""
                        const findElements = (root, selector) => {
                            const elements = [...root.querySelectorAll(selector)];
                            for (const element of root.querySelectorAll('*')) {
                                if (element.shadowRoot) {
                                    elements.push(...findElements(element.shadowRoot, selector));
                                }
                            }
                            return elements;
                        };
                        const selects = findElements(document, 'dsm-select')
                            .filter((element) => element.offsetParent !== null);
                        const select = selects[arguments[1]];
                        if (!select) {
                            return false;
                        }
                        const control = select.shadowRoot?.querySelector(
                            '[role="button"][aria-controls], [role="button"].select, '
                            + 'button, input, div[role="button"], div[tabindex]');
                        if (!control) {
                            select.click();
                            return true;
                        }
                        control.click();
                        return true;
                        """, label, selectIndex));
        if (!opened) {
            throw new AssertionError("DBS select field was not found: " + label
                    + ". Current URL: " + driver.getCurrentUrl());
        }

        sleep(1000);
        boolean selected = Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript("""
                const findElements = (root, selector) => {
                    const elements = [...root.querySelectorAll(selector)];
                    for (const element of root.querySelectorAll('*')) {
                        if (element.shadowRoot) {
                            elements.push(...findElements(element.shadowRoot, selector));
                        }
                    }
                    return elements;
                };
                const selects = findElements(document, 'dsm-select')
                    .filter((element) => element.offsetParent !== null);
                const normalize = (value) => value.split(' ').filter(Boolean).join(' ').trim();
                const select = selects[arguments[2]];
                const roots = select?.shadowRoot ? [select.shadowRoot, document] : [document];
                const options = roots.flatMap((root) =>
                    findElements(root, 'li, [role="option"], dsm-option'));
                const option = options.find((element) =>
                    normalize(element.textContent || '') === normalize(arguments[1]));
                if (!option) {
                    return false;
                }
                option.click();
                return true;
                """, label, option, selectIndex));
        if (!selected) {
            throw new NoSuchElementException("DBS option was not found: " + option);
        }
        sleep(1000);
    }
}
