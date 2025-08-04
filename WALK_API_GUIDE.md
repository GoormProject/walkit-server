# 🚶‍♂️ 산책기록 API 흐름 가이드

## 📋 개요

이 문서는 Walkit 서비스의 산책기록 관련 API 사용법과 데이터 흐름을 설명합니다.

## 🔄 전체 산책 흐름

```
1. 산책 시작 → 2. 산책 진행 → 3. 산책 종료 → 4. 산책 기록 저장
```

## 📡 API 엔드포인트

### **1. 산책 시작**
```http
POST /api/walks/start
Authorization: Bearer {JWT_TOKEN}
```

**응답:**
```json
{
  "status": "SUCCESS",
  "message": "산책 기록 시작 성공",
  "data": {
    "walkId": 18,
    "eventId": 12,
    "eventType": "START",
    "eventTime": "2025-08-04T11:55:08"
  }
}
```

**중요:** `walkId`와 `eventId`를 저장해두세요!

### **2. 산책 일시정지**
```http
PUT /api/walks/{walkId}/pause
Authorization: Bearer {JWT_TOKEN}
```

**응답:**
```json
{
  "status": "SUCCESS",
  "message": "산책 기록 일시정지 성공",
  "data": {
    "walkId": 18,
    "eventId": 13,
    "eventType": "PAUSE",
    "eventTime": "2025-08-04T12:00:00"
  }
}
```

### **3. 산책 재개**
```http
PUT /api/walks/{walkId}/resume
Authorization: Bearer {JWT_TOKEN}
```

**응답:**
```json
{
  "status": "SUCCESS",
  "message": "산책 기록 재개 성공",
  "data": {
    "walkId": 18,
    "eventId": 14,
    "eventType": "RESUME",
    "eventTime": "2025-08-04T12:05:00"
  }
}
```

### **4. 산책 종료**
```http
PUT /api/walks/{walkId}/end
Authorization: Bearer {JWT_TOKEN}
```

**응답:**
```json
{
  "status": "SUCCESS",
  "message": "산책 기록 종료 성공",
  "data": {
    "walkId": 18,
    "eventId": 15,
    "eventType": "END",
    "eventTime": "2025-08-04T12:30:00"
  }
}
```

### **5. 산책 기록 저장** ⭐ **가장 중요**
```http
POST /api/walks/new
Authorization: Bearer {JWT_TOKEN}
Content-Type: application/json
```

**요청 데이터:**
```json
{
  "walkId": 18,
  "eventId": 15,
  "eventType": "END",
  "walkTitle": "2025-08-04의 산책",
  "totalTime": 2100,
  "totalDistance": 2.5,
  "pace": 4.2,
  "path": [
    [126.986, 37.541],
    [126.987, 37.542],
    [126.988, 37.543]
  ],
  "startPoint": [126.986, 37.541],
  "routeUrl": "https://example.com/route-image.jpg"
}
```

**중요 필드 설명:**
- `walkId`: 산책 시작 시 받은 ID
- `eventId`: **마지막 이벤트의 ID** (보통 END 이벤트)
- `totalTime`: **초 단위** (분이 아님!)
- `totalDistance`: **킬로미터 단위**
- `pace`: **분/킬로미터 단위**
- `path`: GPS 좌표 배열 `[경도, 위도]`
- `startPoint`: 시작점 `[경도, 위도]`

## ⚠️ 주의사항

### **1. 시간 계산**
```javascript
// ❌ 잘못된 예시
const totalTime = 30; // 30분

// ✅ 올바른 예시
const totalTime = 30 * 60; // 30분을 초로 변환
```

### **2. 거리 계산**
```javascript
// ❌ 잘못된 예시
const totalDistance = 2500; // 2500미터

// ✅ 올바른 예시
const totalDistance = 2.5; // 2.5킬로미터
```

### **3. 속도 계산**
```javascript
// ❌ 잘못된 예시
const pace = 4.2; // m/s

// ✅ 올바른 예시
const pace = 12.0; // 12분/킬로미터
```

### **4. eventId 관리**
```javascript
// ✅ 올바른 예시
let currentWalkId = null;
let currentEventId = null;

// 산책 시작 시
const startResponse = await startWalk();
currentWalkId = startResponse.data.walkId;
currentEventId = startResponse.data.eventId;

// 산책 종료 시
const endResponse = await endWalk(currentWalkId);
currentEventId = endResponse.data.eventId; // 업데이트!

// 산책 기록 저장 시
await createWalk({
  walkId: currentWalkId,
  eventId: currentEventId, // 최신 eventId 사용
  // ... 기타 데이터
});
```

