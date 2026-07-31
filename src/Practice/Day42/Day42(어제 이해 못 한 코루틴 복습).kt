package Practice.Day42

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.*
import kotlin.system.measureTimeMillis

// 복습 1단계: async 두 개 병렬 실행해서 합치기
// suspend: 코루틴 블록 내부에서 멈출 수 있도록 하는 키워드
// 없으면 delay가 안 되지?
suspend fun fetchData(id: Int): Int {
    delay(1000) // 1초 대기
    return id * 10 // 이후 id * 10을 반환
}

// coroutineScope: 이 안에서 실행되는 코루틴들을 하나로 묶어 관리하는 함수
// 부모 코루틴이 자식 코루틴들이 끝날 때까지 기다리는 공간이라고 이해하면 편하다.
suspend fun pracPart1() = coroutineScope {
    println("==연습 문제 1번==")
    val time = measureTimeMillis { // 시간 측정용, 내부 블록의 실행 시간을 측정하여 변수에 보관한다.

        // 동시에 실행
        // async: 비동기로 작업하되, 나중에 결과물(Deferred<T>)를 받을 필요가 있을 때 사용
        // 블록 내부의 최종 결과물을 반환, 이 값을 꺼내 쓰려면 .await()으로 대기 호출을 반드시 해야 한다.
        val deferred1 = async { fetchData(1) }
        val deferred2 = async { fetchData(2) }

        // 두 작업이 다 끝날 때까지 대기 후 결과 합산
        // await()으로 대기 호출을 해서 결과를 빼다 쓴다.
        val sum = deferred1.await() + deferred2.await()
        println("결과: $sum") // 결과: 30
    }
    // 최종 연산 시간 출력, 약 1000ms 안팎이 나와야 한다.
    // 두 개의 연산(fetchData 함수)이 동시에 진행되어야 하기 때문.
    println("소요 시간: ${time}ms")
}

// 복습 2단계: map과 awaitAll을 이용한 N개 리스트 병렬 처리
suspend fun fetchAllData(ids: List<Int>): List<Int> = coroutineScope {
    // 5개의 작업을 동시에 async로 띄움
    val deferredList: List<Deferred<Int>> = ids.map { id ->
        async { fetchData(id) } // id를 fetchData()(위에서 만든 10배 해주는 함수) 함수를 이용하여 매핑.
    }

    // 5개가 전부 끝날 때까지 일괄 대기 후 List<Int>로 반환
    // awaitAll: 여러 개의 Deferred(결과물)을 한 번에 기다리는 함수.
    // async 여러 개 돌리고 하나씩 await하는 일이 없도록 해 준다.
    deferredList.awaitAll()
}

suspend fun pracPart2() = coroutineScope {
    val part2Time = measureTimeMillis {
        val ids = listOf(1, 2, 3, 4, 5)
        val result = fetchAllData(ids)

        // 동시에 돌렸으니 1초가 걸려야 한다.
        println("\n==연습 문제 2번==\n최종 결과: ${result}")
    }
    println("소요 시간: ${part2Time}\n")
}

// 복습 3단계: Semaphore로 동시 실행 개수 제한하기
// 대충 로그까지 출력하는 함수니까 이건 볼 필요 없다.
suspend fun fetchDataWithLog(id: Int): Int {
    println("▶️ [요청 시작] ID: $id")
    delay(1000)
    println("✅ [요청 완료] ID: $id")
    return id * 10
}

suspend fun pracPart3() = coroutineScope {
    val part3Time = measureTimeMillis {
        val ids = (1..10).toList()
        val semaphore = Semaphore(2) // 동시에 최대 2개만 허용

        val results = ids.map { id ->
            async {
                // async는 10개가 생성되지만, 함수까지는 못 간다.
                // 세마포어 허가권(Permit)을 획득해야만 fetchDataWithLog 실행
                // 최대 두 개씩만 연산이 진행된다.
                semaphore.withPermit {
                    fetchDataWithLog(id)
                }
            }
        }.awaitAll()

        println("\n==연습 문제 3번==\n최종 결과 리스트: $results")
    }
    // 대충 5초 가량 소요된다.
    println("소요 시간: ${part3Time}")
}

// 테스트용 메인 함수
suspend fun main() = coroutineScope {
    pracPart1()
    pracPart2()
    pracPart3()
}