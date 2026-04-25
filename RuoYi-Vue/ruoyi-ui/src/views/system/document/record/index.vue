<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="文书编号" prop="documentNo">
        <el-input
          v-model="queryParams.documentNo"
          placeholder="请输入文书编号"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="模板名称" prop="templateName">
        <el-input
          v-model="queryParams.templateName"
          placeholder="请输入模板名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="草稿" value="0" />
          <el-option label="已生成" value="1" />
          <el-option label="已打印" value="2" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="recordList">
      <el-table-column label="文书编号" align="center" prop="documentNo" width="180" />
      <el-table-column label="模板名称" align="center" prop="templateName" />
      <el-table-column label="关联记录ID" align="center" prop="recordId" width="100" />
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="info">草稿</el-tag>
          <el-tag v-else-if="scope.row.status === '1'" type="success">已生成</el-tag>
          <el-tag v-else-if="scope.row.status === '2'" type="primary">已打印</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="同步状态" align="center" prop="syncStatus" width="100">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.syncStatus === '0'" type="warning">未同步</el-tag>
          <el-tag v-else type="success">已同步</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="创建时间" align="center" prop="createTime" width="160" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleDetail(scope.row)"
            v-hasPermi="['system:document:record:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-download"
            @click="handleDownload(scope.row)"
            v-hasPermi="['system:document:record:export']"
          >下载</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:document:record:remove']"
          >删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <!-- 文书记录详情对话框 -->
    <el-dialog title="文书记录详情" :visible.sync="detailVisible" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="文书编号">{{ detailData.documentNo }}</el-descriptions-item>
        <el-descriptions-item label="模板名称">{{ detailData.templateName }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailData.status === '0'" type="info">草稿</el-tag>
          <el-tag v-else-if="detailData.status === '1'" type="success">已生成</el-tag>
          <el-tag v-else type="primary">已打印</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="同步状态">
          <el-tag v-if="detailData.syncStatus === '0'" type="warning">未同步</el-tag>
          <el-tag v-else type="success">已同步</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ detailData.createTime }}</el-descriptions-item>
      </el-descriptions>
      <el-divider>变量内容</el-divider>
      <pre>{{ formatJson(detailData.variables) }}</pre>
      <div slot="footer">
        <el-button @click="detailVisible = false">关 闭</el-button>
      </div>
    </el-dialog>

    <pagination
      v-show="total>0"
      :total="total"
      :page.sync="queryParams.pageNum"
      :limit.sync="queryParams.pageSize"
      @pagination="getList"
    />
  </div>
</template>

<script>
import { listRecord, getRecord, delRecord } from "@/api/system/document"

export default {
  name: "DocumentRecord",
  data() {
    return {
      loading: true,
      showSearch: true,
      total: 0,
      recordList: [],
      detailVisible: false,
      detailData: {},
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        documentNo: null,
        templateName: null,
        status: null
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listRecord(this.queryParams).then(response => {
        this.recordList = response.rows || []
        this.total = response.total || 0
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.resetForm("queryForm")
      this.handleQuery()
    },
    handleDetail(row) {
      getRecord(row.id).then(response => {
        this.detailData = response.data || {}
        this.detailVisible = true
      })
    },
    handleDownload(row) {
      window.open('/prod-api/admin/document/download/' + row.id, '_blank')
    },
    handleDelete(row) {
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除该文书记录？').then(() => {
        return delRecord(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    formatJson(str) {
      if (!str) return ''
      try {
        return JSON.stringify(JSON.parse(str), null, 2)
      } catch (e) {
        return str
      }
    }
  }
}
</script>
