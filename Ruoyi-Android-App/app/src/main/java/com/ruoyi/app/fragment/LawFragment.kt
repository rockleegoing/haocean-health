package com.ruoyi.app.fragment

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.ruoyi.app.databinding.FragmentLawBinding
import com.ruoyi.app.feature.law.repository.LawRepository
import com.ruoyi.app.model.Constant
import com.ruoyi.code.base.BaseBindingFragment
import com.therouter.TheRouter
import kotlinx.coroutines.launch

class LawFragment : BaseBindingFragment<FragmentLawBinding>() {

    private lateinit var repository: LawRepository

    companion object {
        @JvmStatic
        fun newInstance() = LawFragment()
    }

    override fun initView() {
        repository = LawRepository(requireContext())
        setupSearch()
        // TODO: 重新设计法律页面 UI，使用新的 LawEntity 和 LegalTermEntity
    }

    override fun initData() {
    }

    private fun setupSearch() {

    }
}

