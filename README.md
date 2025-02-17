# 사용자 레시피 공유와 AI 기반 레시피 생성 플랫폼

사용자 레시피 공유와 AI기반 레시피 생성 기능을 결합한 플랫폼입니다.
사용자는 자신의 레시피를 공유하거나, AI가 제공하는 창의적인 레시피를 활용할 수 있습니다.

---
## 주소 : http://15.164.64.144:8080  

## swagger 주소 : http://15.164.64.144:8080/swagger-ui/index.html  

## api 요청 전
http://15.164.64.144:8080/oauth2/authorization/naver 로 요청 후 naver 로그인 -> jwt 토큰과 "/"로 redirect -> "/" 에서 jwt 토큰 리턴
-> 모든 api 요청 전 Authorization 헤더에 Bearer <jwt 토큰> 넣어서 요청  

## google 로그인은 배포 시 도메인을 구매해야지만 사용 가능해서 제외(로컬일 때 사용하던 코드는 있음) naver로그인 만 지원  

## 미리 레시피, 태그 데이터 좀 넣어놨습니다.
## ERD
![Image](https://github.com/user-attachments/assets/5b0e1086-6c9a-4904-ad37-51a6752d6330)
---

## 주요 기능

### 1. **태그 기반 시스템**
- **태그 종류**:
  - **재료**: 사용되는 재료들
  - **조리 시간**: 10분, 20분, 30분, 1시간 등
  - **조리 난이도**: 쉬움, 보통, 어려움 등
  - **음식 스타일**: 한식, 양식, 중식, 디저트 등
  - **맛**: 매운맛, 단맛, 짠맛, 신맛, 쓴맛 등
  - 기타 다양한 태그 사용자들이 추가 가능
- **특징**:
  - 태그만 저장하는 기능은 없음
  - 레시피 게시글, ai 레시피 생성 시 사용한 태그 자동 저장(사용자가 임의로 만든 태그를 레시피 작성 시 넣으면 자동 생성)
  - 태그는 자동 완성 기능으로 입력 및 검색 지원.(redis 사용)
  - 각 레시피에 여러 태그 추가 가능.
  - 전체 유저를 기준으로 자주 사용되는 태그는 우선적으로 표시.
  - AI 레시피, 레시피 게시글에 적용된 태그는 태그 자동완성, 검색 조건으로 활용

---

### 2. **레시피 커뮤니티**
- **게시글 CRUD**:
  - 사용자가 자신의 레시피를 게시글로 작성 가능.
  - 생성 시 제목, 레시피명, 레시피, 태그, 하고 싶은 말 등 입력
  - 게시글 작성 시 사용한 태그들 자동 저장(자동완성, 검색에 사용됨)
- **좋아요 기능**:
  - 레시피가 마음에 들면 좋아요를 누를 수 있음.
- **코멘트 기능**:
   - 간단한 피드백 및 질문을 위한 코멘트 CRUD 지원.
   - 원댓글과 그 외(대댓글 이하)로 구분
   - 대대댓글 부터는 부모 댓글의 작성자 보이도록(유튜브 댓글 스타일)
- **게시글 검색 및 정렬**:
  - 작성할 때 넣은 태그 및 키워드로 검색 가능.
  - 정렬 조건:
    - 좋아요 순
    - 최신 순
    - 내림차순/오름차순 선택 가능.

---

### 3. **AI 활용 레시피 생성**
- **사용 시점**:
  - 기존 레시피에서 마음에 드는 결과가 없거나 맞춤형 레시피를 원하는 경우.
- **OpenAI API 활용**:
  - 다양한 태그(재료, 조리 시간, 난이도 등), 창의성, 추가 세부 사항 등을 입력하여 개인화된 레시피 생성.
  - 레시피 생성 시 사용한 태그들 자동 저장(자동완성, 검색에 사용됨)
- **레시피 관리**:
  - 생성된 레시피, 레시피명, 요청 시 사용한 파라미터(태그, 창의성, 추가 세부사항)들은 DB에 저장.
  - 생성자는 생성된 레시피를 조회, 수정, 삭제 가능.
- **공개 범위**:
  - 기본적으로 본인만 조회 가능.
  - 커뮤니티에 공유하려면 별도로 게시글로 등록해야 함.
  - 게시글로 등록할 때 : AI레시피와 게시글 둘다 가지고 있는 필드 -> name(레시피명), tag, content(레시피)는  게시글 작성 시 그대로 사용 가능, 게시글만 가지고 있는 필드 title, bonus 등은 자유롭게 작성

---

### 4. **태그 자동 완성**
- **구현 방식**:
  - redis 활용하여 입력 중인 텍스트를 접두어로 가지는 태그를 표시.(최대 5개 표시)
- **정렬 기준**:
  - 전체 유저를 기준으로 자주 사용되는 태그 순서로 정렬.

---

### 5. **개인 페이지**
- 사용자 개인 대시보드 제공:
  - 본인 정보 조회
  - 탈퇴
  - 생성한 게시글 조회.
  - AI로 생성한 레시피 조회.
  - 좋아요를 표시한 게시글 조회.

---

### 6. **회원가입 및 로그인**
- OAuth 2.0 및 JWT를 사용하여 회원가입 및 로그인 구현.

---
# API
## 모든 API는 호출 전 Authorization header에 Bearer <jwt토큰> 형식으로 추가하고 요청해야 동작
## 1. **유저 관련**
### 1-1. **본인 정보 조회**
@GetMapping("user")
### 1-2. **탈퇴**
@DeleteMapping("user")
## 2. **태그 관련**
### 2-1. **태그 수정**
@PutMapping("/tag")
- **입력 데이터**:
  - `tag`: 원래 태그명 String
  - `change`: 바꿀 태그명 String
### 2-2. **태그 삭제**
@DeleteMapping("/tag")
- **입력 데이터**:
  - `tags`: 삭제할 태그명 리스트 List<String>
### 2-3. **태그 조회**
@GetMapping("/tag")
- **입력 데이터**:
  - `pageable`: pageable 객체
## 3. **AI 레시피 관련**
### 3-1. **AI 레시피 생성**
`@PostMapping("/airecipe")`
- **입력 데이터**:
  - `tags`: 레시피 생성에 사용할 태그명 리스트 List<String>
  - `temperature`: 창의도(0.0~1.0) double
  - `extraDetails`: 추가 요구 사항 String
### 3-2. **AI 레시피 수정**
`@PutMapping("/airecipe/{id}")`
- **입력 데이터**:
  - '{id}': AI 레시피 id Long
  - `tags`: 수정할 태그명 리스트 List<String>
  - `name`: 레시피명 String
  - `content`: 레시피 설명, 조리 순서 등 String
### 3-3. **AI 레시피 삭제**
`@DeleteMapping("/airecipe/{id}")`
- **입력 데이터**:
  - `{id}`: AI 레시피 id Long
### 3-4. **AI 레시피 조회**
`@PostMapping("/airecipe/list")`
- **입력 데이터**:
  - `tags`: 검색에 사용할 태그명 리스트 List<String>
  - `keyword`: 검색 키워드 String
  - `upper`: 오름차순, 내림차순 boolean
### 3-5. **AI 레시피 세부 조회**
`@GetMapping("/airecipe/{id}")`
- **입력 데이터**:
  - `{id}`: AI 레시피 id Long
## 4. **레시피 게시글 관련**
### 4-1. **레시피 게시글 생성**
`@PostMapping("/recipe")`
- **입력 데이터**:
  - 'title': 게시글 제목 String
  - `name`: 레시피명 String
  - `tags`: 적용될 태그명 리스트 List<String>
  - `content`: 레시피 설명, 조리 순서 등 String
  - `bonus`: 추가로 하고 싶은 말 String
### 4-2. **레시피 게시글 수정**
`@PutMapping("/recipe/{id}")`
- **입력 데이터**:
  - 'title': 수정할 게시글 제목 String
  - `name`: 수정할 레시피명 String
  - `tags`: 수정할 적용될 태그명 리스트 List<String>
  - `content`: 수정할 레시피 설명, 조리 순서 등 String
  - `bonus`: 수정할 추가로 하고 싶은 말 String 
### 4-3. **레시피 게시글 삭제**
`@DeleteMapping("/recipe/{id}")`
- **입력 데이터**:
  - '{id}': 레시피 게시글 id Long
### 4-4. **레시피 게시글 조회**
`@PostMapping("/recipe/list")`
- **입력 데이터**:
  - 'tags': 검색 조건 태그명 리스트 List<String>
  - `keyword`: 검색 키워드 String
  - `sortBy`: 정렬 조건(LIKES(좋아요 순), LATEST(최신 순)) enum 
  - `upper`: 내림차순, 오름차순 boolean
### 4-5. **레시피 게시글 세부 조회**
`@GetMapping("/recipe/{id}")`
- **입력 데이터**:
  - '{id}': 레시피 게시글 id Long
### 4-6. **본인이 작성한 레시피 게시글 조회**
`@GetMapping("/recipe/own")`
- **입력 데이터**:
  - 'pageable': pageable 객체
### 4-7. **본인이 좋아요 누른 레시피 게시글 조회**
`@GetMapping("/recipe/likes")`
- **입력 데이터**:
  - 'pageable': pageable 객체
## 5. **좋아요 관련**
### 5-1. **좋아요**
`@PostMapping("/like/{id}")`
- **입력 데이터**:
  - '{id}': 레시피 게시글 id Long
### 5-2. **좋아요 취소**
`@DeleteMapping("/like/{id}")`
- **입력 데이터**:
  - '{id}': 레시피 게시글 id Long
## 6. **댓글 관련**
### 6-1. **원댓글 생성**
`@PostMapping("/comment/{id}")`
- **입력 데이터**:
  - '{id}': 레시피 게시글 id Long
  - `content`: 댓글 내용 String
### 6-2. **대댓글 이하 생성**
`@PostMapping("/replyComment/{parentId}")`
- **입력 데이터**:
  - '{parentId}': 부모 댓글 id Long
  - `content`: 댓글 내용 String
### 6-3. **해당 레시피 댓글 조회**
`@GetMapping("/comment/{id}")`
- **입력 데이터**:
  - '{id}': 레시피 게시글 id Long
  - `pageabke`: pageable 객체
### 6-4. **댓글 수정**
`@PutMapping("/comment/{id}")`
- **입력 데이터**:
  - '{id}': 댓글 id Long
  - `content`: 수정할 댓글 내용 String
### 6-5. **댓글 삭제**
`@DeleteMapping("/comment/{id}")`
- **입력 데이터**:
  - '{id}': 댓글 id Long
## 7. **자동완성 관련**
### 7-1. **자동완성**
`@GetMapping("/autocomplete")`
- **입력 데이터**:
  - 'name': 태그 접두어 String
---

## tech stack
- Spring Boot
- Mysql, Redis
- OAuth 2.0 + JWT
- OpenAI api ->  명세 https://platform.openai.com/docs/api-reference/chat
- kafka
- docker
- aws
