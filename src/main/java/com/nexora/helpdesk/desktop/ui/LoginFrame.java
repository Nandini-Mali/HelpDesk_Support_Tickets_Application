package com.nexora.helpdesk.desktop.ui;

import java.awt.*;
import java.util.Arrays;

import javax.swing.*;

import com.nexora.helpdesk.desktop.api.ApiClient;
import com.nexora.helpdesk.desktop.model.UserSession;
import com.nexora.helpdesk.desktop.util.SessionManager;

public class LoginFrame extends JFrame {

    private final JTextField emailField = new JTextField(20);
    private final JPasswordField passwordField =
            new JPasswordField(20);

    private final ApiClient api = new ApiClient();

    private JButton loginButton;

    public LoginFrame() {

        setTitle("HelpDesk Portal - Login");
        setSize(450, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 35, 25, 35)
        );

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(10, 8, 10, 8);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        // Heading
        JLabel heading = new JLabel(
                "HelpDesk Portal",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 25));
        heading.setForeground(new Color(40, 100, 200));

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        panel.add(heading, c);

        // Subtitle
        JLabel subtitle = new JLabel(
                "Login to your account",
                SwingConstants.CENTER
        );

        subtitle.setFont(new Font("Arial", Font.PLAIN, 14));

        c.gridy = 1;
        panel.add(subtitle, c);

        // Email
        c.gridwidth = 1;
        c.gridy = 2;
        c.gridx = 0;

        panel.add(new JLabel("Email:"), c);

        c.gridx = 1;
        panel.add(emailField, c);

        // Password
        c.gridy = 3;
        c.gridx = 0;

        panel.add(new JLabel("Password:"), c);

        c.gridx = 1;
        panel.add(passwordField, c);

        // Login Button
        loginButton = new JButton("Login");

        styleButton(loginButton);

        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;

        panel.add(loginButton, c);

        loginButton.addActionListener(
                e -> login(loginButton)
        );

        // Register Button
        JButton registerButton =
                new JButton("Create New Account");

        styleButton(registerButton);

        c.gridy = 5;
        panel.add(registerButton, c);

        registerButton.addActionListener(e -> {
            new RegisterFrame().setVisible(true);
            dispose();
        });

        add(panel);

        getRootPane().setDefaultButton(loginButton);
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

    // Login Method
    private void login(JButton button) {

        String email = emailField.getText().trim();
        char[] password = passwordField.getPassword();

        if (email.isEmpty() || password.length == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter email and password.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            Arrays.fill(password, '\0');
            return;
        }

        button.setEnabled(false);
        button.setText("Logging in...");

        new SwingWorker<UserSession, Void>() {

            @Override
            protected UserSession doInBackground()
                    throws Exception {

                return api.login(email, password);
            }

            @Override
            protected void done() {

                button.setEnabled(true);
                button.setText("Login");

                try {

                    UserSession user = get();

                    if (user.getId() == 0 ||
                            user.getRole() == null ||
                            user.getRole().isBlank()) {

                        throw new RuntimeException(
                                "Invalid login response from server."
                        );
                    }

                    SessionManager.start(
                            user,
                            email,
                            password
                    );

                    new DashboardFrame().setVisible(true);
                    dispose();

                } catch (Exception ex) {

                    String message = ex.getCause() != null
                            ? ex.getCause().getMessage()
                            : ex.getMessage();

                    JOptionPane.showMessageDialog(
                            LoginFrame.this,
                            message,
                            "Login Failed",
                            JOptionPane.ERROR_MESSAGE
                    );

                } finally {

                    Arrays.fill(password, '\0');
                }
            }

        }.execute();
    }
}
