<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="68px">
      <el-form-item label="单位名称" prop="unitName">
        <el-input
          v-model="queryParams.unitName"
          placeholder="请输入单位名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="行业分类ID" prop="industryCategoryId">
        <el-input
          v-model="queryParams.industryCategoryId"
          placeholder="请输入行业分类ID"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="区域" prop="region">
        <el-input
          v-model="queryParams.region"
          placeholder="请输入区域"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="统一社会信用代码" prop="creditCode">
        <el-input
          v-model="queryParams.creditCode"
          placeholder="请输入统一社会信用代码"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="法定代表人" prop="legalPerson">
        <el-input
          v-model="queryParams.legalPerson"
          placeholder="请输入法定代表人"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="联系电话" prop="contactPhone">
        <el-input
          v-model="queryParams.contactPhone"
          placeholder="请输入联系电话"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="经营地址" prop="businessAddress">
        <el-input
          v-model="queryParams.businessAddress"
          placeholder="请输入经营地址"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="纬度" prop="latitude">
        <el-input
          v-model="queryParams.latitude"
          placeholder="请输入纬度"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="经度" prop="longitude">
        <el-input
          v-model="queryParams.longitude"
          placeholder="请输入经度"
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
          v-hasPermi="['system:unit:add']"
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
          v-hasPermi="['system:unit:edit']"
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
          v-hasPermi="['system:unit:remove']"
        >删除</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button
          type="warning"
          plain
          icon="el-icon-download"
          size="mini"
          @click="handleExport"
          v-hasPermi="['system:unit:export']"
        >导出</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="unitList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="单位ID" align="center" prop="unitId" />
      <el-table-column label="单位名称" align="center" prop="unitName" />
      <el-table-column label="行业分类ID" align="center" prop="industryCategoryId" />
      <el-table-column label="区域" align="center" prop="region" />
      <el-table-column label="监管类型" align="center" prop="supervisionType" />
      <el-table-column label="统一社会信用代码" align="center" prop="creditCode" />
      <el-table-column label="法定代表人" align="center" prop="legalPerson" />
      <el-table-column label="联系电话" align="center" prop="contactPhone" />
      <el-table-column label="经营地址" align="center" prop="businessAddress" />
      <el-table-column label="纬度" align="center" prop="latitude" />
      <el-table-column label="经度" align="center" prop="longitude" />
      <el-table-column label="当事人" align="center" prop="personName" width="80" />
      <el-table-column label="注册地址" align="center" prop="registrationAddress" width="150" />
      <el-table-column label="经营面积" align="center" prop="businessArea" width="80" />
      <el-table-column label="许可证名称" align="center" prop="licenseName" width="120" />
      <el-table-column label="许可证号" align="center" prop="licenseNo" width="120" />
      <el-table-column label="性别" align="center" prop="gender" width="50" />
      <el-table-column label="民族" align="center" prop="nation" width="60" />
      <el-table-column label="职务" align="center" prop="post" width="80" />
      <el-table-column label="身份证" align="center" prop="idCard" width="150" />
      <el-table-column label="出生日期" align="center" prop="birthday" width="100" />
      <el-table-column label="家庭住址" align="center" prop="homeAddress" width="200" />
      <el-table-column label="状态:0正常,1停用" align="center" prop="status" />
      <el-table-column label="备注" align="center" prop="remark" />
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button
            size="mini"
            type="text"
            icon="el-icon-edit"
            @click="handleUpdate(scope.row)"
            v-hasPermi="['system:unit:edit']"
          >修改</el-button>
          <el-button
            size="mini"
            type="text"
            icon="el-icon-delete"
            @click="handleDelete(scope.row)"
            v-hasPermi="['system:unit:remove']"
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

    <!-- 添加或修改执法单位对话框 -->
    <el-dialog :title="title" :visible.sync="open" width="500px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="100px">
        <el-row>
          <el-col :span="24">
            <el-form-item label="单位名称" prop="unitName">
              <el-input v-model="form.unitName" placeholder="请输入单位名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="行业分类ID" prop="industryCategoryId">
              <el-input v-model="form.industryCategoryId" placeholder="请输入行业分类ID" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="区域" prop="region">
              <el-input v-model="form.region" placeholder="请输入区域" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="统一社会信用代码" prop="creditCode">
              <el-input v-model="form.creditCode" placeholder="请输入统一社会信用代码" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="法定代表人" prop="legalPerson">
              <el-input v-model="form.legalPerson" placeholder="请输入法定代表人" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="联系电话" prop="contactPhone">
              <el-input v-model="form.contactPhone" placeholder="请输入联系电话" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="经营地址" prop="businessAddress">
              <el-input v-model="form.businessAddress" placeholder="请输入经营地址" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="纬度" prop="latitude">
              <el-input v-model="form.latitude" placeholder="请输入纬度" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="经度" prop="longitude">
              <el-input v-model="form.longitude" placeholder="请输入经度" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="当事人" prop="personName">
              <el-input v-model="form.personName" placeholder="请输入当事人" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="注册地址" prop="registrationAddress">
              <el-input v-model="form.registrationAddress" placeholder="请输入注册地址" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="经营面积" prop="businessArea">
              <el-input v-model="form.businessArea" placeholder="请输入经营面积" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="许可证名称" prop="licenseName">
              <el-input v-model="form.licenseName" placeholder="请输入许可证名称" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="许可证号" prop="licenseNo">
              <el-input v-model="form.licenseNo" placeholder="请输入许可证号" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="性别" prop="gender">
              <el-input v-model="form.gender" placeholder="请输入性别" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="民族" prop="nation">
              <el-input v-model="form.nation" placeholder="请输入民族" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="职务" prop="post">
              <el-input v-model="form.post" placeholder="请输入职务" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="身份证" prop="idCard">
              <el-input v-model="form.idCard" placeholder="请输入身份证" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="出生日期" prop="birthday">
              <el-input v-model="form.birthday" placeholder="请输入出生日期" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="家庭住址" prop="homeAddress">
              <el-input v-model="form.homeAddress" placeholder="请输入家庭住址" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="删除标志:0存在,1删除" prop="delFlag">
              <el-input v-model="form.delFlag" placeholder="请输入删除标志:0存在,1删除" />
            </el-form-item>
          </el-col>
          <el-col :span="24">
            <el-form-item label="备注" prop="remark">
              <el-input v-model="form.remark" type="textarea" placeholder="请输入内容" />
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
import { listUnit, getUnit, delUnit, addUnit, updateUnit } from "@/api/system/unit"

