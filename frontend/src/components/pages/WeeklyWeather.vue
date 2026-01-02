<template>
  <main class="max-w-[1440px] mx-auto px-20 py-8">
    <div class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50 mb-8">
      <div class="flex items-center justify-between mb-6">
        <div class="flex items-center gap-4">
          <button class="flex items-center gap-2 text-[#1F2A37] hover:bg-[#F6FAFF] px-4 py-2 rounded-xl transition-all">
            <span class="text-lg font-semibold">서울</span>
            <ChevronDown class="w-5 h-5" />
          </button>
          <span class="text-[#6B7280]">{{ weekDateRange }}</span>
        </div>

        <div class="flex gap-2 bg-[#F6FAFF] p-1 rounded-2xl">
          <button
              type="button"
              class="px-6 py-2 rounded-xl transition-all"
              :class="viewMode === 'list' ? 'bg-white text-[#1F2A37] font-semibold shadow-sm' : 'text-[#6B7280]'"
              @click="viewMode = 'list'"
          >
            리스트 보기
          </button>
          <button
              type="button"
              class="px-6 py-2 rounded-xl transition-all"
              :class="viewMode === 'graph' ? 'bg-white text-[#1F2A37] font-semibold shadow-sm' : 'text-[#6B7280]'"
              @click="viewMode = 'graph'"
          >
            그래프 보기
          </button>
        </div>
      </div>

      <div class="flex gap-3">
        <div class="bg-[#F0F8FF] px-4 py-2 rounded-2xl border border-[#E6EEF9]"><span class="text-sm text-[#1F2A37]">비 오는 날 2일</span></div>
        <div class="bg-[#FFF5F8] px-4 py-2 rounded-2xl border border-[#FFE8F0]"><span class="text-sm text-[#1F2A37]">큰 일교차 3일</span></div>
        <div class="bg-[#F6FAFF] px-4 py-2 rounded-2xl border border-[#E6EEF9]"><span class="text-sm text-[#1F2A37]">최저 -2°C</span></div>
      </div>
    </div>

    <div class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50 mb-8">
      <h3 class="text-[#1F2A37] mb-6">주간 날씨 상세</h3>

      <!-- Loading State -->
      <div v-if="isLoading" class="p-8 text-center bg-[#F6FAFF] rounded-3xl mb-4">
        <p class="text-[#1F2A37] font-semibold">날씨 정보를 불러오는 중입니다...</p>
        <p class="text-sm text-[#6B7280] mt-2">위치 정보 권한을 허용해주세요.</p>
      </div>

      <!-- Error State -->
      <div v-if="weatherError" class="p-6 bg-red-50 text-red-600 rounded-2xl mb-4 flex items-center gap-2">
        <span class="font-bold">오류:</span>
        <span>{{ weatherError }}</span>
        <button @click="loadWeatherByLocation" class="ml-auto underline text-sm">재시도</button>
      </div>

      <div class="space-y-4" v-if="!isLoading && !weatherError">
        <div
            v-for="(day, index) in weeklyData"
            :key="index"
            class="flex items-center gap-6 p-5 bg-[#F6FAFF] rounded-3xl hover:bg-[#EAF2FF] transition-all group"
        >
          <div class="w-20 flex-shrink-0">
            <div class="text-lg font-semibold text-[#1F2A37]">{{ day.day }}요일</div>
            <div class="text-sm text-[#6B7280]">{{ day.date }}</div>
          </div>

          <div class="w-16 h-16 bg-white rounded-2xl flex items-center justify-center shadow-sm flex-shrink-0">
            <component :is="iconMap[day.icon].Component" :class="`w-8 h-8 ${iconMap[day.icon].color}`" />
          </div>

          <div class="flex items-baseline gap-2 w-32 flex-shrink-0">
            <span class="text-2xl font-bold text-[#1F2A37]">{{ day.high }}°</span>
            <span class="text-lg text-[#6B7280]">/ {{ day.low }}°</span>
          </div>

          <div class="w-24 flex-shrink-0">
            <div class="bg-[#EAF2FF] px-3 py-2 rounded-full inline-block">
              <span class="text-sm text-[#6B7280]">강수 {{ day.precipitation }}</span>
            </div>
          </div>

          <div class="flex-1 min-w-0">
            <span class="text-[#1F2A37]">{{ day.outfit }}</span>
          </div>

          <div class="w-12 h-12 bg-gradient-to-br from-[#FFF5F8] to-[#FFE8F0] rounded-2xl flex items-center justify-center flex-shrink-0 group-hover:scale-110 transition-transform">
            <span class="text-2xl">🐧</span>
          </div>
        </div>
      </div>
    </div>

    <div class="mb-8">
      <h3 class="text-[#1F2A37] mb-4">일주일 옷차림 요약</h3>
      <div class="grid grid-cols-3 gap-6">
        <div class="bg-gradient-to-br from-[#F0F8FF] to-[#E6F3FF] rounded-3xl p-6 shadow-lg shadow-blue-100/30">
          <div class="text-4xl mb-3">🧥</div>
          <h4 class="text-[#1F2A37] mb-2">가디건 / 자켓</h4>
          <p class="text-[#6B7280]">3일</p>
        </div>

        <div class="bg-gradient-to-br from-[#FFF5F8] to-[#FFE8F0] rounded-3xl p-6 shadow-lg shadow-pink-100/30">
          <div class="text-4xl mb-3">🧥</div>
          <h4 class="text-[#1F2A37] mb-2">코트 / 패딩</h4>
          <p class="text-[#6B7280]">2일</p>
        </div>

        <div class="bg-gradient-to-br from-[#F6FAFF] to-[#EAF2FF] rounded-3xl p-6 shadow-lg shadow-blue-100/30">
          <div class="text-4xl mb-3">☔</div>
          <h4 class="text-[#1F2A37] mb-2">우산 필요한 날</h4>
          <p class="text-[#6B7280]">2일</p>
        </div>
      </div>
    </div>

    <div class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50">
      <h3 class="text-[#1F2A37] mb-6">온도 변화 그래프 (미리보기)</h3>
      <div class="h-48 bg-gradient-to-br from-[#F6FAFF] to-[#EAF2FF] rounded-3xl flex items-center justify-center">
        <p class="text-[#6B7280]">그래프 보기 모드에서 확인하세요</p>
      </div>
    </div>
  </main>
