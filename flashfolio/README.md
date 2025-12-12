# ⚡ FlashFolio - AI 포트폴리오 생성기

> **GitHub 주소만 입력하세요. 나머지는 AI가 알아서 만듭니다.**
>
> FlashFolio는 Google Gemini AI를 활용하여 GitHub 리포지토리의 README를 분석하고, 기술 면접관과 채용 담당자가 보기 좋은 전문적인 포트폴리오 웹사이트를 자동으로 생성해주는 서비스입니다.

<br>

## 📸 프로젝트 미리보기

| 메인 화면 | 포트폴리오 생성 결과 |
| :---: | :---: |
| ![Main Page](https://via.placeholder.com/600x400?text=Main+Screen) | ![Result Page](https://via.placeholder.com/600x400?text=Portfolio+Result) |
| **마이페이지 (목록 관리)** | **회원가입 & 로그인** |
| ![My Page](https://via.placeholder.com/600x400?text=My+Page) | ![Login Page](https://via.placeholder.com/600x400?text=Login+Sign+Up) |

<br>

## ✨ 주요 기능 (Key Features)

### 1. 🤖 AI 기반 포트폴리오 자동 생성
- **원클릭 분석:** 복잡한 설정 없이 GitHub 리포지토리 URL만 입력하면 됩니다.
- **Google Gemini Pro 연동:** 최신 LLM을 사용하여 README 내용을 심층 분석합니다.
- **자동 데이터 추출:**
    - 프로젝트 한 줄 요약 및 개요
    - **기술 스택 (Tech Stack)** 자동 분류 및 뱃지 생성
    - **핵심 기능 (Key Features)** 요약
    - **트러블 슈팅 (Troubleshooting)**: 개발 과정의 문제와 해결책 추출
    - **시스템 아키텍처 & 실행 방법** 가이드
- **HTML 렌더링:** 마크다운 텍스트를 깔끔한 웹 디자인으로 변환하여 보여줍니다.

### 2. 👤 사용자 관리 시스템
- **보안 인증:** Spring Security & BCrypt 암호화를 적용한 안전한 회원가입/로그인.
- **마이페이지:** 내가 생성한 포트폴리오를 영구 저장하고 언제든 다시 볼 수 있습니다.
- **관리 기능:** 불필요한 포트폴리오는 삭제할 수 있으며, 비밀번호 변경 기능을 제공합니다.

### 3. 🎨 사용자 경험 (UX/UI)
- **반응형 디자인:** Bootstrap 5를 사용하여 PC, 태블릿, 모바일 등 모든 기기에서 최적화된 화면을 제공합니다.
- **편의 기능:** 로딩 애니메이션, 실시간 유효성 검사, 직관적인 알림 메시지(Alert) 등을 통해 사용성을 높였습니다.

<br>

## 🛠 기술 스택 (Tech Stack)

### Backend
- **Java 17**
- **Spring Boot 3.4**
- **Spring Security** (인증/인가 및 CSRF 보안)
- **Spring Data JPA** (ORM)
- **H2 Database** (인메모리 DB)

### Frontend
- **Thymeleaf** (서버 사이드 렌더링)
- **Bootstrap 5** (UI 프레임워크)
- **JavaScript (ES6) & jQuery** (동적 기능 제어)
- **HTML5 / CSS3**

### AI & API
- **Google Gemini API** (Generative AI)
- **GitHub Raw Content API** (README 추출)

<br>

## 🚀 시작 가이드 (Getting Started)

### 1. 사전 요구사항
- Java 17 이상 설치
- Google Cloud Gemini API Key 발급 필요

### 2. 프로젝트 설치
```bash
git clone [https://github.com/your-username/flashfolio.git](https://github.com/your-username/flashfolio.git)
cd flashfolio
```

### 3. 환경 변수 설정
src/main/resources/application.properties 파일을 열고 본인의 API 키를 입력하세요.
```bash
# Google Gemini API Key 설정
gemini.api.key=여기에_발급받은_API_KEY_입력
gemini.api.url=[https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent](https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-preview-09-2025:generateContent)

# Database 설정 (H2)
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.h2.console.enabled=true
```

### 4. 실행
```bash
./gradlew bootRun
```

## 📂 프로젝트 구조
```bash
src/main/java/com/flashfolio
├── controller    # 웹 요청 처리 (Portfolio, User)
├── service       # 비즈니스 로직 (Gemini AI, User Security)
├── repository    # 데이터베이스 접근 (JPA)
├── entity        # DB 테이블 매핑 (User, Portfolio)
├── dto           # 데이터 전송 객체
└── config        # Spring Security 설정
```
