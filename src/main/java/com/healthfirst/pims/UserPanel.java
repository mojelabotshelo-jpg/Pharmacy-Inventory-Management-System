package com.healthfirst.pims;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class UserPanel extends JPanel {

    private JTextField fullNameField;
    private JTextField usernameField;
    private JPasswordField passwordField;

    private JTable userTable;
    private DefaultTableModel tableModel;

    private int selectedUserId = -1;

    public UserPanel() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // =========================
        // FORM PANEL
        // =========================

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Full Name
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Full Name:"), gbc);

        fullNameField = new JTextField(20);

        gbc.gridx = 1;
        gbc.gridy = 0;
        formPanel.add(fullNameField, gbc);

        // Username
        gbc.gridx = 2;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Username:"), gbc);

        usernameField = new JTextField(20);

        gbc.gridx = 3;
        gbc.gridy = 0;
        formPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Password:"), gbc);

        passwordField = new JPasswordField(20);

        gbc.gridx = 1;
        gbc.gridy = 1;
        formPanel.add(passwordField, gbc);

        // Role
        gbc.gridx = 2;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Role:"), gbc);

        JLabel roleLabel = new JLabel("Cashier");
        roleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        gbc.gridx = 3;
        gbc.gridy = 1;
        formPanel.add(roleLabel, gbc);

        add(formPanel, BorderLayout.NORTH);

        // =========================
        // TABLE
        // =========================

        tableModel = new DefaultTableModel(
                new Object[]{
                        "ID",
                        "Full Name",
                        "Username",
                        "Role"
                }, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        userTable = new JTable(tableModel);

        userTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        userTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {
                selectUser();
            }
        });

        JScrollPane scrollPane = new JScrollPane(userTable);

        add(scrollPane, BorderLayout.CENTER);

        // =========================
        // BUTTONS
        // =========================

        JButton addButton = new JButton("Add Cashier");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh");

        addButton.addActionListener(e -> addCashier());

        deleteButton.addActionListener(e -> deleteCashier());

        clearButton.addActionListener(e -> clearFields());

        refreshButton.addActionListener(e -> loadUsers());

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 10, 10)
        );

        buttonPanel.add(addButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // Load users when panel opens
        loadUsers();
    }

    // =====================================================
    // LOAD CASHIERS
    // =====================================================

    private void loadUsers() {

        tableModel.setRowCount(0);

        String sql =
                "SELECT user_id, full_name, username, role " +
                        "FROM users " +
                        "WHERE role = 'Cashier' " +
                        "ORDER BY user_id";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                tableModel.addRow(new Object[]{
                        rs.getInt("user_id"),
                        rs.getString("full_name"),
                        rs.getString("username"),
                        rs.getString("role")
                });
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading users:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // ADD CASHIER
    // =====================================================

    private void addCashier() {

        String fullName = fullNameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(
                passwordField.getPassword()
        );

        if (fullName.isEmpty()
                || username.isEmpty()
                || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please complete all fields.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String sql =
                "INSERT INTO users " +
                        "(username, password, role, full_name) " +
                        "VALUES (?, ?, 'Cashier', ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setString(1, username);
            pst.setString(2, password);
            pst.setString(3, fullName);

            pst.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Cashier account created successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );

            clearFields();
            loadUsers();

        } catch (SQLIntegrityConstraintViolationException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "That username already exists.",
                    "Username Error",
                    JOptionPane.WARNING_MESSAGE
            );

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error creating cashier:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // SELECT USER
    // =====================================================

    private void selectUser() {

        int row = userTable.getSelectedRow();

        if (row == -1) {
            return;
        }

        selectedUserId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString()
        );

        fullNameField.setText(
                tableModel.getValueAt(row, 1).toString()
        );

        usernameField.setText(
                tableModel.getValueAt(row, 2).toString()
        );

        // Password is deliberately not loaded from database
        passwordField.setText("");
    }

    // =====================================================
    // DELETE CASHIER
    // =====================================================

    private void deleteCashier() {

        if (selectedUserId == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a cashier to delete.",
                    "Delete User",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this cashier?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM users " +
                        "WHERE user_id = ? AND role = 'Cashier'";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pst = conn.prepareStatement(sql)) {

            pst.setInt(1, selectedUserId);

            int rows = pst.executeUpdate();

            if (rows > 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "Cashier deleted successfully.",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE
                );

                clearFields();
                loadUsers();

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Cashier could not be deleted.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting cashier:\n" + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // CLEAR
    // =====================================================

    private void clearFields() {

        fullNameField.setText("");
        usernameField.setText("");
        passwordField.setText("");

        selectedUserId = -1;

        userTable.clearSelection();
    }
}
