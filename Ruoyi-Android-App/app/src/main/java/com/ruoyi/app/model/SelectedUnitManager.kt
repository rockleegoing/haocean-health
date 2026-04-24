package com.ruoyi.app.model

import com.tencent.mmkv.MMKV

/**
 * 当前选中单位管理器
 * 使用 MMKV 持久化存储
 */
object SelectedUnitManager {

    private const val KEY_SELECTED_UNIT_ID = "selected_unit_id"
    private const val KEY_SELECTED_UNIT_NAME = "selected_unit_name"
    private const val KEY_SELECTED_CATEGORY_ID = "selected_category_id"
    private const val KEY_SELECTED_CATEGORY_NAME = "selected_category_name"

    fun saveSelectedUnit(unitId: Long, unitName: String, categoryId: Long?, categoryName: String?) {
        MMKV.defaultMMKV().encode(KEY_SELECTED_UNIT_ID, unitId)
        MMKV.defaultMMKV().encode(KEY_SELECTED_UNIT_NAME, unitName)
        MMKV.defaultMMKV().encode(KEY_SELECTED_CATEGORY_ID, categoryId ?: -1)
        MMKV.defaultMMKV().encode(KEY_SELECTED_CATEGORY_NAME, categoryName ?: "")
    }

    fun getSelectedUnitId(): Long? {
        val id = MMKV.defaultMMKV().decodeLong(KEY_SELECTED_UNIT_ID, -1)
        return if (id == -1L) null else id
    }

    fun getSelectedUnitName(): String? {
        return MMKV.defaultMMKV().decodeString(KEY_SELECTED_UNIT_NAME)
    }

    fun getSelectedCategoryId(): Long? {
        val id = MMKV.defaultMMKV().decodeLong(KEY_SELECTED_CATEGORY_ID, -1)
        return if (id == -1L) null else id
    }

    fun getSelectedCategoryName(): String? {
        return MMKV.defaultMMKV().decodeString(KEY_SELECTED_CATEGORY_NAME)
    }

    fun clearSelectedUnit() {
        MMKV.defaultMMKV().removeValueForKey(KEY_SELECTED_UNIT_ID)
        MMKV.defaultMMKV().removeValueForKey(KEY_SELECTED_UNIT_NAME)
        MMKV.defaultMMKV().removeValueForKey(KEY_SELECTED_CATEGORY_ID)
        MMKV.defaultMMKV().removeValueForKey(KEY_SELECTED_CATEGORY_NAME)
    }

    fun hasSelectedUnit(): Boolean {
        return getSelectedUnitId() != null
    }
}
