<script setup>
import { computed } from 'vue';
import { useRoute, useRouter } from 'vue-router';
import Navigation from './components/Navigation.vue';

const route = useRoute();
const router = useRouter();

const tabByRouteName = {
  today: '오늘의 날씨',
  weekly: '이번주 날씨',
  map: '국내 기온지도',
  calendar: '달력 기록',
  login: '로그인',
};

const routeByTab = {
  '오늘의 날씨': 'today',
  '이번주 날씨': 'weekly',
  '국내 기온지도': 'map',
  '달력 기록': 'calendar',
  '로그인': 'login',
};

const activeTab = computed(() => tabByRouteName[route.name] ?? '오늘의 날씨');
const isLogin = computed(() => route.name === 'login');

function handleTabChange(tab) {
  const name = routeByTab[tab];
  if (name) {
    router.push({ name });
  }
}
</script>

<template>
  <div class="min-h-screen bg-gradient-to-b from-[#F6FAFF] to-[#FFFFFF]">
    <Navigation :active-tab="activeTab" :simple="isLogin" @tab-change="handleTabChange" />
    <RouterView />
  </div>
</template>
