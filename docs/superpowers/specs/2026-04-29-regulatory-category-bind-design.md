# regulatory_category_bind 管理页面设计

## 1. 概述

管理 `regulatory_matter`（监管事项）与 `sys_industry_category`（行业分类）的多对多关联关系。

## 2. 页面布局

采用**左侧行业分类树 + 右侧关联事项列表**的布局：

```
┌─────────────────────────────────────────────────────────┐
│  [左侧：行业分类树]          [右侧：关联的监管事项列表]    │
│  ┌──────────────┐        ┌─────────────────────────┐  │
│  │ ▼ 全部       │        │ 监管事项  │ 操作        │  │
│  │   └ 食品类   │        │ 食品安全   │ [查看] [解绑]│  │
│  │     └ 生产   │        │ 卫生检查   │ [查看] [解绑]│  │
│  │     └ 销售   │        │ 质量监督   │ [查看] [解绑]│  │
│  │   └ 药品类   │        └─────────────────────────┘  │
│  │   └ 医疗类   │                                      │
│  └──────────────┘        [新增绑定]                   │
└─────────────────────────────────────────────────────────┘
```

## 3. 数据表关系

```sql
-- 关联表
regulatory_category_bind (
    matter_id BIGINT,          -- 关联 regulatory_matter.id
    category_id BIGINT         -- 关联 sys_industry_category.category_id
)
```

## 4. 功能清单

| 功能 | 说明 |
|------|------|
| 左侧行业分类树 | 按 `sys_industry_category` 构建，支持展开/收起，点击节点筛选 |
| 右侧事项列表 | 显示当前分类关联的 `regulatory_matter` |
| 新增绑定 | 弹出对话框，选择 matter 并绑定到当前 category |
| 解绑 | 移除某条关联 |
| 切换视图 | 可切换为"按事项查看分类"（左侧显示事项树，右侧显示分类列表） |

## 5. API 设计

### 后端接口

| 接口 | 方法 | 说明 |
|------|------|------|
| `/system/regulatoryCategoryBind/list` | GET | 查询关联列表（支持按 categoryId 或 matterId 筛选） |
| `/system/regulatoryCategoryBind` | POST | 新增关联 |
| `/system/regulatoryCategoryBind` | DELETE | 删除关联 |
| `/system/category/tree` | GET | 获取行业分类树 |

### 请求/响应示例

**新增绑定**
```json
POST /system/regulatoryCategoryBind
{
  "matterId": 1,
  "categoryId": 2
}
```

**查询列表（按分类筛选）**
```
GET /system/regulatoryCategoryBind/list?categoryId=2
```

响应：
```json
{
  "rows": [
    { "matterId": 1, "matterName": "食品安全检查", "mainCode": "A001" },
    { "matterId": 3, "matterName": "卫生监督", "mainCode": "A003" }
  ]
}
```

## 6. 文件清单

### 后端
- `RegulatoryCategoryBindController.java`
- `RegulatoryCategoryBindService.java` + `Impl`
- `RegulatoryCategoryBindMapper.java`
- `RegulatoryCategoryBindMapper.xml`
- `RegulatoryCategoryBind.java` (Domain)

### 前端
- `api/system/regulatoryCategoryBind.js`
- `views/system/regulatoryCategoryBind/index.vue`

## 7. 实现顺序

1. 后端 Domain、Mapper、Service、Controller
2. 前端 API
3. 前端页面（左侧树 + 右侧列表）
4. 新增绑定功能
5. 解绑功能
6. 切换视图功能
