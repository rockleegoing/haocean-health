<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="100px">
      <el-form-item label="专业类别" prop="professionalCategory">
        <el-select v-model="queryParams.professionalCategory" placeholder="请选择专业类别" clearable @change="onProfessionalChange" style="width: 150px;">
          <el-option v-for="item in professionalOptions" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="一级分类" prop="primaryCategory">
        <el-select v-model="queryParams.primaryCategory" placeholder="请选择一级分类" clearable @change="onPrimaryChange" style="width: 150px;">
          <el-option v-for="item in primaryOptions" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="二级分类" prop="secondaryCategory">
        <el-select v-model="queryParams.secondaryCategory" placeholder="请选择二级分类" clearable @change="onSecondaryChange" style="width: 150px;">
          <el-option v-for="item in secondaryOptions" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="三级分类" prop="tertiaryCategory">
        <el-select v-model="queryParams.tertiaryCategory" placeholder="请选择三级分类" clearable style="width: 150px;">
          <el-option v-for="item in tertiaryOptions" :key="item.code" :label="item.name" :value="item.code" />
        </el-select>
      </el-form-item>
      <el-form-item label="规范用语代码" prop="standardCode">
        <el-input
          v-model="queryParams.standardCode"
          placeholder="请输入规范用语代码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="违法事实编码" prop="violationCode">
        <el-input
          v-model="queryParams.violationCode"
          placeholder="请输入违法事实编码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="信息报告" prop="informationReport">
        <el-input
          v-model="queryParams.informationReport"
          placeholder="请输入信息报告"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="定性依据" prop="qualitativeBasis">
        <el-input
          v-model="queryParams.qualitativeBasis"
          placeholder="请输入定性依据"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="处理依据" prop="handlingBasis">
        <el-input
          v-model="queryParams.handlingBasis"
          placeholder="请输入处理依据"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="处理程序" prop="handlingProcedure">
        <el-input
          v-model="queryParams.handlingProcedure"
          placeholder="请输入处理程序"
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
          v-hasPermi="['system:language:add']"
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
          v-hasPermi="['system:language:edit']"
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
          v-hasPermi="['system:language:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:language:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="languageList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="专业类别" align="center" prop="professionalCategory" width="100" />
      <el-table-column label="一级分类" align="center" prop="primaryCategory" width="100" />
      <el-table-column label="二级分类" align="center" prop="secondaryCategory" width="100" />
      <el-table-column label="三级分类" align="center" prop="tertiaryCategory" width="100" />
      <el-table-column label="规范用语代码" align="center" prop="standardCode" width="150" />
      <el-table-column label="违法事实编码" align="center" prop="violationCode" width="120" />
      <el-table-column label="信息报告" align="center" prop="informationReport" width="180" show-overflow-tooltip />
      <el-table-column label="检查内容" align="center" prop="inspectionContent" width="200" show-overflow-tooltip />
      <el-table-column label="定性依据" align="center" prop="qualitativeBasis" width="200" show-overflow-tooltip />
      <el-table-column label="处理依据" align="center" prop="handlingBasis" width="200" show-overflow-tooltip />
      <el-table-column label="规范用语" align="center" prop="standardPhrase" width="250" show-overflow-tooltip />
      <el-table-column label="监督意见" align="center" prop="supervisoryOpinion" width="250" show-overflow-tooltip />
      <el-table-column label="处理内容" align="center" prop="handlingContent" width="250" show-overflow-tooltip />
      <el-table-column label="处理程序" align="center" prop="handlingProcedure" width="150" />
      <el-table-column label="行政处罚决定" align="center" prop="administrativePenalty" width="200" show-overflow-tooltip />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleViewData(scope.row)"
            v-hasPermi="['system:language:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:language:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:language:remove']"
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

    <!-- 规范用语详情抽屉 -->
    <language-view-drawer ref="languageViewRef" />
    <!-- 添加或修改规范用语对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="800px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="130px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="专业类别" prop="professionalCategory">
              <el-select v-model="form.professionalCategory" placeholder="请选择专业类别" @change="onFormProfessionalChange" style="width: 100%;">
                <el-option v-for="item in professionalOptions" :key="item.code" :label="item.name" :value="item.code" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="一级分类" prop="primaryCategory">
              <el-select v-model="form.primaryCategory" placeholder="请选择一级分类" @change="onFormPrimaryChange" style="width: 100%;">
                <el-option v-for="item in formPrimaryOptions" :key="item.code" :label="item.name" :value="item.code" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="二级分类" prop="secondaryCategory">
              <el-select v-model="form.secondaryCategory" placeholder="请选择二级分类" @change="onFormSecondaryChange" style="width: 100%;">
                <el-option v-for="item in formSecondaryOptions" :key="item.code" :label="item.name" :value="item.code" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="三级分类" prop="tertiaryCategory">
              <el-select v-model="form.tertiaryCategory" placeholder="请选择三级分类" style="width: 100%;">
                <el-option v-for="item in formTertiaryOptions" :key="item.code" :label="item.name" :value="item.code" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="规范用语代码" prop="standardCode">
              <el-input v-model="form.standardCode" placeholder="请输入规范用语代码" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="违法事实编码" prop="violationCode">
              <el-input v-model="form.violationCode" placeholder="请输入违法事实编码" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="信息报告违法事实" prop="informationReport">
              <el-input v-model="form.informationReport" placeholder="请输入信息报告违法事实" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="检查内容">
              <editor v-model="form.inspectionContent" :min-height="192"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="定性依据" prop="qualitativeBasis">
              <el-input v-model="form.qualitativeBasis" placeholder="请输入定性依据" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="处理依据" prop="handlingBasis">
              <el-input v-model="form.handlingBasis" placeholder="请输入处理依据" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="规范用语" prop="standardPhrase">
              <el-input v-model="form.standardPhrase" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="监督意见" prop="supervisoryOpinion">
              <el-input v-model="form.supervisoryOpinion" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="处理内容">
              <editor v-model="form.handlingContent" :min-height="192"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="处理程序" prop="handlingProcedure">
              <el-input v-model="form.handlingProcedure" placeholder="请输入处理程序" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="行政处罚决定" prop="administrativePenalty">
              <el-input v-model="form.administrativePenalty" placeholder="请输入行政处罚决定" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="行政强制及其他措施" prop="administrativeForcedMeasures">
              <el-input v-model="form.administrativeForcedMeasures" placeholder="请输入行政强制及其他措施" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="其他处理情况" prop="otherHandling">
              <el-input v-model="form.otherHandling" placeholder="请输入其他处理情况" />
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
import { listLanguage, getLanguage, delLanguage, addLanguage, updateLanguage } from "@/api/system/language"
import { listNormativecategory } from "@/api/system/normativecategory"
import LanguageViewDrawer from "./view"

