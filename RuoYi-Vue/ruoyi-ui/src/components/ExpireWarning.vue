<template>
  <el-tag :type="warningType" size="small" v-if="showWarning">
    <i class="el-icon-warning"></i>
    {{ warningText }}
  </el-tag>
</template>

<script>
export default {
  name: 'ExpireWarning',
  props: {
    expireTime: {
      type: [Number, String],
      required: true
    }
  },
  data() {
    return {
      currentTime: Date.now()
    }
  },
  computed: {
    showWarning() {
      const remainDays = this.getRemainDays()
      return remainDays >= 0 && remainDays <= 7
    },
    warningType() {
      const remainDays = this.getRemainDays()
      if (remainDays <= 1) return 'danger'
      if (remainDays <= 3) return 'warning'
      return ''
    },
    warningText() {
      const remainDays = this.getRemainDays()
      if (remainDays < 0) return '已过期'
      if (remainDays === 0) return '今日到期'
      return `剩${remainDays}天到期`
    }
  },
  mounted() {
    // 每分钟更新一次显示
    this.timer = setInterval(() => {
      this.currentTime = Date.now()
    }, 60000)
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    getRemainDays() {
      const now = this.currentTime
      const expire = new Date(this.expireTime).getTime()
      return Math.floor((expire - now) / (1000 * 60 * 60 * 24))
    }
  }
}
</script>

<style scoped>
.el-tag {
  margin-left: 8px;
}
</style>
