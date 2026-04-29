<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧：行业分类列表 -->
      <el-col :span="6" :xs="24">
        <div class="left-panel">
          <div class="panel-header">
            <span>行业分类</span>
            <el-radio-group v-model="viewMode" size="mini" style="margin-left: 10px;">
              <el-radio-button label="byCategory">按分类</el-radio-button>
              <el-radio-button label="byMatter">按事项</el-radio-button>
            </el-radio-group>
          </div>
          <!-- 按事项视图时显示搜索框 -->
          <el-input
            v-if="viewMode === 'byMatter'"
            v-model="matterSearchKeyword"
            placeholder="搜索事项名称"
            size="mini"
            clearable
            prefix-icon="el-icon-search"
            style="margin-bottom: 10px;"
            @input="handleMatterSearch"
          />
          <el-table
            v-loading="categoryLoading"
            :data="viewMode === 'byMatter' && matterSearchKeyword ? matterSearchResult : categoryList"
            :show-header="false"
            border
            stripe
            highlight-current-row
            @row-click="handleCategoryRowClick"
            :row-class-name="tableRowClassName"
            style="width: 100%; cursor: pointer;">
            <el-table-column prop="categoryName" label="分类名称">
              <template slot-scope="scope">
                <span v-if="viewMode === 'byCategory'">
                  {{ scope.row.categoryName }}
                </span>
                <span v-else>
                  {{ scope.row.matterName }}
                </span>
              </template>
            </el-table-column>
          </el-table>
        </div>
      </el-col>

      <!-- 右侧：关联事项列表 -->
      <el-col :span="18" :xs="24">
        <div class="right-panel">
          <div class="panel-header">
            <span>{{ viewMode === 'byCategory' ? '关联的监管事项' : '关联的行业分类' }}</span>
            <el-button type="primary" plain size="mini" icon="el-icon-plus" @click="handleAdd" style="margin-left: auto;">
              新增绑定
            </el-button>
          </div>

          <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
            <el-form-item label="事项名称" prop="matterName" v-if="viewMode === 'byCategory'">
              <el-input
                v-model="queryParams.matterName"
                placeholder="请输入事项名称"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item label="分类名称" prop="categoryName" v-else>
              <el-input
                v-model="queryParams.categoryName"
                placeholder="请输入分类名称"
                clearable
                @keyup.enter.native="handleQuery"
              />
            </el-form-item>
            <el-form-item>
              <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
              <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
            </el-form-item>
          </el-form>

          <el-table v-loading="loading" :data="bindList" @selection-change="handleSelectionChange">
            <el-table-column type="selection" width="55" align="center" />
            <el-table-column label="监管事项" align="center" prop="matterName" show-overflow-tooltip />
            <el-table-column label="行业分类" align="center" prop="categoryName" show-overflow-tooltip />
            <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="120">
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
                  icon="el-icon-delete"
                  @click="handleDelete(scope.row)"
                >解绑</el-button>
              </template>
            </el-table-column>
          </el-table>

          <pagination
            v-show="total>0"
            :total="total"
            :page.sync="queryParams.pageNum"
            :limit.sync="queryParams.pageSize"
            @pagination="getBindList"
          />
        </div>
      </el-col>
    </el-row>

    <!-- 新增绑定对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="监管事项" prop="matterId" v-if="viewMode === 'byCategory'">
          <el-select v-model="form.matterId" placeholder="请选择监管事项" filterable style="width: 100%">
            <el-option
              v-for="item in matterOptions"
              :key="item.id"
              :label="item.mainName"
              :value="item.id"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="行业分类" prop="categoryId" v-else>
          <el-select v-model="form.categoryId" placeholder="请选择行业分类" filterable style="width: 100%">
            <el-option
              v-for="item in categoryOptions"
              :key="item.categoryId"
              :label="item.categoryName"
              :value="item.categoryId"
            />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRegulatorycategorybind, getRegulatorycategorybind, delRegulatorycategorybind, addRegulatorycategorybind, listCategory, listBoundMatter } from "@/api/system/regulatorycategorybind"
import { listMatter } from "@/api/system/matter"

