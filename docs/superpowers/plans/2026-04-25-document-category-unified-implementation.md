# 文书分类统一管理实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 统一前后端的文书分类系统，让移动端和后台管理系统使用同一套分类数据

**Architecture:** 通过 `category_id` 外键关联模板表和分类表，移动端按 `categoryId` 查询匹配

**Tech Stack:** Spring Boot + MyBatis (后端), Vue + Element UI (前端), Kotlin + Room (Android)

---

## 阶段一：数据修正

- [ ] **1.1: 更新数据库模板的 category_id**

```sql
UPDATE sys_document_template SET category_id = 2 WHERE category = '快速制作文书';
UPDATE sys_document_template SET category_id = 1 WHERE category = '其他模板';
```

---

## 阶段二：前端改造

**文件:** `ruoyi-ui/src/views/system/document/template/index.vue`

- [ ] **2.1: 删除硬编码的 templateTypeOptions 数组**

  将第 186-192 行的：
  ```javascript
  templateTypeOptions: [
    { label: '现场笔录类', value: 'SCENE_RECORD' },
    ...
  ]
  ```
  替换为：
  ```javascript
  categoryOptions: []
  ```

- [ ] **2.2: 导入分类 API**

  在 `import { listTemplate, getTemplate, addTemplate, updateTemplate, delTemplate } from "@/api/system/document"` 后添加：
  ```javascript
  import { listCategory } from "@/api/system/documentCategory"
  ```

- [ ] **2.3: 添加分类加载**

  在 `created()` 中添加：
  ```javascript
  this.getList()
  this.getCategoryOptions()
  ```
  添加新方法：
  ```javascript
  getCategoryOptions() {
    listCategory().then(response => {
      this.categoryOptions = response.rows || (response.data && response.data.rows) || []
    })
  }
  ```

- [ ] **2.4: 修改表单下拉框**

  将"模板类型"改为"所属分类"，修改第 118-127 行：
  ```html
  <el-form-item label="所属分类" prop="categoryId">
    <el-select v-model="form.categoryId" placeholder="请选择所属分类" @change="handleCategoryChange" style="width: 100%">
      <el-option
        v-for="item in categoryOptions"
        :key="item.categoryId"
        :label="item.categoryName"
        :value="item.categoryId"
      />
    </el-select>
  </el-form-item>
  ```

- [ ] **2.5: 添加 handleCategoryChange 方法**

  在 `methods` 中添加：
  ```javascript
  handleCategoryChange(categoryId) {
    const category = this.categoryOptions.find(c => c.categoryId === categoryId)
    if (category) {
      this.form.category = category.categoryName
    }
  }
  ```

- [ ] **2.6: 列表新增"所属分类"列**

  在表格中添加新列（在模板类型列后）：
  ```html
  <el-table-column label="所属分类" align="center" prop="category" />
  ```

- [ ] **2.7: 修改 reset 方法确保 category 重置**

  在 `reset()` 表单重置部分添加 `category: null`

---

## 阶段三：Android 适配

**文件检查 (已完成的修改):**

- [ ] **3.1: 确认 DocumentTemplate 模型包含 categoryId**

  文件: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/model/DocumentTemplate.kt`
  ```kotlin
  data class DocumentTemplate(
    val id: Long,
    val templateCode: String,
    val templateName: String,
    val templateType: String?,
    val category: String?,
    val categoryId: Long = 0,  // 已添加
    ...
  )
  ```

- [ ] **3.2: 确认 API 解析 categoryId**

  文件: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/api/DocumentApi.kt`
  ```kotlin
  private fun parseTemplate(obj: JSONObject): DocumentTemplate {
    return DocumentTemplate(
      ...
      categoryId = obj.optLong("categoryId", 0),  // 已添加
      ...
    )
  }
  ```

- [ ] **3.3: 确认 toEntity() 使用 categoryId**

  文件: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/repository/DocumentRepository.kt`
  ```kotlin
  private fun DocumentTemplate.toEntity() = DocumentTemplateEntity(
    ...
    categoryId = categoryId,  // 使用 API 返回的 categoryId
    ...
  )
  ```

- [ ] **3.4: 确认 DAO 有 getTemplatesByCategoryName 方法**

  文件: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/DocumentTemplateDao.kt`
  ```kotlin
  @Query("SELECT * FROM document_template WHERE isActive = '1' AND category = :categoryName ORDER BY sort ASC, id ASC")
  fun getTemplatesByCategoryName(categoryName: String): Flow<List<DocumentTemplateEntity>>
  ```

- [ ] **3.5: 确认 DocumentRepository 有 getTemplatesByCategoryName 方法**

  文件: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/repository/DocumentRepository.kt`
  ```kotlin
  fun getTemplatesByCategoryName(categoryName: String): Flow<List<DocumentTemplateEntity>> {
    return templateDao.getTemplatesByCategoryName(categoryName)
  }
  ```

---

## 阶段四：后端验证

- [ ] **4.1: 验证分类 API 返回正确数据**

  ```bash
  curl http://localhost:8080/api/admin/document/category/sync
  ```

- [ ] **4.2: 验证模板 API 返回 categoryId**

  ```bash
  curl http://localhost:8080/api/admin/document/sync/template
  ```

- [ ] **4.3: 验证数据库 category_id 已更新**

  ```sql
  SELECT id, template_name, category_id, category FROM sys_document_template;
  ```
