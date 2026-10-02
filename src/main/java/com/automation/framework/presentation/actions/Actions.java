package com.automation.framework.presentation.actions;

import com.automation.framework.infrastructure.wrappers.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Duration;
import java.util.List;

public class Actions extends WebActions {

    // Localiza um campo DSM (dsm-select, dsm-input, dsm-radio-button-group) pelo atributo label
    // ou, para campos sem label, pelo data-testid, procurando inclusive dentro de shadow roots.
    // Usado pelos métodos "...ByLabel".
    private static final String FIND_FIELD_JS = """
            const normalize = (value) => String(value || '').split(' ').filter(Boolean).join(' ')
                .trim().toLowerCase();
            const findElements = (root, selector) => {
                const elements = [...root.querySelectorAll(selector)];
                for (const element of root.querySelectorAll('*')) {
                    if (element.shadowRoot) {
                        elements.push(...findElements(element.shadowRoot, selector));
                    }
                }
                return elements;
            };
            const findField = (tag, label) => findElements(document, tag)
                .filter((element) => element.getClientRects().length > 0)
                .find((element) => normalize(element.getAttribute('label')) === normalize(label)
                    || element.getAttribute('data-testid') === label);
            const textOf = (node) => {
                if (!node) return '';
                if (node.nodeType === Node.TEXT_NODE) return node.nodeValue || '';
                if (node.nodeType === Node.ELEMENT_NODE && node.tagName.toLowerCase() === 'slot') {
                    return node.assignedNodes({flatten: true}).map(textOf).join(' ');
                }
                const children = [...node.childNodes].map(textOf).join(' ');
                return node.shadowRoot ? textOf(node.shadowRoot) + ' ' + children : children;
            };
            """;

    private final WebDriver driver;
    private final org.openqa.selenium.interactions.Actions interactionActions;

    public Actions(WebDriver driver, int timeoutInSeconds) {
        super(driver, timeoutInSeconds);
        this.driver = driver;
        this.interactionActions = new org.openqa.selenium.interactions.Actions(driver);
    }

    public void sleep(int milliseconds) {
        super.sleep((long) milliseconds);
    }

    public void switchToFrameByXpath(String xpath) {
        super.switchToFrameByXpath(xpath);
    }

    @Override
    public void click(By locator) {
        try {
            super.click(locator);
        } catch (ElementClickInterceptedException exception) {
            click(findElement(locator));
        }
    }

