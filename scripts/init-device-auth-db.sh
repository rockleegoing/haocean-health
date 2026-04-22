#!/bin/bash
# ============================================================
# 脚本：init-device-auth-db.sh
# 功能：设备与认证模块 - 数据库初始化脚本
# 用法：./scripts/init-device-auth-db.sh [密码]
# ============================================================

set -e

# 颜色定义
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 配置
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(dirname "$SCRIPT_DIR")"
SQL_DIR="$PROJECT_ROOT/RuoYi-Vue/sql"

# 数据库配置（可通过环境变量覆盖）
DB_HOST="${DB_HOST:-127.0.0.1}"
DB_PORT="${DB_PORT:-3306}"
DB_USER="${DB_USER:-root}"
DB_PASS="${DB_PASS:-$1}"
DB_NAME="${DB_NAME:-ruoyi}"

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

# 检查 MySQL 密码
check_db_password() {
    if [ -z "$DB_PASS" ]; then
        # 尝试从配置文件读取
        if [ -f "$HOME/.my.cnf" ]; then
            info "使用 ~/.my.cnf 中的配置"
            MYSQL_EXTRA_ARGS="--defaults-file=$HOME/.my.cnf"
        else
            error "未提供 MySQL 密码，请设置 MYSQL_PWD 环境变量或作为参数传递"
            echo "用法：$0 [密码]"
            echo "   或：MYSQL_PWD=your_password $0"
            exit 1
        fi
    else
        MYSQL_EXTRA_ARGS="-p$DB_PASS"
    fi
}

# 检查 MySQL 连接
check_mysql_connection() {
    info "检查 MySQL 连接..."
    if ! mysql $MYSQL_EXTRA_ARGS -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -e "SELECT 1;" > /dev/null 2>&1; then
        error "无法连接到 MySQL，请检查："
        echo "  - MySQL 服务是否启动"
        echo "  - 用户名密码是否正确"
        echo "  - 主机地址和端口是否正确"
        exit 1
    fi
    info "MySQL 连接成功"
}

# 检查数据库是否存在
check_database() {
    info "检查数据库 $DB_NAME..."
    DB_EXISTS=$(mysql $MYSQL_EXTRA_ARGS -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -e "SHOW DATABASES LIKE '$DB_NAME';" | grep "$DB_NAME" || true)

    if [ -z "$DB_EXISTS" ]; then
        warn "数据库 $DB_NAME 不存在，正在创建..."
        mysql $MYSQL_EXTRA_ARGS -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" -e "CREATE DATABASE $DB_NAME DEFAULT CHARACTER SET utf8mb4;"
    fi
    info "数据库检查完成"
}

# 执行 SQL 脚本
run_sql_file() {
    local sql_file="$1"
    local description="$2"

    if [ ! -f "$sql_file" ]; then
        error "SQL 文件不存在：$sql_file"
        exit 1
    fi

    info "执行 $description: $sql_file"
    if mysql $MYSQL_EXTRA_ARGS -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" "$DB_NAME" < "$sql_file"; then
        info "$description 执行成功"
    else
        error "$description 执行失败"
        exit 1
    fi
}

# 验证表是否创建
verify_tables() {
    info "验证表结构..."

    local tables=("sys_activation_code" "sys_device")
    local all_exist=true

    for table in "${tables[@]}"; do
        TABLE_EXISTS=$(mysql $MYSQL_EXTRA_ARGS -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" "$DB_NAME" -e "SHOW TABLES LIKE '$table';" | grep "$table" || true)
        if [ -n "$TABLE_EXISTS" ]; then
            info "  ✓ 表 $table 已创建"
        else
            error "  ✗ 表 $table 不存在"
            all_exist=false
        fi
    done

    if [ "$all_exist" = false ]; then
        error "表结构验证失败"
        exit 1
    fi

    info "表结构验证成功"
}

# 验证测试数据
verify_test_data() {
    info "验证测试数据..."

    local code_count=$(mysql $MYSQL_EXTRA_ARGS -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" "$DB_NAME" -N -e "SELECT COUNT(*) FROM sys_activation_code WHERE code_value LIKE 'TEST%';")

    if [ "$code_count" -gt 0 ]; then
        info "  ✓ 测试激活码：$code_count 个"
        echo ""
        echo "    激活码列表:"
        mysql $MYSQL_EXTRA_ARGS -h "$DB_HOST" -P "$DB_PORT" -u "$DB_USER" "$DB_NAME" -e "SELECT code_value, status, expire_time FROM sys_activation_code WHERE code_value LIKE 'TEST%' ORDER BY code_value;"
    else
        warn "  未找到测试激活码数据"
    fi
}

# 主函数
main() {
    echo "============================================================"
    echo "  设备与认证模块 - 数据库初始化"
    echo "============================================================"
    echo ""

    # 检查密码
    check_db_password

    # 检查连接
    check_mysql_connection

    # 检查数据库
    check_database

    # 执行表结构脚本
    run_sql_file "$SQL_DIR/V1.0.0__device_auth_tables.sql" "表结构初始化"

    # 执行测试数据脚本
    run_sql_file "$SQL_DIR/V1.0.0__device_auth_data.sql" "测试数据插入"

    # 验证表结构
    verify_tables

    # 验证测试数据
    verify_test_data

    echo ""
    echo "============================================================"
    info "数据库初始化完成！"
    echo "============================================================"
}

# 执行主函数
main
