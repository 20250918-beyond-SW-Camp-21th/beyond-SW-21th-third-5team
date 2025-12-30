<template>
  <nav class="w-full h-[72px] bg-white/80 backdrop-blur-sm border-b border-[#E6EEF9] sticky top-0 z-50">
    <div class="max-w-[1440px] mx-auto px-20 h-full flex items-center justify-between">
      <div class="flex-shrink-0">
        <h1 class="text-[20px] font-semibold text-[#1F2A37]">자란날씨</h1>
      </div>

      <button
        v-if="simple"
        class="text-[#6B7280] hover:text-[#1F2A37]"
        type="button"
        @click="emit('tab-change', '오늘의 날씨')"
      >
        오늘의 날씨로 돌아가기
      </button>

      <template v-else>
        <div class="flex items-center gap-2">
          <button
            v-for="tab in tabs"
            :key="tab"
            type="button"
            class="px-4 py-2 rounded-full transition-all"
            :class="
              tab === activeTab
                ? 'bg-[#EAF2FF] text-[#1F2A37] font-semibold'
                : 'text-[#6B7280] hover:bg-[#F6FAFF]'
            "
            @click="emit('tab-change', tab)"
          >
            {{ tab }}
          </button>
        </div>

        <div class="flex items-center gap-3">
          <div class="relative">
            <Search class="absolute left-3 top-1/2 -translate-y-1/2 w-5 h-5 text-[#6B7280]" />
            <input
              type="text"
              placeholder="도시 검색"
              class="w-[200px] pl-10 pr-4 py-2 bg-[#F6FAFF] border border-[#E6EEF9] rounded-full placeholder:text-[#6B7280] focus:outline-none focus:ring-2 focus:ring-[#EAF2FF]"
            />
          </div>
          <button
            type="button"
            class="px-5 py-2 rounded-full bg-white border border-[#E6EEF9] text-[#1F2A37] hover:bg-[#F6FAFF] transition-all"
            @click="emit('tab-change', '로그인')"
          >
            로그인
          </button>
        </div>
      </template>
    </div>
  </nav>
</template>

<script setup lang="ts">
import { Search } from 'lucide-vue-next';
import { computed } from 'vue';

const tabs = ['오늘의 날씨', '이번주 날씨', '국내 기온지도', '달력 기록'] as const;
type PageKey = (typeof tabs)[number] | '로그인';

const props = defineProps<{ activeTab?: PageKey; simple?: boolean }>();
const emit = defineEmits<{ 'tab-change': [tab: PageKey] }>();

const activeTab = computed(() => props.activeTab ?? '오늘의 날씨');
</script>
