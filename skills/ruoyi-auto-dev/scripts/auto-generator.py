#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
RuoYi 代码生成全自动脚本
下载 ZIP 并自动解压复制到项目正确位置
"""

import requests
import zipfile
import shutil
import sys
import os
from pathlib import Path
from typing import Dict, List, Optional

# ==================== 配置区 ====================

# 后端 API 配置
BASE_URL = "http://localhost:8080"
LOGIN_API = f"{BASE_URL}/login"
GEN_API_BASE = f"{BASE_URL}/tool/gen"
DEFAULT_USERNAME = "admin"
DEFAULT_PASSWORD = "admin123"

# 项目路径配置
SCRIPT_DIR = Path(__file__).parent
PROJECT_ROOT = SCRIPT_DIR.parent.parent  # projectV4
RuoYi_VUE = PROJECT_ROOT / "RuoYi-Vue"

# 目标路径映射
TARGET_PATHS = {
    # 后端代码
    "ruoyi-admin/src/main/java": RuoYi_VUE / "ruoyi-admin/src/main/java",
    "ruoyi-common/src/main/java": RuoYi_VUE / "ruoyi-common/src/main/java",
    "ruoyi-system/src/main/java": RuoYi_VUE / "ruoyi-system/src/main/java",
    "ruoyi-generator/src/main/java": RuoYi_VUE / "ruoyi-generator/src/main/java",
    # Mapper XML
    "ruoyi-system/src/main/resources/mapper": RuoYi_VUE / "ruoyi-system/src/main/resources/mapper",
    "ruoyi-generator/src/main/resources/mapper": RuoYi_VUE / "ruoyi-generator/src/main/resources/mapper",
    # 前端代码
    "vue/views": RuoYi_VUE / "ruoyi-ui/src/views",
    "vue/api": RuoYi_VUE / "ruoyi-ui/src/api",
    "vue/types": RuoYi_VUE / "ruoyi-ui/src/types",
}


class RuoYiCodeGenerator:
    """RuoYi 代码生成器客户端"""

    def __init__(self, username: str = DEFAULT_USERNAME, password: str = DEFAULT_PASSWORD):
        self.username = username
        self.password = password
        self.token = None
        self.headers = {}

    def login(self) -> bool:
        """登录获取 token"""
        try:
            response = requests.post(LOGIN_API, json={
                "username": self.username,
                "password": self.password,
                "code": "",
                "uuid": ""
            })
            if response.status_code == 200:
                data = response.json()
                self.token = data.get("token")
                self.headers["Authorization"] = f"Bearer {self.token}"
                print(f"✓ 登录成功：{self.username}")
                return True
            else:
                print(f"✗ 登录失败：{response.text}")
                return False
        except Exception as e:
            print(f"✗ 登录异常：{e}")
            return False

    def create_table(self, sql: str, tpl_web_type: str = "vue") -> bool:
        """创建表并导入到生成器"""
        url = f"{GEN_API_BASE}/createTable"
        data = {"sql": sql, "tplWebType": tpl_web_type}
        try:
            response = requests.post(url, data=data, headers=self.headers)
            if response.status_code == 200:
                result = response.json()
                if result.get("code") == 200:
                    print(f"✓ 表创建成功")
                    return True
                else:
                    print(f"✗ 创建表失败：{result.get('msg')}")
                    return False
            else:
                print(f"✗ HTTP 错误：{response.status_code}")
                return False
        except Exception as e:
            print(f"✗ 创建表异常：{e}")
            return False

    def get_table_id(self, table_name: str) -> Optional[int]:
        """获取表 ID"""
        url = f"{GEN_API_BASE}/list?tableName={table_name}"
        try:
            response = requests.get(url, headers=self.headers)
            if response.status_code == 200:
                data = response.json()
                rows = data.get("rows", [])
                if rows:
                    return rows[0].get("tableId")
            return None
        except Exception as e:
            print(f"✗ 获取表 ID 异常：{e}")
            return None

    def preview_code(self, table_id: int) -> Optional[Dict]:
        """预览代码"""
        url = f"{GEN_API_BASE}/preview/{table_id}"
        try:
            response = requests.get(url, headers=self.headers)
            if response.status_code == 200:
                return response.json().get("data")
            return None
        except Exception as e:
            print(f"✗ 预览代码异常：{e}")
            return None

    def download_code(self, table_name: str, output_path: str = "generated.zip") -> Optional[Path]:
        """下载代码 ZIP"""
        url = f"{GEN_API_BASE}/download/{table_name}"
        try:
            response = requests.get(url, headers=self.headers)
            if response.status_code == 200:
                zip_path = Path(output_path)
                with open(zip_path, "wb") as f:
                    f.write(response.content)
                print(f"✓ 代码已下载到：{zip_path}")
                return zip_path
            else:
                print(f"✗ 下载失败：{response.status_code}")
                return None
        except Exception as e:
            print(f"✗ 下载异常：{e}")
            return None


def parse_zip_path(zip_entry_name: str) -> str:
    """解析 ZIP 文件中的路径，返回对应的目标路径前缀"""
    # 后端 Java 文件
    if "src/main/java" in zip_entry_name:
        if "ruoyi-admin" in zip_entry_name:
            return "ruoyi-admin/src/main/java"
        elif "ruoyi-common" in zip_entry_name:
            return "ruoyi-common/src/main/java"
        elif "ruoyi-system" in zip_entry_name:
            return "ruoyi-system/src/main/java"
        elif "ruoyi-generator" in zip_entry_name:
            return "ruoyi-generator/src/main/java"

    # Mapper XML
    if "src/main/resources/mapper" in zip_entry_name:
        if "ruoyi-generator" in zip_entry_name:
            return "ruoyi-generator/src/main/resources/mapper"
        else:
            return "ruoyi-system/src/main/resources/mapper"

    # 前端代码
    if zip_entry_name.startswith("vue/views/"):
        return "vue/views"
    if zip_entry_name.startswith("vue/api/"):
        return "vue/api"
    if zip_entry_name.startswith("vue/types/"):
        return "vue/types"

    # SQL 文件单独处理
    if zip_entry_name.endswith(".sql"):
        return "sql"

    return ""


def extract_and_copy(zip_path: Path, dry_run: bool = False) -> Dict[str, List[str]]:
    """解压 ZIP 文件并复制到目标位置"""
    if not zip_path.exists():
        print(f"✗ ZIP 文件不存在：{zip_path}")
        return {}

    # 创建临时解压目录
    temp_dir = zip_path.parent / "temp_extract"
    if temp_dir.exists():
        shutil.rmtree(temp_dir)
    temp_dir.mkdir(parents=True)

    copied_files = {"backend": [], "frontend": [], "mapper": [], "sql": [], "skipped": []}

    try:
        with zipfile.ZipFile(zip_path, 'r') as zip_ref:
            print(f"\n=== ZIP 文件内容预览 ===")
            for name in zip_ref.namelist():
                print(f"  {name}")

            print(f"\n=== 开始解压和复制 ===")
            zip_ref.extractall(temp_dir)

            for file_path in temp_dir.rglob("*"):
                if file_path.is_file():
                    rel_path = file_path.relative_to(temp_dir)
                    rel_path_str = str(rel_path).replace("\\", "/")
                    path_prefix = parse_zip_path(rel_path_str)

                    if not path_prefix:
                        print(f"跳过（无法识别路径）: {rel_path_str}")
                        copied_files["skipped"].append(rel_path_str)
                        continue

                    # SQL 文件特殊处理
                    if path_prefix == "sql":
                        sql_dir = PROJECT_ROOT / "generated_sql"
                        sql_dir.mkdir(parents=True, exist_ok=True)
                        target_path = sql_dir / file_path.name
                        if not dry_run:
                            shutil.copy2(file_path, target_path)
                        print(f"{'[预览] ' if dry_run else ''}SQL: {rel_path_str} -> {target_path}")
                        copied_files["sql"].append(str(rel_path_str))
                        continue

                    # 获取目标根目录
                    target_root = TARGET_PATHS.get(path_prefix)
                    if not target_root:
                        print(f"跳过（目标路径未配置）: {rel_path_str}")
                        copied_files["skipped"].append(rel_path_str)
                        continue

                    # 计算目标路径
                    if "src/main/java" in rel_path_str:
                        parts = rel_path_str.split("src/main/java/", 1)
                        target_file = target_root / parts[1] if len(parts) == 2 else None
                    elif "src/main/resources/mapper" in rel_path_str:
                        parts = rel_path_str.split("src/main/resources/mapper/", 1)
                        target_file = target_root / parts[1] if len(parts) == 2 else None
                    elif rel_path_str.startswith("vue/views/"):
                        parts = rel_path_str.split("vue/views/", 1)
                        target_file = target_root / parts[1] if len(parts) == 2 else None
                    elif rel_path_str.startswith("vue/api/"):
                        parts = rel_path_str.split("vue/api/", 1)
                        target_file = target_root / parts[1] if len(parts) == 2 else None
                    elif rel_path_str.startswith("vue/types/"):
                        parts = rel_path_str.split("vue/types/", 1)
                        target_file = target_root / parts[1] if len(parts) == 2 else None
                    else:
                        target_file = None

                    if not target_file:
                        continue

                    # 创建目录并复制文件
                    if not dry_run:
                        target_file.parent.mkdir(parents=True, exist_ok=True)
                        shutil.copy2(file_path, target_file)

                    # 记录
                    if "src/main/java" in rel_path_str:
                        copied_files["backend"].append(rel_path_str)
                    elif "mapper" in rel_path_str:
                        copied_files["mapper"].append(rel_path_str)
                    else:
                        copied_files["frontend"].append(rel_path_str)

                    print(f"{'[预览] ' if dry_run else ''}{rel_path_str} -> {target_file}")

    except zipfile.BadZipFile:
        print(f"✗ 无效的 ZIP 文件：{zip_path}")
    except Exception as e:
        print(f"✗ 错误：{e}")
    finally:
        if temp_dir.exists():
            shutil.rmtree(temp_dir)

    return copied_files


def print_summary(copied_files: Dict[str, List[str]]):
    """打印复制摘要"""
    print(f"\n=== 复制摘要 ===")
    print(f"后端代码：   {len(copied_files.get('backend', []))} 个文件")
    print(f"前端代码：   {len(copied_files.get('frontend', []))} 个文件")
    print(f"Mapper XML:  {len(copied_files.get('mapper', []))} 个文件")
    print(f"SQL 文件：     {len(copied_files.get('sql', []))} 个文件")
    if copied_files.get('skipped'):
        print(f"跳过：       {len(copied_files['skipped'])} 个文件")


def main():
    import argparse
    parser = argparse.ArgumentParser(description="RuoYi 代码生成全自动工具")
    parser.add_argument("--table", type=str, help="要生成的表名")
    parser.add_argument("--sql", type=str, help="建表 SQL 语句（可选，提供则先创建表）")
    parser.add_argument("--output", type=str, default="generated.zip", help="ZIP 输出路径")
    parser.add_argument("--dry-run", action="store_true", help="只预览，不实际复制")
    parser.add_argument("--username", type=str, default=DEFAULT_USERNAME, help="用户名")
    parser.add_argument("--password", type=str, default=DEFAULT_PASSWORD, help="密码")
    parser.add_argument("--skip-copy", action="store_true", help="只下载 ZIP，不复制文件")

    args = parser.parse_args()

    print("=" * 60)
    print("RuoYi 代码生成全自动工具")
    print("=" * 60)

    # 创建客户端并登录
    client = RuoYiCodeGenerator(username=args.username, password=args.password)
    if not client.login():
        print("登录失败，请检查后端服务是否启动")
        sys.exit(1)

    # 如果需要，先创建表
    if args.sql:
        print(f"\n=== 步骤 1: 创建表 ===")
        if not client.create_table(args.sql, "vue"):
            print("表创建失败，退出")
            sys.exit(1)
    else:
        print(f"\n=== 步骤 1: 跳过建表（使用已有表） ===")

    if not args.table:
        print("错误：请指定 --table 参数")
        sys.exit(1)

    # 下载代码
    print(f"\n=== 步骤 2: 下载代码 (表：{args.table}) ===")
    zip_path = client.download_code(args.table, args.output)
    if not zip_path:
        print("下载失败，退出")
        sys.exit(1)

    # 跳过复制
    if args.skip_copy:
        print(f"\n已跳过复制，ZIP 文件位于：{zip_path}")
        print(f"手动复制命令：python {__file__} --output {zip_path}")
        return

    # 解压并复制
    print(f"\n=== 步骤 3: 解压并复制文件 ===")
    copied_files = extract_and_copy(zip_path, dry_run=args.dry_run)

    if copied_files:
        print_summary(copied_files)
        if args.dry_run:
            print(f"\n如需实际复制，请运行：python {__file__} --table {args.table}")
    else:
        print("\n没有文件被复制。")
        sys.exit(1)

    print("\n" + "=" * 60)
    print("完成!")
    print("=" * 60)


if __name__ == "__main__":
    main()
