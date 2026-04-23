<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="设备名称" prop="deviceName">
        <el-input
          v-model="queryParams.deviceName"
          placeholder="请输入设备名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="设备 ID" prop="deviceId">
        <el-input
          v-model="queryParams.deviceId"
          placeholder="请输入设备 ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="设备状态" clearable>
          <el-option label="在线" value="1" />
          <el-option label="离线" value="0" />
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
          type="danger"
          plain
          icon="el-icon-delete"
          size="mini"
          :disabled="multiple"
          @click="handleDelete"
          v-hasPermi="['device:device:remove']"
        >删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="deviceList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="displayId" width="80" />
      <el-table-column label="设备名称" align="center" prop="deviceName" min-width="120" />
      <el-table-column label="设备 ID" align="center" prop="displayDeviceId" min-width="180" />
      <el-table-column label="设备型号" align="center" prop="deviceModel" width="120" />
      <el-table-column label="系统版本" align="center" prop="deviceOs" width="100" />
      <el-table-column label="应用版本" align="center" prop="appVersion" width="100" />
      <el-table-column label="状态" align="center" prop="status" width="80">
        <template slot-scope="scope">
          <el-tag
            :type="scope.row.status === '1' ? 'success' : 'info'"
            size="small"
          >{{ scope.row.status === '1' ? '在线' : '离线' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="激活码" align="center" prop="activationCodeId" min-width="150">
        <template slot-scope="scope">
          <span v-if="scope.row.activationCodeId">{{ scope.row.activationCodeId }}</span>
          <span v-else style="color: #909399;">未绑定</span>
        </template>
      </el-table-column>
      <el-table-column label="最后在线" align="center" prop="lastLoginTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.lastOnlineTime) }}</span>
        </template>
      </el-table-column>
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
            icon="el-icon-refresh-left"
            @click="handleUnbind(scope.row)"
            v-hasPermi="['device:device:unbind']"
          >解绑</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['device:device:remove']"
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

    <!-- 设备详情对话框 -->
    <el-dialog title="设备详情" :visible.sync="openView" width="600px" append-to-body>
      <el-descriptions :column="1" border>
        <el-descriptions-item label="ID">{{ viewForm.deviceId }}</el-descriptions-item>
        <el-descriptions-item label="设备名称">{{ viewForm.deviceName }}</el-descriptions-item>
        <el-descriptions-item label="设备 ID">{{ viewForm.deviceUuid || '-' }}</el-descriptions-item>
        <el-descriptions-item label="设备型号">{{ viewForm.deviceModel || '-' }}</el-descriptions-item>
        <el-descriptions-item label="系统版本">{{ viewForm.deviceOs || '-' }}</el-descriptions-item>
        <el-descriptions-item label="应用版本">{{ viewForm.appVersion || '-' }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag :type="viewForm.status === '1' ? 'success' : 'info'" size="small">
            {{ viewForm.status === '1' ? '在线' : '离线' }}
          </el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="激活码">{{ viewForm.activationCodeId || '-' }}</el-descriptions-item>
        <el-descriptions-item label="最后在线">{{ parseTime(viewForm.lastLoginTime) }}</el-descriptions-item>
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
import { listDevice, delDevice, batchDelDevice, unbindDevice } from "@/api/device/device"

export default {
  name: "Device",
  data() {
    return {
      // 遮罩层
      loading: true,
      // 选中数组
      ids: [],
      // 非多个禁用
      multiple: true,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 设备表格数据
      deviceList: [],
      // 是否显示详情对话框
      openView: false,
      // 详情数据
      viewForm: {},
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        deviceName: undefined,
        deviceId: undefined,
        status: undefined
      },
      // 定时刷新 ID
      refreshTimer: null
    }
  },
  created() {
    this.getList()
    // 30 秒定时刷新
    this.refreshTimer = setInterval(() => {
      this.getList()
    }, 30000)
  },
  beforeDestroy() {
    if (this.refreshTimer) {
      clearInterval(this.refreshTimer)
    }
  },
  methods: {
    /** 查询设备列表 */
    getList() {
      this.loading = true
      listDevice(this.queryParams).then(response => {
        // 映射后端字段到前端
        this.deviceList = (response.rows || []).map(item => ({
          ...item,
          // 保留原始 deviceId 用于删除等操作
          _deviceId: item.deviceId,
          // 前端显示用
          displayId: item.deviceId,
          displayDeviceId: item.deviceUuid,
          activationCode: item.activationCodeId ? String(item.activationCodeId) : null,
          lastOnlineTime: item.lastLoginTime
        }))
        this.total = response.total
        this.loading = false
      })
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
      this.ids = selection.map(item => item._deviceId)  // 使用后端 deviceId
      this.multiple = !selection.length
    },
    /** 详情按钮操作 */
    handleView(row) {
      this.viewForm = row
      this.openView = true
    },
    /** 解绑按钮操作 */
    handleUnbind(row) {
      this.$modal.confirm('是否确认解绑设备 "' + row.deviceName + '"？解绑后该设备需要重新激活').then(function() {
        return unbindDevice(row._deviceId)  // 使用后端 deviceId
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("解绑成功")
      }).catch(() => {})
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const ids = row._deviceId ? row._deviceId.toString() : this.ids.join(',')  // 使用 deviceId
      this.$modal.confirm('是否确认删除设备 "' + (row.deviceName || ids) + '"？').then(function() {
        return delDevice(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    }
  }
}
</script>
