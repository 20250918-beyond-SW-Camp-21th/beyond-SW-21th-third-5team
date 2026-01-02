<template>
  <div v-if="modal.isOpen" class="modal-backdrop" @click.self="closeAll">
    <div class="modal-card" :class="{ wide: view !== 'login' }">
      <div class="modal-header">
        <h2 class="modal-title">
          {{ viewTitle }}
        </h2>
        <button class="modal-close" @click="closeAll">✕</button>
      </div>

      <div class="modal-body">
        <!-- LOGIN -->
        <template v-if="view === 'login'">
          <input v-model.trim="loginId" class="modal-input" placeholder="로그인 ID" />
          <input v-model="password" class="modal-input" placeholder="비밀번호" type="password" />
          <button class="modal-primary" :disabled="busy" @click="submitLogin">
            {{ busy ? "처리중..." : "로그인" }}
          </button>

          <div class="modal-row">
            <button class="modal-link-inline" @click="goSignup">회원가입</button>
            <span class="dot">·</span>
            <button class="modal-link-inline" @click="goResetRequest">비밀번호 찾기</button>
          </div>
        </template>

        <!-- SIGNUP -->
        <template v-else-if="view === 'signup'">
          <input v-model.trim="signup.loginId" class="modal-input" placeholder="로그인 ID (4~20, 영문/숫자)" />
          <input v-model.trim="signup.email" class="modal-input" placeholder="이메일" />
          <input v-model="signup.password" class="modal-input" placeholder="비밀번호 (8~20)" type="password" />
          <input v-model.trim="signup.nickname" class="modal-input" placeholder="닉네임 (2~10)" />

          <button class="modal-primary" :disabled="busy" @click="submitSignup">
            {{ busy ? "처리중..." : "회원가입" }}
          </button>

          <button class="modal-link" :disabled="busy" @click="goLogin">로그인으로</button>
        </template>

        <!-- RESET (2-step) -->
        <template v-else>
          <template v-if="resetStep === 'request'">
            <input v-model.trim="reset.loginId" class="modal-input" placeholder="로그인 ID" />
            <input v-model.trim="reset.email" class="modal-input" placeholder="이메일" />

            <button class="modal-primary" :disabled="busy" @click="submitResetRequest">
              {{ busy ? "처리중..." : "재설정 코드 받기" }}
            </button>

            <button class="modal-link" :disabled="busy" @click="goLogin">로그인으로</button>
          </template>

          <template v-else>
            <input v-model.trim="reset.loginId" class="modal-input" placeholder="로그인 ID" />
            <input v-model.trim="reset.code" class="modal-input" placeholder="이메일로 받은 코드" />
            <input v-model="reset.newPassword" class="modal-input" placeholder="새 비밀번호" type="password" />

            <button class="modal-primary" :disabled="busy" @click="submitResetConfirm">
              {{ busy ? "처리중..." : "비밀번호 변경" }}
            </button>

            <button class="modal-link" :disabled="busy" @click="goLogin">로그인으로</button>
          </template>
        </template>

        <p v-if="errorMsg" class="modal-error">{{ errorMsg }}</p>
        <p v-if="infoMsg" class="modal-info">{{ infoMsg }}</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { computed, ref } from "vue";
import { useAuthModalStore } from "@/stores/authModalStore";
import { authApi } from "@/api/auth/authApi";
import { tokenStore } from "@/api/tokenStore.js";

const modal = useAuthModalStore();

/**
 * view: login | signup | reset
 * resetStep: request | confirm
 */
const view = ref("login");
const resetStep = ref("request");

const busy = ref(false);
const errorMsg = ref("");
const infoMsg = ref("");

const loginId = ref("");
const password = ref("");

const signup = ref({
  loginId: "",
  email: "",
  password: "",
  nickname: "",
});

const reset = ref({
  loginId: "",
  email: "",
  code: "",
  newPassword: "",
});

const viewTitle = computed(() => {
  if (view.value === "login") return "로그인";
  if (view.value === "signup") return "회원가입";
  return resetStep.value === "request" ? "비밀번호 찾기" : "비밀번호 재설정";
});

function clearMessages() {
  errorMsg.value = "";
  infoMsg.value = "";
}

function closeAll() {
  clearMessages();
  busy.value = false;
  view.value = "login";
  resetStep.value = "request";
  modal.close();
}

function goLogin() {
  clearMessages();
  view.value = "login";
  resetStep.value = "request";
}

