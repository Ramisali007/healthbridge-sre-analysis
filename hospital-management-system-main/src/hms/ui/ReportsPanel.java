package hms.ui;

import hms.model.Patient;
import hms.service.PatientService;
import hms.util.PDFGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ReportsPanel extends JPanel {
    private JComboBox<String> reportTypeComboBox;
    private JPanel parametersPanel;
    private JButton generateButton;

    private PatientService patientService;

    public ReportsPanel() {
        patientService = new PatientService();
        initializeUI();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create top panel with report type selection
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JLabel reportTypeLabel = new JLabel("Report Type:");
        reportTypeComboBox = new JComboBox<>(new String[]{
                "Patient Report",
                "Doctor Schedule",
                "Appointment Summary",
                "Billing Summary",
                "Inventory Status",
                "Revenue Report"
        });

        topPanel.add(reportTypeLabel);
        topPanel.add(reportTypeComboBox);

        // Create parameters panel
        parametersPanel = new JPanel();
        parametersPanel.setLayout(new BoxLayout(parametersPanel, BoxLayout.Y_AXIS));
        parametersPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Create button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        generateButton = new JButton("Generate Report");
        buttonPanel.add(generateButton);

        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(parametersPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        reportTypeComboBox.addActionListener(e -> updateParametersPanel());
        generateButton.addActionListener(e -> generateReport());

        // Initialize parameters panel
        updateParametersPanel();
    }

    private void updateParametersPanel() {
        parametersPanel.removeAll();

        String reportType = (String) reportTypeComboBox.getSelectedItem();

        JLabel titleLabel = new JLabel(reportType);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        parametersPanel.add(titleLabel);
        parametersPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        switch (reportType) {
            case "Patient Report":
                setupPatientReportParameters(formPanel);
                break;
            case "Doctor Schedule":
                setupDoctorScheduleParameters(formPanel);
                break;
            case "Appointment Summary":
                setupAppointmentSummaryParameters(formPanel);
                break;
            case "Billing Summary":
                setupBillingSummaryParameters(formPanel);
                break;
            case "Inventory Status":
                setupInventoryStatusParameters(formPanel);
                break;
            case "Revenue Report":
                setupRevenueReportParameters(formPanel);
                break;
        }

        parametersPanel.add(formPanel);

        // Add description
        JLabel descriptionLabel = new JLabel("Description:");
        descriptionLabel.setFont(new Font("Arial", Font.BOLD, 14));
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea descriptionArea = new JTextArea(getReportDescription(reportType));
        descriptionArea.setEditable(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setBackground(getBackground());
        descriptionArea.setFont(new Font("Arial", Font.PLAIN, 12));
        descriptionArea.setAlignmentX(Component.LEFT_ALIGNMENT);

        parametersPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        parametersPanel.add(descriptionLabel);
        parametersPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        parametersPanel.add(descriptionArea);

        parametersPanel.revalidate();
        parametersPanel.repaint();
    }

    private void setupPatientReportParameters(JPanel formPanel) {
        JLabel patientLabel = new JLabel("Patient:");
        JComboBox<String> patientComboBox = new JComboBox<>();
        patientComboBox.setName("patientComboBox");

        // Load patients
        java.util.List<Patient> patients = patientService.getAll();
        for (Patient patient : patients) {
            patientComboBox.addItem(patient.getId() + " - " + patient.getName());
        }

        JLabel includeLabel = new JLabel("Include:");
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JCheckBox medicalRecordsCheckbox = new JCheckBox("Medical Records");
        medicalRecordsCheckbox.setName("medicalRecordsCheckbox");
        medicalRecordsCheckbox.setSelected(true);

        JCheckBox appointmentsCheckbox = new JCheckBox("Appointments");
        appointmentsCheckbox.setName("appointmentsCheckbox");
        appointmentsCheckbox.setSelected(true);

        JCheckBox billingsCheckbox = new JCheckBox("Billings");
        billingsCheckbox.setName("billingsCheckbox");
        billingsCheckbox.setSelected(true);

        checkboxPanel.add(medicalRecordsCheckbox);
        checkboxPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        checkboxPanel.add(appointmentsCheckbox);
        checkboxPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        checkboxPanel.add(billingsCheckbox);

        formPanel.add(patientLabel);
        formPanel.add(patientComboBox);
        formPanel.add(includeLabel);
        formPanel.add(checkboxPanel);
    }

    private void setupDoctorScheduleParameters(JPanel formPanel) {
        JLabel doctorLabel = new JLabel("Doctor:");
        JComboBox<String> doctorComboBox = new JComboBox<>();
        doctorComboBox.setName("doctorComboBox");

        // Add sample doctors
        doctorComboBox.addItem("D001 - Dr. Smith");
        doctorComboBox.addItem("D002 - Dr. Johnson");
        doctorComboBox.addItem("D003 - Dr. Williams");

        JLabel dateRangeLabel = new JLabel("Date Range:");
        JPanel dateRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JTextField startDateField = new JTextField(10);
        startDateField.setName("startDateField");
        startDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        JLabel toLabel = new JLabel(" to ");

        JTextField endDateField = new JTextField(10);
        endDateField.setName("endDateField");
        // Set end date to 7 days from now
        Date endDate = new Date();
        endDate.setTime(endDate.getTime() + 7 * 24 * 60 * 60 * 1000);
        endDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(endDate));

        dateRangePanel.add(startDateField);
        dateRangePanel.add(toLabel);
        dateRangePanel.add(endDateField);

        formPanel.add(doctorLabel);
        formPanel.add(doctorComboBox);
        formPanel.add(dateRangeLabel);
        formPanel.add(dateRangePanel);
    }

    private void setupAppointmentSummaryParameters(JPanel formPanel) {
        JLabel dateRangeLabel = new JLabel("Date Range:");
        JPanel dateRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JTextField startDateField = new JTextField(10);
        startDateField.setName("startDateField");
        // Set start date to 30 days ago
        Date startDate = new Date();
        startDate.setTime(startDate.getTime() - 30 * 24 * 60 * 60 * 1000);
        startDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(startDate));

        JLabel toLabel = new JLabel(" to ");

        JTextField endDateField = new JTextField(10);
        endDateField.setName("endDateField");
        endDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        dateRangePanel.add(startDateField);
        dateRangePanel.add(toLabel);
        dateRangePanel.add(endDateField);

        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{
                "All", "SCHEDULED", "COMPLETED", "CANCELLED"
        });
        statusComboBox.setName("statusComboBox");

        JLabel groupByLabel = new JLabel("Group By:");
        JComboBox<String> groupByComboBox = new JComboBox<>(new String[]{
                "Date", "Doctor", "Status"
        });
        groupByComboBox.setName("groupByComboBox");

        formPanel.add(dateRangeLabel);
        formPanel.add(dateRangePanel);
        formPanel.add(statusLabel);
        formPanel.add(statusComboBox);
        formPanel.add(groupByLabel);
        formPanel.add(groupByComboBox);
    }

    private void setupBillingSummaryParameters(JPanel formPanel) {
        JLabel dateRangeLabel = new JLabel("Date Range:");
        JPanel dateRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JTextField startDateField = new JTextField(10);
        startDateField.setName("startDateField");
        // Set start date to 30 days ago
        Date startDate = new Date();
        startDate.setTime(startDate.getTime() - 30 * 24 * 60 * 60 * 1000);
        startDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(startDate));

        JLabel toLabel = new JLabel(" to ");

        JTextField endDateField = new JTextField(10);
        endDateField.setName("endDateField");
        endDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

        dateRangePanel.add(startDateField);
        dateRangePanel.add(toLabel);
        dateRangePanel.add(endDateField);

        JLabel statusLabel = new JLabel("Payment Status:");
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{
                "All", "PAID", "UNPAID"
        });
        statusComboBox.setName("statusComboBox");

        JLabel groupByLabel = new JLabel("Group By:");
        JComboBox<String> groupByComboBox = new JComboBox<>(new String[]{
                "Date", "Patient", "Status"
        });
        groupByComboBox.setName("groupByComboBox");

        formPanel.add(dateRangeLabel);
        formPanel.add(dateRangePanel);
        formPanel.add(statusLabel);
        formPanel.add(statusComboBox);
        formPanel.add(groupByLabel);
        formPanel.add(groupByComboBox);
    }

    private void setupInventoryStatusParameters(JPanel formPanel) {
        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{
                "All", "Pain Relief", "Antibiotics", "Antihistamine", "Cardiovascular",
                "Gastrointestinal", "Respiratory", "Dermatological", "Other"
        });
        categoryComboBox.setName("categoryComboBox");

        JLabel stockStatusLabel = new JLabel("Stock Status:");
        JComboBox<String> stockStatusComboBox = new JComboBox<>(new String[]{
                "All", "In Stock", "Low Stock", "Out of Stock"
        });
        stockStatusComboBox.setName("stockStatusComboBox");

        JLabel sortByLabel = new JLabel("Sort By:");
        JComboBox<String> sortByComboBox = new JComboBox<>(new String[]{
                "Name", "Category", "Quantity", "Price"
        });
        sortByComboBox.setName("sortByComboBox");

        formPanel.add(categoryLabel);
        formPanel.add(categoryComboBox);
        formPanel.add(stockStatusLabel);
        formPanel.add(stockStatusComboBox);
        formPanel.add(sortByLabel);
        formPanel.add(sortByComboBox);
    }

    private void setupRevenueReportParameters(JPanel formPanel) {
        JLabel dateRangeLabel = new JLabel("Date Range:");
        JPanel dateRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JTextField startDateField = new JTextField(10);
        startDateField.setName("startDateField");
        // Set start date to first day of current month
        Date now = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String currentMonth = sdf.format(now).substring(0, 8) + "01";
        startDateField.setText(currentMonth);

        JLabel toLabel = new JLabel(" to ");

        JTextField endDateField = new JTextField(10);
        endDateField.setName("endDateField");
        endDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(now));

        dateRangePanel.add(startDateField);
        dateRangePanel.add(toLabel);
        dateRangePanel.add(endDateField);

        JLabel groupByLabel = new JLabel("Group By:");
        JComboBox<String> groupByComboBox = new JComboBox<>(new String[]{
                "Day", "Week", "Month"
        });
        groupByComboBox.setName("groupByComboBox");

        JLabel includeLabel = new JLabel("Include:");
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));

        JCheckBox consultationsCheckbox = new JCheckBox("Consultations");
        consultationsCheckbox.setName("consultationsCheckbox");
        consultationsCheckbox.setSelected(true);

        JCheckBox medicinesCheckbox = new JCheckBox("Medicines");
        medicinesCheckbox.setName("medicinesCheckbox");
        medicinesCheckbox.setSelected(true);

        JCheckBox testsCheckbox = new JCheckBox("Tests");
        testsCheckbox.setName("testsCheckbox");
        testsCheckbox.setSelected(true);

        checkboxPanel.add(consultationsCheckbox);
        checkboxPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        checkboxPanel.add(medicinesCheckbox);
        checkboxPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        checkboxPanel.add(testsCheckbox);

        formPanel.add(dateRangeLabel);
        formPanel.add(dateRangePanel);
        formPanel.add(groupByLabel);
        formPanel.add(groupByComboBox);
        formPanel.add(includeLabel);
        formPanel.add(checkboxPanel);
    }

    private String getReportDescription(String reportType) {
        switch (reportType) {
            case "Patient Report":
                return "Generates a comprehensive report for a specific patient, including personal information, medical history, appointments, and billing information.";
            case "Doctor Schedule":
                return "Displays the schedule of a specific doctor for a given date range, including all appointments, available slots, and working hours.";
            case "Appointment Summary":
                return "Provides a summary of all appointments within a specified date range, with options to filter by status and group by different criteria.";
            case "Billing Summary":
                return "Generates a summary of all billing transactions within a specified date range, with options to filter by payment status and group by different criteria.";
            case "Inventory Status":
                return "Displays the current status of the medicine inventory, with options to filter by category and stock status, and sort by different criteria.";
            case "Revenue Report":
                return "Provides a detailed analysis of revenue generated within a specified date range, with options to group by time periods and include different revenue sources.";
            default:
                return "";
        }
    }

    private void generateReport() {
        String reportType = (String) reportTypeComboBox.getSelectedItem();

        try {
            switch (reportType) {
                case "Patient Report":
                    generatePatientReport();
                    break;
                case "Doctor Schedule":
                case "Appointment Summary":
                case "Billing Summary":
                case "Inventory Status":
                case "Revenue Report":
                    JOptionPane.showMessageDialog(this,
                            reportType + " generation will be implemented in the future",
                            "Information",
                            JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                    "Error generating report: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void generatePatientReport() {
        // Get selected patient
        JComboBox<?> patientComboBox = findComponentByName(parametersPanel, "patientComboBox");
        if (patientComboBox == null || patientComboBox.getSelectedIndex() == -1) {
            JOptionPane.showMessageDialog(this, "Please select a patient", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String patientInfo = (String) patientComboBox.getSelectedItem();
        String patientId = patientInfo.substring(0, patientInfo.indexOf(" - "));

        // Get patient object
        Patient patient = patientService.getById(patientId);
        if (patient == null) {
            JOptionPane.showMessageDialog(this, "Patient not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            String outputPath = "reports/patient_" + patient.getId() + ".pdf";
            File file = PDFGenerator.generatePatientReport(patient, null, outputPath);

            if (file != null && file.exists()) {
                int option = JOptionPane.showConfirmDialog(
                        this,
                        "Patient report generated successfully. Do you want to open it?",
                        "Report Generated",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.INFORMATION_MESSAGE
                );

                if (option == JOptionPane.YES_OPTION) {
                    // Open the PDF file with the default system viewer
                    if (Desktop.isDesktopSupported()) {
                        Desktop.getDesktop().open(file);
                    } else {
                        JOptionPane.showMessageDialog(this, "Cannot open PDF file automatically. File saved at: " + file.getAbsolutePath(), "Information", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to generate patient report", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating report: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private <T extends Component> T findComponentByName(Container container, String name) {
        for (Component component : container.getComponents()) {
            if (name.equals(component.getName())) {
                return (T) component;
            }

            if (component instanceof Container) {
                T found = findComponentByName((Container) component, name);
                if (found != null) {
                    return found;
                }
            }
        }

        return null;
    }
}
