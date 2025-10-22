# 第一階段: 建置
FROM maven:3.9-eclipse-temurin-17 AS builder
WORKDIR /app

# 複製 pom.xml 並下載依賴（利用 Docker 快取）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 複製原始碼並建置
COPY src ./src
RUN mvn clean package -DskipTests

# 第二階段: 執行
FROM eclipse-temurin:17-jre
WORKDIR /app

# 建立非 root 使用者
RUN groupadd -r spring && useradd -r -g spring spring

# 從建置階段複製 JAR 檔案
COPY --from=builder /app/target/*.jar app.jar

# 更改檔案擁有者
RUN chown -R spring:spring /app

# 切換到非 root 使用者
USER spring

# 暴露埠號
EXPOSE 8080

# 健康檢查
HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/actuator/health || exit 1

# 啟動應用程式
ENTRYPOINT ["java", "-jar", "/app/app.jar"]