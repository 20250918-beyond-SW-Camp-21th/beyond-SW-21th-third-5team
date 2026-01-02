<template>
  <main class="max-w-[1440px] mx-auto px-20 py-8">
    <div class="grid grid-cols-[1fr_400px] gap-6">
      <div class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50 relative min-h-[700px]">
        <h3 class="text-[#1F2A37] mb-6">대한민국 기온 지도</h3>
        <div
          ref="mapRef"
          class="relative w-full max-w-[720px] bg-gradient-to-br from-[#F0F8FF] to-[#E6F3FF] rounded-3xl select-none mx-auto"
          style="aspect-ratio: 2 / 3;"
        >
          <img
            :src="koreaMap"
            alt="대한민국 지도"
            class="w-full h-full object-contain opacity-20"
          />

          <div
            v-for="city in cities"
            :key="city.name"
            class="absolute"
            :style="{ top: city.position.top, left: city.position.left }"
          >
            <button
              type="button"
              class="group relative"
              :class="selectedCity === city.name ? 'z-20' : 'z-10'"
              @click="selectedCity = city.name"
              @mousedown="startDrag(city, $event)"
              @touchstart.prevent="startDrag(city, $event)"
            >
              <span
                class="flex h-12 w-12 items-center justify-center rounded-full shadow-lg hover:shadow-xl transition-all transform hover:scale-110"
                :class="[tempColor(city.temp), selectedCity === city.name ? 'ring-4 ring-[#6AA9FF] ring-opacity-50' : '']"
              >
                <span class="text-sm font-semibold text-[#1F2A37]">{{ city.temp }}°C</span>
              </span>
              <span class="absolute -bottom-6 left-1/2 -translate-x-1/2 whitespace-nowrap block">
                <span class="text-xs font-medium text-[#6B7280]">{{ city.name }}</span>
              </span>
            </button>
          </div>
        </div>
      </div>

      <div class="space-y-6">
        <div class="bg-white rounded-[28px] p-6 shadow-lg shadow-blue-100/50">
          <h4 class="text-[#1F2A37] mb-4">필터</h4>
          <div class="mb-6">
            <label class="block text-sm text-[#6B7280] mb-2">표시 온도</label>
            <div class="grid grid-cols-2 gap-2">
              <button
                v-for="mode in temperatureModes"
                :key="mode.value"
                type="button"
                class="py-2 px-3 rounded-xl transition-all"
                :class="tempMode === mode.value ? 'bg-[#6AA9FF] text-white' : 'bg-[#F6FAFF] text-[#6B7280] hover:bg-[#EAF2FF]'"
                @click="tempMode = mode.value"
              >
                {{ mode.label }}
              </button>
            </div>
          </div>

          <div class="flex items-center justify-between">
            <label class="text-sm text-[#6B7280]">옷차림 아이콘 표시</label>
            <button
              type="button"
              class="w-12 h-6 rounded-full transition-all"
              :class="showOutfitIcons ? 'bg-[#6AA9FF]' : 'bg-[#E6EEF9]'"
              @click="showOutfitIcons = !showOutfitIcons"
            >
              <span
                class="block w-5 h-5 bg-white rounded-full shadow-sm transition-transform"
                :class="showOutfitIcons ? 'translate-x-6' : 'translate-x-1'"
              />
            </button>
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

        <div class="bg-gradient-to-br from-[#F0F8FF] to-[#E6F3FF] rounded-[28px] p-6 shadow-lg shadow-blue-100/50">
          <div class="flex items-start gap-3 mb-4">
            <MapPin class="w-6 h-6 text-[#6AA9FF]" />
            <div class="flex-1">
              <h4 class="text-[#1F2A37] mb-1">{{ currentCity.name }}</h4>
              <div class="text-3xl font-bold text-[#1F2A37] mb-2">{{ currentCity.temp }}°C</div>
            </div>
            <div class="text-4xl">🐧</div>
          </div>

          <div class="space-y-2 mb-4">
            <div class="flex items-center gap-2 text-sm">
              <Wind class="w-4 h-4 text-[#6B7280]" />
              <span class="text-[#6B7280]">바람</span>
              <span class="text-[#1F2A37] font-medium">3m/s</span>
            </div>
            <div class="flex items-center gap-2 text-sm">
              <Droplets class="w-4 h-4 text-[#6B7280]" />
              <span class="text-[#6B7280]">습도</span>
              <span class="text-[#1F2A37] font-medium">45%</span>
            </div>
          </div>

          <div class="bg-white/80 rounded-2xl p-4">
            <div class="text-sm text-[#6B7280] mb-1">추천 옷차림</div>
            <div class="text-[#1F2A37] font-medium">{{ currentCity.outfit }}</div>
          </div>
        </div>

        <div class="bg-white rounded-[28px] p-6 shadow-lg shadow-blue-100/50">
          <h4 class="text-[#1F2A37] mb-4">인기 지역 빠른 선택</h4>
          <div class="flex flex-wrap gap-2">
            <button
              v-for="city in popularCities"
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
import { Droplets, MapPin, Wind } from 'lucide-vue-next';
import { computed, onBeforeUnmount, ref } from 'vue';
import koreaMap from '../../assets/images/korea-map.png';

