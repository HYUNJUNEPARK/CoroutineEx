# Coroutine

---

<img src="ref/CoroutineEx_ImgDownloadApp_img.png" height="400"/>

---
1. <a href = "#content1">코루틴(Coroutine)</a></br>
-코루틴 실행 스코프(GlobalScope/CoroutineScope/runBlocking)</br>
2. <a href = "#content2">CoroutineScope 디스패처</a></br>
-Dispatchers.IO/Dispatchers.Main/Dispatchers.Default</br>
3. <a href = "#content3">코루틴 스코프 함수</a></br>
launch{},async{}</br>
4. <a href = "#content4">Blocking 함수</a></br>
join(), await(), withTimeoutOrNull(mills)</br>
5. <a href = "#content5">suspend</a></br>
6. <a href = "#content6">withContext</a></br>

* <a href = "#ref">참고링크</a>
---

><a id = "content1">**1. 코루틴(Coroutine)**</a></br>

**0. Build**
`implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-android:1.3.9'`

**1.1 기능**</br>
-동시성(Async) 프로그래밍 개념을 코틀린에 도입</br>
-스레드는 코루틴이 실행되는 공간을 제공하고 하나의 스레드에 여러개의 코루틴이 존재할 수 있음</br>
-같은 스레드에 있는 코루틴간 작업을 넘겨받는 중에도 공간을 제공한 스레드는 계속 움직임</br>
-스레드 간 작업을 주고 받는 것(컨텍스트 스위칭, Context Switching)보다 하나의 스레드 안에 있는 코루틴 간 작업을 주고 받는 것이 성능 저하 방지</br>
-코루틴은 특정 스레드에 종속적이지 않으며 시스템에 의해 상황에 맞게 적절한 스레드에서 실행됨. 스레드가 blocking 되어 낭비되는 일 없이 루틴을 교환해가며 작업</br>
-코루틴이 빠른 이유 : 작업마다 스레드를 생성하는 것이 아니라 스레드 안에서 루틴을 만들고 교환하여 자원 낭비가 덜 하기 때문</br>

**1.2 코루틴 실행 스코프**</br>
1.2.1 글로벌 스코프(GlobalScope)</br>
-앱의 생명 주기와 함께 동작하고 별도의 생명 주기 관리가 필요하지 않음</br>
-앱의 시작부터 종료까지 장시간 실행되어야하는 코루틴이 있다면 GlobalScope 에 작성</br>

1.2.2 코루틴 스코프(CoroutineScope)</br>
-필요할 때만 열고 완료 되면 닫는 코루틴을 담는 스코프 (ex. 버튼을 클릭해서 서버의 정보를 갖고오거나 파일 오픈)</br>
-디스패처를 코루틴 스코프의 괄호 안에 넣어 코루틴이 실행될 스레드를 지정</br>

1.2.3 runBlocking </br>
-runBlocking 을 사용하면 코루틴이 종료될때까지 메인 루틴을 잠시 대기시켜준다.</br>
(코루틴은 제어되는 스코프나 프로그램이 종료되면 함께 종료되기 때문에 코루틴이 끝까지 실행되는 것을 보장할 필요가 있다)</br>
단, 메인 스레드에서 runBlocking 을 사용할 경우 일정 시간 응답이 없다면 ANR 이 발생한다.</br>

<br></br>
<br></br>

><a id = "content2">**2.CoroutineScope 디스패처**</a></br>

**2.1 기능**</br>
-코루틴을 어떤 스레드에게 보낼지 정하는 도구</br>
-스레드 풀(Thread Pool)은 스레드를 일정 개수만큼 만들어두고 작업 큐에 들어오는 작업을 하나씩 처리하는데,</br>
디스패처는 스레드 풀안의 스레드 부하 상황에 맞게 코루틴을 배분함</br>

**2.2 종류**</br>
2.2.1 **Dispatchers.IO** : </br>
-대기 시간이 있는 작업을 수행하기 위해 최적화되어 있는 디스패처</br>
ex. 이미지 다운, 파일 입출력 등</br>

2.2.2 **Dispatchers.Main** : </br>
-UI 와 상호작용에 최적화되어 있는 디스패처</br>
ex. 텍스트뷰에 글자를 입력해야할 경우 등</br>

