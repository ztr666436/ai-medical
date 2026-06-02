# 步骤 7 — Gateway + Vue3 前端（完整演示闭环）

## Gateway 模块（3 个文件）

```
ai-medical-care-gateway/
├── pom.xml                                    # Spring Cloud Gateway + Redis Reactive
├── src/main/resources/application.yml          # 端口 8080, 路由规则
└── src/main/java/com/medical/gateway/
    ├── GatewayApplication.java                 # @EnableDiscoveryClient
    ├── config/CorsConfig.java                  # CORS 跨域
    └── filter/JwtAuthFilter.java               # 全局 Token 校验
```

### Gateway 路由表

| 路径前缀 | 转发目标 | Token 校验 |
|----------|---------|-----------|
| `/api/auth/**` | auth-service (8081) | 白名单放行 |
| `/api/health/**` | health-data-service (8083) | ✅ 需要 Token |
| `/api/goal/**` | goal-service (8084) | ✅ 需要 Token |
| `/api/agent/**` | ai-agent-service (8085) | ✅ 需要 Token |

### JwtAuthFilter 核心逻辑
```
请求 → 提取 Authorization Header
  ├─ 白名单? → 直接放行
  ├─ Token 缺失/无效? → 401 JSON
  └─ Token 有效 → 注入 X-User-Id header → 转发
```

---

## Vue3 前端（3 个标签页）

```
ai-medical-care-web/
├── package.json                               # Vue3 + Vant4 + ECharts + Axios
├── vite.config.js                             # Vite 5 + /api 代理
└── index.html                                 # 完整 SPA（580行）
    ├── 🐧 阿福企鹅 Mascot（SVG 手绘）
    ├── 🔐 注册/登录页
    ├── 💬 对话页（阿福萌趣聊天 + 快捷回复）
    ├── 📊 健康仪表盘（步数/体重/心率/血压卡片）
    └── 🎯 健康目标（进度条 + 创建新目标）
```

### 视觉设计（蚂蚁阿福风格）

| 元素 | 设计 |
|------|------|
| 主色调 | #FF8C42 温暖橙色 |
| 背景 | #FFF8F3 暖白 |
| 卡片 | 圆角 16px + 微阴影 |
| 用户气泡 | 渐变蓝色右对齐 |
| 阿福气泡 | 白色左对齐 + 企鹅头像 |
| 动画 | 消息滑入 + 打字指示器 + 点弹跳 |

### 启动命令

```bash
# 后端（需先启动 MySQL、Redis、Nacos）
mvn clean compile
# 分别启动各服务...

# 前端
cd ai-medical-care-web
npm install
npm run dev
# 打开 http://localhost:5173
```

---

## 项目最终全景

```
共计: 55 个 Java 文件 + 17 个配置/脚本文件 = 72 个文件

ai-medical-care-agents/
├── pom.xml                                    # 父 POM
├── docs/                                      # 7 份架构文档
├── ai-medical-care-common/     (9 文件)       # 统一基础
├── ai-medical-care-gateway/    (4 文件, :8080) # API 网关
├── ai-medical-care-auth/       (13 文件, :8081) # 认证授权
├── ai-medical-care-health-data/(12 文件, :8083) # 健康数据
├── ai-medical-care-goal/       (10 文件, :8084) # 目标管理
├── ai-medical-care-ai-agent/   (7 文件, :8085)  # DeepSeek AI
└── ai-medical-care-web/        (3 文件)         # 前端 SPA
```

## 服务启动顺序

```bash
1. MySQL + Redis + Nacos
2. ai-medical-care-common (install)
3. auth → health-data → goal → ai-agent → gateway
4. cd ai-medical-care-web && npm run dev
```

---

> 🎉 **ai-medical-care-agents 项目 7 步架构指导全部完成！**
> 
> 从零到可运行的原型：72 个文件、6 个微服务、DeepSeek AI 集成、蚂蚁阿福风格前端！
