# 法律类型多对多绑定实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将 law 与 law_type 从一对多改为多对多关系，支持一部法律关联多个类型

**Architecture:** 创建独立中间表 `law_type_bind`，在法律管理页面通过多选框维护关联关系

**Tech Stack:** Java/Spring Boot, MyBatis, Vue 2.6, Element UI, Kotlin/Room

---

## 文件结构

### 后端新增

| 文件 | 职责 |
|-----|------|
| `ruoyi-system/.../domain/LawTypeBind.java` | 关联实体 |
| `ruoyi-system/.../mapper/LawTypeBindMapper.java` | DAO接口 |
| `ruoyi-system/.../mapper/LawTypeBindMapper.xml` | MyBatis映射 |
| `ruoyi-system/.../service/ILawTypeBindService.java` | Service接口 |
| `ruoyi-system/.../service/impl/LawTypeBindServiceImpl.java` | Service实现 |
| `ruoyi-admin/.../AppLawTypeBindController.java` | Android端绑定接口 |

### 后端修改

| 文件 | 变更 |
|-----|------|
| `ruoyi-system/.../domain/Law.java` | 移除 typeId 字段 |
| `ruoyi-system/.../mapper/LawMapper.xml` | 移除 typeId 相关 |
| `ruoyi-system/.../mapper/LawMapper.java` | 移除 typeId 相关 |

### 前端

| 文件 | 变更 |
|-----|------|
| `ruoyi-ui/.../law/index.vue` | 类型选择改为多选 |

### Android新增

| 文件 | 职责 |
|-----|------|
| `app/.../entity/LawTypeBindEntity.kt` | 中间表实体 |
| `app/.../dao/LawTypeBindDao.kt` | 中间表DAO |
| `app/.../api/LawTypeBindApi.kt` | 绑定关系API |
| `app/.../repository/LawTypeBindRepository.kt` | 绑定关系Repository |

### Android修改

| 文件 | 变更 |
|-----|------|
| `app/.../entity/LawEntity.kt` | 移除 typeId |
| `app/.../api/LawApi.kt` | LawDto 移除 typeId |
| `app/.../repository/LawRepository.kt` | toEntity 移除 typeId |
| `app/.../database/AppDatabase.kt` | 注册新实体，版本升级 |
| `app/.../sync/SyncManager.kt` | 添加 MODULE_LAW_TYPE_BIND |

---

## Task 1: 后端 - 创建数据库迁移脚本

**Files:**
- Create: `RuoYi-Vue/sql/V1.2.4__law_type_bind.sql`

- [ ] **Step 1: 创建 SQL 迁移脚本**

```sql
-- ============================================
-- 脚本：V1.2.4__law_type_bind.sql
-- 版本：1.2.4
-- 日期：2026-04-30
-- 描述：法律类型多对多关联表
-- ============================================

-- 1. 创建 law_type_bind 中间表
CREATE TABLE `law_type_bind` (
  `law_id` BIGINT NOT NULL COMMENT '法律ID',
  `type_id` BIGINT NOT NULL COMMENT '类型ID',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  PRIMARY KEY (`law_id`, `type_id`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8MB4 COMMENT='法律类型关联表';

-- 2. 迁移现有 law.type_id 数据到中间表
INSERT INTO `law_type_bind` (`law_id`, `type_id`, `create_by`, `create_time`)
SELECT `id`, `type_id`, 'system', NOW()
FROM `law`
WHERE `type_id` IS NOT NULL;

-- 3. 删除 law 表的 type_id 字段
ALTER TABLE `law` DROP COLUMN `type_id`;
```

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/sql/V1.2.4__law_type_bind.sql
git commit -m "feat(sql): 添加法律类型多对多关联表"
```

---

## Task 2: 后端 - 创建 LawTypeBind 实体

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/LawTypeBind.java`

- [ ] **Step 1: 创建 LawTypeBind.java**

