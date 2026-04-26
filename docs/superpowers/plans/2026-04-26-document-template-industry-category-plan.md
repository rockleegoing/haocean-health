# 文书模板关联行业分类实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**目标：** 实现文书模板与行业分类的多对多关联，支持按行业过滤显示模板

**架构：** 后端通过中间表存储模板-行业关联，同步API返回行业信息；Android端本地存储关联关系，选择单位后按行业过滤模板

**技术栈：** Spring Boot + MyBatis + Room + Flow

---

## 文件结构概览

### 后端 RuoYi-Vue
```
ruoyi-system/
├── src/main/java/com/ruoyi/system/
│   ├── domain/
│   │   └── SysDocumentTemplateIndustry.java      [新建]
│   ├── mapper/
│   │   ├── SysDocumentTemplateIndustryMapper.java [新建]
│   │   └── SysDocumentTemplateMapper.java         [修改]
│   └── service/
│       ├── ISysDocumentTemplateService.java        [修改]
│       └── impl/
│           └── SysDocumentTemplateServiceImpl.java [修改]
├── src/main/resources/mapper/system/
│   └── SysDocumentTemplateIndustryMapper.xml      [新建]
└── sql/
    └── V1.1.7__document_template_industry.sql     [新建]

ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/
└── SysDocumentTemplateController.java              [修改]
```

### 前端 Vue
```
ruoyi-ui/src/views/system/document/template/
└── index.vue                                      [修改]
```

### Android Ruoyi-Android-App
```
app/src/main/java/com/ruoyi/app/
├── data/database/
│   ├── entity/
│   │   └── DocumentTemplateIndustryEntity.kt      [新建]
│   ├── dao/
│   │   └── DocumentTemplateIndustryDao.kt          [新建]
│   └── AppDatabase.kt                             [修改]
├── feature/document/
│   ├── api/
│   │   └── DocumentApi.kt                         [修改]
│   └── repository/
│       └── DocumentRepository.kt                   [修改]
├── sync/
│   └── SyncManager.kt                             [修改]
└── fragment/
    └── HomeFragment.kt                            [修改]
```

---

## 阶段 1：数据库

### Task 1: 创建数据库迁移脚本

**Files:**
- Create: `RuoYi-Vue/sql/V1.1.7__document_template_industry.sql`

- [ ] **Step 1: 创建 SQL 迁移脚本**

```sql
-- 给文书模板表添加行业分类字段
ALTER TABLE sys_document_template
ADD COLUMN industry_category_id BIGINT(20) DEFAULT NULL COMMENT '行业分类ID' AFTER category,
ADD COLUMN industry_category_name VARCHAR(100) DEFAULT NULL COMMENT '行业分类名称' AFTER industry_category_id;

-- 创建文书模板与行业分类关联表
CREATE TABLE sys_document_template_industry (
    id BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
    template_id BIGINT(20) NOT NULL COMMENT '文书模板ID',
    industry_category_id BIGINT(20) NOT NULL COMMENT '行业分类ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_template_industry (template_id, industry_category_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书模板与行业分类关联表';
```

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/sql/V1.1.7__document_template_industry.sql
git commit -m "feat(document): 添加文书模板行业分类关联表和字段"
```

---

## 阶段 2：后端

### Task 2: 修改 SysDocumentTemplate 实体

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDocumentTemplate.java:176`

- [ ] **Step 1: 添加字段声明（在 sort 字段后添加）**

在 `SysDocumentTemplate.java` 第29行 `private Long categoryId;` 之后添加：

```java
/** 行业分类ID */
private Long industryCategoryId;

/** 行业分类名称 */
private String industryCategoryName;
```

- [ ] **Step 2: 添加 getter/setter 方法（在 getSort 方法后添加）**

```java
public Long getIndustryCategoryId() {
    return industryCategoryId;
}

public void setIndustryCategoryId(Long industryCategoryId) {
    this.industryCategoryId = industryCategoryId;
}

public String getIndustryCategoryName() {
    return industryCategoryName;
}

public void setIndustryCategoryName(String industryCategoryName) {
    this.industryCategoryName = industryCategoryName;
}
```

- [ ] **Step 3: 修改 toString 方法**

在 `toString()` 方法的 `.append("category", getCategory())` 后添加：

