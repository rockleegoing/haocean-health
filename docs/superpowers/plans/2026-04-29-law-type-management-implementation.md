# 法律法规类型管理实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 实现法律法规类型管理功能，包括类型CRUD、树形展示、法律列表联动查询

**Architecture:** 后端采用标准 RuoYi 框架结构（Controller-Service-Mapper），前端采用 Vue + Element UI，使用 vue-treeselect 实现树形选择，handleTree 处理树形数据转换

**Tech Stack:** Java/Spring Boot, MyBatis, Vue 2.6, Element UI, vue-treeselect

---

## 文件清单

### 后端（RuoYi-Vue）

| 文件 | 操作 | 职责 |
|-----|------|-----|
| `ruoyi-system/src/main/java/com/ruoyi/system/domain/LawType.java` | 修改 | 添加 ancestors 自动计算 |
| `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/LawTypeServiceImpl.java` | 修改 | 添加 ancestors 自动计算、新增/修改时更新子节点 ancestors |
| `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/LawTypeController.java` | 修改 | 添加树形列表接口 `/treeList` |
| `ruoyi-system/src/main/java/com/ruoyi/system/domain/Law.java` | 修改 | 添加 typeId 字段及 getter/setter |
| `ruoyi-system/src/main/resources/mapper/system/LawMapper.xml` | 修改 | 添加 typeId 字段映射和查询条件 |
| `ruoyi-ui/src/api/system/lawtype.js` | 修改 | 添加 treeList API |

### 前端（ruoyi-ui）

| 文件 | 操作 | 职责 |
|-----|------|-----|
| `ruoyi-ui/src/views/system/lawtype/index.vue` | 修改 | 树形表格展示、父类型选择器、图标选择器、状态切换 |
| `ruoyi-ui/src/views/system/law/index.vue` | 修改 | 左侧添加类型树、联动查询法律 |

### SQL 脚本

| 文件 | 职责 |
|-----|-----|
| `sql/V1.2.3__law_type.sql` | 创建 law_type 表、为 law 表添加 type_id 字段、插入初始数据、添加菜单 |

---

## Task 1: 创建 SQL 迁移脚本

**Files:**
- Create: `RuoYi-Vue/sql/V1.2.3__law_type.sql`

- [ ] **Step 1: 创建 SQL 脚本**

```sql
-- ============================================
-- 脚本：V1.2.3__law_type.sql
-- 版本：1.2.3
-- 日期：2026-04-29
-- 描述：法律法规类型管理功能
-- ============================================

-- 1. 创建 law_type 表
CREATE TABLE `law_type` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
  `parent_id` BIGINT DEFAULT 0 COMMENT '父类型ID（0为顶级）',
  `ancestors` VARCHAR(500) DEFAULT '' COMMENT '祖先路径',
  `name` VARCHAR(50) NOT NULL COMMENT '类型名称',
  `icon` VARCHAR(100) DEFAULT '' COMMENT '图标',
  `sort` INT DEFAULT 0 COMMENT '排序',
  `status` CHAR(1) DEFAULT '0' COMMENT '状态（0正常 1停用）',
  `create_by` VARCHAR(64) DEFAULT '' COMMENT '创建者',
  `create_time` DATETIME DEFAULT NULL COMMENT '创建时间',
  `update_by` VARCHAR(64) DEFAULT '' COMMENT '更新者',
  `update_time` DATETIME DEFAULT NULL COMMENT '更新时间',
  `remark` VARCHAR(500) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=UTF8MB4 COMMENT='法律法规类型表';

-- 2. 为 law 表添加 type_id 字段
ALTER TABLE `law` ADD COLUMN `type_id` BIGINT COMMENT '法律法规类型ID（末级）' AFTER `release_time`;

-- 3. 插入初始数据（示例）
INSERT INTO `law_type` (`id`, `parent_id`, `ancestors`, `name`, `icon`, `sort`, `status`) VALUES
(1, 0, '0', '国家法律', 'el-icon-document', 1, '0'),
(2, 0, '0', '行政法规', 'el-icon-collection', 2, '0'),
(3, 0, '0', '地方性法规', 'el-icon-location', 3, '0');

INSERT INTO `law_type` (`id`, `parent_id`, `ancestors`, `name`, `icon`, `sort`, `status`) VALUES
(4, 1, '0,1', '宪法', 'el-icon-document', 1, '0'),
(5, 1, '0,1', '基本法律', 'el-icon-document', 2, '0'),
(6, 2, '0,2', '国务院规章', 'el-icon-collection', 1, '0'),
(7, 3, '0,3', '省级法规', 'el-icon-location', 1, '0');

-- 4. 添加菜单（法律类型管理）
-- 父菜单：法律法规
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`)
SELECT '法律类型管理', menu_id, 2, 'lawType', 'system/lawType/index', 1, 0, 'C', '0', '0', 'system:lawType:list', 'el-icon-set-up', 'admin', NOW(), '法律类型管理菜单'
FROM `sys_menu` WHERE `menu_name` = '法律法规' AND `parent_id` = 0 LIMIT 1;

