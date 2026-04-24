# 便捷执法模块设计文档

## 1. 模块概述

### 1.1 功能范围

便捷执法模块是移动端的核心功能之一，为执法人员提供现场快速执法的能力。

**第一期交付内容**：
- 快捷入口（5个）
- 快速操作（拍照/录音/录像/导航）
- 执法记录管理（列表/详情/上报/删除）

**第二期交付内容**：
- 文书制作（模板选择/预览/填写/生成）
- 打印导出（蓝牙打印/PDF导出）
- 多条件筛选

### 1.2 核心设计理念

- **离线优先**：所有操作先保存本地，网络恢复后自动同步
- **以本地为准**：执法记录以本地数据为准，冲突时保留本地
- **行业分类为骨架**：所有数据关联行业分类

---

## 2. 技术架构

### 2.1 架构模式

采用 MVVM 架构，沿用现有 View 系统（Activity/Fragment + ViewBinding）

```
UI Layer (Activity/Fragment)
    ↓
ViewModel (AndroidX ViewModel + LiveData)
    ↓
Repository (数据仓库)
    ↓
Data Source (Local SQLite + Remote API)
```

### 2.2 目录结构

```
app/src/main/java/com/ruoyi/app/
├── feature/
│   └── lawenforcement/           # 便捷执法模块
│       ├── ui/
│       │   ├── LawEnforcementFragment.kt      # 主界面
│       │   ├── RecordListActivity.kt          # 记录列表
│       │   ├── RecordDetailActivity.kt        # 记录详情
│       │   ├── RecordEditActivity.kt         # 记录编辑
│       │   └── adapter/
│       │       ├── RecordListAdapter.kt       # 记录列表适配器
│       │       └── EvidenceAdapter.kt         # 证据材料适配器
│       ├── viewmodel/
│       │   ├── LawEnforcementViewModel.kt     # 主 ViewModel
│       │   ├── RecordListViewModel.kt        # 记录列表 ViewModel
│       │   └── RecordDetailViewModel.kt      # 记录详情 ViewModel
│       ├── model/
│       │   ├── EnforcementRecord.kt           # 执法记录实体
│       │   ├── EvidenceMaterial.kt            # 证据材料实体
│       │   └── RecordRequest.kt               # 请求参数
│       ├── repository/
│       │   └── LawEnforcementRepository.kt    # 数据仓库
│       └── api/
│           └── LawEnforcementApi.kt           # API 接口
└── data/
    ├── local/
    │   ├── dao/
    │   │   ├── EnforcementRecordDao.kt        # 记录 DAO
    │   │   └── EvidenceMaterialDao.kt         # 证据 DAO
    │   └── AppDatabase.kt                    # Room 数据库
    └── sync/
        └── SyncManager.kt                     # 同步管理器（复用）
```

### 2.3 数据库设计

#### 执法记录主表

```sql
CREATE TABLE t_enforcement_record (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    record_no       TEXT NOT NULL UNIQUE,      -- 记录编号
    unit_id         INTEGER NOT NULL,          -- 单位 ID
    unit_name       TEXT NOT NULL,             -- 单位名称
    industry_id     INTEGER NOT NULL,          -- 行业 ID
    industry_code   TEXT NOT NULL,             -- 行业代码
    record_type     TEXT NOT NULL,             -- 记录类型
    record_status   TEXT NOT NULL,             -- 记录状态
    description     TEXT,                       -- 备注说明
    longitude       REAL,                       -- 经度
    latitude        REAL,                       -- 纬度
    location_name   TEXT,                       -- 位置名称
    sync_status     TEXT DEFAULT 'PENDING',     -- 同步状态
    create_by       TEXT NOT NULL,             -- 创建人
    create_time     INTEGER NOT NULL,           -- 创建时间
    update_by       TEXT,                       -- 更新人
    update_time     INTEGER,                    -- 更新时间
    del_flag       TEXT DEFAULT '0'            -- 删除标志
);
```

#### 证据材料表（统一表）

