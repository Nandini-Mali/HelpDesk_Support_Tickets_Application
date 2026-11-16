
package com.nexora.helpdesk.desktop.ui;

import java.awt.*;
import java.util.List;

import javax.swing.*;

import com.nexora.helpdesk.desktop.api.ApiClient;
import com.nexora.helpdesk.desktop.model.Ticket;
import com.nexora.helpdesk.desktop.util.SessionManager;

public class DashboardFrame extends JFrame {

    private final ApiClient api = new ApiClient();

    private final JLabel totalLabel = new JLabel("0");
    private final JLabel openLabel = new JLabel("0");
    private final JLabel progressLabel = new JLabel("0");
    private final JLabel closedLabel = new JLabel("0");

    private JButton ticketsButton;
    private JButton createButton;

    public DashboardFrame() {

        setTitle("HelpDesk-Dashboard");
        setSize(700, 390);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        getContentPane().setBackground(Color.WHITE);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 20));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 15, 25)
        );

        // HEADER

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);

        JLabel heading = new JLabel("HelpDesk Dashboard");
        heading.setFont(new Font("Arial", Font.BOLD, 23));
        heading.setForeground(new Color(40, 100, 200));

        JPanel userPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT, 10, 0)
        );
        userPanel.setBackground(Color.WHITE);

        JLabel welcome = new JLabel(
                "Welcome, " +
                SessionManager.getUser().getName() +
                " (" +
                SessionManager.getUser().getRole() +
                ")"
        );

        welcome.setFont(new Font("Arial", Font.BOLD, 12));

        JButton logoutButton = new JButton("Logout");
        styleLogoutButton(logoutButton);

        userPanel.add(welcome);
        userPanel.add(logoutButton);

        headerPanel.add(heading, BorderLayout.WEST);
        headerPanel.add(userPanel, BorderLayout.EAST);

        logoutButton.addActionListener(e -> {

            int result = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to logout?",
                    "Logout",
                    JOptionPane.YES_NO_OPTION
            );

            if (result == JOptionPane.YES_OPTION) {
                SessionManager.clear();
                new LoginFrame().setVisible(true);
                dispose();
            }
        });

        // SUMMARY CARDS

        JPanel summaryPanel = new JPanel(
                new GridLayout(1, 4, 10, 10)
        );
        summaryPanel.setBackground(Color.WHITE);

        summaryPanel.add(createSummaryCard(
                "Total Tickets",
                totalLabel,
                new Color(40, 100, 200)
        ));

        summaryPanel.add(createSummaryCard(
                "Open Tickets",
                openLabel,
                new Color(220, 140, 30)
        ));

        summaryPanel.add(createSummaryCard(
                "In Progress",
                progressLabel,
                new Color(120, 80, 200)
        ));

        summaryPanel.add(createSummaryCard(
                "Closed Tickets",
                closedLabel,
                new Color(40, 150, 90)
        ));

        // ACTION BUTTONS

        JPanel centerPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 35, 25)
        );
        centerPanel.setBackground(Color.WHITE);

        ticketsButton = new JButton("View Tickets");
        createButton = new JButton("Create Ticket");

        styleActionButton(ticketsButton);
        styleActionButton(createButton);

        centerPanel.add(ticketsButton);
        centerPanel.add(createButton);

        ticketsButton.addActionListener(e ->
                new TicketListFrame().setVisible(true)
        );

        createButton.addActionListener(e ->
                createTicket()
        );

        // MAIN LAYOUT (NO FOOTER)

        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(summaryPanel, BorderLayout.CENTER);
        mainPanel.add(centerPanel, BorderLayout.SOUTH);

        add(mainPanel, BorderLayout.CENTER);

        // LOAD SUMMARY

        loadSummary();
    }

    private JPanel createSummaryCard(
            String title,
            JLabel valueLabel,
            Color color) {

        JPanel card = new JPanel(new BorderLayout(3, 3));
        card.setBackground(Color.WHITE);

        card.setPreferredSize(new Dimension(125, 65));

        card.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(color, 1),
                        BorderFactory.createEmptyBorder(5, 5, 5, 5)
                )
        );

        JLabel titleLabel = new JLabel(
                title,
                SwingConstants.CENTER
        );

        titleLabel.setFont(new Font("Arial", Font.BOLD, 11));

        valueLabel.setFont(new Font("Arial", Font.BOLD, 19));
        valueLabel.setForeground(color);
        valueLabel.setHorizontalAlignment(SwingConstants.CENTER);

        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);

        return card;
    }

    private void styleActionButton(JButton button) {

        button.setPreferredSize(new Dimension(190, 48));
        button.setBackground(new Color(245, 245, 245));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 15));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(180, 180, 180), 1
                        ),
                        BorderFactory.createEmptyBorder(8, 15, 8, 15)
                )
        );

        button.setRolloverEnabled(false);
        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    private void styleLogoutButton(JButton button) {

        button.setBackground(new Color(245, 245, 245));
        button.setForeground(Color.BLACK);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setContentAreaFilled(true);
        button.setRolloverEnabled(false);

        button.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(190, 190, 190)
                        ),
                        BorderFactory.createEmptyBorder(5, 12, 5, 12)
                )
        );

        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    // LOAD TICKET SUMMARY

    private void loadSummary() {

        new SwingWorker<List<Ticket>, Void>() {

            @Override
            protected List<Ticket> doInBackground()
                    throws Exception {
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

                    totalLabel.setText(String.valueOf(tickets.size()));
                    openLabel.setText(String.valueOf(open));
                    progressLabel.setText(String.valueOf(progress));
                    closedLabel.setText(String.valueOf(closed));

                } catch (Exception e) {

                    JOptionPane.showMessageDialog(
                            DashboardFrame.this,
                            "Unable to load tickets: " + e.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        }.execute();
    }

    // CREATE TICKET

    private void createTicket() {

        JTextField title = new JTextField(20);
        JTextField category = new JTextField(20);

        JComboBox<String> priority = new JComboBox<>(
                new String[]{"LOW", "MEDIUM", "HIGH"}
        );

        JTextArea description = new JTextArea(4, 20);
        description.setLineWrap(true);
        description.setWrapStyleWord(true);

        JPanel panel = new JPanel(new GridLayout(0, 1, 5, 5));
        panel.setPreferredSize(new Dimension(320, 250));

        panel.setBorder(
                BorderFactory.createEmptyBorder(10, 15, 10, 15)
        );

        panel.add(new JLabel("Title:"));
        panel.add(title);
        panel.add(new JLabel("Category:"));
        panel.add(category);
        panel.add(new JLabel("Priority:"));
        panel.add(priority);
        panel.add(new JLabel("Description:"));
        panel.add(new JScrollPane(description));

        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "Create New Ticket",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String ticketTitle = title.getText().trim();
        String ticketCategory = category.getText().trim();
        String ticketDescription = description.getText().trim();

        if (ticketTitle.isEmpty()
                || ticketCategory.isEmpty()
                || ticketDescription.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please fill all fields.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        createButton.setEnabled(false);
        createButton.setText("Creating...");

        new SwingWorker<Ticket, Void>() {

            @Override
            protected Ticket doInBackground()
                    throws Exception {

                return api.createTicket(
                        ticketTitle,
                        ticketDescription,
                        ticketCategory,
                        priority.getSelectedItem().toString()
                );
            }

            @Override
            protected void done() {

                createButton.setEnabled(true);
                createButton.setText("Create Ticket");

                try {

                    get();

                    JOptionPane.showMessageDialog(
                            DashboardFrame.this,
                            "Ticket created successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    loadSummary();

                } catch (Exception e) {

                    String message = e.getCause() != null
                            ? e.getCause().getMessage()
                            : e.getMessage();

                    JOptionPane.showMessageDialog(
                            DashboardFrame.this,
                            message,
                            "Ticket Creation Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        }.execute();
    }
}
