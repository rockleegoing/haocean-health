# 法律法规模块完善实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 完善法律法规模块后台管理和移动端功能，包括分页修复、导入导出、法规详情增强、树状图展示、混合模式同步

**Architecture:** 后端采用 Spring Boot + MyBatis + PageHelper 分页，前端 Vue + Element UI，移动端 Android + Kotlin + RoomDB

**Tech Stack:** Spring Boot 4.0.3, Vue 2.6, Element UI, Android Kotlin, RoomDB, Apache POI

---

## 文件结构

### 后台管理端 (RuoYi-Vue)

```
RuoYi-Vue/
├── ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/
│   ├── SysRegulationController.java      # 修改：添加分页、导入导出
│   └── SysLegalBasisController.java      # 修改：添加导入导出
├── ruoyi-system/src/main/java/com/ruoyi/system/
│   ├── service/ISysRegulationService.java   # 修改：添加分页方法
│   ├── service/impl/SysRegulationServiceImpl.java  # 修改：实现分页
│   ├── mapper/SysRegulationMapper.java     # 修改：添加分页SQL
│   └── domain/
│       ├── SysRegulation.java             # 修改：添加 updateTime 字段
│       ├── SysRegulationChapter.java       # 修改：添加 updateTime 字段
│       ├── SysRegulationArticle.java       # 修改：添加 updateTime 字段
│       └── SysLegalBasis.java             # 修改：添加 updateTime 字段
├── ruoyi-ui/src/views/system/regulation/
│   ├── index.vue                         # 修改：法规详情增强
│   ├── chapter/index.vue                  # 修改：分页支持
│   └── article/index.vue                 # 修改：分页支持
└── ruoyi-ui/src/views/system/legalBasis/
    └── index.vue                         # 修改：导入导出
```

### 移动端 (Ruoyi-Android-App)

```
Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/
├── feature/law/
│   ├── api/LawApi.kt                     # 修改：添加增量同步接口
│   ├── repository/LawRepository.kt       # 修改：混合同步、移除收藏
│   ├── db/
│   │   ├── dao/RegulationDao.kt         # 修改：增量查询
│   │   └── entity/RegulationEntity.kt    # 不变
│   └── ui/regulation/
│       ├── RegulationDetailActivity.kt   # 修改：树状图展示
│       └── ArticleDetailActivity.kt     # 不变
├── feature/basis/
│   └── ui/
│       └── LegalBasisDetailActivity.kt  # 修改：添加复制功能
└── sync/
    └── LawSyncManager.kt                # 新增：同步管理器
```

---

## 阶段一：后台管理端 - 分页修复

### Task 1: 章节列表分页支持

**Files:**
- Modify: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysRegulationController.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysRegulationService.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysRegulationServiceImpl.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SysRegulationMapper.java`
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/system/regulation/chapter/index.vue`

- [ ] **Step 1: 修改 SysRegulationController.java - 添加分页参数**

在 `getChapterList` 方法添加分页支持：

```java
/**
 * 获取章节列表
 */
@Anonymous
@GetMapping("/chapters/{regulationId}")
public TableDataInfo getChapterList(
    @PathVariable("regulationId") Long regulationId,
    @RequestParam(defaultValue = "1") Integer pageNum,
    @RequestParam(defaultValue = "10") Integer pageSize) {
    startPage();
    List<SysRegulationChapter> list = sysRegulationService.selectChapterListByRegulationId(regulationId);
    return getDataTable(list);
}
```

- [ ] **Step 2: 修改 ISysRegulationService.java - 添加重载方法**

添加带分页参数的方法签名：

```java
/**
 * 查询章节列表（带分页）
 */
public List<SysRegulationChapter> selectChapterListByRegulationId(Long regulationId);
```

- [ ] **Step 3: 修改 SysRegulationServiceImpl.java - 章节方法不变**

章节列表查询本身不需要修改，因为 PageHelper 会在查询前拦截

- [ ] **Step 4: 修改 chapter/index.vue - 使用真实分页**

修改 `getList()` 方法：

```javascript
getList() {
  if (!this.queryParams.regulationId) {
    this.chapterList = []
    this.total = 0
    this.loading = false
    return
  }
  this.loading = true
  getChapterList(this.queryParams.regulationId, {
    pageNum: this.queryParams.pageNum,
    pageSize: this.queryParams.pageSize
  }).then(response => {
    this.chapterList = response.rows || []
    this.total = response.total || 0
    this.loading = false
  })
},
```

修改 API 调用导入：

```javascript
import { getChapter, delChapter, addChapter, updateChapter, getChapterList } from "@/api/system/regulation"
```

修改 `getChapterList` 调用传入分页参数

- [ ] **Step 5: 提交**

```bash
git add RuoYi-Vue/ruoyi-admin/.../SysRegulationController.java
git add RuoYi-Vue/ruoyi-system/.../ISysRegulationService.java
git add RuoYi-Vue/ruoyi-system/.../SysRegulationServiceImpl.java
git add RuoYi-Vue/ruoyi-system/.../SysRegulationMapper.java
git add RuoYi-Vue/ruoyi-ui/.../chapter/index.vue
git commit -m "fix(backend): 添加章节列表分页支持"
```

---

### Task 2: 条款列表分页支持

**Files:**
- Modify: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysRegulationController.java`
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/system/regulation/article/index.vue`

- [ ] **Step 1: 修改 SysRegulationController.java - 添加分页参数**

```java
/**
 * 获取条款列表
 */
@Anonymous
@GetMapping("/articles/{regulationId}")
public TableDataInfo getArticleList(
    @PathVariable("regulationId") Long regulationId,
    @RequestParam(required = false) Long chapterId,
    @RequestParam(defaultValue = "1") Integer pageNum,
    @RequestParam(defaultValue = "10") Integer pageSize) {
    startPage();
    List<SysRegulationArticle> list = sysRegulationService.selectArticleListByRegulationId(regulationId, chapterId);
    return getDataTable(list);
}
```

