<template>
  <el-drawer title="法律条款详情" :visible.sync="visible" direction="rtl" size="60%" append-to-body :before-close="handleClose" custom-class="detail-drawer">
    <div v-loading="loading" class="drawer-content">
      <h4 class="section-header">基本信息</h4>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">法律名称：</label>
            <span class="info-value plaintext">
              {{ info.lawName }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">编：</label>
            <span class="info-value plaintext">
              {{ info.part }}
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">分编：</label>
            <span class="info-value plaintext">
              {{ info.partBranch }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">章：</label>
            <span class="info-value plaintext">
              {{ info.chapter }}
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">节：</label>
            <span class="info-value plaintext">
              {{ info.quarter }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">条：</label>
            <span class="info-value plaintext">
              {{ info.article }}
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">款：</label>
            <span class="info-value plaintext">
              {{ info.section }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">项：</label>
            <span class="info-value plaintext">
              {{ info.subparagraph }}
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">目：</label>
            <span class="info-value plaintext">
              {{ info.item }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">中文条款编码：</label>
            <span class="info-value plaintext">
              {{ info.zhCode }}
            </span>
          </div>
        </el-col>
      </el-row>
      <el-row :gutter="20" class="mb8">
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">条款内容：</label>
            <span class="info-value plaintext">
              {{ info.content }}
            </span>
          </div>
        </el-col>
        <el-col :span="12">
          <div class="info-item">
            <label class="info-label">远程库id：</label>
            <span class="info-value plaintext">
              {{ info.stashTermId }}
            </span>
          </div>
        </el-col>
      </el-row>
    </div>
  </el-drawer>
</template>

<script>
import { getLegalterm } from '@/api/system/legalterm'
import { listLaw } from '@/api/system/law'

export default {
  name: 'LegaltermViewDrawer',
  data() {
    return {
      visible: false,
      loading: false,
      info: {},
      lawOptions: []
    }
  },
  methods: {
    open(id) {
      this.visible = true
      this.loading = true
      // 获取法律列表用于显示名称
      listLaw({ pageNum: 1, pageSize: 1000 }).then(lawRes => {
        this.lawOptions = lawRes.rows || []
        return getLegalterm(id)
      }).then(res => {
        const law = this.lawOptions.find(item => item.id === res.data.lawId)
        this.info = {
          ...res.data,
          lawName: law ? law.name : res.data.lawId
        }
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
