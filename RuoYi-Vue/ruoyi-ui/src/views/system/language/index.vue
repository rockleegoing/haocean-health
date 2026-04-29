<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧：分类树 -->
      <el-col :span="6" :xs="24">
        <div class="left-panel">
          <div class="panel-header">
            <span>规范用语分类</span>
            <el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddCategory" style="margin-left: auto;">
              新增分类
            </el-button>
          </div>
          <!-- 分类搜索框 -->
          <el-input
            v-model="categorySearchKeyword"
            placeholder="搜索分类名称"
            size="mini"
            clearable
            prefix-icon="el-icon-search"
            style="margin-bottom: 10px;"
            @input="handleCategorySearch"
          />
          <el-tree
            v-loading="categoryLoading"
            :data="categoryTreeData"
            :props="treeProps"
            node-key="code"
            :expand-on-click-node="false"
            :highlight-current="true"
            :filter-node-method="filterNode"
            ref="categoryTree"
            @node-click="handleCategoryNodeClick"
            style="background: transparent;"
          >
            <span slot-scope="{ node, data }" class="custom-tree-node">
              <span>{{ node.label }}</span>
              <span class="tree-node-actions">
                <el-button type="text" size="mini" icon="el-icon-edit" @click.stop="handleEditCategory(data)" />
                <el-button type="text" size="mini" icon="el-icon-plus" @click.stop="handleAddChildCategory(data)" />
                <el-button type="text" size="mini" icon="el-icon-delete" @click.stop="handleDeleteCategory(data)" />
              </span>
            </span>
          </el-tree>
        </div>
      </el-col>

      <!-- 右侧：规范用语列表 -->
      <el-col :span="18" :xs="24">
        <div class="right-panel">
          <div class="panel-header">
            <span v-if="currentCategoryCode">规范用语列表: {{ currentCategoryName }}</span>
            <span v-else>请选择左侧分类</span>
            <div style="margin-left: auto; display: flex;">
              <el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddLanguage" :disabled="!currentCategoryCode">
                新增用语
              </el-button>
              <el-button type="warning" plain size="mini" icon="el-icon-download" @click="handleExport" :disabled="!currentCategoryCode">
                导出
              </el-button>
            </div>
          </div>

          <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
            <el-form-item label="规范用语" prop="standardPhrase">
              <el-input
                v-model="queryParams.standardPhrase"
                placeholder="请输入规范用语"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item label="代码" prop="standardCode">
              <el-input
                v-model="queryParams.standardCode"
                placeholder="请输入代码"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table v-loading="loading" :data="languageList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="规范用语代码" align="center" prop="standardCode" width="120" show-overflow-tooltip />
            <el-table-column label="违法事实编码" align="center" prop="violationCode" width="120" show-overflow-tooltip />
            <el-table-column label="规范用语" align="center" prop="standardPhrase" show-overflow-tooltip />
            <el-table-column label="监督意见" align="center" prop="supervisoryOpinion" width="200" show-overflow-tooltip />
            <el-table-column label="处理程序" align="center" prop="handlingProcedure" width="100" show-overflow-tooltip />
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-view"
                  @click="handleView(scope.row)"
                >查看</el-button>
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
            @pagination="getLanguageList"
          />
        </div>
      </el-col>
    </el-row>

    <!-- 新增/修改分类对话框 -->
    <el-dialog :title="categoryTitle" :visible.sync="categoryOpen" width="500px" append-to-body>
      <el-form ref="categoryForm" :model="categoryForm" :rules="categoryRules" label-width="100px">
        <el-form-item label="分类名称" prop="name">
          <el-input v-model="categoryForm.name" placeholder="请输入分类名称" />
        </el-form-item>
        <el-form-item label="父级分类" prop="parentCode">
          <treeselect v-model="categoryForm.parentCode" :options="categoryTreeOptions" :normalizer="categoryNormalizer" placeholder="请选择父级分类" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitCategoryForm">确 定</el-button>
        <el-button @click="cancelCategory">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 新增/修改规范用语对话框 -->
    <el-dialog :title="languageTitle" :visible.sync="languageOpen" width="800px" append-to-body>
      <el-form ref="languageForm" :model="languageForm" :rules="languageRules" label-width="130px">
        <el-row>
          <el-col :span="12">
            <el-form-item label="专业类别" prop="professionalCategory">
              <el-select v-model="languageForm.professionalCategory" placeholder="请选择专业类别" @change="onFormProfessionalChange" style="width: 100%;">
                <el-option v-for="item in professionalOptions" :key="item.code" :label="item.name" :value="item.code" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="一级分类" prop="primaryCategory">
              <el-select v-model="languageForm.primaryCategory" placeholder="请选择一级分类" @change="onFormPrimaryChange" style="width: 100%;">
                <el-option v-for="item in formPrimaryOptions" :key="item.code" :label="item.name" :value="item.code" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="二级分类" prop="secondaryCategory">
              <el-select v-model="languageForm.secondaryCategory" placeholder="请选择二级分类" @change="onFormSecondaryChange" style="width: 100%;">
                <el-option v-for="item in formSecondaryOptions" :key="item.code" :label="item.name" :value="item.code" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="三级分类" prop="tertiaryCategory">
              <el-select v-model="languageForm.tertiaryCategory" placeholder="请选择三级分类" style="width: 100%;">
                <el-option v-for="item in formTertiaryOptions" :key="item.code" :label="item.name" :value="item.code" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="规范用语代码" prop="standardCode">
              <el-input v-model="languageForm.standardCode" placeholder="请输入规范用语代码" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="违法事实编码" prop="violationCode">
              <el-input v-model="languageForm.violationCode" placeholder="请输入违法事实编码" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="信息报告违法事实" prop="informationReport">
              <el-input v-model="languageForm.informationReport" placeholder="请输入信息报告违法事实" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="检查内容">
              <editor v-model="languageForm.inspectionContent" :min-height="192"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="定性依据" prop="qualitativeBasis">
              <el-input v-model="languageForm.qualitativeBasis" placeholder="请输入定性依据" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="处理依据" prop="handlingBasis">
              <el-input v-model="languageForm.handlingBasis" placeholder="请输入处理依据" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="规范用语" prop="standardPhrase">
              <el-input v-model="languageForm.standardPhrase" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="监督意见" prop="supervisoryOpinion">
              <el-input v-model="languageForm.supervisoryOpinion" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="处理内容">
              <editor v-model="languageForm.handlingContent" :min-height="192"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="处理程序" prop="handlingProcedure">
              <el-input v-model="languageForm.handlingProcedure" placeholder="请输入处理程序" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="行政处罚决定" prop="administrativePenalty">
              <el-input v-model="languageForm.administrativePenalty" placeholder="请输入行政处罚决定" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="行政强制及其他措施" prop="administrativeForcedMeasures">
              <el-input v-model="languageForm.administrativeForcedMeasures" placeholder="请输入行政强制及其他措施" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="其他处理情况" prop="otherHandling">
              <el-input v-model="languageForm.otherHandling" placeholder="请输入其他处理情况" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitLanguageForm">确 定</el-button>
        <el-button @click="cancelLanguage">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog title="规范用语详情" :visible.sync="viewOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="规范用语代码">{{ viewData.standardCode }}</el-descriptions-item>
        <el-descriptions-item label="违法事实编码">{{ viewData.violationCode }}</el-descriptions-item>
        <el-descriptions-item label="专业类别">{{ viewData.professionalCategory }}</el-descriptions-item>
        <el-descriptions-item label="一级分类">{{ viewData.primaryCategory }}</el-descriptions-item>
        <el-descriptions-item label="二级分类">{{ viewData.secondaryCategory }}</el-descriptions-item>
        <el-descriptions-item label="三级分类">{{ viewData.tertiaryCategory }}</el-descriptions-item>
        <el-descriptions-item label="规范用语" :span="2">{{ viewData.standardPhrase }}</el-descriptions-item>
        <el-descriptions-item label="监督意见" :span="2">{{ viewData.supervisoryOpinion }}</el-descriptions-item>
        <el-descriptions-item label="定性依据" :span="2">{{ viewData.qualitativeBasis }}</el-descriptions-item>
        <el-descriptions-item label="处理依据" :span="2">{{ viewData.handlingBasis }}</el-descriptions-item>
        <el-descriptions-item label="处理程序">{{ viewData.handlingProcedure }}</el-descriptions-item>
        <el-descriptions-item label="行政处罚决定">{{ viewData.administrativePenalty }}</el-descriptions-item>
        <el-descriptions-item label="行政强制及其他措施" :span="2">{{ viewData.administrativeForcedMeasures }}</el-descriptions-item>
        <el-descriptions-item label="其他处理情况" :span="2">{{ viewData.otherHandling }}</el-descriptions-item>
        <el-descriptions-item label="检查内容" :span="2">{{ viewData.inspectionContent }}</el-descriptions-item>
        <el-descriptions-item label="处理内容" :span="2">{{ viewData.handlingContent }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listLanguage, getLanguage, delLanguage, addLanguage, updateLanguage } from "@/api/system/language"
