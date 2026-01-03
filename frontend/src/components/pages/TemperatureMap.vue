<template>
  <main class="max-w-[1440px] mx-auto px-20 py-8">
    <div class="grid grid-cols-[1fr_400px] gap-6">
      <div class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50 relative min-h-[700px]">
        <h3 class="text-[#1F2A37] mb-6">대한민국 기온 지도</h3>
        <div
          class="relative w-full max-w-[720px] bg-gradient-to-br from-[#F0F8FF] to-[#E6F3FF] rounded-3xl select-none mx-auto"
          style="aspect-ratio: 2 / 3;"
        >
          <img
            :src="koreaMap"
            alt="대한민국 지도"
            class="w-full h-full object-contain opacity-20"
          />

          <div
            v-for="city in visibleCities"
            :key="city.name"
            class="absolute"
            :style="{ top: city.position.top, left: city.position.left }"
          >
            <button
              type="button"
              class="group relative"
              :class="selectedCity === city.name ? 'z-20' : 'z-10'"
              @click="selectedCity = city.name"
            >
              <span
                class="flex h-12 w-12 items-center justify-center rounded-full shadow-lg hover:shadow-xl transition-all transform hover:scale-110"
                :class="[tempColor(city.temp), selectedCity === city.name ? 'ring-4 ring-[#6AA9FF] ring-opacity-50' : '']"
              >
                <span class="text-sm font-semibold text-[#1F2A37]">{{ city.temp }}°C</span>
              </span>
              <span
                class="absolute -top-2 right-0 bg-white/80 border border-[#E6EEF9] text-[#1F2A37] text-xs px-2 py-1 rounded-full shadow-sm"
              >
                {{ formatPercent(city.pop) }}
              </span>
              <span class="absolute -bottom-6 left-1/2 -translate-x-1/2 whitespace-nowrap block">
                <span class="text-xs font-medium text-[#6B7280]">{{ city.name }}</span>
              </span>
            </button>
          </div>
        </div>
      </div>

      <div class="space-y-6">
        <div class="bg-gradient-to-br from-[#F0F8FF] to-[#E6F3FF] rounded-[28px] p-6 shadow-lg shadow-blue-100/50">
          <div class="flex items-start gap-3 mb-4">
            <MapPin class="w-6 h-6 text-[#6AA9FF]" />
            <div class="flex-1">
              <h4 class="text-[#1F2A37] mb-1">{{ currentCity.name }}</h4>
              <div class="text-3xl font-bold text-[#1F2A37] mb-2">{{ currentCity.temp }}°C</div>
            </div>
            <img :src="mascotSrc" alt="마스코트" class="w-12 h-12 object-contain" />
          </div>

          <div class="space-y-2 mb-4">
            <div class="flex items-center gap-2 text-sm">
              <Wind class="w-4 h-4 text-[#6B7280]" />
              <span class="text-[#6B7280]">바람</span>
              <span class="text-[#1F2A37] font-medium">{{ formatWind(currentCity.wsd) }}</span>
            </div>
            <div class="flex items-center gap-2 text-sm">
              <Droplets class="w-4 h-4 text-[#6B7280]" />
              <span class="text-[#6B7280]">습도</span>
              <span class="text-[#1F2A37] font-medium">{{ formatPercent(currentCity.reh) }}</span>
            </div>
            <div class="flex items-center gap-2 text-sm">
              <CloudRain class="w-4 h-4 text-[#6B7280]" />
              <span class="text-[#6B7280]">강수확률</span>
              <span class="text-[#1F2A37] font-medium">{{ formatPercent(currentCity.pop) }}</span>
            </div>
          </div>

          <div class="bg-white/80 rounded-2xl p-4">
            <div class="text-sm text-[#6B7280] mb-1">추천 옷차림</div>
            <div class="text-[#1F2A37] font-medium">{{ currentCity.outfit }}</div>
          </div>
        </div>

        <div class="bg-white rounded-[28px] p-6 shadow-lg shadow-blue-100/50">
          <h4 class="text-[#1F2A37] mb-4">범례</h4>
          <div class="space-y-3">
            <div v-for="legend in legends" :key="legend.range" class="flex items-center gap-3">
              <div class="w-8 h-8 rounded-lg shadow-sm" :class="legend.color" />
              <span class="text-sm text-[#6B7280]">{{ legend.range }}</span>
            </div>
          </div>
        </div>

        <div class="bg-white rounded-[28px] p-6 shadow-lg shadow-blue-100/50">
          <h4 class="text-[#1F2A37] mb-4">인기 지역 빠른 선택</h4>
          <div class="flex flex-wrap gap-2">
            <button
              v-for="city in loadedPopularCities"
              :key="city"
              type="button"
              class="px-4 py-2 rounded-xl transition-all"
              :class="selectedCity === city ? 'bg-[#6AA9FF] text-white' : 'bg-[#F6FAFF] text-[#6B7280] hover:bg-[#EAF2FF]'"
              @click="selectedCity = city"
            >
              {{ city }}
            </button>
          </div>
        </div>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import axios from 'axios';
