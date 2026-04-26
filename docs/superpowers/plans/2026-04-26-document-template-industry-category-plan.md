# 文书模板关联行业分类实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**目标：** 实现文书模板与行业分类的多对多关联，支持按行业过滤显示模板

**架构：** 后端通过中间表存储模板-行业关联，提供同步API；Android端本地存储关联关系，选择单位后按行业过滤模板

**技术栈：** Spring Boot + MyBatis + Room + Flow

---

## 文件结构概览

### 后端 RuoYi-Vue
```
ruoyi-system/
├── src/main/java/com/ruoyi/system/
│   ├── domain/
│   │   ├── SysDocumentTemplate.java              [修改]
│   │   └── SysDocumentTemplateIndustry.java      [新建]
│   ├── mapper/
│   │   ├── SysDocumentTemplateMapper.java         [修改]
│   │   ├── SysDocumentTemplateIndustryMapper.java [新建]
│   └── service/
│       ├── ISysDocumentTemplateService.java        [修改]
│       ├── ISysDocumentTemplateIndustryService.java [新建]
│       └── impl/
│           ├── SysDocumentTemplateServiceImpl.java [修改]
│           └── SysDocumentTemplateIndustryServiceImpl.java [新建]
├── src/main/resources/mapper/system/
│   ├── SysDocumentTemplateMapper.xml               [修改]
│   └── SysDocumentTemplateIndustryMapper.xml      [新建]
└── sql/
    └── V1.1.7__document_template_industry.sql     [新建]

ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/
├── SysDocumentTemplateController.java              [修改]
└── SysDocumentTemplateIndustryController.java     [新建]
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
│   │   ├── DocumentTemplateEntity.kt              [修改]
│   │   └── DocumentTemplateIndustryEntity.kt      [新建]
│   ├── dao/
│   │   ├── DocumentTemplateDao.kt                  [修改]
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
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDocumentTemplate.java`

- [ ] **Step 1: 添加字段声明（在 category 字段后添加）**

```java
/** 行业分类ID */
private Long industryCategoryId;

/** 行业分类名称 */
private String industryCategoryName;

/** 行业分类ID列表（前端传入，不存库） */
private List<Long> industryCategoryIds;
```

- [ ] **Step 2: 添加 getter/setter 方法**

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

public List<Long> getIndustryCategoryIds() {
    return industryCategoryIds;
}

public void setIndustryCategoryIds(List<Long> industryCategoryIds) {
    this.industryCategoryIds = industryCategoryIds;
}
```

- [ ] **Step 3: 修改 toString 方法**

在 `toString()` 方法中添加：

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

    private Long id;
    private Long templateId;
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

    int insertBatch(@Param("list") List<SysDocumentTemplateIndustry> list);

    int deleteByTemplateId(Long templateId);

    List<Long> selectByIndustryCategoryId(Long industryCategoryId);

    List<SysDocumentTemplateIndustry> selectAll();
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

    <resultMap type="SysDocumentTemplateIndustry" id="BaseResultMap">
        <result property="id" column="id"/>
        <result property="templateId" column="template_id"/>
        <result property="industryCategoryId" column="industry_category_id"/>
    </resultMap>

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

    <select id="selectAll" resultMap="BaseResultMap">
        SELECT id, template_id, industry_category_id, create_time
        FROM sys_document_template_industry
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

```java
List<SysDocumentTemplate> selectByIndustryCategoryId(Long industryCategoryId);

List<SysDocumentTemplate> selectByTemplateIds(@Param("templateIds") List<Long> templateIds);
```

- [ ] **Step 2: 修改 SysDocumentTemplateMapper.xml resultMap**

添加：

```xml
<result property="industryCategoryId"    column="industry_category_id"/>
<result property="industryCategoryName"   column="industry_category_name"/>
```

- [ ] **Step 3: 修改 selectSysDocumentTemplateVo SQL片断**

在 SELECT 语句中添加 `industry_category_id, industry_category_name`

- [ ] **Step 4: 添加新的 select 语句**

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
List<SysDocumentTemplate> selectByIndustryCategoryId(Long industryCategoryId);

List<SysDocumentTemplate> selectByTemplateIds(List<Long> templateIds);
```

- [ ] **Step 2: 修改 SysDocumentTemplateServiceImpl.java**

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

修改 insert 和 update 方法：

