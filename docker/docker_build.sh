#!/bin/sh

# 버전 설정
VERSION="0.0.1"

# Gradle 빌드 (테스트 제외)
echo "Running Gradle build..."
cd ../ || exit  # 최상위 디렉토리로 이동
./gradlew clean build -x test

# Docker 이미지 빌드 (최상위 디렉토리에서 실행)
echo "Building Docker image..."
docker build -t recipe-app:$VERSION -f Dockerfile .

echo "Docker build completed successfully!"