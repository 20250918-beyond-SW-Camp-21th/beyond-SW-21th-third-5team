<template>
  <main class="max-w-[1440px] mx-auto px-20 py-8">

    <div class="grid grid-cols-2 gap-6 mb-8">
      <section class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-[#1F2A37] text-lg font-semibold">내 기록</h3>
        </div>

        <div v-if="records.length === 0" class="text-[#9CA3AF]">
          아직 등록한 기록이 없습니다.
        </div>
        <div v-else>
          <div class="grid grid-cols-4 gap-4 bg-red-200">
            <button
                v-for="r in pageItems"
                :key="r.id"
                type="button"
                class="w-full relative overflow-hidden rounded-2xl bg-gray-100 border border-gray-200"
                style="padding-top: 100%;"
            @click="goDetail(r.id)"
            >
            <img
                v-if="r.photoUrl"
                :src="r.photoUrl"
                class="absolute inset-0 w-full h-full object-cover"
                alt="ootd"
            />
            <div
                v-else
                class="absolute inset-0 flex items-center justify-center text-xs font-semibold text-gray-400"
            >
              No Image
            </div>
            </button>
          </div>

          <div class="mt-6 flex items-center justify-between">
            <button
                type="button"
                class="px-3 py-2 rounded-xl border border-gray-200 bg-white font-semibold"
                :disabled="page === 1"
                @click="page--"
            >
              이전
            </button>

            <button
                type="button"
                class="px-3 py-2 rounded-xl border border-gray-200 bg-white font-semibold"
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
        @close="closeReviewModal"
    />
  </main>
</template>

<script setup lang="ts">
  import { computed, ref } from 'vue';
  import ReviewModal from "../ootd/ReviewModal.vue";

  const today = new Date().toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric'
  })

  const todayTemp = ref(null)
  const Weather = ref(null)
  const score = ref(null)
  const ootd = ref(null)
  const review = ref(null)

  const isReviewModalOpen = ref(false)
  const openReviewModal = () => (isReviewModalOpen.value = true)
  const closeReviewModal = () => (isReviewModalOpen.value = false)

  /*const goDetail = (id) => {
    router.push({ name: 'ootd-detail', params: { id } })
  }*/

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

</style>
