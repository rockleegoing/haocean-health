package com.ruoyi.app.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.recyclerview.widget.LinearLayoutManager
import com.ruoyi.app.adapter.UnitListAdapter
import com.ruoyi.app.databinding.ActivitySelectUnitBinding
import com.ruoyi.app.model.Constant
import com.ruoyi.app.model.SelectedUnitManager
import com.ruoyi.app.utils.DistanceUtils
import com.ruoyi.code.base.BaseBindingActivity
import com.ruoyi.code.utils.clickDelay
import com.therouter.TheRouter
import com.therouter.router.Route

@Route(path = Constant.selectUnitRoute)
class SelectUnitActivity : BaseBindingActivity<ActivitySelectUnitBinding>() {

    companion object {
        fun startActivity(context: Context) {
            val intent = Intent(context, SelectUnitActivity::class.java)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }
    }

    private lateinit var unitAdapter: UnitListAdapter
    private val viewModel = SelectUnitViewModel(application)

    private var currentLat: Double? = null
    private var currentLon: Double? = null

    override fun initView() {
        binding.titlebar.setOnTitleBarListener(object : com.hjq.bar.OnTitleBarListener {
            override fun onLeftClick(titleBar: com.hjq.bar.TitleBar?) {
                finish()
            }
        })

        unitAdapter = UnitListAdapter { unit ->
            SelectedUnitManager.saveSelectedUnit(
                unit.unitId,
                unit.unitName,
                unit.industryCategoryId,
                unit.industryCategoryName
            )
            TheRouter.build(Constant.mainRoute).navigation()
            finish()
        }
        binding.rvUnits.apply {
            layoutManager = LinearLayoutManager(this@SelectUnitActivity)
            adapter = unitAdapter
        }

        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                viewModel.searchUnits(s?.toString() ?: "")
            }
        })

        binding.tvSkip.clickDelay {
            TheRouter.build(Constant.mainRoute).navigation()
            finish()
        }
    }

    override fun initData() {
        viewModel.getCurrentLocation { lat, lon ->
            currentLat = lat
            currentLon = lon
            loadUnits()
        }

        viewModel.units.observe(this) { units ->
            val sortedUnits = DistanceUtils.sortUnitsByDistance(units, currentLat, currentLon)
            unitAdapter.submitList(sortedUnits)
        }
    }

    private fun loadUnits() {
        viewModel.loadUnits()
    }
}
