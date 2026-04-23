# 设备管理前端页面完善设计

**文档版本**: v1.0
**创建日期**: 2026-04-22
**状态**: 待实施

---

## 一、目标

完善设备与激活码管理的前端 Web 页面，使其可以与后端 API 正常交互，并通过移动卫生执法系统框架的动态权限系统访问。

---

## 二、背景

### 2.1 当前状态

**已有资源：**
- 后端 Controller：`SysActivationCodeController`、`SysDeviceController` 已完整实现
- 后端 Service：`ISysActivationCodeService`、`ISysDeviceService` 已实现
- 后端 Domain/Mapper：实体类和 Mapper 接口已创建
- 前端页面：`device/activation-code/index.vue`、`device/device/index.vue` 已存在
- 前端 API：`api/device/activationCode.js`、`api/device/device.js` 已存在
- 前端组件：`CountdownTimer.vue`（倒计时）、`CreateDialog.vue`（生成对话框）已存在

**需要解决的问题：**
1. 前端 API 端点与后端不匹配
2. 缺少动态路由菜单配置
3. 缺少部署配置说明文档

### 2.2 技术栈

- Vue 2.6 + Element UI
- 移动卫生执法系统框架 3.9.2
- Axios 请求封装

---

## 三、架构设计

### 3.1 动态路由模式

采用移动卫生执法系统框架标准的动态路由模式：

```
┌─────────────┐     ┌──────────────┐     ┌─────────────┐
│  用户登录   │ ──> │ 加载权限菜单 │ ──> │ 显示侧边栏  │
└─────────────┘     └──────────────┘     └─────────────┘
                             │
                             v
                    ┌─────────────────┐
                    │ sys_menu 表配置 │
                    └─────────────────┘
```

### 3.2 菜单结构

在 `sys_menu` 表中添加以下配置：

```
菜单 ID | 菜单名称    | 父 ID | 类型 | 路由地址              | 权限标识
--------|-----------|------|------|----------------------|---------------------------
100     | 设备管理   | 0    | 目录 | /device              | device:menu
101     | 激活码管理 | 100  | 菜单 | /device/activation-code | device:activationCode:list
102     | 设备管理   | 100  | 菜单 | /device/device       | device:device:list
```

### 3.3 权限配置

| 功能 | 权限标识 | 说明 |
|------|---------|------|
| 激活码列表 | `device:activationCode:list` | 查询激活码列表 |
| 激活码生成 | `device:activationCode:add` | 生成新激活码 |
| 激活码删除 | `device:activationCode:remove` | 删除激活码 |
| 激活码导出 | `device:activationCode:export` | 导出 Excel |
| 激活码查询 | `device:activationCode:query` | 查询单条详情 |
| 设备列表 | `device:device:list` | 查询设备列表 |
| 设备解绑 | `device:device:unbind` | 解绑设备 |

---

## 四、前端 API 修复

### 4.1 activationCode.js 修改

| 方法 | 当前配置 | 修改后 |
|------|---------|--------|
| 列表查询 | `GET /device/activationCode/list` | `POST /device/activationCode/list` |
| 生成激活码 | `POST /device/activationCode/generate` | `POST /device/activationCode/batchGenerate` |
| 批量删除 | `DELETE /device/activationCode/batch` | `DELETE /device/activationCode/{codeIds}` |

### 4.2 device.js 修改

| 方法 | 当前配置 | 修改后 |
|------|---------|--------|
| 列表查询 | `GET /device/device/list` | `POST /device/device/list` |
| 设备解绑 | `PUT /device/device/unbind/{id}` | `PUT /device/device/unbind` (body 传参) |

---

## 五、SQL 脚本设计

### 5.1 文件命名

`RuoYi-Vue/sql/V1.0.1__device_menu.sql`

### 5.2 插入语句

```sql
-- 设备管理父菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_time)
VALUES ('设备管理', 0, 3, '/device', 'M', '0', 'device:menu', 'component', NOW());

-- 获取父菜单 ID
SET @parentId = LAST_INSERT_ID();

-- 激活码管理子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_time)
VALUES ('激活码管理', @parentId, 1, '/device/activation-code', 'C', '0', 'device:activationCode:list', '#', NOW());

-- 设备管理子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_time)
VALUES ('设备管理', @parentId, 2, '/device/device', 'C', '0', 'device:device:list', '#', NOW());
```

---

## 六、部署流程

1. **执行 SQL 脚本** - 插入菜单配置
2. **修复前端 API** - 修改 `activationCode.js` 和 `device.js`
3. **重启后端服务** - 确保后端运行
4. **启动前端开发服务器** - `npm run dev`
5. **登录测试** - 使用 admin 账号登录
6. **功能验证** - 测试查询、生成、删除等操作

---

## 七、测试用例

### 7.1 激活码管理页面

| 编号 | 操作 | 预期结果 |
|------|------|---------|
| TC-01 | 页面加载 | 显示激活码列表 |
| TC-02 | 搜索激活码 | 按条件筛选成功 |
| TC-03 | 生成激活码 | 成功生成指定数量 |
| TC-04 | 复制激活码 | 复制到剪贴板成功 |
| TC-05 | 查看详情 | 显示完整信息 |
| TC-06 | 删除激活码 | 删除成功 |

### 7.2 设备管理页面

| 编号 | 操作 | 预期结果 |
|------|------|---------|
| TC-01 | 页面加载 | 显示设备列表 |
| TC-02 | 搜索设备 | 按条件筛选成功 |
| TC-03 | 设备解绑 | 解绑成功 |

---

## 八、关键文件清单

| 文件路径 | 操作 |
|---------|------|
| `RuoYi-Vue/ruoyi-ui/src/api/device/activationCode.js` | 修改 |
| `RuoYi-Vue/ruoyi-ui/src/api/device/device.js` | 修改 |
| `RuoYi-Vue/sql/V1.0.1__device_menu.sql` | 新增 |
| `docs/device-frontend-setup.md` | 新增 |

---

## 九、验收标准

1. ✅ 菜单 SQL 执行成功
2. ✅ 登录后侧边栏显示"设备管理"菜单
3. ✅ 激活码管理页面可正常访问
4. ✅ 设备管理页面可正常访问
5. ✅ 所有 CRUD 操作功能正常
6. ✅ 权限控制正常工作
