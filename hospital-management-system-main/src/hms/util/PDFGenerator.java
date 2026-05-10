package hms.util;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import hms.model.*;

public class PDFGenerator {

    private static final Font TITLE_FONT = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
    private static final Font SUBTITLE_FONT = new Font(Font.FontFamily.HELVETICA, 14, Font.BOLD);
    private static final Font NORMAL_FONT = new Font(Font.FontFamily.HELVETICA, 12, Font.NORMAL);
    private static final Font SMALL_FONT = new Font(Font.FontFamily.HELVETICA, 10, Font.NORMAL);

    // Generate patient report
    public static File generatePatientReport(Patient patient, List<MedicalRecord> records, String outputPath) {
        if (patient == null || outputPath == null || outputPath.trim().isEmpty()) {
            return null;
        }

        Document document = new Document();
        File file = new File(outputPath);

        try {
            // Ensure directory exists
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Add hospital header
            addHospitalHeader(document);

            // Add patient information
            document.add(new Paragraph("PATIENT REPORT", TITLE_FONT));
            document.add(new Paragraph(" "));

            // Patient details
            document.add(new Paragraph("Patient Information:", SUBTITLE_FONT));
            document.add(new Paragraph("ID: " + safeString(patient.getId()), NORMAL_FONT));
            document.add(new Paragraph("Name: " + safeString(patient.getName()), NORMAL_FONT));
            document.add(new Paragraph("Age: " + patient.getAge(), NORMAL_FONT));
            document.add(new Paragraph("Contact: " + safeString(patient.getContact()), NORMAL_FONT));
            document.add(new Paragraph("Email: " + safeString(patient.getEmail()), NORMAL_FONT));
            document.add(new Paragraph("Address: " + safeString(patient.getAddress()), NORMAL_FONT));
            document.add(new Paragraph("Gender: " + safeString(patient.getGender()), NORMAL_FONT));
            document.add(new Paragraph("Disease: " + safeString(patient.getDisease()), NORMAL_FONT));
            document.add(new Paragraph("Blood Group: " + safeString(patient.getBloodGroup()), NORMAL_FONT));
            document.add(new Paragraph("Allergies: " + safeString(patient.getAllergies()), NORMAL_FONT));
            document.add(new Paragraph(" "));

            // Medical records
            if (records != null && !records.isEmpty()) {
                document.add(new Paragraph("Medical History:", SUBTITLE_FONT));
                document.add(new Paragraph(" "));

                for (MedicalRecord record : records) {
                    if (record != null) {
                        document.add(new Paragraph("Date: " + formatDate(record.getRecordDate()), NORMAL_FONT));
                        document.add(new Paragraph("Doctor: " + safeString(record.getDoctorId()), NORMAL_FONT));
                        document.add(new Paragraph("Diagnosis: " + safeString(record.getDiagnosis()), NORMAL_FONT));
                        document.add(new Paragraph("Treatment: " + safeString(record.getTreatment()), NORMAL_FONT));
                        document.add(new Paragraph("Notes: " + safeString(record.getNotes()), NORMAL_FONT));
                        document.add(new Paragraph(" "));
                    }
                }
            } else {
                document.add(new Paragraph("No medical records found.", NORMAL_FONT));
            }

            // Add footer
            addFooter(document);

            document.close();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            if (document.isOpen()) {
                document.close();
            }
            return null;
        }
    }

