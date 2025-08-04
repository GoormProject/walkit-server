# 🐛 Fix: Walk Entity Null Constraint Violation

## 📋 문제 상황

Walk 엔티티에서 null 값으로 인한 데이터베이스 제약조건 위반 에러가 발생했습니다.

### 발생한 에러들:
1. **pace 컬럼 null 제약조건 위반**
   ```
   ERROR: null value in column "pace" of relation "walk" violates not-null constraint
   ```

2. **total_distance 컬럼 null 제약조건 위반**
   ```
   ERROR: null value in column "total_distance" of relation "walk" violates not-null constraint
   ```

## 🔧 수정 내용

### 1. Walk 엔티티 수정 (`src/main/java/life/walkit/server/walk/entity/Walk.java`)

#### 변경사항:
- **클래스 레벨 `@Builder` 어노테이션 제거**: Lombok 자동 생성 빌더와 충돌 방지
- **수동 생성자 추가**: `@Builder` 어노테이션이 있는 명시적 생성자 정의
- **필드 제약조건 수정**:
  - `totalDistance`: `nullable = false` 추가
  - `totalTime`: `nullable = false` 추가
  - `pace`: `@Builder.Default` 제거, 생성자에서 기본값 처리
- **기본값 처리**: 생성자에서 null 값을 적절한 기본값으로 변환
  - `pace`: null → 0.0
  - `isUploaded`: null → false

#### 코드 변경:
```java
// Before
@Builder
@Column(name = "total_distance")
private Double totalDistance;

@Column(name = "total_time")
private Duration totalTime;

@Column(name = "pace", nullable = false)
@Builder.Default
private Double pace = 0.0;

// After
@Column(name = "total_distance", nullable = false)
private Double totalDistance;

@Column(name = "total_time", nullable = false)
private Duration totalTime;

@Column(name = "pace", nullable = false)
private Double pace;

@Builder
public Walk(Member member, Trail trail, Path path, String walkTitle,
           Double totalDistance, Duration totalTime, Double pace, Boolean isUploaded) {
    // ... 생성자 로직
    this.pace = pace != null ? pace : 0.0;
    this.isUploaded = isUploaded != null ? isUploaded : false;
}
```

### 2. WalkService 수정 (`src/main/java/life/walkit/server/walk/service/WalkService.java`)

#### 변경사항:
- **startWalk() 메서드 수정**: null 값 대신 기본값 설정
  - `totalDistance`: null → 0.0
  - `totalTime`: null → Duration.ZERO

#### 코드 변경:
```java
// Before
Walk.builder()
    .totalDistance(null)
    .totalTime(null)
    .build()

// After
Walk.builder()
    .totalDistance(0.0)
    .totalTime(Duration.ZERO)
    .build()
```

## 🎯 해결된 문제

1. **컴파일 에러 해결**: Lombok `@Builder`와 수동 생성자 간 충돌 해결
2. **데이터베이스 제약조건 위반 해결**: null 값 삽입으로 인한 에러 방지
3. **일관성 있는 기본값 처리**: 엔티티와 서비스 레벨에서 일관된 기본값 적용

## 🧪 테스트 결과

- ✅ 컴파일 성공
- ✅ 서버 정상 시작 (8080 포트)
- ✅ 데이터베이스 연결 성공
- ✅ Walk 엔티티 생성 시 null 제약조건 위반 없음

## 📝 관련 이슈

- **Issue**: #76 - Walk null exception
- **Branch**: `bug/#76-walk-null-exception`

## 🔍 추가 고려사항

- 산책 시작 시 기본값(0.0, Duration.ZERO)으로 설정하는 것이 비즈니스 로직상 적절한지 검토 필요
- 실제 산책 데이터가 업데이트될 때 올바른 값으로 교체되는지 확인 필요 
