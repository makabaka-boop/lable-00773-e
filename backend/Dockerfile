# 多阶段构建 - 支持 ARM 和 X86
# 阶段1: 构建阶段
FROM maven:3.9-eclipse-temurin-17 AS builder

WORKDIR /app

# 配置 Maven 使用阿里云镜像加速
COPY settings.xml /root/.m2/settings.xml

# 复制 pom.xml 先下载依赖（利用缓存）
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 阶段2: 运行阶段
FROM eclipse-temurin:17-jre-jammy

WORKDIR /app

# 安装 curl 用于健康检查
RUN apt-get update && \
    apt-get install -y --no-install-recommends curl && \
    rm -rf /var/lib/apt/lists/*

# 从构建阶段复制 jar 文件
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 8080

# 健康检查 - 使用公开的端点
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=5 \
  CMD curl -f http://localhost:8080/api/health || exit 1

# 运行应用
ENTRYPOINT ["java", "-jar", "app.jar"]
