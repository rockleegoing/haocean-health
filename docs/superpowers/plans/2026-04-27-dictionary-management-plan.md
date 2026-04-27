# 通用字典管理模块实现计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 为法律类型和监管类型创建独立的数据表和功能菜单，支持后台管理和 Android 移动端同步

**Architecture:** 创建 sys_legal_type 和 sys_supervision_type 两张独立表，通过混合同步模式同步到 Android 移动端，前端 Vue 页面独立管理字典数据

**Tech Stack:** Spring Boot + MyBatis + Vue + Element UI + Kotlin + RoomDB

---

## 设计确认

| 项目 | 设计 |
|------|------|
| API 路径 | `/system/legal/type/*` 和 `/system/supervision/type/*` |
| 菜单位置 | 法律法规 > 法律类型管理 / 监管类型管理 |
| 前端页面 | `/system/regulation/legalType/index.vue`<br>`/system/regulation/supervisionType/index.vue` |
| 前端 API | `legalType.js` 和 `supervisionType.js` (分开) |
| Vo 对象 | 保留 LegalTypeVo、SupervisionTypeVo |

---

## 阶段一：后端

### Task 1: 创建后端Domain对象

**Files:**
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/domain/dict/SysLegalType.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/domain/dict/SysSupervisionType.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/domain/vo/LegalTypeVo.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/domain/vo/SupervisionTypeVo.java`

- [ ] **Step 1: Create SysLegalType.java**

- [ ] **Step 2: Create SysSupervisionType.java**

- [ ] **Step 3: Create LegalTypeVo.java**

- [ ] **Step 4: Create SupervisionTypeVo.java**

- [ ] **Step 5: Compile to verify**

- [ ] **Step 6: Commit**

---

### Task 2: 创建后端Mapper接口和XML

**Files:**
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysLegalTypeMapper.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysSupervisionTypeMapper.java`
- Create: `ruoyi-system/src/main/resources/mapper/system/SysLegalTypeMapper.xml`
- Create: `ruoyi-system/src/main/resources/mapper/system/SysSupervisionTypeMapper.xml`

- [ ] **Step 1: Create SysLegalTypeMapper.java**

- [ ] **Step 2: Create SysSupervisionTypeMapper.java**

- [ ] **Step 3: Create SysLegalTypeMapper.xml**

- [ ] **Step 4: Create SysSupervisionTypeMapper.xml**

- [ ] **Step 5: Compile to verify**

- [ ] **Step 6: Commit**

---

### Task 3: 创建后端Service接口和实现

**Files:**
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/service/ISysLegalTypeService.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/service/ISysSupervisionTypeService.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysLegalTypeServiceImpl.java`
- Create: `ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysSupervisionTypeServiceImpl.java`

- [ ] **Step 1: Create ISysLegalTypeService.java**

- [ ] **Step 2: Create ISysSupervisionTypeService.java**

- [ ] **Step 3: Create SysLegalTypeServiceImpl.java**

- [ ] **Step 4: Create SysSupervisionTypeServiceImpl.java**

- [ ] **Step 5: Compile to verify**

- [ ] **Step 6: Commit**

---

### Task 4: 创建后端Controller

**Files:**
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysLegalTypeController.java`
- Create: `ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysSupervisionTypeController.java`

**API 路径：**
- `/system/legal/type/list` - 列表查询
- `/system/legal/type/{typeId}` - 详情
- `/system/legal/type` - 新增
- `/system/legal/type/{typeIds}` - 删除
- `/system/legal/type/all` - 获取所有正常状态

- [ ] **Step 1: Create SysLegalTypeController.java**

- [ ] **Step 2: Create SysSupervisionTypeController.java**

- [ ] **Step 3: Compile to verify**

- [ ] **Step 4: Commit**

---

## 阶段二：前端

### Task 5: 创建前端API文件

**Files:**
- Create: `ruoyi-ui/src/api/system/legalType.js`
- Create: `ruoyi-ui/src/api/system/supervisionType.js`

**API 方法：**
```javascript
// legalType.js
export function listLegalType(query)  // GET /system/legal/type/list
export function allLegalType()        // GET /system/legal/type/all

// supervisionType.js
export function listSupervisionType(query)  // GET /system/supervision/type/list
export function allSupervisionType()        // GET /system/supervision/type/all
```

- [ ] **Step 1: Create legalType.js**

