# 执法终端系统投标需求追溯矩阵

**版本**: v1.0
**日期**: 2026-04-22
**状态**: 投标响应技术规格书

本文档将 15 项投标硬性要求映射到具体实现，确保每条需求可追溯、可验证、可测试。

---

## 需求追溯总表

| 序号 | 需求描述 | 状态 | 规则文件 | 代码模块 | 数据库表 | API 端点 | 测试用例 |
|------|---------|------|---------|---------|---------|---------|---------|
| 1 | 卫生监督管理云平台集成 | ✅ | sync-conflict.md | backend/sync/ | t_sync_queue | /api/sync/* | SyncTest |
| 2 | 37+ 标准文书模板 | ✅ | document-template.md | feature/document/ | t_document_template | /api/document/* | DocumentTest |
| 3 | 本地模板自定义 | ✅ | document-template.md | feature/template/ | t_template_variable | /api/template/* | TemplateTest |
| 4 | A4 打印支持 | ✅ | mobile-android.md | hardware/print/ | - | BluetoothManager | PrintTest |
| 5 | 电子印章与模板打印 | ✅ | document-template.md | hardware/print/ | t_electronic_seal | /api/seal/* | SealPrintTest |
| 6 | 实时数据同步 | ✅ | sync-conflict.md | data/sync/ | t_sync_queue | /api/sync/realtime | SyncRealtimeTest |
| 7 | 标准用语分解 | ✅ | mobile-android.md | feature/knowledge/phrase/ | t_phrase_item | /api/phrase/* | PhraseTest |
| 8 | 自动生成文书套组 | ✅ | document-template.md | feature/document/ | t_document_set | /api/document/set | DocumentSetTest |
| 9 | 自动匹配法律条款 | ✅ | mobile-android.md | feature/lawenforcement/ | t_law_clause | /api/law/match | LawMatchTest |
| 10 | 自定义模板支持 | ✅ | document-template.md | feature/template/ | t_custom_template | /api/template/custom | CustomTemplateTest |
| 11 | 音视频证据采集 | ✅ | mobile-android.md | feature/media/ | t_media_evidence | /api/media/* | MediaTest |
| 12 | 照片时间水印 | ✅ | mobile-android.md | feature/media/ | - | WatermarkUtil | WatermarkTest |
| 13 | 300+ 法律法规库 | ✅ | database.md | backend/law/ | t_law_book | /api/law/* | LawTest |
| 14 | 地方法规支持 | ✅ | database.md | backend/law/ | t_local_regulation | /api/regulation/local | LocalRegulationTest |
| 15 | OTA 在线升级 | ⏳ | ota-updates.md | feature/update/ | t_app_version | /api/app/update | OTATest |

---

## 详细需求映射

### 需求 1：卫生监督管理云平台集成

**需求原文**：系统应集成到省级卫生监督管理云平台，实现数据互通。

**实现方案**：
- **规则文件**：`rules/sync-conflict.md` - 数据同步与冲突处理规范
- **代码模块**：
  - `Ruoyi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/mapper/SyncMapper.java`
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/data/repository/SyncRepository.kt`
- **数据库表**：
  ```sql
  -- 同步队列表（Android 本地）
  CREATE TABLE t_sync_queue (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      table_name VARCHAR(64) NOT NULL,
      record_id BIGINT NOT NULL,
      sync_type VARCHAR(16) NOT NULL,  -- CREATE/UPDATE/DELETE
      sync_status VARCHAR(16) DEFAULT 'PENDING',
      retry_count INT DEFAULT 0,
      request_data TEXT,
      create_time INTEGER DEFAULT (strftime('%s', 'now') * 1000)
  );

  -- 平台对接表（后端）
  CREATE TABLE sys_platform_sync (
      sync_id BIGINT PRIMARY KEY,
      platform_code VARCHAR(32),
      sync_time DATETIME,
      sync_status VARCHAR(16),
      error_message TEXT
  );
  ```
- **API 端点**：
  - `POST /api/sync/upload` - 上传本地数据到平台
  - `POST /api/sync/download` - 从平台下载数据
  - `GET /api/sync/status` - 查询同步状态
- **测试用例**：
  - `SyncTest.testSyncToPlatform()` - 验证数据上报
  - `SyncTest.testDownloadFromPlatform()` - 验证数据下载
  - `SyncTest.testSyncConflict()` - 验证冲突处理

**验证方法**：在测试环境配置平台地址，执行同步操作，验证数据一致性。

---

### 需求 2：37+ 标准文书模板

**需求原文**：系统应包含 37 种标准卫生执法文书模板。

**37 种标准文书清单**：

| 序号 | 文书名称 | 模板编码 | 适用场景 |
|------|---------|---------|---------|
| 1 | 现场笔录 | WS001 | 现场检查记录 |
| 2 | 询问笔录 | WS002 | 当事人询问 |
| 3 | 监督意见书 | WS003 | 监督建议 |
| 4 | 责令改正通知书 | WS004 | 责令整改 |
| 5 | 卫生监督决定书 | WS005 | 行政决定 |
| 6 | 先行登记保存决定书 | WS006 | 证据保全 |
| 7 | 证据登记保存清单 | WS007 | 保全物品清单 |
| 8 | 解除证据登记保存决定书 | WS008 | 解除保全 |
| 9 | 采样记录 | WS009 | 样品采集 |
| 10 | 产品样品采集记录 | WS010 | 产品采样 |
| 11 | 非产品样品采集记录 | WS011 | 环境采样 |
| 12 | 卫生行政执法文书送达回证 | WS012 | 文书送达 |
| 13 | 行政处罚事先告知书 | WS013 | 处罚告知 |
| 14 | 行政处罚听证告知书 | WS014 | 听证告知 |
| 15 | 行政处罚决定书 | WS015 | 处罚决定 |
| 16 | 当场行政处罚决定书 | WS016 | 简易程序 |
| 17 | 责令立即改正通知书 | WS017 | 立即整改 |
| 18 | 责令限期改正通知书 | WS018 | 限期整改 |
| 19 | 暂停营业决定书 | WS019 | 暂停营业 |
| 20 | 取缔决定书 | WS020 | 取缔决定 |
| 21 | 查封（扣押）决定书 | WS021 | 行政强制 |
| 22 | 解除查封（扣押）决定书 | WS022 | 解除强制 |
| 23 | 案件移送书 | WS023 | 案件移送 |
| 24 | 案件来源登记表 | WS024 | 案件登记 |
| 25 | 立案审批表 | WS025 | 立案审批 |
| 26 | 案件调查终结报告 | WS026 | 调查终结 |
| 27 | 行政处罚合议记录 | WS027 | 合议记录 |
| 28 | 案件集体讨论记录 | WS028 | 集体讨论 |
| 29 | 行政处罚审批表 | WS029 | 处罚审批 |
| 30 | 结案报告 | WS030 | 案件结案 |
| 31 | 听证申请书 | WS031 | 听证申请 |
| 32 | 听证通知书 | WS032 | 听证通知 |
| 33 | 听证笔录 | WS033 | 听证记录 |
| 34 | 听证报告书 | WS034 | 听证报告 |
| 35 | 行政执法证 | WS035 | 执法证件 |
| 36 | 执法人员出勤表 | WS036 | 出勤记录 |
| 37 | 执法装备领用单 | WS037 | 装备领用 |

**实现方案**：
- **规则文件**：`rules/document-template.md` - 文书模板管理规范
- **代码模块**：
  - `Ruoyi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/DocumentTemplate.java`
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/feature/document/DocumentFillActivity.kt`
- **数据库表**：
  ```sql
  CREATE TABLE t_document_template (
      id BIGINT PRIMARY KEY AUTOINCREMENT,
      template_code VARCHAR(64) NOT NULL,  -- WS001, WS002...
      template_name VARCHAR(128) NOT NULL,
      template_type VARCHAR(32),           -- 现场笔录类/询问笔录类/行政强制类...
      category VARCHAR(32),                -- 分类
      file_path VARCHAR(256),
      file_url VARCHAR(256),
      version INT DEFAULT 1,
      variables TEXT,                      -- JSON 变量定义
      is_active CHAR(1) DEFAULT '1'
  );
  ```
- **API 端点**：
  - `GET /api/document/template/list` - 获取模板列表
  - `GET /api/document/template/download/{templateId}` - 下载模板
  - `POST /api/document/generate` - 生成文书
- **测试用例**：
  - `DocumentTest.testAll37TemplatesExist()` - 验证 37 种模板存在
  - `DocumentTest.testTemplateGenerate()` - 验证文书生成
  - `DocumentTest.testTemplateVariables()` - 验证变量替换

**验证方法**：在后台管理系统查看模板列表，确认 37 种模板全部存在且可用。

---

### 需求 3：本地模板自定义支持

**需求原文**：支持根据本地实际执法需求，自定义文书模板。

**实现方案**：
- **规则文件**：`rules/document-template.md` 第 4 节 模板编辑器规范
- **代码模块**：
  - `Ruoyi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/TemplateCustomService.java`
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/feature/template/TemplateEditorActivity.kt`
- **数据库表**：
  ```sql
  CREATE TABLE t_custom_template (
      id BIGINT PRIMARY KEY AUTOINCREMENT,
      template_name VARCHAR(128) NOT NULL,
      template_content TEXT,
      variables TEXT,                      -- JSON 变量定义
      create_by VARCHAR(64),
      create_time DATETIME,
      is_system INT DEFAULT 0,             -- 0=自定义，1=系统
      region_code VARCHAR(32)              -- 适用地区
  );
  ```
- **API 端点**：
  - `POST /api/template/custom/create` - 创建自定义模板
  - `PUT /api/template/custom/update` - 更新自定义模板
  - `DELETE /api/template/custom/delete` - 删除自定义模板
  - `GET /api/template/custom/list` - 获取自定义模板列表
- **测试用例**：
  - `TemplateTest.testCreateCustomTemplate()` - 验证创建
  - `TemplateTest.testEditCustomTemplate()` - 验证编辑
  - `TemplateTest.testDeleteCustomTemplate()` - 验证删除

**验证方法**：在后台模板编辑器中创建新模板，验证可保存、可使用。

---

### 需求 4：A4 打印支持

**需求原文**：支持 A4 纸张打印，满足执法文书归档要求。

**实现方案**：
- **规则文件**：`rules/mobile-android.md` 第 8 节 蓝牙打印规范
- **代码模块**：
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/hardware/print/BluetoothPrintService.kt`
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/hardware/print/A4PrintAdapter.kt`
- **打印规格**：
  ```kotlin
  // A4 纸张规格（单位：点）
  object A4PrintSpec {
      const val WIDTH_PX = 590      // A4 宽度（203dpi）
      const val HEIGHT_PX = 842     // A4 高度（203dpi）
      const val MARGIN_LEFT = 40    // 左边距
      const val MARGIN_RIGHT = 40   // 右边距
      const val MARGIN_TOP = 60     // 上边距
      const val MARGIN_BOTTOM = 60  // 下边距
  }
  ```
- **测试用例**：
  - `PrintTest.testA4PrintLayout()` - 验证 A4 布局
  - `PrintTest.testPrintQuality()` - 验证打印质量
  - `PrintTest.testPrintMultipleCopies()` - 验证多份打印

**验证方法**：连接蓝牙打印机，打印测试页，验证 A4 格式正确。

---

### 需求 5：电子印章与模板打印

**需求原文**：支持电子印章功能，可实现模板打印与电子印章合成。

**实现方案**：
- **规则文件**：`rules/document-template.md` 第 8 节 电子签名规范
- **代码模块**：
  - `Ruoyi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/SealService.java`
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/feature/seal/SealSynthesizer.kt`
- **数据库表**：
  ```sql
  CREATE TABLE t_electronic_seal (
      seal_id BIGINT PRIMARY KEY,
      seal_name VARCHAR(128) NOT NULL,     -- 印章名称
      seal_image BLOB,                      -- 印章图片
      seal_type VARCHAR(32),                -- 公章/个人章
      create_by VARCHAR(64),
      create_time DATETIME,
      is_active CHAR(1) DEFAULT '1'
  );
  ```
- **API 端点**：
  - `POST /api/seal/upload` - 上传印章图片
  - `POST /api/seal/synthesize` - 合成印章到 PDF
  - `GET /api/seal/list` - 获取印章列表
- **测试用例**：
  - `SealPrintTest.testSealUpload()` - 验证上传
  - `SealPrintTest.testSealSynthesize()` - 验证合成
  - `SealPrintTest.testSealPrintPosition()` - 验证位置

**验证方法**：上传印章图片，合成到文书，验证印章位置正确、清晰。

---

### 需求 6：实时数据同步

**需求原文**：支持实时数据同步，确保移动终端与后台数据一致。

**实现方案**：
- **规则文件**：`rules/sync-conflict.md` - 数据同步与冲突处理规范
- **代码模块**：
  - `Ruoyi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/RealtimeSyncService.java`
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/data/sync/RealtimeSyncWorker.kt`
- **数据库表**：
  ```sql
  CREATE TABLE t_sync_queue (
      id INTEGER PRIMARY KEY AUTOINCREMENT,
      table_name VARCHAR(64) NOT NULL,
      record_id BIGINT NOT NULL,
      sync_type VARCHAR(16) NOT NULL,
      sync_status VARCHAR(16) DEFAULT 'PENDING',
      priority INT DEFAULT 0,               -- 0=普通，1=紧急
      retry_count INT DEFAULT 0,
      next_retry_time INTEGER
  );

  -- 索引
  CREATE INDEX idx_sync_status ON t_sync_queue(sync_status);
  CREATE INDEX idx_sync_priority ON t_sync_queue(priority);
  ```
- **API 端点**：
  - `POST /api/sync/realtime/upload` - 实时上传
  - `POST /api/sync/realtime/download` - 实时下载
  - `GET /api/sync/queue/status` - 查询队列状态
- **测试用例**：
  - `SyncRealtimeTest.testRealtimeUpload()` - 验证实时上传
  - `SyncRealtimeTest.testRealtimeDownload()` - 验证实时下载
  - `SyncRealtimeTest.testSyncQueueRetry()` - 验证重试机制

**验证方法**：在无网络环境下创建数据，恢复网络后验证自动同步。

---

### 需求 7：标准用语分解

**需求原文**：支持将标准用语分解到具体执法环节，提供标准化指导。

**实现方案**：
- **规则文件**：`rules/mobile-android.md` 第 6 节 规范用语库规范
- **代码模块**：
  - `Ruoyi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/PhraseItem.java`
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/feature/knowledge/phrase/PhraseListFragment.kt`
- **数据库表**：
  ```sql
  CREATE TABLE t_phrase_item (
      phrase_id BIGINT PRIMARY KEY,
      phrase_content TEXT NOT NULL,        -- 用语内容
      phase_type VARCHAR(32),              -- 检查前/检查中/检查后
      scene_type VARCHAR(32),              -- 适用场景
      industry_code VARCHAR(32),           -- 适用行业
      sort_order INT DEFAULT 0
  );
  ```
- **API 端点**：
  - `GET /api/phrase/list` - 获取用语列表
  - `GET /api/phrase/by-phase` - 按环节筛选
  - `GET /api/phrase/by-scene` - 按场景筛选
- **测试用例**：
  - `PhraseTest.testPhraseList()` - 验证列表
  - `PhraseTest.testPhaseFilter()` - 验证环节筛选
  - `PhraseTest.testSceneFilter()` - 验证场景筛选

**验证方法**：选择不同执法环节，验证显示对应的标准用语。

---

### 需求 8：自动生成文书套组

**需求原文**：根据检查类型自动生成对应的文书套组，带文书编号。

**实现方案**：
- **规则文件**：`rules/document-template.md` 第 5 节 文书生成流程
- **代码模块**：
  - `Ruoyi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/DocumentSetService.java`
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/feature/document/DocumentSetGenerator.kt`
- **数据库表**：
  ```sql
  CREATE TABLE t_document_set (
      set_id BIGINT PRIMARY KEY,
      set_name VARCHAR(128),               -- 套组名称
      check_type VARCHAR(32),              -- 检查类型
      templates TEXT,                      -- JSON 模板列表
      numbering_rule VARCHAR(64),          -- 编号规则
      create_time DATETIME
  );

  CREATE TABLE t_document_numbering (
      number_id BIGINT PRIMARY KEY,
      template_code VARCHAR(32),
      current_number INT,                  -- 当前编号
      year VARCHAR(4),                     -- 年份
      prefix VARCHAR(16)                   -- 前缀
  );
  ```
- **API 端点**：
  - `POST /api/document/set/generate` - 生成文书套组
  - `GET /api/document/set/list` - 获取套组列表
  - `POST /api/document/numbering/get` - 获取文书编号
- **测试用例**：
  - `DocumentSetTest.testGenerateSet()` - 验证套组生成
  - `DocumentSetTest.testNumberingSequence()` - 验证编号连续性
  - `DocumentSetTest.testCheckTypeMapping()` - 验证检查类型映射

**验证方法**：选择检查类型，验证自动生成的文书套组包含正确模板，编号连续。

---

### 需求 9：法律条款自动匹配

**需求原文**：支持根据违法事实描述，自动匹配对应的法律法规条款。

**实现方案**：
- **规则文件**：`rules/mobile-android.md` 第 5 节 法律法规库规范
- **代码模块**：
  - `Ruoyi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/LawMatchService.java`
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/feature/lawenforcement/LawMatchHelper.kt`
- **数据库表**：
  ```sql
  CREATE TABLE t_law_clause (
      clause_id BIGINT PRIMARY KEY,
      law_code VARCHAR(32),                -- 法律编码
      clause_content TEXT,                 -- 条款内容
      violation_keywords TEXT,             -- 违法关键词（JSON）
      penalty_range VARCHAR(256),          -- 处罚幅度
      industry_code VARCHAR(32)            -- 适用行业
  );

  -- 全文索引
  CREATE FULLTEXT INDEX idx_clause_search ON t_law_clause(clause_content, violation_keywords);
  ```
- **API 端点**：
  - `POST /api/law/match` - 匹配法律条款
  - `GET /api/law/clause/detail` - 获取条款详情
  - `GET /api/law/penalty/calc` - 计算处罚幅度
- **测试用例**：
  - `LawMatchTest.testKeywordMatch()` - 验证关键词匹配
  - `LawMatchTest.testPenaltyCalc()` - 验证处罚计算
  - `LawMatchTest.testIndustryFilter()` - 验证行业筛选

**验证方法**：输入违法事实描述，验证推荐的法律条款准确。

---

### 需求 10：自定义模板支持

**需求原文**：支持自定义模板，满足特殊执法场景需求。

**实现方案**：
- **规则文件**：`rules/document-template.md` 第 3 节 模板数据结构
- **代码模块**：
  - `Ruoyi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/controller/CustomTemplateController.java`
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/feature/template/CustomTemplateEditor.kt`
- **数据库表**：同需求 3
- **API 端点**：同需求 3
- **测试用例**：
  - `CustomTemplateTest.testTemplateCreate()` - 验证创建
  - `CustomTemplateTest.testTemplateEdit()` - 验证编辑
  - `CustomTemplateTest.testTemplateUse()` - 验证使用

**验证方法**：创建自定义模板，在执法场景中使用，验证功能完整。

---

### 需求 11：音视频证据采集

**需求原文**：支持拍照、录音、录像等多种证据采集方式。

**实现方案**：
- **规则文件**：`rules/mobile-android.md` 第 7 节 媒体采集规范
- **代码模块**：
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/feature/media/MediaCaptureActivity.kt`
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/feature/media/MediaRepository.kt`
- **数据库表**：
  ```sql
  CREATE TABLE t_media_evidence (
      media_id BIGINT PRIMARY KEY,
      law_record_id BIGINT,                -- 关联执法记录
      media_type VARCHAR(16),              -- PHOTO/AUDIO/VIDEO
      file_path VARCHAR(256),
      file_size BIGINT,
      duration INT,                        -- 时长（秒）
      capture_time DATETIME,
      location_lat DECIMAL(10,8),          -- 纬度
      location_lng DECIMAL(11,8),          -- 经度
      description TEXT
  );
  ```
- **测试用例**：
  - `MediaTest.testPhotoCapture()` - 验证拍照
  - `MediaTest.testAudioRecord()` - 验证录音
  - `MediaTest.testVideoRecord()` - 验证录像
  - `MediaTest.testMediaUpload()` - 验证上传

**验证方法**：分别测试拍照、录音、录像功能，验证文件保存和上传。

---

### 需求 12：照片时间水印

**需求原文**：打印照片时应自动添加时间戳水印。

**实现方案**：
- **规则文件**：`rules/mobile-android.md` 第 7.3 节水印处理
- **代码模块**：
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/feature/media/WatermarkUtil.kt`
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/feature/print/PhotoPrintHelper.kt`
- **工具类**：
  ```kotlin
  object WatermarkUtil {
      fun addWatermark(
          bitmap: Bitmap,
          timestamp: Long,
          location: Location? = null
      ): Bitmap {
          // 添加时间戳水印
          // 可选添加经纬度水印
      }
  }
  ```
- **测试用例**：
  - `WatermarkTest.testTimestampFormat()` - 验证时间格式
  - `WatermarkTest.testWatermarkPosition()` - 验证水印位置
  - `WatermarkTest.testWatermarkClarity()` - 验证水印清晰度

**验证方法**：拍摄照片后打印，验证水印包含正确时间和地点信息。

---

### 需求 13：300+ 法律法规库

**需求原文**：内置 300+ 部法律法规，并支持后台更新。

**实现方案**：
- **规则文件**：`rules/database.md` 第 6 节 本地缓存表设计
- **代码模块**：
  - `Ruoyi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/LawBook.java`
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/feature/knowledge/law/LawBookRepository.kt`
- **数据库表**：
  ```sql
  CREATE TABLE t_law_book (
      book_id BIGINT PRIMARY KEY,
      book_name VARCHAR(256) NOT NULL,     -- 法律名称
      law_type VARCHAR(32),                -- 法律/法规/规章/规范性文件
      law_code VARCHAR(32),                -- 发文文号
      publish_date DATE,                   -- 发布日期
      implement_date DATE,                 -- 实施日期
      content TEXT,                        -- 完整内容
      chapter_list TEXT,                   -- 章节列表（JSON）
      industry_code VARCHAR(32),           -- 适用行业
      sync_time INTEGER,
      version INT DEFAULT 1
  );

  CREATE TABLE t_law_chapter (
      chapter_id BIGINT PRIMARY KEY,
      book_id BIGINT,
      chapter_title VARCHAR(256),
      chapter_order INT,
      chapter_content TEXT,
      FOREIGN KEY (book_id) REFERENCES t_law_book(book_id)
  );
  ```
- **API 端点**：
  - `GET /api/law/book/list` - 获取法律列表
  - `GET /api/law/book/detail` - 获取法律详情
  - `GET /api/law/chapter/content` - 获取章节内容
  - `POST /api/admin/law/import` - 批量导入法律
- **测试用例**：
  - `LawTest.testLawBookCount()` - 验证数量 >= 300
  - `LawTest.testLawBookDownload()` - 验证下载
  - `LawTest.testLawBookUpdate()` - 验证更新

**验证方法**：在法律法规库中查看法律数量，验证不少于 300 部。

---

### 需求 14：地方法规支持

**需求原文**：支持地方性法规和规章的添加与使用。

**实现方案**：
- **规则文件**：`rules/database.md` 第 6.5 节 地方性法规扩展
- **代码模块**：
  - `Ruoyi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/domain/LocalRegulation.java`
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/feature/knowledge/regulation/LocalRegulationFragment.kt`
- **数据库表**：
  ```sql
  CREATE TABLE t_local_regulation (
      regulation_id BIGINT PRIMARY KEY,
      regulation_name VARCHAR(256) NOT NULL,
      region_code VARCHAR(32),             -- 行政区划代码
      region_name VARCHAR(128),            -- 地区名称
      regulation_type VARCHAR(32),         -- 地方法规/地方政府规章
      content TEXT,
      publish_date DATE,
      implement_date DATE,
      is_active CHAR(1) DEFAULT '1'
  );
  ```
- **API 端点**：
  - `GET /api/regulation/local/list` - 获取地方法规列表
  - `POST /api/admin/regulation/local/add` - 添加地方法规
  - `PUT /api/admin/regulation/local/update` - 更新地方法规
- **测试用例**：
  - `LocalRegulationTest.testAddLocalRegulation()` - 验证添加
  - `LocalRegulationTest.testRegionFilter()` - 验证地区筛选
  - `LocalRegulationTest.testLocalRegulationUse()` - 验证使用

**验证方法**：添加地方法规，在执法中引用，验证功能正常。

---

### 需求 15：OTA 在线升级

**需求原文**：支持系统升级更新功能，可通过在线升级获取最新版本。

**实现方案**：
- **规则文件**：`rules/ota-updates.md`（待创建）
- **代码模块**：
  - `Ruoyi-Vue/ruoyi-system/src/main/java/com/ruoyi/system/service/AppUpdateService.java`
  - `Ruoyi-Android-App/app/src/main/java/com/ruoyi/android/feature/update/OtaUpdateManager.kt`
- **数据库表**：
  ```sql
  CREATE TABLE t_app_version (
      version_id BIGINT PRIMARY KEY,
      version_code INT NOT NULL,           -- 版本号（整数）
      version_name VARCHAR(32),            -- 版本名称（如 1.2.3）
      platform VARCHAR(16),                -- android/web
      download_url VARCHAR(512),           -- 下载地址
      release_notes TEXT,                  -- 更新说明
      min_sdk INT,                         -- 最低 SDK 要求
      is_force INT DEFAULT 0,              -- 是否强制更新
      publish_time DATETIME,
      is_active CHAR(1) DEFAULT '1'
  );
  ```
- **API 端点**：
  - `GET /api/app/update/check` - 检查更新
  - `GET /api/app/version/latest` - 获取最新版本
  - `POST /api/app/upload` - 上传安装包
- **测试用例**：
  - `OTATest.testCheckUpdate()` - 验证检查更新
  - `OTATest.testDownloadApk()` - 验证下载
  - `OTATest.testForceUpdate()` - 验证强制更新

**验证方法**：发布新版本，验证 App 可检测并下载更新。

---

## 验证清单

### 功能验证

| 序号 | 验证项 | 验证方法 | 预期结果 | 状态 |
|------|-------|---------|---------|------|
| 1 | 平台集成 | 配置平台地址，执行同步 | 数据一致 | ⏳ |
| 2 | 37+ 模板 | 后台查看模板列表 | >= 37 种 | ⏳ |
| 3 | 自定义模板 | 创建并使用自定义模板 | 功能完整 | ⏳ |
| 4 | A4 打印 | 连接打印机打印测试页 | 格式正确 | ⏳ |
| 5 | 电子印章 | 上传印章并合成到文书 | 位置正确 | ⏳ |
| 6 | 实时同步 | 离线创建数据后同步 | 自动同步 | ⏳ |
| 7 | 标准用语 | 选择环节查看用语 | 显示正确 | ⏳ |
| 8 | 文书套组 | 选择检查类型生成套组 | 模板完整、编号连续 | ⏳ |
| 9 | 法律匹配 | 输入违法事实描述 | 推荐准确 | ⏳ |
| 10 | 自定义模板 | 创建并使用模板 | 功能完整 | ⏳ |
| 11 | 音视频采集 | 拍照/录音/录像 | 文件保存正常 | ⏳ |
| 12 | 照片水印 | 拍摄并打印照片 | 水印清晰 | ⏳ |
| 13 | 法律法规 | 查看法律库数量 | >= 300 部 | ⏳ |
| 14 | 地方法规 | 添加并使用地方法规 | 功能正常 | ⏳ |
| 15 | OTA 升级 | 发布新版本 | 可检测下载 | ⏳ |

### 性能验证

| 验证项 | 指标 | 目标值 | 实测值 | 状态 |
|-------|------|-------|-------|------|
| 同步速度 | 单次同步时间 | < 30 秒 | - | ⏳ |
| 法律检索 | 搜索响应时间 | < 1 秒 | - | ⏳ |
| 文书生成 | 生成时间 | < 5 秒 | - | ⏳ |
| 照片水印 | 处理时间 | < 2 秒 | - | ⏳ |
| OTA 下载 | 下载速度 | 根据网络 | - | ⏳ |

---

## 附录

### A. 术语表

| 术语 | 说明 |
|------|------|
| OTA | Over-The-Air，空中下载技术 |
| 文书套组 | 针对特定检查类型的一组关联文书 |
| 定性依据 | 违法行为定性的法律依据 |
| 同步队列 | 存储待同步数据变更的本地队列 |

### B. 参考文档

- [PRD 文档](../PRD.md)
- [Android 开发规范](../rules/mobile-android.md)
- [数据同步规范](../rules/sync-conflict.md)
- [文书模板规范](../rules/document-template.md)
- [数据库管理规范](../rules/database.md)

### C. 版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|------|------|---------|------|
| v1.0 | 2026-04-22 | 初始版本 | - |
