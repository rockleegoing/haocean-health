# 设备管理前端页面完善实施计划

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 完善设备与激活码管理的前端 Web 页面，修复 API 端点匹配问题，添加动态路由菜单配置，使其可以与后端 API 正常交互。

**Architecture:** 采用若依框架标准的动态路由模式，通过数据库 `sys_menu` 表配置菜单权限，前端页面组件已存在，需要修复 API 端点以匹配后端 Controller。

**Tech Stack:** Vue 2.6 + Element UI + 若依框架 3.9.2 + Axios

---

### Task 1: 创建数据库菜单配置 SQL 脚本

**Files:**
- Create: `RuoYi-Vue/sql/V1.0.1__device_menu.sql`

- [ ] **Step 1: 创建 SQL 脚本文件**

```sql
-- ============================================================
-- 设备管理模块 - 菜单配置
-- 版本：V1.0.1
-- 说明：添加设备管理和激活码管理的菜单权限
-- ============================================================

-- 1. 添加设备管理父菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_time, update_time)
VALUES ('设备管理', 0, 3, '/device', 'M', '0', 'device:menu', 'component', NOW(), NOW());

-- 2. 获取父菜单 ID
SET @parentId = LAST_INSERT_ID();

-- 3. 添加激活码管理子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_time, update_time)
VALUES ('激活码管理', @parentId, 1, '/device/activation-code', 'C', '0', 'device:activationCode:list', '#', NOW(), NOW());

-- 4. 添加设备管理子菜单
INSERT INTO sys_menu (menu_name, parent_id, order_num, url, menu_type, visible, perms, icon, create_time, update_time)
VALUES ('设备管理', @parentId, 2, '/device/device', 'C', '0', 'device:device:list', '#', NOW(), NOW());
```

- [ ] **Step 2: 执行 SQL 脚本**

```bash
# 进入项目目录
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue

# 执行菜单配置 SQL（使用数据库 ry-vue）
mysql -u root -p -h 127.0.0.1 ry-vue < sql/V1.0.1__device_menu.sql
```

预期输出：`Query OK, X rows affected`

- [ ] **Step 3: 验证菜单数据**

```sql
-- 登录 MySQL
mysql -u root -p -h 127.0.0.1

-- 查询菜单
USE ry-vue;
SELECT menu_name, url, perms FROM sys_menu WHERE url LIKE '/device%' ORDER BY parent_id, order_num;
```

预期输出：显示 3 条记录（设备管理父菜单 + 2 个子菜单）

- [ ] **Step 4: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git add RuoYi-Vue/sql/V1.0.1__device_menu.sql
git commit -m "feat(sql): 添加设备管理模块菜单配置"
```

---

### Task 2: 修复激活码管理 API 端点

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/api/device/activationCode.js`

- [ ] **Step 1: 读取当前文件**

```bash
cat RuoYi-Vue/ruoyi-ui/src/api/device/activationCode.js
```

- [ ] **Step 2: 修改列表查询方法 (GET → POST)**

修改前：
```javascript
// 查询激活码列表
export function listActivationCode(query) {
  return request({
    url: '/device/activationCode/list',
    method: 'get',
    params: query
  })
}
```

修改后：
```javascript
// 查询激活码列表
export function listActivationCode(query) {
  return request({
    url: '/device/activationCode/list',
    method: 'post',
    params: query
  })
}
```

- [ ] **Step 3: 修改生成激活码方法 (/generate → /batchGenerate)**

修改前：
```javascript
// 生成激活码
export function generateActivationCode(data) {
  return request({
    url: '/device/activationCode/generate',
    method: 'post',
    data: data
  })
}
```

修改后：
```javascript
// 批量生成激活码
export function generateActivationCode(data) {
  return request({
    url: '/device/activationCode/batchGenerate',
    method: 'post',
    data: data
  })
}
```

- [ ] **Step 4: 修改批量删除方法 (/batch → /{codeIds})**

