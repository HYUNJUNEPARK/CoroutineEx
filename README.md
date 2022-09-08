# Coroutine

---
1. <a href = "#content1">코루틴(Coroutine)</a></br>
2. <a href = "#content2">디스패처</a></br>
3. <a href = "#content3">코루틴 스코프 함수</a></br>
3.1 Launch{ }</br>
_3.1.1. cancel()</br>
_3.1.2 join()</br>
3.2 async{ }</br>
_3.2.1 await()</br>
3.3 suspend</br>
3.4 withContext()</br>

* <a href = "#ref">참고링크</a>
---
><a id = "content1">**1. 코루틴(Coroutine)**</a></br>

**1.1 기능**</br>
동시성 프로그래밍 개념을 코틀린에 도입</br>
스레드는 코루틴이 실행되는 공간을 제공하고 하나의 스레드에 여러개의 코루틴이 존재할 수 있음</br>
같은 스레드에 있는 코루틴간 작업을 넘겨받는 중에도 공간을 제공한 스레드는 계속 움직임</br>
스레드 간 작업을 주고 받는 것(컨텍스트 스위칭)보다 하나의 스레드 안에 있는 코루틴 간 작업을 주고 받는 것이 성능 저하 방지</br>
```kotlin
//build
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9'
```

**1.2 코루틴 실행 스코프**</br>
1.2.1 글로벌 스코프(GlobalScope)</br>
앱의 생명 주기와 함께 동작하고 별도의 생명 주기 관리가 필요하지 않음</br>
앱의 시작부터 종료까지 장시간 실행되어야하는 코루틴이 있다면 GlobalScope 에 작성</br>

1.2.2 코루틴 스코프(CoroutineScope)</br>
필요할 때만 열고 완료 되면 닫는 코루틴을 담는 스코프 (ex. 버튼을 클릭해서 서버의 정보를 갖고오거나 파일 오픈)</br>
디스패처를 코루틴 스코프의 괄호 안에 넣어 코루틴이 실행될 스레드를 지정</br>
```kotlin
//코루틴 스코프 예시
CoroutineScope(Dispatchers.IO).launch {
    //... 
}

```
<br></br>
<br></br>




><a id = "content2">**2. 디스패처**</a></br>

**2.1 기능**</br>
코루틴 스코프 내부에서 실행할 코드의 성격에 맞게 사용해야하는 도구</br>

**2.2 종류**</br>
2.2.1 **Dispatchers.IO** : 이미지 다운, 파일 입출력 등 입출력에 최적화 되어있는 디스패처.</br>
2.2.2 **Dispatchers.Main** : UI 와 상호작용에 최적화되어 있는 디스패처. 텍스트뷰에 글자를 입력해야할 경우 Main 컨텍스트 사용.</br>
2.2.3 **Dispatchers.Default** : CPU 를 많이 사용하는 작업을 백그라운드 스레드에서 실행하도록 최적화되어있는 디스패처.</br>
2.2.4 **Dispatchers.Unconfined**</br>
<br></br>
<br></br>




><a id = "content3">**3. 코루틴 스코프 함수**</a></br>

