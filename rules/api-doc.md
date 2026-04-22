# API 文档规范

## 后端 API 文档 (Swagger/OpenAPI)

### 依赖配置

在 `pom.xml` 中添加 Swagger 依赖：

```xml
<dependency>
    <groupId>io.springfox</groupId>
    <artifactId>springfox-boot-starter</artifactId>
    <version>3.0.0</version>
</dependency>
```

### Swagger 配置

```java
@Configuration
@EnableSwagger2WebMvc
public class SwaggerConfig {

    @Bean
    public Docket createRestApi() {
        return new Docket(DocumentationType.OAS_30)
            .apiInfo(apiInfo())
            .enable(true)
            .select()
            .apis(RequestHandlerSelectors.basePackage("com.ruoyi"))
            .paths(PathSelectors.any())
            .build()
            .securitySchemes(securitySchemes())
            .securityContexts(securityContexts());
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
            .title("移动卫生执法系统 API 文档")
            .description("移动卫生执法系统 RESTful API 接口文档")
            .version("1.0.0")
            .contact(new Contact("开发团队", "", "support@example.com"))
            .build();
    }

    // JWT Token 认证配置
    private List<SecurityScheme> securitySchemes() {
        List<SecurityScheme> apiKeyList = new ArrayList<>();
        apiKeyList.add(new ApiKey("Authorization", "Authorization", "header"));
        return apiKeyList;
    }

    private List<SecurityContext> securityContexts() {
        List<SecurityContext> apiKeyList = new ArrayList<>();
        apiKeyList.add(SecurityContext.builder()
            .securityReferences(defaultAuth())
            .operationSelector(o -> o.requestMappingPattern().matches("/.*"))
            .build());
        return apiKeyList;
    }

    private List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference("Authorization", authorizationScopes));
        return securityReferences;
    }
}
```

### Controller 注解规范

#### 必须添加的注解

```java
@RestController
@RequestMapping("/system/user")
@Tag(name = "用户管理", description = "用户信息的增删改查")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    /**
     * 获取用户列表
     */
    @GetMapping("/list")
    @Operation(summary = "获取用户列表", description = "分页查询系统用户信息")
    @Log(title = "用户管理", businessType = BusinessType.LIST)
    public TableDataInfo list(SysUser user) {
        startPage();
        List<SysUser> list = userService.selectUserList(user);
        return getDataTable(list);
    }

    /**
     * 根据用户 ID 查询用户详情
     */
    @GetMapping(value = "/{userId}")
    @Operation(summary = "查询用户详情", description = "根据用户 ID 获取用户详细信息")
    @Log(title = "用户管理", businessType = BusinessType.SELECT)
    public AjaxResult getInfo(@PathVariable("userId") Long userId) {
        return success(userService.selectUserById(userId));
    }

    /**
     * 新增用户
     */
    @PostMapping
    @Operation(summary = "新增用户", description = "添加系统用户信息")
    @Log(title = "用户管理", businessType = BusinessType.INSERT)
    public AjaxResult add(@Validated @RequestBody AddUserRequest request) {
        // ...
    }

    /**
     * 修改用户
     */
    @PutMapping(value = "/{userId}")
    @Operation(summary = "修改用户", description = "根据用户 ID 更新用户信息")
    @Log(title = "用户管理", businessType = BusinessType.UPDATE)
    public AjaxResult edit(@PathVariable("userId") Long userId,
                          @Validated @RequestBody UpdateUserRequest request) {
        // ...
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{userIds}")
    @Operation(summary = "删除用户", description = "根据用户 ID 批量删除用户信息")
    @Log(title = "用户管理", businessType = BusinessType.DELETE)
    public AjaxResult remove(@PathVariable Long[] userIds) {
        return toAjax(userService.deleteUserByIds(userIds));
    }
}
```

### DTO/VO 类注解规范

#### 请求参数类

