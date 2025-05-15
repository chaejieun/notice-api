# 공지사항 관리 REST API
- 2025.05.15 알서포트 공지사항 관리 과제

## 기술 스택
**1. 백엔드** 
- Java 17
- Spring Boot 3.4.5
- Spring Data JPA
- QueryDsl 5.0
- Lombok

**2. 데이터베이스**
- H2 Database

**3. 테스트**
- JUnit5
- Mockito

**4. 빌드 도구**
- Gradle

**5. 캐시 도구**
- Redis

## 프로젝트 구조
<pre> 
src/ 
├── main/ 
│   ├── java/ 
│   │   └── com/
│   │       └── rsupport/
│   │           └── notice/
│   │               ├── config/         # 설정 클래스 및 예외처리
│   │               ├── constant/       # 공통 상수 및 열거형 정의
│   │               ├── controller/     # controller
│   │               ├── dto/            # 요청/응답 DTO 클래스 
│   │               ├── entity/         # 도메인 엔티티
│   │               ├── mapper/         # Entity-DTO 매핑 
│   │               ├── repository/     # JPA 및 QueryDSL
│   │               └── service/        # 비즈니스 로직 처리 
│   └── resources/
│       └── application.yml             # 환경 설정 파일
├── test/
│   ├── java/
│   │   └── com/
│   │       └── rsupport
│   │           └── notice/
│   │               ├── NoticeApplicationTest.java  # 통합 테스트
│   │               ├── NoticeServiceTest.java      # 단위 테스트
│   │               └── NoticeTestFactory.java      # 테스트 객체 생성용
uploads/                                # 파일첨부 업로드 경로
</pre>


## API 목록
| 기능          | HTTP 메소드 | URL                             | 설명                                           |
| ----------- | -------- | ------------------------------- |----------------------------------------------|
| 공지사항 등록     | `POST`   | `/api/notices`                  | 신규 공지사항 등록(첨부파일 미필수)                         |
| 공지사항 목록 조회  | `GET`    | `/api/notices`                  | 공지사항 목록을 조회 및 페이징합니다. 검색 조건(미필수)             |
| 공지사항 상세 조회  | `GET`    | `/api/notices/{noticeId}`       | 공지사항 상세 조회 (조회수 증가)                          |
| 관리자 공지사항 조회 | `GET`    | `/api/notices/admin/{noticeId}` | 공지사항 상세 조회 (관리자)  `X-USER-ROLE: ADMIN` 헤더 필요 |
| 공지사항 수정     | `PUT`    | `/api/notices/{noticeId}`       | 공지사항 수정 (첨부파일 변경 가능)                         |
| 공지사항 삭제     | `DELETE` | `/api/notices/{noticeId}`       | 공지사항 삭제                                  |

## API 상세 명세서
### 1. 공지사항 등록
- **URL**: `/api/notices`
- **Method**: `POST`
- **Request Body**:
 
- **noticeRegDto** (공지사항 등록 정보):
  ```json
  {
    "title": "공지사항 제목",
    "content": "공지사항 내용",
    "startDate": "2025-05-01T10:00:00",
    "endDate": "2025-05-07T10:00:00",
    "writer": "작성자명"
  }
  ```
