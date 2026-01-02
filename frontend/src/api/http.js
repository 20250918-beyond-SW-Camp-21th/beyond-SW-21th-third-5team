import axios from "axios";
import { tokenStore } from "./tokenStore";

const LOGIN_PATH = "/";

export function redirectToLogin() {
    tokenStore.clear();
    if (window.location.pathname !== LOGIN_PATH) {
        window.location.href = LOGIN_PATH;
    }
}

export const http = axios.create({
    baseURL: "/api",
    headers: { "Content-Type": "application/json" },
});

http.interceptors.request.use((config) => {
    const access = tokenStore.access;
    config.headers = config.headers ?? {};
    if (access) config.headers.Authorization = `Bearer ${access}`;
    return config;
});

let refreshingPromise = null;

http.interceptors.response.use(
    (res) => res,
    async (err) => {
        if (!err.response) throw err;

        const original = err.config;
        if (!original) throw err;

        if (original.url?.includes("/auth/refresh") && (err.response.status === 401 || err.response.status === 403)) {
            redirectToLogin();
            throw err;
        }

        if (err.response.status === 401 && !original._retry) {
            original._retry = true;

            const refresh = tokenStore.refresh;
            if (!refresh) {
                redirectToLogin();
                throw err;
            }

            if (!refreshingPromise) {
                refreshingPromise = axios
                    .post("/api/auth/refresh", { refreshToken: refresh })
                    .then((res) => {
                        const data = res.data?.data;
                        const accessToken = data?.accessToken;
                        const refreshToken = data?.refreshToken;

                        if (!accessToken || !refreshToken) throw new Error("Invalid refresh response");

                        tokenStore.access = accessToken;
                        tokenStore.refresh = refreshToken;
                        return accessToken;
                    })
                    .catch((e) => {
                        redirectToLogin();
                        throw e;
                    })
                    .finally(() => {
                        refreshingPromise = null;
                    });
            }

            const newAccess = await refreshingPromise;
            original.headers = original.headers ?? {};
            original.headers.Authorization = `Bearer ${newAccess}`;
            return http(original);
        }

        throw err;
    }
);
