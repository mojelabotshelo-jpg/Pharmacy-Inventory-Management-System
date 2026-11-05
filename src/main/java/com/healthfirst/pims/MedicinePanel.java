package com.healthfirst.pims;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.sql.Date;


public class MedicinePanel extends JPanel {

    private JTextField nameField;
    private JTextField companyField;
    private JTextField typeField;
    private JTextField priceField;
    private JTextField quantityField;
    private JTextField reorderField;
    private JTextField expiryField;
    private JTextField supplierIdField;

    private JTable medicineTable;
    private DefaultTableModel tableModel;

    public MedicinePanel() {

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // =========================
        // FORM PANEL
        // =========================

        JPanel formPanel = new JPanel(new GridLayout(4, 4, 8, 8));

        nameField = new JTextField();
        companyField = new JTextField();
        typeField = new JTextField();
        priceField = new JTextField();
        quantityField = new JTextField();
        reorderField = new JTextField();
        expiryField = new JTextField();
        supplierIdField = new JTextField();

        formPanel.add(new JLabel("Medicine Name:"));
        formPanel.add(nameField);

        formPanel.add(new JLabel("Company:"));
        formPanel.add(companyField);

        formPanel.add(new JLabel("Medicine Type:"));
        formPanel.add(typeField);

        formPanel.add(new JLabel("Price:"));
        formPanel.add(priceField);

        formPanel.add(new JLabel("Quantity:"));
        formPanel.add(quantityField);

        formPanel.add(new JLabel("Reorder Level:"));
        formPanel.add(reorderField);

        formPanel.add(new JLabel("Expiry Date (YYYY-MM-DD):"));
        formPanel.add(expiryField);

        formPanel.add(new JLabel("Supplier ID:"));
        formPanel.add(supplierIdField);

        add(formPanel, BorderLayout.NORTH);

        // =========================
        // TABLE
        // =========================

        tableModel = new DefaultTableModel(
                new String[]{
                        "ID",
                        "Name",
                        "Company",
                        "Type",
                        "Price",
                        "Quantity",
                        "Reorder",
                        "Expiry",
                        "Supplier ID"
                },
                0
        );

        medicineTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(medicineTable);

        add(scrollPane, BorderLayout.CENTER);

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
        // BUTTON ACTIONS
        // =========================

        addButton.addActionListener(e -> addMedicine());

        updateButton.addActionListener(e -> updateMedicine());

        deleteButton.addActionListener(e -> deleteMedicine());

        clearButton.addActionListener(e -> clearFields());

        refreshButton.addActionListener(e -> loadMedicines());

        // Select table row
        medicineTable.getSelectionModel().addListSelectionListener(e -> {

            if (!e.getValueIsAdjusting()) {

                int row = medicineTable.getSelectedRow();

                if (row >= 0) {

                    nameField.setText(tableModel.getValueAt(row, 1).toString());
                    companyField.setText(tableModel.getValueAt(row, 2).toString());
                    typeField.setText(tableModel.getValueAt(row, 3).toString());
                    priceField.setText(tableModel.getValueAt(row, 4).toString());
                    quantityField.setText(tableModel.getValueAt(row, 5).toString());
                    reorderField.setText(tableModel.getValueAt(row, 6).toString());
                    expiryField.setText(tableModel.getValueAt(row, 7).toString());
                    supplierIdField.setText(tableModel.getValueAt(row, 8).toString());
                }
            }
        });

        // Load existing medicines
        loadMedicines();
    }

    // =========================
    // LOAD MEDICINES
    // =========================

