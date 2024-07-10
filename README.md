# robomi_frontend

---

### 레파지터리 이용 방식 및 환경 구성 작성 안내

* main 브랜치에 작동하는 소스 상태 유지 필요

* 별도의 환경 설정이 따로 필요한 경우, 간단히 1줄 제목 단위로라도 언급

---

### 프로그램 구현 내 포함 기능

* 통신 처리: 웹소켓

* 영상 처리

* 음성 처리

---

### 환경 구성

* 서버 연동 필요 (소스 코드 중 시험 서버 IP 수정 필요)

* 기본 환경: android-studio, jdk-21

* 부가 환경: 

* 기타 참고: 

---

### Base Code 05-31 기준

화면 분류 기준

- 메인 화면
    - 로그인
    - 관리자 로그인
- 로그인 화면
    - Front 카메라 실행
- 알림 화면
    - 알림 리스트뷰
    - 실시간 영상보기 버튼
- 영상보기 화면
    - 음성전송 버튼(버튼만)
    - 영상 출력 TextureView
- 관리자 등록 화면
    - Front 카메라 실행
    - 촬영 버튼(버튼만)
    - 이름입력
    - 등록 버튼(버튼만)
- 전시물 등록 화면(관리자 등록화면과 같은 Resource)
    - Front 카메라 실행
    - 촬영 버튼(버튼만)
    - 이름입력
    - 등록 버튼(버튼만)

---

### 서버 ip 변경 시 변경되는 파일 목록

```
	modified:   app/build.gradle
	modified:   app/src/main/java/com/robomi/robomifront/MyApplication.java
	modified:   app/src/main/java/com/robomi/robomifront/VideoActivity.java
```


 

