<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="法律名称" prop="lawId">
        <el-select v-model="queryParams.lawId" placeholder="请选择法律" clearable filterable @change="handleQuery">
          <el-option
            v-for="item in lawOptions"
            :key="item.id"
            :label="item.name"
            :value="item.id"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="编" prop="part">
        <el-input
          v-model="queryParams.part"
          placeholder="请输入编"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="分编" prop="partBranch">
        <el-input
          v-model="queryParams.partBranch"
          placeholder="请输入分编"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="章" prop="chapter">
        <el-input
          v-model="queryParams.chapter"
          placeholder="请输入章"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="节" prop="quarter">
        <el-input
          v-model="queryParams.quarter"
          placeholder="请输入节"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="条" prop="article">
        <el-input
          v-model="queryParams.article"
          placeholder="请输入条"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="款" prop="section">
        <el-input
          v-model="queryParams.section"
          placeholder="请输入款"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="项" prop="subparagraph">
        <el-input
          v-model="queryParams.subparagraph"
          placeholder="请输入项"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="目" prop="item">
        <el-input
          v-model="queryParams.item"
          placeholder="请输入目"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="远程库id" prop="stashTermId">
        <el-input
          v-model="queryParams.stashTermId"
          placeholder="请输入远程库id"
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
          v-hasPermi="['system:legalterm:add']"
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
          v-hasPermi="['system:legalterm:edit']"
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
          v-hasPermi="['system:legalterm:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:legalterm:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="legaltermList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="id" align="center" prop="id" />
      <el-table-column label="法律名称" align="center" prop="lawName" />
      <el-table-column label="编" align="center" prop="part" />
      <el-table-column label="分编" align="center" prop="partBranch" />
      <el-table-column label="章" align="center" prop="chapter" />
      <el-table-column label="节" align="center" prop="quarter" />
      <el-table-column label="条" align="center" prop="article" />
      <el-table-column label="款" align="center" prop="section" />
      <el-table-column label="项" align="center" prop="subparagraph" />
      <el-table-column label="目" align="center" prop="item" />
      <el-table-column label="中文条款编码" align="center" prop="zhCode" />
      <el-table-column label="条款内容" align="center" prop="content" />
      <el-table-column label="远程库id" align="center" prop="stashTermId" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-view"
            @click="handleViewData(scope.row)"
            v-hasPermi="['system:legalterm:query']"
          >详情</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:legalterm:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:legalterm:remove']"
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

    <!-- 法律条款详情抽屉 -->
    <legalterm-view-drawer ref="legaltermViewRef" />
    <!-- 添加或修改法律条款对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="法律名称" prop="lawId">
              <el-select v-model="form.lawId" placeholder="请选择法律" filterable style="width: 100%">
                <el-option
                  v-for="item in lawOptions"
                  :key="item.id"
                  :label="item.name"
                  :value="item.id"
                />
              </el-select>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="编" prop="part">
              <el-input v-model="form.part" placeholder="请输入编" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="分编" prop="partBranch">
              <el-input v-model="form.partBranch" placeholder="请输入分编" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="章" prop="chapter">
              <el-input v-model="form.chapter" placeholder="请输入章" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="节" prop="quarter">
              <el-input v-model="form.quarter" placeholder="请输入节" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="条" prop="article">
              <el-input v-model="form.article" placeholder="请输入条" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="款" prop="section">
              <el-input v-model="form.section" placeholder="请输入款" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="项" prop="subparagraph">
              <el-input v-model="form.subparagraph" placeholder="请输入项" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="目" prop="item">
              <el-input v-model="form.item" placeholder="请输入目" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="中文条款编码" prop="zhCode">
              <el-input v-model="form.zhCode" type="textarea" placeholder="请输入内容" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="条款内容">
              <editor v-model="form.content" :min-height="192"/>
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="远程库id" prop="stashTermId">
              <el-input v-model="form.stashTermId" placeholder="请输入远程库id" />
            </el-form-item>
          </el-col>
        </el-row>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listLegalterm, getLegalterm, delLegalterm, addLegalterm, updateLegalterm } from "@/api/system/legalterm"
import { listLaw } from "@/api/system/law"
import LegaltermViewDrawer from "./view"

export default {
  name: "Legalterm",
  components: { LegaltermViewDrawer },
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
      // 法律条款表格数据
      legaltermList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        lawId: null,
        part: null,
        partBranch: null,
        chapter: null,
        quarter: null,
        article: null,
        section: null,
        subparagraph: null,
        item: null,
        zhCode: null,
        content: null,
        stashTermId: null
      },
      // 表单参数
      form: {},
      // 法律下拉选项
      lawOptions: [],
      // 表单校验
      rules: {
        lawId: [
          { required: true, message: "法律编号不能为空", trigger: "blur" }
        ],
        article: [
          { required: true, message: "条不能为空", trigger: "blur" }
        ],
        section: [
          { required: true, message: "款不能为空", trigger: "blur" }
        ],
        subparagraph: [
          { required: true, message: "项不能为空", trigger: "blur" }
        ],
        item: [
          { required: true, message: "目不能为空", trigger: "blur" }
        ],
        zhCode: [
          { required: true, message: "中文条款编码不能为空", trigger: "blur" }
        ],
        content: [
          { required: true, message: "条款内容不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
    this.getLawOptions()
  },
  methods: {
    /** 获取法律下拉选项 */
    getLawOptions() {
      listLaw({ pageNum: 1, pageSize: 1000 }).then(response => {
        this.lawOptions = response.rows || []
      })
    },
    /** 查询法律条款列表 */
    getList() {
      this.loading = true
      listLegalterm(this.queryParams).then(response => {
        this.legaltermList = response.rows
        this.total = response.total
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
        id: null,
        lawId: null,
        part: null,
        partBranch: null,
        chapter: null,
        quarter: null,
        article: null,
        section: null,
        subparagraph: null,
        item: null,
        zhCode: null,
        content: null,
        updateTime: null,
        stashTermId: null
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
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加法律条款"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const id = row.id || this.ids
      getLegalterm(id).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改法律条款"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.id != null) {
            updateLegalterm(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addLegalterm(this.form).then(response => {
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
      const ids = row.id || this.ids
      this.$modal.confirm('是否确认删除法律条款编号为"' + ids + '"的数据项？').then(function() {
        return delLegalterm(ids)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 详情按钮操作 */
    handleViewData(row) {
      this.$refs["legaltermViewRef"].open(row.id)
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/legalterm/export', {
        ...this.queryParams
      }, `legalterm_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
