package com.automation.framework.presentation.actions.dbs;

import com.automation.framework.presentation.actions.Actions;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

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

    // ---------------------------------------------------------------------
    // Multi select com checkbox do DBS (componente "multiSelect", ex.: Feed details)
    // ---------------------------------------------------------------------

    private static final String FIND_MULTI_SELECT_JS = """
            const wrapper = [...document.querySelectorAll('[data-testid^="multi-select-"]')]
                .find((element) => [...element.querySelectorAll('label')]
                    .some((label) => (label.textContent || '').trim() === arguments[0]));
            const trigger = wrapper?.querySelector('button[class*="select-trigger"]');
            const optionElements = () => [...(wrapper?.querySelectorAll('[class*="multiSelect_option__"]') || [])];
            const optionText = (option) =>
                (option.querySelector('[class*="option-text"]')?.textContent || '').trim();
            """;

    public boolean isMultiSelectOpen(String label) {
        Object open = ((JavascriptExecutor) driver).executeScript(FIND_MULTI_SELECT_JS + """
                if (!trigger) return null;
                return String(trigger.className).includes('open');
                """, label);
        if (open == null) {
            throw new NoSuchElementException("DBS multi select was not found: " + label);
        }
        return Boolean.TRUE.equals(open);
    }

    public void openMultiSelect(String label) {
        if (isMultiSelectOpen(label)) {
            return;
        }
        WebElement trigger = (WebElement) ((JavascriptExecutor) driver).executeScript(
                FIND_MULTI_SELECT_JS + "return trigger || null;", label);
        click(trigger);
        sleep(500);
        if (!isMultiSelectOpen(label)) {
            throw new AssertionError("DBS multi select did not open: " + label);
        }
    }

    // Fecha clicando fora do campo (no título da página); se não fechar, usa ESC e por último o próprio gatilho
    public void closeMultiSelect(String label) {
        if (!isMultiSelectOpen(label)) {
            return;
        }
        click(driver.findElement(org.openqa.selenium.By.cssSelector("h2")));
        sleep(500);
        if (isMultiSelectOpen(label)) {
            new org.openqa.selenium.interactions.Actions(driver)
                    .sendKeys(org.openqa.selenium.Keys.ESCAPE).perform();
            sleep(500);
        }
        if (isMultiSelectOpen(label)) {
            WebElement trigger = (WebElement) ((JavascriptExecutor) driver).executeScript(
                    FIND_MULTI_SELECT_JS + "return trigger || null;", label);
            click(trigger);
            sleep(500);
        }
        if (isMultiSelectOpen(label)) {
            throw new AssertionError("DBS multi select did not close: " + label);
        }
    }

    public void clickMultiSelectOption(String label, String option) {
        openMultiSelect(label);
        WebElement element = (WebElement) ((JavascriptExecutor) driver).executeScript(FIND_MULTI_SELECT_JS + """
                return optionElements().find((option) => optionText(option) === arguments[1]) || null;
                """, label, option);
        if (element == null) {
            throw new NoSuchElementException("Option '" + option + "' was not found in multi select: " + label);
        }
        click(element);
        sleep(500);
    }

    public boolean isMultiSelectOptionSelected(String label, String option) {
        Object selected = ((JavascriptExecutor) driver).executeScript(FIND_MULTI_SELECT_JS + """
                const element = optionElements().find((option) => optionText(option) === arguments[1]);
                return element ? String(element.className).includes('selected') : null;
                """, label, option);
        if (selected == null) {
            throw new NoSuchElementException("Option '" + option + "' was not found in multi select: " + label);
        }
        return Boolean.TRUE.equals(selected);
    }

    // Opções da lista, sem o "Select all" (a lista precisa estar aberta para existir no DOM)
    @SuppressWarnings("unchecked")
    public java.util.List<String> getMultiSelectOptions(String label) {
        openMultiSelect(label);
        return (java.util.List<String>) ((JavascriptExecutor) driver).executeScript(FIND_MULTI_SELECT_JS + """
                return optionElements()
                    .filter((option) => !String(option.className).includes('select-all'))
                    .map(optionText);
                """, label);
    }

    // Texto exibido no gatilho com as opções selecionadas
    public String getMultiSelectSelectedText(String label) {
        Object text = ((JavascriptExecutor) driver).executeScript(FIND_MULTI_SELECT_JS + """
                return (wrapper?.querySelector('[class*="selected-text"]')?.textContent || '').trim();
                """, label);
        return text == null ? "" : String.valueOf(text);
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
