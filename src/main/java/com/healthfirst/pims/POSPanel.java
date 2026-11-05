package com.healthfirst.pims;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.print.PrinterException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class POSPanel extends JPanel {

    private JComboBox<Medicine> medicineComboBox;
    private JSpinner quantitySpinner;

    private JTable cartTable;
    private DefaultTableModel cartModel;

    private JLabel totalLabel;

    private int userId;

    private double total = 0.0;

    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public POSPanel(int userId) {

        this.userId = userId;

        setLayout(new BorderLayout(10, 10));

        setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15
                )
        );

        createPOSInterface();

        loadMedicines();
    }

    // =========================================================
    // CREATE POS INTERFACE
    // =========================================================

    private void createPOSInterface() {

        JPanel topPanel =
                new JPanel(new GridBagLayout());

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(8, 8, 8, 8);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        // Medicine label
        gbc.gridx = 0;
        gbc.gridy = 0;

        topPanel.add(
                new JLabel("Medicine:"),
                gbc
        );

        // Medicine combo box
        medicineComboBox =
                new JComboBox<>();

        medicineComboBox.setPreferredSize(
                new Dimension(300, 30)
        );

        gbc.gridx = 1;
        gbc.gridy = 0;

        topPanel.add(
                medicineComboBox,
                gbc
        );

        // Quantity label
        gbc.gridx = 2;
        gbc.gridy = 0;

        topPanel.add(
                new JLabel("Quantity:"),
                gbc
        );

        // Quantity spinner
        quantitySpinner =
                new JSpinner(
                        new SpinnerNumberModel(
                                1,
                                1,
                                1000,
                                1
                        )
                );

        quantitySpinner.setPreferredSize(
                new Dimension(80, 30)
        );

        gbc.gridx = 3;
        gbc.gridy = 0;

        topPanel.add(
                quantitySpinner,
                gbc
        );

        // Add button
        JButton addButton =
                new JButton("Add to Cart");

        gbc.gridx = 4;
        gbc.gridy = 0;

        topPanel.add(
                addButton,
                gbc
        );

        addButton.addActionListener(
                e -> addToCart()
        );

        add(
                topPanel,
                BorderLayout.NORTH
        );

        // =====================================================
        // CART TABLE
        // =====================================================

        cartModel =
                new DefaultTableModel(
                        new Object[]{
                                "Medicine ID",
                                "Medicine",
                                "Price",
                                "Quantity",
                                "Subtotal"
                        },
                        0
                ) {

                    @Override
                    public boolean isCellEditable(
                            int row,
                            int column
                    ) {
                        return false;
                    }
                };

        cartTable =
                new JTable(cartModel);

        cartTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        cartTable.setRowHeight(28);

        JScrollPane scrollPane =
                new JScrollPane(cartTable);

        add(
                scrollPane,
                BorderLayout.CENTER
        );

        // =====================================================
        // BOTTOM PANEL
        // =====================================================

        JPanel bottomPanel =
                new JPanel(
                        new BorderLayout()
                );

        JPanel buttonPanel =
                new JPanel(
                        new FlowLayout(
                                FlowLayout.LEFT
                        )
                );

        JButton removeButton =
                new JButton("Remove Selected");

        JButton clearButton =
                new JButton("Clear Cart");

        JButton checkoutButton =
                new JButton("CHECKOUT");

        checkoutButton.setBackground(
                new Color(46, 125, 50)
        );

        checkoutButton.setForeground(
                Color.WHITE
        );

        buttonPanel.add(
                removeButton
        );

        buttonPanel.add(
                clearButton
        );

        buttonPanel.add(
                checkoutButton
        );

        bottomPanel.add(
                buttonPanel,
                BorderLayout.WEST
        );

        totalLabel =
                new JLabel(
                        "TOTAL: R0.00"
                );

        totalLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        20
                )
        );

        bottomPanel.add(
                totalLabel,
                BorderLayout.EAST
        );

        add(
                bottomPanel,
                BorderLayout.SOUTH
        );

        removeButton.addActionListener(
                e -> removeSelectedItem()
        );

        clearButton.addActionListener(
                e -> clearCart()
        );

        checkoutButton.addActionListener(
                e -> checkout()
        );
    }

    // =========================================================
    // LOAD MEDICINES
    // =========================================================

    private void loadMedicines() {

        medicineComboBox.removeAllItems();

        String sql =
                "SELECT medicine_id, name, price, quantity_in_stock " +
                        "FROM medicines " +
                        "WHERE quantity_in_stock > 0 " +
                        "ORDER BY name";

        try (
                Connection conn =
                        DBConnection.getConnection();

                PreparedStatement pst =
                        conn.prepareStatement(sql);

                ResultSet rs =
                        pst.executeQuery()
        ) {

            while (rs.next()) {

                Medicine medicine =
                        new Medicine(
                                rs.getInt(
                                        "medicine_id"
                                ),
                                rs.getString(
                                        "name"
                                ),
                                rs.getDouble(
                                        "price"
                                ),
                                rs.getInt(
                                        "quantity_in_stock"
                                )
                        );

                medicineComboBox.addItem(
                        medicine
                );
            }

        } catch (SQLException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading medicines:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =========================================================
    // ADD TO CART
    // =========================================================

    private void addToCart() {

        Medicine medicine =
                (Medicine)
                        medicineComboBox
                                .getSelectedItem();

        if (medicine == null) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medicine.",
                    "Input Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int quantity =
                (Integer)
                        quantitySpinner.getValue();

        if (quantity > medicine.stock) {

            JOptionPane.showMessageDialog(
                    this,
                    "Only "
                            + medicine.stock
                            + " units are available.",
                    "Insufficient Stock",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Check if already in cart
        for (
                int i = 0;
                i < cartModel.getRowCount();
                i++
        ) {

            int existingId =
                    Integer.parseInt(
                            cartModel
                                    .getValueAt(
                                            i,
                                            0
                                    )
                                    .toString()
                    );

            if (existingId == medicine.id) {

                int existingQuantity =
                        Integer.parseInt(
                                cartModel
                                        .getValueAt(
                                                i,
                                                3
                                        )
                                        .toString()
                        );

                int newQuantity =
                        existingQuantity
                                + quantity;

                if (
                        newQuantity
                                > medicine.stock
                ) {

                    JOptionPane.showMessageDialog(
                            this,
                            "You cannot add more than "
                                    + medicine.stock
                                    + " units.",
                            "Insufficient Stock",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                double subtotal =
                        newQuantity
                                * medicine.price;

                cartModel.setValueAt(
                        newQuantity,
                        i,
                        3
                );

                cartModel.setValueAt(
                        String.format(
                                "R%.2f",
                                subtotal
                        ),
                        i,
                        4
                );

                calculateTotal();

                return;
            }
        }

        double subtotal =
                medicine.price
                        * quantity;

        cartModel.addRow(
                new Object[]{
                        medicine.id,
                        medicine.name,
                        String.format(
                                "R%.2f",
                                medicine.price
                        ),
                        quantity,
                        String.format(
                                "R%.2f",
                                subtotal
                        )
                }
        );

        calculateTotal();
    }

    // =========================================================
    // REMOVE SELECTED ITEM
    // =========================================================

    private void removeSelectedItem() {

        int row =
                cartTable.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an item to remove.",
                    "Remove Item",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        cartModel.removeRow(row);

        calculateTotal();
    }

    // =========================================================
    // CLEAR CART
    // =========================================================

    private void clearCart() {

        if (
                cartModel.getRowCount()
                        == 0
        ) {
            return;
        }

        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        "Clear all items from the cart?",
                        "Clear Cart",
                        JOptionPane.YES_NO_OPTION
                );

        if (
                choice
                        == JOptionPane.YES_OPTION
        ) {

            cartModel.setRowCount(0);

            total = 0.0;

            totalLabel.setText(
                    "TOTAL: R0.00"
            );
        }
    }

    // =========================================================
    // CALCULATE TOTAL
    // =========================================================

    private void calculateTotal() {

        total = 0.0;

        for (
                int i = 0;
                i < cartModel.getRowCount();
                i++
        ) {

            String subtotalText =
                    cartModel
                            .getValueAt(
                                    i,
                                    4
                            )
                            .toString()
                            .replace(
                                    "R",
                                    ""
                            )
                            .trim();

            total +=
                    Double.parseDouble(
                            subtotalText
                    );
        }

        totalLabel.setText(
                String.format(
                        "TOTAL: R%.2f",
                        total
                )
        );
    }

    // =========================================================
    // CHECKOUT
    // =========================================================

    private void checkout() {

        if (
                cartModel.getRowCount()
                        == 0
        ) {

            JOptionPane.showMessageDialog(
                    this,
                    "The cart is empty.",
                    "Checkout",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (userId <= 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Invalid cashier user ID.",
                    "Checkout Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        int choice =
                JOptionPane.showConfirmDialog(
                        this,
                        String.format(
                                "Complete sale for R%.2f?",
                                total
                        ),
                        "Confirm Checkout",
                        JOptionPane.YES_NO_OPTION
                );

        if (
                choice
                        != JOptionPane.YES_OPTION
        ) {
            return;
        }

        Connection conn = null;

        try {

            conn =
                    DBConnection.getConnection();

            conn.setAutoCommit(false);

            // =================================================
            // INSERT SALE
            // =================================================

            String saleSQL =
                    "INSERT INTO sales " +
                            "(total_amount, user_id) " +
                            "VALUES (?, ?)";

            int saleId;

            try (
                    PreparedStatement saleStatement =
                            conn.prepareStatement(
                                    saleSQL,
                                    Statement.RETURN_GENERATED_KEYS
                            )
            ) {

                saleStatement.setDouble(
                        1,
                        total
                );

                saleStatement.setInt(
                        2,
                        userId
                );

                saleStatement.executeUpdate();

                try (
                        ResultSet keys =
                                saleStatement
                                        .getGeneratedKeys()
                ) {

                    if (!keys.next()) {

                        throw new SQLException(
                                "Could not create sale."
                        );
                    }

                    saleId =
                            keys.getInt(1);
                }
            }

            // =================================================
            // INSERT SALE ITEMS
            // =================================================

            String itemSQL =
                    "INSERT INTO sale_items " +
                            "(sale_id, medicine_id, " +
                            "quantity_sold, price_at_sale) " +
                            "VALUES (?, ?, ?, ?)";

            String stockSQL =
                    "UPDATE medicines " +
                            "SET quantity_in_stock = " +
                            "quantity_in_stock - ? " +
                            "WHERE medicine_id = ? " +
                            "AND quantity_in_stock >= ?";

            for (
                    int i = 0;
                    i < cartModel.getRowCount();
                    i++
            ) {

                int medicineId =
                        Integer.parseInt(
                                cartModel
                                        .getValueAt(
                                                i,
                                                0
                                        )
                                        .toString()
                        );

                int quantity =
                        Integer.parseInt(
                                cartModel
                                        .getValueAt(
                                                i,
                                                3
                                        )
                                        .toString()
                        );

                String priceText =
                        cartModel
                                .getValueAt(
                                        i,
                                        2
                                )
                                .toString()
                                .replace(
                                        "R",
                                        ""
                                )
                                .trim();

                double price =
                        Double.parseDouble(
                                priceText
                        );

                // Insert item
                try (
                        PreparedStatement itemStatement =
                                conn.prepareStatement(
                                        itemSQL
                                )
                ) {

                    itemStatement.setInt(
                            1,
                            saleId
                    );

                    itemStatement.setInt(
                            2,
                            medicineId
                    );

                    itemStatement.setInt(
                            3,
                            quantity
                    );

                    itemStatement.setDouble(
                            4,
                            price
                    );

                    itemStatement.executeUpdate();
                }

                // Reduce stock
                try (
                        PreparedStatement stockStatement =
                                conn.prepareStatement(
                                        stockSQL
                                )
                ) {

                    stockStatement.setInt(
                            1,
                            quantity
                    );

                    stockStatement.setInt(
                            2,
                            medicineId
                    );

                    stockStatement.setInt(
                            3,
                            quantity
                    );

                    int updated =
                            stockStatement
                                    .executeUpdate();

                    if (updated == 0) {

                        throw new SQLException(
                                "Insufficient stock for "
                                        + cartModel
                                        .getValueAt(
                                                i,
                                                1
                                        )
                        );
                    }
                }
            }

            conn.commit();

            // Create bill
            String bill =
                    createBill(saleId);

            showBill(bill);

            // Clear cart
            cartModel.setRowCount(0);

            total = 0.0;

            totalLabel.setText(
                    "TOTAL: R0.00"
            );

            // Refresh medicine list
            loadMedicines();

        } catch (SQLException ex) {

            if (conn != null) {

                try {
                    conn.rollback();
                } catch (SQLException ignored) {
                }
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Checkout failed:\n"
                            + ex.getMessage(),
                    "Checkout Error",
                    JOptionPane.ERROR_MESSAGE
            );

        } finally {

            if (conn != null) {

                try {

                    conn.setAutoCommit(true);

                    conn.close();

                } catch (SQLException ignored) {
                }
            }
        }
    }

    // =========================================================
    // CREATE BILL
    // =========================================================

    private String createBill(
            int saleId
    ) {

        StringBuilder bill =
                new StringBuilder();

        String date =
                new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss"
                ).format(new Date());

        bill.append(
                "====================================\n"
        );

        bill.append(
                "       HEALTHFIRST PHARMACY\n"
        );

        bill.append(
                "             INVOICE\n"
        );

        bill.append(
                "====================================\n"
        );

        bill.append(
                "Sale ID: "
        );

        bill.append(saleId);

        bill.append("\n");

        bill.append(
                "Date: "
        );

        bill.append(date);

        bill.append("\n");

        bill.append(
                "------------------------------------\n"
        );

        for (
                int i = 0;
                i < cartModel.getRowCount();
                i++
        ) {

            String medicine =
                    cartModel
                            .getValueAt(
                                    i,
                                    1
                            )
                            .toString();

            String price =
                    cartModel
                            .getValueAt(
                                    i,
                                    2
                            )
                            .toString();

            String quantity =
                    cartModel
                            .getValueAt(
                                    i,
                                    3
                            )
                            .toString();

            String subtotal =
                    cartModel
                            .getValueAt(
                                    i,
                                    4
                            )
                            .toString();

            bill.append(medicine);

            bill.append("\n");

            bill.append(
                    "  "
            );

            bill.append(quantity);

            bill.append(
                    " x "
            );

            bill.append(price);

            bill.append(
                    " = "
            );

            bill.append(subtotal);

            bill.append("\n");
        }

        bill.append(
                "------------------------------------\n"
        );

        bill.append(
                String.format(
                        "TOTAL: R%.2f\n",
                        total
                )
        );

        bill.append(
                "====================================\n"
        );

        bill.append(
                "       Thank you for your purchase!\n"
        );

        bill.append(
                "====================================\n"
        );

        return bill.toString();
    }

    // =========================================================
    // SHOW BILL
    // =========================================================

    private void showBill(
            String billText
    ) {

        JTextArea billArea =
                new JTextArea(
                        billText
                );

        billArea.setFont(
                new Font(
                        "Monospaced",
                        Font.PLAIN,
                        14
                )
        );

        billArea.setEditable(false);

        JScrollPane scrollPane =
                new JScrollPane(
                        billArea
                );

        scrollPane.setPreferredSize(
                new Dimension(
                        450,
                        450
                )
        );

        JButton saveButton =
                new JButton("Save Bill");

        JButton printButton =
                new JButton("Print");

        JButton closeButton =
                new JButton("Close");

        JPanel buttonPanel =
                new JPanel();

        buttonPanel.add(
                saveButton
        );

        buttonPanel.add(
                printButton
        );

        buttonPanel.add(
                closeButton
        );

        JDialog dialog =
                new JDialog(
                        SwingUtilities
                                .getWindowAncestor(
                                        this
                                ),
                        "HealthFirst Pharmacy - Bill",
                        Dialog.ModalityType
                                .APPLICATION_MODAL
                );

        dialog.setLayout(
                new BorderLayout(
                        10,
                        10
                )
        );

        dialog.add(
                scrollPane,
                BorderLayout.CENTER
        );

        dialog.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        dialog.setSize(
                500,
                550
        );

        dialog.setLocationRelativeTo(
                this
        );

        // =====================================================
        // SAVE BILL
        // =====================================================

        saveButton.addActionListener(
                e -> {

                    JFileChooser fileChooser =
                            new JFileChooser();

                    fileChooser.setSelectedFile(
                            new File(
                                    "HealthFirst_Bill.txt"
                            )
                    );

                    int result =
                            fileChooser
                                    .showSaveDialog(
                                            dialog
                                    );

                    if (
                            result
                                    == JFileChooser
                                    .APPROVE_OPTION
                    ) {

                        File file =
                                fileChooser
                                        .getSelectedFile();

                        try (
                                FileWriter writer =
                                        new FileWriter(
                                                file
                                        )
                        ) {

                            writer.write(
                                    billText
                            );

                            JOptionPane.showMessageDialog(
                                    dialog,
                                    "Bill saved successfully."
                            );

                        } catch (IOException ex) {

                            JOptionPane.showMessageDialog(
                                    dialog,
                                    "Could not save bill:\n"
                                            + ex.getMessage(),
                                    "Save Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        }
                    }
                }
        );

        // =====================================================
        // PRINT BILL
        // =====================================================

        printButton.addActionListener(
                e -> {

                    try {

                        boolean complete =
                                billArea.print();

                        if (complete) {

                            JOptionPane.showMessageDialog(
                                    dialog,
                                    "Bill sent to printer."
                            );
                        }

                    } catch (PrinterException ex) {

                        JOptionPane.showMessageDialog(
                                dialog,
                                "Could not print bill:\n"
                                        + ex.getMessage(),
                                "Print Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    }
                }
        );

        // =====================================================
        // CLOSE
        // =====================================================

        closeButton.addActionListener(
                e -> dialog.dispose()
        );

        dialog.setVisible(true);
    }

    // =========================================================
    // MEDICINE CLASS
    // =========================================================

    private static class Medicine {

        int id;
        String name;
        double price;
        int stock;

        Medicine(
                int id,
                String name,
                double price,
                int stock
        ) {

            this.id = id;
            this.name = name;
            this.price = price;
            this.stock = stock;
        }

        @Override
        public String toString() {

            return name
                    + " - R"
                    + String.format(
                    "%.2f",
                    price
            )
                    + " (Stock: "
                    + stock
                    + ")";
        }
    }
}
