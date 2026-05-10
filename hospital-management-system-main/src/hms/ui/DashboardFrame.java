package hms.ui;

import hms.model.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.border.EmptyBorder;

public class DashboardFrame extends JFrame {
    private User currentUser;
    private JPanel mainPanel;
    private JPanel menuPanel;
    private JPanel contentPanel;

    // Menu buttons
    private JButton patientsButton;
    private JButton doctorsButton;
    private JButton appointmentsButton;
    private JButton medicineButton;
    private JButton billingButton;
    private JButton reportsButton;
    private JButton usersButton;
    private JButton logoutButton;

    public DashboardFrame(User user) {
        this.currentUser = user;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Hospital Management System - Dashboard");
        setSize(1200, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel with border layout
        mainPanel = new JPanel(new BorderLayout());

        // Create header panel
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Create menu panel
        menuPanel = createMenuPanel();
        mainPanel.add(menuPanel, BorderLayout.WEST);

        // Create content panel
        contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBackground(new Color(240, 248, 255));
        contentPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Add welcome panel as default content
        showWelcomePanel();

        mainPanel.add(contentPanel, BorderLayout.CENTER);

        // Create footer panel
        JPanel footerPanel = createFooterPanel();
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);
        setVisible(true);
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(new Color(70, 130, 180)); // Steel blue
        headerPanel.setPreferredSize(new Dimension(1200, 60));

        // Logo and title panel
        JPanel logoTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        logoTitlePanel.setBackground(new Color(70, 130, 180));

        // Try to load logo
        try {
            ImageIcon logoIcon = new ImageIcon("logo.png");
            Image img = logoIcon.getImage();
            Image scaledImg = img.getScaledInstance(40, 40, Image.SCALE_SMOOTH);
            ImageIcon scaledIcon = new ImageIcon(scaledImg);

            JLabel logoLabel = new JLabel(scaledIcon);
            logoTitlePanel.add(logoLabel);
            logoTitlePanel.add(Box.createRigidArea(new Dimension(10, 0)));
        } catch (Exception e) {
            System.out.println("Logo not found: " + e.getMessage());
        }

        JLabel titleLabel = new JLabel("ZM HOSPITAL");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        logoTitlePanel.add(titleLabel);

        headerPanel.add(logoTitlePanel, BorderLayout.WEST);

        JPanel userPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        userPanel.setBackground(new Color(70, 130, 180));

        JLabel userLabel = new JLabel("Welcome, " + currentUser.getFullName() + " (" + currentUser.getRole() + ")");
        userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        userLabel.setForeground(Color.WHITE);
        userPanel.add(userLabel);

        headerPanel.add(userPanel, BorderLayout.EAST);

        return headerPanel;
    }

    private JPanel createMenuPanel() {
        JPanel menuPanel = new JPanel();
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setBackground(new Color(51, 51, 51));
        menuPanel.setPreferredSize(new Dimension(200, 600));

        // Create menu buttons
        patientsButton = createMenuButton("Patients", "icons/patient.png");
        doctorsButton = createMenuButton("Doctors", "icons/doctor.png");
        appointmentsButton = createMenuButton("Appointments", "icons/appointment.png");
        medicineButton = createMenuButton("Pharmacy", "icons/medicine.png");
        billingButton = createMenuButton("Billing", "icons/billing.png");
        reportsButton = createMenuButton("Reports", "icons/report.png");
        usersButton = createMenuButton("Users", "icons/user.png");
        logoutButton = createMenuButton("Logout", "icons/logout.png");

        // Add buttons to menu panel
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        menuPanel.add(patientsButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(doctorsButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(appointmentsButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(medicineButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(billingButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        menuPanel.add(reportsButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // Only show Users button for admin
        if ("ADMIN".equals(currentUser.getRole())) {
            menuPanel.add(usersButton);
            menuPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        menuPanel.add(Box.createVerticalGlue());
        menuPanel.add(logoutButton);
        menuPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Add action listeners
        patientsButton.addActionListener(e -> showPatientManagement());
        doctorsButton.addActionListener(e -> showDoctorManagement());
        appointmentsButton.addActionListener(e -> showAppointmentManagement());
        medicineButton.addActionListener(e -> showPharmacyManagement());
        billingButton.addActionListener(e -> showBillingManagement());
        reportsButton.addActionListener(e -> showReportsPanel());
        usersButton.addActionListener(e -> showUserManagement());
        logoutButton.addActionListener(e -> logout());

        return menuPanel;
    }

    private JButton createMenuButton(String text, String iconPath) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(51, 51, 51));
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setMaximumSize(new Dimension(200, 40));
        button.setPreferredSize(new Dimension(200, 40));

        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(51, 51, 51));
            }
        });

        return button;
    }

    private JPanel createFooterPanel() {
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(70, 130, 180));
        footerPanel.setPreferredSize(new Dimension(1200, 30));

        JLabel footerLabel = new JLabel("© 2025 ZM Hospital | All Rights Reserved");
        footerLabel.setForeground(Color.WHITE);
        footerPanel.add(footerLabel);

        return footerPanel;
    }

    private void showWelcomePanel() {
        contentPanel.removeAll();

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BorderLayout());
        welcomePanel.setBackground(new Color(240, 248, 255));

        JLabel welcomeLabel = new JLabel("Welcome to Hospital Management System");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 20, 20));
        statsPanel.setBackground(new Color(240, 248, 255));
        statsPanel.setBorder(new EmptyBorder(50, 50, 50, 50));

        // Add stat cards
        statsPanel.add(createStatCard("Patients", "152", new Color(70, 130, 180)));
        statsPanel.add(createStatCard("Doctors", "45", new Color(46, 139, 87)));
        statsPanel.add(createStatCard("Appointments", "37", new Color(255, 140, 0)));
        statsPanel.add(createStatCard("Medicines", "423", new Color(220, 20, 60)));
        statsPanel.add(createStatCard("Pending Bills", "18", new Color(148, 0, 211)));
        statsPanel.add(createStatCard("Users", "12", new Color(30, 144, 255)));

        welcomePanel.add(welcomeLabel, BorderLayout.NORTH);
        welcomePanel.add(statsPanel, BorderLayout.CENTER);

        contentPanel.add(welcomePanel);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private JPanel createStatCard(String title, String value, Color color) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBackground(color);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(new EmptyBorder(10, 0, 0, 0));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        cardPanel.add(titleLabel, BorderLayout.NORTH);
        cardPanel.add(valueLabel, BorderLayout.CENTER);

        return cardPanel;
    }

    private void showPatientManagement() {
        contentPanel.removeAll();
        contentPanel.add(new PatientManagementPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showDoctorManagement() {
        contentPanel.removeAll();
        contentPanel.add(new DoctorManagementPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showAppointmentManagement() {
        contentPanel.removeAll();
        contentPanel.add(new AppointmentManagementPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showPharmacyManagement() {
        contentPanel.removeAll();
        contentPanel.add(new PharmacyManagementPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showBillingManagement() {
        contentPanel.removeAll();
        contentPanel.add(new BillingManagementPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showReportsPanel() {
        contentPanel.removeAll();
        contentPanel.add(new ReportsPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void showUserManagement() {
        contentPanel.removeAll();
        contentPanel.add(new UserManagementPanel(), BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    private void logout() {
        int option = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION
        );

        if (option == JOptionPane.YES_OPTION) {
            new LoginFrame();
            dispose();
        }
    }
}