</template>

<script setup lang="ts">
import { onMounted, ref, computed } from 'vue';
import { ChevronDown, Cloud, CloudRain, Sun, Zap, Umbrella } from 'lucide-vue-next';

const viewMode = ref<'list' | 'graph'>('list');
const isLoading = ref(false);
const weatherError = ref<string | null>(null);

const weekDateRange = computed(() => {
  const start = new Date();
  const end = new Date();
  end.setDate(start.getDate() + 6);

  const format = (d: Date) => {
    const m = String(d.getMonth() + 1).padStart(2, '0');
    const day = String(d.getDate()).padStart(2, '0');
    return `${m}/${day}`;
  };

  return `${format(start)}–${format(end)}`;
});

// Default mock data structure (used as fallback or initial state)
const weeklyData = ref([
  { day: '월', date: '12/29', icon: 'thunder', high: 18, low: 14, precipitation: '40%', outfit: '가디건 + 긴바지' },
  { day: '화', date: '12/30', icon: 'rain', high: 21, low: 16, precipitation: '60%', outfit: '얇은 자켓 + 우산' },
  { day: '수', date: '12/31', icon: 'cloud', high: 19, low: 15, precipitation: '20%', outfit: '니트 + 긴바지' },
  { day: '목', date: '01/01', icon: 'sun', high: 22, low: 17, precipitation: '5%', outfit: '가디건 + 긴바지' },
  { day: '금', date: '01/02', icon: 'cloud', high: 20, low: 15, precipitation: '10%', outfit: '트렌치 + 니트' },
  { day: '토', date: '01/03', icon: 'cloud', high: 16, low: 8, precipitation: '15%', outfit: '코트 + 목도리' },
  { day: '일', date: '01/04', icon: 'sun', high: 14, low: 2, precipitation: '0%', outfit: '패딩 + 장갑' },
]);

// Icon mapping
const iconMap: Record<string, { Component: any; color: string }> = {
  thunder: { Component: Zap, color: 'text-yellow-500 fill-yellow-400' },
  rain: { Component: CloudRain, color: 'text-blue-400' },
  cloud: { Component: Cloud, color: 'text-gray-400' },
  sun: { Component: Sun, color: 'text-orange-400' },
  snow: { Component: Cloud, color: 'text-blue-200' }, // Added Snow mapping (using Cloud with light blue as simple fallback or Snowflake if available but not imported yet)
  overcast: { Component: Cloud, color: 'text-gray-600' },
};

// --- API & Processing Logic ---

// Get User Location
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

interface WeatherItem {
  baseDate: string;
  baseTime: string;
  category: string; // TMP, SKY, PTY, POP, etc.
  fcstDate: string;
  fcstTime: string;
  fcstValue: string;
  nx: number;
  ny: number;
}

async function loadWeatherByLocation() {
  isLoading.value = true;
  weatherError.value = null;

  try {
    const position = await getCurrentPosition();
    const lat = position.coords.latitude;
    const lon = position.coords.longitude;

    const res = await fetch(`/api/weather/by-location?lat=${lat}&lon=${lon}`);
    if (!res.ok) throw new Error(`날씨 API 요청 실패 (${res.status})`);

    const data = await res.json();
    const items: WeatherItem[] = Array.isArray(data) ? data : data?.items ?? [];

    if (items.length > 0) {
      processWeeklyData(items);
    }

  } catch (error) {
    console.error(error);
    weatherError.value = error instanceof Error ? error.message : '알 수 없는 오류';
  } finally {
    isLoading.value = false;
  }
}