修改前：
```javascript
// 批量删除激活码
export function batchDelActivationCode(ids) {
  return request({
    url: '/device/activationCode/batch',
    method: 'delete',
    data: ids
  })
}
```

修改后：
```javascript
// 批量删除激活码
export function batchDelActivationCode(ids) {
  return request({
    url: '/device/activationCode/' + ids,
    method: 'delete'
  })
}
```

- [ ] **Step 5: 验证文件内容**

```bash
cat RuoYi-Vue/ruoyi-ui/src/api/device/activationCode.js
```

确认修改后的完整内容：
```javascript
import request from '@/utils/request'

// 查询激活码列表
export function listActivationCode(query) {
  return request({
    url: '/device/activationCode/list',
    method: 'post',
    params: query
  })
}

// 查询激活码详细
export function getActivationCode(id) {
  return request({
    url: '/device/activationCode/' + id,
    method: 'get'
  })
}

// 批量生成激活码
export function generateActivationCode(data) {
  return request({
    url: '/device/activationCode/batchGenerate',
    method: 'post',
    data: data
  })
}

// 删除激活码
export function delActivationCode(id) {
  return request({
    url: '/device/activationCode/' + id,
    method: 'delete'
  })
}

// 批量删除激活码
export function batchDelActivationCode(ids) {
  return request({
    url: '/device/activationCode/' + ids,
    method: 'delete'
  })
}
```

- [ ] **Step 6: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git add RuoYi-Vue/ruoyi-ui/src/api/device/activationCode.js
git commit -m "fix(api): 修复激活码管理 API 端点以匹配后端"
```

---

### Task 3: 修复设备管理 API 端点

**Files:**
- Modify: `RuoYi-Vue/ruoyi-ui/src/api/device/device.js`

- [ ] **Step 1: 读取当前文件**

```bash
cat RuoYi-Vue/ruoyi-ui/src/api/device/device.js
```

- [ ] **Step 2: 修改列表查询方法 (GET → POST)**

修改前：
```javascript
// 查询设备列表
export function listDevice(query) {
  return request({
    url: '/device/device/list',
    method: 'get',
    params: query
  })
}
```

修改后：
```javascript
// 查询设备列表
export function listDevice(query) {
  return request({
    url: '/device/device/list',
    method: 'post',
    params: query
  })
}
```

- [ ] **Step 3: 修改解绑设备方法 (/{id} → body 传参)**

修改前：
```javascript
// 解绑设备
export function unbindDevice(id) {
  return request({
    url: '/device/device/unbind/' + id,
    method: 'put'
  })
}
```

修改后：
```javascript
// 解绑设备
export function unbindDevice(deviceId) {
  return request({
    url: '/device/device/unbind',
    method: 'put',
    data: { deviceId: deviceId }
  })
}
```

- [ ] **Step 4: 验证文件内容**

```bash
cat RuoYi-Vue/ruoyi-ui/src/api/device/device.js
```

确认修改后的完整内容：
```javascript
import request from '@/utils/request'

// 查询设备列表
export function listDevice(query) {
  return request({
    url: '/device/device/list',
    method: 'post',
    params: query
  })
}

// 查询设备详细
export function getDevice(id) {
  return request({
    url: '/device/device/' + id,
    method: 'get'
  })
}

// 解绑设备
export function unbindDevice(deviceId) {
  return request({
    url: '/device/device/unbind',
    method: 'put',
    data: { deviceId: deviceId }
  })
}

// 删除设备
export function delDevice(id) {
  return request({
    url: '/device/device/' + id,
    method: 'delete'
  })
}

// 批量删除设备
export function batchDelDevice(ids) {
  return request({
    url: '/device/device/' + ids,
    method: 'delete'
  })
}
```

- [ ] **Step 5: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git add RuoYi-Vue/ruoyi-ui/src/api/device/device.js
git commit -m "fix(api): 修复设备管理 API 端点以匹配后端"
```

---

### Task 4: 创建前端部署配置说明文档

**Files:**
- Create: `docs/device-frontend-setup.md`