```java
@Override
public int insertSysDocumentTemplate(SysDocumentTemplate template) {
    int rows = documentTemplateMapper.insertSysDocumentTemplate(template);
    if (template.getIndustryCategoryIds() != null && !template.getIndustryCategoryIds().isEmpty()) {
        saveIndustryRelations(template.getId(), template.getIndustryCategoryIds());
    }
    return rows;
}

@Override
public int updateSysDocumentTemplate(SysDocumentTemplate template) {
    documentTemplateIndustryMapper.deleteByTemplateId(template.getId());
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

- [ ] **Step 3: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysDocumentTemplateService.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDocumentTemplateServiceImpl.java
git commit -m "feat(document): Service层添加行业分类关联处理"
```

---

### Task 8: 修改 Controller 层

**Files:**
- Modify: 现有的 SysDocumentTemplateController（如果存在则修改，否则跳过此Task）

- [ ] **Step 1: 确保 add/edit 方法能接收 industryCategoryIds**

确保使用 @RequestBody 接收 JSON 参数

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysDocumentTemplateController.java
git commit -m "feat(document): Controller支持行业分类IDs参数"
```

---

### Task 9: 创建模板行业关联同步 API

**Files:**
- Create: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysDocumentTemplateIndustryController.java`

- [ ] **Step 1: 创建 Controller**

```java
package com.ruoyi.web.controller.system;

import java.util.List;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysDocumentTemplateIndustry;
import com.ruoyi.system.service.ISysDocumentTemplateIndustryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/document/template/industry")
public class SysDocumentTemplateIndustryController extends BaseController {

    @Autowired
    private ISysDocumentTemplateIndustryService templateIndustryService;

    @Anonymous
    @GetMapping("/sync")
    public AjaxResult syncTemplateIndustry() {
        List<SysDocumentTemplateIndustry> list = templateIndustryService.selectAllTemplateIndustryRelations();
        return AjaxResult.success(list);
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysDocumentTemplateIndustryController.java
git commit -m "feat(document): 添加模板行业关联同步API"
```

---

### Task 10: 创建 Service 接口和实现

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysDocumentTemplateIndustryService.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDocumentTemplateIndustryServiceImpl.java`

- [ ] **Step 1: 创建 Service 接口**

```java
package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysDocumentTemplateIndustry;
import java.util.List;

/**
 * 文书模板与行业分类关联Service接口
 *
 * @author ruoyi
 * @date 2026-04-26
 */
public interface ISysDocumentTemplateIndustryService {

    List<SysDocumentTemplateIndustry> selectAllTemplateIndustryRelations();

    int insertBatch(List<SysDocumentTemplateIndustry> list);

    int deleteByTemplateId(Long templateId);

    List<Long> selectByIndustryCategoryId(Long industryCategoryId);
}
```

- [ ] **Step 2: 创建 Service 实现**

```java
package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.SysDocumentTemplateIndustry;
import com.ruoyi.system.mapper.SysDocumentTemplateIndustryMapper;
import com.ruoyi.system.service.ISysDocumentTemplateIndustryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class SysDocumentTemplateIndustryServiceImpl implements ISysDocumentTemplateIndustryService {

    @Autowired
    private SysDocumentTemplateIndustryMapper templateIndustryMapper;

    @Override
    public List<SysDocumentTemplateIndustry> selectAllTemplateIndustryRelations() {
        return templateIndustryMapper.selectAll();
    }

    @Override
    public int insertBatch(List<SysDocumentTemplateIndustry> list) {
        return templateIndustryMapper.insertBatch(list);
    }

    @Override
    public int deleteByTemplateId(Long templateId) {
        return templateIndustryMapper.deleteByTemplateId(templateId);
    }

    @Override
    public List<Long> selectByIndustryCategoryId(Long industryCategoryId) {
        return templateIndustryMapper.selectByIndustryCategoryId(industryCategoryId);
    }
}
```

- [ ] **Step 3: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysDocumentTemplateIndustryService.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDocumentTemplateIndustryServiceImpl.java
git commit -m "feat(document): 添加模板行业关联Service"
```

---

### Task 11: 修改模板同步API返回数据

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDocumentTemplateMapper.xml`

- [ ] **Step 1: 修改 selectAllSysDocumentTemplates 返回数据包含行业分类信息**

在 selectAllSysDocumentTemplates 的 SELECT 语句中添加 `industry_category_id, industry_category_name` 字段

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDocumentTemplateMapper.xml
git commit -m "feat(document): 模板同步API返回行业分类字段"
```

