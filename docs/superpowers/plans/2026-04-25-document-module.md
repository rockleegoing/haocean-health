# 文书模块实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现便捷执法的文书模块，包括后端API、前端Vue管理页面、Android端填写生成和全栈数据同步

**Architecture:** 后端采用 Spring Boot + MyBatis + MySQL，前端采用 Vue 2.6 + Element UI，Android 采用 Kotlin + MVVM + Room

**Tech Stack:** Spring Boot 4.0.3, MyBatis, MySQL, Vue 2.6, Element UI, Kotlin, Room, OkHttp

---

## 第一阶段：后端基础

### Task 1: 创建数据库表 SQL 脚本

**Files:**
- Create: `RuoYi-Vue/sql/V1.1.2__document_tables.sql`

```sql
-- 文书模板表
CREATE TABLE `sys_document_template` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `template_code` VARCHAR(64) NOT NULL COMMENT '模板编码(唯一，如WS001)',
    `template_name` VARCHAR(128) NOT NULL COMMENT '模板名称',
    `template_type` VARCHAR(32) DEFAULT NULL COMMENT '模板类型',
    `category` VARCHAR(32) DEFAULT NULL COMMENT '分类',
    `file_path` VARCHAR(256) DEFAULT NULL COMMENT '模板文件路径',
    `file_url` VARCHAR(256) DEFAULT NULL COMMENT '模板文件URL',
    `version` INT DEFAULT 1 COMMENT '版本号',
    `is_active` CHAR(1) DEFAULT '1' COMMENT '是否启用(0/1)',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志(0存在/1删除)',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_template_code` (`template_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书模板表';

-- 文书模板变量表
CREATE TABLE `sys_document_variable` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `template_id` BIGINT NOT NULL COMMENT '关联模板ID',
    `variable_name` VARCHAR(64) NOT NULL COMMENT '变量名(英文，如unitName)',
    `variable_label` VARCHAR(128) DEFAULT NULL COMMENT '变量标签(中文，如单位名称)',
    `variable_type` VARCHAR(32) DEFAULT 'TEXT' COMMENT '变量类型(TEXT/DATE/DATETIME/SELECT/RADIO/CHECKBOX/SIGNATURE/PHOTO)',
    `required` CHAR(1) DEFAULT '1' COMMENT '是否必填(0/1)',
    `default_value` VARCHAR(256) DEFAULT NULL COMMENT '默认值',
    `options` TEXT DEFAULT NULL COMMENT '选项JSON(下拉框/单选/复选)',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `max_length` INT DEFAULT NULL COMMENT '最大长度(文本)',
    PRIMARY KEY (`id`),
    KEY `idx_template_id` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书模板变量表';

-- 文书套组表
CREATE TABLE `sys_document_group` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `group_code` VARCHAR(64) NOT NULL COMMENT '套组编码',
    `group_name` VARCHAR(128) NOT NULL COMMENT '套组名称',
    `group_type` VARCHAR(32) DEFAULT NULL COMMENT '套组类型',
    `templates` TEXT DEFAULT NULL COMMENT '模板列表JSON([{code, required, order}])',
    `is_active` CHAR(1) DEFAULT '1' COMMENT '是否启用(0/1)',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志(0存在/1删除)',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
    PRIMARY KEY (`id`),
    KEY `uk_group_code` (`group_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书套组表';

-- 文书记录表
CREATE TABLE `sys_document_record` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    `document_no` VARCHAR(64) DEFAULT NULL COMMENT '文书编号',
    `template_id` BIGINT NOT NULL COMMENT '关联模板ID',
    `template_code` VARCHAR(64) DEFAULT NULL COMMENT '模板编码',
    `record_id` BIGINT DEFAULT NULL COMMENT '关联执法记录ID',
    `unit_id` BIGINT DEFAULT NULL COMMENT '关联单位ID',
    `variables` TEXT DEFAULT NULL COMMENT '变量值JSON',
    `file_path` VARCHAR(256) DEFAULT NULL COMMENT '生成文件路径',
    `file_url` VARCHAR(256) DEFAULT NULL COMMENT '生成文件URL',
    `signatures` TEXT DEFAULT NULL COMMENT '签名图片JSON',
    `status` VARCHAR(32) DEFAULT 'DRAFT' COMMENT '状态(草稿DRAFT/已生成GENERATED/已打印PRINTED)',
    `sync_status` CHAR(1) DEFAULT '0' COMMENT '同步状态(0未同步/1已同步)',
    `del_flag` CHAR(1) DEFAULT '0' COMMENT '删除标志(0存在/1删除)',
    `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
    `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
    `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
    `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_record_id` (`record_id`),
    KEY `idx_template_id` (`template_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文书记录表';
```

- [ ] **Step 1: 创建 SQL 脚本文件**

- [ ] **Step 2: 提交 SQL 脚本**

```bash
git add RuoYi-Vue/sql/V1.1.2__document_tables.sql
git commit -m "feat(database): 添加文书模块表结构"
```

---

### Task 2: 创建实体类

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDocumentTemplate.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDocumentVariable.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDocumentGroup.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDocumentRecord.java`

**参考模式**: `SysPhraseBook.java` - 相同的实体结构模式

- [ ] **Step 1: 创建 SysDocumentTemplate.java**

```java
package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 文书模板对象 sys_document_template
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysDocumentTemplate extends BaseEntity {
    private static final long serialVersionUID = 1L;

    /** 主键 */
    private Long id;

    /** 模板编码(唯一，如WS001) */
    @Excel(name = "模板编码")
    private String templateCode;

    /** 模板名称 */
    @Excel(name = "模板名称")
    private String templateName;

    /** 模板类型 */
    @Excel(name = "模板类型")
    private String templateType;

    /** 分类 */
    @Excel(name = "分类")
    private String category;

    /** 模板文件路径 */
    private String filePath;

    /** 模板文件URL */
    private String fileUrl;

    /** 版本号 */
    private Integer version;

    /** 是否启用(0/1) */
    @Excel(name = "是否启用")
    private String isActive;

    /** 删除标志(0存在/1删除) */
    private String delFlag;

    // getters and setters + toString()
}
```

- [ ] **Step 2: 创建 SysDocumentVariable.java** (变量表实体)

```java
package com.ruoyi.system.domain;

/**
 * 文书模板变量对象 sys_document_variable
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysDocumentVariable {
    private static final long serialVersionUID = 1L;

    private Long id;
    private Long templateId;
    private String variableName;
    private String variableLabel;
    private String variableType;
    private String required;
    private String defaultValue;
    private String options;
    private Integer sortOrder;
    private Integer maxLength;

    // getters and setters
}
```

- [ ] **Step 3: 创建 SysDocumentGroup.java** (套组表实体)

```java
package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.annotation.Excel;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 文书套组对象 sys_document_group
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysDocumentGroup extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    @Excel(name = "套组编码")
    private String groupCode;
    @Excel(name = "套组名称")
    private String groupName;
    @Excel(name = "套组类型")
    private String groupType;
    private String templates;
    @Excel(name = "是否启用")
    private String isActive;
    private String delFlag;

    // getters and setters + toString()
}
```

- [ ] **Step 4: 创建 SysDocumentRecord.java** (文书记录表实体)

```java
package com.ruoyi.system.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.ruoyi.common.core.domain.BaseEntity;

/**
 * 文书记录对象 sys_document_record
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public class SysDocumentRecord extends BaseEntity {
    private static final long serialVersionUID = 1L;

    private Long id;
    private String documentNo;
    private Long templateId;
    private String templateCode;
    private Long recordId;
    private Long unitId;
    private String variables;
    private String filePath;
    private String fileUrl;
    private String signatures;
    private String status;
    private String syncStatus;
    private String delFlag;

    // getters and setters + toString()
}
```

- [ ] **Step 5: 提交实体类**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysDocument*.java
git commit -m "feat(domain): 添加文书模块实体类"
```

---

### Task 3: 创建 Mapper 接口

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDocumentTemplateMapper.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDocumentVariableMapper.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDocumentGroupMapper.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDocumentRecordMapper.java`

**参考模式**: `SysPhraseBookMapper.java` - 相同的 Mapper 结构

- [ ] **Step 1: 创建 SysDocumentTemplateMapper.java**

```java
package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysDocumentTemplate;

/**
 * 文书模板Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface SysDocumentTemplateMapper {
    /**
     * 查询文书模板
     */
    public SysDocumentTemplate selectSysDocumentTemplateById(Long id);

    /**
     * 查询文书模板列表
     */
    public List<SysDocumentTemplate> selectSysDocumentTemplateList(SysDocumentTemplate sysDocumentTemplate);

    /**
     * 查询所有启用的模板(用于App同步)
     */
    public List<SysDocumentTemplate> selectAllSysDocumentTemplates();

    /**
     * 新增文书模板
     */
    public int insertSysDocumentTemplate(SysDocumentTemplate sysDocumentTemplate);

    /**
     * 修改文书模板
     */
    public int updateSysDocumentTemplate(SysDocumentTemplate sysDocumentTemplate);

    /**
     * 删除文书模板
     */
    public int deleteSysDocumentTemplateById(Long id);

    /**
     * 批量删除文书模板
     */
    public int deleteSysDocumentTemplateByIds(Long[] ids);
}
```

- [ ] **Step 2: 创建 SysDocumentVariableMapper.java**

```java
package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysDocumentVariable;

/**
 * 文书模板变量Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface SysDocumentVariableMapper {
    /**
     * 查询文书模板变量
     */
    public SysDocumentVariable selectSysDocumentVariableById(Long id);

    /**
     * 查询文书模板变量列表
     */
    public List<SysDocumentVariable> selectSysDocumentVariableList(SysDocumentVariable sysDocumentVariable);

    /**
     * 根据模板ID查询变量列表
     */
    public List<SysDocumentVariable> selectVariablesByTemplateId(Long templateId);

    /**
     * 新增文书模板变量
     */
    public int insertSysDocumentVariable(SysDocumentVariable sysDocumentVariable);

    /**
     * 修改文书模板变量
     */
    public int updateSysDocumentVariable(SysDocumentVariable sysDocumentVariable);

    /**
     * 删除文书模板变量
     */
    public int deleteSysDocumentVariableById(Long id);

    /**
     * 批量删除文书模板变量
     */
    public int deleteSysDocumentVariableByIds(Long[] ids);

    /**
     * 根据模板ID删除所有变量
     */
    public int deleteVariablesByTemplateId(Long templateId);
}
```

- [ ] **Step 3: 创建 SysDocumentGroupMapper.java**

```java
package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysDocumentGroup;

/**
 * 文书套组Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface SysDocumentGroupMapper {
    /**
     * 查询文书套组
     */
    public SysDocumentGroup selectSysDocumentGroupById(Long id);

    /**
     * 查询文书套组列表
     */
    public List<SysDocumentGroup> selectSysDocumentGroupList(SysDocumentGroup sysDocumentGroup);

    /**
     * 查询所有启用的套组(用于App同步)
     */
    public List<SysDocumentGroup> selectAllSysDocumentGroups();

    /**
     * 新增文书套组
     */
    public int insertSysDocumentGroup(SysDocumentGroup sysDocumentGroup);

    /**
     * 修改文书套组
     */
    public int updateSysDocumentGroup(SysDocumentGroup sysDocumentGroup);

    /**
     * 删除文书套组
     */
    public int deleteSysDocumentGroupById(Long id);

    /**
     * 批量删除文书套组
     */
    public int deleteSysDocumentGroupByIds(Long[] ids);
}
```

- [ ] **Step 4: 创建 SysDocumentRecordMapper.java**

```java
package com.ruoyi.system.mapper;

import java.util.List;
import com.ruoyi.system.domain.SysDocumentRecord;

/**
 * 文书记录Mapper接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface SysDocumentRecordMapper {
    /**
     * 查询文书记录
     */
    public SysDocumentRecord selectSysDocumentRecordById(Long id);

    /**
     * 查询文书记录列表
     */
    public List<SysDocumentRecord> selectSysDocumentRecordList(SysDocumentRecord sysDocumentRecord);

    /**
     * 根据执法记录ID查询文书列表
     */
    public List<SysDocumentRecord> selectRecordsByRecordId(Long recordId);

    /**
     * 新增文书记录
     */
    public int insertSysDocumentRecord(SysDocumentRecord sysDocumentRecord);

    /**
     * 修改文书记录
     */
    public int updateSysDocumentRecord(SysDocumentRecord sysDocumentRecord);

    /**
     * 删除文书记录
     */
    public int deleteSysDocumentRecordById(Long id);

    /**
     * 批量删除文书记录
     */
    public int deleteSysDocumentRecordByIds(Long[] ids);
}
```

- [ ] **Step 5: 提交 Mapper 接口**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysDocument*Mapper.java
git commit -m "feat(mapper): 添加文书模块Mapper接口"
```

---

### Task 4: 创建 Mapper XML 文件

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDocumentTemplateMapper.xml`
- Create: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDocumentVariableMapper.xml`
- Create: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDocumentGroupMapper.xml`
- Create: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDocumentRecordMapper.xml`

**参考模式**: `SysPhraseBookMapper.xml` - 相同的 XML 结构

- [ ] **Step 1: 创建 SysDocumentTemplateMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ruoyi.system.mapper.SysDocumentTemplateMapper">

    <resultMap type="SysDocumentTemplate" id="SysDocumentTemplateResult">
        <result property="id" column="id"/>
        <result property="templateCode" column="template_code"/>
        <result property="templateName" column="template_name"/>
        <result property="templateType" column="template_type"/>
        <result property="category" column="category"/>
        <result property="filePath" column="file_path"/>
        <result property="fileUrl" column="file_url"/>
        <result property="version" column="version"/>
        <result property="isActive" column="is_active"/>
        <result property="delFlag" column="del_flag"/>
        <result property="createBy" column="create_by"/>
        <result property="createTime" column="create_time"/>
        <result property="updateBy" column="update_by"/>
        <result property="updateTime" column="update_time"/>
        <result property="remark" column="remark"/>
    </resultMap>

    <sql id="selectSysDocumentTemplateVo">
        select id, template_code, template_name, template_type, category, file_path, file_url,
               version, is_active, del_flag, create_by, create_time, update_by, update_time, remark
        from sys_document_template
    </sql>

    <select id="selectSysDocumentTemplateList" parameterType="SysDocumentTemplate" resultMap="SysDocumentTemplateResult">
        <include refid="selectSysDocumentTemplateVo"/>
        <where>
            del_flag = '0'
            <if test="templateCode != null and templateCode != ''">
                and template_code = #{templateCode}
            </if>
            <if test="templateName != null and templateName != ''">
                and template_name like concat('%', #{templateName}, '%')
            </if>
            <if test="templateType != null and templateType != ''">
                and template_type = #{templateType}
            </if>
            <if test="isActive != null and isActive != ''">
                and is_active = #{isActive}
            </if>
        </where>
        order by id asc
    </select>

    <select id="selectSysDocumentTemplateById" parameterType="Long" resultMap="SysDocumentTemplateResult">
        <include refid="selectSysDocumentTemplateVo"/>
        where id = #{id}
    </select>

    <select id="selectAllSysDocumentTemplates" resultMap="SysDocumentTemplateResult">
        <include refid="selectSysDocumentTemplateVo"/>
        where del_flag = '0' and is_active = '1'
        order by id asc
    </select>

    <insert id="insertSysDocumentTemplate" parameterType="SysDocumentTemplate" useGeneratedKeys="true" keyProperty="id">
        insert into sys_document_template
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="templateCode != null and templateCode != ''">template_code,</if>
            <if test="templateName != null and templateName != ''">template_name,</if>
            <if test="templateType != null">template_type,</if>
            <if test="category != null">category,</if>
            <if test="filePath != null">file_path,</if>
            <if test="fileUrl != null">file_url,</if>
            <if test="version != null">version,</if>
            <if test="isActive != null">is_active,</if>
            <if test="delFlag != null">del_flag,</if>
            <if test="createBy != null">create_by,</if>
            <if test="createTime != null">create_time,</if>
            <if test="remark != null">remark,</if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="templateCode != null and templateCode != ''">#{templateCode},</if>
            <if test="templateName != null and templateName != ''">#{templateName},</if>
            <if test="templateType != null">#{templateType},</if>
            <if test="category != null">#{category},</if>
            <if test="filePath != null">#{filePath},</if>
            <if test="fileUrl != null">#{fileUrl},</if>
            <if test="version != null">#{version},</if>
            <if test="isActive != null">#{isActive},</if>
            <if test="delFlag != null">#{delFlag},</if>
            <if test="createBy != null">#{createBy},</if>
            <if test="createTime != null">#{createTime},</if>
            <if test="remark != null">#{remark},</if>
        </trim>
    </insert>

    <update id="updateSysDocumentTemplate" parameterType="SysDocumentTemplate">
        update sys_document_template
        <trim prefix="SET" suffixOverrides=",">
            <if test="templateCode != null and templateCode != ''">template_code = #{templateCode},</if>
            <if test="templateName != null and templateName != ''">template_name = #{templateName},</if>
            <if test="templateType != null">template_type = #{templateType},</if>
            <if test="category != null">category = #{category},</if>
            <if test="filePath != null">file_path = #{filePath},</if>
            <if test="fileUrl != null">file_url = #{fileUrl},</if>
            <if test="version != null">version = #{version},</if>
            <if test="isActive != null">is_active = #{isActive},</if>
            <if test="delFlag != null">del_flag = #{delFlag},</if>
            <if test="updateBy != null">update_by = #{updateBy},</if>
            <if test="updateTime != null">update_time = #{updateTime},</if>
            <if test="remark != null">remark = #{remark},</if>
        </trim>
        where id = #{id}
    </update>

    <delete id="deleteSysDocumentTemplateById" parameterType="Long">
        update sys_document_template set del_flag = '1' where id = #{id}
    </delete>

    <delete id="deleteSysDocumentTemplateByIds" parameterType="String">
        update sys_document_template set del_flag = '1' where id in
        <foreach item="id" collection="array" open="(" separator="," close=")">
            #{id}
        </foreach>
    </delete>

</mapper>
```

- [ ] **Step 2: 创建 SysDocumentVariableMapper.xml**

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<!DOCTYPE mapper
PUBLIC "-//mybatis.org//DTD Mapper 3.0//EN"
"http://mybatis.org/dtd/mybatis-3-mapper.dtd">
<mapper namespace="com.ruoyi.system.mapper.SysDocumentVariableMapper">

    <resultMap type="SysDocumentVariable" id="SysDocumentVariableResult">
        <result property="id" column="id"/>
        <result property="templateId" column="template_id"/>
        <result property="variableName" column="variable_name"/>
        <result property="variableLabel" column="variable_label"/>
        <result property="variableType" column="variable_type"/>
        <result property="required" column="required"/>
        <result property="defaultValue" column="default_value"/>
        <result property="options" column="options"/>
        <result property="sortOrder" column="sort_order"/>
        <result property="maxLength" column="max_length"/>
    </resultMap>

    <sql id="selectSysDocumentVariableVo">
        select id, template_id, variable_name, variable_label, variable_type, required,
               default_value, options, sort_order, max_length
        from sys_document_variable
    </sql>

    <select id="selectSysDocumentVariableList" parameterType="SysDocumentVariable" resultMap="SysDocumentVariableResult">
        <include refid="selectSysDocumentVariableVo"/>
        <where>
            <if test="templateId != null">template_id = #{templateId},</if>
            <if test="variableName != null and variableName != ''">
                and variable_name = #{variableName}
            </if>
        </where>
        order by sort_order asc, id asc
    </select>

    <select id="selectSysDocumentVariableById" parameterType="Long" resultMap="SysDocumentVariableResult">
        <include refid="selectSysDocumentVariableVo"/>
        where id = #{id}
    </select>

    <select id="selectVariablesByTemplateId" parameterType="Long" resultMap="SysDocumentVariableResult">
        <include refid="selectSysDocumentVariableVo"/>
        where template_id = #{templateId}
        order by sort_order asc, id asc
    </select>

    <insert id="insertSysDocumentVariable" parameterType="SysDocumentVariable" useGeneratedKeys="true" keyProperty="id">
        insert into sys_document_variable
        <trim prefix="(" suffix=")" suffixOverrides=",">
            <if test="templateId != null">template_id,</if>
            <if test="variableName != null and variableName != ''">variable_name,</if>
            <if test="variableLabel != null">variable_label,</if>
            <if test="variableType != null">variable_type,</if>
            <if test="required != null">required,</if>
            <if test="defaultValue != null">default_value,</if>
            <if test="options != null">options,</if>
            <if test="sortOrder != null">sort_order,</if>
            <if test="maxLength != null">max_length,</if>
        </trim>
        <trim prefix="values (" suffix=")" suffixOverrides=",">
            <if test="templateId != null">#{templateId},</if>
            <if test="variableName != null and variableName != ''">#{variableName},</if>
            <if test="variableLabel != null">#{variableLabel},</if>
            <if test="variableType != null">#{variableType},</if>
            <if test="required != null">#{required},</if>
            <if test="defaultValue != null">#{defaultValue},</if>
            <if test="options != null">#{options},</if>
            <if test="sortOrder != null">#{sortOrder},</if>
            <if test="maxLength != null">#{maxLength},</if>
        </trim>
    </insert>

    <update id="updateSysDocumentVariable" parameterType="SysDocumentVariable">
        update sys_document_variable
        <trim prefix="SET" suffixOverrides=",">
            <if test="templateId != null">template_id = #{templateId},</if>
            <if test="variableName != null and variableName != ''">variable_name = #{variableName},</if>
            <if test="variableLabel != null">variable_label = #{variableLabel},</if>
            <if test="variableType != null">variable_type = #{variableType},</if>
            <if test="required != null">required = #{required},</if>
            <if test="defaultValue != null">default_value = #{defaultValue},</if>
            <if test="options != null">options = #{options},</if>
            <if test="sortOrder != null">sort_order = #{sortOrder},</if>
            <if test="maxLength != null">max_length = #{maxLength},</if>
        </trim>
        where id = #{id}
    </update>

    <delete id="deleteSysDocumentVariableById" parameterType="Long">
        delete from sys_document_variable where id = #{id}
    </delete>

    <delete id="deleteSysDocumentVariableByIds" parameterType="String">
        delete from sys_document_variable where id in
        <foreach item="id" collection="array" open="(" separator="," close=")">
            #{id}
        </foreach>
    </delete>

    <delete id="deleteVariablesByTemplateId" parameterType="Long">
        delete from sys_document_variable where template_id = #{templateId}
    </delete>

</mapper>
```

- [ ] **Step 3: 创建 SysDocumentGroupMapper.xml 和 SysDocumentRecordMapper.xml** (类似模式)

- [ ] **Step 4: 提交 Mapper XML 文件**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysDocument*Mapper.xml
git commit -m "feat(mapper): 添加文书模块Mapper XML"
```

---

### Task 5: 创建 Service 接口和实现

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysDocumentService.java`
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDocumentServiceImpl.java`

**参考模式**: `ISysSupervisionService.java` 和 `SysSupervisionServiceImpl.java`

- [ ] **Step 1: 创建 ISysDocumentService.java**

```java
package com.ruoyi.system.service;

import java.util.List;
import com.ruoyi.system.domain.SysDocumentTemplate;
import com.ruoyi.system.domain.SysDocumentVariable;
import com.ruoyi.system.domain.SysDocumentGroup;
import com.ruoyi.system.domain.SysDocumentRecord;

/**
 * 文书模块Service接口
 *
 * @author ruoyi
 * @date 2026-04-25
 */
public interface ISysDocumentService {

    // ==================== 文书模板 ====================

    /**
     * 查询文书模板
     */
    public SysDocumentTemplate selectSysDocumentTemplateById(Long id);

    /**
     * 查询文书模板列表
     */
    public List<SysDocumentTemplate> selectSysDocumentTemplateList(SysDocumentTemplate sysDocumentTemplate);

    /**
     * 查询所有启用的模板(用于App同步)
     */
    public List<SysDocumentTemplate> selectAllSysDocumentTemplates();

    /**
     * 新增文书模板
     */
    public int insertSysDocumentTemplate(SysDocumentTemplate sysDocumentTemplate);

    /**
     * 修改文书模板
     */
    public int updateSysDocumentTemplate(SysDocumentTemplate sysDocumentTemplate);

    /**
     * 删除文书模板
     */
    public int deleteSysDocumentTemplateById(Long id);

    /**
     * 批量删除文书模板
     */
    public int deleteSysDocumentTemplateByIds(Long[] ids);

    // ==================== 文书模板变量 ====================

    /**
     * 根据模板ID查询变量列表
     */
    public List<SysDocumentVariable> selectVariablesByTemplateId(Long templateId);

    /**
     * 新增文书模板变量
     */
    public int insertSysDocumentVariable(SysDocumentVariable sysDocumentVariable);

    /**
     * 修改文书模板变量
     */
    public int updateSysDocumentVariable(SysDocumentVariable sysDocumentVariable);

    /**
     * 删除文书模板变量
     */
    public int deleteSysDocumentVariableById(Long id);

    // ==================== 文书套组 ====================

    /**
     * 查询文书套组
     */
    public SysDocumentGroup selectSysDocumentGroupById(Long id);

    /**
     * 查询文书套组列表
     */
    public List<SysDocumentGroup> selectSysDocumentGroupList(SysDocumentGroup sysDocumentGroup);

    /**
     * 查询所有启用的套组(用于App同步)
     */
    public List<SysDocumentGroup> selectAllSysDocumentGroups();

    /**
     * 新增文书套组
     */
    public int insertSysDocumentGroup(SysDocumentGroup sysDocumentGroup);

    /**
     * 修改文书套组
     */
    public int updateSysDocumentGroup(SysDocumentGroup sysDocumentGroup);

    /**
     * 删除文书套组
     */
    public int deleteSysDocumentGroupById(Long id);

    // ==================== 文书记录 ====================

    /**
     * 查询文书记录
     */
    public SysDocumentRecord selectSysDocumentRecordById(Long id);

    /**
     * 查询文书记录列表
     */
    public List<SysDocumentRecord> selectSysDocumentRecordList(SysDocumentRecord sysDocumentRecord);

    /**
     * 根据执法记录ID查询文书列表
     */
    public List<SysDocumentRecord> selectRecordsByRecordId(Long recordId);

    /**
     * 新增文书记录
     */
    public int insertSysDocumentRecord(SysDocumentRecord sysDocumentRecord);

    /**
     * 修改文书记录
     */
    public int updateSysDocumentRecord(SysDocumentRecord sysDocumentRecord);

    /**
     * 删除文书记录
     */
    public int deleteSysDocumentRecordById(Long id);
}
```

- [ ] **Step 2: 创建 SysDocumentServiceImpl.java** (实现所有接口方法)

- [ ] **Step 3: 提交 Service 文件**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysDocumentService.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysDocumentServiceImpl.java
git commit -m "feat(service): 添加文书模块Service"
```

---

### Task 6: 创建 Controller

**Files:**
- Create: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysDocumentController.java`

**参考模式**: `SysSupervisionController.java`

- [ ] **Step 1: 创建 SysDocumentController.java**

```java
package com.ruoyi.web.controller.system;

import java.util.List;
import com.ruoyi.common.annotation.Anonymous;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.system.domain.SysDocumentTemplate;
import com.ruoyi.system.domain.SysDocumentVariable;
import com.ruoyi.system.domain.SysDocumentGroup;
import com.ruoyi.system.domain.SysDocumentRecord;
import com.ruoyi.system.service.ISysDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 文书模块管理接口
 */
@RestController
@RequestMapping("/api/admin/document")
public class SysDocumentController extends BaseController {

    @Autowired
    private ISysDocumentService sysDocumentService;

    // ==================== 文书模板管理 ====================

    /**
     * 获取文书模板列表
     */
    @GetMapping("/template/list")
    public AjaxResult listTemplate(SysDocumentTemplate template) {
        List<SysDocumentTemplate> list = sysDocumentService.selectSysDocumentTemplateList(template);
        return AjaxResult.success(list);
    }

    /**
     * 获取文书模板详情(含变量)
     */
    @GetMapping("/template/{id}")
    public AjaxResult getTemplate(@PathVariable("id") Long id) {
        SysDocumentTemplate template = sysDocumentService.selectSysDocumentTemplateById(id);
        List<SysDocumentVariable> variables = sysDocumentService.selectVariablesByTemplateId(id);
        return AjaxResult.success()
                .put("template", template)
                .put("variables", variables);
    }

    /**
     * 新增文书模板
     */
    @PostMapping("/template")
    public AjaxResult add(@RequestBody SysDocumentTemplate template) {
        return AjaxResult.success(sysDocumentService.insertSysDocumentTemplate(template));
    }

    /**
     * 修改文书模板
     */
    @PutMapping("/template")
    public AjaxResult edit(@RequestBody SysDocumentTemplate template) {
        return AjaxResult.success(sysDocumentService.updateSysDocumentTemplate(template));
    }

    /**
     * 删除文书模板
     */
    @DeleteMapping("/template/{id}")
    public AjaxResult remove(@PathVariable("id") Long id) {
        return AjaxResult.success(sysDocumentService.deleteSysDocumentTemplateById(id));
    }

    // ==================== 文书模板变量管理 ====================

    /**
     * 新增文书模板变量
     */
    @PostMapping("/variable")
    public AjaxResult addVariable(@RequestBody SysDocumentVariable variable) {
        return AjaxResult.success(sysDocumentService.insertSysDocumentVariable(variable));
    }

    /**
     * 修改文书模板变量
     */
    @PutMapping("/variable")
    public AjaxResult editVariable(@RequestBody SysDocumentVariable variable) {
        return AjaxResult.success(sysDocumentService.updateSysDocumentVariable(variable));
    }

    /**
     * 删除文书模板变量
     */
    @DeleteMapping("/variable/{id}")
    public AjaxResult removeVariable(@PathVariable("id") Long id) {
        return AjaxResult.success(sysDocumentService.deleteSysDocumentVariableById(id));
    }

    // ==================== 文书套组管理 ====================

    /**
     * 获取文书套组列表
     */
    @GetMapping("/group/list")
    public AjaxResult listGroup(SysDocumentGroup group) {
        List<SysDocumentGroup> list = sysDocumentService.selectSysDocumentGroupList(group);
        return AjaxResult.success(list);
    }

    /**
     * 获取文书套组详情
     */
    @GetMapping("/group/{id}")
    public AjaxResult getGroup(@PathVariable("id") Long id) {
        return AjaxResult.success(sysDocumentService.selectSysDocumentGroupById(id));
    }

    /**
     * 新增文书套组
     */
    @PostMapping("/group")
    public AjaxResult addGroup(@RequestBody SysDocumentGroup group) {
        return AjaxResult.success(sysDocumentService.insertSysDocumentGroup(group));
    }

    /**
     * 修改文书套组
     */
    @PutMapping("/group")
    public AjaxResult editGroup(@RequestBody SysDocumentGroup group) {
        return AjaxResult.success(sysDocumentService.updateSysDocumentGroup(group));
    }

    /**
     * 删除文书套组
     */
    @DeleteMapping("/group/{id}")
    public AjaxResult removeGroup(@PathVariable("id") Long id) {
        return AjaxResult.success(sysDocumentService.deleteSysDocumentGroupById(id));
    }

    // ==================== 文书记录管理 ====================

    /**
     * 获取文书记录列表
     */
    @GetMapping("/record/list")
    public AjaxResult listRecord(SysDocumentRecord record) {
        List<SysDocumentRecord> list = sysDocumentService.selectSysDocumentRecordList(record);
        return AjaxResult.success(list);
    }

    /**
     * 获取文书记录详情
     */
    @GetMapping("/record/{id}")
    public AjaxResult getRecord(@PathVariable("id") Long id) {
        return AjaxResult.success(sysDocumentService.selectSysDocumentRecordById(id));
    }

    /**
     * 新增文书记录
     */
    @PostMapping("/record")
    public AjaxResult addRecord(@RequestBody SysDocumentRecord record) {
        return AjaxResult.success(sysDocumentService.insertSysDocumentRecord(record));
    }

    /**
     * 修改文书记录
     */
    @PutMapping("/record")
    public AjaxResult editRecord(@RequestBody SysDocumentRecord record) {
        return AjaxResult.success(sysDocumentService.updateSysDocumentRecord(record));
    }

    /**
     * 删除文书记录
     */
    @DeleteMapping("/record/{id}")
    public AjaxResult removeRecord(@PathVariable("id") Long id) {
        return AjaxResult.success(sysDocumentService.deleteSysDocumentRecordById(id));
    }

    // ==================== 移动端API(匿名访问) ====================

    /**
     * 同步文书模板(下行)
     */
    @Anonymous
    @GetMapping("/sync/template")
    public AjaxResult syncTemplates() {
        List<SysDocumentTemplate> templates = sysDocumentService.selectAllSysDocumentTemplates();
        return AjaxResult.success(templates);
    }

    /**
     * 同步文书套组(下行)
     */
    @Anonymous
    @GetMapping("/sync/group")
    public AjaxResult syncGroups() {
        List<SysDocumentGroup> groups = sysDocumentService.selectAllSysDocumentGroups();
        return AjaxResult.success(groups);
    }

    /**
     * 获取模板详情(含变量，用于移动端)
     */
    @Anonymous
    @GetMapping("/mobile/template/{id}")
    public AjaxResult getMobileTemplate(@PathVariable("id") Long id) {
        SysDocumentTemplate template = sysDocumentService.selectSysDocumentTemplateById(id);
        List<SysDocumentVariable> variables = sysDocumentService.selectVariablesByTemplateId(id);
        return AjaxResult.success()
                .put("template", template)
                .put("variables", variables);
    }

    /**
     * 上传生成的文书(上行)
     */
    @Anonymous
    @PostMapping("/mobile/upload")
    public AjaxResult uploadDocument(@RequestBody SysDocumentRecord record) {
        return AjaxResult.success(sysDocumentService.insertSysDocumentRecord(record));
    }
}
```

- [ ] **Step 2: 提交 Controller**

```bash
git add RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysDocumentController.java
git commit -m "feat(controller): 添加文书模块Controller"
```

---

## 第二阶段：前端 Vue 管理页面

### Task 7: 创建前端 API 文件

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/api/system/document.js`

**参考模式**: `supervision.js`

- [ ] **Step 1: 创建 document.js**

```javascript
import request from '@/utils/request'

// ==================== 文书模板管理 API ====================

// 查询文书模板列表
export function listTemplate(query) {
  return request({
    url: '/api/admin/document/template/list',
    method: 'get',
    params: query
  })
}

// 查询文书模板详情
export function getTemplate(id) {
  return request({
    url: '/api/admin/document/template/' + id,
    method: 'get'
  })
}

// 新增文书模板
export function addTemplate(data) {
  return request({
    url: '/api/admin/document/template',
    method: 'post',
    data: data
  })
}

// 修改文书模板
export function updateTemplate(data) {
  return request({
    url: '/api/admin/document/template',
    method: 'put',
    data: data
  })
}

// 删除文书模板
export function delTemplate(id) {
  return request({
    url: '/api/admin/document/template/' + id,
    method: 'delete'
  })
}

// ==================== 文书模板变量管理 API ====================

// 新增变量
export function addVariable(data) {
  return request({
    url: '/api/admin/document/variable',
    method: 'post',
    data: data
  })
}

// 修改变量
export function updateVariable(data) {
  return request({
    url: '/api/admin/document/variable',
    method: 'put',
    data: data
  })
}

// 删除变量
export function delVariable(id) {
  return request({
    url: '/api/admin/document/variable/' + id,
    method: 'delete'
  })
}

// ==================== 文书套组管理 API ====================

// 查询文书套组列表
export function listGroup(query) {
  return request({
    url: '/api/admin/document/group/list',
    method: 'get',
    params: query
  })
}

// 查询文书套组详情
export function getGroup(id) {
  return request({
    url: '/api/admin/document/group/' + id,
    method: 'get'
  })
}

// 新增文书套组
export function addGroup(data) {
  return request({
    url: '/api/admin/document/group',
    method: 'post',
    data: data
  })
}

// 修改文书套组
export function updateGroup(data) {
  return request({
    url: '/api/admin/document/group',
    method: 'put',
    data: data
  })
}

// 删除文书套组
export function delGroup(id) {
  return request({
    url: '/api/admin/document/group/' + id,
    method: 'delete'
  })
}

// ==================== 文书记录管理 API ====================

// 查询文书记录列表
export function listRecord(query) {
  return request({
    url: '/api/admin/document/record/list',
    method: 'get',
    params: query
  })
}

// 查询文书记录详情
export function getRecord(id) {
  return request({
    url: '/api/admin/document/record/' + id,
    method: 'get'
  })
}

// 新增文书记录
export function addRecord(data) {
  return request({
    url: '/api/admin/document/record',
    method: 'post',
    data: data
  })
}

// 修改文书记录
export function updateRecord(data) {
  return request({
    url: '/api/admin/document/record',
    method: 'put',
    data: data
  })
}

// 删除文书记录
export function delRecord(id) {
  return request({
    url: '/api/admin/document/record/' + id,
    method: 'delete'
  })
}
```

- [ ] **Step 2: 提交 API 文件**

```bash
git add RuoYi-Vue/ruoyi-ui/src/api/system/document.js
git commit -m "feat(frontend): 添加文书模块API"
```

---

### Task 8: 创建文书模板管理页面

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/views/system/document/template/index.vue`
- Create: `RuoYi-Vue/ruoyi-ui/src/views/system/document/template/edit.vue`

**参考模式**: `supervision/index.vue`

- [ ] **Step 1: 创建模板列表页面 index.vue**

```vue
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" :inline="true">
      <el-form-item label="模板名称" prop="templateName">
        <el-input v-model="queryParams.templateName" placeholder="请输入模板名称" clearable @keyup.enter="handleQuery" />
      </el-form-item>
      <el-form-item label="模板类型" prop="templateType">
        <el-select v-model="queryParams.templateType" placeholder="请选择" clearable>
          <el-option label="现场笔录类" value="SCENE_RECORD" />
          <el-option label="询问笔录类" value="INQUIRY_RECORD" />
          <el-option label="行政强制类" value="ADMIN_FORCE" />
          <el-option label="行政处罚类" value="ADMIN_PUNISH" />
          <el-option label="其他" value="OTHER" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="Search" @click="handleQuery">搜索</el-button>
        <el-button icon="Refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="Plus" @click="handleAdd">新增</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="templateList">
      <el-table-column label="模板编码" align="center" prop="templateCode" width="120" />
      <el-table-column label="模板名称" align="center" prop="templateName" />
      <el-table-column label="模板类型" align="center" prop="templateType" width="120">
        <template #default="scope">
          <span>{{ getTypeName(scope.row.templateType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="版本" align="center" prop="version" width="80" />
      <el-table-column label="状态" align="center" prop="isActive" width="80">
        <template #default="scope">
          <el-tag v-if="scope.row.isActive === '1'" type="success">启用</el-tag>
          <el-tag v-else type="danger">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="200">
        <template #default="scope">
          <el-button link type="primary" @click="handleUpdate(scope.row)">编辑</el-button>
          <el-button link type="primary" @click="handleVariable(scope.row)">变量配置</el-button>
          <el-button link type="danger" @click="handleDelete(scope.row)">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" v-model:page="queryParams.pageNum" v-model:limit="queryParams.pageSize" @pagination="getList" />
  </div>
</template>

<script setup>
import { listTemplate, delTemplate } from '@/api/system/document'

const loading = ref(true)
const templateList = ref([])
const total = ref(0)
const queryParams = ref({
  pageNum: 1,
  pageSize: 10,
  templateName: '',
  templateType: ''
})

const getTypeName = (type) => {
  const map = {
    'SCENE_RECORD': '现场笔录类',
    'INQUIRY_RECORD': '询问笔录类',
    'ADMIN_FORCE': '行政强制类',
    'ADMIN_PUNISH': '行政处罚类',
    'OTHER': '其他'
  }
  return map[type] || type
}

const getList = async () => {
  loading.value = true
  try {
    const res = await listTemplate(queryParams.value)
    templateList.value = res.data || []
    total.value = res.total || 0
  } finally {
    loading.value = false
  }
}

const handleQuery = () => {
  queryParams.value.pageNum = 1
  getList()
}

const resetQuery = () => {
  queryParams.value = { pageNum: 1, pageSize: 10, templateName: '', templateType: '' }
  handleQuery()
}

const handleAdd = () => {
  // 跳转到编辑页面
}

const handleUpdate = (row) => {
  // 跳转到编辑页面
}

const handleVariable = (row) => {
  // 跳转到变量配置页面
}

const handleDelete = async (row) => {
  await ElMessageBox.confirm('是否确认删除?')
  await delTemplate(row.id)
  getList()
}

getList()
</script>
```

- [ ] **Step 2: 创建模板编辑页面 edit.vue** (包含表单和变量配置)

- [ ] **Step 3: 提交模板管理页面**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/system/document/template/
git commit -m "feat(frontend): 添加文书模板管理页面"
```

---

### Task 9: 创建文书套组管理页面

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/views/system/document/group/index.vue`
- Create: `RuoYi-Vue/ruoyi-ui/src/views/system/document/group/edit.vue`

- [ ] **Step 1: 创建套组列表页面 index.vue** (类似模板列表结构)

- [ ] **Step 2: 创建套组编辑页面 edit.vue** (包含模板选择和排序)

- [ ] **Step 3: 提交套组管理页面**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/system/document/group/
git commit -m "feat(frontend): 添加文书套组管理页面"
```

---

## 第三阶段：Android 端

### Task 10: 创建数据库 Entity 和 DAO

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/DocumentTemplateEntity.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/DocumentVariableEntity.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/DocumentGroupEntity.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/DocumentTemplateDao.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/DocumentVariableDao.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/DocumentGroupDao.kt`

**参考模式**: `SupervisionCategoryEntity.kt`, `SupervisionCategoryDao.kt`

- [ ] **Step 1: 创建 DocumentTemplateEntity.kt**

```kotlin
package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 文书模板实体
 */
@Entity(tableName = "document_template")
data class DocumentTemplateEntity(
    @PrimaryKey
    val id: Long,
    val templateCode: String,
    val templateName: String,
    val templateType: String?,
    val category: String?,
    val filePath: String?,
    val fileUrl: String?,
    val version: Int = 1,
    val isActive: String = "1",
    val syncTime: Long = System.currentTimeMillis()
)
```

- [ ] **Step 2: 创建 DocumentVariableEntity.kt**

```kotlin
package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 文书模板变量实体
 */
@Entity(tableName = "document_variable")
data class DocumentVariableEntity(
    @PrimaryKey
    val id: Long,
    val templateId: Long,
    val variableName: String,
    val variableLabel: String?,
    val variableType: String = "TEXT",
    val required: String = "1",
    val defaultValue: String?,
    val options: String?,
    val sortOrder: Int = 0,
    val maxLength: Int?
)
```

- [ ] **Step 3: 创建 DocumentGroupEntity.kt**

```kotlin
package com.ruoyi.app.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * 文书套组实体
 */
@Entity(tableName = "document_group")
data class DocumentGroupEntity(
    @PrimaryKey
    val id: Long,
    val groupCode: String,
    val groupName: String,
    val groupType: String?,
    val templates: String?,
    val isActive: String = "1",
    val syncTime: Long = System.currentTimeMillis()
)
```

- [ ] **Step 4: 创建 DAO 接口 DocumentTemplateDao.kt, DocumentVariableDao.kt, DocumentGroupDao.kt**

```kotlin
package com.ruoyi.app.data.database.dao

import androidx.room.*
import com.ruoyi.app.data.database.entity.DocumentTemplateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentTemplateDao {
    @Query("SELECT * FROM document_template WHERE isActive = '1' ORDER BY id ASC")
    fun getActiveTemplates(): Flow<List<DocumentTemplateEntity>>

    @Query("SELECT * FROM document_template WHERE id = :id")
    suspend fun getTemplateById(id: Long): DocumentTemplateEntity?

    @Query("SELECT * FROM document_template WHERE templateCode = :code")
    suspend fun getTemplateByCode(code: String): DocumentTemplateEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(template: DocumentTemplateEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(templates: List<DocumentTemplateEntity>)

    @Delete
    suspend fun delete(template: DocumentTemplateEntity)

    @Query("DELETE FROM document_template")
    suspend fun deleteAll()
}
```

- [ ] **Step 5: 在 AppDatabase.kt 中添加新的 DAO 和表**

```kotlin
@Database(entities = [
    // ... existing entities
    DocumentTemplateEntity::class,
    DocumentVariableEntity::class,
    DocumentGroupEntity::class
], version = X, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun documentTemplateDao(): DocumentTemplateDao
    abstract fun documentVariableDao(): DocumentVariableDao
    abstract fun documentGroupDao(): DocumentGroupDao
}
```

- [ ] **Step 6: 提交 Android 数据库文件**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/entity/Document*Entity.kt
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/dao/Document*Dao.kt
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/data/database/AppDatabase.kt
git commit -m "feat(android): 添加文书模块数据库结构"
```

---

### Task 11: 创建 Android Model 类

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/model/DocumentTemplate.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/model/DocumentVariable.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/model/DocumentGroup.kt`

**参考模式**: `SupervisionModels.kt`

- [ ] **Step 1: 创建 DocumentTemplate.kt**

```kotlin
package com.ruoyi.app.feature.document.model

data class DocumentTemplate(
    val id: Long,
    val templateCode: String,
    val templateName: String,
    val templateType: String?,
    val category: String?,
    val filePath: String?,
    val fileUrl: String?,
    val version: Int = 1,
    val isActive: String = "1"
)
```

- [ ] **Step 2: 创建 DocumentVariable.kt**

```kotlin
package com.ruoyi.app.feature.document.model

data class DocumentVariable(
    val id: Long,
    val templateId: Long,
    val variableName: String,
    val variableLabel: String?,
    val variableType: String = "TEXT",
    val required: String = "1",
    val defaultValue: String?,
    val options: String?,
    val sortOrder: Int = 0,
    val maxLength: Int?
)

object VariableType {
    const val TEXT = "TEXT"
    const val DATE = "DATE"
    const val DATETIME = "DATETIME"
    const val SELECT = "SELECT"
    const val RADIO = "RADIO"
    const val CHECKBOX = "CHECKBOX"
    const val SIGNATURE = "SIGNATURE"
    const val PHOTO = "PHOTO"
}
```

- [ ] **Step 3: 创建 DocumentGroup.kt**

```kotlin
package com.ruoyi.app.feature.document.model

data class DocumentGroup(
    val id: Long,
    val groupCode: String,
    val groupName: String,
    val groupType: String?,
    val templates: String?,
    val isActive: String = "1"
)

data class GroupTemplate(
    val code: String,
    val required: Boolean,
    val order: Int
)
```

- [ ] **Step 4: 提交 Model 类**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/model/
git commit -m "feat(android): 添加文书模块Model类"
```

---

### Task 12: 创建 Android API 和 Repository

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/api/DocumentApi.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/repository/DocumentRepository.kt`

**参考模式**: `SupervisionApi.kt`, `SupervisionRepository.kt`

- [ ] **Step 1: 创建 DocumentApi.kt**

```kotlin
package com.ruoyi.app.feature.document.api

import com.ruoyi.app.api.ConfigApi
import com.ruoyi.app.feature.document.model.DocumentTemplate
import com.ruoyi.app.feature.document.model.DocumentVariable
import com.ruoyi.app.feature.document.model.DocumentGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object DocumentApi {
    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    /**
     * 同步文书模板(下行)
     */
    suspend fun syncTemplates(): List<DocumentTemplate> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/api/admin/document/sync/template")
            .get()
            .build()
        val response = client.newCall(request).execute()
        parseTemplateList(response.body?.string() ?: "")
    }

    /**
     * 同步文书套组(下行)
     */
    suspend fun syncGroups(): List<DocumentGroup> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/api/admin/document/sync/group")
            .get()
            .build()
        val response = client.newCall(request).execute()
        parseGroupList(response.body?.string() ?: "")
    }

    /**
     * 获取模板详情(含变量)
     */
    suspend fun getTemplateDetail(id: Long): Pair<DocumentTemplate?, List<DocumentVariable>> = withContext(Dispatchers.IO) {
        val request = Request.Builder()
            .url("${ConfigApi.baseUrl}/api/admin/document/mobile/template/$id")
            .get()
            .build()
        val response = client.newCall(request).execute()
        parseTemplateDetail(response.body?.string() ?: "")
    }

    private fun parseTemplateList(json: String): List<DocumentTemplate> {
        return try {
            val obj = JSONObject(json)
            val dataArray = obj.optJSONArray("data") ?: JSONArray()
            val templates = mutableListOf<DocumentTemplate>()
            for (i in 0 until dataArray.length()) {
                val o = dataArray.getJSONObject(i)
                templates.add(DocumentTemplate(
                    id = o.optLong("id", 0),
                    templateCode = o.optString("templateCode", ""),
                    templateName = o.optString("templateName", ""),
                    templateType = o.optString("templateType", null),
                    category = o.optString("category", null),
                    filePath = o.optString("filePath", null),
                    fileUrl = o.optString("fileUrl", null),
                    version = o.optInt("version", 1),
                    isActive = o.optString("isActive", "1")
                ))
            }
            templates
        } catch (e: Exception) { emptyList() }
    }

    private fun parseGroupList(json: String): List<DocumentGroup> {
        // 类似解析
        return emptyList()
    }

    private fun parseTemplateDetail(json: String): Pair<DocumentTemplate?, List<DocumentVariable>> {
        // 解析详情和变量
        return Pair(null, emptyList())
    }
}
```

- [ ] **Step 2: 创建 DocumentRepository.kt**

```kotlin
package com.ruoyi.app.feature.document.repository

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.DocumentTemplateEntity
import com.ruoyi.app.data.database.entity.DocumentVariableEntity
import com.ruoyi.app.data.database.entity.DocumentGroupEntity
import com.ruoyi.app.feature.document.api.DocumentApi
import com.ruoyi.app.feature.document.model.DocumentTemplate
import com.ruoyi.app.feature.document.model.DocumentVariable
import com.ruoyi.app.feature.document.model.DocumentGroup
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class DocumentRepository(private val context: Context) {
    private val templateDao = AppDatabase.getInstance(context).documentTemplateDao()
    private val variableDao = AppDatabase.getInstance(context).documentVariableDao()
    private val groupDao = AppDatabase.getInstance(context).documentGroupDao()

    // 同步模板到本地
    suspend fun syncTemplates() = withContext(Dispatchers.IO) {
        val templates = DocumentApi.syncTemplates()
        templateDao.insertAll(templates.map { it.toEntity() })
    }

    // 同步套组到本地
    suspend fun syncGroups() = withContext(Dispatchers.IO) {
        val groups = DocumentApi.syncGroups()
        groupDao.insertAll(groups.map { it.toEntity() })
    }

    // 获取本地模板
    fun getTemplates(): Flow<List<DocumentTemplateEntity>> {
        return templateDao.getActiveTemplates()
    }

    // 获取本地套组
    fun getGroups(): Flow<List<DocumentGroupEntity>> {
        return groupDao.getActiveGroups()
    }

    // 获取模板详情(含变量)
    suspend fun fetchTemplateDetail(id: Long): Pair<DocumentTemplate?, List<DocumentVariable>> {
        val (template, variables) = DocumentApi.getTemplateDetail(id)
        // 缓存到本地
        template?.let { templateDao.insert(it.toEntity()) }
        variableDao.insertAll(variables.map { it.toEntity() })
        return Pair(template, variables)
    }

    // 获取本地变量
    suspend fun getVariablesByTemplateId(templateId: Long): List<DocumentVariableEntity> = withContext(Dispatchers.IO) {
        variableDao.getVariablesByTemplateId(templateId)
    }

    private fun DocumentTemplate.toEntity() = DocumentTemplateEntity(
        id = id, templateCode = templateCode, templateName = templateName,
        templateType = templateType, category = category, filePath = filePath,
        fileUrl = fileUrl, version = version, isActive = isActive
    )

    private fun DocumentVariable.toEntity() = DocumentVariableEntity(
        id = id, templateId = templateId, variableName = variableName,
        variableLabel = variableLabel, variableType = variableType, required = required,
        defaultValue = defaultValue, options = options, sortOrder = sortOrder,
        maxLength = maxLength
    )

    private fun DocumentGroup.toEntity() = DocumentGroupEntity(
        id = id, groupCode = groupCode, groupName = groupName,
        groupType = groupType, templates = templates, isActive = isActive
    )
}
```

- [ ] **Step 3: 提交 API 和 Repository**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/api/
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/repository/
git commit -m "feat(android): 添加文书模块API和Repository"
```

---

### Task 13: 创建 Android UI Activity

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/ui/DocumentListActivity.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/ui/DocumentFillActivity.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/document/ui/SignatureActivity.kt`

**参考模式**: `SupervisionDetailActivity.kt`, `RecordDetailActivity.kt`

- [ ] **Step 1: 创建 DocumentListActivity.kt** (从执法记录进入，选择模板)

- [ ] **Step 2: 创建 DocumentFillActivity.kt** (变量填写表单)

- [ ] **Step 3: 创建 SignatureActivity.kt** (电子签名)

- [ ] **Step 4: 提交 UI 文件**

---

## 验证方式

1. **后端 API**: `mvn spring-boot:run -pl ruoyi-admin` 启动后访问 Swagger
2. **前端页面**: `cd ruoyi-ui && npm run dev` 访问页面
3. **Android 端**: `./gradlew assembleDebug` 编译通过