    // Generate billing report
    public static File generateBillingReport(Billing billing, Patient patient, String outputPath) {
        if (billing == null || patient == null || outputPath == null || outputPath.trim().isEmpty()) {
            return null;
        }

        Document document = new Document();
        File file = new File(outputPath);

        try {
            // Ensure directory exists
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }

            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Add hospital header
            addHospitalHeader(document);

            // Add invoice header
            document.add(new Paragraph("INVOICE", TITLE_FONT));
            document.add(new Paragraph(" "));

            // Add billing information
            document.add(new Paragraph("Invoice #: " + safeString(billing.getBillId()), NORMAL_FONT));
            document.add(new Paragraph("Date: " + formatDate(billing.getBillDate()), NORMAL_FONT));
            document.add(new Paragraph("Payment Status: " + safeString(billing.getPaymentStatus()), NORMAL_FONT));
            document.add(new Paragraph(" "));

            // Add patient information
            document.add(new Paragraph("Patient Information:", SUBTITLE_FONT));
            document.add(new Paragraph("ID: " + safeString(patient.getId()), NORMAL_FONT));
            document.add(new Paragraph("Name: " + safeString(patient.getName()), NORMAL_FONT));
            document.add(new Paragraph("Contact: " + safeString(patient.getContact()), NORMAL_FONT));
            document.add(new Paragraph(" "));

            // Add billing items
            document.add(new Paragraph("Billing Details:", SUBTITLE_FONT));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            // Add table headers
            addTableHeader(table, new String[]{"Description", "Quantity", "Unit Price", "Amount"});

            // Add billing items
            double subtotal = 0;
            List<Billing.BillItem> items = billing.getItems();
            if (items != null && !items.isEmpty()) {
                for (Billing.BillItem item : items) {
                    if (item != null) {
                        table.addCell(new Phrase(safeString(item.getDescription()), SMALL_FONT));
                        table.addCell(new Phrase(String.valueOf(item.getQuantity()), SMALL_FONT));
                        table.addCell(new Phrase(String.format("$%.2f", item.getUnitPrice()), SMALL_FONT));
                        table.addCell(new Phrase(String.format("$%.2f", item.getAmount()), SMALL_FONT));
                        subtotal += item.getAmount();
                    }
                }
            } else {
                // Add sample items if none exist
                table.addCell(new Phrase("Consultation Fee", SMALL_FONT));
                table.addCell(new Phrase("1", SMALL_FONT));
                table.addCell(new Phrase(String.format("$%.2f", billing.getTotalAmount()), SMALL_FONT));
                table.addCell(new Phrase(String.format("$%.2f", billing.getTotalAmount()), SMALL_FONT));
                subtotal = billing.getTotalAmount();
            }

            document.add(table);
            document.add(new Paragraph(" "));

            // Add summary
            PdfPTable summaryTable = new PdfPTable(2);
            summaryTable.setWidthPercentage(50);
            summaryTable.setHorizontalAlignment(Element.ALIGN_RIGHT);

            summaryTable.addCell(new Phrase("Subtotal:", NORMAL_FONT));
            summaryTable.addCell(new Phrase(String.format("$%.2f", subtotal), NORMAL_FONT));

            double discountAmount = subtotal * (billing.getDiscount() / 100.0);
            summaryTable.addCell(new Phrase("Discount (" + billing.getDiscount() + "%):", NORMAL_FONT));
            summaryTable.addCell(new Phrase(String.format("$%.2f", discountAmount), NORMAL_FONT));

            double taxAmount = subtotal * (billing.getTax() / 100.0);
            summaryTable.addCell(new Phrase("Tax (" + billing.getTax() + "%):", NORMAL_FONT));
            summaryTable.addCell(new Phrase(String.format("$%.2f", taxAmount), NORMAL_FONT));

            PdfPCell totalCell = new PdfPCell(new Phrase("Total:", SUBTITLE_FONT));
            totalCell.setBorder(0);
            summaryTable.addCell(totalCell);

            PdfPCell totalAmountCell = new PdfPCell(new Phrase(String.format("$%.2f", billing.getTotalAmount()), SUBTITLE_FONT));
            totalAmountCell.setBorder(0);
            summaryTable.addCell(totalAmountCell);

            document.add(summaryTable);
            document.add(new Paragraph(" "));

            // Add payment information
            if (billing.getPaymentMethod() != null && !billing.getPaymentMethod().isEmpty()) {
                document.add(new Paragraph("Payment Method: " + billing.getPaymentMethod(), NORMAL_FONT));
            }

            // Add footer
            addFooter(document);

            document.close();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            if (document.isOpen()) {
                document.close();
            }
            return null;
        }
    }

    // Generate prescription
    public static File generatePrescription(Prescription prescription, Patient patient, Doctor doctor, String outputPath) {
        Document document = new Document();
        File file = new File(outputPath);

        try {
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();

            // Add hospital header
            addHospitalHeader(document);

            // Add prescription header
            document.add(new Paragraph("PRESCRIPTION", TITLE_FONT));
            document.add(new Paragraph(" "));

            // Add prescription information
            document.add(new Paragraph("Prescription #: " + prescription.getPrescriptionId(), NORMAL_FONT));
            document.add(new Paragraph("Date: " + formatDate(prescription.getIssueDate()), NORMAL_FONT));
            document.add(new Paragraph(" "));

            // Add patient information
            document.add(new Paragraph("Patient Information:", SUBTITLE_FONT));
            document.add(new Paragraph("Name: " + patient.getName(), NORMAL_FONT));
            document.add(new Paragraph("ID: " + patient.getId(), NORMAL_FONT));
            document.add(new Paragraph("Age: " + patient.getAge(), NORMAL_FONT));
            document.add(new Paragraph(" "));

            // Add doctor information
            document.add(new Paragraph("Doctor Information:", SUBTITLE_FONT));
            document.add(new Paragraph("Name: " + doctor.getName(), NORMAL_FONT));
            document.add(new Paragraph("Specialization: " + doctor.getSpecialization(), NORMAL_FONT));
            document.add(new Paragraph(" "));

            // Add medicines
            document.add(new Paragraph("Medicines:", SUBTITLE_FONT));
            document.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);

            // Add table headers
            addTableHeader(table, new String[]{"Medicine", "Dosage", "Frequency", "Duration"});

            // Add medicines
            for (Medicine medicine : prescription.getMedicines()) {
                table.addCell(new Phrase(medicine.getName(), SMALL_FONT));
                table.addCell(new Phrase(medicine.getDosage(), SMALL_FONT));
                table.addCell(new Phrase(medicine.getFrequency(), SMALL_FONT));
                table.addCell(new Phrase(medicine.getDuration(), SMALL_FONT));
            }

            document.add(table);
            document.add(new Paragraph(" "));

            // Add notes
            if (prescription.getNotes() != null && !prescription.getNotes().isEmpty()) {
                document.add(new Paragraph("Notes:", SUBTITLE_FONT));
                document.add(new Paragraph(prescription.getNotes(), NORMAL_FONT));
                document.add(new Paragraph(" "));
            }

            // Add doctor's signature
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("____________________", NORMAL_FONT));
            document.add(new Paragraph("Doctor's Signature", NORMAL_FONT));

            // Add footer
            addFooter(document);

            document.close();
            return file;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Helper methods
    private static void addHospitalHeader(Document document) throws DocumentException {
        Paragraph header = new Paragraph("ZM HOSPITAL", TITLE_FONT);
        header.setAlignment(Element.ALIGN_CENTER);
        document.add(header);

        Paragraph address = new Paragraph("123 Medical City, Lahore, Pakistan", NORMAL_FONT);
        address.setAlignment(Element.ALIGN_CENTER);
        document.add(address);

        Paragraph contact = new Paragraph("Phone: +92-42-123-4567 | Email: info@zmhospital.com", NORMAL_FONT);
        contact.setAlignment(Element.ALIGN_CENTER);
        document.add(contact);

        document.add(new Paragraph(" "));
        document.add(new Paragraph("------------------------------------------------------------", NORMAL_FONT));
        document.add(new Paragraph(" "));
    }

    private static void addFooter(Document document) throws DocumentException {
        document.add(new Paragraph(" "));
        document.add(new Paragraph("------------------------------------------------------------", NORMAL_FONT));
        document.add(new Paragraph(" "));

        Paragraph footer = new Paragraph("This is a computer-generated document. No signature is required.", SMALL_FONT);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);

        Paragraph date = new Paragraph("Generated on: " + formatDate(new Date()), SMALL_FONT);
        date.setAlignment(Element.ALIGN_CENTER);
        document.add(date);
    }

    private static void addTableHeader(PdfPTable table, String[] headers) {
        for (String header : headers) {
            PdfPCell cell = new PdfPCell(new Phrase(safeString(header), SUBTITLE_FONT));
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            table.addCell(cell);
        }
    }

    private static String formatDate(Date date) {
        if (date == null) return "N/A";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        return sdf.format(date);
    }

    private static String safeString(String str) {
        return str != null && !str.trim().isEmpty() ? str : "N/A";
    }
}
