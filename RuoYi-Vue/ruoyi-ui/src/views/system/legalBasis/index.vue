<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入标题"
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
          v-hasPermi="['system:legalBasis:add']"
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
          v-hasPermi="['system:legalBasis:edit']"
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
          v-hasPermi="['system:legalBasis:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:legalBasis:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="legalBasisList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="定性依据ID" align="center" prop="basisId" />
      <el-table-column label="标题" align="center" prop="title" show-overflow-tooltip />
      <el-table-column label="关联法规" align="center">
        <template slot-scope="scope">
          {{ getRegulationName(scope.row.regulationId) }}
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status">
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
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:legalBasis:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:legalBasis:remove']"
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

    <!-- 添加或修改定性依据对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body @opened="onDialogOpened">
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入标题" maxlength="100" show-word-limit />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联法规" prop="regulationId">
              <el-select v-model="form.regulationId" placeholder="请选择关联法规" filterable style="width: 100%">
                <el-option
                  v-for="item in regulationOptions"
                  :key="item.regulationId"
                  :label="item.title"
                  :value="item.regulationId"
                />
              </el-select>
            </el-form-item>
          </el-col>
        </el-row>
        <el-row :gutter="20">
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
        </el-row>
        <el-divider content-position="left">内容明细</el-divider>
        <div class="content-table">
          <el-table ref="contentTable" :data="form.contents" border size="small">
            <el-table-column label="排序" width="100" align="center">
              <template slot-scope="scope">
                <el-input-number v-model="scope.row.sortOrder" :min="1" :max="99" size="mini" controls-position="right" style="width: 80px;" />
              </template>
            </el-table-column>
            <el-table-column label="标签" width="150">
              <template slot-scope="scope">
                <el-input v-model="scope.row.label" placeholder="如：编号、违法类型" maxlength="50" />
              </template>
            </el-table-column>
            <el-table-column label="内容">
              <template slot-scope="scope">
                <el-input v-model="scope.row.content" type="textarea" :rows="2" placeholder="请输入内容" maxlength="500" show-word-limit />
              </template>
            </el-table-column>
            <el-table-column label="操作" width="60" align="center">
              <template slot-scope="scope">
                <el-button type="danger" icon="el-icon-delete" size="mini" @click="removeContent(scope.$index)" circle />
              </template>
            </el-table-column>
          </el-table>
          <div class="content-add-btn">
            <el-button type="primary" plain icon="el-icon-plus" size="small" @click="addContent">新增内容行</el-button>
          </div>
        </div>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 定性依据详情对话框 -->
    <el-dialog title="定性依据详情" :visible.sync="detailOpen" width="800px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="标题">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="关联法规">{{ getRegulationName(detailData.regulationId) }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailData.status === '0'" type="success">正常</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="备注">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
      <el-divider content-position="left">内容</el-divider>
      <el-table :data="detailData.contents || []" border>
        <el-table-column label="标签" prop="label" width="150" />
        <el-table-column label="内容" prop="content" show-overflow-tooltip />
        <el-table-column label="排序" prop="sortOrder" width="80" align="center" />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import Sortable from 'sortablejs'
import { listLegalBasis, getLegalBasis, delLegalBasis, addLegalBasis, updateLegalBasis, getLegalBasisByRegulation } from "@/api/system/regulation"
import { listRegulation } from "@/api/system/regulation"

