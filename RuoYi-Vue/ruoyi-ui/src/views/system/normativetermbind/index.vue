<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧：法律法规列表 + 条款列表 -->
      <el-col :span="6" :xs="24">
        <div class="left-panel">
          <!-- 上方：法律法规列表 -->
          <div class="sub-panel">
            <div class="panel-header">
              <span>法律法规</span>
            </div>
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
              max-height="200"
            >
              <el-table-column label="法律名称" align="left" prop="name">
                <template slot-scope="scope">
                  <span>{{ scope.row.name }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>

          <!-- 下方：法律条款列表 -->
          <div class="sub-panel" style="margin-top: 15px;">
            <div class="panel-header">
              <span v-if="currentLawId">条款列表: {{ currentLawName }}</span>
              <span v-else>请选择法律</span>
            </div>
            <el-input
              v-model="termSearchKeyword"
              placeholder="搜索条款编码"
              size="mini"
              clearable
              prefix-icon="el-icon-search"
              style="margin-bottom: 10px;"
              @input="handleTermSearch"
              :disabled="!currentLawId"
            />
            <el-table
              v-loading="termLoading"
              :data="termSearchKeyword ? termSearchResult : termList"
              :show-header="false"
              border
              stripe
              highlight-current-row
              @row-click="handleTermRowClick"
              :row-class-name="termRowClassName"
              style="width: 100%; cursor: pointer;"
              max-height="250"
            >
              <el-table-column label="条款编码" align="left" prop="zhCode">
                <template slot-scope="scope">
                  <span>{{ scope.row.zhCode || '第' + scope.row.article + '条' }}</span>
                </template>
              </el-table-column>
            </el-table>
          </div>
        </div>
      </el-col>

      <!-- 右侧：已绑定规范用语列表 -->
      <el-col :span="18" :xs="24">
        <div class="right-panel">
          <div class="panel-header">
            <span v-if="currentTermId">已绑定规范用语</span>
            <span v-else>请选择条款</span>
            <el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddBind" :disabled="!currentTermId" style="margin-left: auto;">
              新增绑定
            </el-button>
          </div>

          <el-table v-loading="bindLoading" :data="bindList">
            <el-table-column label="规范用语代码" align="center" prop="standardCode" width="120" show-overflow-tooltip />
            <el-table-column label="规范用语" align="center" prop="normativeName" show-overflow-tooltip />
            <el-table-column label="监督意见" align="center" prop="supervisoryOpinion" width="200" show-overflow-tooltip />
            <el-table-column label="依据类型" align="center" prop="basisType" width="100">
              <template slot-scope="scope">
                <el-tag v-if="scope.row.basisType === 1" type="success" size="small">定性依据</el-tag>
                <el-tag v-else-if="scope.row.basisType === 0" type="warning" size="small">处理依据</el-tag>
                <span v-else>-</span>
              </template>
            </el-table-column>
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
              <template slot-scope="scope">
                <el-button
                  size="mini"
                  type="text"
                  icon="el-icon-delete"
                  @click="handleUnbind(scope.row)"
                >解绑</el-button>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>
    </el-row>

    <!-- 新增绑定对话框 -->
    <el-dialog title="新增绑定" :visible.sync="bindOpen" width="500px" append-to-body>
      <el-form ref="bindForm" :model="bindForm" :rules="bindRules" label-width="100px">
        <el-form-item label="法律" prop="lawId">
          <el-select v-model="bindForm.lawId" placeholder="请选择法律" filterable style="width: 100%" @change="onLawChange">
            <el-option
              v-for="item in lawOptions"
              :key="item.id"
              :label="item.name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="法律条款" prop="legalTermId">
          <el-select v-model="bindForm.legalTermId" placeholder="请选择法律条款" filterable style="width: 100%">
            <el-option
              v-for="item in termOptions"
              :key="item.id"
              :label="item.zhCode || '第' + item.article + '条'"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="规范用语" prop="normativeLanguageId">
          <el-select v-model="bindForm.normativeLanguageId" placeholder="请选择规范用语" filterable style="width: 100%">
            <el-option
              v-for="item in languageOptions"
              :key="item.id"
              :label="item.standardPhrase"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="依据类型" prop="basisType">
          <el-radio-group v-model="bindForm.basisType">
            <el-radio :label="1">定性依据</el-radio>
            <el-radio :label="0">处理依据</el-radio>
          </el-radio-group>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitBindForm">确 定</el-button>
        <el-button @click="cancelBind">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listLaw } from "@/api/system/law"
import { listLegalterm } from "@/api/system/legalterm"
import { listLanguage } from "@/api/system/language"
import { listNormativetermbind, addNormativetermbind, unbindNormativetermbind } from "@/api/system/normativetermbind"