export default {
  name: "Unit",
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
      // 执法单位表格数据
      unitList: [],
      // 弹出层标题
      title: "",
      // 是否显示弹出层
      open: false,
      // 查询参数
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        unitName: null,
        industryCategoryId: null,
        region: null,
        supervisionType: null,
        creditCode: null,
        legalPerson: null,
        contactPhone: null,
        businessAddress: null,
        latitude: null,
        longitude: null,
        status: null,
      },
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        unitName: [
          { required: true, message: "单位名称不能为空", trigger: "blur" }
        ],
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    /** 查询执法单位列表 */
    getList() {
      this.loading = true
      listUnit(this.queryParams).then(response => {
        this.unitList = response.rows
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
        unitId: null,
        unitName: null,
        industryCategoryId: null,
        region: null,
        supervisionType: null,
        creditCode: null,
        legalPerson: null,
        contactPhone: null,
        businessAddress: null,
        latitude: null,
        longitude: null,
        personName: null,
        registrationAddress: null,
        businessArea: null,
        licenseName: null,
        licenseNo: null,
        gender: null,
        nation: null,
        post: null,
        idCard: null,
        birthday: null,
        homeAddress: null,
        status: null,
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
      this.ids = selection.map(item => item.unitId)
      this.single = selection.length !== 1
      this.multiple = !selection.length
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.reset()
      this.open = true
      this.title = "添加执法单位"
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      this.reset()
      const unitId = row.unitId || this.ids
      getUnit(unitId).then(response => {
        this.form = response.data
        this.open = true
        this.title = "修改执法单位"
      })
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.unitId != null) {
            updateUnit(this.form).then(response => {
              this.$modal.msgSuccess("修改成功")
              this.open = false
              this.getList()
            })
          } else {
            addUnit(this.form).then(response => {
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
      const unitIds = row.unitId || this.ids
      this.$modal.confirm('是否确认删除执法单位编号为"' + unitIds + '"的数据项？').then(function() {
        return delUnit(unitIds)
      }).then(() => {
        this.getList()
        this.$modal.msgSuccess("删除成功")
      }).catch(() => {})
    },
    /** 导出按钮操作 */
    handleExport() {
      this.download('system/unit/export', {
        ...this.queryParams
      }, `unit_${new Date().getTime()}.xlsx`)
    }
  }
}
</script>