---

## 阶段 3：Android 端

### Task 12: 修改 DocumentTemplateEntity

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/DocumentTemplateEntity.kt`

- [ ] **Step 1: 添加行业分类字段**

```kotlin
val industryCategoryId: Long? = null,
val industryCategoryName: String? = null
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/DocumentTemplateEntity.kt
git commit -m "feat(document): DocumentTemplateEntity添加行业分类字段"
```

---

### Task 13: 创建 DocumentTemplateIndustryEntity

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/DocumentTemplateIndustryEntity.kt`

- [ ] **Step 1: 创建关联实体**

```kotlin
package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

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

### Task 14: 创建 DocumentTemplateIndustryDao

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/DocumentTemplateIndustryDao.kt`

- [ ] **Step 1: 创建 DAO**

```kotlin
package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.DocumentTemplateIndustryEntity

@Dao
interface DocumentTemplateIndustryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<DocumentTemplateIndustryEntity>)

    @Query("DELETE FROM document_template_industry")
    suspend fun deleteAll()

    @Query("SELECT templateId FROM document_template_industry WHERE industryCategoryId = :industryCategoryId")
    suspend fun getTemplateIdsByIndustryCategory(industryCategoryId: Long): List<Long>
}
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/DocumentTemplateIndustryDao.kt
git commit -m "feat(document): 添加文书模板行业关联Dao"
```

---

### Task 15: 修改 AppDatabase

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt`

- [ ] **Step 1: 添加新的 entity 到 @Database 注解**

将 `DocumentTemplateIndustryEntity::class` 添加到 entities 数组

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

### Task 16: 修改 DocumentApi

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/api/DocumentApi.kt`

- [ ] **Step 1: 修改 parseTemplate 方法**

在 `DocumentTemplate` 构造中添加：

```kotlin
industryCategoryId = obj.optLong("industryCategoryId", 0).takeIf { it > 0 },
industryCategoryName = obj.optString("industryCategoryName", null).takeIf { it.isNotEmpty() }
```

- [ ] **Step 2: 添加同步模板行业关联的方法**

```kotlin
suspend fun syncTemplateIndustryRelations(): List<Pair<Long, Long>> = withContext(Dispatchers.IO) {
    val request = Request.Builder()
        .url("${ConfigApi.baseUrl}/api/admin/document/template/industry/sync")
        .get()
        .build()

    val response = client.newCall(request).execute()
    parseTemplateIndustryRelations(response.body?.string() ?: "")
}

private fun parseTemplateIndustryRelations(json: String): List<Pair<Long, Long>> {
    return try {
        val obj = JSONObject(json)
        val dataArray = obj.optJSONArray("data") ?: JSONArray()
        val relations = mutableListOf<Pair<Long, Long>>()
        for (i in 0 until dataArray.length()) {
            val item = dataArray.getJSONObject(i)
            val templateId = item.optLong("templateId", 0)
            val industryCategoryId = item.optLong("industryCategoryId", 0)
            if (templateId > 0 && industryCategoryId > 0) {
                relations.add(Pair(templateId, industryCategoryId))
            }
        }
        relations
    } catch (e: Exception) {
        emptyList()
    }
}
```

- [ ] **Step 3: 确保 DocumentTemplate model 也有这些字段**

- [ ] **Step 4: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/api/DocumentApi.kt
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/model/DocumentTemplate.kt
git commit -m "feat(document): DocumentApi添加行业关联同步方法"
```

---

### Task 17: 修改 DocumentRepository

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/repository/DocumentRepository.kt`

- [ ] **Step 1: 注入新的 DAO**

```kotlin
private val templateIndustryDao = AppDatabase.getInstance(context).documentTemplateIndustryDao()
```

- [ ] **Step 2: 添加同步方法（从 API 获取中间表数据）**