```java
package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 法律类型关联对象 law_type_bind
 *
 * @author ruoyi
 * @date 2026-04-30
 */
public class LawTypeBind extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 法律ID */
    private Long lawId;

    /** 类型ID */
    private Long typeId;

    public void setLawId(Long lawId)
    {
        this.lawId = lawId;
    }

    public Long getLawId()
    {
        return lawId;
    }

    public void setTypeId(Long typeId)
    {
        this.typeId = typeId;
    }

    public Long getTypeId()
    {
        return typeId;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
            .append("lawId", getLawId())
            .append("typeId", getTypeId())
            .toString();
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/LawTypeBind.java
git commit -m "feat(domain): 创建 LawTypeBind 实体"
```

---

## Task 3: 后端 - 创建 LawTypeBindMapper

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/LawTypeBindMapper.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/LawTypeBindMapper.xml`

- [ ] **Step 1: 创建 LawTypeBindMapper.java**

```java
package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.LawTypeBind;

/**
 * 法律类型绑定Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-30
 */
public interface LawTypeBindMapper
{
    /**
     * 查询某法律关联的所有类型
     *
     * @param lawId 法律ID
     * @return 绑定列表
     */
    public List<LawTypeBind> selectByLawId(Long lawId);

    /**
     * 查询某类型关联的所有法律
     *
     * @param typeId 类型ID
     * @return 绑定列表
     */
    public List<LawTypeBind> selectByTypeId(Long typeId);

    /**
     * 批量插入绑定关系
     *
     * @param binds 绑定列表
     * @return 结果
     */
    public int insertBatch(List<LawTypeBind> binds);

    /**
     * 删除某法律的全部绑定
     *
     * @param lawId 法律ID
     * @return 结果
     */
    public int deleteByLawId(Long lawId);

    /**
     * 删除单个绑定
     *
     * @param lawId 法律ID
     * @param typeId 类型ID
     * @return 结果
     */
    public int deleteByLawIdAndTypeId(Long lawId, Long typeId);

    /**
     * 查询所有绑定列表
     *
     * @return 绑定列表
     */
    public List<LawTypeBind> selectAll();
}
```

- [ ] **Step 2: 创建 LawTypeBindMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ruoyi.system.mapper.LawTypeBindMapper">

    <resultMap type="LawTypeBind" id="LawTypeBindResult">
        <result property="lawId"    column="law_id"    />
        <result property="typeId"    column="type_id"    />
        <result property="createBy"    column="create_by"    />
        <result property="createTime"    column="create_time"    />
    </resultMap>

    <sql id="selectLawTypeBindVo">
        select law_id, type_id, create_by, create_time from law_type_bind
    </sql>

    <select id="selectByLawId" parameterType="Long" resultMap="LawTypeBindResult">
        <include refid="selectLawTypeBindVo"/>
        where law_id = #{lawId}
    </select>

    <select id="selectByTypeId" parameterType="Long" resultMap="LawTypeBindResult">
        <include refid="selectLawTypeBindVo"/>
        where type_id = #{typeId}
    </select>

    <select id="selectAll" resultMap="LawTypeBindResult">
        <include refid="selectLawTypeBindVo"/>
    </select>

    <insert id="insertBatch" parameterType="java.util.List">
        insert into law_type_bind (law_id, type_id, create_by, create_time) values
        <foreach item="item" collection="list" separator=",">
            (#{item.lawId}, #{item.typeId}, #{item.createBy}, #{item.createTime})
        </foreach>
    </insert>

    <delete id="deleteByLawId" parameterType="Long">
        delete from law_type_bind where law_id = #{lawId}
    </delete>

    <delete id="deleteByLawIdAndTypeId">
        delete from law_type_bind where law_id = #{lawId} and type_id = #{typeId}
    </delete>
</mapper>
```

- [ ] **Step 3: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/LawTypeBindMapper.java
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/LawTypeBindMapper.xml
git commit -m "feat(mapper): 创建 LawTypeBindMapper"
```

---

## Task 4: 后端 - 创建 ILawTypeBindService 和实现

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ILawTypeBindService.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/LawTypeBindServiceImpl.java`

- [ ] **Step 1: 创建 ILawTypeBindService.java**