2.2.3 **Dispatchers.Default** : </br>
-CPU 를 많이 사용하는 작업을 백그라운드 스레드에서 실행하도록 최적화되어 있는 디스패처</br>
-리스트를 정렬하거나, Json  Parsing 작업 등에 최적화</br>
-CPU 코어 개수만큼 스레드를 생성해 작업</br>
ㄴ코어가 스레드1 작업을 잠시 멈추고 스레드2 작업을 이어한다면, 스레드1에서 진행 중이던 작업을 기록하고 스레드2에서 진행할 작업을 불러오는 작업이 필요한데 이렇게 전환하는 것을 **Context Switching** 이라고 하고 이때 낭비되는 시간을 **오버헤드**라고 함</br>
코어보다 사용 스레드 갯수와 오버헤드 발생은 비례하는데, 이를 최소화하기 위해 CPU 코어 개수만큼 스레드에 제한을 두고 사용하는 전략을 사용</br>

2.2.4 **Dispatchers.Unconfined**</br>
<br></br>
<br></br>

><a id = "content3">**3. 코루틴 스코프 함수**</a></br>

**3.1 launch()**</br>
-반환값이 없는 Job 객체</br>
-호출하는 것만으로도 코루틴 생성</br>
-cancel(), join()와 조합해 사용 가능</br>
-코루틴 스코프 안에 선언된 여러개의 launch 블록은 모두 새로운 코루틴으로 분기 되면서 동시에 처리되기 때문에 순서를 정할 수 없음(병렬 처리)</br>
cf. 순차 처리가 필요할 때 join() 사용</br>
-현재 스레드 중단 없이 코루틴을 즉시 시작 시킨다. 결과를 호출한 쪽에 반환하지 않는다.</br>
-suspend 함수가 아닌 **1)일반 함수 안에서 suspend 함수를 호출할 때**와 **2)코루틴의 결과 처리가 필요없을 때 사용**</br> 

```kotlin
CoroutineScope(Dispatchers.Default).launch {
    val job1 = launch {
        for (i in 0..50) {
            Log.d(TAG, "onCreate: launch 1 $i")
            delay(500)
        }
    }

    val job2 = launch {
        for (i in 0..50) {
            Log.d(TAG, "onCreate: launch 2 $i")
            delay(500)
        }
    }
}
//job1 에 blocking 함수(join()) 이 없기 때문에 job1 과 job2 가 무작위로 실행됨
//launch 2 0 / launch 1 0 / launch 2 1 / launch 1 1 / launch 2 2 / launch 1 2 / launch 2 3
```

**3.2 async**</br>
-반환값이 있는 Deffered 객체</br>
-현재 스레드 중단 없이 코루틴을 즉시 시작 시킨다.</br>
-호출 쪽에서 await()를 통해 코루틴 결과를 기다릴 수 있다. **병행으로 실행될 필요가 있는 다수의 코루틴을 사용할 때 사용**</br>
**async 빌더는 suspend 함수 내부에서만 사용 가능**</br>

**3.3 cancel()**</br>
-아래 두가지 조건이 발생하며 코루틴의 동작을 멈춤</br>
-1)코루틴 내부의 delay() 함수 또는 yield() 함수가 사용된 위치까지 수행된 뒤 종료</br>
-2)cancel()로 인해 속성인 isActive 가 false 가 되므로 이를 확인하여 수동으로 종료</br>

```kotlin
CoroutineScope(Dispatchers.Default).launch {
    val job = launch {
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


><a id = "content4">**4. Blocking 함수**</a></br>

4.1 join()</br>
-Job.join()</br>
-launch 블록 뒤에 join()을 사용하면 코루틴이 순차적으로 실행됨</br>
```kotlin
//job1의 로그가 모두 출력된 후 job2 로그가 출력
CoroutineScope(Dispatchers.Default).launch {
    val job1 = launch {
        for(i in 0..2) {
           delay(500)
           Log.d("코루틴", "launch1 = $i")
        }
    }.join()
 
    val job2 = launch {
        for(i in 0..2) {
           delay(500)
           Log.d("코루틴", "launch2 = $i")
        }
    }
}
```

4.2 await()</br>
-Deffered.await()</br>
-코루틴을 async 로 선언하고 결괏값을 처리하는 곳에 await() 함수를 사용하면 결과 처리가 완료된 후에 await() 를 호출한 줄의 코드가 실행됨</br>
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

```kotlin
CoroutineScope(Dispatchers.Default).async {
  var sum = async {
    var _sum = 0
    for (i in 0..10) {
       _sum += i
       Log.d(TAG, "onCreate _sum: $_sum")
       delay(200)
    }
    Log.d(TAG, "async2 _sum: $_sum")
    _sum //최종적 반환값
  }
 
  Log.d(TAG, "async1 sum : ${sum.await()}")
 
  async {
   Log.d(TAG, "async3")
  }
}