import { CloudRain, Droplets, MapPin, Wind } from 'lucide-vue-next';
import { computed, onMounted, ref } from 'vue';
import koreaMap from '../../assets/images/korea-map.png';

type SidoWeatherPayload = {
  temperature?: number | null;
  pop?: number | null;
  wsd?: number | null;
  reh?: number | null;
  recommnededOutfit?: string;
};

type FetchResult = {
  sido: string;
  payload: SidoWeatherPayload | null;
  error: boolean;
};

interface City {
  name: string;
  temp: number;
  position: { top: string; left: string };
  outfit: string;
  pop?: number | null;
  wsd?: number | null;
  reh?: number | null;
}

const SIDO_ORDER = [
  '서울',
  '부산',
  '대구',
  '인천',
  '광주',
  '대전',
  '울산',
  '세종',
  '경기',
  '강원',
  '충북',
  '충남',
  '전북',
  '전남',
  '경북',
  '경남',
  '제주',
];

const selectedCity = ref('서울');
const weatherBySido = ref<Record<string, SidoWeatherPayload>>({});
const loadedOrder = ref<string[]>([]);
const cacheKey = 'weather-sido-cache';
const mascotSrc = new URL('../../assets/images/마스코트.png', import.meta.url).href;

const cities = ref<City[]>([
  { name: '서울', temp: 18, position: { top: '15.895806520806522%', left: '30.378257722007724%' }, outfit: '가디건 + 긴바지' },
  { name: '인천', temp: 17, position: { top: '17.95500858000858%', left: '20.259210102960104%' }, outfit: '가디건 + 긴바지' },
  { name: '경기', temp: 17, position: { top: '21.677257614757615%', left: '40.327380952380956%' }, outfit: '가디건 + 긴바지' },
  { name: '강원', temp: 14, position: { top: '13.762870012870012%', left: '58.49521396396396%' }, outfit: '자켓 + 긴바지' },
  { name: '세종', temp: 18, position: { top: '33.588052338052336%', left: '34.26842020592021%' }, outfit: '가디건 + 긴바지' },
  { name: '대전', temp: 19, position: { top: '41.39317889317889%', left: '40.38670366795367%' }, outfit: '자켓 + 긴바지' },
  { name: '충북', temp: 17, position: { top: '29.36038717288717%', left: '45.1295045045045%' }, outfit: '가디건 + 긴바지' },
  { name: '충남', temp: 18, position: { top: '38.19176319176319%', left: '24.120213963963963%' }, outfit: '가디건 + 긴바지' },
  { name: '전북', temp: 19, position: { top: '51.16500429000429%', left: '33.55554214929215%' }, outfit: '니트 + 긴바지' },
  { name: '전남', temp: 21, position: { top: '67.11175461175462%', left: '27.3970398970399%' }, outfit: '가디건 + 긴바지' },
  { name: '광주', temp: 21, position: { top: '58.67720398970398%', left: '25.764157014157014%' }, outfit: '가디건 + 긴바지' },
  { name: '대구', temp: 20, position: { top: '46.64776383526383%', left: '63.81113256113257%' }, outfit: '니트 + 긴바지' },
  { name: '경북', temp: 19, position: { top: '37.59116259116259%', left: '69.67302123552123%' }, outfit: '자켓 + 긴바지' },
  { name: '부산', temp: 22, position: { top: '57.671063921063926%', left: '73.90001608751608%' }, outfit: '얇은 셔츠 + 반팔' },
  { name: '울산', temp: 21, position: { top: '51.156290218790225%', left: '79.0590814028314%' }, outfit: '얇은 셔츠 + 반팔' },
  { name: '경남', temp: 21, position: { top: '57.50549656799657%', left: '55.792511261261254%' }, outfit: '가디건 + 긴바지' },
  { name: '제주', temp: 24, position: { top: '90.51627727470013%', left: '19.318181818181817%' }, outfit: '반팔 + 반바지' },
]);

