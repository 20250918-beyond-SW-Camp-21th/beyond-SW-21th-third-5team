<template>
  <div class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50 h-full flex flex-col min-h-[480px]">
    <div class="flex items-center justify-between mb-6">
      <button class="flex items-center gap-2 text-[#1F2A37] hover:bg-[#F6FAFF] px-4 py-2 rounded-xl transition-all">
        <span class="text-lg font-semibold">{{ locationDisplay }}</span>
        <ChevronDown class="w-5 h-5" />
      </button>
      <span class="text-[#6B7280]">{{ dateLabel }}</span>
    </div>

    <p v-if="statusMessage" class="text-sm text-[#6B7280] mb-4">{{ statusMessage }}</p>

    <div class="mb-8">
      <div class="flex items-baseline gap-3 mb-2">
        <span class="text-6xl font-bold text-[#1F2A37]">{{ temperatureDisplay }}</span>
      </div>
      <span class="text-[#6B7280]">{{ weatherSummary }}</span>
    </div>

    <div class="flex gap-3 mb-8">
      <div
        v-for="stat in weatherStats"
        :key="stat.label"
        class="flex items-center gap-2 bg-[#F6FAFF] px-4 py-2 rounded-2xl border border-[#E6EEF9]"
      >
        <component :is="stat.icon" class="w-4 h-4 text-[#6B7280]" />
        <span class="text-sm text-[#6B7280]">{{ stat.label }}</span>
        <span class="text-sm font-semibold text-[#1F2A37]">{{ stat.value }}</span>
      </div>
    </div>

    <div class="mt-auto">
      <h3 class="text-[#1F2A37] mb-4">오늘의 옷차림</h3>

      <div class="flex gap-3 mb-4">
        <div
          v-for="(item, index) in outfitItems"
          :key="index"
          class="w-14 h-14 bg-gradient-to-br from-[#FFF5F8] to-[#FFE8F0] rounded-2xl flex items-center justify-center shadow-sm"
        >
          <component :is="item.icon" class="w-6 h-6 text-[#FF9BCB]" />
        </div>
      </div>

      <p class="text-[#6B7280] mb-6">{{ outfitDescription }}</p>

      <button class="w-full bg-[#FF9BCB] text-white py-4 rounded-2xl hover:bg-[#FF7FB3] transition-all shadow-lg shadow-pink-200/50 font-semibold">
        오늘 기록하기
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { ChevronDown, CloudRain, Droplets, Glasses, ShoppingBag, Wind, Coffee } from 'lucide-vue-next';

type WeatherItem = {
  category?: string;
  fcstValue?: string;
  fcstDate?: string;
  fcstTime?: string;
};

const props = defineProps<{
  weatherItems?: WeatherItem[] | null;
  isLoading?: boolean;
  error?: string | null;
  locationLabel?: string;
}>();

const locationDisplay = computed(() => props.locationLabel || '현재 위치');

const dateLabel = computed(() => formatKoreanDate(new Date()));

const closestForecast = computed(() => getClosestForecast(props.weatherItems ?? []));

