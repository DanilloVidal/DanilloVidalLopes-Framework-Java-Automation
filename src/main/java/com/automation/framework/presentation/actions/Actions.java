package com.automation.framework.presentation.actions;

import com.automation.framework.infrastructure.wrappers.WebActions;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementClickInterceptedException;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
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
                .filter(WebElement::isDisplayed)
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
                .filter(WebElement::isDisplayed)
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
