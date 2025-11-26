# 阶段1：使用Maven构建JAR包
FROM maven:3.8.6-openjdk-11 as builder

# 设置工作目录
WORKDIR /app

# 复制pom.xml和项目文件
COPY pom.xml .
COPY src ./src

# 构建应用
RUN mvn clean package -DskipTests

# 阶段2：使用JDK运行应用
FROM openjdk:11-jre-slim

# 设置工作目录
WORKDIR /app

# 从构建阶段复制JAR包
COPY --from=builder /app/target/*.jar app.jar

# 暴露端口
EXPOSE 1234

# 设置环境变量
ENV SPRING_PROFILES_ACTIVE=prod
ENV JAVA_OPTS="-Xmx512m -Xms256m"

# 运行应用
CMD ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]