SET @parent_id = LAST_INSERT_ID();

-- 按钮权限
INSERT INTO `sys_menu` (`menu_name`, `parent_id`, `order_num`, `path`, `component`, `is_frame`, `is_cache`, `menu_type`, `visible`, `status`, `perms`, `icon`, `create_by`, `create_time`, `remark`) VALUES
('法律类型查询', @parent_id, 1, '#', '', 1, 0, 'F', '0', '0', 'system:lawType:query', '#', 'admin', NOW(), ''),
('法律类型新增', @parent_id, 2, '#', '', 1, 0, 'F', '0', '0', 'system:lawType:add', '#', 'admin', NOW(), ''),
('法律类型修改', @parent_id, 3, '#', '', 1, 0, 'F', '0', '0', 'system:lawType:edit', '#', 'admin', NOW(), ''),
('法律类型删除', @parent_id, 4, '#', '', 1, 0, 'F', '0', '0', 'system:lawType:remove', '#', 'admin', NOW(), '');
```

- [ ] **Step 2: 提交 SQL 脚本**

```bash
git add RuoYi-Vue/sql/V1.2.3__law_type.sql
git commit -m "feat(sql): 添加法律法规类型管理数据库脚本"
```

---

## Task 2: LawType 实体类 - 添加 ancestors 自动计算

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/LawType.java:40-50`

- [ ] **Step 1: 查看现有 LawType 实体**

确认现有字段：id, parentId, ancestors, name, icon, sort, status, createBy, createTime, updateBy, updateTime, remark

- [ ] **Step 2: 添加 calcAncestors 方法**

在 LawType 类中添加计算祖先路径的方法：

```java
/**
 * 计算祖先路径
 * @return 祖先路径字符串，格式：0,1,2
 */
public String calcAncestors() {
    if (this.parentId == null || this.parentId == 0) {
        return "0";
    }
    // 如果父节点存在，需要在 Service 层查询父节点的 ancestors
    return String.valueOf(this.parentId);
}
```

注：完整的 ancestors 计算需要在 Service 层实现（查询父节点 ancestors 后拼接）

- [ ] **Step 3: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/LawType.java
git commit -m "feat(domain): LawType 添加 calcAncestors 方法"
```

---

## Task 3: LawTypeServiceImpl - 添加 ancestors 自动计算逻辑

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/LawTypeServiceImpl.java`

- [ ] **Step 1: 修改 insertLawType 方法**

添加插入时自动计算 ancestors 的逻辑：

```java
@Override
public int insertLawType(LawType lawType) {
    lawType.setCreateTime(DateUtils.getNowDate());
    // 自动计算 ancestors
    if (lawType.getParentId() == null || lawType.getParentId() == 0) {
        lawType.setAncestors("0");
    } else {
        // 查询父节点的 ancestors
        LawType parent = lawTypeMapper.selectLawTypeById(lawType.getParentId());
        if (parent != null) {
            lawType.setAncestors(parent.getAncestors() + "," + parent.getId());
        } else {
            lawType.setAncestors("0");
        }
    }
    return lawTypeMapper.insertLawType(lawType);
}
```

- [ ] **Step 2: 修改 updateLawType 方法**

添加修改时更新子节点 ancestors 的逻辑：

