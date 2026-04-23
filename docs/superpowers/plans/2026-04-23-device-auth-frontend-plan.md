# 前端体验优化实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 提升设备与激活码管理页面的用户体验，实现状态定时刷新、到期提醒功能。

**Architecture:** 在现有 Vue 2.6 + Element UI 架构上扩展，新增 ExpireWarning 组件，修改现有页面添加定时刷新逻辑。

**Tech Stack:** Vue 2.6, Element UI, Axios

---

### Task 1: 创建激活码到期提醒组件

**Files:**
- Create: `RuoYi-Vue/ruoyi-ui/src/components/ExpireWarning.vue`

- [ ] **Step 1: 创建组件文件**

```vue
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
```

- [ ] **Step 2: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue/ruoyi-ui
git add src/components/ExpireWarning.vue
git commit -m "feat(ui): 创建激活码到期提醒组件"
```

---

### Task 2: 在激活码列表页面使用到期提醒组件

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/device/activation-code/index.vue`

- [ ] **Step 1: 读取当前文件内容**

```bash
cat RuoYi-Vue/ruoyi-ui/src/views/device/activation-code/index.vue
```

- [ ] **Step 2: 导入 ExpireWarning 组件**

在 `<script>` 部分修改：

```javascript
import { listActivationCode, delActivationCode, batchDelActivationCode } from "@/api/device/activationCode"
import CreateDialog from "./CreateDialog"
import CountdownTimer from "@/components/CountdownTimer"
import ExpireWarning from "@/components/ExpireWarning"  // 新增

export default {
  name: "ActivationCode",
  components: {
    CreateDialog,
    CountdownTimer,
    ExpireWarning  // 新增
  },
```

- [ ] **Step 3: 在表格中添加到期提醒**

找到"有效期"列，修改为：

```vue
<el-table-column label="有效期" align="center" prop="expireTime" width="200">
  <template slot-scope="scope">
    <div style="display: flex; align-items: center;">
      <countdown-timer v-if="scope.row.status === '0'" :expire-time="scope.row.expireTime" />
      <span v-else>{{ parseTime(scope.row.expireTime) }}</span>
      <expire-warning v-if="scope.row.status === '0'" :expire-time="scope.row.expireTime" />
    </div>
  </template>
</el-table-column>
```

- [ ] **Step 4: 验证文件内容**

```bash
cat RuoYi-Vue/ruoyi-ui/src/views/device/activation-code/index.vue
```

- [ ] **Step 5: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue/ruoyi-ui
git add src/views/device/activation-code/index.vue
git commit -m "feat(ui): 激活码列表添加到期提醒"
```

---

### Task 3: 设备列表页面添加定时刷新

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/device/device/index.vue`

- [ ] **Step 1: 读取当前文件内容**

```bash
cat RuoYi-Vue/ruoyi-ui/src/views/device/device/index.vue
```

- [ ] **Step 2: 添加定时器变量**

在 `data()` 中添加：

```javascript
data() {
  return {
    // ... 现有数据
    // 定时刷新 ID
    refreshTimer: null
  }
},
```

- [ ] **Step 3: 在 created 钩子中启动定时器**

修改 `created()` 方法：

```javascript
created() {
  this.getList()
  // 30 秒定时刷新
  this.refreshTimer = setInterval(() => {
    this.getList()
  }, 30000)
},
```

- [ ] **Step 4: 添加 beforeDestroy 钩子清除定时器**

在 methods 后添加：

```javascript
beforeDestroy() {
  if (this.refreshTimer) {
    clearInterval(this.refreshTimer)
  }
}
```

- [ ] **Step 5: 验证文件内容**

```bash
cat RuoYi-Vue/ruoyi-ui/src/views/device/device/index.vue
```

- [ ] **Step 6: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue/ruoyi-ui
git add src/views/device/device/index.vue
git commit -m "feat(ui): 设备列表添加 30 秒定时刷新"
```

---

### Task 4: 激活码列表页面添加定时刷新

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/device/activation-code/index.vue`

- [ ] **Step 1: 读取当前文件内容**

```bash
cat RuoYi-Vue/ruoyi-ui/src/views/device/activation-code/index.vue
```

- [ ] **Step 2: 添加定时器变量**

在 `data()` 中添加：

```javascript
data() {
  return {
    // ... 现有数据
    // 定时刷新 ID
    refreshTimer: null
  }
},
```

- [ ] **Step 3: 在 created 钩子中启动定时器**

修改 `created()` 方法：

```javascript
created() {
  this.getList()
  // 30 秒定时刷新
  this.refreshTimer = setInterval(() => {
    this.getList()
  }, 30000)
},
```

- [ ] **Step 4: 添加 beforeDestroy 钩子清除定时器**

在 methods 后添加：

```javascript
beforeDestroy() {
  if (this.refreshTimer) {
    clearInterval(this.refreshTimer)
  }
}
```

- [ ] **Step 5: 验证文件内容**

```bash
cat RuoYi-Vue/ruoyi-ui/src/views/device/activation-code/index.vue
```

- [ ] **Step 6: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue/ruoyi-ui
git add src/views/device/activation-code/index.vue
git commit -m "feat(ui): 激活码列表添加 30 秒定时刷新"
```

---

### Task 5: 前端编译测试

**Files:**
- Test: npm 编译

- [ ] **Step 1: 安装依赖（如需要）**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue/ruoyi-ui
npm install --registry=https://registry.npmmirror.com
```

- [ ] **Step 2: 编译前端项目**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue/ruoyi-ui
npm run build:prod
```

预期输出：`Build complete`

- [ ] **Step 3: 检查是否有编译错误**

如有错误，修复后重新编译。

---

## 验证

执行完所有任务后，应验证：
- [ ] ExpireWarning 组件正常显示
- [ ] 激活码列表显示到期提醒（7 天/3 天/1 天）
- [ ] 设备列表 30 秒自动刷新
- [ ] 激活码列表 30 秒自动刷新
- [ ] 前端编译成功

---

## 后续任务

前端体验优化完成后，所有模块实施完毕。执行最终验证：

```bash
# 1. 执行后端数据库变更
mysql -u root -p -h 127.0.0.1 ry-vue < RuoYi-Vue/sql/V1.0.4__device_auth_enhancement.sql

# 2. 启动后端服务
cd RuoYi-Vue
mvn spring-boot:run -pl ruoyi-admin

# 3. 启动前端开发服务器
cd RuoYi-Vue/ruoyi-ui
npm run dev
```