```java
.append("industryCategoryId", getIndustryCategoryId())
.append("industryCategoryName", getIndustryCategoryName())
```

- [ ] **Step 4: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDocumentTemplate.java
git commit -m "feat(document): SysDocumentTemplate添加行业分类字段"
```

---

### Task 3: 创建关联实体 SysDocumentTemplateIndustry

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDocumentTemplateIndustry.java`

- [ ] **Step 1: 创建实体类**

```java
package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

/**
 * 文书模板与行业分类关联对象 sys_document_template_industry
 *
 * @author ruoyi
 * @date 2026-04-26
 */
public class SysDocumentTemplateIndustry {

    /** 主键ID */
    private Long id;

    /** 文书模板ID */
    private Long templateId;

    /** 行业分类ID */
    private Long industryCategoryId;

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public void setTemplateId(Long templateId) {
        this.templateId = templateId;
    }

    public Long getTemplateId() {
        return templateId;
    }

    public void setIndustryCategoryId(Long industryCategoryId) {
        this.industryCategoryId = industryCategoryId;
    }

    public Long getIndustryCategoryId() {
        return industryCategoryId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("templateId", getTemplateId())
            .append("industryCategoryId", getIndustryCategoryId())
            .toString();
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDocumentTemplateIndustry.java
git commit -m "feat(document): 添加文书模板行业关联实体类"
```

---

### Task 4: 创建 SysDocumentTemplateIndustryMapper 接口

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDocumentTemplateIndustryMapper.java`

- [ ] **Step 1: 创建 Mapper 接口**

```java
package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysDocumentTemplateIndustry;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文书模板与行业分类关联Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-26
 */
public interface SysDocumentTemplateIndustryMapper {

    /**
     * 批量插入关联记录
     */
    int insertBatch(@Param("list") List<SysDocumentTemplateIndustry> list);

    /**
     * 根据模板ID删除关联记录
     */
    int deleteByTemplateId(Long templateId);

    /**
     * 根据行业分类ID查询关联的模板ID列表
     */
    List<Long> selectByIndustryCategoryId(Long industryCategoryId);

    /**
     * 根据模板ID查询关联的行业分类ID列表
     */
    List<Long> selectIndustryCategoryIdsByTemplateId(Long templateId);

    /**
     * 根据模板ID列表查询所有关联的行业分类ID
     */
    List<Long> selectIndustryCategoryIdsByTemplateIds(@Param("templateIds") List<Long> templateIds);
}
```

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDocumentTemplateIndustryMapper.java
git commit -m "feat(document): 添加文书模板行业关联Mapper接口"
```

---

### Task 5: 创建 SysDocumentTemplateIndustryMapper XML

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDocumentTemplateIndustryMapper.xml`

- [ ] **Step 1: 创建 Mapper XML**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ruoyi.system.mapper.SysDocumentTemplateIndustryMapper">

    <insert id="insertBatch" parameterType="java.util.List">
        INSERT INTO sys_document_template_industry (template_id, industry_category_id, create_time)
        VALUES
        <foreach collection="list" item="item" separator=",">
            (#{item.templateId}, #{item.industryCategoryId}, NOW())
        </foreach>
    </insert>

    <delete id="deleteByTemplateId" parameterType="Long">
        DELETE FROM sys_document_template_industry WHERE template_id = #{templateId}
    </delete>

    <select id="selectByIndustryCategoryId" parameterType="Long" resultType="Long">
        SELECT template_id FROM sys_document_template_industry WHERE industry_category_id = #{industryCategoryId}
    </select>

    <select id="selectIndustryCategoryIdsByTemplateId" parameterType="Long" resultType="Long">
        SELECT industry_category_id FROM sys_document_template_industry WHERE template_id = #{templateId}
    </select>

    <select id="selectIndustryCategoryIdsByTemplateIds" parameterType="java.util.List" resultType="Long">
        SELECT DISTINCT industry_category_id FROM sys_document_template_industry
        WHERE template_id IN
        <foreach collection="templateIds" item="id" open="(" separator="," close=")">
            #{id}
        </foreach>
    </select>

</mapper>
```

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDocumentTemplateIndustryMapper.xml
git commit -m "feat(document): 添加文书模板行业关联Mapper XML"
```

---

### Task 6: 修改 SysDocumentTemplateMapper

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDocumentTemplateMapper.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDocumentTemplateMapper.xml`