    private void loadMedicines() {

        tableModel.setRowCount(0);

        String sql = "SELECT medicine_id, name, company, medicine_type, " +
                "price, quantity_in_stock, reorder_level, expiry_date, supplier_id " +
                "FROM medicines";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {

                tableModel.addRow(new Object[]{
                        resultSet.getInt("medicine_id"),
                        resultSet.getString("name"),
                        resultSet.getString("company"),
                        resultSet.getString("medicine_type"),
                        resultSet.getBigDecimal("price"),
                        resultSet.getInt("quantity_in_stock"),
                        resultSet.getInt("reorder_level"),
                        resultSet.getDate("expiry_date"),
                        resultSet.getInt("supplier_id")
                });
            }

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading medicines:\n" + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // ADD MEDICINE
    // =========================

    private void addMedicine() {

        if (!validateFields()) {
            return;
        }

        String sql = "INSERT INTO medicines " +
                "(name, company, medicine_type, price, quantity_in_stock, " +
                "reorder_level, expiry_date, supplier_id) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nameField.getText().trim());
            statement.setString(2, companyField.getText().trim());
            statement.setString(3, typeField.getText().trim());
            statement.setBigDecimal(4, new java.math.BigDecimal(priceField.getText().trim()));
            statement.setInt(5, Integer.parseInt(quantityField.getText().trim()));
            statement.setInt(6, Integer.parseInt(reorderField.getText().trim()));
            statement.setDate(7, Date.valueOf(expiryField.getText().trim()));
            statement.setInt(8, Integer.parseInt(supplierIdField.getText().trim()));

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Medicine added successfully!"
            );

            clearFields();
            loadMedicines();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error adding medicine:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // UPDATE MEDICINE
    // =========================

    private void updateMedicine() {

        int row = medicineTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medicine to update."
            );

            return;
        }

        if (!validateFields()) {
            return;
        }

        int medicineId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString()
        );

        String sql = "UPDATE medicines SET " +
                "name=?, company=?, medicine_type=?, price=?, " +
                "quantity_in_stock=?, reorder_level=?, expiry_date=?, supplier_id=? " +
                "WHERE medicine_id=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, nameField.getText().trim());
            statement.setString(2, companyField.getText().trim());
            statement.setString(3, typeField.getText().trim());
            statement.setBigDecimal(4, new java.math.BigDecimal(priceField.getText().trim()));
            statement.setInt(5, Integer.parseInt(quantityField.getText().trim()));
            statement.setInt(6, Integer.parseInt(reorderField.getText().trim()));
            statement.setDate(7, Date.valueOf(expiryField.getText().trim()));
            statement.setInt(8, Integer.parseInt(supplierIdField.getText().trim()));
            statement.setInt(9, medicineId);

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Medicine updated successfully!"
            );

            clearFields();
            loadMedicines();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error updating medicine:\n" + e.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================
    // DELETE MEDICINE
    // =========================

    private void deleteMedicine() {

        int row = medicineTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medicine to delete."
            );

            return;
        }

        int medicineId = Integer.parseInt(
                tableModel.getValueAt(row, 0).toString()
        );

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this medicine?",
                "Confirm Delete",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {
            return;
        }

        String sql = "DELETE FROM medicines WHERE medicine_id=?";

        try (Connection connection = DBConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, medicineId);

            statement.executeUpdate();

            JOptionPane.showMessageDialog(
                    this,
                    "Medicine deleted successfully!"
            );

            clearFields();
            loadMedicines();

        } catch (SQLException e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error deleting medicine:\n" + e.getMessage(),
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
                companyField.getText().trim().isEmpty() ||
                typeField.getText().trim().isEmpty() ||
                priceField.getText().trim().isEmpty() ||
                quantityField.getText().trim().isEmpty() ||
                reorderField.getText().trim().isEmpty() ||
                expiryField.getText().trim().isEmpty() ||
                supplierIdField.getText().trim().isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please complete all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return false;
        }

        try {

            new java.math.BigDecimal(priceField.getText().trim());

            Integer.parseInt(quantityField.getText().trim());
            Integer.parseInt(reorderField.getText().trim());
            Integer.parseInt(supplierIdField.getText().trim());

            Date.valueOf(expiryField.getText().trim());

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter valid values.\n\n" +
                            "Price: decimal number\n" +
                            "Quantity/Reorder/Supplier ID: whole numbers\n" +
                            "Expiry: YYYY-MM-DD",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return false;
        }

        return true;
    }

    // =========================
    // CLEAR FIELDS
    // =========================

    private void clearFields() {

        nameField.setText("");
        companyField.setText("");
        typeField.setText("");
        priceField.setText("");
        quantityField.setText("");
        reorderField.setText("");
        expiryField.setText("");
        supplierIdField.setText("");

        medicineTable.clearSelection();
    }
}