- [ ] **Step 2: 修改 ISysRegulationService.java - 添加带 chapterId 参数的方法**

```java
/**
 * 查询条款列表（可选按章节筛选）
 */
public List<SysRegulationArticle> selectArticleListByRegulationId(Long regulationId, Long chapterId);
```

- [ ] **Step 3: 修改 SysRegulationServiceImpl.java - 实现章节筛选**

```java
@Override
public List<SysRegulationArticle> selectArticleListByRegulationId(Long regulationId, Long chapterId) {
    List<SysRegulationArticle> articles = sysRegulationMapper.selectArticleListByRegulationId(regulationId);
    // 填充法规和章节标题
    // ... 保持原有逻辑 ...
    // 如果指定了 chapterId，进行过滤
    if (chapterId != null) {
        articles = articles.stream()
            .filter(a -> chapterId.equals(a.getChapterId()))
            .collect(Collectors.toList());
    }
    return articles;
}
```

- [ ] **Step 4: 修改 article/index.vue - 使用真实分页**

修改 `getList()` 方法：

```javascript
getList() {
  this.loading = true
  getArticleList(this.queryParams.regulationId, this.queryParams.chapterId, {
    pageNum: this.queryParams.pageNum,
    pageSize: this.queryParams.pageSize
  }).then(response => {
    this.articleList = response.rows || []
    this.total = response.total || 0
    this.loading = false
  })
},
```

- [ ] **Step 5: 提交**

```bash
git add RuoYi-Vue/ruoyi-admin/.../SysRegulationController.java
git add RuoYi-Vue/ruoyi-system/.../ISysRegulationService.java
git add RuoYi-Vue/ruoyi-system/.../SysRegulationServiceImpl.java
git add RuoYi-Vue/ruoyi-ui/.../article/index.vue
git commit -m "fix(backend): 添加条款列表分页和章节筛选支持"
```

---

## 阶段二：后台管理端 - 导入导出功能

### Task 3: Excel + JSON 导入导出

**Files:**
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/vo/RegulationImportVo.java` (新增)
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/vo/ChapterImportVo.java` (新增)
- Create: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/vo/ArticleImportVo.java` (新增)
- Modify: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysRegulationController.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysRegulationService.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysRegulationServiceImpl.java`

- [ ] **Step 1: 创建 ArticleImportVo.java**

```java
package com.ruoyi.system.domain.vo;

public class ArticleImportVo {
    private String articleNo;
    private String content;
    private Integer sortOrder;

    public String getArticleNo() { return articleNo; }
    public void setArticleNo(String articleNo) { this.articleNo = articleNo; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
```

- [ ] **Step 2: 创建 ChapterImportVo.java**

```java
package com.ruoyi.system.domain.vo;

import java.util.List;

public class ChapterImportVo {
    private String chapterNo;
    private String chapterTitle;
    private Integer sortOrder;
    private List<ArticleImportVo> articles;

    public String getChapterNo() { return chapterNo; }
    public void setChapterNo(String chapterNo) { this.chapterNo = chapterNo; }
    public String getChapterTitle() { return chapterTitle; }
    public void setChapterTitle(String chapterTitle) { this.chapterTitle = chapterTitle; }
    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    public List<ArticleImportVo> getArticles() { return articles; }
    public void setArticles(List<ArticleImportVo> articles) { this.articles = articles; }
}
```

- [ ] **Step 3: 创建 RegulationImportVo.java**

```java
package com.ruoyi.system.domain.vo;

import java.util.List;

public class RegulationImportVo {
    private String title;
    private String legalType;
    private String supervisionTypes;
    private String publishDate;
    private String effectiveDate;
    private String issuingAuthority;
    private String content;
    private List<ChapterImportVo> chapters;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getLegalType() { return legalType; }
    public void setLegalType(String legalType) { this.legalType = legalType; }
    public String getSupervisionTypes() { return supervisionTypes; }
    public void setSupervisionTypes(String supervisionTypes) { this.supervisionTypes = supervisionTypes; }
    public String getPublishDate() { return publishDate; }
    public void setPublishDate(String publishDate) { this.publishDate = publishDate; }
    public String getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(String effectiveDate) { this.effectiveDate = effectiveDate; }
    public String getIssuingAuthority() { return issuingAuthority; }
    public void setIssuingAuthority(String issuingAuthority) { this.issuingAuthority = issuingAuthority; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public List<ChapterImportVo> getChapters() { return chapters; }
    public void setChapters(List<ChapterImportVo> chapters) { this.chapters = chapters; }
}
```

- [ ] **Step 4: 修改 ISysRegulationService.java - 添加导入方法**

```java
/**
 * 批量导入法律法规（支持章节和条款）
 * @param regulations 法规列表
 * @param updateSupport 是否支持更新
 * @param operName 操作人
 * @return 导入结果
 */
public Map<String, Object> importRegulation(List<RegulationImportVo> regulations, boolean updateSupport, String operName);
```

- [ ] **Step 5: 修改 SysRegulationServiceImpl.java - 实现导入逻辑**

