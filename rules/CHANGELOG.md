# 规则变更日志

本文件用于记录规则执行过程中的问题和修正，确保规则文档持续改进。

---

## 待修正

暂无待修正问题。

---

## 已修正

### 2026-04-22 - Docker 部署后端连接数据库失败
- 规则文件：deployment.md
- 问题描述：Docker 容器启动后无法连接宿主机 MySQL 数据库
- 原因分析：
  1. 容器内无法通过 localhost 访问宿主机 MySQL
  2. 需要使用 host.docker.internal 或 host network 模式
  3. MySQL 连接 URL 需要添加 allowPublicKeyRetrieval=true 参数
- 修正方案：
  1. 更新 docker-compose.yml，添加 extra_hosts 配置 host.docker.internal
  2. 修改 application-druid.yml，数据库 URL 使用 host.docker.internal
  3. 添加 allowPublicKeyRetrieval=true 参数
  4. 禁用 useSSL 或确保 SSL 配置正确
- 修正人：Claude
- 状态：已验证 ✓

### 2026-04-22 - Docker 镜像拉取超时
- 规则文件：deployment.md
- 问题描述：构建 Docker 镜像时无法从 Docker Hub 拉取基础镜像
- 原因分析：Docker Hub 国内访问不稳定，镜像源连接超时
- 修正方案：
  1. 使用本地已有的镜像（如 nginx:latest）
  2. 或使用国内镜像源（如 registry.cn-hangzhou.aliyuncs.com）
  3. 更新 deployment.md 添加镜像源备选方案
- 修正人：Claude
- 状态：已验证 ✓

### 模板

```markdown
### YYYY-MM-DD - 规则名称修正
- 规则文件：xxx.md
- 问题描述：执行 xxx 步骤时失败
- 原因分析：xxx
- 修正方案：更新 xxx 章节，添加 xxx 说明
- 修正人：xxx
- 状态：已验证 ✓
```

---

## 使用说明

### 记录失败情况

当按照规则执行失败时，在"待修正"部分添加：

```markdown
### YYYY-MM-DD - 问题简述
- 规则文件：xxx.md
- 问题描述：执行 xxx 步骤时出现 xxx 错误
- 临时方案：xxx
- 状态：待验证
```

### 问题解决后

1. 将待修正条目移动到"已修正"部分
2. 补充原因分析和修正方案
3. 更新对应的规则文件
4. 将状态改为"已验证 ✓"

### 提交变更

```bash
git add rules/CHANGELOG.md rules/<修正的规则文件>.md
git commit -m "chore(rules): 修正 xxx 规则

- 记录问题：执行 xxx 步骤失败
- 修正方案：更新 xxx 说明
- Closes rules/#1"
git push origin main
```
