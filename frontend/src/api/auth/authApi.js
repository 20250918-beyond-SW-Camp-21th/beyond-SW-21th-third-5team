import { http } from "../http";

export const authApi = {
    login(payload) {
        return http.post("/auth/login", payload);
    },
    signup(payload) {
        return http.post("/auth/signup", payload);
    },
    refresh(payload) {
        return http.post("/auth/refresh", payload);
    },
    logout(payload) {
        return http.post("/auth/logout", payload);
    },
    passwordResetRequest(payload) {
        return http.post("/auth/password/reset/request", payload);
    },
    passwordResetConfirm(payload) {
        return http.post("/auth/password/reset/confirm", payload);
    },
};