function getDayLabel(dateStr: string): string {
  const days = ['일', '월', '화', '수', '목', '금', '토'];
  const year = parseInt(dateStr.substring(0, 4));
  const month = parseInt(dateStr.substring(4, 6)) - 1;
  const day = parseInt(dateStr.substring(6, 8));
  const d = new Date(year, month, day);
  return days[d.getDay()];
}

// 기온별 옷차림
// 1. 기온별 옷차림 추천 로직 (요청하신 5단계 완벽 반영)
function recommendOutfit(temp: number): string {
  if (temp >= 23) {
    return '반팔, 얇은 셔츠, 반바지, 면바지, 민소매, 짧은 치마, 린넨 옷';
  }
  if (temp >= 17) {
    return '얇은 가디건, 니트, 맨투맨, 후드, 긴 바지, 블라우스, 긴팔 티, 면바지, 슬랙스';
  }
  if (temp >= 9) {
    return '트렌치 코트, 야상, 점퍼, 스타킹, 기모바지, 자켓, 가디건, 청자켓, 니트, 청바지';
  }
  if (temp >= 5) {
    return '울 코트, 히트텍, 가죽 옷, 기모 옷';
  }
  // 4도 이하
  return '패딩, 두꺼운 코트, 누빔 옷, 기온, 목도리, 장갑';
}

// 5 Weather Types: Clear, Cloudy, Rain, Snow, Thunderstorm
function determineIcon(ptyCodes: number[], skyCodes: number[]): string {
  // PTY: 0(None), 1(Rain), 2(Rain/Snow), 3(Snow), 4(Shower)
  // SKY: 1(Clear), 3(Cloudy), 4(Overcast)

  // Thunderstorm isn't explicitly in standard PTY.
  // We'll map 'Shower' (PTY 4) to Rain for now, or if user specifically wants Thunder,
  // we might need to rely on PTY 4 as a proxy or just use Rain/Cloud/Sun/Snow priority.
  // User requested: Clear, Cloudy(Cloud), Rain, Snow, Thunder

  // Check for Snow first (PTY 3 or 2)
  if (ptyCodes.some(c => c === 3)) return 'snow';
  if (ptyCodes.some(c => c === 2)) return 'snow'; // Rain/Snow mix -> Snow for safety/visual

  // Check for Rain (PTY 1 or 4)
  if (ptyCodes.some(c => c === 1 || c === 4)) return 'rain';

  // Check for Cloudy/Overcast
  if (skyCodes.some(c => c === 4)) return 'cloud'; // Overcast
  if (skyCodes.some(c => c === 3)) return 'cloud'; // Cloudy ('Cloudy' usually means lots of clouds)

  // Default to Sun (Clear)
  return 'sun';
}

// 2. 날씨 데이터 가공 로직 수정
function processWeeklyData(items: WeatherItem[]) {
  const grouped = new Map<string, WeatherItem[]>();
  items.forEach(item => {
    if (!grouped.has(item.fcstDate)) grouped.set(item.fcstDate, []);
    grouped.get(item.fcstDate)?.push(item);
  });

  const dates = Array.from(grouped.keys()).sort();
  const newWeeklyData = []; // 새 데이터를 담을 배열

  dates.forEach((dateStr, index) => {
    if (index >= 7) return;

    const dayItems = grouped.get(dateStr) || [];
    const tmps = dayItems.filter(i => i.category === 'TMP').map(i => parseFloat(i.fcstValue));
    const pops = dayItems.filter(i => i.category === 'POP').map(i => parseInt(i.fcstValue));
    const skyCodes = dayItems.filter(i => i.category === 'SKY').map(i => parseInt(i.fcstValue));
    const ptyCodes = dayItems.filter(i => i.category === 'PTY').map(i => parseInt(i.fcstValue));

    if (tmps.length === 0) return;

    const high = Math.max(...tmps);
    const low = Math.min(...tmps);
    const maxPop = pops.length ? Math.max(...pops) : 0;

    // 평균 기온 혹은 최저/최고 기온 중 적절한 값으로 옷차림 추천 (여기서는 최고기온 기준)
    const outfit = recommendOutfit(high);

    newWeeklyData.push({
      day: getDayLabel(dateStr),
      date: `${dateStr.substring(4, 6)}/${dateStr.substring(6, 8)}`,
      icon: determineIcon(ptyCodes, skyCodes),
      high: Math.round(high),
      low: Math.round(low),
      precipitation: `${maxPop}%`,
      outfit: outfit // 수정된 옷차림 적용
    });
  });

  weeklyData.value = newWeeklyData;
}

onMounted(() => {
  loadWeatherByLocation();
});
</script>
