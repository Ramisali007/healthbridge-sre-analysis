package hms.ui;

import hms.model.User;
import hms.service.UserService;
import hms.util.ValidationUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;

public class LoginFrame extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton exitButton;
    private UserService userService;

    public LoginFrame() {
        userService = new UserService();
        initializeUI();
    }

    private void initializeUI() {
        setTitle("Hospital Management System - Login");
        setSize(500, 350);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // Main panel with background color
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(new Color(240, 248, 255)); // Light blue background

        // Header panel with logo and title
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180)); // Steel blue
        headerPanel.setPreferredSize(new Dimension(500, 100)); // Increased height for logo
        headerPanel.setLayout(new BorderLayout());

        // Logo and title panel
        JPanel logoTitlePanel = new JPanel();
        logoTitlePanel.setLayout(new BoxLayout(logoTitlePanel, BoxLayout.Y_AXIS));
        logoTitlePanel.setBackground(new Color(70, 130, 180));

        // Try to load logo from multiple possible locations
        ImageIcon logoIcon = loadLogo();
        if (logoIcon != null) {
            JLabel logoLabel = new JLabel(logoIcon);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoTitlePanel.add(logoLabel);
            logoTitlePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        JLabel titleLabel = new JLabel("ZM HOSPITAL");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoTitlePanel.add(titleLabel);

        headerPanel.add(logoTitlePanel, BorderLayout.CENTER);

        // Login form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(null);
        formPanel.setBackground(new Color(240, 248, 255));

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(100, 30, 100, 25);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(200, 30, 200, 25);
        formPanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(100, 70, 100, 25);
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 14));
        formPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(200, 70, 200, 25);
        formPanel.add(passwordField);

        loginButton = new JButton("Login");
        loginButton.setBounds(200, 120, 100, 30);
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.BLACK);
        loginButton.setFocusPainted(false);
        formPanel.add(loginButton);

        exitButton = new JButton("Exit");
        exitButton.setBounds(310, 120, 90, 30);
        exitButton.setBackground(new Color(220, 20, 60));
        exitButton.setForeground(Color.BLACK);
        exitButton.setFocusPainted(false);
        formPanel.add(exitButton);

        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(70, 130, 180));
        footerPanel.setPreferredSize(new Dimension(500, 40));

        JLabel footerLabel = new JLabel("© 2025 ZM Hospital");
        footerLabel.setForeground(Color.WHITE);
        footerPanel.add(footerLabel);

        // Add panels to main panel
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        // Add main panel to frame
        add(mainPanel);

        // Add action listeners
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login();
            }
        });

        exitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        // Add key listener for Enter key
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    login();
                }
            }
        });

        setVisible(true);
    }

    private ImageIcon loadLogo() {
        // Try multiple possible locations for the logo
        String[] possiblePaths = {
                "logo.png",                    // Project root
                "./logo.png",                  // Current directory
                "src/logo.png",               // In src folder
                "resources/logo.png",         // In resources folder
                "images/logo.png",            // In images folder
                System.getProperty("user.dir") + "/logo.png"  // Absolute path to project root
        };

        for (String path : possiblePaths) {
            try {
                File logoFile = new File(path);
                System.out.println("Trying logo path: " + logoFile.getAbsolutePath());
                System.out.println("File exists: " + logoFile.exists());

                if (logoFile.exists()) {
                    ImageIcon originalIcon = new ImageIcon(path);
                    if (originalIcon.getIconWidth() > 0) {
                        // Scale the logo to appropriate size
                        Image img = originalIcon.getImage();
                        Image scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                        System.out.println("Logo loaded successfully from: " + path);
                        return new ImageIcon(scaledImg);
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to load logo from " + path + ": " + e.getMessage());
            }
        }

        // Try loading from classpath (if logo is in resources)
        try {
            java.net.URL logoURL = getClass().getClassLoader().getResource("logo.png");
            if (logoURL != null) {
                ImageIcon originalIcon = new ImageIcon(logoURL);
                Image img = originalIcon.getImage();
                Image scaledImg = img.getScaledInstance(60, 60, Image.SCALE_SMOOTH);
                System.out.println("Logo loaded from classpath");
                return new ImageIcon(scaledImg);
            }
        } catch (Exception e) {
            System.out.println("Failed to load logo from classpath: " + e.getMessage());
        }

        System.out.println("Logo not found in any location. Current working directory: " + System.getProperty("user.dir"));
        return null;
    }

    private void login() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Username and password cannot be empty",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        User user = userService.authenticate(username, password);

        if (user != null) {
            new DashboardFrame(user);
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Invalid username or password",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            // Set system look and feel
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new LoginFrame();
            }
        });
    }
}
