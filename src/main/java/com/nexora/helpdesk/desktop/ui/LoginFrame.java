package com.nexora.helpdesk.desktop.ui;

import java.awt.*;
import java.util.Arrays;

import javax.swing.*;

import com.nexora.helpdesk.desktop.api.ApiClient;
import com.nexora.helpdesk.desktop.model.UserSession;
import com.nexora.helpdesk.desktop.util.SessionManager;

public class LoginFrame extends JFrame {

    private final JTextField emailField = new JTextField(20);

    private final JPasswordField passwordField = new JPasswordField(20);

    private final ApiClient api = new ApiClient();

    public LoginFrame() {

        setTitle("HelpDesk Portal - Login");

        setSize(430, 300);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel panel = new JPanel(new GridBagLayout());

        panel.setBorder(
                BorderFactory.createEmptyBorder(25, 35, 25, 35)
        );

        GridBagConstraints c = new GridBagConstraints();

        c.insets = new Insets(10, 8, 10, 8);

        c.fill = GridBagConstraints.HORIZONTAL;

        JLabel heading = new JLabel(
                "HelpDesk Portal",
                SwingConstants.CENTER
        );

        heading.setFont(new Font("Arial", Font.BOLD, 25));

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;

        panel.add(heading, c);

        c.gridwidth = 1;

        c.gridy++;
        c.gridx = 0;

        panel.add(new JLabel("Email"), c);

        c.gridx = 1;

        panel.add(emailField, c);

        c.gridy++;
        c.gridx = 0;

        panel.add(new JLabel("Password"), c);

        c.gridx = 1;

        panel.add(passwordField, c);

        JButton loginButton = new JButton("Login");

        c.gridy++;
        c.gridx = 0;
        c.gridwidth = 2;

        panel.add(loginButton, c);

        loginButton.addActionListener(e -> login(loginButton));

        add(panel);
    }

    private void login(JButton button) {

        String email = emailField.getText().trim();

        char[] password = passwordField.getPassword();

        if (email.isEmpty() || password.length == 0) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter email and password."
            );

            Arrays.fill(password, '\0');

            return;
        }

        button.setEnabled(false);

        new SwingWorker<UserSession, Void>() {

            @Override
            protected UserSession doInBackground() throws Exception {

                return api.login(email, password);
            }

            @Override
            protected void done() {

                button.setEnabled(true);

                try {

                    UserSession user = get();

                    if (user.getId() == 0 ||
                            user.getRole() == null ||
                            user.getRole().isBlank()) {

                        throw new RuntimeException(
                                "Unexpected login response from server."
                        );
                    }

                    SessionManager.start(user, email, password);

                    new DashboardFrame().setVisible(true);

                    dispose();

                } catch (Exception ex) {

                    JOptionPane.showMessageDialog(
                            LoginFrame.this,
                            ex.getMessage(),
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