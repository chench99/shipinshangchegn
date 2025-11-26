# Spring Boot 3 集成 Log4j2 完整教程

## 目录
1. [简介](#简介)
2. [环境要求](#环境要求)
3. [依赖配置](#依赖配置)
4. [排除默认日志框架](#排除默认日志框架)
5. [Log4j2配置文件](#log4j2配置文件)
6. [配置文件详解](#配置文件详解)
7. [在代码中使用Log4j2](#在代码中使用log4j2)
8. [高级配置](#高级配置)
9. [性能优化](#性能优化)
10. [常见问题及解决方案](#常见问题及解决方案)
11. [最佳实践](#最佳实践)

## 简介

Log4j2是Apache开发的高性能日志框架，是Log4j的升级版本。相比于Logback（Spring Boot默认日志框架），Log4j2具有以下优势：

- **更高性能**：异步日志器比Logback快10倍以上
- **更低延迟**：在高负载情况下延迟更低
- **零垃圾收集**：避免临时对象分配
- **更灵活的配置**：支持XML、JSON、YAML等多种配置格式
- **插件架构**：易于扩展和自定义

## 环境要求

- Java 17+
- Spring Boot 3.0+
- Maven 3.6+ 或 Gradle 7.5+

## 依赖配置

### Maven配置

在`pom.xml`中添加Log4j2依赖：

```xml
<dependencies>
    <!-- Spring Boot Starter Web（排除默认日志） -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <exclusions>
            <exclusion>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-starter-logging</artifactId>
            </exclusion>
        </exclusions>
    </dependency>
    
    <!-- Log4j2 Spring Boot Starter -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-log4j2</artifactId>
    </dependency>
    
    <!-- Log4j2异步日志依赖（可选，用于性能优化） -->
    <dependency>
        <groupId>com.lmax</groupId>
        <artifactId>disruptor</artifactId>
        <version>3.4.4</version>
    </dependency>
</dependencies>
```

### Gradle配置

在`build.gradle`中添加：

```gradle
dependencies {
    implementation('org.springframework.boot:spring-boot-starter-web') {
        exclude group: 'org.springframework.boot', module: 'spring-boot-starter-logging'
    }
    implementation 'org.springframework.boot:spring-boot-starter-log4j2'
    
    // 异步日志依赖（可选）
    implementation 'com.lmax:disruptor:3.4.4'
}
```

## 排除默认日志框架

Spring Boot默认使用Logback作为日志框架，要使用Log4j2需要排除默认的日志依赖。

### 方法一：在每个starter中排除

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <exclusions>
        <exclusion>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

### 方法二：全局排除（推荐）

在Maven中使用全局排除：

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-dependencies</artifactId>
            <version>${spring-boot.version}</version>
            <type>pom</type>
            <scope>import</scope>
            <exclusions>
                <exclusion>
                    <groupId>org.springframework.boot</groupId>
                    <artifactId>spring-boot-starter-logging</artifactId>
                </exclusion>
            </exclusions>
        </dependency>
    </dependencies>
</dependencyManagement>
```

## Log4j2配置文件

### 配置文件位置

Log4j2会按以下顺序查找配置文件：

1. `log4j2-test.json` 或 `log4j2-test.jsn`
2. `log4j2-test.xml`
3. `log4j2.json` 或 `log4j2.jsn`
4. `log4j2.xml`

配置文件应放在`src/main/resources`目录下。

### 基础XML配置文件

创建`src/main/resources/log4j2.xml`：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<Configuration status="WARN" monitorInterval="30">
    
    <!-- 变量定义 -->
    <Properties>
        <Property name="LOG_PATTERN">
            %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n
        </Property>
        <Property name="LOG_DIR">./logs</Property>
    </Properties>
    
    <!-- 输出源配置 -->
    <Appenders>
        <!-- 控制台输出 -->
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <ThresholdFilter level="DEBUG" onMatch="ACCEPT" onMismatch="DENY"/>
        </Console>
        
        <!-- 文件输出 - 所有日志 -->
        <RollingFile name="FileAppender" fileName="${LOG_DIR}/application.log" 
                     filePattern="${LOG_DIR}/application-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <TimeBasedTriggeringPolicy/>
                <SizeBasedTriggeringPolicy size="100MB"/>
            </Policies>
            <DefaultRolloverStrategy max="10"/>
        </RollingFile>
        
        <!-- 错误日志单独文件 -->
        <RollingFile name="ErrorAppender" fileName="${LOG_DIR}/error.log" 
                     filePattern="${LOG_DIR}/error-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="${LOG_PATTERN}"/>
            <Policies>
                <TimeBasedTriggeringPolicy/>
                <SizeBasedTriggeringPolicy size="100MB"/>
            </Policies>
            <DefaultRolloverStrategy max="10"/>
            <ThresholdFilter level="ERROR" onMatch="ACCEPT" onMismatch="DENY"/>
        </RollingFile>
    </Appenders>
    
    <!-- 日志器配置 -->
    <Loggers>
        <!-- Spring框架日志 -->
        <Logger name="org.springframework" level="INFO" additivity="false">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="FileAppender"/>
        </Logger>
        
        <!-- Hibernate日志 -->
        <Logger name="org.hibernate" level="INFO" additivity="false">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="FileAppender"/>
        </Logger>
        
        <!-- 应用程序日志 -->
        <Logger name="com.yourpackage" level="DEBUG" additivity="false">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="FileAppender"/>
            <AppenderRef ref="ErrorAppender"/>
        </Logger>
        
        <!-- 根日志器 -->
        <Root level="INFO">
            <AppenderRef ref="Console"/>
            <AppenderRef ref="FileAppender"/>
            <AppenderRef ref="ErrorAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

### YAML配置文件

创建`src/main/resources/log4j2.yml`：

```yaml
Configuration:
  status: WARN
  monitorInterval: 30
  
  Properties:
    Property:
      - name: LOG_PATTERN
        value: "%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"
      - name: LOG_DIR
        value: "./logs"
  
  Appenders:
    Console:
      name: Console
      target: SYSTEM_OUT
      PatternLayout:
        pattern: "${LOG_PATTERN}"
      ThresholdFilter:
        level: DEBUG
        onMatch: ACCEPT
        onMismatch: DENY
    
    RollingFile:
      - name: FileAppender
        fileName: "${LOG_DIR}/application.log"
        filePattern: "${LOG_DIR}/application-%d{yyyy-MM-dd}-%i.log.gz"
        PatternLayout:
          pattern: "${LOG_PATTERN}"
        Policies:
          TimeBasedTriggeringPolicy: {}
          SizeBasedTriggeringPolicy:
            size: 100MB
        DefaultRolloverStrategy:
          max: 10
      
      - name: ErrorAppender
        fileName: "${LOG_DIR}/error.log"
        filePattern: "${LOG_DIR}/error-%d{yyyy-MM-dd}-%i.log.gz"
        PatternLayout:
          pattern: "${LOG_PATTERN}"
        Policies:
          TimeBasedTriggeringPolicy: {}
          SizeBasedTriggeringPolicy:
            size: 100MB
        DefaultRolloverStrategy:
          max: 10
        ThresholdFilter:
          level: ERROR
          onMatch: ACCEPT
          onMismatch: DENY
  
  Loggers:
    Logger:
      - name: org.springframework
        level: INFO
        additivity: false
        AppenderRef:
          - ref: Console
          - ref: FileAppender
      
      - name: com.yourpackage
        level: DEBUG
        additivity: false
        AppenderRef:
          - ref: Console
          - ref: FileAppender
          - ref: ErrorAppender
    
    Root:
      level: INFO
      AppenderRef:
        - ref: Console
        - ref: FileAppender
        - ref: ErrorAppender
```

## 配置文件详解

### Configuration根元素

- `status`：Log4j2内部日志级别
- `monitorInterval`：配置文件监控间隔（秒），自动重载配置

### Properties（属性定义）

定义可重用的属性变量：

```xml
<Properties>
    <Property name="LOG_PATTERN">
        %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n
    </Property>
    <Property name="LOG_DIR">./logs</Property>
    <Property name="APP_NAME">MyApp</Property>
</Properties>
```

### 常用Pattern Layout模式

- `%d{pattern}`：日期时间，如`%d{yyyy-MM-dd HH:mm:ss.SSS}`
- `%t`：线程名
- `%level`：日志级别
- `%-5level`：左对齐的5位日志级别
- `%logger{length}`：Logger名称，可指定长度
- `%msg`：日志消息
- `%n`：换行符
- `%X{key}`：MDC中的值
- `%ex`：异常堆栈信息

### Appenders（输出源）

#### Console Appender

```xml
<Console name="Console" target="SYSTEM_OUT">
    <PatternLayout pattern="${LOG_PATTERN}"/>
    <ThresholdFilter level="DEBUG" onMatch="ACCEPT" onMismatch="DENY"/>
</Console>
```

#### File Appender

```xml
<File name="FileAppender" fileName="${LOG_DIR}/application.log">
    <PatternLayout pattern="${LOG_PATTERN}"/>
</File>
```

#### RollingFile Appender

```xml
<RollingFile name="RollingFileAppender" 
             fileName="${LOG_DIR}/application.log" 
             filePattern="${LOG_DIR}/application-%d{yyyy-MM-dd}-%i.log.gz">
    <PatternLayout pattern="${LOG_PATTERN}"/>
    <Policies>
        <!-- 基于时间的滚动策略 -->
        <TimeBasedTriggeringPolicy/>
        <!-- 基于文件大小的滚动策略 -->
        <SizeBasedTriggeringPolicy size="100MB"/>
    </Policies>
    <!-- 保留文件数量 -->
    <DefaultRolloverStrategy max="10"/>
</RollingFile>
```

### Filters（过滤器）

#### ThresholdFilter

```xml
<ThresholdFilter level="ERROR" onMatch="ACCEPT" onMismatch="DENY"/>
```

#### LevelRangeFilter

```xml
<LevelRangeFilter minLevel="DEBUG" maxLevel="INFO" onMatch="ACCEPT" onMismatch="DENY"/>
```

### Loggers（日志器）

#### 具名Logger

```xml
<Logger name="com.yourpackage.service" level="DEBUG" additivity="false">
    <AppenderRef ref="Console"/>
    <AppenderRef ref="FileAppender"/>
</Logger>
```

#### Root Logger

```xml
<Root level="INFO">
    <AppenderRef ref="Console"/>
    <AppenderRef ref="FileAppender"/>
</Root>
```

## 在代码中使用Log4j2

### 导入依赖

```java
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
```

### 创建Logger实例

```java
@RestController
public class UserController {
    
    // 方式1：使用当前类
    private static final Logger logger = LogManager.getLogger(UserController.class);
    
    // 方式2：使用字符串名称
    private static final Logger logger = LogManager.getLogger("UserController");
    
    // 方式3：使用工厂方法
    private static final Logger logger = LogManager.getLogger();
}
```

### 基本日志记录

```java
@Service
public class UserService {
    private static final Logger logger = LogManager.getLogger(UserService.class);
    
    public User getUserById(Long id) {
        logger.trace("进入 getUserById 方法，参数：{}", id);
        logger.debug("查询用户ID：{}", id);
        logger.info("获取用户信息：{}", id);
        logger.warn("用户{}可能不存在", id);
        logger.error("获取用户{}时发生错误", id);
        logger.fatal("系统严重错误");
        
        return userRepository.findById(id);
    }
}
```

### 参数化日志

```java
// 推荐：使用参数化日志
logger.info("用户{}登录成功，IP地址：{}", username, ipAddress);

// 不推荐：字符串拼接
logger.info("用户" + username + "登录成功，IP地址：" + ipAddress);
```

### 异常日志记录

```java
try {
    // 业务逻辑
} catch (Exception e) {
    // 记录异常
    logger.error("处理用户请求时发生异常：{}", e.getMessage(), e);
    
    // 或者
    logger.error("处理用户请求时发生异常", e);
}
```

### 条件日志记录

```java
// 避免不必要的字符串拼接
if (logger.isDebugEnabled()) {
    logger.debug("复杂计算结果：{}", expensiveCalculation());
}

// 使用Supplier（Java 8+）
logger.debug("复杂计算结果：{}", () -> expensiveCalculation());
```

### MDC（Mapped Diagnostic Context）使用

```java
import org.apache.logging.log4j.ThreadContext;

@Component
public class RequestTracker {
    
    public void processRequest(String requestId, String userId) {
        try {
            // 设置MDC
            ThreadContext.put("requestId", requestId);
            ThreadContext.put("userId", userId);
            
            logger.info("开始处理请求");
            // 业务逻辑
            logger.info("请求处理完成");
            
        } finally {
            // 清理MDC
            ThreadContext.clearAll();
        }
    }
}
```

配置文件中使用MDC：

```xml
<Property name="LOG_PATTERN">
    %d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level [%X{requestId}] [%X{userId}] %logger{36} - %msg%n
</Property>
```

## 高级配置

### 异步日志配置

#### 全异步配置

在`log4j2.xml`中配置：

```xml
<Configuration status="WARN" monitorInterval="30">
    <!-- 启用全异步 -->
    <AsyncRoot level="INFO">
        <AppenderRef ref="Console"/>
        <AppenderRef ref="FileAppender"/>
    </AsyncRoot>
</Configuration>
```

或者通过系统属性：

```properties
# application.properties
log4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
```

#### 混合异步配置

```xml
<Loggers>
    <!-- 异步Logger -->
    <AsyncLogger name="com.yourpackage.service" level="DEBUG" additivity="false">
        <AppenderRef ref="FileAppender"/>
    </AsyncLogger>
    
    <!-- 同步Logger -->
    <Logger name="com.yourpackage.controller" level="INFO" additivity="false">
        <AppenderRef ref="Console"/>
    </Logger>
    
    <Root level="INFO">
        <AppenderRef ref="Console"/>
    </Root>
</Loggers>
```

### 自定义Appender

```xml
<!-- 邮件Appender -->
<SMTP name="MailAppender" 
      subject="应用程序错误" 
      to="admin@example.com" 
      from="app@example.com" 
      smtpHost="smtp.example.com" 
      smtpPort="587" 
      smtpUsername="user" 
      smtpPassword="password">
    <ThresholdFilter level="ERROR" onMatch="ACCEPT" onMismatch="DENY"/>
    <PatternLayout pattern="${LOG_PATTERN}"/>
</SMTP>

<!-- 数据库Appender -->
<JDBC name="DatabaseAppender" tableName="application_logs">
    <DataSource jndiName="java:comp/env/jdbc/LoggingDataSource"/>
    <Column name="date" pattern="%d{yyyy-MM-dd HH:mm:ss.SSS}"/>
    <Column name="level" pattern="%-5level"/>
    <Column name="logger" pattern="%logger"/>
    <Column name="message" pattern="%msg"/>
    <Column name="exception" pattern="%ex{full}"/>
</JDBC>
```

### 配置环境区分

#### 开发环境配置

`log4j2-dev.xml`：

```xml
<Configuration status="WARN">
    <Appenders>
        <Console name="Console" target="SYSTEM_OUT">
            <PatternLayout pattern="%d{HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
        </Console>
    </Appenders>
    
    <Loggers>
        <Logger name="com.yourpackage" level="DEBUG" additivity="false">
            <AppenderRef ref="Console"/>
        </Logger>
        <Root level="DEBUG">
            <AppenderRef ref="Console"/>
        </Root>
    </Loggers>
</Configuration>
```

#### 生产环境配置

`log4j2-prod.xml`：

```xml
<Configuration status="WARN">
    <Properties>
        <Property name="LOG_DIR">/var/log/myapp</Property>
    </Properties>
    
    <Appenders>
        <RollingFile name="FileAppender" fileName="${LOG_DIR}/application.log" 
                     filePattern="${LOG_DIR}/application-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
            <Policies>
                <TimeBasedTriggeringPolicy/>
                <SizeBasedTriggeringPolicy size="100MB"/>
            </Policies>
            <DefaultRolloverStrategy max="30"/>
        </RollingFile>
        
        <RollingFile name="ErrorAppender" fileName="${LOG_DIR}/error.log" 
                     filePattern="${LOG_DIR}/error-%d{yyyy-MM-dd}-%i.log.gz">
            <PatternLayout pattern="%d{yyyy-MM-dd HH:mm:ss.SSS} [%t] %-5level %logger{36} - %msg%n"/>
            <ThresholdFilter level="ERROR" onMatch="ACCEPT" onMismatch="DENY"/>
            <Policies>
                <TimeBasedTriggeringPolicy/>
                <SizeBasedTriggeringPolicy size="100MB"/>
            </Policies>
            <DefaultRolloverStrategy max="30"/>
        </RollingFile>
    </Appenders>
    
    <Loggers>
        <Logger name="com.yourpackage" level="INFO" additivity="false">
            <AppenderRef ref="FileAppender"/>
            <AppenderRef ref="ErrorAppender"/>
        </Logger>
        <Root level="WARN">
            <AppenderRef ref="FileAppender"/>
            <AppenderRef ref="ErrorAppender"/>
        </Root>
    </Loggers>
</Configuration>
```

#### 在application.properties中指定配置文件

```properties
# 根据环境指定配置文件
logging.config=classpath:log4j2-${spring.profiles.active}.xml

# 或者直接指定
logging.config=classpath:log4j2-prod.xml
```

## 性能优化

### 1. 启用异步日志

```properties
# application.properties
log4j2.contextSelector=org.apache.logging.log4j.core.async.AsyncLoggerContextSelector

# 异步队列大小
AsyncLogger.RingBufferSize=262144

# 等待策略
AsyncLogger.WaitStrategy=Block
```

### 2. 无垃圾收集配置

```properties
# 启用无垃圾收集
log4j2.enable.threadlocals=true
log4j2.enable.direct.encoders=true
```

### 3. 缓冲区优化

```xml
<RollingFile name="FileAppender" fileName="${LOG_DIR}/application.log" 
             filePattern="${LOG_DIR}/application-%d{yyyy-MM-dd}-%i.log.gz"
             bufferedIO="true" bufferSize="8192">
    <!-- ... -->
</RollingFile>
```

### 4. 模式布局优化

```xml
<!-- 使用高性能模式 -->
<PatternLayout pattern="%d{DEFAULT} [%t] %-5level %logger{36} - %m%n" 
               disableAnsi="false" 
               header="" 
               footer=""/>
```

### 5. JVM参数优化

```bash
# 异步日志相关参数
-DAsyncLogger.RingBufferSize=262144
-DAsyncLogger.WaitStrategy=Block

# 无垃圾收集参数
-Dlog4j2.enable.threadlocals=true
-Dlog4j2.enable.direct.encoders=true

# 禁用JMX
-Dlog4j2.disable.jmx=true
```

## 常见问题及解决方案

### 1. 日志不输出问题

**问题**：配置了Log4j2但没有日志输出

**解决方案**：
- 检查是否正确排除了Spring Boot默认的logback依赖
- 确认配置文件名称和位置正确
- 检查Logger级别设置
- 查看Log4j2内部日志：设置`status="DEBUG"`

### 2. 配置文件不生效

**问题**：修改配置文件后不生效

**解决方案**：
- 确认配置文件路径正确
- 检查XML语法是否正确
- 设置`monitorInterval`属性自动重载
- 重启应用程序

### 3. 性能问题

**问题**：日志记录影响应用性能

**解决方案**：
- 启用异步日志
- 使用参数化日志避免字符串拼接
- 合理设置日志级别
- 优化Appender配置

### 4. 文件滚动问题

**问题**：日志文件不自动滚动

**解决方案**：
```xml
<RollingFile name="FileAppender" fileName="${LOG_DIR}/application.log" 
             filePattern="${LOG_DIR}/application-%d{yyyy-MM-dd}-%i.log.gz">
    <PatternLayout pattern="${LOG_PATTERN}"/>
    <Policies>
        <!-- 确保包含滚动策略 -->
        <TimeBasedTriggeringPolicy/>
        <SizeBasedTriggeringPolicy size="100MB"/>
    </Policies>
    <!-- 确保包含滚动策略 -->
    <DefaultRolloverStrategy max="10"/>
</RollingFile>
```

### 5. 内存泄漏问题

**问题**：MDC导致内存泄漏

**解决方案**：
```java
try {
    ThreadContext.put("requestId", requestId);
    // 业务逻辑
} finally {
    // 必须清理MDC
    ThreadContext.clearAll();
}
```

### 6. 类路径冲突

**问题**：Log4j版本冲突

**解决方案**：
```xml
<!-- 排除传递依赖中的旧版本 -->
<dependency>
    <groupId>some.library</groupId>
    <artifactId>library-name</artifactId>
    <version>1.0.0</version>
    <exclusions>
        <exclusion>
            <groupId>log4j</groupId>
            <artifactId>log4j</artifactId>
        </exclusion>
    </exclusions>
</dependency>
```

## 最佳实践

### 1. 日志级别详解

Log4j2日志级别从高到低分为以下几个级别：

#### FATAL - 致命错误
- **级别**：最高
- **用途**：导致应用程序完全无法继续运行的严重错误
- **使用场景**：
  - 系统启动失败
  - 数据库连接完全失败
  - 配置文件严重错误导致应用无法启动
- **示例**：
```java
logger.fatal("数据库连接失败，应用程序无法启动");
logger.fatal("配置文件{}不存在，系统退出", configFile);
```

#### ERROR - 错误
- **级别**：高
- **用途**：错误事件，但应用程序仍能继续运行
- **使用场景**：
  - 业务处理失败
  - 外部服务调用失败
  - 数据处理异常
- **示例**：
```java
logger.error("用户{}订单创建失败：{}", userId, e.getMessage(), e);
logger.error("调用外部API失败，服务不可用");
```

#### WARN - 警告
- **级别**：中高
- **用途**：潜在的有害情况，需要注意但不影响正常运行
- **使用场景**：
  - 过时API的使用
  - 配置参数使用默认值
  - 性能警告
  - 业务逻辑警告
- **示例**：
```java
logger.warn("用户{}尝试访问已过期的优惠券", userId);
logger.warn("缓存命中率过低：{}%", hitRate);
logger.warn("使用了已过时的API方法：{}", methodName);
```

#### INFO - 信息
- **级别**：中
- **用途**：应用程序的运行信息，记录重要的业务流程
- **使用场景**：
  - 应用启动/关闭
  - 重要业务操作
  - 配置信息
  - 系统状态变化
- **示例**：
```java
logger.info("应用程序启动完成，端口：{}", port);
logger.info("用户{}登录成功，IP：{}", username, clientIP);
logger.info("定时任务开始执行：{}", taskName);
```

#### DEBUG - 调试
- **级别**：低
- **用途**：调试信息，帮助开发人员诊断问题
- **使用场景**：
  - 方法进入/退出
  - 变量值
  - 执行流程
  - 中间计算结果
- **示例**：
```java
logger.debug("进入getUserById方法，参数：{}", userId);
logger.debug("SQL查询结果：{}", resultSet);
logger.debug("缓存命中，返回数据：{}", cachedData);
```

#### TRACE - 跟踪
- **级别**：最低
- **用途**：最详细的调试信息，通常用于框架级别的调试
- **使用场景**：
  - 框架内部流程
  - 详细的执行路径
  - 性能分析
  - 深度调试
- **示例**：
```java
logger.trace("SQL执行计划：{}", executionPlan);
logger.trace("HTTP请求详细信息：{}", requestDetails);
logger.trace("内存使用情况：已用{}MB，可用{}MB", usedMemory, freeMemory);
```

#### 级别层次关系

```
FATAL > ERROR > WARN > INFO > DEBUG > TRACE
```

**重要特性**：
- 当设置某个级别时，该级别及以上级别的日志都会被输出
- 例如：设置为INFO级别时，FATAL、ERROR、WARN、INFO都会输出，而DEBUG和TRACE不会输出

#### 各环境推荐配置

**开发环境**：
```xml
<Logger name="com.yourpackage" level="DEBUG"/>
<Root level="DEBUG"/>
```

**测试环境**：
```xml
<Logger name="com.yourpackage" level="INFO"/>
<Root level="INFO"/>
```

**生产环境**：
```xml
<Logger name="com.yourpackage" level="WARN"/>
<Root level="WARN"/>
```

#### 使用建议

1. **生产环境**：建议使用WARN及以上级别，避免过多日志影响性能
2. **开发调试**：可以使用DEBUG或TRACE级别获取详细信息
3. **日志量控制**：根据系统负载合理设置日志级别
4. **分级记录**：不同的Logger可以设置不同的级别

### 2. 日志内容规范

```java
// 好的实践
logger.info("用户登录成功 - 用户名: {}, IP: {}, 登录时间: {}", 
           username, ipAddress, LocalDateTime.now());

// 避免的做法
logger.info("用户登录成功");  // 信息不够详细
logger.info("用户" + username + "登录成功");  // 字符串拼接
```

### 3. 异常日志记录

```java
try {
    // 业务逻辑
} catch (BusinessException e) {
    // 业务异常记录为WARN级别
    logger.warn("业务处理异常: {}", e.getMessage());
} catch (Exception e) {
    // 系统异常记录为ERROR级别，包含完整堆栈
    logger.error("系统异常: {}", e.getMessage(), e);
}
```

### 4. 避免日志污染

```java
// 避免在循环中大量输出日志
for (User user : users) {
    // 不好的做法
    logger.debug("处理用户: {}", user.getName());
}

// 更好的做法
logger.debug("开始处理 {} 个用户", users.size());
// 处理逻辑
logger.debug("用户处理完成");
```

### 5. 配置文件组织

```
src/main/resources/
├── log4j2.xml              # 默认配置
├── log4j2-dev.xml          # 开发环境
├── log4j2-test.xml         # 测试环境
├── log4j2-prod.xml         # 生产环境
└── application.properties  # Spring配置
```

### 6. 监控和告警

```xml
<!-- 配置错误日志邮件告警 -->
<SMTP name="ErrorMailAppender" 
      subject="[PROD] 应用程序错误告警" 
      to="devops@company.com" 
      from="noreply@company.com" 
      smtpHost="smtp.company.com">
    <ThresholdFilter level="ERROR" onMatch="ACCEPT" onMismatch="DENY"/>
    <PatternLayout pattern="${LOG_PATTERN}"/>
</SMTP>
```

### 7. 敏感信息处理

```java
// 避免记录敏感信息
logger.info("用户{}登录", user.getUsername());  // 好
logger.info("用户{}登录，密码{}", user.getUsername(), user.getPassword());  // 不好

// 使用脱敏工具
logger.info("用户{}登录，手机号{}", user.getUsername(), maskPhone(user.getPhone()));
```

### 8. 结构化日志

```java
// 使用结构化日志便于分析
logger.info("用户操作 - 操作类型: {}, 用户ID: {}, 资源ID: {}, 操作结果: {}, 耗时: {}ms",
           operation, userId, resourceId, result, duration);
```

## 总结

Log4j2是一个功能强大、性能优异的日志框架。通过合理的配置和使用，可以为Spring Boot 3应用程序提供高效的日志记录功能。关键要点包括：

1. **正确配置依赖**：排除默认日志框架，引入Log4j2依赖
2. **合理设计配置**：根据环境分别配置，设置合适的日志级别和输出策略
3. **性能优化**：启用异步日志，使用参数化日志，避免不必要的字符串操作
4. **最佳实践**：遵循日志规范，避免敏感信息泄露，合理使用MDC
5. **监控维护**：配置日志滚动策略，设置告警机制，定期清理日志文件

通过遵循本教程的指导，您可以在Spring Boot 3项目中成功集成和使用Log4j2，提升应用程序的可观测性和可维护性。 