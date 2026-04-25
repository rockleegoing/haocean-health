# 文书管理页面功能完善实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 完善文书管理模块的前端 Vue 管理页面和 Android 端功能

**Architecture:** 前端采用 Vue 2.6 + Element UI，后端已有完整 REST API；Android 端采用 MVVM 架构，通过 DocumentRepository 访问 DocumentApi

**Tech Stack:** Vue 2.6, Element UI, Kotlin, MVVM, OkHttp, Room

---

## 第一阶段：前端 Vue 管理页面

### Task 1: 模板管理 - 修复 index.vue API 调用

**Files:**
- Modify: `ruoyi-ui/src/views/system/document/template/index.vue:219-290`

- [ ] **Step 1: 修复 handleUpdate 方法**

```javascript
/** 修改按钮操作 */
handleUpdate(row) {
  this.reset()
  const templateId = row.id || this.ids
  getTemplate(templateId).then(response => {
    this.form = response.data
    this.open = true
    this.title = "修改文书模板"
  })
},
```

- [ ] **Step 2: 修复 submitForm 方法**

```javascript
/** 提交按钮 */
submitForm() {
  this.$refs["form"].validate(valid => {
    if (valid) {
      if (this.form.id != null) {
        updateTemplate(this.form).then(response => {
          this.$modal.msgSuccess("修改成功")
          this.open = false
          this.getList()
        })
      } else {
        addTemplate(this.form).then(response => {
          this.$modal.msgSuccess("新增成功")
          this.open = false
          this.getList()
        })
      }
    }
  })
},
```

- [ ] **Step 3: 修复 handleDelete 方法**

```javascript
/** 删除按钮操作 */
handleDelete(row) {
  const ids = row.id || this.ids
  this.$modal.confirm('是否确认删除模板编号为"' + ids + '"的数据项？').then(() => {
    return delTemplate(ids)
  }).then(() => {
    this.getList()
    this.$modal.msgSuccess("删除成功")
  }).catch(() => {})
},
```

- [ ] **Step 4: 修复 handleAdd 方法**

```javascript
/** 新增按钮操作 */
handleAdd() {
  this.reset()
  this.open = true
  this.title = "添加文书模板"
},
```

- [ ] **Step 5: 提交**

```bash
git add ruoyi-ui/src/views/system/document/template/index.vue
git commit -m "fix(frontend): 修复模板管理API调用"
```

---

### Task 2: 模板管理 - 添加编辑对话框到 index.vue

**Files:**
- Modify: `ruoyi-ui/src/views/system/document/template/index.vue`

当前 index.vue 没有编辑对话框，需要添加。

- [ ] **Step 1: 在 template 标签内、pagination 之前添加对话框**

```vue
    <!-- 添加或修改模板对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="模板编码" prop="templateCode">
          <el-input v-model="form.templateCode" placeholder="请输入模板编码" />
        </el-form-item>
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="form.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="模板类型" prop="templateType">
          <el-select v-model="form.templateType" placeholder="请选择模板类型" style="width: 100%">
            <el-option
              v-for="item in templateTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="文件URL" prop="fileUrl">
          <el-input v-model="form.fileUrl" placeholder="请输入模板文件URL" />
        </el-form-item>
        <el-form-item label="版本" prop="version">
          <el-input v-model="form.version" placeholder="请输入版本号" />
        </el-form-item>
        <el-form-item label="是否启用" prop="isActive">
          <el-radio-group v-model="form.isActive">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
```

- [ ] **Step 2: 在 data() 中添加 form 初始化和 rules**

```javascript
form: {},
rules: {
  templateName: [
    { required: true, message: "模板名称不能为空", trigger: "blur" }
  ],
  templateCode: [
    { required: true, message: "模板编码不能为空", trigger: "blur" }
  ]
},
```

- [ ] **Step 3: 在 reset() 方法中更新 form 初始化**

```javascript
reset() {
  this.form = {
    id: null,
    templateCode: null,
    templateName: null,
    templateType: null,
    fileUrl: null,
    version: '1.0',
    isActive: '1',
    remark: null
  }
  this.resetForm("form")
},
```

- [ ] **Step 4: 添加 cancel 方法**

```javascript
cancel() {
  this.open = false
  this.reset()
},
```

- [ ] **Step 5: 提交**

```bash
git add ruoyi-ui/src/views/system/document/template/index.vue
git commit -m "feat(frontend): 添加模板管理编辑对话框"
```

---

### Task 3: 套组管理 - 修复 index.vue API 调用

**Files:**
- Modify: `ruoyi-ui/src/views/system/document/group/index.vue`

- [ ] **Step 1: 修复 handleUpdate 方法，使用 getGroup 而不是 getDocumentGroup**

```javascript
/** 修改按钮操作 */
handleUpdate(row) {
  this.reset()
  const groupId = row.id || this.ids
  getGroup(groupId).then(response => {
    this.form = response.data
    this.open = true
    this.title = "修改文书套组"
  })
},
```

