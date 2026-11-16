package com.nexora.helpdesk.desktop.ui;

import javax.swing.*;
import java.awt.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;

public class RegisterFrame extends JFrame {

    private JTextField nameField;
    private JTextField emailField;

    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;

    private JButton registerButton;
    private JButton loginButton;

    private final HttpClient httpClient =
            HttpClient.newHttpClient();

    private final ObjectMapper objectMapper =
            new ObjectMapper();

    private static final String REGISTER_URL =
            "http://localhost:8080/api/auth/register";

    public RegisterFrame() {

        setTitle("HelpDesk - User Registration");
        setSize(450, 520);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel mainPanel =
                new JPanel(new GridBagLayout());

        mainPanel.setBackground(Color.WHITE);

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(15, 25, 15, 25)
        );

        GridBagConstraints gbc =
                new GridBagConstraints();

        gbc.insets = new Insets(7, 12, 7, 12);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1;

        // Heading
        JLabel titleLabel =
                new JLabel("Create Account");

        titleLabel.setFont(
                new Font("Arial", Font.BOLD, 25)
        );

        titleLabel.setForeground(
                new Color(40, 100, 200)
        );

        titleLabel.setHorizontalAlignment(
                SwingConstants.CENTER
        );

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;

        mainPanel.add(titleLabel, gbc);

        // Name
        addLabel(mainPanel, gbc, "Full Name:", 1);

        nameField = new JTextField(20);
        addField(mainPanel, gbc, nameField, 2);

        // Email
        addLabel(mainPanel, gbc, "Email:", 3);

        emailField = new JTextField(20);
        addField(mainPanel, gbc, emailField, 4);

        // Password
        addLabel(mainPanel, gbc, "Password:", 5);

        passwordField = new JPasswordField(20);
        addField(mainPanel, gbc, passwordField, 6);

        // Confirm Password
        addLabel(mainPanel, gbc, "Confirm Password:", 7);

        confirmPasswordField = new JPasswordField(20);
        addField(mainPanel, gbc, confirmPasswordField, 8);

        // Register Button
        registerButton = new JButton("Register");

        styleButton(registerButton);

        gbc.gridx = 0;
        gbc.gridy = 9;
        gbc.gridwidth = 2;

        gbc.insets = new Insets(25, 12, 7, 12);

        mainPanel.add(registerButton, gbc);

        // Login Button
        loginButton = new JButton(
                "Already have an account? Login"
        );

        styleButton(loginButton);

        gbc.gridy = 10;
        gbc.insets = new Insets(7, 12, 7, 12);
        mainPanel.add(loginButton, gbc);

        // Add Panel
        add(mainPanel);

        // Button Actions
        registerButton.addActionListener(
                e -> registerUser()
        );

        loginButton.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });
    }

    // Label Method
    private void addLabel(
            JPanel panel,
            GridBagConstraints gbc,
            String text,
            int row) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;

        JLabel label = new JLabel(text);

        label.setFont(
                new Font("Arial", Font.PLAIN, 14)
        );

        panel.add(label, gbc);
    }

    // Text Field Method
    private void addField(
            JPanel panel,
            GridBagConstraints gbc,
            JComponent field,
            int row) {

        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 2;

        panel.add(field, gbc);
    }

 // Button Styling
    private void styleButton(JButton button) {

        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);

        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorderPainted(true);

        button.setRolloverEnabled(false);

        button.setFont(new Font("Arial", Font.BOLD, 14));

        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }


    // Registration Method
    private void registerUser() {

        String name = nameField.getText().trim();
        String email = emailField.getText().trim();

        char[] password = passwordField.getPassword();
        char[] confirmPassword =
                confirmPasswordField.getPassword();

        // Empty Field Validation
        if (name.isEmpty() ||
                email.isEmpty() ||
                password.length == 0 ||
                confirmPassword.length == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            Arrays.fill(password, '\0');
            Arrays.fill(confirmPassword, '\0');
            return;
        }

        // Email Validation
        if (!email.matches(
                "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a valid email address."
            );

            Arrays.fill(password, '\0');
            Arrays.fill(confirmPassword, '\0');
            return;
        }

        // Password Match Validation
        if (!Arrays.equals(password, confirmPassword)) {

            JOptionPane.showMessageDialog(
                    this,
                    "Passwords do not match.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            Arrays.fill(password, '\0');
            Arrays.fill(confirmPassword, '\0');
            return;
        }

        if (password.length < 8) {

            JOptionPane.showMessageDialog(
                    this,
                    "Password must contain at least 8 characters."
            );

            Arrays.fill(password, '\0');
            Arrays.fill(confirmPassword, '\0');
            return;
        }

        Arrays.fill(confirmPassword, '\0');

        registerButton.setEnabled(false);
        registerButton.setText("Registering...");

        // API Request
        new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground()
                    throws Exception {

                Map<String, String> requestData =
                        new HashMap<>();

                requestData.put("name", name);
                requestData.put("email", email);
                requestData.put(
                        "password",
                        new String(password)
                );

                String json =
                        objectMapper.writeValueAsString(
                                requestData
                        );

                HttpRequest request =
                        HttpRequest.newBuilder()
                                .uri(URI.create(REGISTER_URL))
                                .header(
                                        "Content-Type",
                                        "application/json"
                                )
                                .POST(
                                        HttpRequest.BodyPublishers.ofString(
                                                json,
                                                StandardCharsets.UTF_8
                                        )
                                )
                                .build();

                HttpResponse<String> response =
                        httpClient.send(
                                request,
                                HttpResponse.BodyHandlers.ofString()
                        );

                if (response.statusCode() >= 200 &&
                        response.statusCode() < 300) {

                    return "SUCCESS";
                }

                throw new RuntimeException(
                        "Registration failed: " +
                        response.body()
                );
            }

            @Override
            protected void done() {

                registerButton.setEnabled(true);
                registerButton.setText("Register");

                Arrays.fill(password, '\0');

                try {

                    get();

                    JOptionPane.showMessageDialog(
                            RegisterFrame.this,
                            "Registration successful! Please login.",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    new LoginFrame().setVisible(true);
                    dispose();

                } catch (Exception ex) {

                    String message =
                            ex.getCause() != null
                            ? ex.getCause().getMessage()
                            : ex.getMessage();

                    JOptionPane.showMessageDialog(
                            RegisterFrame.this,
                            message,
                            "Registration Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        }.execute();
    }
}
