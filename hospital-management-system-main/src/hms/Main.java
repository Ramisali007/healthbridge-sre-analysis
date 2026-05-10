package hms;

import hms.ui.LoginFrame;

import javax.swing.*;
import java.io.File;

public class Main {
    public static void main(String[] args) {
        // Set system properties for better UI rendering
        System.setProperty("awt.useSystemAAFontSettings", "on");
        System.setProperty("swing.aatext", "true");

        // Create data directories if they don't exist
        createDataDirectories();

        // Set look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Could not set system look and feel: " + e.getMessage());
            // Continue with default look and feel
        }

        // Start application on EDT
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    new LoginFrame();
                } catch (Exception e) {
                    System.err.println("Error starting application: " + e.getMessage());
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null,
                            "Error starting application: " + e.getMessage(),
                            "Application Error",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    private static void createDataDirectories() {
        try {
            // Create data directory
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                boolean created = dataDir.mkdirs();
                if (created) {
                    System.out.println("Created data directory: " + dataDir.getAbsolutePath());
                } else {
                    System.err.println("Failed to create data directory: " + dataDir.getAbsolutePath());
                }
            }

            // Create reports directory
            File reportsDir = new File("reports");
            if (!reportsDir.exists()) {
                boolean created = reportsDir.mkdirs();
                if (created) {
                    System.out.println("Created reports directory: " + reportsDir.getAbsolutePath());
                } else {
                    System.err.println("Failed to create reports directory: " + reportsDir.getAbsolutePath());
                }
            }
        } catch (Exception e) {
            System.err.println("Error creating directories: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