```java
package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.LawTypeBind;

/**
 * 法律类型绑定Service接口
 *
 * @author ruoyi
 * @date 2026-04-30
 */
public interface ILawTypeBindService
{
    /**
     * 查询某法律关联的所有类型
     *
     * @param lawId 法律ID
     * @return 绑定列表
     */
    public List<LawTypeBind> selectByLawId(Long lawId);

    /**
     * 查询某类型关联的所有法律
     *
     * @param typeId 类型ID
     * @return 绑定列表
     */
    public List<LawTypeBind> selectByTypeId(Long typeId);

    /**
     * 批量插入绑定关系
     *
     * @param binds 绑定列表
     * @return 结果
     */
    public int insertBatch(List<LawTypeBind> binds);

    /**
     * 删除某法律的全部绑定
     *
     * @param lawId 法律ID
     * @return 结果
     */
    public int deleteByLawId(Long lawId);

    /**
     * 删除单个绑定
     *
     * @param lawId 法律ID
     * @param typeId 类型ID
     * @return 结果
     */
    public int deleteByLawIdAndTypeId(Long lawId, Long typeId);

    /**
     * 查询所有绑定列表
     *
     * @return 绑定列表
     */
    public List<LawTypeBind> selectAll();
}
```

- [ ] **Step 2: 创建 LawTypeBindServiceImpl.java**

```java
package com.ruoyi.system.service.impl;

import java.util.List;
import com.ruoyi.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.ruoyi.system.mapper.LawTypeBindMapper;
import com.ruoyi.system.domain.LawTypeBind;
import com.ruoyi.system.service.ILawTypeBindService;

/**
 * 法律类型绑定Service实现
 *
 * @author ruoyi
 * @date 2026-04-30
 */
@Service
public class LawTypeBindServiceImpl implements ILawTypeBindService
{
    @Autowired
    private LawTypeBindMapper lawTypeBindMapper;

    @Override
    public List<LawTypeBind> selectByLawId(Long lawId)
    {
        return lawTypeBindMapper.selectByLawId(lawId);
    }

    @Override
    public List<LawTypeBind> selectByTypeId(Long typeId)
    {
        return lawTypeBindMapper.selectByTypeId(typeId);
    }

    @Override
    public int insertBatch(List<LawTypeBind> binds)
    {
        if (binds == null || binds.isEmpty()) {
            return 0;
        }
        for (LawTypeBind bind : binds) {
            bind.setCreateTime(DateUtils.getNowDate());
        }
        return lawTypeBindMapper.insertBatch(binds);
    }

    @Override
    public int deleteByLawId(Long lawId)
    {
        return lawTypeBindMapper.deleteByLawId(lawId);
    }

    @Override
    public int deleteByLawIdAndTypeId(Long lawId, Long typeId)
    {
        return lawTypeBindMapper.deleteByLawIdAndTypeId(lawId, typeId);
    }

    @Override
    public List<LawTypeBind> selectAll()
    {
        return lawTypeBindMapper.selectAll();
    }
}
```

- [ ] **Step 3: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ILawTypeBindService.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/LawTypeBindServiceImpl.java
git commit -m "feat(service): 创建 LawTypeBindService"
```

---

## Task 5: 后端 - 创建 AppLawTypeBindController

**Files:**
- Create: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppLawTypeBindController.java`

- [ ] **Step 1: 创建 AppLawTypeBindController.java**

