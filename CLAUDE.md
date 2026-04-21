# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## 项目概述

本项目包含两个独立的子项目：

1. **RuoYi-Vue** - 基于 Spring Boot + Vue 的前后端分离管理系统
2. **Ruoyi-Android-App** - 基于 Kotlin 的 Android 移动端应用

## RuoYi-Vue (后端)

### 技术栈
- Spring Boot 4.x (JDK 17+)
- Spring Security + Redis + JWT 权限认证
- MyBatis + Druid 连接池
- MySQL 数据库

### 模块结构
- `ruoyi-admin` - 入口服务
- `ruoyi-framework` - 框架核心
- `ruoyi-system` - 系统模块
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

## Ruoyi-Android-App

### 技术栈
- Kotlin + MVVM + DataBinding
- AndroidX
- 网络库：Retrofit + OkHttp
- 路由：TheRouter
- 图片加载：Glide

### 环境要求
- Android Studio 2024.3.2+
- Gradle 7.5
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
- `rules/gitignore.md` - .gitignore 和环境配置规范（新增）
- `rules/api-doc.md` - API 文档规范（Swagger、前后端 API 管理）（新增）
- `rules/logging.md` - 日志规范（日志级别、配置、最佳实践）（新增）

### 部署规范
- `rules/deployment.md` - 部署流程规范（后端、前端、Android 部署）
- `rules/database.md` - 数据库管理规范（SQL 脚本、变更流程、备份恢复）

在执行代码编辑、提交、审查等操作时，应主动参考相应规则文件的要求。
