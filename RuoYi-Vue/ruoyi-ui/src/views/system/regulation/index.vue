<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="88px">
      <el-form-item label="法律名称" prop="title">
        <el-input
          v-model="queryParams.title"
          placeholder="请输入法律名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="法律类型" prop="legalType">
        <el-select v-model="queryParams.legalType" placeholder="请选择法律类型" clearable>
          <el-option label="法律" value="法律" />
          <el-option label="法规" value="法规" />
          <el-option label="规章" value="规章" />
          <el-option label="规范性文件" value="规范性文件" />
          <el-option label="批复文件" value="批复文件" />
          <el-option label="标准" value="标准" />
        </el-select>
      </el-form-item>
      <el-form-item label="颁发机构" prop="issuingAuthority">
        <el-input
          v-model="queryParams.issuingAuthority"
          placeholder="请输入颁发机构"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="状态" prop="status">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable>
          <el-option label="正常" value="0" />
          <el-option label="停用" value="1" />
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
          v-hasPermi="['system:regulation:add']"
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
          v-hasPermi="['system:regulation:edit']"
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
          v-hasPermi="['system:regulation:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:regulation:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="regulationList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="法律法规ID" align="center" prop="regulationId" />
      <el-table-column label="法律名称" align="center" prop="title" show-overflow-tooltip />
      <el-table-column label="法律类型" align="center" prop="legalType" />
      <el-table-column label="监管类型" align="center" prop="supervisionTypes" show-overflow-tooltip />
      <el-table-column label="发布日期" align="center" prop="publishDate" width="120" />
      <el-table-column label="实施日期" align="center" prop="effectiveDate" width="120" />
      <el-table-column label="颁发机构" align="center" prop="issuingAuthority" show-overflow-tooltip />
      <el-table-column label="状态" align="center" prop="status">
        <template slot-scope="scope">
          <el-tag v-if="scope.row.status === '0'" type="success">正常</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="备注" align="center" prop="remark" show-overflow-tooltip />
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
            v-hasPermi="['system:regulation:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:regulation:remove']"
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

    <!-- 添加或修改法律法规对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="700px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="法律名称" prop="title">
              <el-input v-model="form.title" placeholder="请输入法律名称" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="法律类型" prop="legalType">
              <el-select v-model="form.legalType" placeholder="请选择法律类型" style="width: 100%">
                <el-option label="法律" value="法律" />
                <el-option label="法规" value="法规" />
                <el-option label="规章" value="规章" />
                <el-option label="规范性文件" value="规范性文件" />
                <el-option label="批复文件" value="批复文件" />
                <el-option label="标准" value="标准" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="颁发机构" prop="issuingAuthority">
              <el-input v-model="form.issuingAuthority" placeholder="请输入颁发机构" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="发布日期" prop="publishDate">
              <el-date-picker
                v-model="form.publishDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="实施日期" prop="effectiveDate">
              <el-date-picker
                v-model="form.effectiveDate"
                type="date"
                placeholder="选择日期"
                style="width: 100%"
              />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="监管类型" prop="supervisionTypes">
              <el-select v-model="form.supervisionTypes" placeholder="请选择监管类型" multiple style="width: 100%">
                <el-option label="食品生产" value="食品生产" />
                <el-option label="食品销售" value="食品销售" />
                <el-option label="餐饮服务" value="餐饮服务" />
                <el-option label="药品经营" value="药品经营" />
                <el-option label="医疗器械" value="医疗器械" />
                <el-option label="化妆品" value="化妆品" />
                <el-option label="特种设备" value="特种设备" />
                <el-option label="工业产品" value="工业产品" />
                <el-option label="计量标准" value="计量标准" />
                <el-option label="认证认可" value="认证认可" />
                <el-option label="检验检测" value="检验检测" />
                <el-option label="广告监管" value="广告监管" />
                <el-option label="知识产权" value="知识产权" />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="完整内容" prop="content">
              <el-input v-model="form.content" type="textarea" :rows="4" placeholder="请输入完整内容" />
            </el-form-item>
          </el-col>
          <el-col :span="12">
            <el-form-item label="状态" prop="status">
              <el-radio-group v-model="form.status">
                <el-radio label="0">正常</el-radio>
                <el-radio label="1">停用</el-radio>
              </el-radio-group>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 法律法规详情对话框 -->
    <el-dialog title="法律法规详情" :visible.sync="detailOpen" width="700px" append-to-body>
      <el-descriptions :column="2" border>
        <el-descriptions-item label="法律名称">{{ detailData.title }}</el-descriptions-item>
        <el-descriptions-item label="法律类型">{{ detailData.legalType }}</el-descriptions-item>
        <el-descriptions-item label="颁发机构">{{ detailData.issuingAuthority }}</el-descriptions-item>
        <el-descriptions-item label="发布日期">{{ detailData.publishDate }}</el-descriptions-item>
        <el-descriptions-item label="实施日期">{{ detailData.effectiveDate }}</el-descriptions-item>
        <el-descriptions-item label="监管类型">{{ detailData.supervisionTypes }}</el-descriptions-item>
        <el-descriptions-item label="状态">
          <el-tag v-if="detailData.status === '0'" type="success">正常</el-tag>
          <el-tag v-else type="danger">停用</el-tag>
        </el-descriptions-item>
        <el-descriptions-item label="版本号">{{ detailData.version }}</el-descriptions-item>
        <el-descriptions-item label="完整内容" :span="2">{{ detailData.content }}</el-descriptions-item>
        <el-descriptions-item label="备注" :span="2">{{ detailData.remark }}</el-descriptions-item>
      </el-descriptions>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="handleManageChapters">管理章节</el-button>
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listRegulation, getRegulation, delRegulation, addRegulation, updateRegulation } from "@/api/system/regulation"

export default {
  name: "Regulation",
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
      // 法律法规表格数据
      regulationList: [],
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
        title: null,
        legalType: null,
        issuingAuthority: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        title: [
          { required: true, message: "法律名称不能为空", trigger: "blur" }
        ],
        legalType: [
          { required: true, message: "法律类型不能为空", trigger: "change" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询法律法规列表 */
    getList() {
      this.loading = true
      listRegulation(this.queryParams).then(response => {
        this.regulationList = response.rows || []
        this.total = response.total || 0
        this.loading = false
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
        regulationId: null,
        title: null,
        legalType: null,
        supervisionTypes: null,
        publishDate: null,
        effectiveDate: null,
        issuingAuthority: null,
        content: null,
        version: "1.0",
        status: "0",
        delFlag: null,
        createBy: null,
        createTime: null,
        updateBy: null,
        updateTime: null,
        remark: null
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
      this.ids = selection.map(item => item.regulationId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加法律法规"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const regulationId = row.regulationId || this.ids
      getRegulation(regulationId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改法律法规"
      })
    },
    /** 详情按钮操作 */
    handleDetail(row) {
      getRegulation(row.regulationId).then(response => {
        this.detailData = response.data || {}
        this.detailOpen = true
      })
    },
    /** 管理章节按钮操作 */
    handleManageChapters() {
      this.$router.push({
        path: '/system/regulation/chapter',
        query: { regulationId: this.detailData.regulationId }
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.regulationId != null) {
            updateRegulation(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addRegulation(this.form).then(response => {
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
      const regulationIds = row.regulationId || this.ids
      this.$modal.confirm('是否确认删除法律法规编号为"' + regulationIds + '"的数据项？').then(function() {
        return delRegulation(regulationIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/regulation/export', {
        ...this.queryParams
      }, `regulation_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
