# Android 法律法规页面实现计划

> **Goal:** 基于 Room 本地数据开发「法律法规」分组列表页面

---

## 1. 文件清单

### 新建文件
| 文件 | 职责 |
|-----|------|
| `app/src/main/java/com/ruoyi/app/feature/law/adapter/LawListAdapter.kt` | 分组列表适配器 |
| `app/src/main/java/com/ruoyi/app/feature/law/model/LawListItem.kt` | 列表数据模型 |
| `app/src/main/res/layout/item_law_group_header.xml` | 分组标题布局 |
| `app/src/main/res/layout/item_law_type_book.xml` | 书本书本项布局 |
| `app/src/main/res/drawable/bg_book_comprehensive.xml` | 综合类蓝色背景 |
| `app/src/main/res/drawable/bg_book_supervision.xml` | 监管类红色背景 |

### 修改文件
| 文件 | 变更 |
|-----|------|
| `fragment_law.xml` | 重新设计布局，使用 RecyclerView + GridLayoutManager |
| `LawFragment.kt` | 实现数据加载和列表绑定 |
| `LawTypeRepository.kt` | 添加 getChildrenByParentId() 方法 |

---

## 2. 数据模型

```kotlin
sealed class LawListItem {
    data class GroupHeader(val name: String) : LawListItem()
    data class LawItem(val id: Long, val name: String, val type: LawType) : LawListItem()
}

enum class LawType { COMPREHENSIVE, SUPERVISION }
```

---

## 3. 布局设计

### fragment_law.xml
- ConstraintLayout 根布局
- 顶部标题栏：LinearLayout，蓝色背景，标题「法律法规」+ 搜索图标
- RecyclerView：spanCount=4，分组列表

### item_law_group_header.xml
- 透明背景
- TextView：分组标题，加粗，16sp

### item_law_type_book.xml
- CardView 或 LinearLayout
- 背景色：综合类蓝色，监管类红色
- 圆角矩形
- TextView：居中白色文字，显示分类名称

---

## 4. 实施步骤

- [ ] **Step 1: 创建 Drawable 背景文件**
  - `bg_book_comprehensive.xml` - 蓝色背景 (#0081FF)
  - `bg_book_supervision.xml` - 红色背景 (#FF5050)

- [ ] **Step 2: 创建数据模型**
  - `LawListItem.kt` - sealed class 和 enum

- [ ] **Step 3: 创建布局文件**
  - `item_law_group_header.xml`
  - `item_law_type_book.xml`

- [ ] **Step 4: 创建适配器**
  - `LawListAdapter.kt` - 支持 GroupHeader 和 LawItem

- [ ] **Step 5: 修改 Repository**
  - `LawTypeRepository.kt` - 添加 getChildrenByParentId() 方法

- [ ] **Step 6: 重新设计 Fragment 布局**
  - `fragment_law.xml` - 使用 ConstraintLayout + RecyclerView

- [ ] **Step 7: 实现 Fragment 数据加载**
  - `LawFragment.kt` - 从 Room 加载数据并绑定

- [ ] **Step 8: 编译验证**
  - `./gradlew assembleDebug`
