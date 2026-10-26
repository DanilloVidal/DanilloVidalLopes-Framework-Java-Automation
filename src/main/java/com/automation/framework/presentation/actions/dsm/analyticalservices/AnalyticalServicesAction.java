package com.automation.framework.presentation.actions.dsm.analyticalservices;

import com.automation.framework.presentation.actions.Actions;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

// Ações específicas das telas do Analytical Services.
// Ações genéricas (inclusive de componentes React Aria) ficam em Actions.
public class AnalyticalServicesAction extends Actions {

    // Título do passo atual do wizard (ex.: "Step 1: General sample information")
    private static final By STEP_TITLE = By.cssSelector("section h2");
    private static final By ANALYTE_ROWS = By.cssSelector("section table tbody tr");

    private final WebDriver driver;

    public AnalyticalServicesAction(WebDriver driver, int timeoutInSeconds) {
        super(driver, timeoutInSeconds);
        this.driver = driver;
    }

    // Botão que abre o select do campo; o campo é achado pelo name do <select>/<input> escondido
    public By fieldTrigger(String fieldName) {
        return By.xpath("//*[@name='" + fieldName + "']"
                + "/ancestor::div[contains(@class,'sci-select-input')][1]"
                + "//button[@aria-haspopup='listbox']");
    }

    public void selectFieldOption(String fieldName, String option) {
        selectReactAriaOption(fieldTrigger(fieldName), option);
    }

    public List<String> getFieldOptions(String fieldName) {
        return getReactAriaOptions(fieldTrigger(fieldName));
    }

    public String getFieldSelectedText(String fieldName) {
        return getReactAriaSelectedText(fieldTrigger(fieldName));
    }

    public boolean isFieldEnabled(String fieldName) {
        return isReactAriaFieldEnabled(fieldTrigger(fieldName));
    }

    public boolean isFieldDisplayed(String fieldName) {
        return !driver.findElements(By.name(fieldName)).isEmpty();
    }

    public void typeField(String fieldName, String text) {
        WebElement input = new WebDriverWait(driver, Duration.ofSeconds(20))
                .until(ExpectedConditions.elementToBeClickable(By.name(fieldName)));
        ((JavascriptExecutor) driver).executeScript(
                "arguments[0].scrollIntoView({block: 'center', behavior: 'instant'});", input);
        type(input, text);
    }

    // O date picker guarda o valor num <input type="date"> escondido (formato yyyy-mm-dd)
    public void setSamplingDate(String cssSelector, String isoDate) {
        setInputValue(By.cssSelector(cssSelector), isoDate);
        sleep(500);
    }

    public String getStepTitle() {
        return getText(STEP_TITLE).trim();
    }

    public void waitForStep(String stepPrefix) {
        new WebDriverWait(driver, Duration.ofSeconds(20)).until(d ->
                d.findElement(STEP_TITLE).getText().toUpperCase()
                        .startsWith(stepPrefix.toUpperCase()));
    }

    public boolean isTextDisplayed(String text) {
        return !driver.findElements(By.xpath(
                "//*[contains(normalize-space(text()), \"" + text + "\")]")).isEmpty();
    }

    public void clickWizardButton(String text) {
        closeReactAriaPopovers();
        clickByText(text);
        sleep(1500);
    }

    // ----- Step 3: tabela de analitos (colunas: checkbox, Analyte, Unit, Laboratory) -----

    public void waitForAnalyteTable() {
        new WebDriverWait(driver, Duration.ofSeconds(30)).until(d -> {
            List<WebElement> rows = d.findElements(ANALYTE_ROWS);
            return !rows.isEmpty() && rows.stream()
                    .allMatch(row -> row.findElements(By.tagName("td")).size() >= 4);
        });
    }

    // O mesmo analito pode existir em mais de um laboratório, por isso a linha é achada pelo par
    private By analyteRow(String analyte, String laboratory) {
        return By.xpath("//section//table/tbody/tr[normalize-space(td[2])='" + analyte
                + "' and normalize-space(td[4])='" + laboratory + "']");
    }

    public void checkAnalyte(String analyte, String laboratory) {
        WebElement row = findElement(analyteRow(analyte, laboratory));
        WebElement checkbox = row.findElement(By.cssSelector("input[type='checkbox']"));
        if (!checkbox.isSelected()) {
            click(row.findElement(By.cssSelector("td:first-child label")));
            sleep(500);
        }
    }

    public boolean isAnalyteChecked(String analyte, String laboratory) {
        return findElement(analyteRow(analyte, laboratory))
                .findElement(By.cssSelector("input[type='checkbox']")).isSelected();
    }

    public boolean isAnalyteEnabled(String analyte, String laboratory) {
        return findElement(analyteRow(analyte, laboratory))
                .findElement(By.cssSelector("input[type='checkbox']")).isEnabled();
    }

    public void selectAnalyteUnit(String analyte, String laboratory, String unit) {
        By trigger = By.xpath("//section//table/tbody/tr[normalize-space(td[2])='" + analyte
                + "' and normalize-space(td[4])='" + laboratory + "']"
                + "//button[@aria-haspopup='listbox']");
        selectReactAriaOption(trigger, unit);
    }

    // Lista "Laboratory | Analyte | unidade selecionada" de todas as linhas da tabela
    public List<String> getAnalyteRows() {
        return driver.findElements(ANALYTE_ROWS).stream().map(row -> {
            List<WebElement> cells = row.findElements(By.tagName("td"));
            String unit = cells.get(2).getText().split("\\R")[0].trim();
            return cells.get(3).getText().trim() + " | " + cells.get(1).getText().trim()
                    + " | " + unit;
        }).toList();
    }
}
