<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="套组编码" prop="groupCode">
        <el-input v-model="form.groupCode" placeholder="请输入套组编码（新增时必填）" />
      </el-form-item>
      <el-form-item label="套组名称" prop="groupName">
        <el-input v-model="form.groupName" placeholder="请输入套组名称" />
      </el-form-item>
      <el-form-item label="套组类型" prop="groupType">
        <el-select v-model="form.groupType" placeholder="请选择套组类型" style="width: 100%">
          <el-option
            v-for="item in groupTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="模板配置" prop="templates">
        <el-input v-model="form.templates" type="textarea" placeholder="请输入模板配置（JSON格式）" rows="4" />
      </el-form-item>
      <el-form-item label="是否启用" prop="isActive">
        <el-radio-group v-model="form.isActive">
          <el-radio label="1">启用</el-radio>
          <el-radio label="0">禁用</el-radio>
        </el-radio-group>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="请输入备注" />
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitForm">确 定</el-button>
      <el-button @click="cancel">取 消</el-button>
    </div>
  </div>
</template>

<script>
export default {
  name: "DocumentGroupEdit",
  data() {
    return {
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        groupName: [
          { required: true, message: "套组名称不能为空", trigger: "blur" }
        ],
        groupCode: [
          { required: true, message: "套组编码不能为空", trigger: "blur" }
        ],
      },
      // 套组类型选项
      groupTypeOptions: [
        { label: '日常检查', value: 'DAILY_INSPECTION' },
        { label: '立案查处', value: 'CASE_INVESTIGATION' },
        { label: '证据保全', value: 'EVIDENCE_PRESERVATION' },
        { label: '行政处罚', value: 'ADMIN_PUNISHMENT' },
        { label: '行政强制', value: 'ADMIN_FORCE' },
        { label: '听证程序', value: 'HEARING_PROCEDURE' }
      ]
    }
  },
  created() {
    // 初始化表单数据
    this.reset()
  },
  methods: {
    // 表单重置
    reset() {
      this.form = {
        groupId: null,
        groupCode: null,
        groupName: null,
        groupType: null,
        templates: null,
        isActive: '1',
        remark: null
      }
      this.resetForm("form")
    },
    // 取消按钮
    cancel() {
      this.open = false
      this.reset()
    },
    /** 提交按钮 */
    submitForm() {
      this.$refs["form"].validate(valid => {
        if (valid) {
          if (this.form.groupId != null) {
            // TODO: 调用实际的API接口
            // updateDocumentGroup(this.form).then(response => {
            //   this.$modal.msgSuccess("修改成功")
            //   this.open = false
            //   this.getList()
            // })
            this.$modal.msgSuccess("修改成功")
            this.cancel()
          } else {
            // TODO: 调用实际的API接口
            // addDocumentGroup(this.form).then(response => {
            //   this.$modal.msgSuccess("新增成功")
            //   this.open = false
            //   this.getList()
            // })
            this.$modal.msgSuccess("新增成功")
            this.cancel()
          }
        }
      })
    }
  }
}
</script>