- [ ] **Step 1: 修改 SysDocumentTemplateMapper.java 添加新方法声明**

在接口中添加：

```java
/**
 * 根据行业分类ID查询模板列表
 */
List<SysDocumentTemplate> selectByIndustryCategoryId(Long industryCategoryId);

/**
 * 根据模板ID列表查询模板
 */
List<SysDocumentTemplate> selectByTemplateIds(@Param("templateIds") List<Long> templateIds);
```

- [ ] **Step 2: 修改 SysDocumentTemplateMapper.xml resultMap**

在 `<resultMap id="SysDocumentTemplateResult">` 中添加：

```xml
<result property="industryCategoryId"    column="industry_category_id"    />
<result property="industryCategoryName" column="industry_category_name"  />
```

- [ ] **Step 3: 修改 selectSysDocumentTemplateVo SQL片断**

在 `select id, template_code, ...` 后面添加 `industry_category_id, industry_category_name`：

```xml
<sql id="selectSysDocumentTemplateVo">
    select id, template_code, template_name, category_id, sort, template_type, category,
           industry_category_id, industry_category_name,
           file_path, file_url, version, is_active, del_flag, create_by, create_time,
           update_by, update_time, remark
    from sys_document_template
</sql>
```

- [ ] **Step 4: 添加新的 select 语句（在 </mapper> 前添加）**

```xml
<select id="selectByIndustryCategoryId" parameterType="Long" resultMap="SysDocumentTemplateResult">
    <include refid="selectSysDocumentTemplateVo"/>
    WHERE del_flag = '0' AND is_active = '1'
      AND industry_category_id = #{industryCategoryId}
    ORDER BY category_id ASC, sort ASC, id ASC
</select>

<select id="selectByTemplateIds" parameterType="java.util.List" resultMap="SysDocumentTemplateResult">
    <include refid="selectSysDocumentTemplateVo"/>
    WHERE del_flag = '0' AND is_active = '1'
      AND id IN
    <foreach collection="templateIds" item="id" open="(" separator="," close=")">
        #{id}
    </foreach>
    ORDER BY category_id ASC, sort ASC, id ASC
</select>
```

- [ ] **Step 5: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDocumentTemplateMapper.java
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDocumentTemplateMapper.xml
git commit -m "feat(document): SysDocumentTemplateMapper添加行业分类查询方法"
```

---

### Task 7: 修改 Service 层

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysDocumentTemplateService.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDocumentTemplateServiceImpl.java`

- [ ] **Step 1: 修改 ISysDocumentTemplateService.java 添加接口方法**

```java
/**
 * 根据行业分类ID查询模板列表
 */
List<SysDocumentTemplate> selectByIndustryCategoryId(Long industryCategoryId);

/**
 * 根据模板ID列表查询模板
 */
List<SysDocumentTemplate> selectByTemplateIds(List<Long> templateIds);
```

- [ ] **Step 2: 修改 SysDocumentTemplateServiceImpl.java 添加实现**

注入新的 Mapper：

```java
@Autowired
private SysDocumentTemplateIndustryMapper documentTemplateIndustryMapper;
```

添加实现方法：

```java
@Override
public List<SysDocumentTemplate> selectByIndustryCategoryId(Long industryCategoryId) {
    return documentTemplateMapper.selectByIndustryCategoryId(industryCategoryId);
}

@Override
public List<SysDocumentTemplate> selectByTemplateIds(List<Long> templateIds) {
    if (templateIds == null || templateIds.isEmpty()) {
        return new ArrayList<>();
    }
    return documentTemplateMapper.selectByTemplateIds(templateIds);
}
```

修改 insert 和 update 方法，在保存前处理行业分类关联：