    public void click(WebElement element) {
        try {
            element.click();
        } catch (ElementClickInterceptedException exception) {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].scrollIntoView({block: 'center'}); arguments[0].click();",
                    element);
        }
    }

    public void clickBy(By locator) {
        click(locator);
    }

    public void clickById(String id) {
        click(By.id(id));
    }

    public void clickByName(String name) {
        click(By.name(name));
    }

    public void clickByCssSelector(String selector) {
        click(By.cssSelector(selector));
    }

    public void clickByXpath(String xpath) {
        click(By.xpath(xpath));
    }

    public void clickByLinkText(String linkText) {
        click(By.linkText(linkText));
    }

    public void clickByPartialLinkText(String linkText) {
        click(By.partialLinkText(linkText));
    }

    public void clickByText(String text) {
        clickByText(text, false);
    }

    public void clickByText(String text, boolean lastMatch) {
        List<WebElement> matches = findElements(
                By.cssSelector("button, a, [role='button']"))
                .stream()
                .filter(element -> element.isDisplayed())
                .filter(element -> {
                    String visibleText = element.getText();
                    String accessibleName = element.getAttribute("aria-label");
                    return (visibleText != null && visibleText.contains(text))
                            || (accessibleName != null && accessibleName.contains(text));
                })
                .toList();
        if (matches.isEmpty()) {
            throw new NoSuchElementException("No visible clickable element matched text: " + text);
        }
        click(matches.get(lastMatch ? matches.size() - 1 : 0));
    }

    public void type(By locator, String text) {
        sendKeys(locator, text);
    }

    public void type(WebElement element, String text) {
        element.clear();
        element.sendKeys(text);
    }

    public void typeById(String id, String text) {
        type(By.id(id), text);
    }

    public void typeByName(String name, String text) {
        type(By.name(name), text);
    }

    public void typeByCssSelector(String selector, String text) {
        type(By.cssSelector(selector), text);
    }

    public void typeByXpath(String xpath, String text) {
        type(By.xpath(xpath), text);
    }

    public void setInputValue(By locator, String value) {
        WebElement element = findElement(locator);
        setElementValue(element, value);
    }

    public void setQuantity(String quantity) {
        boolean updated = Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript("""
                const findInputs = (root) => {
                    const inputs = [...root.querySelectorAll('input[type="number"]')];
                    for (const element of root.querySelectorAll('*')) {
                        if (element.shadowRoot) {
                            inputs.push(...findInputs(element.shadowRoot));
                        }
                    }
                    return inputs;
                };
                const input = findInputs(document)
                    .find((element) => element.getClientRects().length > 0
                        && !element.disabled && !element.readOnly);
                if (!input) {
                    return false;
                }
                input.focus();
                const setter = Object.getOwnPropertyDescriptor(
                    HTMLInputElement.prototype, 'value').set;
                setter.call(input, arguments[0]);
                input.dispatchEvent(new Event('input', {bubbles: true, composed: true}));
                input.dispatchEvent(new Event('change', {bubbles: true, composed: true}));
                input.blur();
                return true;
                """, quantity));
        if (!updated) {
            throw new NoSuchElementException("No editable number input was found.");
        }
    }

    public void setQuantity(By locator, String quantity) {
        WebElement input = findElement(locator);
        if (!"number".equalsIgnoreCase(input.getAttribute("type"))) {
            throw new IllegalArgumentException("Quantity locator must target an input[type=number].");
        }
        setElementValue(input, quantity);
    }

    public void setQuantityById(String id, String quantity) {
        setQuantity(By.id(id), quantity);
    }

    public void setQuantityByName(String name, String quantity) {
        setQuantity(By.name(name), quantity);
    }

    private void setElementValue(WebElement element, String value) {
        ((JavascriptExecutor) driver).executeScript("""
                const input = arguments[0];
                input.focus();
                const setter = Object.getOwnPropertyDescriptor(
                    HTMLInputElement.prototype, 'value').set;
                setter.call(input, arguments[1]);
                input.dispatchEvent(new Event('input', {bubbles: true, composed: true}));
                input.dispatchEvent(new Event('change', {bubbles: true, composed: true}));
                input.blur();
                """, element, value);
    }

    public String getText(WebElement element) {
        return element.getText();
    }

    public String getInnerText(By locator) {
        return (String) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].innerText;", findElement(locator));
    }

    public String getTextContent(By locator) {
        return (String) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].textContent;", findElement(locator));
    }

    public String getHtml(By locator) {
        return (String) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].outerHTML;", findElement(locator));
    }

    public String getInnerHtml(By locator) {
        return (String) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].innerHTML;", findElement(locator));
    }

    public String getValue(By locator) {
        return getAttribute(locator, "value");
    }

    public void scrollToTop() {
        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, 0);");
    }

    public void scrollToBottom() {
        ((JavascriptExecutor) driver).executeScript(
                "window.scrollTo(0, document.body.scrollHeight);");
    }

    public void scrollBy(int horizontalPixels, int verticalPixels) {
        ((JavascriptExecutor) driver).executeScript(
                "window.scrollBy(arguments[0], arguments[1]);",
                horizontalPixels, verticalPixels);
    }

    public void scrollToElement(WebElement element) {
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    public void doubleClick(By locator) {
        interactionActions.doubleClick(findElement(locator)).perform();
    }

    public void rightClick(By locator) {
        interactionActions.contextClick(findElement(locator)).perform();
    }

    public void clickAndHold(By locator) {
        interactionActions.clickAndHold(findElement(locator)).perform();
    }

    public void release(By locator) {
        interactionActions.release(findElement(locator)).perform();
    }

    public void hover(By locator) {
        interactionActions.moveToElement(findElement(locator)).perform();
    }

    public void dragAndDrop(By sourceLocator, By targetLocator) {
        interactionActions.dragAndDrop(
                findElement(sourceLocator), findElement(targetLocator)).perform();
    }

    public void pressKey(By locator, org.openqa.selenium.Keys key) {
        findElement(locator).sendKeys(key);
    }

    public void setChecked(By locator, boolean checked) {
        WebElement element = findElement(locator);
        if (!"checkbox".equalsIgnoreCase(element.getAttribute("type"))
                && !"radio".equalsIgnoreCase(element.getAttribute("type"))) {
            throw new IllegalArgumentException("Locator must target a checkbox or radio input.");
        }
        if (element.isSelected() != checked) {
            click(element);
        }
    }

    public void selectById(String id, String visibleText) {
        selectFromDropdownByVisibleText(By.id(id), visibleText);
    }

    public void selectByName(String name, String visibleText) {
        selectFromDropdownByVisibleText(By.name(name), visibleText);
    }

    public void selectByCssSelector(String selector, String visibleText) {
        selectFromDropdownByVisibleText(By.cssSelector(selector), visibleText);
    }

    public void selectByValue(By locator, String value) {
        selectFromDropdownByValue(locator, value);
    }

    public void selectCustomOption(By triggerLocator, By optionsLocator, String visibleText) {
        click(triggerLocator);
        List<WebElement> options = findElements(optionsLocator);
        WebElement option = options.stream()
                .filter(element -> element.isDisplayed())
                .filter(element -> element.getText().trim().equals(visibleText.trim()))
                .findFirst()
                .orElseThrow(() -> new NoSuchElementException(
                        "Dropdown option was not found: " + visibleText));
        click(option);
    }

    public void printPage() {
        ((JavascriptExecutor) driver).executeScript("window.print();");
    }

    public void waitForVisible(By locator, Duration timeout) {
        new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public void waitForClickable(By locator, Duration timeout) {
        new WebDriverWait(driver, timeout)
                .until(ExpectedConditions.elementToBeClickable(locator));
    }

    public void switchToWindow(String windowHandle) {
        driver.switchTo().window(windowHandle);
    }

    public Path waitForDownload(Path directory, String fileName, Duration timeout) {
        Path downloadedFile = directory.resolve(fileName);
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            if (Files.isRegularFile(downloadedFile) && isDownloadComplete(directory, fileName)
                    && fileHasContent(downloadedFile)) {
                return downloadedFile;
            }
            sleep(200);
        }
        throw new TimeoutException(
                "Download did not complete before timeout: " + downloadedFile);
    }

    public Path clickAndWaitForDownload(
            By clickLocator, Path directory, String fileName, Duration timeout) {
        Path downloadedFile = directory.resolve(fileName);
        boolean existedBefore = Files.isRegularFile(downloadedFile);
        long previousSize = fileSize(downloadedFile);
        FileTime previousModified = fileLastModified(downloadedFile);
        click(clickLocator);
        long deadline = System.nanoTime() + timeout.toNanos();
        while (System.nanoTime() < deadline) {
            if (Files.isRegularFile(downloadedFile)
                    && isDownloadComplete(directory, fileName)
                    && fileHasContent(downloadedFile)) {
                long currentSize = fileSize(downloadedFile);
                FileTime currentModified = fileLastModified(downloadedFile);
                boolean changed = !existedBefore
                        || currentSize != previousSize
                        || !currentModified.equals(previousModified);
                if (changed) {
                    return downloadedFile;
                }
            }
            sleep(200);
        }
        throw new TimeoutException(
                "Click did not produce a new or updated download before timeout: "
                        + downloadedFile);
    }

    public boolean fileExists(Path file) {
        return Files.isRegularFile(file);
    }

    public void assertFileExists(Path file) {
        if (!fileExists(file)) {
            throw new AssertionError("Expected file does not exist: " + file);
        }
    }

    public String readPdf(Path file) throws IOException {
        assertFileExists(file);
        try (PDDocument document = PDDocument.load(file.toFile())) {
            return new PDFTextStripper().getText(document);
        }
    }

    public void assertPdfContains(Path file, String expectedText) throws IOException {
        String pdfText = readPdf(file);
        if (!pdfText.contains(expectedText)) {
            throw new AssertionError(
                    "PDF did not contain expected text [" + expectedText + "]: " + file);
        }
    }

    public void uploadFile(By locator, Path file) {
        assertFileExists(file);
        uploadFile(locator, file.toAbsolutePath().toString());
    }

    public String readTextFile(Path file) throws IOException {
        return Files.readString(file, StandardCharsets.UTF_8);
    }

    public boolean fileMatchesText(Path file, String expectedText) throws IOException {
        return readTextFile(file).equals(expectedText);
    }

    public boolean compareText(String actualText, String expectedText) {
        return actualText.equals(expectedText);
    }

    public void clickQuantity(By locator, int times) {
        for (int index = 0; index < times; index++) {
            click(locator);
        }
    }

    // ---------------------------------------------------------------------
    // Componentes DSM (dsm-button, dsm-select, dsm-input, dsm-radio-button-group, dsm-modal)
    // ---------------------------------------------------------------------

    public void clickDsmButton(String text, boolean lastMatch) {
        boolean clicked = Boolean.TRUE.equals(((JavascriptExecutor) driver).executeScript("""
                const buttons = [...document.querySelectorAll('dsm-button')]
                    .filter((element) => {
                        const content = [
                            element.shadowRoot?.textContent,
                            element.textContent,
                            element.innerText
                        ].filter(Boolean).join(' ').trim();
                        // getClientRects também considera botões em elementos fixos (ex.: dsm-modal)
                        return element.getClientRects().length > 0
                            && content.includes(arguments[0]);
                    });
                if (buttons.length === 0) {
                    return false;
                }
                const index = arguments[1] ? buttons.length - 1 : 0;
                const host = buttons[index];
                const button = host.shadowRoot?.querySelector('button');
                (button || host).scrollIntoView({block: 'center'});
                (button || host).click();
                return true;
                """, text, lastMatch));
        if (!clicked) {
            throw new NoSuchElementException("DSM button was not found: " + text);
        }
    }

    public void selectDsmOptionByLabel(String label, String option) {
        String result = "";
        for (int attempt = 0; attempt < 20; attempt++) {
            result = String.valueOf(((JavascriptExecutor) driver).executeScript(FIND_FIELD_JS + """
                    const select = findField('dsm-select', arguments[0]);
                    if (!select) return 'field not found';
                    if (select.hasAttribute('disabled') && select.getAttribute('disabled') !== 'false') {
                        return 'field disabled';
                    }
                    const control = select.shadowRoot?.querySelector(
                        '[role="button"][aria-controls], [role="button"].select, '
                        + 'button, div[role="button"], div[tabindex]');
                    (control || select).scrollIntoView({block: 'center'});
                    (control || select).click();
                    const options = findElements(select.shadowRoot || select, 'li, [role="option"]');
                    const match = options.find((element) =>
                        normalize(element.textContent) === normalize(arguments[1]));
                    if (!match) return 'option not found';
                    match.scrollIntoView({block: 'center'});
                    match.click();
                    return 'ok';
                    """, label, option));
            if ("ok".equals(result)) {
                sleep(1000);
                return;
            }
            sleep(500);
        }
        throw new NoSuchElementException(
                "DSM option '" + option + "' could not be selected in '" + label + "': " + result);
    }

    public void typeDsmInputByLabel(String label, String text) {
        WebElement input = null;
        for (int attempt = 0; attempt < 20 && input == null; attempt++) {
            input = (WebElement) ((JavascriptExecutor) driver).executeScript(FIND_FIELD_JS + """
                    const field = findField('dsm-input, dsm-textarea', arguments[0]);
                    return field?.shadowRoot?.querySelector('input, textarea') || null;
                    """, label);
            if (input == null) {
                sleep(500);
            }
        }
        if (input == null) {
            throw new NoSuchElementException("DSM input field was not found: " + label);
        }
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center'});", input);
        // Seleciona e apaga o conteúdo atual antes de digitar, para substituir valores já preenchidos
        input.sendKeys(org.openqa.selenium.Keys.chord(org.openqa.selenium.Keys.CONTROL, "a"),
                org.openqa.selenium.Keys.DELETE);
        input.sendKeys(text);
    }

    public String getDsmInputValueByLabel(String label) {
        Object value = ((JavascriptExecutor) driver).executeScript(FIND_FIELD_JS + """
                const field = findField('dsm-input', arguments[0]);
                if (!field) return null;
                return field.shadowRoot?.querySelector('input, textarea')?.value
                    ?? field.getAttribute('value') ?? '';
                """, label);
        if (value == null) {
            throw new NoSuchElementException("DSM input field was not found: " + label);
        }
        return String.valueOf(value);
    }

    public void chooseDsmRadioByLabel(String groupLabel, String option) {
        String result = "";
        for (int attempt = 0; attempt < 20; attempt++) {
            result = String.valueOf(((JavascriptExecutor) driver).executeScript(FIND_FIELD_JS + """
                    const group = findField('dsm-radio-button-group', arguments[0]);
                    if (!group) return 'group not found';
                    if (group.hasAttribute('disabled') && group.getAttribute('disabled') !== 'false') {
                        return 'group disabled';
                    }
                    const radios = [...new Set([
                        ...findElements(group, 'dsm-radio-button'),
                        ...(group.shadowRoot ? findElements(group.shadowRoot, 'dsm-radio-button') : [])
                    ])];
                    const radio = radios.find((element) =>
                        normalize(element.getAttribute('label') || textOf(element))
                            === normalize(arguments[1]));
                    if (!radio) return 'option not found';
                    const control = radio.shadowRoot?.querySelector('input, label') || radio;
                    control.scrollIntoView({block: 'center'});
                    control.click();
                    return 'ok';
                    """, groupLabel, option));
            if ("ok".equals(result)) {
                sleep(1000);
                return;
            }
            sleep(500);
        }
        throw new NoSuchElementException(
                "DSM radio '" + option + "' could not be chosen in '" + groupLabel + "': " + result);
    }

    public String getDsmFieldTextByLabel(String label) {
        Object text = ((JavascriptExecutor) driver).executeScript(FIND_FIELD_JS + """
                const field = ['dsm-select', 'dsm-input', 'dsm-textarea', 'dsm-radio-button-group']
                    .map((tag) => findField(tag, arguments[0]))
                    .find(Boolean);
                if (!field) return null;
                return (field.getAttribute('invalid') === 'true' ? '[invalid] ' : '') + textOf(field);
                """, label);
        if (text == null) {
            throw new NoSuchElementException("DSM field was not found: " + label);
        }
        return String.valueOf(text);
    }

    public String getDsmFieldValueByLabel(String label) {
        Object value = ((JavascriptExecutor) driver).executeScript(FIND_FIELD_JS + """
                const input = findField('dsm-input, dsm-textarea', arguments[0]);
                if (input) {
                    return input.shadowRoot?.querySelector('input, textarea')?.value
                        ?? input.getAttribute('value') ?? '';
                }
                const field = findField('dsm-select', arguments[0])
                    || findField('dsm-radio-button-group', arguments[0]);
                if (!field) return null;
                return field.getAttribute('value') ?? field.value ?? '';
                """, label);
        if (value == null) {
            throw new NoSuchElementException("DSM field was not found: " + label);
        }
        return String.valueOf(value);
    }

    public boolean isDsmFieldDisabledByLabel(String label) {
        Object disabled = ((JavascriptExecutor) driver).executeScript(FIND_FIELD_JS + """
                const field = ['dsm-select', 'dsm-input', 'dsm-radio-button-group']
                    .map((tag) => findField(tag, arguments[0]))
                    .find(Boolean);
                if (!field) return null;
                return field.hasAttribute('disabled') && field.getAttribute('disabled') !== 'false';
                """, label);
        if (disabled == null) {
            throw new NoSuchElementException("DSM field was not found: " + label);
        }
        return Boolean.TRUE.equals(disabled);
    }

    // Retorna o texto do dsm-modal aberto com o header informado, ou null se ele não estiver aberto
    public String getOpenDsmModalText(String header) {
        Object text = ((JavascriptExecutor) driver).executeScript("""
                const modal = [...document.querySelectorAll('dsm-modal')].find((element) =>
                    element.hasAttribute('open') && element.getAttribute('open') !== 'false'
                        && (element.getAttribute('header') || '').trim() === arguments[0].trim());
                return modal ? (modal.innerText || modal.textContent || '') : null;
                """, header);
        return text == null ? null : String.valueOf(text);
    }

    public void clickDsmModalButton(String xpath) {
        for (int attempt = 0; attempt < 20; attempt++) {
            try {
                WebElement host = driver.findElement(By.xpath(xpath));
                SearchContext shadowRoot = host.getShadowRoot();
                WebElement button = shadowRoot.findElement(By.cssSelector("button"));
                if (!host.isDisplayed() || !button.isDisplayed() || !button.isEnabled()) {
                    sleep(500);
                    continue;
                }
                ((JavascriptExecutor) driver).executeScript(
                        "arguments[0].scrollIntoView({block: 'center'});", button);
                try {
                    button.click();
                } catch (ElementClickInterceptedException exception) {
                    ((JavascriptExecutor) driver).executeScript(
                            "arguments[0].click();", button);
                }
                return;
            } catch (NoSuchElementException | StaleElementReferenceException exception) {
                sleep(500);
            }
        }
        throw new AssertionError(
                "The button was not clickable in the visible DSM modal: " + xpath);
    }

    private boolean isDownloadComplete(Path directory, String fileName) {
        if (!Files.isDirectory(directory)) {
            return false;
        }
        try (var files = Files.list(directory)) {
            String partialName = fileName.toLowerCase();
            return files.noneMatch(path -> {
                String name = path.getFileName().toString().toLowerCase();
                return name.equals(partialName + ".crdownload")
                        || name.equals(partialName + ".part")
                        || name.equals(partialName + ".tmp");
            });
        } catch (IOException exception) {
            throw new IllegalStateException("Could not inspect download directory: " + directory,
                    exception);
        }
    }

    private boolean fileHasContent(Path file) {
        try {
            return Files.size(file) > 0;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not inspect downloaded file: " + file,
                    exception);
        }
    }

    private long fileSize(Path file) {
        try {
            return Files.isRegularFile(file) ? Files.size(file) : -1;
        } catch (IOException exception) {
            throw new IllegalStateException("Could not inspect file size: " + file, exception);
        }
    }

    private FileTime fileLastModified(Path file) {
        try {
            return Files.isRegularFile(file)
                    ? Files.getLastModifiedTime(file)
                    : FileTime.fromMillis(0);
        } catch (IOException exception) {
            throw new IllegalStateException("Could not inspect file timestamp: " + file, exception);
        }
    }
}
