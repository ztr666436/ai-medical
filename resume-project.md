# AI Medical Care Agents（阿福健康助手）— 简历项目描述

> 基于 Spring Cloud 微服务 + DeepSeek AI Agent 的智能健康管理平台，支持对话式健康数据录入、
> AI 目标推荐、SSE 流式对话、事件驱动进度追踪，前端采用 Vue3 + 蚂蚁阿福萌趣风格。

---

## 一、项目概述

**项目定位**：将传统健康管理 App（数据记录、目标管理、健康建议）与 AI Agent 对话式交互
能力融合的 Web 端健康管理平台，用户通过自然语言即可完成健康数据录入、查询和获取个性化建议。

**项目规模**：6 个微服务 + 1 个公共模块 + 1 个前端模块，共 50+ Java 文件，7 份架构设计文档。

**项目周期**：独立开发，从架构设计到完整可运行原型。

---

## 二、技术栈

| 层级 | 技术 |
|------|------|
| **后端框架** | Spring Boot 3.2.5、Spring Cloud 2023.0.2（Gateway + OpenFeign） |
| **认证授权** | Spring Security 6.x + JWT（Access/Refresh 双 Token）+ BCrypt |
| **ORM** | MyBatis-Plus 3.5.7 |
| **数据库与缓存** | MySQL 8.0、Redis 7.0 |
| **注册配置** | Nacos 2.3.x（服务发现 + 配置中心） |
| **消息队列** | RabbitMQ（事件驱动解耦） |
| **AI 集成** | DeepSeek API（deepseek-reasoner）+ Spring WebFlux + SSE 流式输出 |
| **API 文档** | Knife4j 4.5.0 |
| **前端** | Vue3（Composition API）、Vite5、Axios、ECharts5 |
| **语言与构建** | Java 17、Maven 多模块管理 |

---

## 三、微服务架构（7 模块）

```
gateway (:8080)          统一入口，路由转发 + 全局 JWT Token 校验 + CORS
auth-service (:8081)     注册/登录，JWT 签发与刷新，Redis Token 黑名单
health-data-service (:8083)  对话式健康数据录入，AI 解析 + 规则引擎兜底
goal-service (:8084)     AI 目标推荐，事件驱动自动进度检查与达成
ai-agent-service (:8085) DeepSeek AI 对话，意图识别，SSE 流式输出
common                   R<T> 统一响应体、全局异常处理、JWT/AES 工具类
web (Vue3 SPA)           阿福萌趣风格前端，对话/仪表盘/目标三个标签页
```

---

## 四、核心功能实现

### 1. 对话式健康数据录入
- 用户通过自然语言输入（如"今天走了 9000 步，体重 70kg"），系统自动解析并结构化入库
- **双轨解析策略**：优先调用 DeepSeek AI 提取 JSON 结构化数据，AI 不可用时正则规则引擎自动降级
- 支持步数、体重、血压、血糖、心率 5 类健康指标，异常值自动警告

### 2. AI 智能对话助手（"阿福"卡通企鹅角色）
- 集成 DeepSeek API（deepseek-reasoner 推理模型），4 套精心设计的 System Prompt 模板
- **SSE 流式输出**：基于 Spring WebFlux 实现实时逐字返回，体验优于传统一次性回复
- 支持多轮对话上下文管理（保留最近 10 轮），全链路降级策略（每个 AI 接口均有兜底回复）

### 3. 健康目标管理与自动进度追踪
- 基于 WHO 建议的规则推荐引擎，为步数/体重/血压等指标自动生成合理目标
- **事件驱动架构**：健康数据写入后发布 `HealthDataUpdatedEvent`，goal-service 异步监听
- 目标达成时**自动标记完成**，无需用户手动操作

### 4. 统一认证与安全体系
- JWT Access Token（2h）+ Refresh Token（7d）双 Token 机制，Redis 黑名单防止 Token 泄露
- AES 加密敏感字段（手机号/邮箱），BCrypt 加密密码
- Gateway 全局过滤器统一拦截非白名单请求，注入用户上下文

---

## 五、项目亮点

- **全链路降级容错**：AI 调用失败时各接口均有独立降级策略，确保系统不因外部依赖崩溃
- **事件驱动解耦**：健康数据写入与目标检查异步分离，不阻塞用户主流程
- **工程化文档**：7 份架构设计文档覆盖全部模块，设计先行，设计文档与代码一一对应
- **返回码分段设计**：按模块划分错误码段（1xxxx/2xxxx/3xxxx/4xxxx），快速定位问题
- **Prompt Engineering**：4 套 System Prompt 均包含输出格式约束和行为边界，确保 AI 输出稳定可解析