```java
@Override
public Map<String, Object> importRegulation(List<RegulationImportVo> regulations, boolean updateSupport, String operName) {
    Map<String, Object> result = new HashMap<>();
    int successCount = 0;
    int failCount = 0;
    List<String> errors = new ArrayList<>();

    for (int i = 0; i < regulations.size(); i++) {
        RegulationImportVo vo = regulations.get(i);
        try {
            // 校验必填字段
            if (StringUtils.isEmpty(vo.getTitle()) || StringUtils.isEmpty(vo.getLegalType())) {
                errors.add("第" + (i+1) + "条：法律名称和法律类型不能为空");
                failCount++;
                continue;
            }

            // 检查是否已存在
            SysRegulation existReg = new SysRegulation();
            existReg.setTitle(vo.getTitle());
            List<SysRegulation> existList = selectSysRegulationList(existReg);

            Long regulationId;
            if (!existList.isEmpty() && updateSupport) {
                // 更新模式
                SysRegulation updateReg = existList.get(0);
                updateReg.setLegalType(vo.getLegalType());
                updateReg.setSupervisionTypes(vo.getSupervisionTypes());
                updateReg.setPublishDate(vo.getPublishDate());
                updateReg.setEffectiveDate(vo.getEffectiveDate());
                updateReg.setIssuingAuthority(vo.getIssuingAuthority());
                updateReg.setContent(vo.getContent());
                updateReg.setUpdateBy(operName);
                updateSysRegulation(updateReg);
                regulationId = updateReg.getRegulationId();
            } else if (!existList.isEmpty()) {
                errors.add("第" + (i+1) + "条：法律名称已存在（" + vo.getTitle() + "），跳过导入");
                failCount++;
                continue;
            } else {
                // 新增模式
                SysRegulation newReg = new SysRegulation();
                newReg.setTitle(vo.getTitle());
                newReg.setLegalType(vo.getLegalType());
                newReg.setSupervisionTypes(vo.getSupervisionTypes());
                newReg.setPublishDate(vo.getPublishDate());
                newReg.setEffectiveDate(vo.getEffectiveDate());
                newReg.setIssuingAuthority(vo.getIssuingAuthority());
                newReg.setContent(vo.getContent());
                newReg.setCreateBy(operName);
                insertSysRegulation(newReg);
                regulationId = newReg.getRegulationId();
            }

            // 导入章节和条款
            if (vo.getChapters() != null) {
                for (ChapterImportVo chapterVo : vo.getChapters()) {
                    if (StringUtils.isEmpty(chapterVo.getChapterNo()) && StringUtils.isEmpty(chapterVo.getChapterTitle())) {
                        continue;
                    }
                    SysRegulationChapter chapter = new SysRegulationChapter();
                    chapter.setRegulationId(regulationId);
                    chapter.setChapterNo(chapterVo.getChapterNo());
                    chapter.setChapterTitle(chapterVo.getChapterTitle());
                    chapter.setSortOrder(chapterVo.getSortOrder() != null ? chapterVo.getSortOrder() : 0);
                    chapter.setCreateBy(operName);
                    insertSysRegulationChapter(chapter);

                    // 导入条款
                    if (chapterVo.getArticles() != null) {
                        for (ArticleImportVo articleVo : chapterVo.getArticles()) {
                            if (StringUtils.isEmpty(articleVo.getArticleNo()) && StringUtils.isEmpty(articleVo.getContent())) {
                                continue;
                            }
                            SysRegulationArticle article = new SysRegulationArticle();
                            article.setRegulationId(regulationId);
                            article.setChapterId(chapter.getChapterId());
                            article.setArticleNo(articleVo.getArticleNo());
                            article.setContent(articleVo.getContent());
                            article.setSortOrder(articleVo.getSortOrder() != null ? articleVo.getSortOrder() : 0);
                            article.setCreateBy(operName);
                            insertSysRegulationArticle(article);
                        }
                    }
                }
            }
            successCount++;
        } catch (Exception e) {
            errors.add("第" + (i+1) + "条：导入失败 - " + e.getMessage());
            failCount++;
        }
    }

    result.put("success", successCount);
    result.put("fail", failCount);
    result.put("errors", errors);
    return result;
}
```

- [ ] **Step 6: 修改 SysRegulationController.java - 添加导出导入方法**

```java
/**
 * 导出法律法规 Excel
 */
@Anonymous
@GetMapping("/export")
public void export(HttpServletResponse response, SysRegulation sysRegulation) {
    List<SysRegulation> list = sysRegulationService.selectSysRegulationList(sysRegulation);
    ExcelUtil.exportExcel(response, list, "法律法规数据", SysRegulation.class);
}

/**
 * 导入法律法规 Excel
 */
@Anonymous
@PostMapping("/import")
public AjaxResult importExcel(MultipartFile file, boolean updateSupport) throws Exception {
    List<RegulationImportVo> voList = ExcelUtil.importExcel(file.getInputStream(), RegulationImportVo.class);
    String operName = getUsername();
    Map<String, Object> result = sysRegulationService.importRegulation(voList, updateSupport, operName);
    return AjaxResult.success("导入成功，成功" + result.get("success") + "条，失败" + result.get("fail") + "条", result.get("errors"));
}

/**
 * 导出法律法规 JSON
 */
@Anonymous
@GetMapping("/export/json")
public void exportJson(HttpServletResponse response, SysRegulation sysRegulation) {
    List<SysRegulation> list = sysRegulationService.selectSysRegulationList(sysRegulation);
    List<Map<String, Object>> fullList = new ArrayList<>();
    for (SysRegulation reg : list) {
        Map<String, Object> regMap = new HashMap<>();
        regMap.put("title", reg.getTitle());
        regMap.put("legalType", reg.getLegalType());
        regMap.put("supervisionTypes", reg.getSupervisionTypes());
        regMap.put("publishDate", reg.getPublishDate());
        regMap.put("effectiveDate", reg.getEffectiveDate());
        regMap.put("issuingAuthority", reg.getIssuingAuthority());
        regMap.put("content", reg.getContent());
        regMap.put("chapters", sysRegulationService.selectChapterListByRegulationId(reg.getRegulationId()));
        fullList.add(regMap);
    }
    response.setContentType("application/json;charset=utf-8");
    response.setCharacterEncoding("utf-8");
    try (PrintWriter writer = response.getWriter()) {
        writer.write(new ObjectMapper().writeValueAsString(fullList));
    }
}

/**
 * 导入法律法规 JSON
 */
@Anonymous
@PostMapping("/import/json")
public AjaxResult importJson(@RequestBody List<RegulationImportVo> voList) {
    String operName = getUsername();
    Map<String, Object> result = sysRegulationService.importRegulation(voList, false, operName);
    @SuppressWarnings("unchecked")
    List<String> errors = (List<String>) result.get("errors");
    return AjaxResult.success("导入完成，成功" + result.get("success") + "条，失败" + result.get("fail") + "条", errors);
}
```

