package Practice.Day47

import kotlinx.coroutines.flow.*
import kotlinx.coroutines.*

// 문제: 실시간 센서 데이터 스트림 처리기 (Sensor Data Stream Processor)
// 주기적으로 들어오는 센서 측정값(온도 데이터) 스트림에서 이상 수치(Noise/Error)를 필터링하고, 화씨(℉)로 변환한 후 최종 출력하는 Flow 파이프라인을 작성하세요.

fun generateSensorData(): Flow<Double> = flow {
    // 측정 데이터 리스트, -999.0이 오류 데이터. 일단 이 함수는 단순 데이터 발행(emit)용 함수이므로, 걸러줄 필요는 없다.
    val sensorData = listOf(22.5, -999.0, 25.0, 31.2, -999.0, 18.4, 40.1)
    for (temp in sensorData) {
        emit(temp)
        delay(200) // 0.2초 간격으로 발행.
    }
}

// 전달받은 Flow<Double>을 받아 오류 데이터를 필터링하고, 섭씨 데이터를 화씨로 변환하여 emit하는 함수.
// 정상 범위는 0~50이라고 가정.
fun processTemperatureStream(sensorFlow: Flow<Double>): Flow<Double> =
    sensorFlow.filter { it in 0.0..50.0 } // 0 이상 50 이하의 데이터만 남김.
        .map { it * 1.8 + 32 } // 섭씨를 화씨로 변환하는 공식 적용

// 테스트 케이스
fun main() = runBlocking {
    val rawStream = generateSensorData()
    val processedStream = processTemperatureStream(rawStream)

    println("=== 실시간 센서 데이터 수신 시작 ===")
    processedStream.collect { tempFahrenheit ->
        println("정상 측정 온도: ${"%.1f".format(tempFahrenheit)}℉")
    }
    println("=== 스트림 수신 완료 ===")
}