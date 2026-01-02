<template>
  <main class="max-w-[1440px] mx-auto px-20 py-8">
    <div class="grid grid-cols-2 gap-6 mb-8">
      <HeroCard :hero-video-src="heroVideoSrc" :weather-type="weatherType" />
            <WeatherSummaryCard
        :location-label="locationDisplay"
        :date-label="dateLabel"
        :status-message="statusMessage"
        :temperature-display="temperatureDisplay"
        :weather-summary="weatherSummary"
        :weather-stats="weatherStats"
        :outfit-items="outfitItems"
        :outfit-description="outfitDescription"
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
import axios from 'axios';
import { CloudRain, Droplets, Glasses, ShoppingBag, Wind, Coffee } from 'lucide-vue-next';
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

type WeatherStat = {
  icon: unknown;
  label: string;
  value: string;
};

type OutfitItem = {
  icon: unknown;
  label: string;
};

const weatherItems = ref<WeatherItem[] | null>(null);
const weatherError = ref<string | null>(null);
const isLoading = ref(false);
const locationLabel = ref("현재 위치");

const closestForecast = computed(() => getClosestForecast(weatherItems.value ?? []));
const weatherType = computed(() => mapWeatherKey());

const heroVideoSrc = computed(() => {
  const tempValue = findValue(['TMP', 'T1H']);
  const tempC = parseNumber(tempValue);
  if (tempC === null) {
    return fallbackHeroVideoSrc;
  }
  const tempKey = mapTemperatureKey(tempC);
  const videoKey = `${tempKey}_${weatherType.value}`;
  const path = `../../assets/videos/${videoKey}.mp4`;
  return (heroVideos[path] as string | undefined) ?? fallbackHeroVideoSrc;
});

const locationDisplay = computed(() => locationLabel.value || "--");
const dateLabel = computed(() => formatKoreanDate(new Date()));

const temperatureDisplay = computed(() => {
  const value = findValue(['TMP', 'T1H']);
  return value ? `${value}C` : "--C";
});

const weatherSummary = computed(() => {
  const skyValue = findValue(['SKY']);
  if (!skyValue) {
    return "--";
  }
  const label = mapWeatherLabel(weatherType.value);
  return "날씨: " + label;
});

const weatherStats = computed<WeatherStat[]>(() => {
  const windValue = findValue(['WSD']);
  const humidityValue = findValue(['REH']);
  const precipValue = findValue(['POP']);

  return [
    { icon: Wind, label: "바람", value: windValue ? `${windValue}m/s` : "--" },
    { icon: Droplets, label: "습도", value: humidityValue ? `${humidityValue}%` : "--" },
    { icon: CloudRain, label: "강수확률", value: precipValue ? `${precipValue}%` : "--" },
  ];
});

const statusMessage = computed(() => {
  if (weatherError.value) {
    return "날씨 가져오기 실패: " + weatherError.value;
  }
  if (isLoading.value) {
    return "현재 위치의 날씨를 불러오는 중입니다.";
  }
  if (weatherItems.value?.length) {
    return "현재 위치 날씨 수신 (" + weatherItems.value.length + "개)";
  }
  return "";
});

const outfitRecommendation = computed(() => {
  const tempValue = findValue(['TMP', 'T1H']);
  const tempC = parseNumber(tempValue);
  const type = weatherType.value;

  if (tempC === null) {
    return {
      description: "--",
      items: [] as OutfitItem[],
    };
  }

  let description = "";
  let items: OutfitItem[] = [];

  if (tempC >= 23) {
    description = "민소매, 반팔, 반바지, 린넨";
    items = [
      { icon: Glasses, label: "자외선 차단" },
      { icon: ShoppingBag, label: "가벼운 옷차림" },
      { icon: Coffee, label: "시원한 음료" },
    ];
  } else if (tempC >= 17) {
    description = "반팔, 얇은 셔츠, 면바지";
    items = [
      { icon: ShoppingBag, label: "얇은 겉옷" },
      { icon: Glasses, label: "선글라스" },
      { icon: Coffee, label: "가벼운 음료" },
    ];
  } else if (tempC >= 9) {
    description = "가디건, 니트, 긴바지";
    items = [
      { icon: ShoppingBag, label: "가디건" },
      { icon: Coffee, label: "따뜻한 음료" },
      { icon: Glasses, label: "보온용품" },
    ];
  } else if (tempC >= 5) {
    description = "코트, 니트, 기모";
    items = [
      { icon: ShoppingBag, label: "코트" },
      { icon: Coffee, label: "따뜻한 음료" },
      { icon: Glasses, label: "보온용품" },
    ];
  } else {
    description = "패딩, 두꺼운 코트, 목도리";
    items = [
      { icon: ShoppingBag, label: "패딩" },
      { icon: Coffee, label: "따뜻한 음료" },
      { icon: Glasses, label: "보온용품" },
    ];
  }

  if (type === "rain" || type === "thunder") {
    items = [...items.slice(0, 2), { icon: CloudRain, label: "우산" }];
  }

  return { description, items };
});

const outfitItems = computed(() => outfitRecommendation.value.items);
const outfitDescription = computed(() => outfitRecommendation.value.description);

function getCurrentPosition(): Promise<GeolocationPosition> {
  return new Promise((resolve, reject) => {
    if (!('geolocation' in navigator)) {
      reject(new Error("브라우저에서 위치 정보를 지원하지 않습니다."));
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
    if (axios.isAxiosError(error)) {
      const status = error.response?.status;
      const message = error.response?.data?.message;
      weatherError.value = status ? `날씨 API 요청 실패 (${status})` : message ?? error.message;
    } else {
      weatherError.value = error instanceof Error ? error.message : "알 수 없는 오류가 발생했습니다.";
    }
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
        categories.includes(item.category ?? "") &&
        item.fcstDate === target.fcstDate &&
        item.fcstTime === target.fcstTime,
    );
    if (match?.fcstValue) {
      return match.fcstValue;
    }
  }
  const fallback = items.find((item) => item && categories.includes(item.category ?? ""));
  return fallback?.fcstValue ?? null;
}

function mapWeatherKey() {
  const lightning = parseNumber(findValue(['LGT']));
  if (lightning !== null && lightning > 0) {
    return "thunder";
  }

  const precipitationType = parseNumber(findValue(['PTY']));
  if (precipitationType !== null && precipitationType > 0) {
    if (precipitationType === 3) {
      return "snow";
    }
    return "rain";
  }

  const sky = findValue(['SKY']);
  if (sky === "1") {
    return "sunny";
  }
  if (sky === "3" || sky === "4" || sky === "2") {
    return "cloud";
  }
  return "cloud";
}

function mapWeatherLabel(type: string) {
  switch (type) {
    case "sunny":
      return "맑음";
    case "cloud":
      return "흐림";
    case "rain":
      return "비";
    case "snow":
      return "눈";
    case "thunder":
      return "천둥번개";
    default:
      return "--";
  }
}

function mapTemperatureKey(tempC: number) {
  if (tempC >= 23) {
    return "23";
  }
  if (tempC >= 17) {
    return "22";
  }
  if (tempC >= 9) {
    return "16";
  }
  if (tempC >= 5) {
    return "8";
  }
  return "4";
}

function formatKoreanDate(date: Date) {
  const weekdays = ["일", "월", "화", "수", "목", "금", "토"];
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const weekday = weekdays[date.getDay()];
  return `${month}월 ${day}일 (${weekday})`;
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
