package com.ruoyi.app.api.service
import com.drake.net.Get
import com.drake.net.Net
import com.drake.net.component.Progress
import com.drake.net.interfaces.ProgressListener
import com.ruoyi.app.api.ConfigApi
import com.xuexiang.xupdate.proxy.IUpdateHttpService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.io.File

class OKHttpUpdateHttpService : IUpdateHttpService {

    @OptIn(DelicateCoroutinesApi::class)
    override fun asyncGet(
        url: String,
        params: MutableMap<String, Any>,
        callBack: IUpdateHttpService.Callback
    ) {
        println("asyncGet")
        GlobalScope.launch {
            try {
                val result = Get<String>(ConfigApi.uploadApp) { params }.await()
                callBack.onSuccess(result)
            } catch (e: Exception) {
                callBack.onError(e)
            }
        }
    }

    override fun asyncPost(
        url: String,
        params: MutableMap<String, Any>,
        callBack: IUpdateHttpService.Callback
    ) {
        println("asyncPost")
        TODO("Not yet implemented")
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun download(
        url: String,
        path: String,
        fileName: String,
        callback: IUpdateHttpService.DownloadCallback
    ) {
        GlobalScope.launch {
            try {
                callback.onStart()
                val file = Get<File>(url) {
                    setDownloadFileName(fileName)
                    setDownloadDir(path)
                    setId(url)
                    setDownloadMd5Verify()
                    addDownloadListener(object : ProgressListener() {
                        override fun onProgress(p: Progress) {
                            val bytesWritten = p.currentByteCount
                            val total = p.totalByteCount
                            val progress = "%.2f".format(bytesWritten * 1.0f / total)
                            callback.onProgress(progress.toFloat(), total)
                        }
                    })
                }.await()
                callback.onSuccess(file)
            } catch (e: Exception) {
                callback.onError(e)
            }
        }
    }

    override fun cancelDownload(url: String) {
        Net.cancelId(url)
    }
}