```java
package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.LawTypeBind;
import com.ruoyi.system.service.ILawTypeBindService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Android 端法律类型绑定接口（匿名可访问）
 */
@RestController
@RequestMapping("/app/lawtype/bind")
public class AppLawTypeBindController extends BaseController {

    @Autowired
    private ILawTypeBindService lawTypeBindService;

    /**
     * 获取所有绑定关系
     */
    @Anonymous
    @GetMapping("/list")
    public AjaxResult list() {
        List<LawTypeBind> list = lawTypeBindService.selectAll();
        return AjaxResult.success(list);
    }

    /**
     * 查询某法律关联的类型列表
     */
    @Anonymous
    @GetMapping("/{lawId}")
    public AjaxResult getByLawId(@PathVariable Long lawId) {
        List<LawTypeBind> list = lawTypeBindService.selectByLawId(lawId);
        return AjaxResult.success(list);
    }

    /**
     * 绑定（可批量）
     */
    @Anonymous
    @PostMapping("/{lawId}")
    public AjaxResult bind(@PathVariable Long lawId, @RequestBody List<Long> typeIds) {
        if (typeIds == null || typeIds.isEmpty()) {
            return AjaxResult.error("typeIds不能为空");
        }
        // 先删除旧绑定
        lawTypeBindService.deleteByLawId(lawId);
        // 批量插入新绑定
        List<LawTypeBind> binds = typeIds.stream().map(typeId -> {
            LawTypeBind bind = new LawTypeBind();
            bind.setLawId(lawId);
            bind.setTypeId(typeId);
            return bind;
        }).toList();
        lawTypeBindService.insertBatch(binds);
        return AjaxResult.success();
    }

    /**
     * 删除某法律的全部绑定
     */
    @Anonymous
    @DeleteMapping("/{lawId}")
    public AjaxResult deleteByLawId(@PathVariable Long lawId) {
        lawTypeBindService.deleteByLawId(lawId);
        return AjaxResult.success();
    }

    /**
     * 删除单个绑定
     */
    @Anonymous
    @DeleteMapping("/{lawId}/{typeId}")
    public AjaxResult delete(@PathVariable Long lawId, @PathVariable Long typeId) {
        lawTypeBindService.deleteByLawIdAndTypeId(lawId, typeId);
        return AjaxResult.success();
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/AppLawTypeBindController.java
git commit -m "feat(android-api): 创建 AppLawTypeBindController"
```

---

## Task 6: 后端 - 修改 Law.java 移除 typeId

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/Law.java`

- [ ] **Step 1: 修改 Law.java**

删除以下内容：
1. `private Long typeId;` 字段声明
2. `setTypeId(Long typeId)` 方法
3. `getTypeId()` 方法
4. `toString()` 中的 `.append("typeId", getTypeId())`
5. `@Excel(name = "类型ID")` 注解

保留：
- `id`, `name`, `releaseTime` 字段及相关方法

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/Law.java
git commit -m "refactor(domain): Law 移除 typeId 字段"
```

---

## Task 7: 后端 - 修改 LawMapper 移除 typeId

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/LawMapper.xml`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/LawMapper.java`

- [ ] **Step 1: 修改 LawMapper.xml**

在 `LawResult` resultMap 中删除：
```xml
<result property="typeId"    column="type_id"    />
```

在 `selectLawVo` 中删除：
```sql
type_id,
```

在 `selectLawList` 的 where 条件中删除：
```xml
<if test="typeId != null"> and type_id = #{typeId}</if>
```

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/LawMapper.xml
git commit -m "refactor(mapper): LawMapper 移除 typeId"
```

---

## Task 8: 前端 - 修改 law/index.vue 类型多选

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/system/law/index.vue`

- [ ] **Step 1: 修改法律表单的类型选择器**

将单选改为多选：

```vue
<!-- 原来的单选 -->
<el-select v-model="lawForm.typeId" placeholder="请选择类型">

<!-- 改为多选 -->
<el-select v-model="lawForm.typeIds" multiple placeholder="请选择类型" collapse-tags>
```

- [ ] **Step 2: 修改 data 中的 form.typeId 改为 typeIds**

```javascript
// 原来的
lawForm: {
  id: null,
  name: null,
  typeId: null,
  releaseTime: null
}

// 改为
lawForm: {
  id: null,
  name: null,
  typeIds: [],
  releaseTime: null
}
```

- [ ] **Step 3: 修改提交方法**