- [ ] **Step 7: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/vo/RegulationImportVo.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/vo/ChapterImportVo.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/vo/ArticleImportVo.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/ISysRegulationService.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/impl/SysRegulationServiceImpl.java
git add RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysRegulationController.java
git commit -m "feat(backend): 添加法律法规Excel和JSON导入导出功能"
```

---

## 阶段三：后台管理端 - 法规详情增强

### Task 5: 法规详情页添加管理章节入口

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/views/system/regulation/index.vue`

- [ ] **Step 1: 修改 regulation/index.vue - 法规详情对话框添加管理章节按钮**

在法规详情对话框中添加按钮：

```vue
<!-- 法规详情对话框 -->
<el-dialog title="法律法规详情" :visible.sync="detailOpen" width="800px" append-to-body>
  <el-descriptions :column="2" border>
    <!-- ... 现有字段 ... -->
  </el-descriptions>
  <div slot="footer" class="dialog-footer">
    <el-button type="primary" @click="handleManageChapters">管理章节</el-button>
    <el-button @click="detailOpen = false">关 闭</el-button>
  </div>
</el-dialog>
```

添加方法：

```javascript
handleManageChapters() {
  this.$router.push({
    path: '/system/regulation/chapter',
    query: { regulationId: this.detailData.regulationId }
  })
},
```

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/ruoyi-ui/src/views/system/regulation/index.vue
git commit -m "feat(backend): 法规详情页添加管理章节入口"
```

---

## 阶段四：移动端 - 移除收藏功能

### Task 6: 删除收藏相关代码

**Files:**
- Delete: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/db/dao/CollectionDao.kt` (如存在)
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawRepository.kt`
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/api/LawApi.kt`

- [ ] **Step 1: 检查收藏相关文件是否存在**

```bash
find Ruoyi-Android-App -name "*Collection*" -o -name "*collection*"
```

- [ ] **Step 2: 从 LawRepository 删除收藏相关方法**

删除以下方法（如果存在）：
- `addCollection()`
- `removeCollection()`
- `isCollected()`
- `getCollections()`

- [ ] **Step 3: 从 LawApi 删除收藏相关方法**

删除以下方法（如果存在）：
- `addCollection()`
- `removeCollection()`
- `getCollections()`

- [ ] **Step 4: 提交**

```bash
git add -A  # 删除的文件会被暂存
git commit -m "refactor(android): 移除收藏功能相关代码"
```

---

## 阶段五：移动端 - 混合模式同步

### Task 7: 后端添加增量同步参数支持

**Files:**
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysRegulation.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysRegulationChapter.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysRegulationArticle.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysLegalBasis.java`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysRegulationMapper.xml`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysRegulationChapterMapper.xml`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysRegulationArticleMapper.xml`
- Modify: `RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysLegalBasisMapper.xml`
- Modify: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysRegulationController.java`
- Modify: `RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysLegalBasisController.java`

- [ ] **Step 1: 修改 SysRegulation.java - 添加 updateTimeFrom 字段**

在 SysRegulation 类中添加（用于接收前端传来的增量同步时间参数）：

```java
/** 增量同步起始时间（前端传入，非数据库字段） */
private String updateTimeFrom;

public String getUpdateTimeFrom() { return updateTimeFrom; }
public void setUpdateTimeFrom(String updateTimeFrom) { this.updateTimeFrom = updateTimeFrom; }
```

- [ ] **Step 1.5: 修改 SysRegulationChapter.java - 添加 updateTimeFrom 字段**

```java
/** 增量同步起始时间（前端传入，非数据库字段） */
private String updateTimeFrom;

public String getUpdateTimeFrom() { return updateTimeFrom; }
public void setUpdateTimeFrom(String updateTimeFrom) { this.updateTimeFrom = updateTimeFrom; }
```

- [ ] **Step 1.6: 修改 SysRegulationArticle.java - 添加 updateTimeFrom 字段**

```java
/** 增量同步起始时间（前端传入，非数据库字段） */
private String updateTimeFrom;

