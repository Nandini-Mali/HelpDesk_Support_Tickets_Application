package com.nexora.helpdesk.desktop.ui;

import java.awt.*;

import javax.swing.*;

import com.nexora.helpdesk.desktop.api.ApiClient;
import com.nexora.helpdesk.desktop.model.Ticket;
import com.nexora.helpdesk.desktop.util.SessionManager;

public class TicketDetailsFrame extends JFrame {

    private final ApiClient api = new ApiClient();

    private final long ticketId;

    private final JTextArea details = new JTextArea();

    private final JTextArea comments = new JTextArea();

    private final JTextArea history = new JTextArea();

    public TicketDetailsFrame(long ticketId) {

        this.ticketId = ticketId;

        setTitle("Ticket Details #" + ticketId);

        setSize(750, 600);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        details.setEditable(false);

        comments.setEditable(false);

        history.setEditable(false);

        JTabbedPane tabs = new JTabbedPane();

        tabs.addTab("Ticket Details", new JScrollPane(details));

        tabs.addTab("Comments", new JScrollPane(comments));

        tabs.addTab("History", new JScrollPane(history));

        add(tabs, BorderLayout.CENTER);

        if (SessionManager.getUser().isAgent()) {

            JPanel controls = new JPanel();

            JComboBox<String> status = new JComboBox<>(
                    new String[]{
                            "OPEN",
                            "IN_PROGRESS",
                            "CLOSED"
                    }
            );

            JComboBox<String> priority = new JComboBox<>(
                    new String[]{
                            "LOW",
                            "MEDIUM",
                            "HIGH"
                    }
            );

            JButton updateStatus = new JButton("Update Status");

            JButton updatePriority = new JButton("Update Priority");

            controls.add(status);

            controls.add(updateStatus);

            controls.add(priority);

            controls.add(updatePriority);

            add(controls, BorderLayout.SOUTH);

            updateStatus.addActionListener(e ->
                    updateTicket(true, status.getSelectedItem().toString())
            );

            updatePriority.addActionListener(e ->
                    updateTicket(false, priority.getSelectedItem().toString())
            );
        }

        loadTicket();
    }

    private void loadTicket() {

        new SwingWorker<Ticket, Void>() {

            @Override
            protected Ticket doInBackground() throws Exception {

                return api.getTicket(ticketId);
            }

            @Override
            protected void done() {

                try {

                    Ticket ticket = get();

                    details.setText(
                            "Ticket ID: " + ticket.getId()
                            + "\nTitle: " + ticket.getTitle()
                            + "\nCategory: " + ticket.getCategory()
                            + "\nPriority: " + ticket.getPriority()
                            + "\nStatus: " + ticket.getStatus()
                            + "\nCreated By: " + ticket.getCreatedByName()
                            + "\nCreated At: " + ticket.getCreatedAt()
                            + "\n\nDescription:\n" + ticket.getDescription()
                    );

                } catch (Exception e) {

                    details.setText(e.getMessage());
                }
            }

        }.execute();

        new SwingWorker<String[], Void>() {

            @Override
            protected String[] doInBackground() throws Exception {

                return new String[]{
                        api.getComments(ticketId),
                        api.getHistory(ticketId)
                };
            }

            @Override
            protected void done() {

                try {

                    String[] result = get();

                    comments.setText(result[0]);

                    history.setText(result[1]);

                } catch (Exception e) {

                    comments.setText(
                            "Unable to load comments/history: " + e.getMessage()
                    );
                }
            }

        }.execute();
    }

    private void updateTicket(boolean isStatus, String value) {

        try {

            if (isStatus) {

                api.updateStatus(ticketId, value);

            } else {

                api.updatePriority(ticketId, value);
            }

            JOptionPane.showMessageDialog(
                    this,
                    "Ticket updated successfully."
            );

            loadTicket();

        } catch (Exception e) {

            JOptionPane.showMessageDialog(
                    this,
                    e.getMessage(),
                    "Update Failed",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }
}