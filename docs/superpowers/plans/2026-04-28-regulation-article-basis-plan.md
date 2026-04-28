# 法律法规章节关联依据功能 实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 在法规详情页展示目录→章节→条目结构，条目关联定性依据和处理依据，点击可跳转查看详情，详情页字段可复制。

**Architecture:** 后端新增处理依据表和关联表，前端新增管理页面，Android端新增列表页/详情页和数据同步。

**Tech Stack:** Spring Boot (后端) + Vue (前端) + Kotlin/Android (移动端)

---

## 第一阶段：后端实现

### Task 1: 创建数据库表SQL脚本

**Files:**
- Create: `RuoYi-Vue/sql/V1.2.3__processing_basis_tables.sql`

- [ ] **Step 1: 创建SQL脚本**

```sql
-- ============================================
-- 脚本：V1.2.3__processing_basis_tables.sql
-- 版本：1.2.3
-- 日期：2026-04-28
-- 描述：创建处理依据表和依据章节关联表
-- ============================================

-- 1. 处理依据表（与定性依据表结构一致）
CREATE TABLE IF NOT EXISTS `sys_processing_basis` (
    `basis_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '处理依据ID',
    `basis_no` varchar(20) DEFAULT NULL COMMENT '编号',
    `title` varchar(255) NOT NULL COMMENT '标题',
    `violation_type` varchar(100) DEFAULT NULL COMMENT '违法类型',
    `issuing_authority` varchar(100) DEFAULT NULL COMMENT '颁发机构',
    `effective_date` varchar(20) DEFAULT NULL COMMENT '实施时间',
    `legal_level` varchar(20) DEFAULT NULL COMMENT '法律层级',
    `clauses` text COMMENT '条款内容（JSON数组）',
    `legal_liability` text COMMENT '法律责任',
    `discretion_standard` text COMMENT '裁量标准',
    `regulation_id` bigint(20) DEFAULT NULL COMMENT '关联法规ID',
    `status` char(1) DEFAULT '0' COMMENT '状态：0正常 1停用',
    `del_flag` char(1) DEFAULT '0' COMMENT '删除标志',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    `remark` varchar(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`basis_id`),
    KEY `idx_regulation_id` (`regulation_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处理依据表';

-- 2. 依据章节关联表
CREATE TABLE IF NOT EXISTS `sys_basis_chapter_link` (
    `link_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '关联ID',
    `basis_type` varchar(20) NOT NULL COMMENT '关联类型：legal/processing',
    `basis_id` bigint(20) NOT NULL COMMENT '依据ID',
    `chapter_id` bigint(20) NOT NULL COMMENT '章节ID',
    `article_id` bigint(20) DEFAULT NULL COMMENT '条款ID',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    PRIMARY KEY (`link_id`),
    UNIQUE KEY `uk_basis_chapter_article` (`basis_type`, `basis_id`, `chapter_id`, `article_id`),
    KEY `idx_chapter` (`chapter_id`),
    KEY `idx_article` (`article_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='依据章节关联表';

-- 3. 菜单数据
-- 处理依据管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '处理依据管理', menu_id, 6, 'processingBasis', 'system/processingBasis/index', 'C', '0', '0', 'system:processingBasis:list', 'form', 'admin', sysdate()
FROM sys_menu WHERE menu_name = '法律法规' AND del_flag = '0';

-- 依据关联管理
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, menu_type, visible, status, perms, icon, create_by, create_time)
SELECT '依据关联管理', menu_id, 7, 'basisLink', 'system/basisLink/index', 'C', '0', '0', 'system:basisLink:list', 'link', 'admin', sysdate()
FROM sys_menu WHERE menu_name = '法律法规' AND del_flag = '0';

-- 4. 权限数据
INSERT INTO sys_post (post_code, post_name, post_sort, status, create_by, create_time) VALUES
('processingBasis:add', '处理依据新增', 1, '0', 'admin', sysdate()),
('processingBasis:edit', '处理依据修改', 2, '0', 'admin', sysdate()),
('processingBasis:remove', '处理依据删除', 3, '0', 'admin', sysdate()),
('processingBasis:list', '处理依据查询', 4, '0', 'admin', sysdate()),
('basisLink:add', '关联新增', 5, '0', 'admin', sysdate()),
('basisLink:remove', '关联删除', 6, '0', 'admin', sysdate()),
('basisLink:list', '关联查询', 7, '0', 'admin', sysdate());
```

- [ ] **Step 2: 提交SQL脚本**

```bash
git add RuoYi-Vue/sql/V1.2.3__processing_basis_tables.sql
git commit -m "feat(system): 添加处理依据表和依据章节关联表"
```

---

### Task 2: 后端 - 处理依据Domain/Mapper/Service/Controller

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysProcessingBasis.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysProcessingBasisMapper.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysProcessingBasisService.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysProcessingBasisServiceImpl.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/controller/SysProcessingBasisController.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysProcessingBasisMapper.xml`

- [ ] **Step 1: 创建 SysProcessingBasis.java**（参考 SysLegalBasis.java 结构）

```java
package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 处理依据对象 sys_processing_basis
 */
public class SysProcessingBasis extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long basisId;
    @Excel(name = "编号")
    private String basisNo;
    @Excel(name = "标题")
    private String title;
    @Excel(name = "违法类型")
    private String violationType;
    @Excel(name = "颁发机构")
    private String issuingAuthority;
    @Excel(name = "实施时间")
    private String effectiveDate;
    @Excel(name = "法律层级")
    private String legalLevel;
    private String clauses;
    private String legalLiability;
    private String discretionStandard;
    private Long regulationId;
    @Excel(name = "状态")
    private String status;
    private String delFlag;

    // getters and setters...
    // toString() method...
}
```

- [ ] **Step 2: 创建 SysProcessingBasisMapper.java**

```java
package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysProcessingBasis;

public interface SysProcessingBasisMapper {
    List<SysProcessingBasis> selectSysProcessingBasisList(SysProcessingBasis sysProcessingBasis);
    SysProcessingBasis selectSysProcessingBasisById(Long basisId);
    int insertSysProcessingBasis(SysProcessingBasis sysProcessingBasis);
    int updateSysProcessingBasis(SysProcessingBasis sysProcessingBasis);
    int deleteSysProcessingBasisByIds(Long[] basisIds);
    int deleteSysProcessingBasisById(Long basisId);
}
```

- [ ] **Step 3: 创建 SysProcessingBasisMapper.xml**（参考 SysLegalBasisMapper.xml）

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ruoyi.system.mapper.SysProcessingBasisMapper">
    <resultMap type="SysProcessingBasis" id="SysProcessingBasisResult">
        <result property="basisId" column="basis_id"/>
        <!-- 其他字段映射 -->
    </resultMap>

    <sql id="selectProcessingBasisVo">
        select basis_id, basis_no, title, violation_type, issuing_authority,
               effective_date, legal_level, clauses, legal_liability,
               discretion_standard, regulation_id, status, del_flag,
               create_by, create_time, update_by, update_time, remark
        from sys_processing_basis
    </sql>

    <select id="selectSysProcessingBasisList" resultMap="SysProcessingBasisResult">
        <include refid="selectProcessingBasisVo"/>
        <where>
            <if test="title != null and title != ''">and title like concat('%', #{title}, '%')</if>
            <if test="violationType != null">and violation_type = #{violationType}</if>
            <if test="basisNo != null">and basis_no = #{basisNo}</if>
            <if test="regulationId != null">and regulation_id = #{regulationId}</if>
            <if test="status != null">and status = #{status}</if>
            <if test="delFlag != null">and del_flag = #{delFlag}</if>
        </where>
        order by create_time desc
    </select>

    <!-- 其他CRUD方法... -->
</mapper>
```

- [ ] **Step 4: 创建 ISysProcessingBasisService.java**

```java
package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysProcessingBasis;

public interface ISysProcessingBasisService {
    SysProcessingBasis selectSysProcessingBasisById(Long basisId);
    List<SysProcessingBasis> selectSysProcessingBasisList(SysProcessingBasis sysProcessingBasis);
    int insertSysProcessingBasis(SysProcessingBasis sysProcessingBasis);
    int updateSysProcessingBasis(SysProcessingBasis sysProcessingBasis);
    int deleteSysProcessingBasisByIds(Long[] basisIds);
    int deleteSysProcessingBasisById(Long basisId);
}
```

- [ ] **Step 5: 创建 SysProcessingBasisServiceImpl.java**（实现ISysProcessingBasisService）

- [ ] **Step 6: 创建 SysProcessingBasisController.java**

```java
package com.ruoyi.system.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysProcessingBasis;
import com.ruoyi.system.service.ISysProcessingBasisService;

@RestController
@RequestMapping("/system/processingBasis")
public class SysProcessingBasisController extends BaseController {
    @Autowired
    private ISysProcessingBasisService processingBasisService;

    @PreAuthorize("@ss.hasPermi('system:processingBasis:list')")
    @GetMapping("/list")
    public AjaxResult list(SysProcessingBasis sysProcessingBasis) {
        List<SysProcessingBasis> list = processingBasisService.selectSysProcessingBasisList(sysProcessingBasis);
        return AjaxResult.success(list);
    }

    @PreAuthorize("@ss.hasPermi('system:processingBasis:query')")
    @GetMapping("/{basisId}")
    public AjaxResult getInfo(@PathVariable Long basisId) {
        return AjaxResult.success(processingBasisService.selectSysProcessingBasisById(basisId));
    }

    @PreAuthorize("@ss.hasPermi('system:processingBasis:add')")
    @PostMapping
    public AjaxResult add(@RequestBody SysProcessingBasis sysProcessingBasis) {
        return AjaxResult.success(processingBasisService.insertSysProcessingBasis(sysProcessingBasis));
    }

    @PreAuthorize("@ss.hasPermi('system:processingBasis:edit')")
    @PutMapping
    public AjaxResult edit(@RequestBody SysProcessingBasis sysProcessingBasis) {
        return AjaxResult.success(processingBasisService.updateSysProcessingBasis(sysProcessingBasis));
    }

    @PreAuthorize("@ss.hasPermi('system:processingBasis:remove')")
    @DeleteMapping("/{basisIds}")
    public AjaxResult remove(@PathVariable Long[] basisIds) {
        return AjaxResult.success(processingBasisService.deleteSysProcessingBasisByIds(basisIds));
    }
}
```

- [ ] **Step 7: 提交后端处理依据代码**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysProcessingBasisMapper.xml
git commit -m "feat(system): 添加处理依据CRUD接口"
```

---

### Task 3: 后端 - 依据关联管理 Domain/Mapper/Service/Controller

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysBasisChapterLink.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysBasisChapterLinkMapper.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysBasisChapterLinkService.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysBasisChapterLinkServiceImpl.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/controller/SysBasisChapterLinkController.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysBasisChapterLinkMapper.xml`

- [ ] **Step 1: 创建 SysBasisChapterLink.java**

```java
package com.ruoyi.system.domain;

import com.ruoyi.common.core.domain.BaseEntity;

public class SysBasisChapterLink extends BaseEntity {
    private Long linkId;
    private String basisType;  // 'legal' 或 'processing'
    private Long basisId;
    private Long chapterId;
    private Long articleId;

    // getters and setters...
}
```

- [ ] **Step 2: 创建 Mapper/Service/Controller**（参考处理依据结构）

- [ ] **Step 3: 创建统计接口 - 修改 SysRegulationController 或新建接口**

在 SysRegulationController 或新建 SysRegulationArticleBasisController 添加：

```java
/**
 * 获取章节下条目的关联依据统计
 */
@GetMapping("/articleBasisCount/{regulationId}")
public AjaxResult getArticleBasisCount(@PathVariable Long regulationId) {
    List<Map<String, Object>> result = regulationService.selectArticleBasisCount(regulationId);
    return AjaxResult.success(result);
}
```

在 ISysRegulationService 添加方法：

```java
List<Map<String, Object>> selectArticleBasisCount(Long regulationId);
```

- [ ] **Step 4: 提交后端关联管理代码**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/
git commit -m "feat(system): 添加依据关联管理接口"
```

---

## 第二阶段：前端实现（Vue）

### Task 4: 前端 - API接口定义

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/api/system/processingBasis.js`
- Create: `RuoYi-Vue/ruoyi-ui/src/api/system/basisLink.js`

- [ ] **Step 1: 创建 processingBasis.js**

```javascript
import request from '@/utils/request'

export function listProcessingBasis(query) {
  return request({
    url: '/system/processingBasis/list',
    method: 'get',
    params: query
  })
}

export function getProcessingBasis(basisId) {
  return request({
    url: '/system/processingBasis/' + basisId,
    method: 'get'
  })
}

export function addProcessingBasis(data) {
  return request({
    url: '/system/processingBasis',
    method: 'post',
    data: data
  })
}

export function updateProcessingBasis(data) {
  return request({
    url: '/system/processingBasis',
    method: 'put',
    data: data
  })
}

export function delProcessingBasis(basisId) {
  return request({
    url: '/system/processingBasis/' + basisId,
    method: 'delete'
  })
}
```

- [ ] **Step 2: 创建 basisLink.js**

```javascript
import request from '@/utils/request'

export function listBasisLink(query) {
  return request({
    url: '/system/basisLink/list',
    method: 'get',
    params: query
  })
}

export function addBasisLink(data) {
  return request({
    url: '/system/basisLink',
    method: 'post',
    data: data
  })
}

export function delBasisLink(linkId) {
  return request({
    url: '/system/basisLink/' + linkId,
    method: 'delete'
  })
}

export function getBasisLinkByArticle(articleId) {
  return request({
    url: '/system/basisLink/byArticle/' + articleId,
    method: 'get'
  })
}
```

- [ ] **Step 3: 提交前端API**

```bash
git add RuoYi-Vue/ruoyi-ui/src/api/system/processingBasis.js
git add RuoYi-Vue/ruoyi-ui/src/api/system/basisLink.js
git commit -m "feat(ui): 添加处理依据和关联管理API接口"
```

---

### Task 5: 前端 - 处理依据管理页面

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/views/system/processingBasis/index.vue`

- [ ] **Step 1: 创建页面**（参考 regulation/index.vue 结构）

页面结构：
- 搜索表单：编号、标题、违法类型
- 表格：编号、标题、违法类型、法律层级、状态、操作
- 新增/编辑弹窗
- 删除确认

```vue
<template>
  <div class="app-container">
    <!-- 搜索表单 -->
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="编号" prop="basisNo">
        <el-input v-model="queryParams.basisNo" placeholder="请输入编号" clearable/>
      </el-form-item>
      <el-form-item label="标题" prop="title">
        <el-input v-model="queryParams.title" placeholder="请输入标题" clearable/>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <!-- 表格和操作按钮 -->
    <el-table v-loading="loading" :data="dataList">
      <el-table-column label="编号" align="center" prop="basisNo"/>
      <el-table-column label="标题" align="center" prop="title" show-overflow-tooltip/>
      <el-table-column label="违法类型" align="center" prop="violationType"/>
      <el-table-column label="法律层级" align="center" prop="legalLevel"/>
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success">正常</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </template>
      </el-table-column>
    </el-table>

    <!-- 分页 -->
    <pagination/>

    <!-- 新增/编辑弹窗 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="编号" prop="basisNo">
          <el-input v-model="form.basisNo" placeholder="请输入编号"/>
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题"/>
        </el-form-item>
        <!-- 其他字段... -->
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>
```

- [ ] **Step 2: 提交处理依据页面**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/system/processingBasis/index.vue
git commit -m "feat(ui): 添加处理依据管理页面"
```

---

### Task 6: 前端 - 依据关联管理页面

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/views/system/basisLink/index.vue`

- [ ] **Step 1: 创建页面**

页面结构：
- 左侧树：法规 → 章节 → 条款层级选择
- 右侧Tab：定性依据列表 / 处理依据列表
- 新增关联按钮
- 删除关联操作

```vue
<template>
  <div class="app-container" style="display: flex;">
    <!-- 左侧：法规-章节-条款树 -->
    <div style="width: 300px; margin-right: 16px;">
      <el-tree
        :data="treeData"
        :props="treeProps"
        node-key="id"
        @node-click="handleNodeClick"
      />
    </div>

    <!-- 右侧：关联列表 -->
    <div style="flex: 1;">
      <el-tabs v-model="activeTab">
        <el-tab-pane label="定性依据" name="legal">
          <el-button type="primary" size="mini" @click="handleAddLink('legal')">新增关联</el-button>
          <el-table :data="legalLinks">
            <el-table-column label="编号" prop="basisNo"/>
            <el-table-column label="标题" prop="title"/>
            <el-table-column label="操作">
              <template slot-scope="scope">
                <el-button size="mini" type="text" @click="handleDelLink(scope.row)">删除</el-button>
              </template>
            </el-table-column>
          </el-table>
        </el-tab-pane>
        <el-tab-pane label="处理依据" name="processing">
          <!-- 类似结构 -->
        </el-tab-pane>
      </el-tabs>
    </div>
  </div>
</template>
```

- [ ] **Step 2: 提交关联管理页面**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/system/basisLink/index.vue
git commit -m "feat(ui): 添加依据关联管理页面"
```

---

## 第三阶段：Android端实现

### Task 7: Android - 数据层（Entity/DAO）

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/entity/ProcessingBasisEntity.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/entity/BasisChapterLinkEntity.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/dao/ProcessingBasisDao.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/dao/BasisChapterLinkDao.kt`
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt`（添加新DAO）

- [ ] **Step 1: 创建 ProcessingBasisEntity.kt**

```kotlin
package com.ruoyi.app.feature.law.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sys_processing_basis")
data class ProcessingBasisEntity(
    @PrimaryKey @ColumnInfo(name = "basis_id") val basisId: Long,
    @ColumnInfo(name = "basis_no") val basisNo: String?,
    val title: String,
    @ColumnInfo(name = "violation_type") val violationType: String?,
    @ColumnInfo(name = "issuing_authority") val issuingAuthority: String?,
    @ColumnInfo(name = "effective_date") val effectiveDate: String?,
    @ColumnInfo(name = "legal_level") val legalLevel: String?,
    val clauses: String?,
    @ColumnInfo(name = "legal_liability") val legalLiability: String?,
    @ColumnInfo(name = "discretion_standard") val discretionStandard: String?,
    @ColumnInfo(name = "regulation_id") val regulationId: Long?,
    val status: String,
    @ColumnInfo(name = "del_flag") val delFlag: String,
    val syncStatus: String = "SYNCED"
)
```

- [ ] **Step 2: 创建 BasisChapterLinkEntity.kt**

```kotlin
package com.ruoyi.app.feature.law.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sys_basis_chapter_link")
data class BasisChapterLinkEntity(
    @PrimaryKey @ColumnInfo(name = "link_id") val linkId: Long,
    @ColumnInfo(name = "basis_type") val basisType: String,  // 'legal' 或 'processing'
    @ColumnInfo(name = "basis_id") val basisId: Long,
    @ColumnInfo(name = "chapter_id") val chapterId: Long,
    @ColumnInfo(name = "article_id") val articleId: Long?,
    val syncStatus: String = "SYNCED"
)
```

- [ ] **Step 3: 创建 ProcessingBasisDao.kt**

```kotlin
package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.ProcessingBasisEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProcessingBasisDao {
    @Query("SELECT * FROM sys_processing_basis WHERE del_flag = '0' ORDER BY basis_id DESC")
    fun getAllProcessingBasises(): Flow<List<ProcessingBasisEntity>>

    @Query("SELECT * FROM sys_processing_basis WHERE basis_id = :basisId")
    suspend fun getProcessingBasisById(basisId: Long): ProcessingBasisEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessingBasis(processingBasis: ProcessingBasisEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProcessingBasises(processingBasises: List<ProcessingBasisEntity>)

    @Query("DELETE FROM sys_processing_basis")
    suspend fun deleteAll()
}
```

- [ ] **Step 4: 创建 BasisChapterLinkDao.kt**

```kotlin
package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.BasisChapterLinkEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BasisChapterLinkDao {
    @Query("SELECT * FROM sys_basis_chapter_link")
    fun getAllLinks(): Flow<List<BasisChapterLinkEntity>>

    @Query("SELECT COUNT(*) FROM sys_basis_chapter_link WHERE article_id = :articleId AND basis_type = 'legal'")
    suspend fun getLegalBasisCountByArticle(articleId: Long): Int

    @Query("SELECT COUNT(*) FROM sys_basis_chapter_link WHERE article_id = :articleId AND basis_type = 'processing'")
    suspend fun getProcessingBasisCountByArticle(articleId: Long): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLinks(links: List<BasisChapterLinkEntity>)

    @Query("DELETE FROM sys_basis_chapter_link")
    suspend fun deleteAll()
}
```

- [ ] **Step 5: 在 AppDatabase.kt 添加新DAO**

```kotlin
// 在 AppDatabase 类中添加
abstract fun processingBasisDao(): ProcessingBasisDao
abstract fun basisChapterLinkDao(): BasisChapterLinkDao
```

- [ ] **Step 6: 提交Android数据层**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/
git commit -m "feat(android): 添加处理依据和关联表实体类与DAO"
```

---

### Task 8: Android - Repository层

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawRepository.kt`

- [ ] **Step 1: 在 LawRepository.kt 添加新方法**

```kotlin
// 获取某条款关联的定性依据数量
suspend fun getLegalBasisCountByArticle(articleId: Long): Int {
    return basisChapterLinkDao.getLegalBasisCountByArticle(articleId)
}

// 获取某条款关联的处理依据数量
suspend fun getProcessingBasisCountByArticle(articleId: Long): Int {
    return basisChapterLinkDao.getProcessingBasisCountByArticle(articleId)
}

// 获取某条款关联的定性依据列表
fun getLegalBasisesByArticle(articleId: Long): Flow<List<LegalBasisEntity>> {
    return legalBasisDao.getLegalBasisesByArticle(articleId)
}

// 获取某条款关联的处理依据列表
fun getProcessingBasisesByArticle(articleId: Long): Flow<List<ProcessingBasisEntity>> {
    return processingBasisDao.getProcessingBasisesByArticle(articleId)
}
```

- [ ] **Step 2: 提交Repository层**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawRepository.kt
git commit -m "feat(android): LawRepository添加关联查询方法"
```

---

### Task 9: Android - UI层（法规详情页改造）

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/regulation/RegulationDetailActivity.kt`
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/regulation/ChapterTreeAdapter.kt`
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/regulation/ChapterTreeItem.kt`
- Modify: `Ruoyi-Android-App/app/src/main/res/layout/activity_regulation_detail.xml`

- [ ] **Step 1: 修改 ChapterTreeItem.kt - 添加 Article 类**

```kotlin
data class Article(
    val articleId: Long,
    val chapterId: Long?,
    val articleNo: String?,
    val content: String?,
    val legalBasisCount: Int = 0,
    val processingBasisCount: Int = 0
)
```

- [ ] **Step 2: 修改 activity_regulation_detail.xml - 添加目录区域**

在现有布局顶部添加：
```xml
<!-- 目录区域 -->
<LinearLayout
    android:id="@+id/layout_catalog"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:padding="12dp"
    android:background="@color/white">
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="目 录"
        android:textSize="12sp"
        android:textColor="@color/color_9"/>
    <!-- 目录列表将通过 RecyclerView 实现 -->
</LinearLayout>
```

- [ ] **Step 3: 修改 RegulationDetailActivity.kt - 加载目录和关联统计**

在 loadData() 方法中添加：
```kotlin
// 加载章节下条目的关联依据统计
lifecycleScope.launch {
    val articleBasisCounts = repository.getArticleBasisCount(regulationId)
    // 更新 Article 对象的 legalBasisCount 和 processingBasisCount
}
```

- [ ] **Step 4: 修改 ChapterTreeAdapter.kt - 支持 Article 层级渲染**

```kotlin
// 修改 bind 方法支持 Article
when (item) {
    is ChapterTreeItem.Chapter -> bindChapter(item)
    is ChapterTreeItem.Article -> bindArticle(item)
}

// 新增 bindArticle 方法
private fun bindArticle(item: ChapterTreeItem.Article) {
    binding.tvArticleNo.text = item.articleNo
    binding.tvArticleContent.text = item.content
    binding.tvLegalBasisCount.text = "定性依据 ${item.legalBasisCount}"
    binding.tvProcessingBasisCount.text = "处理依据 ${item.processingBasisCount}"

    binding.layoutLegalBasis.setOnClickListener {
        navigateToLegalBasisList(item.articleId, item.articleNo)
    }
    binding.layoutProcessingBasis.setOnClickListener {
        navigateToProcessingBasisList(item.articleId, item.articleNo)
    }
}
```

- [ ] **Step 5: 提交UI层改动**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/regulation/
git add Ruoyi-Android-App/app/src/main/res/layout/activity_regulation_detail.xml
git commit -m "feat(android): 法规详情页添加目录和条目关联依据"
```

---

### Task 10: Android - 新建依据列表页和详情页

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/basis/LegalBasisListActivity.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/basis/LegalBasisDetailActivity.kt`
- Create: `Ruoyi-Android-App/app/src/main/res/layout/activity_legal_basis_list.xml`
- Create: `Ruoyi-Android-App/app/src/main/res/layout/activity_legal_basis_detail.xml`

- [ ] **Step 1: 创建 LegalBasisListActivity.kt**

页面接收 articleId 参数，加载该条款关联的定性依据列表。

```kotlin
@Route(path = Constant.legalBasisListRoute)
class LegalBasisListActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLegalBasisListBinding
    private lateinit var repository: LawRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLegalBasisListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        repository = LawRepository(this)
        val articleId = extractArticleIdFromIntent()

        setupToolbar("定性依据 - " + getArticleTitle())
        loadData(articleId)
    }

    private fun loadData(articleId: Long) {
        lifecycleScope.launch {
            repository.getLegalBasisesByArticle(articleId).collect { list ->
                adapter.submitList(list)
            }
        }
    }
}
```

- [ ] **Step 2: 创建 LegalBasisDetailActivity.kt**

详情页显示所有字段，每行有"复制"按钮。

```kotlin
@Route(path = Constant.legalBasisDetailRoute)
class LegalBasisDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLegalBasisDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLegalBasisDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val basisId = extractBasisIdFromIntent()
        loadData(basisId)
    }

    private fun loadData(basisId: Long) {
        lifecycleScope.launch {
            val basis = repository.getLegalBasisById(basisId)
            basis?.let { displayData(it) }
        }
    }

    private fun displayData(basis: LegalBasisEntity) {
        binding.tvTitle.text = basis.title
        binding.tvBasisNo.text = formatBasisNo(basis.basisNo)
        binding.tvViolationType.text = basis.violationType
        binding.tvIssuingAuthority.text = basis.issuingAuthority
        binding.tvEffectiveDate.text = basis.effectiveDate
        binding.tvLegalLevel.text = basis.legalLevel
        binding.tvClauses.text = parseClauses(basis.clauses)
        binding.tvLegalLiability.text = basis.legalLiability
        binding.tvDiscretionStandard.text = basis.discretionStandard

        // 设置复制按钮
        binding.btnCopyBasisNo.setOnClickListener { copyToClipboard(basis.basisNo ?: "") }
        binding.btnCopyTitle.setOnClickListener { copyToClipboard(basis.title) }
        // ... 其他字段复制按钮
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("依据内容", text)
        clipboard.setPrimaryClip(clip)
        Toast.makeText(this, "已复制", Toast.LENGTH_SHORT).show()
    }

    private fun formatBasisNo(basisNo: String?): String {
        if (basisNo.isNullOrBlank()) return ""
        val num = basisNo.toLongOrNull() ?: return basisNo
        return "$num."
    }
}
```

- [ ] **Step 3: 创建布局文件**

activity_legal_basis_list.xml - 列表页布局
activity_legal_basis_detail.xml - 详情页布局（参考设计原型，每行label+value，右侧复制按钮）

- [ ] **Step 4: 添加路由常量到 Constant.kt**

```kotlin
const val legalBasisListRoute = "/law/legalBasis/list"
const val legalBasisDetailRoute = "/law/legalBasis/detail"
const val processingBasisListRoute = "/law/processingBasis/list"
const val processingBasisDetailRoute = "/law/processingBasis/detail"
```

- [ ] **Step 5: 提交新页面**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/basis/
git add Ruoyi-Android-App/app/src/main/res/layout/activity_legal_basis_list.xml
git add Ruoyi-Android-App/app/src/main/res/layout/activity_legal_basis_detail.xml
git commit -m "feat(android): 添加依据列表页和详情页"
```

---

### Task 11: Android - 数据同步

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/SyncManager.kt`

- [ ] **Step 1: 在 SyncManager.kt 添加同步逻辑**

```kotlin
// 添加新表的同步方法
suspend fun syncProcessingBasisFromServer(): Result<Unit> = withContext(Dispatchers.IO) {
    try {
        val response = LawApi.getProcessingBasisList(pageNum = 1, pageSize = 1000)
        if (response.code == 200) {
            val entities = response.rows.map { it.toEntity() }
            processingBasisDao.insertProcessingBasises(entities)
            Result.success(Unit)
        } else {
            Result.failure(Exception(response.msg ?: "同步失败"))
        }
    } catch (e: Exception) {
        Result.failure(e)
    }
}

suspend fun syncBasisChapterLinkFromServer(): Result<Unit> = withContext(Dispatchers.IO) {
    try {
        // 调用后端接口获取关联数据
        val legalLinks = LawApi.getBasisLinkList("legal")
        val processingLinks = LawApi.getBasisLinkList("processing")
        val allLinks = (legalLinks + processingLinks).map { it.toEntity() }
        basisChapterLinkDao.insertLinks(allLinks)
        Result.success(Unit)
    } catch (e: Exception) {
        Result.failure(e)
    }
}
```

- [ ] **Step 2: 在 LawApi.kt 添加新接口**

```kotlin
// 获取处理依据列表
suspend fun getProcessingBasisList(pageNum: Int, pageSize: Int): ProcessingBasisListResponse

// 获取依据关联列表
suspend fun getBasisLinkList(basisType: String): List<BasisLinkResponse>
```

- [ ] **Step 3: 提交同步逻辑**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/SyncManager.kt
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/api/LawApi.kt
git commit -m "feat(android): 添加处理依据和关联表同步逻辑"
```

---

## 自检清单

完成所有任务后，检查以下内容：

1. **后端：**
   - [ ] 数据库表已创建
   - [ ] 菜单已添加
   - [ ] 处理依据CRUD接口可访问
   - [ ] 关联管理接口可访问
   - [ ] 统计接口返回正确数据

2. **前端：**
   - [ ] 处理依据管理页面可访问
   - [ ] 依据关联管理页面可访问
   - [ ] 表格、分页、弹窗功能正常

3. **Android：**
   - [ ] 数据库实体和DAO已创建
   - [ ] Repository方法已添加
   - [ ] 法规详情页显示目录
   - [ ] 条目显示关联依据数量徽章
   - [ ] 点击徽章跳转列表页
   - [ ] 列表页点击跳转详情页
   - [ ] 详情页每个字段可复制
   - [ ] 数据同步正常

---

## 计划完成

计划已保存至 `docs/superpowers/plans/2026-04-28-regulation-article-basis-plan.md`
