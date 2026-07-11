import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*

// 23일차 주식 시스템을 실시간 데이터 스트림 구조로 만든 것.
// 시세를 생성하는 함수. 반환 타입: Flow<데이터 타입>
// Int형 데이터를 연속적으로 발행(emit)해서 흘려 보내주는 Flow.
fun streamStockPrice(): Flow<Int> = flow {
    for (i in 1..5) {
        // 60000에서 70000 사이의 랜덤한 정수 (가격) 생성 후 발행
        emit((60000..70000).random())
        delay(1000) // 1초 대기
    }
}

fun main() {
    runBlocking {
        // flow 내부의 코드는 소비자(collect 함수 등)가 실행하기 전까지는 절대 먼저 실행되지 않는 콜드 스트림 방식.
        // collect를 호출하는 순간 파이프라인이 연결, 생산자가 emit()으로 밀어줄 때마다 소비자 블록이 실시간으로 데이터 처리.
        streamStockPrice()
            // 플로우 내부에서 데이터를 생성하는 작업만 별도의 스레드에서 돌리고 싶을 때 사용하는 함수.
            // 데이터 생성이 무거운 작업이라 가정, Dispatchers.Default (백그라운드 워커 스레드) 스레드에서 데이터 생성 작업을 거치도록 한다.
            .flowOn(Dispatchers.Default)
            // 받은 데이터를 실시간으로 받아 호출. 반드시 코루틴 블록 내부에서만 사용할 수 있다.
            // 받은 데이터는 it을 통하여 참조할 수 있다. 1초 간격으로 주가 무빙을 감상하자.
            .collect { println("[실시간 시세] 현재 주가: ${it}원") }
        
        // + Flow는 Sequence와 사용법이 매우 유사하여, 고차 함수 체이닝 연산자를 자유롭게 결합할 수 있다.
    }
}