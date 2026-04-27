<template>
  <div class="app-container">
    <el-row :gutter="20">
      <!-- 左侧：法规-章节-条款树 -->
      <el-col :span="8" :xs="24">
        <div class="tree-container">
          <div class="tree-header">
            <span>法规结构</span>
            <el-button icon="el-icon-refresh" size="mini" @click="refreshTree">刷新</el-button>
          </div>
          <el-tree
            ref="tree"
            :data="treeData"
            :props="treeProps"
            node-key="id"
            :expand-on-click-node="false"
            :default-expand-all="true"
            @node-click="handleNodeClick"
          >
            <span slot-scope="{ node, data }" class="tree-node">
              <span>{{ node.label }}</span>
              <span v-if="data.type === 'article'" class="node-count">
                <el-tag v-if="data.legalCount > 0" size="mini" type="primary">定性{{ data.legalCount }}</el-tag>
                <el-tag v-if="data.processingCount > 0" size="mini" type="warning">处理{{ data.processingCount }}</el-tag>
              </span>
            </span>
          </el-tree>
        </div>
      </el-col>

      <!-- 右侧：关联列表 -->
      <el-col :span="16" :xs="24">
        <div class="link-container">
          <el-tabs v-model="activeTab" @tab-click="handleTabClick">
            <el-tab-pane label="定性依据" name="legal">
              <el-row :gutter="10" class="mb8">
                <el-col :span="1.5">
                  <el-button
                    type="primary"
                    plain
                    icon="el-icon-plus"
                    size="mini"
                    :disabled="!selectedArticle"
                    @click="handleAddLink('legal')"
                  >新增关联</el-button>
                </el-col>
                <el-col :span="1.5">
                  <el-button
                    type="danger"
                    plain
                    icon="el-icon-delete"
                    size="mini"
                    :disabled="selectedLegalLinks.length === 0"
                    @click="handleDeleteLinks('legal')"
                  >删除</el-button>
                </el-col>
              </el-row>
              <el-table
                v-loading="loading"
                :data="legalLinks"
                @selection-change="handleLegalSelectionChange"
              >
                <el-table-column type="selection" width="55" align="center" />
                <el-table-column label="编号" align="center" prop="basisNo" width="80" />
                <el-table-column label="标题" align="center" prop="title" show-overflow-tooltip />
                <el-table-column label="违法类型" align="center" prop="violationType" />
                <el-table-column label="操作" align="center" width="100">
                  <template slot-scope="scope">
                    <el-button
                      size="mini"
                      type="text"
                      icon="el-icon-delete"
                      @click="handleDeleteLink(scope.row, 'legal')"
                    >删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>

            <el-tab-pane label="处理依据" name="processing">
              <el-row :gutter="10" class="mb8">
                <el-col :span="1.5">
                  <el-button
                    type="primary"
                    plain
                    icon="el-icon-plus"
                    size="mini"
                    :disabled="!selectedArticle"
                    @click="handleAddLink('processing')"
                  >新增关联</el-button>
                </el-col>
                <el-col :span="1.5">
                  <el-button
                    type="danger"
                    plain
                    icon="el-icon-delete"
                    size="mini"
                    :disabled="selectedProcessingLinks.length === 0"
                    @click="handleDeleteLinks('processing')"
                  >删除</el-button>
                </el-col>
              </el-row>
              <el-table
                v-loading="loading"
                :data="processingLinks"
                @selection-change="handleProcessingSelectionChange"
              >
                <el-table-column type="selection" width="55" align="center" />
                <el-table-column label="编号" align="center" prop="basisNo" width="80" />
                <el-table-column label="标题" align="center" prop="title" show-overflow-tooltip />
                <el-table-column label="违法类型" align="center" prop="violationType" />
                <el-table-column label="操作" align="center" width="100">
                  <template slot-scope="scope">
                    <el-button
                      size="mini"
                      type="text"
                      icon="el-icon-delete"
                      @click="handleDeleteLink(scope.row, 'processing')"
                    >删除</el-button>
                  </template>
                </el-table-column>
              </el-table>
            </el-tab-pane>
          </el-tabs>
        </div>
      </el-col>
    </el-row>

    <!-- 新增关联对话框 -->
    <el-dialog :title="'新增' + (linkType === 'legal' ? '定性依据' : '处理依据') + '关联'" :visible.sync="addLinkOpen" width="700px" append-to-body>
      <el-form :model="addLinkQuery" :inline="true" label-width="80px">
        <el-form-item label="标题">
          <el-input v-model="addLinkQuery.title" placeholder="请输入标题" clearable @keyup.enter.native="searchBasis" />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" icon="el-icon-search" @click="searchBasis">搜索</el-button>
        </el-form-item>
      </el-form>
      <el-table
        v-loading="basisLoading"
        :data="basisList"
        @selection-change="handleBasisSelectionChange"
        height="300"
      >
        <el-table-column type="selection" width="55" align="center" />
        <el-table-column label="编号" align="center" prop="basisNo" width="80" />
        <el-table-column label="标题" align="center" prop="title" show-overflow-tooltip />
        <el-table-column label="违法类型" align="center" prop="violationType" />
      </el-table>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitLink">确 定</el-button>
        <el-button @click="cancelLink">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRegulation } from "@/api/system/regulation"
