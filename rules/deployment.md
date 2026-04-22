# 部署流程规范

## 部署原则（重要）

**Claude Code 在执行部署和更新操作时必须遵循以下要求：**

| 组件 | 部署方式 | 说明 |
|------|---------|------|
| **后端服务** | Docker 容器 | 必须使用 Docker 部署，禁止使用 ry.sh 或直接运行 jar |
| **前端服务** | Nginx Docker 容器 | 必须使用 Docker 部署，禁止使用本地 Nginx |
| **MySQL 数据库** | 本地环境 | 使用宿主机运行的 MySQL，不部署到 Docker |
| **Redis** | 本地环境 | 使用宿主机运行的 Redis，不部署到 Docker |

**禁止的部署方式：**
- 禁止使用 `ry.sh` 脚本启动服务
- 禁止使用 `nohup java -jar` 直接运行
- 禁止使用本地 Nginx 部署前端
- 禁止将 MySQL、Redis 部署到 Docker 容器

---

## 常见问题及解决方案

### Health 端点配置

**问题现象**: 添加健康检查端点后，`/prod-api/health` 返回 500 错误 `No static resource`

**原因分析**:
1. Controller 类级别使用 `@RequestMapping("/health")` 导致路径叠加
2. `@GetMapping("/prod-api/health")` 实际映射到 `/health/prod-api/health` 而非 `/prod-api/health`
3. 请求被 Spring MVC 当作静态资源处理

**正确配置**:

1. 创建 HealthController.java（不要使用类级别的 `@RequestMapping`）:
```java
package com.ruoyi.web.controller.monitor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

/**
 * 健康检查监控
 */
@RestController
public class HealthController {

    @GetMapping("/health")
    public Map<String, Object> health() {
        Map<String, Object> health = new HashMap<>();
        health.put("status", "UP");
        health.put("timestamp", System.currentTimeMillis());
        return health;
    }

    @GetMapping("/prod-api/health")
    public Map<String, Object> prodApiHealth() {
        return health();
    }
}
```

2. 在 SecurityConfig.java 中添加端点到白名单:
```java
requests.requestMatchers("/login", "/register", "/captchaImage", "/health", "/prod-api/health").permitAll()
```

3. 验证端点:
```bash
# 两个端点都应返回 200
curl http://localhost:8080/health
curl http://localhost:8080/prod-api/health
# 期望返回：{"status":"UP","timestamp":...}
```

### Docker 容器无法连接宿主机 MySQL/Redis

**问题现象**: 容器启动后无法连接数据库，报错 `Connection refused`

**原因分析**:
- Docker 容器内无法通过 `localhost` 访问宿主机服务
- 需要使用 `host.docker.internal` 或 host 网络模式

**解决方案**:

1. 修改数据库配置文件 (application-druid.yml)
```yaml
spring:
  datasource:
    url: jdbc:mysql://host.docker.internal:3306/ry-vue?useUnicode=true&characterEncoding=utf8&allowPublicKeyRetrieval=true
```

2. 修改 redis 配置 (application.yml)
```yaml
spring:
  data:
    redis:
      host: host.docker.internal
      port: 6379
```

3. docker-compose.yml 添加 extra_hosts
```yaml
services:
  app:
    extra_hosts:
      - "host.docker.internal:host-gateway"
```

### Docker 镜像拉取超时

**问题现象**: `docker build` 时无法从 Docker Hub 拉取镜像

**解决方案**:

1. 使用国内镜像源
```dockerfile
FROM registry.cn-hangzhou.aliyuncs.com/anycorp/temurin:17-jre-alpine
```

2. 使用本地已有镜像
```dockerfile
FROM nginx:latest  # 使用本地已有的 nginx 镜像
```

3. 配置 Docker 镜像加速器
```bash
# /etc/docker/daemon.json
{
  "registry-mirrors": [
    "https://docker.mirrors.ustc.edu.cn",
    "https://registry.docker-cn.com"
  ]
}
```

### 配置文件修改后未生效

**问题现象**: 修改配置文件后重新构建 Docker 镜像，但配置未生效

**原因分析**: 配置文件未重新编译到 jar 包中

**解决方案**:
```bash
# 清理并重新编译
mvn clean package -DskipTests

# 重新构建 Docker 镜像
docker compose build --no-cache
```

---