- [ ] **Step 1: 创建部署说明文档**

```markdown
# 设备管理前端页面部署配置

**文档版本**: v1.0
**创建日期**: 2026-04-22

---

## 一、前置条件

1. 后端服务已启动并运行在 http://localhost:8080
2. 数据库菜单配置已执行 (V1.0.1__device_menu.sql)
3. Node.js 16+ 已安装

---

## 二、部署步骤

### 2.1 执行数据库菜单配置

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue
mysql -u root -p -h 127.0.0.1 ry-vue < sql/V1.0.1__device_menu.sql
```

### 2.2 启动后端服务

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue
mvn spring-boot:run -pl ruoyi-admin
```

### 2.3 启动前端开发服务器

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue/ruoyi-ui

# 安装依赖（如果未安装）
npm install --registry=https://registry.npmmirror.com

# 启动开发服务器
npm run dev
```

访问地址：http://localhost:80

---

## 三、功能验证

### 3.1 登录测试

- 访问：http://localhost:80
- 账号：`admin`
- 密码：`admin123`

### 3.2 菜单验证

登录后，侧边栏应显示：
- 设备管理（父菜单）
  - 激活码管理
  - 设备管理

### 3.3 功能测试

**激活码管理页面测试：**
1. 页面加载 → 显示激活码列表
2. 点击"生成"按钮 → 弹出生成对话框
3. 输入数量 1，有效期 7 天 → 点击"确定"
4. 预期：成功生成 1 个激活码，显示结果

**设备管理页面测试：**
1. 页面加载 → 显示设备列表
2. 搜索设备 → 按条件筛选成功

---

## 四、常见问题

### 问题 1：侧边栏不显示设备管理菜单

**解决方案：**
1. 检查 SQL 是否执行成功
2. 退出重新登录刷新权限
3. 检查后端服务是否运行

### 问题 2：API 请求失败

**解决方案：**
1. 检查后端服务是否启动
2. 检查代理配置 `ruoyi-ui/vue.config.js`
3. 查看浏览器控制台错误信息

### 问题 3：权限不足错误

**解决方案：**
1. 确保使用 admin 账号登录
2. 检查菜单 perms 字段是否正确
```

- [ ] **Step 2: 提交**

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4
git add docs/device-frontend-setup.md
git commit -m "docs: 添加设备管理前端页面部署配置说明"
```

---

### Task 5: 功能测试验证

**Files:**
- Test: 手动功能测试

- [ ] **Step 1: 确保服务运行**

```bash
# 检查后端服务
curl -s http://localhost:8080/admin | grep -q "ruoyi" && echo "后端服务正常" || echo "后端服务异常"

# 检查前端服务
curl -s http://localhost:80 | grep -q "html" && echo "前端服务正常" || echo "前端服务异常"
```

- [ ] **Step 2: 登录测试**

```bash
# 使用默认管理员账号登录
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "code": "",
    "uuid": ""
  }' | grep '"code":200' && echo "登录成功" || echo "登录失败"
```

- [ ] **Step 3: 测试激活码列表 API**

```bash
# 使用上一步获取的 token 测试列表查询
TOKEN="<从上一步获取的 token>"
curl -X POST http://localhost:8080/device/activationCode/list \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"pageNum": 1, "pageSize": 10}' | grep '"code":200' && echo "列表查询成功" || echo "列表查询失败"
```

- [ ] **Step 4: 测试生成激活码 API**

```bash
curl -X POST http://localhost:8080/device/activationCode/batchGenerate \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{"count": 1, "expireDays": 7, "remark": "测试"}' | grep '"code":200' && echo "生成成功" || echo "生成失败"
```

- [ ] **Step 5: 记录测试结果**

如果所有测试通过，记录测试结果：
```bash
echo "测试结果: 全部通过"
```

---

## 验证

执行 `./scripts/test-device-auth-api.sh` 后应输出：
- API 服务检查通过
- 所有 API 测试用例通过
- 测试报告已生成

---
