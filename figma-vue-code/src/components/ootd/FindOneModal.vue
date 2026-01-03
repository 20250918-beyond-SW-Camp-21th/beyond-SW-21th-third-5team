<script setup lang="ts">
  import { reactive, ref } from 'vue'

  const emit = defineEmits(['close'])

  const emitClose = () => {
    emit('close')
  }


</script>

<template>
  <div class="modalOverlay" @click.self="emitClose">
    <div class="modal">

      <div class="topBar">
        <h4 class="title">리뷰 등록</h4>
        <!-- 저장 버튼 -->
        <button
            class="saveBtn"
            :disabled="isSaving"
            @click="saveToDB"
        >
          {{ isSaving ? '저장 중...' : '리뷰 저장' }}
        </button>
      </div>


      <div class="bodyGrid">

        <!-- 사진 선택 -->
        <section class="box photoBox">
          <p class="label">사진</p>
            <img
                v-if="photoPreview"
                :src="photoPreview"
                class="photoPreview"
            />
        </section>

        <section class="rightCol">

          <!-- 날짜 -->
          <div class="box">
            <p class="label">날짜</p>
            <input
                type="date"
                v-model="form.date"
                class="input"
            />
          </div>

          <!-- 만족도 -->
          <div class="box">
            <p class="label">만족도</p>

            <div class="segmented">
              <button
                  v-for="f in feelings"
                  :key="f.value"
                  type="button"
                  class="segBtn"
                  :class="{ active: form.feeling === f.value }"
                  @click="form.feeling = f.value"
              >
                {{ f.label }}
              </button>
            </div>
          </div>

          <!-- 후기 -->
          <div class="box reviewBox">
            <p class="label">후기</p>
            <textarea
                v-model.trim="form.review"
                class="textarea"
                rows="6"
                placeholder="후기를 입력해 주세요."
            />
            <p v-if="errorMsg" class="error">{{ errorMsg }}</p>
          </div>

        </section>
      </div>

      <!-- 닫기 -->
      <div class="footer">
        <button class="cancelBtn" @click="emitClose">
          닫기
        </button>
      </div>

    </div>
  </div>
</template>

<style scoped>

</style>