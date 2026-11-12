const API_BASE = "/api";

// ===============================
// GET SAVED SESSION
// ===============================

function getSession() {
    try {
        const session = JSON.parse(
            sessionStorage.getItem("helpdeskSession")
        );

        if (!session || !session.user) {
            return null;
        }

        return session;

    } catch {
        return null;
    }
}

// ===============================
// SAVE SESSION
// ===============================

function saveSession(user, email, password) {

    sessionStorage.setItem(
        "helpdeskSession",
        JSON.stringify({
            user,
            email,
            password
        })
    );
}

// ===============================
// LOGOUT
// ===============================

function logout() {

    sessionStorage.removeItem("helpdeskSession");

    window.location.replace("index.html");
}

// ===============================
// CHECK LOGIN AND ROLE
// ===============================

function checkAuth(requiredRole = null) {

    const session = getSession();

    if (!session) {

        window.location.replace("index.html");

        return null;
    }

    if (
        requiredRole &&
        session.user.role !== requiredRole
    ) {

        if (session.user.role === "AGENT") {

            window.location.replace("agent-dashboard.html");

        } else {

            window.location.replace("user-dashboard.html");
        }

        return null;
    }

    return session;
}

// ===============================
// DISPLAY MESSAGE
// ===============================

function showMessage(element, message, success = false) {

    if (!element) return;

    element.textContent = message;

    element.className = success
        ? "message success"
        : "message";
}

// ===============================
// API REQUEST
// ===============================

async function apiRequest(url, options = {}, authenticate = true) {

    const session = getSession();

    const headers = {
        ...(options.headers || {})
    };

    if (options.body !== undefined) {

        headers["Content-Type"] = "application/json";
    }

    if (authenticate && session) {

        const credentials = new TextEncoder().encode(
            `${session.email}:${session.password}`
        );

        let binary = "";

        credentials.forEach(byte => {
            binary += String.fromCharCode(byte);
        });

        headers.Authorization =
            "Basic " + btoa(binary);
    }

    const response = await fetch(API_BASE + url, {
        ...options,
        headers
    });

    if (response.status === 401) {

        if (authenticate) {
            sessionStorage.removeItem("helpdeskSession");
        }

        throw new Error(
            "Invalid email or password. Please login again."
        );
    }

    if (response.status === 403) {

        throw new Error(
            "You do not have permission to perform this action."
        );
    }

    if (!response.ok) {

        let errorMessage = "Something went wrong.";

        try {

            const error = await response.json();

            errorMessage =
                error.message ||
                error.detail ||
                error.error ||
                errorMessage;

        } catch {

            errorMessage =
                `Server error: ${response.status}`;
        }

        throw new Error(errorMessage);
    }

    if (response.status === 204) {
        return null;
    }

    const contentType =
        response.headers.get("content-type") || "";

    if (contentType.includes("application/json")) {
        return await response.json();
    }

    return null;
}

// ===============================
// REGISTRATION
// ===============================

document.addEventListener("DOMContentLoaded", () => {

    const registerForm =
        document.getElementById("registerForm");

    if (!registerForm) return;

    registerForm.addEventListener("submit", async event => {

        event.preventDefault();

        const name =
            document.getElementById("name").value.trim();

        const email =
            document.getElementById("email")
                .value.trim().toLowerCase();

        const password =
            document.getElementById("password").value;

        const confirmPassword =
            document.getElementById("confirmPassword").value;

        const message =
            document.getElementById("message");

        if (!name || !email || !password) {

            showMessage(
                message,
                "All fields are required."
            );

            return;
        }

        if (password.length < 8) {

            showMessage(
                message,
                "Password must contain at least 8 characters."
            );

            return;
        }

        if (password !== confirmPassword) {

            showMessage(
                message,
                "Passwords do not match."
            );

            return;
        }

        try {

            await apiRequest(
                "/auth/register",
                {
                    method: "POST",
                    body: JSON.stringify({
                        name,
                        email,
                        password
                    })
                },
                false
            );

            showMessage(
                message,
                "Registration successful! Please login.",
                true
            );

            registerForm.reset();

            setTimeout(() => {

                window.location.href = "index.html";

            }, 1200);

        } catch (error) {

            showMessage(
                message,
                error.message
            );
        }
    });

});

// ===============================
// LOGIN
// ===============================

document.addEventListener("DOMContentLoaded", () => {

    const loginForm =
        document.getElementById("loginForm");

    if (!loginForm) return;

    loginForm.addEventListener("submit", async event => {

        event.preventDefault();

        const email =
            document.getElementById("email")
                .value.trim().toLowerCase();

        const password =
            document.getElementById("password").value;

        const message =
            document.getElementById("message");

        if (!email || !password) {

            showMessage(
                message,
                "Email and password are required."
            );

            return;
        }

        try {

            const response = await apiRequest(
                "/auth/login",
                {
                    method: "POST",
                    body: JSON.stringify({
                        email,
                        password
                    })
                },
                false
            );

            if (!response || !response.id || !response.role) {

                throw new Error(
                    "Invalid login response from server."
                );
            }

            if (
                response.role !== "USER" &&
                response.role !== "AGENT"
            ) {

                throw new Error("Invalid user role.");
            }

            saveSession(response, email, password);

            showMessage(
                message,
                "Login successful!",
                true
            );

            if (response.role === "AGENT") {

                window.location.href =
                    "agent-dashboard.html";

            } else {

                window.location.href =
                    "user-dashboard.html";
            }

        } catch (error) {

            showMessage(
                message,
                error.message
            );
        }
    });

});
// SHOW / HIDE PASSWORD

document.addEventListener("DOMContentLoaded", () => {

    const password = document.getElementById("password");
    const toggle = document.getElementById("togglePassword");

    if (!password || !toggle) return;

    toggle.addEventListener("click", () => {

        if (password.type === "password") {

            password.type = "text";
            toggle.textContent = "Hide";

        } else {

            password.type = "password";
            toggle.textContent = "Show";

        }

    });

});

// ===============================
// GLOBAL FUNCTIONS
// ===============================

window.HelpDesk = {

    API_BASE,

    apiRequest,

    getSession,

    saveSession,

    checkAuth,

    logout,

    showMessage

};

window.logout = logout;
