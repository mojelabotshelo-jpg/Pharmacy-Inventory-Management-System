package com.healthfirst.pims;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.sql.*;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class ReportPanel extends JPanel {

    private JTable reportTable;
    private DefaultTableModel tableModel;

    private JComboBox<String> reportTypeCombo;

    private JTextField fromDateField;
    private JTextField toDateField;

    private JButton generateButton;
    private JButton printButton;
    private JButton refreshButton;

    public ReportPanel() {

        setLayout(new BorderLayout(10, 10));

        setBorder(
                BorderFactory.createEmptyBorder(
                        10, 10, 10, 10
                )
        );

        // =====================================================
        // TITLE
        // =====================================================

        JLabel titleLabel = new JLabel(
                "Pharmacy Reports",
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        26
                )
        );

        titleLabel.setForeground(
                new Color(30, 136, 229)
        );

        add(
                titleLabel,
                BorderLayout.NORTH
        );

        // =====================================================
        // TOP CONTROL PANEL
        // =====================================================

        JPanel controlPanel = new JPanel(
                new GridBagLayout()
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(5, 5, 5, 5);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        // =====================================================
        // REPORT TYPE
        // =====================================================

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0;

        controlPanel.add(
                new JLabel("Report Type:"),
                gbc
        );

        reportTypeCombo =
                new JComboBox<>(
                        new String[]{
                                "Sales Report",
                                "Item-Wise Sales Report",
                                "Low Stock Report",
                                "Expiry Report"
                        }
                );

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 1;

        controlPanel.add(
                reportTypeCombo,
                gbc
        );

        // =====================================================
        // FROM DATE
        // =====================================================

        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.weightx = 0;

        controlPanel.add(
                new JLabel("From Date:"),
                gbc
        );

        fromDateField =
                new JTextField(10);

        fromDateField.setToolTipText(
                "Format: YYYY-MM-DD"
        );

        gbc.gridx = 3;
        gbc.gridy = 0;

        controlPanel.add(
                fromDateField,
                gbc
        );

        // =====================================================
        // TO DATE
        // =====================================================

        gbc.gridx = 4;
        gbc.gridy = 0;

        controlPanel.add(
                new JLabel("To Date:"),
                gbc
        );

        toDateField =
                new JTextField(10);

        toDateField.setToolTipText(
                "Format: YYYY-MM-DD"
        );

        gbc.gridx = 5;
        gbc.gridy = 0;

        controlPanel.add(
                toDateField,
                gbc
        );

        // =====================================================
        // BUTTONS
        // =====================================================

        generateButton =
                new JButton("Generate Report");

        printButton =
                new JButton("Print Report");

        refreshButton =
                new JButton("Refresh");

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 2;
        gbc.weightx = 1;

        controlPanel.add(
                generateButton,
                gbc
        );

        gbc.gridx = 2;
        gbc.gridy = 1;
        gbc.gridwidth = 2;

        controlPanel.add(
                printButton,
                gbc
        );

        gbc.gridx = 4;
        gbc.gridy = 1;
        gbc.gridwidth = 2;

        controlPanel.add(
                refreshButton,
                gbc
        );

        // =====================================================
        // TABLE
        // =====================================================

        tableModel =
                new DefaultTableModel() {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        reportTable =
                new JTable(tableModel);

        reportTable.setRowHeight(25);

        reportTable.setAutoCreateRowSorter(true);

        JScrollPane scrollPane =
                new JScrollPane(reportTable);

        scrollPane.setBorder(
                BorderFactory.createTitledBorder(
                        "Report Results"
                )
        );

        // =====================================================
        // CENTER PANEL
        // =====================================================

        JPanel centerPanel =
                new JPanel(
                        new BorderLayout(10, 10)
                );

        centerPanel.add(
                controlPanel,
                BorderLayout.NORTH
        );

        centerPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        add(
                centerPanel,
                BorderLayout.CENTER
        );

        // =====================================================
        // BUTTON ACTIONS
        // =====================================================

        generateButton.addActionListener(
                e -> generateReport()
        );

        refreshButton.addActionListener(
                e -> generateReport()
        );

        printButton.addActionListener(
                e -> printReport()
        );

        // =====================================================
        // REPORT TYPE CHANGE
        // =====================================================

        reportTypeCombo.addActionListener(
                e -> updateDateFields()
        );

        updateDateFields();

        // =====================================================
        // LOAD DEFAULT REPORT
        // =====================================================

        generateReport();
    }

    // =========================================================
    // UPDATE DATE FIELDS
    // =========================================================

    private void updateDateFields() {

        String selected =
                reportTypeCombo
                        .getSelectedItem()
                        .toString();

        boolean salesReport =
                selected.equals("Sales Report")
                        || selected.equals(
                        "Item-Wise Sales Report"
                );

        fromDateField.setEnabled(
                salesReport
        );

        toDateField.setEnabled(
                salesReport
        );

        // Clear date fields for reports
        // that do not use dates
        if (!salesReport) {

            fromDateField.setText("");
            toDateField.setText("");
        }
    }

    // =========================================================
    // GENERATE REPORT
    // =========================================================

    private void generateReport() {

        String selected =
                reportTypeCombo
                        .getSelectedItem()
                        .toString();

        if (selected.equals("Sales Report")) {

            loadSalesReport();

        } else if (
                selected.equals(
                        "Item-Wise Sales Report"
                )
        ) {

            loadItemWiseSalesReport();

        } else if (
                selected.equals(
                        "Low Stock Report"
                )
        ) {

            loadLowStockReport();

        } else if (
                selected.equals(
                        "Expiry Report"
                )
        ) {

            loadExpiryReport();
        }
    }

    // =========================================================
    // SALES REPORT
    // =========================================================

    private void loadSalesReport() {

        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        tableModel.setColumnIdentifiers(
                new String[]{
                        "Sale ID",
                        "Sale Date",
                        "Cashier",
                        "Total Amount"
                }
        );

        String fromDate =
                fromDateField.getText().trim();

        String toDate =
                toDateField.getText().trim();

        StringBuilder sql =
                new StringBuilder();

        sql.append(
                "SELECT s.sale_id, " +
                        "s.sale_date, " +
                        "u.full_name AS cashier, " +
                        "s.total_amount " +
                        "FROM sales s " +
                        "LEFT JOIN users u " +
                        "ON s.user_id = u.user_id "
        );

        boolean hasFrom =
                !fromDate.isEmpty();

        boolean hasTo =
                !toDate.isEmpty();

        if (hasFrom) {

            sql.append(
                    "WHERE DATE(s.sale_date) >= ? "
            );
        }

        if (hasTo) {

            sql.append(
                    hasFrom
                            ? "AND DATE(s.sale_date) <= ? "
                            : "WHERE DATE(s.sale_date) <= ? "
            );
        }

        sql.append(
                "ORDER BY s.sale_date DESC"
        );

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql.toString()
                        )
        ) {

            int parameterIndex = 1;

            if (hasFrom) {

                validateDate(fromDate);

                statement.setDate(
                        parameterIndex++,
                        Date.valueOf(fromDate)
                );
            }

            if (hasTo) {

                validateDate(toDate);

                statement.setDate(
                        parameterIndex++,
                        Date.valueOf(toDate)
                );
            }

            try (
                    ResultSet rs =
                            statement.executeQuery()
            ) {

                while (rs.next()) {

                    tableModel.addRow(
                            new Object[]{
                                    rs.getInt("sale_id"),

                                    rs.getTimestamp(
                                            "sale_date"
                                    ),

                                    rs.getString(
                                            "cashier"
                                    ),

                                    String.format(
                                            "R%.2f",
                                            rs.getDouble(
                                                    "total_amount"
                                            )
                                    )
                            }
                    );
                }
            }

        } catch (DateTimeParseException ex) {

            showError(
                    "Invalid date format.\n\n"
                            + "Please use YYYY-MM-DD."
            );

        } catch (SQLException ex) {

            showDatabaseError(ex);
        }
    }

    // =========================================================
    // ITEM-WISE SALES REPORT
    // =========================================================

    private void loadItemWiseSalesReport() {

        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        tableModel.setColumnIdentifiers(
                new String[]{
                        "Medicine",
                        "Quantity Sold",
                        "Price",
                        "Total Sales"
                }
        );

        String fromDate =
                fromDateField.getText().trim();

        String toDate =
                toDateField.getText().trim();

        StringBuilder sql =
                new StringBuilder();

        sql.append(
                "SELECT " +
                        "m.name AS medicine, " +
                        "SUM(si.quantity_sold) AS quantity_sold, " +
                        "si.price_at_sale AS price, " +
                        "SUM(si.quantity_sold * " +
                        "si.price_at_sale) AS total_sales " +
                        "FROM sale_items si " +
                        "JOIN medicines m " +
                        "ON si.medicine_id = m.medicine_id " +
                        "JOIN sales s " +
                        "ON si.sale_id = s.sale_id "
        );

        boolean hasFrom =
                !fromDate.isEmpty();

        boolean hasTo =
                !toDate.isEmpty();

        if (hasFrom) {

            sql.append(
                    "WHERE DATE(s.sale_date) >= ? "
            );
        }

        if (hasTo) {

            sql.append(
                    hasFrom
                            ? "AND DATE(s.sale_date) <= ? "
                            : "WHERE DATE(s.sale_date) <= ? "
            );
        }

        sql.append(
                "GROUP BY m.medicine_id, " +
                        "m.name, si.price_at_sale " +
                        "ORDER BY total_sales DESC"
        );

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(
                                sql.toString()
                        )
        ) {

            int parameterIndex = 1;

            if (hasFrom) {

                validateDate(fromDate);

                statement.setDate(
                        parameterIndex++,
                        Date.valueOf(fromDate)
                );
            }

            if (hasTo) {

                validateDate(toDate);

                statement.setDate(
                        parameterIndex++,
                        Date.valueOf(toDate)
                );
            }

            try (
                    ResultSet rs =
                            statement.executeQuery()
            ) {

                while (rs.next()) {

                    tableModel.addRow(
                            new Object[]{
                                    rs.getString(
                                            "medicine"
                                    ),

                                    rs.getInt(
                                            "quantity_sold"
                                    ),

                                    String.format(
                                            "R%.2f",
                                            rs.getDouble(
                                                    "price"
                                            )
                                    ),

                                    String.format(
                                            "R%.2f",
                                            rs.getDouble(
                                                    "total_sales"
                                            )
                                    )
                            }
                    );
                }
            }

        } catch (DateTimeParseException ex) {

            showError(
                    "Invalid date format.\n\n"
                            + "Please use YYYY-MM-DD."
            );

        } catch (SQLException ex) {

            showDatabaseError(ex);
        }
    }

    // =========================================================
    // LOW STOCK REPORT
    // =========================================================

    private void loadLowStockReport() {

        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        tableModel.setColumnIdentifiers(
                new String[]{
                        "Medicine ID",
                        "Medicine",
                        "Company",
                        "Current Stock",
                        "Reorder Level",
                        "Supplier ID"
                }
        );

        String sql =
                "SELECT medicine_id, name, company, " +
                        "quantity_in_stock, reorder_level, " +
                        "supplier_id " +
                        "FROM medicines " +
                        "WHERE quantity_in_stock <= reorder_level " +
                        "ORDER BY quantity_in_stock ASC";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet rs =
                        statement.executeQuery()
        ) {

            while (rs.next()) {

                tableModel.addRow(
                        new Object[]{
                                rs.getInt(
                                        "medicine_id"
                                ),

                                rs.getString(
                                        "name"
                                ),

                                rs.getString(
                                        "company"
                                ),

                                rs.getInt(
                                        "quantity_in_stock"
                                ),

                                rs.getInt(
                                        "reorder_level"
                                ),

                                rs.getInt(
                                        "supplier_id"
                                )
                        }
                );
            }

        } catch (SQLException ex) {

            showDatabaseError(ex);
        }
    }

    // =========================================================
    // EXPIRY REPORT
    // =========================================================

    private void loadExpiryReport() {

        tableModel.setRowCount(0);
        tableModel.setColumnCount(0);

        tableModel.setColumnIdentifiers(
                new String[]{
                        "Medicine ID",
                        "Medicine",
                        "Company",
                        "Expiry Date",
                        "Current Stock",
                        "Supplier ID"
                }
        );

        /*
         * ASSIGNMENT REQUIREMENT:
         *
         * Show medicines expiring within the next ONE MONTH.
         *
         * expiry_date >= CURDATE()
         *     -> Do not show medicines already expired.
         *
         * expiry_date <= DATE_ADD(CURDATE(), INTERVAL 1 MONTH)
         *     -> Only show medicines expiring within one month.
         */

        String sql =
                "SELECT medicine_id, name, company, " +
                        "expiry_date, quantity_in_stock, " +
                        "supplier_id " +
                        "FROM medicines " +
                        "WHERE expiry_date >= CURDATE() " +
                        "AND expiry_date <= " +
                        "DATE_ADD(CURDATE(), INTERVAL 1 MONTH) " +
                        "ORDER BY expiry_date ASC";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql);

                ResultSet rs =
                        statement.executeQuery()
        ) {

            while (rs.next()) {

                tableModel.addRow(
                        new Object[]{
                                rs.getInt(
                                        "medicine_id"
                                ),

                                rs.getString(
                                        "name"
                                ),

                                rs.getString(
                                        "company"
                                ),

                                rs.getDate(
                                        "expiry_date"
                                ),

                                rs.getInt(
                                        "quantity_in_stock"
                                ),

                                rs.getInt(
                                        "supplier_id"
                                )
                        }
                );
            }

            // Show a message if there are no medicines
            // expiring within the next month.
            if (tableModel.getRowCount() == 0) {

                JOptionPane.showMessageDialog(
                        this,
                        "No medicines are expiring " +
                                "within the next month.",
                        "Expiry Report",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (SQLException ex) {

            showDatabaseError(ex);
        }
    }

    // =========================================================
    // PRINT REPORT
    // =========================================================

    private void printReport() {

        if (reportTable.getRowCount() == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "There is no report data to print.",
                    "Print Report",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        String reportName =
                reportTypeCombo
                        .getSelectedItem()
                        .toString();

        try {

            MessageFormat header =
                    new MessageFormat(
                            "HealthFirst Pharmacy - "
                                    + reportName
                    );

            MessageFormat footer =
                    new MessageFormat(
                            "Page {0}"
                    );

            boolean complete =
                    reportTable.print(
                            JTable.PrintMode.FIT_WIDTH,
                            header,
                            footer
                    );

            if (complete) {

                JOptionPane.showMessageDialog(
                        this,
                        "Report printed successfully.",
                        "Print Report",
                        JOptionPane.INFORMATION_MESSAGE
                );

            } else {

                JOptionPane.showMessageDialog(
                        this,
                        "Printing was cancelled.",
                        "Print Report",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }

        } catch (PrinterException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Unable to print report:\n"
                            + ex.getMessage(),
                    "Print Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // =========================================================
    // DATE VALIDATION
    // =========================================================

    private void validateDate(
            String date
    ) {

        LocalDate.parse(date);
    }

    // =========================================================
    // ERROR MESSAGE
    // =========================================================

    private void showError(
            String message
    ) {

        JOptionPane.showMessageDialog(
                this,
                message,
                "Validation Error",
                JOptionPane.WARNING_MESSAGE
        );
    }

    // =========================================================
    // DATABASE ERROR
    // =========================================================

    private void showDatabaseError(
            SQLException ex
    ) {

        JOptionPane.showMessageDialog(
                this,
                "Database error:\n"
                        + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE
        );

        ex.printStackTrace();
    }
}
