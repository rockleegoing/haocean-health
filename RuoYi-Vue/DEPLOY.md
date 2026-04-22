# RuoYi-Vue 快速部署指南

本文档提供基于 Docker 的快速部署方案。

## 部署架构

```
┌─────────────────────────────────────────────────────────┐
│                     宿主机                              │
│  ┌─────────────┐  ┌─────────────┐                      │
│  │   MySQL     │  │    Redis    │                      │
│  │  (3306)     │  │   (6379)    │                      │
│  └─────────────┘  └─────────────┘                      │
│         ▲                ▲                              │
│         │                │                              │
│  ┌─────────────────────────────────────┐               │
│  │    Docker Container (backend)       │               │
│  │  - 使用 host.docker.internal 连接   │               │
│  └─────────────────────────────────────┘               │
│  ┌─────────────────────────────────────┐               │
│  │    Docker Container (frontend)      │               │
│  │  - 代理到后端 8080                   │               │
│  └─────────────────────────────────────┘               │
└─────────────────────────────────────────────────────────┘
```

## 方案一：标准 Docker Compose 部署（推荐）

### 1. 编译打包

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue

# 后端打包
mvn clean package -DskipTests

# 前端打包（可选，如使用 Nginx 部署）
cd ruoyi-ui
npm install --registry=https://registry.npmmirror.com
npm run build:prod
cd ..
```

### 2. 启动服务

```bash
# 启动后端
docker-compose up -d ruoyi-backend

# 启动前端
docker-compose up -d ruoyi-frontend

# 查看日志
docker-compose logs -f
```

### 3. 验证部署

```bash
# 检查容器状态
docker-compose ps

# 测试后端接口
curl http://localhost:8080/ruoyi/admin

# 测试前端页面
curl http://localhost:80
```

### 4. 停止服务

```bash
docker-compose down
```

## 方案二：使用 host 网络模式

如果需要更简单的网络配置，可以使用 `network_mode: host`：

### 1. 修改 docker-compose.yml

```yaml
version: '3.8'

services:
  ruoyi-backend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: ruoyi-backend
    restart: always
    network_mode: host  # 使用宿主机网络
    environment:
      - TZ=Asia/Shanghai
    volumes:
      - /tmp/ruoyi/uploadPath:/tmp/ruoyi/uploadPath
      - /tmp/ruoyi/logs:/app/logs
```

### 2. 修改数据库配置

使用 `network_mode: host` 后，容器可以直接通过 `localhost` 访问宿主机服务：

```yaml
# application-druid.yml
datasource:
  url: jdbc:mysql://localhost:3306/ruoyi?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=CONVERT_TO_NULL&useSSL=false&serverTimezone=GMT%2B8
```

### 3. 启动服务

```bash
docker-compose up -d ruoyi-backend
```

## 目录挂载说明

### 后端容器

| 宿主机路径 | 容器路径 | 说明 |
|-----------|---------|------|
| `/tmp/ruoyi/uploadPath` | `/tmp/ruoyi/uploadPath` | 文件上传目录 |
| `/tmp/ruoyi/logs` | `/app/logs` | 日志目录 |

### 前端容器

| 宿主机路径 | 容器路径 | 说明 |
|-----------|---------|------|
| `/tmp/ruoyi/nginx/conf.d` | `/etc/nginx/conf.d` | Nginx 配置 |
| `/tmp/ruoyi/nginx/logs` | `/var/log/nginx` | Nginx 日志 |

## 常见问题

### 1. 容器无法连接数据库

**检查项：**
- 确保 MySQL 已启动且可访问
- 检查 `application-druid.yml` 中的数据库地址
- 使用 `host.docker.internal` 而非 `localhost`

### 2. 文件上传失败

**解决方案：**
```bash
# 创建上传目录
mkdir -p /tmp/ruoyi/uploadPath
chmod 755 /tmp/ruoyi/uploadPath

# 重启容器
docker-compose restart ruoyi-backend
```

### 3. 日志无法写入

**解决方案：**
```bash
# 创建日志目录
mkdir -p /tmp/ruoyi/logs
chmod 755 /tmp/ruoyi/logs

# 重启容器
docker-compose restart ruoyi-backend
```

## 生产环境部署

生产环境请使用专用部署脚本，并考虑：

1. 使用外部 Nginx 反向代理
2. 配置 HTTPS 证书
3. 数据库连接池调优
4. 日志轮转策略
5. 监控告警系统
