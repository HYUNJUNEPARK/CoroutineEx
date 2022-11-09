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

    val loading: LiveData<Boolean>
        get() = _loading
    private var _loading = MutableLiveData<Boolean>()

    init {
        _loading.value = false
    }

    //클립보드에 지정 URL 링크를 복사
    fun copyImgLink(): Boolean {
        try {
            val clipboard = context.getSystemService(AppCompatActivity.CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(
                "simple clip",
                "https://www.hanbit.co.kr/data/editor/20200519155220_aglmvinv.png"
            )
            clipboard.setPrimaryClip(clipData)
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return false
    }

    suspend fun loadImg(url: String): Bitmap? {
        //DefaultDispatcher-worker-1
        try {
            //main thread
            withContext(Dispatchers.Main) {
                _loading.value = true
            }

            val url = URL(url)
            val inputStream = url.openStream()
            val bitmap = BitmapFactory.decodeStream(inputStream)

            //main thread
            withContext(Dispatchers.Main) {
                _loading.value = false
            }
            return bitmap
        } catch (e: Exception) {
            e.printStackTrace()
        }
        withContext(Dispatchers.Main) {
            _loading.value = false
        }
        return null
    }
}