```java
@Data
public class AddUserRequest {

    @NotBlank(message = "用户账号不能为空")
    @Size(min = 2, max = 20, message = "用户账号长度必须在 2 到 20 个字符之间")
    @Schema(description = "用户账号", example = "zhangsan", required = true)
    private String userName;

    @NotBlank(message = "用户密码不能为空")
    @Size(min = 6, max = 20, message = "密码长度必须在 6 到 20 个字符之间")
    @Schema(description = "用户密码", example = "123456", required = true)
    private String password;

    @Email(message = "邮箱格式不正确")
    @Schema(description = "用户邮箱", example = "zhangsan@example.com")
    private String email;

    @Schema(description = "用户昵称", example = "张三")
    private String nickName;

    @Schema(description = "部门 ID", example = "101")
    private Long deptId;

    @Schema(description = "用户性别", example = "0", allowableValues = {"0", "1", "2"})
    private String sex;

    @Schema(description = "帐号状态", example = "0", allowableValues = {"0", "1"})
    private String status;

    @Schema(description = "备注", example = "这是一个测试用户")
    private String remark;
}
```

#### 响应结果类

```java
@Data
@Schema(description = "用户信息响应")
public class UserResponse {

    @Schema(description = "用户 ID", example = "1")
    private Long userId;

    @Schema(description = "用户账号", example = "admin")
    private String userName;

    @Schema(description = "用户昵称", example = "管理员")
    private String nickName;

    @Schema(description = "用户邮箱", example = "admin@example.com")
    private String email;

    @Schema(description = "用户性别", example = "男")
    private String sex;

    @Schema(description = "帐号状态", example = "正常")
    private String status;

    @Schema(description = "最后登录时间", example = "2024-01-01 12:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date loginDate;

    @Schema(description = "创建时间", example = "2023-01-01 00:00:00")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
```

### 统一返回结果

```java
@Data
@Schema(description = "统一 API 响应结果")
public class AjaxResult {

    @Schema(description = "状态码", example = "200")
    private int code;

    @Schema(description = "返回消息", example = "操作成功")
    private String msg;

    @Schema(description = "数据对象")
    private Object data;

    public static AjaxResult success(Object data) {
        AjaxResult result = new AjaxResult();
        result.setCode(200);
        result.setMsg("操作成功");
        result.setData(data);
        return result;
    }

    public static AjaxResult error(String msg) {
        AjaxResult result = new AjaxResult();
        result.setCode(500);
        result.setMsg(msg);
        return result;
    }
}
```

### 分页响应结果

```java
@Data
@Schema(description = "表格分页响应")
public class TableDataInfo {

    @Schema(description = "总记录数", example = "100")
    private long total;

    @Schema(description = "数据列表")
    private List<?> rows;

    public static TableDataInfo build(List<?> list) {
        TableDataInfo result = new TableDataInfo();
        result.setRows(list);
        result.setTotal(list.size());
        return result;
    }
}
```

---

## 前端 API 管理规范

### API 文件组织

```
src/api/
├── system/
│   ├── user.js        # 用户管理接口
│   ├── role.js        # 角色管理接口
│   ├── menu.js        # 菜单管理接口
│   ├── dept.js        # 部门管理接口
│   └── dict.js        # 字典管理接口
├── monitor/
│   ├── online.js      # 在线用户接口
│   ├── job.js         # 定时任务接口
│   └── log.js         # 日志接口
└── tool/
    ├── gen.js         # 代码生成接口
    └── build.js       # 表单构建接口
```

### API 模块编写规范

```javascript
// src/api/system/user.js

import request from '@/utils/request'

// 查询用户列表
export function listUser(query) {
  return request({
    url: '/system/user/list',
    method: 'get',
    params: query
  })
}

// 查询用户详细
export function getUser(userId) {
  return request({
    url: '/system/user/' + userId,
    method: 'get'
  })
}

// 新增用户
export function addUser(data) {
  return request({
    url: '/system/user',
    method: 'post',
    data: data
  })
}

// 修改用户
export function updateUser(userId, data) {
  return request({
    url: '/system/user/' + userId,
    method: 'put',
    data: data
  })
}

// 删除用户
export function delUser(userId) {
  return request({
    url: '/system/user/' + userId,
    method: 'delete'
  })
}

// 导出用户
export function exportUser(query) {
  return request({
    url: '/system/user/export',
    method: 'post',
    data: query
  })
}

// 下载用户导入模板
export function importTemplate() {
  return request({
    url: '/system/user/importData',
    method: 'get'
  })
}
```

### JSDoc 注释规范

