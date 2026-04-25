<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="套组名称" prop="groupName">
        <el-input
          v-model="queryParams.groupName"
          placeholder="请输入套组名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="套组类型" prop="groupType">
        <el-select v-model="queryParams.groupType" placeholder="请选择套组类型" clearable>
          <el-option
            v-for="item in groupTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
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
          v-hasPermi="['system:documentGroup:add']"
        >新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['system:documentGroup:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="groupList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="套组编码" align="center" prop="groupCode" />
      <el-table-column label="套组名称" align="center" prop="groupName" />
      <el-table-column label="套组类型" align="center" prop="groupType">
        <template slot-scope="scope">
          <span>{{ getGroupTypeLabel(scope.row.groupType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="isActive" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.isActive === '1'" type="success">启用</el-tag>
          <el-tag v-else type="danger">禁用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" :show-overflow-tooltip="true" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:documentGroup:edit']"
          >编辑</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:documentGroup:remove']"
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
  </div>
</template>

<script>
export default {
  name: "DocumentGroup",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 套组表格数据
      groupList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 套组类型选项
      groupTypeOptions: [
        { label: '日常检查', value: 'DAILY_INSPECTION' },
        { label: '立案查处', value: 'CASE_INVESTIGATION' },
        { label: '证据保全', value: 'EVIDENCE_PRESERVATION' },
        { label: '行政处罚', value: 'ADMIN_PUNISHMENT' },
        { label: '行政强制', value: 'ADMIN_FORCE' },
        { label: '听证程序', value: 'HEARING_PROCEDURE' }
      ],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        groupName: null,
        groupType: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        groupName: [
          { required: true, message: "套组名称不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询套组列表 */
    getList() {
      this.loading = true
      // TODO: 调用实际的API接口
      // listDocumentGroup(this.queryParams).then(response => {
      //   this.groupList = response.rows
      //   this.total = response.total
      //   this.loading = false
      // })
      // 临时模拟数据
      setTimeout(() => {
        this.groupList = []
        this.total = 0
        this.loading = false
      }, 500)
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        groupId: null,
        groupCode: null,
        groupName: null,
        groupType: null,
        templates: null,
        isActive: '1',
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
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.groupId)
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加文书套组"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const groupId = row.groupId || this.ids
      // TODO: 调用实际的API接口
      // getDocumentGroup(groupId).then(response => {
      //   this.form = response.data
      //   this.open = true
      //   this.title = "修改文书套组"
      // })
      this.form = { ...row }
      this.open = true
      this.title = "修改文书套组"
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.groupId != null) {
            // TODO: 调用实际的API接口
            // updateDocumentGroup(this.form).then(response => {
            //   this.$modal.msgSuccess("修改成功")
            //   this.open = false
            //   this.getList()
            // })
            this.$modal.msgSuccess("修改成功")
            this.open = false
            this.getList()
          } else {
            // TODO: 调用实际的API接口
            // addDocumentGroup(this.form).then(response => {
            //   this.$modal.msgSuccess("新增成功")
            //   this.open = false
            //   this.getList()
            // })
            this.$modal.msgSuccess("新增成功")
            this.open = false
            this.getList()
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const groupIds = row.groupId || this.ids
      this.$modal.confirm('是否确认删除套组编号为"' + groupIds + '"的数据项？').then(() => {
        // TODO: 调用实际的API接口
        // return delDocumentGroup(groupIds)
        return Promise.resolve()
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 获取套组类型标签 */
    getGroupTypeLabel(value) {
      const item = this.groupTypeOptions.find(opt => opt.value === value)
      return item ? item.label : value
    }
  }
}
</script>
