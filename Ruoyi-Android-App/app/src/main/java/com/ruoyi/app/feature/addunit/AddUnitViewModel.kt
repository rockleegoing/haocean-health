package com.ruoyi.app.feature.addunit

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ruoyi.app.data.database.entity.UnitEntity
import kotlinx.coroutines.launch

class AddUnitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AddUnitRepository(application)

    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    fun saveUnit(unit: UnitEntity) {
        viewModelScope.launch {
            val success = repository.saveUnit(unit)
            _saveResult.value = success
        }
    }
}