import { getLegalBasisByArticle, getProcessingBasisByArticle, addBasisLink, delBasisLink, listBasisLink } from "@/api/system/basisLink"
import { listLegalBasis } from "@/api/system/regulation"
import { listProcessingBasis } from "@/api/system/processingBasis"

export default {
  name: "BasisLink",
  data() {
    return {
      // 加载状态
      loading: false,
      basisLoading: false,
      // 树数据
      treeData: [],
      treeProps: {
        children: 'children',
        label: 'label'
      },
      // 选中的节点
      selectedNode: null,
      selectedArticle: null,
      // 当前Tab
      activeTab: 'legal',
      // 关联类型
      linkType: 'legal',
      // 定性依据列表
      legalLinks: [],
      // 处理依据列表
      processingLinks: [],
      // 新增关联
      addLinkOpen: false,
      // 依据列表
      basisList: [],
      // 新增关联表单
      addLinkQuery: {
        title: ''
      },
      // 选中的依据
      selectedBasises: [],
      // 选中的关联
      selectedLegalLinks: [],
      selectedProcessingLinks: []
    }
  },
  created() {
    this.loadRegulationTree()
  },
  methods: {
    /** 加载法规树 */
    loadRegulationTree() {
      listRegulation({ pageNum: 1, pageSize: 1000 }).then(response => {
        const regulations = response.rows || []
        this.treeData = regulations.map(reg => ({
          id: 'reg_' + reg.regulationId,
          label: reg.title,
          type: 'regulation',
          regulationId: reg.regulationId,
          children: []
        }))
        // 加载章节
        this.loadChapters(this.treeData)
      })
    },
    /** 加载章节 */
    loadChapters(nodes) {
      nodes.forEach(node => {
        if (node.type === 'regulation') {
          this.$axios.get('/system/regulation/chapters/' + node.regulationId).then(res => {
            if (res.code === 200 && res.data) {
              node.children = (res.data || []).map(ch => ({
                id: 'ch_' + ch.chapterId,
                label: (ch.chapterNo || '') + ' ' + (ch.chapterTitle || ''),
                type: 'chapter',
                chapterId: ch.chapterId,
                regulationId: node.regulationId,
                children: []
              }))
              // 加载条款
              this.loadArticles(node.children)
            }
          })
        }
      })
    },
    /** 加载条款 */
    loadArticles(chapters) {
      chapters.forEach(chapter => {
        if (chapter.type === 'chapter') {
          this.$axios.get('/system/regulation/articles/' + chapter.regulationId + '?chapterId=' + chapter.chapterId).then(res => {
            if (res.code === 200 && res.data) {
              chapter.children = (res.data || []).map(art => ({
                id: 'art_' + art.articleId,
                label: (art.articleNo || '') + ' ' + ((art.content || '').substring(0, 20) + '...'),
                type: 'article',
                articleId: art.articleId,
                chapterId: chapter.chapterId,
                regulationId: chapter.regulationId,
                legalCount: 0,
                processingCount: 0
              }))
              // 加载关联统计
              this.loadLinkCounts(chapter.children)
            }
          })
        }
      })
    },
    /** 加载关联统计 */
    loadLinkCounts(articles) {
      articles.forEach(art => {
        listBasisLink({ articleId: art.articleId }).then(res => {
          if (res.code === 200) {
            const links = res.data || []
            art.legalCount = links.filter(l => l.basisType === 'legal').length
            art.processingCount = links.filter(l => l.basisType === 'processing').length
            // 强制更新树
            this.$refs.tree.updateKeyChild(art.id, art)
          }
        })
      })
    },
    /** 刷新树 */
    refreshTree() {
      this.loadRegulationTree()
    },
    /** 节点点击 */
    handleNodeClick(data) {
      this.selectedNode = data
      if (data.type === 'article') {
        this.selectedArticle = data
        this.loadLinks(data.articleId)
      } else {
        this.selectedArticle = null
        this.legalLinks = []
        this.processingLinks = []
      }
    },
    /** 加载关联列表 */
    loadLinks(articleId) {
      this.loading = true
      Promise.all([
        getLegalBasisByArticle(articleId),
        getProcessingBasisByArticle(articleId)
      ]).then(([legalRes, processingRes]) => {
        this.legalLinks = legalRes.data || []
        this.processingLinks = processingRes.data || []
        this.loading = false
      }).catch(() => {
        this.loading = false
      })
    },
    /** Tab切换 */
    handleTabClick(tab) {
      this.linkType = tab.name
    },
    /** 新增关联 */
    handleAddLink(type) {
      this.linkType = type
      this.addLinkQuery.title = ''
      this.basisList = []
      this.selectedBasises = []
      this.addLinkOpen = true
    },
    /** 搜索依据 */
    searchBasis() {
      this.basisLoading = true
      if (this.linkType === 'legal') {
        listLegalBasis({ title: this.addLinkQuery.title, pageNum: 1, pageSize: 100 }).then(res => {
          this.basisList = res.rows || []
          this.basisLoading = false
        }).catch(() => {
          this.basisLoading = false
        })
      } else {
        listProcessingBasis({ title: this.addLinkQuery.title, pageNum: 1, pageSize: 100 }).then(res => {
          this.basisList = res.rows || []
          this.basisLoading = false
        }).catch(() => {
          this.basisLoading = false
        })
      }
    },
    /** 依据选择变化 */
    handleBasisSelectionChange(selection) {
      this.selectedBasises = selection
    },
    /** 提交关联 */
    submitLink() {
      if (this.selectedBasises.length === 0) {
        this.$modal.msgWarning('请选择要关联的' + (this.linkType === 'legal' ? '定性依据' : '处理依据'))
        return
      }
      const promises = this.selectedBasises.map(basis => ({
        basisType: this.linkType,
        basisId: basis.basisId,
        chapterId: this.selectedArticle.chapterId,
        articleId: this.selectedArticle.articleId
      }))
      Promise.all(promises.map(p => addBasisLink(p))).then(() => {
        this.$modal.msgSuccess('关联成功')
        this.addLinkOpen = false
        this.loadLinks(this.selectedArticle.articleId)
        // 更新树节点统计
        this.loadLinkCounts([this.selectedArticle])
      })
    },
    /** 取消关联 */
    cancelLink() {
      this.addLinkOpen = false
    },
    /** 删除单个关联 */
    handleDeleteLink(row, type) {
      this.$modal.confirm('是否确认删除该关联？').then(() => {
        return delBasisLink(row.linkId || row.basisId)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.loadLinks(this.selectedArticle.articleId)
        // 更新树节点统计
        this.loadLinkCounts([this.selectedArticle])
      }).catch(() => {})
    },
    /** 批量删除关联 */
    handleDeleteLinks(type) {
      const links = type === 'legal' ? this.selectedLegalLinks : this.selectedProcessingLinks
      const ids = links.map(l => l.linkId || l.basisId)
      this.$modal.confirm('是否确认删除选中的' + ids.length + '条关联？').then(() => {
        return delBasisLink(ids)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.loadLinks(this.selectedArticle.articleId)
        // 更新树节点统计
        this.loadLinkCounts([this.selectedArticle])
      }).catch(() => {})
    },
    /** 定性依据选择变化 */
    handleLegalSelectionChange(selection) {
      this.selectedLegalLinks = selection
    },
    /** 处理依据选择变化 */
    handleProcessingSelectionChange(selection) {
      this.selectedProcessingLinks = selection
    }
  }
}
</script>

<style scoped>
.tree-container {
  background: #fff;
  border-radius: 4px;
  padding: 16px;
}
.tree-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 12px;
  font-weight: bold;
}
.tree-node {
  display: flex;
  justify-content: space-between;
  align-items: center;
  width: 100%;
}
.node-count {
  display: flex;
  gap: 4px;
}
.link-container {
  background: #fff;
  border-radius: 4px;
  padding: 16px;
}
</style>