export default {
  name: "Language",
  components: { LanguageViewDrawer },
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
      // 规范用语表格数据
      languageList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 所有分类列表
      categoryList: [],
      // 专业类别下拉选项
      professionalOptions: [],
      // 一级分类下拉选项
      primaryOptions: [],
      // 二级分类下拉选项
      secondaryOptions: [],
      // 三级分类下拉选项
      tertiaryOptions: [],
      // 表单专用 - 一级分类下拉选项
      formPrimaryOptions: [],
      // 表单专用 - 二级分类下拉选项
      formSecondaryOptions: [],
      // 表单专用 - 三级分类下拉选项
      formTertiaryOptions: [],
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        professionalCategory: null,
        primaryCategory: null,
        secondaryCategory: null,
        tertiaryCategory: null,
        standardCode: null,
        violationCode: null,
        informationReport: null,
        inspectionContent: null,
        qualitativeBasis: null,
        handlingBasis: null,
        standardPhrase: null,
        supervisoryOpinion: null,
        handlingContent: null,
        handlingProcedure: null,
        administrativePenalty: null,
        administrativeForcedMeasures: null,
        otherHandling: null,
        isDeleted: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        professionalCategory: [
          { required: true, message: "专业类别不能为空", trigger: "change" }
        ],
        primaryCategory: [
          { required: true, message: "一级分类不能为空", trigger: "change" }
        ]
      }
    }
  },
  created() {
    this.getList()
    this.loadCategoryList()
  },
  methods: {
    /** 加载分类列表 */
    loadCategoryList() {
      listNormativecategory().then(response => {
        this.categoryList = response.data || []
        this.buildProfessionalOptions()
      })
    },
    /** 构建专业类别下拉选项 */
    buildProfessionalOptions() {
      // 专业类别 = parent_code 为 NULL 的记录
      this.professionalOptions = this.categoryList
        .filter(item => item.parentCode === null || item.parentCode === '')
        .map(item => ({ code: item.code, name: item.name }))
    },
    /** 专业类别变更 */
    onProfessionalChange(value) {
      this.queryParams.primaryCategory = null
      this.queryParams.secondaryCategory = null
      this.queryParams.tertiaryCategory = null
      this.primaryOptions = []
      this.secondaryOptions = []
      this.tertiaryOptions = []
      if (value) {
        this.primaryOptions = this.categoryList
          .filter(item => item.parentCode === value)
          .map(item => ({ code: item.code, name: item.name }))
      }
    },
    /** 一级分类变更 */
    onPrimaryChange(value) {
      this.queryParams.secondaryCategory = null
      this.queryParams.tertiaryCategory = null
      this.secondaryOptions = []
      this.tertiaryOptions = []
      if (value) {
        this.secondaryOptions = this.categoryList
          .filter(item => item.parentCode === value)
          .map(item => ({ code: item.code, name: item.name }))
      }
    },
    /** 二级分类变更 */
    onSecondaryChange(value) {
      this.queryParams.tertiaryCategory = null
      this.tertiaryOptions = []
      if (value) {
        this.tertiaryOptions = this.categoryList
          .filter(item => item.parentCode === value)
          .map(item => ({ code: item.code, name: item.name }))
      }
    },
    /** 表单 - 专业类别变更 */
    onFormProfessionalChange(value) {
      this.form.primaryCategory = null
      this.form.secondaryCategory = null
      this.form.tertiaryCategory = null
      this.formPrimaryOptions = []
      this.formSecondaryOptions = []
      this.formTertiaryOptions = []
      if (value) {
        this.formPrimaryOptions = this.categoryList
          .filter(item => item.parentCode === value)
          .map(item => ({ code: item.code, name: item.name }))
      }
    },
    /** 表单 - 一级分类变更 */
    onFormPrimaryChange(value) {
      this.form.secondaryCategory = null
      this.form.tertiaryCategory = null
      this.formSecondaryOptions = []
      this.formTertiaryOptions = []
      if (value) {
        this.formSecondaryOptions = this.categoryList
          .filter(item => item.parentCode === value)
          .map(item => ({ code: item.code, name: item.name }))
      }
    },
    /** 表单 - 二级分类变更 */
    onFormSecondaryChange(value) {
      this.form.tertiaryCategory = null
      this.formTertiaryOptions = []
      if (value) {
        this.formTertiaryOptions = this.categoryList
          .filter(item => item.parentCode === value)
          .map(item => ({ code: item.code, name: item.name }))
      }
    },
    /** 回显表单分类数据 */
    echoFormCategories() {
      if (this.form.professionalCategory) {
        this.formPrimaryOptions = this.categoryList
          .filter(item => item.parentCode === this.form.professionalCategory)
          .map(item => ({ code: item.code, name: item.name }))
      }
      if (this.form.primaryCategory) {
        this.formSecondaryOptions = this.categoryList
          .filter(item => item.parentCode === this.form.primaryCategory)
          .map(item => ({ code: item.code, name: item.name }))
      }
      if (this.form.secondaryCategory) {
        this.formTertiaryOptions = this.categoryList
          .filter(item => item.parentCode === this.form.secondaryCategory)
          .map(item => ({ code: item.code, name: item.name }))
      }
    },
    /** 查询规范用语列表 */
    getList() {
      this.loading = true
      listLanguage(this.queryParams).then(response => {
        this.languageList = response.rows
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
        professionalCategory: null,
        primaryCategory: null,
        secondaryCategory: null,
        tertiaryCategory: null,
        standardCode: null,
        violationCode: null,
        informationReport: null,
        inspectionContent: null,
        qualitativeBasis: null,
        handlingBasis: null,
        standardPhrase: null,
        supervisoryOpinion: null,
        handlingContent: null,
        handlingProcedure: null,
        administrativePenalty: null,
        administrativeForcedMeasures: null,
        otherHandling: null,
        createTime: null,
        isDeleted: null
      }
      this.formPrimaryOptions = []
      this.formSecondaryOptions = []
      this.formTertiaryOptions = []
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
      this.queryParams.professionalCategory = null
      this.queryParams.primaryCategory = null
      this.queryParams.secondaryCategory = null
      this.queryParams.tertiaryCategory = null
      this.primaryOptions = []
      this.secondaryOptions = []
      this.tertiaryOptions = []
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
      this.title = "添加规范用语"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getLanguage(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改规范用语"
        // 回显分类下拉选项
        this.$nextTick(() => {
          this.echoFormCategories()
        })
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateLanguage(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addLanguage(this.form).then(response => {
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
      this.$modal.confirm('是否确认删除规范用语编号为"' + ids + '"的数据项？').then(function() {
        return delLanguage(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 详情按钮操作 */
    handleViewData(row) {
      this.$refs["languageViewRef"].open(row.id)
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/language/export', {
        ...this.queryParams
      }, `language_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