```javascript
submitLawForm() {
  this.$refs["lawForm"].validate(valid => {
    if (valid) {
      const lawData = { ...this.lawForm }
      const typeIds = lawData.typeIds
      delete lawData.typeIds

      if (this.lawForm.id) {
        updateLaw(lawData).then(response => {
          // 调用绑定接口
          bindLawType(this.lawForm.id, typeIds).then(() => {
            this.$modal.msgSuccess("修改成功")
            this.lawOpen = false
            this.getList()
          })
        })
      } else {
        addLaw(lawData).then(response => {
          const newId = response.data.id
          // 调用绑定接口
          bindLawType(newId, typeIds).then(() => {
            this.$modal.msgSuccess("新增成功")
            this.lawOpen = false
            this.getList()
          })
        })
      }
    }
  })
}
```

- [ ] **Step 4: 导入绑定API**

```javascript
import { listLaw, getLaw, addLaw, updateLaw, delLaw } from "@/api/system/law"
import { treeList } from "@/api/system/lawtype"
import { bindLawType } from "@/api/system/lawbind"
```

- [ ] **Step 5: 提交**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/system/law/index.vue
git commit -m "feat(frontend): law 类型选择改为多选"
```

---

## Task 9: 前端 - 创建 lawbind.js API

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/api/system/lawbind.js`

- [ ] **Step 1: 创建 lawbind.js**

```javascript
import request from '@/utils/request'

// 绑定法律类型（可批量）
export function bindLawType(lawId, typeIds) {
  return request({
    url: '/app/lawtype/bind/' + lawId,
    method: 'post',
    data: typeIds
  })
}

// 获取某法律的类型绑定列表
export function getLawTypeBind(lawId) {
  return request({
    url: '/app/lawtype/bind/' + lawId,
    method: 'get'
  })
}

// 删除某法律的全部绑定
export function deleteLawTypeBind(lawId) {
  return request({
    url: '/app/lawtype/bind/' + lawId,
    method: 'delete'
  })
}
```

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/ruoyi-ui/src/api/system/lawbind.js
git commit -m "feat(api): 创建 lawbind.js"
```

---

## Task 10: Android - 创建 LawTypeBindEntity

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/LawTypeBindEntity.kt`

- [ ] **Step 1: 创建 LawTypeBindEntity.kt**

```kotlin
package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 法律类型绑定实体
 * 对应数据库表：law_type_bind
 */
@Entity(
    tableName = "law_type_bind",
    primaryKeys = ["lawId", "typeId"]
)
data class LawTypeBindEntity(
    val lawId: Long,          // 法律ID
    val typeId: Long,         // 类型ID
    val createTime: Long?     // 创建时间
)
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/LawTypeBindEntity.kt
git commit -m "feat(android): 创建 LawTypeBindEntity"
```

---

## Task 11: Android - 创建 LawTypeBindDao

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/LawTypeBindDao.kt`

- [ ] **Step 1: 创建 LawTypeBindDao.kt**

```kotlin
package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.LawTypeBindEntity

@Dao
interface LawTypeBindDao {
    @Query("SELECT * FROM law_type_bind")
    suspend fun getAll(): List<LawTypeBindEntity>

    @Query("SELECT * FROM law_type_bind WHERE lawId = :lawId")
    suspend fun getByLawId(lawId: Long): List<LawTypeBindEntity>

    @Query("SELECT * FROM law_type_bind WHERE typeId = :typeId")
    suspend fun getByTypeId(typeId: Long): List<LawTypeBindEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(binds: List<LawTypeBindEntity>)

    @Query("DELETE FROM law_type_bind")
    suspend fun deleteAll()

    @Query("DELETE FROM law_type_bind WHERE lawId = :lawId")
    suspend fun deleteByLawId(lawId: Long)

    @Query("DELETE FROM law_type_bind WHERE lawId = :lawId AND typeId = :typeId")
    suspend fun deleteByLawIdAndTypeId(lawId: Long, typeId: Long)
}
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/LawTypeBindDao.kt
git commit -m "feat(android): 创建 LawTypeBindDao"
```

---

## Task 12: Android - 创建 LawTypeBindApi

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/api/LawTypeBindApi.kt`

- [ ] **Step 1: 创建 LawTypeBindApi.kt**

