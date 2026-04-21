# 开发规范索引

本项目开发规范文档，所有开发人员应严格遵守以下规范。

## 规范文件列表

| 文件 | 描述 | 适用范围 |
|------|------|---------|
| [general.md](./general.md) | 通用开发规范 | 全体开发人员 |
| [commit.md](./commit.md) | Git 提交与分支管理规范 | 全体开发人员 |
| [code-review.md](./code-review.md) | 代码审查规范 | 全体开发人员 |
| [testing.md](./testing.md) | 测试规范 | 全体开发人员 |
| [deployment.md](./deployment.md) | 部署流程规范 | 运维/后端开发 |
| [database.md](./database.md) | 数据库管理规范 | 后端开发 |
| [gitignore.md](./gitignore.md) | 文件忽略和环境配置规范 | 全体开发人员 |
| [api-doc.md](./api-doc.md) | API 文档规范 | 后端/前端/Android 开发 |
| [logging.md](./logging.md) | 日志规范 | 全体开发人员 |

## 快速参考

### 新员工入职

1. 首先阅读 [general.md](./general.md) 了解代码风格和最佳实践
2. 阅读 [commit.md](./commit.md) 了解 Git 提交流程
3. 配置 IDE 格式化规则（参考 general.md）
4. 申请代码仓库访问权限

### 日常开发

- 提交前：参考 [commit.md](./commit.md) 的检查清单
- 开发中：遵循 [general.md](./general.md) 的代码规范
- 写接口：参考 [api-doc.md](./api-doc.md) 添加文档
- 写日志：遵循 [logging.md](./logging.md) 的级别规范

### 代码审查

- 提交审查：参考 [code-review.md](./code-review.md) 的 Checklist
- 审查他人：遵循 [code-review.md](./code-review.md) 的质量要求

### 测试

- 编写测试：参考 [testing.md](./testing.md) 的测试层级
- 运行测试：参考 [testing.md](./testing.md) 的运行命令

### 部署上线

- 后端部署：遵循 [deployment.md](./deployment.md) 的 Docker 部署流程
- 数据库变更：遵循 [database.md](./database.md) 的变更流程

## 规范版本

| 版本号 | 日期 | 变更内容 |
|-------|------|---------|
| v1.0.0 | 2026-04-22 | 初始版本，整合现有规范 |

## 规范修订

如需修订规范，请提交 MR 并说明修改理由。规范修订需要经过团队讨论通过。

## 相关资源

- [Conventional Commits](https://www.conventionalcommits.org/)
- [Clean Code](https://book.douban.com/subject/4735171/)
- [阿里巴巴 Java 开发手册](https://github.com/alibaba/p3c)
