<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="所属法规" prop="regulationId">
        <el-select v-model="queryParams.regulationId" placeholder="请选择法规" filterable clearable style="width: 240px">
          <el-option
            v-for="item in regulationOptions"
            :key="item.regulationId"
            :label="item.title"
            :value="item.regulationId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="章节标题" prop="chapterTitle">
        <el-input
          v-model="queryParams.chapterTitle"
          placeholder="请输入章节标题"
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
          v-hasPermi="['system:regulation:chapter:add']"
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
          v-hasPermi="['system:regulation:chapter:edit']"
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
          v-hasPermi="['system:regulation:chapter:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:regulation:chapter:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="chapterList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="章节ID" align="center" prop="chapterId" width="80" />
      <el-table-column label="所属法规" align="center" prop="regulationTitle" show-overflow-tooltip />
      <el-table-column label="章节号" align="center" prop="chapterNo" width="100" />
      <el-table-column label="章节标题" align="center" prop="chapterTitle" show-overflow-tooltip />
      <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
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
            v-hasPermi="['system:regulation:chapter:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:regulation:chapter:remove']"
          >删除</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-document"
            @click="handleManageArticles(scope.row)"
          >条款</el-button>
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

    <!-- 添加或修改章节对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属法规" prop="regulationId">
          <el-select v-model="form.regulationId" placeholder="请选择法规" filterable style="width: 100%">
            <el-option
              v-for="item in regulationOptions"
              :key="item.regulationId"
              :label="item.title"
              :value="item.regulationId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="章节号" prop="chapterNo">
          <el-input v-model="form.chapterNo" placeholder="如：第一章" />
        </el-form-item>
        <el-form-item label="章节标题" prop="chapterTitle">
          <el-input v-model="form.chapterTitle" placeholder="请输入章节标题" />
        </el-form-item>
        <el-form-item label="排序" prop="sortOrder">
          <el-input-number v-model="form.sortOrder" :min="0" controls-position="right" style="width: 100%" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 章节详情对话框 -->
    <el-dialog title="章节详情" :visible.sync="detailOpen" width="500px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="章节ID">{{ detailData.chapterId }}</el-descriptions-item>
        <el-descriptions-item label="所属法规">{{ detailData.regulationTitle }}</el-descriptions-item>
        <el-descriptions-item label="章节号">{{ detailData.chapterNo }}</el-descriptions-item>
        <el-descriptions-item label="章节标题">{{ detailData.chapterTitle }}</el-descriptions-item>
        <el-descriptions-item label="排序">{{ detailData.sortOrder }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { getChapter, delChapter, addChapter, updateChapter, getChapterList } from "@/api/system/regulation"
import { listRegulation } from "@/api/system/regulation"

export default {
  name: "RegulationChapter",
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
      // 章节表格数据
      chapterList: [],
      // 法规选项
      regulationOptions: [],
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
        regulationId: null,
        chapterTitle: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        regulationId: [
          { required: true, message: "请选择所属法规", trigger: "change" }
        ],
        chapterNo: [
          { required: true, message: "章节号不能为空", trigger: "blur" }
        ],
        chapterTitle: [
          { required: true, message: "章节标题不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getRegulationOptions()
    // 如果有 regulationId 参数，自动设置
    if (this.$route.query.regulationId) {
      this.queryParams.regulationId = parseInt(this.$route.query.regulationId)
    }
    this.getList()
  },
  methods: {
    /** 查询章节列表 */
    getList() {
      if (!this.queryParams.regulationId) {
        this.chapterList = []
        this.total = 0
        this.loading = false
        return
      }
      this.loading = true
      getChapterList(this.queryParams.regulationId, {
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize
      }).then(response => {
        this.chapterList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
    },
    /** 获取法规选项 */
    getRegulationOptions() {
      listRegulation({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.regulationOptions = response.rows || []
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
        chapterId: null,
        regulationId: null,
        chapterNo: null,
        chapterTitle: null,
        sortOrder: 0,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
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
      this.ids = selection.map(item => item.chapterId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加章节"
      // 如果查询条件有法规，自动选中
      if (this.queryParams.regulationId) {
        this.form.regulationId = this.queryParams.regulationId
      }
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const chapterId = row.chapterId || this.ids
      getChapter(chapterId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改章节"
      })
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      getChapter(row.chapterId).then(response => {
        this.detailData = response.data || {}
        this.detailOpen = true
      })
    },
    /** 管理条款按钮 */
    handleManageArticles(row) {
      this.$router.push({
        path: '/system/regulation/article',
        query: { chapterId: row.chapterId, regulationId: row.regulationId }
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.chapterId != null) {
            updateChapter(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addChapter(this.form).then(response => {
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
      const chapterIds = row.chapterId || this.ids
      this.$modal.confirm('是否确认删除章节编号为"' + chapterIds + '"的数据项？').then(function() {
        return delChapter(chapterIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/regulation/chapter/export', {
        ...this.queryParams
      }, `chapter_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
