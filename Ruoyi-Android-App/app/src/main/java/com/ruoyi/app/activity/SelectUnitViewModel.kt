package com.ruoyi.app.activity

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.scopeNetLife
import com.ruoyi.app.api.repository.UnitRepository
import com.ruoyi.app.data.database.entity.UnitEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SelectUnitViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = UnitRepository(application)

    val units = MutableLiveData<List<UnitEntity>>()
    val isLoading = MutableLiveData<Boolean>()
    val isLoadingMore = MutableLiveData<Boolean>()
    val isEmpty = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()

    private var currentPage = 0
    private val pageSize = 20
    private var hasMoreData = true
    private var isLoadingPage = false

    private var currentKeyword = ""
    private var currentCategoryId: Long? = null
    private var currentRegion: String? = null
    private var currentSupervisionType: String? = null

    private var currentLat: Double? = null
    private var currentLon: Double? = null

    private var searchJob: Job? = null

    fun loadUnits() {
        if (isLoadingPage) return
        scopeNetLife {
            isLoadingPage = true
            isLoading.value = true
            currentPage = 0
            hasMoreData = true

            val result = queryUnits()
            units.value = result
            isEmpty.value = result.isEmpty()
            hasMoreData = result.size >= pageSize
            currentPage++
            isLoading.value = false
            isLoadingPage = false
        }
    }

    fun loadMore() {
        if (isLoadingPage || !hasMoreData) return
        scopeNetLife {
            isLoadingPage = true
            isLoadingMore.value = true

            currentPage++
            val result = queryUnits()
            val currentList = units.value.orEmpty()
            val newList = currentList + result
            units.value = newList
            hasMoreData = result.size >= pageSize
            isLoadingMore.value = false
            isLoadingPage = false
        }
    }

    fun searchUnits(keyword: String) {
        currentKeyword = keyword
        searchJob?.cancel()
        searchJob = CoroutineScope(Dispatchers.Main).launch {
            delay(300) // 防抖 300ms
            isLoading.value = true
            currentPage = 0
            hasMoreData = true

            val result = queryUnits()
            units.value = result
            isEmpty.value = result.isEmpty()
            hasMoreData = result.size >= pageSize
            currentPage++
            isLoading.value = false
        }
    }

    fun filterByCategory(categoryId: Long?) {
        currentCategoryId = categoryId
        scopeNetLife {
            isLoading.value = true
            currentPage = 0
            hasMoreData = true

            val result = queryUnits()
            units.value = result
            isEmpty.value = result.isEmpty()
            hasMoreData = result.size >= pageSize
            currentPage++
            isLoading.value = false
        }
    }

    fun filterByRegion(region: String?) {
        currentRegion = region
        scopeNetLife {
            isLoading.value = true
            currentPage = 0
            hasMoreData = true

            val result = queryUnits()
            units.value = result
            isEmpty.value = result.isEmpty()
            hasMoreData = result.size >= pageSize
            currentPage++
            isLoading.value = false
        }
    }

    fun filterBySupervisionType(supervisionType: String?) {
        currentSupervisionType = supervisionType
        scopeNetLife {
            isLoading.value = true
            currentPage = 0
            hasMoreData = true

            val result = queryUnits()
            units.value = result
            isEmpty.value = result.isEmpty()
            hasMoreData = result.size >= pageSize
            currentPage++
            isLoading.value = false
        }
    }

    fun clearFilters() {
        currentCategoryId = null
        currentRegion = null
        currentSupervisionType = null
        currentKeyword = ""
        loadUnits()
    }

    @SuppressLint("MissingPermission")
    fun getCurrentLocation(callback: (Double?, Double?) -> Unit) {
        val context = getApplication<Application>()
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            callback(null, null)
            return
        }

        try {
            // 尝试获取最后一次已知位置
            val lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                ?: locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)

            if (lastKnownLocation != null) {
                currentLat = lastKnownLocation.latitude
                currentLon = lastKnownLocation.longitude
                callback(currentLat, currentLon)
            } else {
                // 请求新的位置更新
                val locationListener = object : LocationListener {
                    override fun onLocationChanged(location: Location) {
                        currentLat = location.latitude
                        currentLon = location.longitude
                        callback(currentLat, currentLon)
                        locationManager.removeUpdates(this)
                    }

                    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
                    override fun onProviderEnabled(provider: String) {}
                    override fun onProviderDisabled(provider: String) {}
                }

                try {
                    locationManager.requestSingleUpdate(LocationManager.NETWORK_PROVIDER, locationListener, null)
                } catch (e: Exception) {
                    try {
                        locationManager.requestSingleUpdate(LocationManager.GPS_PROVIDER, locationListener, null)
                    } catch (e: Exception) {
                        callback(null, null)
                    }
                }
            }
        } catch (e: Exception) {
            callback(null, null)
        }
    }

    fun getCurrentLatLon(): Pair<Double?, Double?> = Pair(currentLat, currentLon)

    private suspend fun queryUnits(): List<UnitEntity> {
        return when {
            currentKeyword.isNotEmpty() -> repository.searchUnits(currentKeyword)
            currentCategoryId != null -> repository.getUnitsByCategory(currentCategoryId!!)
            currentRegion != null -> repository.getUnitsByRegion(currentRegion!!)
            currentSupervisionType != null -> repository.getUnitsBySupervisionType(currentSupervisionType!!)
            else -> repository.getAllUnitsFromLocal()
        }
    }
}