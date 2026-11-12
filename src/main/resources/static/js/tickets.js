
"use strict";

// ========================================
// COMMON FUNCTIONS
// ========================================

// GET CURRENT SESSION
function getCurrentSession() {
    return HelpDesk.checkAuth();
}

// GET TICKET ID FROM URL
function getTicketId() {
    const params = new URLSearchParams(window.location.search);
    return params.get("id");
}

// SAFE TEXT DISPLAY
function setText(id, value) {
    const element = document.getElementById(id);

    if (element) {
        element.textContent = value ?? "-";
    }
}

// DISPLAY MESSAGE
function displayMessage(message, success = false) {
    HelpDesk.showMessage(
        document.getElementById("message"),
        message,
        success
    );
}


// ========================================
// CREATE TICKET
// ========================================

const ticketForm = document.getElementById("ticketForm");

if (ticketForm) {

    ticketForm.addEventListener("submit", async function(event) {

        event.preventDefault();

        const session = getCurrentSession();

        if (!session) return;

        const ticket = {
            title: document.getElementById("title").value.trim(),
            description: document.getElementById("description").value.trim(),
            category: document.getElementById("category").value,
            priority: document.getElementById("priority").value
        };

        if (
            !ticket.title ||
            !ticket.description ||
            !ticket.category ||
            !ticket.priority
        ) {
            displayMessage("Please fill in all required fields.");
            return;
        }

        try {

            const result = await HelpDesk.apiRequest(
                `/tickets?userId=${session.user.id}`,
                {
                    method: "POST",
                    body: JSON.stringify(ticket)
                }
            );

            displayMessage(
                `Ticket created successfully! Ticket ID: ${result.id}`,
                true
            );

            ticketForm.reset();

        } catch (error) {

            displayMessage(error.message);
        }
    });
}


// ========================================
// LOAD ALL TICKETS / MY TICKETS
// ========================================

async function loadMyTickets() {

    const session = getCurrentSession();

    if (!session) return;

    const table = document.getElementById("ticketList");

    if (!table) return;

    table.replaceChildren();

    try {

        const user = session.user;

        let tickets = [];

        // AGENT - GET ALL TICKETS WITH PAGINATION

        if (user.role === "AGENT") {

            let page = 0;
            let totalPages = 1;

            while (page < totalPages) {

                const result = await HelpDesk.apiRequest(
                    `/tickets?userId=${user.id}&page=${page}&size=100`
                );

                tickets.push(...(result.content || []));

                totalPages = Number(result.totalPages || 0);

                page++;
            }

        }

        // USER - GET OWN TICKETS

        else {

            tickets = await HelpDesk.apiRequest(
                `/tickets/user/${user.id}?requesterId=${user.id}`
            );
        }

        displayTicketList(tickets);

    } catch (error) {

        console.error("Ticket loading error:", error);

        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 6;
        cell.textContent = error.message;

        row.appendChild(cell);
        table.appendChild(row);
    }
}


// ========================================
// DISPLAY TICKET LIST
// ========================================

function displayTicketList(tickets) {

    const table = document.getElementById("ticketList");

    if (!table) return;

    table.replaceChildren();

    if (!Array.isArray(tickets) || tickets.length === 0) {

        const row = document.createElement("tr");
        const cell = document.createElement("td");

        cell.colSpan = 6;
        cell.textContent = "No tickets found.";

        row.appendChild(cell);
        table.appendChild(row);

        return;
    }

    tickets.forEach(ticket => {

        const row = document.createElement("tr");

        const values = [
            ticket.id,
            ticket.title,
            ticket.category,
            ticket.priority,
            ticket.status
        ];

        values.forEach(value => {

            const cell = document.createElement("td");

            cell.textContent = value ?? "-";

            row.appendChild(cell);
        });

        // ACTION COLUMN

        const actionCell = document.createElement("td");

        const link = document.createElement("a");

        link.href =
            `ticket-details.html?id=${encodeURIComponent(ticket.id)}`;

        link.textContent = "View Details";

        link.className = "action-link";

        actionCell.appendChild(link);

        row.appendChild(actionCell);

        table.appendChild(row);

    });
}


// ========================================
// LOAD TICKET DETAILS
// ========================================

async function loadTicketDetails() {

    const session = getCurrentSession();

    if (!session) return;

    const ticketId = getTicketId();

    if (!ticketId) {

        displayMessage("Ticket ID is missing.");

        return;
    }

    try {

        const ticket = await HelpDesk.apiRequest(
            `/tickets/${encodeURIComponent(ticketId)}?userId=${session.user.id}`
        );

        console.log("FULL TICKET RESPONSE:", ticket);

        // TICKET INFORMATION

        setText("detailTitle", ticket.title);
        setText("detailId", ticket.id);
        setText("detailDescription", ticket.description);
        setText("detailCategory", ticket.category);
        setText("detailPriority", ticket.priority);
        setText("detailStatus", ticket.status);

        setText(
            "detailUser",
            ticket.createdByName || "Unknown User"
        );

        setText(
            "detailCreatedAt",
            ticket.createdAt
                ? new Date(ticket.createdAt).toLocaleString()
                : "-"
        );

        setText(
            "detailClosedAt",
            ticket.closedAt
                ? new Date(ticket.closedAt).toLocaleString()
                : "Not closed"
        );

        // AGENT CONTROLS

        const agentControls =
            document.getElementById("agentControls");

        const isAgent = session.user.role === "AGENT";

        if (agentControls) {
            agentControls.hidden = !isAgent;
        }

        const status = document.getElementById("newStatus");

        const priority = document.getElementById("newPriority");

        if (status) {
            status.value = ticket.status;
        }

        if (priority) {
            priority.value = ticket.priority;
        }

        // LOAD COMMENTS AND HISTORY

        await Promise.all([
            loadComments(ticketId),
            loadHistory(ticketId)
        ]);

    } catch (error) {

        displayMessage(error.message);
    }
}