import { listNormativecategory, addNormativecategory, updateNormativecategory, delNormativecategory } from "@/api/system/normativecategory"
import Treeselect from "@riophae/vue-treeselect"
import "@riophae/vue-treeselect/dist/vue-treeselect.css"

export default {
  name: "LanguageWithCategory",
  components: { Treeselect },
  data() {
    return {
      // 左侧分类相关
      categoryLoading: false,
      categoryTreeData: [],
      categoryTreeOptions: [],
      treeProps: {
        children: 'children',
        label: 'name'
      },
      categorySearchKeyword: '',
      currentCategoryCode: null,
      currentCategoryName: '',
      categoryTitle: "",
      categoryOpen: false,
      categoryForm: {},
      categoryRules: {
        name: [{ required: true, message: "分类名称不能为空", trigger: "blur" }]
      },
      // 所有分类列表（用于级联选择）
      categoryList: [],
      // 专业类别下拉选项
      professionalOptions: [],
      // 表单专用 - 一级分类下拉选项
      formPrimaryOptions: [],
      // 表单专用 - 二级分类下拉选项
      formSecondaryOptions: [],
      // 表单专用 - 三级分类下拉选项
      formTertiaryOptions: [],

      // 右侧用语相关
      loading: false,
      languageList: [],
      total: 0,
      showSearch: true,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        professionalCategory: null,
        standardPhrase: null,
        standardCode: null
      },
      languageTitle: "",
      languageOpen: false,
      languageForm: {},
      languageRules: {
        professionalCategory: [{ required: true, message: "专业类别不能为空", trigger: "change" }]
      },

      // 查看详情
      viewOpen: false,
      viewData: {}
    }
  },
  created() {
    this.getCategoryTree()
    this.loadCategoryList()
  },
  methods: {
    // ========== 左侧分类相关 ==========
    /** 获取分类树 */
    getCategoryTree() {
      this.categoryLoading = true
      listNormativecategory().then(response => {
        this.categoryList = response.data || []
        this.categoryTreeData = this.handleTree(this.categoryList, 'code', 'parentCode')
        this.buildProfessionalOptions()
        this.categoryLoading = false
      })
    },
    /** 构建专业类别下拉选项 */
    buildProfessionalOptions() {
      this.professionalOptions = this.categoryList
        .filter(item => item.parentCode === null || item.parentCode === '' || item.parentCode === 0)
        .map(item => ({ code: item.code, name: item.name }))
    },
    /** 分类搜索 */
    handleCategorySearch() {
      this.$refs.categoryTree.filter(this.categorySearchKeyword)
    },
    /** 过滤节点 */
    filterNode(value, data) {
      if (!value) return true
      return data.name.toLowerCase().includes(value.toLowerCase())
    },
    /** 点击分类节点 */
    handleCategoryNodeClick(data) {
      this.currentCategoryCode = data.code
      this.currentCategoryName = data.name
      // 根据层级设置查询参数
      const level = this.getNodeLevel(data)
      if (level === 1) {
        this.queryParams.professionalCategory = data.code
      } else if (level === 2) {
        this.queryParams.primaryCategory = data.code
      } else if (level === 3) {
        this.queryParams.secondaryCategory = data.code
      } else {
        this.queryParams.tertiaryCategory = data.code
      }
      this.queryParams.pageNum = 1
      this.getLanguageList()
    },
    /** 获取节点层级 */
    getNodeLevel(data) {
      if (data.parentCode === null || data.parentCode === 0) {
        return 1
      }
      const parent = this.categoryList.find(item => item.code === data.parentCode)
      if (parent && (parent.parentCode === null || parent.parentCode === 0)) {
        return 2
      }
      if (parent) {
        const grandParent = this.categoryList.find(item => item.code === parent.parentCode)
        if (grandParent && (grandParent.parentCode === null || grandParent.parentCode === 0)) {
          return 3
        }
        return 4
      }
      return 1
    },
    /** 新增分类按钮 */
    handleAddCategory() {
      this.categoryForm = { parentCode: 0 }
      this.categoryTitle = "新增分类"
      this.getCategoryTreeOptions()
      this.categoryOpen = true
    },
    /** 编辑分类 */
    handleEditCategory(data) {
      this.categoryForm = { code: data.code, name: data.name, parentCode: data.parentCode }
      this.categoryTitle = "修改分类"
      this.getCategoryTreeOptions()
      this.categoryOpen = true
    },
    /** 新增子分类 */
    handleAddChildCategory(data) {
      this.categoryForm = { parentCode: data.code }
      this.categoryTitle = "新增子分类"
      this.getCategoryTreeOptions()
      this.categoryOpen = true
    },
    /** 获取分类树下拉选项 */
    getCategoryTreeOptions() {
      const data = { code: 0, name: '顶级节点', children: [] }
      data.children = this.handleTree(this.categoryList, 'code', 'parentCode')
      this.categoryTreeOptions = [data]
    },
    /** 分类树转换器 */
    categoryNormalizer(node) {
      if (node.children && !node.children.length) {
        delete node.children
      }
      return {
        id: node.code,
        label: node.name,
        children: node.children
      }
    },
    /** 提交分类表单 */
    submitCategoryForm() {
      this.$refs["categoryForm"].validate(valid => {
        if (valid) {
          if (this.categoryForm.code) {
            updateNormativecategory(this.categoryForm).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.categoryOpen = false
              this.getCategoryTree()
            })
          } else {
            addNormativecategory(this.categoryForm).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.categoryOpen = false
              this.getCategoryTree()
            })
          }
        }
      })
    },
    /** 删除分类 */
    handleDeleteCategory(data) {
      this.$modal.confirm('是否确认删除分类"' + data.name + '"？').then(() => {
        return delNormativecategory(data.code)
      }).then(() => {
        this.getCategoryTree()
        this.$modal.msgSuccess("删除成功")
        if (this.currentCategoryCode === data.code) {
          this.currentCategoryCode = null
          this.currentCategoryName = ''
        }
      }).catch(() => {})
    },
    /** 取消分类弹窗 */
    cancelCategory() {
      this.categoryOpen = false
      this.categoryForm = {}
    },

    // ========== 右侧用语相关 ==========
    /** 获取用语列表 */
    getLanguageList() {
      if (!this.currentCategoryCode) {
        this.languageList = []
        this.total = 0
        return
      }
      this.loading = true
      listLanguage(this.queryParams).then(response => {
        this.languageList = response.rows || []
        this.total = response.total
        this.loading = false
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getLanguageList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams.standardPhrase = null
      this.queryParams.standardCode = null
      this.queryParams.pageNum = 1
      this.getLanguageList()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
    },
    /** 新增用语按钮 */
    handleAddLanguage() {
      this.languageForm = { professionalCategory: this.currentCategoryCode }
      this.languageTitle = "新增规范用语"
      this.languageOpen = true
    },
    /** 查看详情 */
    handleView(row) {
      this.viewData = row
      this.viewOpen = true
    },
    /** 修改用语 */
    handleUpdate(row) {
      this.languageForm = Object.assign({}, row)
      this.languageTitle = "修改规范用语"
      this.languageOpen = true
      this.$nextTick(() => {
        this.echoFormCategories()
      })
    },
    /** 表单 - 专业类别变更 */
    onFormProfessionalChange(value) {
      this.languageForm.primaryCategory = null
      this.languageForm.secondaryCategory = null
      this.languageForm.tertiaryCategory = null
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
      this.languageForm.secondaryCategory = null
      this.languageForm.tertiaryCategory = null
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
      this.languageForm.tertiaryCategory = null
      this.formTertiaryOptions = []
      if (value) {
        this.formTertiaryOptions = this.categoryList
          .filter(item => item.parentCode === value)
          .map(item => ({ code: item.code, name: item.name }))
      }
    },
    /** 回显表单分类数据 */
    echoFormCategories() {
      if (this.languageForm.professionalCategory) {
        this.formPrimaryOptions = this.categoryList
          .filter(item => item.parentCode === this.languageForm.professionalCategory)
          .map(item => ({ code: item.code, name: item.name }))
      }
      if (this.languageForm.primaryCategory) {
        this.formSecondaryOptions = this.categoryList
          .filter(item => item.parentCode === this.languageForm.primaryCategory)
          .map(item => ({ code: item.code, name: item.name }))
      }
      if (this.languageForm.secondaryCategory) {
        this.formTertiaryOptions = this.categoryList
          .filter(item => item.parentCode === this.languageForm.secondaryCategory)
          .map(item => ({ code: item.code, name: item.name }))
      }
    },
    /** 删除用语 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除规范用语编号为"' + ids + '"的数据项？').then(() => {
        return delLanguage(ids)
      }).then(() => {
        this.getLanguageList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 提交用语表单 */
    submitLanguageForm() {
      this.$refs["languageForm"].validate(valid => {
        if (valid) {
          if (this.languageForm.id) {
            updateLanguage(this.languageForm).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.languageOpen = false
              this.getLanguageList()
            })
          } else {
            addLanguage(this.languageForm).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.languageOpen = false
              this.getLanguageList()
            })
          }
        }
      })
    },
    /** 取消用语弹窗 */
    cancelLanguage() {
      this.languageOpen = false
      this.languageForm = {}
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/language/export', {
        ...this.queryParams
      }, `language_${new Date().getTime()}.xlsx`)
    },
    /** 加载分类列表（用于表单级联） */
    loadCategoryList() {
      listNormativecategory().then(response => {
        this.categoryList = response.data || []
        this.buildProfessionalOptions()
      })
    }
  }
}
</script>

<style scoped>
.left-panel, .right-panel {
  background: #fff;
  border-radius: 4px;
  padding: 10px;
}
.panel-header {
  display: flex;
  align-items: center;
  padding: 10px 0;
  font-weight: bold;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 10px;
}
.custom-tree-node {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  padding-right: 8px;
  overflow: hidden;
}
.tree-node-actions {
  display: none;
}
.custom-tree-node:hover .tree-node-actions {
  display: block;
}
</style>
