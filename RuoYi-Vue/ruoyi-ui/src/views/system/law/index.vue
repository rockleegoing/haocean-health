<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧：类型树 + 法律列表 -->
      <el-col :span="8" :xs="24">
        <div class="left-panel">
          <div class="panel-header">
            <span>法律法规类型</span>
          </div>
          <!-- 类型选择器 -->
          <el-select
            v-model="selectedTypeId"
            placeholder="全部类型"
            clearable
            size="small"
            style="width: 100%; margin-bottom: 10px;"
            @change="handleTypeChange"
          >
            <el-option
              v-for="item in lawTypeOptions"
              :key="item.id"
              :label="item.label"
              :value="item.id"
            />
          </el-select>

          <el-divider />

          <div class="panel-header">
            <span>{{ selectedTypeName || '全部法律' }}</span>
            <el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddLaw" style="margin-left: auto;">
              新增法律
            </el-button>
          </div>
          <!-- 法律列表（根据选中类型过滤） -->
          <el-input
            v-model="lawSearchKeyword"
            placeholder="搜索法律名称"
            size="mini"
            clearable
            prefix-icon="el-icon-search"
            style="margin-bottom: 10px;"
            @input="handleLawSearch"
          />
          <el-table
            v-loading="lawLoading"
            :data="lawSearchKeyword ? lawSearchResult : lawList"
            :show-header="false"
            border
            stripe
            highlight-current-row
            @row-click="handleLawRowClick"
            :row-class-name="lawRowClassName"
            style="width: 100%; cursor: pointer;"
          >
            <el-table-column label="法律名称" align="left" prop="name" />
            <el-table-column label="发布日期" align="center" prop="releaseTime" width="100">
              <template slot-scope="scope">
                <span>{{ parseTime(scope.row.releaseTime, '{y}-{m}') }}</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" width="80">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-edit"
                  @click.stop="handleEditLaw(scope.row)"
                >修改</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>

      <!-- 右侧：法律条款列表 -->
      <el-col :span="16" :xs="24">
        <div class="right-panel">
          <div class="panel-header">
            <span v-if="currentLawId">法律条款: {{ currentLawName }}</span>
            <span v-else>请选择左侧法律</span>
            <div style="margin-left: auto; display: flex;">
              <el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddTerm" :disabled="!currentLawId">
                新增条款
              </el-button>
              <el-button type="warning" plain size="mini" icon="el-icon-download" @click="handleExport" :disabled="!currentLawId">
                导出
              </el-button>
            </div>
          </div>

          <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
            <el-form-item label="条款编码" prop="zhCode">
              <el-input
                v-model="queryParams.zhCode"
                placeholder="请输入条款编码"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item label="条款内容" prop="content">
              <el-input
                v-model="queryParams.content"
                placeholder="请输入条款内容"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table v-loading="termLoading" :data="termList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="条款编码" align="center" prop="zhCode" width="150" show-overflow-tooltip />
            <el-table-column label="条款内容" align="left" prop="content" show-overflow-tooltip />
            <el-table-column label="更新时间" align="center" prop="updateTime" width="180" />
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
            @pagination="getTermList"
          />
        </div>
      </el-col>
    </el-row>

    <!-- 新增/修改法律对话框 -->
    <el-dialog :title="lawTitle" :visible.sync="lawOpen" width="500px" append-to-body>
      <el-form ref="lawForm" :model="lawForm" :rules="lawRules" label-width="100px">
        <el-form-item label="法律名称" prop="name">
          <el-input v-model="lawForm.name" placeholder="请输入法律名称" />
        </el-form-item>
        <el-form-item label="所属类型" prop="typeIds">
          <el-select v-model="lawForm.typeIds" multiple placeholder="请选择类型" collapse-tags style="width: 100%">
            <el-option
              v-for="item in lawTypeOptions"
              :key="item.id"
              :label="item.label"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="发布日期" prop="releaseTime">
          <el-date-picker
            v-model="lawForm.releaseTime"
            type="date"
            value-format="yyyy-MM-dd"
            placeholder="请选择发布日期"
            style="width: 100%;"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitLawForm">确 定</el-button>
        <el-button @click="cancelLaw">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 新增/修改条款对话框 -->
    <el-dialog :title="termTitle" :visible.sync="termOpen" width="700px" append-to-body>
      <el-form ref="termForm" :model="termForm" :rules="termRules" label-width="100px">
        <el-form-item label="所属法律" prop="lawId">
          <el-select v-model="termForm.lawId" placeholder="请选择所属法律" filterable style="width: 100%">
            <el-option
              v-for="item in lawOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-row>
          <el-col :span="8">
            <el-form-item label="编" prop="part">
              <el-input v-model="termForm.part" placeholder="请输入编" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="分编" prop="partBranch">
              <el-input v-model="termForm.partBranch" placeholder="请输入分编" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="章" prop="chapter">
              <el-input v-model="termForm.chapter" placeholder="请输入章" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="节" prop="quarter">
              <el-input v-model="termForm.quarter" placeholder="请输入节" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="条" prop="article">
              <el-input v-model="termForm.article" placeholder="请输入条" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="款" prop="section">
              <el-input v-model="termForm.section" placeholder="请输入款" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-row>
          <el-col :span="8">
            <el-form-item label="项" prop="subparagraph">
              <el-input v-model="termForm.subparagraph" placeholder="请输入项" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="目" prop="item">
              <el-input v-model="termForm.item" placeholder="请输入目" />
            </el-form-item>
          </el-col>
          <el-col :span="8">
            <el-form-item label="中文编码" prop="zhCode">
              <el-input v-model="termForm.zhCode" placeholder="请输入中文编码" />
            </el-form-item>
          </el-col>
        </el-row>
        <el-form-item label="条款内容" prop="content">
          <el-input v-model="termForm.content" type="textarea" placeholder="请输入条款内容" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitTermForm">确 定</el-button>
        <el-button @click="cancelTerm">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog title="条款详情" :visible.sync="viewOpen" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="条款编码">{{ viewData.zhCode }}</el-descriptions-item>
        <el-descriptions-item label="所属法律">{{ viewData.lawName }}</el-descriptions-item>
        <el-descriptions-item label="编">{{ viewData.part }}</el-descriptions-item>
        <el-descriptions-item label="分编">{{ viewData.partBranch }}</el-descriptions-item>
        <el-descriptions-item label="章">{{ viewData.chapter }}</el-descriptions-item>
        <el-descriptions-item label="节">{{ viewData.quarter }}</el-descriptions-item>
        <el-descriptions-item label="条">{{ viewData.article }}</el-descriptions-item>
        <el-descriptions-item label="款">{{ viewData.section }}</el-descriptions-item>
        <el-descriptions-item label="项">{{ viewData.subparagraph }}</el-descriptions-item>
        <el-descriptions-item label="目">{{ viewData.item }}</el-descriptions-item>
        <el-descriptions-item label="条款内容" :span="2">{{ viewData.content }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listLaw, getLaw, addLaw, updateLaw, delLaw } from "@/api/system/law"
