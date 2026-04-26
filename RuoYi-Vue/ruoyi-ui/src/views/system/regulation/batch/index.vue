<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="法律名称" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入法律名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="法律类型" prop="legalType">
        <el-select v-model="queryParams.legalType" placeholder="请选择法律类型" clearable>
          <el-option label="法律" value="法律" />
          <el-option label="法规" value="法规" />
          <el-option label="规章" value="规章" />
          <el-option label="规范性文件" value="规范性文件" />
          <el-option label="批复文件" value="批复文件" />
          <el-option label="标准" value="标准" />
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
          type="warning"
          plain
          icon="el-icon-upload"
          size="mini"
          @click="handleImportExcel"
          v-hasPermi="['system:regulation:import']"
        >导入Excel</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExportExcel"
          v-hasPermi="['system:regulation:export']"
        >导出Excel</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-upload2"
          size="mini"
          @click="handleImportJson"
          v-hasPermi="['system:regulation:importJson']"
        >导入JSON</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="success"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExportJson"
          v-hasPermi="['system:regulation:exportJson']"
        >导出JSON</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="regulationList">
      <el-table-column label="法律法规ID" align="center" prop="regulationId" width="80" />
      <el-table-column label="法律名称" align="center" prop="title" show-overflow-tooltip />
      <el-table-column label="法律类型" align="center" prop="legalType" width="100" />
      <el-table-column label="监管类型" align="center" prop="supervisionTypes" show-overflow-tooltip />
      <el-table-column label="发布日期" align="center" prop="publishDate" width="120" />
      <el-table-column label="实施日期" align="center" prop="effectiveDate" width="120" />
      <el-table-column label="颁发机构" align="center" prop="issuingAuthority" show-overflow-tooltip />
    </el-table>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />

    <!-- 导入Excel对话框 -->
    <el-dialog title="导入Excel" :visible.sync="importExcelOpen" width="500px" append-to-body>
      <el-upload
        ref="uploadExcel"
        :limit="1"
        accept=".xlsx,.xls"
        :headers="upload.headers"
        :action="upload.importExcelUrl"
        :disabled="upload.isUploading"
        :on-progress="handleExcelUploadProgress"
        :on-success="handleExcelUploadSuccess"
        :on-error="handleExcelUploadError"
        :auto-upload="false"
        drag
      >
        <i class="el-icon-upload"></i>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
        <div class="el-upload__tip" slot="tip">
          <el-checkbox v-model="upload.updateSupport" />是否更新已存在的数据
          <el-link type="info" :underline="false" style="font-size:12px;vertical-align: baseline;" @click="importTemplate">下载模板</el-link>
        </div>
      </el-upload>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitExcelUpload">确 定</el-button>
        <el-button @click="importExcelOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 导入JSON对话框 -->
    <el-dialog title="导入JSON" :visible.sync="importJsonOpen" width="600px" append-to-body>
      <el-form :model="jsonForm" :rules="jsonRules" ref="jsonFormRef" label-width="100px">
        <el-form-item label="JSON数据" prop="jsonData">
          <el-input
            v-model="jsonForm.jsonData"
            type="textarea"
            :rows="10"
            placeholder="请输入JSON数据"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitJsonImport">确 定</el-button>
        <el-button @click="importJsonOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRegulation, importRegulation, importRegulationJson, exportRegulationJson } from "@/api/system/regulation"
import { getToken } from "@/utils/auth"

export default {
  name: "RegulationBatch",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 法律法规表格数据
      regulationList: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        title: null,
        legalType: null,
      },
      // 导入Excel对话框
      importExcelOpen: false,
      // 导入JSON对话框
      importJsonOpen: false,
      // JSON表单
      jsonForm: {
        jsonData: ''
      },
      jsonRules: {
        jsonData: [
          { required: true, message: "JSON数据不能为空", trigger: "blur" }
        ]
      },
      // 上传参数
      upload: {
        isUploading: false,
        updateSupport: false,
        importExcelUrl: process.env.VUE_APP_BASE_API + "/system/regulation/import"
      },
      // 上传头
      headers: {
        Authorization: "Bearer " + getToken()
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询法律法规列表 */
    getList() {
      this.loading = true
      listRegulation(this.queryParams).then(response => {
        this.regulationList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
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
    /** 导入Excel按钮操作 */
    handleImportExcel() {
      this.importExcelOpen = true
    },
    /** 提交Excel上传 */
    submitExcelUpload() {
      this.$refs.uploadExcel.submit()
    },
    /** Excel上传进度 */
    handleExcelUploadProgress() {
      this.upload.isUploading = true
    },
    /** Excel上传成功 */
    handleExcelUploadSuccess(response) {
      this.upload.isUploading = false
      this.$refs.uploadExcel.clearFiles()
      this.$modal.msgSuccess(response.msg || "导入成功")
      this.importExcelOpen = false
      this.getList()
    },
    /** Excel上传失败 */
    handleExcelUploadError(response) {
      this.upload.isUploading = false
      this.$refs.uploadExcel.clearFiles()
      this.$modal.msgError("导入失败")
    },
    /** 下载模板 */
    importTemplate() {
      this.download('/system/regulation/importTemplate', {
      }, `regulation_template.xlsx`)
    },
    /** 导出Excel */
    handleExportExcel() {
      this.$modal.confirm('是否导出所有法律法规数据?').then(() => {
        this.download('/system/regulation/export', {
          ...this.queryParams
        }, `regulation_${new Date().getTime()}.xlsx`)
      })
    },
    /** 导入JSON按钮操作 */
    handleImportJson() {
      this.jsonForm.jsonData = ''
      this.importJsonOpen = true
    },
    /** 提交JSON导入 */
    submitJsonImport() {
      this.$refs.jsonFormRef.validate(valid => {
        if (valid) {
          try {
            const jsonData = JSON.parse(this.jsonForm.jsonData)
            importRegulationJson(jsonData).then(response => {
              this.$modal.msgSuccess("导入成功")
              this.importJsonOpen = false
              this.getList()
            })
          } catch (e) {
            this.$modal.msgError("JSON格式不正确: " + e.message)
          }
        }
      })
    },
    /** 导出JSON */
    handleExportJson() {
      this.$modal.confirm('是否导出所有法律法规数据为JSON?').then(() => {
        this.download('/system/regulation/export/json', {
          ...this.queryParams
        }, `regulation_${new Date().getTime()}.json`)
      })
    }
  }
}
</script>
