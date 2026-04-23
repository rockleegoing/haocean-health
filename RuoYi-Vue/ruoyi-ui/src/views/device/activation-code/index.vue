<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="激活码" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入激活码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="激活码状态" clearable>
          <el-option label="未激活" value="0" />
          <el-option label="已激活" value="1" />
          <el-option label="已过期" value="2" />
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
          @click="handleGenerate"
          v-hasPermi="['device:activationCode:add']"
        >生成</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['device:activationCode:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="activationCodeList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="codeId" width="80" />
      <el-table-column label="激活码" align="center" prop="code" min-width="180">
        <template slot-scope="scope">
          <span>{{ scope.row.code }}</span>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-document-copy"
            @click="handleCopy(scope.row.code)"
          >复制</el-button>
        </template>
      </el-table-column>
      <el-table-column label="状态" align="center" prop="status" width="100">
        <template slot-scope="scope">
          <el-tag
            :type="getStatusType(scope.row.status)"
            size="small"
          >{{ getStatusLabel(scope.row.status) }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="有效期" align="center" prop="expireTime" width="180">
        <template slot-scope="scope">
          <div style="display: flex; align-items: center;">
            <countdown-timer v-if="scope.row.status === '0'" :expire-time="scope.row.expireTime" />
            <span v-else>{{ parseTime(scope.row.expireTime) }}</span>
            <expire-warning v-if="scope.row.status === '0'" :expire-time="scope.row.expireTime" />
          </div>
        </template>
      </el-table-column>
      <el-table-column label="激活时间" align="center" prop="activationTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.activationTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="激活设备" align="center" prop="deviceName" width="150" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width" width="180">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleView(scope.row)"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['device:activationCode:remove']"
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

    <!-- 生成激活码对话框 -->
    <create-dialog
      ref="createDialog"
      @success="handleGenerateSuccess"
    />

    <!-- 激活码详情对话框 -->
    <el-dialog title="激活码详情" :visible.sync="openView" width="600px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ viewForm.codeId }}</el-descriptions-item>
        <el-descriptions-item label="激活码">{{ viewForm.code || viewForm.codeValue }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="getStatusType(viewForm.status)" size="small">
            {{ getStatusLabel(viewForm.status) }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="有效期">{{ parseTime(viewForm.expireTime) }}</el-descriptions-item>
        <el-descriptions-item label="激活时间">{{ parseTime(viewForm.activationTime) }}</el-descriptions-item>
        <el-descriptions-item label="激活设备">{{ viewForm.deviceName || '-' }}</el-descriptions-item>
        <el-descriptions-item label="创建时间">{{ parseTime(viewForm.createTime) }}</el-descriptions-item>
        <el-descriptions-item label="备注">{{ viewForm.remark || '-' }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button @click="openView = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listActivationCode, delActivationCode, batchDelActivationCode } from "@/api/device/activationCode"
import CreateDialog from "./CreateDialog"
import CountdownTimer from "@/components/CountdownTimer"
import ExpireWarning from "@/components/ExpireWarning"

export default {
  name: "ActivationCode",
  components: {
    CreateDialog,
    CountdownTimer,
    ExpireWarning
  },
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      codeIds: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 激活码表格数据
      activationCodeList: [],
      // 是否显示详情对话框
      openView: false,
      // 详情数据
      viewForm: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        code: undefined,
        status: undefined
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询激活码列表 */
    getList() {
      this.loading = true
      listActivationCode(this.queryParams).then(response => {
        // 映射后端字段 codeValue -> code
        this.activationCodeList = (response.rows || []).map(item => ({
          ...item,
          code: item.codeValue
        }))
        this.total = response.total
        this.loading = false
      })
    },
    // 获取状态类型
    getStatusType(status) {
      const statusMap = {
        '0': '', // 未激活
        '1': 'success', // 已激活
        '2': 'info' // 已过期
      }
      return statusMap[status] || ''
    },
    // 获取状态标签
    getStatusLabel(status) {
      const labelMap = {
        '0': '未激活',
        '1': '已激活',
        '2': '已过期'
      }
      return labelMap[status] || '未知'
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
      this.codeIds = selection.map(item => item.codeId)
      this.multiple = !selection.length
    },
    /** 生成按钮操作 */
    handleGenerate() {
      this.$refs.createDialog.show()
    },
    /** 生成成功回调 */
    handleGenerateSuccess() {
      this.getList()
    },
    /** 详情按钮操作 */
    handleView(row) {
      this.viewForm = row
      this.openView = true
    },
    /** 复制激活码 */
    handleCopy(code) {
      if (navigator.clipboard) {
        navigator.clipboard.writeText(code).then(() => {
          this.$modal.msgSuccess('复制成功')
        }, () => {
          this.$modal.msgError('复制失败')
        })
      } else {
        // 降级方案：使用传统的 execCommand 方式
        const ta = document.createElement('textarea')
        ta.value = code
        document.body.appendChild(ta)
        ta.select()
        const result = document.execCommand('copy')
        document.body.removeChild(ta)
        if (result) {
          this.$modal.msgSuccess('复制成功')
        } else {
          this.$modal.msgError('复制失败')
        }
      }
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const codeIds = row.codeId ? row.codeId.toString() : this.codeIds.join(',')
      this.$modal.confirm('是否确认删除激活码？').then(function() {
        return delActivationCode(codeIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    }
  }
}
</script>
