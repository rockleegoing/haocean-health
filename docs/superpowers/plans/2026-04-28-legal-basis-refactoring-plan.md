# 定性依据与处理依据表结构重构实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将定性依据和处理依据从单表拆分为：主表（存储基本信息）+ 内容表（存储可配置的内容行），支持内容行的动态管理。

**Architecture:**
- 后端：创建新的内容表实体类和CRUD接口，主表只保留基础字段（title, regulation_id, status等），删除冗余字段
- Android：新增内容表Entity和DAO，更新详情页UI从内容表读取数据
- Web前端：修改表单支持动态添加内容行
- 数据库：新建内容表，修改主表结构（删除冗余字段）

**Tech Stack:** Spring Boot + MyBatis + MySQL / Android Room + Kotlin + OkHttp / Vue 2 + Element UI

---

## 一、后端实现

### 文件结构

```
RuoYi-Vue/
├── sql/
│   └── V1.2.6__legal_basis_content_tables.sql  (新建)
│   └── V1.2.7__legal_basis_mock_data.sql       (新建)
├── ruoyi-system/
│   └── src/main/java/com/ruoyi/system/domain/
│       ├── SysLegalBasis.java                  (修改 - 精简主表)
│       ├── SysLegalBasisContent.java            (新建 - 内容表实体)
│       ├── SysProcessingBasis.java              (修改 - 精简主表)
│       └── SysProcessingBasisContent.java       (新建 - 内容表实体)
│   └── src/main/java/com/ruoyi/system/mapper/
│       ├── SysLegalBasisContentMapper.java      (新建)
│       └── SysProcessingBasisContentMapper.java (新建)
│   └── src/main/java/com/ruoyi/system/service/
│       ├── ISysLegalBasisService.java          (修改)
│       ├── ISysLegalBasisContentService.java   (新建)
│       ├── impl/SysLegalBasisServiceImpl.java  (修改)
│       └── impl/SysLegalBasisContentServiceImpl.java (新建)
│   └── src/main/java/com/ruoyi/system/service/
│       ├── ISysProcessingBasisService.java          (修改)
│       ├── ISysProcessingBasisContentService.java   (新建)
│       ├── impl/SysProcessingBasisServiceImpl.java  (修改)
│       └── impl/SysProcessingBasisContentServiceImpl.java (新建)
├── ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/
│   ├── SysLegalBasisController.java            (修改)
│   ├── SysLegalBasisContentController.java     (新建)
│   ├── SysProcessingBasisController.java       (修改)
│   └── SysProcessingBasisContentController.java (新建)
└── ruoyi-system/src/main/resources/mapper/system/
    ├── SysLegalBasisContentMapper.xml          (新建)
    └── SysProcessingBasisContentMapper.xml     (新建)
```

---

### Task 1: 创建 SQL 脚本

**Files:**
- Create: `RuoYi-Vue/sql/V1.2.6__legal_basis_content_tables.sql`
- Create: `RuoYi-Vue/sql/V1.2.7__legal_basis_mock_data.sql`

- [ ] **Step 1: 创建 V1.2.6__legal_basis_content_tables.sql**

