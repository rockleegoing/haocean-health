<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="监管事项" prop="regulatoryMatterId">
        <el-select v-model="queryParams.regulatoryMatterId" placeholder="请选择监管事项" clearable filterable @change="handleQuery">
          <el-option
            v-for="item in matterOptions"
            :key="item.id"
            :label="item.mainName"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="数据源id" prop="countyItemId">
        <el-input
          v-model="queryParams.countyItemId"
          placeholder="请输入数据源id"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="监管子项编码" prop="itemCode">
        <el-input
          v-model="queryParams.itemCode"
          placeholder="请输入监管子项编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="是否标记删除，flag 0否 1是" prop="isDeleted">
        <el-input
          v-model="queryParams.isDeleted"
          placeholder="请输入是否标记删除，flag 0否 1是"
          clearable
          @keyup.enter.native="handleQuery"
        />
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
          v-hasPermi="['system:matteritem:add']"
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
          v-hasPermi="['system:matteritem:edit']"
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
          v-hasPermi="['system:matteritem:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:matteritem:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="matteritemList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="${comment}" align="center" prop="id" />
      <el-table-column label="监管事项" align="center" prop="mainName" show-overflow-tooltip />
      <el-table-column label="数据源id" align="center" prop="countyItemId" />
      <el-table-column label="类型" align="center" prop="codeType" />
      <el-table-column label="监管子项编码" align="center" prop="itemCode" />
      <el-table-column label="详情名称" align="center" prop="itemName" />
      <el-table-column label="描述" align="center" prop="according" />
      <el-table-column label="是否标记删除，flag 0否 1是" align="center" prop="isDeleted" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleViewData(scope.row)"
            v-hasPermi="['system:matteritem:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:matteritem:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:matteritem:remove']"
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

    <!-- 监管事项详情详情抽屉 -->
    <matteritem-view-drawer ref="matteritemViewRef" />
    <!-- 添加或修改监管事项详情对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="监管事项" prop="regulatoryMatterId">
              <el-select v-model="form.regulatoryMatterId" placeholder="请选择监管事项" filterable style="width: 100%">
                <el-option
                  v-for="item in matterOptions"
                  :key="item.id"
                  :label="item.mainName"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="数据源id" prop="countyItemId">
              <el-input v-model="form.countyItemId" placeholder="请输入数据源id" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="监管子项编码" prop="itemCode">
              <el-input v-model="form.itemCode" placeholder="请输入监管子项编码" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="详情名称" prop="itemName">
              <el-input v-model="form.itemName" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="描述" prop="according">
              <el-input v-model="form.according" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="是否标记删除，flag 0否 1是" prop="isDeleted">
              <el-input v-model="form.isDeleted" placeholder="请输入是否标记删除，flag 0否 1是" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMatteritem, getMatteritem, delMatteritem, addMatteritem, updateMatteritem } from "@/api/system/matteritem"
import { listMatter } from "@/api/system/matter"
import MatteritemViewDrawer from "./view"

export default {
  name: "Matteritem",
  components: { MatteritemViewDrawer },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非单个禁用
      single: true,
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 监管事项详情表格数据
      matteritemList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 监管事项下拉选项
      matterOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        regulatoryMatterId: null,
        countyItemId: null,
        codeType: null,
        itemCode: null,
        itemName: null,
        according: null,
        isDeleted: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        regulatoryMatterId: [
          { required: true, message: "主表id不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
    this.getMatterOptions()
  },
  methods: {
    /** 获取监管事项下拉选项 */
    getMatterOptions() {
      listMatter({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.matterOptions = response.rows || []
      })
    },
    /** 查询监管事项详情列表 */
    getList() {
      this.loading = true
      listMatteritem(this.queryParams).then(response => {
        this.matteritemList = response.rows
        this.total = response.total
        this.loading = false
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
        regulatoryMatterId: null,
        countyItemId: null,
        codeType: null,
        itemCode: null,
        itemName: null,
        according: null,
        isDeleted: null,
        createTime: null,
        updateTime: null
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加监管事项详情"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getMatteritem(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改监管事项详情"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateMatteritem(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addMatteritem(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除监管事项详情编号为"' + ids + '"的数据项？').then(function() {
        return delMatteritem(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 详情按钮操作 */
    handleViewData(row) {
      this.$refs["matteritemViewRef"].open(row.id)
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/matteritem/export', {
        ...this.queryParams
      }, `matteritem_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
