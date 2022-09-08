package com.example.coroutines_imgdownloadapp

import android.content.ClipData
import android.content.ClipboardManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import com.example.coroutines_imgdownloadapp.databinding.ActivityMainBinding
import kotlinx.coroutines.*
import java.net.MalformedURLException
import java.net.URL

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "testLog"
    }

    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    fun copyImageLink(v: View) {
        try {
            val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
            val clipData = ClipData.newPlainText(
                "simple text",
                "https://www.hanbit.co.kr/data/editor/20200519155220_aglmvinv.png"
            )
            clipboard.setPrimaryClip(clipData)
            Toast.makeText(this, "링크를 복사했습니다.", Toast.LENGTH_SHORT).show()
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun loadImage(v: View) {
        CoroutineScope(Dispatchers.Main).launch {
            Log.d(TAG, "Dispatchers.Main.launch Block")
            showProgressBar()

            var bitmap: Bitmap? = null

            //join() : bitmap 초기화가 된 후 다음 launch 블럭을 실행
            CoroutineScope(Dispatchers.IO).launch {
                Log.d(TAG, "Dispatchers.IO.launch : load bitmap")
                try {
                    val url = URL(binding.editUrl.text.toString())
                    val inputStream = url.openStream()
                    bitmap = BitmapFactory.decodeStream(inputStream)
                }
                catch (e: MalformedURLException) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "올바르지 않은 형식의 URL 입니다.", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }
                catch (e: Exception) {
                    e.printStackTrace()
                    return@launch
                }
            }.join()

            //join()이 없었을 때, 아래 launch 블럭이 실행되기 전 dismissProgressBar() 가 실행됐었음
            launch {
                Log.d(TAG, "Dispatchers.Main.launch : setImageBitmap")
                binding.imagePreview.setImageBitmap(bitmap)
            }.join()

            launch {
                dismissProgressBar()
            }
        }
    }

    private suspend fun showProgressBar() {
        Log.d(TAG, "suspend showProgressBar")
        binding.progress.visibility = View.VISIBLE
    }

    private suspend fun dismissProgressBar() {
        Log.d(TAG, "suspend dismissProgressBar")
        binding.progress.visibility = View.GONE
    }
}