const popularCities = ['서울', '경기', '대전', '부산', '제주'];

const legends = [
  { range: '0~5°C', color: 'bg-blue-400' },
  { range: '6~10°C', color: 'bg-blue-300' },
  { range: '11~16°C', color: 'bg-green-300' },
  { range: '17~22°C', color: 'bg-yellow-300' },
  { range: '23~27°C', color: 'bg-orange-300' },
  { range: '28°C+', color: 'bg-red-300' },
];

const visibleCities = computed(() => {
  if (!loadedOrder.value.length) return [];
  const orderSet = new Set(loadedOrder.value);
  return cities.value.filter((city) => orderSet.has(city.name));
});

const loadedPopularCities = computed(() => {
  if (!loadedOrder.value.length) return [];
  const orderSet = new Set(loadedOrder.value);
  return popularCities.filter((city) => orderSet.has(city));
});

const currentCity = computed(() => {
  if (!loadedOrder.value.length) {
    return cities.value[0];
  }
  const fallbackName = loadedOrder.value[0];
  const fallback = cities.value.find((city) => city.name === fallbackName) ?? cities.value[0];
  return loadedOrder.value.includes(selectedCity.value)
    ? cities.value.find((city) => city.name === selectedCity.value) ?? fallback
    : fallback;
});

if (import.meta.env.DEV) {
  (window as Window & { __temperatureMapCities?: City[] }).__temperatureMapCities = cities.value;
}

onMounted(() => {
  console.debug('[TemperatureMap] mounted');
  void loadSidoWeatherProgressively();
});

function tempColor(temp: number) {
  if (temp <= 5) return 'bg-blue-400';
  if (temp <= 10) return 'bg-blue-300';
  if (temp <= 16) return 'bg-green-300';
  if (temp <= 22) return 'bg-yellow-300';
  if (temp <= 27) return 'bg-orange-300';
  return 'bg-red-300';
}

async function loadSidoWeatherProgressively() {
  console.debug('[TemperatureMap] start load');
  const base = resolveBaseDateTime();
  const cachedMap = readCache(base.baseDate, base.baseTime);
  const staleMap = cachedMap ?? readStaleCache();
  if (staleMap) {
    applyCachedData(staleMap);
  }

  for (let i = 0; i < SIDO_ORDER.length; i += 1) {
    const sido = SIDO_ORDER[i];
    if ((cachedMap ?? staleMap)?.[sido]) {
      continue;
    }

    console.debug('[TemperatureMap] loading sido', sido);
    let result: FetchResult;
    try {
      const payload = await fetchOneSido(sido, base.baseDate, base.baseTime);
      result = { sido, payload, error: !payload };
    } catch {
      result = { sido, payload: null, error: true };
    }

    if (!result.payload && (staleMap ?? cachedMap)?.[sido]) {
      result = { ...result, payload: (staleMap ?? cachedMap)?.[sido] ?? null, error: false };
    }

    applyBatch([result]);
    const nextCache = mergeCache(cachedMap ?? staleMap ?? null, [result]);
    if (nextCache) {
      writeCache(base.baseDate, base.baseTime, nextCache);
    }
    await sleep(1500);
  }
}

async function fetchOneSido(sido: string, baseDate: string, baseTime: string) {
  console.debug('[TemperatureMap] fetchOneSido', sido);
  try {
    const { data } = await axios.get('/api/map/sido/one', {
      params: { sido, baseDate, baseTime },
    });
    return unwrapPayload(data);
  } catch (error) {
    if (shouldRetry(error)) {
      await sleep(600);
      const { data } = await axios.get('/api/map/sido/one', {
        params: { sido, baseDate, baseTime },
      });
      return unwrapPayload(data);
    }
    throw error;
  }
}

function unwrapPayload(data: any): SidoWeatherPayload | null {
  const candidate = data && typeof data === 'object' && 'success' in data && 'data' in data ? data.data : data;
  if (!candidate || typeof candidate !== 'object') return null;
  return {
    temperature: candidate.temperature ?? null,
    pop: candidate.pop ?? null,
    wsd: candidate.wsd ?? null,
    reh: candidate.reh ?? null,
    recommnededOutfit: candidate.recommnededOutfit ?? null,
  };
}

function applyCachedData(cachedMap: Record<string, SidoWeatherPayload>) {
  Object.entries(cachedMap).forEach(([sido, payload]) => {
    if (payload) {
      applySidoPayload(sido, payload);
    }
    pushLoadedOrder(sido);
  });
}