public String getUpdateTimeFrom() { return updateTimeFrom; }
public void setUpdateTimeFrom(String updateTimeFrom) { this.updateTimeFrom = updateTimeFrom; }
```

同样为 SysLegalBasis 添加 updateTimeFrom 字段（Step 1 中已说明）。

- [ ] **Step 2: 修改 SysRegulationMapper.xml - 添加 updateTimeFrom 查询条件**

找到 `selectSysRegulationList` SQL，添加 where 条件：

```xml
<select id="selectSysRegulationList" parameterType="SysRegulation" resultMap="SysRegulationResult">
    <include refid="selectSysRegulationVo"/>
    <where>
        <if test="title != null and title != ''">and title like concat('%', #{title}, '%')</if>
        <if test="legalType != null and legalType != ''">and legal_type = #{legalType}</if>
        <if test="status != null and status != ''">and status = #{status}</if>
        <if test="delFlag != null and delFlag != ''">and del_flag = #{delFlag}</if>
        <if test="updateTimeFrom != null and updateTimeFrom != ''">and update_time &gt;= #{updateTimeFrom}</if>
    </where>
    order by create_time desc
</select>
```

- [ ] **Step 2.5: 修改 SysRegulationChapterMapper.xml - 添加 updateTimeFrom 查询条件**

在章节列表查询 SQL 中添加 updateTimeFrom 条件（如果有的话）。

- [ ] **Step 2.6: 修改 SysRegulationArticleMapper.xml - 添加 updateTimeFrom 查询条件**

在条款列表查询 SQL 中添加 updateTimeFrom 条件（如果有的话）。

- [ ] **Step 3: 修改 SysRegulationController.java - 添加 updateTimeFrom 参数支持**

修改法规列表接口：

```java
/**
 * 获取法律法规列表
 */
@Anonymous
@GetMapping("/list")
public TableDataInfo list(SysRegulation sysRegulation) {
    startPage();
    List<SysRegulation> list = sysRegulationService.selectSysRegulationList(sysRegulation);
    return getDataTable(list);
}
```

修改定性依据列表接口（同样添加 updateTimeFrom 参数）。

同时修改章节列表和条款列表接口，添加 updateTimeFrom 参数支持（如果需要增量同步章节和条款的话）。

- [ ] **Step 4: 提交**

```bash
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysRegulation.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysRegulationChapter.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysRegulationArticle.java
git add RuoYi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/SysLegalBasis.java
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysRegulationMapper.xml
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysRegulationChapterMapper.xml
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysRegulationArticleMapper.xml
git add RuoYi-Vue/ruoyi-system/src/main/resources/mapper/system/SysLegalBasisMapper.xml
git add RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysRegulationController.java
git add RuoYi-Vue/ruoyi-admin/src/main/java/com/ruoyi/web/controller/system/SysLegalBasisController.java
git commit -m "feat(backend): 添加增量同步updateTimeFrom参数支持"
```

---

### Task 8: 移动端添加增量同步接口支持

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/api/LawApi.kt`

- [ ] **Step 1: 修改 LawApi.kt - 添加增量同步参数支持**

```kotlin
/**
 * 获取法律法规列表（支持增量同步）
 */
suspend fun getRegulationList(
    pageNum: Int = 1,
    pageSize: Int = 20,
    title: String? = null,
    legalType: String? = null,
    status: String? = null,
    updateTimeFrom: String? = null  // 新增：增量同步起始时间
): RegulationListResponse = withContext(Dispatchers.IO) {
    val urlBuilder = StringBuilder("${ConfigApi.baseUrl}/system/regulation/list?")
        .append("pageNum=$pageNum")
        .append("&pageSize=$pageSize")
    title?.let { urlBuilder.append("&title=$it") }
    legalType?.let { urlBuilder.append("&legalType=$it") }
    status?.let { urlBuilder.append("&status=$it") }
    updateTimeFrom?.let { urlBuilder.append("&updateTimeFrom=$it") }

    val request = Request.Builder()
        .url(urlBuilder.toString())
        .get()
        .build()

    val response = client.newCall(request).execute()
    parseRegulationListResponse(response.body?.string() ?: "")
}
```

同样修改 `getLegalBasisList` 方法添加 `updateTimeFrom` 参数。

- [ ] **Step 2: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/api/LawApi.kt
git commit -m "feat(android): 添加增量同步时间参数支持"
```

---

### Task 9: 实现混合模式同步逻辑

**注意：** LawSyncManager 中调用的 DAO 方法已在现有代码中存在：
- `RegulationDao.insertRegulations(List)` ✓ 存在
- `ChapterDao.insertChapters(List)` ✓ 存在
- `ArticleDao.insertArticles(List)` ✓ 存在
- `LegalBasisDao.insertLegalBasises(List)` ✓ 存在

**Files:**
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/LawSyncManager.kt`
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawRepository.kt`

- [ ] **Step 1: 创建 LawSyncManager.kt**

```kotlin
package com.ruoyi.app.sync

import android.content.Context
import android.content.SharedPreferences
import com.ruoyi.app.feature.law.api.LawApi
import com.ruoyi.app.feature.law.db.AppDatabase
import com.ruoyi.app.feature.law.db.entity.RegulationEntity
import com.ruoyi.app.feature.law.db.entity.RegulationChapterEntity
import com.ruoyi.app.feature.law.db.entity.RegulationArticleEntity
import com.ruoyi.app.feature.law.db.entity.LegalBasisEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONArray

class LawSyncManager(private val context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val regulationDao = AppDatabase.getInstance(context).regulationDao()
    private val chapterDao = AppDatabase.getInstance(context).chapterDao()
    private val articleDao = AppDatabase.getInstance(context).articleDao()
    private val legalBasisDao = AppDatabase.getInstance(context).legalBasisDao()

    companion object {
        private const val PREFS_NAME = "law_sync_prefs"
        private const val KEY_LAST_SYNC_TIME = "last_sync_time"
        private const val KEY_HAS_FULL_SYNC = "has_full_sync"
    }

    /**
     * 执行混合模式同步
     */
    suspend fun sync(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val hasFullSync = prefs.getBoolean(KEY_HAS_FULL_SYNC, false)

            if (!hasFullSync) {
                // 首次全量同步
                syncFull()
                prefs.edit().putBoolean(KEY_HAS_FULL_SYNC, true).apply()
            } else {
                // 增量同步
                syncIncremental()
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * 全量同步
     */
    private suspend fun syncFull() {
        // 同步法规
        val regulationResponse = LawApi.getRegulationList(pageNum = 1, pageSize = 1000)
        if (regulationResponse.code == 200) {
            val entities = regulationResponse.rows.map { it.toEntity() }
            regulationDao.insertRegulations(entities)
        }

        // 同步章节和条款（每个法规）
        for (regulation in regulationResponse.rows) {
            syncChaptersAndArticles(regulation.regulationId)
        }

        // 同步定性依据
        val basisResponse = LawApi.getLegalBasisList(pageNum = 1, pageSize = 1000)
        if (basisResponse.code == 200) {
            val entities = basisResponse.rows.map { it.toEntity() }
            legalBasisDao.insertLegalBasises(entities)
        }

        // 更新同步时间
        prefs.edit().putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis()).apply()
    }

    /**
     * 增量同步
     */
    private suspend fun syncIncremental() {
        val lastSyncTime = prefs.getLong(KEY_LAST_SYNC_TIME, 0)
        val lastSyncTimeStr = if (lastSyncTime > 0) {
            java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.CHINA)
                .format(java.util.Date(lastSyncTime))
        } else null

        // 增量同步法规
        val regulationResponse = LawApi.getRegulationList(
            pageNum = 1,
            pageSize = 1000,
            updateTimeFrom = lastSyncTimeStr
        )
        if (regulationResponse.code == 200) {
            for (regulation in regulationResponse.rows) {
                val entity = regulation.toEntity()
                regulationDao.insertRegulations(listOf(entity))
                // 同步该法规的章节和条款
                syncChaptersAndArticles(regulation.regulationId)
            }
        }

        // 增量同步定性依据
        val basisResponse = LawApi.getLegalBasisList(
            pageNum = 1,
            pageSize = 1000,
            updateTimeFrom = lastSyncTimeStr
        )
        if (basisResponse.code == 200) {
            val entities = basisResponse.rows.map { it.toEntity() }
            legalBasisDao.insertLegalBasises(entities)
        }

        // 更新同步时间
        prefs.edit().putLong(KEY_LAST_SYNC_TIME, System.currentTimeMillis()).apply()
    }

    /**
     * 同步章节和条款
     */
    private suspend fun syncChaptersAndArticles(regulationId: Long) {
        val chapterResponse = LawApi.getChapterList(regulationId)
        if (chapterResponse.code == 200) {
            val chapterEntities = chapterResponse.rows.map { it.toEntity() }
            chapterDao.insertChapters(chapterEntities)
        }

        val articleResponse = LawApi.getArticleList(regulationId)
        if (articleResponse.code == 200) {
            val articleEntities = articleResponse.rows.map { it.toEntity() }
            articleDao.insertArticles(articleEntities)
        }
    }

    /**
     * 获取上次同步时间
     */
    fun getLastSyncTime(): Long = prefs.getLong(KEY_LAST_SYNC_TIME, 0)

    /**
     * 强制全量重同步
     */
    suspend fun forceFullSync() {
        prefs.edit().putBoolean(KEY_HAS_FULL_SYNC, false).apply()
        sync()
    }
}
```

- [ ] **Step 2: 修改 Regulation.toEntity() 扩展方法**

在 LawRepository.kt 中添加（注意：createTime/updateTime 是 String 格式 "yyyy-MM-dd HH:mm:ss"，需转换为 Long）：

```kotlin
private fun com.ruoyi.app.feature.law.model.Regulation.toEntity(): RegulationEntity {
    val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss", java.util.Locale.CHINA)
    return RegulationEntity(
        regulationId = regulationId,
        title = title,
        legalType = legalType,
        supervisionTypes = JSONArray(supervisionTypes).toString(),
        publishDate = publishDate,
        effectiveDate = effectiveDate,
        issuingAuthority = issuingAuthority,
        content = content,
        version = version,
        status = status,
        delFlag = delFlag,
        createBy = createBy,
        createTime = createTime?.let { try { dateFormat.parse(it)?.time } catch (e: Exception) { null } },
        updateBy = updateBy,
        updateTime = updateTime?.let { try { dateFormat.parse(it)?.time } catch (e: Exception) { null } },
        remark = remark
    )
}
```

- [ ] **Step 3: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/sync/LawSyncManager.kt
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/repository/LawRepository.kt
git commit -m "feat(android): 实现混合模式法律法规同步"
```

---

## 阶段六：移动端 - 法规详情树状图

### Task 10: 实现法规详情页树状结构

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/regulation/RegulationDetailActivity.kt`
- Create: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/regulation/ChapterTreeAdapter.kt`

- [ ] **Step 1: 创建 ChapterTreeAdapter.kt - 树状列表适配器**

```kotlin
package com.ruoyi.app.feature.law.ui.regulation

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.ruoyi.app.databinding.ItemChapterBinding
import com.ruoyi.app.databinding.ItemArticleBinding

/**
 * 章节-条款树状结构适配器
 */
class ChapterTreeAdapter(
    private val onChapterClick: (ChapterTreeItem.Chapter) -> Unit,
    private val onArticleClick: (ChapterTreeItem.Article) -> Unit
) : ListAdapter<ChapterTreeItem, RecyclerView.ViewHolder>(DiffCallback()) {

    companion object {
        private const val TYPE_CHAPTER = 0
        private const val TYPE_ARTICLE = 1
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is ChapterTreeItem.Chapter -> TYPE_CHAPTER
            is ChapterTreeItem.Article -> TYPE_ARTICLE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CHAPTER -> {
                val binding = ItemChapterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ChapterViewHolder(binding)
            }
            TYPE_ARTICLE -> {
                val binding = ItemArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                ArticleViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown view type")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = getItem(position)) {
            is ChapterTreeItem.Chapter -> (holder as ChapterViewHolder).bind(item)
            is ChapterTreeItem.Article -> (holder as ArticleViewHolder).bind(item)
        }
    }

    inner class ChapterViewHolder(private val binding: ItemChapterBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChapterTreeItem.Chapter) {
            binding.tvChapterNo.text = item.chapterNo
            binding.tvChapterTitle.text = item.chapterTitle
            binding.ivExpand.rotation = if (item.isExpanded) 90f else 0f
            binding.ivExpand.visibility = if (item.hasArticles) View.VISIBLE else View.GONE

            binding.root.setOnClickListener {
                onChapterClick(item)
            }
        }
    }

    inner class ArticleViewHolder(private val binding: ItemArticleBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ChapterTreeItem.Article) {
            binding.tvArticleNo.text = item.articleNo
            binding.tvArticleContent.text = item.content

            binding.root.setOnClickListener {
                onArticleClick(item)
            }
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<ChapterTreeItem>() {
        override fun areItemsTheSame(oldItem: ChapterTreeItem, newItem: ChapterTreeItem): Boolean {
            return when {
                oldItem is ChapterTreeItem.Chapter && newItem is ChapterTreeItem.Chapter ->
                    oldItem.chapterId == newItem.chapterId
                oldItem is ChapterTreeItem.Article && newItem is ChapterTreeItem.Article ->
                    oldItem.articleId == newItem.articleId
                else -> false
            }
        }

        override fun areContentsTheSame(oldItem: ChapterTreeItem, newItem: ChapterTreeItem): Boolean {
            return oldItem == newItem
        }
    }
}

sealed class ChapterTreeItem {
    data class Chapter(
        val chapterId: Long,
        val chapterNo: String?,
        val chapterTitle: String?,
        val hasArticles: Boolean = false,
        var isExpanded: Boolean = false
    ) : ChapterTreeItem()

    data class Article(
        val articleId: Long,
        val chapterId: Long,
        val articleNo: String?,
        val content: String?
    ) : ChapterTreeItem()
}
```

- [ ] **Step 2: 创建 item_chapter.xml 布局**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="12dp"
    android:gravity="center_vertical">

    <ImageView
        android:id="@+id/ivExpand"
        android:layout_width="24dp"
        android:layout_height="24dp"
        android:src="@android:drawable/arrow_down_float"
        android:contentDescription="展开" />

    <TextView
        android:id="@+id/tvChapterNo"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textColor="#333333"
        android:textSize="15sp"
        android:textStyle="bold"
        android:layout_marginStart="8dp" />

    <TextView
        android:id="@+id/tvChapterTitle"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:textColor="#666666"
        android:textSize="14sp"
        android:layout_marginStart="8dp" />

</LinearLayout>
```

- [ ] **Step 3: 创建 item_article.xml 布局**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:padding="12dp"
    android:paddingStart="48dp"
    android:gravity="center_vertical">

    <TextView
        android:id="@+id/tvArticleNo"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:textColor="#999999"
        android:textSize="13sp" />

    <TextView
        android:id="@+id/tvArticleContent"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:textColor="#666666"
        android:textSize="13sp"
        android:maxLines="2"
        android:ellipsize="end"
        android:layout_marginStart="8dp" />

</LinearLayout>
```

- [ ] **Step 4: 修改 RegulationDetailActivity.kt - 实现树状逻辑**

```kotlin
class RegulationDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegulationDetailBinding
    private lateinit var adapter: ChapterTreeAdapter
    private lateinit var repository: LawRepository

    private val expandedChapters = mutableSetOf<Long>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegulationDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val regulationId = intent.getLongExtra("regulation_id", 0)

        setupRecyclerView()
        loadData(regulationId)
    }

    private fun setupRecyclerView() {
        adapter = ChapterTreeAdapter(
            onChapterClick = { chapter ->
                if (expandedChapters.contains(chapter.chapterId)) {
                    expandedChapters.remove(chapter.chapterId)
                } else {
                    expandedChapters.add(chapter.chapterId)
                }
                // 重新构建列表（不重新查询数据库）
                rebuildTreeItems()
            },
            onArticleClick = { article ->
                // 跳转到条款详情
                val bundle = Bundle().apply {
                    putLong("article_id", article.articleId)
                }
                TheRouter.build(Constant.articleDetailRoute).with(bundle).navigation()
            }
        )
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter
    }

    private var chaptersCache: List<RegulationChapterEntity> = emptyList()
    private var articlesCache: List<RegulationArticleEntity> = emptyList()

    private fun loadData(regulationId: Long) {
        lifecycleScope.launch {
            // 并行加载章节和条款
            val chaptersDeferred = async { repository.getChaptersByRegulationId(regulationId).first() }
            val articlesDeferred = async { repository.getArticlesByRegulationId(regulationId).first() }

            chaptersCache = chaptersDeferred.await()
            articlesCache = articlesDeferred.await()

            rebuildTreeItems()
        }
    }

    private fun rebuildTreeItems() {
        val treeItems = mutableListOf<ChapterTreeItem>()

        for (chapter in chaptersCache) {
            val chapterItem = ChapterTreeItem.Chapter(
                chapterId = chapter.chapterId,
                chapterNo = chapter.chapterNo,
                chapterTitle = chapter.chapterTitle,
                hasArticles = articlesCache.any { it.chapterId == chapter.chapterId },
                isExpanded = expandedChapters.contains(chapter.chapterId)
            )
            treeItems.add(chapterItem)

            // 如果展开，显示条款
            if (expandedChapters.contains(chapter.chapterId)) {
                val chapterArticles = articlesCache.filter { it.chapterId == chapter.chapterId }
                for (article in chapterArticles) {
                    treeItems.add(
                        ChapterTreeItem.Article(
                            articleId = article.articleId,
                            chapterId = article.chapterId,
                            articleNo = article.articleNo,
                            content = article.content
                        )
                    )
                }
            }
        }

        adapter.submitList(treeItems)
    }
}
```

**注意：** 使用 `first()` 替代 `collectLatest` 在循环中，正确获取一次性数据后再构建树状列表。

- [ ] **Step 5: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/regulation/ChapterTreeAdapter.kt
git add Ruoyi-Android-App/app/src/main/res/layout/item_chapter.xml
git add Ruoyi-Android-App/app/src/main/res/layout/item_article.xml
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/regulation/RegulationDetailActivity.kt
git commit -m "feat(android): 实现法规详情页章节条款树状结构"
```

---

## 阶段七：移动端 - 定性依据复制功能

### Task 11: 定性依据详情页添加复制功能

**Files:**
- Modify: `Ruoyi-Android-App/app/src/main/res/layout/activity_legal_basis_detail.xml`
- Modify: `Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/basis/LegalBasisDetailActivity.kt`

- [ ] **Step 1: 修改 activity_legal_basis_detail.xml - 在条款内容区域添加复制按钮**

在条款内容 TextView 后添加复制按钮：

```xml
<!-- 条款内容 -->
<TextView
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"
    android:text="条款内容"
    android:textSize="15sp"
    android:textColor="@color/black"
    android:textStyle="bold"/>

<LinearLayout
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    android:orientation="horizontal"
    android:gravity="center_vertical"
    android:layout_marginTop="8dp">

    <TextView
        android:id="@+id/tv_clauses"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_weight="1"
        android:textSize="14sp"
        android:textColor="@color/color_6"
        android:lineSpacingExtra="4dp"/>

    <ImageButton
        android:id="@+id/btn_copy_clauses"
        android:layout_width="40dp"
        android:layout_height="40dp"
        android:src="@android:drawable/ic_menu_share"
        android:background="?attr/selectableItemBackgroundBorderless"
        android:contentDescription="复制条款内容"/>
</LinearLayout>
```

- [ ] **Step 2: 修改 LegalBasisDetailActivity.kt - 添加复制逻辑**

```kotlin
class LegalBasisDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLegalBasisDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLegalBasisDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val basisId = intent.getLongExtra("basis_id", 0)
        loadData(basisId)
        setupCopyButton()
    }

    private fun setupCopyButton() {
        binding.btnCopyClauses.setOnClickListener {
            val clauses = binding.tvClauses.text.toString()
            copyToClipboard(clauses)
            Toast.makeText(this, "条款内容已复制", Toast.LENGTH_SHORT).show()
        }
    }

    private fun copyToClipboard(text: String) {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipboardData.newPlainText("条款内容", text)
        clipboard.setPrimaryClip(clip)
    }
}
```

- [ ] **Step 3: 提交**

```bash
git add Ruoyi-Android-App/app/src/main/res/layout/activity_legal_basis_detail.xml
git add Ruoyi-Android-App/app/src/main/java/com/ruoyi/app/feature/law/ui/basis/LegalBasisDetailActivity.kt
git commit -m "feat(android): 定性依据详情页添加条款复制功能"
```

---

## 阶段八：前端菜单配置

### Task 12: 添加前端菜单

**Files:**
- Create: `RuoYi-Vue/sql/V1.1.7__regulation_menu.sql`

- [ ] **Step 1: 创建菜单SQL脚本**

```sql
-- ============================================
-- 脚本：V1.1.7__regulation_menu.sql
-- 版本：1.1.7
-- 日期：2026-04-26
-- 描述：添加法律法规批量操作菜单
-- ============================================

