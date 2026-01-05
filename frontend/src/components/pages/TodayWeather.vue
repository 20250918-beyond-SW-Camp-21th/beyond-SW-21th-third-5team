<template>
  <main class="max-w-[1440px] mx-auto px-20 py-8">
    <!--  상단 펭귄 영상, 날씨 요약 카드  -->
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
          @record="goToCalendar"
      />
    </div>

    <!--  시간대별 예보  -->
    <div class="mb-8">
      <HourlyForecastCard :hourly-items="hourlyItems" />
    </div>
    <!--  날씨 종류 알림 멘트   -->
    <div class="mb-8">
      <AlertBanner :weather-type="weatherType"/>
    </div>

    <!-- 각 페이지 미리보기 카드   -->
    <div class="grid grid-cols-3 gap-6">
      <PreviewCard type="tomorrow" :content="tomorrowPreview" />
      <PreviewCard type="week" />
      <PreviewCard type="map" :content="mapPreview" />
    </div>
  </main>
</template>

<script setup lang="ts">
import { computed, onMounted, ref } from 'vue';
import { useRouter } from 'vue-router';
import axios from 'axios';
import { Cloud, CloudRain, CloudSnow, Droplets, Glasses, ShoppingBag, Sun, Wind, Coffee, Zap, Shirt, Layers, ThermometerSnowflake } from 'lucide-vue-next';
import AlertBanner from '../AlertBanner.vue';
import HeroCard from '../HeroCard.vue';
import HourlyForecastCard from '../HourlyForecastCard.vue';
import PreviewCard from '../PreviewCard.vue';
import WeatherSummaryCard from '../WeatherSummaryCard.vue';


/* 영상 파일*/
const heroVideos = import.meta.glob('../../assets/videos/*.mp4', {
  eager: true,
  import: 'default',
});
const fallbackHeroVideoSrc = new URL('../../assets/videos/basic.mp4', import.meta.url).href;


/*기상청 api 응답 형태*/
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

type TomorrowPreview = {
  title: string;
  subtitle: string;
  highlight: string;
  description: string;
};

type MapPreview = {
  title: string;
  subtitle: string;
  highlight: string;
  description: string;
};

/* 날씨 화면 상태 */
const weatherItems = ref<WeatherItem[] | null>(null);
const hourlyItems = ref<{ time: string; temperature: string; precipitation:string; summary?: string }[]>([]);
const weatherError = ref<string | null>(null);
const isLoading = ref(false);
const locationLabel = ref("현재 위치");
const router = useRouter();
const mapTemps = ref<Record<string, number | null>>({});

/* 현재 시간에 가장 가까운 예보 시각 계산 */
const closestForecast = computed(() => getClosestForecast(weatherItems.value ?? []));

/*현재 날씨 타입 계산(sunny/cloud/rain/snow/thunder)*/
const weatherType = computed(() => mapWeatherKey());
const tomorrowPreview = computed(() => buildTomorrowPreview(weatherItems.value ?? []));
const mapPreview = computed<MapPreview>(() => {
  const fallbackOrder = ['서울', '대전', '부산'];
  const temps = fallbackOrder.map((name) => {
    const value = mapTemps.value[name];
    return `${name} ${value ?? '--'}°C`;
  });
  return {
    title: '국내 기온지도',
    subtitle: '서울/대전/부산',
    highlight: temps.join('  ·  '),
    description: '',
  };
});

/* 날씨타입+온도로 펭귄 영상 선택*/
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

/* 요약 화면 표시값 */
const locationDisplay = computed(() => locationLabel.value || "--");
const dateLabel = computed(() => formatKoreanDate(new Date()));

const temperatureDisplay = computed(() => {
  const value = findValue(['TMP', 'T1H']);
  return value ? `${value}°C` : "--°C";
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
    return "현재 위치의 날씨를 불러왔어✨";
  }
  return "";
});

/* 옷 추천 로직 */
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
      { icon: Shirt, label: "가벼운 옷차림" },
      { icon: Coffee, label: "시원한 음료" },
    ];
  } else if (tempC >= 17) {
    description = "반팔, 얇은 셔츠, 면바지";
    items = [
      { icon: Shirt, label: "얇은 겉옷" },
      { icon: Glasses, label: "선글라스" },
      { icon: Coffee, label: "가벼운 음료" },
    ];
  } else if (tempC >= 9) {
    description = "가디건, 니트, 긴바지";
    items = [
      { icon: Shirt, label: "가디건" },
      { icon: Coffee, label: "따뜻한 음료" },
      { icon: ThermometerSnowflake, label: "보온용품" },
    ];
  } else if (tempC >= 5) {
    description = "코트, 니트, 기모";
    items = [
      { icon: Shirt, label: "코트" },
      { icon: Coffee, label: "따뜻한 음료" },
      { icon: ThermometerSnowflake, label: "보온용품" },
    ];
  } else {
    description = "패딩, 두꺼운 코트, 목도리";
    items = [
      { icon: Shirt, label: "패딩" },
      { icon: Coffee, label: "따뜻한 음료" },
      { icon: ThermometerSnowflake, label: "보온용품" },
    ];
  }

  if (type === "rain" || type === "thunder") {
    items = [...items.slice(0, 2), { icon: CloudRain, label: "우산" }];
  }

  return { description, items };
});

