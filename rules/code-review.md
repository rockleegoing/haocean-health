# 代码审查规范

## Review Checklist

### 通用检查项

- [ ] 代码功能是否符合需求
- [ ] 是否存在明显的逻辑错误
- [ ] 是否有适当的错误处理
- [ ] 是否有安全隐患（SQL 注入、XSS 等）
- [ ] 是否包含敏感信息（密码、密钥、token）
- [ ] 日志输出是否恰当（无敏感数据泄露）

### 后端 (Java)

- [ ] Controller 层是否只处理请求/响应
- [ ] Service 层是否有事务注解 `@Transactional`
- [ ] 数据库查询是否有参数校验
- [ ] SQL 是否使用预编译语句
- [ ] 异常是否统一处理
- [ ] 返回结果格式是否统一
- [ ] 是否有 N+1 查询问题
- [ ] 批量操作是否有限制
- [ ] 接口是否有权限控制 `@PreAuthorize`

### 前端 (Vue)

- [ ] API 调用是否有错误处理
- [ ] 表单提交是否有防重复提交
- [ ] 用户输入是否有校验
- [ ] 敏感操作是否有二次确认
- [ ] 权限指令 `v-hasPermi` 使用是否正确
- [ ] 定时器/监听器是否正确销毁
- [ ] 大列表是否有分页/虚拟滚动
- [ ] 图片等静态资源是否优化

### Android (Kotlin)

- [ ] 是否有内存泄漏风险（Context 使用）
- [ ] 网络请求是否在非主线程
- [ ] UI 操作是否在主线程
- [ ] 权限是否有运行时检查
- [ ] 敏感数据是否加密存储
- [ ] 异常是否有捕获处理
- [ ] 资源是否正确关闭（Cursor、Stream）
- [ ] 列表是否有防抖处理

## 质量要求

### 代码可读性

- 变量、方法命名清晰易懂
- 方法长度适中（不超过 80 行）
- 类职责单一
- 避免魔法数字，使用常量
- 适当添加注释说明复杂逻辑

### 代码复用性

- 避免重复代码
- 通用功能抽取为工具类
- 使用继承或组合复用代码
- 模板方法模式处理相似流程

### 性能考虑

- 避免在循环中查询数据库
- 大数据量使用分页处理
- 缓存使用恰当
- 及时释放资源
- 避免不必要的对象创建

### 安全考虑

- 用户输入必须校验
- 密码等敏感数据加密存储
- 接口权限控制
- 防止 SQL 注入
- 防止 XSS 攻击
- 文件上传类型和大小限制

## Review 流程

1. **自评**: 提交者先自行检查代码
2. **提交 MR/PR**: 填写变更描述和测试说明
3. **分配 Reviewer**: 至少 1 人审核
4. **反馈修改**: 根据意见修改代码
5. **合并**: 审核通过后合并

## 常见 Code Smell

### Java

```java
// ❌ 坏味道：过长方法
public void processUser() {
    // 100+ 行代码...
}

// ✅ 推荐：拆分方法
public void processUser() {
    validateUser();
    saveUser();
    sendNotification();
}

// ❌ 坏味道：魔法数字
if (status == 1) { ... }

// ✅ 推荐：使用常量
if (status == UserStatus.ACTIVE) { ... }

// ❌ 坏味道：捕获异常不处理
try {
    // ...
} catch (Exception e) {
    // 空
}

// ✅ 推荐：记录日志或抛出异常
try {
    // ...
} catch (Exception e) {
    log.error("处理失败", e);
    throw new ServiceException("处理失败");
}
```

### Vue

```javascript
// ❌ 坏味道：在 template 中写复杂逻辑
<div v-if="user.status === 1 && user.role === 'admin' && user.deleted === 0">

// ✅ 推荐：使用计算属性
computed: {
  canEdit() {
    return this.user.status === 1 &&
           this.user.role === 'admin' &&
           this.user.deleted === 0
  }
}
```

### Kotlin

```kotlin
// ❌ 坏味道：嵌套过深
if (user != null) {
    if (user.isActive) {
        if (user.hasPermission()) {
            // ...
        }
    }
}

// ✅ 推荐：使用 guard 语句
if (!user?.isActive == true) return
if (!user.hasPermission()) return
// ...

// ✅ 或使用 let
user?.takeIf { it.isActive && it.hasPermission() }?.let {
    // ...
}
```