```sql
-- ============================================
-- 脚本：V1.2.6__legal_basis_content_tables.sql
-- 版本：1.2.6
-- 日期：2026-04-28
-- 描述：创建定性依据和处理依据内容表
-- ============================================

-- 1. 创建定性依据内容表
CREATE TABLE IF NOT EXISTS `sys_legal_basis_content` (
    `content_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '内容ID',
    `basis_id` bigint(20) NOT NULL COMMENT '关联定性依据ID',
    `label` varchar(100) NOT NULL COMMENT '标签（如：条款内容、法律责任、裁量标准）',
    `content` text COMMENT '内容',
    `sort_order` int(11) DEFAULT 0 COMMENT '排序',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`content_id`),
    KEY `idx_basis_id` (`basis_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='定性依据内容表';

-- 添加外键约束（级联删除）
ALTER TABLE `sys_legal_basis_content`
    ADD CONSTRAINT `fk_legal_basis_content_basis`
    FOREIGN KEY (`basis_id`) REFERENCES `sys_legal_basis` (`basis_id`)
    ON DELETE CASCADE;

-- 2. 创建处理依据内容表
CREATE TABLE IF NOT EXISTS `sys_processing_basis_content` (
    `content_id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '内容ID',
    `basis_id` bigint(20) NOT NULL COMMENT '关联处理依据ID',
    `label` varchar(100) NOT NULL COMMENT '标签',
    `content` text COMMENT '内容',
    `sort_order` int(11) DEFAULT 0 COMMENT '排序',
    `create_by` varchar(64) DEFAULT '' COMMENT '创建者',
    `create_time` datetime DEFAULT NULL COMMENT '创建时间',
    `update_by` varchar(64) DEFAULT '' COMMENT '更新者',
    `update_time` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`content_id`),
    KEY `idx_basis_id` (`basis_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处理依据内容表';

-- 添加外键约束（级联删除）
ALTER TABLE `sys_processing_basis_content`
    ADD CONSTRAINT `fk_processing_basis_content_basis`
    FOREIGN KEY (`basis_id`) REFERENCES `sys_processing_basis` (`basis_id`)
    ON DELETE CASCADE;

-- 3. 修改定性依据主表 - 删除冗余字段
ALTER TABLE `sys_legal_basis`
    DROP COLUMN IF EXISTS `basis_no`,
    DROP COLUMN IF EXISTS `violation_type`,
    DROP COLUMN IF EXISTS `issuing_authority`,
    DROP COLUMN IF EXISTS `effective_date`,
    DROP COLUMN IF EXISTS `legal_level`,
    DROP COLUMN IF EXISTS `clauses`,
    DROP COLUMN IF EXISTS `legal_liability`,
    DROP COLUMN IF EXISTS `discretion_standard`;

-- 4. 修改处理依据主表 - 删除冗余字段
ALTER TABLE `sys_processing_basis`
    DROP COLUMN IF EXISTS `basis_no`,
    DROP COLUMN IF EXISTS `violation_type`,
    DROP COLUMN IF EXISTS `issuing_authority`,
    DROP COLUMN IF EXISTS `effective_date`,
    DROP COLUMN IF EXISTS `legal_level`,
    DROP COLUMN IF EXISTS `clauses`,
    DROP COLUMN IF EXISTS `legal_liability`,
    DROP COLUMN IF EXISTS `discretion_standard`;
```

- [ ] **Step 2: 创建 V1.2.7__legal_basis_mock_data.sql**

```sql
-- ============================================
-- 脚本：V1.2.7__legal_basis_mock_data.sql
-- 版本：1.2.7
-- 日期：2026-04-28
-- 描述：定性依据和处理依据 Mock 数据
-- ============================================

-- 定性依据内容标签定义
-- sort_order: 1-编号, 2-违法类型, 3-颁发机构, 4-实施时间, 5-效级, 6-条款内容, 7-法律责任, 8-裁量标准

-- 插入定性依据主表数据（保留原有 ID）
INSERT INTO `sys_legal_basis` (`basis_id`, `title`, `regulation_id`, `status`, `del_flag`, `create_by`, `create_time`, `remark`) VALUES
(1, '超出资质认可范围从事诊疗活动', 1, '0', '0', 'admin', NOW(), '基本医疗卫生与健康促进法'),
(2, '医疗卫生机构等未按规定报告传染病疫情', 1, '0', '0', 'admin', NOW(), '传染病防治法');

-- 插入定性依据内容表数据
-- 第一条定性依据的内容（8行）
INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(1, '编号', '001', 1, 'admin', NOW()),
(1, '违法类型', '超出资质认可范围', 2, 'admin', NOW()),
(1, '颁发机构', '卫健委', 3, 'admin', NOW()),
(1, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(1, '效级', '法律', 5, 'admin', NOW()),
(1, '条款内容', '基本医疗卫生与健康促进法第三十八条：医疗卫生机构不得使用非卫生技术人员从事医疗卫生技术工作。', 6, 'admin', NOW()),
(1, '法律责任', '由县级以上人民政府卫生健康主管部门责令停止执业，没收违法所得，并处罚款；情节严重的，吊销执业证书。', 7, 'admin', NOW()),
(1, '裁量标准', '轻微：责令停止执业，处1万元以下罚款；一般：责令停止执业，处1-3万元罚款；严重：责令停止执业，处3-5万元罚款，吊销执业证书。', 8, 'admin', NOW());

-- 第二条定性依据的内容（8行）
INSERT INTO `sys_legal_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(2, '编号', '002', 1, 'admin', NOW()),
(2, '违法类型', '传染病防治', 2, 'admin', NOW()),
(2, '颁发机构', '卫健委', 3, 'admin', NOW()),
(2, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(2, '效级', '法律', 5, 'admin', NOW()),
(2, '条款内容', '传染病防治法第三十条：疾病预防控制机构、医疗机构和采供血机构及其工作人员发现传染病疫情，应当按照规定及时报告。', 6, 'admin', NOW()),
(2, '法律责任', '由属地卫生健康主管部门责令改正，通报批评，给予警告；对负有责任的主管人员和其他直接责任人员，依法给予处分。', 7, 'admin', NOW()),
(2, '裁量标准', '轻微：责令改正，警告；一般：责令改正，通报批评；严重：给予处分，构成犯罪的，依法追究刑事责任。', 8, 'admin', NOW());

-- 处理依据主表数据
INSERT INTO `sys_processing_basis` (`basis_id`, `title`, `regulation_id`, `status`, `del_flag`, `create_by`, `create_time`, `remark`) VALUES
(1, '超出资质认可范围 - 处罚', 1, '0', '0', 'admin', NOW(), '裁量标准'),
(2, '传染病疫情瞒报 - 处罚', 1, '0', '0', 'admin', NOW(), '裁量标准');

-- 处理依据内容表数据
-- 第一条处理依据的内容（8行）
INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(1, '编号', 'P001', 1, 'admin', NOW()),
(1, '违法类型', '超出资质认可范围', 2, 'admin', NOW()),
(1, '颁发机构', '卫健委', 3, 'admin', NOW()),
(1, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(1, '效级', '法律', 5, 'admin', NOW()),
(1, '条款内容', '基本医疗卫生与健康促进法第三十八条', 6, 'admin', NOW()),
(1, '法律责任', '责令停止执业、没收违法所得、罚款、吊销执业证书', 7, 'admin', NOW()),
(1, '裁量标准', '轻微：停执+1万以下罚款；一般：停执+1-3万罚款；严重：停执+3-5万罚款+吊证', 8, 'admin', NOW());

-- 第二条处理依据的内容（8行）
INSERT INTO `sys_processing_basis_content` (`basis_id`, `label`, `content`, `sort_order`, `create_by`, `create_time`) VALUES
(2, '编号', 'P002', 1, 'admin', NOW()),
(2, '违法类型', '传染病疫情瞒报', 2, 'admin', NOW()),
(2, '颁发机构', '卫健委', 3, 'admin', NOW()),
(2, '实施时间', '2020-06-01', 4, 'admin', NOW()),
(2, '效级', '法律', 5, 'admin', NOW()),
(2, '条款内容', '传染病防治法第三十条', 6, 'admin', NOW()),
(2, '法律责任', '责令改正、通报批评、警告、处分', 7, 'admin', NOW()),
(2, '裁量标准', '轻微：责令改正+警告；一般：责令改正+通报批评；严重：处分+追究刑责', 8, 'admin', NOW());
```

- [ ] **Step 3: 提交 SQL 脚本**

```bash
git add RuoYi-Vue/sql/V1.2.6__legal_basis_content_tables.sql RuoYi-Vue/sql/V1.2.7__legal_basis_mock_data.sql
git commit -m "feat(database): 添加定性依据和处理依据内容表SQL脚本"
```

---

### Task 2: 后端 - 定性依据内容表实体类

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysLegalBasisContent.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysLegalBasis.java:1-201`

- [ ] **Step 1: 创建 SysLegalBasisContent.java**

```java
package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 定性依据内容表 sys_legal_basis_content
 */
public class SysLegalBasisContent extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 内容ID */
    private Long contentId;

    /** 关联定性依据ID */
    private Long basisId;

    /** 标签 */
    private String label;

    /** 内容 */
    private String content;

    /** 排序 */
    private Integer sortOrder;

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setBasisId(Long basisId) {
        this.basisId = basisId;
    }

    public Long getBasisId() {
        return basisId;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("contentId", getContentId())
                .append("basisId", getBasisId())
                .append("label", getLabel())
                .append("content", getContent())
                .append("sortOrder", getSortOrder())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
```

- [ ] **Step 2: 修改 SysLegalBasis.java - 精简主表**

找到并删除以下字段的 getter/setter 以及 toString 方法中的对应行：
- basisNo
- violationType
- issuingAuthority
- effectiveDate
- legalLevel
- clauses
- legalLiability
- discretionStandard

修改后的 SysLegalBasis.java 应保留字段：
- basisId, title, regulationId, status, delFlag, createBy, createTime, updateBy, updateTime, remark

```java
// 删除的字段（共8个）
// private String basisNo;
// private String violationType;
// private String issuingAuthority;
// private String effectiveDate;
// private String legalLevel;
// private String clauses;
// private String legalLiability;
// private String discretionStandard;
```

- [ ] **Step 3: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysLegalBasisContent.java RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysLegalBasis.java
git commit -m "feat(system): 添加定性依据内容表实体，精简主表字段"
```

---

### Task 3: 后端 - 处理依据内容表实体类

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysProcessingBasisContent.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysProcessingBasis.java:1-201`

- [ ] **Step 1: 创建 SysProcessingBasisContent.java**

```java
package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 处理依据内容表 sys_processing_basis_content
 */
public class SysProcessingBasisContent extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 内容ID */
    private Long contentId;

    /** 关联处理依据ID */
    private Long basisId;

    /** 标签 */
    private String label;

    /** 内容 */
    private String content;

    /** 排序 */
    private Integer sortOrder;

    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }

    public Long getContentId() {
        return contentId;
    }

    public void setBasisId(Long basisId) {
        this.basisId = basisId;
    }

    public Long getBasisId() {
        return basisId;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
                .append("contentId", getContentId())
                .append("basisId", getBasisId())
                .append("label", getLabel())
                .append("content", getContent())
                .append("sortOrder", getSortOrder())
                .append("createBy", getCreateBy())
                .append("createTime", getCreateTime())
                .append("updateBy", getUpdateBy())
                .append("updateTime", getUpdateTime())
                .toString();
    }
}
```

- [ ] **Step 2: 修改 SysProcessingBasis.java - 精简主表**

同样删除 8 个冗余字段。

- [ ] **Step 3: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysProcessingBasisContent.java RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysProcessingBasis.java
git commit -m "feat(system): 添加处理依据内容表实体，精简主表字段"
```

---

### Task 4: 后端 - Mapper 层

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysLegalBasisContentMapper.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysLegalBasisContentMapper.xml`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysProcessingBasisContentMapper.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysProcessingBasisContentMapper.xml`

- [ ] **Step 1: 创建 SysLegalBasisContentMapper.java**

```java
package com.ruoyi.system.mapper;

import com.ruoyi.system.domain.SysLegalBasisContent;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SysLegalBasisContentMapper {

    /**
     * 查询定性依据内容
     */
    SysLegalBasisContent selectSysLegalBasisContentById(Long contentId);

    /**
     * 查询定性依据内容列表
     */
    List<SysLegalBasisContent> selectSysLegalBasisContentList(SysLegalBasisContent sysLegalBasisContent);

    /**
     * 查询某定性依据的所有内容
     */
    List<SysLegalBasisContent> selectSysLegalBasisContentByBasisId(Long basisId);

    /**
     * 新增定性依据内容
     */
    int insertSysLegalBasisContent(SysLegalBasisContent sysLegalBasisContent);

    /**
     * 批量新增定性依据内容
     */
    int batchInsertSysLegalBasisContent(@Param("list") List<SysLegalBasisContent> list);

    /**
     * 修改定性依据内容
     */
    int updateSysLegalBasisContent(SysLegalBasisContent sysLegalBasisContent);

    /**
     * 删除定性依据内容
     */
    int deleteSysLegalBasisContentById(Long contentId);

    /**
     * 删除某定性依据的所有内容
     */
    int deleteSysLegalBasisContentByBasisId(Long basisId);

    /**
     * 批量删除定性依据内容
     */
    int deleteSysLegalBasisContentByIds(Long[] contentIds);
}
```

- [ ] **Step 2: 创建 SysLegalBasisContentMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN" "http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ruoyi.system.mapper.SysLegalBasisContentMapper">

    <resultMap type="com.ruoyi.system.domain.SysLegalBasisContent" id="SysLegalBasisContentResult">
        <result property="contentId" column="content_id"/>
        <result property="basisId" column="basis_id"/>
        <result property="label" column="label"/>
        <result property="content" column="content"/>
        <result property="sortOrder" column="sort_order"/>
        <result property="createBy" column="create_by"/>
        <result property="createTime" column="create_time"/>
        <result property="updateBy" column="update_by"/>
        <result property="updateTime" column="update_time"/>
    </resultMap>

    <sql id="selectSysLegalBasisContentVo">
        SELECT content_id, basis_id, label, content, sort_order, create_by, create_time, update_by, update_time
        FROM sys_legal_basis_content
    </sql>

    <select id="selectSysLegalBasisContentList" resultMap="SysLegalBasisContentResult">
        <include refid="selectSysLegalBasisContentVo"/>
        <where>
            <if test="basisId != null">AND basis_id = #{basisId}</if>
            <if test="label != null and label != ''">AND label LIKE '%' || #{label} || '%'</if>
        </where>
        ORDER BY sort_order ASC
    </select>

    <select id="selectSysLegalBasisContentById" resultMap="SysLegalBasisContentResult">
        <include refid="selectSysLegalBasisContentVo"/>
        WHERE content_id = #{contentId}
    </select>

    <select id="selectSysLegalBasisContentByBasisId" resultMap="SysLegalBasisContentResult">
        <include refid="selectSysLegalBasisContentVo"/>
        WHERE basis_id = #{basisId}
        ORDER BY sort_order ASC
    </select>

    <insert id="insertSysLegalBasisContent" parameterType="com.ruoyi.system.domain.SysLegalBasisContent" useGeneratedKeys="true" keyProperty="contentId">
        INSERT INTO sys_legal_basis_content
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="basisId != null">basis_id,</if>
            <if test="label != null">label,</if>
            <if test="content != null">content,</if>
            <if test="sortOrder != null">sort_order,</if>
            <if test="createBy != null">create_by,</if>
            <if test="createTime != null">create_time,</if>
        </trim>
        <trim prefix="VALUES (" suffix=")" suffixOverrides=",">
            <if test="basisId != null">#{basisId},</if>
            <if test="label != null">#{label},</if>
            <if test="content != null">#{content},</if>
            <if test="sortOrder != null">#{sortOrder},</if>
            <if test="createBy != null">#{createBy},</if>
            <if test="createTime != null">#{createTime},</if>
        </trim>
    </insert>

    <insert id="batchInsertSysLegalBasisContent">
        INSERT INTO sys_legal_basis_content (basis_id, label, content, sort_order, create_by, create_time)
        VALUES
        <foreach collection="list" item="item" separator=",">
            (#{item.basisId}, #{item.label}, #{item.content}, #{item.sortOrder}, #{item.createBy}, #{item.createTime})
        </foreach>
    </insert>

    <update id="updateSysLegalBasisContent" parameterType="com.ruoyi.system.domain.SysLegalBasisContent">
        UPDATE sys_legal_basis_content
        <trim prefix="SET" suffixOverrides=",">
            <if test="label != null">label = #{label},</if>
            <if test="content != null">content = #{content},</if>
            <if test="sortOrder != null">sort_order = #{sortOrder},</if>
            <if test="updateBy != null">update_by = #{updateBy},</if>
            <if test="updateTime != null">update_time = #{updateTime},</if>
        </trim>
        WHERE content_id = #{contentId}
    </update>

    <delete id="deleteSysLegalBasisContentById">
        DELETE FROM sys_legal_basis_content WHERE content_id = #{contentId}
    </delete>

    <delete id="deleteSysLegalBasisContentByBasisId">
        DELETE FROM sys_legal_basis_content WHERE basis_id = #{basisId}
    </delete>

    <delete id="deleteSysLegalBasisContentByIds">
        DELETE FROM sys_legal_basis_content WHERE content_id IN
        <foreach item="contentId" collection="array" open="(" separator="," close=")">
            #{contentId}
        </foreach>
    </delete>
</mapper>
```

- [ ] **Step 3: 创建 SysProcessingBasisContentMapper.java 和 SysProcessingBasisContentMapper.xml**

参考上面的 SysLegalBasisContentMapper.java/xml，使用相同的模式创建处理依据内容表的Mapper。

- [ ] **Step 4: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysLegalBasisContentMapper.java RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysLegalBasisContentMapper.xml RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysProcessingBasisContentMapper.java RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysProcessingBasisContentMapper.xml
git commit -m "feat(system): 添加定性依据和处理依据内容表Mapper"
```

---

### Task 5: 后端 - Service 层

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysLegalBasisContentService.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysLegalBasisContentServiceImpl.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysProcessingBasisContentService.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysProcessingBasisContentServiceImpl.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysLegalBasisService.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysLegalBasisServiceImpl.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysProcessingBasisService.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysProcessingBasisServiceImpl.java`

- [ ] **Step 1: 创建 ISysLegalBasisContentService.java**

```java
package com.ruoyi.system.service;

import com.ruoyi.system.domain.SysLegalBasisContent;
import java.util.List;

public interface ISysLegalBasisContentService {

    /**
     * 查询定性依据内容
     */
    SysLegalBasisContent selectSysLegalBasisContentById(Long contentId);

    /**
     * 查询定性依据内容列表
     */
    List<SysLegalBasisContent> selectSysLegalBasisContentList(SysLegalBasisContent sysLegalBasisContent);

    /**
     * 查询某定性依据的所有内容
     */
    List<SysLegalBasisContent> selectSysLegalBasisContentByBasisId(Long basisId);

    /**
     * 新增定性依据内容
     */
    int insertSysLegalBasisContent(SysLegalBasisContent sysLegalBasisContent);

    /**
     * 批量新增定性依据内容
     */
    int batchInsertSysLegalBasisContent(Long basisId, List<SysLegalBasisContent> contents);

    /**
     * 修改定性依据内容
     */
    int updateSysLegalBasisContent(SysLegalBasisContent sysLegalBasisContent);

    /**
     * 删除定性依据内容
     */
    int deleteSysLegalBasisContentById(Long contentId);

    /**
     * 删除某定性依据的所有内容
     */
    int deleteSysLegalBasisContentByBasisId(Long basisId);

    /**
     * 批量删除定性依据内容
     */
    int deleteSysLegalBasisContentByIds(Long[] contentIds);
}
```

- [ ] **Step 2: 创建 SysLegalBasisContentServiceImpl.java**

```java
package com.ruoyi.system.service.impl;

import com.ruoyi.system.domain.SysLegalBasisContent;
import com.ruoyi.system.mapper.SysLegalBasisContentMapper;
import com.ruoyi.system.service.ISysLegalBasisContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SysLegalBasisContentServiceImpl implements ISysLegalBasisContentService {

    @Autowired
    private SysLegalBasisContentMapper sysLegalBasisContentMapper;

    @Override
    public SysLegalBasisContent selectSysLegalBasisContentById(Long contentId) {
        return sysLegalBasisContentMapper.selectSysLegalBasisContentById(contentId);
    }

    @Override
    public List<SysLegalBasisContent> selectSysLegalBasisContentList(SysLegalBasisContent sysLegalBasisContent) {
        return sysLegalBasisContentMapper.selectSysLegalBasisContentList(sysLegalBasisContent);
    }

    @Override
    public List<SysLegalBasisContent> selectSysLegalBasisContentByBasisId(Long basisId) {
        return sysLegalBasisContentMapper.selectSysLegalBasisContentByBasisId(basisId);
    }

    @Override
    public int insertSysLegalBasisContent(SysLegalBasisContent sysLegalBasisContent) {
        return sysLegalBasisContentMapper.insertSysLegalBasisContent(sysLegalBasisContent);
    }

    @Override
    @Transactional
    public int batchInsertSysLegalBasisContent(Long basisId, List<SysLegalBasisContent> contents) {
        if (contents == null || contents.isEmpty()) {
            return 0;
        }
        // 设置 basisId
        for (SysLegalBasisContent content : contents) {
            content.setBasisId(basisId);
        }
        return sysLegalBasisContentMapper.batchInsertSysLegalBasisContent(contents);
    }

    @Override
    public int updateSysLegalBasisContent(SysLegalBasisContent sysLegalBasisContent) {
        return sysLegalBasisContentMapper.updateSysLegalBasisContent(sysLegalBasisContent);
    }

    @Override
    public int deleteSysLegalBasisContentById(Long contentId) {
        return sysLegalBasisContentMapper.deleteSysLegalBasisContentById(contentId);
    }

    @Override
    public int deleteSysLegalBasisContentByBasisId(Long basisId) {
        return sysLegalBasisContentMapper.deleteSysLegalBasisContentByBasisId(basisId);
    }

    @Override
    public int deleteSysLegalBasisContentByIds(Long[] contentIds) {
        return sysLegalBasisContentMapper.deleteSysLegalBasisContentByIds(contentIds);
    }
}
```

- [ ] **Step 3: 创建 ISysProcessingBasisContentService.java 和 SysProcessingBasisContentServiceImpl.java**

参考上面的 ISysLegalBasisContentService.java 和 SysLegalBasisContentServiceImpl.java，使用相同的模式创建处理依据内容表的Service。

- [ ] **Step 4: 修改 ISysLegalBasisService.java - 添加新方法声明**

在接口中添加：
```java
/**
 * 查询定性依据详情（含内容列表）
 */
Map<String, Object> selectLegalBasisDetail(Long basisId);
```

- [ ] **Step 5: 修改 SysLegalBasisServiceImpl.java**

添加 `selectLegalBasisDetail` 方法实现：
```java
@Override
public Map<String, Object> selectLegalBasisDetail(Long basisId) {
    SysLegalBasis legalBasis = sysLegalBasisMapper.selectSysLegalBasisById(basisId);
    List<SysLegalBasisContent> contents = sysLegalBasisContentMapper.selectSysLegalBasisContentByBasisId(basisId);
    Map<String, Object> result = new HashMap<>();
    result.put("basis", legalBasis);
    result.put("contents", contents);
    return result;
}
```

同时需要注入 `ISysLegalBasisContentService`。

- [ ] **Step 6: 修改 ISysProcessingBasisService.java 和 SysProcessingBasisServiceImpl.java**

同样添加 `selectProcessingBasisDetail` 方法。

- [ ] **Step 7: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysLegalBasisContentService.java RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysLegalBasisContentServiceImpl.java RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysProcessingBasisContentService.java RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysProcessingBasisContentServiceImpl.java RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysLegalBasisService.java RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysLegalBasisServiceImpl.java RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysProcessingBasisService.java RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysProcessingBasisServiceImpl.java
git commit -m "feat(system): 添加定性依据和处理依据内容表Service层"
```

---

### Task 6: 后端 - Controller 层

**Files:**
- Create: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysLegalBasisContentController.java`
- Create: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysProcessingBasisContentController.java`
- Modify: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysLegalBasisController.java`
- Modify: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysProcessingBasisController.java`

- [ ] **Step 1: 创建 SysLegalBasisContentController.java**

```java
package com.ruoyi.web.controller.system;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.system.domain.SysLegalBasisContent;
import com.ruoyi.system.service.ISysLegalBasisContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/system/legalBasisContent")
public class SysLegalBasisContentController extends BaseController {

    @Autowired
    private ISysLegalBasisContentService sysLegalBasisContentService;

    /**
     * 获取某定性依据的所有内容
     */
    @PreAuthorize("@ss.hasPermi('system:legalBasis:list')")
    @GetMapping("/list/{basisId}")
    public TableDataInfo list(@PathVariable Long basisId) {
        List<SysLegalBasisContent> list = sysLegalBasisContentService.selectSysLegalBasisContentByBasisId(basisId);
        return getDataTable(list);
    }

    /**
     * 新增内容行
     */
    @PreAuthorize("@ss.hasPermi('system:legalBasis:add')")
    @Log(title = "定性依据内容", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody SysLegalBasisContent sysLegalBasisContent) {
        return toAjax(sysLegalBasisContentService.insertSysLegalBasisContent(sysLegalBasisContent));
    }

    /**
     * 修改内容行
     */
    @PreAuthorize("@ss.hasPermi('system:legalBasis:edit')")
    @Log(title = "定性依据内容", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody SysLegalBasisContent sysLegalBasisContent) {
        return toAjax(sysLegalBasisContentService.updateSysLegalBasisContent(sysLegalBasisContent));
    }

    /**
     * 删除内容行
     */
    @PreAuthorize("@ss.hasPermi('system:legalBasis:remove')")
    @Log(title = "定性依据内容", businessType = BusinessType.DELETE)
    @DeleteMapping("/{contentIds}")
    public AjaxResult remove(@PathVariable Long[] contentIds) {
        return toAjax(sysLegalBasisContentService.deleteSysLegalBasisContentByIds(contentIds));
    }
}
```

- [ ] **Step 2: 创建 SysProcessingBasisContentController.java**

参考上面的 SysLegalBasisContentController.java，使用相同的模式创建处理依据内容表的Controller。

- [ ] **Step 3: 修改 SysLegalBasisController.java**

修改 `getInfo(Long basisId)` 方法，返回详情（含内容列表）：
```java
/**
 * 获取定性依据详情
 */
@GetMapping("/{basisId}")
public AjaxResult getInfo(@PathVariable Long basisId) {
    Map<String, Object> detail = sysLegalBasisService.selectLegalBasisDetail(basisId);
    return success(detail);
}
```

- [ ] **Step 4: 修改 SysProcessingBasisController.java**

同样修改 `getInfo(Long basisId)` 方法。

- [ ] **Step 5: 提交**

```bash
git add RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysLegalBasisContentController.java RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysProcessingBasisContentController.java RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysLegalBasisController.java RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysProcessingBasisController.java
git commit -m "feat(controller): 添加内容表Controller，修改详情接口返回结构"
```

---

## 二、Android 实现

### 文件结构

```
Ruoyi-Android-App/
└── app/src/main/java/com/ruoyi/app/
    └── feature/law/
        └── db/
            ├── entity/
            │   ├── LegalBasisContentEntity.kt     (新建)
            │   ├── ProcessingBasisContentEntity.kt (新建)
            │   ├── LegalBasisEntity.kt           (修改 - 精简)
            │   └── ProcessingBasisEntity.kt       (修改 - 精简)
            └── dao/
                ├── LegalBasisContentDao.kt        (新建)
                └── ProcessingBasisContentDao.kt   (新建)
        └── api/
        │   └── LawApi.kt                         (修改 - 添加内容表API)
        └── repository/
        │   └── LawRepository.kt                  (修改 - 添加内容查询方法)
    └── sync/
        └── LawSyncManager.kt                     (修改 - 同步内容表)
    └── feature/law/ui/basis/
        ├── LegalBasisDetailActivity.kt          (修改 - 从内容表读取)
        ├── ProcessingBasisDetailActivity.kt     (修改 - 从内容表读取)
        └── activity_legal_basis_detail.xml       (修改 - 动态内容布局)
        └── activity_processing_basis_detail.xml  (修改 - 动态内容布局)
    └── data/database/
        └── AppDatabase.kt                       (修改 - 注册新DAO)
```

---

### Task 7: Android - Room 实体类

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/entity/LegalBasisContentEntity.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/entity/ProcessingBasisContentEntity.kt`
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/entity/LegalBasisEntity.kt`
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/entity/ProcessingBasisEntity.kt`

- [ ] **Step 1: 创建 LegalBasisContentEntity.kt**

```kotlin
package com.ruoyi.app.feature.law.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 定性依据内容实体
 */
@Entity(
    tableName = "sys_legal_basis_content",
    foreignKeys = [
        ForeignKey(
            entity = LegalBasisEntity::class,
            parentColumns = ["basis_id"],
            childColumns = ["basis_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class LegalBasisContentEntity(
    @PrimaryKey @ColumnInfo(name = "content_id") val contentId: Long,
    @ColumnInfo(name = "basis_id") val basisId: Long,
    @ColumnInfo(name = "label") val label: String,
    @ColumnInfo(name = "content") val content: String?,
    @ColumnInfo(name = "sort_order") val sortOrder: Int,
    @ColumnInfo(name = "create_by") val createBy: String?,
    @ColumnInfo(name = "create_time") val createTime: Long?,
    @ColumnInfo(name = "update_by") val updateBy: String?,
    @ColumnInfo(name = "update_time") val updateTime: Long?
)
```

- [ ] **Step 2: 创建 ProcessingBasisContentEntity.kt**

```kotlin
package com.ruoyi.app.feature.law.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * 处理依据内容实体
 */
@Entity(
    tableName = "sys_processing_basis_content",
    foreignKeys = [
        ForeignKey(
            entity = ProcessingBasisEntity::class,
            parentColumns = ["basis_id"],
            childColumns = ["basis_id"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class ProcessingBasisContentEntity(
    @PrimaryKey @ColumnInfo(name = "content_id") val contentId: Long,
    @ColumnInfo(name = "basis_id") val basisId: Long,
    @ColumnInfo(name = "label") val label: String,
    @ColumnInfo(name = "content") val content: String?,
    @ColumnInfo(name = "sort_order") val sortOrder: Int,
    @ColumnInfo(name = "create_by") val createBy: String?,
    @ColumnInfo(name = "create_time") val createTime: Long?,
    @ColumnInfo(name = "update_by") val updateBy: String?,
    @ColumnInfo(name = "update_time") val updateTime: Long?
)
```

- [ ] **Step 3: 修改 LegalBasisEntity.kt - 精简主表**

删除以下字段（它们将被移到内容表）：
- basisNo
- violationType
- issuingAuthority
- effectiveDate
- legalLevel
- clauses
- legalLiability
- discretionStandard

保留字段：basisId, title, regulationId, status, delFlag, createBy, createTime, updateBy, updateTime, remark, syncStatus

修改后的实体：
```kotlin
@Entity(tableName = "sys_legal_basis")
data class LegalBasisEntity(
    @PrimaryKey @ColumnInfo(name = "basis_id") val basisId: Long,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "regulation_id") val regulationId: Long?,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "del_flag") val delFlag: String,
    @ColumnInfo(name = "create_by") val createBy: String?,
    @ColumnInfo(name = "create_time") val createTime: Long?,
    @ColumnInfo(name = "update_by") val updateBy: String?,
    @ColumnInfo(name = "update_time") val updateTime: Long?,
    @ColumnInfo(name = "remark") val remark: String?,
    val syncStatus: String = "SYNCED"
)
```

- [ ] **Step 4: 修改 ProcessingBasisEntity.kt - 精简主表**

同样的方式删除 8 个冗余字段。

- [ ] **Step 5: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/entity/LegalBasisContentEntity.kt Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/entity/ProcessingBasisContentEntity.kt Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/entity/LegalBasisEntity.kt Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/entity/ProcessingBasisEntity.kt
git commit -m "feat(android): 添加内容表Entity，精简主表Entity"
```

---

### Task 8: Android - DAO 接口

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/dao/LegalBasisContentDao.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/dao/ProcessingBasisContentDao.kt`

- [ ] **Step 1: 创建 LegalBasisContentDao.kt**

```kotlin
package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.LegalBasisContentEntity
import kotlinx.coroutines.flow.Flow

/**
 * 定性依据内容DAO
 */
@Dao
interface LegalBasisContentDao {

    @Query("SELECT * FROM sys_legal_basis_content WHERE basis_id = :basisId ORDER BY sort_order ASC")
    fun getContentsByBasisId(basisId: Long): Flow<List<LegalBasisContentEntity>>

    @Query("SELECT * FROM sys_legal_basis_content WHERE basis_id = :basisId ORDER BY sort_order ASC")
    suspend fun getContentsByBasisIdList(basisId: Long): List<LegalBasisContentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContents(contents: List<LegalBasisContentEntity>)

    @Query("DELETE FROM sys_legal_basis_content WHERE basis_id = :basisId")
    suspend fun deleteByBasisId(basisId: Long)

    @Query("DELETE FROM sys_legal_basis_content")
    suspend fun deleteAll()
}
```

- [ ] **Step 2: 创建 ProcessingBasisContentDao.kt**

```kotlin
package com.ruoyi.app.feature.law.db.dao

import androidx.room.*
import com.ruoyi.app.feature.law.db.entity.ProcessingBasisContentEntity
import kotlinx.coroutines.flow.Flow

/**
 * 处理依据内容DAO
 */
@Dao
interface ProcessingBasisContentDao {

    @Query("SELECT * FROM sys_processing_basis_content WHERE basis_id = :basisId ORDER BY sort_order ASC")
    fun getContentsByBasisId(basisId: Long): Flow<List<ProcessingBasisContentEntity>>

    @Query("SELECT * FROM sys_processing_basis_content WHERE basis_id = :basisId ORDER BY sort_order ASC")
    suspend fun getContentsByBasisIdList(basisId: Long): List<ProcessingBasisContentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContents(contents: List<ProcessingBasisContentEntity>)

    @Query("DELETE FROM sys_processing_basis_content WHERE basis_id = :basisId")
    suspend fun deleteByBasisId(basisId: Long)

    @Query("DELETE FROM sys_processing_basis_content")
    suspend fun deleteAll()
}
```

- [ ] **Step 3: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/dao/LegalBasisContentDao.kt Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/dao/ProcessingBasisContentDao.kt
git commit -m "feat(android): 添加内容表DAO接口"
```

---

### Task 9: Android - AppDatabase 注册

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt:1-164`

- [ ] **Step 1: 在 entities 列表中添加新实体**

在 @Database 注解的 entities 数组中添加：
```kotlin
LegalBasisContentEntity::class,
ProcessingBasisContentEntity::class,
```

同时删除已精简字段的旧实体中的 `LegalBasisEntity::class` 和 `ProcessingBasisEntity::class`（如果之前已存在则忽略）。

- [ ] **Step 2: 添加 DAO 抽象方法**

在 AppDatabase 类中添加：
```kotlin
abstract fun legalBasisContentDao(): LegalBasisContentDao
abstract fun processingBasisContentDao(): ProcessingBasisContentDao
```

- [ ] **Step 3: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt
git commit -m "feat(android): 在AppDatabase中注册内容表Entity和DAO"
```

---

### Task 10: Android - API 解析更新

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/model/LegalBasis.kt`
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/api/LawApi.kt`

- [ ] **Step 1: 创建 LegalBasisContent 数据类**

在 `LegalBasis.kt` 中添加：
```kotlin
/**
 * 定性依据内容
 */
data class LegalBasisContent(
    val contentId: Long,
    val basisId: Long,
    val label: String,
    val content: String?,
    val sortOrder: Int
)
```

- [ ] **Step 2: 添加 LegalBasisDetailResponse 模型**

```kotlin
/**
 * 定性依据详情响应（主表+内容列表）
 */
data class LegalBasisDetailResponse(
    val code: Int,
    val msg: String?,
    val data: LegalBasisDetailData?
)

data class LegalBasisDetailData(
    val basis: LegalBasis,
    val contents: List<LegalBasisContent>
)
```

- [ ] **Step 3: 修改 LawApi.kt - 添加内容表解析方法**

在 `parseLegalBasisDetailResponse` 方法中，修改为解析新的响应结构：
```kotlin
private fun parseLegalBasisDetailResponse(json: String): LegalBasisDetailResponse {
    return try {
        val obj = JSONObject(json)
        val dataObj = obj.optJSONObject("data")
        val basisObj = dataObj?.optJSONObject("basis")
        val contentsArray = dataObj?.optJSONArray("contents") ?: JSONArray()

        val contents = mutableListOf<LegalBasisContent>()
        for (i in 0 until contentsArray.length()) {
            val item = contentsArray.getJSONObject(i)
            contents.add(
                LegalBasisContent(
                    contentId = item.optLong("contentId", 0),
                    basisId = item.optLong("basisId", 0),
                    label = item.optString("label", ""),
                    content = item.optString("content", null),
                    sortOrder = item.optInt("sortOrder", 0)
                )
            )
        }

        LegalBasisDetailResponse(
            code = obj.optInt("code", 0),
            msg = obj.optString("msg", null),
            data = if (basisObj != null) LegalBasisDetailData(
                basis = parseLegalBasis(basisObj),
                contents = contents
            ) else null
        )
    } catch (e: Exception) {
        LegalBasisDetailResponse(code = 500, msg = e.message, data = null)
    }
}
```

- [ ] **Step 4: 添加 toEntity 扩展函数**

```kotlin
fun LegalBasisContent.toEntity(): LegalBasisContentEntity {
    return LegalBasisContentEntity(
        contentId = contentId,
        basisId = basisId,
        label = label,
        content = content,
        sortOrder = sortOrder,
        createBy = null,
        createTime = null,
        updateBy = null,
        updateTime = null
    )
}
```

- [ ] **Step 5: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/model/LegalBasis.kt Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/api/LawApi.kt
git commit -m "feat(android): 更新API解析支持内容表数据结构"
```

---

### Task 11: Android - Repository 层

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawRepository.kt`

- [ ] **Step 1: 添加内容查询方法**

在 LawRepository 中添加：
```kotlin
/**
 * 获取定性依据内容列表
 */
fun getLegalBasisContents(basisId: Long): Flow<List<LegalBasisContentEntity>> {
    return legalBasisContentDao.getContentsByBasisId(basisId)
}

/**
 * 获取处理依据内容列表
 */
fun getProcessingBasisContents(basisId: Long): Flow<List<ProcessingBasisContentEntity>> {
    return processingBasisContentDao.getContentsByBasisId(basisId)
}
```

需要注入 `legalBasisContentDao` 和 `processingBasisContentDao`。

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawRepository.kt
git commit -m "feat(android): 在LawRepository中添加内容表查询方法"
```

---

### Task 12: Android - 同步逻辑更新

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/LawSyncManager.kt`

- [ ] **Step 1: 添加 DAO 注入**

```kotlin
private val legalBasisContentDao = AppDatabase.getInstance(context).legalBasisContentDao()
private val processingBasisContentDao = AppDatabase.getInstance(context).processingBasisContentDao()
```

- [ ] **Step 2: 修改 syncFull() - 同步内容表**

在 `syncFull()` 中，在同步定性依据主表之后添加：
```kotlin
// 同步定性依据内容表
for (basis in basisResponse.rows) {
    syncLegalBasisContents(basis.basisId)
}

// 同步处理依据
val processingBasisResponse = LawApi.getProcessingBasisList(pageNum = 1, pageSize = 1000)
if (processingBasisResponse.code == 200) {
    val entities = processingBasisResponse.rows.map { it.toEntity() }
    processingBasisDao.insertProcessingBasises(entities)
}

// 同步处理依据内容表
for (basis in processingBasisResponse.rows) {
    syncProcessingBasisContents(basis.basisId)
}
```

- [ ] **Step 3: 添加 syncLegalBasisContents() 方法**

```kotlin
private suspend fun syncLegalBasisContents(basisId: Long) {
    val detailResponse = LawApi.getLegalBasisDetail(basisId)
    if (detailResponse.code == 200 && detailResponse.data?.contents != null) {
        val contentEntities = detailResponse.data.contents.map { it.toEntity() }
        legalBasisContentDao.insertContents(contentEntities)
    }
}
```

- [ ] **Step 4: 添加 syncProcessingBasisContents() 方法**

类似上面的 `syncLegalBasisContents()`。

- [ ] **Step 5: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/LawSyncManager.kt
git commit -m "feat(android): 更新同步逻辑支持内容表数据同步"
```

---

### Task 13: Android - 详情页 UI 重构

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/basis/LegalBasisDetailActivity.kt`
- Modify: `Ruoyi-Android-App/app/src/main/res/layout/activity_legal_basis_detail.xml`
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/basis/ProcessingBasisDetailActivity.kt`
- Modify: `Ruoyi-Android-App/app/src/main/res/layout/activity_processing_basis_detail.xml`

- [ ] **Step 1: 修改 LegalBasisDetailActivity.kt - 从内容表读取**

当前 Activity 直接从主表实体读取数据显示。现在改为：
1. 从主表读取标题
2. 从内容表读取所有内容行
3. 动态显示内容行，每行包含：标签 + 复制按钮 + 内容

```kotlin
private fun loadDetail() {
    lifecycleScope.launch {
        val legalBasis = repository.getLegalBasisById(basisId)
        legalBasis?.let { displayDetail(it) }

        // 从内容表加载内容
        repository.getLegalBasisContents(basisId).collect { contents ->
            displayContents(contents)
        }
    }
}

private fun displayContents(contents: List<LegalBasisContentEntity>) {
    // 使用 RecyclerView 或动态添加 View 显示内容
    // 每行：标签 + 复制按钮 + 内容
    binding.contentContainer.removeAllViews()
    for (content in contents) {
        val itemView = layoutInflater.inflate(R.layout.item_basis_content, binding.contentContainer, false)
        itemView.findViewById<TextView>(R.id.tv_label).text = content.label
        itemView.findViewById<TextView>(R.id.tv_content).text = content.content ?: "暂无"
        itemView.findViewById<ImageButton>(R.id.btn_copy).setOnClickListener {
            copyToClipboard(content.content ?: "", content.label)
        }
        binding.contentContainer.addView(itemView)
    }
}
```

- [ ] **Step 2: 创建 item_basis_content.xml 布局**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:layout_marginTop="12dp">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="horizontal"
        android:gravity="center_vertical">

        <TextView
            android:id="@+id/tv_label"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:textSize="14sp"
            android:textColor="@color/color_6"/>

        <View
            android:layout_width="0dp"
            android:layout_height="0dp"
            android:layout_weight="1"/>

        <ImageButton
            android:id="@+id/btn_copy"
            android:layout_width="32dp"
            android:layout_height="32dp"
            android:src="@android:drawable/ic_menu_share"
            android:background="?attr/selectableItemBackgroundBorderless"
            android:contentDescription="复制"/>
    </LinearLayout>

    <TextView
        android:id="@+id/tv_content"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:layout_marginTop="4dp"
        android:textSize="14sp"
        android:textColor="@color/black"
        android:lineSpacingExtra="4dp"/>
</LinearLayout>
```

- [ ] **Step 3: 修改 activity_legal_basis_detail.xml**

保留标题区域，删除固定的 8 个字段区域，替换为动态容器：
```xml
<LinearLayout
    android:id="@+id/content_container"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="vertical"
    android:layout_marginTop="16dp"/>
```

- [ ] **Step 4: 修改 ProcessingBasisDetailActivity.kt 和对应的 layout**

与 LegalBasisDetailActivity 相同的修改模式。

- [ ] **Step 5: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/basis/LegalBasisDetailActivity.kt Ruoyi-Android-App/app/src/main/res/layout/activity_legal_basis_detail.xml Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/basis/ProcessingBasisDetailActivity.kt Ruoyi-Android-App/app/src/main/res/layout/activity_processing_basis_detail.xml Ruoyi-Android-App/app/src/main/res/layout/item_basis_content.xml
git commit -m "feat(android): 重构详情页UI，从内容表读取数据"
```

---

## 三、Web 前端实现

### 文件结构

```
RuoYi-Vue/ruoyi-ui/src/views/system/
├── legalBasis/
│   └── index.vue        (修改 - 支持动态内容行)
└── regulation/
    └── processingBasis/
        └── index.vue    (修改 - 支持动态内容行)
```

---

### Task 14: Web 前端 - 定性依据页面

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/system/legalBasis/index.vue`

- [ ] **Step 1: 分析现有结构**

现有页面应该是表单形式，包含 8 个固定字段。需要修改为：
- 主表字段：title（标题）
- 内容表：动态数组，每行包含 label + content

- [ ] **Step 2: 添加表单数据结构**

```javascript
// 表单数据
form: {
  basisId: undefined,
  title: '',
  contents: [
    { label: '编号', content: '' },
    { label: '违法类型', content: '' },
    { label: '颁发机构', content: '' },
    { label: '实施时间', content: '' },
    { label: '效级', content: '' },
    { label: '条款内容', content: '' },
    { label: '法律责任', content: '' },
    { label: '裁量标准', content: '' }
  ]
}
```

- [ ] **Step 3: 添加内容行操作**

```html
<div v-for="(row, index) in form.contents" :key="index" class="content-row">
  <el-input v-model="row.label" placeholder="标签" style="width: 120px;" />
  <el-input v-model="row.content" type="textarea" placeholder="内容" />
  <el-button @click="removeContent(index)" icon="el-icon-delete">删除</el-button>
</div>
<el-button @click="addContent" icon="el-icon-plus">新增内容行</el-button>
```

```javascript
addContent() {
  this.form.contents.push({ label: '', content: '' })
},
removeContent(index) {
  this.form.contents.splice(index, 1)
}
```

- [ ] **Step 4: 修改提交逻辑**

```javascript
submitForm() {
  // 先提交主表
  // 再提交内容表（批量）
}
```

- [ ] **Step 5: 提交**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/system/legalBasis/index.vue
git commit -m "feat(ui): 定性依据页面支持动态内容行"
```

---

### Task 15: Web 前端 - 处理依据页面

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/system/regulation/processingBasis/index.vue`

- [ ] **Step 1: 参考 Task 14 相同的模式修改**

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/system/regulation/processingBasis/index.vue
git commit -m "feat(ui): 处理依据页面支持动态内容行"
```

---

## 四、验证与测试

### Task 16: 数据库验证

- [ ] **Step 1: 执行 SQL 脚本**

```bash
# 连接到 MySQL 数据库
mysql -u root -p your_database < RuoYi-Vue/sql/V1.2.6__legal_basis_content_tables.sql
mysql -u root -p your_database < RuoYi-Vue/sql/V1.2.7__legal_basis_mock_data.sql
```

- [ ] **Step 2: 验证表结构**

```sql
-- 验证定性依据内容表
DESCRIBE sys_legal_basis_content;

-- 验证处理依据内容表
DESCRIBE sys_processing_basis_content;

-- 验证主表字段已删除
DESCRIBE sys_legal_basis;
-- 应该没有 basis_no, violation_type, issuing_authority 等字段

-- 验证数据
SELECT * FROM sys_legal_basis;
SELECT * FROM sys_legal_basis_content;
```

---

### Task 17: 后端验证

- [ ] **Step 1: 启动后端服务**

```bash
cd RuoYi-Vue
mvn spring-boot:run -pl ruoyi-admin
```

- [ ] **Step 2: 测试接口**

```bash
# 测试定性依据详情接口
curl http://localhost:8080/system/legalBasis/1

# 预期返回：
# {
#   "code": 200,
#   "data": {
#     "basis": { "basisId": 1, "title": "超出资质认可范围从事诊疗活动", ... },
#     "contents": [
#       { "contentId": 1, "basisId": 1, "label": "编号", "content": "001", "sortOrder": 1 },
#       ...
#     ]
#   }
# }
```

---

### Task 18: Android 验证

- [ ] **Step 1: 编译安装**

```bash
cd Ruoyi-Android-App
./gradlew assembleDebug
adb install -r app/build/outputs/apk/debug/app-debug.apk
```

- [ ] **Step 2: 测试流程**

1. 登录 App
2. 强制同步数据
3. 进入定性依据列表
4. 点击一条定性依据
5. 验证详情页显示所有内容行（编号、违法类型、颁发机构...）
6. 验证每个内容行都有复制按钮

---

## 五、实现顺序

1. **SQL 脚本** (Task 1)
2. **后端实体类** (Task 2, 3)
3. **后端 Mapper** (Task 4)
4. **后端 Service** (Task 5)
5. **后端 Controller** (Task 6)
6. **执行 SQL 脚本**
7. **Android 实体类** (Task 7)
8. **Android DAO** (Task 8)
9. **Android AppDatabase** (Task 9)
10. **Android API 解析** (Task 10)
11. **Android Repository** (Task 11)
12. **Android 同步** (Task 12)
13. **Android 详情页** (Task 13)
14. **Web 前端** (Task 14, 15)
15. **测试验证** (Task 16, 17, 18)
