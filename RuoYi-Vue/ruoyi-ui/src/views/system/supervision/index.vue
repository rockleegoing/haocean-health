<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="事项编码" prop="itemNo">
        <el-input
          v-model="queryParams.itemNo"
          placeholder="请输入事项编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="事项名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入事项名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="监管类型" prop="categoryId">
        <el-select v-model="queryParams.categoryId" placeholder="请选择监管类型" clearable>
          <el-option
            v-for="item in categoryList"
            :key="item.categoryId"
            :label="item.categoryName"
            :value="item.categoryId"
          />
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
          v-hasPermi="['system:supervision:add']"
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
          v-hasPermi="['system:supervision:edit']"
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
          v-hasPermi="['system:supervision:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:supervision:export']"
        >导出</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-upload
          :show-file-list="false"
          :before-upload="beforeUpload"
          :disabled="uploadLoading"
          action="#"
          accept=".xlsx,.xls"
        >
          <el-button size="mini" type="warning" plain icon="el-icon-upload2" :loading="uploadLoading" v-hasPermi="['system:supervision:import']">
            导入
          </el-button>
        </el-upload>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="supervisionList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="事项ID" align="center" prop="itemId" />
      <el-table-column label="事项编码" align="center" prop="itemNo" />
      <el-table-column label="事项名称" align="center" prop="name" />
      <el-table-column label="监管类型" align="center" prop="categoryName" />
      <el-table-column label="监管要求" align="center" prop="description" :show-overflow-tooltip="true" />
      <el-table-column label="法律依据" align="center" prop="legalBasis" :show-overflow-tooltip="true" />
      <el-table-column label="排序" align="center" prop="sortOrder" width="60" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success">正常</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['system:supervision:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:supervision:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:supervision:remove']"
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

    <!-- 添加或修改监管事项对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="事项编码" prop="itemNo">
          <el-input v-model="form.itemNo" placeholder="请输入事项编码" />
        </el-form-item>
        <el-form-item label="事项名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入事项名称" />
        </el-form-item>
        <el-form-item label="监管类型" prop="categoryId">
          <el-select v-model="form.categoryId" placeholder="请选择监管类型" style="width: 100%">
            <el-option
              v-for="item in categoryList"
              :key="item.categoryId"
              :label="item.categoryName"
              :value="item.categoryId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="父级事项" prop="parentId">
          <treeselect
            v-model="form.parentId"
            :options="treeOptions"
            :normalizer="normalizer"
            :show-count="true"
            placeholder="请选择父级事项（不选则为一级）"
          />
        </el-form-item>
        <el-form-item label="监管要求" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入监管要求描述" />
        </el-form-item>
        <el-form-item label="法律依据" prop="legalBasis">
          <el-input v-model="form.legalBasis" type="textarea" :rows="3" placeholder="请输入法律依据" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" controls-position="right" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-radio-group v-model="form.status">
            <el-radio label="0">正常</el-radio>
            <el-radio label="1">停用</el-radio>
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

    <!-- 监管事项详情对话框 -->
    <el-dialog title="监管事项详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="事项编码">{{ detailData.itemNo }}</el-descriptions-item>
        <el-descriptions-item label="事项名称">{{ detailData.name }}</el-descriptions-item>
        <el-descriptions-item label="监管类型">{{ detailData.categoryName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailData.status === '0'" type="success">正常</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="监管要求" :span="2">{{ detailData.description }}</el-descriptions-item>
        <el-descriptions-item label="法律依据" :span="2">{{ detailData.legalBasis }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>

      <el-divider>关联规范用语</el-divider>
      <el-table :data="languageLinks" size="small">
        <el-table-column label="规范用语名称" prop="languageName" />
        <el-table-column label="内容" prop="languageContent" :show-overflow-tooltip="true" />
        <el-table-column label="操作" width="80" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDeleteLanguageLink(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 8px;">
        <el-button size="mini" type="primary" plain icon="el-icon-plus" @click="showAddLanguageDialog">添加规范用语</el-button>
      </div>

      <el-divider>关联法律法规</el-divider>
      <el-table :data="regulationLinks" size="small">
        <el-table-column label="法规名称" prop="regulationName" />
        <el-table-column label="法规编码" prop="lawCode" />
        <el-table-column label="操作" width="80" align="center">
          <template slot-scope="scope">
            <el-button
              size="mini"
              type="text"
              icon="el-icon-delete"
              @click="handleDeleteRegulationLink(scope.row)"
            >删除</el-button>
          </template>
        </el-table-column>
      </el-table>
      <div style="margin-top: 8px;">
        <el-button size="mini" type="primary" plain icon="el-icon-plus" @click="showAddRegulationDialog">添加法律法规</el-button>
      </div>

      <div slot="footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <!-- 添加规范用语对话框 -->
    <el-dialog title="添加规范用语关联" :visible.sync="addLanguageOpen" width="400px" append-to-body>
      <el-form :model="linkForm" label-width="100px">
        <el-form-item label="规范用语ID">
          <el-input v-model="linkForm.languageId" placeholder="请输入规范用语ID" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="handleAddLanguageLink">确 定</el-button>
        <el-button @click="addLanguageOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 添加法律法规对话框 -->
    <el-dialog title="添加法律法规关联" :visible.sync="addRegulationOpen" width="400px" append-to-body>
      <el-form :model="linkForm" label-width="100px">
        <el-form-item label="法律法规ID">
          <el-input v-model="linkForm.regulationId" placeholder="请输入法律法规ID" />
        </el-form-item>
      </el-form>
      <div slot="footer">
        <el-button type="primary" @click="handleAddRegulationLink">确 定</el-button>
        <el-button @click="addRegulationOpen = false">取 消</el-button>
      </div>
    </el-dialog>
    </el-dialog>
  </div>
</template>

<script>
import { listSupervision, getSupervision, delSupervision, addSupervision, updateSupervision, addLanguageLink, delLanguageLink, addRegulationLink, delRegulationLink } from "@/api/system/supervision"
import { listAllCategory } from "@/api/system/supervision"
import Treeselect from "@riophae/vue-treeselect"
import "@riophae/vue-treeselect/dist/vue-treeselect.css"

export default {
  name: "Supervision",
  components: { Treeselect },
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
      // 监管事项表格数据
      supervisionList: [],
      // 监管类型列表
      categoryList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 详情弹出层
      detailOpen: false,
      // 详情数据
      detailData: {},
      // 规范用语关联
      languageLinks: [],
      // 法律法规关联
      regulationLinks: [],
      // 关联操作弹窗
      addLanguageOpen: false,
      addRegulationOpen: false,
      linkForm: {
        languageId: null,
        regulationId: null
      },
      // 当前操作的关联ID
      currentItemId: null,
      // 树形选项
      treeOptions: [],
      // 导入 loading
      uploadLoading: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        itemNo: null,
        name: null,
        categoryId: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [
          { required: true, message: "事项名称不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
    this.getCategoryList()
  },
  methods: {
    /** 查询监管事项列表 */
    getList() {
      this.loading = true
      listSupervision(this.queryParams).then(response => {
        this.supervisionList = response.rows
        this.total = response.total
        this.loading = false
      })
    },
    /** 查询监管类型列表 */
    getCategoryList() {
      listAllCategory().then(response => {
        this.categoryList = response.data || []
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
        itemId: null,
        itemNo: null,
        name: null,
        parentId: 0,
        categoryId: null,
        description: null,
        legalBasis: null,
        sortOrder: 0,
        status: "0",
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
      this.ids = selection.map(item => item.itemId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加监管事项"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const itemId = row.itemId || this.ids
      getSupervision(itemId).then(response => {
        this.form = response.data.item
        this.open = true
        this.title = "修改监管事项"
      })
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.currentItemId = row.itemId
      getSupervision(row.itemId).then(response => {
        this.detailData = response.data.item || {}
        this.languageLinks = response.data.languageLinks || []
        this.regulationLinks = response.data.regulationLinks || []
        this.detailOpen = true
      })
    },
    /** 显示添加规范用语弹窗 */
    showAddLanguageDialog() {
      this.linkForm.languageId = null
      this.addLanguageOpen = true
    },
    /** 显示添加法律法规弹窗 */
    showAddRegulationDialog() {
      this.linkForm.regulationId = null
      this.addRegulationOpen = true
    },
    /** 添加规范用语关联 */
    handleAddLanguageLink() {
      if (!this.linkForm.languageId) {
        this.$modal.msgError('请输入规范用语ID')
        return
      }
      // 注意：需要实现实际的添加接口
      // addLanguageLink(this.currentItemId, this.linkForm.languageId).then(() => {
      //   this.$modal.msgSuccess('添加成功')
      //   this.addLanguageOpen = false
      //   this.handleDetail({ itemId: this.currentItemId })
      // })
      this.$modal.msgInfo('添加规范用语功能需要实现后端接口')
      this.addLanguageOpen = false
    },
    /** 添加法律法规关联 */
    handleAddRegulationLink() {
      if (!this.linkForm.regulationId) {
        this.$modal.msgError('请输入法律法规ID')
        return
      }
      // 注意：需要实现实际的添加接口
      // addRegulationLink(this.currentItemId, this.linkForm.regulationId).then(() => {
      //   this.$modal.msgSuccess('添加成功')
      //   this.addRegulationOpen = false
      //   this.handleDetail({ itemId: this.currentItemId })
      // })
      this.$modal.msgInfo('添加法律法规功能需要实现后端接口')
      this.addRegulationOpen = false
    },
    /** 删除规范用语关联 */
    handleDeleteLanguageLink(row) {
      // 注意：需要实现实际的删除接口
      // this.$modal.confirm('是否确认删除该规范用语关联？').then(() => {
      //   delLanguageLink(row.linkId).then(() => {
      //     this.$modal.msgSuccess('删除成功')
      //     this.handleDetail({ itemId: this.currentItemId })
      //   })
      // })
      this.$modal.msgInfo('删除规范用语功能需要实现后端接口')
    },
    /** 删除法律法规关联 */
    handleDeleteRegulationLink(row) {
      // 注意：需要实现实际的删除接口
      // this.$modal.confirm('是否确认删除该法律法规关联？').then(() => {
      //   delRegulationLink(row.linkId).then(() => {
      //     this.$modal.msgSuccess('删除成功')
      //     this.handleDetail({ itemId: this.currentItemId })
      //   })
      // })
      this.$modal.msgInfo('删除法律法规功能需要实现后端接口')
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.itemId != null) {
            updateSupervision(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addSupervision(this.form).then(response => {
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
      const itemIds = row.itemId || this.ids
      this.$modal.confirm('是否确认删除监管事项编号为"' + itemIds + '"的数据项？').then(function() {
        return delSupervision(itemIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 树形结构标准化 */
    normalizer(node) {
      return {
        id: node.itemId,
        label: node.name,
        children: node.children
      }
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('/api/admin/supervision/export', {
        ...this.queryParams
      }, `supervision_item_${new Date().getTime()}.xlsx`)
    },
    /** 导入按钮操作 */
    beforeUpload(file) {
      const isExcel = file.name.endsWith('.xlsx') || file.name.endsWith('.xls')
      const isSize = file.size / 1024 / 1024 < 10
      if (!isExcel) {
        this.$modal.msgError('只能上传 Excel 文件！')
        return false
      }
      if (!isSize) {
        this.$modal.msgError('文件大小不能超过 10MB！')
        return false
      }
      this.uploadLoading = true
      const formData = new FormData()
      formData.append('file', file)
      // 注意：需要实现实际的导入接口
      // importSupervision(formData).then(response => {
      //   this.$modal.msgSuccess('导入成功')
      //   this.uploadLoading = false
      //   this.getList()
      // }).catch(() => {
      //   this.uploadLoading = false
      // })
      this.$modal.msgInfo('导入功能需要实现后端接口')
      this.uploadLoading = false
      return false
    }
  }
}
</script>
