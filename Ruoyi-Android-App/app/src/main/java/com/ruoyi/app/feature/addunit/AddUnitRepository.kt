package com.ruoyi.app.feature.addunit

import android.content.Context
import com.ruoyi.app.data.database.AppDatabase
import com.ruoyi.app.data.database.entity.UnitEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 添加单位仓库
 * 负责单位数据的本地数据库操作
 */
class AddUnitRepository(private val context: Context) {

    private val unitDao = AppDatabase.getInstance(context).unitDao()

    /**
     * 保存单位到本地数据库
     * @param unit 单位实体
     * @return 保存是否成功
     */
    suspend fun saveUnit(unit: UnitEntity): Boolean {
        return try {
            withContext(Dispatchers.IO) {
                unitDao.insertUnit(unit)
            }
            true
        } catch (e: Exception) {
            false
        }
    }
}