```java
@Override
public int updateLawType(LawType lawType) {
    lawType.setUpdateTime(DateUtils.getNowDate());
    LawType old = lawTypeMapper.selectLawTypeById(lawType.getId());
    String oldAncestors = old.getAncestors();
    String newAncestors;

    if (lawType.getParentId() == null || lawType.getParentId() == 0) {
        newAncestors = "0";
    } else {
        LawType parent = lawTypeMapper.selectLawTypeById(lawType.getParentId());
        if (parent != null) {
            newAncestors = parent.getAncestors() + "," + parent.getId();
        } else {
            newAncestors = "0";
        }
    }
    lawType.setAncestors(newAncestors);

    int result = lawTypeMapper.updateLawType(lawType);

    // 如果 ancestors 发生变化，需要更新所有子节点的 ancestors
    if (result > 0 && !oldAncestors.equals(newAncestors)) {
        // 更新子节点的 ancestors（将旧前缀替换为新前缀）
        List<LawType> children = lawTypeMapper.selectChildrenByAncestorsLike(oldAncestors + "," + lawType.getId());
        for (LawType child : children) {
            LawType updateChild = new LawType();
            updateChild.setId(child.getId());
            updateChild.setAncestors(newAncestors + child.getAncestors().substring(oldAncestors.length()));
            lawTypeMapper.updateLawType(updateChild);
        }
    }

    return result;
}
```

- [ ] **Step 3: 添加查询子节点的方法声明到 ILawTypeService**

在 `ILawTypeService.java` 添加：

```java
/**
 * 查询指定 ancestors 前缀的所有子节点
 * @param ancestorsPrefix 祖先路径前缀
 * @return 子节点列表
 */
List<LawType> selectChildrenByAncestorsLike(String ancestorsPrefix);
```

- [ ] **Step 4: 在 LawTypeMapper.xml 添加 selectChildrenByAncestorsLike**

```xml
<select id="selectChildrenByAncestorsLike" parameterType="String" resultMap="LawTypeResult">
    <include refid="selectLawTypeVo"/>
    WHERE ancestors LIKE CONCAT(#{ancestorsPrefix}, '%')
</select>
```

- [ ] **Step 5: 在 LawTypeMapper.java 添加方法声明**

```java
List<LawType> selectChildrenByAncestorsLike(String ancestorsPrefix);
```

- [ ] **Step 6: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ILawTypeService.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/LawTypeServiceImpl.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/LawTypeMapper.java
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/LawTypeMapper.xml
git commit -m "feat(service): LawTypeService ancestors 自动计算逻辑"
```

---

## Task 4: LawTypeController - 添加树形列表接口

**Files:**
- Modify: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/LawTypeController.java`

- [ ] **Step 1: 添加 treeList 接口**

在 LawTypeController 类中添加：

```java
/**
 * 获取法律类型树结构
 */
@PreAuthorize("@ss.hasPermi('system:lawtype:list')")
@GetMapping("/treeList")
public AjaxResult treeList(LawType lawType) {
    List<LawType> list = lawTypeService.selectLawTypeList(lawType);
    return success(handleTree(list));
}

/**
 * 处理树形结构
 */
private List<LawType> handleTree(List<LawType> list) {
    List<LawType> returnList = new ArrayList<>();
    List<Long> tempList = list.stream().map(LawType::getId).collect(Collectors.toList());
    for (LawType lawType : list) {
        if (!tempList.contains(lawType.getParentId())) {
            recursionFn(list, lawType);
            returnList.add(lawType);
        }
    }
    if (returnList.isEmpty()) {
        returnList = list;
    }
    return returnList;
}

/**
 * 递归列表
 */
private void recursionFn(List<LawType> list, LawType t) {
    List<LawType> childList = getChildList(list, t);
    t.setChildren(childList);
    for (LawType tChild : childList) {
        if (hasChild(list, tChild)) {
            recursionFn(list, tChild);
        }
    }
}

/**
 * 获取子列表
 */
private List<LawType> getChildList(List<LawType> list, LawType t) {
    List<LawType> tlist = new ArrayList<>();
    for (LawType n : list) {
        if (n.getParentId() != null && n.getParentId().longValue() == t.getId().longValue()) {
            tlist.add(n);
        }
    }
    return tlist;
}

/**
 * 判断是否有子节点
 */
private boolean hasChild(List<LawType> list, LawType t) {
    return getChildList(list, t).size() > 0;
}
```

- [ ] **Step 2: 添加必要的 import**

```java
import java.util.ArrayList;
import java.util.stream.Collectors;
```

- [ ] **Step 3: 提交**

```bash
git add RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/LawTypeController.java
git commit -m "feat(controller): LawTypeController 添加树形列表接口"
```

---

