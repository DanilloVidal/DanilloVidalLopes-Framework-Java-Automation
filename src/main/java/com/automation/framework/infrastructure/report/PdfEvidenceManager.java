package com.automation.framework.infrastructure.report;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.properties.TextAlignment;
import io.cucumber.plugin.event.Status;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javax.imageio.ImageIO;

public class PdfEvidenceManager {

    private static final ThreadLocal<Document> pdfDocument = new ThreadLocal<>();
    private Path reportPath;

    public void startReport(String scenarioName) throws IOException {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String sanitizedScenarioName = scenarioName.replaceAll("[^a-zA-Z0-9]", "_");
        String folderName = sanitizedScenarioName + "_" + timestamp;

        reportPath = Paths.get("evidencia", folderName);
        Files.createDirectories(reportPath);
        Path pdfFile = reportPath.resolve("Test_Evidence.pdf");

        PdfWriter writer = new PdfWriter(pdfFile.toString());
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(new Paragraph("Test Evidence Report")
                .setBold().setFontSize(20).setTextAlignment(TextAlignment.CENTER));
        document.add(new Paragraph("Scenario: " + scenarioName)
                .setTextAlignment(TextAlignment.CENTER).setItalic());
        document.add(new Paragraph("Executed on: " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd 'at' HH:mm:ss")))
                .setTextAlignment(TextAlignment.CENTER).setFontSize(8));
        document.add(new Paragraph("\n"));

        pdfDocument.set(document);
    }

    // Pasta de evidência do cenário atual (usada também para salvar a gravação de tela)
    public Path getReportPath() {
        return reportPath;
    }

    public void addScreenshot(WebDriver driver, String stepText, Status status, Throwable error) {
        Document document = pdfDocument.get();
        if (document == null || driver == null) return;

        Paragraph stepParagraph = new Paragraph(stepText + " - " + status)
                .setBold();
        stepParagraph.setFontColor(status == Status.PASSED
                ? ColorConstants.GREEN : ColorConstants.RED);
        document.add(stepParagraph);

        if (error != null) {
            document.add(new Paragraph("Error: " + error.getMessage())
                    .setFontColor(ColorConstants.RED));
        }

        byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Image screenshotImage = new Image(ImageDataFactory.create(screenshot));
        screenshotImage.setAutoScale(true);

        document.add(screenshotImage);
        document.add(new Paragraph("\n"));
    }

    public void endReport() {
        Document document = pdfDocument.get();
        if (document != null) {
            document.close();
            pdfDocument.remove();
        }
    }

    public void addFinalStatus(Status status) {
        Document document = pdfDocument.get();
        if (document == null) return;

        document.add(new Paragraph("\n"));
        Paragraph statusParagraph = new Paragraph("Final Status: " + status.toString())
                .setBold().setFontSize(14).setTextAlignment(TextAlignment.CENTER);

        statusParagraph.setFontColor(status == Status.PASSED ? ColorConstants.GREEN : ColorConstants.RED);
        document.add(statusParagraph);
    }

    public static void createSummaryReport(List<ScenarioResult> results) throws IOException {
        if (results.isEmpty()) {
            return;
        }

        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"));
        String featureName = results.stream()
                .map(ScenarioResult::featureName)
                .distinct()
                .collect(Collectors.joining("_"));
        Path summaryPath = Paths.get("evidencia",
                sanitize(featureName) + "_execution_summary_" + timestamp);
        Files.createDirectories(summaryPath);

        Path chartPath = summaryPath.resolve("status_pie_chart.png");
        createPieChart(results, chartPath);

        Path pdfFile = summaryPath.resolve("Execution_Summary.pdf");
        try (PdfWriter writer = new PdfWriter(pdfFile.toString());
             PdfDocument pdf = new PdfDocument(writer);
             Document document = new Document(pdf)) {
            document.add(new Paragraph("Execution Summary")
                    .setBold().setFontSize(20)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Feature: " + featureName)
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("Executed on: "
                    + LocalDateTime.now().format(DateTimeFormatter.ofPattern(
                    "yyyy-MM-dd 'at' HH:mm:ss")))
                    .setTextAlignment(TextAlignment.CENTER));
            document.add(new Paragraph("\n"));
            document.add(new Image(ImageDataFactory.create(chartPath.toString()))
                    .setAutoScale(true));

            for (ScenarioResult result : results) {
                Paragraph scenario = new Paragraph(
                        result.scenarioName() + " - " + result.status())
                        .setBold();
                scenario.setFontColor(result.status() == Status.PASSED
                        ? ColorConstants.GREEN : ColorConstants.RED);
                document.add(scenario);
                if (result.error() != null) {
                    document.add(new Paragraph("Error: " + result.error())
                            .setFontColor(ColorConstants.RED));
                }
            }
        }
    }

    private static void createPieChart(List<ScenarioResult> results, Path chartPath)
            throws IOException {
        Map<Status, Long> counts = results.stream()
                .collect(Collectors.groupingBy(ScenarioResult::status, Collectors.counting()));
        int total = results.size();
        BufferedImage chart = new BufferedImage(800, 520, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = chart.createGraphics();
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, chart.getWidth(), chart.getHeight());

        int x = 80;
        int y = 70;
        int size = 300;
        int startAngle = 0;
        Color[] colors = {new Color(46, 125, 50), new Color(198, 40, 40),
                new Color(245, 124, 0), new Color(84, 110, 122)};
        int colorIndex = 0;

        for (Map.Entry<Status, Long> entry : counts.entrySet()) {
            int angle = (int) Math.round(entry.getValue() * 360.0 / total);
            graphics.setColor(colors[colorIndex++ % colors.length]);
            graphics.fillArc(x, y, size, size, startAngle, angle);
            startAngle += angle;
        }

        graphics.setColor(Color.DARK_GRAY);
        graphics.setStroke(new BasicStroke(2));
        graphics.drawOval(x, y, size, size);
        graphics.setFont(new Font("Arial", Font.PLAIN, 18));
        int legendY = 100;
        colorIndex = 0;
        for (Map.Entry<Status, Long> entry : counts.entrySet()) {
            graphics.setColor(colors[colorIndex++ % colors.length]);
            graphics.fillRect(450, legendY - 15, 18, 18);
            graphics.setColor(Color.DARK_GRAY);
            graphics.drawString(entry.getKey() + ": " + entry.getValue(),
                    480, legendY);
            legendY += 35;
        }
        graphics.dispose();
        ImageIO.write(chart, "png", chartPath.toFile());
    }

    private static String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9_-]", "_");
    }

    public record ScenarioResult(
            String scenarioName,
            Status status,
            String error,
            String featureName) {
    }
}