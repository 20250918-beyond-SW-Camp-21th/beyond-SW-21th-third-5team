import { createRouter, createWebHistory } from 'vue-router';
import CalendarRecord from '../components/pages/CalendarRecord.vue';
import LoginPage from '../components/pages/LoginPage.vue';
import TemperatureMap from '../components/pages/TemperatureMap.vue';
import TodayWeather from '../components/pages/TodayWeather.vue';
import WeeklyWeather from '../components/pages/WeeklyWeather.vue';

const routes = [
  { path: '/', redirect: { name: 'today' } },
  { path: '/today', name: 'today', component: TodayWeather },
  { path: '/weekly', name: 'weekly', component: WeeklyWeather },
  { path: '/temperature-map', name: 'map', component: TemperatureMap },
  { path: '/calendar', name: 'calendar', component: CalendarRecord },
  { path: '/login', name: 'login', component: LoginPage },
];

const router = createRouter({
  history: createWebHistory(),
  routes,
  scrollBehavior() {
    return { top: 0 };
  },
});

export default router;
