<template>
  <div class="modalOverlay" @click.self="emitClose">
    <div class="modal">
      <div class="topBar">
        <h4 class="title">리뷰 등록</h4>

        <button
            type="button"
            class="saveBtn"
            :disabled="isSaving"
            @click="submit"
        >
          {{ isSaving ? "저장 중..." : "리뷰 저장" }}
        </button>
      </div>


      <div class="bodyGrid">

        <section class="box photoBox">
          <p class="label">사진</p>

          <label class="uploadArea">
            <input
                type="file"
                accept="image/*"
                class="fileInput"
                @change="onPickFile"
            />

            <img v-if="photoPreview" :src="photoPreview" class="photoPreview" />
            <span v-else class="uploadHint">클릭해서 사진 선택</span>
          </label>

          <p class="help">사진을 등록해 주세요.</p>
        </section>

        <section class="rightCol">

          <div class="box">
            <p class="label">날짜</p>
            <input type="date" v-model="form.date" class="input" />
          </div>


          <div class="box">
            <p class="label">만족도</p>

            <div class="segmented">
              <button
                  v-for="f in feelings"
                  :key="f.value"
                  type="button"
                  class="segBtn"
                  :class="{ active: form.satisfaction === f.value }"
                  @click="form.satisfaction = f.value"
              >
                {{ f.label }}
              </button>
            </div>
          </div>


          <div class="box reviewBox">
            <p class="label">후기</p>
            <textarea
                v-model.trim="form.outfitReview"
                class="textarea"
                rows="6"
                placeholder="후기를 입력해 주세요."
            />
            <p v-if="errorMsg" class="error">{{ errorMsg }}</p>

            <div class="hint">
              <div>PTY: {{ weatherNum ?? "-" }}</div>
              <div>TMX: {{ temperature ?? "-" }}</div>
            </div>
          </div>
        </section>
      </div>

      <div class="footer">
        <button type="button" class="cancelBtn" @click="emitClose">닫기</button>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { reactive, ref } from "vue";
import {outfitApi, OutfitSatisfaction} from "@/api/outfit/outfit";

const props = defineProps<{
  weatherNum: number | null;
  temperature: number | null;
}>();

const emit = defineEmits<{
  (e: "close"): void;
  (e: "saved"): void;
}>();

const emitClose = () => emit("close");

const isSaving = ref(false);
const errorMsg = ref("");

const photoFile = ref<File | null>(null);
const photoPreview = ref<string | null>(null);

const form = reactive({
  date: new Date().toISOString().slice(0, 10),
  satisfaction: "GOOD" as "COLD" | "GOOD" | "HOT",
  outfitReview: "",
  imageUrl: "",
});

const feelings = [
  { value: "COLD", label: "춥다" },
  { value: "GOOD", label: "좋았다" },
  { value: "HOT", label: "덥다" },
];

const onPickFile = (e: Event) => {
  const input = e.target as HTMLInputElement;
  const file = input.files?.[0];
  if (!file) return;

  photoFile.value = file;
  if (photoPreview.value) URL.revokeObjectURL(photoPreview.value);
  photoPreview.value = URL.createObjectURL(file);
};

const validate = () => {
  errorMsg.value = "";

  if (props.weatherNum === null || props.temperature === null) {
    errorMsg.value = "날씨 정보가 없습니다. (PTY/TMX)";
    return false;
  }

  if (!form.date) {
    errorMsg.value = "날짜를 선택해 주세요.";
    return false;
  }

  if (!form.outfitReview.trim()) {
    errorMsg.value = "후기를 입력해 주세요.";
    return false;
  }

  if (!photoFile.value) {
    errorMsg.value = "사진을 선택해 주세요.";
    return false;
  }

  return true;
};

const submit = async () => {
  if (!validate()) return;

  isSaving.value = true;
  try {
    await outfitApi.saveOutfit({
      weatherNum: props.weatherNum,
      temperature: props.temperature,
      imageUrl: photoPreview.value ?? "",
      outfitReview: form.outfitReview,
      date: form.date,
      satisfaction: form.satisfaction as OutfitSatisfaction,
    });

    emit("saved");
    emit("close");
  } catch (e: any) {
    errorMsg.value = e?.response?.data?.message || e?.message || "저장 실패";
  } finally {
    isSaving.value = false;
  }
};

</script>

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

.modal{
  width: min(980px, 100%);
  background: #fff;
  border-radius: 18px;
  overflow: hidden;
}

.topBar{
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 16px 18px;
  border-bottom: 1px solid #e5e7eb;
}
.title{
  font-size: 18px;
  font-weight: 900;
  color: #111827;
}
.saveBtn{
  padding: 10px 16px;
  border-radius: 12px;
  background: #6AA9FF;
  color: #fff;
  font-weight: 900;
}
.saveBtn:disabled{ opacity: 0.6; cursor: not-allowed; }

.bodyGrid{
  display: grid;
  grid-template-columns: 360px 1fr;
  gap: 18px;
  padding: 18px;
}

.box{
  border: 1px solid #e5e7eb;
  border-radius: 14px;
  padding: 14px;
  background: #fff;
}
.label{
  font-size: 13px;
  font-weight: 900;
  margin-bottom: 8px;
  color: #111827;
}

.photoBox{
  min-height: 520px;
  display: flex;
  flex-direction: column;
}
.uploadArea{
  flex: 1;
  border: 1px dashed #cbd5e1;
  border-radius: 12px;
  background: #f8fafc;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  overflow: hidden;
}
.fileInput{ display: none; }
.uploadHint{ color: #64748b; font-weight: 800; }
.photoPreview{
  width: 100%;
  height: 100%;
  object-fit: cover;
}
.help{
  margin-top: 8px;
  font-size: 12px;
  color: #6b7280;
  font-weight: 700;
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
.textarea{ resize: vertical; }

.segmented{
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 10px;
}
.segBtn{
  padding: 10px;
  border-radius: 10px;
  border: 1px solid #e5e7eb;
  font-weight: 900;
  background: #fff;
}
.segBtn.active{
  background: #EAF2FF;
  border-color: #6AA9FF;
}

.error{
  margin-top: 8px;
  color: #ef4444;
  font-size: 12px;
  font-weight: 900;
}

.hint{
  margin-top: 10px;
  font-size: 12px;
  color: #6b7280;
  font-weight: 700;
}

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
  background: #fff;
  font-weight: 900;
}
</style>
