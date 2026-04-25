# 文书分类功能实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在首页添加文书分类展示，支持按分类显示文书模板

**Architecture:** 后端新增分类表和API，App同步分类数据并在首页按分类展示

**Tech Stack:** Spring Boot + MyBatis (后端), Room + Kotlin (Android)

---

## 文件结构

### 后端新建
- `RuoYi-Vue/sql/V1.1.5__document_category.sql` - 分类表SQL
- `RuoYi-Vue/sql/V1.1.6__add_template_category_sort.sql` - 模板表字段变更
- `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDocumentCategory.java` - 分类实体
- `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDocumentCategoryMapper.java` - Mapper
- `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysDocumentCategoryService.java` - Service接口
- `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDocumentCategoryServiceImpl.java` - Service实现
- `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysDocumentCategoryController.java` - Controller

### 后端修改
- `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDocumentTemplate.java` - 添加categoryId+sort字段
- `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDocumentTemplateMapper.java` - 添加selectAllTemplates
- `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysDocumentController.java` - 添加分类同步接口

### Android新建
- `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/DocumentCategoryEntity.kt` - 分类实体
- `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/DocumentCategoryDao.kt` - 分类DAO

### Android修改
- `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/DocumentTemplateEntity.kt` - 添加categoryId+sort
- `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/DocumentTemplateDao.kt` - 添加按categoryId查询
- `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt` - 添加分类表和DAO
- `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/model/DocumentCategory.kt` - API响应模型
- `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/api/DocumentApi.kt` - 添加分类同步API
- `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/repository/DocumentRepository.kt` - 添加分类数据读取
- `Ruoyi-Android-App/app/src/main/res/layout/fragment_home.xml` - 添加分类展示区域
- `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/fragment/HomeFragment.kt` - 加载分类数据
- `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/ui/adapter/DocumentCategoryAdapter.kt` - 分类区域适配器

---

## 实施步骤

### Phase 1: 后端数据库和实体

- [ ] **Task 1.1: 创建分类表SQL**
  - Create: `RuoYi-Vue/sql/V1.1.5__document_category.sql`

```sql
-- 文书分类表
CREATE TABLE sys_document_category (
    category_id    BIGINT(20)      NOT NULL AUTO_INCREMENT  COMMENT '分类ID',
    category_name  VARCHAR(100)    NOT NULL                  COMMENT '分类名称',
    display_type   VARCHAR(20)     NOT NULL DEFAULT 'grid'   COMMENT '展示方式：grid=九宫格，table=表格，list=列表',
    sort           INT(11)         NOT NULL DEFAULT 0        COMMENT '排序',
    status         CHAR(1)         NOT NULL DEFAULT '0'      COMMENT '状态（0正常 1停用）',
    create_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time    DATETIME        NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书分类表';

-- 初始化内置"其他模板"分类（category_id=0）
INSERT INTO sys_document_category (category_id, category_name, display_type, sort, status) VALUES (0, '其他模板', 'grid', 999, '0');
```

- [ ] **Task 1.2: 创建模板表字段变更SQL**
  - Create: `RuoYi-Vue/sql/V1.1.6__add_template_category_sort.sql`

```sql
-- 给文书模板表添加分类和排序字段
ALTER TABLE sys_document_template
ADD COLUMN category_id BIGINT(20) DEFAULT 0 COMMENT '分类ID，0表示其他模板' AFTER template_name,
ADD COLUMN sort INT(11) NOT NULL DEFAULT 0 COMMENT '排序';
```

- [ ] **Task 1.3: 创建分类实体类**
  - Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDocumentCategory.java`

```java
package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