- **attachmentList** (첨부파일, 선택 사항):
  ```json
  [
    {"file": "파일1.jpg"},
    {"file": "파일2.pdf"}
  ]

### 2. 공지사항 목록 조회
- **URL**: `/api/notices`
- **Method**: `GET`
- **Query Parameters**:
  - keyword: 검색 내용
  - searchType: TITLE, TITLE_OR_CONTENT
  - regStartDate: 검색 시작일
  - regEndDate: 검색 종료일


### 3. 공지사항 상세 조회
- **URL**: `/api/notices/{noticeId}`
- **Method**: `GET`
- **Path Variable**:
  - noticeId: 조회할 공지사항의 ID

### 4. 공지사항 상세 조회(관리자용)
- **URL**: `/api/notices/admin/{noticeId}`
- **Method**: `GET`
- **Path Variable**:
  - noticeId: 조회할 공지사항의 ID
- **Request Header**: X-USER-ROLE: 사용자 역할 (ADMIN)

### 5. 공지사항 수정
- **URL**: `/api/notices/{noticeId}`
- **Method**: `PUT`
- **Path Variable**:
  - noticeId: 조회할 공지사항의 ID
- **Request Body**:
  - **noticeModDto** (공지사항 수정 정보):
  ```json
  {
    "title": "수정된 제목",
    "content": "수정된 내용",
    "startDate": "2025-05-01T10:00:00",
    "endDate": "2025-05-07T10:00:00",
    "writer": "수정자명"
  }
  ```

### 6. 공지사항 삭제
- **URL**: `/api/notices/{noticeId}`
- **Method**: `DELETE`
- **Path Variable**:
  - noticeId: 조회할 공지사항의 ID

## API 응답 코드
- HTTP 상태 코드

| 상태코드 | 설명          | 의미        |
|------|-------------|---------------|
| 200  | OK          | 요청 정상 처리  |
| 400  | BAD_REQUEST | 클라이언트 잘못된 요청 처리|
| 403  | FORBIDDEN   | 권한이 부족하여 접근 거부|
| 404  | NOT_FOUND   | 요청한 리소스를 찾을 수 없음 |
| 405  | METHOD_NOT_ALLOWED       | 해당 리소스에 대해 요청한 HTTP 메소드가 허용되지 않음 |
| 500  | INTERNAL_SERVER_ERROR    | 서버 내부 오류로 요청을 처리할 수 없음|


- 에러 코드

| 에러코드  | 메세지                   | 설명                               |
|-------|-----------------------|----------------------------------|
| 1001  | NOTICE_NOT_FOUND     | 요청한 공지사항을 찾을 수 없음|
| 2001   | ATTACHMENT_DIR_FAILED  | 첨부파일 디렉토리 생성 또는 접근 실패|
| 2002   | ATTACHMENT_UPLOAD_FAILED | 첨부파일 업로드 오류 발생 |
| 4001   | AUTHENTICATION_FAILED | 인증 실패 또는 권한 부족|

- 에러 응답 예시
```
{
    "code": 400,
    "status": "BAD_REQUEST",
    "errorCode": 1001,
    "errorMessage": "NOTICE_NOT_FOUND",
    "data": null
}
```
### 환경 설정 안내
- `application.yml`은 테스트를 쉽게 하기 위해 포함시켰습니다.
- 해당 설정은 테스트용 H2 메모리 DB와 로컬 환경 기준으로 작성되어 있으며, 민감한 정보는 포함되어 있지 않습니다.


## 테스트 전략
- 단위 테스트: NoticeService에 대한 비즈니스 로직 테스트 (`NoticeServiceTest`)
- 통합 테스트: API 전체 흐름 검증을 위한 통합 테스트 (`NoticeApplicationTest`)

## 인증 및 권한
- 현재는 간단한 시뮬레이션 방식으로 구현하였습니다.
- 관리자 API(`GET /api/notices/admin/{id}`)는 헤더 `X-USER-ROLE: ADMIN` 값을 통해 관리자 권한을 검증합니다.


## 첨부파일 처리
- 파일은 `uploads/` 디렉토리에 저장되며, 서버 경로를 DB에 저장하여 관리


## 핵심 문제해결 전략
### 1. 페이지 기반 페이징 처리(OFFSET/LIMIT)
- QueryDSL (findByListDto 메소드)에서 사용된 `OFFSET`과 `LIMIT`을 활용하여 필요한 데이터만 조회
- `pageable.getOffset()`과 `pageable.getPageSize()`를 이용하여 한 번에 필요한 부분적인 데이터만을 조회

### 2. 공지사항 상세 조회 시, 조회수 증가 동시성 이슈 (비관적 락 적용)
- 동일한 공지사항을 여러 사용자가 동시에 조회할 경우, 조회수 증가 시점에 경쟁 조건 발생 가능
- `@Lock(PESSIMISTIC_WRITE)`를 사용하여 DB 레벨에서 락을 걸어, 조회수 증가 시 동시성 이슈를 방지
- `findWithLockByNoticeId()` 메소드로 구현

### 3. Redis 캐시를 활용한 Spring Cache 구현
- 자주 변경되지 않는 데이터는 Redis에 캐시
- `@Cacheable("noticeDto")`을 사용하여 조회 데이터 캐시,  `@CacheEvict`로 등록/수정/삭제 시 캐시 무효화 처리
