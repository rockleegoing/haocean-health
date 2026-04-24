package com.ruoyi.app.model

object Constant {

    // 公共变量
    const val theme_default_color = "#A4A4A4"
    const val theme_select_color = "#0081ff"
    const val site_url = "https://ruoyi-go.qiqjia.com"
    const val site_phone = "400-999-9999"
    const val site_email = "ziqijiatj@126.com"
    const val copy_right = "Copyright copy; 2026 ruoyi.vip All Rights Reserved."

    // 路由配置
    const val mainRoute = "http://com.ruoyi/home"
    const val logsRoute = "http://com.ruoyi/log"
    const val messagesRoute = "http://com.ruoyi/message"
    const val settingRoute = "http://com.ruoyi/setting"
    const val activationRoute = "http://com.ruoyi/activation"
    const val homeFragment = "http://com.ruoyi/home/fragment"
    const val workFragment = "http://com.ruoyi/work/fragment"
    const val mineFragment = "http://com.ruoyi/mine/fragment"
    const val loginRoute = "http://com.ruoyi/login"
    const val syncWaitRoute = "http://com.ruoyi/syncWait"
    const val selectUnitRoute = "http://com.ruoyi/selectUnit"

    // 便捷执法模块路由
    const val recordListRoute = "/lawenforcement/record/list"
    const val recordDetailRoute = "/lawenforcement/record/detail"
    const val recordEditRoute = "/lawenforcement/record/edit"

    // 首页动态设置
    const val mainStyleDefault = "1" // 1 首页图片+文字
    const val mainStyleAlipay = "2" //  2 首页图片
    const val mainStyle = mainStyleDefault // 1 首页图片和文字 2 首页图片，仿照支付宝
    const val isPersonalization = false //首页个性化，从网络获取
    // 是否显示水印
    const val isShowWaterMark = true

}