- [ ] **Step 2: 修复 submitForm 方法，使用 addGroup/updateGroup**

```javascript
/** 提交按钮 */
submitForm() {
  this.$refs["form"].validate(valid => {
    if (valid) {
      if (this.form.id != null) {
        updateGroup(this.form).then(response => {
          this.$modal.msgSuccess("修改成功")
          this.open = false
          this.getList()
        })
      } else {
        addGroup(this.form).then(response => {
          this.$modal.msgSuccess("新增成功")
          this.open = false
          this.getList()
        })
      }
    }
  })
},
```

- [ ] **Step 3: 修复 handleDelete 方法**

```javascript
/** 删除按钮操作 */
handleDelete(row) {
  const ids = row.id || this.ids
  this.$modal.confirm('是否确认删除套组编号为"' + ids + '"的数据项？').then(() => {
    return delGroup(ids)
  }).then(() => {
    this.getList()
    this.$modal.msgSuccess("删除成功")
  }).catch(() => {})
},
```

- [ ] **Step 4: 修复 handleAdd 方法**

```javascript
/** 新增按钮操作 */
handleAdd() {
  this.reset()
  this.open = true
  this.title = "添加文书套组"
},
```

- [ ] **Step 5: 提交**

```bash
git add ruoyi-ui/src/views/system/document/group/index.vue
git commit -m "fix(frontend): 修复套组管理API调用"
```

---

### Task 4: 套组管理 - 添加编辑对话框到 index.vue

**Files:**
- Modify: `ruoyi-ui/src/views/system/document/group/index.vue`

- [ ] **Step 1: 在 template 标签内添加对话框（在 pagination 之前）**

```vue
    <!-- 添加或修改套组对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="套组编码" prop="groupCode">
          <el-input v-model="form.groupCode" placeholder="请输入套组编码" />
        </el-form-item>
        <el-form-item label="套组名称" prop="groupName">
          <el-input v-model="form.groupName" placeholder="请输入套组名称" />
        </el-form-item>
        <el-form-item label="套组类型" prop="groupType">
          <el-select v-model="form.groupType" placeholder="请选择套组类型" style="width: 100%">
            <el-option
              v-for="item in groupTypeOptions"
              :key="item.value"
              :label="item.label"
              :value="item.value"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="是否启用" prop="isActive">
          <el-radio-group v-model="form.isActive">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
```

- [ ] **Step 2: 在 data() 中添加 form, rules, open, title**

```javascript
form: {},
rules: {
  groupName: [
    { required: true, message: "套组名称不能为空", trigger: "blur" }
  ],
  groupCode: [
    { required: true, message: "套组编码不能为空", trigger: "blur" }
  ]
},
```

- [ ] **Step 3: 在 reset() 中更新 form 初始化**

```javascript
reset() {
  this.form = {
    id: null,
    groupCode: null,
    groupName: null,
    groupType: null,
    isActive: '1',
    remark: null
  }
  this.resetForm("form")
},
```

- [ ] **Step 4: 添加 cancel 方法**

```javascript
cancel() {
  this.open = false
  this.reset()
},
```

- [ ] **Step 5: 提交**

```bash
git add ruoyi-ui/src/views/system/document/group/index.vue
git commit -m "feat(frontend): 添加套组管理编辑对话框"
```

---

### Task 5: 文书记录 - 实现详情、下载、删除功能

**Files:**
- Modify: `ruoyi-ui/src/views/system/document/record/index.vue`

- [ ] **Step 1: 添加详情对话框模板（在 pagination 之前）**

```vue
    <!-- 文书记录详情对话框 -->
    <el-dialog title="文书记录详情" :visible.sync="detailVisible" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="文书编号">{{ detailData.documentNo }}</el-descriptions-item>
        <el-descriptions-item label="模板名称">{{ detailData.templateName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailData.status === '0'" type="info">草稿</el-tag>
          <el-tag v-else-if="detailData.status === '1'" type="success">已生成</el-tag>
          <el-tag v-else type="primary">已打印</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="同步状态">
          <el-tag v-if="detailData.syncStatus === '0'" type="warning">未同步</el-tag>
          <el-tag v-else type="success">已同步</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
      </el-descriptions>
      <el-divider>变量内容</el-divider>
      <pre>{{ formatJson(detailData.variables) }}</pre>
      <div slot="footer">
        <el-button @click="detailVisible = false">关 闭</el-button>
      </div>
    </el-dialog>
```

- [ ] **Step 2: 在 data() 中添加 detailVisible, detailData**

```javascript
detailVisible: false,
detailData: {},
```

- [ ] **Step 3: 修复 handleDetail 方法**

```javascript
handleDetail(row) {
  getRecord(row.id).then(response => {
    this.detailData = response.data || {}
    this.detailVisible = true
  })
},
```

- [ ] **Step 4: 修复 handleDownload 方法**

