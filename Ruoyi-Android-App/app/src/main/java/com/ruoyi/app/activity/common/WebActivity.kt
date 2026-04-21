package com.ruoyi.app.activity.common

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.view.ViewGroup
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import com.hjq.bar.OnTitleBarListener
import com.hjq.bar.TitleBar
import com.ruoyi.app.databinding.ActivityWebBinding
import com.ruoyi.app.widget.AndroidToJs
import com.ruoyi.code.base.BaseBindingActivity

// 公共web 页面端
class WebActivity : BaseBindingActivity<ActivityWebBinding>() {

    companion object {
        private const val INTENT_KEY_IN_TITLE: String = "WebActivityTitle"
        private const val INTENT_KEY_IN_URL: String = "WebActivityUrl"
        private const val INTENT_KEY_IN_IS_HIDE_TITLE: String = "WebActivityIsHideTitle"

        // 启动app
        fun startActivity(context: Context, title: String, url: String) {
            startActivity(context, title, url,false)
        }

        fun startActivity(context: Context, title: String, url: String, isHideTitle: Boolean) {
            val intent = Intent(context, WebActivity::class.java)
            intent.putExtra(INTENT_KEY_IN_TITLE, title)
            intent.putExtra(INTENT_KEY_IN_URL, url)
            intent.putExtra(INTENT_KEY_IN_IS_HIDE_TITLE, isHideTitle)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    private var url: String? = ""

    /**
     * web 端 进行图片和视频选择交互
     */
    private var fileUploadCallback: ValueCallback<Array<Uri>>? = null

    private val androidToJs by lazy {
        AndroidToJs()
    }

    override fun initView() {
        val title = intent.getStringExtra(INTENT_KEY_IN_TITLE)
        val isHideTitle = intent.getBooleanExtra(INTENT_KEY_IN_IS_HIDE_TITLE, false)
        url = intent.getStringExtra(INTENT_KEY_IN_URL)
        binding.titlebar.title = title
        if (isHideTitle) {
            binding.titlebar.visibility = View.GONE
        }
        binding.titlebar.setOnTitleBarListener(object : OnTitleBarListener {
            override fun onLeftClick(titleBar: TitleBar?) {
                finish()
            }
        })
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun initData() {
        val webSettings: WebSettings = binding.webview.settings
        webSettings.javaScriptEnabled = true //支持js脚本
        webSettings.domStorageEnabled = true //默认是不开启DOM Storage缓存
        webSettings.useWideViewPort = true //将图片调整到适合webview的大小
        webSettings.loadWithOverviewMode = false // 缩放至屏幕的大小
        webSettings.setSupportZoom(false) //支持缩放，默认为true。是下面那个的前提。
        webSettings.builtInZoomControls = false //设置内置的缩放控件。若为false，则该WebView不可缩放
        webSettings.displayZoomControls = true //隐藏原生的缩放控件
        webSettings.cacheMode = WebSettings.LOAD_CACHE_ELSE_NETWORK //关闭webview中缓存
        webSettings.allowFileAccess = true //设置可以访问文件
        webSettings.javaScriptCanOpenWindowsAutomatically = true //支持通过JS打开新窗口
        webSettings.loadsImagesAutomatically = true //支持自动加载图片
        webSettings.defaultTextEncodingName = "utf-8" //设置编码格式
        url?.let { binding.webview.loadUrl(it) }
        binding.webview.webViewClient = object : WebViewClient() {
            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                view.loadUrl(url)
                return true
            }
        }
        // 解决 vue 或 html 里面 input 组件 选择图片 进行上传使用
        binding.webview.webChromeClient = object : WebChromeClient() {
            override fun onShowFileChooser(
                webView: WebView,
                filePathCallback: ValueCallback<Array<Uri>>,
                fileChooserParams: FileChooserParams
            ): Boolean {
                fileUploadCallback = filePathCallback;
                // 创建一个文件选择的Intent
                val intent = fileChooserParams.createIntent();
                try {
                    startActivityForResult(intent, 1);
                } catch (e: Exception) {
                    fileUploadCallback = null;
                    return false;
                }
                return true
            }
        }
        // html 或 vue 里面进行 ** window.Android.chooseFile **
        binding.webview.addJavascriptInterface(androidToJs, "Android") //AndroidtoJS类对象映射到js的test对象
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1) {
            if (fileUploadCallback == null) {
                return;
            }

            var results: Array<Uri>? = null
            // 检查选择的文件是否为空
            if (resultCode == RESULT_OK && data != null) {
                val dataString = data.dataString;
                if (dataString != null) {
                    results = arrayOf(Uri.parse(dataString))
                }
            }

            // 传递选择的文件给WebView
            fileUploadCallback?.onReceiveValue(results);
            fileUploadCallback = null;
        }
    }

}