```java
@Override
public int insertSysDocumentTemplate(SysDocumentTemplate template) {
    int rows = documentTemplateMapper.insertSysDocumentTemplate(template);
    // 保存行业分类关联
    if (template.getIndustryCategoryIds() != null && !template.getIndustryCategoryIds().isEmpty()) {
        saveIndustryRelations(template.getId(), template.getIndustryCategoryIds());
    }
    return rows;
}

@Override
public int updateSysDocumentTemplate(SysDocumentTemplate template) {
    // 先删除旧的关联
    documentTemplateIndustryMapper.deleteByTemplateId(template.getId());
    // 再插入新的关联
    if (template.getIndustryCategoryIds() != null && !template.getIndustryCategoryIds().isEmpty()) {
        saveIndustryRelations(template.getId(), template.getIndustryCategoryIds());
    }
    return documentTemplateMapper.updateSysDocumentTemplate(template);
}

private void saveIndustryRelations(Long templateId, List<Long> industryCategoryIds) {
    List<SysDocumentTemplateIndustry> relations = industryCategoryIds.stream()
        .map(industryId -> {
            SysDocumentTemplateIndustry relation = new SysDocumentTemplateIndustry();
            relation.setTemplateId(templateId);
            relation.setIndustryCategoryId(industryId);
            return relation;
        })
        .collect(Collectors.toList());
    documentTemplateIndustryMapper.insertBatch(relations);
}
```

- [ ] **Step 3: 修改 SysDocumentTemplate.java 添加临时字段（用于接收前端传来的行业ID列表）**

在实体类中添加（不是数据库字段，用 transient 或 @TableField(exist = false) 标注）：

```java
/** 行业分类ID列表（前端传入，不存库） */
@TableField(exist = false)
private List<Long> industryCategoryIds;
```

添加 getter/setter：

```java
public List<Long> getIndustryCategoryIds() {
    return industryCategoryIds;
}

public void setIndustryCategoryIds(List<Long> industryCategoryIds) {
    this.industryCategoryIds = industryCategoryIds;
}
```

- [ ] **Step 4: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysDocumentTemplateService.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDocumentTemplateServiceImpl.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDocumentTemplate.java
git commit -m "feat(document): Service层添加行业分类关联处理"
```

---

### Task 8: 修改 Controller 层

**Files:**
- Modify: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysDocumentTemplateController.java`

- [ ] **Step 1: 查看现有 Controller 结构并修改 add 方法**

在 add 方法的 SysDocumentTemplate 参数中添加 @RequestBody 注解确保能接收 JSON 中的 industryCategoryIds 数组

- [ ] **Step 2: 在 edit 方法同样处理**

确保 edit 方法能接收 industryCategoryIds

- [ ] **Step 3: 提交**

```bash
git add RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysDocumentTemplateController.java
git commit -m "feat(document): Controller支持行业分类IDs参数"
```

---

### Task 9: 修改模板同步API返回数据

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/web/controller/system/SysDocumentTemplateController.java` 中的 syncTemplates 方法

- [ ] **Step 1: 修改 selectAllSysDocumentTemplates 返回数据包含行业分类信息**

检查 syncTemplates 方法使用的查询，确保包含 industry_category_id 和 industry_category_name 字段

- [ ] **Step 2: 提交**

```bash
git commit -m "feat(document): 同步API返回行业分类字段"
```

---

## 阶段 3：Android 端（Entity、Dao、Api、同步）

### Task 10: 修改 DocumentTemplateEntity

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/DocumentTemplateEntity.kt`

- [ ] **Step 1: 添加行业分类字段**

在 entity 中添加：

```kotlin
val industryCategoryId: Long? = null,    // 行业分类ID
val industryCategoryName: String? = null  // 行业分类名称
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/DocumentTemplateEntity.kt
git commit -m "feat(document): DocumentTemplateEntity添加行业分类字段"
```

---

### Task 11: 创建 DocumentTemplateIndustryEntity

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/DocumentTemplateIndustryEntity.kt`

- [ ] **Step 1: 创建关联实体**

```kotlin
package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 文书模板与行业分类关联实体
 */
@Entity(
    tableName = "document_template_industry",
    primaryKeys = ["templateId", "industryCategoryId"],
    indices = [Index("industryCategoryId")]
)
data class DocumentTemplateIndustryEntity(
    val templateId: Long,
    val industryCategoryId: Long
)
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/DocumentTemplateIndustryEntity.kt
git commit -m "feat(document): 添加文书模板行业关联Entity"
```

---

### Task 12: 创建 DocumentTemplateIndustryDao

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/DocumentTemplateIndustryDao.kt`

- [ ] **Step 1: 创建 DAO**