```javascript
handleDownload(row) {
  window.open('/prod-api/admin/document/download/' + row.id, '_blank')
},
```

- [ ] **Step 5: 修复 handleDelete 方法**

```javascript
handleDelete(row) {
  const ids = row.id || this.ids
  this.$modal.confirm('是否确认删除该文书记录？').then(() => {
    return delRecord(ids)
  }).then(() => {
    this.getList()
    this.$modal.msgSuccess("删除成功")
  }).catch(() => {})
},
```

- [ ] **Step 6: 添加 formatJson 辅助方法**

```javascript
formatJson(str) {
  if (!str) return ''
  try {
    return JSON.stringify(JSON.parse(str), null, 2)
  } catch (e) {
    return str
  }
},
```

- [ ] **Step 7: 提交**

```bash
git add ruoyi-ui/src/views/system/document/record/index.vue
git commit -m "feat(frontend): 实现文书记录详情下载删除功能"
```

---

## 第二阶段：Android 端

### Task 6: Android - 添加 DocumentApi.submitDocument 方法

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/api/DocumentApi.kt`

- [ ] **Step 1: 添加提交请求数据类（在 DocumentApi.kt 中或新建 model）**

```kotlin
data class DocumentSubmitRequest(
    val templateId: Long,
    val recordId: Long? = null,
    val unitId: Long? = null,
    val variables: String,
    val signatures: String,
    val status: String = "1"
)

data class DocumentSubmitResponse(
    val code: Int,
    val msg: String,
    val data: DocumentRecord?
)
```

- [ ] **Step 2: 在 DocumentApi 中添加 submitDocument 方法**

```kotlin
/**
 * 提交生成的文书
 */
suspend fun submitDocument(request: DocumentSubmitRequest): DocumentSubmitResponse = withContext(Dispatchers.IO) {
    val json = JSONObject().apply {
        put("templateId", request.templateId)
        request.recordId?.let { put("recordId", it) }
        request.unitId?.let { put("unitId", it) }
        put("variables", request.variables)
        put("signatures", request.signatures)
        put("status", request.status)
    }

    val req = Request.Builder()
        .url("${ConfigApi.baseUrl}/api/document/record")
        .post(RequestBody.create(MediaType.parse("application/json"), json.toString()))
        .build()

    val response = client.newCall(req).execute()
    val body = response.body?.string() ?: ""
    parseSubmitResponse(body)
}

private fun parseSubmitResponse(json: String): DocumentSubmitResponse {
    return try {
        val obj = JSONObject(json)
        DocumentSubmitResponse(
            code = obj.optInt("code", 500),
            msg = obj.optString("msg", ""),
            data = null
        )
    } catch (e: Exception) {
        DocumentSubmitResponse(code = 500, msg = e.message ?: "解析失败")
    }
}
```

- [ ] **Step 3: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/api/DocumentApi.kt
git commit -m "feat(android): 添加文书提交API"
```

---

### Task 7: Android - 实现 DocumentFillActivity.submitForm

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/ui/DocumentFillActivity.kt`

- [ ] **Step 1: 实现 submitForm 方法**

```kotlin
/**
 * 提交文书
 */
private fun submitForm() {
    val variables = buildVariablesJson()
    val signatures = buildSignaturesJson()

    val request = DocumentSubmitRequest(
        templateId = templateId,
        recordId = recordId,
        unitId = unitId,
        variables = variables,
        signatures = signatures,
        status = "1"
    )

    lifecycleScope.launch {
        try {
            val response = DocumentApi.submitDocument(request)
            if (response.code == 200) {
                Toast.makeText(this@DocumentFillActivity, "提交成功", Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this@DocumentFillActivity, "提交失败: ${response.msg}", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Toast.makeText(this@DocumentFillActivity, "提交异常: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
}

/**
 * 构建变量 JSON
 */
private fun buildVariablesJson(): String {
    val map = mutableMapOf<String, Any?>()
    variableViews.forEach { (name, value) ->
        map[name] = value
    }
    return JSONObject(map).toString()
}

/**
 * 构建签名 JSON
 */
private fun buildSignaturesJson(): String {
    val map = mutableMapOf<String, String?>()
    signatureViews.forEach { (name, path) ->
        map[name] = path
    }
    return JSONObject(map).toString()
}
```

- [ ] **Step 2: 在签名完成后调用 submitForm**

修改签名回调逻辑，签名完成后自动提交。

- [ ] **Step 3: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/ui/DocumentFillActivity.kt
git commit -m "feat(android): 实现文书填写提交功能"
```

---

## 验收标准

### 前端 Vue
- [ ] 模板管理：新增/编辑/删除功能正常
- [ ] 套组管理：新增/编辑/删除功能正常
- [ ] 文书记录：列表展示、详情查看、下载功能正常

### Android 端
- [ ] 变量填写完成后可提交
- [ ] 电子签名正常保存
- [ ] 提交成功后显示成功提示