function goSignup() {
  clearMessages();
  view.value = "signup";
}

function goResetRequest() {
  clearMessages();
  view.value = "reset";
  resetStep.value = "request";
}

async function submitLogin() {
  clearMessages();
  busy.value = true;
  try {
    const res = await authApi.login({ loginId: loginId.value, password: password.value });
    const data = res.data?.data;
    tokenStore.access = data.accessToken;
    tokenStore.refresh = data.refreshToken;
    infoMsg.value = "로그인 성공";
    closeAll();
  } catch (e) {
    errorMsg.value = "로그인에 실패했습니다.";
  } finally {
    busy.value = false;
  }
}

async function submitSignup() {
  clearMessages();
  busy.value = true;
  try {
    await authApi.signup({ ...signup.value });
    infoMsg.value = "회원가입이 완료되었습니다. 로그인 해주세요.";
    goLogin();
  } catch (e) {
    errorMsg.value = "회원가입에 실패했습니다.";
  } finally {
    busy.value = false;
  }
}

async function submitResetRequest() {
  clearMessages();
  busy.value = true;
  try {
    await authApi.passwordResetRequest({ loginId: reset.value.loginId, email: reset.value.email });
    infoMsg.value = "이메일로 코드를 전송했습니다. 코드를 입력해 주세요.";
    resetStep.value = "confirm";
  } catch (e) {
    // 백엔드 메시지 정책상 “정보가 맞다면 전송” 같은 응답을 주는 편이라,
    // 실패해도 사용자에겐 동일 메시지로 처리해도 됨.
    infoMsg.value = "입력하신 정보가 맞다면 이메일로 코드를 전송했습니다.";
    resetStep.value = "confirm";
  } finally {
    busy.value = false;
  }
}

async function submitResetConfirm() {
  clearMessages();
  busy.value = true;
  try {
    await authApi.passwordResetConfirm({
      loginId: reset.value.loginId,
      code: reset.value.code,
      newPassword: reset.value.newPassword,
    });
    infoMsg.value = "비밀번호가 변경되었습니다. 로그인 해주세요.";
    goLogin();
  } catch (e) {
    errorMsg.value = "비밀번호 변경에 실패했습니다.";
  } finally {
    busy.value = false;
  }
}
</script>

<style scoped>
.modal-backdrop {
  position: fixed;
  inset: 0;
  z-index: 9999;
  background: rgba(0, 0, 0, 0.4);
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 16px;
}

.modal-card {
  width: 420px;
  max-width: 100%;
  background: #fff;
  border-radius: 16px;
  padding: 24px;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.2);
}
.modal-card.wide {
  width: 520px;
}

.modal-header {
  display: flex;
  align-items: center;
  justify-content: space-between;
  margin-bottom: 16px;
}
.modal-title {
  font-size: 18px;
  font-weight: 700;
  margin: 0;
}
.modal-close {
  border: 0;
  background: transparent;
  font-size: 18px;
  cursor: pointer;
  opacity: 0.6;
}
.modal-close:hover { opacity: 1; }

.modal-body {
  display: flex;
  flex-direction: column;
  gap: 12px;
}

.modal-input {
  width: 100%;
  border: 1px solid #ddd;
  border-radius: 10px;
  padding: 10px 12px;
  outline: none;
}
.modal-input:focus { border-color: #999; }

.modal-primary {
  width: 100%;
  border: 0;
  border-radius: 10px;
  padding: 10px 12px;
  background: #111;
  color: #fff;
  cursor: pointer;
}
.modal-primary:disabled { opacity: 0.6; cursor: not-allowed; }

.modal-row {
  display: flex;
  align-items: center;
  justify-content: center;
  gap: 8px;
  margin-top: 4px;
}
.modal-link-inline {
  border: 0;
  background: transparent;
  color: #666;
  font-size: 14px;
  cursor: pointer;
}
.dot { color: #999; }

.modal-link {
  width: 100%;
  border: 0;
  background: transparent;
  color: #666;
  font-size: 14px;
  cursor: pointer;
}
.modal-link:disabled { opacity: 0.6; cursor: not-allowed; }

.modal-error {
  margin: 4px 0 0;
  color: #c62828;
  font-size: 13px;
  text-align: center;
}
.modal-info {
  margin: 4px 0 0;
  color: #2e7d32;
  font-size: 13px;
  text-align: center;
}
</style>