```kotlin
package com.ruoyi.app.feature.law.api

import com.drake.net.Get
import com.drake.net.Post
import com.drake.net.Delete
import com.ruoyi.app.api.ConfigApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable

object LawTypeBindApi {

    suspend fun getAllBinds(): LawTypeBindListResponse = withContext(Dispatchers.IO) {
        Get<LawTypeBindListResponse>("${ConfigApi.baseUrl}/app/lawtype/bind/list").await()
    }

    suspend fun getBindsByLawId(lawId: Long): LawTypeBindListResponse = withContext(Dispatchers.IO) {
        Get<LawTypeBindListResponse>("${ConfigApi.baseUrl}/app/lawtype/bind/$lawId").await()
    }

    suspend fun bind(lawId: Long, typeIds: List<Long>): BaseResponse = withContext(Dispatchers.IO) {
        Post<BaseResponse>("${ConfigApi.baseUrl}/app/lawtype/bind/$lawId") {
            setBody(typeIds)
        }.await()
    }

    suspend fun deleteByLawId(lawId: Long): BaseResponse = withContext(Dispatchers.IO) {
        Delete<BaseResponse>("${ConfigApi.baseUrl}/app/lawtype/bind/$lawId").await()
    }

    suspend fun deleteBind(lawId: Long, typeId: Long): BaseResponse = withContext(Dispatchers.IO) {
        Delete<BaseResponse>("${ConfigApi.baseUrl}/app/lawtype/bind/$lawId/$typeId").await()
    }
}

@Serializable
data class LawTypeBindListResponse(
    val code: Int,
    val msg: String,
    val data: List<LawTypeBindDto> = emptyList()
)

@Serializable
data class LawTypeBindDto(
    val lawId: Long,
    val typeId: Long
)

@Serializable
data class BaseResponse(
    val code: Int,
    val msg: String
)
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/api/LawTypeBindApi.kt
git commit -m "feat(android): 创建 LawTypeBindApi"
```

---