- [ ] **Step 2: Create supervisionType.js**

- [ ] **Step 3: Commit**

---

### Task 6: 创建前端Vue页面

**Files:**
- Create: `ruoyi-ui/src/views/system/regulation/legalType/index.vue`
- Create: `ruoyi-ui/src/views/system/regulation/supervisionType/index.vue`

**页面功能：**
- 表格列表（typeCode, typeName, sortOrder, status, createTime）
- 新增/编辑弹窗
- 删除确认
- 导出功能

- [ ] **Step 1: Create legalType/index.vue**

- [ ] **Step 2: Create supervisionType/index.vue**

- [ ] **Step 3: Test in browser**

- [ ] **Step 4: Commit**

---

### Task 7: 修改法律法规页面使用API字典

**Files:**
- Modify: `ruoyi-ui/src/views/system/regulation/index.vue`

修改法律类型下拉框，改为调用 `allLegalType()` API 获取数据

注：article/index.vue 无法律类型下拉框，无需改造

- [ ] **Step 1: Modify regulation/index.vue**

- [ ] **Step 2: Test in browser**

- [ ] **Step 3: Commit**

---

## 阶段三：Android端

### Task 8: 创建Android字典Entity和Dao

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/feature/law/db/entity/LegalTypeEntity.kt`
- Create: `app/src/main/java/com/ruoyi/app/feature/law/db/entity/SupervisionTypeEntity.kt`
- Create: `app/src/main/java/com/ruoyi/app/feature/law/db/dao/LegalTypeDao.kt`
- Create: `app/src/main/java/com/ruoyi/app/feature/law/db/dao/SupervisionTypeDao.kt`

- [ ] **Step 1: Create LegalTypeEntity.kt**

- [ ] **Step 2: Create SupervisionTypeEntity.kt**

- [ ] **Step 3: Create LegalTypeDao.kt**

- [ ] **Step 4: Create SupervisionTypeDao.kt**

- [ ] **Step 5: Compile to verify**

- [ ] **Step 6: Commit**

---

### Task 9: 创建Android字典Api

**Files:**
- Create: `app/src/main/java/com/ruoyi/app/feature/law/api/DictApi.kt`

- [ ] **Step 1: Create DictApi.kt**

- [ ] **Step 2: Compile to verify**

- [ ] **Step 3: Commit**

---

### Task 10: 修改Android的LawSyncManager同步字典

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/sync/LawSyncManager.kt`

- [ ] **Step 1: Add dictDao properties**

- [ ] **Step 2: Add syncLegalTypes method**

- [ ] **Step 3: Add syncSupervisionTypes method**

- [ ] **Step 4: Compile to verify**

- [ ] **Step 5: Commit**

---

### Task 11: 修改Android的LawRepository提供字典查询

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/feature/law/repository/LawRepository.kt`

- [ ] **Step 1: Add getAllLegalTypes method**

- [ ] **Step 2: Add getAllSupervisionTypes method**

- [ ] **Step 3: Compile to verify**

- [ ] **Step 4: Commit**

---

### Task 12: 修改Android的LawFragment使用数据库字典

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/fragment/LawFragment.kt`
- Modify: `app/src/main/java/com/ruoyi/app/feature/law/model/Regulation.kt`

移除硬编码的 LegalType 和 SupervisionType 对象，改为从数据库读取

- [ ] **Step 1: Remove hardcoded LegalType and SupervisionType objects**

- [ ] **Step 2: Update LawFragment to use repository methods**

- [ ] **Step 3: Compile to verify**

- [ ] **Step 4: Commit**

---

### Task 13: 修改Android的RegulationListActivity使用数据库字典

**Files:**
- Modify: `app/src/main/java/com/ruoyi/app/feature/law/ui/regulation/RegulationListActivity.kt`

- [ ] **Step 1: Update filter dropdowns to use database**

- [ ] **Step 2: Compile to verify**

- [ ] **Step 3: Commit**

---

## 实现顺序总结

1. Task 1: 后端Domain
2. Task 2: 后端Mapper
3. Task 3: 后端Service
4. Task 4: 后端Controller
5. Task 5: 前端API
6. Task 6: 前端页面
7. Task 7: 修改法规页面
8. Task 8: Android Entity/Dao
9. Task 9: Android Api
10. Task 10: Android Sync
11. Task 11: Android Repository
12. Task 12: Android LawFragment
13. Task 13: Android RegulationListActivity