## 🔧 프론트엔드 구현 예시

### **산책 상태 관리**
```javascript
class WalkManager {
  constructor() {
    this.walkId = null;
    this.eventId = null;
    this.isWalking = false;
    this.startTime = null;
    this.path = [];
  }

  async startWalk() {
    try {
      const response = await fetch('/api/walks/start', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${getToken()}`,
          'Content-Type': 'application/json'
        }
      });
      
      const data = await response.json();
      this.walkId = data.data.walkId;
      this.eventId = data.data.eventId;
      this.isWalking = true;
      this.startTime = new Date();
      
      return data;
    } catch (error) {
      console.error('산책 시작 실패:', error);
      throw error;
    }
  }

  async endWalk() {
    if (!this.walkId) throw new Error('산책이 시작되지 않았습니다');
    
    try {
      const response = await fetch(`/api/walks/${this.walkId}/end`, {
        method: 'PUT',
        headers: {
          'Authorization': `Bearer ${getToken()}`,
          'Content-Type': 'application/json'
        }
      });
      
      const data = await response.json();
      this.eventId = data.data.eventId; // eventId 업데이트!
      this.isWalking = false;
      
      return data;
    } catch (error) {
      console.error('산책 종료 실패:', error);
      throw error;
    }
  }

  async saveWalk(walkTitle, routeUrl) {
    if (!this.walkId || !this.eventId) {
      throw new Error('산책 정보가 없습니다');
    }

    const endTime = new Date();
    const totalTimeSeconds = Math.floor((endTime - this.startTime) / 1000);
    
    // 거리 계산 (실제 GPS 데이터 기반)
    const totalDistance = this.calculateDistance();
    
    // 속도 계산 (분/킬로미터)
    const pace = totalDistance > 0 ? (totalTimeSeconds / 60) / totalDistance : 0;

    const walkData = {
      walkId: this.walkId,
      eventId: this.eventId,
      eventType: "END",
      walkTitle: walkTitle,
      totalTime: totalTimeSeconds, // 초 단위!
      totalDistance: totalDistance, // 킬로미터!
      pace: pace, // 분/킬로미터!
      path: this.path,
      startPoint: this.path[0] || [0, 0],
      routeUrl: routeUrl
    };

    try {
      const response = await fetch('/api/walks/new', {
        method: 'POST',
        headers: {
          'Authorization': `Bearer ${getToken()}`,
          'Content-Type': 'application/json'
        },
        body: JSON.stringify(walkData)
      });
      
      return await response.json();
    } catch (error) {
      console.error('산책 기록 저장 실패:', error);
      throw error;
    }
  }

  calculateDistance() {
    // GPS 좌표 기반 거리 계산 로직
    // Haversine 공식 사용 권장
    return 2.5; // 예시 값
  }
}
```

## 🚨 자주 발생하는 에러

### **1. 400 Bad Request**
- `totalTime`이 초 단위가 아닌 경우
- `totalDistance`가 킬로미터 단위가 아닌 경우
- `eventId`가 잘못된 경우

### **2. "유효하지 않은 산책 세션입니다"**
- `eventId`가 최신 세션과 일치하지 않는 경우
- 산책이 이미 종료된 경우

### **3. 외래키 제약조건 위반**
- `walkId`가 존재하지 않는 경우
- 산책이 시작되지 않은 상태에서 종료/저장 시도

## 📝 체크리스트

- [ ] 산책 시작 시 `walkId`와 `eventId` 저장
- [ ] 각 이벤트 후 `eventId` 업데이트
- [ ] `totalTime`을 초 단위로 변환
- [ ] `totalDistance`를 킬로미터 단위로 변환
- [ ] `pace`를 분/킬로미터 단위로 계산
- [ ] GPS 좌표를 `[경도, 위도]` 형식으로 저장
- [ ] 최신 `eventId`로 산책 기록 저장

## 🔗 관련 문서

- [WalkController.java](./src/main/java/life/walkit/server/walk/controller/WalkController.java)
- [WalkService.java](./src/main/java/life/walkit/server/walk/service/WalkService.java)
- [WalkRequest.java](./src/main/java/life/walkit/server/walk/dto/request/WalkRequest.java) 