**3.1 launch()**</br>
호출하는 것만으로도 코루틴 생성</br>
반환 값을 변수에 저장해두는 상태 관리용으로 cancel(), join()와 조합해 사용</br>
코루틴 스코프 안에 선언된 여러개의 launch 블록은 모두 새로운 코루틴으로 분기 되면서 동시에 처리되기 때문에 순서를 정할 수 없음</br>
현재 스레드 중단 없이 코루틴을 즉시 시작 시킨다. 결과를 호출한 쪽에 반환하지 않는다. suspend 함수가 아닌 **일반 함수 안에서 suspend 함수를 호출할 때**와 **코루틴의 결과 처리가 필요없을 때 사용**</br> 
3.1.1 cancel()</br>
코루틴의 동작을 멈춤</br>
```kotlin
job = CoroutineScope(Dispatchers.Default).launch() {
    val job1 = launch {
        for(i in 0..10) {
            delay(500)
            Log.d("코루틴", "결과 = $i")
        }
    }
}

binding.btnJobStop.setOnClickListener {
    job?.cancel()
}
```
3.1.2 join()</br>
launch 블록 뒤에 join()을 사용하면 코루틴이 순차적으로 실행됨</br>
```kotlin
CoroutineScope(Dispatchers.Default).launch() {
    //launch1
    launch {
        for(i in 0..2) {
           delay(500)
           Log.d("코루틴", "launch1 = $i")
        }
    }.join()

    //launch2
    launch {
        for(i in 0..2) {
           delay(500)
           Log.d("코루틴", "launch2 = $i")
        }
    }
}
/* 결과 launch1 -> launch2
launch1 = 0
launch1 = 1
launch1 = 2
launch2 = 0
launch2 = 1
launch2 = 2 */
```

**3.2 async**</br>
상태 관리와 연산 결과까지 반환 받을 수 있음</br>
현재 스레드 중단 없이 코루틴을 즉시 시작 시킨다. 호출 쪽에서 await()를 통해 코루틴 결과를 기다릴 수 있다. **병행으로 실행될 필요가 있는 다수의 코루틴을 사용할 때 사용**</br>
**async 빌더는 suspend 함수 내부에서만 사용 가능**</br>
3.2.1 await()</br>
코루틴을 async 로 선언하고 결괏값을 처리하는 곳에 await() 함수를 사용하면 결과 처리가 완료된 후에 await() 를 호출한 줄의 코드가 실행됨</br>
```kotlin
CoroutineScope(Dispatchers.Default).async {
    val value1 = async {
        delay(5000)
        350 //return
    }
    val value2 = async {
        delay(1000)
        200 //return
    }
    Log.d("코루틴", "연산 결과 = ${value1.await() + value2.await()}")//결과 처리가 완료된 후에 await() 를 호출한 줄의 코드가 실행됨
}
```

**3.3 suspend**</br>
일반 함수를 코루틴으로 만드는 키워드</br>
코루틴 안에서 suspend 키워드로 선언된 함수가 호출되면 이전까지의 코드 실행이 멈추고 suspend 함수의 처리가 완료된 후에 멈춰 있던 코드의 다음 코드부터 실행됨</br>
suspend 키워드가 있으면 코루틴 스코프 안에서 자동으로 백그라운드 스레드처럼 동작</br>
```kotlin
private suspend fun readFile() {
    //...
}

CoroutineScope(Dispatcher.IO).launch {
    {코드1}
    readFile()
    {코드2}
}
/*{코드1} 부분이 실행되다가 readFile()이 실행되면 {코드1} 동작 스레드가 잠시 멈춤 #{코드1}의 상태값은 저장됨
-> readFile() 작업이 종료
-> {코드1}의 상태값 복구 후 작업을 이어서 진행
{코드1}의 실행은 잠시 멈추지만 스레드의 중단은 없음*/
```

**3.4 withContext**</br>
디스패처를 분리시키는 키워드</br>
suspend 함수를 코루틴 스코프에서 호출할 때 호출한 스코프와 다른 디스패처를 사용할 경우 사용</br>
(ex. Main 디스패처에서 UI 를 제어해야하는 데 호출된 suspend 함수는 파일을 읽어와야하는 경우)</br>
```kotlin
CoroutineScope(Dispatchers.Main).launch {
    //코드1
    val result = withContext(Dispatchers.IO) {
        readFile() //suspend 함수
    }
    //코드2
    Log.d("코루틴", "파일결과=$result")
}

private suspend fun readFile() : String {
    return "파일내용"
}
```
<br></br>
<br></br>
---

><a id = "ref">**참고링크**</a></br>

코틀린 코루틴 한번에 끝내기</br>
https://whyprogrammer.tistory.com/596</br>

이것이 안드로이드다. 10장 스레드와 코루틴</br>




