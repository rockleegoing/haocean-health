<template>
  <div class="app-container">
    <el-tabs v-model="activeName" @tab-click="handleTabClick">
      <el-tab-pane label="模板管理" name="template">
        <template v-if="activeName === 'template'">
          <template-list />
        </template>
      </el-tab-pane>
      <el-tab-pane label="套组管理" name="group">
        <template v-if="activeName === 'group'">
          <group-list />
        </template>
      </el-tab-pane>
    </el-tabs>
  </div>
</template>

<script>
import TemplateList from './template/index.vue'
import GroupList from './group/index.vue'

export default {
  name: 'Document',
  components: { TemplateList, GroupList },
  data() {
    return {
      activeName: 'template'
    }
  },
  methods: {
    handleTabClick(tab) {
      this.activeName = tab.name
    }
  }
}
</script>
