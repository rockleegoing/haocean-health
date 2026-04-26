<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="所属法规" prop="regulationId">
        <el-select v-model="queryParams.regulationId" placeholder="请选择法规" filterable clearable style="width: 200px" @change="onRegulationChange">
          <el-option
            v-for="item in regulationOptions"
            :key="item.regulationId"
            :label="item.title"
            :value="item.regulationId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="所属章节" prop="chapterId">
        <el-select v-model="queryParams.chapterId" placeholder="请选择章节" filterable clearable style="width: 200px" :disabled="!queryParams.regulationId">
          <el-option
            v-for="item in chapterOptions"
            :key="item.chapterId"
            :label="item.chapterNo + ' ' + item.chapterTitle"
            :value="item.chapterId"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="条款内容" prop="content">
        <el-input
          v-model="queryParams.content"
          placeholder="请输入条款内容关键词"
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
          v-hasPermi="['system:regulation:article:add']"
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
          v-hasPermi="['system:regulation:article:edit']"
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
          v-hasPermi="['system:regulation:article:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:regulation:article:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="articleList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="条款ID" align="center" prop="articleId" width="80" />
      <el-table-column label="所属法规" align="center" prop="regulationTitle" show-overflow-tooltip />
      <el-table-column label="所属章节" align="center" prop="chapterTitle" show-overflow-tooltip />
      <el-table-column label="条款号" align="center" prop="articleNo" width="100" />
      <el-table-column label="条款内容" align="center" prop="content" show-overflow-tooltip />
      <el-table-column label="排序" align="center" prop="sortOrder" width="80" />
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
            v-hasPermi="['system:regulation:article:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:regulation:article:remove']"
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

    <!-- 添加或修改条款对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="所属法规" prop="regulationId">
          <el-select v-model="form.regulationId" placeholder="请选择法规" filterable style="width: 100%" @change="onFormRegulationChange">
            <el-option
              v-for="item in regulationOptions"
              :key="item.regulationId"
              :label="item.title"
              :value="item.regulationId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="所属章节" prop="chapterId">
          <el-select v-model="form.chapterId" placeholder="请选择章节" filterable style="width: 100%" :disabled="!form.regulationId">
            <el-option
              v-for="item in formChapterOptions"
              :key="item.chapterId"
              :label="item.chapterNo + ' ' + item.chapterTitle"
              :value="item.chapterId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="条款号" prop="articleNo">
          <el-input v-model="form.articleNo" placeholder="如：第一条" />
        </el-form-item>
        <el-form-item label="条款内容" prop="content">
          <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入条款内容" />
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

    <!-- 条款详情对话框 -->
    <el-dialog title="条款详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="条款ID">{{ detailData.articleId }}</el-descriptions-item>
        <el-descriptions-item label="所属法规">{{ detailData.regulationTitle }}</el-descriptions-item>
        <el-descriptions-item label="所属章节">{{ detailData.chapterTitle }}</el-descriptions-item>
        <el-descriptions-item label="条款号">{{ detailData.articleNo }}</el-descriptions-item>
        <el-descriptions-item label="条款内容">{{ detailData.content }}</el-descriptions-item>
        <el-descriptions-item label="排序">{{ detailData.sortOrder }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRegulation, getChapterList, addArticle, updateArticle, delArticle } from "@/api/system/regulation"

export default {
  name: "RegulationArticle",
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
      // 条款表格数据
      articleList: [],
      // 法规选项
      regulationOptions: [],
      // 章节选项
      chapterOptions: [],
      // 表单章节选项
      formChapterOptions: [],
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
        chapterId: null,
        content: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        regulationId: [
          { required: true, message: "请选择所属法规", trigger: "change" }
        ],
        chapterId: [
          { required: true, message: "请选择所属章节", trigger: "change" }
        ],
        articleNo: [
          { required: true, message: "条款号不能为空", trigger: "blur" }
        ],
        content: [
          { required: true, message: "条款内容不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getRegulationOptions()
    // 如果有 regulationId 和 chapterId 参数，自动设置
    if (this.$route.query.regulationId) {
      this.queryParams.regulationId = parseInt(this.$route.query.regulationId)
    }
    if (this.$route.query.chapterId) {
      this.queryParams.chapterId = parseInt(this.$route.query.chapterId)
    }
    if (this.queryParams.regulationId) {
      this.loadChapters(this.queryParams.regulationId)
    }
    this.getList()
  },
  methods: {
    /** 查询条款列表 */
    getList() {
      this.loading = true
      // 使用条款列表接口，按 regulationId 筛选，支持后端分页
      getArticleList(this.queryParams.regulationId, {
        pageNum: this.queryParams.pageNum,
        pageSize: this.queryParams.pageSize,
        chapterId: this.queryParams.chapterId
      }).then(response => {
        this.articleList = response.rows || []
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
    /** 加载章节列表 */
    loadChapters(regulationId) {
      if (!regulationId) {
        this.chapterOptions = []
        return
      }
      getChapterList(regulationId).then(response => {
        this.chapterOptions = response.data || []
      })
    },
    /** 加载表单章节列表 */
    loadFormChapters(regulationId) {
      if (!regulationId) {
        this.formChapterOptions = []
        return
      }
      getChapterList(regulationId).then(response => {
        this.formChapterOptions = response.data || []
      })
    },
    /** 法规选择变化 */
    onRegulationChange(regulationId) {
      this.queryParams.chapterId = null
      this.loadChapters(regulationId)
    },
    /** 表单法规选择变化 */
    onFormRegulationChange(regulationId) {
      this.form.chapterId = null
      this.loadFormChapters(regulationId)
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    // 表单重置
    reset() {
      this.form = {
        articleId: null,
        regulationId: null,
        chapterId: null,
        articleNo: null,
        content: null,
        sortOrder: 0,
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
      this.queryParams.chapterId = null
      this.handleQuery()
    },
    // 多选框选中数据
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.articleId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加条款"
      // 如果查询条件有法规和章节，自动选中
      if (this.queryParams.regulationId) {
        this.form.regulationId = this.queryParams.regulationId
        this.loadFormChapters(this.queryParams.regulationId)
      }
      if (this.queryParams.chapterId) {
        this.form.chapterId = this.queryParams.chapterId
      }
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      this.form = {
        articleId: row.articleId,
        regulationId: row.regulationId,
        chapterId: row.chapterId,
        articleNo: row.articleNo,
        content: row.content,
        sortOrder: row.sortOrder || 0,
      }
      this.loadFormChapters(row.regulationId)
      this.open = true
      this.title = "修改条款"
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      this.detailData = {
        articleId: row.articleId,
        regulationTitle: row.regulationTitle || this.getRegulationTitle(row.regulationId),
        chapterTitle: row.chapterTitle || this.getChapterTitle(row.chapterId),
        articleNo: row.articleNo,
        content: row.content,
        sortOrder: row.sortOrder,
      }
      this.detailOpen = true
    },
    getRegulationTitle(regulationId) {
      const reg = this.regulationOptions.find(r => r.regulationId === regulationId)
      return reg ? reg.title : ''
    },
    getChapterTitle(chapterId) {
      const ch = this.chapterOptions.find(c => c.chapterId === chapterId)
      return ch ? ch.chapterTitle : ''
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.articleId != null) {
            updateArticle(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addArticle(this.form).then(response => {
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
      const articleIds = row.articleId || this.ids
      this.$modal.confirm('是否确认删除条款编号为"' + articleIds + '"的数据项？').then(() => {
        return delArticle(articleIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/regulation/article/export', {
        ...this.queryParams
      }, `article_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
