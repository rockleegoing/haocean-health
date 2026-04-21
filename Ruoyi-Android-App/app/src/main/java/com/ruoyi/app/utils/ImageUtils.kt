package com.ruoyi.app.utils

import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.os.FileUtils
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*


object ImageUtils {

    /*选择图片*/
    fun openAlbum(): Intent {
        val intent = Intent(Intent.ACTION_PICK, null)
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
        return intent
    }

    /**
     * 创建一个拍照图片文件路径
     */
    fun createCameraFile(video: Boolean): File {
        var folder = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM),
            "Camera"
        )
        if (!folder.exists() || !folder.isDirectory) {
            if (!folder.mkdirs()) {
                folder = Environment.getExternalStorageDirectory()
            }
        }
        val file = File(
            folder, ((if (video) "VID" else "IMG") + "_" +
                    SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date()) +
                    (if (video) ".mp4" else ".jpg"))
        )
        if (file.exists()) file.delete()
        try {
            file.parentFile?.mkdirs()
            file.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    fun fileToUri(context: Context, file: File): Uri? {
        val uri: Uri? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            FileProvider.getUriForFile(
                context, "com.ruoyi.app.provider",
                file
            )
        } else {
            Uri.fromFile(file)
        }
        return uri
    }

    fun uriToFile(context: Context, uri: Uri): File? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return getRealFilePath(context, uri);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT
            && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q
            && DocumentsContract.isDocumentUri(context, uri)
        ) {
            if (isExternalStorageDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                if ("primary".equals(type, ignoreCase = true)) {
                    return File(Environment.getExternalStorageDirectory().toString() + "/" + split[1])
                }
            } else if (isDownloadsDocument(uri)) {
                val id = DocumentsContract.getDocumentId(uri)
                val contentUri = ContentUris.withAppendedId(
                    Uri.parse("content://downloads/public_downloads"),
                    java.lang.Long.valueOf(id)
                )
                return getDataColumn(context, contentUri, null, null)
            } else if (isMediaDocument(uri)) {
                val docId = DocumentsContract.getDocumentId(uri)
                val split = docId.split(":").toTypedArray()
                val type = split[0]
                var contentUri: Uri? = null
                if ("image" == type) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                } else if ("video" == type) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI
                } else if ("audio" == type) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
                }
                val selection = MediaStore.Images.Media._ID + "=?"
                val selectionArgs = arrayOf(split[1])
                return getDataColumn(context, contentUri!!, selection, selectionArgs)
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q){
            return uriToFileApiQ(context, uri);
        } else if ("content".equals(uri.getScheme(), true)) {
            // Return the remote address
            if (isGooglePhotosUri(uri)) {
                return File(uri.getLastPathSegment())
            }
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equals(uri.getScheme(), true)) {
            return uri.path?.let { File(it) }
        }
        return null;
    }

    //此方法 只能用于4.4以下的版本
    private fun getRealFilePath(context: Context, uri: Uri?): File? {
        if (null == uri) {
            return null
        }
        val scheme = uri.scheme
        var data: String? = null
        if (scheme == null) {
            data = uri.path
        } else if (ContentResolver.SCHEME_FILE == scheme) {
            data = uri.path
        } else if (ContentResolver.SCHEME_CONTENT == scheme) {
            val projection = arrayOf(MediaStore.Images.ImageColumns.DATA)
            val cursor = context.contentResolver.query(uri, projection, null, null, null)
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    val index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA)
                    if (index > -1) {
                        data = cursor.getString(index)
                    }
                }
                cursor.close()
            }
        }
        return File(data)
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    private fun isExternalStorageDocument(uri: Uri): Boolean {
        return "com.android.externalstorage.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    private fun isDownloadsDocument(uri: Uri): Boolean {
        return "com.android.providers.downloads.documents" == uri.authority
    }

    private fun getDataColumn(
        context: Context,
        uri: Uri,
        selection: String?,
        selectionArgs: Array<String>?
    ): File? {
        var cursor: Cursor? = null
        val column = MediaStore.Images.Media.DATA
        val projection = arrayOf(column)
        try {
            cursor = context.contentResolver.query(uri, projection, selection, selectionArgs, null)
            if (cursor != null && cursor.moveToFirst()) {
                val index: Int = cursor.getColumnIndexOrThrow(column)
                return File(cursor.getString(index))
            }
        } finally {
            if (cursor != null) {
                cursor.close()
            }
        }
        return null
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    private fun isMediaDocument(uri: Uri): Boolean {
        return "com.android.providers.media.documents" == uri.authority
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    private fun isGooglePhotosUri(uri: Uri): Boolean {
        return "com.google.android.apps.photos.content" == uri.authority
    }


    /**
     * Android 10 以上适配 另一种写法
     * @param context
     * @param uri
     * @return
     */
    @SuppressLint("Range")
    private fun getFileFromContentUri(context: Context, uri: Uri?): File? {
        if (uri == null) {
            return null
        }
        val filePath: String
        val filePathColumn =
            arrayOf(MediaStore.MediaColumns.DATA, MediaStore.MediaColumns.DISPLAY_NAME)
        val contentResolver = context.contentResolver
        val cursor: Cursor? = contentResolver.query(
            uri, filePathColumn, null,
            null, null
        )
        if (cursor != null) {
            cursor.moveToFirst()
            try {
                filePath = cursor.getString(cursor.getColumnIndex(filePathColumn[0]))
                return File(filePath)
            } catch (e: Exception) {
            } finally {
                cursor.close()
            }
        }
        return null
    }

    /**
     * Android 10 以上适配
     * @param context
     * @param uri
     * @return
     */
    @SuppressLint("Range")
    @RequiresApi(api = Build.VERSION_CODES.Q)
    private fun uriToFileApiQ(context: Context, uri: Uri): File? {
        var file: File? = null
        //android10以上转换
        if (uri.scheme == ContentResolver.SCHEME_FILE) {
            file = File(uri.path)
        } else if (uri.scheme == ContentResolver.SCHEME_CONTENT) {
            //把文件复制到沙盒目录
            val contentResolver = context.contentResolver
            val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
            if (cursor!!.moveToFirst()) {
                val displayName: String =
                    cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                try {
                    val `is`: InputStream? = contentResolver.openInputStream(uri)
                    val cache = File(
                        context.externalCacheDir!!.absolutePath,
                        Math.round((Math.random() + 1) * 1000).toString() + displayName
                    )
                    val fos = FileOutputStream(cache)
                    FileUtils.copy(`is`!!, fos)
                    file = cache
                    fos.close()
                    `is`?.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return file
    }

}