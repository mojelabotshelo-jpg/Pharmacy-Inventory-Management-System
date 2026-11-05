package com.healthfirst.pims;

import javax.swing.*;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Login extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;

    public Login() {

        setTitle("HealthFirst Pharmacy - Login");
        setSize(450, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(
                BorderFactory.createEmptyBorder(
                        20, 20, 20, 20
                )
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets =
                new Insets(8, 8, 8, 8);

        gbc.fill =
                GridBagConstraints.HORIZONTAL;

        // =========================
        // TITLE
        // =========================

        JLabel titleLabel =
                new JLabel(
                        "HealthFirst Pharmacy",
                        SwingConstants.CENTER
                );

        titleLabel.setFont(
                new Font(
                        "Arial",
                        Font.BOLD,
                        24
                )
        );

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        panel.add(titleLabel, gbc);

        // =========================
        // SUBTITLE
        // =========================

        JLabel subtitleLabel =
                new JLabel(
                        "Pharmacy Inventory Management System",
                        SwingConstants.CENTER
                );

        gbc.gridy = 1;

        panel.add(subtitleLabel, gbc);

        // =========================
        // USERNAME
        // =========================

        gbc.gridwidth = 1;

        gbc.gridx = 0;
        gbc.gridy = 2;

        panel.add(
                new JLabel("Username:"),
                gbc
        );

        usernameField =
                new JTextField(20);

        gbc.gridx = 1;

        panel.add(
                usernameField,
                gbc
        );

        // =========================
        // PASSWORD
        // =========================

        gbc.gridx = 0;
        gbc.gridy = 3;

        panel.add(
                new JLabel("Password:"),
                gbc
        );

        passwordField =
                new JPasswordField(20);

        gbc.gridx = 1;

        panel.add(
                passwordField,
                gbc
        );

        // =========================
        // LOGIN BUTTON
        // =========================

        JButton loginButton =
                new JButton("Login");

        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;

        panel.add(
                loginButton,
                gbc
        );

        add(panel);

        // Login button
        loginButton.addActionListener(
                e -> login()
        );

        // Press Enter in password field
        passwordField.addActionListener(
                e -> login()
        );
    }

    // =====================================================
    // LOGIN METHOD
    // =====================================================

    private void login() {

        String username =
                usernameField.getText().trim();

        String password =
                new String(
                        passwordField.getPassword()
                );

        // Check empty fields
        if (username.isEmpty()
                || password.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter username and password.",
                    "Login Error",
                    JOptionPane.ERROR_MESSAGE
            );

            return;
        }

        // SQL query
        String sql =
                "SELECT user_id, username, role, full_name " +
                        "FROM users " +
                        "WHERE username = ? AND password = ?";

        try (
                Connection connection =
                        DBConnection.getConnection();

                PreparedStatement statement =
                        connection.prepareStatement(sql)
        ) {

            statement.setString(
                    1,
                    username
            );

            statement.setString(
                    2,
                    password
            );

            try (
                    ResultSet resultSet =
                            statement.executeQuery()
            ) {

                // =========================
                // USER FOUND
                // =========================

                if (resultSet.next()) {

                    // IMPORTANT:
                    // Get the user ID from database
                    int userId =
                            resultSet.getInt(
                                    "user_id"
                            );

                    String role =
                            resultSet.getString(
                                    "role"
                            );

                    String fullName =
                            resultSet.getString(
                                    "full_name"
                            );

                    JOptionPane.showMessageDialog(
                            this,
                            "Welcome, "
                                    + fullName
                                    + "!"
                    );

                    // =========================
                    // ADMIN
                    // =========================

                    if (role.equalsIgnoreCase(
                            "Admin")) {

                        AdminDashboard
                                adminDashboard =
                                new AdminDashboard(
                                        fullName
                                );

                        adminDashboard.setVisible(
                                true
                        );

                    }

                    // =========================
                    // CASHIER
                    // =========================

                    else if (role.equalsIgnoreCase(
                            "Cashier")) {

                        CashierDashboard
                                cashierDashboard =
                                new CashierDashboard(
                                        userId,
                                        fullName
                                );

                        cashierDashboard.setVisible(
                                true
                        );

                    }

                    // =========================
                    // UNKNOWN ROLE
                    // =========================

                    else {

                        JOptionPane.showMessageDialog(
                                this,
                                "Unknown user role: "
                                        + role,
                                "Login Error",
                                JOptionPane.ERROR_MESSAGE
                        );

                        return;
                    }

                    // Close login window
                    dispose();

                }

                // =========================
                // USER NOT FOUND
                // =========================

                else {

                    JOptionPane.showMessageDialog(
                            this,
                            "Invalid username or password.",
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE
                    );

                    passwordField.setText("");

                    usernameField.requestFocus();
                }
            }

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    "Database error:\n"
                            + e.getMessage(),
                    "Connection Error",
                    JOptionPane.ERROR_MESSAGE
            );

            e.printStackTrace();
        }
    }

    // =====================================================
    // MAIN METHOD
    // =====================================================

    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {

            Login login =
                    new Login();

            login.setVisible(true);
        });
    }
}