## 环境要求

### 基础环境
| 软件 | 版本要求 | 说明 |
|------|---------|------|
| Docker | 20.10+ | 容器运行环境 |
| Docker Compose | 2.0+ | 容器编排 |
| JDK | 17+ | 仅用于编译打包 |
| Maven | 3.6+ | 构建工具 |
| Node.js | 14+ | 前端构建 |

### 本地服务（宿主机运行）
| 服务 | 版本 | 端口 | 数据库名 | 用户名/密码 |
|------|------|------|---------|-----------|
| MySQL | 5.7+ / 8.0+ | 3306 | ry-vue | root/123456 |
| Redis | 6.0+ | 6379 | - | 无密码 |

---

## 后端 Docker 部署

### 1. Dockerfile

在项目根目录创建 `Dockerfile`：

```dockerfile
# 基于 OpenJDK 17
FROM openjdk:17-jdk-slim

# 设置时区
ENV TZ=Asia/Shanghai
RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

# 创建工作目录
WORKDIR /app

# 复制 jar 包
COPY ruoyi-admin/target/ruoyi-admin.jar app.jar

# 暴露端口
EXPOSE 8080

# JVM 参数
ENV JAVA_OPTS="-Xms256m -Xmx512m -Duser.timezone=Asia/Shanghai"

# 启动应用
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
```

### 2. docker-compose.yml

在项目根目录创建 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  ruoyi-backend:
    build:
      context: .
      dockerfile: Dockerfile
    container_name: ruoyi-backend
    restart: always
    ports:
      - "8080:8080"
    environment:
      - TZ=Asia/Shanghai
      # 数据库配置（使用宿主机 MySQL）
      - DB_HOST=host.docker.internal
      - DB_PORT=3306
      - DB_NAME=ry-vue
      - DB_USER=root
      - DB_PASSWORD=123456
      # Redis 配置（使用宿主机 Redis）
      - REDIS_HOST=host.docker.internal
      - REDIS_PORT=6379
    volumes:
      # 文件上传目录映射
      - /data/ruoyi/uploadPath:/home/ruoyi/uploadPath
      # 日志目录映射
      - /data/ruoyi/logs:/app/logs
    network_mode: host  # 使用宿主机网络，可直接访问 localhost
```

### 3. 构建和启动

```bash
cd RuoYi-Vue

# 1. 编译打包
mvn clean package -DskipTests

# 2. 构建 Docker 镜像
docker compose build

# 3. 启动容器
docker compose up -d

# 4. 查看状态
docker compose ps

# 5. 查看日志
docker compose logs -f ruoyi-backend
```

### 4. 更新部署

```bash
# 1. 重新编译
mvn clean package -DskipTests

# 2. 重新构建镜像
docker compose build

# 3. 重启容器
docker compose restart

# 或使用零停机更新
docker compose up -d --force-recreate
```

### 5. 停止服务

```bash
# 停止容器
docker compose stop

# 停止并删除容器
docker compose down

# 删除镜像
docker compose down --rmi all
```

---

## 前端 Docker 部署

### 1. Dockerfile

在 `ruoyi-ui/` 目录创建 `Dockerfile`：

```dockerfile
# 构建阶段
FROM node:14-alpine AS builder

WORKDIR /app

# 复制 package.json
COPY package*.json ./

# 安装依赖
RUN npm install --registry=https://registry.npmmirror.com

# 复制源代码
COPY . .

# 构建生产版本
RUN npm run build:prod

# 运行阶段
FROM nginx:alpine

# 复制构建产物
COPY --from=builder /app/dist /usr/share/nginx/html

# 复制 Nginx 配置
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

### 2. nginx.conf

在 `ruoyi-ui/` 目录创建 `nginx.conf`：

```nginx
server {
    listen 80;
    server_name localhost;

    # 前端静态文件
    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

    # 后端 API 代理（使用宿主机地址）
    location /prod-api/ {
        proxy_pass http://host.docker.internal:8080/;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
    }
}
```

### 3. 构建和启动

```bash
cd RuoYi-Vue/ruoyi-ui

# 1. 构建 Docker 镜像
docker build -t ruoyi-frontend:latest .

# 2. 启动容器
docker run -d \
  --name ruoyi-frontend \
  --restart always \
  -p 80:80 \
  ruoyi-frontend:latest

# 3. 查看状态
docker ps

# 4. 查看日志
docker logs -f ruoyi-frontend
```

