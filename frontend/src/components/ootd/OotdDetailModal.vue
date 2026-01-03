<script setup lang="ts">
type Feeling = 'COLD' | 'GOOD' | 'HOT'

defineProps<{
  detail: {
    id: number
    photoUrl: string | null
    date: string
    feeling: Feeling
    review: string
  }
}>()

defineEmits(['close'])
</script>

<template>
  <!-- 배경(오버레이) -->
  <div class="overlay" @click.self="$emit('close')">
    <div class="modal">
      <!-- 헤더 -->
      <header class="modalHeader">
        <h2 class="title">리뷰 조회</h2>

        <button class="saveBtn" type="button" disabled>
          리뷰 저장
        </button>
      </header>

      <!-- 본문 레이아웃(좌측 사진 / 우측 날짜, 만족도, 후기) -->
      <section class="bodyGrid">
        <!-- 좌측: 사진 -->
        <div class="card photoCard">
          <div class="cardTitle">사진</div>

          <div class="photoSlot">
            <img v-if="detail.photoUrl" :src="detail.photoUrl" class="photo" />
            <div v-else class="photoEmpty">등록된 사진이 없습니다.</div>
          </div>
        </div>

        <!-- 우측 -->
        <div class="rightCol">
          <!-- 날짜 -->
          <div class="card">
            <div class="cardTitle">날짜</div>
            <div class="readField">
              <span class="readValue">{{ detail.date }}</span>
              <span class="calendarIcon">📅</span>
            </div>
          </div>

          <!-- 만족도 -->
          <div class="card">
            <div class="cardTitle">만족도</div>
            <div class="satisfactionRow">
              <div class="pill" :class="{ active: detail.feeling === 'COLD' }">춥다</div>
              <div class="pill" :class="{ active: detail.feeling === 'GOOD' }">좋았다</div>
              <div class="pill" :class="{ active: detail.feeling === 'HOT' }">덥다</div>
            </div>
          </div>

          <!-- 후기 -->
          <div class="card">
            <div class="cardTitle">후기</div>
            <div class="reviewBox">
              <div class="reviewText">
                {{ detail.review || '후기가 없습니다.' }}
              </div>
            </div>
          </div>
        </div>
      </section>

      <!-- 하단 닫기 -->
      <footer class="footer">
        <button class="closeBtn" type="button" @click="$emit('close')">닫기</button>
      </footer>
    </div>
  </div>
</template>

<style scoped>
.overlay{
  position: fixed;
  inset: 0;
  background: rgba(0,0,0,0.45);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 9999;
}

.modal{
  width: 1200px;
  max-width: calc(100vw - 48px);
  background: #fff;
  border-radius: 22px;
  overflow: hidden;
}

.modalHeader{
  display:flex;
  align-items:center;
  justify-content:space-between;
  padding: 18px 22px;
  border-bottom: 1px solid #e9edf3;
}

.title{
  font-size: 28px;
  font-weight: 900;
}

.saveBtn{
  padding: 12px 18px;
  border-radius: 18px;
  border: none;
  background: #6AA9FF;
  color: #fff;
  font-weight: 900;
  opacity: 0.55;
  cursor: not-allowed;
}

.bodyGrid{
  display:grid;
  grid-template-columns: 1fr 1.4fr;
  gap: 22px;
  padding: 22px;
}

.card{
  border: 2px solid #111;
  border-radius: 14px;
  padding: 18px;
  background: #fff;
}

.photoCard{
  background: #f3f4f6;
}

.cardTitle{
  font-weight: 900;
  margin-bottom: 14px;
}

.photoSlot{
  height: 560px;
  border-radius: 12px;
  border: 1px dashed #cbd5e1;
  background: #f9fafb;
  display:flex;
  align-items:center;
  justify-content:center;
  overflow:hidden;
}

.photo{
  width:100%;
  height:100%;
  object-fit: cover;
}

.photoEmpty{
  color:#6b7280;
  font-weight: 800;
}

.rightCol{
  display:flex;
  flex-direction:column;
  gap: 18px;
}

.readField{
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 14px 16px;
  display:flex;
  align-items:center;
  justify-content:space-between;
}

.readValue{
  font-weight: 800;
  font-size: 18px;
}

.calendarIcon{
  font-size: 18px;
}

.satisfactionRow{
  display:grid;
  grid-template-columns: 1fr 1fr 1fr;
  gap: 12px;
}

.pill{
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 14px 10px;
  text-align:center;
  font-weight: 900;
  background:#fff;
}

.pill.active{
  border-color: #6AA9FF;
  background: #eaf2ff;
}

.reviewBox{
  border: 1px solid #e5e7eb;
  border-radius: 12px;
  padding: 14px 16px;
  min-height: 260px;
}

.reviewText{
  white-space: pre-wrap;
  line-height: 1.6;
}

.footer{
  display:flex;
  justify-content:flex-end;
  padding: 18px 22px;
}

.closeBtn{
  padding: 12px 20px;
  border-radius: 18px;
  border: 1px solid #e5e7eb;
  background: #fff;
  font-weight: 900;
  cursor: pointer;
}
</style>