FROM maven:3.9.4-eclipse-temurin-17 AS build

WORKDIR /app

# 复制pom.xml并下载依赖
COPY pom.xml .
RUN mvn dependency:go-offline -B

# 复制源代码并构建
COPY src ./src
RUN mvn clean package -DskipTests

# 运行阶段
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 从构建阶段复制jar文件
COPY --from=build /app/target/*.jar app.jar

# 创建上传目录
RUN mkdir -p /app/upload

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