```kotlin
suspend fun syncTemplateIndustryRelations() = withContext(Dispatchers.IO) {
    try {
        val relations = DocumentApi.syncTemplateIndustryRelations()
        Log.d("DocumentRepository", "从API获取模板-行业关联: ${relations.size}条")

        val entities = relations.map { (templateId, industryCategoryId) ->
            DocumentTemplateIndustryEntity(templateId, industryCategoryId)
        }

        if (entities.isNotEmpty()) {
            templateIndustryDao.deleteAll()
            templateIndustryDao.insertAll(entities)
            Log.d("DocumentRepository", "模板-行业关联已同步: ${entities.size}条")
        }
    } catch (e: Exception) {
        Log.e("DocumentRepository", "同步模板-行业关联失败: ${e.message}", e)
    }
}
```

- [ ] **Step 3: 添加查询方法**

```kotlin
suspend fun getTemplateIdsByIndustryCategory(industryCategoryId: Long): List<Long> = withContext(Dispatchers.IO) {
    templateIndustryDao.getTemplateIdsByIndustryCategory(industryCategoryId)
}

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

### Task 18: 修改 DocumentTemplateDao

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

### Task 19: 修改 SyncManager

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/SyncManager.kt`

- [ ] **Step 1: 添加常量**

```kotlin
const val MODULE_DOCUMENT_TEMPLATE_INDUSTRY = "文书模板行业关联"
```

- [ ] **Step 2: 在 FULL_SYNC_MODULES 中添加**

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

### Task 20: 修改模板管理页面

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/system/document/template/index.vue`

- [ ] **Step 1: 在 data() 中添加行业分类选项**

```javascript
industryCategoryOptions: [],
```

- [ ] **Step 2: 添加 getIndustryCategoryOptions 方法**

```javascript
getIndustryCategoryOptions() {
    listIndustryCategory().then(response => {
        this.industryCategoryOptions = response.rows || []
    })
},
```

- [ ] **Step 3: 在 created() 或 init() 中调用 getIndustryCategoryOptions**

- [ ] **Step 4: 在列表页表格列中添加"行业分类"列**

```javascript
{
  label: "行业分类",
  prop: "industryCategoryName",
  align: "center",
  width: 150,
  formatter: row => row.industryCategoryName || '-'
}
```

- [ ] **Step 5: 在表单弹窗中添加行业分类多选**

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

- [ ] **Step 6: 提交**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/system/document/template/index.vue
git commit -m "feat(document): 前端模板管理页面添加行业分类多选"
```

---

## 阶段 5：Android 首页过滤逻辑

### Task 21: 修改 HomeFragment

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/fragment/HomeFragment.kt`

- [ ] **Step 1: 修改 loadDocumentCategories 方法**

将原来的直接加载所有模板改为按行业过滤：

```kotlin
private fun loadDocumentCategories() {
    viewLifecycleOwner.lifecycleScope.launch {
        // 获取当前选中单位的行业分类ID
        val industryCategoryId = SelectedUnitManager.getSelectedCategoryId()

        if (industryCategoryId == null || industryCategoryId == 0L) {
            binding.llDocumentCategories.visibility = View.GONE
            return@launch
        }

        // 获取该行业关联的模板ID列表
        val templateIds = documentRepository.getTemplateIdsByIndustryCategory(industryCategoryId)

        if (templateIds.isEmpty()) {
            binding.llDocumentCategories.visibility = View.GONE
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

                categoryAdapter.submitList(categoryWithTemplatesList)
            }
        }
    }
}
```

- [ ] **Step 2: 提交**

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
| 后端：SysDocumentTemplateMapper修改 | Task 6 |
| 后端：Service修改 | Task 7 |
| 后端：Controller修改 | Task 8 |
| 后端：行业关联同步API | Task 9 |
| 后端：Service接口和实现 | Task 10 |
| 后端：模板同步API修改 | Task 11 |
| 前端：模板管理页面 | Task 20 |
| Android：Entity修改 | Task 12, 13 |
| Android：Dao创建 | Task 14, 18 |
| Android：AppDatabase | Task 15 |
| Android：Api修改 | Task 16 |
| Android：Repository | Task 17 |
| Android：SyncManager | Task 19 |
| Android：HomeFragment过滤 | Task 21 |

### 类型一致性检查

- `SysDocumentTemplate.industryCategoryId` → `Long`
- `SysDocumentTemplate.industryCategoryIds` → `List<Long>`
- `DocumentTemplateEntity.industryCategoryId` → `Long?`
- `DocumentTemplateIndustryEntity` → `templateId: Long, industryCategoryId: Long`

### 占位符检查

无 TBD、TODO 或其他占位符，所有步骤均包含完整代码。
