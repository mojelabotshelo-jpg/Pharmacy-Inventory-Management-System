package com.healthfirst.pims;

import javax.swing.*;
import java.awt.*;

public class AdminDashboard extends JFrame {

    private JTabbedPane tabbedPane;

    public AdminDashboard(String fullName) {

        setTitle("HealthFirst PIMS - Admin Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());

        // =========================
        // HEADER
        // =========================

        JPanel headerPanel = new JPanel(new BorderLayout());

        headerPanel.setBackground(
                new Color(30, 136, 229)
        );

        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 20, 15, 20
                )
        );

        JLabel titleLabel = new JLabel(
                "HealthFirst Pharmacy - Admin Dashboard"
        );

        titleLabel.setForeground(Color.WHITE);

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        22
                )
        );

        JLabel welcomeLabel = new JLabel(
                "Welcome, " + fullName
        );

        welcomeLabel.setForeground(Color.WHITE);

        welcomeLabel.setFont(
                new Font(
                        "Arial",
                        Font.PLAIN,
                        16
                )
        );

        headerPanel.add(
                titleLabel,
                BorderLayout.WEST
        );

        headerPanel.add(
                welcomeLabel,
                BorderLayout.EAST
        );

        // =========================
        // TABS
        // =========================

        tabbedPane = new JTabbedPane();

        // Manage Medicines
        JPanel medicinesPanel =
                new MedicinePanel();

        // Manage Suppliers
        JPanel suppliersPanel =
                new SupplierPanel();

        // Manage Users
        JPanel usersPanel =
                new UserPanel();

        // Reports
        JPanel reportsPanel =
                new ReportPanel();

        // Add tabs
        tabbedPane.addTab(
                "Manage Medicines",
                medicinesPanel
        );

        tabbedPane.addTab(
                "Manage Suppliers",
                suppliersPanel
        );

        tabbedPane.addTab(
                "Manage Users",
                usersPanel
        );

        tabbedPane.addTab(
                "Reports",
                reportsPanel
        );

        // =========================
        // LOGOUT BUTTON
        // =========================

        JButton logoutButton =
                new JButton("Logout");

        logoutButton.addActionListener(e -> {

            int choice =
                    JOptionPane.showConfirmDialog(
                            this,
                            "Are you sure you want to logout?",
                            "Logout",
                            JOptionPane.YES_NO_OPTION
                    );

            if (choice ==
                    JOptionPane.YES_OPTION) {

                dispose();

                new Login().setVisible(true);
            }
        });

        JPanel bottomPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.RIGHT
                        )
                );

        bottomPanel.add(
                logoutButton
        );

        // =========================
        // ADD EVERYTHING
        // =========================

        mainPanel.add(
                headerPanel,
                BorderLayout.NORTH
        );

        mainPanel.add(
                tabbedPane,
                BorderLayout.CENTER
        );

        mainPanel.add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        add(mainPanel);
    }
}
