export const tokenStore = {
    get access() {
        return localStorage.getItem("accessToken") || "";
    },
    set access(v) {
        if (!v) localStorage.removeItem("accessToken");
        else localStorage.setItem("accessToken", v);
    },
    get refresh() {
        return localStorage.getItem("refreshToken") || "";
    },
    set refresh(v) {
        if (!v) localStorage.removeItem("refreshToken");
        else localStorage.setItem("refreshToken", v);
    },
    clear() {
        localStorage.removeItem("accessToken");
        localStorage.removeItem("refreshToken");
    },
};
