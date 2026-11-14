package com.nexora.helpdesk.desktop.ui;

import java.awt.*;
import java.util.List;

import javax.swing.*;

import com.nexora.helpdesk.desktop.api.ApiClient;
import com.nexora.helpdesk.desktop.model.Ticket;
import com.nexora.helpdesk.desktop.util.SessionManager;

public class DashboardFrame extends JFrame {

    private final ApiClient api = new ApiClient();

    private final JLabel summary = new JLabel("Loading tickets...");

    public DashboardFrame() {

        setTitle("HelpDesk Desktop Dashboard");

        setSize(850, 500);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JPanel topPanel = new JPanel(new BorderLayout());

        JLabel welcome = new JLabel(
                "Welcome, " +
                SessionManager.getUser().getName() +
                " (" + SessionManager.getUser().getRole() + ")"
        );

        welcome.setFont(new Font("Arial", Font.BOLD, 20));

        topPanel.add(welcome, BorderLayout.WEST);

        JButton logoutButton = new JButton("Logout");

        topPanel.add(logoutButton, BorderLayout.EAST);

        logoutButton.addActionListener(e -> {

            SessionManager.clear();

            new LoginFrame().setVisible(true);

            dispose();
        });

        JPanel centerPanel = new JPanel(new GridLayout(3, 1, 15, 15));

        centerPanel.setBorder(
                BorderFactory.createEmptyBorder(40, 70, 40, 70)
        );

        summary.setFont(new Font("Arial", Font.BOLD, 18));

        summary.setHorizontalAlignment(SwingConstants.CENTER);

        JButton ticketsButton = new JButton("View Tickets");

        JButton createButton = new JButton("Create Ticket");

        centerPanel.add(summary);

        centerPanel.add(ticketsButton);

        centerPanel.add(createButton);

        ticketsButton.addActionListener(e -> {

            new TicketListFrame().setVisible(true);
        });

        createButton.addActionListener(e -> createTicket());

        add(topPanel, BorderLayout.NORTH);

        add(centerPanel, BorderLayout.CENTER);

        loadSummary();
    }

    private void loadSummary() {

        new SwingWorker<List<Ticket>, Void>() {

            @Override
            protected List<Ticket> doInBackground() throws Exception {

                return api.getTickets();
            }

            @Override
            protected void done() {

                try {

                    List<Ticket> tickets = get();

                    long open = tickets.stream()
                            .filter(t -> "OPEN".equals(t.getStatus()))
                            .count();

                    long progress = tickets.stream()
                            .filter(t -> "IN_PROGRESS".equals(t.getStatus()))
                            .count();

                    long closed = tickets.stream()
                            .filter(t -> "CLOSED".equals(t.getStatus()))
                            .count();

                    summary.setText(
                            "Total: " + tickets.size()
                            + "     Open: " + open
                            + "     In Progress: " + progress
                            + "     Closed: " + closed
                    );

                } catch (Exception e) {

                    summary.setText(
                            "Unable to load tickets: " + e.getMessage()
                    );
                }
            }

        }.execute();
    }

    private void createTicket() {

        JTextField title = new JTextField();

        JTextField category = new JTextField();

        JComboBox<String> priority = new JComboBox<>(
                new String[]{"LOW", "MEDIUM", "HIGH"}
        );

        JTextArea description = new JTextArea(5, 20);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));

        panel.add(new JLabel("Title"));

        panel.add(title);

        panel.add(new JLabel("Category"));

        panel.add(category);

        panel.add(new JLabel("Priority"));

        panel.add(priority);

        panel.add(new JLabel("Description"));

        panel.add(new JScrollPane(description));

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Create New Ticket",
                JOptionPane.OK_CANCEL_OPTION
        );

        if (result == JOptionPane.OK_OPTION) {

            if (title.getText().trim().isEmpty()
                    || category.getText().trim().isEmpty()
                    || description.getText().trim().isEmpty()) {

                JOptionPane.showMessageDialog(
                        this,
                        "Please fill all fields."
                );

                return;
            }

            try {

                api.createTicket(
                        title.getText().trim(),
                        description.getText().trim(),
                        category.getText().trim(),
                        priority.getSelectedItem().toString()
                );

                JOptionPane.showMessageDialog(
                        this,
                        "Ticket created successfully."
                );

                loadSummary();

            } catch (Exception e) {

                JOptionPane.showMessageDialog(
                        this,
                        e.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        }
    }
}