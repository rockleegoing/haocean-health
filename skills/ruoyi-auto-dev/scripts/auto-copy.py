#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
RuoYi 代码生成自动解压和复制脚本
下载 ZIP 文件后自动解压并复制到项目的正确位置
"""

import os
import sys
import zipfile
import shutil
import argparse
from pathlib import Path
from typing import Dict, List

# 项目根目录（根据实际路径修改）
PROJECT_ROOT = Path(__file__).parent.parent.parent
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


def parse_zip_path(zip_entry_name: str) -> str:
    """
    解析 ZIP 文件中的路径，返回对应的目标路径前缀

    ZIP 中的路径格式：
    - ruoyi-admin/src/main/java/com/ruoyi/...
    - ruoyi-system/src/main/java/com/ruoyi/...
    - ruoyi-common/src/main/java/com/ruoyi/...
    - vue/views/system/user/index.vue
    - vue/api/system/user/user.js
    """
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


def extract_and_copy(zip_path: str, dry_run: bool = False) -> Dict[str, List[str]]:
    """
    解压 ZIP 文件并复制到目标位置

    Args:
        zip_path: ZIP 文件路径
        dry_run: 如果为 True，只预览不实际复制

    Returns:
        复制文件列表的字典
    """
    zip_path = Path(zip_path)
    if not zip_path.exists():
        print(f"错误：ZIP 文件不存在：{zip_path}")
        return {}

    # 创建临时解压目录
    temp_dir = zip_path.parent / "temp_extract"
    if temp_dir.exists():
        shutil.rmtree(temp_dir)
    temp_dir.mkdir(parents=True)

    copied_files = {
        "backend": [],
        "frontend": [],
        "mapper": [],
        "sql": [],
        "skipped": []
    }

    try:
        with zipfile.ZipFile(zip_path, 'r') as zip_ref:
            print(f"\n=== ZIP 文件内容预览 ===")

            # 首先列出所有内容
            for name in zip_ref.namelist():
                print(f"  {name}")

            print(f"\n=== 开始解压和复制 ===")

            # 解压到临时目录
            zip_ref.extractall(temp_dir)

            # 遍历临时目录中的文件
            for file_path in temp_dir.rglob("*"):
                if file_path.is_file():
                    # 获取相对于临时目录的路径
                    rel_path = file_path.relative_to(temp_dir)
                    rel_path_str = str(rel_path).replace("\\", "/")

                    # 解析目标路径前缀
                    path_prefix = parse_zip_path(rel_path_str)

                    if not path_prefix:
                        print(f"跳过（无法识别路径）: {rel_path_str}")
                        copied_files["skipped"].append(rel_path_str)
                        continue

                    # SQL 文件特殊处理
                    if path_prefix == "sql":
                        target_path = PROJECT_ROOT / "generated_sql" / file_path.name
                        target_path.parent.mkdir(parents=True, exist_ok=True)
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
                    # 需要从 rel_path_str 中提取有效部分
                    # 例如：ruoyi-system/src/main/java/com/ruoyi/system/user/domain/User.java
                    # 应该提取 com/ruoyi/... 之后的部分

                    if "src/main/java" in rel_path_str:
                        # Java 文件：提取包路径部分
                        parts = rel_path_str.split("src/main/java/", 1)
                        if len(parts) == 2:
                            target_file = target_root / parts[1]
                        else:
                            continue
                    elif "src/main/resources/mapper" in rel_path_str:
                        # Mapper XML
                        parts = rel_path_str.split("src/main/resources/mapper/", 1)
                        if len(parts) == 2:
                            target_file = target_root / parts[1]
                        else:
                            continue
                    elif rel_path_str.startswith("vue/views/"):
                        # Vue 视图
                        parts = rel_path_str.split("vue/views/", 1)
                        if len(parts) == 2:
                            target_file = target_root / parts[1]
                        else:
                            continue
                    elif rel_path_str.startswith("vue/api/"):
                        # API 文件
                        parts = rel_path_str.split("vue/api/", 1)
                        if len(parts) == 2:
                            target_file = target_root / parts[1]
                        else:
                            continue
                    elif rel_path_str.startswith("vue/types/"):
                        # TypeScript 类型
                        parts = rel_path_str.split("vue/types/", 1)
                        if len(parts) == 2:
                            target_file = target_root / parts[1]
                        else:
                            continue
                    else:
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
        print(f"错误：无效的 ZIP 文件：{zip_path}")
    except Exception as e:
        print(f"错误：{e}")
    finally:
        # 清理临时目录
        if temp_dir.exists():
            shutil.rmtree(temp_dir)

    return copied_files


def print_summary(copied_files: Dict[str, List[str]]):
    """打印复制摘要"""
    print(f"\n=== 复制摘要 ===")
    print(f"后端代码：  {len(copied_files.get('backend', []))} 个文件")
    print(f"前端代码：  {len(copied_files.get('frontend', []))} 个文件")
    print(f"Mapper XML: {len(copied_files.get('mapper', []))} 个文件")
    print(f"SQL 文件：    {len(copied_files.get('sql', []))} 个文件")
    if copied_files.get('skipped'):
        print(f"跳过：      {len(copied_files['skipped'])} 个文件")


def main():
    parser = argparse.ArgumentParser(description="RuoYi 代码生成自动解压和复制工具")
    parser.add_argument("zip_path", help="ZIP 文件路径")
    parser.add_argument("--dry-run", action="store_true", help="只预览，不实际复制")
    parser.add_argument("--project-root", type=str, help="项目根目录路径")

    args = parser.parse_args()

    if args.project_root:
        global PROJECT_ROOT, RuoYi_VUE
        PROJECT_ROOT = Path(args.project_root)
        RuoYi_VUE = PROJECT_ROOT / "RuoYi-Vue"

    print(f"项目根目录：{PROJECT_ROOT}")
    print(f"ZIP 文件：{args.zip_path}")

    if args.dry_run:
        print("模式：预览（不会实际复制文件）")

    copied_files = extract_and_copy(args.zip_path, dry_run=args.dry_run)

    if copied_files:
        print_summary(copied_files)

        if args.dry_run:
            print("\n如需实际复制，请运行：")
            print(f"  python {__file__} {args.zip_path}")
        else:
            print("\n复制完成！请检查目标文件。")
    else:
        print("\n没有文件被复制。")
        sys.exit(1)


if __name__ == "__main__":
    main()
