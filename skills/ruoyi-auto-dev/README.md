# RuoYi 自动化开发技能

## 安装

将此技能目录放到 Claude Code 的技能目录下：

```bash
# 方式 1：全局技能目录
cp -ru ruoyi-auto-dev ~/.claude/skills/

# 方式 2：项目技能目录（推荐）
# 已放置在 projectV4/skills/ruoyi-auto-dev/
```

## 使用方法

### 方式 1：通过 Claude Code 技能

在 Claude Code 中直接描述你的需求，例如：

```
创建一个产品管理模块，包含产品名称、价格、库存、分类、状态等字段
```

技能会自动：
1. 设计数据库表结构
2. 调用后端 API 创建表
3. 配置代码生成参数
4. 下载 ZIP 并自动解压到正确位置
5. 提供菜单 SQL

### 方式 2：使用自动化脚本

```bash
cd skills/ruoyi-auto-dev

# 完整流程（建表 + 下载 + 解压）
python scripts/auto-generator.py --table sys_product --sql "CREATE TABLE ..."

# 仅下载和解压（表已存在）
python scripts/auto-generator.py --table sys_product

# 预览模式（不实际复制）
python scripts/auto-generator.py --table sys_product --dry-run

# 只下载 ZIP
python scripts/auto-generator.py --table sys_product --skip-copy
```

## 前置条件

1. RuoYi-Vue 后端服务已启动（默认端口 8080）
2. 数据库已正确配置并可访问
3. 使用 admin 账号或有 `tool:gen:*` 权限的账号登录
4. `generator.yml` 中配置 `allowOverwrite: true`

## 技能文件说明

```
ruoyi-auto-dev/
├── SKILL.md                  # 技能主文件（必读）
├── scripts/
│   ├── generate.py           # API 调用基础脚本
│   ├── auto-copy.py          # 自动解压复制脚本
│   └── auto-generator.py     # 全自动脚本（下载 + 解压）
├── evals/
│   └── evals.json            # 测试用例
└── README.md                 # 本文件
```

## API 调用流程

```
POST /login                    # 登录获取 token
POST /tool/gen/createTable     # 创建表
PUT  /tool/gen                 # 更新生成配置
GET  /tool/gen/preview/{id}    # 预览代码
GET  /tool/gen/genCode/{name}  # 生成代码
```

## 故障排查

### 登录失败
- 检查后端服务是否启动
- 确认账号密码正确

### 权限不足
- 使用 admin 账号登录
- 检查角色权限配置

### 代码生成失败
- 检查 `generator.yml` 配置
- 确认目录写权限

## 测试用例

运行测试用例验证技能：

```bash
cd skills/ruoyi-auto-dev
# 在 Claude Code 中输入 evals/evals.json 中的 prompts
```
