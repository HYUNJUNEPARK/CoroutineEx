package com.example.coroutine

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.URL

class ImgViewModel(application: Application): AndroidViewModel(application) {
    private val context = getApplication<Application>().applicationContext
    private val clipDataLabel = "simple_clip"
    private val clipBoardText = "https://www.hanbit.co.kr/data/editor/20200519155220_aglmvinv.png"

    val loading: LiveData<Boolean> get() = _loading
    private var _loading = MutableLiveData<Boolean>()

    init {
        _loading.value = false
    }

    /**
     * 클립보드에 지정 URL 링크를 복사
     */
    fun copyImgLink(): Boolean {
        try {
            val clipboard = context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(
                clipDataLabel,
                clipBoardText
            )
            clipboard.setPrimaryClip(clipData)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    /**
     * 이미지 URL 을 Bitmap 으로 바꾼다.
     * Dispatchers.IO 에서 호출
     */
    suspend fun convertUrlToBitmap(url: String, callback:(Bitmap)->Unit) {
        try {
            startLoading()

            val url = URL(url)
            val inputStream = url.openStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)

            finishLoading()
            callback(bitmap)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun startLoading() {
        withContext(Dispatchers.Main) {
            _loading.value = true
        }
    }

    private suspend fun finishLoading() {
        withContext(Dispatchers.Main) {
            _loading.value = false
        }
    }
}