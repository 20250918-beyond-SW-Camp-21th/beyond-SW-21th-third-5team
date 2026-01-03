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
            v-for="item in outfitItems"
            :key="item.label"
            class="flex flex-col items-center gap-2"
        >
          <div class="w-14 h-14 bg-gradient-to-br from-[#FFF5F8] to-[#FFE8F0] rounded-2xl flex items-center justify-center shadow-sm">
            <component :is="item.icon" class="w-6 h-6 text-[#FF9BCB]" />
          </div>
          <span class="text-xs text-[#6B7280]">{{ item.label }}</span>
        </div>
      </div>


      <p class="text-[#6B7280] mb-6">{{ outfitDescription }}</p>

      <button class="w-full bg-[#FF9BCB] text-white py-4 rounded-2xl hover:bg-[#FF7FB3] transition-all shadow-lg shadow-pink-200/50 font-semibold" @click="handleRecordClick">
        오늘 기록하기
      </button>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { ChevronDown } from 'lucide-vue-next';

const emit = defineEmits<{
  (event: 'record'): void;
}>();

function handleRecordClick() {
  emit('record');
}


type WeatherStat = {
  icon: unknown;
  label: string;
  value: string;
};

type OutfitItem = {
  icon: unknown;
  label: string;
};

const props = defineProps<{
  locationLabel?: string;
  dateLabel?: string;
  statusMessage?: string;
  temperatureDisplay?: string;
  weatherSummary?: string;
  weatherStats?: WeatherStat[];
  outfitItems?: OutfitItem[];
  outfitDescription?: string;
}>();

const locationDisplay = computed(() => props.locationLabel || '--');
const dateLabel = computed(() => props.dateLabel || '--');
const temperatureDisplay = computed(() => props.temperatureDisplay || '--');
const weatherSummary = computed(() => props.weatherSummary || '--');
const weatherStats = computed(() => props.weatherStats ?? []);
const outfitItems = computed(() => props.outfitItems ?? []);
const outfitDescription = computed(() => props.outfitDescription || '--');
</script>