function applyBatch(results: FetchResult[]) {
  results.forEach((result) => {
    if (result.payload) {
      applySidoPayload(result.sido, result.payload);
    }
    pushLoadedOrder(result.sido);
  });
}

function applySidoPayload(sido: string, payload: SidoWeatherPayload) {
  weatherBySido.value = {
    ...weatherBySido.value,
    [sido]: payload,
  };
  cities.value = cities.value.map((city) => {
    if (city.name !== sido) return city;
    const nextTemp = payload.temperature ?? null;
    const nextPop = payload.pop ?? null;
    const nextWsd = payload.wsd ?? null;
    const nextReh = payload.reh ?? null;
    const nextOutfit = payload.recommnededOutfit ?? null;
    return {
      ...city,
      temp: nextTemp === null ? city.temp : nextTemp,
      outfit: nextOutfit === null ? city.outfit : nextOutfit,
      pop: nextPop === null ? city.pop : nextPop,
      wsd: nextWsd === null ? city.wsd : nextWsd,
      reh: nextReh === null ? city.reh : nextReh,
    };
  });
}

function pushLoadedOrder(sido: string) {
  if (!loadedOrder.value.includes(sido)) {
    loadedOrder.value = [...loadedOrder.value, sido];
  }
}

function mergeCache(
  cachedMap: Record<string, SidoWeatherPayload> | null,
  results: FetchResult[]
): Record<string, SidoWeatherPayload> | null {
  const nextMap = { ...(cachedMap ?? {}) };
  let updated = false;
  results.forEach((result) => {
    if (result.payload) {
      nextMap[result.sido] = result.payload;
      updated = true;
    }
  });
  return updated ? nextMap : cachedMap;
}

function sleep(ms: number) {
  return new Promise((resolve) => setTimeout(resolve, ms));
}

function shouldRetry(error: unknown) {
  if (!axios.isAxiosError(error)) {
    return false;
  }
  const status = error.response?.status;
  if (!status) {
    return true;
  }
  return status === 429 || status >= 500;
}

function formatPercent(value?: number | null) {
  if (value === null || value === undefined || Number.isNaN(value)) {
    return '--%';
  }
  return `${Math.round(value)}%`;
}

function formatWind(value?: number | null) {
  if (value === null || value === undefined || Number.isNaN(value)) {
    return '--m/s';
  }
  return `${value}m/s`;
}

function readCache(baseDate: string, baseTime: string) {
  try {
    const raw = localStorage.getItem(cacheKey);
    if (!raw) return null;
    const parsed = JSON.parse(raw);
    if (!parsed || typeof parsed !== 'object') return null;
    if (parsed.baseDate !== baseDate || parsed.baseTime !== baseTime) return null;
    if (!parsed.data || typeof parsed.data !== 'object') return null;
    return parsed.data as Record<string, SidoWeatherPayload>;
  } catch {
    return null;
  }
}

function writeCache(baseDate: string, baseTime: string, data: Record<string, SidoWeatherPayload>) {
  try {
    localStorage.setItem(cacheKey, JSON.stringify({ baseDate, baseTime, data, savedAt: Date.now() }));
  } catch {
    // Ignore storage errors (private mode, quota, etc.).
  }
}

function readStaleCache() {
  try {
    const raw = localStorage.getItem(cacheKey);
    if (!raw) return null;
    const parsed = JSON.parse(raw);
    if (!parsed || typeof parsed !== 'object') return null;
    if (!parsed.data || typeof parsed.data !== 'object') return null;
    return parsed.data as Record<string, SidoWeatherPayload>;
  } catch {
    return null;
  }
}

function resolveBaseDateTime() {
  const now = new Date(Date.now() - 30 * 60 * 1000);
  const baseTimes = [2300, 2000, 1700, 1400, 1100, 800, 500, 200];
  const hhmm = now.getHours() * 100 + now.getMinutes();
  let picked = baseTimes.find((bt) => hhmm >= bt);

  let date = new Date(now);
  if (!picked) {
    date.setDate(date.getDate() - 1);
    picked = 2300;
  }

  const baseDate = [
    date.getFullYear(),
    String(date.getMonth() + 1).padStart(2, '0'),
    String(date.getDate()).padStart(2, '0'),
  ].join('');

  const baseTime = String(picked).padStart(4, '0');
  return { baseDate, baseTime };
}
</script>
