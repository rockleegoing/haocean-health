# 文书分类管理菜单与前后端设计文档

## 1. 概述

本文档描述文书分类管理功能的前端菜单配置、Vue页面设计和后端API扩展。

**已有内容（参考 `2026-04-25-document-category-design.md`）：**
- 数据库表 `sys_document_category` 已创建
- 后端 Controller `SysDocumentCategoryController` 已实现
- Android 端 DocumentCategoryEntity、DAO、Adapter 已实现
- 首页文书分类展示功能已实现

**本文档内容：**
- 前端菜单 SQL 配置
- 分类管理 Vue 页面开发
- 分类管理 API 前端对接
- 模板列表页添加分类筛选

---

## 2. 菜单配置

### 2.1 菜单 SQL

在 `sys_menu` 表中插入文书分类管理菜单位置：

```sql
-- 文书管理菜单（假设已有父菜单 menu_id = XXX）
-- 在"系统管理"下找"文书管理"菜单或创建

-- 查找现有菜单
SELECT menu_id, menu_name, parent_id, order_num FROM sys_menu WHERE menu_name IN ('文书管理', '文书模板', 'system');

-- 在文书管理下添加"分类管理"子菜单
INSERT INTO sys_menu (
    parent_id,
    menu_name,
    order_num,
    path,
    component,
    is_frame,
    is_cache,
    menu_type,
    visible,
    status,
    perms,
    icon,
    create_by,
    create_time,
    update_by,
    update_time,
    remark
) VALUES (
    (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '文书管理') AS t),
    '分类管理',
    1,
    'category',
    'system/document/index',
    1,
    0,
    'C',
    '0',
    '0',
    'system:document:category:list',
    'tree',
    'admin',
    SYSDATE(),
    '',
    SYSDATE(),
    '文书分类管理菜单'
);

-- 添加菜单权限
INSERT INTO sys_menu (
    parent_id,
    menu_name,
    order_num,
    path,
    component,
    is_frame,
    is_cache,
    menu_type,
    visible,
    status,
    perms,
    icon,
    create_by,
    create_time,
    update_by,
    update_time,
    remark
) VALUES (
    (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '分类管理') AS t),
    '查询分类',
    1,
    '',
    NULL,
    1,
    0,
    'F',
    '0',
    '0',
    'system:document:category:query',
    '#',
    'admin',
    SYSDATE(),
    '',
    SYSDATE(),
    ''
);

INSERT INTO sys_menu (
    parent_id,
    menu_name,
    order_num,
    path,
    component,
    is_frame,
    is_cache,
    menu_type,
    visible,
    status,
    perms,
    icon,
    create_by,
    create_time,
    update_by,
    update_time,
    remark
) VALUES (
    (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '分类管理') AS t),
    '新增分类',
    2,
    '',
    NULL,
    1,
    0,
    'F',
    '0',
    '0',
    'system:document:category:add',
    '#',
    'admin',
    SYSDATE(),
    '',
    SYSDATE(),
    ''
);

INSERT INTO sys_menu (
    parent_id,
    menu_name,
    order_num,
    path,
    component,
    is_frame,
    is_cache,
    menu_type,
    visible,
    status,
    perms,
    icon,
    create_by,
    create_time,
    update_by,
    update_time,
    remark
) VALUES (
    (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '分类管理') AS t),
    '修改分类',
    3,
    '',
    NULL,
    1,
    0,
    'F',
    '0',
    '0',
    'system:document:category:edit',
    '#',
    'admin',
    SYSDATE(),
    '',
    SYSDATE(),
    ''
);

INSERT INTO sys_menu (
    parent_id,
    menu_name,
    order_num,
    path,
    component,
    is_frame,
    is_cache,
    menu_type,
    visible,
    status,
    perms,
    icon,
    create_by,
    create_time,
    update_by,
    update_time,
    remark
) VALUES (
    (SELECT menu_id FROM (SELECT menu_id FROM sys_menu WHERE menu_name = '分类管理') AS t),
    '删除分类',
    4,
    '',
    NULL,
    1,
    0,
    'F',
    '0',
    '0',
    'system:document:category:remove',
    '#',
    'admin',
    SYSDATE(),
    '',
    SYSDATE(),
    ''
);

-- 角色分配权限时使用
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(1, (SELECT menu_id FROM sys_menu WHERE perms = 'system:document:category:list'));
```