export default {
  name: "RegulatoryCategoryBind",
  data() {
    return {
      // 视图模式：byCategory-按分类查看, byMatter-按事项查看
      viewMode: 'byCategory',
      // 左侧分类列表
      categoryLoading: false,
      categoryList: [],
      categoryListAll: [],
      // 事项搜索关键词（按事项视图）
      matterSearchKeyword: '',
      matterSearchResult: [],
      // 右侧绑定列表
      loading: false,
      bindList: [],
      total: 0,
      showSearch: true,
      // 当前选中的分类/事项
      currentCategoryId: null,
      currentMatterId: null,
      // 事项下拉选项
      matterOptions: [],
      // 分类下拉选项
      categoryOptions: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        matterId: null,
        categoryId: null,
        matterName: null,
        categoryName: null
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        matterId: [
          { required: true, message: "监管事项不能为空", trigger: "change" }
        ],
        categoryId: [
          { required: true, message: "行业分类不能为空", trigger: "change" }
        ]
      }
    }
  },
  created() {
    this.getCategoryList()
    this.getMatterOptions()
  },
  methods: {
    /** 获取分类列表 */
    getCategoryList() {
      this.categoryLoading = true
      listCategory({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.categoryListAll = response.rows || []
        if (this.viewMode === 'byCategory') {
          this.getBindList()
        }
      }).finally(() => {
        this.categoryLoading = false
      })
    },
    /** 获取所有已绑定的事项列表（用于按事项视图） */
    getBoundMatterList() {
      listBoundMatter().then(response => {
        this.categoryList = response.data || []
      })
    },
    /** 事项搜索 */
    handleMatterSearch() {
      if (!this.matterSearchKeyword) {
        this.matterSearchResult = []
        return
      }
      const keyword = this.matterSearchKeyword.toLowerCase()
      this.matterSearchResult = this.categoryList.filter(item =>
        item.matterName && item.matterName.toLowerCase().includes(keyword)
      )
    },
    /** 获取事项下拉选项 */
    getMatterOptions() {
      listMatter({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.matterOptions = response.rows || []
      })
    },
    /** 点击分类行 */
    handleCategoryRowClick(row) {
      if (this.viewMode === 'byCategory') {
        this.currentCategoryId = row.categoryId
        this.currentMatterId = null
        this.queryParams.categoryId = row.categoryId
        this.queryParams.matterId = null
        this.getBindList()
      } else {
        this.currentMatterId = row.matterId
        this.currentCategoryId = null
        this.queryParams.matterId = row.matterId
        this.queryParams.categoryId = null
        this.getBindList()
      }
    },
    /** 设置行样式 */
    tableRowClassName({ row, rowIndex }) {
      if (this.viewMode === 'byCategory' && row.categoryId === this.currentCategoryId) {
        return 'current-row'
      }
      if (this.viewMode === 'byMatter' && row.matterId === this.currentMatterId) {
        return 'current-row'
      }
      return ''
    },
    /** 获取绑定列表 */
    getBindList() {
      this.loading = true
      listRegulatorycategorybind(this.queryParams).then(response => {
        this.bindList = response.rows || []
        this.total = response.total
        this.loading = false
        // 根据视图模式更新左侧列表显示
        this.updateCategoryList()
      })
    },
    /** 更新左侧列表（按绑定关系去重显示） */
    updateCategoryList() {
      if (this.viewMode === 'byCategory') {
        // 按分类视图：显示所有分类，高亮已选
        this.categoryList = this.categoryListAll
      } else {
        // 按事项视图：左侧列表已通过 getBoundMatterList 获取，无需从bindList构建
      }
    },
    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getBindList()
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.queryParams.matterName = null
      this.queryParams.categoryName = null
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.matterId + '_' + item.categoryId)
    },
    /** 新增按钮操作 */
    handleAdd() {
      // 根据当前视图模式预填表单
      if (this.viewMode === 'byCategory') {
        this.form = { matterId: null, categoryId: this.currentCategoryId }
      } else {
        this.form = { matterId: this.currentMatterId, categoryId: null }
      }
      this.open = true
      this.title = "新增绑定"
    },
    /** 查看详情 */
    handleView(row) {
      this.$message.info('查看详情：' + row.matterName + ' - ' + row.categoryName)
    },
    /** 解绑按钮操作 */
    handleDelete(row) {
      const params = {
        matterId: row.matterId,
        categoryId: row.categoryId
      }
      this.$modal.confirm('是否确认解除"' + row.matterName + '"与"' + row.categoryName + '"的绑定关系？')
        .then(() => {
          return delRegulatorycategorybind(params)
        }).then(() => {
          this.getBindList()
          if (this.viewMode === 'byMatter') {
            this.getBoundMatterList()
          }
          this.$modal.msgSuccess("解绑成功")
        }).catch(() => {})
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          addRegulatorycategorybind(this.form).then(response => {
            this.$modal.msgSuccess("绑定成功")
            this.open = false
            this.getBindList()
            if (this.viewMode === 'byMatter') {
              this.getBoundMatterList()
            }
          })
        }
      })
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.form = {}
    },
    /** 监听视图模式变化 */
    handleViewModeChange() {
      this.currentCategoryId = null
      this.currentMatterId = null
      this.matterSearchKeyword = ''
      this.matterSearchResult = []
      this.queryParams.matterId = null
      this.queryParams.categoryId = null
      this.queryParams.matterName = null
      this.queryParams.categoryName = null
      this.queryParams.pageNum = 1
      if (this.viewMode === 'byCategory') {
        this.getBindList()
      } else {
        this.getBoundMatterList()
      }
    }
  },
  watch: {
    viewMode(newVal) {
      this.handleViewModeChange()
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
