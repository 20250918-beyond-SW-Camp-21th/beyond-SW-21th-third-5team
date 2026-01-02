<script setup>
import { computed } from "vue";
import { useRoute, useRouter } from "vue-router";
import Navigation from "./components/Navigation.vue";
import AuthModal from "./components/auth/authModal.vue";
import { onMounted, onBeforeUnmount } from "vue";
import { useAuthModalStore } from "./stores/authModalStore";

const route = useRoute();
const router = useRouter();
const authModal = useAuthModalStore();

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

function handleTabChange(tab) {
  console.log("tab-change:", JSON.stringify(tab));
  if (tab === "로그인") {
    authModal.openWith("login");
    return;
  }
  const name = routeByTab[tab];
  if (name) router.push({ name });
}

function openLogin() {
  authModal.openWith("login");
}
onMounted(() => {
  window.addEventListener("auth:open", openLogin);
});
onBeforeUnmount(() => {
  window.removeEventListener("auth:open", openLogin);
});

</script>

<template>
  <div class="min-h-screen bg-gradient-to-b from-[#F6FAFF] to-[#FFFFFF]">
    <Navigation :active-tab="activeTab" @tab-change="handleTabChange" />
    <RouterView />
    <AuthModal />
  </div>
</template>
