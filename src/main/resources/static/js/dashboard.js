
document.addEventListener("DOMContentLoaded", loadDashboard);

// LOAD DASHBOARD
async function loadDashboard() {

    const page = window.location.pathname;

    let requiredRole = null;

    if (page.includes("agent-dashboard")) {
        requiredRole = "AGENT";
    } else if (page.includes("user-dashboard")) {
        requiredRole = "USER";
    } else {
        return;
    }

    const session = HelpDesk.checkAuth(requiredRole);

    if (!session) return;

    const user = session.user;
    const isAgent = user.role === "AGENT";

    // Welcome message
    setValue("welcome", `Welcome, ${user.name}`);

    setValue("userName", user.name);
    setValue("userEmail", user.email);
    setValue("userRole", user.role);

    try {

        let tickets = [];

        if (isAgent) {

            // AGENT: GET ALL TICKETS WITH PAGINATION
            tickets = await getAllTickets(user.id);

        } else {

            // USER: GET ONLY OWN TICKETS
            tickets = await HelpDesk.apiRequest(
                `/tickets/user/${user.id}?requesterId=${user.id}`
            );
        }

        updateStatistics(tickets);

        displayTickets(tickets);

    } catch (error) {

        console.error("Dashboard Error:", error);

        updateStatistics([]);

        showTableMessage(error.message);
    }
}


// GET ALL TICKETS - AGENT
async function getAllTickets(userId) {

    let allTickets = [];

    let page = 0;
    let totalPages = 1;

    while (page < totalPages) {

        const result = await HelpDesk.apiRequest(
            `/tickets?userId=${userId}&page=${page}&size=100&sort=createdAt,desc`
        );

        if (!result || !Array.isArray(result.content)) {
            throw new Error("Invalid ticket response from server.");
        }

        allTickets = allTickets.concat(result.content);

        totalPages = Number(result.totalPages || 0);

        page++;

        if (totalPages === 0) break;
    }

    return allTickets;
}


// UPDATE DASHBOARD STATISTICS
function updateStatistics(tickets) {

    const total = tickets.length;

    const open = tickets.filter(
        ticket => ticket.status === "OPEN"
    ).length;

    const progress = tickets.filter(
        ticket => ticket.status === "IN_PROGRESS"
    ).length;

    const closed = tickets.filter(
        ticket => ticket.status === "CLOSED"
    ).length;

    setValue("totalTickets", total);
    setValue("openTickets", open);
    setValue("progressTickets", progress);
    setValue("closedTickets", closed);
}


// SET HTML VALUES
function setValue(id, value) {

    const element = document.getElementById(id);

    if (element) {
        element.textContent = value ?? "-";
    }
}


// DISPLAY TICKETS
function displayTickets(tickets) {

    const table = document.getElementById("recentTickets");

    if (!table) return;

    table.replaceChildren();

    if (!Array.isArray(tickets) || tickets.length === 0) {

        showTableMessage("No tickets available.");

        return;
    }

    // Show latest tickets first
    const sortedTickets = [...tickets].sort((a, b) => {

        return new Date(b.createdAt || 0) -
               new Date(a.createdAt || 0);

    });

    sortedTickets.slice(0, 10).forEach(ticket => {

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


// DISPLAY TABLE MESSAGE
function showTableMessage(message) {

    const table = document.getElementById("recentTickets");

    if (!table) return;

    table.replaceChildren();

    const row = document.createElement("tr");

    const cell = document.createElement("td");

    cell.colSpan = 6;

    cell.textContent = message;

    row.appendChild(cell);

    table.appendChild(row);
}