## Task 13: Android - 创建 LawTypeBindRepository

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawTypeBindRepository.kt`

- [ ] **Step 1: 创建 LawTypeBindRepository.kt**

```kotlin
package com.ruoyi.app.feature.law.repository

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.LawTypeBindEntity
import com.ruoyi.app.feature.law.api.LawTypeBindApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LawTypeBindRepository(private val context: Context) {

    private val lawTypeBindDao = AppDatabase.getInstance(context).lawTypeBindDao()

    /**
     * 同步所有绑定关系
     */
    suspend fun syncLawTypeBinds(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = LawTypeBindApi.getAllBinds()
                if (response.code == 200) {
                    // 清空本地中间表，再插入服务端数据
                    lawTypeBindDao.deleteAll()
                    val entities = response.data.map { dto ->
                        LawTypeBindEntity(
                            lawId = dto.lawId,
                            typeId = dto.typeId,
                            createTime = null
                        )
                    }
                    lawTypeBindDao.insertAll(entities)
                    Result.success(Unit)
                } else {
                    Result.failure(Exception(response.msg))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    /**
     * 获取某法律的类型ID列表
     */
    suspend fun getTypeIdsByLawId(lawId: Long): List<Long> {
        return withContext(Dispatchers.IO) {
            lawTypeBindDao.getByLawId(lawId).map { it.typeId }
        }
    }

    /**
     * 获取本地所有绑定
     */
    suspend fun getAll(): List<LawTypeBindEntity> {
        return withContext(Dispatchers.IO) {
            lawTypeBindDao.getAll()
        }
    }
}
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawTypeBindRepository.kt
git commit -m "feat(android): 创建 LawTypeBindRepository"
```

---

## Task 14: Android - 修改 LawEntity 移除 typeId

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/LawEntity.kt`

- [ ] **Step 1: 修改 LawEntity.kt**

```kotlin
@Entity(
    tableName = "law",
    indices = [Index(value = ["name"])]
)
data class LawEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val releaseTime: Long?
    // 移除 typeId 字段
)
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/LawEntity.kt
git commit -m "refactor(android): LawEntity 移除 typeId"
```

---

## Task 15: Android - 修改 LawApi 移除 typeId

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/api/LawApi.kt`

- [ ] **Step 1: 修改 LawDto**

```kotlin
@Serializable
data class LawDto(
    val id: Long,
    val name: String
    // 移除 typeId
)
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/api/LawApi.kt
git commit -m "refactor(android): LawDto 移除 typeId"
```

---

## Task 16: Android - 修改 LawRepository 移除 typeId

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawRepository.kt`

- [ ] **Step 1: 修改 toEntity 扩展函数**

```kotlin
fun LawDto.toEntity() = LawEntity(
    id = id,
    name = name,
    releaseTime = null
    // 移除 typeId
)
```

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawRepository.kt
git commit -m "refactor(android): LawRepository toEntity 移除 typeId"
```

---

## Task 17: Android - 修改 AppDatabase 注册新实体

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt`

- [ ] **Step 1: 添加 LawTypeBindDao 抽象方法**

```kotlin
abstract fun lawTypeBindDao(): LawTypeBindDao
```

- [ ] **Step 2: 在 entities 列表中添加 LawTypeBindEntity::class**

- [ ] **Step 3: 版本升级**（当前是17，升级到18）

```kotlin
@Database(
    entities = [
        // ... existing entities ...
        LawTypeBindEntity::class
    ],
    version = 18
)
```

- [ ] **Step 4: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt
git commit -m "feat(android): AppDatabase 注册 LawTypeBindEntity，版本升级到 18"
```

---

## Task 18: Android - 修改 SyncManager 添加 MODULE_LAW_TYPE_BIND

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/SyncManager.kt`

- [ ] **Step 1: 添加模块常量**

```kotlin
const val MODULE_LAW_TYPE_BIND = "法律类型绑定"
```

- [ ] **Step 2: 添加到 FULL_SYNC_MODULES 列表**

- [ ] **Step 3: 添加 syncModule 中的处理**

```kotlin
MODULE_LAW_TYPE_BIND -> syncLawTypeBind(context)
```

- [ ] **Step 4: 添加 syncLawTypeBind 方法**

```kotlin
private suspend fun syncLawTypeBind(context: Context?): Boolean {
    if (context == null) return false
    return try {
        val repository = LawTypeBindRepository(context)
        val result = repository.syncLawTypeBinds()
        if (result.isFailure) {
            Log.e("SyncManager", "法律类型绑定同步失败: ${result.exceptionOrNull()?.message}", result.exceptionOrNull())
        }
        result.isSuccess
    } catch (e: Exception) {
        Log.e("SyncManager", "法律类型绑定同步异常: ${e.message}", e)
        false
    }
}
```

- [ ] **Step 5: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/SyncManager.kt
git commit -m "feat(android): SyncManager 添加 MODULE_LAW_TYPE_BIND"
```

---

## Task 19: 编译验证

- [ ] **Step 1: 后端编译**

```bash
cd RuoYi-Vue && mvn clean compile -pl ruoyi-admin -am -DskipTests
```

- [ ] **Step 2: 前端编译**

```bash
cd ruoyi-ui && npm run dev
```

- [ ] **Step 3: Android编译**

```bash
cd Ruoyi-Android-App && ./gradlew assembleDebug
```

- [ ] **Step 4: 提交**

```bash
git add -A && git commit -m "test: 验证法律类型多对多绑定功能"
```

---

## Spec 覆盖率检查

| 设计文档需求 | 对应任务 |
|-------------|---------|
| 数据库中间表 | Task 1 |
| LawTypeBind实体 | Task 2 |
| LawTypeBindMapper | Task 3 |
| LawTypeBindService | Task 4 |
| AppLawTypeBindController | Task 5 |
| Law移除typeId | Task 6-7 |
| 前端类型多选 | Task 8-9 |
| Android Entity/Dao/Api/Repository | Task 10-13 |
| Android Law移除typeId | Task 14-16 |
| AppDatabase注册 | Task 17 |
| SyncManager模块 | Task 18 |

---

**Plan complete and saved to `docs/superpowers/plans/2026-04-30-law-type-bind-implementation.md`**

**Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**
