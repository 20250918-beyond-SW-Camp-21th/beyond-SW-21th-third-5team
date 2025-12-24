# 🚨 반드시 지킬 것

> **리니어로 이슈 생성 → 브랜치 명 복사 → 작업 시작**

## 1. 전체 패키지 구조
```
src
├─ core
│ ├─ api
│ │ ├─ <feature>
│ │ │ ├─ controller
│ │ │ ├─ scheduler
│ │ │ └─ dto
│ │ │ ├─ request
│ │ │ └─ response
│ │ └─ ...
│ │
│ ├─ domain
│ │ ├─ <feature>
│ │ │ ├─ service
│ │ │ ├─ entity
│ │ │ ├─ repository
│ │ │ └─ port
│ │ └─ ...
│ │
│ └─ support
│ ├─ response
│ ├─ error
│ │ ├─ type
│ │ └─ exception
│ └─ handler
│ └─ exception
│
└─ infra
├─ config
├─ client
└─ util
```
---

## 2. 계층별 역할 정의

### 2.1 core/api (외부 입력 계층)

**책임**

- HTTP 요청/응답 처리
- Request / Response DTO 정의
- 인증 정보 추출
- 요청 데이터 검증
- Domain 계층 호출

**규칙**

- 비즈니스 로직 작성 ❌
- Repository 직접 접근 ❌
- infra 패키지 직접 참조 ❌

---

### 2.2 core/domain (비즈니스 계층)

**책임**

- 비즈니스 규칙 처리
- 트랜잭션 관리
- 도메인 엔티티 관리
- 외부 의존성에 대한 추상화(port) 정의

**규칙**

- api DTO 사용 ❌
- HTTP, Request, Response 개념 사용 ❌
- 기술 구현 세부사항 의존 ❌

---

### 2.3 core/support (공통 지원 계층)

**책임**

- 공통 응답 포맷
- 전역 예외 처리
- 공통 에러 타입 정의

**규칙**

- 비즈니스 로직 포함 ❌
- 특정 feature 전용 코드 포함 ❌

---

### 2.4 infra (기술 구현 계층)

**책임**

- 외부 API 연동 (Feign / WebClient)
- 설정 파일
- 기술적 유틸리티 구현
- Domain port의 구현체 제공

**규칙**

- 비즈니스 규칙 작성 ❌
- api 계층 로직 포함 ❌

---

## 3. DTO 사용 규칙 (중요)

### 3.1 API DTO

- 위치: `core/api/<feature>/dto`
- 용도: HTTP 요청/응답 전용

**금지 사항**

- Domain service에서 API DTO를 파라미터로 받는 행위 ❌
- Domain service에서 API DTO를 반환하는 행위 ❌

---

### 3.2 Domain Command / Query

- Service는 **도메인 전용 객체** 또는 원시 타입을 사용한다.
- Controller에서 API DTO → Domain Command 변환 후 Service 호출한다.

---

## 4. 의존성 방향 규칙

### 허용되는 의존 방향

api → domain
infra → domain
support → (api / domain)

### 금지되는 의존 방향

domain → api ❌
domain → infra ❌
api → infra ❌

---

## 5. 예외 처리 규칙

- 모든 예외는 `core/support/error` 하위에서 정의한다.
- Controller 단에서 try-catch 사용 ❌
- 전역 예외 처리기에서 응답 변환 처리

---

## 예상되는 질문

- 비즈니스 로직이 뭔가요?
  컨트롤러가 권한/규칙을 판단하는 것을 금지하는것으로
  ex. 지금 작동한 사람이 관리자인지? 이런 판단을 전부 service에서 하는것을 의미합니다.
- Repository 직접 접근 하지말라는건 뭔가요?
  마찬가지로 데이터 무결성 유지를 위해 controller에서 트랜잭션을 잡고 서비스에서도 잡고 그런
  문제를 방지하기 위해 db에 접근하는건 오직 서비스에서만 진행합니다.
