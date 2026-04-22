#!/bin/bash
# ============================================================
# 脚本：start-backend.sh
# 功能：后端服务启动脚本（Docker 方式）
# 用法：./scripts/start-backend.sh [选项]
# 选项：--build    强制重新编译
#       --stop    停止服务
#       --restart 重启服务
# ============================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# 配置
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
BACKEND_DIR="$PROJECT_ROOT/RuoYi-Vue"

# 参数解析
FORCE_BUILD=false
STOP_ONLY=false
RESTART=false

while [[ $# -gt 0 ]]; do
    case $1 in
        --build)
            FORCE_BUILD=true
            shift
            ;;
        --stop)
            STOP_ONLY=true
            shift
            ;;
        --restart)
            RESTART=true
            shift
            ;;
        *)
            echo "未知参数：$1"
            echo "用法：$0 [--build] [--stop] [--restart]"
            exit 1
            ;;
    esac
done

# 打印函数
info() {
    echo -e "${GREEN}[INFO]${NC} $1"
}

warn() {
    echo -e "${YELLOW}[WARN]${NC} $1"
}

error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

step() {
    echo -e "${BLUE}[STEP]${NC} $1"
}

# 检查 Docker 是否运行
check_docker() {
    if ! command -v docker &> /dev/null; then
        error "Docker 未安装"
        exit 1
    fi

    if ! docker info &> /dev/null; then
        error "Docker 服务未运行"
        exit 1
    fi

    info "Docker 服务正常"
}

# 检查 Docker Compose
check_docker_compose() {
    if command -v docker-compose &> /dev/null; then
        DOCKER_COMPOSE_CMD="docker-compose"
    elif docker compose version &> /dev/null; then
        DOCKER_COMPOSE_CMD="docker compose"
    else
        error "Docker Compose 未安装"
        exit 1
    fi

    info "Docker Compose: $DOCKER_COMPOSE_CMD"
}

# 停止服务
stop_service() {
    step "停止后端服务..."
    cd "$BACKEND_DIR"
    if $DOCKER_COMPOSE_CMD ps | grep -q ruoyi-backend; then
        $DOCKER_COMPOSE_CMD down ruoyi-backend
        info "后端服务已停止"
    else
        warn "后端服务未运行"
    fi
}

# 编译项目
build_project() {
    step "编译后端项目..."
    cd "$BACKEND_DIR"

    # 检查是否已编译
    if [ -f "ruoyi-admin/target/ruoyi-admin.jar" ] && [ "$FORCE_BUILD" = false ]; then
        info "检测到已编译的 JAR 文件，跳过编译"
        info "如需强制编译，请使用 --build 参数"
    else
        info "开始 Maven 编译..."
        if command -v mvn &> /dev/null; then
            mvn clean package -DskipTests -q
            info "编译完成"
        else
            error "Maven 未安装，无法编译项目"
            exit 1
        fi
    fi
}

# 启动服务
start_service() {
    step "启动后端服务..."
    cd "$BACKEND_DIR"

    # 检查 Docker 网络配置
    if ! grep -q "host.docker.internal" docker-compose.yml 2>/dev/null; then
        warn "docker-compose.yml 中未配置 host.docker.internal"
        warn "请确保 MySQL 可通过 127.0.0.1 访问"
    fi

    # 启动容器
    $DOCKER_COMPOSE_CMD up -d ruoyi-backend

    # 等待服务启动
    info "等待服务启动..."
    local max_attempts=30
    local attempt=0

    while [ $attempt -lt $max_attempts ]; do
        sleep 2
        attempt=$((attempt + 1))

        # 检查容器状态
        if ! $DOCKER_COMPOSE_CMD ps | grep -q "ruoyi-backend.*Up"; then
            if [ $attempt -eq $max_attempts ]; then
                error "容器启动失败，请查看日志："
                $DOCKER_COMPOSE_CMD logs ruoyi-backend
                exit 1
            fi
            continue
        fi

        # 检查 HTTP 健康
        if curl -s http://localhost:8080/ruoyi/admin > /dev/null 2>&1; then
            info "服务启动成功！"
            break
        fi

        if [ $attempt -eq $max_attempts ]; then
            error "服务健康检查失败"
            $DOCKER_COMPOSE_CMD logs ruoyi-backend
            exit 1
        fi

        echo -n "."
    done

    echo ""
}

# 健康检查
health_check() {
    step "执行健康检查..."

    # 检查端口
    if ! lsof -i :8080 > /dev/null 2>&1; then
        warn "端口 8080 未被监听"
    else
        info "端口 8080：✓"
    fi

    # 检查 API
    local response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/ruoyi/admin)
    if [ "$response" = "302" ] || [ "$response" = "200" ]; then
        info "API 健康检查：✓ (HTTP $response)"
    else
        warn "API 健康检查：✗ (HTTP $response)"
    fi

    # 检查 Swagger
    response=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:8080/swagger-ui/index.html)
    if [ "$response" = "200" ]; then
        info "Swagger UI：✓"
    else
        warn "Swagger UI：✗"
    fi
}

# 显示服务状态
show_status() {
    echo ""
    echo "============================================================"
    echo "  后端服务状态"
    echo "============================================================"

    cd "$BACKEND_DIR"
    $DOCKER_COMPOSE_CMD ps

    echo ""
    echo "服务访问地址:"
    echo "  - API:      http://localhost:8080/ruoyi/admin"
    echo "  - Swagger:  http://localhost:8080/swagger-ui/index.html"
    echo ""
    echo "日志查看:"
    echo "  $DOCKER_COMPOSE_CMD logs -f ruoyi-backend"
    echo ""
}

# 主函数
main() {
    echo "============================================================"
    echo "  后端服务启动脚本"
    echo "============================================================"
    echo ""

    # 检查依赖
    check_docker
    check_docker_compose

    # 处理停止
    if [ "$STOP_ONLY" = true ]; then
        stop_service
        exit 0
    fi

    # 处理重启
    if [ "$RESTART" = true ]; then
        stop_service
        RESTART=false
    fi

    # 编译项目
    build_project

    # 启动服务
    start_service

    # 健康检查
    health_check

    # 显示状态
    show_status
}

# 执行主函数
main