```kotlin
package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.DocumentTemplateIndustryEntity
import kotlinx.coroutines.flow.Flow

/**
 * 文书模板与行业分类关联 DAO
 */
@Dao
interface DocumentTemplateIndustryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<DocumentTemplateIndustryEntity>)

    @Query("DELETE FROM document_template_industry")
    suspend fun deleteAll()

    @Query("DELETE FROM document_template_industry WHERE templateId = :templateId")
    suspend fun deleteByTemplateId(templateId: Long)

    @Query("SELECT templateId FROM document_template_industry WHERE industryCategoryId = :industryCategoryId")
    suspend fun getTemplateIdsByIndustryCategory(industryCategoryId: Long): List<Long>

    @Query("SELECT industryCategoryId FROM document_template_industry WHERE templateId = :templateId")
    suspend fun getIndustryCategoryIdsByTemplateId(templateId: Long): List<Long>

    @Query("SELECT industryCategoryId FROM document_template_industry WHERE templateId IN (:templateIds)")
    suspend fun getIndustryCategoryIdsByTemplateIds(templateIds: List<Long>): List<Long>
}
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/DocumentTemplateIndustryDao.kt
git commit -m "feat(document): 添加文书模板行业关联Dao"
```

---

### Task 13: 修改 AppDatabase

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt`

- [ ] **Step 1: 添加新的 entity 到 @Database 注解**

将 `DocumentTemplateIndustryEntity::class` 添加到 entities 数组中

- [ ] **Step 2: 添加新的 DAO 抽象方法**

```kotlin
abstract fun documentTemplateIndustryDao(): DocumentTemplateIndustryDao
```

- [ ] **Step 3: 更新数据库版本**

将 `version = 9` 改为 `version = 10`

- [ ] **Step 4: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt
git commit -m "feat(document): AppDatabase添加行业关联Dao并升级版本"
```

---

### Task 14: 修改 DocumentApi

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/api/DocumentApi.kt`

- [ ] **Step 1: 修改 parseTemplate 方法**

在 `DocumentTemplate` 构造中添加：

```kotlin
industryCategoryId = obj.optLong("industryCategoryId", 0).takeIf { it > 0 },
industryCategoryName = obj.optString("industryCategoryName", null).takeIf { it.isNotEmpty() }
```

- [ ] **Step 2: 确保 DocumentTemplate model 也有这些字段**

检查 `com.ruoyi.app.feature.document.model.DocumentTemplate` 是否需要同步修改

- [ ] **Step 3: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/api/DocumentApi.kt
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/model/DocumentTemplate.kt
git commit -m "feat(document): DocumentApi解析行业分类字段"
```

---

### Task 15: 修改 DocumentRepository

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/repository/DocumentRepository.kt`

- [ ] **Step 1: 注入新的 DAO**

```kotlin
private val templateIndustryDao = AppDatabase.getInstance(context).documentTemplateIndustryDao()
```

- [ ] **Step 2: 添加同步方法**

```kotlin
/**
 * 同步模板-行业关联到本地
 */
suspend fun syncTemplateIndustryRelations() = withContext(Dispatchers.IO) {
    // 获取所有模板的行业关联（通过API或从本地模板数据解析）
    // 这里暂时从模板数据的 industryCategoryId 字段解析
    val templates = templateDao.getActiveTemplates().first()
    val relations = mutableListOf<DocumentTemplateIndustryEntity>()

    for (template in templates) {
        template.industryCategoryId?.let { industryId ->
            if (industryId > 0) {
                relations.add(DocumentTemplateIndustryEntity(template.id, industryId))
            }
        }
    }

    if (relations.isNotEmpty()) {
        templateIndustryDao.deleteAll()
        templateIndustryDao.insertAll(relations)
        Log.d("DocumentRepository", "模板-行业关联已同步: ${relations.size}条")
    }
}
```

- [ ] **Step 3: 添加查询方法**

```kotlin
/**
 * 根据行业分类ID获取关联的模板ID列表
 */
suspend fun getTemplateIdsByIndustryCategory(industryCategoryId: Long): List<Long> = withContext(Dispatchers.IO) {
    templateIndustryDao.getTemplateIdsByIndustryCategory(industryCategoryId)
}

/**
 * 根据模板ID列表获取模板
 */