public class SysDocumentCategory extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long categoryId;
    @Excel(name = "分类名称")
    private String categoryName;
    @Excel(name = "展示方式")
    private String displayType;
    @Excel(name = "排序")
    private Integer sort;
    @Excel(name = "状态")
    private String status;

    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }
    public String getDisplayType() { return displayType; }
    public void setDisplayType(String displayType) { this.displayType = displayType; }
    public Integer getSort() { return sort; }
    public void setSort(Integer sort) { this.sort = sort; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("categoryId", getCategoryId())
            .append("categoryName", getCategoryName())
            .append("displayType", getDisplayType())
            .append("sort", getSort())
            .append("status", getStatus())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
```

- [ ] **Task 1.4: 创建分类Mapper**
  - Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDocumentCategoryMapper.java`

```java
package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysDocumentCategory;

public interface SysDocumentCategoryMapper {
    public SysDocumentCategory selectSysDocumentCategoryById(Long categoryId);
    public List<SysDocumentCategory> selectSysDocumentCategoryList(SysDocumentCategory category);
    public List<SysDocumentCategory> selectAllSysDocumentCategories();
    public int insertSysDocumentCategory(SysDocumentCategory category);
    public int updateSysDocumentCategory(SysDocumentCategory category);
    public int deleteSysDocumentCategoryById(Long categoryId);
    public int deleteSysDocumentCategoryByIds(Long[] categoryIds);
}
```

- [ ] **Task 1.5: 创建分类Mapper XML**
  - Create: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDocumentCategoryMapper.xml`

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ruoyi.system.mapper.SysDocumentCategoryMapper">
    <resultMap type="com.ruoyi.system.domain.SysDocumentCategory" id="SysDocumentCategoryResult">
        <result property="categoryId" column="category_id"/>
        <result property="categoryName" column="category_name"/>
        <result property="displayType" column="display_type"/>
        <result property="sort" column="sort"/>
        <result property="status" column="status"/>
        <result property="createTime" column="create_time"/>
        <result property="updateTime" column="update_time"/>
    </resultMap>

    <sql id="selectCategoryVo">
        SELECT category_id, category_name, display_type, sort, status, create_time, update_time
        FROM sys_document_category
    </sql>

    <select id="selectSysDocumentCategoryList" resultMap="SysDocumentCategoryResult">
        <include refid="selectCategoryVo"/>
        WHERE del_flag = '0'
        <if test="categoryName != null and categoryName != ''">
            AND category_name LIKE CONCAT('%', #{categoryName}, '%')
        </if>
        <if test="status != null and status != ''">
            AND status = #{status}
        </if>
        ORDER BY sort ASC, category_id ASC
    </select>

    <select id="selectAllSysDocumentCategories" resultMap="SysDocumentCategoryResult">
        <include refid="selectCategoryVo"/>
        WHERE del_flag = '0' AND status = '0'
        ORDER BY sort ASC, category_id ASC
    </select>

    <select id="selectSysDocumentCategoryById" resultMap="SysDocumentCategoryResult">
        <include refid="selectCategoryVo"/>
        WHERE category_id = #{categoryId}
    </select>

    <insert id="insertSysDocumentCategory" parameterType="com.ruoyi.system.domain.SysDocumentCategory" useGeneratedKeys="true" keyProperty="categoryId">
        INSERT INTO sys_document_category (
            <if test="categoryName != null and categoryName != ''">category_name,</if>
            <if test="displayType != null and displayType != ''">display_type,</if>
            <if test="sort != null">sort,</if>
            <if test="status != null and status != ''">status,</if>
            create_time
        ) VALUES (
            <if test="categoryName != null and categoryName != ''">#{categoryName},</if>
            <if test="displayType != null and displayType != ''">#{displayType},</if>
            <if test="sort != null">#{sort},</if>
            <if test="status != null and status != ''">#{status},</if>
            NOW()
        )
    </insert>

    <update id="updateSysDocumentCategory" parameterType="com.ruoyi.system.domain.SysDocumentCategory">
        UPDATE sys_document_category SET
            <if test="categoryName != null and categoryName != ''">category_name = #{categoryName},</if>
            <if test="displayType != null and displayType != ''">display_type = #{displayType},</if>
            <if test="sort != null">sort = #{sort},</if>
            <if test="status != null and status != ''">status = #{status},</if>
            update_time = NOW()
        WHERE category_id = #{categoryId}
    </update>

    <delete id="deleteSysDocumentCategoryById">
        UPDATE sys_document_category SET del_flag = '1' WHERE category_id = #{categoryId}
    </delete>

    <delete id="deleteSysDocumentCategoryByIds">
        UPDATE sys_document_category SET del_flag = '1' WHERE category_id IN
        <foreach collection="array" item="categoryId" open="(" separator="," close=")">
            #{categoryId}
        </foreach>
    </delete>
</mapper>
```

- [ ] **Task 1.6: 创建分类Service接口**
  - Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysDocumentCategoryService.java`

```java
package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysDocumentCategory;

public interface ISysDocumentCategoryService {
    public SysDocumentCategory selectSysDocumentCategoryById(Long categoryId);
    public List<SysDocumentCategory> selectSysDocumentCategoryList(SysDocumentCategory category);
    public List<SysDocumentCategory> selectAllSysDocumentCategories();
    public int insertSysDocumentCategory(SysDocumentCategory category);
    public int updateSysDocumentCategory(SysDocumentCategory category);
    public int deleteSysDocumentCategoryById(Long categoryId);
    public int deleteSysDocumentCategoryByIds(Long[] categoryIds);
}
```

- [ ] **Task 1.7: 创建分类Service实现**
  - Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDocumentCategoryServiceImpl.java`

```java
package com.ruoyi.system.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.SysDocumentCategoryMapper;
import com.ruoyi.system.domain.SysDocumentCategory;
import com.ruoyi.system.service.ISysDocumentCategoryService;

@Service
public class SysDocumentCategoryServiceImpl implements ISysDocumentCategoryService {

    @Autowired
    private SysDocumentCategoryMapper categoryMapper;

    @Override
    public SysDocumentCategory selectSysDocumentCategoryById(Long categoryId) {
        return categoryMapper.selectSysDocumentCategoryById(categoryId);
    }

    @Override
    public List<SysDocumentCategory> selectSysDocumentCategoryList(SysDocumentCategory category) {
        return categoryMapper.selectSysDocumentCategoryList(category);
    }

    @Override
    public List<SysDocumentCategory> selectAllSysDocumentCategories() {
        return categoryMapper.selectAllSysDocumentCategories();
    }

    @Override
    public int insertSysDocumentCategory(SysDocumentCategory category) {
        return categoryMapper.insertSysDocumentCategory(category);
    }

    @Override
    public int updateSysDocumentCategory(SysDocumentCategory category) {
        return categoryMapper.updateSysDocumentCategory(category);
    }

    @Override
    public int deleteSysDocumentCategoryById(Long categoryId) {
        return categoryMapper.deleteSysDocumentCategoryById(categoryId);
    }

    @Override
    public int deleteSysDocumentCategoryByIds(Long[] categoryIds) {
        return categoryMapper.deleteSysDocumentCategoryByIds(categoryIds);
    }
}
```

- [ ] **Task 1.8: 创建分类Controller**
  - Create: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysDocumentCategoryController.java`

```java
package com.ruoyi.web.controller.system;

import java.util.List;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysDocumentCategory;
import com.ruoyi.system.service.ISysDocumentCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/document/category")
public class SysDocumentCategoryController extends BaseController {

    @Autowired
    private ISysDocumentCategoryService categoryService;

    @GetMapping("/list")
    public AjaxResult list(SysDocumentCategory category) {
        startPage();
        List<SysDocumentCategory> list = categoryService.selectSysDocumentCategoryList(category);
        return AjaxResult.success(getDataTable(list));
    }

    @GetMapping("/{categoryId}")
    public AjaxResult getInfo(@PathVariable Long categoryId) {
        return AjaxResult.success(categoryService.selectSysDocumentCategoryById(categoryId));
    }

    @PostMapping
    public AjaxResult add(@RequestBody SysDocumentCategory category) {
        return AjaxResult.success(categoryService.insertSysDocumentCategory(category));
    }

    @PutMapping
    public AjaxResult edit(@RequestBody SysDocumentCategory category) {
        return AjaxResult.success(categoryService.updateSysDocumentCategory(category));
    }

    @DeleteMapping("/{categoryIds}")
    public AjaxResult remove(@PathVariable Long[] categoryIds) {
        return AjaxResult.success(categoryService.deleteSysDocumentCategoryByIds(categoryIds));
    }

    @Anonymous
    @GetMapping("/sync")
    public AjaxResult syncCategory() {
        List<SysDocumentCategory> list = categoryService.selectAllSysDocumentCategories();
        return AjaxResult.success(list);
    }
}
```

### Phase 2: 后端模板表修改

- [ ] **Task 2.1: 修改模板实体添加字段**
  - Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDocumentTemplate.java`

在类中添加两个字段（getter/setter方法）:

```java
/** 分类ID，0表示其他模板 */
private Long categoryId;

/** 排序 */
private Integer sort;

public Long getCategoryId() { return categoryId; }
public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }

public Integer getSort() { return sort; }
public void setSort(Integer sort) { this.sort = sort; }
```

- [ ] **Task 2.2: 修改模板Mapper添加查询方法**
  - Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDocumentTemplateMapper.java`

添加方法:
```java
/**
 * 查询所有启用的模板（用于App同步，含categoryId和sort）
 */
public List<SysDocumentTemplate> selectAllSysDocumentTemplates();
```

- [ ] **Task 2.3: 修改模板Mapper XML添加selectAllTemplates**
  - Modify: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDocumentTemplateMapper.xml`

在 `<mapper>` 内添加:

```xml
<select id="selectAllSysDocumentTemplates" resultMap="SysDocumentTemplateResult">
    SELECT id, template_code, template_name, category_id, sort, template_type, category,
           file_path, file_url, version, is_active, del_flag, create_time, update_time
    FROM sys_document_template
    WHERE del_flag = '0' AND is_active = '0'
    ORDER BY category_id ASC, sort ASC, id ASC
</select>
```

- [ ] **Task 2.4: 修改Controller添加分类同步接口**
  - Modify: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysDocumentController.java`

在移动端API区域添加:

```java
/**
 * 同步分类（下行的文书分类）
 */
@Anonymous
@GetMapping("/sync/category")
public AjaxResult syncCategory() {
    List<SysDocumentCategory> list = sysDocumentService.selectAllSysDocumentCategories();
    return AjaxResult.success(list);
}
```

需要添加导入:
```java
import com.ruoyi.system.domain.SysDocumentCategory;
import com.ruoyi.system.service.ISysDocumentCategoryService;
```

### Phase 3: Android本地数据库

- [ ] **Task 3.1: 创建分类实体**
  - Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/DocumentCategoryEntity.kt`

```kotlin
package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "document_category")
data class DocumentCategoryEntity(
    @PrimaryKey
    val categoryId: Long,
    val categoryName: String,
    val displayType: String,
    val sort: Int
)
```

- [ ] **Task 3.2: 创建分类DAO**
  - Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/DocumentCategoryDao.kt`

```kotlin
package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.DocumentCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentCategoryDao {
    @Query("SELECT * FROM document_category ORDER BY sort ASC, categoryId ASC")
    fun getAllCategories(): Flow<List<DocumentCategoryEntity>>

    @Query("SELECT * FROM document_category WHERE categoryId = :categoryId")
    suspend fun getCategoryById(categoryId: Long): DocumentCategoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: DocumentCategoryEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(categories: List<DocumentCategoryEntity>)

    @Delete
    suspend fun delete(category: DocumentCategoryEntity)

    @Query("DELETE FROM document_category")
    suspend fun deleteAll()
}
```

- [ ] **Task 3.3: 修改模板实体添加字段**
  - Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/DocumentTemplateEntity.kt`

添加两个字段:
```kotlin
val categoryId: Long = 0,
val sort: Int = 0
```

- [ ] **Task 3.4: 修改模板DAO添加按categoryId查询**
  - Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/DocumentTemplateDao.kt`

添加方法:
```kotlin
@Query("SELECT * FROM document_template WHERE isActive = '1' AND categoryId = :categoryId ORDER BY sort ASC, id ASC")
fun getTemplatesByCategory(categoryId: Long): Flow<List<DocumentTemplateEntity>>

@Query("SELECT * FROM document_template WHERE isActive = '1' AND categoryId != 0 ORDER BY categoryId ASC, sort ASC, id ASC")
fun getTemplatesWithCategory(): Flow<List<DocumentTemplateEntity>>
```

- [ ] **Task 3.5: 修改AppDatabase添加分类表和DAO**
  - Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt`

1. 添加导入:
```kotlin
import com.ruoyi.app.data.database.entity.DocumentCategoryEntity
import com.ruoyi.app.data.database.dao.DocumentCategoryDao
```

2. 在entities数组中添加:
```kotlin
DocumentCategoryEntity::class
```

3. 在abstract函数中添加:
```kotlin
abstract fun documentCategoryDao(): DocumentCategoryDao
```

4. 版本号从8改为9:
```kotlin
version = 9
```

### Phase 4: Android API和Repository

- [ ] **Task 4.1: 创建分类API响应模型**
  - Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/model/DocumentCategory.kt`

```kotlin
package com.ruoyi.app.feature.document.model

data class DocumentCategory(
    val categoryId: Long,
    val categoryName: String,
    val displayType: String,
    val sort: Int,
    val status: String,
    val createTime: String?
)
```

- [ ] **Task 4.2: 修改DocumentApi添加分类同步**
  - Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/api/DocumentApi.kt`

添加方法:
```kotlin
/**
 * 同步文书分类(下行)
 */
suspend fun syncCategories(): List<DocumentCategory> = withContext(Dispatchers.IO) {
    val request = Request.Builder()
        .url("${ConfigApi.baseUrl}/api/admin/document/category/sync")
        .get()
        .build()

    val response = client.newCall(request).execute()
    parseCategoryListResponse(response.body?.string() ?: "")
}
```

添加解析方法:
```kotlin
private fun parseCategoryListResponse(json: String): List<DocumentCategory> {
    return try {
        val obj = JSONObject(json)
        val dataArray = obj.optJSONArray("data") ?: JSONArray()
        val categories = mutableListOf<DocumentCategory>()
        for (i in 0 until dataArray.length()) {
            val obj = dataArray.getJSONObject(i)
            categories.add(DocumentCategory(
                categoryId = obj.optLong("categoryId", 0),
                categoryName = obj.optString("categoryName", ""),
                displayType = obj.optString("displayType", "grid"),
                sort = obj.optInt("sort", 0),
                status = obj.optString("status", "0"),
                createTime = obj.optString("createTime", null)
            ))
        }
        categories
    } catch (e: Exception) {
        emptyList()
    }
}
```

- [ ] **Task 4.3: 修改DocumentRepository添加分类操作**
  - Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/repository/DocumentRepository.kt`

1. 添加导入:
```kotlin
import com.ruoyi.app.data.database.entity.DocumentCategoryEntity
import com.ruoyi.app.feature.document.model.DocumentCategory
```

2. 添加categoryDao:
```kotlin
private val categoryDao = AppDatabase.getInstance(context).documentCategoryDao()
```

3. 添加同步方法:
```kotlin
suspend fun syncCategories() = withContext(Dispatchers.IO) {
    val categories = DocumentApi.syncCategories()
    val entities = categories.map { it.toEntity() }
    categoryDao.insertAll(entities)
}
```

4. 添加本地查询方法:
```kotlin
fun getCategories(): Flow<List<DocumentCategoryEntity>> {
    return categoryDao.getAllCategories()
}
```

5. 添加转换方法:
```kotlin
private fun DocumentCategory.toEntity() = DocumentCategoryEntity(
    categoryId = categoryId,
    categoryName = categoryName,
    displayType = displayType,
    sort = sort
)
```

### Phase 5: Android UI实现

- [ ] **Task 5.1: 创建分类适配器**
  - Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/ui/adapter/DocumentCategoryAdapter.kt`

```kotlin
package com.ruoyi.app.feature.document.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.R

data class CategoryWithTemplates(
    val categoryId: Long,
    val categoryName: String,
    val displayType: String,
    val templates: List<TemplateItem>
)

data class TemplateItem(
    val id: Long,
    val templateName: String
)

class DocumentCategoryAdapter(
    private val onTemplateClick: (Long, String) -> Unit
) : RecyclerView.Adapter<DocumentCategoryAdapter.CategoryViewHolder>() {

    private var categories: List<CategoryWithTemplates> = emptyList()

    fun submitList(list: List<CategoryWithTemplates>) {
        categories = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document_category, parent, false)
        return CategoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        holder.bind(categories[position])
    }

    override fun getItemCount() = categories.size

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val titleText: TextView = itemView.findViewById(R.id.tv_category_title)
        private val recyclerView: RecyclerView = itemView.findViewById(R.id.rv_templates)

        fun bind(category: CategoryWithTemplates) {
            titleText.text = category.categoryName

            val spanCount = when (category.displayType) {
                "grid" -> 3
                "table" -> 2
                else -> 1
            }

            recyclerView.layoutManager = if (category.displayType == "list") {
                LinearLayoutManager(itemView.context)
            } else {
                GridLayoutManager(itemView.context, spanCount)
            }

            val adapter = TemplateAdapter(category.templates) { id, name ->
                onTemplateClick(id, name)
            }
            recyclerView.adapter = adapter
        }
    }

    class TemplateAdapter(
        private val templates: List<TemplateItem>,
        private val onClick: (Long, String) -> Unit
    ) : RecyclerView.Adapter<TemplateAdapter.TemplateViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TemplateViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_document_template_simple, parent, false)
            return TemplateViewHolder(view)
        }

        override fun onBindViewHolder(holder: TemplateViewHolder, position: Int) {
            holder.bind(templates[position])
        }

        override fun getItemCount() = templates.size

        inner class TemplateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            private val nameText: TextView = itemView.findViewById(R.id.tv_template_name)

            fun bind(template: TemplateItem) {
                nameText.text = template.templateName
                itemView.setOnClickListener { onClick(template.id, template.templateName) }
            }
        }
    }
}
```

- [ ] **Task 5.2: 创建分类区域布局**
  - Create: `Ruoyi-Android-App/app/src/main/res/layout/item_document_category.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:id="@+id/tv_category_title"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textSize="16sp"
        android:textStyle="bold"
        android:textColor="@color/text_primary" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_templates"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="8dp"
        android:nestedScrollingEnabled="false" />

</LinearLayout>
```

- [ ] **Task 5.3: 创建模板项布局**
  - Create: `Ruoyi-Android-App/app/src/main/res/layout/item_document_template_simple.xml`

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:gravity="center"
    android:padding="8dp"
    android:background="?attr/selectableItemBackground">

    <ImageView
        android:id="@+id/iv_template_icon"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:src="@drawable/ic_document" />

    <TextView
        android:id="@+id/tv_template_name"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_marginTop="4dp"
        android:textSize="12sp"
        android:gravity="center"
        android:maxLines="2"
        android:ellipsize="end" />

</LinearLayout>
```

- [ ] **Task 5.4: 修改首页布局添加分类区域**
  - Modify: `Ruoyi-Android-App/app/src/main/res/layout/fragment_home.xml`

在现有布局末尾（`</LinearLayout>` 之前）添加:

```xml
<!-- 文书分类区域 -->
<LinearLayout
    android:id="@+id/ll_document_categories"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="16dp">

    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="文书模板"
        android:textSize="18sp"
        android:textStyle="bold"
        android:textColor="@color/text_primary" />

    <androidx.recyclerview.widget.RecyclerView
        android:id="@+id/rv_document_categories"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="12dp" />

</LinearLayout>
```

- [ ] **Task 5.5: 修改HomeFragment加载分类数据**
  - Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/fragment/HomeFragment.kt`

1. 添加导入:
```kotlin
import com.ruoyi.app.feature.document.model.DocumentCategory
import com.ruoyi.app.feature.document.repository.DocumentRepository
import com.ruoyi.app.feature.document.ui.adapter.CategoryWithTemplates
import com.ruoyi.app.feature.document.ui.adapter.TemplateItem
import com.ruoyi.app.feature.document.ui.adapter.DocumentCategoryAdapter
import com.therouter.TheRouter
```

2. 在类中添加:
```kotlin
private lateinit var categoryAdapter: DocumentCategoryAdapter
private val documentRepository by lazy { DocumentRepository(requireContext()) }
```

3. 在initView()末尾添加:
```kotlin
setupDocumentCategories()
```

4. 添加方法:
```kotlin
private fun setupDocumentCategories() {
    categoryAdapter = DocumentCategoryAdapter { templateId, templateName ->
        // 跳转到文书填写页
        val unitId = SelectedUnitManager.getSelectedUnitId()
        if (unitId == null) {
            ToastUtils.show("请先选择执法单位")
            return@DocumentCategoryAdapter
        }
        TheRouter.build(Constant.documentFillRoute)
            .with("templateId", templateId)
            .with("templateName", templateName)
            .with("unitId", unitId)
            .navigation()
    }
    binding.rvDocumentCategories.adapter = categoryAdapter

    loadDocumentCategories()
}

private fun loadDocumentCategories() {
    viewLifecycleOwner.lifecycleScope.launch {
        documentRepository.getCategories().collect { categories ->
            if (categories.isEmpty()) {
                binding.llDocumentCategories.visibility = View.GONE
                return@collect
            }

            val categoryWithTemplatesList = mutableListOf<CategoryWithTemplates>()
            for (category in categories) {
                documentRepository.getTemplatesByCategory(category.categoryId).collect { templates ->
                    if (templates.isNotEmpty()) {
                        val items = templates.map { TemplateItem(it.id, it.templateName) }
                        categoryWithTemplatesList.add(CategoryWithTemplates(
                            category.categoryId,
                            category.categoryName,
                            category.displayType,
                            items
                        ))
                    }
                }
            }
            categoryAdapter.submitList(categoryWithTemplatesList)
        }
    }
}
```

需要添加协程导入:
```kotlin
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
```

---

## 测试要点

1. 分类CRUD功能正常（后端接口测试）
2. 模板关联分类正常
3. App同步分类数据成功
4. 首页按分类展示正确
5. 不同displayType展示方式正确
6. 点击模板跳转文书编辑页正常
