# 지하철 노선도, 경로 미션 

## 기능 요구사항 작성

### 지하철역/노선 관리 기능

1. 지하철 역 관리 API 기능 완성
   1. [예외]: 이미 등록된 지하철역 생성시 예외 처리

2. 지하철 노선 관리 API
   1. 지하철 노선 등록
      1. [예외]: 이미 등록된 노선 생성시 예외 처리
   2. 지하철 노선 목록
   3. 지하철 노선 조회
   4. 지하철 노선 수정
   5. 지하철 노선 삭제

3. 노선 기능 E2E 테스트 작성 


### 지하철 기능 구간 관리 기능
   1. 지하철 노선 API 수정
        - upStationId: 상행 종점
        - downStationId: 하행 종점
        - distance: 두 종점간의 거리

   2. 구간 관리 API
        - 노선에 구간 추가
          - 예외) 역 사이에 새로운 역을 등록할 경우 기존 역 사이 길이보다 크거나 같으면 등록을 할 수 없다.
          - 예외) 상행역과 하행역이 이미 노선에 모두 등록되어 있다면 추가할 수 없다.
          - 예외) 상행역과 하행역 둘 중 하나도 포함되어있지 않으면 추가할 수 없다.
        - 구간 정보로 상행 종점부터 하행 종점까지 역 목록 제공
        - 구간 제거
          - 예외) 구간이 하나인 노선에서 마지막 구간을 제거할 수 없다.

### 경로 조회 기능
- 지하철 노선 API 명세 수정 (추가 요금)
- 경로 조회 API 구현
  - 검색 시 최단 경로와 경로에 따른 계산된 요금 출력

### 요금 정책
- 노선별 추가 요금
  - 추가 요금이 있는 노선을 이용 할 경우, 전체 요금과 추가 요금 합산
  - 노선을 환승 하여 이용 할 경우, 가장 높은 금액의 추가 요금만 적용

- 연령별 요금 할인
  - 유아: 무료
    - 유아: 5세 이하
  - 어린이: 운임에서 350원을 공제한 금액의 50%할인
      - 어린이: 6세 이상~13세 미만
  - 청소년: 운임에서 350원을 공제한 금액의 20%할인
    - 청소년: 13세 이상~19세 미만
  - 경로 우대: 무료
    - 노인: 80세 이상