## Task 5: 前端 API - 添加 treeList 方法

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/api/system/lawtype.js`

- [ ] **Step 1: 添加 treeList API**

```javascript
// 获取法律类型树
export function treeList(query) {
  return request({
    url: '/system/lawtype/treeList',
    method: 'get',
    params: query
  })
}
```

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/ruoyi-ui/src/api/system/lawtype.js
git commit -m "feat(api): lawtype 添加 treeList 接口"
```

---

## Task 6: lawtype/index.vue - 增强为树形表格和图标选择器

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/system/lawtype/index.vue`

- [ ] **Step 1: 查看现有的 vue-treeselect 组件是否已安装**

检查 `package.json` 或直接尝试使用

- [ ] **Step 2: 修改 template 部分**

将表格改为树形表格展示，添加图标选择器对话框：

```vue
<template>
  <div class="app-container">
    <!-- 搜索栏保持不变 -->

    <el-row :gutter="10" class="mb8">
      <!-- 按钮保持不变 -->
    </el-row>

    <!-- 树形表格 -->
    <el-table v-loading="loading" :data="lawtypeList" row-key="id" default-expand-all :tree-props="{children: 'children', hasChildren: 'hasChildren'}">
      <!-- 选择框列 -->
      <el-table-column type="selection" width="55" align="center" />
      <!-- id 列 -->
      <el-table-column label="主键" align="center" prop="id" width="80" />
      <!-- 名称列（带缩进效果） -->
      <el-table-column label="类型名称" align="left" prop="name" width="200">
        <template slot-scope="scope">
          <span :style="{marginLeft: scope.row.parentId === 0 ? '0px' : '20px'}">{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <!-- 图标列 -->
      <el-table-column label="图标" align="center" prop="icon" width="100">
        <template slot-scope="scope">
          <i :class="scope.row.icon" v-if="scope.row.icon" />
        </template>
      </el-table-column>
      <!-- 排序列 -->
      <el-table-column label="排序" align="center" prop="sort" width="80" />
      <!-- 状态列 -->
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
          />
        </template>
      </el-table-column>
      <!-- 备注列 -->
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <!-- 操作列保持不变 -->
    </el-table>

    <!-- 添改对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="父类型" prop="parentId">
              <treeselect
                v-model="form.parentId"
                :options="treeOptions"
                :normalizer="normalizer"
                placeholder="选择父类型（默认为顶级）"
                :show-count="true"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="类型名称" prop="name">
              <el-input v-model="form.name" placeholder="请输入类型名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="图标" prop="icon">
              <el-input v-model="form.icon" placeholder="请选择图标" readonly @click.native="openIconPicker">
                <i slot="suffix" :class="form.icon" v-if="form.icon" style="margin-right: 8px;" />
              </el-input>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="排序" prop="sort">
              <el-input-number v-model="form.sort" :min="0" controls-position="right" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 图标选择器对话框 -->
    <el-dialog title="选择图标" :visible.sync="iconPickerOpen" width="800px" append-to-body>
      <div class="icon-list">
        <div v-for="icon in iconList" :key="icon" :class="['icon-item', {selected: form.icon === icon}]" @click="selectIcon(icon)">
          <i :class="icon" />
        </div>
      </div>
    </el-dialog>
  </div>
</template>
```

- [ ] **Step 3: 修改 script 部分**

```javascript
import { listLawtype, treeList, getLawtype, delLawtype, addLawtype, updateLawtype } from "@/api/system/lawtype"
import Treeselect from "@riophae/vue-treeselect"
import "@riophae/vue-treeselect/dist/vue-treeselect.css"

export default {
  name: "Lawtype",
  components: { Treeselect },
  data() {
    return {
      // ... 原有 data ...
      // 添加 treeOptions 和 iconPickerOpen
      treeOptions: [],
      iconPickerOpen: false,
      // Element UI 图标列表（常用图标）
      iconList: [
        'el-icon-document', 'el-icon-edit', 'el-icon-delete', 'el-icon-search',
        'el-icon-plus', 'el-icon-setting', 'el-icon-view', 'el-icon-upload',
        'el-icon-download', 'el-icon-refresh', 'el-icon-sort', 'el-icon-star-on',
        'el-icon-collection', 'el-icon-folder', 'el-icon-folder-opened', 'el-icon-location',
        'el-icon-phone', 'el-icon-message', 'el-icon-user', 'el-icon-info',
        'el-icon-s-check', 'el-icon-s-tools', 'el-icon-s-data', 'el-icon-s-grid',
        'el-icon-menu', 'el-icon-minus', 'el-icon-close', 'el-icon-check',
        'el-icon-arrow-left', 'el-icon-arrow-right', 'el-icon-arrow-up', 'el-icon-arrow-down'
      ]
    }
  },
  created() {
    this.getList()
    this.getTreeList()
  },
  methods: {
    /** 转换树形结构 */
    normalizer(node) {
      return {
        id: node.id,
        label: node.name,
        children: node.children
      }
    },
    /** 获取树形列表 */
    getTreeList() {
      treeList().then(response => {
        // 添加一个虚拟的顶级节点
        this.treeOptions = [{ id: 0, name: '顶级节点', children: response.data }]
      })
    },
    /** 打开图标选择器 */
    openIconPicker() {
      this.iconPickerOpen = true
    },
    /** 选择图标 */
    selectIcon(icon) {
      this.form.icon = icon
      this.iconPickerOpen = false
    },
    /** 状态切换 */
    handleStatusChange(row) {
      const data = { id: row.id, status: row.status }
      updateLawtype(data).then(response => {
        this.$modal.msgSuccess("状态修改成功")
      }).catch(() => {
        // 恢复原状态
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    // ... 其他原有方法保持不变 ...
  }
}
```

- [ ] **Step 4: 添加图标选择器样式**

```css
<style scoped>
/* ... 原有样式 ... */
.icon-list {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}
.icon-item {
  width: 50px;
  height: 50px;
  display: flex;
  align-items: center;
  justify-content: center;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
  cursor: pointer;
  font-size: 20px;
}
.icon-item:hover {
  border-color: #409eff;
  background-color: #f5f7fa;
}
.icon-item.selected {
  border-color: #409eff;
  background-color: #ecf5ff;
}
</style>
```

- [ ] **Step 5: 提交**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/system/lawtype/index.vue
git commit -m "feat(frontend): lawtype 页面增强为树形表格和图标选择器"
```

---

## Task 7: law/index.vue - 添加左侧类型树和联动查询

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/system/law/index.vue`

- [ ] **Step 1: 修改 template - 左侧改为类型树**

将左侧法律列表区域改为类型树+法律列表：

```vue
<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧：类型树 + 法律列表 -->
      <el-col :span="8" :xs="24">
        <div class="left-panel">
          <div class="panel-header">
            <span>法律法规类型</span>
          </div>
          <!-- 类型树 -->
          <el-tree
            ref="lawTypeTree"
            :data="lawTypeTreeData"
            :props="lawTypeTreeProps"
            node-key="id"
            :expand-on-click-node="false"
            :highlight-current="true"
            @node-click="handleLawTypeNodeClick"
          >
            <span slot-scope="{ node, data }" class="custom-tree-node">
              <span>
                <i :class="data.icon" v-if="data.icon" style="margin-right: 5px;" />
                {{ node.label }}
              </span>
              <span style="color: #999; font-size: 12px;">
                {{ data.children ? data.children.length : '' }}
              </span>
            </span>
          </el-tree>

          <el-divider />

          <div class="panel-header">
            <span>{{ selectedTypeName || '全部法律' }}</span>
            <el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddLaw" style="margin-left: auto;">
              新增法律
            </el-button>
          </div>
          <!-- 法律列表（根据选中类型过滤） -->
          <el-input
            v-model="lawSearchKeyword"
            placeholder="搜索法律名称"
            size="mini"
            clearable
            prefix-icon="el-icon-search"
            style="margin-bottom: 10px;"
            @input="handleLawSearch"
          />
          <el-table
            v-loading="lawLoading"
            :data="lawSearchKeyword ? lawSearchResult : lawList"
            :show-header="false"
            border
            stripe
            highlight-current-row
            @row-click="handleLawRowClick"
            :row-class-name="lawRowClassName"
            style="width: 100%; cursor: pointer;"
          >
            <el-table-column label="法律名称" align="left" prop="name" />
            <el-table-column label="发布日期" align="center" prop="releaseTime" width="100">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.releaseTime, '{y}-{m}') }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>

      <!-- 右侧：法律条款列表 - 保持不变 -->
      <el-col :span="16" :xs="24">
        <!-- ... 右侧内容保持不变 ... -->
      </el-col>
    </el-row>

    <!-- 法律对话框 - 添加类型选择 -->
    <el-dialog :title="lawTitle" :visible.sync="lawOpen" width="500px" append-to-body>
      <el-form ref="lawForm" :model="lawForm" :rules="lawRules" label-width="100px">
        <el-form-item label="法律名称" prop="name">
          <el-input v-model="lawForm.name" placeholder="请输入法律名称" />
        </el-form-item>
        <el-form-item label="所属类型" prop="typeId">
          <el-select v-model="lawForm.typeId" placeholder="请选择类型（仅末级）" clearable style="width: 100%">
            <el-option
              v-for="item in lawTypeOptions"
              :key="item.id"
              :label="item.label"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="发布日期" prop="releaseTime">
          <el-date-picker
            v-model="lawForm.releaseTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择发布日期"
            style="width: 100%;"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitLawForm">确 定</el-button>
        <el-button @click="cancelLaw">取 消</el-button>
      </div>
    </el-dialog>

    <!-- ... 其他对话框保持不变 ... -->
  </div>
</template>
```

- [ ] **Step 2: 修改 script - 添加类型树相关数据和方法**

```javascript
import { listLaw, getLaw, addLaw, updateLaw, delLaw } from "@/api/system/law"
import { treeList } from "@/api/system/lawtype"

export default {
  name: "LawWithTerm",
  data() {
    return {
      // ... 原有 data ...

      // 类型树相关
      lawTypeTreeData: [],
      lawTypeTreeProps: {
        children: 'children',
        label: 'name'
      },
      selectedTypeId: null,
      selectedTypeName: '',
      lawTypeOptions: [], // 用于法律新增/编辑的类型下拉选项

      // 法律表单校验规则 - 添加 typeId
      lawRules: {
        name: [{ required: true, message: "法律名称不能为空", trigger: "blur" }],
        typeId: [{ required: false, message: "请选择类型", trigger: "change" }]
      }
    }
  },
  created() {
    this.getLawTypeTree()
    this.getLawTypeOptions() // 获取用于下拉选择的类型列表
    this.getLawList()
    this.getLawOptions()
  },
  methods: {
    // ========== 类型树相关 ==========
    /** 获取类型树 */
    getLawTypeTree() {
      treeList().then(response => {
        if (response.data && response.data.length > 0) {
          // 兼容顶级节点
          this.lawTypeTreeData = response.data[0].children || response.data
        }
      })
    },
    /** 获取类型下拉选项（仅末级节点） */
    getLawTypeOptions() {
      treeList().then(response => {
        const flattenOptions = []
        const flatten = (list, level = 0) => {
          list.forEach(item => {
            flattenOptions.push({ id: item.id, name: item.name, label: '-'.repeat(level) + item.name })
            if (item.children && item.children.length > 0) {
              flatten(item.children, level + 1)
            }
          })
        }
        if (response.data && response.data.length > 0) {
          flatten(response.data[0].children || response.data)
        }
        this.lawTypeOptions = flattenOptions
      })
    },
    /** 点击类型节点 */
    handleLawTypeNodeClick(data) {
      this.selectedTypeId = data.id
      this.selectedTypeName = data.name
      this.currentLawId = null
      this.currentLawName = ''
      this.getLawList()
    },
    /** 法律搜索 - 增加类型过滤 */
    handleLawSearch() {
      if (!this.lawSearchKeyword) {
        this.lawSearchResult = []
        return
      }
      const keyword = this.lawSearchKeyword.toLowerCase()
      this.lawSearchResult = this.lawList.filter(item =>
        (item.name || '').toLowerCase().includes(keyword) &&
        (this.selectedTypeId ? item.typeId === this.selectedTypeId : true)
      )
    },
    /** 获取法律列表 - 支持类型过滤 */
    getLawList() {
      this.lawLoading = true
      const params = { pageNum: 1, pageSize: 9999 }
      if (this.selectedTypeId) {
        params.typeId = this.selectedTypeId
      }
      listLaw(params).then(response => {
        this.lawList = response.rows || []
        this.lawLoading = false
      })
    },
    /** 新增法律按钮 */
    handleAddLaw() {
      this.lawForm = { typeId: this.selectedTypeId }
      this.lawTitle = "新增法律"
      this.lawOpen = true
    },
    /** 提交法律表单 - 确保 typeId 正确传递 */
    submitLawForm() {
      this.$refs["lawForm"].validate(valid => {
        if (valid) {
          if (this.lawForm.id) {
            updateLaw(this.lawForm).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.lawOpen = false
              this.getLawList()
            })
          } else {
            addLaw(this.lawForm).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.lawOpen = false
              this.getLawList()
            })
          }
        }
      })
    },
    // ... 其他原有方法保持不变 ...
  }
}
</script>
```

- [ ] **Step 3: 添加样式**

```css
<style scoped>
/* ... 原有样式 ... */
.custom-tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
</style>
```

- [ ] **Step 4: 提交**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/system/law/index.vue
git commit -m "feat(frontend): law 页面添加左侧类型树和联动查询"
```

