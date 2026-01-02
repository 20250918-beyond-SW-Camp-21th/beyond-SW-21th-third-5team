<template>
  <main class="max-w-[1440px] mx-auto px-20 py-8">

    <div class="grid grid-cols-2 gap-6 mb-8">
      <section class="bg-white rounded-[28px] p-8 shadow-lg shadow-blue-100/50">
        <div class="flex items-center justify-between mb-6">
          <h3 class="text-[#1F2A37] text-lg font-semibold">내 기록</h3>
        </div>

        <!-- 빈 상태 -->
        <div v-if="records.length === 0" class="text-[#9CA3AF]">
          아직 등록한 기록이 없습니다.
        </div>

        <!-- 목록 -->
        <ul v-else class="space-y-3">
          <li
              v-for="r in records"
              :key="r.id"
              class="recordItem"
              @click="goDetail(r.id)"
          >
            <div class="thumb">
              <img v-if="r.photoUrl" :src="r.photoUrl" class="thumbImg" />
              <div v-else class="thumbEmpty">No</div>
            </div>
          </li>
        </ul>
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

  const records = ref([
    { id: 101, date: '2026-01-02', photoUrl: '', score: 4, review: '코트 입기 딱 좋았다.' },
    { id: 102, date: '2026-01-01', photoUrl: '', score: 5, review: '레이어드가 완벽했다.' }
  ])

  const goDetail = (id) => {
    router.push({ name: 'ootd-detail', params: { id } })
  }


</script>

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

