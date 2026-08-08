package Practice.Day48

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

// 문제: 실시간 거래소 데이터 예외 처리 및 누적 합계 (Exchange Stream & Accumulator)
// 실시간 가상화폐 거래소에서 시세 데이터 스트림이 들어옵니다. 데이터 수신 중 예외가 발생할 때 안전하게 복구하고, 실시간 이동 누적 거래액을 계산하는 파이프라인을 작성하세요.

fun fetchPriceStream(): Flow<Double> = flow {
    val priceList = listOf(1000.0, 1500.0, 2000.0, -1.0, 3000.0)
    for (price in priceList) {
        if (price < 0) { throw IllegalStateException("잘못된 시세 데이터 수신") } // 음수 가격이 들어오면 예외 던지기
        emit(price) // 아니라면 발행
        delay(100) // 0.1초 간격으로
    }
}

fun processPriceStream(priceFlow: Flow<Double>): Flow<Double> = priceFlow
    .catch { // 예외 처리 블록
        println("[경고] 에러 발생: ${it.message}") // 메시지 출력 후
        emit(0.0) // 기본값 0.0을 발행
    }
    // .scan(초기값) { 누적된 값, 현재 리스트의 원소 -> 본문 }
    // 시세가 들어올 때만다 현재까지의 누적 합계를 계산하여 출력 스트림으로 전달한다.
    .scan(0.0) { accumulator, value -> accumulator + value }

// 테스트 케이스
fun main() = runBlocking {
    val rawStream = fetchPriceStream()
    val processedStream = processPriceStream(rawStream)

    println("=== 실시간 누적 거래액 수신 시작 ===")
    processedStream.collect { totalAmount ->
        println("현재 누적 거래액: ${totalAmount}원")
    }
    println("=== 스트림 수신 완료 ===")
}

// 알아두면 좋은 점 (Flow 연산자 배치 팁)
// scan -> catch 순서로 배치할 경우:
// scan 연산 자체에서 혹시라도 발생할 수 있는 내부 예외까지 포함하여
// 가공 파이프라인 전체의 예외를 가장 아래에서 포획하고 싶다면 catch를 맨 밑에 두는 것이 보편적이다.
// 나는 반대로 써버렸다. 앞으로는 catch를 맨 밑에 두자.