# 测试规范

## 测试层级

### 单元测试 (Unit Test)

测试单个方法或类的功能。

#### 后端 (JUnit + Mockito)

```java
// 测试类命名：XxxTest
public class UserServiceTest {

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @Test
    public void testSelectUserById() {
        // Given
        Long userId = 1L;
        SysUser expectedUser = new SysUser();
        expectedUser.setUserId(userId);
        Mockito.when(userMapper.selectById(userId)).thenReturn(expectedUser);

        // When
        SysUser result = userService.selectUserById(userId);

        // Then
        Assert.assertNotNull(result);
        Assert.assertEquals(userId, result.getUserId());
    }
}
```

**要求：**
- 核心业务逻辑覆盖率 >= 80%
- 使用 Given-When-Then 结构
- 测试方法命名：`test` + `方法名` 或 `should` + `预期结果`
- 每个测试只验证一个行为
- 使用 Mock 隔离外部依赖

### 集成测试 (Integration Test)

测试多个模块协作的功能。

```java
@SpringBootTest
@AutoConfigureMockMvc
public class UserControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testLogin() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"username\":\"admin\",\"password\":\"admin123\"}"))
                .andExpect(MockMvcResultMatchers
                .status()
                .isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").value(200));
    }
}
```

### 前端测试

#### Vue 组件测试 (Jest + Vue Test Utils)

```javascript
import { shallowMount } from '@vue/test-utils'
import UserForm from '@/components/UserForm.vue'

describe('UserForm.vue', () => {
  it('renders props when passed', () => {
    const wrapper = shallowMount(UserForm, {
      propsData: {
        userInfo: { name: 'Test' }
      }
    })
    expect(wrapper.find('.user-name').text()).toBe('Test')
  })

  it('emits submit event on form submit', async () => {
    const wrapper = shallowMount(UserForm)
    await wrapper.find('form').trigger('submit.prevent')
    expect(wrapper.emitted().submit).toBeTruthy()
  })
})
```

### Android 测试

#### 本地单元测试 (JUnit + MockK)

```kotlin
@Test
fun testUserLogin_success() = runTest {
    // Given
    val expectedUser = User(id = 1, name = "Admin")
    every { userRepository.login(any(), any()) } returns expectedUser

    // When
    val result = viewModel.login("admin", "admin123")

    // Then
    assertEquals(expectedUser, result.getOrNull())
}
```

#### UI 测试 (Espresso)

```kotlin
@Test
fun testLoginButton_Click_ShowsHome() {
    onView(withId(R.id.et_username)).perform(typeText("admin"))
    onView(withId(R.id.et_password)).perform(typeText("admin123"))
    onView(withId(R.id.btn_login)).perform(click())

    onView(withId(R.id.fragment_home)).check(matches(isDisplayed()))
}
```

## 测试运行命令

### 后端
```bash
# 运行所有测试
mvn test

# 运行指定测试类
mvn test -Dtest=UserServiceTest

# 跳过测试打包
mvn package -DskipTests
```

### 前端
```bash
# 运行测试
npm run test

# 运行测试并生成覆盖率报告
npm run test:unit -- --coverage

# 监听模式
npm run test:unit -- --watch
```

### Android
```bash
# 运行所有测试
./gradlew test

# 运行指定模块测试
./gradlew app:test

# 运行 UI 测试
./gradlew connectedAndroidTest
```

## 测试原则

### FIRST 原则

- **F**ast: 测试要快速执行
- **I**ndependent: 测试之间相互独立
- **R**epeatable: 可重复执行，结果一致
- **S**elf-validating: 自动判断通过与否
- **T**imely: 及时编写，最好 TDD

### AAA 模式

```java
@Test
public void testSomething() {
    // Arrange (准备)
    UserService service = new UserService();
    User user = new User("admin");

    // Act (执行)
    boolean result = service.validate(user);

    // Assert (断言)
    assertTrue(result);
}
```

## 测试覆盖要求

| 模块类型 | 最低覆盖率 | 目标覆盖率 |
|---------|----------|----------|
| 核心业务 | 80% | 90% |
| 工具类 | 90% | 95% |
| Controller | 70% | 80% |
| 前端组件 | 60% | 80% |

## 持续集成

测试将自动在以下时机运行：

1. 本地提交前运行单元测试
2. PR/MR 提交时运行全量测试
3. 合并到 develop 分支后运行集成测试
4. 发布前运行全量回归测试
