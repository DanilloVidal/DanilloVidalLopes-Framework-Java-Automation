package com.automation.framework.presentation.actions.dsm.pigmentation;

import com.automation.framework.presentation.actions.Actions;
import org.openqa.selenium.WebDriver;

// Ações específicas das telas do Pigmentation calculator.
// Ações genéricas (inclusive de componentes React Aria) ficam em Actions.
public class PigmentationAction extends Actions {

    public PigmentationAction(WebDriver driver, int timeoutInSeconds) {
        super(driver, timeoutInSeconds);
    }
}
