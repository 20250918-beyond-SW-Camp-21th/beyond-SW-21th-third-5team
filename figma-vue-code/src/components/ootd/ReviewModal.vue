<script setup lang="ts">
import { reactive, ref } from 'vue'

const emit = defineEmits(['close'])

const emitClose = () => {
  emit('close')
}

const isSaving = ref(false)
const errorMsg = ref('')

const photoFile = ref(null)
const photoPreview = ref(null)

const form = reactive({
  date: new Date().toISOString().slice(0, 10),
  feeling: 'GOOD',
  review: ''
})

const feelings = [
  { value: 'COLD', label: '춥다' },
  { value: 'GOOD', label: '좋았다' },
  { value: 'HOT', label: '덥다' }
]


const onPickFile = (e) => {
  const file = e.target.files?.[0]
  if (!file) return

  photoFile.value = file
  photoPreview.value = URL.createObjectURL(file)
}

const saveToDB = async () => {
  errorMsg.value = ''

  if (!form.review) {
    errorMsg.value = '후기를 입력해 주세요.'
    return
  }

  isSaving.value = true
  try {
    const fd = new FormData()
    fd.append('date', form.date)
    fd.append('feeling', form.feeling)
    fd.append('review', form.review)

    if (photoFile.value) {
      fd.append('photo', photoFile.value)
    }

    const res = await fetch('/api/v1/ootd-reviews', {
      method: 'POST',
      body: fd
    })

    if (!res.ok) {
      throw new Error('저장에 실패했습니다.')
    }

    emitClose()
  } catch (e) {
    errorMsg.value = e.message || '저장 중 오류가 발생했습니다.'
  } finally {
    isSaving.value = false
  }
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

          <label class="uploadArea">
            <input
                type="file"
                accept="image/*"
                class="fileInput"
                @change="onPickFile"
            />

            <img
                v-if="photoPreview"
                :src="photoPreview"
                class="photoPreview"
            />

            <span v-else class="uploadHint">
              클릭해서 사진 선택
            </span>
          </label>

          <p class="help">사진을 등록해 주세요!.</p>
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
  .modalOverlay{
    position: fixed;
    inset: 0;
    background: rgba(0,0,0,0.45);
    display: flex;
    align-items: center;
    justify-content: center;
    z-index: 50;
  }

  /* 모달 */
  .modal{
    width: min(980px, 100%);
    background: #fff;
    border-radius: 18px;
    overflow: hidden;
  }

  /*상단*/
  .topBar{
    display: flex;
    justify-content: space-between;
    align-items: center;
    padding: 16px 18px;
    border-bottom: 1px solid #e5e7eb;
  }
  .title{
    font-size: 18px;
    font-weight: 800;
  }
  .saveBtn{
    padding: 10px 16px;
    border-radius: 12px;
    background: #6AA9FF;
    color: #fff;
    font-weight: 800;
  }
  .saveBtn:disabled{
    opacity: 0.6;
  }

  .bodyGrid{
    display: grid;
    grid-template-columns: 360px 1fr;
    gap: 18px;
    padding: 18px;
  }


  .box{
    border: 2px solid #000;
    border-radius: 8px;
    padding: 14px;
  }
  .label{
    font-size: 13px;
    font-weight: 800;
    margin-bottom: 8px;
  }

  /*사진*/
  .photoBox{
    min-height: 520px;
    display: flex;
    flex-direction: column;
  }
  .uploadArea{
    flex: 1;
    border: 1px dashed #cbd5e1;
    border-radius: 10px;
    background: #f8fafc;
    display: flex;
    align-items: center;
    justify-content: center;
    cursor: pointer;
  }
  .fileInput{ display: none; }
  .uploadHint{ color: #64748b; font-weight: 700; }
  .photoPreview{
    width: 100%;
    height: 100%;
    object-fit: cover;
  }
  .help{
    margin-top: 8px;
    font-size: 12px;
    color: #6b7280;
  }


  .rightCol{
    display: grid;
    grid-template-rows: 120px 140px 1fr;
    gap: 18px;
  }


  .input, .textarea{
    width: 100%;
    border: 1px solid #e5e7eb;
    border-radius: 10px;
    padding: 10px 12px;
  }
  .textarea{
    resize: vertical;
  }


  .segmented{
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 10px;
  }
  .segBtn{
    padding: 10px;
    border-radius: 10px;
    border: 1px solid #e5e7eb;
    font-weight: 800;
  }
  .segBtn.active{
    background: #EAF2FF;
    border-color: #6AA9FF;
  }

  /* 에러 */
  .error{
    margin-top: 8px;
    color: #ef4444;
    font-size: 12px;
    font-weight: 800;
  }

  /* 닫기 */
  .footer{
    padding: 14px 18px;
    border-top: 1px solid #e5e7eb;
    display: flex;
    justify-content: flex-end;
  }
  .cancelBtn{
    padding: 10px 16px;
    border-radius: 12px;
    border: 1px solid #e5e7eb;
    font-weight: 800;
  }
</style>