// ========================================
// UPDATE TICKET - AGENT ONLY
// ========================================

async function updateTicket() {

    const session = getCurrentSession();

    if (!session || session.user.role !== "AGENT") {

        displayMessage("Only agents can update tickets.");

        return;
    }

    const ticketId = getTicketId();

    if (!ticketId) return;

    const statusElement = document.getElementById("newStatus");

    const priorityElement = document.getElementById("newPriority");

    if (!statusElement || !priorityElement) {

        displayMessage("Ticket update controls are missing.");

        return;
    }

    const status = statusElement.value;

    const priority = priorityElement.value;

    const userId = session.user.id;

    try {

        // UPDATE STATUS

        await HelpDesk.apiRequest(
            `/tickets/${encodeURIComponent(ticketId)}/status?status=${encodeURIComponent(status)}&userId=${userId}`,
            {
                method: "PUT"
            }
        );

        // UPDATE PRIORITY

        await HelpDesk.apiRequest(
            `/tickets/${encodeURIComponent(ticketId)}/priority?priority=${encodeURIComponent(priority)}&userId=${userId}`,
            {
                method: "PUT"
            }
        );

        displayMessage("Ticket updated successfully!", true);

        await loadTicketDetails();

    } catch (error) {

        displayMessage(error.message);

        await loadTicketDetails();
    }
}


// ========================================
// LOAD COMMENTS
// ========================================

async function loadComments(ticketId) {

    const session = getCurrentSession();

    const container = document.getElementById("commentsList");

    if (!container || !session) return;

    try {

        const comments = await HelpDesk.apiRequest(
            `/comments/ticket/${encodeURIComponent(ticketId)}?userId=${session.user.id}`
        );

        container.replaceChildren();

        if (!Array.isArray(comments) || comments.length === 0) {

            container.textContent = "No comments available.";

            return;
        }

        comments.forEach(comment => {

            const item = document.createElement("div");

            item.className = "comment-item";

            const author = document.createElement("strong");

            author.textContent = comment.authorName || "User";

            const content = document.createElement("p");

            content.textContent = comment.message ?? "";

            const date = document.createElement("small");

            date.textContent = comment.createdAt
                ? new Date(comment.createdAt).toLocaleString()
                : "";

            item.append(author, content, date);

            container.appendChild(item);
        });

    } catch (error) {

        container.textContent = error.message;
    }
}


// ========================================
// ADD COMMENT
// ========================================

const commentForm = document.getElementById("commentForm");

if (commentForm) {

    commentForm.addEventListener("submit", async function(event) {

        event.preventDefault();

        const session = getCurrentSession();

        if (!session) return;

        const ticketId = getTicketId();

        if (!ticketId) return;

        const commentInput = document.getElementById("commentText");

        const commentText = commentInput.value.trim();

        if (!commentText || commentText.length > 1000) {

            displayMessage(
                "Comment must contain 1 to 1000 characters."
            );

            return;
        }

        try {

            await HelpDesk.apiRequest(
                `/comments/ticket/${encodeURIComponent(ticketId)}?userId=${session.user.id}`,
                {
                    method: "POST",
                    body: JSON.stringify({
                        message: commentText
                    })
                }
            );

            commentForm.reset();

            displayMessage("Comment added successfully!", true);

            await loadComments(ticketId);

        } catch (error) {

            displayMessage(error.message);
        }
    });
}


// ========================================
// LOAD TICKET HISTORY
// ========================================

async function loadHistory(ticketId) {

    const session = getCurrentSession();

    const container = document.getElementById("historyList");

    if (!container || !session) return;

    try {

        const history = await HelpDesk.apiRequest(
            `/ticket-history/ticket/${encodeURIComponent(ticketId)}?userId=${session.user.id}`
        );

        container.replaceChildren();

        if (!Array.isArray(history) || history.length === 0) {

            container.textContent = "No ticket history available.";

            return;
        }

        history.forEach(record => {

            const item = document.createElement("div");

            item.className = "history-item";

            const heading = document.createElement("strong");

            heading.textContent = record.fieldChanged || "Updated";

            const description = document.createElement("p");

            description.textContent =
                `${record.oldValue ?? "-"} → ${record.newValue ?? "-"}`;

            const author = document.createElement("small");

            author.textContent =
                `${record.changedByName || "User"} | ${
                    record.changedAt
                        ? new Date(record.changedAt).toLocaleString()
                        : ""
                }`;

            item.append(heading, description, author);

            container.appendChild(item);
        });

    } catch (error) {

        container.textContent = error.message;
    }
}


// ========================================
// PAGE INITIALIZATION
// ========================================

// MY TICKETS / ALL TICKETS PAGE

if (document.getElementById("ticketList")) {

    loadMyTickets();
}

// TICKET DETAILS PAGE

if (document.getElementById("detailTitle")) {

    loadTicketDetails();
}


// ========================================
// MAKE FUNCTIONS AVAILABLE TO HTML
// ========================================

window.updateTicket = updateTicket;

window.loadMyTickets = loadMyTickets;

window.loadTicketDetails = loadTicketDetails;

window.loadComments = loadComments;

window.loadHistory = loadHistory;
