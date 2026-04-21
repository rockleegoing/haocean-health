package com.ruoyi.code.utils

import android.annotation.TargetApi
import android.content.Context
import android.os.Build
import android.preference.PreferenceManager
import java.util.Locale

object LocaleHelper {
    private val SELECTED_LANGUAGE = "Locale.Helper.Selected.Language"

    /**
     * 将当前语言代码存入 SharedPreferences
     */
    fun persist(context: Context, language: String) {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        val editor = preferences.edit()
        editor.putString(SELECTED_LANGUAGE, language)
        editor.apply()
    }

    /**
     * 获取当前保存的语言代码，若无则返回默认的系统语言
     */
    fun getPersistedData(context: Context, defaultLanguage: String): String? {
        val preferences = PreferenceManager.getDefaultSharedPreferences(context)
        return preferences.getString(SELECTED_LANGUAGE, defaultLanguage)
    }

    /**
     * 更新 Context 的 Locale 设置，并返回新的 Context
     *
     * @param context      当前上下文
     * @param language     要切换到的语言代码，例如 "en" 或 "zh"
     * @return 更新后的 Context
     */
    fun setLocale(context: Context, language: String): Context {
        persist(context, language)
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(context, language)
        } else updateResourcesLegacy(context, language)
    }

    @TargetApi(Build.VERSION_CODES.N)
    private fun updateResources(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = context.resources.configuration
        config.setLocale(locale)
        // 此处创建新的 Context 使设置生效
        return context.createConfigurationContext(config)
    }

    @Suppress("deprecation")
    private fun updateResourcesLegacy(context: Context, language: String): Context {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val res = context.resources
        val config = res.configuration
        config.locale = locale
        res.updateConfiguration(config, res.displayMetrics)
        return context
    }
}