<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧：章节列表 -->
      <el-col :span="8">
        <div class="head-container">
          <el-input
            v-model="chapterName"
            placeholder="请输入章节名称"
            clearable
            size="small"
            prefix-icon="el-icon-search"
            style="margin-bottom: 20px"
          />
        </div>
        <el-table
          v-loading="chapterLoading"
          :data="chapterList"
          highlight-current-row
          @row-click="handleChapterClick"
          :height="tableHeight"
        >
          <el-table-column label="章节" prop="chapterTitle">
            <template slot-scope="scope">
              <span>{{ scope.row.chapterNo }} {{ scope.row.chapterTitle }}</span>
            </template>
          </el-table-column>
        </el-table>
      </el-col>

      <!-- 右侧：关联列表 -->
      <el-col :span="16">
        <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
          <el-form-item label="依据类型" prop="basisType">
            <el-select v-model="queryParams.basisType" placeholder="请选择依据类型" clearable>
              <el-option label="定性依据" value="legal" />
              <el-option label="处理依据" value="processing" />
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
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="handleAdd"
              v-hasPermi="['system:basisLink:add']"
            >新增</el-button>
          </el-col>
          <el-col :span="1.5">
            <el-button
              type="danger"
              plain
              icon="el-icon-delete"
              size="mini"
              :disabled="multiple"
              @click="handleDelete"
              v-hasPermi="['system:basisLink:remove']"
            >删除</el-button>
          </el-col>
          <right-toolbar :showSearch.sync="showSearch" @queryTable="getLinkList"></right-toolbar>
        </el-row>

        <el-table v-loading="linkLoading" :data="linkList" @selection-change="handleSelectionChange">
          <el-table-column type="selection" width="55" align="center" />
          <el-table-column label="章节" align="center" prop="chapterTitle" width="150" />
          <el-table-column label="条款" align="center" prop="articleNo" width="100" />
          <el-table-column label="依据类型" align="center" prop="basisType" width="100">
            <template slot-scope="scope">
              <el-tag v-if="scope.row.basisType === 'legal'" type="success">定性依据</el-tag>
              <el-tag v-else type="warning">处理依据</el-tag>
            </template>
          </el-table-column>
          <el-table-column label="依据标题" align="center" prop="basisTitle" :show-overflow-tooltip="true" />
          <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
            <template slot-scope="scope">
              <el-button
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
                v-hasPermi="['system:basisLink:remove']"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <pagination
          v-show="total>0"
          :total="total"
          :page.sync="queryParams.pageNum"
          :limit.sync="queryParams.pageSize"
          @pagination="getLinkList"
        />
      </el-col>
    </el-row>

    <!-- 添加关联对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-form-item label="章节" prop="chapterId">
          <el-select v-model="form.chapterId" placeholder="请选择章节" @change="handleChapterChange">
            <el-option
              v-for="ch in chapterList"
              :key="ch.chapterId"
              :label="ch.chapterNo + ' ' + ch.chapterTitle"
              :value="ch.chapterId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="条款" prop="articleId">
          <el-select v-model="form.articleId" placeholder="请选择条款" clearable>
            <el-option
              v-for="art in articleList"
              :key="art.articleId"
              :label="art.articleNo"
              :value="art.articleId"
            />
          </el-select>
        </el-form-item>
        <el-form-item label="依据类型" prop="basisType">
          <el-radio-group v-model="form.basisType" @change="handleBasisTypeChange">
            <el-radio label="legal">定性依据</el-radio>
            <el-radio label="processing">处理依据</el-radio>
          </el-radio-group>
        </el-form-item>
        <el-form-item label="选择依据" prop="basisId">
          <el-select v-model="form.basisId" placeholder="请选择依据" filterable>
            <el-option
              v-for="basis in basisSelectList"
              :key="basis.basisId"
              :label="basis.basisNo + ' ' + basis.title"
              :value="basis.basisId"
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
import { listBasisLink, getBasisLink, delBasisLink, addBasisLink, getBasisLinkByChapterId } from "@/api/system/basisLink"
import { listRegulation, getChapterList } from "@/api/system/regulation"
import { listProcessingBasis } from "@/api/system/processingBasis"
import { listLegalBasis } from "@/api/system/regulation"