### 2.2 菜单字段说明

| 字段 | 值 | 说明 |
|------|-----|------|
| parent_id | 父菜单ID | 指向"文书管理"菜单 |
| menu_name | 分类管理 | 菜单显示名称 |
| order_num | 1 | 排序号 |
| path | category | 路由路径 |
| component | system/document/index | 组件路径（复用现有index） |
| menu_type | C | C=菜单，F=按钮 |
| perms | system:document:category:list | 权限标识 |
| visible | 0 | 0=显示，1=隐藏 |
| status | 0 | 0=正常，1=停用 |

---

## 3. 后端 API 扩展

### 3.1 现有 Controller

已实现的 Controller：`com.ruoyi.web.controller.system.SysDocumentCategoryController`

路径：`/api/admin/document/category`

| 方法 | 路径 | 权限 | 说明 |
|------|------|------|------|
| GET | /list | system:document:category:query | 分页查询分类 |
| GET | /{categoryId} | system:document:category:query | 获取分类详情 |
| POST | / | system:document:category:add | 新增分类 |
| PUT | / | system:document:category:edit | 修改分类 |
| DELETE | /{categoryIds} | system:document:category:remove | 删除分类 |
| GET | /sync | Anonymous | App同步接口 |

### 3.2 模板管理扩展（待后续实现）

模板管理页面需要添加分类筛选和分类字段编辑：

**修改模板表单项（edit.vue）**

```java
// 在 SysDocumentTemplateForm 或相关实体中添加
private Long categoryId;  // 分类ID

// 修改模板时可选分类
```

---

## 4. 前端 Vue 页面开发

### 4.1 分类管理 API

新建文件：`src/api/system/documentCategory.js`

```javascript
import request from '@/utils/request'

// 查询分类列表
export function listCategory(query) {
  return request({
    url: '/admin/document/category/list',
    method: 'get',
    params: query
  })
}

// 查询分类详细
export function getCategory(categoryId) {
  return request({
    url: '/admin/document/category/' + categoryId,
    method: 'get'
  })
}

// 新增分类
export function addCategory(data) {
  return request({
    url: '/admin/document/category',
    method: 'post',
    data: data
  })
}

// 修改分类
export function updateCategory(data) {
  return request({
    url: '/admin/document/category',
    method: 'put',
    data: data
  })
}

// 删除分类
export function delCategory(categoryId) {
  return request({
    url: '/admin/document/category/' + categoryId,
    method: 'delete'
  })
}
```

### 4.2 分类管理组件

新建文件：`src/views/system/document/category/index.vue`

