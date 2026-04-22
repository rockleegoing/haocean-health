# PRD 与原型图索引

本文档提供 PRD 需求与原型图、规则文件、代码模块之间的映射关系。

## 1. PRD 功能模块索引

### 1.1 设备与认证模块

| 功能点 | 原型图 | 规则/规范 | 代码模块 |
|-------|-------|----------|---------|
| 用户登录 | `登录.png` | `mobile-android.md` | `feature/login/` |
| 设备激活 | `激活.png` | `mobile-android.md` | `feature/device/` |
| 激活码管理 | - | `api-doc.md` | `backend/activatation/` |

### 1.2 执法单位管理

| 功能点 | 原型图 | 规则/规范 | 代码模块 |
|-------|-------|----------|---------|
| 单位列表 | `选择单位.png` | `sync-conflict.md` | `feature/unit/` |
| 单位详情 | `更多资料.png` | `sync-conflict.md` | `feature/unit/` |
| 单位编辑 | `修改资料.png` 等 | `sync-conflict.md` | `feature/unit/` |
| 行业分类 | - | `database.md` | `backend/industry/` |

### 1.3 便捷执法模块（核心）

| 功能点 | 原型图 | 规则/规范 | 代码模块 |
|-------|-------|----------|---------|
| 快速拍照 | `便捷执法_快速拍照.png` | `mobile-android.md` | `feature/lawenforcement/` |
| 快速录音 | `便捷执法_快速录音.png` | `mobile-android.md` | `feature/lawenforcement/` |
| 快速录像 | `便捷执法_快速录像.png` | `mobile-android.md` | `feature/lawenforcement/` |
| 快速导航 | `便捷执法_快速导航.png` | `mobile-android.md` | `feature/lawenforcement/` |
| 执法记录列表 | `便捷执法_执法记录.png` | `sync-conflict.md` | `feature/lawenforcement/` |
| 填写文书 | `便捷执法_执法记录_填写文书.png` | `document-template.md` | `feature/document/` |
| 快捷打印 | `便捷执法_执法记录_快捷打印.png` | `document-template.md` | `hardware/print/` |
| 快速上报 | `便捷执法_执法记录_快速上报.png` | `sync-conflict.md` | `feature/lawenforcement/` |

### 1.4 法律法规库

| 功能点 | 原型图 | 规则/规范 | 代码模块 |
|-------|-------|----------|---------|
| 法律法规首页 | `法律法规.png` | `mobile-android.md` | `feature/knowledge/law/` |
| 搜索功能 | `法律法规_搜索样式.png` 等 | `mobile-android.md` | `feature/knowledge/law/` |
| 法律类型筛选 | `法律法规_搜索样式_法律类型.png` | `database.md` | `backend/law/` |
| 监管类型筛选 | `法律法规_搜索样式_监督类型.png` | `database.md` | `backend/law/` |
| 书籍列表 | `法律法规_书本列表.png` | `database.md` | `backend/law/` |
| 书籍详情 | `法律法规_书本列表_书本详情.png` | `database.md` | `backend/law/` |
| 定性依据列表 | `法律法规_书本列表_书本详情_定性依据列表.png` | `database.md` | `backend/law/` |
| 定性依据详情 | `法律法规_书本列表_书本详情_定性依据列表_定性依据详情.png` | `database.md` | `backend/law/` |

### 1.5 规范用语库

| 功能点 | 原型图 | 规则/规范 | 代码模块 |
|-------|-------|----------|---------|
| 规范用语首页 | `规范用语.png` | `mobile-android.md` | `feature/knowledge/phrase/` |
| 搜索功能 | `规范用语_搜索.png` | `mobile-android.md` | `feature/knowledge/phrase/` |
| 书本详情 | `规范用语_书本详情.png` | `database.md` | `backend/phrase/` |
| 项列表 | `规范用语_书本详情_项列表.png` | `database.md` | `backend/phrase/` |
| 项明细 | `规范用语_书本详情_项列表_项明细.png` | `database.md` | `backend/phrase/` |

### 1.6 监管事项库

| 功能点 | 原型图 | 规则/规范 | 代码模块 |
|-------|-------|----------|---------|
| 监管事项首页 | `监管事项.png` | `mobile-android.md` | `feature/knowledge/item/` |
| 监管子项列表 | `监管事项_监管子项.png` | `database.md` | `backend/item/` |
| 对应规范用语 | `监管事项_监管子项_对应规范用语.png` | `database.md` | `backend/item/` |
| 规范用语明细 | `监管事项_监管子项_对应规范用语_明细.png` | `database.md` | `backend/item/` |

### 1.7 日常办公模块

