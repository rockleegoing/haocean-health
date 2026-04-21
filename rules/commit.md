# Git 提交规范

## Commit Message 格式

采用 [Conventional Commits](https://www.conventionalcommits.org/) 规范：

```
<type>(<scope>): <subject>

<body>

<footer>
```

### Type 类型

- `feat`: 新功能
- `fix`: 修复 bug
- `docs`: 文档变更
- `style`: 代码格式（不影响功能）
- `refactor`: 重构（既不是新功能也不是 bug 修复）
- `perf`: 性能优化
- `test`: 测试相关
- `chore`: 构建过程或辅助工具变更
- `ci`: CI 配置相关
- `revert`: 回滚提交

### Scope 范围

#### 后端 (RuoYi-Vue)
- `admin`: 启动模块
- `common`: 通用工具
- `framework`: 框架配置
- `system`: 系统模块
- `quartz`: 定时任务
- `generator`: 代码生成

#### 前端 (ruoyi-ui)
- `api`: 接口
- `components`: 组件
- `views`: 页面
- `utils`: 工具
- `router`: 路由
- `store`: 状态管理

#### Android
- `app`: 应用层
- `api`: 网络接口
- `ui`: 界面
- `model`: 数据模型
- `utils`: 工具类

### Subject 主题

- 简短描述，不超过 50 个字符
- 使用祈使语气，如 "add" 而不是 "added"
- 首字母小写
- 结尾不加句号

### Body 正文（可选）

- 详细描述变更动机和对比之前的行为
- 每行不超过 72 个字符

### Footer 注脚（可选）

- 关联 Issue：`Closes #123`
- 重大变更：`BREAKING CHANGE: `

## 示例

```
feat(system): 添加用户导入功能

- 新增用户导入接口
- 支持 Excel 文件解析
- 添加数据验证和错误提示

Closes #45
```

```
fix(common): 修复日期格式化工具类时区问题

- 修正 DateUtils 默认时区为 UTC 的问题
- 统一使用 GMT+8 时区

BREAKING CHANGE: 日期格式化结果可能因时区变化而不同
```

```
refactor(ui): 重构用户管理页面表格组件

- 提取通用表格逻辑到 mixins
- 优化代码复用性
```

## 分支管理

### 分支命名

- `master`: 主分支，生产环境代码
- `develop`: 开发分支
- `feature/*`: 功能分支，从 develop 切出，合并回 develop
- `bugfix/*`: 修复分支，从 develop 切出，合并回 develop
- `hotfix/*`: 紧急修复，从 master 切出，合并回 master 和 develop
- `release/*`: 发布分支，从 develop 切出，合并回 master 和 develop

### 工作流程

```bash
# 创建功能分支
git checkout develop
git checkout -b feature/user-management

# 开发完成后合并回 develop
git checkout develop
git merge --no-ff feature/user-management

# 删除已合并的分支
git branch -d feature/user-management
```

## 代码提交前检查清单

- [ ] 代码已通过本地编译
- [ ] 新增代码已添加必要注释
- [ ] 敏感信息（密码、密钥）已移除
- [ ] Commit Message 格式正确
- [ ] 关联的 Issue 已在 Footer 中声明