```vue
<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="分类名称" prop="categoryName">
        <el-input
          v-model="queryParams.categoryName"
          placeholder="请输入分类名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="展示方式" prop="displayType">
        <el-select v-model="queryParams.displayType" placeholder="请选择展示方式" clearable>
          <el-option label="九宫格" value="grid" />
          <el-option label="表格" value="table" />
          <el-option label="列表" value="list" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button
          type="primary"
          plain
          icon="el-icon-plus"
          size="mini"
          @click="handleAdd"
          v-hasPermi="['system:document:category:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-edit"
          size="mini"
          :disabled="single"
          @click="handleUpdate"
          v-hasPermi="['system:document:category:edit']"
        >修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:document:category:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="categoryList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="分类ID" align="center" prop="categoryId" width="80" />
      <el-table-column label="分类名称" align="center" prop="categoryName" :show-overflow-tooltip="true" />
      <el-table-column label="展示方式" align="center" prop="displayType" width="100">
        <template slot-scope="scope">
          <span>{{ getDisplayTypeLabel(scope.row.displayType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sort" width="80" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-switch
            v-model="scope.row.status"
            active-value="0"
            inactive-value="1"
            @change="handleStatusChange(scope.row)"
            v-hasPermi="['system:document:category:edit']"
          />
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:document:category:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:document:category:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 添加或修改分类对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="分类名称" prop="categoryName">
          <el-input v-model="form.categoryName" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="展示方式" prop="displayType">
          <el-radio-group v-model="form.displayType">
            <el-radio label="grid">九宫格</el-radio>
            <el-radio label="table">表格</el-radio>
            <el-radio label="list">列表</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="form.sort" :min="0" :max="9999" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listCategory, getCategory, addCategory, updateCategory, delCategory } from "@/api/system/documentCategory"

export default {
  name: "DocumentCategory",
  data() {
    return {
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      categoryList: [],
      title: "",
      open: false,
      displayTypeOptions: [
        { label: '九宫格', value: 'grid' },
        { label: '表格', value: 'table' },
        { label: '列表', value: 'list' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        categoryName: null,
        displayType: null,
        status: null
      },
      form: {},
      rules: {
        categoryName: [
          { required: true, message: "分类名称不能为空", trigger: "blur" }
        ],
        displayType: [
          { required: true, message: "展示方式不能为空", trigger: "change" }
        ]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listCategory(this.queryParams).then(response => {
        this.categoryList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        categoryId: null,
        categoryName: null,
        displayType: 'grid',
        sort: 0,
        status: '0'
      }
      this.resetForm("form")
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.categoryId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加文书分类"
    },
    handleUpdate(row) {
      this.reset()
      const categoryId = row.categoryId || this.ids[0]
      getCategory(categoryId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改文书分类"
      })
    },
    handleStatusChange(row) {
      const text = row.status === '0' ? '启用' : '停用'
      updateCategory(row).then(response => {
        this.$modal.msgSuccess(text + "成功")
      }).catch(() => {
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.categoryId != null) {
            updateCategory(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addCategory(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const categoryIds = row.categoryId || this.ids
      if (row.categoryId === 0) {
        this.$modal.msgError("内置分类【其他模板】不可删除")
        return
      }
      this.$modal.confirm('是否确认删除分类编号为"' + categoryIds + '"的数据项？').then(() => {
        return delCategory(categoryIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    getDisplayTypeLabel(value) {
      const item = this.displayTypeOptions.find(opt => opt.value === value)
      return item ? item.label : value
    }
  }
}
</script>
```

### 4.3 修改文书管理首页添加分类 Tab

修改文件：`src/views/system/document/index.vue`

```vue
<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleTabClick">
      <el-tab-pane label="模板管理" name="template">
        <template v-if="activeName === 'template'">
          <template-list />
        </template>
      </el-tab-pane>
      <el-tab-pane label="套组管理" name="group">
        <template v-if="activeName === 'group'">
          <group-list />
        </template>
      </el-tab-pane>
      <el-tab-pane label="分类管理" name="category">
        <template v-if="activeName === 'category'">
          <category-list />
        </template>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import TemplateList from './template/index.vue'
import GroupList from './group/index.vue'
import CategoryList from './category/index.vue'

export default {
  name: 'Document',
  components: { TemplateList, GroupList, CategoryList },
  data() {
    return {
      activeName: 'template'
    }
  },
  methods: {
    handleTabClick(tab) {
      this.activeName = tab.name
    }
  }
}
</script>
```

---

## 5. 文件变更清单

### 5.1 后端（无需变更）

后端 Controller 已实现，菜单权限需要通过 SQL 插入。

### 5.2 前端

| 文件 | 变更 | 说明 |
|------|------|------|
| `src/api/system/documentCategory.js` | 新建 | 分类管理 API |
| `src/views/system/document/category/index.vue` | 新建 | 分类管理组件 |
| `src/views/system/document/index.vue` | 修改 | 添加分类 Tab |

### 5.3 数据库

| 文件 | 变更 | 说明 |
|------|------|------|
| `sql/Vx.x.x__document_category_menu.sql` | 新建 | 菜单和权限 SQL |

---

## 6. 测试要点

1. **菜单显示**：登录后侧边栏应显示"文书管理 > 分类管理"
2. **分类增删改查**：完整 CRUD 功能正常
3. **状态切换**：启用/停用状态切换正常
4. **权限控制**：无权限用户不显示操作按钮
5. **Tab 切换**：三个 Tab 切换正常，内容正确
6. **同步接口**：`/api/admin/document/category/sync` 正常返回数据
