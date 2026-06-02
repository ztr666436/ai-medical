# 步骤 2 — Maven 父工程与 Common 模块

## 完成内容

### 1. 父 POM (`pom.xml`)

| 配置项 | 说明 |
|--------|------|
| 构建工具 | Maven 3.9+ |
| Spring Boot | 3.2.5 |
| Spring Cloud | 2023.0.2 |
| Spring Cloud Alibaba | 2023.0.1.2 |
| Java 版本 | 17 LTS |
| 依赖管理 | 集中管理 20+ 依赖版本，子模块无需指定版本号 |

### 2. Common 模块

```
ai-medical-care-common/
└── src/main/java/com/medical/common/
    ├── result/
    │   ├── R.java                    # 统一响应体 (code+message+data)
    │   └── ResultCode.java           # 返回码枚举 (成功/业务/系统)
    ├── exception/
    │   ├── BusinessException.java    # 业务异常类
    │   └── GlobalExceptionHandler.java # 全局异常处理器
    ├── constant/
    │   └── SystemConstants.java      # 系统常量
    ├── utils/
    │   ├── JwtUtil.java             # JWT Token 工具
    │   ├── AESUtil.java             # AES 加解密 + 脱敏
    │   └── UserContext.java         # 用户上下文 (ThreadLocal)
    └── config/
        └── CommonAutoConfiguration.java # 自动装配配置
```

### 3. 关键设计决策

| 决策 | 选择 | 原因 |
|------|------|------|
| 响应体格式 | `R<T>` 泛型类 + `ResultCode` 枚举 | 类型安全，避免魔法数字 |
| 异常体系 | `BusinessException` + `GlobalExceptionHandler` | 业务异常与系统异常分离 |
| 异常码分段 | 1xxxx(用户) 2xxxx(健康) 3xxxx(目标) 4xxxx(AI) | 按模块分段，快速定位 |
| Token 类型 | Access(2h) + Refresh(7d) 双 Token | 安全与体验兼顾 |
| 加密方式 | AES (手机号/邮箱) + BCrypt (密码) | 可逆与不可逆各取所需 |
| JWT 库 | jjwt 0.12.5 | 纯 Java 实现，无外部依赖 |
| Spring Boot 3.x | jakarta.* 迁移 | 紧跟官方路线，避免技术债 |
| 自动装配 | spring.factories → AutoConfiguration.imports | Spring Boot 3.x 新规范 |

### 4. 构建验证

```bash
cd D:\workbuddy_pj\ai-medical-care-agents
mvn clean compile
```

### 5. 下一步预览

步骤 3 将创建 **auth-service**：注册、登录、Token 签发/刷新、Spring Security 配置、网关路由规则。
