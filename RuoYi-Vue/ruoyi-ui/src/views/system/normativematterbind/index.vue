<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧：监管事项列表 -->
      <el-col :span="6" :xs="24">
        <div class="left-panel">
          <div class="panel-header">
            <span>监管事项</span>
          </div>
          <!-- 事项搜索框 -->
          <el-input
            v-model="matterSearchKeyword"
            placeholder="搜索事项名称"
            size="mini"
            clearable
            prefix-icon="el-icon-search"
            style="margin-bottom: 10px;"
            @input="handleMatterSearch"
          />
          <el-table
            v-loading="matterLoading"
            :data="matterSearchKeyword ? matterSearchResult : matterList"
            :show-header="false"
            border
            stripe
            highlight-current-row
            @row-click="handleMatterRowClick"
            :row-class-name="matterRowClassName"
            style="width: 100%; cursor: pointer;"
          >
            <el-table-column prop="mainName" label="事项名称">
              <template slot-scope="scope">
                <span>{{ scope.row.mainName || scope.row.main_name }}</span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>

      <!-- 右侧：子项列表 + 绑定列表 -->
      <el-col :span="18" :xs="24">
        <div class="right-panel">
          <!-- 上方：子项列表 -->
          <div class="sub-panel">
            <div class="panel-header">
              <span v-if="currentMatterId">子项列表: {{ currentMatterName }}</span>
              <span v-else>请选择左侧事项</span>
            </div>

            <el-table
              v-loading="itemLoading"
              :data="itemList"
              border
              stripe
              highlight-current-row
              @row-click="handleItemRowClick"
              :row-class-name="itemRowClassName"
              style="width: 100%; cursor: pointer;"
            >
              <el-table-column label="子项编码" align="center" prop="itemCode" width="120" show-overflow-tooltip />
              <el-table-column label="详情名称" align="center" prop="itemName" show-overflow-tooltip />
              <el-table-column label="依据" align="center" prop="according" show-overflow-tooltip />
            </el-table>
          </div>

          <!-- 下方：已绑定规范用语列表 -->
          <div class="sub-panel" style="margin-top: 20px;">
            <div class="panel-header">
              <span v-if="currentItemId">已绑定规范用语</span>
              <span v-else>请选择子项</span>
              <el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddBind" :disabled="!currentItemId" style="margin-left: auto;">
                新增绑定
              </el-button>
            </div>

            <el-table v-loading="bindLoading" :data="bindList">
              <el-table-column label="规范用语代码" align="center" prop="standardCode" width="120" show-overflow-tooltip />
              <el-table-column label="规范用语" align="center" prop="normativeName" show-overflow-tooltip />
              <el-table-column label="监督意见" align="center" prop="supervisoryOpinion" width="200" show-overflow-tooltip />
              <el-table-column label="处理程序" align="center" prop="handlingProcedure" width="100" show-overflow-tooltip />
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
        </div>
      </el-col>
    </el-row>

    <!-- 新增绑定对话框 -->
    <el-dialog title="新增绑定" :visible.sync="bindOpen" width="600px" append-to-body>
      <el-form ref="bindForm" :model="bindForm" :rules="bindRules" label-width="100px">
        <el-form-item label="监管事项" prop="matterId">
          <el-select v-model="bindForm.matterId" placeholder="请选择监管事项" filterable style="width: 100%" @change="onMatterChange">
            <el-option
              v-for="item in matterOptions"
              :key="item.id"
              :label="item.mainName || item.main_name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="子项" prop="matterItemId">
          <el-select v-model="bindForm.matterItemId" placeholder="请选择子项" filterable style="width: 100%">
            <el-option
              v-for="item in itemOptions"
              :key="item.id"
              :label="item.itemName || item.item_name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="规范用语" prop="normativeId">
          <el-select v-model="bindForm.normativeId" placeholder="请选择规范用语" filterable style="width: 100%">
            <el-option
              v-for="item in languageOptions"
              :key="item.id"
              :label="item.standardPhrase"
              :value="item.id"
            />
          </el-select>
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
import { listMatter } from "@/api/system/matter"
import { listMatteritem } from "@/api/system/matteritem"
import { listLanguage } from "@/api/system/language"
import { listNormativematterbind, addNormativematterbind, delNormativematterbind } from "@/api/system/normativematterbind"

