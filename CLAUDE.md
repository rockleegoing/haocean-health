# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

全程使用中文进行交流

## 项目概述

本项目包含两个独立的子项目：

1. **RuoYi-Vue** - 基于 Spring Boot + Vue 的前后端分离管理系统
2. **Ruoyi-Android-App** - 基于 Kotlin 的 Android 移动端应用

## RuoYi-Vue (后端)

### 技术栈
- Spring Boot 4.0.3 (JDK 17+)
- Spring Security + Redis + JWT 权限认证
- MyBatis + Druid 连接池
- MySQL 数据库

### 模块结构
- `ruoyi-admin` - 入口服务
- `ruoyi-framework` - 框架核心
- `ruoyi-system` - 系统模块（含 supervision、regulation、legalBasis 等业务域）
- `ruoyi-quartz` - 定时任务
- `ruoyi-generator` - 代码生成器
- `ruoyi-common` - 通用工具

### 开发命令
```bash
cd RuoYi-Vue

# 后端启动
mvn spring-boot:run -pl ruoyi-admin

# 后端打包
mvn clean package -DskipTests
```

### 配置文件
- 主配置：`ruoyi-admin/src/main/resources/application.yml`
- 数据库：`ruoyi-admin/src/main/resources/application-druid.yml`
- SQL 脚本：`sql/` 目录（按版本命名，如 `V1.1.0__sys_regulation_tables.sql`）
- 默认端口：8080

## RuoYi-Vue (前端)

### 技术栈
- Vue 2.6 + Element UI
- Vue CLI 4.x

### 开发命令
```bash
cd RuoYi-Vue/ruoyi-ui

# 安装依赖
npm install --registry=https://registry.npmmirror.com

# 开发启动
npm run dev

# 生产构建
npm run build:prod
```

### 配置文件
- 开发环境：`.env.development`
- 生产环境：`.env.production`
- 代理配置：`vue.config.js`

### 前端 API 结构
- `src/api/system/` - 系统管理 API（regulation.js、supervision.js 等）

## Ruoyi-Android-App

### 技术栈
- Kotlin + MVVM + DataBinding
- AndroidX
- 网络库：Retrofit + OkHttp
- 路由：TheRouter
- 图片加载：Glide

### 模块结构
- `app/feature/law/` - 法律法规模块
- `app/feature/phrase/` - 规范用语模块
- `app/feature/supervision/` - 监督执法模块
- `app/data/database/` - 本地数据库（Room）
- `app/sync/` - 数据同步管理

### 环境要求
- Android Studio 2024.3.2+
- Android Gradle Plugin 8.13.2 (需要 Gradle 8.x)
- JDK 11

### 开发命令
```bash
cd Ruoyi-Android-App

# 清理项目
./gradlew clean

# 编译 Debug
./gradlew assembleDebug

# 编译 Release APK
./gradlew assembleRelease
```

### 关键配置
- 编译配置：`build.gradle` (root)
- 应用配置：`app/build.gradle`
- 签名配置：`config/key.properties`

## 通用说明

- 默认登录账号：admin / admin123
- Redis 默认端口：6379
- 文件上传限制：单个 10MB，总共 20MB

## 开发规则

本项目在 `rules/` 目录下定义了开发规范，Claude Code 在工作时应遵循这些规则：

### 开发规范
- `rules/general.md` - 通用开发规范（代码风格、命名约定、最佳实践）
- `rules/commit.md` - Git 提交规范（Commit Message 格式、分支管理、分支保护）
- `rules/code-review.md` - 代码审查规范（Review Checklist、质量要求）
- `rules/testing.md` - 测试规范（测试层级、运行命令、测试原则）
- `rules/gitignore.md` - .gitignore 和环境配置规范
- `rules/api-doc.md` - API 文档规范（Swagger、前后端 API 管理）
- `rules/logging.md` - 日志规范（日志级别、配置、最佳实践）
- `rules/mobile-android.md` - Android 移动端开发规范（MVVM、离线同步、蓝牙打印）
- `rules/sync-conflict.md` - 数据同步与冲突处理规范（上行/下行同步、冲突解决策略）
- `rules/ota-updates.md` - OTA 在线升级管理规范（强制更新、建议更新、静默更新）
- `rules/document-template.md` - 文书模板管理规范（37 种标准文书模板）

### 部署规范
- `rules/deployment.md` - 部署流程规范（后端、前端、Android 部署）
- `rules/database.md` - 数据库管理规范（SQL 脚本、变更流程、备份恢复）

在执行代码编辑、提交、审查等操作时，应主动参考相应规则文件的要求。

## API 调用强制规范

**在编写前端 (Vue) 和 Android (Kotlin) API 调用代码之前，必须先阅读后端 Controller 实现**

### 操作流程

1. **定位 Controller** - 使用 Grep/Glob 找到对应的后端 Controller 类
2. **阅读接口实现** - 确认接口路径、请求方法、参数名称、参数类型
3. **验证参数细节** - 特别注意 `@RequestBody` / `@PathVariable` / `@RequestParam` 的使用
4. **编写调用代码** - 确保参数名称、类型、位置与后端完全一致
5. Conversation compacted 完成后续主动重新阅读规则文件

### 常见错误预防

| 错误类型 | 说明 | 预防方法 |
|---------|------|---------|
| 参数名不一致 | 后端要 `deviceUuid`，前端传 `deviceId` | 复制后端参数名，不臆测 |
| 类型不匹配 | 后端要 `String`，前端传 `Long` | 检查后端参数类型声明 |
| 参数位置错误 | 后端用 `@RequestBody`，前端放 URL | 确认后端接收方式注解 |

### 技能使用

遇到 API 调用任务时，自动使用 `api-call-best-practice` 技能。

## 规则自我完善

当按照规则执行失败时：
1. 在 `rules/CHANGELOG.md` 中记录失败情况
2. 找到正确方法后立即更新对应规则文件
3. 提交变更并使用 `chore(rules): 修正 xxx 规则` 的 commit message
4. 在 `rules/CHANGELOG.md` 中记录修正方案
