#!/usr/bin/env python3
# -*- coding: utf-8 -*-
"""
RuoYi 代码生成 API 调用脚本
用于自动化执行代码生成流程
"""

import requests
import json
import sys
from typing import Optional, Dict, List

# 配置
BASE_URL = "http://localhost:8080"
LOGIN_API = f"{BASE_URL}/login"
GEN_API_BASE = f"{BASE_URL}/tool/gen"

class RuoYiGenerator:
    """RuoYi 代码生成器 API 客户端"""

    def __init__(self, username: str = "admin", password: str = "admin123"):
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
                print(f"登录成功：{self.username}")
                return True
            else:
                print(f"登录失败：{response.text}")
                return False
        except Exception as e:
            print(f"登录异常：{e}")
            return False

    def create_table(self, sql: str, tpl_web_type: str = "vue") -> bool:
        """创建表并导入到生成器"""
        url = f"{GEN_API_BASE}/createTable"
        data = {
            "sql": sql,
            "tplWebType": tpl_web_type
        }
        try:
            response = requests.post(url, data=data, headers=self.headers)
            if response.status_code == 200:
                result = response.json()
                if result.get("code") == 200:
                    print(f"表创建成功")
                    return True
                else:
                    print(f"创建表失败：{result.get('msg')}")
                    return False
            else:
                print(f"HTTP 错误：{response.status_code}")
                return False
        except Exception as e:
            print(f"创建表异常：{e}")
            return False

    def get_table_info(self, table_name: str) -> Optional[Dict]:
        """获取表信息"""
        url = f"{GEN_API_BASE}/list?tableName={table_name}"
        try:
            response = requests.get(url, headers=self.headers)
            if response.status_code == 200:
                data = response.json()
                rows = data.get("rows", [])
                if rows:
                    return rows[0]
            return None
        except Exception as e:
            print(f"获取表信息异常：{e}")
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
            print(f"预览代码异常：{e}")
            return None

    def generate_code(self, table_name: str) -> bool:
        """生成代码到本地"""
        url = f"{GEN_API_BASE}/genCode/{table_name}"
        try:
            response = requests.get(url, headers=self.headers)
            if response.status_code == 200:
                result = response.json()
                if result.get("code") == 200:
                    print(f"代码生成成功：{table_name}")
                    return True
                else:
                    print(f"代码生成失败：{result.get('msg')}")
                    return False
            else:
                print(f"HTTP 错误：{response.status_code}")
                return False
        except Exception as e:
            print(f"生成代码异常：{e}")
            return False

    def download_code(self, table_name: str, output_path: str = "generated.zip") -> bool:
        """下载代码 ZIP"""
        url = f"{GEN_API_BASE}/download/{table_name}"
        try:
            response = requests.get(url, headers=self.headers)
            if response.status_code == 200:
                with open(output_path, "wb") as f:
                    f.write(response.content)
                print(f"代码已下载到：{output_path}")
                return True
            else:
                print(f"下载失败：{response.status_code}")
                return False
        except Exception as e:
            print(f"下载异常：{e}")
            return False


def main():
    """主函数 - 示例用法"""
    # 创建客户端并登录
    client = RuoYiGenerator(username="admin", password="admin123")
    if not client.login():
        print("登录失败，请检查后端服务是否启动")
        sys.exit(1)

    # 示例：创建表
    create_sql = """
    CREATE TABLE sys_product (
      id bigint NOT NULL AUTO_INCREMENT COMMENT '产品 ID',
      product_name varchar(200) NOT NULL COMMENT '产品名称',
      price decimal(10,2) DEFAULT '0.00' COMMENT '价格',
      stock int DEFAULT '0' COMMENT '库存',
      status char(1) DEFAULT '0' COMMENT '状态（0 正常 1 停用）',
      create_by varchar(64) DEFAULT '' COMMENT '创建者',
      create_time datetime DEFAULT NULL COMMENT '创建时间',
      update_by varchar(64) DEFAULT '' COMMENT '更新者',
      update_time datetime DEFAULT NULL COMMENT '更新时间',
      remark varchar(500) DEFAULT NULL COMMENT '备注',
      del_flag char(2) DEFAULT '0' COMMENT '删除标志',
      PRIMARY KEY (id)
    ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='产品表';
    """

    if client.create_table(create_sql, "vue"):
        print("表创建成功，可以继续生成代码")
        # 获取表信息
        table_info = client.get_table_info("sys_product")
        if table_info:
            table_id = table_info.get("tableId")
            # 预览代码
            preview = client.preview_code(table_id)
            if preview:
                print("代码预览成功")
                # 可以提取 menu.sql
                for key, value in preview.items():
                    if "menu.sql" in key:
                        print("\n=== 菜单 SQL ===")
                        print(value)
            # 生成代码
            # client.generate_code("sys_product")


if __name__ == "__main__":
    main()
