import { LoginResponse, User } from "../entity";
import { client } from "./client";

function checkLoggedIn() {
    let token = localStorage.getItem("token");
    if (!token) {
        return false;
    }

    try {
        let payload = token.split(".")[1];
        payload = JSON.parse(atob(payload));
        return payload["exp"] && payload["exp"] > Date.now() / 1000;
    } catch (e) {
        return false;
    }
}

function setToken(token) {
    localStorage.setItem("token", token);
}

function getToken() {
    return localStorage.getItem("token");
}

function logout() {
    localStorage.removeItem("token");
}

function login(username, password) {
    let params = {
        username,
        password
    };

    const data = Object.entries(params)
        .map(([key, val]) => `${key}=${encodeURIComponent(val)}`)
        .join('&');

    return new Promise((resolve, reject) => {
        client.post('login', data, {
            headers: {
                'content-type': 'application/x-www-form-urlencoded'
            }
        }).then(response => {
            resolve(new LoginResponse(response.data));
        }).catch(reason => {
            reject("Authentication failed");
        })
    });
}

const authService = {
    login,
    logout,
    setToken,
    getToken,
    checkLoggedIn
};

export {
    login,
    logout,
    setToken,
    getToken,
    checkLoggedIn
}

export default authService;