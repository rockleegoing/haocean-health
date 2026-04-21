# 通用开发规范

## 代码风格

### Java 后端 (RuoYi-Vue)

- 使用 4 个空格缩进，不使用 Tab
- 类名使用大驼峰命名 (PascalCase)
- 方法名和变量名使用小驼峰命名 (camelCase)
- 常量名使用全大写，下划线分隔 (UPPER_SNAKE_CASE)
- 私有方法/字段以下划线前缀命名 `_methodName`
- 每个类只负责单一职责
- 公共方法必须添加 JavaDoc 注释

### Vue 前端 (ruoyi-ui)

- 组件名使用大驼峰命名，如 `UserForm.vue`
- 模板中的自定义组件使用大驼峰命名
- v-on 监听器使用 handle 前缀，如 `handleClick`
- 计算属性使用 get 前缀或形容词，如 `getUserInfo` / `isVisible`
- 方法使用动词开头，如 `submitForm` / `fetchData`
- props 使用 camelCase，模板中使用 kebab-case

### Android (Ruoyi-Android-App)

- Kotlin 代码遵循官方代码风格
- 类名使用大驼峰命名
- 方法、变量、参数使用小驼峰命名
- 常量使用 const val，全大写命名
- 私有函数使用 private 修饰符
- 优先使用 data class 代替普通 POJO
- 使用 extension function 扩展工具方法

## 最佳实践

### 后端

-  controller 层只处理请求参数和响应结果
-  service 层处理业务逻辑和事务
-  mapper/xml 层处理数据持久化
- 使用 `@DataScope` 注解实现数据权限
- 使用 `@Log` 注解记录操作日志
- 异常统一使用 `GlobalException` 处理
- 返回结果统一使用 `AjaxResult` 或 `R`

### 前端

- API 请求统一在 `src/api` 目录下管理
- 通用组件放在 `src/components` 目录
- 使用 `mapGetters` 和 `mapActions` 简化 Vuex 调用
- 路由跳转前检查权限 `checkPermi`
- 表格数据使用 `parseTime` 格式化时间

### Android

- 使用 MVVM 架构，UI 与业务逻辑分离
- 使用 DataBinding 减少 findViewById
- 网络请求封装在 Repository 层
- 使用 Coroutine 处理异步操作
- 使用 TheRouter 进行页面路由导航
- 图片加载统一使用 Glide
- Toast 提示统一使用 ToastUtils

## 文件组织

### 后端模块结构
```
ruoyi-admin/          # 启动模块
ruoyi-common/         # 通用工具
ruoyi-framework/      # 框架配置
ruoyi-system/         # 系统模块
ruoyi-quartz/         # 定时任务
ruoyi-generator/      # 代码生成
```

### 前端目录结构
```
src/
├── api/              # API 接口
├── assets/           # 静态资源
├── components/       # 公共组件
├── directive/        # 自定义指令
├── layout/           # 布局组件
├── router/           # 路由配置
├── store/            # 状态管理
├── utils/            # 工具方法
├── views/            # 页面组件
```

### Android 目录结构
```
app/
├── api/              # 网络 API
├── base/             # 基类封装
├── db/               # 数据库
├── model/            # 数据模型
├── repository/       # 数据仓库
├── ui/               # UI 界面
├── utils/            # 工具类
└── viewmodel/        # ViewModel
```

### 规则文件结构
```
rules/
├── general.md        # 通用开发规范
├── commit.md         # Git 提交与分支管理规范
├── code-review.md    # 代码审查规范
├── testing.md        # 测试规范
├── deployment.md     # 部署流程规范
├── database.md       # 数据库管理规范
├── gitignore.md      # .gitignore 和环境配置规范
├── api-doc.md        # API 文档规范
└── logging.md        # 日志规范
```