const outfitItems = computed(() => outfitRecommendation.value.items);
const outfitDescription = computed(() => outfitRecommendation.value.description);

/* 현재 위치 가져오기 */
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

/* 위치 기반 날씨 호출 */
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
    hourlyItems.value = buildHourlyItems(weatherItems.value ?? []);
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

async function loadMapPreviewTemps() {
  const base = resolveBaseDateTime();
  const cities = ['서울', '대전', '부산'];
  for (const city of cities) {
    try {
      const payload = await fetchOneSido(city, base.baseDate, base.baseTime);
      const temp = payload?.temperature ?? null;
      mapTemps.value = { ...mapTemps.value, [city]: temp };
    } catch {
      mapTemps.value = { ...mapTemps.value, [city]: null };
    }
  }
}

async function fetchOneSido(sido: string, baseDate: string, baseTime: string) {
  const { data } = await axios.get('/api/map/sido/one', {
    params: { sido, baseDate, baseTime },
  });
  return unwrapSidoPayload(data);
}

function unwrapSidoPayload(data: any) {
  const candidate = data && typeof data === 'object' && 'success' in data && 'data' in data ? data.data : data;
  if (!candidate || typeof candidate !== 'object') return null;
  return {
    temperature: candidate.temperature ?? null,
  };
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

/* 특정 카테고리 값 찾기 */
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

/* 현 시점 날씨 타입 */
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

/* 날씨 타입 한글라벨 변환*/
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

/*온도 구간 키(영상 파일명 사용)*/
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

/*날짜 형태 변환*/
function formatKoreanDate(date: Date) {
  const weekdays = ["일", "월", "화", "수", "목", "금", "토"];
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const weekday = weekdays[date.getDay()];
  return `${month}월 ${day}일 (${weekday})`;
}

// 날짜 형태 변환(프리뷰)
function formatMonthDay(date: Date) {
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${month}/${day}`;
}

// 날짜 형식 맞추기
function formatDateKey(date: Date) {
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, "0");
  const day = String(date.getDate()).padStart(2, "0");
  return `${year}${month}${day}`;
}

/*프리뷰*/
function buildTomorrowPreview(items: WeatherItem[]): TomorrowPreview | null {
  if (!items.length) {
    return null;
  }

  const tomorrow = new Date();
  tomorrow.setDate(tomorrow.getDate() + 1);

  const tomorrowKey = formatDateKey(tomorrow);
  const tomorrowItems = items.filter((item) => item && item.fcstDate === tomorrowKey && item.fcstTime);

  if (!tomorrowItems.length) {
    return null;
  }

  const temps = tomorrowItems
      .filter((item) => item.category === "TMP" || item.category === "T1H")
      .map((item) => parseNumber(item.fcstValue))
      .filter((value): value is number => value !== null);

  const maxTemp = temps.length ? Math.max(...temps) : null;
  const minTemp = temps.length ? Math.min(...temps) : null;

  const summaryTime = pickSummaryTime(tomorrowItems);
  const summaryKey = summaryTime ? mapWeatherKeyForTime(items, tomorrowKey, summaryTime) : mapWeatherKey();
  const summaryLabel = mapWeatherLabel(summaryKey);

  const maxDisplay = maxTemp === null ? "--" : String(maxTemp);
  const minDisplay = minTemp === null ? "--" : String(minTemp);

  return {
    title: "내일 날씨",
    subtitle: formatMonthDay(tomorrow),
    highlight: `${summaryLabel} ${maxDisplay}°C/${minDisplay}°C`,
    description: `최고 ${maxDisplay}°C · 최저 ${minDisplay}°C`,
  };
}

/* 프리뷰 기준 시간 정함(날씨 종류 결정)*/
function pickSummaryTime(items: WeatherItem[]) {
  const times = items
      .map((item) => item.fcstTime)
      .filter((time): time is string => !!time);

  if (!times.length) {
    return null;
  }
  if (times.includes("1200")) {
    return "1200";
  }
  return times.sort()[0];
}

/* 시간대별 온도 데이터 (최대 8개)*/
function buildHourlyItems(items: WeatherItem[]) {
  if (!items.length) {
    return [] as { time: string; temperature: string; precipitation: string; summary?: string }[];
  }

  const byKey = new Map<string, WeatherItem>();

  for (const item of items) {
    if (!item || !item.category) {
      continue;
    }
    if (item.category !== 'TMP' && item.category !== 'T1H') {
      continue;
    }
    if (!item.fcstDate || !item.fcstTime || !item.fcstValue) {
      continue;
    }
    const key = `${item.fcstDate}${item.fcstTime}`;
    const existing = byKey.get(key);
    if (!existing || item.category === 'TMP') {
      byKey.set(key, item);
    }
  }

  const now = new Date();
  const rows = Array.from(byKey.values())
      .map((item) => ({
        item,
        key: `${item.fcstDate}${item.fcstTime}`,
        dateTime: parseForecastDateTime(item.fcstDate, item.fcstTime),
      }))
      .sort((a, b) => {
        if (a.dateTime && b.dateTime) {
          return a.dateTime.getTime() - b.dateTime.getTime();
        }
        if (a.dateTime) {
          return -1;
        }
        if (b.dateTime) {
          return 1;
        }
        return a.key.localeCompare(b.key);
      });

  const future = rows.filter((entry) => entry.dateTime && entry.dateTime >= now);
  const past = rows.filter((entry) => entry.dateTime && entry.dateTime < now).reverse();
  const ordered = (future.length || past.length ? future.concat(past) : rows).slice(0, 8);

  return ordered.map(({ item }) => {
    const rawPrecipitation =
        findValueForTime(items, ['RN1'], item.fcstDate!, item.fcstTime!) ??
        findValueForTime(items, ['PCP'], item.fcstDate!, item.fcstTime!) ??
        '--';
    const precipitation = normalizePrecipitation(rawPrecipitation);

    return {
      time: formatHour(item.fcstTime),
      temperature: item.fcstValue ?? '--',
      precipitation,
      summary: mapWeatherLabel(mapWeatherKeyForTime(items, item.fcstDate!, item.fcstTime!)),
    };
  });
}

/* 특정 시간 날씨 타입 계산*/
function mapWeatherKeyForTime(items: WeatherItem[], fcstDate: string, fcstTime: string) {
  const lightning = findValueForTime(items, ['LGT'], fcstDate, fcstTime);
  const lightningValue = parseNumber(lightning);
  if (lightningValue !== null && lightningValue > 0) {
    return 'thunder';
  }

  const precipitationType = parseNumber(findValueForTime(items, ['PTY'], fcstDate, fcstTime));
  if (precipitationType !== null && precipitationType > 0) {
    if (precipitationType === 3) {
      return 'snow';
    }
    return 'rain';
  }

  const sky = findValueForTime(items, ['SKY'], fcstDate, fcstTime);
  if (sky === '1') {
    return 'sunny';
  }
  if (sky === '3' || sky === '4' || sky === '2') {
    return 'cloud';
  }
  return 'cloud';
}

/* 특정 시간 카테고리값 */
function findValueForTime(items: WeatherItem[], categories: string[], fcstDate: string, fcstTime: string) {
  for (const item of items) {
    if (!item || !item.category) {
      continue;
    }
    if (!categories.includes(item.category)) {
      continue;
    }
    if (item.fcstDate === fcstDate && item.fcstTime === fcstTime) {
      return item.fcstValue ?? null;
    }
  }
  return null;
}

/*강수량 강수없음,-- -> 0 */
function normalizePrecipitation(value: string) {
  if (!value || value.trim() === '--') {
    return '0';
  }
  const cleaned = value.trim();
  if (cleaned === '강수없음') {
    return '0';
  }
  return cleaned;
}

/*HHmm -> HH:mm*/
function formatHour(fcstTime?: string) {
  if (!fcstTime || fcstTime.length !== 4) {
    return '--:--';
  }
  const hour = fcstTime.slice(0, 2);
  const minute = fcstTime.slice(2);
  return `${hour}:${minute}`;
}

/*문자열 숫자 파싱*/
function parseNumber(value?: string | null) {
  if (!value) {
    return null;
  }
  const parsed = Number(value);
  return Number.isNaN(parsed) ? null : parsed;
}

/* items에서 현재와 가장 가까운 시간 구하기 */
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

/** YYYYMMDD + HHmm → Date 변환 */
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
  void loadMapPreviewTemps();
});

function goToCalendar() {
  router.push({ name: 'calendar' });
}

// 양승재 추가
  const emit = defineEmits<{
    (e: 'weather-loaded', payload: { pty: number; tmx: number }): void
  }>()

  const todayPTY = ref<number>(0)
  const todayTmx = ref<number>(0)

  onMounted(async () => {
    // 실제로는 여기서 날씨 API 호출
    todayPTY.value = 0
    todayTmx.value = 6.0

    emit('weather-loaded', {
      pty: todayPTY.value,
      tmx: todayTmx.value,
    })
  })
</script>