export default {
  name: "NormativeTermBind",
  data() {
    return {
      // 左侧 - 法律法规
      lawLoading: false,
      lawList: [],
      lawSearchKeyword: '',
      lawSearchResult: [],
      currentLawId: null,
      currentLawName: '',

      // 左侧 - 法律条款
      termLoading: false,
      termList: [],
      termSearchKeyword: '',
      termSearchResult: [],
      currentTermId: null,

      // 右侧 - 绑定列表
      bindLoading: false,
      bindList: [],

      // 弹窗
      bindOpen: false,
      bindForm: {
        lawId: null,
        legalTermId: null,
        normativeLanguageId: null,
        basisType: 1
      },
      bindRules: {
        lawId: [{ required: true, message: "法律不能为空", trigger: "change" }],
        legalTermId: [{ required: true, message: "法律条款不能为空", trigger: "change" }],
        normativeLanguageId: [{ required: true, message: "规范用语不能为空", trigger: "change" }],
        basisType: [{ required: true, message: "依据类型不能为空", trigger: "change" }]
      },

      // 下拉选项
      lawOptions: [],
      termOptions: [],
      languageOptions: []
    }
  },
  created() {
    this.getLawList()
    this.getLanguageOptions()
  },
  methods: {
    // ========== 法律法规相关 ==========
    /** 获取法律列表 */
    getLawList() {
      this.lawLoading = true
      listLaw({ pageNum: 1, pageSize: 9999 }).then(response => {
        this.lawList = response.rows || []
        this.lawOptions = this.lawList
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
      this.currentTermId = null
      this.bindList = []
      this.getTermList()
    },
    /** 设置法律行样式 */
    lawRowClassName({ row, rowIndex }) {
      if (row.id === this.currentLawId) {
        return 'current-row'
      }
      return ''
    },

    // ========== 条款相关 ==========
    /** 获取条款列表 */
    getTermList() {
      if (!this.currentLawId) {
        this.termList = []
        return
      }
      this.termLoading = true
      listLegalterm({ lawId: this.currentLawId, pageNum: 1, pageSize: 9999 }).then(response => {
        this.termList = response.rows || []
        this.termOptions = this.termList
        this.termLoading = false
      })
    },
    /** 条款搜索 */
    handleTermSearch() {
      if (!this.termSearchKeyword) {
        this.termSearchResult = []
        return
      }
      const keyword = this.termSearchKeyword.toLowerCase()
      this.termSearchResult = this.termList.filter(item =>
        (item.zhCode || '').toLowerCase().includes(keyword) ||
        (item.content || '').toLowerCase().includes(keyword)
      )
    },
    /** 点击条款行 */
    handleTermRowClick(row) {
      this.currentTermId = row.id
      this.getBindList()
    },
    /** 设置条款行样式 */
    termRowClassName({ row, rowIndex }) {
      if (row.id === this.currentTermId) {
        return 'current-row'
      }
      return ''
    },

    // ========== 绑定列表相关 ==========
    /** 获取绑定列表 */
    getBindList() {
      if (!this.currentTermId) {
        this.bindList = []
        return
      }
      this.bindLoading = true
      listNormativetermbind({ legalTermId: this.currentTermId }).then(response => {
        this.bindList = response.rows || []
        this.bindLoading = false
      })
    },
    /** 新增绑定按钮 */
    handleAddBind() {
      this.bindForm = {
        lawId: this.currentLawId,
        legalTermId: this.currentTermId,
        normativeLanguageId: null,
        basisType: 1
      }
      this.termOptions = this.termList
      this.bindOpen = true
    },
    /** 法律变更 */
    onLawChange(lawId) {
      this.bindForm.legalTermId = null
      listLegalterm({ lawId: lawId, pageNum: 1, pageSize: 9999 }).then(response => {
        this.termOptions = response.rows || []
      })
    },
    /** 提交绑定表单 */
    submitBindForm() {
      this.$refs["bindForm"].validate(valid => {
        if (valid) {
          const data = {
            legalTermId: this.bindForm.legalTermId,
            normativeLanguageId: this.bindForm.normativeLanguageId,
            basisType: this.bindForm.basisType
          }
          addNormativetermbind(data).then(response => {
            this.$modal.msgSuccess("绑定成功")
            this.bindOpen = false
            this.getBindList()
          })
        }
      })
    },
    /** 取消绑定弹窗 */
    cancelBind() {
      this.bindOpen = false
      this.bindForm = {}
    },
    /** 解绑操作 */
    handleUnbind(row) {
      this.$modal.confirm('是否确认解除该绑定关系？').then(() => {
        return unbindNormativetermbind(row.legalTermId, row.normativeLanguageId)
      }).then(() => {
        this.getBindList()
        this.$modal.msgSuccess("解绑成功")
      }).catch(() => {})
    },

    // ========== 下拉选项 ==========
    /** 获取规范用语下拉选项 */
    getLanguageOptions() {
      listLanguage({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.languageOptions = response.rows || []
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
.left-panel {
  display: flex;
  flex-direction: column;
}
.sub-panel {
  flex: 1;
}
.panel-header {
  display: flex;
  align-items: center;
  padding: 8px 0;
  font-weight: bold;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 10px;
}
</style>