### 4. 更新部署

```bash
# 1. 重新构建镜像
docker build -t ruoyi-frontend:latest .

# 2. 停止旧容器
docker stop ruoyi-frontend

# 3. 删除旧容器
docker rm ruoyi-frontend

# 4. 启动新容器
docker run -d \
  --name ruoyi-frontend \
  --restart always \
  -p 80:80 \
  ruoyi-frontend:latest
```

---

## 本地数据库配置

### 配置信息

| 配置项 | 值 |
|--------|-----|
| MySQL 主机 | localhost:3306 |
| 数据库名 | ry-vue |
| 用户名 | root |
| 密码 | 123456 |
| Redis 主机 | localhost:6379 |
| Redis 密码 | 无 |

### 配置文件位置

- 数据库配置：`RuoYi-Vue/ruoyi-admin/src/main/resources/application-druid.yml`
- 主配置：`RuoYi-Vue/ruoyi-admin/src/main/resources/application.yml`

### application-druid.yml 配置示例

```yaml
spring:
    datasource:
        druid:
            master:
                url: jdbc:mysql://localhost:3306/ry-vue?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=convertToNull&useSSL=true&serverTimezone=GMT%2B8
                username: root
                password: 123456
```

---

## Docker 容器连接本地数据库

由于 MySQL 和 Redis 运行在宿主机，Docker 容器需要通过特殊方式访问：

### 方式一：使用 network_mode=host（推荐）

在 docker-compose.yml 中配置：
```yaml
network_mode: host
```

然后 application.yml 中使用 localhost：
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/ry-vue?useUnicode=true&characterEncoding=utf8
  data:
    redis:
      host: localhost
      port: 6379
```

### 方式二：使用 host.docker.internal

```yaml
spring:
  datasource:
    url: jdbc:mysql://host.docker.internal:3306/ry-vue
  data:
    redis:
      host: host.docker.internal
```

---

## 部署检查清单

### 部署前

- [ ] Docker 和 Docker Compose 已安装
- [ ] MySQL 和 Redis 服务正在运行
- [ ] 数据库连接测试通过
- [ ] 代码已编译通过
- [ ] 配置文件已更新

### 部署中

- [ ] Docker 镜像构建成功
- [ ] 容器启动成功
- [ ] 容器状态为 Up
- [ ] 日志无启动错误

### 部署后

- [ ] 后端接口可访问（http://localhost:8080）
- [ ] 前端页面可访问（http://localhost）
- [ ] 登录功能测试通过
- [ ] 数据库连接正常
- [ ] Redis 连接正常

---

## 常用 Docker 命令

```bash
# 查看容器状态
docker ps
docker ps -a

# 查看日志
docker logs ruoyi-backend
docker logs -f ruoyi-backend        # 实时查看
docker logs --tail 100 ruoyi-backend # 查看最近 100 行

# 进入容器
docker exec -it ruoyi-backend bash

# 重启容器
docker restart ruoyi-backend

# 停止容器
docker stop ruoyi-backend

# 启动容器
docker start ruoyi-backend

# 查看容器详情
docker inspect ruoyi-backend

# 清理资源
docker system prune -a    # 清理未使用的镜像和容器
docker volume prune       # 清理未使用的卷
```

---

## 常见问题

### 1. 容器无法访问宿主机 MySQL/Redis

**解决方案一**：使用 `network_mode: host`
```yaml
network_mode: host
```

**解决方案二**：使用 `host.docker.internal`
```bash
# Linux 需要额外配置
docker run --add-host=host.docker.internal:host-gateway ...
```

### 2. 端口被占用

```bash
# 查看占用端口的容器
docker ps --filter "publish=8080"

# 停止占用端口的容器
docker stop <container_id>
```

### 3. 容器启动后立即退出

```bash
# 查看日志
docker logs ruoyi-backend

# 常见原因：
# - 数据库连接失败
# - 端口被占用
# - JVM 内存不足
```

### 4. 文件上传失败

确保 volumes 映射正确：
```yaml
volumes:
  - /data/ruoyi/uploadPath:/home/ruoyi/uploadPath
```

并检查目录权限：
```bash
chmod 755 /data/ruoyi/uploadPath
```
