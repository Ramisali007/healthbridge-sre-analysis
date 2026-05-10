package hms.ui;

import hms.model.Billing;
import hms.model.Patient;
import hms.service.PatientService;
import hms.util.PDFGenerator;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class BillingManagementPanel extends JPanel {
    private JTable billingTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewButton;
    private JButton refreshButton;
    private JButton printButton;

    private PatientService patientService;

    public BillingManagementPanel() {
        patientService = new PatientService();
        initializeUI();
        loadBillings();
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
        addButton = new JButton("Generate Bill");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        viewButton = new JButton("View Details");
        printButton = new JButton("Print Bill");
        refreshButton = new JButton("Refresh");

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(viewButton);
        buttonsPanel.add(printButton);
        buttonsPanel.add(refreshButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonsPanel, BorderLayout.EAST);

        // Create table
        String[] columns = {"Bill ID", "Patient", "Date", "Amount", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        billingTable = new JTable(tableModel);
        billingTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        billingTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(billingTable);

        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        searchButton.addActionListener(e -> searchBillings());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchBillings();
                }
            }
        });

        addButton.addActionListener(e -> showGenerateBillDialog());
        editButton.addActionListener(e -> showEditBillDialog());
        deleteButton.addActionListener(e -> deleteBill());
        viewButton.addActionListener(e -> viewBillDetails());
        printButton.addActionListener(e -> printBill());
        refreshButton.addActionListener(e -> loadBillings());
    }

    private void loadBillings() {
        // Clear table
        tableModel.setRowCount(0);

        // Add sample data for now
        // In a real implementation, this would load from a service
        addSampleBillings();
    }

    private void addSampleBillings() {
        // Add some sample billings for demonstration
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Object[] row1 = {"B001", "John Doe", sdf.format(new Date()), "$150.00", "PAID"};
        Object[] row2 = {"B002", "Jane Smith", "2025-06-10", "$275.50", "UNPAID"};
        Object[] row3 = {"B003", "Robert Brown", "2025-06-05", "$420.75", "PAID"};
        Object[] row4 = {"B004", "Emily Davis", "2025-06-01", "$95.25", "UNPAID"};

        tableModel.addRow(row1);
        tableModel.addRow(row2);
        tableModel.addRow(row3);
        tableModel.addRow(row4);
    }

    private void searchBillings() {
        String query = searchField.getText().trim().toLowerCase();

        if (query.isEmpty()) {
            loadBillings();
            return;
        }

        // Clear table
        tableModel.setRowCount(0);

        // In a real implementation, this would search from a service
        // For now, just reload all and filter
        addSampleBillings();

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

    private void showGenerateBillDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Generate Bill", true);
        dialog.setSize(700, 600);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Bill header panel
        JPanel headerPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        // Generate bill ID
        String billId = "B" + System.currentTimeMillis();

        JLabel idLabel = new JLabel("Bill ID:");
        JTextField idField = new JTextField(billId);
        idField.setEditable(false);

        JLabel dateLabel = new JLabel("Date:");
        JTextField dateField = new JTextField(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        dateField.setEditable(false);

        JLabel patientLabel = new JLabel("Patient:");
        JComboBox<String> patientComboBox = new JComboBox<>();

        // Load patients
        java.util.List<Patient> patients = patientService.getAll();
        for (Patient patient : patients) {
            patientComboBox.addItem(patient.getId() + " - " + patient.getName());
        }

        headerPanel.add(idLabel);
        headerPanel.add(idField);
        headerPanel.add(dateLabel);
        headerPanel.add(dateField);
        headerPanel.add(patientLabel);
        headerPanel.add(patientComboBox);

        // Bill items panel
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBorder(new EmptyBorder(10, 0, 10, 0));

        JLabel itemsLabel = new JLabel("Bill Items:");
        itemsLabel.setFont(new Font("Arial", Font.BOLD, 14));

        // Items table
        String[] columns = {"Description", "Quantity", "Unit Price", "Amount"};
        DefaultTableModel itemsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 3; // Amount is calculated
            }
        };

        JTable itemsTable = new JTable(itemsModel);
        JScrollPane itemsScrollPane = new JScrollPane(itemsTable);

        // Buttons for items
        JPanel itemButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton addItemButton = new JButton("Add Item");
        JButton removeItemButton = new JButton("Remove Item");

        itemButtonsPanel.add(addItemButton);
        itemButtonsPanel.add(removeItemButton);

        itemsPanel.add(itemsLabel, BorderLayout.NORTH);
        itemsPanel.add(itemsScrollPane, BorderLayout.CENTER);
        itemsPanel.add(itemButtonsPanel, BorderLayout.SOUTH);

        // Summary panel
        JPanel summaryPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        summaryPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel subtotalLabel = new JLabel("Subtotal:");
        JTextField subtotalField = new JTextField("$0.00");
        subtotalField.setEditable(false);

        JLabel discountLabel = new JLabel("Discount (%):");
        JTextField discountField = new JTextField("0");

        JLabel taxLabel = new JLabel("Tax (%):");
        JTextField taxField = new JTextField("0");

        JLabel totalLabel = new JLabel("Total:");
        JTextField totalField = new JTextField("$0.00");
        totalField.setEditable(false);

        summaryPanel.add(subtotalLabel);
        summaryPanel.add(subtotalField);
        summaryPanel.add(discountLabel);
        summaryPanel.add(discountField);
        summaryPanel.add(taxLabel);
        summaryPanel.add(taxField);
        summaryPanel.add(totalLabel);
        summaryPanel.add(totalField);

        // Payment panel
        JPanel paymentPanel = new JPanel(new GridLayout(2, 2, 10, 10));
        paymentPanel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel statusLabel = new JLabel("Payment Status:");
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"PAID", "UNPAID"});

        JLabel methodLabel = new JLabel("Payment Method:");
        JComboBox<String> methodComboBox = new JComboBox<>(new String[]{"Cash", "Credit Card", "Debit Card", "Insurance", "Other"});

        paymentPanel.add(statusLabel);
        paymentPanel.add(statusComboBox);
        paymentPanel.add(methodLabel);
        paymentPanel.add(methodComboBox);

        // Add all panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(itemsPanel, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(summaryPanel, BorderLayout.NORTH);
        bottomPanel.add(paymentPanel, BorderLayout.SOUTH);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Save Bill");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        addItemButton.addActionListener(e -> {
            // Show dialog to add item
            showAddItemDialog(dialog, itemsModel, subtotalField, totalField, discountField, taxField);
        });

        removeItemButton.addActionListener(e -> {
            int selectedRow = itemsTable.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "Please select an item to remove", "No Selection", JOptionPane.WARNING_MESSAGE);
                return;
            }

            itemsModel.removeRow(selectedRow);
            updateBillTotal(itemsModel, subtotalField, totalField, discountField, taxField);
        });

        // Add listeners to discount and tax fields
        discountField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateBillTotal(itemsModel, subtotalField, totalField, discountField, taxField);
            }
        });

        taxField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                updateBillTotal(itemsModel, subtotalField, totalField, discountField, taxField);
            }
        });

        saveButton.addActionListener(e -> {
            try {
                // Validate input
                if (patientComboBox.getSelectedIndex() == -1) {
                    JOptionPane.showMessageDialog(dialog, "Please select a patient", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (itemsModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(dialog, "Please add at least one item", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // In a real implementation, this would save to a service
                // For now, just add to the table
                String patientInfo = (String) patientComboBox.getSelectedItem();
                String patientName = patientInfo.substring(patientInfo.indexOf(" - ") + 3);

                Object[] row = {
                        idField.getText(),
                        patientName,
                        dateField.getText(),
                        totalField.getText(),
                        statusComboBox.getSelectedItem()
                };

                tableModel.addRow(row);

                JOptionPane.showMessageDialog(dialog, "Bill generated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showAddItemDialog(JDialog parentDialog, DefaultTableModel itemsModel,
                                   JTextField subtotalField, JTextField totalField,
                                   JTextField discountField, JTextField taxField) {
        JDialog dialog = new JDialog(parentDialog, "Add Item", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(parentDialog);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel descriptionLabel = new JLabel("Description:");
        JTextField descriptionField = new JTextField();

        JLabel quantityLabel = new JLabel("Quantity:");
        JTextField quantityField = new JTextField("1");

        JLabel priceLabel = new JLabel("Unit Price:");
        JTextField priceField = new JTextField();

        JLabel amountLabel = new JLabel("Amount:");
        JTextField amountField = new JTextField("$0.00");
        amountField.setEditable(false);

        formPanel.add(descriptionLabel);
        formPanel.add(descriptionField);
        formPanel.add(quantityLabel);
        formPanel.add(quantityField);
        formPanel.add(priceLabel);
        formPanel.add(priceField);
        formPanel.add(amountLabel);
        formPanel.add(amountField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton addButton = new JButton("Add");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(addButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add listeners to calculate amount
        FocusListener calculateAmount = new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                try {
                    int quantity = Integer.parseInt(quantityField.getText().trim());
                    double price = Double.parseDouble(priceField.getText().trim());
                    double amount = quantity * price;
                    amountField.setText(String.format("$%.2f", amount));
                } catch (NumberFormatException ex) {
                    amountField.setText("$0.00");
                }
            }
        };

        quantityField.addFocusListener(calculateAmount);
        priceField.addFocusListener(calculateAmount);

        // Add action listeners
        addButton.addActionListener(e -> {
            try {
                // Validate input
                String description = descriptionField.getText().trim();
                if (description.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Description cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                int quantity;
                try {
                    quantity = Integer.parseInt(quantityField.getText().trim());
                    if (quantity <= 0) {
                        JOptionPane.showMessageDialog(dialog, "Quantity must be greater than 0", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(dialog, "Quantity must be a number", "Validation Error", JOptionPane.ERROR_MESSAGE);
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

                double amount = quantity * price;

                // Add to items table
                Object[] row = {
                        description,
                        quantity,
                        String.format("$%.2f", price),
                        String.format("$%.2f", amount)
                };

                itemsModel.addRow(row);

                // Update bill total
                updateBillTotal(itemsModel, subtotalField, totalField, discountField, taxField);

                dialog.dispose();

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void updateBillTotal(DefaultTableModel itemsModel, JTextField subtotalField,
                                 JTextField totalField, JTextField discountField, JTextField taxField) {
        double subtotal = 0.0;

        // Calculate subtotal
        for (int i = 0; i < itemsModel.getRowCount(); i++) {
            String amountStr = (String) itemsModel.getValueAt(i, 3);
            double amount = Double.parseDouble(amountStr.substring(1));
            subtotal += amount;
        }

        // Get discount and tax
        double discount = 0.0;
        try {
            discount = Double.parseDouble(discountField.getText().trim());
        } catch (NumberFormatException e) {
            discountField.setText("0");
        }

        double tax = 0.0;
        try {
            tax = Double.parseDouble(taxField.getText().trim());
        } catch (NumberFormatException e) {
            taxField.setText("0");
        }

        // Calculate total
        double discountAmount = subtotal * (discount / 100.0);
        double taxAmount = subtotal * (tax / 100.0);
        double total = subtotal - discountAmount + taxAmount;

        // Update fields
        subtotalField.setText(String.format("$%.2f", subtotal));
        totalField.setText(String.format("$%.2f", total));
    }

    private void showEditBillDialog() {
        int selectedRow = billingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JOptionPane.showMessageDialog(this, "Edit Bill functionality will be implemented here", "Information", JOptionPane.INFORMATION_MESSAGE);
    }

    private void deleteBill() {
        int selectedRow = billingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billId = (String) billingTable.getValueAt(selectedRow, 0);
        String patientName = (String) billingTable.getValueAt(selectedRow, 1);

        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete bill " + billId + " for " + patientName + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            // In a real implementation, this would delete from a service
            // For now, just remove from the table
            tableModel.removeRow(selectedRow);

            JOptionPane.showMessageDialog(this, "Bill deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void viewBillDetails() {
        int selectedRow = billingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to view", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billId = (String) billingTable.getValueAt(selectedRow, 0);
        String patientName = (String) billingTable.getValueAt(selectedRow, 1);
        String date = (String) billingTable.getValueAt(selectedRow, 2);
        String amount = (String) billingTable.getValueAt(selectedRow, 3);
        String status = (String) billingTable.getValueAt(selectedRow, 4);

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Bill Details", true);
        dialog.setSize(600, 500);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        // Bill details
        JLabel titleLabel = new JLabel("Bill Information");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        addDetailRow(infoPanel, "Bill ID:", billId);
        addDetailRow(infoPanel, "Patient:", patientName);
        addDetailRow(infoPanel, "Date:", date);
        addDetailRow(infoPanel, "Amount:", amount);
        addDetailRow(infoPanel, "Status:", status);

        detailsPanel.add(titleLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        detailsPanel.add(infoPanel);

        // Bill items
        JLabel itemsLabel = new JLabel("Bill Items");
        itemsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        itemsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] columns = {"Description", "Quantity", "Unit Price", "Amount"};
        DefaultTableModel itemsModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable itemsTable = new JTable(itemsModel);
        JScrollPane itemsScrollPane = new JScrollPane(itemsTable);
        itemsScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        itemsScrollPane.setPreferredSize(new Dimension(550, 200));

        // Add sample items
        Object[] item1 = {"Consultation Fee", 1, "$50.00", "$50.00"};
        Object[] item2 = {"Medicine - Paracetamol", 2, "$5.99", "$11.98"};
        Object[] item3 = {"Blood Test", 1, "$35.00", "$35.00"};

        itemsModel.addRow(item1);
        itemsModel.addRow(item2);
        itemsModel.addRow(item3);

        detailsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        detailsPanel.add(itemsLabel);
        detailsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        detailsPanel.add(itemsScrollPane);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton printButton = new JButton("Print Bill");
        JButton closeButton = new JButton("Close");

        buttonPanel.add(printButton);
        buttonPanel.add(closeButton);

        dialog.add(detailsPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        printButton.addActionListener(e -> {
            // In a real implementation, this would generate a PDF
            JOptionPane.showMessageDialog(dialog, "Bill printed successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        closeButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void printBill() {
        int selectedRow = billingTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a bill to print", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String billId = (String) billingTable.getValueAt(selectedRow, 0);
        String patientName = (String) billingTable.getValueAt(selectedRow, 1);

        try {
            // Create a sample billing and patient for demonstration
            Billing billing = new Billing(billId, "P001", new Date());
            billing.setTotalAmount(Double.parseDouble(((String) billingTable.getValueAt(selectedRow, 3)).substring(1)));
            billing.setPaymentStatus((String) billingTable.getValueAt(selectedRow, 4));

            Patient patient = new Patient("P001", patientName, 35, "123-456-7890", "Fever");

            // Add some sample items
            billing.addItem(new Billing.BillItem("Consultation Fee", 50.0, 1));
            billing.addItem(new Billing.BillItem("Medicine - Paracetamol", 5.99, 2));
            billing.addItem(new Billing.BillItem("Blood Test", 35.0, 1));

            String outputPath = "reports/bill_" + billId + ".pdf";
            File file = PDFGenerator.generateBillingReport(billing, patient, outputPath);

            if (file != null && file.exists()) {
                int option = JOptionPane.showConfirmDialog(
                        this,
                        "Bill generated successfully. Do you want to open it?",
                        "Bill Generated",
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
                JOptionPane.showMessageDialog(this, "Failed to generate bill", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Error generating bill: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
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