| 功能点 | 原型图 | 规则/规范 | 代码模块 |
|-------|-------|----------|---------|
| 日常办公首页 | `日常办公.png` | `mobile-android.md` | `feature/office/` |
| 添加单位 | `日常办公_添加单位.png` | `sync-conflict.md` | `feature/unit/` |
| 系统设置 | `日常办公_系统设置.png` | `mobile-android.md` | `feature/settings/` |
| 通知公告 | `日常办公_通知公告.png` | `mobile-android.md` | `feature/notice/` |
| 现场笔录 | `现场笔录.png` | `document-template.md` | `feature/document/` |

---

## 2. 原型图分类索引

### 按功能模块分类

#### 基础页面 (7 个)
- `登录.png`
- `激活.png`
- `首页.png`
- `选择单位.png`
- `更多资料.png`
- `修改资料.png`
- `修改资料自定义输入注册地址样式.png`
- `修改资料输入文字样式.png`
- `修改资料选择名族样式.png`
- `修改资料选择时间样式.png`

#### 便捷执法 (8 个)
- `便捷执法_快速拍照.png`
- `便捷执法_快速录音.png`
- `便捷执法_快速录像.png`
- `便捷执法_快速导航.png`
- `便捷执法_执法记录.png`
- `便捷执法_执法记录_导入图片.png`
- `便捷执法_执法记录_填写文书.png`
- `便捷执法_执法记录_快捷打印.png`
- `便捷执法_执法记录_快速上报.png`

#### 法律法规 (8 个)
- `法律法规.png`
- `法律法规_搜索样式.png`
- `法律法规_搜索样式_法律类型.png`
- `法律法规_搜索样式_监督类型.png`
- `法律法规_书本列表.png`
- `法律法规_书本列表_书本详情.png`
- `法律法规_书本列表_书本详情_定性依据列表.png`
- `法律法规_书本列表_书本详情_定性依据列表_定性依据详情.png`

#### 规范用语 (5 个)
- `规范用语.png`
- `规范用语_搜索.png`
- `规范用语_书本详情.png`
- `规范用语_书本详情_项列表.png`
- `规范用语_书本详情_项列表_项明细.png`

#### 监管事项 (4 个)
- `监管事项.png`
- `监管事项_监管子项.png`
- `监管事项_监管子项_对应规范用语.png`
- `监管事项_监管子项_对应规范用语_明细.png`

#### 日常办公 (5 个)
- `日常办公.png`
- `日常办公_添加单位.png`
- `日常办公_系统设置.png`
- `日常办公_通知公告.png`
- `现场笔录.png`

---

## 3. 规则文件索引

### 新增规则文件 (3 个)

| 文件 | 说明 | 适用模块 |
|------|------|---------|
| `rules/mobile-android.md` | Android 移动端开发规范 | 所有移动端功能 |
| `rules/sync-conflict.md` | 数据同步与冲突处理 | 执法单位、执法记录 |
| `rules/document-template.md` | 文书模板管理 | 文书制作、打印 |

### 更新规则文件 (3 个)

| 文件 | 更新内容 | 适用模块 |
|------|---------|---------|
| `rules/testing.md` | Android 测试、离线测试、同步测试 | 所有移动端功能 |
| `rules/logging.md` | Android 日志持久化、模块日志 | 所有移动端功能 |
| `rules/database.md` | SQLite 本地数据库规范 | 所有离线数据存储 |

---

## 4. 开发流程映射

### 新增功能开发流程

```
1. 查看 PRD.md 了解需求
       ↓
2. 找到对应原型图参考 UI
       ↓
3. 阅读相关规则文件
   - mobile-android.md (UI 开发)
   - sync-conflict.md (数据同步)
   - document-template.md (文书相关)
       ↓
4. 创建代码模块
   - feature/xxx/ (功能模块)
   - data/xxx/ (数据层)
       ↓
5. 编写测试用例
   - 单元测试
   - 离线功能测试
   - 同步机制测试
       ↓
6. Code Review (检查规则遵循情况)
```

### 需求变更追踪

| PRD 版本 | 变更日期 | 变更内容 | 影响模块 |
|---------|---------|---------|---------|
| v1.0 | 2026-04-22 | 初始版本 | 全部 |

---

## 5. 快速参考

### 常用命令

```bash
# 查看原型图
open 原型图/首页.png

# 查看规则文件
cat rules/mobile-android.md

# 查看 PRD
cat PRD.md
```

### 相关链接

- [PRD 文档](../PRD.md)
- [Android 开发规范](./mobile-android.md)
- [数据同步规范](./sync-conflict.md)
- [文书模板规范](./document-template.md)
