# 지하철 노선도 미션
[ATDD 강의](https://edu.nextstep.camp/c/R89PYi5H) 실습을 위한 지하철 노선도 애플리케이션

### 1단계 요구사항
- [x] 개인별로 관리할 수 있는 즐겨 찾기
- [x] 인증 기반의 인수 테스트 작성 시 인증 정보는 재사용
- [x] 즐겨찾기 생성
- [x] 즐겨찾기 조회
- [x] 즐겨찾기 삭제
- [x] 예외사항
    - [x] 로그인이 필요한 API 요청 시 유효하지 않은 경우 401 Unauthorized 응답
      - [x] 내 정보 관리 / 즐겨 찾기 기능은 로그인 된 상태에서만 가능
    - [x] 내가 등록하지 않은 즐겨 찾기를 제거 하려고 할 경우
    - [x] 경로를 찾을 수 없거나 연결되지 않는 등 경로 조회가 불가능한 조회의 경우