```javascript
/**
 * 用户管理相关 API 接口
 * @module api/system/user
 * @author 开发团队
 */

/**
 * 查询用户列表（分页）
 * @function listUser
 * @param {Object} query - 查询参数
 * @param {string} [query.userName] - 用户账号
 * @param {string} [query.phonenumber] - 手机号码
 * @param {string} [query.status] - 用户状态
 * @param {number} [query.pageNum=1] - 当前页码
 * @param {number} [query.pageSize=10] - 每页数量
 * @returns {Promise<TableDataInfo>} 用户列表数据
 */
```

---

## Android API 管理规范

### API 接口定义

```kotlin
// app/api/UserApi.kt
package com.ruoyi.api

import com.ruoyi.model.Response
import com.ruoyi.model.User
import retrofit2.http.*

interface UserApi {

    /**
     * 获取用户列表
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @param userName 用户账号（可选）
     */
    @GET("system/user/list")
    suspend fun getUserList(
        @Query("pageNum") pageNum: Int = 1,
        @Query("pageSize") pageSize: Int = 10,
        @Query("userName") userName: String? = null
    ): Response<PageData<User>>

    /**
     * 获取用户详情
     * @param userId 用户 ID
     */
    @GET("system/user/{userId}")
    suspend fun getUserDetail(@Path("userId") userId: Long): Response<User>

    /**
     * 新增用户
     * @param user 用户信息
     */
    @POST("system/user")
    suspend fun addUser(@Body user: UserRequest): Response<Unit>

    /**
     * 修改用户
     * @param userId 用户 ID
     * @param user 用户信息
     */
    @PUT("system/user/{userId}")
    suspend fun updateUser(
        @Path("userId") userId: Long,
        @Body user: UserRequest
    ): Response<Unit>

    /**
     * 删除用户
     * @param userIds 用户 ID 数组
     */
    @DELETE("system/user/{userIds}")
    suspend fun deleteUser(@Path("userIds") userIds: List<Long>): Response<Unit>
}
```

### 数据模型类

```kotlin
// app/model/User.kt
package com.ruoyi.model

import com.google.gson.annotations.SerializedName
import java.util.Date

/**
 * 用户实体类
 * @property userId 用户 ID
 * @property userName 用户账号
 * @property nickName 用户昵称
 * @property email 邮箱
 * @property sex 性别 (0:男，1:女，2:未知)
 * @property status 状态 (0:正常，1:停用)
 */
data class User(
    @SerializedName("userId")
    val userId: Long? = null,

    @SerializedName("userName")
    val userName: String = "",

    @SerializedName("nickName")
    val nickName: String = "",

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("sex")
    val sex: String = "0",

    @SerializedName("status")
    val status: String = "0",

    @SerializedName("loginDate")
    val loginDate: Date? = null,

    @SerializedName("createTime")
    val createTime: Date? = null
)

/**
 * 用户请求数据
 */
data class UserRequest(
    @SerializedName("userId")
    val userId: Long? = null,

    @SerializedName("userName")
    val userName: String,

    @SerializedName("password")
    val password: String? = null,

    @SerializedName("nickName")
    val nickName: String,

    @SerializedName("email")
    val email: String? = null,

    @SerializedName("sex")
    val sex: String = "0",

    @SerializedName("status")
    val status: String = "0"
)
```

---

## API 版本管理

### URL 路径版本控制

```
/api/v1/users      - 第一版用户接口
/api/v2/users      - 第二版用户接口
```

### 请求头版本控制

```java
@ApiVersion(1)
@RequestMapping("/user")
public class UserControllerV1 {
    // ...
}

@ApiVersion(2)
@RequestMapping("/user")
public class UserControllerV2 {
    // ...
}
```

---

## 检查清单

### API 设计检查

- [ ] 所有公开接口都有 Swagger 注解
- [ ] 接口路径符合 RESTful 规范
- [ ] 请求/响应类都有清晰的字段说明
- [ ] 参数验证注解完整
- [ ] 接口有统一的返回格式
- [ ] 错误码定义清晰
- [ ] 接口文档可正常访问

### 前端 API 检查

- [ ] API 文件按模块组织
- [ ] 接口方法有 JSDoc 注释
- [ ] 请求参数有类型定义
- [ ] 错误处理统一
- [ ] 接口地址使用环境变量

### Android API 检查

- [ ] API 接口有 KDoc 注释
- [ ] 数据模型使用 data class
- [ ] 使用协程处理异步请求
- [ ] 错误处理统一
- [ ] 序列化注解正确
