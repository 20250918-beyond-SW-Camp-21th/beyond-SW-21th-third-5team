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
import { computed, onMounted, ref } from 'vue';
import AlertBanner from '../AlertBanner.vue';
import HeroCard from '../HeroCard.vue';
import HourlyForecastCard from '../HourlyForecastCard.vue';
import PreviewCard from '../PreviewCard.vue';
import WeatherSummaryCard from '../WeatherSummaryCard.vue';

const heroVideos = import.meta.glob('../../assets/videos/*.mp4', {
  eager: true,
  import: 'default',
});
const fallbackHeroVideoSrc = new URL('../../assets/videos/basic.mp4', import.meta.url).href;

type WeatherItem = {
  category?: string;
  fcstValue?: string;
  fcstDate?: string;
  fcstTime?: string;
};

const weatherItems = ref<WeatherItem[] | null>(null);
const weatherError = ref<string | null>(null);
const isLoading = ref(false);
const locationLabel = ref('현재 위치');

const closestForecast = computed(() => getClosestForecast(weatherItems.value ?? []));

const heroVideoSrc = computed(() => {
  const tempValue = findValue(['TMP', 'T1H']);
  const tempC = parseNumber(tempValue);
  if (tempC === null) {
    return fallbackHeroVideoSrc;
  }
  const tempKey = mapTemperatureKey(tempC);
  const weatherKey = mapWeatherKey();
  const videoKey = `${tempKey}_${weatherKey}`;
  const path = `../../assets/videos/${videoKey}.mp4`;
  return (heroVideos[path] as string | undefined) ?? fallbackHeroVideoSrc;
});

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

    const res = await fetch(`/api/weather/by-location?lat=${lat}&lon=${lon}`);
    if (!res.ok) {
      throw new Error(`날씨 API 요청 실패 (${res.status})`);
    }

    const data = await res.json();
    weatherItems.value = Array.isArray(data) ? data : data?.items ?? [];
  } catch (error) {
    weatherError.value = error instanceof Error ? error.message : '알 수 없는 오류가 발생했습니다.';
  } finally {
    isLoading.value = false;
  }
}

function findValue(categories: string[]) {
  const items = weatherItems.value ?? [];
  if (!items.length) {
    return null;
  }
  const target = closestForecast.value;
  if (target) {
    const match = items.find(
      (item) =>
        item &&
        categories.includes(item.category ?? '') &&
        item.fcstDate === target.fcstDate &&
        item.fcstTime === target.fcstTime,
    );
    if (match?.fcstValue) {
      return match.fcstValue;
    }
  }
  const fallback = items.find((item) => item && categories.includes(item.category ?? ''));
  return fallback?.fcstValue ?? null;
}

function mapWeatherKey() {
  const lightning = parseNumber(findValue(['LGT']));
  if (lightning !== null && lightning > 0) {
    return 'thunder';
  }

  const precipitationType = parseNumber(findValue(['PTY']));
  if (precipitationType !== null && precipitationType > 0) {
    if (precipitationType === 3) {
      return 'snow';
    }
    return 'rain';
  }

  const sky = findValue(['SKY']);
  if (sky === '1') {
    return 'sunny';
  }
  return 'cloud';
}

function mapTemperatureKey(tempC: number) {
  if (tempC >= 23) {
    return '23';
  }
  if (tempC >= 17) {
    return '22';
  }
  if (tempC >= 9) {
    return '16';
  }
  if (tempC >= 5) {
    return '8';
  }
  return '4';
}

function parseNumber(value?: string | null) {
  if (!value) {
    return null;
  }
  const parsed = Number(value);
  return Number.isNaN(parsed) ? null : parsed;
}

function getClosestForecast(items: WeatherItem[]) {
  if (!items.length) {
    return null;
  }

  const candidates = new Map<string, Date>();
  for (const item of items) {
    const date = parseForecastDateTime(item.fcstDate, item.fcstTime);
    if (!date) {
      continue;
    }
    const key = `${item.fcstDate}-${item.fcstTime}`;
    if (!candidates.has(key)) {
      candidates.set(key, date);
    }
  }

  if (!candidates.size) {
    return null;
  }

  const now = new Date();
  let bestFuture: { key: string; date: Date; diff: number } | null = null;
  let bestPast: { key: string; date: Date; diff: number } | null = null;

  for (const [key, date] of candidates.entries()) {
    const diff = date.getTime() - now.getTime();
    if (diff >= 0) {
      if (!bestFuture || diff < bestFuture.diff) {
        bestFuture = { key, date, diff };
      }
    } else {
      const abs = Math.abs(diff);
      if (!bestPast || abs < bestPast.diff) {
        bestPast = { key, date, diff: abs };
      }
    }
  }

  const chosen = bestFuture ?? bestPast;
  if (!chosen) {
    return null;
  }

  const [fcstDate, fcstTime] = chosen.key.split('-');
  return { fcstDate, fcstTime };
}

function parseForecastDateTime(fcstDate?: string, fcstTime?: string) {
  if (!fcstDate || !fcstTime || fcstDate.length !== 8 || fcstTime.length !== 4) {
    return null;
  }
  const year = Number(fcstDate.slice(0, 4));
  const month = Number(fcstDate.slice(4, 6)) - 1;
  const day = Number(fcstDate.slice(6, 8));
  const hour = Number(fcstTime.slice(0, 2));
  const minute = Number(fcstTime.slice(2, 4));

  if ([year, month, day, hour, minute].some(Number.isNaN)) {
    return null;
  }
  return new Date(year, month, day, hour, minute);
}

onMounted(() => {
  void loadWeatherByLocation();
});
</script>