type TempMode = 'current' | 'feels' | 'high' | 'low';

interface City {
  name: string;
  temp: number;
  position: { top: string; left: string };
  outfit: string;
}

const selectedCity = ref('서울');
const showOutfitIcons = ref(true);
const tempMode = ref<TempMode>('current');
const mapRef = ref<HTMLElement | null>(null);
const draggingCity = ref<City | null>(null);
const dragOffset = ref({ x: 0, y: 0 });
const previousUserSelect = ref('');

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

const temperatureModes = [
  { value: 'current' as TempMode, label: '현재' },
  { value: 'feels' as TempMode, label: '체감' },
  { value: 'high' as TempMode, label: '최고' },
  { value: 'low' as TempMode, label: '최저' },
];

const popularCities = cities.value.map(city => city.name);

const legends = [
  { range: '0~5°C', color: 'bg-blue-400' },
  { range: '6~10°C', color: 'bg-blue-300' },
  { range: '11~16°C', color: 'bg-green-300' },
  { range: '17~22°C', color: 'bg-yellow-300' },
  { range: '23~27°C', color: 'bg-orange-300' },
  { range: '28°C+', color: 'bg-red-300' },
];

const currentCity = computed(() => cities.value.find(city => city.name === selectedCity.value) ?? cities.value[0]);

if (import.meta.env.DEV) {
  (window as Window & { __temperatureMapCities?: City[] }).__temperatureMapCities = cities.value;
}

function getClientPoint(event: MouseEvent | TouchEvent) {
  if ('touches' in event && event.touches.length > 0) {
    return { x: event.touches[0].clientX, y: event.touches[0].clientY };
  }
  const mouseEvent = event as MouseEvent;
  return { x: mouseEvent.clientX, y: mouseEvent.clientY };
}

function startDrag(city: City, event: MouseEvent | TouchEvent) {
  if (!mapRef.value) return;
  selectedCity.value = city.name;
  draggingCity.value = city;

  const mapRect = mapRef.value.getBoundingClientRect();
  const target = event.currentTarget as HTMLElement | null;
  const targetRect = target?.getBoundingClientRect();
  const point = getClientPoint(event);
  dragOffset.value = {
    x: targetRect ? point.x - (targetRect.left - mapRect.left) : 0,
    y: targetRect ? point.y - (targetRect.top - mapRect.top) : 0,
  };

  previousUserSelect.value = document.body.style.userSelect;
  document.body.style.userSelect = 'none';

  window.addEventListener('mousemove', handleDragMove);
  window.addEventListener('mouseup', stopDrag);
  window.addEventListener('touchmove', handleDragMove, { passive: false });
  window.addEventListener('touchend', stopDrag);
}

function handleDragMove(event: MouseEvent | TouchEvent) {
  if (!draggingCity.value || !mapRef.value) return;
  if ('touches' in event) event.preventDefault();

  const mapRect = mapRef.value.getBoundingClientRect();
  const point = getClientPoint(event);
  const x = point.x - mapRect.left - dragOffset.value.x;
  const y = point.y - mapRect.top - dragOffset.value.y;
  const left = Math.min(100, Math.max(0, (x / mapRect.width) * 100));
  const top = Math.min(100, Math.max(0, (y / mapRect.height) * 100));

  draggingCity.value.position.left = `${left}%`;
  draggingCity.value.position.top = `${top}%`;
}

function stopDrag() {
  draggingCity.value = null;
  document.body.style.userSelect = previousUserSelect.value;
  window.removeEventListener('mousemove', handleDragMove);
  window.removeEventListener('mouseup', stopDrag);
  window.removeEventListener('touchmove', handleDragMove);
  window.removeEventListener('touchend', stopDrag);
}

onBeforeUnmount(() => {
  stopDrag();
});


function tempColor(temp: number) {
  if (temp <= 5) return 'bg-blue-400';
  if (temp <= 10) return 'bg-blue-300';
  if (temp <= 16) return 'bg-green-300';
  if (temp <= 22) return 'bg-yellow-300';
  if (temp <= 27) return 'bg-orange-300';
  return 'bg-red-300';
}
</script>
