<script setup lang="ts">
  import { computed, ref } from 'vue';
  import { ChevronLeft, ChevronRight, Plus, Trash2, Upload } from 'lucide-vue-next';

  type Satisfaction = 'cold' | 'good' | 'hot';

  const selectedDate = ref(29);
  const selectedOutfits = ref<string[]>(['가디건', '긴바지']);
  const satisfaction = ref<Satisfaction>('good');

  const outfitOptions = ['가디건', '자켓', '코트', '패딩', '반팔', '긴팔', '긴바지', '반바지', '치마', '운동화', '부츠'];

  const recordedDates = [
    { date: 15, temp: 12, icon: '🧥' },
    { date: 20, temp: 18, icon: '👕' },
    { date: 25, temp: -3, icon: '🧥' },
    { date: 29, temp: 18, icon: '⚡' },
  ];

  const recentRecords = [
    { date: '12월 25일', temp: '-3°C', note: '너무 추웠어요. 패딩 필수!' },
    { date: '12월 20일', temp: '18°C', note: '딱 좋은 날씨. 가디건 하나면 충분' },
    { date: '12월 15일', temp: '12°C', note: '자켓 입고 나갔는데 괜찮았음' },
  ];

  const daysInMonth = 31;
  const startDay = 0;
  const weekDays = ['일', '월', '화', '수', '목', '금', '토'];

  const calendarDays = computed(() => {
    return Array.from({ length: startDay }, () => null as number | null).concat(
        Array.from({ length: daysInMonth }, (_, i) => i + 1)
    );
  });

  function toggleOutfit(outfit: string) {
    if (selectedOutfits.value.includes(outfit)) {
      selectedOutfits.value = selectedOutfits.value.filter(o => o !== outfit);
    } else {
      selectedOutfits.value = [...selectedOutfits.value, outfit];
    }
  }

  function findRecord(day: number) {
    return recordedDates.find(r => r.date === day);
  }

  function calendarButtonClass(day: number) {
    const hasRecord = findRecord(day);
    const isSelected = day === selectedDate.value;

    if (isSelected) return 'bg-[#EAF2FF] text-[#1F2A37] font-semibold shadow-md';
    if (hasRecord) return 'bg-[#F6FAFF] text-[#1F2A37] hover:bg-[#EAF2FF]';
    return 'text-[#6B7280] hover:bg-[#F6FAFF]';
  }
</script>