/*
case1 Log.d(TAG, "async1 sum: $sum")
async1 sum : DeferredCoroutine{Active}@c45e5bb
async3
async2 sum: 55

case2 Log.d(TAG, "async1 sum : ${sum.awit()}")
async2 sum : 55
async1 sum : 55
async3
*/
```

4.3 withTimeoutOrNull(mills)</br>
-제한 시간 내에 수행되면 결과값을 아니면 null을 반환</br>
```kotlin
runBlocking {
  var result = withTimeoutOrNull(50) {
     for (i in 1..1000) {
      print(i)
      delay(10)
     }
     "Finish"
  }
  print(result) //1 / 2 / 3 / null
}
```

><a id = "content5">**5. suspend**</a></br>

**5. suspend**</br>
-일반 함수를 코루틴으로 만드는 키워드</br>
-코루틴 안에서 suspend 키워드로 선언된 함수가 호출되면 이전까지의 코드 실행이 멈추고 suspend 함수의 처리가 완료된 후에 멈춰 있던 코드의 다음 코드부터 실행됨</br>
-suspend 키워드가 있으면 코루틴 스코프 안에서 자동으로 백그라운드 스레드처럼 동작</br>

```kotlin
suspend fun readFile() {
    //...
}

CoroutineScope(Dispatcher.IO).launch {
    {코드1}
    readFile()
    {코드2}
}
/*
{코드1} 부분이 실행되다가 readFile()이 실행되면 {코드1} 동작 스레드가 잠시 멈춤 #{코드1}의 상태값은 저장됨
-> readFile() 작업이 종료
-> {코드1}의 상태값 복구 후 작업을 이어서 진행
{코드1}의 실행은 잠시 멈추지만 스레드의 중단은 없음
*/
```

><a id = "content6">**6. withContext**</a></br>

**6. withContext**</br>
-디스패처를 분리시키는 키워드</br>
-suspend 함수를 코루틴 스코프에서 호출할 때 호출한 스코프와 다른 디스패처를 사용할 경우 사용</br>
ex. Main 디스패처에서 UI 를 제어해야하는 데 호출된 suspend 함수는 파일을 읽어와야하는 경우</br>
-비동기 코드를 순차적 실행할 때 사용</br>
-withContext 는 async-await 와 거의 흡사한 것 처럼 보이며 속도는 withContext 가 2배 이상 빠르나 ns 단위에서 차이이기 때문에 거의 의미가 없는 차이라고 볼 수 있음</br>
-async 는 병렬 처리가 가능하고 withContext 는 순차 처리만 가능</br>
-async 내에서 발생한 예외는 try-catch 로 잡을 수 없으며 withContext 는 예외 처리가 가능</br>

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

```kotlin
//withContext, 순차처리 -> 3초 소요
suspend fun exampleSuspend() {
    withContext(Dispatchers.IO) {
        delay(1000)
    }
    withContext(Dispatchers.IO) {
        delay(1000)
    }
    withContext(Dispatchers.IO) {
        delay(1000)
    }
}

//async, 병렬처리 -> 1초 소요
suspend fun test() {
    CoroutineScope(Dispatchers.IO).async {
        delay(1000)
    }
    CoroutineScope(Dispatchers.IO).async {
        delay(1000)
    }
    CoroutineScope(Dispatchers.IO).async {
        delay(1000)
    }
}
```

<br></br>
<br></br>
---

><a id = "ref">**참고링크**</a></br>

이것이 안드로이드다. 10장 스레드와 코루틴</br>

코틀린 코루틴 한번에 끝내기</br>
https://whyprogrammer.tistory.com/596</br>

Coroutine Dispatcher, 넌 대체 뭐야?</br>
https://todaycode.tistory.com/182</br>

코루틴은 왜 빠른 걸까요?</br>
https://todaycode.tistory.com/179</br>

스레드 관련해서 원초적인 궁금증이 생겼습니다</br>
https://www.inflearn.com/questions/335497</br>

withContext는 무엇이며 async와 무슨 차이가 있을까?</br>
https://todaycode.tistory.com/183</br>

