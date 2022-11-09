package com.example.coroutine

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.coroutine.databinding.ActivityMainBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "testLog"
    }
    private lateinit var binding: ActivityMainBinding
    private val viewModel: ImgViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.mainActivity = this

        //CoroutineEx.startTask()
        observeViewModel()
    }

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
        //main thread
        try {
            val url = binding.editUrl.text.toString()
            if (url.isNullOrEmpty()) { return }

            //DefaultDispatcher-worker-1
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.loadImg(url).let { bitmap ->
                    //main thread
                    withContext(Dispatchers.Main) {
                        binding.imagePreview.setImageBitmap(bitmap)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun observeViewModel() {
        viewModel.loading.observe(this) { loadingState ->
            if (loadingState) {
                binding.progress.visibility = View.VISIBLE
            } else {
                binding.progress.visibility = View.GONE
            }
        }
    }
}
