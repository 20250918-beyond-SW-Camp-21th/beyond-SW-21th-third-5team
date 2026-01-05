<template>
  <main class="max-w-[1440px] mx-auto px-20 py-8">
    <TodayWeather @weather-loaded="onWeatherLoaded" style="display:none"/>
    <div class="grid grid-cols-2 gap-6 mb-8">
      <section class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-[#1F2A37] text-lg font-semibold">내 기록</h3>
        </div>

        <div v-if="listLoading" class="text-[#9CA3AF]">
          불러오는 중...
        </div>

        <div v-else-if="listError" class="text-[#ef4444] font-semibold">
          {{ listError }}
        </div>

        <div v-else-if="records.length === 0" class="text-[#9CA3AF]">
          아직 등록한 기록이 없습니다.
        </div>

        <div v-else>
          <div class="recordGrid">
            <button
                v-for="r in records"
                :key="r.id"
                type="button"
                class="recordCard"
                @click="openDetailModalById(r.id)"
            >
              <img
                  v-if="r.imageUrl"
                  :src="r.imageUrl"
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
                :disabled="!hasPrev"
                @click="prevPage"
            >
              이전
            </button>

            <button
                type="button"
                class="pageBtn"
                :disabled="!hasNext"
                @click="nextPage"
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
import {computed, onMounted, ref} from 'vue';
import ReviewModal from "../ootd/ReviewModal.vue";
import TodayWeather from "../pages/TodayWeather.vue";
import { useRouter } from "vue-router";
import { outfitApi } from "@/api/outfit/outfit"

const router = useRouter();

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

const page = ref(0)
const pageSize = 16 //

const listLoading = ref(false)
const listError = ref("")

// 서버에서 내려주는 목록(items)
const records = (
    Array.from({ length: 16 }, (_, i) => ({
      id: i + 1,
      // Picsum 무료 이미지 서비스 사용 (랜덤 이미지)
      imageUrl: `https://picsum.photos/300/300?random=${i}`,
    }))
    );


    // ref<Array<{ id: number; imageUrl: string | null }>>([])

// 이전/다음 활성 여부
const hasPrev = ref(false)
const hasNext = ref(false)

// 목록 로딩
const loadList = async () => {
  listLoading.value = true
  listError.value = ""
  try {
    const res = await outfitApi.getOutfits(page.value, pageSize)

    const data = res.data

    records.value = data.items ?? []
    hasPrev.value = !!data.hasPrev
    hasNext.value = !!data.hasNext

    if (typeof data.page === "number") page.value = data.page
  } catch (e: any) {
    console.error(e)
    listError.value = e?.response?.data?.message || "목록 조회에 실패했습니다."
    records.value = []
    hasPrev.value = false
    hasNext.value = false
  } finally {
    listLoading.value = false
  }
}

const prevPage = async () => {
  if (!hasPrev.value) return
  page.value -= 1
  await loadList()
}

const nextPage = async () => {
  if (!hasNext.value) return
  page.value += 1
  await loadList()
}

onMounted(() => {
  loadList()
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

const isDetailModalOpen = ref(false)
const selectedDetail = ref<any>(null)

const openDetailModalById = async (id: number) => {
  try {
    // 단건 조회 호출
    const res = await outfitApi.getMyOutfitDetail(id)
    selectedDetail.value = res.data
    isDetailModalOpen.value = true
  } catch (e) {
    console.error(e)
    alert("상세 조회에 실패했습니다.")
  }
}

const closeDetailModal = () => (isDetailModalOpen.value = false)


import OotdDetailModal from '../ootd/OotdDetailModal.vue'

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
