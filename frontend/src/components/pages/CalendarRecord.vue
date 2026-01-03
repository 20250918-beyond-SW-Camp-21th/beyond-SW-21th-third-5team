<template>
  <main class="max-w-[1440px] mx-auto px-20 py-8">
    <TodayWeather @weather-loaded="onWeatherLoaded" style="display:none"/>
    <div class="grid grid-cols-2 gap-6 mb-8">
      <section class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-[#1F2A37] text-lg font-semibold">내 기록</h3>
        </div>

        <div v-if="records.length === 0" class="text-[#9CA3AF]">
          아직 등록한 기록이 없습니다.
        </div>
        <div v-else>
          <div class="recordGrid">
            <button
                v-for="r in pageItems"
                :key="r.id"
                type="button"
                class="recordCard"
                @click="openDetailModal(r)"
            >
              <img
                  v-if="r.photoUrl"
                  :src="r.photoUrl"
                  class="recordImage"
                  alt="ootd"
              />
              <div v-else class="recordEmpty">No Image</div>
            </button>
          </div>

          <div class="pagination">
            <button
                type="button"
                class="pageBtn"
                :disabled="page === 1"
                @click="page--"
            >
              이전
            </button>

            <button
                type="button"
                class="pageBtn"
                :disabled="page === totalPages"
                @click="page++"
            >
              다음
            </button>
          </div>
        </div>



      </section>

      <div class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-[#1F2A37] text-lg font-semibold">
            {{ today }}의 OOTD
          </h3>

          <button
              type="button"
              class="px-4 py-2 rounded-xl text-sm font-medium
                   bg-[#6AA9FF] text-white
                   hover:bg-[#4F8FFF] transition-colors"
              @click="openReviewModal"
          >
            리뷰 등록
          </button>
        </div>

        <div class="bg-[#F6FAFF] rounded-2xl p-4 mb-6 border border-[#E6EEF9]">
          <div class="flex items-center gap-3 mb-2">
            <span class="text-2xl">⚡</span>
            <div>
              <div class="text-[#1F2A37] font-semibold">오늘의 기온 {{ todayTemp }}°C</div>
              <div class="text-sm text-[#6B7280]">{{ Weather }}</div>
            </div>
          </div>
        </div>

        <section class="bottom">
          <!-- 사진 -->
          <div class="box left">
            <img
                v-if="photoUrl"
                :src="photoUrl"
                class="photo"
            />
            <div
                v-else
                class="photo-placeholder"
            >
              사진 없음
            </div>
          </div>

          <div class="right">
            <!-- 만족도 -->
            <div class="box rightTop">
              <p class="label">{{ score }}</p>
            </div>

            <!-- 후기 -->
            <div class="box rightBottom">
              <p class="label">후기</p>
              <p class="review">
                {{ review }}
              </p>
            </div>
          </div>
        </section>
      </div>
    </div>
    <ReviewModal
        v-if="isReviewModalOpen"
        :weatherNum="todayPty"
        :temperature="todayTmx"
        @close="closeReviewModal"
    />

    <OotdDetailModal
        v-if="isDetailModalOpen"
        :detail="selectedDetail"
        @close="closeDetailModal"
    />
  </main>
</template>

<script setup lang="ts">
import { computed, ref } from 'vue';
import ReviewModal from "../ootd/ReviewModal.vue";
import TodayWeather from "../pages/TodayWeather.vue";

const today = new Date().toLocaleDateString('ko-KR', {
  year: 'numeric',
  month: 'long',
  day: 'numeric'
})

const todayTemp = ref(null)
const Weather = ref(null)
const score = ref(null)
const review = ref(null)

const isReviewModalOpen = ref(false)
const openReviewModal = () => (isReviewModalOpen.value = true)
const closeReviewModal = () => (isReviewModalOpen.value = false)

const page = ref(1)
const pageSize = 16 //

const total = computed(() => records.value.length)
const totalPages = computed(() => Math.max(1, Math.ceil(total.value / pageSize)))

const startIndex = computed(() => (page.value - 1) * pageSize)
const endIndex = computed(() => Math.min(total.value, startIndex.value + pageSize))

const pageItems = computed(() =>
    records.value.slice(startIndex.value, endIndex.value)
)

const pagesToShow = computed(() => {
  const maxButtons = 5
  const tp = totalPages.value
  const cur = page.value

  let start = Math.max(1, cur - Math.floor(maxButtons / 2))
  let end = Math.min(tp, start + maxButtons - 1)
  start = Math.max(1, end - maxButtons + 1)

  const arr = []
  for (let p = start; p <= end; p++) arr.push(p)
  return arr
})

/** 이동 */
const goDetail = (id) => {
  router.push({ name: 'ootd-detail', params: { id } })
}

/** util */
const formatDate = (iso) => {
  const d = new Date(iso)
  if (Number.isNaN(d.getTime())) return iso
  return d.toLocaleDateString('ko-KR', { month: 'numeric', day: 'numeric' })
}

const feelingLabel = (v) => {
  if (v === 'COLD') return '춥다'
  if (v === 'GOOD') return '좋았다'
  if (v === 'HOT') return '덥다'
  return v
}

const records = ref([
  { id: 1, photoUrl: 'https://picsum.photos/300/300?random=1' },
  { id: 2, photoUrl: 'https://picsum.photos/300/300?random=2' },
  { id: 3, photoUrl: 'https://picsum.photos/300/300?random=3' },
  { id: 4, photoUrl: 'https://picsum.photos/300/300?random=4' },

  { id: 5, photoUrl: 'https://picsum.photos/300/300?random=5' },
  { id: 6, photoUrl: 'https://picsum.photos/300/300?random=6' },
  { id: 7, photoUrl: 'https://picsum.photos/300/300?random=7' },
  { id: 8, photoUrl: 'https://picsum.photos/300/300?random=8' },

  { id: 9, photoUrl: 'https://picsum.photos/300/300?random=9' },
  { id: 10, photoUrl: 'https://picsum.photos/300/300?random=10' },
  { id: 11, photoUrl: 'https://picsum.photos/300/300?random=11' },
  { id: 12, photoUrl: 'https://picsum.photos/300/300?random=12' },

  { id: 13, photoUrl: 'https://picsum.photos/300/300?random=13' },
  { id: 14, photoUrl: 'https://picsum.photos/300/300?random=14' },
  { id: 15, photoUrl: 'https://picsum.photos/300/300?random=15' },
  { id: 16, photoUrl: 'https://picsum.photos/300/300?random=16' }
])

import OotdDetailModal from '../ootd/OotdDetailModal.vue'

const isDetailModalOpen = ref(false)
const selectedDetail = ref({
  id: 0,
  photoUrl: null as string | null,
  date: '',
  feeling: 'GOOD' as 'COLD' | 'GOOD' | 'HOT',
  review: '',
})

const openDetailModal = (record: { id: number; photoUrl: string }) => {
  // 지금은 API 없이 예시 데이터로 채움 (나중에 DB 조회로 교체)
  selectedDetail.value = {
    id: record.id,
    photoUrl: record.photoUrl,
    date: '2026-01-02',
    feeling: 'GOOD',
    review: '이 날은 체감이 딱 좋아서 만족했습니다.',
  }
  isDetailModalOpen.value = true
}

const closeDetailModal = () => (isDetailModalOpen.value = false)

const todayPty = ref<number | null>(1)
const todayTmx = ref<number | null>(1)

const onWeatherLoaded = (payload: { pty: number; tmx: number }) => {
  todayPty.value = payload.pty
  todayTmx.value = payload.tmx
}
</script>


<style>
.content {
  display: grid;
  grid-template-columns: 320px 1fr;
  gap: 36px;
}


.photoBox {
  min-height: 320px;
  background: #f3f4f6;
  border-radius: 20px;
  display: flex;
  align-items: center;
  justify-content: center;
}


.right {
  display: grid;
  grid-template-rows: 120px 1fr;
}

.satisfactionBox {
  min-height: 120px;
  background: #fff;
  border-radius: 20px;
  padding: 20px;
}

.reviewBox {
  min-height: 200px;
  background: #fff;
  border-radius: 20px;
  padding: 20px;
}

.pageBtn{
  padding: 8px 12px;
  border-radius: 12px;
  border: 1px solid #e5e7eb;
  background: #fff;
  font-weight: 800;
}
.pageBtn:disabled{ opacity: 0.45; cursor: not-allowed; }

.recordGrid{
  display: grid;
  grid-template-columns: repeat(4, 1fr); /* ✅ 4칸 고정 */
  gap: 16px;
  width: 100%;
}

.recordCard{
  position: relative;
  width: 100%;
  aspect-ratio: 1 / 1; /* ✅ 정사각형 */
  border-radius: 16px;
  overflow: hidden;
  background: #f3f4f6;
  border: 1px solid #e5e7eb;
  cursor: pointer;
}

.recordImage{
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.recordEmpty{
  position: absolute;
  inset: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 12px;
  font-weight: 600;
  color: #9ca3af;
}

.pagination{
  margin-top: 24px;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

</style>
