# 规则变更日志

本文件用于记录规则执行过程中的问题和修正，确保规则文档持续改进。

---

## 待修正

暂无待修正问题。

---

## 已修正

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
