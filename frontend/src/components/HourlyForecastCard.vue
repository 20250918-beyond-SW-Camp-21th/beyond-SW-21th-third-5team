<template>
  <div class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50">
    <div class="flex items-center justify-between mb-6">
      <div>
        <h3 class="text-[#1F2A37] text-xl">시간대별 기상 온도</h3>
        <p class="text-[#6B7280] text-sm">바람이나 체감온도를 고려했어요</p>
      </div>
      <div class="flex items-center gap-2 bg-[#F6FAFF] px-3 py-2 rounded-xl border border-[#E6EEF9]">
        <span class="text-sm text-[#6B7280]">서울</span>
        <ChevronDown class="w-4 h-4" />
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
          <span class="text-sm text-[#6B7280]">체감 {{ item.feelsLike }}°</span>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ChevronDown, CloudRain, Sun, Zap, Wind } from 'lucide-vue-next';

const hourlyData = [
  { time: '12:00', temperature: 18, feelsLike: 16, icon: Zap, summary: '' +
        '번개/비' },
  { time: '13:00', temperature: 19, feelsLike: 17, icon: CloudRain, summary: '비' },
  { time: '14:00', temperature: 20, feelsLike: 18, icon: Sun, summary: '맑음' },
  { time: '15:00', temperature: 19, feelsLike: 17, icon: Wind, summary: '바람' },
];
</script>
