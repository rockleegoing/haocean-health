<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="类型名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入类型名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['system:lawtype:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="info"
          plain
          icon="el-icon-sort"
          size="mini"
          @click="toggleExpandAll"
        >展开/折叠</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table
      v-loading="loading"
      ref="lawtypeTable"
      :data="lawtypeList"
      row-key="id"
      :tree-props="{children: 'children', hasChildren: 'hasChildren'}"
    >
      <el-table-column label="类型名称" align="left" prop="name" width="200">
        <template slot-scope="scope">
          <span>{{ scope.row.name }}</span>
        </template>
      </el-table-column>
      <el-table-column label="图标" align="center" prop="icon" width="80">
        <template slot-scope="scope">
          <i :class="scope.row.icon" v-if="scope.row.icon" />
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sort" width="80" />
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
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:lawtype:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-plus"
            @click="handleAdd(scope.row)"
            v-hasPermi="['system:lawtype:add']"
          >新增</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:lawtype:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改法律法规类型对话框 -->
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

<script>
import { listLawtype, treeList, getLawtype, delLawtype, addLawtype, updateLawtype } from "@/api/system/lawtype"
import Treeselect from "@riophae/vue-treeselect"
import "@riophae/vue-treeselect/dist/vue-treeselect.css"

export default {
  name: "Lawtype",
  components: { Treeselect },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 法律法规类型表格数据
      lawtypeList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 是否展开，默认全部展开
      isExpandAll: true,
      // 树形选项
      treeOptions: [],
      // 图标选择器
      iconPickerOpen: false,
      // Element UI 图标列表
      iconList: [
        'el-icon-document', 'el-icon-edit', 'el-icon-delete', 'el-icon-search',
        'el-icon-plus', 'el-icon-setting', 'el-icon-view', 'el-icon-upload',
        'el-icon-download', 'el-icon-refresh', 'el-icon-sort', 'el-icon-star-on',
        'el-icon-collection', 'el-icon-folder', 'el-icon-folder-opened', 'el-icon-location',
        'el-icon-phone', 'el-icon-message', 'el-icon-user', 'el-icon-info',
        'el-icon-s-check', 'el-icon-s-tools', 'el-icon-s-data', 'el-icon-s-grid',
        'el-icon-menu', 'el-icon-minus', 'el-icon-close', 'el-icon-check',
        'el-icon-arrow-left', 'el-icon-arrow-right', 'el-icon-arrow-up', 'el-icon-arrow-down',
        'el-icon-paperclip', 'el-icon-printer', 'el-icon-takeaway-box', 'el-icon-house',
        'el-icon-files', 'el-icon-bangzhu', 'el-icon-s-comment', 'el-icon-s-opportunity',
        'el-icon-s-promotion', 'el-icon-s-order', 'el-icon-s-management'
      ],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        name: null,
        status: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "类型名称不能为空", trigger: "blur" }
        ]
      }
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
    /** 查询法律法规类型列表 */
    getList() {
      this.loading = true
      listLawtype(this.queryParams).then(response => {
        this.lawtypeList = this.handleTree(response.rows || [])
        this.loading = false
      })
    },
    /** 处理树形结构 */
    handleTree(data) {
      const list = []
      const tempList = data.map(item => item.id)
      for (const item of data) {
        if (!tempList.includes(item.parentId) || item.parentId === 0) {
          this.recursionFn(data, item)
          list.push(item)
        }
      }
      return list.length > 0 ? list : data
    },
    /** 递归 */
    recursionFn(data, parent) {
      const children = data.filter(item => item.parentId === parent.id)
      if (children.length > 0) {
        parent.children = children
        children.forEach(item => this.recursionFn(data, item))
      }
    },
    /** 获取树形选项 */
    getTreeList() {
      treeList().then(response => {
        // 添加一个虚拟的顶级节点
        this.treeOptions = [{ id: 0, name: '顶级节点', children: response.data }]
      })
    },
    /** 展开/折叠切换 */
    toggleExpandAll() {
      this.isExpandAll = !this.isExpandAll
      // 遍历所有行，切换展开/折叠状态
      const toggleTreeNode = (rows) => {
        rows.forEach(row => {
          if (row.children && row.children.length > 0) {
            this.$refs.lawtypeTable.toggleRowExpansion(row, this.isExpandAll)
            toggleTreeNode(row.children)
          }
        })
      }
      toggleTreeNode(this.lawtypeList)
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
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        id: null,
        parentId: null,
        name: null,
        icon: null,
        sort: 0,
        status: '0',
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    /** 新增按钮操作 */
    handleAdd(row) {
      this.reset()
      this.getTreeList()
      if (row != null && row.id) {
        this.form.parentId = row.id
      } else {
        this.form.parentId = 0
      }
      this.open = true
      this.title = "添加法律法规类型"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      this.getTreeList()
      const id = row.id || this.ids
      getLawtype(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改法律法规类型"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateLawtype(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
              this.getTreeList()
            })
          } else {
            addLawtype(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
              this.getTreeList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除法律法规类型编号为"' + ids + '"的数据项？').then(function() {
        return delLawtype(ids)
      }).then(() => {
        this.getList()
        this.getTreeList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
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
