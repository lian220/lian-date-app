# 개발용 Next.js Dockerfile with Hot Reloading

FROM node:20-alpine

# 작업 디렉토리 설정
WORKDIR /app

# 의존성 파일 복사 (캐싱 최적화)
COPY package*.json ./

# 의존성 설치
RUN npm ci

# 소스 코드는 볼륨 마운트로 제공됨
# COPY . .

# 포트 노출
EXPOSE 3000

# 개발 서버 실행 (핫 리로딩 활성화)
CMD ["npm", "run", "dev"]
