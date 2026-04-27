<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="编号" prop="basisNo">
        <el-input
          v-model="queryParams.basisNo"
          placeholder="请输入编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="标题" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入标题"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="违法类型" prop="violationType">
        <el-input
          v-model="queryParams.violationType"
          placeholder="请输入违法类型"
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
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="dataList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="basisNo" width="100" />
      <el-table-column label="标题" align="center" prop="title" show-overflow-tooltip />
      <el-table-column label="违法类型" align="center" prop="violationType" />
      <el-table-column label="颁发机构" align="center" prop="issuingAuthority" show-overflow-tooltip />
      <el-table-column label="法律层级" align="center" prop="legalLevel" />
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
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
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

    <!-- 添加或修改处理依据对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="编号" prop="basisNo">
              <el-input v-model="form.basisNo" placeholder="请输入编号" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="法律层级" prop="legalLevel">
              <el-select v-model="form.legalLevel" placeholder="请选择法律层级" style="width: 100%">
                <el-option label="法律" value="法律" />
                <el-option label="行政法规" value="行政法规" />
                <el-option label="部门规章" value="部门规章" />
                <el-option label="地方性法规" value="地方性法规" />
                <el-option label="其他" value="其他" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="标题" prop="title">
              <el-input v-model="form.title" placeholder="请输入标题" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="违法类型" prop="violationType">
              <el-input v-model="form.violationType" placeholder="请输入违法类型" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="颁发机构" prop="issuingAuthority">
              <el-input v-model="form.issuingAuthority" placeholder="请输入颁发机构" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实施时间" prop="effectiveDate">
              <el-date-picker
                v-model="form.effectiveDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="关联法规" prop="regulationId">
              <el-input v-model="form.regulationId" placeholder="请输入关联法规ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="条款内容">
              <el-input v-model="form.clauses" type="textarea" :rows="3" placeholder="请输入条款内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="法律责任">
              <el-input v-model="form.legalLiability" type="textarea" :rows="3" placeholder="请输入法律责任" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="裁量标准">
              <el-input v-model="form.discretionStandard" type="textarea" :rows="3" placeholder="请输入裁量标准" />
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
            <el-form-item label="备注">
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

    <!-- 处理依据详情对话框 -->
    <el-dialog title="处理依据详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="编号">{{ detailData.basisNo }}</el-descriptions-item>
        <el-descriptions-item label="法律层级">{{ detailData.legalLevel }}</el-descriptions-item>
        <el-descriptions-item label="标题" :span="2">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="违法类型">{{ detailData.violationType }}</el-descriptions-item>
        <el-descriptions-item label="颁发机构">{{ detailData.issuingAuthority }}</el-descriptions-item>
        <el-descriptions-item label="实施时间">{{ detailData.effectiveDate }}</el-descriptions-item>
        <el-descriptions-item label="关联法规ID">{{ detailData.regulationId }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailData.status === '0'" type="success">正常</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="条款内容" :span="2">{{ detailData.clauses }}</el-descriptions-item>
        <el-descriptions-item label="法律责任" :span="2">{{ detailData.legalLiability }}</el-descriptions-item>
        <el-descriptions-item label="裁量标准" :span="2">{{ detailData.discretionStandard }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listProcessingBasis, getProcessingBasis, delProcessingBasis, addProcessingBasis, updateProcessingBasis } from "@/api/system/processingBasis"

export default {
  name: "ProcessingBasis",
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
      // 处理依据表格数据
      dataList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 详情弹出层
      detailOpen: false,
      // 详情数据
      detailData: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        basisNo: null,
        title: null,
        violationType: null,
        status: null,
      },
      // 表单参数
      form: {},
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
  },
  methods: {
    /** 查询处理依据列表 */
    getList() {
      this.loading = true
      listProcessingBasis(this.queryParams).then(response => {
        this.dataList = response.rows || []
        this.total = response.total || 0
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
        basisId: null,
        basisNo: null,
        title: null,
        violationType: null,
        issuingAuthority: null,
        effectiveDate: null,
        legalLevel: null,
        clauses: null,
        legalLiability: null,
        discretionStandard: null,
        regulationId: null,
        status: "0",
        delFlag: null,
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
      this.title = "添加处理依据"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const basisId = row.basisId || this.ids
      getProcessingBasis(basisId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改处理依据"
      })
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      getProcessingBasis(row.basisId).then(response => {
        this.detailData = response.data || {}
        this.detailOpen = true
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.basisId != null) {
            updateProcessingBasis(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addProcessingBasis(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除处理依据编号为"' + basisIds + '"的数据项？').then(function() {
        return delProcessingBasis(basisIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
  }
}
</script>