function findValue(categories: string[]) {
  if (!props.weatherItems?.length) {
    return null;
  }
  const target = closestForecast.value;
  if (target) {
    const match = props.weatherItems.find(
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
  const fallback = props.weatherItems.find((item) => item && categories.includes(item.category ?? ''));
  return fallback?.fcstValue ?? null;
}

const temperatureDisplay = computed(() => {
  const value = findValue(['TMP', 'T1H']);
  return value ? `${value}°C` : '--°C';
});

const weatherType = computed(() => {
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
  if (sky === '3' || sky === '4' || sky === '2') {
    return 'cloud';
  }
  return 'cloud';
});

const weatherSummary = computed(() => {
  const label = mapWeatherLabel(weatherType.value);
  const skyValue = findValue(['SKY']);
  return skyValue ? `날씨: ${label}` : `${label}`;
});

const weatherStats = computed(() => {
  const windValue = findValue(['WSD']);
  const humidityValue = findValue(['REH']);
  const precipValue = findValue(['POP']);

  return [
    { icon: Wind, label: '바람', value: windValue ? `${windValue}m/s` : '-' },
    { icon: Droplets, label: '습도', value: humidityValue ? `${humidityValue}%` : '-' },
    { icon: CloudRain, label: '강수확률', value: precipValue ? `${precipValue}%` : '-' },
  ];
});

const statusMessage = computed(() => {
  if (props.error) {
    return `날씨 가져오기 실패: ${props.error}`;
  }
  if (props.isLoading) {
    return '현재 위치의 날씨를 불러오는 중입니다.';
  }
  if (props.weatherItems?.length) {
    return `현재 위치 날씨 수신 (${props.weatherItems.length}개)`;
  }
  return '';
});

const outfitRecommendation = computed(() => {
  const tempValue = findValue(['TMP', 'T1H']);
  const tempC = parseNumber(tempValue);
  const type = weatherType.value;

  if (tempC === null) {
    return {
      description: '기온 정보를 불러오는 중입니다.',
      items: [
        { icon: ShoppingBag, label: '아우터' },
        { icon: Coffee, label: '따뜻한 음료' },
        { icon: Glasses, label: '선글라스' },
      ],
    };
  }

  let description = '';
  let items = [] as { icon: typeof ShoppingBag; label: string }[];

  if (tempC >= 23) {
    description = '민소매, 반팔, 반바지, 린넨';
    items = [
      { icon: Glasses, label: '자외선 차단' },
      { icon: ShoppingBag, label: '가벼운 옷차림' },
      { icon: Coffee, label: '시원한 음료' },
    ];
  } else if (tempC >= 17) {
    description = '반팔, 얇은 셔츠, 면바지';
    items = [
      { icon: ShoppingBag, label: '얇은 겉옷' },
      { icon: Glasses, label: '선글라스' },
      { icon: Coffee, label: '가벼운 음료' },
    ];
  } else if (tempC >= 9) {
    description = '가디건, 니트, 긴바지';
    items = [
      { icon: ShoppingBag, label: '가디건' },
      { icon: Coffee, label: '따뜻한 음료' },
      { icon: Glasses, label: '선글라스' },
    ];
  } else if (tempC >= 5) {
    description = '코트, 니트, 기모';
    items = [
      { icon: ShoppingBag, label: '코트' },
      { icon: Coffee, label: '따뜻한 음료' },
      { icon: Glasses, label: '보온용품' },
    ];
  } else {
    description = '패딩, 두꺼운 코트, 목도리';
    items = [
      { icon: ShoppingBag, label: '패딩' },
      { icon: Coffee, label: '따뜻한 음료' },
      { icon: Glasses, label: '보온용품' },
    ];
  }

  if (type === 'rain' || type === 'thunder') {
    items = [...items.slice(0, 2), { icon: CloudRain, label: '우산' }];
  }

  return { description, items };
});

const outfitItems = computed(() => outfitRecommendation.value.items);
const outfitDescription = computed(() => outfitRecommendation.value.description);

function formatKoreanDate(date: Date) {
  const weekdays = ['일', '월', '화', '수', '목', '금', '토'];
  const month = date.getMonth() + 1;
  const day = date.getDate();
  const weekday = weekdays[date.getDay()];
  return `${month}월 ${day}일(${weekday})`;
}

function parseNumber(value?: string | null) {
  if (!value) {
    return null;
  }
  const parsed = Number(value);
  return Number.isNaN(parsed) ? null : parsed;
}

function mapSkyStatus(value: string) {
  switch (value) {
    case '1':
      return '맑음';
    case '2':
      return '구름조금';
    case '3':
      return '구름많음';
    case '4':
      return '흐림';
    default:
      return '정보 없음';
  }
}

function mapWeatherLabel(type: string) {
  switch (type) {
    case 'sunny':
      return '맑음';
    case 'cloud':
      return '흐림';
    case 'rain':
      return '비';
    case 'snow':
      return '눈';
    case 'thunder':
      return '천둥번개';
    default:
      return '흐림';
  }
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
</script>
