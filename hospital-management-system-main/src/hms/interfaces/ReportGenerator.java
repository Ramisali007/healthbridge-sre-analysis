package hms.interfaces;

import java.io.File;

/**
 * Interface for report generation
 */
public interface ReportGenerator<T> {
    // Generate a report as a string
    String generateReport(T data);

    // Generate and save a report to a file
    boolean generateAndSaveReport(T data, String outputPath);

    // Generate a PDF report
    File generatePdfReport(T data, String outputPath);
}
