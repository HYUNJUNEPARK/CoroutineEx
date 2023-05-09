package com.example.coroutine

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.coroutine.databinding.ActivityMainBinding
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "testLog"
    }
    private lateinit var binding: ActivityMainBinding
    private val viewModel: ImgViewModel by viewModels()
    private val myCoroutineScope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainActivity = this

        observeViewModel()
        //startTask()
    }

//Sample 1. Img Load
    fun onCopyImgLink() {
        try {
            if (viewModel.copyImgLink()) {
                Toast.makeText(this, "링크를 복사했습니다.", Toast.LENGTH_SHORT).show()
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun onLoadImage() {
        try {
            val url = binding.editUrl.text.toString()
            if (url.isNullOrEmpty()) { return }

            CoroutineScope(Dispatchers.IO).launch {
                viewModel.convertUrlToBitmap(url) { bitmap ->

                    Toast.makeText(this@MainActivity, "Failed Load Image", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "onLoadImage: $bitmap")
                    Log.d(TAG, "thread name: ${Thread.currentThread().name}")
                }

//                viewModel.convertUrlToBitmap(url).let { bitmap ->
//                    //main thread
//                    withContext(Dispatchers.Main) {
//                        if (bitmap == null) {
//                            Toast.makeText(this@MainActivity, "Failed Load Image", Toast.LENGTH_SHORT).show()
//                        }
//                        binding.imagePreview.setImageBitmap(bitmap)
//                    }
//                }


            }


        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun observeViewModel() {
        viewModel.loading.observe(this) { loadingState ->
            if (loadingState) {
                binding.progress.visibility = View.VISIBLE
            } else {
                binding.progress.visibility = View.GONE
            }
        }
    }

    //Sample 2. launch vs Async
    fun startTask() {
        myCoroutineScope.launch(Dispatchers.Default) {
            /* 코루틴에서 결과를 받아오려면 async 빌더로 코루틴을 실행시켜야하는데
            startTask()는 suspend 함수가 아니기 때문에 performSlowTaskAsync() 에서 async 빌더를 사용 */
            val asyncResult = performTaskAsync().await()
            Log.d(TAG, "$asyncResult")

            /* withContext : 부모 코루틴에 의해 사용되던 컨텍스트와 다른 컨텍스트에서 코루틴을 실행시킴
            코루틴에서 결과를 반환할 때 async 대신 사용할 수 있음 */
            val withContextResult = performTaskWithContext()
            Log.d(TAG, "$withContextResult")
        }
    }

    private suspend fun performTaskAsync(): Deferred<String> =
        //Deferred<String> : 향후 언젠가 String 타입의 값을 반환한다는 의미
        myCoroutineScope.async(Dispatchers.Default) {
            Log.i(TAG, "async() : before count")
            for (i in 0..5) {
                Log.d(TAG, "async() : $i")
                delay(500)
            }
            Log.i(TAG, "async() : after count")
            return@async "async() : Count Finish"
        }

    //withContext : 부모 코루틴에 의해 사용되던 컨텍스트와 다른 컨텍스트에서 코루틴을 실행시킬 수 있다.
    //코루틴에서 결과를 반환할 때 async 대신 사용한 예시
    private suspend fun performTaskWithContext(): String =
        withContext(Dispatchers.Default) {
            Log.i(TAG, "withContext() : before count")
            for (i in 0..5) {
                Log.d(TAG, "withContext() : $i")
                delay(500)
            }
            Log.i(TAG, "withContext() : after count")
            return@withContext "withContext() : Count Finish"
        }
}