```sql
CREATE TABLE t_evidence_material (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    record_id      INTEGER NOT NULL,           -- 关联记录 ID
    evidence_type   TEXT NOT NULL,             -- 类型：photo/audio/video
    file_path       TEXT NOT NULL,             -- 文件路径
    file_name       TEXT,                       -- 文件名
    file_size       INTEGER,                    -- 文件大小
    duration        INTEGER,                    -- 时长（音视频）
    description     TEXT,                       -- 描述
    sync_status     TEXT DEFAULT 'PENDING',     -- 同步状态
    create_time     INTEGER NOT NULL,           -- 创建时间
    FOREIGN KEY (record_id) REFERENCES t_enforcement_record(id)
);
```

---

## 3. UI 设计

### 3.1 第一期界面清单

| 界面 | 布局文件 | 说明 |
|------|----------|------|
| 便捷执法入口 | fragment_law_enforcement.xml | 工作区显示5个快捷入口 |
| 执法记录列表 | activity_record_list.xml | 列表 + 筛选 |
| 执法记录详情 | activity_record_detail.xml | 详情 + 操作按钮 |
| 执法记录编辑 | activity_record_edit.xml | 编辑备注说明 |

### 3.2 快捷入口设计

位置：HomeFragment 工作区（WorkFragment）

```
┌─────────────────────────────────────────┐
│  便捷执法                              │
├─────────────────────────────────────────┤
│  ┌────┐ ┌────┐ ┌────┐ ┌────┐ ┌────┐ │
│  │ 📷 │ │ 🎤 │ │ 📹 │ │ 🧭 │ │ 📋 │ │
│  │    │ │    │ │    │ │    │ │    │ │
│  │拍照│ │录音│ │录像│ │导航│ │记录│ │
│  └────┘ └────┘ └────┘ └────┘ └────┘ │
└─────────────────────────────────────────┘
```

**实现要点**：
- 5个入口横向排列，均匀分布
- 每个入口：图标（48dp）+ 文字标签
- 点击跳转到对应功能
- 入口根据 activation_status 显示/隐藏

### 3.3 执法记录列表设计

**顶部筛选栏**：
- 状态筛选：全部 / 待上报 / 已上报
- 单位筛选：下拉选择
- 日期筛选：开始日期 ~ 结束日期
- 行业筛选：下拉选择

**列表项布局**：
```
┌─────────────────────────────────────────┐
│ ● 待上报        2026-04-24 14:30      │
│                                         │
│ 杭州市江干区xxx酒店                      │
│ 公共场所卫生                         │
│                                         │
│ 📷3 🎤1 📹0     [编辑] [上报] [删除]  │
└─────────────────────────────────────────┘
```

### 3.4 执法记录详情设计

**基本信息区域**：
- 记录编号、单位名称、行业分类
- 记录状态、创建时间、位置信息

**证据材料区域**：
- Photo: 网格展示，可点击放大
- Audio: 列表展示，带播放按钮和时长
- Video: 缩略图展示，带播放图标和时长

**操作按钮**：
- 上报（待上报状态显示）
- 编辑（创建人可操作）
- 删除（创建人/管理员可操作）
- 打印（第二期）

### 3.5 组件设计

使用 Material Design 3 组件：
- Cards: 列表项使用 CardView
- Buttons: FilledButton / OutlinedButton
- BottomSheet: 筛选条件选择
- Dialog: 确认对话框
- Snackbar: 操作反馈

---

## 4. 功能流程

### 4.1 快速拍照流程

```
1. 用户点击"拍照"入口
2. 检查相机权限（无权限请求）
3. 检查 activation_status（未激活提示）
4. 使用系统 Intent 调用相机
5. 拍照完成返回
6. 检查当前是否已选单位（未选则提示选择）
7. 创建/更新执法记录
8. 保存证据材料到本地
9. 如果有网络，立即上传证据文件
10. 返回并刷新界面
```

### 4.2 快速录音流程

```
1. 用户点击"录音"入口
2. 检查麦克风权限
3. 检查 activation_status
4. 弹出录音界面（时长 + 波形动画）
5. 点击停止
6. 检查当前单位
7. 创建/更新记录 + 保存证据
8. 立即上传（如有网络）
```

