
package com.nexora.helpdesk.desktop.ui;

import java.awt.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import com.nexora.helpdesk.desktop.api.ApiClient;
import com.nexora.helpdesk.desktop.model.Ticket;
import com.nexora.helpdesk.desktop.util.SessionManager;

public class TicketDetailsFrame extends JFrame {

    private final ApiClient api = new ApiClient();

    private final long ticketId;

    private final JTextArea details = new JTextArea();

    private final JTextArea comments = new JTextArea();

    private final JTextArea history = new JTextArea();

    private final JTextArea commentInput = new JTextArea(3, 20);

    private final JButton sendCommentButton =
            new JButton("Send Comment");

    private JComboBox<String> status;

    private JComboBox<String> priority;

    private JButton updateStatus;

    private JButton updatePriority;

    public TicketDetailsFrame(long ticketId) {

        this.ticketId = ticketId;

        setTitle("HelpDesk - Ticket Details #" + ticketId);

        setSize(750, 600);

        setLocationRelativeTo(null);

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        getContentPane().setBackground(Color.WHITE);

        // MAIN PANEL

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));

        mainPanel.setBackground(Color.WHITE);

        mainPanel.setBorder(
                new EmptyBorder(20, 25, 20, 25)
        );

        // HEADER

        JPanel headerPanel = new JPanel(new BorderLayout());

        headerPanel.setBackground(Color.WHITE);

        JLabel heading = new JLabel("Ticket Details #" + ticketId);

        heading.setFont(new Font("Arial", Font.BOLD, 23));

        heading.setForeground(new Color(40, 100, 200));

        JLabel subtitle = new JLabel(
                "View ticket information, comments and activity history"
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

        // TEXT AREAS

        styleTextArea(details);

        styleTextArea(comments);

        styleTextArea(history);

        // TABS

        JTabbedPane tabs = new JTabbedPane();

        tabs.setFont(new Font("Arial", Font.BOLD, 13));

        tabs.setBackground(Color.WHITE);

        tabs.setForeground(new Color(40, 100, 200));

        tabs.addTab(
                "Ticket Details",
                createTextPanel(details)
        );

        tabs.addTab(
                "Comments",
                createCommentsPanel()
        );

        tabs.addTab(
                "History",
                createTextPanel(history)
        );

        // AGENT CONTROLS

        JPanel bottomPanel = new JPanel(
                new FlowLayout(FlowLayout.CENTER, 15, 12)
        );

        bottomPanel.setBackground(new Color(248, 249, 252));

        bottomPanel.setBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 225, 225)
                )
        );

        if (SessionManager.getUser().isAgent()) {

            status = new JComboBox<>(
                    new String[]{
                            "OPEN",
                            "IN_PROGRESS",
                            "CLOSED"
                    }
            );

            priority = new JComboBox<>(
                    new String[]{
                            "LOW",
                            "MEDIUM",
                            "HIGH"
                    }
            );

            updateStatus = new JButton("Update Status");

            updatePriority = new JButton("Update Priority");

            styleComboBox(status);

            styleComboBox(priority);

            styleButton(updateStatus);

            styleButton(updatePriority);

            bottomPanel.add(new JLabel("Status:"));

            bottomPanel.add(status);

            bottomPanel.add(updateStatus);

            bottomPanel.add(new JLabel("Priority:"));

            bottomPanel.add(priority);

            bottomPanel.add(updatePriority);

            updateStatus.addActionListener(e ->
                    updateTicket(
                            true,
                            status.getSelectedItem().toString()
                    )
            );

            updatePriority.addActionListener(e ->
                    updateTicket(
                            false,
                            priority.getSelectedItem().toString()
                    )
            );

        } else {

            JLabel viewOnly = new JLabel("Ticket Information");

            viewOnly.setFont(new Font("Arial", Font.BOLD, 12));

            viewOnly.setForeground(Color.GRAY);

            bottomPanel.add(viewOnly);
        }

        // LAYOUT

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        mainPanel.add(tabs, BorderLayout.CENTER);

        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // LOAD DATA

        loadTicket();
    }

    // TEXT AREA STYLE

    private void styleTextArea(JTextArea area) {

        area.setEditable(false);

        area.setFont(new Font("Arial", Font.PLAIN, 14));

        area.setForeground(new Color(50, 50, 50));

        area.setBackground(new Color(250, 251, 253));

        area.setLineWrap(true);

        area.setWrapStyleWord(true);

        area.setBorder(
                new EmptyBorder(15, 15, 15, 15)
        );

        area.setMargin(new Insets(5, 5, 5, 5));
    }

    // TEXT PANEL

    private JScrollPane createTextPanel(JTextArea area) {

        JScrollPane scrollPane = new JScrollPane(area);

        scrollPane.setBorder(
                BorderFactory.createLineBorder(
                        new Color(225, 225, 225)
                )
        );

        scrollPane.getVerticalScrollBar().setUnitIncrement(15);

        return scrollPane;
    }

    // COMMENTS PANEL

    private JPanel createCommentsPanel() {

        JPanel panel = new JPanel(new BorderLayout(10, 10));

        panel.setBackground(Color.WHITE);

        panel.setBorder(
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        );

        comments.setEditable(false);

        JScrollPane commentScroll = new JScrollPane(comments);

        commentInput.setLineWrap(true);

        commentInput.setWrapStyleWord(true);

        commentInput.setFont(new Font("Arial", Font.PLAIN, 13));

        commentInput.setBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(
                                new Color(210, 210, 210)
                        ),
                        BorderFactory.createEmptyBorder(8, 8, 8, 8)
                )
        );

        JScrollPane inputScroll = new JScrollPane(commentInput);

        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));

        bottomPanel.setBackground(Color.WHITE);

        bottomPanel.add(inputScroll, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(
                new FlowLayout(FlowLayout.RIGHT)
        );

        buttonPanel.setBackground(Color.WHITE);

        styleButton(sendCommentButton);

        buttonPanel.add(sendCommentButton);

        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(commentScroll, BorderLayout.CENTER);

        panel.add(bottomPanel, BorderLayout.SOUTH);

        sendCommentButton.addActionListener(e -> sendComment());

        return panel;
    }

    // BUTTON STYLE

    private void styleButton(JButton button) {

        button.setPreferredSize(new Dimension(135, 35));

        button.setFont(new Font("Arial", Font.BOLD, 12));

        button.setBackground(new Color(40, 100, 200));

        button.setForeground(Color.BLACK);

        button.setFocusPainted(false);

        button.setOpaque(true);

        button.setContentAreaFilled(true);

        button.setBorder(
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        );

        button.setRolloverEnabled(false);

        button.setCursor(
                Cursor.getPredefinedCursor(Cursor.HAND_CURSOR)
        );
    }

    // COMBOBOX STYLE

    private void styleComboBox(JComboBox<String> combo) {

        combo.setPreferredSize(new Dimension(115, 35));

        combo.setFont(new Font("Arial", Font.PLAIN, 12));

        combo.setBackground(Color.WHITE);

        combo.setForeground(Color.BLACK);

        combo.setFocusable(false);
    }

    // LOAD TICKET

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
                            "TICKET INFORMATION\n\n"
                            + "Ticket ID: " + ticket.getId()
                            + "\n\nTitle: " + ticket.getTitle()
                            + "\n\nCategory: " + ticket.getCategory()
                            + "\n\nPriority: " + ticket.getPriority()
                            + "\n\nStatus: " + ticket.getStatus()
                            + "\n\nCreated By: " + ticket.getCreatedByName()
                            + "\n\nCreated At: " + ticket.getCreatedAt()
                            + "\n\nDESCRIPTION\n\n"
                            + ticket.getDescription()
                    );

                    if (status != null) {
                        status.setSelectedItem(ticket.getStatus());
                    }

                    if (priority != null) {
                        priority.setSelectedItem(ticket.getPriority());
                    }

                } catch (Exception e) {

                    details.setText(
                            "Unable to load ticket details:\n"
                            + e.getMessage()
                    );
                }
            }

        }.execute();

        // LOAD COMMENTS

        loadComments();

        // LOAD HISTORY

        new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() throws Exception {

                return api.getHistory(ticketId);
            }

            @Override
            protected void done() {

                try {

                    history.setText(get());

                } catch (Exception e) {

                    history.setText(
                            "Unable to load history: " + e.getMessage()
                    );
                }
            }

        }.execute();
    }

    // LOAD COMMENTS AUTOMATICALLY

    private void loadComments() {

        new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() throws Exception {

                return api.getComments(ticketId);
            }

            @Override
            protected void done() {

                try {

                    comments.setText(get());

                    comments.setCaretPosition(0);

                } catch (Exception e) {

                    comments.setText(
                            "Unable to load comments: " + e.getMessage()
                    );
                }
            }

        }.execute();
    }

    // SEND COMMENT - USER AND AGENT

    private void sendComment() {

        String message = commentInput.getText().trim();

        if (message.isEmpty()) {

            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a comment.",
                    "Empty Comment",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        if (message.length() > 1000) {

            JOptionPane.showMessageDialog(
                    this,
                    "Comment cannot exceed 1000 characters.",
                    "Validation Error",
                    JOptionPane.WARNING_MESSAGE
            );

            return;
        }

        sendCommentButton.setEnabled(false);

        sendCommentButton.setText("Sending...");

        new SwingWorker<String, Void>() {

            @Override
            protected String doInBackground() throws Exception {

                return api.addComment(ticketId, message);
            }

            @Override
            protected void done() {

                sendCommentButton.setEnabled(true);

                sendCommentButton.setText("Send Comment");

                try {

                    get();

                    commentInput.setText("");

                    // AUTOMATICALLY RELOAD COMMENTS

                    loadComments();

                    JOptionPane.showMessageDialog(
                            TicketDetailsFrame.this,
                            "Comment sent successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                } catch (Exception e) {

                    String error = e.getCause() != null
                            ? e.getCause().getMessage()
                            : e.getMessage();

                    JOptionPane.showMessageDialog(
                            TicketDetailsFrame.this,
                            error,
                            "Comment Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        }.execute();
    }

    // UPDATE TICKET STATUS AND PRIORITY

    private void updateTicket(boolean isStatus, String value) {

        if (updateStatus != null) {
            updateStatus.setEnabled(false);
        }

        if (updatePriority != null) {
            updatePriority.setEnabled(false);
        }

        new SwingWorker<Void, Void>() {

            @Override
            protected Void doInBackground() throws Exception {

                if (isStatus) {

                    api.updateStatus(ticketId, value);

                } else {

                    api.updatePriority(ticketId, value);
                }

                return null;
            }

            @Override
            protected void done() {

                if (updateStatus != null) {
                    updateStatus.setEnabled(true);
                }

                if (updatePriority != null) {
                    updatePriority.setEnabled(true);
                }

                try {

                    get();

                    JOptionPane.showMessageDialog(
                            TicketDetailsFrame.this,
                            "Ticket updated successfully!",
                            "Success",
                            JOptionPane.INFORMATION_MESSAGE
                    );

                    loadTicket();

                } catch (Exception e) {

                    String message = e.getCause() != null
                            ? e.getCause().getMessage()
                            : e.getMessage();

                    JOptionPane.showMessageDialog(
                            TicketDetailsFrame.this,
                            message,
                            "Update Failed",
                            JOptionPane.ERROR_MESSAGE
                    );
                }
            }

        }.execute();
    }
}
