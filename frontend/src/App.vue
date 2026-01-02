<script setup>
import { computed, onMounted, onBeforeUnmount, ref } from "vue";
import { useRoute, useRouter } from "vue-router";
import Navigation from "./components/Navigation.vue";
import AuthModal from "./components/auth/authModal.vue";
import { useAuthModalStore } from "./stores/authModalStore";
import { tokenStore } from "@/api/tokenStore";
import { authApi } from "@/api/auth/authApi";

const route = useRoute();
const router = useRouter();
const authModal = useAuthModalStore();

const authVersion = ref(0);
const isAuthed = computed(() => {
  authVersion.value;
  return !!tokenStore.access;
});

const tabByRouteName = {
  today: "오늘의 날씨",
  weekly: "이번주 날씨",
  map: "국내 기온지도",
  calendar: "달력 기록",
};

const routeByTab = {
  "오늘의 날씨": "today",
  "이번주 날씨": "weekly",
  "국내 기온지도": "map",
  "달력 기록": "calendar",
};

const activeTab = computed(() => tabByRouteName[route.name] ?? "오늘의 날씨");

async function handleTabChange(tab) {
  console.log("before", authModal.isOpen, authModal.mode);
  if (tab === "로그인") {
    authModal.openWith("login");
    console.log("after", authModal.isOpen, authModal.mode);
    return;
  }
  if (tab === "로그아웃") {
    await logout();
    return;
  }
  const name = routeByTab[tab];
  if (name) router.push({ name });
}

function openLogin() {
  authModal.openWith("login");
}

function onAuthChanged() {
  console.log("auth:changed received");
  authVersion.value++;
}

onMounted(() => {
  window.addEventListener("auth:open", openLogin);
  window.addEventListener("auth:changed", onAuthChanged);
});

onBeforeUnmount(() => {
  window.removeEventListener("auth:open", openLogin);
  window.removeEventListener("auth:changed", onAuthChanged);
});

async function logout() {
  try {
    const refreshToken = tokenStore.refresh;
    if (refreshToken) await authApi.logout({ refreshToken });
  } finally {
    tokenStore.clear();
    authVersion.value++;
    router.push({ name: "today" });
  }
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-b from-[#F6FAFF] to-[#FFFFFF]">
    <Navigation :active-tab="activeTab" :is-authed="isAuthed" @tab-change="handleTabChange" />
    <RouterView />
    <AuthModal />
  </div>
</template>