export default {
  name: "BasisLink",
  data() {
    return {
      loading: true,
      chapterLoading: true,
      linkLoading: true,
      tableHeight: 500,
      chapterName: "",
      chapterList: [],
      currentChapter: null,
      articleList: [],
      linkList: [],
      total: 0,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      title: "",
      open: false,
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        chapterId: null,
        basisType: null
      },
      form: {
        chapterId: null,
        articleId: null,
        basisType: "legal",
        basisId: null
      },
      rules: {
        chapterId: [
          { required: true, message: "请选择章节", trigger: "change" }
        ],
        basisType: [
          { required: true, message: "请选择依据类型", trigger: "change" }
        ],
        basisId: [
          { required: true, message: "请选择依据", trigger: "change" }
        ]
      },
      legalBasisList: [],
      processingBasisList: [],
      basisSelectList: []
    }
  },
  created() {
    this.getRegulationList()
  },
  methods: {
    getRegulationList() {
      this.chapterLoading = true
      listRegulation({ pageNum: 1, pageSize: 100 }).then(response => {
        const regulations = response.rows || []
        const promises = regulations.map(reg => {
          return getChapterList(reg.regulationId, { pageNum: 1, pageSize: 500 }).then(chapterRes => {
            const chapters = chapterRes.rows || []
            return chapters.map(ch => ({
              ...ch,
              regulationTitle: reg.title
            }))
          })
        })
        Promise.all(promises).then(chapterArrays => {
          this.chapterList = chapterArrays.flat()
          this.chapterLoading = false
          if (this.chapterList.length > 0) {
            this.handleChapterClick(this.chapterList[0])
          }
        })
      })
    },
    handleChapterClick(row) {
      this.currentChapter = row
      this.queryParams.chapterId = row.chapterId
      this.articleList = row.articles || []
      this.getLinkList()
    },
    handleChapterChange(chapterId) {
      const chapter = this.chapterList.find(c => c.chapterId === chapterId)
      if (chapter) {
        this.articleList = chapter.articles || []
      }
      this.form.articleId = null
    },
    handleBasisTypeChange(basisType) {
      this.form.basisId = null
      if (basisType === 'legal') {
        this.basisSelectList = this.legalBasisList
      } else {
        this.basisSelectList = this.processingBasisList
      }
    },
    getLinkList() {
      if (!this.currentChapter) {
        this.linkList = []
        this.linkLoading = false
        return
      }
      this.linkLoading = true
      getBasisLinkByChapterId(this.currentChapter.chapterId).then(response => {
        let list = response.data || []
        if (this.queryParams.basisType) {
          list = list.filter(item => item.basisType === this.queryParams.basisType)
        }
        this.linkList = list
        this.total = list.length
        this.linkLoading = false
      })
    },
    handleQuery() {
      this.getLinkList()
    },
    resetQuery() {
      this.queryParams.basisType = null
      this.handleQuery()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.linkId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    handleAdd() {
      if (!this.currentChapter) {
        this.$modal.msgWarning("请先选择章节")
        return
      }
      this.title = "添加关联"
      this.open = true
      this.form = {
        chapterId: this.currentChapter.chapterId,
        articleId: null,
        basisType: "legal",
        basisId: null
      }
      // 加载依据列表
      this.loadBasisLists()
      this.basisSelectList = this.legalBasisList
    },
    loadBasisLists() {
      // 加载定性依据
      listLegalBasis({ pageNum: 1, pageSize: 500 }).then(response => {
        this.legalBasisList = response.rows || []
      })
      // 加载处理依据
      listProcessingBasis({ pageNum: 1, pageSize: 500 }).then(response => {
        this.processingBasisList = response.rows || []
      })
    },
    cancel() {
      this.open = false
      this.reset()
    },
    reset() {
      this.form = {
        chapterId: null,
        articleId: null,
        basisType: "legal",
        basisId: null
      }
      this.resetForm("form")
    },
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          addBasisLink(this.form).then(response => {
            this.$modal.msgSuccess("新增成功")
            this.open = false
            this.getLinkList()
          })
        }
      })
    },
    handleDelete(row) {
      const linkIds = row.linkId || this.ids
      this.$modal.confirm('是否确认删除选中的关联数据？').then(() => {
        return delBasisLink(linkIds)
      }).then(() => {
        this.getLinkList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    }
  }
}
</script>