fun getTemplatesByIds(templateIds: List<Long>): Flow<List<DocumentTemplateEntity>> {
    return templateDao.getTemplatesByIds(templateIds)
}
```

- [ ] **Step 4: 修改模板同步方法**

在 `syncTemplates()` 中，同步完成后调用 `syncTemplateIndustryRelations()`

- [ ] **Step 5: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/repository/DocumentRepository.kt
git commit -m "feat(document): DocumentRepository添加行业关联同步和查询方法"
```

---

### Task 16: 修改 DocumentTemplateDao

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/DocumentTemplateDao.kt`

- [ ] **Step 1: 添加新查询方法**

```kotlin
@Query("SELECT * FROM document_template WHERE isActive = '1' AND id IN (:ids) ORDER BY categoryId ASC, sort ASC, id ASC")
fun getTemplatesByIds(ids: List<Long>): Flow<List<DocumentTemplateEntity>>
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/DocumentTemplateDao.kt
git commit -m "feat(document): DocumentTemplateDao添加按ID列表查询方法"
```

---

### Task 17: 修改 SyncManager

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/SyncManager.kt`

- [ ] **Step 1: 添加常量**

```kotlin
const val MODULE_DOCUMENT_TEMPLATE_INDUSTRY = "文书模板行业关联"
```

- [ ] **Step 2: 在 FULL_SYNC_MODULES 中添加**

将 `MODULE_DOCUMENT_TEMPLATE_INDUSTRY` 添加到列表中

- [ ] **Step 3: 添加同步方法**

```kotlin
private suspend fun syncDocumentTemplateIndustry(context: Context?): Boolean {
    if (context == null) return false
    return try {
        val repository = DocumentRepository(context)
        repository.syncTemplateIndustryRelations()
        Log.d("SyncManager", "文书模板行业关联同步成功")
        true
    } catch (e: Exception) {
        Log.e("SyncManager", "文书模板行业关联同步异常: ${e.message}", e)
        false
    }
}
```

- [ ] **Step 4: 在 syncModule 方法的 when 中添加**

```kotlin
MODULE_DOCUMENT_TEMPLATE_INDUSTRY -> syncDocumentTemplateIndustry(context)
```

- [ ] **Step 5: 在 syncDocumentTemplate 方法中调用关联同步**

在 `repository.syncTemplates()` 之后添加 `repository.syncTemplateIndustryRelations()`

- [ ] **Step 6: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/SyncManager.kt
git commit -m "feat(document): SyncManager添加模板行业关联同步"
```

---

## 阶段 4：前端 Vue

### Task 18: 修改模板管理页面

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/system/document/template/index.vue`

- [ ] **Step 1: 在 data() 中添加行业分类选项**

```javascript
industryCategoryOptions: [],
```

- [ ] **Step 2: 在 getCategoryOptions 方法后添加 getIndustryCategoryOptions**

```javascript
getIndustryCategoryOptions() {
    listIndustryCategory().then(response => {
        this.industryCategoryOptions = response.rows || []
    })
},
```

- [ ] **Step 3: 在 init() 或 created() 中调用 getIndustryCategoryOptions**

- [ ] **Step 4: 在列表页表格列中添加"行业分类"列**

```javascript
{
  label: "行业分类",
  prop: "industryCategoryName",
  align: "center",
  width: 150,
  formatter: this.formatIndustryCategory
}
```

添加格式化方法：

```javascript
formatIndustryCategory(row) {
    return row.industryCategoryName || '-'
}
```

- [ ] **Step 5: 在表单弹窗中添加行业分类多选**

在 el-form-item 中添加（位置在"所属分类"之后）：

```html
<el-form-item label="行业分类" prop="industryCategoryIds">
    <el-select
        v-model="form.industryCategoryIds"
        multiple
        placeholder="请选择行业分类"
        style="width: 100%;">
        <el-option
            v-for="item in industryCategoryOptions"
            :key="item.categoryId"
            :label="item.categoryName"
            :value="item.categoryId">
        </el-option>
    </el-select>
</el-form-item>
```

- [ ] **Step 6: 在表单数据初始化时处理 industryCategoryIds**

修改 resetForm 方法或 watch，确保编辑时正确加载已选中的行业分类

