package com.example.coroutines_imgdownloadapp


import android.util.Log
import com.example.coroutines_imgdownloadapp.MainActivity.Companion.TAG
import kotlinx.coroutines.*

//코루틴에서 결과 받기 예시_async/withContext
object CoroutineEX1 {
    private val myCoroutineScope = CoroutineScope(Dispatchers.Main)

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