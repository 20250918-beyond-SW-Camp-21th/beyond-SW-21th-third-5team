<template>
  <div class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50 h-full flex flex-col min-h-[480px]">
    <div class="flex items-center justify-between mb-6">
      <button class="flex items-center gap-2 text-[#1F2A37] hover:bg-[#F6FAFF] px-4 py-2 rounded-xl transition-all">
        <span class="text-lg font-semibold">{{ locationDisplay }}</span>
        <ChevronDown class="w-5 h-5" />
      </button>
      <span class="text-[#6B7280]">12월 29일 (월)</span>
    </div>

    <p v-if="statusMessage" class="text-sm text-[#6B7280] mb-4">{{ statusMessage }}</p>

    <div class="mb-8">
      <div class="flex items-baseline gap-2 mb-2">
        <span class="text-6xl font-bold text-[#1F2A37]">{{ temperatureDisplay }}</span>
      </div>
      <span class="text-[#6B7280]">체감 16°C</span>
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
      <h3 class="text-[#1F2A37] mb-4">오늘의 추천</h3>

      <div class="flex gap-3 mb-4">
        <div
          v-for="(item, index) in outfitItems"
          :key="index"
          class="w-14 h-14 bg-gradient-to-br from-[#FFF5F8] to-[#FFE8F0] rounded-2xl flex items-center justify-center shadow-sm"
        >
          <component :is="item.icon" class="w-6 h-6 text-[#FF9BCB]" />
        </div>
      </div>

      <p class="text-[#6B7280] mb-6">가디건 + 긴바지 + 얇은 자켓</p>

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
};

const props = defineProps<{
  weatherItems?: WeatherItem[] | null;
  isLoading?: boolean;
  error?: string | null;
  locationLabel?: string;
}>();

const locationDisplay = computed(() => props.locationLabel || '현재 위치');

function findValue(categories: string[]) {
  if (!props.weatherItems?.length) {
    return null;
  }
  const match = props.weatherItems.find((item) => item && categories.includes(item.category ?? ''));
  return match?.fcstValue ?? null;
}

const temperatureDisplay = computed(() => {
  const value = findValue(['TMP', 'T1H']);
  return value ? `${value}°C` : '18°C';
});

const weatherStats = computed(() => {
  const windValue = findValue(['WSD']);
  const humidityValue = findValue(['REH']);
  const precipValue = findValue(['POP']);

  return [
    { icon: Wind, label: '바람', value: windValue ? `${windValue}m/s` : '3m/s' },
    { icon: Droplets, label: '습도', value: humidityValue ? `${humidityValue}%` : '40%' },
    { icon: CloudRain, label: '강수', value: precipValue ? `${precipValue}%` : '10%' },
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

const outfitItems = [
  { icon: ShoppingBag, label: '가디건' },
  { icon: ShoppingBag, label: '자켓' },
  { icon: Coffee, label: '긴바지' },
  { icon: Glasses, label: '운동화' },
];
</script>
