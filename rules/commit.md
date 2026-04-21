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

---

## 频繁提交规范

### 提交频率要求

- **每次功能修改完成后必须立即提交并推送到 GitHub**
- 禁止长时间（超过 4 小时）不提交代码
- 禁止在本地积累大量变更后再统一提交
- 每天下班前必须提交当天的所有代码变更

### 提交粒度

| 场景 | 提交时机 | 说明 |
|------|---------|------|
| 修复 Bug | 修复完成后立即提交 | 每个 Bug 修复单独提交 |
| 新增功能 | 功能点完成后提交 | 将大功能拆分为小提交 |
| 重构代码 | 每个重构步骤完成后提交 | 避免一次性大范围重构 |
| 文档修改 | 修改完成后立即提交 | 文档变更独立提交 |

### 提交流程

```bash
# 1. 查看变更
git status
git diff

# 2. 添加变更
git add <file>

# 3. 提交（遵循 Commit Message 格式）
git commit -m "type(scope): description"

# 4. 推送到 GitHub
git push origin <branch>
```

### 频繁提交的好处

- 代码变更及时备份，避免丢失
- 便于团队成员了解进展
- 出现问题可快速回退到最近的提交点
- Code Review 可以更早介入
- 减少代码冲突的发生

---

## 分支保护规则

### 保护分支定义

| 分支 | 保护级别 | 说明 |
|------|---------|------|
| `master` | 最高保护 | 生产环境代码，禁止直接推送 |
| `develop` | 高度保护 | 开发主分支，禁止直接推送 |
| `release/*` | 中等保护 | 发布分支，合并后删除 |
| `feature/*` | 低保护 | 功能分支，个人开发使用 |
| `bugfix/*` | 低保护 | 修复分支，个人开发使用 |
| `hotfix/*` | 高度保护 | 紧急修复，合并后删除 |

### 保护规则详情

#### master 分支保护

- [x] 禁止直接推送（Force Push 禁止）
- [x] 必须通过 Pull Request 合并
- [x] 至少需要 1 人 Code Review 批准
- [x] CI 构建必须通过
- [x] 必须 Squash Merge 或 Rebase Merge
- [x] 禁止删除分支

#### develop 分支保护

- [x] 禁止直接推送（Force Push 禁止）
- [x] 必须通过 Pull Request 合并
- [x] 至少需要 1 人 Code Review 批准
- [x] CI 构建必须通过
- [x] 允许 Rebase Merge

### GitHub 分支保护配置

```
Settings → Branches → Add branch protection rule

Branch name pattern: master

Protect matching branches:
[✓] Require a pull request before merging
    [✓] Require approvals
        Required number of approvals before merging: 1
    [ ] Dismiss pull request reviews on new commits
    [✓] Require review from Code Owners
[✓] Require status checks to pass before merging
    [✓] Require branches to be up to date before merging
    Required status checks:
    - build (Java 17)
    - test (Unit Test)
    - lint (Code Style)
[✓] Require linear history
[✓] Do not allow bypassing the above settings
[✓] Do not allow deleting
[✓] Do not allow force pushes
[✓] Allow force pushes after a period of time (7 days)
```

### GitLab 分支保护配置

```
Settings → Repository → Protected Branches

Branch: master
Allowed to merge: Maintainers
Allowed to push and merge: No one (prevent direct pushes)
Allowed to force push: No
```

---

## Code Review 要求

### 最小编辑量规则

| 变更类型 | 最大行数 | 说明 |
|---------|---------|------|
| 小型变更 | < 200 行 | 可快速审批 |
| 中型变更 | 200-500 行 | 需要详细审查 |
| 大型变更 | > 500 行 | 建议拆分为多个 PR |

### Review 响应时间

| 优先级 | 响应时间 | 说明 |
|-------|---------|------|
| Hotfix | 1 小时内 | 生产环境紧急修复 |
| Bugfix | 4 小时内 | 普通 Bug 修复 |
| Feature | 24 小时内 | 新功能开发 |
| Refactor | 48 小时内 | 重构优化 |

### Review 批准要求

```
PR 合并条件:
- 至少 1 名非作者的 Reviewer 批准
- 所有 CI 检查通过
- 无未解决的 Comment
- 分支已更新到最新 develop
```

---

## CI/CD 集成

### GitHub Actions 示例

```yaml
# .github/workflows/ci.yml
name: CI

on:
  pull_request:
    branches: [master, develop]
  push:
    branches: [develop]

jobs:
  build:
    runs-on: ubuntu-latest
    steps:
      - uses: actions/checkout@v3

      - name: Set up JDK 17
        uses: actions/setup-java@v3
        with:
          java-version: '17'
          distribution: 'temurin'

      - name: Build with Maven
        run: mvn clean package -DskipTests

      - name: Run tests
        run: mvn test

      - name: Code style check
        run: mvn checkstyle:check
```

### 推送规则检查

```bash
# Git Hook: pre-push
#!/bin/bash

# 禁止直接推送到 master 和 develop
protected_branches='master develop'
current_branch=$(git symbolic-ref HEAD | sed -e 's,.*/\(.*\),\1,')

if [[ $protected_branches =~ $current_branch ]]; then
    echo "错误：禁止直接推送到保护分支：$current_branch"
    echo "请使用 Pull Request 方式进行代码合并"
    exit 1
fi

exit 0
```

---

## 合并策略

### 推荐的合并方式

| 场景 | 合并方式 | 说明 |
|------|---------|------|
| Feature → Develop | Rebase Merge | 保持提交历史线性 |
| Bugfix → Develop | Rebase Merge | 保持提交历史线性 |
| Develop → Master | Squash Merge | 发布时压缩为一个提交 |
| Hotfix → Master | Squash Merge | 紧急修复快速上线 |

### 禁止的合并方式

- ❌ 禁止在保护分支上使用 Force Push
- ❌ 禁止绕过 CI 检查直接合并
- ❌ 禁止在没有 Review 的情况下合并
- ❌ 禁止合并未解决的冲突代码

---

## 提交辅助工具

### Commitlint 配置

```javascript
// commitlint.config.js
module.exports = {
  extends: ['@commitlint/config-conventional'],
  rules: {
    'type-enum': [
      2,
      'always',
      ['feat', 'fix', 'docs', 'style', 'refactor', 'perf', 'test', 'chore', 'ci', 'revert']
    ],
    'subject-full-stop': [2, 'never'],
    'subject-case': [2, 'never', ['sentence-case', 'start-case', 'pascal-case', 'upper-case']],
    'header-max-length': [2, 'always', 50]
  }
}
```

### Husky Hook 配置

```json
// package.json
{
  "husky": {
    "hooks": {
      "pre-commit": "lint-staged",
      "commit-msg": "commitlint -E HUSKY_GIT_PARAMS",
      "pre-push": "./scripts/pre-push.sh"
    }
  },
  "lint-staged": {
    "*.java": ["mvn spotless:apply"],
    "*.{js,vue}": ["eslint --fix"],
    "*.{js,vue,css,md}": ["prettier --write"]
  }
}
```
