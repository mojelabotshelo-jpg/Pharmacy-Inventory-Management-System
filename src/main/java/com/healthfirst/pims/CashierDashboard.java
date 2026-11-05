package com.healthfirst.pims;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class CashierDashboard extends JFrame {

    private int userId;

    // POS components
    private JTextField medicineSearchField;
    private JTextField quantityField;

    private JTable medicineTable;
    private JTable cartTable;

    private DefaultTableModel medicineModel;
    private DefaultTableModel cartModel;

    private JLabel totalLabel;

    private double totalAmount = 0.00;

    public CashierDashboard(int userId, String fullName) {

        this.userId = userId;

        setTitle("HealthFirst PIMS - Cashier Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel mainPanel = new JPanel(new BorderLayout());

        // =====================================================
        // HEADER
        // =====================================================

        JPanel headerPanel = new JPanel(new BorderLayout());

        headerPanel.setBackground(new Color(30, 136, 229));

        headerPanel.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 20, 15, 20
                )
        );

        JLabel titleLabel = new JLabel(
                "HealthFirst Pharmacy - Cashier"
        );

        titleLabel.setForeground(Color.WHITE);

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 22)
        );

        JLabel welcomeLabel = new JLabel(
                "Welcome, " + fullName
        );

        welcomeLabel.setForeground(Color.WHITE);

        welcomeLabel.setFont(
                new Font("Arial", Font.PLAIN, 16)
        );

        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(welcomeLabel, BorderLayout.EAST);

        // =====================================================
        // TABS
        // =====================================================

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab(
                "Point of Sale",
                createPOSPanel()
        );

        tabbedPane.addTab(
                "Stock Check",
                createStockCheckPanel()
        );

        // =====================================================
        // LOGOUT
        // =====================================================

        JButton logoutButton = new JButton("Logout");

        logoutButton.setPreferredSize(
                new Dimension(100, 35)
        );

        logoutButton.addActionListener(e -> {

            int choice = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (choice == JOptionPane.YES_OPTION) {

                dispose();

                new Login().setVisible(true);
            }
        });

        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        bottomPanel.add(logoutButton);

        // =====================================================
        // ADD COMPONENTS
        // =====================================================

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

    // =========================================================
    // POINT OF SALE PANEL
    // =========================================================

    private JPanel createPOSPanel() {

        JPanel panel = new JPanel(
                new BorderLayout(10, 10)
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        15, 15, 15, 15
                )
        );

        // =====================================================
        // TITLE
        // =====================================================

        JLabel titleLabel = new JLabel(
                "Point of Sale",
                SwingConstants.CENTER
        );

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 26)
        );

        titleLabel.setForeground(
                new Color(30, 136, 229)
        );

        panel.add(titleLabel, BorderLayout.NORTH);

        // =====================================================
        // LEFT SIDE - MEDICINE SEARCH
        // =====================================================

        JPanel leftPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        leftPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Available Medicines"
                )
        );

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        searchPanel.add(
                new JLabel("Search Medicine:")
        );

        medicineSearchField = new JTextField(18);

        JButton searchButton = new JButton("Search");

        JButton showAllButton = new JButton("Show All");

        searchPanel.add(medicineSearchField);
        searchPanel.add(searchButton);
        searchPanel.add(showAllButton);

        leftPanel.add(
                searchPanel,
                BorderLayout.NORTH
        );

        // =====================================================
        // MEDICINE TABLE
        // =====================================================

        medicineModel = new DefaultTableModel(
                new String[]{
                        "ID",
                        "Medicine",
                        "Company",
                        "Type",
                        "Price",
                        "Stock"
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

        medicineTable = new JTable(medicineModel);

        medicineTable.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION
        );

        medicineTable.setRowHeight(25);

        JScrollPane medicineScrollPane =
                new JScrollPane(medicineTable);

        leftPanel.add(
                medicineScrollPane,
                BorderLayout.CENTER
        );

        // =====================================================
        // ADD TO CART PANEL
        // =====================================================

        JPanel addPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT)
        );

        addPanel.add(
                new JLabel("Quantity:")
        );

        quantityField = new JTextField("1", 5);

        JButton addToCartButton =
                new JButton("Add to Cart");

        addPanel.add(quantityField);
        addPanel.add(addToCartButton);

        leftPanel.add(
                addPanel,
                BorderLayout.SOUTH
        );

        // =====================================================
        // RIGHT SIDE - CART
        // =====================================================

        JPanel rightPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        rightPanel.setBorder(
                BorderFactory.createTitledBorder(
                        "Shopping Cart"
                )
        );

        cartModel = new DefaultTableModel(
                new String[]{
                        "ID",
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

        cartTable = new JTable(cartModel);

        cartTable.setRowHeight(25);

        JScrollPane cartScrollPane =
                new JScrollPane(cartTable);

        rightPanel.add(
                cartScrollPane,
                BorderLayout.CENTER
        );

        // =====================================================
        // CART BUTTONS
        // =====================================================

        JPanel cartButtonPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER)
        );

        JButton removeButton =
                new JButton("Remove Selected");

        JButton clearButton =
                new JButton("Clear Cart");

        JButton checkoutButton =
                new JButton("Checkout");

        cartButtonPanel.add(removeButton);
        cartButtonPanel.add(clearButton);
        cartButtonPanel.add(checkoutButton);

        rightPanel.add(
                cartButtonPanel,
                BorderLayout.SOUTH
        );

        // =====================================================
        // TOTAL
        // =====================================================

        totalLabel = new JLabel(
                "Total: R0.00",
                SwingConstants.CENTER
        );

        totalLabel.setFont(
                new Font("Arial", Font.BOLD, 20)
        );

        totalLabel.setForeground(
                new Color(30, 136, 229)
        );

        rightPanel.add(
                totalLabel,
                BorderLayout.NORTH
        );

        // =====================================================
        // SPLIT PANEL
        // =====================================================

        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT,
                leftPanel,
                rightPanel
        );

        splitPane.setDividerLocation(520);

        panel.add(
                splitPane,
                BorderLayout.CENTER
        );

        // =====================================================
        // BUTTON ACTIONS
        // =====================================================

        searchButton.addActionListener(e -> {

            String search =
                    medicineSearchField.getText().trim();

            loadMedicines(search);
        });

        showAllButton.addActionListener(e -> {

            medicineSearchField.setText("");

            loadMedicines("");
        });

        medicineSearchField.addActionListener(e -> {

            String search =
                    medicineSearchField.getText().trim();

            loadMedicines(search);
        });

        addToCartButton.addActionListener(e ->
                addSelectedMedicineToCart()
        );

        removeButton.addActionListener(e ->
                removeSelectedCartItem()
        );

        clearButton.addActionListener(e ->
                clearCart()
        );

        checkoutButton.addActionListener(e ->
                checkout()
        );

        // Load medicines when POS opens
        loadMedicines("");

        return panel;
    }

    // =========================================================
    // LOAD MEDICINES
    // =========================================================

    private void loadMedicines(String search) {

        medicineModel.setRowCount(0);

        String sql =
                "SELECT medicine_id, name, company, " +
                        "medicine_type, price, quantity_in_stock " +
                        "FROM medicines " +
                        "WHERE name LIKE ? " +
                        "ORDER BY name";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    "%" + search + "%"
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                while (resultSet.next()) {

                    medicineModel.addRow(
                            new Object[]{
                                    resultSet.getInt("medicine_id"),
                                    resultSet.getString("name"),
                                    resultSet.getString("company"),
                                    resultSet.getString("medicine_type"),

                                    // Always display decimal point
                                    String.format(
                                            Locale.US,
                                            "R%.2f",
                                            resultSet.getDouble("price")
                                    ),

                                    resultSet.getInt("quantity_in_stock")
                            }
                    );
                }
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error loading medicines:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }

    // =========================================================
    // ADD MEDICINE TO CART
    // =========================================================

    private void addSelectedMedicineToCart() {

        int selectedRow =
                medicineTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a medicine first.",
                    "Add to Cart",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int medicineId =
                (int) medicineModel.getValueAt(
                        selectedRow,
                        0
                );

        String medicineName =
                medicineModel.getValueAt(
                        selectedRow,
                        1
                ).toString();

        // Corrected price conversion
        double price =
                parsePrice(
                        medicineModel.getValueAt(
                                selectedRow,
                                4
                        ).toString()
                );

        int availableStock =
                (int) medicineModel.getValueAt(
                        selectedRow,
                        5
                );

        int quantity;

        try {

            quantity = Integer.parseInt(
                    quantityField.getText().trim()
            );

            if (quantity <= 0) {

                throw new NumberFormatException();
            }

        } catch (NumberFormatException ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid quantity.",
                    "Invalid Quantity",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (quantity > availableStock) {

            JOptionPane.showMessageDialog(
                    this,
                    "Not enough stock available.\n"
                            + "Available stock: "
                            + availableStock,
                    "Insufficient Stock",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Check if medicine is already in cart
        for (int i = 0; i < cartModel.getRowCount(); i++) {

            int existingId =
                    (int) cartModel.getValueAt(i, 0);

            if (existingId == medicineId) {

                int existingQuantity =
                        (int) cartModel.getValueAt(i, 3);

                int newQuantity =
                        existingQuantity + quantity;

                if (newQuantity > availableStock) {

                    JOptionPane.showMessageDialog(
                            this,
                            "The total quantity in the cart "
                                    + "cannot exceed available stock.",
                            "Insufficient Stock",
                            JOptionPane.WARNING_MESSAGE
                    );

                    return;
                }

                double subtotal =
                        price * newQuantity;

                cartModel.setValueAt(
                        newQuantity,
                        i,
                        3
                );

                cartModel.setValueAt(
                        formatMoney(subtotal),
                        i,
                        4
                );

                calculateTotal();

                return;
            }
        }

        double subtotal = price * quantity;

        cartModel.addRow(
                new Object[]{
                        medicineId,
                        medicineName,
                        formatMoney(price),
                        quantity,
                        formatMoney(subtotal)
                }
        );

        calculateTotal();

        quantityField.setText("1");
    }

    // =========================================================
    // PRICE CONVERSION
    // =========================================================

    private double parsePrice(String priceText) {

        return Double.parseDouble(
                priceText
                        .replace("R", "")
                        .replace(",", ".")
                        .trim()
        );
    }

    // =========================================================
    // FORMAT MONEY
    // =========================================================

    private String formatMoney(double amount) {

        return String.format(
                Locale.US,
                "R%.2f",
                amount
        );
    }

    // =========================================================
    // CALCULATE TOTAL
    // =========================================================

    private void calculateTotal() {

        totalAmount = 0.00;

        for (int i = 0; i < cartModel.getRowCount(); i++) {

            String subtotalText =
                    cartModel.getValueAt(i, 4)
                            .toString();

            totalAmount += parsePrice(subtotalText);
        }

        totalLabel.setText(
                "Total: " + formatMoney(totalAmount)
        );
    }

    // =========================================================
    // REMOVE CART ITEM
    // =========================================================

    private void removeSelectedCartItem() {

        int selectedRow =
                cartTable.getSelectedRow();

        if (selectedRow == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select an item to remove.",
                    "Remove Item",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        cartModel.removeRow(selectedRow);

        calculateTotal();
    }

    // =========================================================
    // CLEAR CART
    // =========================================================

    private void clearCart() {

        if (cartModel.getRowCount() == 0) {

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to clear the cart?",
                "Clear Cart",
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {

            cartModel.setRowCount(0);

            calculateTotal();
        }
    }

    // =========================================================
    // CHECKOUT
    // =========================================================

    private void checkout() {

        if (cartModel.getRowCount() == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "The cart is empty.",
                    "Checkout",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        int choice = JOptionPane.showConfirmDialog(
                this,
                "Complete sale for " + formatMoney(totalAmount) + "?",
                "Confirm Checkout",
                JOptionPane.YES_NO_OPTION
        );

        if (choice != JOptionPane.YES_OPTION) {

            return;
        }

        Connection connection = null;

        try {

            connection = DBConnection.getConnection();

            connection.setAutoCommit(false);

            // =================================================
            // INSERT SALE
            // =================================================

            String saleSql =
                    "INSERT INTO sales " +
                            "(total_amount, user_id) " +
                            "VALUES (?, ?)";

            int saleId;

            try (
                    PreparedStatement saleStatement =
                            connection.prepareStatement(
                                    saleSql,
                                    Statement.RETURN_GENERATED_KEYS
                            )
            ) {

                saleStatement.setDouble(
                        1,
                        totalAmount
                );

                saleStatement.setInt(
                        2,
                        userId
                );

                saleStatement.executeUpdate();

                try (
                        ResultSet keys =
                                saleStatement.getGeneratedKeys()
                ) {

                    if (!keys.next()) {

                        throw new SQLException(
                                "Could not create sale."
                        );
                    }

                    saleId = keys.getInt(1);
                }
            }

            // =================================================
            // INSERT SALE ITEMS AND UPDATE STOCK
            // =================================================

            String itemSql =
                    "INSERT INTO sale_items " +
                            "(sale_id, medicine_id, " +
                            "quantity_sold, price_at_sale) " +
                            "VALUES (?, ?, ?, ?)";

            String stockSql =
                    "UPDATE medicines " +
                            "SET quantity_in_stock = " +
                            "quantity_in_stock - ? " +
                            "WHERE medicine_id = ? " +
                            "AND quantity_in_stock >= ?";

            try (
                    PreparedStatement itemStatement =
                            connection.prepareStatement(itemSql);

                    PreparedStatement stockStatement =
                            connection.prepareStatement(stockSql)
            ) {

                for (int i = 0; i < cartModel.getRowCount(); i++) {

                    int medicineId =
                            (int) cartModel.getValueAt(i, 0);

                    double price =
                            parsePrice(
                                    cartModel.getValueAt(i, 2)
                                            .toString()
                            );

                    int quantity =
                            (int) cartModel.getValueAt(i, 3);

                    // Insert sale item
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

                    // Update stock
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

                    int updatedRows =
                            stockStatement.executeUpdate();

                    if (updatedRows == 0) {

                        throw new SQLException(
                                "Insufficient stock for medicine ID: "
                                        + medicineId
                        );
                    }
                }
            }

            connection.commit();

            // Generate bill BEFORE clearing the cart
            showBill(saleId);

            // Clear cart
            cartModel.setRowCount(0);

            calculateTotal();

            // Refresh medicine stock
            loadMedicines("");

        } catch (Exception ex) {

            try {

                if (connection != null) {

                    connection.rollback();
                }

            } catch (SQLException rollbackEx) {

                rollbackEx.printStackTrace();
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Checkout failed:\n"
                            + ex.getMessage(),
                    "Checkout Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();

        } finally {

            try {

                if (connection != null) {

                    connection.setAutoCommit(true);
                    connection.close();
                }

            } catch (SQLException closeEx) {

                closeEx.printStackTrace();
            }
        }
    }

    // =========================================================
    // BILL WINDOW
    // =========================================================

    private void showBill(int saleId) {

        JTextArea billArea = new JTextArea();

        billArea.setFont(
                new Font("Monospaced", Font.PLAIN, 14)
        );

        billArea.setEditable(false);

        StringBuilder bill =
                new StringBuilder();

        bill.append(
                "====================================\n"
        );

        bill.append(
                "       HEALTHFIRST PHARMACY\n"
        );

        bill.append(
                "             CUSTOMER BILL\n"
        );

        bill.append(
                "====================================\n"
        );

        bill.append(
                "Sale ID: " + saleId + "\n"
        );

        bill.append(
                "Date: "
                        + new SimpleDateFormat(
                        "yyyy-MM-dd HH:mm:ss"
                ).format(new Date())
                        + "\n"
        );

        bill.append(
                "Cashier ID: " + userId + "\n"
        );

        bill.append(
                "------------------------------------\n"
        );

        bill.append(
                String.format(
                        Locale.US,
                        "%-18s %5s %10s\n",
                        "Medicine",
                        "Qty",
                        "Subtotal"
                )
        );

        bill.append(
                "------------------------------------\n"
        );

        for (int i = 0; i < cartModel.getRowCount(); i++) {

            String medicineName =
                    cartModel.getValueAt(i, 1)
                            .toString();

            int quantity =
                    (int) cartModel.getValueAt(i, 3);

            String subtotal =
                    cartModel.getValueAt(i, 4)
                            .toString();

            bill.append(
                    String.format(
                            Locale.US,
                            "%-18s %5d %10s\n",
                            medicineName,
                            quantity,
                            subtotal
                    )
            );
        }

        bill.append(
                "------------------------------------\n"
        );

        bill.append(
                "TOTAL: " + formatMoney(totalAmount) + "\n"
        );

        bill.append(
                "====================================\n"
        );

        bill.append(
                "       Thank you for shopping!\n"
        );

        bill.append(
                "====================================\n"
        );

        billArea.setText(bill.toString());

        JScrollPane scrollPane =
                new JScrollPane(billArea);

        scrollPane.setPreferredSize(
                new Dimension(500, 400)
        );

        JButton saveButton =
                new JButton("Save Bill");

        JButton closeButton =
                new JButton("Close");

        JPanel buttonPanel = new JPanel();

        buttonPanel.add(saveButton);
        buttonPanel.add(closeButton);

        JPanel billPanel = new JPanel(
                new BorderLayout()
        );

        billPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        billPanel.add(
                buttonPanel,
                BorderLayout.SOUTH
        );

        JDialog billDialog = new JDialog(
                this,
                "Customer Bill",
                true
        );

        billDialog.setSize(550, 500);

        billDialog.setLocationRelativeTo(this);

        billDialog.add(billPanel);

        saveButton.addActionListener(e -> {

            JFileChooser fileChooser =
                    new JFileChooser();

            fileChooser.setSelectedFile(
                    new java.io.File(
                            "Bill_" + saleId + ".txt"
                    )
            );

            int result =
                    fileChooser.showSaveDialog(
                            billDialog
                    );

            if (result ==
                    JFileChooser.APPROVE_OPTION) {

                try (
                        java.io.FileWriter writer =
                                new java.io.FileWriter(
                                        fileChooser
                                                .getSelectedFile()
                                )
                ) {

                    writer.write(
                            billArea.getText()
                    );

                    JOptionPane.showMessageDialog(
                            billDialog,
                            "Bill saved successfully."
                    );

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            billDialog,
                            "Error saving bill:\n"
                                    + ex.getMessage(),
                            "Save Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }
        });

        closeButton.addActionListener(e ->
                billDialog.dispose()
        );

        billDialog.setVisible(true);
    }

    // =========================================================
    // STOCK CHECK
    // =========================================================

    private JPanel createStockCheckPanel() {

        JPanel panel = new JPanel(
                new BorderLayout(15, 15)
        );

        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        30, 30, 30, 30
                )
        );

        JLabel title = new JLabel(
                "Medicine Stock Check",
                SwingConstants.CENTER
        );

        title.setFont(
                new Font("Arial", Font.BOLD, 24)
        );

        title.setForeground(
                new Color(30, 136, 229)
        );

        panel.add(title, BorderLayout.NORTH);

        JPanel searchPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER)
        );

        JLabel searchLabel =
                new JLabel("Medicine Name:");

        JTextField searchField =
                new JTextField(25);

        JButton searchButton =
                new JButton("Search");

        searchPanel.add(searchLabel);
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        JTextArea resultArea = new JTextArea();

        resultArea.setEditable(false);

        resultArea.setFont(
                new Font("Monospaced", Font.PLAIN, 15)
        );

        resultArea.setBorder(
                BorderFactory.createTitledBorder(
                        "Search Results"
                )
        );

        JScrollPane scrollPane =
                new JScrollPane(resultArea);

        JPanel centerPanel = new JPanel(
                new BorderLayout(10, 10)
        );

        centerPanel.add(
                searchPanel,
                BorderLayout.NORTH
        );

        centerPanel.add(
                scrollPane,
                BorderLayout.CENTER
        );

        panel.add(
                centerPanel,
                BorderLayout.CENTER
        );

        searchButton.addActionListener(e -> {

            String search =
                    searchField.getText().trim();

            if (search.isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please enter a medicine name.",
                        "Search",
                        JOptionPane.WARNING_MESSAGE
                );

                return;
            }

            searchMedicine(
                    search,
                    resultArea
            );
        });

        searchField.addActionListener(e -> {

            String search =
                    searchField.getText().trim();

            if (!search.isEmpty()) {

                searchMedicine(
                        search,
                        resultArea
                );
            }
        });

        return panel;
    }

    // =========================================================
    // SEARCH MEDICINE IN DATABASE
    // =========================================================

    private void searchMedicine(
            String search,
            JTextArea resultArea
    ) {

        String sql =
                "SELECT name, company, medicine_type, " +
                        "price, quantity_in_stock, expiry_date " +
                        "FROM medicines " +
                        "WHERE name LIKE ? " +
                        "ORDER BY name";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    "%" + search + "%"
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                StringBuilder result =
                        new StringBuilder();

                boolean found = false;

                while (resultSet.next()) {

                    found = true;

                    result.append(
                            "Medicine: "
                                    + resultSet.getString("name")
                                    + "\n"
                    );

                    result.append(
                            "Company: "
                                    + resultSet.getString("company")
                                    + "\n"
                    );

                    result.append(
                            "Type: "
                                    + resultSet.getString("medicine_type")
                                    + "\n"
                    );

                    result.append(
                            "Price: "
                                    + formatMoney(
                                    resultSet.getDouble("price")
                            )
                                    + "\n"
                    );

                    result.append(
                            "Available Stock: "
                                    + resultSet.getInt(
                                    "quantity_in_stock"
                            )
                                    + "\n"
                    );

                    result.append(
                            "Expiry Date: "
                                    + resultSet.getDate("expiry_date")
                                    + "\n"
                    );

                    result.append(
                            "--------------------------------------\n"
                    );
                }

                if (!found) {

                    result.append(
                            "No medicine found."
                    );
                }

                resultArea.setText(
                        result.toString()
                );
            }

        } catch (Exception ex) {

            JOptionPane.showMessageDialog(
                    this,
                    "Error searching medicine:\n"
                            + ex.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE
            );

            ex.printStackTrace();
        }
    }
}