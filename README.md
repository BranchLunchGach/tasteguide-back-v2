# 🍽️ TasteGuide-Back-V2

AI 기반 외식 추천 서비스의 백엔드 서버입니다.

---

## 🛠️ 프로젝트 실행 방법

### 1. 레포지토리 클론

```bash
git clone https://github.com/your-org/tasteguide-back-v2.git
cd tasteguide-back-v2
```

---

### 2. 의존성 설치 (Gradle Wrapper 사용)

```bash
./gradlew clean build
```

> Gradle이 설치되어 있지 않아도 `gradlew` 스크립트를 통해 의존성 자동 다운로드 및 빌드가 가능합니다.

---

### 3. Docker 서비스 실행 (MySQL, Redis)

```bash
cd infra
docker-compose up -d
```
> infra폴더로 이동 후
> `docker-compose.yml` 구성 예시:

<details>
<summary>클릭하여 보기</summary>

```yaml
services:
  mysql:
    image: mysql:latest
    ports:
      - '13306:3306'
    environment:
      MYSQL_DATABASE: 'tasteguidebackv2'
      MYSQL_USER: 'testuser'
      MYSQL_PASSWORD: 'testpass'
      MYSQL_ROOT_PASSWORD: 'root'
    volumes:
      - ./mysql/data:/var/lib/mysql

  redis:
    image: redis:latest
    ports:
      - '16379:6379'
    volumes:
      - ./redis/data:/data
      - ./redis/conf/redis.conf:/usr/local/conf/redis.conf
    command: redis-server /usr/local/conf/redis.conf
    restart: always
```

</details>

---

### 4. Docker 컨테이너 실행 상태 확인

```bash
docker ps
```

- `mysql`, `redis` 컨테이너가 정상적으로 뜬 것을 확인합니다.

---

### 5. Spring Boot 서버 실행

```bash
./gradlew bootRun
```

---

### 6. 서버 동작 확인

```bash
curl http://localhost:8080/login
```

- 로그인 엔드포인트가 동작하면 연결 성공입니다.

---

## ✅ 환경 변수 설정 (.env 예시)

`.env` 파일 예시:

```env
SPRING_DATASOURCE_URL=jdbc:mysql://localhost:13306/tasteguidebackv2
SPRING_DATASOURCE_USERNAME=testuser
SPRING_DATASOURCE_PASSWORD=testpass
JWT_SECRET_KEY=your_jwt_secret
```

> `.env`는 `spring-dotenv` 라이브러리를 통해 자동 로딩됩니다.

---

## 📦 주요 기술 스택

- Java 17
- Spring Boot 3
- JPA (Hibernate)
- MySQL / Redis
- Spring Security / JWT
- Gradle
- Docker & Docker Compose


---

## 👥 기여자

- 반좡님, 지남큄, 도현뽹, 차냥님, 선민츄

---

## 📄 라이선스

없음