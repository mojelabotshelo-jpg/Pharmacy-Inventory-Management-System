package com.healthfirst.pims;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class SupplierPanel extends JPanel {

    private JTextField nameField;
    private JTextField contactField;
    private JTextField phoneField;
    private JTextField emailField;
    private JTextArea addressArea;

    private JTable supplierTable;
    private DefaultTableModel tableModel;

    public SupplierPanel() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // =========================
        // FORM
        // =========================

        JPanel formPanel = new JPanel(new GridLayout(3, 4, 8, 8));

        nameField = new JTextField();
        contactField = new JTextField();
        phoneField = new JTextField();
        emailField = new JTextField();
        addressArea = new JTextArea(3, 20);

        addressArea.setLineWrap(true);
        addressArea.setWrapStyleWord(true);

        formPanel.add(new JLabel("Supplier Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Contact Person:"));
        formPanel.add(contactField);

        formPanel.add(new JLabel("Phone:"));
        formPanel.add(phoneField);

        formPanel.add(new JLabel("Email:"));
        formPanel.add(emailField);

        formPanel.add(new JLabel("Address:"));

        JScrollPane addressScroll = new JScrollPane(addressArea);
        formPanel.add(addressScroll);

        add(formPanel, BorderLayout.NORTH);

        // =========================
        // TABLE
        // =========================

        tableModel = new DefaultTableModel(
                new String[]{
                        "ID",
                        "Name",
                        "Contact Person",
                        "Phone",
                        "Email",
                        "Address"
                },
                0
        );

        supplierTable = new JTable(tableModel);

        add(
                new JScrollPane(supplierTable),
                BorderLayout.CENTER
        );

        // =========================
        // BUTTONS
        // =========================

        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton addButton = new JButton("Add");
        JButton updateButton = new JButton("Update");
        JButton deleteButton = new JButton("Delete");
        JButton clearButton = new JButton("Clear");
        JButton refreshButton = new JButton("Refresh");

        buttonPanel.add(addButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        // =========================
        // ACTIONS
        // =========================

        addButton.addActionListener(e -> addSupplier());
        updateButton.addActionListener(e -> updateSupplier());
        deleteButton.addActionListener(e -> deleteSupplier());
        clearButton.addActionListener(e -> clearFields());
        refreshButton.addActionListener(e -> loadSuppliers());

        // Select table row
        supplierTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int row = supplierTable.getSelectedRow();

                if (row >= 0) {

                    nameField.setText(
                            tableModel.getValueAt(row, 1).toString()
                    );

                    contactField.setText(
                            tableModel.getValueAt(row, 2).toString()
                    );

                    phoneField.setText(
                            tableModel.getValueAt(row, 3).toString()
                    );

                    emailField.setText(
                            tableModel.getValueAt(row, 4).toString()
                    );

                    addressArea.setText(
                            tableModel.getValueAt(row, 5).toString()
                    );
                }
            }
        });

        loadSuppliers();
    }

    // =========================
    // LOAD SUPPLIERS
    // =========================

    private void loadSuppliers() {

        tableModel.setRowCount(0);

        String sql = "SELECT supplier_id, name, contact_person, " +
                "phone, email, address FROM suppliers";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                tableModel.addRow(new Object[]{
                        resultSet.getInt("supplier_id"),
                        resultSet.getString("name"),
                        resultSet.getString("contact_person"),
                        resultSet.getString("phone"),
                        resultSet.getString("email"),
                        resultSet.getString("address")
                });
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading suppliers:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // ADD SUPPLIER
    // =========================

    private void addSupplier() {

        if (!validateFields()) {
            return;
        }

        String sql = "INSERT INTO suppliers " +
                "(name, contact_person, phone, email, address) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, nameField.getText().trim());
            statement.setString(2, contactField.getText().trim());
            statement.setString(3, phoneField.getText().trim());
            statement.setString(4, emailField.getText().trim());
            statement.setString(5, addressArea.getText().trim());

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier added successfully!"
            );

            clearFields();
            loadSuppliers();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding supplier:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // UPDATE SUPPLIER
    // =========================

    private void updateSupplier() {

        int row = supplierTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a supplier to update."
            );

            return;
        }

        if (!validateFields()) {
            return;
        }

        int supplierId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString()
        );

        String sql = "UPDATE suppliers SET " +
                "name=?, contact_person=?, phone=?, email=?, address=? " +
                "WHERE supplier_id=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setString(1, nameField.getText().trim());
            statement.setString(2, contactField.getText().trim());
            statement.setString(3, phoneField.getText().trim());
            statement.setString(4, emailField.getText().trim());
            statement.setString(5, addressArea.getText().trim());
            statement.setInt(6, supplierId);

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier updated successfully!"
            );

            clearFields();
            loadSuppliers();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating supplier:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // DELETE SUPPLIER
    // =========================

    private void deleteSupplier() {

        int row = supplierTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a supplier to delete."
            );

            return;
        }

        int supplierId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString()
        );

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this supplier?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql =
                "DELETE FROM suppliers WHERE supplier_id=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement =
                     connection.prepareStatement(sql)) {

            statement.setInt(1, supplierId);

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Supplier deleted successfully!"
            );

            clearFields();
            loadSuppliers();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting supplier:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // VALIDATION
    // =========================

    private boolean validateFields() {

        if (nameField.getText().trim().isEmpty() ||
                contactField.getText().trim().isEmpty() ||
                phoneField.getText().trim().isEmpty() ||
                emailField.getText().trim().isEmpty() ||
                addressArea.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please complete all supplier fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return false;
        }

        if (!emailField.getText().contains("@")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid email address.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return false;
        }

        return true;
    }

    // =========================
    // CLEAR
    // =========================

    private void clearFields() {

        nameField.setText("");
        contactField.setText("");
        phoneField.setText("");
        emailField.setText("");
        addressArea.setText("");

        supplierTable.clearSelection();
    }
}
