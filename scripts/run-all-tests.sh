#!/bin/bash
# ============================================================
# 脚本：run-all-tests.sh
# 功能：设备与认证模块 - 一键完整测试脚本
# 用法：./scripts/run-all-tests.sh [MySQL 密码]
# ============================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

# 配置
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
REPORT_DIR="$PROJECT_ROOT/test-reports"

# MySQL 密码
DB_PASS="$1"

# 测试统计
TOTAL_TESTS=0
TOTAL_PASSED=0
TOTAL_FAILED=0

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

section() {
    echo ""
    echo "============================================================"
    echo -e "${CYAN}$1${NC}"
    echo "============================================================"
    echo ""
}

# 创建报告目录
mkdir -p "$REPORT_DIR"

# 检查脚本
check_scripts() {
    section "检查测试脚本"

    local scripts=("init-device-auth-db.sh" "start-backend.sh" "test-device-auth-api.sh")
    local all_exist=true

    for script in "${scripts[@]}"; do
        if [ -f "$SCRIPT_DIR/$script" ]; then
            info "  ✓ $script"
        else
            error "  ✗ $script 不存在"
            all_exist=false
        fi
    done

    if [ "$all_exist" = false ]; then
        error "脚本文件不完整"
        exit 1
    fi

    # 赋予执行权限
    for script in "${scripts[@]}"; do
        chmod +x "$SCRIPT_DIR/$script"
    done

    info "脚本检查完成"
}

# 阶段 1: 初始化数据库
run_db_init() {
    section "阶段 1: 数据库初始化"

    if [ -n "$DB_PASS" ]; then
        "$SCRIPT_DIR/init-device-auth-db.sh" "$DB_PASS"
    else
        # 尝试使用环境变量
        if [ -n "$MYSQL_PWD" ]; then
            "$SCRIPT_DIR/init-device-auth-db.sh"
        else
            warn "未提供 MySQL 密码，请确保已配置 ~/.my.cnf 或设置 MYSQL_PWD 环境变量"
            "$SCRIPT_DIR/init-device-auth-db.sh"
        fi
    fi

    if [ $? -eq 0 ]; then
        TOTAL_PASSED=$((TOTAL_PASSED + 1))
    else
        TOTAL_FAILED=$((TOTAL_FAILED + 1))
        error "数据库初始化失败"
        exit 1
    fi

    TOTAL_TESTS=$((TOTAL_TESTS + 1))
}

# 阶段 2: 启动后端服务
run_backend_start() {
    section "阶段 2: 启动后端服务"

    "$SCRIPT_DIR/start-backend.sh"

    if [ $? -eq 0 ]; then
        TOTAL_PASSED=$((TOTAL_PASSED + 1))
    else
        TOTAL_FAILED=$((TOTAL_FAILED + 1))
        error "后端服务启动失败"
        exit 1
    fi

    TOTAL_TESTS=$((TOTAL_TESTS + 1))
}

# 阶段 3: API 自动化测试
run_api_tests() {
    section "阶段 3: API 自动化测试"

    "$SCRIPT_DIR/test-device-auth-api.sh"

    if [ $? -eq 0 ]; then
        TOTAL_PASSED=$((TOTAL_PASSED + 1))
    else
        TOTAL_FAILED=$((TOTAL_FAILED + 1))
        error "API 测试失败"
        # 继续执行，不退出
    fi

    TOTAL_TESTS=$((TOTAL_TESTS + 1))
}

# 阶段 4: 停止服务（清理）
cleanup() {
    section "阶段 4: 清理环境"

    info "停止后端服务..."
    cd "$PROJECT_ROOT/RuoYi-Vue"

    if command -v docker-compose &> /dev/null; then
        docker-compose down ruoyi-backend
    elif docker compose version &> /dev/null; then
        docker compose down ruoyi-backend
    else
        warn "未找到 Docker Compose，请手动停止容器"
    fi

    info "清理完成"
}

# 输出总结果
show_summary() {
    section "测试总结"

    echo "阶段测试结果:"
    echo ""

    if [ $TOTAL_FAILED -eq 0 ]; then
        echo -e "  ${GREEN}所有阶段测试通过！${NC}"
    else
        echo "  通过：${GREEN}$TOTAL_PASSED${NC}"
        echo "  失败：${RED}$TOTAL_FAILED${NC}"
    fi

    echo ""
    echo "总测试数：$TOTAL_TESTS"

    # 生成汇总报告
    local summary_file="$REPORT_DIR/summary-$(date +%Y%m%d-%H%M%S).txt"
    {
        echo "设备与认证模块 - 完整测试报告"
        echo "======================================"
        echo "时间：$(date '+%Y-%m-%d %H:%M:%S')"
        echo ""
        echo "阶段测试:"
        echo "  1. 数据库初始化：$([ $TOTAL_PASSED -ge 1 ] && echo '通过' || echo '失败')"
        echo "  2. 后端服务启动：$([ $TOTAL_PASSED -ge 2 ] && echo '通过' || echo '失败')"
        echo "  3. API 自动化测试：$([ $TOTAL_PASSED -ge 3 ] && echo '通过' || echo '部分失败')"
        echo ""
        echo "汇总:"
        echo "  总阶段数：$TOTAL_TESTS"
        echo "  通过：$TOTAL_PASSED"
        echo "  失败：$TOTAL_FAILED"
    } > "$summary_file"

    info "测试报告已保存：$summary_file"

    echo ""
    echo "============================================================"
    if [ $TOTAL_FAILED -eq 0 ]; then
        echo -e "${GREEN}  恭喜！所有测试通过！${NC}"
    else
        echo -e "${RED}  部分测试失败，请查看报告${NC}"
    fi
    echo "============================================================"
}

# 捕获错误
trap 'error "测试中断"; exit 1' INT TERM

# 主函数
main() {
    echo "============================================================"
    echo "  设备与认证模块 - 一键测试"
    echo "============================================================"
    echo ""
    echo "MySQL 密码：[已隐藏]"
    echo "报告目录：$REPORT_DIR"
    echo ""

    # 检查脚本
    check_scripts

    # 执行测试阶段
    run_db_init
    run_backend_start
    run_api_tests

    # 清理（可选）
    # cleanup

    # 输出总结
    show_summary

    # 退出码
    if [ $TOTAL_FAILED -eq 0 ]; then
        exit 0
    else
        exit 1
    fi
}

# 显示帮助
if [ "$1" = "-h" ] || [ "$1" = "--help" ]; then
    echo "用法：$0 [MySQL 密码]"
    echo ""
    echo "示例:"
    echo "  $0 your_password          # 直接传递密码"
    echo "  MYSQL_PWD=pass $0         # 使用环境变量"
    echo ""
    echo "环境配置:"
    echo "  编辑 scripts/init-device-auth-db.sh 修改："
    echo "    - DB_HOST: 数据库主机 (默认：127.0.0.1)"
    echo "    - DB_PORT: 数据库端口 (默认：3306)"
    echo "    - DB_USER: 数据库用户 (默认：root)"
    echo "    - DB_NAME: 数据库名称 (默认：ruoyi)"
    exit 0
fi

# 执行主函数
main
