# 📌 프로젝트 API 명세서

## 📖 개요

- **버전**: v1.0.0
- **설명**: 이 문서는 프로젝트의 API 명세를 정리한 문서이다.
- **작성일**: 2025-04-03

---

## 🔑 인증 정보

- **인증 방식**: JWT -> 쿠키에 저장된 AccessToken과 Fingerprint 기반 처리
- **인증 헤더 예시**:
  ```http
  Authorization: Bearer {token}
  ```
- **토큰 발급 API**: `/auth/login`
- **참고**: 로그인시 가입이 안되어 있는 유저라면 가입 처리도 함께 수행

---

## 📡 공통 응답

```json
{
  "status": "success | error",
  "code": 200,
  "message": "응답 메시지",
  "data": { }
}
```

- `status`: 요청 성공 여부 (`success` or `error`)
- `message`: 응답 메시지
- `data`: 응답 본문 (성공 시 해당 데이터 포함)

---

## 🚀 API 목록

### ✅ 1. 사용자 로그인

- **URL**: `/auth/login`
- **Method**: `POST`
- **설명**: 사용자가 로그인하여 인증 토큰을 발급받는다. 가입 처리가 되어있지 않다면 payload로 전달받은 클라이언트 fingerprint로 가입 처리를 함께 수행한다.
- **요청 예시**:
  ```json
  {
    "fingerprint": "3f8d47a3a92b77e5",
  }
  ```
- **응답 예시**:
  ```json
  {
    "status": "success",
    "code": 200,
    "message": "로그인 성공",
    "data": {
      "accessToken": "eyJhbGciOiJIUzI1NiIsInR5...",
      "refreshToken": "csadqwiek12wSIDJDwqi1221..."
    }
  }
  ```

### ✅ 2. 입장한 방 조회

- **URL**: `/game/canvas/room/enterance`
- **Method**: `GET`
- **설명**: API를 요청한 사용자가 입장해있는 webcanvas-service 방의 정보를 조회한다.
- **요청 예시**:
  ```http
  GET /game/canvas/room/entrance
  Authorization: Bearer {token}
  ```
- **응답 예시**:
  ```json
  {
    "status": "success",
    "code": 200,
    "message": "입장한 방 조회 성공",
    "data": {
      "gameRoomId": 1,
      "joinCode": "UD22E3",
      "otherUsers": [
        {
          "userId": 32
        }
      ]
    }
  }
  ```
  ```json
  {
    "status": "failed",
    "code": 404,
    "message": "입장한 방을 찾을 수 없습니다.",
    "data": null
  }
  ```

### ✅ 3. 방 목록 조회

- **URL**: `/game/canvas/room`
- **Method**: `GET`
- **설명**: 요청을 보낸 유저가 입장할 수 있는 webcanvas-service 방의 목록을 조회한다.
- **요청 예시**:
  ```http
  GET /game/canvas/room
  Authorization: Bearer {token}
  ```
- **응답 예시**:
  ```json
  {
    "status": "success",
    "code": 200,
    "message": "입장 가능한 방 조회 성공",
    "data": {
      "roomList": [
        {
          "gameRoomId": 22,
          "joinCode": "UD22E3"
        }   
      ]
    }
  }
  ```
  ```json
  {
    "status": "failed",
    "code": 404,
    "message": "현재 입장 가능한 방이 없습니다.",
    "data": null
  }
  ```

### ✅ 4. 방 입장 요청

- **URL**: `/game/canvas/room/enterance`
- **Method**: `POST`
- **설명**: 방에 입장을 요청한다.
- **요청 예시**:
  ```http
    POST /game/canvas/room/enterance
    Content-Type: application/json
    Authorization: Bearer {token}
    
    {
      "joinCode": "ABC123"
    }
  ```
- **응답 예시**:
  ```json
  {
    "status": "success",
    "code": 200,
    "message": "게임 방 입장 성공",
    "data": {
      "gameRoomId": 11,
      "gameRoomEntranceId": 222,
      "otherUsers": [
        {
          "userId": 1
        }
      ]
    }
  }
  ```
  ```json
  {
    "status": "failed",
    "code": 422,
    "message": "방이 입장 가능한 상태가 아닙니다.",
    "data": null
  }
  ```
  
### ✅ 5. 방 만들기

- **URL**: `/game/canvas/room`
- **Method**: `POST`
- **설명**: 방을 생성하고 입장한다.
- **요청 예시**:
  ```http
    POST /game/canvas/room
    Content-Type: application/json
    Authorization: Bearer {token}

  ```
- **응답 예시**:
  ```json
  {
    "status": "success",
    "code": 200,
    "message": "방 만들기 성공",
    "data": [
      {
        "gameRoomId": 22,
        "gameRoomEntranceId": 1223
      }
    ]
  }
  ```
  ```json
  {
    "status": "failed",
    "code": 409,
    "message": "방을 만들 수 없는 상태입니다.",
    "data": null
  }
  ```
### ✅ 6. 방 나가기

- **URL**: `/game/canvas/room/entrance`
- **Method**: `DELETE`
- **설명**: 방에서 나간다. - 클라이언트를 강제로 종료하게 되더라도 게임 세션이 끝나기 전까지 나가지지 않음.
- **요청 예시**:
  ```http
    DELETE /game/canvas/room
    Authorization: Bearer {token}

  ```
- **응답 예시**:
  ```json
  {
    "status": "success",
    "code": 200,
    "message": "방 나가기 성공",
    "data": {
      "gameRoomId": 22
    }
  }
  ```
  ```json
  {
    "status": "failed",
    "code": 404,
    "message": "입장한 방을 찾지 못했습니다.",
    "data": null
  }
  ```
  ```json
  {
    "status": "failed",
    "code": 409,
    "message": "방을 나갈 수 없는 상태입니다.",
    "data": null
  }
  ```


### ✅ 7. 게임 시작

- **URL**: `/game/canvas/session`
- **Method**: `POST`
- **설명**: 게임 세션을 시작한다.
- **요청 예시**:
  ```http
    POST /game/canvas/session
    Content-Type: application/json
    Authorization: Bearer {token}
  ```
- **응답 예시**:
  ```json
  {
    "status": "success",
    "code": 200,
    "message": "게임 시작",
    "data": {
      "gameSessionId": 22
    }
  }
  ```
  ```json
  {
    "status": "failed",
    "code": 404,
    "message": "입장한 방을 찾지 못했습니다.",
    "data": null
  }
  ```
  ```json
  {
    "status": "failed",
    "code": 409,
    "message": "게임을 시작할 수 없는 상태입니다.",
    "data": null
  }
  ```
---

## 📌 기타 참고 사항

- 요청/응답 규격은 이후 Swagger 문서에서 상세 정의 예정
- 필요시 예외처리 및 에러 코드 명세 추가 가능

