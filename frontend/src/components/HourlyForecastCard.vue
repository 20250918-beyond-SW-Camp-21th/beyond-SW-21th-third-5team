<template>
  <div class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50">
    <div class="flex items-center justify-between mb-6">
      <div>
        <h3 class="text-[#1F2A37] text-xl">시간대별 기상 온도</h3>
        <p class="text-[#6B7280] text-sm">기상 온도를 확인하고 옷을 신경써보세요 </p>
      </div>
    </div>

    <div class="grid grid-cols-4 gap-4">
      <div
        v-for="(item, index) in hourlyData"
        :key="index"
        class="bg-[#F6FAFF] rounded-3xl p-4 hover:bg-[#EAF2FF] transition-all"
      >
        <div class="flex items-center gap-3 mb-4">
          <div class="w-10 h-10 bg-white rounded-2xl flex items-center justify-center shadow-sm">
            <component :is="item.icon" class="w-5 h-5 text-[#6AA9FF]" />
          </div>
          <div>
            <div class="text-[#1F2A37]">{{ item.time }}</div>
            <div class="text-[#6B7280] text-sm">{{ item.summary }}</div>
          </div>
        </div>

        <div class="flex items-center gap-2">
          <span class="text-2xl font-bold text-[#1F2A37]">{{ item.temperature }}°</span>
        </div>
        <div class="text-sm text-[#6B7280] mt-2">강수량 {{ item.precipitation }}mm</div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { ChevronDown, Cloud, CloudRain, CloudSnow, Sun, Zap } from 'lucide-vue-next';

type HourlyItem = {
  time: string;
  temperature: string;
  precipitation: string;
  summary?: string;
  icon?: unknown;
};

const props = defineProps<{
  hourlyItems?: HourlyItem[];
}>();

const fallbackIcon = Sun;

const hourlyData = computed(() =>
  (props.hourlyItems ?? []).map((item) => ({
    ...item,
    icon: item.icon ?? mapSummaryIcon(item.summary) ?? fallbackIcon,
  }))
);

function mapSummaryIcon(summary?: string) {
  if (!summary) {
    return null;
  }
  if (summary.includes('천둥') || summary.includes('번개')) {
    return Zap;
  }
  if (summary.includes('눈')) {
    return CloudSnow;
  }
  if (summary.includes('비')) {
    return CloudRain;
  }
  if (summary.includes('구름') || summary.includes('흐림')) {
    return Cloud;
  }
  if (summary.includes('맑음')) {
    return Sun;
  }
  return null;
}
</script>
