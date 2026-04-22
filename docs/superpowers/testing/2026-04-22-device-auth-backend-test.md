# 设备与认证模块 - 后端部署测试指南

**文档版本**: v1.2
**创建日期**: 2026-04-22
**更新日期**: 2026-04-22
**状态**: 待测试

---

## 一、快速开始（一键测试）

### 1.1 执行一键测试

```bash
# 进入项目目录
cd /Users/arthur/Documents/workspace/haocean/projectV4

# 执行一键测试（提供 MySQL 密码）
./scripts/run-all-tests.sh your_password

# 或使用环境变量
MYSQL_PWD=your_password ./scripts/run-all-tests.sh
```

### 1.2 测试流程

一键测试脚本会自动执行以下阶段：

| 阶段 | 说明 | 预期时间 |
|------|------|---------|
| 1. 数据库初始化 | 执行 SQL 脚本创建表结构和测试数据 | 5 秒 |
| 2. 后端服务启动 | Maven 编译 + Docker 启动 | 60 秒 |
| 3. API 自动化测试 | 执行 10 个 API 测试用例 | 10 秒 |
| 4. 清理环境 | 停止 Docker 容器（可选） | 5 秒 |

### 1.3 测试结果

测试完成后输出：

```
============================================================
  测试总结
============================================================

阶段测试结果:

  所有阶段测试通过！

总测试数：3
测试报告已保存：/Users/arthur/Documents/workspace/haocean/projectV4/test-reports/summary-20260422-123456.txt

============================================================
  恭喜！所有测试通过！
============================================================
```

---

## 二、分步测试

如需单独控制每个测试阶段，可分别执行以下脚本：

### 2.1 数据库初始化

```bash
# 单独执行数据库初始化
./scripts/init-device-auth-db.sh your_password
```

**输出示例：**
```
============================================================
  设备与认证模块 - 数据库初始化
============================================================

[INFO] 检查 MySQL 连接...
[INFO] MySQL 连接成功
[INFO] 检查数据库 ruoyi...
[INFO] 执行 表结构初始化：V1.0.0__device_auth_tables.sql
[INFO] 表结构初始化 执行成功
[INFO] 执行 测试数据插入：V1.0.0__device_auth_data.sql
[INFO] 测试数据插入 执行成功
[INFO] 验证表结构...
[INFO]   ✓ 表 sys_activation_code 已创建
[INFO]   ✓ 表 sys_device 已创建
[INFO] 表结构验证成功
[INFO] 验证测试数据...
[INFO]   ✓ 测试激活码：10 个

============================================================
[INFO] 数据库初始化完成！
============================================================
```

### 2.2 启动后端服务

```bash
# 启动后端服务
./scripts/start-backend.sh

# 强制重新编译
./scripts/start-backend.sh --build

# 停止服务
./scripts/start-backend.sh --stop

# 重启服务
./scripts/start-backend.sh --restart
```

### 2.3 API 自动化测试

```bash
# 单独执行 API 测试
./scripts/test-device-auth-api.sh
```

**测试用例清单：**

| 编号 | 测试项 | 说明 |
|------|--------|------|
| 1 | 登录获取 Token | 验证用户登录接口 |
| 2 | 查询激活码列表 | 验证分页查询功能 |
| 3 | 查询激活码详情 | 验证单条记录查询 |
| 4 | 生成单个激活码 | 验证生成功能 |
| 5 | 批量生成激活码 | 验证批量生成（5 个） |
| 6 | 验证激活码（有效） | 验证激活码验证逻辑 |
| 7 | 验证激活码（已使用） | 验证重复使用检测 |
| 8 | 查询设备列表 | 验证设备管理功能 |
| 9 | 查询设备详情 | 验证设备详情查询 |

---

## 三、手动测试

### 3.1 数据库初始化

执行 SQL 脚本初始化新表：

```bash
# 进入项目目录
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue

# 执行表结构初始化
mysql -u root -p -h 127.0.0.1 ruoyi < sql/V1.0.0__device_auth_tables.sql

# 执行测试数据插入
mysql -u root -p -h 127.0.0.1 ruoyi < sql/V1.0.0__device_auth_data.sql
```

**验证表是否创建成功：**

