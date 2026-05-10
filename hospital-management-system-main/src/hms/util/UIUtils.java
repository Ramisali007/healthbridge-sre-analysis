package hms.util;

import javax.swing.*;
import java.awt.*;

/**
 * Shared UI utility methods — extracted from duplicate code in
 * BillingManagementPanel, PatientManagementPanel, and PharmacyManagementPanel.
 *
 * Part B4 Refactoring Demonstration:
 * This class resolves the Duplicate Code smell (Category 4: Dispensables)
 * by centralising the addDetailRow() method that was copied identically
 * across three panel classes.
 *
 * @author Ramis Ali (22F-3703), Kamil Mohsin (22F-3713)
 */
public final class UIUtils {

    // Centralised font constants — changing these updates ALL detail panels
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 12);
    private static final Font VALUE_FONT = new Font("Arial", Font.PLAIN, 12);
    private static final String DEFAULT_VALUE = "N/A";

    // Prevent instantiation — all methods are static
    private UIUtils() {
        throw new UnsupportedOperationException("Utility class — do not instantiate");
    }

    /**
     * Add a label-value row to a detail panel.
     * Replaces the private addDetailRow() methods previously duplicated in:
     *   - BillingManagementPanel.java (lines 722-731)
     *   - PatientManagementPanel.java (lines 529-538)
     *   - PharmacyManagementPanel.java (lines 836-851)
     *
     * @param panel The target GridLayout panel
     * @param label The field label text (e.g., "Patient ID:")
     * @param value The field value (null-safe — displays "N/A" if null or empty)
     */
    public static void addDetailRow(JPanel panel, String label, String value) {
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(LABEL_FONT);

        // Null-safe value handling — consolidated from three slightly different implementations
        String displayValue = (value != null && !value.trim().isEmpty()
                && !"null".equals(value)) ? value : DEFAULT_VALUE;

        JLabel valueComponent = new JLabel(displayValue);
        valueComponent.setFont(VALUE_FONT);

        panel.add(labelComponent);
        panel.add(valueComponent);
    }

    /**
     * Create a styled stat card for the dashboard.
     * Could be extracted from DashboardFrame.createStatCard() in a future refactoring.
     *
     * @param title The stat title (e.g., "Patients")
     * @param value The stat value (e.g., "152")
     * @param color The background color
     * @return A styled JPanel representing the stat card
     */
    public static JPanel createStatCard(String title, String value, Color color) {
        JPanel cardPanel = new JPanel();
        cardPanel.setLayout(new BorderLayout());
        cardPanel.setBackground(color);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 36));
        valueLabel.setForeground(Color.WHITE);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        cardPanel.add(titleLabel, BorderLayout.NORTH);
        cardPanel.add(valueLabel, BorderLayout.CENTER);

        return cardPanel;
    }
}
