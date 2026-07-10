import kotlinx.coroutines.*

// 데이터를 가져오는 가상의 함수 두 개.
// suspend: 코루틴 블록 내부에서 멈출 수 있도록 하는 키워드
suspend fun fetchDataFromFirstServer(): String {
    delay(1000L) // 1초간 비동기 대기
    return "Server_A_Data"
}

suspend fun fetchDataFromSecondServer(): String {
    delay(1500) // 1.5초간 비동기 대기 (L 안 써도 기본값이 Long이다)
    return "Server_B_Data"
}

fun main() {
    // runBlocking: 블록 내부의 코루틴들이 모두 완료될 때까지 현재 스레드를 블로킹(멈추게) 함
    // 주로 main함수나 테스트 코드의 최상단 래퍼(Wrapper)로 사용.
    runBlocking {
        // async: 비동기로 작업하되, 나중에 결과물(Deferred<T>)를 받을 필요가 있을 때 사용
        // 블록 내부의 최종 결과물을 반환, 이 값을 꺼내 쓰려면 .await()으로 대기 호출을 반드시 해야 한다.
        val resultA = async { fetchDataFromFirstServer() }
        val resultB = async { fetchDataFromSecondServer() }
        println("[합산 완료] ${resultA.await()} & ${resultB.await()}")
    }

    // 안 쓴 키워드긴 하지만
    // launch: 할 일만 던져두고 결과는 필요 없을 때 사용하는 빌더.
    // 결과값이 없는(void, unit) 작업을 불태울 때 사용한다.
}