```sql
-- 登录 MySQL
mysql -u root -p -h 127.0.0.1

-- 切换到 ruoyi 数据库
USE ruoyi;

-- 查看表是否创建
SHOW TABLES LIKE 'sys_%';

-- 验证激活码表数据
SELECT * FROM sys_activation_code LIMIT 10;

-- 预期输出：10 个测试激活码 (TEST0001-TEST0010)
```

---

## 四、使用 Docker 部署后端服务

### 4.1 编译打包

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue

# 清理编译
mvn clean

# 编译打包（跳过测试）
mvn package -DskipTests
```

### 4.2 使用 Docker Compose 启动服务

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue

# 启动后端服务
docker-compose up -d ruoyi-backend

# 查看日志
docker-compose logs -f ruoyi-backend

# 停止服务
docker-compose down ruoyi-backend
```

**说明：**
- Docker 使用 `host.docker.internal` 连接宿主机 MySQL/Redis
- 确保 `ruoyi-admin/src/main/resources/application-druid.yml` 配置正确：
  ```yaml
  datasource:
    url: jdbc:mysql://host.docker.internal:3306/ruoyi?useUnicode=true&characterEncoding=utf8&zeroDateTimeBehavior=CONVERT_TO_NULL&useSSL=false&serverTimezone=GMT%2B8
    username: root
    password: your_password
  ```

### 4.3 验证服务启动

**检查日志输出：**

```
  ____              _   _
 |  _ \ _   _  ___ | \ | |_   _
 | |_) | | | |/ _ \|  \| | | | |
 |  _ <| |_| | (_) | |\  | |_| |
 |_| \\_\\__,_|\___/|_| \_|\__, |
                          |___/
```

看到以上启动信息且无报错，说明服务启动成功。

**默认访问地址：** http://localhost:8080

---

## 五、API 接口测试

### 5.1 获取 Token

首先需要登录获取访问令牌：

```bash
# 登录接口（使用默认管理员账号）
curl -X POST http://localhost:8080/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "admin",
    "password": "admin123",
    "code": "",
    "uuid": ""
  }'

# 响应示例：
# {
#   "code": 200,
#   "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
#   "userInfo": {...}
# }
```

将返回的 token 保存，后续请求需要携带。

### 5.2 激活码管理接口测试

#### 5.2.1 查询激活码列表

```bash
curl -X POST http://localhost:8080/device/activationCode/list \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "pageNum": 1,
    "pageSize": 10
  }'
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "total": 10,
  "rows": [
    {
      "codeId": 1,
      "codeValue": "TEST0001",
      "status": "0",
      "statusText": "未使用",
      "expireTime": "2027-04-22 00:00:00",
      "createTime": "2026-04-22 00:00:00"
    }
  ]
}
```

#### 5.2.2 生成激活码（单个）

```bash
curl -X POST http://localhost:8080/device/activationCode \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "count": 1,
    "expireDays": 30,
    "remark": "测试生成"
  }'
```

**预期响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": [
    {
      "codeValue": "ABCD1234",
      "expireTime": "2026-05-22 00:00:00"
    }
  ]
}
```

#### 5.2.3 批量生成激活码

```bash
curl -X POST http://localhost:8080/device/activationCode \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "count": 5,
    "expireDays": 60,
    "remark": "批量测试"
  }'
```

#### 5.2.4 验证激活码（App 调用，无需 Token）

```bash
curl -X POST http://localhost:8080/device/activationCode/validate \
  -H "Content-Type: application/json" \
  -d '{
    "codeValue": "TEST0001",
    "deviceUuid": "test-device-uuid-12345"
  }'
```

**预期响应（验证成功）：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "valid": true,
  "message": "激活码有效",
  "code": {
    "codeId": 1,
    "codeValue": "TEST0001",
    "status": "1",
    "bindDeviceId": "test-device-uuid-12345"
  }
}
```

**预期响应（已使用）：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "valid": false,
  "message": "激活码已被使用"
}
```

#### 5.2.5 查询激活码详情

```bash
curl -X GET http://localhost:8080/device/activationCode/1 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

#### 5.2.6 删除激活码

```bash
curl -X DELETE http://localhost:8080/device/activationCode/1,2 \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### 5.3 设备管理接口测试

#### 5.3.1 查询设备列表

```bash
curl -X POST http://localhost:8080/device/device/list \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "pageNum": 1,
    "pageSize": 10
  }'
```

#### 5.3.2 设备解绑

```bash
curl -X PUT http://localhost:8080/device/device/unbind \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "deviceId": 1
  }'