<template>
  <template>
    <main class="max-w-[1440px] mx-auto px-20 py-8">

      <div class="grid grid-cols-2 gap-6 mb-8">
        <div class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50">
          <div class="flex items-center justify-between mb-6">
            <h3 class="text-[#1F2A37]">2025년 12월</h3>
            <div class="flex gap-2">
              <button class="w-10 h-10 bg-[#F6FAFF] rounded-xl hover:bg-[#EAF2FF] transition-all flex items-center justify-center">
                <ChevronLeft class="w-5 h-5 text-[#6B7280]" />
              </button>
              <button class="w-10 h-10 bg-[#F6FAFF] rounded-xl hover:bg-[#EAF2FF] transition-all flex items-center justify-center">
                <ChevronRight class="w-5 h-5 text-[#6B7280]" />
              </button>
            </div>
          </div>

          <div class="grid grid-cols-7 gap-2">
            <div v-for="day in weekDays" :key="day" class="text-center text-sm text-[#6B7280] font-medium py-2">{{ day }}</div>

            <template v-for="(day, index) in calendarDays" :key="`${day ?? 'empty'}-${index}`">
              <div v-if="day === null" />
              <button
                  v-else
                  type="button"
                  :class="calendarButtonClass(day)"
                  class="aspect-square rounded-2xl p-2 transition-all relative"
                  @click="selectedDate = day"
              >
                <span class="block text-sm">{{ day }}</span>
                <span v-if="findRecord(day)" class="absolute bottom-1 left-1/2 -translate-x-1/2 flex gap-1">
                <span class="inline-block w-1 h-1 bg-[#6AA9FF] rounded-full" />
                <span class="text-xs">{{ findRecord(day)?.icon }}</span>
              </span>
              </button>
            </template>
          </div>
        </div>

        <div class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50">
          <h3 class="text-[#1F2A37] mb-6">12월 {{ selectedDate }}일 기록</h3>

          <div class="bg-[#F6FAFF] rounded-2xl p-4 mb-6 border border-[#E6EEF9]">
            <div class="flex items-center gap-3 mb-2">
              <span class="text-2xl">⚡</span>
              <div>
                <div class="text-[#1F2A37] font-semibold">그날 기온 18°C</div>
                <div class="text-sm text-[#6B7280]">체감 16°C · 번개</div>
              </div>
            </div>
          </div>

          <div class="mb-6">
            <label class="block text-[#1F2A37] mb-3">입었던 옷</label>
            <div class="flex flex-wrap gap-2">
              <button
                  v-for="outfit in outfitOptions"
                  :key="outfit"
                  type="button"
                  class="px-4 py-2 rounded-xl transition-all"
                  :class="selectedOutfits.includes(outfit) ? 'bg-[#6AA9FF] text-white' : 'bg-[#F6FAFF] text-[#6B7280] hover:bg-[#EAF2FF]'"
                  @click="toggleOutfit(outfit)"
              >
                {{ outfit }}
              </button>
            </div>
          </div>

          <div class="mb-6">
            <label class="block text-[#1F2A37] mb-3">만족도</label>
            <div class="flex gap-3">
              <button
                  type="button"
                  class="flex-1 py-3 rounded-2xl transition-all"
                  :class="satisfaction === 'cold' ? 'bg-[#6AA9FF] text-white' : 'bg-[#F6FAFF] text-[#6B7280] hover:bg-[#EAF2FF]'"
                  @click="satisfaction = 'cold'"
              >
                ❄️ 추웠어요
              </button>
              <button
                  type="button"
                  class="flex-1 py-3 rounded-2xl transition-all"
                  :class="satisfaction === 'good' ? 'bg-[#6AA9FF] text-white' : 'bg-[#F6FAFF] text-[#6B7280] hover:bg-[#EAF2FF]'"
                  @click="satisfaction = 'good'"
              >
                👍 딱 좋아요
              </button>
              <button
                  type="button"
                  class="flex-1 py-3 rounded-2xl transition-all"
                  :class="satisfaction === 'hot' ? 'bg-[#6AA9FF] text-white' : 'bg-[#F6FAFF] text-[#6B7280] hover:bg-[#EAF2FF]'"
                  @click="satisfaction = 'hot'"
              >
                🔥 더웠어요
              </button>
            </div>
          </div>

          <div class="mb-6">
            <label class="block text-[#1F2A37] mb-3">메모</label>
            <textarea
                class="w-full h-24 px-4 py-3 bg-[#F6FAFF] border border-[#E6EEF9] rounded-2xl resize-none focus:outline-none focus:ring-2 focus:ring-[#6AA9FF] placeholder:text-[#6B7280]"
                placeholder="오늘의 체감이나 메모를 기록하세요"
            ></textarea>
          </div>

          <div class="flex gap-3">
            <button class="flex-1 py-3 bg-[#6AA9FF] text-white rounded-2xl hover:bg-[#5B98EF] transition-all shadow-lg shadow-blue-200/50 font-semibold">
              저장하기
            </button>
            <button class="w-12 h-12 bg-[#F6FAFF] rounded-2xl border border-[#E6EEF9] flex items-center justify-center">
              <Upload class="w-5 h-5 text-[#6B7280]" />
            </button>
            <button class="w-12 h-12 bg-[#FFF5F8] rounded-2xl border border-[#FFE8F0] flex items-center justify-center">
              <Trash2 class="w-5 h-5 text-[#FF9BCB]" />
            </button>
          </div>
        </div>
      </div>

      <div class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50">
        <h3 class="text-[#1F2A37] mb-6">최근 기록</h3>
        <div class="space-y-3">
          <div
              v-for="record in recentRecords"
              :key="record.date"
              class="flex items-center justify-between bg-[#F6FAFF] rounded-2xl px-4 py-3"
          >
            <div>
              <div class="text-[#1F2A37] font-semibold">{{ record.date }}</div>
              <div class="text-sm text-[#6B7280]">{{ record.note }}</div>
            </div>
            <span class="text-[#6B7280]">{{ record.temp }}</span>
          </div>
        </div>
      </div>
    </main>
  </template>

  <script setup lang="ts">
    import { computed, ref } from 'vue';
    import { ChevronLeft, ChevronRight, Plus, Trash2, Upload } from 'lucide-vue-next';

    type Satisfaction = 'cold' | 'good' | 'hot';

    const selectedDate = ref(29);
    const selectedOutfits = ref<string[]>(['가디건', '긴바지']);
    const satisfaction = ref<Satisfaction>('good');

    const outfitOptions = ['가디건', '자켓', '코트', '패딩', '반팔', '긴팔', '긴바지', '반바지', '치마', '운동화', '부츠'];

    const recordedDates = [
      { date: 15, temp: 12, icon: '🧥' },
      { date: 20, temp: 18, icon: '👕' },
      { date: 25, temp: -3, icon: '🧥' },
      { date: 29, temp: 18, icon: '⚡' },
    ];

    const recentRecords = [
      { date: '12월 25일', temp: '-3°C', note: '너무 추웠어요. 패딩 필수!' },
      { date: '12월 20일', temp: '18°C', note: '딱 좋은 날씨. 가디건 하나면 충분' },
      { date: '12월 15일', temp: '12°C', note: '자켓 입고 나갔는데 괜찮았음' },
    ];

    const daysInMonth = 31;
    const startDay = 0;
    const weekDays = ['일', '월', '화', '수', '목', '금', '토'];

    const calendarDays = computed(() => {
      return Array.from({ length: startDay }, () => null as number | null).concat(
          Array.from({ length: daysInMonth }, (_, i) => i + 1)
      );
    });

    function toggleOutfit(outfit: string) {
      if (selectedOutfits.value.includes(outfit)) {
        selectedOutfits.value = selectedOutfits.value.filter(o => o !== outfit);
      } else {
        selectedOutfits.value = [...selectedOutfits.value, outfit];
      }
    }

    function findRecord(day: number) {
      return recordedDates.find(r => r.date === day);
    }

    function calendarButtonClass(day: number) {
      const hasRecord = findRecord(day);
      const isSelected = day === selectedDate.value;

      if (isSelected) return 'bg-[#EAF2FF] text-[#1F2A37] font-semibold shadow-md';
      if (hasRecord) return 'bg-[#F6FAFF] text-[#1F2A37] hover:bg-[#EAF2FF]';
      return 'text-[#6B7280] hover:bg-[#F6FAFF]';
    }
  </script>

</template>

<style scoped>

</style>