-- 批量操作菜单（按钮级权限）
-- menu_name: 批量操作, order_num: 6, path: /system/regulation/batch
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('批量操作', (SELECT menu_id FROM sys_menu WHERE menu_name = '法律法规管理'), 6, '/system/regulation/batch', 'system/regulation/batch/index', 1, 0, 'F', '0', '0', '', 'btn', 'admin', NOW(), '批量操作菜单');

-- 获取刚插入的批量操作菜单ID
SET @parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '批量操作' AND parent_id = (SELECT menu_id FROM sys_menu WHERE menu_name = '法律法规管理'));

-- 导入Excel
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('导入Excel', @parent_id, 1, '', '', 1, 0, 'F', '0', '0', 'system:regulation:import', '#', 'admin', NOW(), '导入Excel权限');

-- 导出Excel
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('导出Excel', @parent_id, 2, '', '', 1, 0, 'F', '0', '0', 'system:regulation:export', '#', 'admin', NOW(), '导出Excel权限');

-- 导入JSON
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('导入JSON', @parent_id, 3, '', '', 1, 0, 'F', '0', '0', 'system:regulation:importJson', '#', 'admin', NOW(), '导入JSON权限');

-- 导出JSON
INSERT INTO sys_menu (menu_name, parent_id, order_num, path, component, is_frame, is_cache, menu_type, visible, status, perms, icon, create_by, create_time, remark)
VALUES ('导出JSON', @parent_id, 4, '', '', 1, 0, 'F', '0', '0', 'system:regulation:exportJson', '#', 'admin', NOW(), '导出JSON权限');
```

- [ ] **Step 2: 提交**

```bash
git add RuoYi-Vue/sql/V1.1.7__regulation_menu.sql
git commit -m "docs: 添加法律法规批量操作菜单SQL"
```

---

## 验证清单

完成所有任务后，验证以下功能：

| 功能 | 验证方法 |
|------|----------|
| 章节分页 | 章节列表切换页码正常显示 |
| 条款分页 | 条款列表按章节筛选+分页正常 |
| Excel导入 | 上传Excel文件，数据正确入库 |
| Excel导出 | 下载Excel文件，内容完整 |
| JSON导入 | 上传JSON文件，数据正确入库 |
| JSON导出 | 下载JSON文件，章节条款结构完整 |
| 章节管理入口 | 法规详情页点击跳转章节管理 |
| 全量同步 | 首次打开App，数据全部下载 |
| 增量同步 | 更新服务器数据，App刷新后同步 |
| 树状图 | 法规详情页展开/折叠章节正常 |
| 条款复制 | 点击复制按钮，剪贴板内容正确 |
| 移除收藏 | 代码中无收藏相关逻辑 |

---

**Plan complete.** 两个执行选项：

**1. Subagent-Driven (recommended)** - 调度独立子agent逐任务执行，任务间审查

**2. Inline Execution** - 在当前会话中使用 executing-plans 批量执行，设置审查检查点

选择哪个方式？