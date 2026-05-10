package hms.ui;

import hms.model.Medicine;
import hms.service.MedicineService;
import hms.util.ValidationUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class PharmacyManagementPanel extends JPanel {
    private MedicineService medicineService;
    private JTable medicineTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    private JButton refreshButton;
    private JButton dispenseMedicineButton;

    private boolean samplesAdded = false; // Add this as a class field

    public PharmacyManagementPanel() {
        medicineService = new MedicineService();
        initializeUI();
        loadMedicines();
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
        addButton = new JButton("Add Medicine");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        viewButton = new JButton("View Details");
        refreshButton = new JButton("Refresh");
        dispenseMedicineButton = new JButton("Dispense Medicine");

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(viewButton);
        buttonsPanel.add(dispenseMedicineButton);
        buttonsPanel.add(refreshButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonsPanel, BorderLayout.EAST);

        // Create table
        String[] columns = {"ID", "Name", "Manufacturer", "Category", "Price", "Quantity"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        medicineTable = new JTable(tableModel);
        medicineTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        medicineTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(medicineTable);

        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        searchButton.addActionListener(e -> searchMedicines());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchMedicines();
                }
            }
        });

        addButton.addActionListener(e -> showAddMedicineDialog());
        editButton.addActionListener(e -> showEditMedicineDialog());
        deleteButton.addActionListener(e -> deleteMedicine());
        viewButton.addActionListener(e -> viewMedicineDetails());
        refreshButton.addActionListener(e -> loadMedicines());
        dispenseMedicineButton.addActionListener(e -> showDispenseMedicineDialog());
    }

    // Fix the sample data loading to prevent recursion
    private void loadMedicines() {
        // Clear table
        tableModel.setRowCount(0);

        try {
            // Load medicines from service
            List<Medicine> medicines = medicineService.getAll();
            System.out.println("Loaded " + medicines.size() + " medicines from service");

            // If no medicines exist, add samples first
            if (medicines.isEmpty() && !samplesAdded) {
                System.out.println("No medicines found, adding sample data...");
                addSampleMedicinesDirectly();
                medicines = medicineService.getAll(); // Reload after adding samples
                System.out.println("After adding samples: " + medicines.size() + " medicines");
            }

            // Add medicines to table
            for (Medicine medicine : medicines) {
                try {
                    Object[] row = {
                            medicine.getMedicineId(),
                            medicine.getName(),
                            medicine.getManufacturer() != null ? medicine.getManufacturer() : "N/A",
                            medicine.getCategory() != null ? medicine.getCategory() : "N/A",
                            String.format("%.2f", medicine.getPrice()),
                            medicine.getQuantity()
                    };
                    tableModel.addRow(row);
                } catch (Exception e) {
                    System.err.println("Error adding medicine to table: " + medicine.getMedicineId());
                    e.printStackTrace();
                }
            }

            System.out.println("Successfully loaded " + tableModel.getRowCount() + " medicines to table");

        } catch (Exception e) {
            System.err.println("Error in loadMedicines:");
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                    "Error loading medicines: " + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private void addSampleMedicinesDirectly() {
        if (samplesAdded) return;
        samplesAdded = true;

        // Add sample medicines directly to service
        Medicine m1 = new Medicine("M001", "Paracetamol", "ABC Pharma", 5.99, 100);
        m1.setCategory("Pain Relief");
        m1.setDescription("Pain relief medication");

        Medicine m2 = new Medicine("M002", "Amoxicillin", "XYZ Pharma", 12.50, 50);
        m2.setCategory("Antibiotics");
        m2.setDescription("Antibiotic medication");

        Medicine m3 = new Medicine("M003", "Ibuprofen", "MNO Pharma", 7.25, 75);
        m3.setCategory("Pain Relief");
        m3.setDescription("Anti-inflammatory medication");

        Medicine m4 = new Medicine("M004", "Cetirizine", "PQR Pharma", 8.99, 60);
        m4.setCategory("Antihistamine");
        m4.setDescription("Allergy medication");

        // Add to service
        medicineService.add(m1);
        medicineService.add(m2);
        medicineService.add(m3);
        medicineService.add(m4);
    }

    private void searchMedicines() {
        String query = searchField.getText().trim();

        // Clear table
        tableModel.setRowCount(0);

        // Search medicines
        List<Medicine> medicines = medicineService.search(query);

        // Add medicines to table
        for (Medicine medicine : medicines) {
            Object[] row = {
                    medicine.getMedicineId(),
                    medicine.getName(),
                    medicine.getManufacturer() != null ? medicine.getManufacturer() : "N/A",
                    medicine.getCategory() != null ? medicine.getCategory() : "N/A",
                    String.format("%.2f", medicine.getPrice()),
                    medicine.getQuantity()
            };
            tableModel.addRow(row);
        }
    }

    private void showAddMedicineDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add Medicine", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Generate medicine ID
        String medicineId = medicineService.generateMedicineId();

        JLabel idLabel = new JLabel("Medicine ID:");
        JTextField idField = new JTextField(medicineId);
        idField.setEditable(false);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField();

        JLabel manufacturerLabel = new JLabel("Manufacturer:");
        JTextField manufacturerField = new JTextField();

        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{
                "Pain Relief", "Antibiotics", "Antihistamine", "Cardiovascular",
                "Gastrointestinal", "Respiratory", "Dermatological", "Other"
        });

        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField();

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField();

        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField();

        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(manufacturerLabel);
        formPanel.add(manufacturerField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryComboBox);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(quantityLabel);
        formPanel.add(quantityField);
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
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String manufacturer = manufacturerField.getText().trim();
                if (manufacturer.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Manufacturer cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double price;
                try {
                    price = Double.parseDouble(priceField.getText().trim());
                    if (price < 0) {
                        JOptionPane.showMessageDialog(dialog, "Price cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Price must be a number", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(quantityField.getText().trim());
                    if (quantity < 0) {
                        JOptionPane.showMessageDialog(dialog, "Quantity cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Quantity must be a number", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create medicine object
                Medicine medicine = new Medicine(
                        idField.getText(),
                        name,
                        manufacturer,
                        price,
                        quantity
                );

                medicine.setCategory((String) categoryComboBox.getSelectedItem());
                medicine.setDescription(descriptionField.getText().trim());

                // Save medicine
                if (medicineService.add(medicine)) {
                    JOptionPane.showMessageDialog(dialog, "Medicine added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadMedicines();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add medicine", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showEditMedicineDialog() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String medicineId = (String) medicineTable.getValueAt(selectedRow, 0);
        Medicine medicine = medicineService.getById(medicineId);

        if (medicine == null) {
            JOptionPane.showMessageDialog(this, "Medicine not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit Medicine", true);
        dialog.setSize(500, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel idLabel = new JLabel("Medicine ID:");
        JTextField idField = new JTextField(medicine.getMedicineId());
        idField.setEditable(false);

        JLabel nameLabel = new JLabel("Name:");
        JTextField nameField = new JTextField(medicine.getName());

        JLabel manufacturerLabel = new JLabel("Manufacturer:");
        JTextField manufacturerField = new JTextField(medicine.getManufacturer());

        JLabel categoryLabel = new JLabel("Category:");
        JComboBox<String> categoryComboBox = new JComboBox<>(new String[]{
                "Pain Relief", "Antibiotics", "Antihistamine", "Cardiovascular",
                "Gastrointestinal", "Respiratory", "Dermatological", "Other"
        });
        categoryComboBox.setSelectedItem(medicine.getCategory());

        JLabel priceLabel = new JLabel("Price:");
        JTextField priceField = new JTextField(String.valueOf(medicine.getPrice()));

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField(String.valueOf(medicine.getQuantity()));

        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField(medicine.getDescription());

        formPanel.add(idLabel);
        formPanel.add(idField);
        formPanel.add(nameLabel);
        formPanel.add(nameField);
        formPanel.add(manufacturerLabel);
        formPanel.add(manufacturerField);
        formPanel.add(categoryLabel);
        formPanel.add(categoryComboBox);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(quantityLabel);
        formPanel.add(quantityField);
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
                String name = nameField.getText().trim();
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Name cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String manufacturer = manufacturerField.getText().trim();
                if (manufacturer.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Manufacturer cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                double price;
                try {
                    price = Double.parseDouble(priceField.getText().trim());
                    if (price < 0) {
                        JOptionPane.showMessageDialog(dialog, "Price cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Price must be a number", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(quantityField.getText().trim());
                    if (quantity < 0) {
                        JOptionPane.showMessageDialog(dialog, "Quantity cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Quantity must be a number", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update medicine object
                medicine.setName(name);
                medicine.setManufacturer(manufacturer);
                medicine.setCategory((String) categoryComboBox.getSelectedItem());
                medicine.setPrice(price);
                medicine.setQuantity(quantity);
                medicine.setDescription(descriptionField.getText().trim());

                // Save medicine
                if (medicineService.update(medicine)) {
                    JOptionPane.showMessageDialog(dialog, "Medicine updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadMedicines();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update medicine", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void deleteMedicine() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String medicineId = (String) medicineTable.getValueAt(selectedRow, 0);
        String medicineName = (String) medicineTable.getValueAt(selectedRow, 1);

        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete medicine " + medicineName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            if (medicineService.delete(medicineId)) {
                JOptionPane.showMessageDialog(this, "Medicine deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadMedicines();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete medicine", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void viewMedicineDetails() {
        int selectedRow = medicineTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a medicine to view", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String medicineId = (String) medicineTable.getValueAt(selectedRow, 0);
        Medicine medicine = medicineService.getById(medicineId);

        if (medicine == null) {
            JOptionPane.showMessageDialog(this, "Medicine not found with ID: " + medicineId, "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Medicine Details", true);
        dialog.setSize(500, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Medicine details
        JLabel titleLabel = new JLabel("Medicine Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addDetailRow(infoPanel, "Medicine ID:", medicine.getMedicineId());
        addDetailRow(infoPanel, "Name:", medicine.getName());
        addDetailRow(infoPanel, "Manufacturer:", medicine.getManufacturer());
        addDetailRow(infoPanel, "Category:", medicine.getCategory());
        addDetailRow(infoPanel, "Price:", String.format("$%.2f", medicine.getPrice()));
        addDetailRow(infoPanel, "Quantity:", String.valueOf(medicine.getQuantity()));
        addDetailRow(infoPanel, "Description:", medicine.getDescription());

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

    private void showDispenseMedicineDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Dispense Medicine", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Patient selection panel
        JPanel patientPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JLabel patientLabel = new JLabel("Patient:");
        JComboBox<String> patientComboBox = new JComboBox<>();

        // Add some sample patients
        patientComboBox.addItem("P001 - John Doe");
        patientComboBox.addItem("P002 - Jane Smith");
        patientComboBox.addItem("P003 - Robert Brown");

        patientPanel.add(patientLabel);
        patientPanel.add(patientComboBox);

        // Medicine selection panel
        JPanel selectionPanel = new JPanel(new BorderLayout());
        selectionPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        // Add instruction label
        JLabel instructionLabel = new JLabel("<html><b>Instructions:</b> Select a medicine from the list below and click 'Add ↓' to add it to the prescription.</html>");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        instructionLabel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Available medicines table
        String[] availableColumns = {"ID", "Name", "Category", "Price", "Available"};
        DefaultTableModel availableModel = new DefaultTableModel(availableColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable availableMedicinesTable = new JTable(availableModel);
        availableMedicinesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane availableScrollPane = new JScrollPane(availableMedicinesTable);
        availableScrollPane.setPreferredSize(new Dimension(550, 120));

        JLabel availableLabel = new JLabel("Available Medicines:");
        availableLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Load available medicines
        List<Medicine> medicines = medicineService.getAll();
        for (Medicine medicine : medicines) {
            if (medicine.getQuantity() > 0) {
                Object[] row = {
                        medicine.getMedicineId(),
                        medicine.getName(),
                        medicine.getCategory(),
                        String.format("$%.2f", medicine.getPrice()),
                        medicine.getQuantity()
                };
                availableModel.addRow(row);
            }
        }

        // Selected medicines table
        String[] selectedColumns = {"ID", "Name", "Quantity", "Dosage", "Frequency", "Duration", "Price"};
        DefaultTableModel selectedModel = new DefaultTableModel(selectedColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2 || column == 3 || column == 4 || column == 5;
            }
        };

        JTable selectedMedicinesTable = new JTable(selectedModel);
        selectedMedicinesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane selectedScrollPane = new JScrollPane(selectedMedicinesTable);
        selectedScrollPane.setPreferredSize(new Dimension(550, 120));

        JLabel selectedLabel = new JLabel("Selected Medicines for Prescription:");
        selectedLabel.setFont(new Font("Arial", Font.BOLD, 12));

        // Buttons panel - make it more prominent
        JPanel buttonsPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));
        JButton addButton = new JButton("Add Medicine ↓");
        addButton.setFont(new Font("Arial", Font.BOLD, 12));
        addButton.setBackground(new Color(70, 130, 180));
        addButton.setForeground(Color.WHITE);
        addButton.setPreferredSize(new Dimension(150, 30));

        JButton removeButton = new JButton("Remove ↑");
        removeButton.setFont(new Font("Arial", Font.BOLD, 12));
        removeButton.setBackground(new Color(220, 20, 60));
        removeButton.setForeground(Color.WHITE);
        removeButton.setPreferredSize(new Dimension(100, 30));

        buttonsPanel.add(addButton);
        buttonsPanel.add(removeButton);

        // Add components to selection panel
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.add(instructionLabel, BorderLayout.NORTH);

        JPanel availablePanel = new JPanel(new BorderLayout());
        availablePanel.add(availableLabel, BorderLayout.NORTH);
        availablePanel.add(availableScrollPane, BorderLayout.CENTER);

        JPanel selectedPanel = new JPanel(new BorderLayout());
        selectedPanel.add(selectedLabel, BorderLayout.NORTH);
        selectedPanel.add(selectedScrollPane, BorderLayout.CENTER);

        topSection.add(availablePanel, BorderLayout.CENTER);

        selectionPanel.add(topSection, BorderLayout.NORTH);
        selectionPanel.add(buttonsPanel, BorderLayout.CENTER);
        selectionPanel.add(selectedPanel, BorderLayout.SOUTH);

        // Summary panel
        JPanel summaryPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JLabel totalLabel = new JLabel("Total: $0.00");
        totalLabel.setFont(new Font("Arial", Font.BOLD, 14));
        summaryPanel.add(totalLabel);

        // Button panel
        JPanel dialogButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton dispenseMedicineButton = new JButton("Dispense Medicine");
        JButton cancelButton = new JButton("Cancel");

        dialogButtonPanel.add(dispenseMedicineButton);
        dialogButtonPanel.add(cancelButton);

        // Add all panels to main panel
        mainPanel.add(patientPanel, BorderLayout.NORTH);
        mainPanel.add(selectionPanel, BorderLayout.CENTER);
        mainPanel.add(summaryPanel, BorderLayout.SOUTH);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(dialogButtonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addButton.addActionListener(e -> {
            int selectedRow = availableMedicinesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog,
                        "Please select a medicine from the 'Available Medicines' table first.",
                        "No Medicine Selected",
                        JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String medicineId = (String) availableMedicinesTable.getValueAt(selectedRow, 0);
            String medicineName = (String) availableMedicinesTable.getValueAt(selectedRow, 1);
            double price = Double.parseDouble(((String) availableMedicinesTable.getValueAt(selectedRow, 3)).substring(1));

            // Check if medicine is already in selected list
            for (int i = 0; i < selectedModel.getRowCount(); i++) {
                if (medicineId.equals(selectedModel.getValueAt(i, 0))) {
                    JOptionPane.showMessageDialog(dialog,
                            "This medicine is already added to the prescription.",
                            "Medicine Already Added",
                            JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // Add to selected medicines
            Object[] row = {
                    medicineId,
                    medicineName,
                    1, // Default quantity
                    "1 tablet", // Default dosage
                    "Twice daily", // Default frequency
                    "7 days", // Default duration
                    String.format("$%.2f", price)
            };
            selectedModel.addRow(row);

            // Update total
            updateTotal(selectedModel, totalLabel);

            // Show success message
            JOptionPane.showMessageDialog(dialog,
                    medicineName + " has been added to the prescription.\nPlease review and edit the dosage, frequency, and duration as needed.",
                    "Medicine Added",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        removeButton.addActionListener(e -> {
            int selectedRow = selectedMedicinesTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select a medicine to remove", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            selectedModel.removeRow(selectedRow);

            // Update total
            updateTotal(selectedModel, totalLabel);
        });

        dispenseMedicineButton.addActionListener(e -> {
            if (selectedModel.getRowCount() == 0) {
                JOptionPane.showMessageDialog(dialog, "Please select at least one medicine", "No Medicines", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Check if all required fields are filled
            for (int i = 0; i < selectedModel.getRowCount(); i++) {
                Integer quantity = null;
                try {
                    quantity = Integer.parseInt(selectedModel.getValueAt(i, 2).toString());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Please enter a valid quantity for all medicines", "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Quantity must be greater than 0", "Invalid Quantity", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                String dosage = (String) selectedModel.getValueAt(i, 3);
                String frequency = (String) selectedModel.getValueAt(i, 4);
                String duration = (String) selectedModel.getValueAt(i, 5);

                if (dosage == null || dosage.trim().isEmpty() ||
                        frequency == null || frequency.trim().isEmpty() ||
                        duration == null || duration.trim().isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Please fill in dosage, frequency, and duration for all medicines", "Missing Information", JOptionPane.WARNING_MESSAGE);
                    return;
                }
            }

            // In a real implementation, this would update inventory and create a prescription
            // For now, just show success message
            JOptionPane.showMessageDialog(dialog, "Medicines dispensed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        // Add table model listener to update total when quantity changes
        selectedModel.addTableModelListener(e -> {
            updateTotal(selectedModel, totalLabel);
        });

        dialog.setVisible(true);
    }

    private void updateTotal(DefaultTableModel model, JLabel totalLabel) {
        double total = 0.0;

        for (int i = 0; i < model.getRowCount(); i++) {
            String priceStr = (String) model.getValueAt(i, 6);
            double price = Double.parseDouble(priceStr.substring(1));

            int quantity = 1;
            try {
                Object quantityObj = model.getValueAt(i, 2);
                if (quantityObj != null) {
                    quantity = Integer.parseInt(quantityObj.toString());
                }
            } catch (NumberFormatException ex) {
                // Use default quantity of 1
            }

            total += price * quantity;
        }

        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Arial", Font.BOLD, 12));

        // Fix null pointer issue and handle empty strings
        String displayValue = "N/A";
        if (value != null && !value.trim().isEmpty() && !value.equals("null")) {
            displayValue = value;
        }

        JLabel valueComponent = new JLabel(displayValue);
        valueComponent.setFont(new Font("Arial", Font.PLAIN, 12));

        panel.add(labelComponent);
        panel.add(valueComponent);
    }
}
