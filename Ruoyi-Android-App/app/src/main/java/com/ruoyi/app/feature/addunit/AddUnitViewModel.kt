package com.ruoyi.app.feature.addunit

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ruoyi.app.data.database.entity.UnitEntity
import kotlinx.coroutines.launch

class AddUnitViewModel : ViewModel() {

    private val repository = AddUnitRepository()

    private val _saveResult = MutableLiveData<Boolean>()
    val saveResult: LiveData<Boolean> = _saveResult

    fun saveUnit(unit: UnitEntity) {
        viewModelScope.launch {
            val success = repository.saveUnit(unit)
            _saveResult.value = success
        }
    }
}
