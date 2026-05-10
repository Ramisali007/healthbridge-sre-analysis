package hms.ui;

import hms.model.User;
import hms.service.UserService;
import hms.util.PasswordUtils;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private UserService userService;
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton resetPasswordButton;
    private JButton refreshButton;

    public UserManagementPanel() {
        userService = new UserService();
        initializeUI();
        loadUsers();
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
        addButton = new JButton("Add User");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        resetPasswordButton = new JButton("Reset Password");
        refreshButton = new JButton("Refresh");

        buttonsPanel.add(addButton);
        buttonsPanel.add(editButton);
        buttonsPanel.add(deleteButton);
        buttonsPanel.add(resetPasswordButton);
        buttonsPanel.add(refreshButton);

        topPanel.add(searchPanel, BorderLayout.WEST);
        topPanel.add(buttonsPanel, BorderLayout.EAST);

        // Create table
        String[] columns = {"Username", "Full Name", "Role", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.getTableHeader().setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(userTable);

        // Add components to panel
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);

        // Add action listeners
        searchButton.addActionListener(e -> searchUsers());
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    searchUsers();
                }
            }
        });

        addButton.addActionListener(e -> showAddUserDialog());
        editButton.addActionListener(e -> showEditUserDialog());
        deleteButton.addActionListener(e -> deleteUser());
        resetPasswordButton.addActionListener(e -> resetPassword());
        refreshButton.addActionListener(e -> loadUsers());
    }

    private void loadUsers() {
        // Clear table
        tableModel.setRowCount(0);

        // Load users from service
        List<User> users = userService.getAll();

        // Add users to table
        for (User user : users) {
            Object[] row = {
                    user.getUsername(),
                    user.getFullName(),
                    user.getRole(),
                    user.isActive() ? "Active" : "Inactive"
            };
            tableModel.addRow(row);
        }
    }

    private void searchUsers() {
        String query = searchField.getText().trim();

        // Clear table
        tableModel.setRowCount(0);

        // Search users
        List<User> users = userService.search(query);

        // Add users to table
        for (User user : users) {
            Object[] row = {
                    user.getUsername(),
                    user.getFullName(),
                    user.getRole(),
                    user.isActive() ? "Active" : "Inactive"
            };
            tableModel.addRow(row);
        }
    }

    private void showAddUserDialog() {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Add User", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField();

        JLabel passwordLabel = new JLabel("Password:");
        JPasswordField passwordField = new JPasswordField();

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField();

        JLabel fullNameLabel = new JLabel("Full Name:");
        JTextField fullNameField = new JTextField();

        JLabel roleLabel = new JLabel("Role:");
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{
                "ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST", "PHARMACIST"
        });

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(confirmPasswordLabel);
        formPanel.add(confirmPasswordField);
        formPanel.add(fullNameLabel);
        formPanel.add(fullNameField);
        formPanel.add(roleLabel);
        formPanel.add(roleComboBox);

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
                String username = usernameField.getText().trim();
                if (username.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Username cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Password cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, "Passwords do not match", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(dialog, "Password must be at least 6 characters", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                String fullName = fullNameField.getText().trim();
                if (fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Full name cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Check if username already exists
                if (userService.getById(username) != null) {
                    JOptionPane.showMessageDialog(dialog, "Username already exists", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Create user object
                User user = new User(
                        username,
                        password, // In a real application, this would be hashed
                        fullName,
                        (String) roleComboBox.getSelectedItem()
                );

                // Save user
                if (userService.add(user)) {
                    JOptionPane.showMessageDialog(dialog, "User added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to add user", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void showEditUserDialog() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) userTable.getValueAt(selectedRow, 0);
        User user = userService.getById(username);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Edit User", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(user.getUsername());
        usernameField.setEditable(false);

        JLabel fullNameLabel = new JLabel("Full Name:");
        JTextField fullNameField = new JTextField(user.getFullName());

        JLabel roleLabel = new JLabel("Role:");
        JComboBox<String> roleComboBox = new JComboBox<>(new String[]{
                "ADMIN", "DOCTOR", "NURSE", "RECEPTIONIST", "PHARMACIST"
        });
        roleComboBox.setSelectedItem(user.getRole());

        JLabel statusLabel = new JLabel("Status:");
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{
                "Active", "Inactive"
        });
        statusComboBox.setSelectedItem(user.isActive() ? "Active" : "Inactive");

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(fullNameLabel);
        formPanel.add(fullNameField);
        formPanel.add(roleLabel);
        formPanel.add(roleComboBox);
        formPanel.add(statusLabel);
        formPanel.add(statusComboBox);

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
                String fullName = fullNameField.getText().trim();
                if (fullName.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Full name cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update user object
                user.setFullName(fullName);
                user.setRole((String) roleComboBox.getSelectedItem());
                user.setActive(statusComboBox.getSelectedItem().equals("Active"));

                // Save user
                if (userService.update(user)) {
                    JOptionPane.showMessageDialog(dialog, "User updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to update user", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }

    private void deleteUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) userTable.getValueAt(selectedRow, 0);

        // Don't allow deleting the admin user
        if (username.equals("admin")) {
            JOptionPane.showMessageDialog(this, "Cannot delete the admin user", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete user " + username + "?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
        );

        if (option == JOptionPane.YES_OPTION) {
            if (userService.delete(username)) {
                JOptionPane.showMessageDialog(this, "User deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadUsers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete user", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void resetPassword() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to reset password", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String username = (String) userTable.getValueAt(selectedRow, 0);
        User user = userService.getById(username);

        if (user == null) {
            JOptionPane.showMessageDialog(this, "User not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Reset Password", true);
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel formPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JLabel usernameLabel = new JLabel("Username:");
        JTextField usernameField = new JTextField(user.getUsername());
        usernameField.setEditable(false);

        JLabel passwordLabel = new JLabel("New Password:");
        JPasswordField passwordField = new JPasswordField();

        JLabel confirmPasswordLabel = new JLabel("Confirm Password:");
        JPasswordField confirmPasswordField = new JPasswordField();

        formPanel.add(usernameLabel);
        formPanel.add(usernameField);
        formPanel.add(passwordLabel);
        formPanel.add(passwordField);
        formPanel.add(confirmPasswordLabel);
        formPanel.add(confirmPasswordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton saveButton = new JButton("Reset Password");
        JButton cancelButton = new JButton("Cancel");

        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        dialog.add(formPanel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // Add action listeners
        saveButton.addActionListener(e -> {
            try {
                // Validate input
                String password = new String(passwordField.getPassword());
                String confirmPassword = new String(confirmPasswordField.getPassword());

                if (password.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "Password cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (!password.equals(confirmPassword)) {
                    JOptionPane.showMessageDialog(dialog, "Passwords do not match", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                if (password.length() < 6) {
                    JOptionPane.showMessageDialog(dialog, "Password must be at least 6 characters", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }

                // Update user password
                user.setPassword(password); // In a real application, this would be hashed

                // Save user
                if (userService.update(user)) {
                    JOptionPane.showMessageDialog(dialog, "Password reset successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                } else {
                    JOptionPane.showMessageDialog(dialog, "Failed to reset password", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setVisible(true);
    }
}
