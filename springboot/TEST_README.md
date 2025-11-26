# UserService 单元测试说明

## 概述

本文档描述了 `UserService` 类的单元测试实现，包含了所有主要方法的测试用例。

## 测试覆盖功能

### 1. 用户登录 (login)
- ✅ 用户不存在
- ✅ 账号被封禁
- ✅ 账号未激活
- ✅ 数据库异常处理

### 2. 用户注册 (register)
- ✅ 密码确认不一致
- ✅ 用户名已存在
- ✅ 邮箱已被注册
- ✅ 无效的用户类型
- ✅ 数据库异常处理

### 3. 获取用户信息 (getUserById)
- ✅ 成功获取用户信息
- ✅ 用户不存在

### 4. 分页查询用户列表 (getUserPage)
- ✅ 成功查询
- ✅ 带查询条件
- ✅ 数据库异常处理

### 5. 删除用户 (deleteUser)
- ✅ 成功删除
- ✅ 用户不存在
- ✅ 不能删除管理员账号
- ✅ 数据库异常处理

### 6. 重置密码 (resetPasswordByEmail)
- ✅ 成功重置
- ✅ 邮箱不存在
- ✅ 数据库异常处理

## 测试技术栈

- **JUnit 5**: 测试框架
- **Mockito**: Mock 框架
- **Spring Boot Test**: Spring Boot 测试支持
- **H2 Database**: 内存数据库用于测试
- **MyBatis-Plus**: ORM 测试支持

## 运行测试

### 1. 运行所有测试
```bash
mvn test
```

### 2. 运行特定测试类
```bash
mvn test -Dtest=UserServiceTest
```

### 3. 运行特定测试方法
```bash
mvn test -Dtest=UserServiceTest#login_UserNotFound
```

### 4. 在 IDE 中运行
- **IntelliJ IDEA**: 右键点击测试类或方法，选择 "Run"
- **Eclipse**: 右键点击测试类或方法，选择 "Run As" > "JUnit Test"

## 测试配置

### 测试环境配置文件
- `src/test/resources/application-test.yml`: 测试环境配置
- `src/test/resources/schema.sql`: 测试数据库表结构
- `src/test/resources/data.sql`: 测试数据

### 主要配置说明
- 使用 H2 内存数据库，测试完成后自动清理
- 启用 SQL 日志输出，便于调试
- 配置 MyBatis-Plus 测试环境

## 测试数据

测试使用以下预设用户数据：

| ID | 用户名 | 邮箱 | 用户类型 | 状态 | 用途 |
|----|--------|------|----------|------|------|
| 1 | admin | admin@example.com | admin | active | 管理员测试 |
| 2 | testuser | test@example.com | user | active | 普通用户测试 |
| 3 | merchant | merchant@example.com | merchant | active | 商家测试 |
| 4 | banneduser | banned@example.com | user | banned | 封禁用户测试 |
| 5 | inactiveuser | inactive@example.com | user | inactive | 未激活用户测试 |

## Mock 策略

### 1. UserMapper Mock
- 使用 `@Mock` 注解模拟 `UserMapper`
- 针对不同测试场景返回相应的模拟数据

### 2. 密码编码器处理
- 使用 `ReflectionTestUtils` 设置自定义密码编码器
- 简化密码验证逻辑，专注于业务逻辑测试

### 3. 静态方法避免
- 避免复杂的静态方法 Mock
- 重点测试业务逻辑和异常处理

## 测试最佳实践

### 1. 测试命名
- 使用 `@DisplayName` 注解提供中文描述
- 方法名遵循 `methodName_scenario` 格式

### 2. 测试结构
- **Arrange**: 准备测试数据
- **Act**: 执行被测试方法
- **Assert**: 验证结果

### 3. 异常测试
- 使用 `assertThrows` 验证异常类型和消息
- 确保正确的异常处理流程

### 4. Mock 验证
- 使用 `verify` 验证 Mock 对象的调用
- 确保方法按预期被调用

## 测试报告

运行测试后，可以通过以下方式查看测试报告：

### 1. Maven Surefire 报告
```bash
mvn surefire-report:report
```
报告位置: `target/site/surefire-report.html`

### 2. 覆盖率报告 (可选)
如需查看代码覆盖率，可以添加 JaCoCo 插件：

```xml
<plugin>
    <groupId>org.jacoco</groupId>
    <artifactId>jacoco-maven-plugin</artifactId>
    <version>0.8.8</version>
    <executions>
        <execution>
            <goals>
                <goal>prepare-agent</goal>
            </goals>
        </execution>
        <execution>
            <id>report</id>
            <phase>test</phase>
            <goals>
                <goal>report</goal>
            </goals>
        </execution>
    </executions>
</plugin>
```

## 常见问题

### 1. 测试数据库连接失败
确保 H2 数据库依赖已正确添加到 `pom.xml`。

### 2. Mock 对象注入失败
检查 `@ExtendWith(MockitoExtension.class)` 注解是否正确添加。

### 3. 测试数据初始化失败
检查 `schema.sql` 和 `data.sql` 文件路径和内容是否正确。

### 4. 静态方法 Mock 失败
本测试避免了复杂的静态方法 Mock，如需要可以使用 `MockedStatic`。

## 扩展建议

1. **集成测试**: 考虑添加 `@SpringBootTest` 集成测试
2. **性能测试**: 添加性能基准测试
3. **安全测试**: 测试安全相关功能
4. **边界测试**: 添加更多边界条件测试

## 维护说明

- 当 `UserService` 添加新方法时，需要相应添加测试用例
- 当业务逻辑发生变化时，需要更新相应的测试用例
- 定期检查测试覆盖率，确保关键业务逻辑被充分测试 