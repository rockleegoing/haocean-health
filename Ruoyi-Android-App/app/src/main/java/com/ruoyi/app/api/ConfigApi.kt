package com.ruoyi.app.api

object ConfigApi {

    // 成功
    const val SUCCESS: Int = 200
    // token 失效
    const val TOKEN_ERROR: String = "401"
    const val API_ERROR: String = "500"
    // var baseUrl = "http://192.168.1.138:8080"
    // const val baseUrl = "http://192.168.31.164:8080"
    const val baseUrl = "http://10.64.90.68:8080"
    const val blogUrl = "https://ruoyi-go.qiqjia.com"

    const val login = "/login";// 登录
    const val register = "/register"// 注册
    const val getInfo = "/getInfo"// 获取信息
    const val appSync = "/app/sync"// Android端数据预加载
    const val categoryList = "/app/category/list"// 行业分类列表
    const val unitList = "/app/unit/list"// 执法单位列表
    const val appUnitAdd = "/app/unit"  // 新增单位接口
    const val logout = "/logout"//退出
    const val getCaptchaImage = "/captchaImage"//获取验证码
    const val updatePwd = "/system/user/profile/updatePwd"// put修改密码
    const val getProfile = "/system/user/profile"//查询个人信息
    const val updateProfile = "/system/user/profile" //put 修改个人信息
    const val updateAvatar = "/system/user/profile/avatar"//post上传头像

    const val operlog = "/monitor/operlog/list"//操作日志
    const val noticeList = "/system/notice/list"//操作日志

    // 版本更新
    const val uploadApp = "https://gitee.com/OptimisticDevelopers/Ruoyi-Android-App/raw/ui_1.1.1/api/app_update.json"
    // 工作台
    const val appWork: String = "https://gitee.com/OptimisticDevelopers/Ruoyi-Android-App/raw/ui_1.1.1/api/app_work.json"
    // 首页
    const val appHome: String = "https://gitee.com/OptimisticDevelopers/Ruoyi-Android-App/raw/ui_1.1.1/api/app_home.json"
    // 首页底部菜单
    const val appHomeButtom: String = "https://gitee.com/OptimisticDevelopers/Ruoyi-Android-App/raw/ui_1.1.1/api/app_home_buttom.json"
}