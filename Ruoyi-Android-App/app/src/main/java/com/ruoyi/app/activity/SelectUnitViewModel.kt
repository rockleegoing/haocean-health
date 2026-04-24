package com.ruoyi.app.activity

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.scopeNetLife
import com.ruoyi.app.api.repository.UnitRepository
import com.ruoyi.app.data.database.entity.UnitEntity

class SelectUnitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UnitRepository(application)

    val units = MutableLiveData<List<UnitEntity>>()
    val isLoading = MutableLiveData<Boolean>()

    fun loadUnits() {
        scopeNetLife {
            isLoading.value = true
            val localUnits = repository.getAllUnitsFromLocal()
            units.value = localUnits
            isLoading.value = false
        }
    }

    fun searchUnits(keyword: String) {
        scopeNetLife {
            val result = if (keyword.isEmpty()) {
                repository.getAllUnitsFromLocal()
            } else {
                repository.searchUnits(keyword)
            }
            units.value = result
        }
    }

    fun filterByCategory(categoryId: Long?) {
        scopeNetLife {
            val result = if (categoryId == null) {
                repository.getAllUnitsFromLocal()
            } else {
                repository.getUnitsByCategory(categoryId)
            }
            units.value = result
        }
    }

    fun filterByRegion(region: String?) {
        scopeNetLife {
            val result = if (region.isNullOrEmpty()) {
                repository.getAllUnitsFromLocal()
            } else {
                repository.getUnitsByRegion(region)
            }
            units.value = result
        }
    }

    fun getCurrentLocation(callback: (Double?, Double?) -> Unit) {
        // TODO: 获取设备当前位置
        callback(null, null)
    }
}
