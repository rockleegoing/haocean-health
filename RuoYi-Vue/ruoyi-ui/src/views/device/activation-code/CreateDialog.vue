<template>
  <el-dialog title="生成激活码" :visible.sync="visible" width="600px" append-to-body @close="handleClose">
    <el-form ref="form" :model="form" :rules="rules" label-width="100px">
      <el-form-item label="生成数量" prop="count">
        <el-input-number v-model="form.count" :min="1" :max="50" controls-position="right" />
        <span class="form-tip">单次最多生成 50 个</span>
      </el-form-item>
      <el-form-item label="有效期 (天)" prop="validDays">
        <el-input-number v-model="form.validDays" :min="1" :max="365" controls-position="right" />
        <span class="form-tip">1-365 天</span>
      </el-form-item>
      <el-form-item label="备注" prop="remark">
        <el-input v-model="form.remark" type="textarea" placeholder="请输入备注（可选）" />
      </el-form-item>
    </el-form>

    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="handleSubmit" :loading="submitLoading">确 定</el-button>
      <el-button @click="visible = false">取 消</el-button>
    </div>

    <!-- 生成的激活码列表 -->
    <el-dialog title="生成的激活码" :visible.sync="resultVisible" width="600px" append-to-body>
      <div class="result-actions">
        <el-button type="primary" size="small" icon="el-icon-document-copy" @click="copyAll">全部复制</el-button>
        <el-button @click="resultVisible = false">关 闭</el-button>
      </div>
      <el-input
        type="textarea"
        :rows="15"
        readonly
        v-model="resultCode"
        style="font-family: monospace;"
      />
    </el-dialog>
  </el-dialog>
</template>

<script>
import { generateActivationCode } from "@/api/device/activationCode"

export default {
  name: 'CreateDialog',
  data() {
    return {
      visible: false,
      resultVisible: false,
      submitLoading: false,
      form: {
        count: 1,
        validDays: 7,
        remark: ''
      },
      rules: {
        count: [
          { required: true, message: '请输入生成数量', trigger: 'blur' }
        ],
        validDays: [
          { required: true, message: '请输入有效期', trigger: 'blur' }
        ]
      },
      resultCode: ''
    }
  },
  methods: {
    show() {
      this.visible = true
      this.resetForm()
    },
    resetForm() {
      this.form = {
        count: 1,
        validDays: 7,
        remark: ''
      }
      if (this.$refs.form) {
        this.$refs.form.clearValidate()
      }
    },
    handleClose() {
      this.resetForm()
      this.resultVisible = false
      this.resultCode = ''
    },
    handleSubmit() {
      this.$refs.form.validate(valid => {
        if (valid) {
          this.submitLoading = true
          generateActivationCode(this.form).then(response => {
            this.submitLoading = false
            this.visible = false
            // 显示生成的激活码
            const codes = response.data || []
            this.resultCode = codes.map(item => item.code).join('\n')
            this.resultVisible = true
            this.$modal.msgSuccess(`成功生成 ${codes.length} 个激活码`)
            this.$emit('success')
          }).catch(() => {
            this.submitLoading = false
          })
        }
      })
    },
    copyAll() {
      this.$copyText(this.resultCode).then(() => {
        this.$modal.msgSuccess('复制成功')
      }, () => {
        this.$modal.msgError('复制失败')
      })
    }
  }
}
</script>

<style scoped>
.form-tip {
  margin-left: 10px;
  font-size: 12px;
  color: #909399;
}
.result-actions {
  margin-bottom: 10px;
}
</style>
