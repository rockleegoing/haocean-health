# 文书管理页面功能完善设计方案

## 1. 概述

**目标：** 完善文书管理模块的前端 Vue 管理页面和 Android 端功能，打通从模板配置到文书生成的完整流程。

**范围：**
- 第一阶段：前端 Vue 管理页面 CRUD 功能
- 第二阶段：Android 端文书填写→签名→提交完整闭环

---

## 2. 第一阶段：前端 Vue 管理页面

### 2.1 模板管理

**文件：`ruoyi-ui/src/views/system/document/template/`**

| 文件 | 当前问题 | 修复方案 |
|------|----------|----------|
| `index.vue` | API 调用被注释 | 取消注释，连接 `addTemplate/updateTemplate/delTemplate` |
| `edit.vue` | import 缺失 | 添加 `addTemplate, updateTemplate, getTemplate` 导入 |

**API 端点：**
- `POST /api/admin/document/template` - 新增
- `PUT /api/admin/document/template` - 修改
- `DELETE /api/admin/document/template/{id}` - 删除
- `GET /api/admin/document/template/{id}` - 详情

**权限标识符：**
- `system:document:template:add`
- `system:document:template:edit`
- `system:document:template:remove`

### 2.2 套组管理

**文件：`ruoyi-ui/src/views/system/document/group/`**

| 文件 | 当前问题 | 修复方案 |
|------|----------|----------|
| `index.vue` | API 调用被注释 | 取消注释，连接 `addGroup/updateGroup/delGroup` |
| `edit.vue` | import 缺失 | 添加 `addGroup, updateGroup, getGroup` 导入 |

**API 端点：**
- `POST /api/admin/document/group` - 新增
- `PUT /api/admin/document/group` - 修改
- `DELETE /api/admin/document/group/{id}` - 删除
- `GET /api/admin/document/group/{id}` - 详情

**权限标识符：**
- `system:document:group:add`
- `system:document:group:edit`
- `system:document:group:remove`

### 2.3 文书记录

**文件：`ruoyi-ui/src/views/system/document/record/index.vue`**

**当前问题：** 详情/下载/删除都是空壳函数

**实现功能：**

| 功能 | 实现方案 |
|------|----------|
| 详情弹窗 | 展示文书编号、模板名称、状态、变量 JSON 格式化展示 |
| 下载 | 调用 `${ConfigApi.baseUrl}/api/admin/document/download/{id}` |
| 删除 | 调用 `delRecord(id)` API，确认后删除 |

**API 端点：**
- `GET /api/admin/document/record/list` - 列表（已实现）
- `GET /api/admin/document/record/{id}` - 详情
- `DELETE /api/admin/document/record/{id}` - 删除
- `GET /api/admin/document/download/{id}` - 下载文书文件

---

## 3. 第二阶段：Android 端

### 3.1 完整流程

```
执法记录详情页
    ↓ [开具文书]
选择套组/模板页面 (DocumentListActivity)
    ↓
填写变量表单 (DocumentFillActivity)
    ↓
电子签名 (SignatureActivity)
    ↓
提交到服务器 → 保存文书记录
```

### 3.2 待实现功能

**1. DocumentFillActivity.submitForm()**
```kotlin
// 收集变量值
val variables = buildVariablesJson()
val signatures = buildSignaturesJson()

// 调用 API 提交
DocumentApi.submitDocument(templateId, recordId, variables, signatures)
```

**2. DocumentApi 新增方法**
```kotlin
// 提交生成的文书
suspend fun submitDocument(data: DocumentSubmitRequest): DocumentSubmitResponse
```

**3. 草稿保存机制**
- 签名前：本地保存草稿到 Room
- 签名后：调用提交 API
- 提交成功后：删除本地草稿，更新 syncStatus

### 3.3 数据模型

**DocumentSubmitRequest：**
```kotlin
data class DocumentSubmitRequest(
    val templateId: Long,
    val recordId: Long?,        // 关联执法记录
    val unitId: Long?,
    val variables: String,      // JSON
    val signatures: String,      // JSON
    val status: String = "1"    // 已生成
)
```

---

## 4. 验收标准

### 前端 Vue
- [ ] 模板管理：新增/编辑/删除功能正常
- [ ] 套组管理：新增/编辑/删除功能正常
- [ ] 文书记录：列表展示、详情查看、下载功能正常

### Android 端
- [ ] 变量填写完成后可提交
- [ ] 电子签名正常保存
- [ ] 提交成功后显示成功提示

---

## 5. 技术约束

- 前端：Vue 2.6 + Element UI，遵循现有 RuoYi 模式
- Android：Kotlin + MVVM，遵循现有项目结构
- API：RESTful 风格，JSON 传输
