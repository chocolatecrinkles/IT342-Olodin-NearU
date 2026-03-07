const API = "http://localhost:8080/api/auth";

export async function register(user) {

    const response = await fetch(API + "/register", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(user)
    });

    return response.json();
}

export async function login(credentials) {

    const response = await fetch(API + "/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify(credentials)
    });

    return response.json();
}