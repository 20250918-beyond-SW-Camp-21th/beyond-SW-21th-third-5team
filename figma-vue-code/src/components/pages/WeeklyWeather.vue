<template>
  <main class="max-w-[1440px] mx-auto px-20 py-8">
    <div class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50 mb-8">
      <div class="flex items-center justify-between mb-6">
        <div class="flex items-center gap-4">
          <button class="flex items-center gap-2 text-[#1F2A37] hover:bg-[#F6FAFF] px-4 py-2 rounded-xl transition-all">
            <span class="text-lg font-semibold">서울</span>
            <ChevronDown class="w-5 h-5" />
          </button>
          <span class="text-[#6B7280]">12/29–01/04</span>
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

      <div class="space-y-4">
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
import { onMounted, ref } from 'vue';
import { ChevronDown, Cloud, CloudRain, Sun, Zap, Umbrella } from 'lucide-vue-next';

const viewMode = ref<'list' | 'graph'>('list');
const isLoading = ref(false);
const weatherError = ref<string | null>(null);

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
  overcast: { Component: Cloud, color: 'text-gray-600' }, // New mapping for overcast
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

interface DailySummary {
  date: string; // YYYYMMDD
  dayLabel: string; // 월, 화, ...
  displayDate: string; // MM/DD
  high: number;
  low: number;
  maxPop: number; // Probability of Precipitation
  skyCodes: number[]; // Collection of sky codes to determine dominant icon
  ptyCodes: number[]; // Collection of PTY codes
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

function recommendOutfit(temp: number, pop: number): string {
  if (pop >= 50) return '우산 + 레인코트';
  if (temp >= 23) return '반팔 + 얇은 바지';
  if (temp >= 20) return '긴팔 + 얇은 가디건';
  if (temp >= 17) return '니트 + 맨투맨';
  if (temp >= 12) return '자켓 + 가디건';
  if (temp >= 9) return '트렌치코트 + 니트';
  if (temp >= 5) return '코트 + 히트텍';
  return '패딩 + 목도리';
}

function determineIcon(ptyCodes: number[], skyCodes: number[]): string {
  // PTY: 0(None), 1(Rain), 2(Rain/Snow), 3(Snow), 4(Shower)
  // SKY: 1(Sunny), 3(Cloudy), 4(Overcast)

  // Prioritize PTY (Precipitation)
  if (ptyCodes.some(c => c === 1 || c === 2 || c === 4)) return 'rain';
  if (ptyCodes.some(c => c === 3)) return 'cloud'; // Snow often depicted with cloud/snow icon, fallback to cloud for now

  // Then SKY
  // Most frequent or max? Let's take 'worst' case or average.
  // If any overcast, show cloud/overcast.
  if (skyCodes.some(c => c === 4)) return 'overcast';
  if (skyCodes.some(c => c === 3)) return 'cloud';
  return 'sun';
}

function processWeeklyData(items: WeatherItem[]) {
  // Group by Date
  const grouped = new Map<string, WeatherItem[]>();
  items.forEach(item => {
    if (!grouped.has(item.fcstDate)) grouped.set(item.fcstDate, []);
    grouped.get(item.fcstDate)?.push(item);
  });

  // Convert to DailySummary
  // Only take the available dates from API (usually 3 days) and update the first few slots of weeklyData
  // or create a new array.
  // The UI expects 7 days. We only have 3. We will iterate and fill what we have.

  const dates = Array.from(grouped.keys()).sort();
  const newWeeklyData = [...weeklyData.value]; // Copy existing mock/structure to preserve 7 slots if needed

  dates.forEach((dateStr, index) => {
    if (index >= 7) return; // limit to 7 days logic just in case

    const dayItems = grouped.get(dateStr) || [];

    // Filter for relevant values
    const tmps = dayItems.filter(i => i.category === 'TMP').map(i => parseFloat(i.fcstValue));
    const pops = dayItems.filter(i => i.category === 'POP').map(i => parseInt(i.fcstValue));
    const skyCodes = dayItems.filter(i => i.category === 'SKY').map(i => parseInt(i.fcstValue));
    const ptyCodes = dayItems.filter(i => i.category === 'PTY').map(i => parseInt(i.fcstValue));

    if (tmps.length === 0) return; // Skip if no temperature data (e.g. partial data)

    const high = Math.max(...tmps);
    const low = Math.min(...tmps);
    const maxPop = pops.length ? Math.max(...pops) : 0;

    const dayLabel = getDayLabel(dateStr);
    const displayDate = `${dateStr.substring(4, 6)}/${dateStr.substring(6, 8)}`;

    const icon = determineIcon(ptyCodes, skyCodes);
    const outfit = recommendOutfit((high + low) / 2, maxPop); // Avg temp for outfit

    // Update the array at 'index'
    newWeeklyData[index] = {
      day: dayLabel,
      date: displayDate,
      icon: icon,
      high: Math.round(high),
      low: Math.round(low),
      precipitation: `${maxPop}%`,
      outfit: outfit
    };
  });

  weeklyData.value = newWeeklyData;
}

onMounted(() => {
  loadWeatherByLocation();
});
</script>