### 4.3 执法记录上报流程

```
1. 用户在详情页点击"上报"
2. 确认对话框
3. 更新记录状态为"已上报"
4. 将记录加入同步队列
5. 如果有网络，立即同步
6. 如果无网络，等待 SyncManager 自动同步
7. 显示同步结果
```

### 4.4 快速导航流程

```
1. 用户点击"导航"入口
2. 检查当前是否有选中单位
3. 检查单位是否有经纬度坐标
4. 检测高德地图是否安装
5. 已安装：调用高德地图导航
6. 未安装：提示安装或使用其他地图
```

---

## 5. 数据同步

### 5.1 同步策略

| 数据类型 | 同步方向 | 冲突处理 |
|---------|---------|---------|
| 执法记录 | App → 后台 | 以本地为准 |
| 证据材料 | App → 后台 | 以本地为准 |
| 行业分类 | 后台 → App | 以服务器为准 |
| 单位数据 | 双向 | 查重后合并 |

### 5.2 证据文件同步

- 照片/录音：生成后立即尝试上传
- 录像：文件较大，使用手动同步
- 上传失败：加入重试队列，下次网络恢复重试

### 5.3 同步状态

| 状态 | 说明 |
|------|------|
| PENDING | 待同步 |
| SYNCING | 同步中 |
| SUCCESS | 同步成功 |
| FAILED | 同步失败 |

---

## 6. 权限要求

| 权限 | 用途 | 敏感度 |
|------|------|--------|
| CAMERA | 拍照 | 高 |
| RECORD_AUDIO | 录音 | 高 |
| ACCESS_FINE_LOCATION | 导航定位 | 中 |
| ACCESS_COARSE_LOCATION | 粗略定位 | 低 |
| INTERNET | 网络通信 | 低 |
| BLUETOOTH | 打印 | 中 |

---

## 7. 第一期工作量评估

| 功能 | Android 端 | 说明 |
|------|-----------|------|
| 快捷入口界面 | 0.5 天 | UI 布局 |
| 拍照功能 | 0.5 天 | Intent 调用 |
| 录音功能 | 0.5 天 | MediaRecorder |
| 录像功能 | 0.5 天 | MediaRecorder |
| 导航功能 | 0.5 天 | 高德 Intent |
| 记录列表 | 1 天 | 列表 + 筛选 |
| 记录详情 | 0.5 天 | 查看 + 操作 |
| 记录编辑 | 0.5 天 | 编辑 + 删除 |
| 数据层 | 0.5 天 | Room DAO + Repository |
| 同步集成 | 0.5 天 | SyncManager 集成 |
| **合计** | **5 天** | |

---

## 8. 第二期规划

| 功能 | 说明 |
|------|------|
| 文书制作 | 模板选择 → 预览 → 填写 → 生成 |
| 蓝牙打印 | ESC/POS 协议打印 |
| PDF导出 | 导出执法记录为 PDF |
| 多条件筛选 | 高级筛选功能 |

---

## 9. 参考文件

- PRD.md - 产品需求文档 v13.0
- 原型图/便捷执法_*.png - 界面原型
- rules/mobile-android.md - Android 开发规范
- rules/sync-conflict.md - 同步冲突处理规范

---

## 10. 验收标准（第一期）

- [ ] 5个快捷入口正常显示和跳转
- [ ] 快速拍照功能正常（调用相机、照片保存、关联单位/行业）
- [ ] 快速录音功能正常（录音、播放、关联单位/行业）
- [ ] 快速录像功能正常（录像、播放、关联单位/行业）
- [ ] 快速导航功能正常（检测高德地图、打开导航、未安装提示）
- [ ] 执法记录列表显示正常（按状态分组、筛选功能）
- [ ] 执法记录详情显示正常（证据材料展示、操作按钮）
- [ ] 执法记录上报功能正常
- [ ] 证据材料添加/删除功能正常
- [ ] 所有执法数据正确关联单位和行业分类
- [ ] 离线模式下数据正确保存
- [ ] 网络恢复后数据自动同步
