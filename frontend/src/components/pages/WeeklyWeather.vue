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
            <img :src="mascotSrc" alt="마스코트" class="w-8 h-8 object-contain" />
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
import { ref } from 'vue';
import { ChevronDown, Cloud, CloudRain, Sun, Zap } from 'lucide-vue-next';

const viewMode = ref<'list' | 'graph'>('list');

const weeklyData = [
  { day: '월', date: '12/29', icon: 'thunder', high: 18, low: 14, precipitation: '40%', outfit: '가디건 + 긴바지' },
  { day: '화', date: '12/30', icon: 'rain', high: 21, low: 16, precipitation: '60%', outfit: '얇은 자켓 + 우산' },
  { day: '수', date: '12/31', icon: 'cloud', high: 19, low: 15, precipitation: '20%', outfit: '니트 + 긴바지' },
  { day: '목', date: '01/01', icon: 'sun', high: 22, low: 17, precipitation: '5%', outfit: '가디건 + 긴바지' },
  { day: '금', date: '01/02', icon: 'cloud', high: 20, low: 15, precipitation: '10%', outfit: '트렌치 + 니트' },
  { day: '토', date: '01/03', icon: 'cloud', high: 16, low: 8, precipitation: '15%', outfit: '코트 + 목도리' },
  { day: '일', date: '01/04', icon: 'sun', high: 14, low: 2, precipitation: '0%', outfit: '패딩 + 장갑' },
];

const iconMap: Record<string, { Component: any; color: string }> = {
  thunder: { Component: Zap, color: 'text-yellow-500 fill-yellow-400' },
  rain: { Component: CloudRain, color: 'text-blue-400' },
  cloud: { Component: Cloud, color: 'text-gray-400' },
  sun: { Component: Sun, color: 'text-orange-400' },
};

const mascotSrc = new URL('../../assets/images/마스코트.png', import.meta.url).href;
</script>
