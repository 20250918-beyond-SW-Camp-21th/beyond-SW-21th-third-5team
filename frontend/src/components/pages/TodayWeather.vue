<template>
  <main class="max-w-[1440px] mx-auto px-20 py-8">
    <div class="grid grid-cols-2 gap-6 mb-8">
      <HeroCard :hero-video-src="heroVideoSrc" />
      <WeatherSummaryCard
        :weather-items="weatherItems"
        :is-loading="isLoading"
        :error="weatherError"
        :location-label="locationLabel"
      />
    </div>

    <div class="mb-8">
      <HourlyForecastCard />
    </div>

    <div class="mb-8">
      <AlertBanner />
    </div>

    <div class="grid grid-cols-3 gap-6">
      <PreviewCard type="tomorrow" />
      <PreviewCard type="week" />
      <PreviewCard type="map" />
    </div>
  </main>
</template>

<script setup lang="ts">
import axios from 'axios';
import { onMounted, ref } from 'vue';
import AlertBanner from '../AlertBanner.vue';
import HeroCard from '../HeroCard.vue';
import HourlyForecastCard from '../HourlyForecastCard.vue';
import PreviewCard from '../PreviewCard.vue';
import WeatherSummaryCard from '../WeatherSummaryCard.vue';

const heroVideoSrc = new URL('../../assets/videos/흐림_4도이하.mp4', import.meta.url).href;

type WeatherItem = {
  category?: string;
  fcstValue?: string;
};

const weatherItems = ref<WeatherItem[] | null>(null);
const weatherError = ref<string | null>(null);
const isLoading = ref(false);
const locationLabel = ref('현재 위치');

function getCurrentPosition(): Promise<GeolocationPosition> {
  return new Promise((resolve, reject) => {
    if (!('geolocation' in navigator)) {
      reject(new Error('브라우저에서 위치 정보를 지원하지 않습니다.'));
      return;
    }
    navigator.geolocation.getCurrentPosition(resolve, reject, {
      enableHighAccuracy: false,
      timeout: 10000,
      maximumAge: 300000,
    });
  });
}

async function loadWeatherByLocation() {
  isLoading.value = true;
  weatherError.value = null;

  try {
    const position = await getCurrentPosition();
    const lat = position.coords.latitude;
    const lon = position.coords.longitude;

    const { data } = await axios.get('/api/weather/by-location', {
      params: { lat, lon },
    });
    weatherItems.value = Array.isArray(data) ? data : data?.items ?? [];
  } catch (error) {
    weatherError.value = error instanceof Error ? error.message : '알 수 없는 오류가 발생했습니다.';
  } finally {
    isLoading.value = false;
  }
}

onMounted(() => {
  void loadWeatherByLocation();
});
</script>
