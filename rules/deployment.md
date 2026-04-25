# 部署流程规范

## 部署原则

**开发环境使用本地命令启动，生产环境使用 Docker 部署。**

| 环境 | 后端启动方式 | 前端启动方式 |
|------|-------------|-------------|
| **开发环境** | `mvn spring-boot:run -pl ruoyi-admin` | `npm run dev` |
| **生产环境** | Docker 容器 | Nginx Docker 容器 |

### 开发环境启动命令

#### 后端启动

```bash
cd RuoYi-Vue
mvn spring-boot:run -pl ruoyi-admin
```

#### 前端启动

```bash
cd RuoYi-Vue/ruoyi-ui
npm run dev
```

### 生产环境部署

生产环境使用 Docker 部署，参考下方「后端 Docker 部署」和「前端 Docker 部署」章节。

---

## 环境要求

### 基础环境
| 软件 | 版本要求 | 说明 |
|------|---------|------|
| JDK | 17+ | 编译运行 |
| Maven | 3.6+ | 构建工具 |
| Node.js | 14+ | 前端构建 |
| MySQL | 5.7+ / 8.0+ | 数据库 |
| Redis | 6.0+ | 缓存 |

### 本地服务配置
| 服务 | 端口 | 数据库名 | 用户名/密码 |
|------|------|---------|-----------|
| MySQL | 3306 | ry-vue | root/123456 |
| Redis | 6379 | - | 无密码 |

### 配置文件位置
- 数据库配置：`RuoYi-Vue/ruoyi-admin/src/main/resources/application-druid.yml`
- Redis配置：`RuoYi-Vue/ruoyi-admin/src/main/resources/application.yml`

---

## 后端 Docker 部署（生产环境）

### 1. Dockerfile

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
    network_mode: host
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
```

### 5. 停止服务

```bash
docker compose stop
docker compose down
```

---

## 前端 Docker 部署（生产环境）

### 1. Dockerfile

```dockerfile
# 构建阶段
FROM node:14-alpine AS builder

WORKDIR /app

COPY package*.json ./

RUN npm install --registry=https://registry.npmmirror.com

COPY . .

RUN npm run build:prod

# 运行阶段
FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html

COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]
```

### 2. nginx.conf

```nginx
server {
    listen 80;
    server_name localhost;

    location / {
        root /usr/share/nginx/html;
        index index.html;
        try_files $uri $uri/ /index.html;
    }

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
```

---

## 常见问题及解决方案

### 1. Docker 容器无法连接宿主机 MySQL/Redis

**解决方案**：使用 `network_mode: host` 或 `host.docker.internal`

```yaml
network_mode: host
```

### 2. 配置文件修改后未生效

```bash
# 清理并重新编译
mvn clean package -DskipTests
```

### 3. Docker 镜像拉取超时

使用国内镜像源或本地已有镜像。

---

## 常用 Docker 命令

```bash
# 查看容器状态
docker ps
docker ps -a

# 查看日志
docker logs ruoyi-backend
docker logs -f ruoyi-backend

# 重启容器
docker restart ruoyi-backend

# 停止容器
docker stop ruoyi-backend

# 清理资源
docker system prune -a
docker volume prune
```
