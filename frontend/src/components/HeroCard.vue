<template>
  <div class="relative bg-white rounded-[28px] p-8 overflow-hidden shadow-lg shadow-blue-100/50 h-full flex flex-col justify-between min-h-[480px]">
    <div class="absolute top-0 right-0 w-64 h-64 bg-white/30 rounded-full blur-3xl" />
    <div class="absolute bottom-0 left-0 w-48 h-48 bg-white/40 rounded-full blur-2xl" />

    <div class="absolute top-12 right-16">
      <Zap class="w-8 h-8 text-yellow-400 fill-yellow-300" />
    </div>
    <div class="absolute top-24 right-32">
      <Zap class="w-6 h-6 text-yellow-400 fill-yellow-300 opacity-70" />
    </div>

    <div class="relative z-10 flex-1 flex items-center justify-center">
      <div class="relative">
        <video
          :src="heroVideoSrc"
          class="w-80 h-80 object-contain"
          autoplay
          loop
          muted
          playsinline
        />
      </div>
    </div>

    <div class="relative z-10 mt-auto">
      <div class="bg-white rounded-3xl px-6 py-4 shadow-lg inline-block max-w-md relative">
        <p class="text-[#1F2A37] text-lg">{{ heroMessage }}</p>
        <div class="absolute -top-2 left-12 w-4 h-4 bg-white transform rotate-45" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { computed } from 'vue';
import { Zap } from 'lucide-vue-next';

const props = defineProps<{
  heroVideoSrc: string;
  weatherType?: string;
}>();

const heroMessage = computed(() => {
  switch (props.weatherType) {
    case 'sunny':
      return '햇볕은 쨍쨍 모래알은 반짝~';
    case 'cloud':
      return '날씨 흐림 예상!!';
    case 'rain':
      return '오늘은 비가 와요! 우산 필수!';
    case 'snow':
      return '귀여운 펭귄 눈사람 만드는건 어때요?';
    case 'thunder':
      return '우르르 쾅쾅! 번개가 친대요';
    default:
      return '오늘도 행복한 하루 보내세요!';
  }
});
</script>
