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
mysql -u root -p123456 -h 127.0.0.1 ry-vue < sql/V1.0.1__device_menu.sql
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