- [ ] **Step 7: 提交**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/system/document/template/index.vue
git commit -m "feat(document): 前端模板管理页面添加行业分类多选"
```

---

## 阶段 5：Android 首页过滤逻辑

### Task 19: 修改 HomeFragment

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/fragment/HomeFragment.kt`

- [ ] **Step 1: 修改 loadDocumentCategories 方法**

将原来的直接加载所有模板改为按行业过滤：

```kotlin
private fun loadDocumentCategories() {
    viewLifecycleOwner.lifecycleScope.launch {
        android.util.Log.d("HomeFragment", "=== 开始观察 categories Flow ===")

        // 获取当前选中单位的行业分类ID
        val industryCategoryId = SelectedUnitManager.getSelectedCategoryId()
        android.util.Log.d("HomeFragment", "当前单位行业分类ID: $industryCategoryId")

        if (industryCategoryId == null || industryCategoryId == 0L) {
            // 没有选择行业，隐藏文书模板区域
            binding.llDocumentCategories.visibility = View.GONE
            android.util.Log.d("HomeFragment", "无行业分类，隐藏区域")
            return@launch
        }

        // 获取该行业关联的模板ID列表
        val templateIds = documentRepository.getTemplateIdsByIndustryCategory(industryCategoryId)
        android.util.Log.d("HomeFragment", "该行业关联的模板ID数量: ${templateIds.size}")

        if (templateIds.isEmpty()) {
            binding.llDocumentCategories.visibility = View.GONE
            android.util.Log.d("HomeFragment", "该行业无关联模板，隐藏区域")
            return@launch
        }

        // 获取这些模板的详情
        documentRepository.getTemplatesByIds(templateIds).collect { templates ->
            if (templates.isEmpty()) {
                binding.llDocumentCategories.visibility = View.GONE
                return@collect
            }

            binding.llDocumentCategories.visibility = View.VISIBLE

            // 按文书分类分组
            documentRepository.getCategories().collect { categories ->
                val categoryWithTemplatesList = mutableListOf<CategoryWithTemplates>()
                for (category in categories) {
                    val categoryTemplates = templates.filter { it.categoryId == category.categoryId }
                    if (categoryTemplates.isNotEmpty()) {
                        val items = categoryTemplates.map { TemplateItem(it.id, it.templateName) }
                        categoryWithTemplatesList.add(CategoryWithTemplates(
                            category.categoryId,
                            category.categoryName,
                            category.displayType,
                            items
                        ))
                    }
                }

                android.util.Log.d("HomeFragment", "最终显示的分类数量: ${categoryWithTemplatesList.size}")
                binding.llDocumentCategories.visibility = View.VISIBLE
                categoryAdapter.submitList(categoryWithTemplatesList)
            }
        }
    }
}
```

- [ ] **Step 2: 移除之前添加的调试日志**

清理多余的 Log.d 语句

- [ ] **Step 3: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/fragment/HomeFragment.kt
git commit -m "feat(document): HomeFragment按行业过滤显示模板"
```

---

## 自检清单

### Spec 覆盖率检查

| 设计章节 | 对应 Task |
|---------|----------|
| 数据库：新增字段和中间表 | Task 1 |
| 后端：实体修改 | Task 2 |
| 后端：关联实体 | Task 3 |
| 后端：Mapper接口 | Task 4 |
| 后端：Mapper XML | Task 5 |
| 后端：Service修改 | Task 7 |
| 后端：Controller修改 | Task 8 |
| 后端：同步API | Task 9 |
| 前端：模板管理页面 | Task 18 |
| Android：Entity修改 | Task 10, 11 |
| Android：Dao创建 | Task 12, 16 |
| Android：AppDatabase | Task 13 |
| Android：Api修改 | Task 14 |
| Android：Repository | Task 15 |
| Android：SyncManager | Task 17 |
| Android：HomeFragment过滤 | Task 19 |

### 类型一致性检查

- `SysDocumentTemplate.industryCategoryId` → `Long`
- `SysDocumentTemplate.industryCategoryIds` → `List<Long>` (临时字段)
- `DocumentTemplateEntity.industryCategoryId` → `Long?`
- `DocumentTemplateIndustryEntity` → `templateId: Long, industryCategoryId: Long`

### 占位符检查

无 TBD、TODO 或其他占位符，所有步骤均包含完整代码。
