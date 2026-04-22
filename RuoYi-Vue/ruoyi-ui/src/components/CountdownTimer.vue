<template>
  <span :style="{ color: textColor }">{{ displayTime }}</span>
</template>

<script>
export default {
  name: 'CountdownTimer',
  props: {
    expireTime: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      currentTime: Date.now(),
      timer: null
    }
  },
  computed: {
    remainingTime() {
      const expire = typeof this.expireTime === 'string'
        ? new Date(this.expireTime).getTime()
        : this.expireTime
      return Math.max(0, expire - this.currentTime)
    },
    displayTime() {
      const remaining = this.remainingTime
      if (remaining <= 0) {
        return '已过期'
      }

      const days = Math.floor(remaining / (1000 * 60 * 60 * 24))
      const hours = Math.floor((remaining % (1000 * 60 * 60 * 24)) / (1000 * 60 * 60))
      const minutes = Math.floor((remaining % (1000 * 60 * 60)) / (1000 * 60))
      const seconds = Math.floor((remaining % (1000 * 60)) / 1000)

      if (days > 0) {
        return `${days}天${hours}时${minutes}分`
      } else if (hours > 0) {
        return `${hours}时${minutes}分${seconds}秒`
      } else {
        return `${minutes}分${seconds}秒`
      }
    },
    textColor() {
      const remaining = this.remainingTime
      const days = Math.floor(remaining / (1000 * 60 * 60 * 24))

      if (remaining <= 0) {
        return '#909399' // 灰色 - 已过期
      } else if (days < 3) {
        return '#F56C6C' // 红色 - 不足 3 天
      } else if (days < 7) {
        return '#E6A23C' // 橙色 - 不足 7 天
      } else {
        return '#67C23A' // 绿色 - 7 天以上
      }
    }
  },
  mounted() {
    // 每分钟更新一次
    this.timer = setInterval(() => {
      this.currentTime = Date.now()
    }, 60000)
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  }
}
</script>
