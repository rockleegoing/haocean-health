<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="模板名称" prop="templateName">
        <el-input
          v-model="queryParams.templateName"
          placeholder="请输入模板名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="所属分类" prop="categoryId">
        <el-select v-model="queryParams.categoryId" placeholder="请选择所属分类" clearable style="width: 100%">
          <el-option
            v-for="item in categoryOptions"
            :key="item.categoryId"
            :label="item.categoryName"
            :value="item.categoryId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="行业分类" prop="industryCategoryId">
        <el-select v-model="queryParams.industryCategoryId" placeholder="请选择行业分类" clearable style="width: 100%">
          <el-option
            v-for="item in industryCategoryOptions"
            :key="item.categoryId"
            :label="item.categoryName"
            :value="item.categoryId"
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
          v-hasPermi="['system:document:template:add']"
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
          v-hasPermi="['system:document:template:edit']"
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
          v-hasPermi="['system:document:template:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="templateList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="模板ID" align="center" prop="id" />
      <el-table-column label="模板编码" align="center" prop="templateCode" />
      <el-table-column label="模板名称" align="center" prop="templateName" />
      <el-table-column label="模板类型" align="center" prop="templateType">
        <template slot-scope="scope">
          <span>{{ getTemplateTypeLabel(scope.row.templateType) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="所属分类" align="center" prop="category" />
      <el-table-column label="行业分类" align="center" prop="industryCategoryName" />
      <el-table-column label="版本" align="center" prop="version" width="80" />
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
            v-hasPermi="['system:document:template:edit']"
          >编辑</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-setting"
            @click="handleConfig(scope.row)"
            v-hasPermi="['system:document:template:edit']"
          >变量配置</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:document:template:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 添加或修改模板对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="模板编码" prop="templateCode">
          <el-input v-model="form.templateCode" placeholder="请输入模板编码" />
        </el-form-item>
        <el-form-item label="模板名称" prop="templateName">
          <el-input v-model="form.templateName" placeholder="请输入模板名称" />
        </el-form-item>
        <el-form-item label="所属分类" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择所属分类" @change="handleCategoryChange" style="width: 100%">
            <el-option
              v-for="item in categoryOptions"
              :key="item.categoryId"
              :label="item.categoryName"
              :value="item.categoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="行业分类" prop="industryCategoryIds">
          <el-select v-model="form.industryCategoryIds" multiple placeholder="请选择行业分类" style="width: 100%">
            <el-option
              v-for="item in industryCategoryOptions"
              :key="item.categoryId"
              :label="item.categoryName"
              :value="item.categoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="文件URL" prop="fileUrl">
          <el-input v-model="form.fileUrl" placeholder="请输入模板文件URL" />
        </el-form-item>
        <el-form-item label="版本" prop="version">
          <el-input v-model="form.version" placeholder="请输入版本号" />
        </el-form-item>
        <el-form-item label="是否启用" prop="isActive">
          <el-radio-group v-model="form.isActive">
            <el-radio label="1">启用</el-radio>
            <el-radio label="0">禁用</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

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
import { listTemplate, getTemplate, addTemplate, updateTemplate, delTemplate } from "@/api/system/document"
import { listCategory } from "@/api/system/documentCategory"
import { listCategory as listIndustryCategory } from "@/api/system/category"

export default {
  name: "DocumentTemplate",
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
      // 模板表格数据
      templateList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 分类选项
      categoryOptions: [],
      // 行业分类选项
      industryCategoryOptions: [],
      // 模板类型选项（保留用于搜索）
      templateTypeOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        templateName: null,
        categoryId: null,
        industryCategoryId: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        templateName: [
          { required: true, message: "模板名称不能为空", trigger: "blur" }
        ],
        templateCode: [
          { required: true, message: "模板编码不能为空", trigger: "blur" }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.getCategoryOptions()
    this.getIndustryCategoryOptions()
  },
  methods: {
    /** 查询模板列表 */
    getList() {
      this.loading = true
      listTemplate(this.queryParams).then(response => {
        // 处理两种响应结构：response.rows 或 response.data.rows
        this.templateList = response.rows || (response.data && response.data.rows) || []
        this.total = response.total || (response.data && response.data.total) || 0
        this.loading = false
      })
    },
    /** 获取分类选项 */
    getCategoryOptions() {
      listCategory().then(response => {
        this.categoryOptions = response.rows || (response.data && response.data.rows) || []
      })
    },
    /** 获取行业分类选项 */
    getIndustryCategoryOptions() {
      listIndustryCategory().then(response => {
        this.industryCategoryOptions = response.rows || (response.data && response.data.rows) || []
      })
    },
    /** 分类选择变更 */
    handleCategoryChange(categoryId) {
      if (!this.categoryOptions || !categoryId) return
      const category = this.categoryOptions.find(c => c.categoryId === categoryId)
      if (category) {
        this.form.category = category.categoryName
      }
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
        templateCode: null,
        templateName: null,
        categoryId: null,
        category: null,
        industryCategoryIds: [],
        templateType: null,
        fileUrl: null,
        version: '1.0',
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加文书模板"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const templateId = row.id || this.ids
      getTemplate(templateId).then(response => {
        this.form = response.data
        // 设置行业分类ID列表（从中间表关联获取）
        if (response.data.industryCategoryIds) {
          this.form.industryCategoryIds = response.data.industryCategoryIds
        } else {
          this.form.industryCategoryIds = []
        }
        this.open = true
        this.title = "修改文书模板"
      })
    },
    /** 变量配置按钮操作 */
    handleConfig(row) {
      this.$modal.msgInfo('变量配置功能开发中')
      // TODO: 跳转到变量配置页面或打开变量配置对话框
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateTemplate(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            }).catch(() => {
              this.$modal.msgError("修改失败")
            })
          } else {
            addTemplate(this.form).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            }).catch(() => {
              this.$modal.msgError("新增失败")
            })
          }
        }
      })
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除模板编号为"' + ids + '"的数据项？').then(() => {
        return delTemplate(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 获取模板类型标签 */
    getTemplateTypeLabel(value) {
      if (!value) return ''
      // 如果有模板类型选项则查找标签，否则直接返回值
      const item = this.templateTypeOptions.find(opt => opt.value === value)
      return item ? item.label : value
    }
  }
}
</script>