export default {
  name: "NormativeMatterBind",
  data() {
    return {
      // 左侧事项列表
      matterLoading: false,
      matterList: [],
      matterSearchKeyword: '',
      matterSearchResult: [],
      currentMatterId: null,
      currentMatterName: '',

      // 上方子项列表
      itemLoading: false,
      itemList: [],
      currentItemId: null,

      // 下方绑定列表
      bindLoading: false,
      bindList: [],

      // 弹窗相关
      bindOpen: false,
      bindForm: {
        matterId: null,
        matterItemId: null,
        normativeId: null
      },
      bindRules: {
        matterId: [{ required: true, message: "监管事项不能为空", trigger: "change" }],
        matterItemId: [{ required: true, message: "子项不能为空", trigger: "change" }],
        normativeId: [{ required: true, message: "规范用语不能为空", trigger: "change" }]
      },

      // 下拉选项
      matterOptions: [],
      itemOptions: [],
      languageOptions: []
    }
  },
  created() {
    this.getMatterList()
    this.getMatterOptions()
    this.getLanguageOptions()
  },
  methods: {
    // ========== 左侧事项列表相关 ==========
    /** 获取事项列表 */
    getMatterList() {
      this.matterLoading = true
      listMatter({ pageNum: 1, pageSize: 9999 }).then(response => {
        this.matterList = response.rows || []
        this.matterLoading = false
      })
    },
    /** 事项搜索 */
    handleMatterSearch() {
      if (!this.matterSearchKeyword) {
        this.matterSearchResult = []
        return
      }
      const keyword = this.matterSearchKeyword.toLowerCase()
      this.matterSearchResult = this.matterList.filter(item =>
        (item.mainName || item.main_name || '').toLowerCase().includes(keyword)
      )
    },
    /** 点击事项行 */
    handleMatterRowClick(row) {
      this.currentMatterId = row.id
      this.currentMatterName = row.mainName || row.main_name
      this.currentItemId = null
      this.itemList = []
      this.bindList = []
      this.getItemList()
    },
    /** 设置事项行样式 */
    matterRowClassName({ row, rowIndex }) {
      if (row.id === this.currentMatterId) {
        return 'current-row'
      }
      return ''
    },

    // ========== 上方子项列表相关 ==========
    /** 获取子项列表 */
    getItemList() {
      if (!this.currentMatterId) {
        this.itemList = []
        return
      }
      this.itemLoading = true
      listMatteritem({ regulatoryMatterId: this.currentMatterId, pageNum: 1, pageSize: 9999 }).then(response => {
        this.itemList = response.rows || []
        this.itemLoading = false
      })
    },
    /** 点击子项行 */
    handleItemRowClick(row) {
      this.currentItemId = row.id
      this.getBindList()
    },
    /** 设置子项行样式 */
    itemRowClassName({ row, rowIndex }) {
      if (row.id === this.currentItemId) {
        return 'current-row'
      }
      return ''
    },

    // ========== 下方绑定列表相关 ==========
    /** 获取绑定列表 */
    getBindList() {
      if (!this.currentItemId) {
        this.bindList = []
        return
      }
      this.bindLoading = true
      listNormativematterbind({ matterId: this.currentItemId }).then(response => {
        this.bindList = response.rows || []
        this.bindLoading = false
      })
    },
    /** 新增绑定按钮 */
    handleAddBind() {
      this.bindForm = {
        matterId: this.currentMatterId,
        matterItemId: this.currentItemId,
        normativeId: null
      }
      this.bindOpen = true
      // 设置当前子项选项
      this.itemOptions = this.itemList
    },
    /** 监管事项变更 */
    onMatterChange(matterId) {
      this.bindForm.matterItemId = null
      this.getItemOptionsByMatter(matterId)
    },
    /** 获取事项下的子项选项 */
    getItemOptionsByMatter(matterId) {
      listMatteritem({ regulatoryMatterId: matterId, pageNum: 1, pageSize: 9999 }).then(response => {
        this.itemOptions = response.rows || []
      })
    },
    /** 提交绑定表单 */
    submitBindForm() {
      this.$refs["bindForm"].validate(valid => {
        if (valid) {
          const data = {
            matterId: this.bindForm.matterItemId,
            normativeId: this.bindForm.normativeId
          }
          addNormativematterbind(data).then(response => {
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
        const params = {
          matterId: row.matterId,
          normativeId: row.normativeId
        }
        return delNormativematterbind(params)
      }).then(() => {
        this.getBindList()
        this.$modal.msgSuccess("解绑成功")
      }).catch(() => {})
    },

    // ========== 下拉选项相关 ==========
    /** 获取事项下拉选项 */
    getMatterOptions() {
      listMatter({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.matterOptions = response.rows || []
      })
    },
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
.right-panel {
  display: flex;
  flex-direction: column;
}
.sub-panel {
  flex: 1;
}
.panel-header {
  display: flex;
  align-items: center;
  padding: 10px 0;
  font-weight: bold;
  border-bottom: 1px solid #ebeef5;
  margin-bottom: 10px;
}
</style>
