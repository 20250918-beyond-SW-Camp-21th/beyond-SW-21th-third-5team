import { createRouter, createWebHistory } from "vue-router";

import CalendarRecord from "../components/pages/CalendarRecord.vue";
import TemperatureMap from "../components/pages/TemperatureMap.vue";
import TodayWeather from "../components/pages/TodayWeather.vue";
import WeeklyWeather from "../components/pages/WeeklyWeather.vue";

import { tokenStore } from "@/api/tokenStore";

const routes = [
    { path: "/", redirect: { name: "today" } },
    { path: "/today", name: "today", component: TodayWeather },
    { path: "/weekly", name: "weekly", component: WeeklyWeather },
    { path: "/temperature-map", name: "map", component: TemperatureMap },
    { path: "/calendar", name: "calendar", component: CalendarRecord, meta: { requiresAuth: true } },
];

const router = createRouter({
    history: createWebHistory(),
    routes,
    scrollBehavior() {
        return { top: 0 };
    },
});

router.beforeEach((to) => {
    if (to.meta?.requiresAuth && !tokenStore.access) {
        window.dispatchEvent(new CustomEvent("auth:open", { detail: { mode: "login" } }));
        return false;
    }
});

export default router;
