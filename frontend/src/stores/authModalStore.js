import { defineStore } from "pinia";

export const useAuthModalStore = defineStore("authModal", {
    state: () => ({
        isOpen: false,
        mode: "login", // "login" | "signup"
    }),
    actions: {
        openWith(mode = "login") {
            this.mode = mode;
            this.isOpen = true;
        },
        close() {
            this.isOpen = false;
        },
    },
});