---

## Task 8: Law 实体添加 typeId 字段

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/Law.java`

- [ ] **Step 1: 添加 typeId 字段和 getter/setter**

在 `releaseTime` 字段后添加：

```java
/** 法律法规类型ID（末级） */
private Long typeId;

public void setTypeId(Long typeId) {
    this.typeId = typeId;
}

public Long getTypeId() {
    return typeId;
}
```

- [ ] **Step 2: 更新 toString 方法**

```java
@Override
public String toString() {
    return new ToStringBuilder(this, ToStringStyle.MULTI_LINE_STYLE)
        .append("id", getId())
        .append("name", getName())
        .append("releaseTime", getReleaseTime())
        .append("typeId", getTypeId())
        .toString();
}
```

- [ ] **Step 3: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/Law.java
git commit -m "feat(domain): Law 实体添加 typeId 字段"
```

---

## Task 9: LawMapper.xml 添加 typeId 支持

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/LawMapper.xml`

- [ ] **Step 1: 查看现有 LawMapper.xml**

确认现有结构

- [ ] **Step 2: 在 resultMap 中添加 typeId**

```xml
<resultMap type="Law" id="LawResult">
    <result property="id"    column="id"    />
    <result property="name"    column="name"    />
    <result property="releaseTime"    column="release_time"    />
    <result property="typeId"    column="type_id"    />
