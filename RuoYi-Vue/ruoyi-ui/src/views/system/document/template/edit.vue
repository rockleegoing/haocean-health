<template>
  <div class="app-container">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="模板编码" prop="templateCode">
        <el-input v-model="form.templateCode" placeholder="请输入模板编码（新增时必填）" />
      </el-form-item>
      <el-form-item label="模板名称" prop="templateName">
        <el-input v-model="form.templateName" placeholder="请输入模板名称" />
      </el-form-item>
      <el-form-item label="模板类型" prop="templateType">
        <el-select v-model="form.templateType" placeholder="请选择模板类型" style="width: 100%">
          <el-option
            v-for="item in templateTypeOptions"
            :key="item.value"
            :label="item.label"
            :value="item.value"
          />
        </el-select>
      </el-form-item>
      <el-form-item label="分类" prop="category">
        <el-input v-model="form.category" placeholder="请输入分类" />
      </el-form-item>
      <el-form-item label="文件路径" prop="filePath">
        <el-input v-model="form.filePath" placeholder="请输入文件路径" />
      </el-form-item>
      <el-form-item label="文件URL" prop="fileUrl">
        <el-input v-model="form.fileUrl" placeholder="请输入文件URL" />
      </el-form-item>
      <el-form-item label="版本" prop="version">
        <el-input v-model="form.version" placeholder="请输入版本号" />
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
  name: "DocumentTemplateEdit",
  data() {
    return {
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        templateName: [
          { required: true, message: "模板名称不能为空", trigger: "blur" }
        ],
        templateCode: [
          { required: true, message: "模板编码不能为空", trigger: "blur" }
        ],
      },
      // 模板类型选项
      templateTypeOptions: [
        { label: '现场笔录类', value: 'SCENE_RECORD' },
        { label: '询问笔录类', value: 'INQUIRY_RECORD' },
        { label: '行政强制类', value: 'ADMIN_FORCE' },
        { label: '行政处罚类', value: 'ADMIN_PUNISH' },
        { label: '其他', value: 'OTHER' }
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
        templateId: null,
        templateCode: null,
        templateName: null,
        templateType: null,
        category: null,
        filePath: null,
        fileUrl: null,
        version: '1.0',
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
          if (this.form.templateId != null) {
            // TODO: 调用实际的API接口
            // updateTemplate(this.form).then(response => {
            //   this.$modal.msgSuccess("修改成功")
            //   this.open = false
            //   this.getList()
            // })
            this.$modal.msgSuccess("修改成功")
            this.cancel()
          } else {
            // TODO: 调用实际的API接口
            // addTemplate(this.form).then(response => {
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
