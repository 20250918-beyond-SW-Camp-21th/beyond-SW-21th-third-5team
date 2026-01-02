<template>
  <div class="min-h-screen bg-gradient-to-b from-[#F6FAFF] to-[#FFFFFF] flex items-center justify-center px-20 py-12">
    <div class="w-full max-w-[1100px] grid grid-cols-2 gap-8">
      <div class="bg-gradient-to-br from-[#F0F8FF] to-[#E6F3FF] rounded-[28px] p-12 shadow-lg shadow-blue-100/50 flex flex-col justify-center items-center relative overflow-hidden">
        <div class="absolute top-0 right-0 w-64 h-64 bg-white/30 rounded-full blur-3xl" />
        <div class="absolute bottom-0 left-0 w-48 h-48 bg-white/40 rounded-full blur-2xl" />

        <div class="relative z-10 mb-8">
          <img
            src="https://images.unsplash.com/photo-1638641569078-bc67cfbcc972?crop=entropy&cs=tinysrgb&fit=max&fm=jpg&q=80&w=1080"
            alt="펭귄 캐릭터"
            class="w-80 h-80 object-contain drop-shadow-2xl"
          />
        </div>

        <div class="relative z-10 text-center">
          <h2 class="text-[#1F2A37] text-2xl mb-3">기록이 쌓일수록,<br />나만의 옷차림이 똑똑해져요.</h2>
          <p class="text-[#6B7280]">날씨와 체감 온도를 기록하고<br />나에게 딱 맞는 옷차림을 찾아보세요</p>
        </div>
      </div>

      <div class="bg-white rounded-[28px] p-12 shadow-lg shadow-blue-100/50">
        <div class="flex gap-2 bg-[#F6FAFF] p-1 rounded-2xl mb-8">
          <button
            type="button"
            class="flex-1 py-3 rounded-xl transition-all"
            :class="mode === 'login' ? 'bg-white text-[#1F2A37] font-semibold shadow-sm' : 'text-[#6B7280]'"
            @click="mode = 'login'"
          >
            로그인
          </button>
          <button
            type="button"
            class="flex-1 py-3 rounded-xl transition-all"
            :class="mode === 'signup' ? 'bg-white text-[#1F2A37] font-semibold shadow-sm' : 'text-[#6B7280]'"
            @click="mode = 'signup'"
          >
            회원가입
          </button>
        </div>

        <div v-if="mode === 'login'">
          <div class="space-y-4 mb-6">
            <div>
              <label class="block text-[#1F2A37] mb-2">이메일</label>
              <input type="email" placeholder="이메일을 입력하세요" class="w-full px-4 py-3 bg-[#F6FAFF] border border-[#E6EEF9] rounded-xl focus:outline-none focus:ring-2 focus:ring-[#6AA9FF] placeholder:text-[#6B7280]" />
            </div>
            <div>
              <label class="block text-[#1F2A37] mb-2">비밀번호</label>
              <input type="password" placeholder="비밀번호를 입력하세요" class="w-full px-4 py-3 bg-[#F6FAFF] border border-[#E6EEF9] rounded-xl focus:outline-none focus:ring-2 focus:ring-[#6AA9FF] placeholder:text-[#6B7280]" />
            </div>
          </div>

          <div class="flex items-center mb-6">
            <input type="checkbox" id="remember" class="w-5 h-5 rounded border-[#E6EEF9] text-[#6AA9FF] focus:ring-[#6AA9FF]" />
            <label for="remember" class="ml-2 text-sm text-[#6B7280]">로그인 상태 유지</label>
          </div>

          <button class="w-full bg-[#6AA9FF] text-white py-4 rounded-2xl hover:bg-[#5B98EF] transition-all shadow-lg shadow-blue-200/50 font-semibold mb-6">
            로그인
          </button>

          <div class="relative mb-6">
            <div class="absolute inset-0 flex items-center"><div class="w-full border-t border-[#E6EEF9]" /></div>
            <div class="relative flex justify-center"><span class="bg-white px-4 text-sm text-[#6B7280]">또는</span></div>
          </div>

          <div class="space-y-3 mb-6">
            <button class="w-full bg-white border-2 border-[#E6EEF9] text-[#1F2A37] py-3 rounded-2xl hover:bg-[#F6FAFF] transition-all font-medium flex items-center justify-center gap-2">
              <span class="inline-block w-5 h-5 bg-gradient-to-br from-red-500 to-yellow-500 rounded-full" />
              Google로 계속하기
            </button>
            <button class="w-full bg-[#FEE500] text-[#1F2A37] py-3 rounded-2xl hover:bg-[#FDDC00] transition-all font-medium flex items-center justify-center gap-2">
              <span class="inline-block w-5 h-5 bg-[#1F2A37] rounded-full" />
              Kakao로 계속하기
            </button>
          </div>

          <div class="text-center">
            <button class="text-sm text-[#6AA9FF] hover:underline">비밀번호를 잊으셨나요?</button>
          </div>
        </div>

        <div v-else>
          <div class="space-y-4 mb-6">
            <div>
              <label class="block text-[#1F2A37] mb-2">닉네임</label>
              <input type="text" placeholder="닉네임을 입력하세요" class="w-full px-4 py-3 bg-[#F6FAFF] border border-[#E6EEF9] rounded-xl focus:outline-none focus:ring-2 focus:ring-[#6AA9FF] placeholder:text-[#6B7280]" />
            </div>
            <div>
              <label class="block text-[#1F2A37] mb-2">이메일</label>
              <input type="email" placeholder="이메일을 입력하세요" class="w-full px-4 py-3 bg-[#F6FAFF] border border-[#E6EEF9] rounded-xl focus:outline-none focus:ring-2 focus:ring-[#6AA9FF] placeholder:text-[#6B7280]" />
            </div>
            <div>
              <label class="block text-[#1F2A37] mb-2">비밀번호</label>
              <input type="password" placeholder="비밀번호를 입력하세요" class="w-full px-4 py-3 bg-[#F6FAFF] border border-[#E6EEF9] rounded-xl focus:outline-none focus:ring-2 focus:ring-[#6AA9FF] placeholder:text-[#6B7280]" />
            </div>
            <div>
              <label class="block text-[#1F2A37] mb-2">비밀번호 확인</label>
              <input type="password" placeholder="비밀번호를 다시 입력하세요" class="w-full px-4 py-3 bg-[#F6FAFF] border border-[#E6EEF9] rounded-xl focus:outline-none focus:ring-2 focus:ring-[#6AA9FF] placeholder:text-[#6B7280]" />
            </div>
          </div>

          <button class="w-full bg-[#FF9BCB] text-white py-4 rounded-2xl hover:bg-[#FF7FB3] transition-all shadow-lg shadow-pink-200/50 font-semibold mb-6">
            회원가입
          </button>

          <div class="relative mb-6">
            <div class="absolute inset-0 flex items-center"><div class="w-full border-t border-[#E6EEF9]" /></div>
            <div class="relative flex justify-center"><span class="bg-white px-4 text-sm text-[#6B7280]">또는</span></div>
          </div>

          <div class="space-y-3">
            <button class="w-full bg-white border-2 border-[#E6EEF9] text-[#1F2A37] py-3 rounded-2xl hover:bg-[#F6FAFF] transition-all font-medium flex items-center justify-center gap-2">
              <span class="inline-block w-5 h-5 bg-gradient-to-br from-red-500 to-yellow-500 rounded-full" />
              Google로 계속하기
            </button>
            <button class="w-full bg-[#FEE500] text-[#1F2A37] py-3 rounded-2xl hover:bg-[#FDDC00] transition-all font-medium flex items-center justify-center gap-2">
              <span class="inline-block w-5 h-5 bg-[#1F2A37] rounded-full" />
              Kakao로 계속하기
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref } from 'vue';

const mode = ref<'login' | 'signup'>('login');
</script>