import { listLegalterm, getLegalterm, addLegalterm, updateLegalterm, delLegalterm } from "@/api/system/legalterm"
import { treeList } from "@/api/system/lawtype"
import { bindLawType, getLawTypeBind } from "@/api/system/lawbind"

export default {
  name: "LawWithTerm",
  data() {
    return {
      // 左侧法律相关
      lawLoading: false,
      lawList: [],
      lawSearchKeyword: '',
      lawSearchResult: [],
      currentLawId: null,
      currentLawName: '',
      lawTitle: "",
      lawOpen: false,
      lawForm: {},
      lawRules: {
        name: [{ required: true, message: "法律名称不能为空", trigger: "blur" }],
        typeIds: [{ required: false, message: "请选择类型", trigger: "change" }]
      },

      // 类型树相关
      lawTypeTreeData: [],
      lawTypeTreeProps: {
        children: 'children',
        label: 'name'
      },
      selectedTypeId: null,
      lawTypeOptions: [],

      // 右侧条款相关
      termLoading: false,
      termList: [],
      total: 0,
      showSearch: true,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        lawId: null,
        zhCode: null,
        content: null
      },
      termTitle: "",
      termOpen: false,
      termForm: {},
      termRules: {
        lawId: [{ required: true, message: "所属法律不能为空", trigger: "change" }],
        zhCode: [{ required: true, message: "条款编码不能为空", trigger: "blur" }]
      },

      // 查看详情
      viewOpen: false,
      viewData: {},

      // 法律下拉选项
      lawOptions: []
    }
  },
  computed: {
    selectedTypeName() {
      if (!this.selectedTypeId) return ''
      const option = this.lawTypeOptions.find(item => item.id === this.selectedTypeId)
      return option ? option.name : ''
    }
  },
  created() {
    this.getLawTypeTree()
    this.getLawTypeOptions()
    this.getLawList()
    this.getLawOptions()
  },
  methods: {
    // ========== 类型树相关 ==========
    /** 获取类型树 */
    getLawTypeTree() {
      treeList().then(response => {
        if (response.data && response.data.length > 0) {
          this.lawTypeTreeData = response.data[0].children || response.data
        }
      })
    },
    /** 获取类型下拉选项 */
    getLawTypeOptions() {
      treeList().then(response => {
        const flattenOptions = []
        const flatten = (list, level = 0) => {
          list.forEach(item => {
            flattenOptions.push({ id: item.id, name: item.name, label: '-'.repeat(level) + item.name })
            if (item.children && item.children.length > 0) {
              flatten(item.children, level + 1)
            }
          })
        }
        if (response.data && response.data.length > 0) {
          flatten(response.data[0].children || response.data)
        }
        this.lawTypeOptions = flattenOptions
      })
    },
    /** 类型选择变化 */
    handleTypeChange(val) {
      this.currentLawId = null
      this.currentLawName = ''
      this.getLawList()
    },

    // ========== 左侧法律相关 ==========
    /** 获取法律列表 */
    getLawList() {
      this.lawLoading = true
      const params = { pageNum: 1, pageSize: 9999 }
      if (this.selectedTypeId) {
        params.typeId = this.selectedTypeId
      }
      listLaw(params).then(response => {
        this.lawList = response.rows || []
        this.lawLoading = false
      })
    },
    /** 法律搜索 */
    handleLawSearch() {
      if (!this.lawSearchKeyword) {
        this.lawSearchResult = []
        return
      }
      const keyword = this.lawSearchKeyword.toLowerCase()
      this.lawSearchResult = this.lawList.filter(item =>
        (item.name || '').toLowerCase().includes(keyword)
      )
    },
    /** 点击法律行 */
    handleLawRowClick(row) {
      this.currentLawId = row.id
      this.currentLawName = row.name
      this.queryParams.lawId = row.id
      this.queryParams.pageNum = 1
      this.getTermList()
    },
    /** 设置法律行样式 */
    lawRowClassName({ row, rowIndex }) {
      if (row.id === this.currentLawId) {
        return 'current-row'
      }
      return ''
    },
    /** 新增法律按钮 */
    handleAddLaw() {
      this.lawForm = { typeIds: [] }
      this.lawTitle = "新增法律"
      this.lawOpen = true
    },
    /** 修改法律按钮 */
    handleEditLaw(row) {
      this.lawTitle = "修改法律"
      // 先获取法律详情
      getLaw(row.id).then(res => {
        this.lawForm = { ...res.data, typeIds: [] }
        // 再获取已有的类型绑定
        getLawTypeBind(row.id).then(bindRes => {
          this.lawForm.typeIds = (bindRes.data || []).map(b => b.typeId)
          this.lawOpen = true
        })
      })
    },
    /** 提交法律表单 */
    submitLawForm() {
      this.$refs["lawForm"].validate(valid => {
        if (valid) {
          const lawData = { ...this.lawForm }
          const typeIds = lawData.typeIds || []
          delete lawData.typeIds
          if (this.lawForm.id) {
            updateLaw(lawData).then(response => {
              bindLawType(this.lawForm.id, typeIds).then(() => {
                this.$modal.msgSuccess("修改成功")
                this.lawOpen = false
                this.getLawList()
              }).catch(() => {
                this.$modal.msgError("法律已保存但类型绑定失败，请重试")
              })
            })
          } else {
            addLaw(lawData).then(response => {
              const newId = response.data.id
              bindLawType(newId, typeIds).then(() => {
                this.$modal.msgSuccess("新增成功")
                this.lawOpen = false
                this.getLawList()
              }).catch(() => {
                this.$modal.msgError("法律已保存但类型绑定失败，请重试")
              })
            })
          }
        }
      })
    },
    /** 取消法律弹窗 */
    cancelLaw() {
      this.lawOpen = false
      this.lawForm = {}
    },

    // ========== 右侧条款相关 ==========
    /** 获取条款列表 */
    getTermList() {
      if (!this.currentLawId) {
        this.termList = []
        this.total = 0
        return
      }
      this.termLoading = true
      listLegalterm(this.queryParams).then(response => {
        this.termList = response.rows || []
        this.total = response.total
        this.termLoading = false
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getTermList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams.zhCode = null
      this.queryParams.content = null
      this.queryParams.pageNum = 1
      this.getTermList()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
    },
    /** 新增条款按钮 */
    handleAddTerm() {
      this.termForm = { lawId: this.currentLawId }
      this.termTitle = "新增条款"
      this.termOpen = true
    },
    /** 查看详情 */
    handleView(row) {
      this.viewData = row
      this.viewOpen = true
    },
    /** 修改条款 */
    handleUpdate(row) {
      this.termForm = Object.assign({}, row)
      this.termTitle = "修改条款"
      this.termOpen = true
    },
    /** 删除条款 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除条款编号为"' + ids + '"的数据项？').then(() => {
        return delLegalterm(ids)
      }).then(() => {
        this.getTermList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 提交条款表单 */
    submitTermForm() {
      this.$refs["termForm"].validate(valid => {
        if (valid) {
          if (this.termForm.id) {
            updateLegalterm(this.termForm).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.termOpen = false
              this.getTermList()
            })
          } else {
            addLegalterm(this.termForm).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.termOpen = false
              this.getTermList()
            })
          }
        }
      })
    },
    /** 取消条款弹窗 */
    cancelTerm() {
      this.termOpen = false
      this.termForm = {}
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/legalterm/export', {
        ...this.queryParams
      }, `legalterm_${new Date().getTime()}.xlsx`)
    },

    // ========== 下拉选项 ==========
    /** 获取法律下拉选项 */
    getLawOptions() {
      listLaw({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.lawOptions = response.rows || []
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
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
</style>