</resultMap>
```

- [ ] **Step 3: 在 selectLawList 的 where 条件中添加 typeId 查询支持**

```xml
<where>
    <if test="typeId != null"> and type_id = #{typeId}</if>
</where>
```

- [ ] **Step 4: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/LawMapper.xml
git commit -m "feat(mapper): LawMapper 添加 typeId 支持"
```

---

## Task 10: 验证和测试

- [ ] **Step 1: 执行 SQL 脚本**

```bash
# 连接到 MySQL 执行
mysql -u root -p your_database < RuoYi-Vue/sql/V1.2.3__law_type.sql
```

- [ ] **Step 2: 启动后端验证**

```bash
cd RuoYi-Vue
mvn spring-boot:run -pl ruoyi-admin
```

- [ ] **Step 3: 启动前端验证**

```bash
cd RuoYi-Vue/ruoyi-ui
npm run dev
```

- [ ] **Step 4: 测试类型管理 CRUD**

1. 访问 `系统管理 -> 法律法规 -> 法律类型管理`
2. 测试新增顶级类型（国家法律）
3. 测试新增子类型（宪法、基本法律）
4. 测试修改类型名称
5. 测试状态切换
6. 测试删除（确认有子类型时不能删除）

- [ ] **Step 5: 测试法律管理联动**

1. 访问 `系统管理 -> 法律法规 -> 法律目录`
2. 确认左侧显示类型树
3. 点击类型节点，确认右侧法律列表按类型过滤
4. 测试新增法律，选择类型
5. 测试修改法律，变更类型

---

## Spec 覆盖率检查

| 设计文档需求 | 对应任务 |
|-------------|---------|
| 数据库设计（law_type 表、law.type_id 字段） | Task 1 |
| 类型 CRUD 功能 | Task 2-6 |
| 树形结构展示 | Task 4, 6 |
| ancestors 自动计算 | Task 3 |
| 联动查询（点击类型展示法律） | Task 7 |
| 法律编辑选择类型 | Task 7, 8, 9 |
| 图标选择器 | Task 6 |
| 状态切换 | Task 6 |

---

**Plan complete and saved to `docs/superpowers/plans/2026-04-29-law-type-management-implementation.md`**

**Two execution options:**

**1. Subagent-Driven (recommended)** - I dispatch a fresh subagent per task, review between tasks, fast iteration

**2. Inline Execution** - Execute tasks in this session using executing-plans, batch execution with checkpoints

**Which approach?**