```

---

## 六、前端页面测试

### 6.1 启动前端开发服务器

```bash
cd /Users/arthur/Documents/workspace/haocean/projectV4/RuoYi-Vue/ruoyi-ui

# 安装依赖（如果未安装）
npm install --registry=https://registry.npmmirror.com

# 启动开发服务器
npm run dev
```

**访问地址：** http://localhost:80

### 6.2 页面功能测试

#### 6.1 激活码管理页面

**访问路径：** http://localhost:80/device/activation-code

**测试用例：**

| 编号 | 操作 | 预期结果 |
|------|------|---------|
| TC-01 | 页面加载 | 显示激活码列表，包含 10 个测试数据 |
| TC-02 | 搜索激活码 | 输入"TEST0001"，显示对应记录 |
| TC-03 | 按状态筛选 | 选择"未使用"，显示所有未使用激活码 |
| TC-04 | 生成激活码（单个） | 点击"生成激活码"，数量 1，生成成功 |
| TC-05 | 批量生成激活码 | 数量 5，生成 5 个激活码 |
| TC-06 | 复制激活码 | 点击"复制"，提示"已复制到剪贴板" |
| TC-07 | 查看详情 | 点击"详情"，显示完整信息 |
| TC-08 | 删除激活码 | 点击"删除"，确认后删除成功 |
| TC-09 | 有效期倒计时 | 显示"剩余 XX 天 XX 小时 XX 分" |

#### 6.2 设备管理页面

**访问路径：** http://localhost:80/device/device

**测试用例：**

| 编号 | 操作 | 预期结果 |
|------|------|---------|
| TC-01 | 页面加载 | 显示设备列表（可能为空） |
| TC-02 | 搜索设备 | 按设备名称/状态筛选 |
| TC-03 | 设备解绑 | 点击"解绑"，确认后解绑成功 |

---

## 七、问题排查

### 7.1 常见问题

#### 问题 1：表不存在

**错误信息：** `Table 'ruoyi.sys_activation_code' doesn't exist`

**解决方案：**
```bash
mysql -u root -p -h 127.0.0.1 ruoyi < sql/V1.0.0__device_auth_tables.sql
```

#### 问题 2：权限不足

**错误信息：** `Access denied for user 'root'@'127.0.0.1'`

**解决方案：** 检查数据库用户权限或使用正确的账号密码

#### 问题 3：Docker 容器无法连接数据库

**错误信息：** `Communications link failure`

**解决方案：**
```bash
# 检查 MySQL 是否允许远程连接
mysql -u root -p -e "SELECT host, user FROM mysql.user;"

# 确保 MySQL 绑定到 0.0.0.0 或允许 Docker 连接
# macOS: Docker 默认可以使用 host.docker.internal
# 检查 application-druid.yml 配置是否正确
```

#### 问题 4：端口被占用

**错误信息：** `Port 8080 was already in use`

**解决方案：**
```bash
# 查看占用端口的进程
lsof -i :8080

# 杀死进程
kill -9 <PID>
```

#### 问题 5：前端页面 404

**原因：** 路由未配置

**解决方案：** 检查 `ruoyi-ui/src/router/index.js` 是否添加了设备管理路由

---

## 八、测试报告模板

### 测试执行记录

| 测试项 | 测试时间 | 执行人 | 结果 | 备注 |
|--------|---------|--------|------|------|
| 数据库初始化 | | | ☐ 通过 ☐ 失败 | |
| Docker 服务启动 | | | ☐ 通过 ☐ 失败 | |
| 激活码列表查询 | | | ☐ 通过 ☐ 失败 | |
| 生成激活码 | | | ☐ 通过 ☐ 失败 | |
| 验证激活码 | | | ☐ 通过 ☐ 失败 | |
| 设备列表查询 | | | ☐ 通过 ☐ 失败 | |
| 前端页面展示 | | | ☐ 通过 ☐ 失败 | |

### 问题记录

| 编号 | 问题描述 | 严重性 | 状态 | 解决方案 |
|------|---------|--------|------|---------|
| 1 | | 高/中/低 | 待解决/已解决 | |

---

## 九、下一步

测试通过后，继续 Android 端开发：

1. ✅ 后端基础开发（已完成）
2. ✅ 前端页面开发（已完成）
3. ⏸️ Android Room 数据库集成
4. ⏸️ Android 激活功能
5. ⏸️ Android 登录改造
6. ⏸️ Android 数据同步功能