export default {
  name: "LegalBasis",
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
      // 定性依据表格数据
      legalBasisList: [],
      // 关联法规选项
      regulationOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 详情弹出层
      detailOpen: false,
      // 详情数据
      detailData: {},
      // 表格拖拽实例
      sortable: null,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        status: null,
      },
      // 表单参数
      form: {
        basisId: undefined,
        title: '',
        regulationId: null,
        status: '0',
        remark: '',
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
      },
      // 表单校验
      rules: {
        title: [
          { required: true, message: "标题不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
    this.getRegulationOptions()
  },
  methods: {
    /** 对话框打开后初始化拖拽 */
    onDialogOpened() {
      this.initSortable()
    },
    /** 初始化表格拖拽 */
    initSortable() {
      if (this.sortable) {
        this.sortable.destroy()
      }
      const table = this.$refs.contentTable
      if (!table) return
      const el = table.$el.querySelector('.el-table__body-wrapper tbody')
      if (!el) return
      this.sortable = Sortable.create(el, {
        handle: '.el-table__row',
        animation: 150,
        onEnd: ({ oldIndex, newIndex }) => {
          if (oldIndex === newIndex) return
          // 复制数组并重新排序
          const contents = [...this.form.contents]
          const row = contents.splice(oldIndex, 1)[0]
          contents.splice(newIndex, 0, row)
          // 更新 sortOrder
          contents.forEach((item, index) => {
            item.sortOrder = index + 1
          })
          this.form.contents = contents
          this.$nextTick(() => {
            this.$refs.contentTable.doLayout()
          })
        }
      })
    },
    /** 查询定性依据列表 */
    getList() {
      this.loading = true
      listLegalBasis(this.queryParams).then(response => {
        this.legalBasisList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
    },
    /** 获取关联法规选项 */
    getRegulationOptions() {
      listRegulation({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.regulationOptions = response.rows || []
      })
    },
    /** 根据ID获取法规名称 */
    getRegulationName(regulationId) {
      if (!regulationId || !this.regulationOptions.length) return ''
      const regulation = this.regulationOptions.find(r => r.regulationId === regulationId)
      return regulation ? regulation.title : ''
    },
    // 取消按钮
    cancel() {
      if (this.sortable) {
        this.sortable.destroy()
        this.sortable = null
      }
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        basisId: undefined,
        title: '',
        regulationId: null,
        status: '0',
        remark: '',
        contents: [
          { sortOrder: 1, label: '编号', content: '' },
          { sortOrder: 2, label: '违法类型', content: '' },
          { sortOrder: 3, label: '颁发机构', content: '' },
          { sortOrder: 4, label: '实施时间', content: '' },
          { sortOrder: 5, label: '效级', content: '' },
          { sortOrder: 6, label: '条款内容', content: '' },
          { sortOrder: 7, label: '法律责任', content: '' },
          { sortOrder: 8, label: '裁量标准', content: '' }
        ]
      }
      this.resetForm("form")
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    /** 新增内容行 */
    addContent() {
      const newSortOrder = this.form.contents.length > 0
        ? Math.max(...this.form.contents.map(c => c.sortOrder || 0)) + 1
        : 1
      this.form.contents.push({ sortOrder: newSortOrder, label: '', content: '' })
    },
    /** 删除内容行 */
    removeContent(index) {
      this.form.contents.splice(index, 1)
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.basisId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加定性依据"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      const basisId = row.basisId || this.ids
      getLegalBasis(basisId).then(response => {
        const result = response.data  // {basis: {...}, contents: [...]}
        const formData = result.basis || {}  // 主表数据
        formData.contents = result.contents || []  // 合并内容

        // 确保 contents 每项都有 sortOrder
        if (formData.contents.length > 0) {
          formData.contents = formData.contents.map((c, index) => ({
            ...c,
            sortOrder: c.sortOrder ?? (index + 1)
          }))
        }

        this.form = formData
        this.open = true
        this.title = "修改定性依据"
      })
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      getLegalBasis(row.basisId).then(response => {
        const result = response.data  // {basis: {...}, contents: [...]}
        this.detailData = result.basis || {}
        this.detailData.contents = result.contents || []
        this.detailOpen = true
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.basisId != null) {
            updateLegalBasis(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addLegalBasis(this.form).then(response => {
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
      const basisIds = row.basisId || this.ids
      this.$modal.confirm('是否确认删除定性依据编号为"' + basisIds + '"的数据项？').then(function() {
        return delLegalBasis(basisIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/legalBasis/export', {
        ...this.queryParams
      }, `legalBasis_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>

<style scoped>
.content-table {
  margin-top: 10px;
}
.content-add-btn {
  margin-top: 12px;
  text-align: left;
}
</style>
