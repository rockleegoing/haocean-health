<template>
  <el-drawer title="规范用语详情" :visible.sync="visible" direction="rtl" size="60%" append-to-body :before-close="handleClose" custom-class="detail-drawer">
    <div v-loading="loading" class="drawer-content">
      <h4 class="section-header">基本信息</h4>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">专业类别：</label>
            <span class="info-value plaintext">
              {{ info.professionalCategory }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">一级分类名称：</label>
            <span class="info-value plaintext">
              {{ info.primaryCategory }}
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">二级分类名称：</label>
            <span class="info-value plaintext">
              {{ info.secondaryCategory }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">三级分类名称：</label>
            <span class="info-value plaintext">
              {{ info.tertiaryCategory }}
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">规范用语代码：</label>
            <span class="info-value plaintext">
              {{ info.standardCode }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">违法事实编码：</label>
            <span class="info-value plaintext">
              {{ info.violationCode }}
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">信息报告违法事实：</label>
            <span class="info-value plaintext">
              {{ info.informationReport }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">检查内容：</label>
            <span class="info-value plaintext">
              {{ info.inspectionContent }}
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">定性依据：</label>
            <span class="info-value plaintext">
              {{ info.qualitativeBasis }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">处理依据：</label>
            <span class="info-value plaintext">
              {{ info.handlingBasis }}
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">规范用语：</label>
            <span class="info-value plaintext">
              {{ info.standardPhrase }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">监督意见：</label>
            <span class="info-value plaintext">
              {{ info.supervisoryOpinion }}
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">处理内容：</label>
            <span class="info-value plaintext">
              {{ info.handlingContent }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">处理程序：</label>
            <span class="info-value plaintext">
              {{ info.handlingProcedure }}
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">行政处罚决定：</label>
            <span class="info-value plaintext">
              {{ info.administrativePenalty }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">行政强制及其他措施：</label>
            <span class="info-value plaintext">
              {{ info.administrativeForcedMeasures }}
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">其他处理情况：</label>
            <span class="info-value plaintext">
              {{ info.otherHandling }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">逻辑删除：</label>
            <span class="info-value plaintext">
              {{ info.isDeleted }}
            </span>
          </div>
        </el-col>
      </el-row>
    </div>
  </el-drawer>
</template>

<script>
import { getLanguage } from '@/api/system/language'

export default {
  name: 'LanguageViewDrawer',
  data() {
    return {
      visible: false,
      loading: false,
      info: {}
    }
  },
  methods: {
    open(id) {
      this.visible = true
      this.loading = true
      getLanguage(id).then(res => {
        this.info = res.data || {}
      }).finally(() => {
        this.loading = false
      })
    },
    handleClose() {
      this.visible = false
    }
  }
}
</script>
