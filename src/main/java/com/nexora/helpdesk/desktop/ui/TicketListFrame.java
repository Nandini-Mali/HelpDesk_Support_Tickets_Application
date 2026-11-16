
package com.nexora.helpdesk.desktop.ui;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;

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

        setTitle("HelpDesk - Ticket Management");
        setSize(950, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        getContentPane().setBackground(Color.WHITE);

        // MAIN PANEL

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

        mainPanel.setBackground(Color.WHITE);

        mainPanel.setBorder(
                BorderFactory.createEmptyBorder(20, 25, 20, 25)
        );

        // HEADER

        JPanel headerPanel = new JPanel(new BorderLayout());

        headerPanel.setBackground(Color.WHITE);

        JLabel heading = new JLabel("Ticket Management");

        heading.setFont(new Font("Arial", Font.BOLD, 23));

        heading.setForeground(new Color(40, 100, 200));

        JLabel subtitle = new JLabel(
                "View, manage and filter support tickets"
        );

        subtitle.setFont(new Font("Arial", Font.PLAIN, 12));

        subtitle.setForeground(Color.GRAY);

        JPanel titlePanel = new JPanel(
                new GridLayout(2, 1, 0, 5)
        );

        titlePanel.setBackground(Color.WHITE);

        titlePanel.add(heading);
        titlePanel.add(subtitle);

        headerPanel.add(titlePanel, BorderLayout.WEST);

        // FILTER PANEL

        JPanel filterPanel = new JPanel(
                new FlowLayout(FlowLayout.LEFT, 12, 10)
        );

        filterPanel.setBackground(new Color(248, 249, 252));

        filterPanel.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(225, 225, 225)
                        ),
                        BorderFactory.createEmptyBorder(5, 10, 5, 10)
                )
        );

        JButton refreshButton = new JButton("Refresh");

        JButton detailsButton = new JButton("View Details");

        styleButton(refreshButton, false);
        styleButton(detailsButton, true);

        styleFilter(statusFilter);
        styleFilter(priorityFilter);

        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(statusFilter);

        filterPanel.add(new JLabel("Priority:"));
        filterPanel.add(priorityFilter);

        filterPanel.add(refreshButton);
        filterPanel.add(detailsButton);

        // TABLE

        table.setRowHeight(32);

        table.setFont(new Font("Arial", Font.PLAIN, 13));

        table.setForeground(new Color(50, 50, 50));

        table.setBackground(Color.WHITE);

        table.setSelectionBackground(new Color(220, 235, 255));

        table.setSelectionForeground(Color.BLACK);

        table.setShowGrid(true);

        table.setGridColor(new Color(235, 235, 235));

        table.setFillsViewportHeight(true);

        table.setAutoCreateRowSorter(false);

        table.getTableHeader().setFont(
                new Font("Arial", Font.BOLD, 13)
        );

        table.getTableHeader().setBackground(
                new Color(40, 100, 200)
        );

        table.getTableHeader().setForeground(Color.BLACK);

        table.getTableHeader().setPreferredSize(
                new Dimension(0, 38)
        );

        table.getTableHeader().setReorderingAllowed(false);

        // ALTERNATING ROW COLORS

        DefaultTableCellRenderer renderer =
                new DefaultTableCellRenderer() {

                    @Override
                    public Component getTableCellRendererComponent(
                            JTable table,
                            Object value,
                            boolean isSelected,
                            boolean hasFocus,
                            int row,
                            int column) {

                        Component c = super.getTableCellRendererComponent(
                                table,
                                value,
                                isSelected,
                                hasFocus,
                                row,
                                column
                        );

                        if (!isSelected) {

                            c.setBackground(
                                    row % 2 == 0
                                            ? Color.WHITE
                                            : new Color(245, 248, 252)
                            );

                            c.setForeground(Color.DARK_GRAY);

                        } else {

                            c.setBackground(
                                    new Color(220, 235, 255)
                            );

                            c.setForeground(Color.BLACK);
                        }

                        setBorder(
                                BorderFactory.createEmptyBorder(
                                        0, 8, 0, 8
                                )
                        );

                        return c;
                    }
                };

        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel()
                    .getColumn(i)
                    .setCellRenderer(renderer);
        }

        table.getColumnModel().getColumn(0).setPreferredWidth(55);
        table.getColumnModel().getColumn(1).setPreferredWidth(200);
        table.getColumnModel().getColumn(2).setPreferredWidth(130);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(150);

        JScrollPane scrollPane = new JScrollPane(table);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(220, 220, 220)
                )
        );

        // LAYOUT

        JPanel topContainer = new JPanel(new BorderLayout(12, 12));

        topContainer.setBackground(Color.WHITE);

        topContainer.add(headerPanel, BorderLayout.NORTH);
        topContainer.add(filterPanel, BorderLayout.CENTER);

        mainPanel.add(topContainer, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        add(mainPanel);

        // ACTIONS

        refreshButton.addActionListener(e -> loadTickets());

        detailsButton.addActionListener(e -> showDetails());

        statusFilter.addActionListener(e -> applyFilters());

        priorityFilter.addActionListener(e -> applyFilters());

        loadTickets();
    }

    // BUTTON STYLE

    private void styleButton(JButton button, boolean primary) {

        button.setPreferredSize(new Dimension(125, 34));

        button.setFont(new Font("Arial", Font.BOLD, 12));

        button.setFocusPainted(false);

        button.setOpaque(true);

        button.setContentAreaFilled(true);

        button.setRolloverEnabled(false);

        if (primary) {

            button.setBackground(new Color(40, 100, 200));

            button.setForeground(Color.BLACK);

            button.setBorder(
                    BorderFactory.createEmptyBorder(5, 12, 5, 12)
            );

        } else {

            button.setBackground(new Color(245, 245, 245));

            button.setForeground(Color.BLACK);

            button.setBorder(
                    BorderFactory.createLineBorder(
                            new Color(190, 190, 190)
                    )
            );
        }

        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    // FILTER STYLE

    private void styleFilter(JComboBox<String> combo) {

        combo.setPreferredSize(new Dimension(130, 32));

        combo.setFont(new Font("Arial", Font.PLAIN, 12));

        combo.setBackground(Color.WHITE);

        combo.setFocusable(false);
    }

    // LOAD TICKETS

    private void loadTickets() {

        new SwingWorker<List<Ticket>, Void>() {

            @Override
            protected List<Ticket> doInBackground()
                    throws Exception {

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

    // APPLY FILTERS

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

    // VIEW TICKET DETAILS

    private void showDetails() {

        int row = table.getSelectedRow();

        if (row == -1) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please select a ticket.",
                    "No Ticket Selected",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        // Convert sorted table row to model row

        int modelRow = table.convertRowIndexToModel(row);

        long ticketId = Long.parseLong(
                model.getValueAt(modelRow, 0).toString()
        );

        new TicketDetailsFrame(ticketId).setVisible(true);
    }
}
