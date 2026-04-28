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
          v-hasPermi="['system:processingBasis:add']"
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
          v-hasPermi="['system:processingBasis:edit']"
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
          v-hasPermi="['system:processingBasis:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="basisList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="编号" align="center" prop="basisNo" width="100" />
      <el-table-column label="标题" align="center" prop="title" :show-overflow-tooltip="true" />
      <el-table-column label="违法类型" align="center" prop="violationType" width="150" />
      <el-table-column label="颁发机构" align="center" prop="issuingAuthority" width="150" />
      <el-table-column label="效级" align="center" prop="legalLevel" width="100" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success">正常</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="180" />
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
            v-hasPermi="['system:processingBasis:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:processingBasis:remove']"
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
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="编号" prop="basisNo">
          <el-input v-model="form.basisNo" placeholder="请输入编号" />
        </el-form-item>
        <el-form-item label="标题" prop="title">
          <el-input v-model="form.title" placeholder="请输入标题" />
        </el-form-item>
        <el-form-item label="违法类型" prop="violationType">
          <el-input v-model="form.violationType" placeholder="请输入违法类型" />
        </el-form-item>
        <el-form-item label="颁发机构" prop="issuingAuthority">
          <el-input v-model="form.issuingAuthority" placeholder="请输入颁发机构" />
        </el-form-item>
        <el-form-item label="实施时间" prop="effectiveDate">
          <el-input v-model="form.effectiveDate" placeholder="请输入实施时间" />
        </el-form-item>
        <el-form-item label="效级" prop="legalLevel">
          <el-input v-model="form.legalLevel" placeholder="请输入效级" />
        </el-form-item>
        <el-form-item label="条款内容" prop="clauses">
          <el-input v-model="form.clauses" type="textarea" placeholder="请输入条款内容" />
        </el-form-item>
        <!-- 动态内容行 -->
        <el-form-item label="内容行" prop="contents">
          <div v-for="(content, index) in form.contents" :key="index" class="content-row">
            <el-input v-model="content.clause" placeholder="条款" style="width: 200px; margin-right: 10px" />
            <el-input v-model="content.legalLiability" placeholder="法律责任" style="width: 200px; margin-right: 10px" />
            <el-input v-model="content.discretionStandard" placeholder="裁量标准" style="width: 200px; margin-right: 10px" />
            <el-button type="danger" icon="el-icon-delete" size="mini" @click="removeContent(index)">删除</el-button>
          </div>
          <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="addContent">添加内容行</el-button>
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

    <!-- 处理依据详情对话框 -->
    <el-dialog title="处理依据详情" :visible.sync="detailOpen" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="编号">{{ detailData.basisNo }}</el-descriptions-item>
        <el-descriptions-item label="违法类型">{{ detailData.violationType }}</el-descriptions-item>
        <el-descriptions-item label="标题">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="颁发机构">{{ detailData.issuingAuthority }}</el-descriptions-item>
        <el-descriptions-item label="实施时间">{{ detailData.effectiveDate }}</el-descriptions-item>
        <el-descriptions-item label="效级">{{ detailData.legalLevel }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailData.status === '0'" type="success">正常</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
        <el-descriptions-item label="条款内容" :span="2">{{ detailData.clauses }}</el-descriptions-item>
        <el-descriptions-item label="法律责任" :span="2">{{ detailData.legalLiability }}</el-descriptions-item>
        <el-descriptions-item label="裁量标准" :span="2">{{ detailData.discretionStandard }}</el-descriptions-item>
        <el-descriptions-item label="内容行" :span="2">
          <div v-if="detailData.contents && detailData.contents.length > 0">
            <div v-for="(content, index) in detailData.contents" :key="index" style="margin-bottom: 5px;">
              <span>条款: {{ content.clause }}</span> |
              <span>法律责任: {{ content.legalLiability }}</span> |
              <span>裁量标准: {{ content.discretionStandard }}</span>
            </div>
          </div>
          <span v-else>-</span>
        </el-descriptions-item>
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
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      basisList: [],
      title: "",
      open: false,
      detailOpen: false,
      detailData: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        basisNo: null,
        title: null,
        violationType: null,
        status: null
      },
      form: {},
      rules: {
        basisNo: [
          { required: true, message: "编号不能为空", trigger: "blur" }
        ],
        title: [
          { required: true, message: "标题不能为空", trigger: "blur" }
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
      listProcessingBasis(this.queryParams).then(response => {
        this.basisList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
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
        status: "0",
        remark: null,
        contents: []
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
      this.ids = selection.map(item => item.basisId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加处理依据"
    },
    handleUpdate(row) {
      this.reset()
      const basisId = row.basisId || this.ids
      getProcessingBasis(basisId).then(response => {
        this.form = response.data || {}
        // 将内容行JSON字符串解析为数组
        if (this.form.contents && typeof this.form.contents === 'string') {
          try {
            this.form.contents = JSON.parse(this.form.contents)
          } catch (e) {
            this.form.contents = []
          }
        } else if (!this.form.contents) {
          this.form.contents = []
        }
        this.open = true
        this.title = "修改处理依据"
      })
    },
    handleDetail(row) {
      getProcessingBasis(row.basisId).then(response => {
        this.detailData = response.data || {}
        // 将内容行JSON字符串解析为数组以便显示
        if (this.detailData.contents && typeof this.detailData.contents === 'string') {
          try {
            this.detailData.contents = JSON.parse(this.detailData.contents)
          } catch (e) {
            // 保持原样
          }
        }
        this.detailOpen = true
      })
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          // 将内容行数组转换为JSON字符串
          const data = { ...this.form }
          if (Array.isArray(data.contents)) {
            data.contents = JSON.stringify(data.contents)
          }
          if (this.form.basisId != null) {
            updateProcessingBasis(data).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addProcessingBasis(data).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.open = false
              this.getList()
            })
          }
        }
      })
    },
    handleDelete(row) {
      const basisIds = row.basisId || this.ids
      this.$modal.confirm('是否确认删除处理依据编号为"' + basisIds + '"的数据项？').then(() => {
        return delProcessingBasis(basisIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 添加内容行 */
    addContent() {
      if (!this.form.contents) {
        this.form.contents = []
      }
      this.form.contents.push({
        clause: null,
        legalLiability: null,
        discretionStandard: null
      })
    },
    /** 删除内容行 */
    removeContent(index) {
      this.form.contents.splice(index, 1)
    }
  }
}
</script>
