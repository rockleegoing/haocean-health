# 设备与认证模块完善 - 实施总览

**创建日期**: 2026-04-23
**状态**: 待实施

---

## 📋 计划文档清单

| 序号 | 计划名称 | 文件路径 | 优先级 |
|------|---------|---------|--------|
| 1 | 后端增强实施计划 | `docs/superpowers/plans/2026-04-23-device-auth-backend-plan.md` | P0 |
| 2 | Android 端存储完善计划 | `docs/superpowers/plans/2026-04-23-device-auth-android-plan.md` | P0 |
| 3 | 前端体验优化计划 | `docs/superpowers/plans/2026-04-23-device-auth-frontend-plan.md` | P1 |

---

## 🎯 实施顺序

```
第 1 步：后端增强实施计划 (P0)
         ↓
    执行数据库变更 SQL
    编译测试后端代码
    部署后端服务验证
         ↓
第 2 步：Android 端存储完善计划 (P0)
         ↓
    编译测试 Android 代码
    打包测试 APK
    真机测试激活流程
         ↓
第 3 步：前端体验优化计划 (P1)
         ↓
    编译测试前端代码
    部署前端验证
         ↓
第 4 步：联调测试与验收
```

---

## 📊 各计划任务统计

### 后端增强实施计划
| 任务数 | 预计工时 | 关键文件 |
|--------|---------|---------|
| 13 个任务 | 2-3 小时 | SysDeviceController.java, SysActivationCodeServiceImpl.java |

**关键验收点**:
- SQL 脚本执行成功
- 心跳 API `/device/device/heartbeat` 可用
- 远程清除 API `/device/device/remoteWipe` 可用
- 一码多设备配置生效

---

### Android 端存储完善计划
| 任务数 | 预计工时 | 关键文件 |
|--------|---------|---------|
| 5 个任务 | 1-2 小时 | ActivationRepository.kt, ActivationActivity.kt |

**关键验收点**:
- ActivationRepository 创建成功
- 激活信息成功写入 Room 数据库
- Android 项目编译成功

---

### 前端体验优化计划
| 任务数 | 预计工时 | 关键文件 |
|--------|---------|---------|
| 5 个任务 | 1 小时 | ExpireWarning.vue, device/index.vue |

**关键验收点**:
- ExpireWarning 组件正常显示
- 列表 30 秒定时刷新正常
- 前端编译成功

---

## 🔧 快速开始

### 开始后端实施
```bash
# 查看后端计划详情
cat docs/superpowers/plans/2026-04-23-device-auth-backend-plan.md

# 执行方式 1: 使用 subagent-driven-development
/execute-plan docs/superpowers/plans/2026-04-23-device-auth-backend-plan.md

# 执行方式 2: 手动按任务执行
# 按照计划文档中的 Task 1 → Task 13 顺序执行
```

### 开始 Android 端实施
```bash
# 查看 Android 计划详情
cat docs/superpowers/plans/2026-04-23-device-auth-android-plan.md
```

### 开始前端实施
```bash
# 查看前端计划详情
cat docs/superpowers/plans/2026-04-23-device-auth-frontend-plan.md
```

---

## ✅ 总体验收标准

### 功能验收
- [ ] 数据库表结构正确更新（新增 4 个字段）
- [ ] 设备心跳 API 正常工作
- [ ] 远程清除指令下发成功
- [ ] 一码多设备配置生效
- [ ] Android 端激活信息存入 Room 数据库
- [ ] 前端设备列表 30 秒自动刷新
- [ ] 激活码到期正确显示警告

### 代码质量验收
- [ ] 所有代码编译通过
- [ ] 无单元测试失败
- [ ] Git 提交记录清晰
- [ ] 代码风格符合规范

---

## 📝 依赖关系

```
后端增强 (P0)
    ├── 数据库变更 SQL → Android 端实施
    ├── 心跳 API → Android 端心跳上报
    └── 一码多设备字段 → 后端验证逻辑

Android 端 (P0)
    └── 依赖后端增强完成

前端体验优化 (P1)
    └── 可独立实施，无依赖
```

---

## ⚠️ 风险提示

1. **数据库变更风险**: 执行 SQL 前务必备份数据
2. **Android 端兼容性**: Room 数据库升级需测试旧版本数据迁移
3. **心跳机制性能**: 定时任务频率需根据实际设备数量调整
4. **前端定时器**: 注意页面销毁时清除定时器，避免内存泄漏

---

## 📖 相关文档

- 设计文档：`docs/superpowers/specs/2026-04-23-device-auth-enhancement-design.md`
- 后端计划：`docs/superpowers/plans/2026-04-23-device-auth-backend-plan.md`
- Android 计划：`docs/superpowers/plans/2026-04-23-device-auth-android-plan.md`
- 前端计划：`docs/superpowers/plans/2026-04-23-device-auth-frontend-plan.md`
