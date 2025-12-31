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
        <p class="text-[#1F2A37] text-lg">오늘 번개가 쳐요! 우산 챙겨요! ⚡</p>
        <div class="absolute -top-2 left-12 w-4 h-4 bg-white transform rotate-45" />
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { Zap } from 'lucide-vue-next';

defineProps<{ heroVideoSrc: string }>();
</script>
