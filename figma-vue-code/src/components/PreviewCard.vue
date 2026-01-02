<template>
  <div class="bg-white rounded-[28px] p-6 shadow-lg shadow-blue-100/50 h-full">
    <div class="flex items-center justify-between mb-4">
      <div>
        <p class="text-[#6B7280] text-sm">{{ cardContent.subtitle }}</p>
        <h4 class="text-[#1F2A37] text-lg">{{ cardContent.title }}</h4>
      </div>
      <component :is="cardContent.icon" class="w-6 h-6 text-[#6AA9FF]" />
    </div>

    <p class="text-[#1F2A37] font-semibold mb-2">{{ cardContent.highlight }}</p>
    <p class="text-[#6B7280] text-sm">{{ cardContent.description }}</p>
  </div>
</template>

<script setup lang="ts">
import { CalendarRange, MapPin, Sun } from 'lucide-vue-next';
import { computed } from 'vue';

type PreviewType = 'tomorrow' | 'week' | 'map';

const props = defineProps<{ type: PreviewType }>();

const cardContent = computed(() => {
  switch (props.type) {
    case 'tomorrow':
      return {
        title: '내일 기상',
        subtitle: '12/30 (화)',
        highlight: '아침까지 번개 후 맑음',
        description: '오후부터 맑음, 12–17°C',
        icon: Sun,
      };
    case 'week':
      return {
        title: '이번주 날씨',
        subtitle: '12/29–01/04',
        highlight: '비 오는 날 2일',
        description: '주말 일교차 큼, 우산 챙기기',
        icon: CalendarRange,
      };
    case 'map':
    default:
      return {
        title: '국내 기온지도',
        subtitle: '전국 기온 분포',
        highlight: '제주도 22°C로 가장 따뜻',
        description: '서울 18°C, 강원 12°C',
        icon: MapPin,
      };
  }
});
</script>
