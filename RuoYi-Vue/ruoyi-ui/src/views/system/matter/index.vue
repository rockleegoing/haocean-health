<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧：监管事项列表 -->
      <el-col :span="6" :xs="24">
        <div class="left-panel">
          <div class="panel-header">
            <span>监管事项</span>
            <el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddMatter" style="margin-left: auto;">
              新增事项
            </el-button>
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
            style="width: 100%; cursor: pointer;">
            <el-table-column prop="mainName" label="事项名称">
              <template slot-scope="scope">
                <span>{{ scope.row.mainName || scope.row.main_name }}</span>
              </template>
            </el-table-column>
          </el-table>
          <pagination
            v-show="matterTotal>0"
            :total="matterTotal"
            :page.sync="matterQueryParams.pageNum"
            :limit.sync="matterQueryParams.pageSize"
            @pagination="getMatterList"
            layout="prev, pager, next"
            style="margin-top: 5px;"
          />
        </div>
      </el-col>

      <!-- 右侧：子项列表 -->
      <el-col :span="18" :xs="24">
        <div class="right-panel">
          <div class="panel-header">
            <span v-if="currentMatterId">关联的子项: {{ currentMatterName }}</span>
            <span v-else>请选择左侧事项</span>
            <div style="margin-left: auto; display: flex;">
              <el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAddItem" :disabled="!currentMatterId">
                新增子项
              </el-button>
              <el-button type="warning" plain size="mini" icon="el-icon-download" @click="handleExport" :disabled="!currentMatterId">
                导出
              </el-button>
            </div>
          </div>

          <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
            <el-form-item label="子项编码" prop="itemCode">
              <el-input
                v-model="queryParams.itemCode"
                placeholder="请输入子项编码"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item label="详情名称" prop="itemName">
              <el-input
                v-model="queryParams.itemName"
                placeholder="请输入详情名称"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table v-loading="loading" :data="itemList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="编码类型" align="center" prop="codeType" width="100" show-overflow-tooltip />
            <el-table-column label="子项编码" align="center" prop="itemCode" width="120" show-overflow-tooltip />
            <el-table-column label="详情名称" align="center" prop="itemName" show-overflow-tooltip />
            <el-table-column label="依据" align="center" prop="according" show-overflow-tooltip />
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
            @pagination="getItemList"
          />
        </div>
      </el-col>
    </el-row>

    <!-- 新增/修改事项对话框 -->
    <el-dialog :title="matterTitle" :visible.sync="matterOpen" width="500px" append-to-body>
      <el-form ref="matterForm" :model="matterForm" :rules="matterRules" label-width="100px">
        <el-form-item label="数据源ID" prop="countyId">
          <el-input v-model="matterForm.countyId" placeholder="请输入数据源ID" />
        </el-form-item>
        <el-form-item label="事项编码" prop="mainCode">
          <el-input v-model="matterForm.mainCode" placeholder="请输入事项编码" />
        </el-form-item>
        <el-form-item label="事项名称" prop="mainName">
          <el-input v-model="matterForm.mainName" placeholder="请输入事项名称" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitMatterForm">确 定</el-button>
        <el-button @click="cancelMatter">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 新增/修改子项对话框 -->
    <el-dialog :title="itemTitle" :visible.sync="itemOpen" width="600px" append-to-body>
      <el-form ref="itemForm" :model="itemForm" :rules="itemRules" label-width="100px">
        <el-form-item label="监管事项" prop="regulatoryMatterId">
          <el-select v-model="itemForm.regulatoryMatterId" placeholder="请选择监管事项" filterable style="width: 100%">
            <el-option
              v-for="item in matterOptions"
              :key="item.id"
              :label="item.mainName || item.main_name"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="数据源ID" prop="countyItemId">
          <el-input v-model="itemForm.countyItemId" placeholder="请输入数据源ID" />
        </el-form-item>
        <el-form-item label="编码类型" prop="codeType">
          <el-input v-model="itemForm.codeType" placeholder="请输入编码类型" />
        </el-form-item>
        <el-form-item label="子项编码" prop="itemCode">
          <el-input v-model="itemForm.itemCode" placeholder="请输入子项编码" />
        </el-form-item>
        <el-form-item label="详情名称" prop="itemName">
          <el-input v-model="itemForm.itemName" type="textarea" placeholder="请输入详情名称" />
        </el-form-item>
        <el-form-item label="依据" prop="according">
          <el-input v-model="itemForm.according" type="textarea" placeholder="请输入依据" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitItemForm">确 定</el-button>
        <el-button @click="cancelItem">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 查看详情对话框 -->
    <el-dialog title="子项详情" :visible.sync="viewOpen" width="600px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="编码类型">{{ viewData.codeType }}</el-descriptions-item>
        <el-descriptions-item label="子项编码">{{ viewData.itemCode }}</el-descriptions-item>
        <el-descriptions-item label="详情名称" :span="2">{{ viewData.itemName }}</el-descriptions-item>
        <el-descriptions-item label="依据" :span="2">{{ viewData.according }}</el-descriptions-item>
        <el-descriptions-item label="数据源ID">{{ viewData.countyItemId }}</el-descriptions-item>
        <el-descriptions-item label="删除状态">{{ viewData.isDeleted === 1 ? '是' : '否' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="viewOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listMatter, getMatter, addMatter, updateMatter, delMatter } from "@/api/system/matter"
import { listMatteritem, getMatteritem, addMatteritem, updateMatteritem, delMatteritem } from "@/api/system/matteritem"

export default {
  name: "MatterWithItem",
  data() {
    return {
      // 左侧事项相关
      matterLoading: false,
      matterList: [],
      matterTotal: 0,
      matterQueryParams: {
        pageNum: 1,
        pageSize: 10,
        mainName: null
      },
      matterSearchKeyword: '',
      matterSearchResult: [],
      currentMatterId: null,
      currentMatterName: '',
      matterTitle: "",
      matterOpen: false,
      matterForm: {},
      matterRules: {
        mainName: [{ required: true, message: "事项名称不能为空", trigger: "blur" }]
      },
      matterOptions: [],

      // 右侧子项相关
      loading: false,
      itemList: [],
      total: 0,
      showSearch: true,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        regulatoryMatterId: null,
        itemCode: null,
        itemName: null
      },
      itemTitle: "",
      itemOpen: false,
      itemForm: {},
      itemRules: {
        regulatoryMatterId: [{ required: true, message: "监管事项不能为空", trigger: "change" }]
      },

      // 查看详情
      viewOpen: false,
      viewData: {}
    }
  },
  created() {
    this.getMatterList()
  },
  methods: {
    // ========== 左侧事项相关 ==========
    /** 获取事项列表 */
    getMatterList() {
      this.matterLoading = true
      listMatter(this.matterQueryParams).then(response => {
        this.matterList = response.rows || []
        this.matterTotal = response.total
        this.matterLoading = false
        // 更新下拉选项
        this.matterOptions = this.matterList
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
        (item.mainName || '').toLowerCase().includes(keyword) ||
        (item.main_code || '').toLowerCase().includes(keyword)
      )
    },
    /** 点击事项行 */
    handleMatterRowClick(row) {
      this.currentMatterId = row.id
      this.currentMatterName = row.mainName || row.main_name
      this.queryParams.regulatoryMatterId = row.id
      this.queryParams.pageNum = 1
      this.getItemList()
    },
    /** 设置事项行样式 */
    matterRowClassName({ row, rowIndex }) {
      if (row.id === this.currentMatterId) {
        return 'current-row'
      }
      return ''
    },
    /** 新增事项按钮 */
    handleAddMatter() {
      this.matterForm = {}
      this.matterTitle = "新增事项"
      this.matterOpen = true
    },
    /** 提交事项表单 */
    submitMatterForm() {
      this.$refs["matterForm"].validate(valid => {
        if (valid) {
          if (this.matterForm.id) {
            updateMatter(this.matterForm).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.matterOpen = false
              this.getMatterList()
            })
          } else {
            addMatter(this.matterForm).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.matterOpen = false
              this.getMatterList()
            })
          }
        }
      })
    },
    /** 取消事项弹窗 */
    cancelMatter() {
      this.matterOpen = false
      this.matterForm = {}
    },

    // ========== 右侧子项相关 ==========
    /** 获取子项列表 */
    getItemList() {
      if (!this.currentMatterId) {
        this.itemList = []
        return
      }
      this.loading = true
      listMatteritem(this.queryParams).then(response => {
        this.itemList = response.rows || []
        this.total = response.total
        this.loading = false
      })
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getItemList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams.itemCode = null
      this.queryParams.itemName = null
      this.queryParams.pageNum = 1
      this.getItemList()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
    },
    /** 新增子项按钮 */
    handleAddItem() {
      this.itemForm = { regulatoryMatterId: this.currentMatterId }
      this.itemTitle = "新增子项"
      this.itemOpen = true
    },
    /** 查看详情 */
    handleView(row) {
      this.viewData = row
      this.viewOpen = true
    },
    /** 修改子项 */
    handleUpdate(row) {
      this.itemForm = Object.assign({}, row)
      this.itemTitle = "修改子项"
      this.itemOpen = true
    },
    /** 删除子项 */
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除子项编号为"' + ids + '"的数据项？').then(() => {
        return delMatteritem(ids)
      }).then(() => {
        this.getItemList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 提交子项表单 */
    submitItemForm() {
      this.$refs["itemForm"].validate(valid => {
        if (valid) {
          if (this.itemForm.id) {
            updateMatteritem(this.itemForm).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.itemOpen = false
              this.getItemList()
            })
          } else {
            addMatteritem(this.itemForm).then(response => {
              this.$modal.msgSuccess("新增成功")
              this.itemOpen = false
              this.getItemList()
            })
          }
        }
      })
    },
    /** 取消子项弹窗 */
    cancelItem() {
      this.itemOpen = false
      this.itemForm = {}
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/matteritem/export', {
        ...this.queryParams
      }, `matteritem_${new Date().getTime()}.xlsx`)
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
</style>
