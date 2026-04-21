# .gitignore 和环境配置规范

## .gitignore 规则

### 通用忽略规则

项目根目录应包含 `.gitignore` 文件，以下内容必须被忽略：

```gitignore
# ==================== 通用 ====================
# 操作系统文件
.DS_Store
Thumbs.db
*.swp
*.swo
*~

# IDE 配置文件
.idea/
.vscode/
*.iml
*.ipr
*.iws

# 构建产物
target/
build/
dist/
*.class
*.jar
*.war
*.ear

# 依赖目录
node_modules/
.gradle/
local.properties

# 日志文件
*.log
logs/

# 临时文件
tmp/
temp/
*.tmp

# ==================== 后端 (Maven) ====================
# Maven
target/
pom.xml.tag
pom.xml.releaseBackup
pom.xml.versionsBackup
pom.xml.next
release.properties
dependency-reduced-pom.xml
buildNumber.properties
.mvn/timing.properties
.mvn/wrapper/maven-wrapper.jar

# Spring Boot
*.pid
.mvn/wrapper/maven-wrapper.jar

# ==================== 前端 (Vue) ====================
# Vue CLI
dist/
node_modules/
*.local
.eslintcache
*_coverage/
*.lcov

# 环境文件
.env
.env.local
.env.*.local

# ==================== Android ====================
# Android/Gradle
*.iml
.gradle/
local.properties
.idea/caches
.idea/libraries
.idea/modules.xml
.idea/workspace.xml
.idea/navEditor.xml
.idea/assetWizardSettings.xml
.idea/shelf/
.DS_Store
/build
/captures
.externalNativeBuild
.cxx
local.properties

# 签名密钥库（重要：禁止提交）
*.jks
*.keystore
keystore.properties
config/key.properties

# ==================== 敏感信息 ====================
# 配置文件（包含密码、密钥）
application-local.yml
application-local.properties
*-local.yml
*-local.properties

# 密钥文件
*.pem
*.key
*.crt
*.p12

# 数据库文件
*.sql.gz
*.dump

# 上传文件
uploadPath/
profile/
```

---

## 环境变量管理规范

### 配置文件层级

| 文件 | 用途 | 是否提交 |
|------|------|----------|
| `.env.example` | 环境变量模板 | ✅ 必须提交 |
| `.env.development` | 开发环境配置 | ✅ 可提交（无敏感信息） |
| `.env.production` | 生产环境配置 | ✅ 可提交（无敏感信息） |
| `.env.local` | 本地个人配置 | ❌ 禁止提交 |
| `.env` | 当前环境配置 | ❌ 禁止提交 |

### .env.example 模板示例

#### 后端 (application-local.yml.example)

```yaml
# ============================================
# 若依项目本地配置示例
# 复制此文件为 application-local.yml 并修改相应配置
# ============================================

spring:
  # 数据库配置
  datasource:
    druid:
      master:
        url: jdbc:mysql://localhost:3306/ry-vue?useUnicode=true&characterEncoding=utf8
        username: ${DB_USERNAME:root}
        password: ${DB_PASSWORD:123456}

  # Redis 配置
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      password: ${REDIS_PASSWORD:}

# JWT 密钥（生产环境必须修改）
jwt:
  secret: ${JWT_SECRET:ruoyi-secret-key-change-in-production}

# 文件上传路径
ruoyi:
  profile: ${UPLOAD_PATH:/tmp/ruoyi-upload}

# 邮件配置（如需要）
mail:
  host: ${MAIL_HOST:smtp.example.com}
  username: ${MAIL_USERNAME:}
  password: ${MAIL_PASSWORD:}
```

#### 前端 (.env.example)

```bash
# 开发环境 API 基础路径
VUE_APP_BASE_API = 'http://localhost:8080'

# 生产环境 API 基础路径（使用相对路径，通过 Nginx 代理）
# VUE_APP_BASE_API = '/prod-api'
```

#### Android (config/key.properties.example)

```properties
# ============================================
# Android 签名配置示例
# 复制此文件为 key.properties 并填写真实配置
# ============================================

# 是否启用签名（Debug 构建可设为 false）
enableSigning=true

# 密钥库信息
storeFilePath=../keystore/release.keystore
storePassword=YOUR_STORE_PASSWORD
keyAlias=your-key-alias
keyPassword=YOUR_KEY_PASSWORD

# 注意：实际的 .keystore 文件不应提交到代码库
```

---

## 敏感信息处理

### 禁止提交的内容

- [ ] 数据库密码
- [ ] Redis 密码
- [ ] JWT 密钥
- [ ] API 密钥（第三方服务）
- [ ] SSH 私钥
- [ ] 签名密钥库（.jks/.keystore）
- [ ] 个人访问令牌（Token）
- [ ] 邮箱/短信服务凭证

### 配置分离策略

```yaml
# ✅ 推荐：使用环境变量占位符
spring:
  datasource:
    password: ${DB_PASSWORD}

# ❌ 不推荐：硬编码密码
spring:
  datasource:
    password: admin123
```

### CI/CD 中的敏感信息

在 CI/CD 平台（GitHub Actions、GitLab CI 等）中使用 Secrets 存储敏感信息：

```yaml
# GitHub Actions 示例
name: Deploy

on: [push]

jobs:
  deploy:
    steps:
      - name: Deploy
        env:
          DB_PASSWORD: ${{ secrets.DB_PASSWORD }}
          JWT_SECRET: ${{ secrets.JWT_SECRET }}
        run: ./deploy.sh
```

---

## 检查清单

### 提交前检查

```bash
# 1. 检查是否有敏感文件被误提交
git status

# 2. 检查提交内容
git diff --cached

# 3. 使用 git-secrets 或 gitleaks 扫描敏感信息
gitleaks detect --source . -v
```

### 误提交处理

```bash
# 如果敏感信息已提交，需要彻底清除
# 1. 使用 BFG Repo-Cleaner
bfg --delete-files key.properties

# 2. 强制推送（谨慎使用）
git push --force

# 3. 通知团队成员重新克隆仓库
```
