package hms.ui;

import hms.model.Appointment;
import hms.model.Doctor;
import hms.model.Patient;
import hms.service.DoctorService;
import hms.service.PatientService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AppointmentManagementPanel extends JPanel {
    private JTable appointmentTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    private JButton refreshButton;

    private PatientService patientService;
    private DoctorService doctorService;

    public AppointmentManagementPanel() {
        patientService = new PatientService();
        doctorService = new DoctorService();
        initializeUI();
        loadAppointments();
    }

    private void initializeUI() {
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // Create top panel with search and buttons
        JPanel topPanel = new JPanel(new BorderLayout());

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel searchLabel = new JLabel("Search:");
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Buttons panel
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        addButton = new JButton("Book Appointment");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Cancel Appointment");
        viewButton = new JButton("View Details");
        refreshButton = new JButton("Refresh");

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(viewButton);
        buttonsPanel.add(refreshButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonsPanel, BorderLayout.EAST);

        // Create table
        String[] columns = {"Appointment ID", "Patient", "Doctor", "Date", "Time", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        appointmentTable = new JTable(tableModel);
        appointmentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        appointmentTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(appointmentTable);

        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        searchButton.addActionListener(e -> searchAppointments());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchAppointments();
                }
            }
        });

        addButton.addActionListener(e -> showBookAppointmentDialog());
        editButton.addActionListener(e -> showEditAppointmentDialog());
        deleteButton.addActionListener(e -> cancelAppointment());
        viewButton.addActionListener(e -> viewAppointmentDetails());
        refreshButton.addActionListener(e -> loadAppointments());
    }

    private void loadAppointments() {
        // Clear table
        tableModel.setRowCount(0);

        // Add sample data for now
        // In a real implementation, this would load from a service
        addSampleAppointments();
    }

    private void addSampleAppointments() {
        // Add some sample appointments for demonstration
        Object[] row1 = {"A001", "John Doe", "Dr. Smith", "2025-06-15", "10:00 AM", "SCHEDULED"};
        Object[] row2 = {"A002", "Jane Smith", "Dr. Johnson", "2025-06-16", "11:30 AM", "SCHEDULED"};
        Object[] row3 = {"A003", "Robert Brown", "Dr. Williams", "2025-06-14", "09:15 AM", "COMPLETED"};
        Object[] row4 = {"A004", "Emily Davis", "Dr. Smith", "2025-06-13", "02:00 PM", "CANCELLED"};

        tableModel.addRow(row1);
        tableModel.addRow(row2);
        tableModel.addRow(row3);
        tableModel.addRow(row4);
    }

    private void searchAppointments() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            loadAppointments();
            return;
        }

        // Clear table
        tableModel.setRowCount(0);

        // In a real implementation, this would search from a service
        // For now, just reload all and filter
        addSampleAppointments();

        // Filter rows
        for (int i = tableModel.getRowCount() - 1; i >= 0; i--) {
            boolean match = false;
            for (int j = 0; j < tableModel.getColumnCount(); j++) {
                Object value = tableModel.getValueAt(i, j);
                if (value != null && value.toString().toLowerCase().contains(query)) {
                    match = true;
                    break;
                }
            }
            if (!match) {
                tableModel.removeRow(i);
            }
        }
    }

    private void showBookAppointmentDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Book Appointment", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Generate appointment ID
        String appointmentId = "A" + System.currentTimeMillis();

        JLabel idLabel = new JLabel("Appointment ID:");
        JTextField idField = new JTextField(appointmentId);
        idField.setEditable(false);

        JLabel patientLabel = new JLabel("Patient:");
        JComboBox<String> patientComboBox = new JComboBox<>();

        // Load patients
        List<Patient> patients = patientService.getAll();
        for (Patient patient : patients) {
            patientComboBox.addItem(patient.getId() + " - " + patient.getName());
        }

        JLabel doctorLabel = new JLabel("Doctor:");
        JComboBox<String> doctorComboBox = new JComboBox<>();

        // Load doctors
        List<Doctor> doctors = doctorService.getAll();
        for (Doctor doctor : doctors) {
            doctorComboBox.addItem(doctor.getId() + " - " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
        }

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        JTextField dateField = new JTextField();

        JLabel timeLabel = new JLabel("Time:");
        JComboBox<String> timeComboBox = new JComboBox<>(new String[]{
                "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
                "12:00 PM", "12:30 PM", "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM",
                "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM", "05:00 PM"
        });

        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"SCHEDULED", "COMPLETED", "CANCELLED"});

        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField();

        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(patientLabel);
        formPanel.add(patientComboBox);
        formPanel.add(doctorLabel);
        formPanel.add(doctorComboBox);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(timeLabel);
        formPanel.add(timeComboBox);
        formPanel.add(statusLabel);
        formPanel.add(statusComboBox);
        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (patientComboBox.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Please select a patient", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (doctorComboBox.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Please select a doctor", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String dateText = dateField.getText().trim();
                if (dateText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Date cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate date format
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdf.setLenient(false);
                    Date date = sdf.parse(dateText);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid date format. Please use YYYY-MM-DD", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // In a real implementation, this would save to a service
                // For now, just add to the table
                String patientInfo = (String) patientComboBox.getSelectedItem();
                String doctorInfo = (String) doctorComboBox.getSelectedItem();

                String patientName = patientInfo.substring(patientInfo.indexOf(" - ") + 3);
                String doctorName = doctorInfo.substring(doctorInfo.indexOf(" - ") + 3, doctorInfo.indexOf(" ("));

                Object[] row = {
                        idField.getText(),
                        patientName,
                        doctorName,
                        dateField.getText(),
                        timeComboBox.getSelectedItem(),
                        statusComboBox.getSelectedItem()
                };

                tableModel.addRow(row);

                JOptionPane.showMessageDialog(dialog, "Appointment booked successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showEditAppointmentDialog() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Appointment", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String appointmentId = (String) appointmentTable.getValueAt(selectedRow, 0);
        String patientName = (String) appointmentTable.getValueAt(selectedRow, 1);
        String doctorName = (String) appointmentTable.getValueAt(selectedRow, 2);
        String date = (String) appointmentTable.getValueAt(selectedRow, 3);
        String time = (String) appointmentTable.getValueAt(selectedRow, 4);
        String status = (String) appointmentTable.getValueAt(selectedRow, 5);

        JLabel idLabel = new JLabel("Appointment ID:");
        JTextField idField = new JTextField(appointmentId);
        idField.setEditable(false);

        JLabel patientLabel = new JLabel("Patient:");
        JComboBox<String> patientComboBox = new JComboBox<>();

        // Load patients
        List<Patient> patients = patientService.getAll();
        for (Patient patient : patients) {
            patientComboBox.addItem(patient.getId() + " - " + patient.getName());
            if (patient.getName().equals(patientName)) {
                patientComboBox.setSelectedIndex(patientComboBox.getItemCount() - 1);
            }
        }

        JLabel doctorLabel = new JLabel("Doctor:");
        JComboBox<String> doctorComboBox = new JComboBox<>();

        // Load doctors
        List<Doctor> doctors = doctorService.getAll();
        for (Doctor doctor : doctors) {
            doctorComboBox.addItem(doctor.getId() + " - " + doctor.getName() + " (" + doctor.getSpecialization() + ")");
            if (doctor.getName().equals(doctorName)) {
                doctorComboBox.setSelectedIndex(doctorComboBox.getItemCount() - 1);
            }
        }

        JLabel dateLabel = new JLabel("Date (YYYY-MM-DD):");
        JTextField dateField = new JTextField(date);

        JLabel timeLabel = new JLabel("Time:");
        JComboBox<String> timeComboBox = new JComboBox<>(new String[]{
                "09:00 AM", "09:30 AM", "10:00 AM", "10:30 AM", "11:00 AM", "11:30 AM",
                "12:00 PM", "12:30 PM", "01:00 PM", "01:30 PM", "02:00 PM", "02:30 PM",
                "03:00 PM", "03:30 PM", "04:00 PM", "04:30 PM", "05:00 PM"
        });
        timeComboBox.setSelectedItem(time);

        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"SCHEDULED", "COMPLETED", "CANCELLED"});
        statusComboBox.setSelectedItem(status);

        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField();

        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(patientLabel);
        formPanel.add(patientComboBox);
        formPanel.add(doctorLabel);
        formPanel.add(doctorComboBox);
        formPanel.add(dateLabel);
        formPanel.add(dateField);
        formPanel.add(timeLabel);
        formPanel.add(timeComboBox);
        formPanel.add(statusLabel);
        formPanel.add(statusComboBox);
        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (patientComboBox.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Please select a patient", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (doctorComboBox.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Please select a doctor", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String dateText = dateField.getText().trim();
                if (dateText.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Date cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Validate date format
                try {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    sdf.setLenient(false);
                    Date date2 = sdf.parse(dateText);
                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(dialog, "Invalid date format. Please use YYYY-MM-DD", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // In a real implementation, this would update in a service
                // For now, just update the table
                String patientInfo = (String) patientComboBox.getSelectedItem();
                String doctorInfo = (String) doctorComboBox.getSelectedItem();

                String newPatientName = patientInfo.substring(patientInfo.indexOf(" - ") + 3);
                String newDoctorName = doctorInfo.substring(doctorInfo.indexOf(" - ") + 3, doctorInfo.indexOf(" ("));

                appointmentTable.setValueAt(newPatientName, selectedRow, 1);
                appointmentTable.setValueAt(newDoctorName, selectedRow, 2);
                appointmentTable.setValueAt(dateField.getText(), selectedRow, 3);
                appointmentTable.setValueAt(timeComboBox.getSelectedItem(), selectedRow, 4);
                appointmentTable.setValueAt(statusComboBox.getSelectedItem(), selectedRow, 5);

                JOptionPane.showMessageDialog(dialog, "Appointment updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void cancelAppointment() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to cancel", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String appointmentId = (String) appointmentTable.getValueAt(selectedRow, 0);
        String patientName = (String) appointmentTable.getValueAt(selectedRow, 1);
        String doctorName = (String) appointmentTable.getValueAt(selectedRow, 2);
        String date = (String) appointmentTable.getValueAt(selectedRow, 3);

        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to cancel the appointment for " + patientName + " with " + doctorName + " on " + date + "?",
                "Confirm Cancellation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            // In a real implementation, this would update in a service
            // For now, just update the table
            appointmentTable.setValueAt("CANCELLED", selectedRow, 5);

            JOptionPane.showMessageDialog(this, "Appointment cancelled successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewAppointmentDetails() {
        int selectedRow = appointmentTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an appointment to view", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String appointmentId = (String) appointmentTable.getValueAt(selectedRow, 0);
        String patientName = (String) appointmentTable.getValueAt(selectedRow, 1);
        String doctorName = (String) appointmentTable.getValueAt(selectedRow, 2);
        String date = (String) appointmentTable.getValueAt(selectedRow, 3);
        String time = (String) appointmentTable.getValueAt(selectedRow, 4);
        String status = (String) appointmentTable.getValueAt(selectedRow, 5);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Appointment Details", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Appointment details
        JLabel titleLabel = new JLabel("Appointment Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addDetailRow(infoPanel, "Appointment ID:", appointmentId);
        addDetailRow(infoPanel, "Patient:", patientName);
        addDetailRow(infoPanel, "Doctor:", doctorName);
        addDetailRow(infoPanel, "Date:", date);
        addDetailRow(infoPanel, "Time:", time);
        addDetailRow(infoPanel, "Status:", status);

        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        detailsPanel.add(infoPanel);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("Close");

        buttonPanel.add(closeButton);

        dialog.add(detailsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        closeButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));

        JLabel valueComponent = new JLabel(value != null && !value.isEmpty() ? value : "N/A");
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(labelComponent);
        panel.add(valueComponent);
    }
}
