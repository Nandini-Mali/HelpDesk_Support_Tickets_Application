package com.nexora.helpdesk.desktop.api;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.nexora.helpdesk.desktop.model.Ticket;
import com.nexora.helpdesk.desktop.model.UserSession;
import com.nexora.helpdesk.desktop.util.SessionManager;

public class ApiClient {

    private static final String BASE_URL = "http://localhost:8080";

    private final HttpClient client = HttpClient.newHttpClient();

    private final ObjectMapper mapper = new ObjectMapper();

    private HttpRequest.Builder request(String path) {

        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json");

        if (SessionManager.getUser() != null) {

            String email = SessionManager.getEmail();
            char[] password = SessionManager.getPassword();

            String credentials = email + ":" + new String(password);

            String encoded = Base64.getEncoder()
                    .encodeToString(credentials.getBytes(StandardCharsets.UTF_8));

            builder.header("Authorization", "Basic " + encoded);

            java.util.Arrays.fill(password, '\0');
        }

        return builder;
    }

    private JsonNode send(HttpRequest request) throws Exception {

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
        );

        if (response.statusCode() < 200 ||
                response.statusCode() >= 300) {

            throw new RuntimeException(
                    "HTTP " + response.statusCode()
                    + ": " + response.body()
            );
        }

        if (response.body() == null || response.body().isBlank()) {
            return mapper.createObjectNode();
        }

        return mapper.readTree(response.body());
    }

    // LOGIN

    public UserSession login(String email, char[] password)
            throws Exception {

        ObjectNode body = mapper.createObjectNode();

        body.put("email", email);
        body.put("password", new String(password));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + "/api/auth/login"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(
                        mapper.writeValueAsString(body)))
                .build();

        JsonNode response = send(request);

        return new UserSession(
                response.path("id").asLong(),
                response.path("name").asText(),
                response.path("email").asText(),
                response.path("role").asText()
        );
    }

    // GET TICKETS

    public List<Ticket> getTickets() throws Exception {

        long userId = SessionManager.getUser().getId();

        JsonNode response = send(
                request("/api/tickets?userId=" + userId)
                        .GET()
                        .build()
        );

        JsonNode rows = response.isArray()
                ? response
                : response.path("content");

        List<Ticket> tickets = new ArrayList<>();

        if (rows.isArray()) {

            for (JsonNode node : rows) {

                tickets.add(
                        mapper.treeToValue(node, Ticket.class)
                );
            }
        }

        return tickets;
    }

    // GET TICKET DETAILS

    public Ticket getTicket(long ticketId) throws Exception {

        long userId = SessionManager.getUser().getId();

        JsonNode response = send(
                request("/api/tickets/" + ticketId + "?userId=" + userId)
                        .GET()
                        .build()
        );

        return mapper.treeToValue(response, Ticket.class);
    }

    // CREATE TICKET

    public Ticket createTicket(
            String title,
            String description,
            String category,
            String priority) throws Exception {

        long userId = SessionManager.getUser().getId();

        ObjectNode body = mapper.createObjectNode();

        body.put("title", title);
        body.put("description", description);
        body.put("category", category);
        body.put("priority", priority);

        HttpRequest request = request(
                "/api/tickets?userId=" + userId
        )
        .POST(HttpRequest.BodyPublishers.ofString(
                mapper.writeValueAsString(body)))
        .build();

        JsonNode response = send(request);

        return mapper.treeToValue(response, Ticket.class);
    }

    // UPDATE STATUS

    public Ticket updateStatus(long ticketId, String status)
            throws Exception {

        long userId = SessionManager.getUser().getId();

        HttpRequest request = request(
                "/api/tickets/" + ticketId
                + "/status?status=" + status
                + "&userId=" + userId
        )
        .PUT(HttpRequest.BodyPublishers.noBody())
        .build();

        return mapper.treeToValue(send(request), Ticket.class);
    }

    // UPDATE PRIORITY

    public Ticket updatePriority(long ticketId, String priority)
            throws Exception {

        long userId = SessionManager.getUser().getId();

        HttpRequest request = request(
                "/api/tickets/" + ticketId
                + "/priority?priority=" + priority
                + "&userId=" + userId
        )
        .PUT(HttpRequest.BodyPublishers.noBody())
        .build();

        return mapper.treeToValue(send(request), Ticket.class);
    }

    // GET COMMENTS

    public String getComments(long ticketId) throws Exception {

        long userId = SessionManager.getUser().getId();

        return send(
                request("/api/comments/ticket/" + ticketId
                        + "?userId=" + userId)
                        .GET()
                        .build()
        ).toPrettyString();
    }

    // GET TICKET HISTORY

    public String getHistory(long ticketId) throws Exception {

        long userId = SessionManager.getUser().getId();

        return send(
                request("/api/ticket-history/ticket/" + ticketId
                        + "?userId=" + userId)
                        .GET()
                        .build()
        ).toPrettyString();
    }
}