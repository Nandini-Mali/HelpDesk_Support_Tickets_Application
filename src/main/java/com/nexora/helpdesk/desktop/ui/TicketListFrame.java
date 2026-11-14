package com.nexora.helpdesk.desktop.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import com.nexora.helpdesk.desktop.api.ApiClient;
import com.nexora.helpdesk.desktop.model.Ticket;

public class TicketListFrame extends JFrame {

    private final ApiClient api = new ApiClient();

    private final DefaultTableModel model =
            new DefaultTableModel(
                    new String[]{
                            "ID",
                            "Title",
                            "Category",
                            "Priority",
                            "Status",
                            "Created By"
                    },
                    0
            ) {

                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };

    private final JTable table = new JTable(model);

    private List<Ticket> tickets = new ArrayList<>();

    private final JComboBox<String> statusFilter =
            new JComboBox<>(
                    new String[]{
                            "ALL",
                            "OPEN",
                            "IN_PROGRESS",
                            "CLOSED"
                    }
            );

    private final JComboBox<String> priorityFilter =
            new JComboBox<>(
                    new String[]{
                            "ALL",
                            "LOW",
                            "MEDIUM",
                            "HIGH"
                    }
            );

    public TicketListFrame() {

        setTitle("HelpDesk - Ticket List");

        setSize(900, 520);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton refreshButton = new JButton("Refresh");

        JButton detailsButton = new JButton("View Details");

        topPanel.add(new JLabel("Status"));

        topPanel.add(statusFilter);

        topPanel.add(new JLabel("Priority"));

        topPanel.add(priorityFilter);

        topPanel.add(refreshButton);

        topPanel.add(detailsButton);

        add(topPanel, BorderLayout.NORTH);

        add(new JScrollPane(table), BorderLayout.CENTER);

        refreshButton.addActionListener(e -> loadTickets());

        detailsButton.addActionListener(e -> showDetails());

        statusFilter.addActionListener(e -> applyFilters());

        priorityFilter.addActionListener(e -> applyFilters());

        loadTickets();
    }

    private void loadTickets() {

        new SwingWorker<List<Ticket>, Void>() {

            @Override
            protected List<Ticket> doInBackground() throws Exception {

                return api.getTickets();
            }

            @Override
            protected void done() {

                try {

                    tickets = get();

                    applyFilters();

                } catch (Exception e) {

                    JOptionPane.showMessageDialog(
                            TicketListFrame.this,
                            e.getMessage(),
                            "API Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        }.execute();
    }

    private void applyFilters() {

        model.setRowCount(0);

        String status = statusFilter.getSelectedItem().toString();

        String priority = priorityFilter.getSelectedItem().toString();

        for (Ticket ticket : tickets) {

            boolean statusMatch =
                    status.equals("ALL")
                    || status.equals(ticket.getStatus());

            boolean priorityMatch =
                    priority.equals("ALL")
                    || priority.equals(ticket.getPriority());

            if (statusMatch && priorityMatch) {

                model.addRow(
                        new Object[]{
                                ticket.getId(),
                                ticket.getTitle(),
                                ticket.getCategory(),
                                ticket.getPriority(),
                                ticket.getStatus(),
                                ticket.getCreatedByName()
                        }
                );
            }
        }
    }

    private void showDetails() {

        int row = table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a ticket."
            );

            return;
        }

        long ticketId = Long.parseLong(
                model.getValueAt(row, 0).toString()
        );

        new TicketDetailsFrame(ticketId).setVisible(true);
    }
}