---

## ===== 简历模板：直接复制使用 =====

---

### 模板 A：精简版（3 行，适合海投 / 项目经历较多时）

> **AI 智能健康管理平台（阿福健康助手）** | 独立开发
> - 基于 Spring Cloud 微服务架构（Gateway + OpenFeign + Nacos），实现认证授权、健康数据对话式录入、
>   AI 目标推荐、事件驱动进度追踪等核心功能，共 6 个微服务模块
> - 集成 DeepSeek API 实现 AI 智能对话与健康数据解析，基于 WebFlux 实现 SSE 流式输出，
>   设计全链路降级策略保证系统稳定性；前端采用 Vue3 + Vite5 + ECharts5
> - 技术栈：Spring Boot 3.2 / Spring Cloud 2023 / MyBatis-Plus / Redis / MySQL / DeepSeek / WebFlux / JWT

---

### 模板 B：标准版（6 行，适合大多数场景）

> **AI 智能健康管理平台（阿福健康助手）** | 后端开发 | 独立完成
>
> **项目描述**：基于 Spring Cloud 微服务 + DeepSeek AI Agent 的对话式健康管理 Web 平台，用户通过
> 自然语言即可完成健康数据录入、目标管理及获取个性化 AI 建议。6 个微服务模块，50+ Java 文件。
>
> **技术栈**：Spring Boot 3.2.5 / Spring Cloud Gateway + OpenFeign / MyBatis-Plus / Spring Security +
> JWT / Nacos / Redis / MySQL / DeepSeek API / WebFlux SSE / Vue3 + Vite5
>
> **核心职责**：
> - 设计微服务架构方案，将系统拆分为认证、健康数据、目标管理、AI Agent、网关 5 大服务，
>   编写 7 份架构设计文档，采用 Maven 多模块管理 20+ 依赖版本
> - 实现 JWT 双 Token 认证体系（Access 2h + Refresh 7d）、Redis Token 黑名单、AES 敏感字段加密
> - 开发对话式健康数据录入功能，设计 **AI 解析 + 规则引擎双轨策略**，AI 不可用时自动降级，
>   支持步数/体重/血压/血糖/心率 5 类指标的正则提取
> - 集成 DeepSeek API，编写 4 套 System Prompt 模板（数据解析/对话/建议/意图识别），
>   基于 WebFlux 实现 SSE 流式输出，设计全链路降级策略
> - 基于事件驱动架构（Spring Event + @Async）实现健康目标自动进度检查与达成判定，
>   数据写入与目标检查异步解耦，不阻塞用户请求

---

### 模板 C：详细版（适合微服务 / AI 方向岗位，突出技术深度）

> **AI 智能健康管理平台 — 基于 Spring Cloud 微服务 + DeepSeek AI Agent** | 独立开发
>
> **项目简介**：从零搭建的对话式健康管理 Web 平台，融合传统健康管理能力与 AI Agent 智能交互。
> 采用 Spring Cloud 微服务架构，集成 DeepSeek 大模型实现自然语言健康数据解析与智能对话。
>
> **架构设计**：
> - 6 微服务模块（Gateway / Auth / Health-Data / Goal / AI-Agent / Common），
>   Maven 多模块依赖管理，Nacos 服务注册与配置中心
> - Gateway 统一入口，全局 JWT Filter 校验 + CORS + 路由转发
> - 服务间同步调用（OpenFeign）+ 异步事件驱动（Spring Event + @Async）
>
> **AI 集成**：
> - 调用 DeepSeek API（deepseek-reasoner），基于 WebClient + WebFlux 实现同步调用与 SSE 流式输出
> - 设计 4 套 System Prompt 模板，通过 JSON 输出约束确保 AI 响应可解析，实现 Prompt Engineering 工程化
> - 全链路降级策略：AI 调用失败时各接口返回友好兜底内容，核心解析逻辑由正则规则引擎接管
>
> **业务功能**：
> - 对话式健康数据录入：用户自然语言输入 → AI 提取结构化 JSON → 入库 + 事件发布 → AI 温馨回复
> - 健康目标管理：规则推荐引擎生成目标，事件监听自动检查进度，达标自动标记完成
> - JWT 双 Token 认证：BCrypt 密码加密、AES 手机号加密脱敏、Redis Token 黑名单
>
> **技术栈**：Spring Boot 3.2.5 / Spring Cloud 2023.0.2 / MyBatis-Plus / Spring Security / JWT /
> Redis / MySQL / Nacos / DeepSeek / WebFlux SSE / Vue3 / Vite5 / ECharts5 / Java 17
