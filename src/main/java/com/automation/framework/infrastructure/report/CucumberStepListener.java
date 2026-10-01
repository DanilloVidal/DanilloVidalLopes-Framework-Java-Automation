package com.automation.framework.infrastructure.report;

import com.automation.framework.infrastructure.driver.DriverFactory;
import io.cucumber.plugin.EventListener;
import io.cucumber.plugin.event.*;
import org.openqa.selenium.WebDriver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CucumberStepListener implements EventListener {

    private static final ThreadLocal<PdfEvidenceManager> evidenceManagerContext = new ThreadLocal<>();
    private static final ThreadLocal<ScreenRecorderManager> screenRecorderContext = new ThreadLocal<>();
    private static final List<PdfEvidenceManager.ScenarioResult> scenarioResults =
            Collections.synchronizedList(new ArrayList<>());

    // --- DIAGNÓSTICO: Sinal de vida da classe ---
// Se o Cucumber estiver carregando este plugin, esta mensagem DEVE aparecer no console.
    public CucumberStepListener() {
        System.out.println("===================================================================");
        System.out.println("[DIAGNÓSTICO] CONSTRUTOR: CucumberStepListener foi instanciado.");
        System.out.println("===================================================================");
    }
// ---------------------------------------------

    // Pasta de evidência do cenário em execução (null se não houver cenário ativo)
    public static java.nio.file.Path currentEvidenceFolder() {
        PdfEvidenceManager manager = evidenceManagerContext.get();
        return manager == null ? null : manager.getReportPath();
    }

    @Override
    public void setEventPublisher(EventPublisher publisher) {
        publisher.registerHandlerFor(TestCaseStarted.class, this::handleTestCaseStarted);
        publisher.registerHandlerFor(TestStepFinished.class, this::handleTestStepFinished);
        publisher.registerHandlerFor(TestCaseFinished.class, this::handleTestCaseFinished);
        publisher.registerHandlerFor(TestRunFinished.class, this::handleTestRunFinished);
    }

    private void handleTestCaseStarted(TestCaseStarted event) {
        PdfEvidenceManager manager = new PdfEvidenceManager();
        evidenceManagerContext.set(manager);
        try {
            String scenarioName = event.getTestCase().getName();
            manager.startReport(scenarioName);
            System.out.println("[LISTENER] Relatório iniciado para o cenário: " + scenarioName);
        } catch (IOException e) {
            e.printStackTrace();
        }
        startScreenRecording(manager);
    }

    // Grava a tela do cenário na mesma pasta da evidência em PDF
    private void startScreenRecording(PdfEvidenceManager manager) {
        if (!ScreenRecorderManager.isEnabled() || manager.getReportPath() == null) {
            return;
        }
        ScreenRecorderManager recorder = new ScreenRecorderManager();
        try {
            recorder.start(manager.getReportPath(), "Test_Recording");
            screenRecorderContext.set(recorder);
            System.out.println("[LISTENER] Gravação de tela iniciada.");
        } catch (Exception e) {
            System.err.println("[LISTENER] Não foi possível iniciar a gravação de tela: " + e.getMessage());
        }
    }

    private void stopScreenRecording() {
        ScreenRecorderManager recorder = screenRecorderContext.get();
        if (recorder == null) {
            return;
        }
        try {
            System.out.println("[LISTENER] Gravação de tela salva em: " + recorder.stop());
        } catch (Exception e) {
            System.err.println("[LISTENER] Não foi possível finalizar a gravação de tela: " + e.getMessage());
        } finally {
            screenRecorderContext.remove();
        }
    }

    private void handleTestStepFinished(TestStepFinished event) {
        PdfEvidenceManager pdfManager = evidenceManagerContext.get();
        WebDriver driver = DriverFactory.get();

        if (event.getTestStep() instanceof PickleStepTestStep && pdfManager != null && driver != null) {
            PickleStepTestStep pickleStep = (PickleStepTestStep) event.getTestStep();
            String stepText = pickleStep.getStep().getKeyword() + pickleStep.getStep().getText();
            try {
                pdfManager.addScreenshot(driver, stepText,
                        event.getResult().getStatus(), event.getResult().getError());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void handleTestCaseFinished(TestCaseFinished event) {
        stopScreenRecording();
        PdfEvidenceManager pdfManager = evidenceManagerContext.get();
        if (pdfManager != null) {
            Status status = event.getResult().getStatus();
            String error = event.getResult().getError() == null
                    ? null : event.getResult().getError().toString();
            pdfManager.addFinalStatus(status);
            pdfManager.endReport();
            scenarioResults.add(new PdfEvidenceManager.ScenarioResult(
                    event.getTestCase().getName(), status, error,
                    featureName(event.getTestCase())));
            evidenceManagerContext.remove();
            System.out.println("[LISTENER] Relatório finalizado.");
        }
    }

    private void handleTestRunFinished(TestRunFinished event) {
        try {
            List<PdfEvidenceManager.ScenarioResult> results;
            synchronized (scenarioResults) {
                results = new ArrayList<>(scenarioResults);
            }
            PdfEvidenceManager.createSummaryReport(results);
            scenarioResults.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String featureName(TestCase testCase) {
        String uri = testCase.getUri() == null
                ? "feature"
                : testCase.getUri().toString();
        int end = uri.lastIndexOf('/');
        int start = uri.lastIndexOf('/', end - 1);
        if (end > start) {
            return uri.substring(start + 1, end);
        }
        return "feature";
    }
}