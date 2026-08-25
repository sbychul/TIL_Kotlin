package Practice.Day60

import kotlinx.coroutines.*

// 응용 과제 #9: 실시간 센서 데이터 이상 감지 파이프라인 (Sensor Monitor)
// 강의계획서의 [4주 차: 람다/고차 함수], [6주 차: 컬렉션 변환],
// 그리고 [7주 차: 코루틴 채널/스트림 처리]를 아우르는 "실시간 센서 데이터 이상 감지 및 알림 파이프라인 (IoT Sensor Monitor)"입니다.
// 다양한 환경 센서(온도, 습도 등)로부터 주기적으로 들어오는 측정 데이터를 수집하고, 임계값을 초과하는 이상 데이터를 감지하여 알림 목록을 생성하는 시스템을 구현하세요.

// 센서 판독 데이터 클래스
data class SensorReading(
    val sensorId: String, // 센서 식별자
    val sensorType: String, // 센서 종류
    val value: Double, // 측정값
    val timeStamp: Long // 측정 시각 밀리초
)

// 경보 알림 sealed 인터페이스
sealed interface Alert {
    // 비상!!!!!!!!!!!!!!!!!!! (임계값 초과 시)
    data class HighWarning(val sensorId: String, val currentValue: Double, val threshold: Double) : Alert
    // 평시
    data class Normal(val sensorId: String, val currentValue: Double) : Alert
}

// 센서 모니터 클래스, 센서 타입별 허용 최대 임계값을 보관하는 맵을 프로퍼티로 가짐.
class SensorMonitor(val thresholdMap: Map<String, Double>) {
    // 임계값에 따른 경보를 반환하는 함수
    fun processReading(reading: SensorReading) : Alert {
        // get을 통해 센서 타입에 맞는 임계값을 가져옴. 등록되지 않은 타입이라면 기본값 100.0을 적용.
        val threshold = thresholdMap.get(reading.sensorType) ?: 100.0

        // 초과한다면 HighWarning, 아니라면 Normal 객체를 생성하여 반환.
        return if (reading.value > threshold) { Alert.HighWarning(reading.sensorId, reading.value, threshold) }
        else Alert.Normal(reading.sensorId, reading.value)
    }

    // reading을 순회하고 모든 데이터에 따른 판정 결과 리스트를 반환하는 함수.
    suspend fun streamAndAnalyze(readings: List<SensorReading>, onAlertDetected: (Alert.HighWarning) -> Unit): List<Alert> {
        val result = mutableListOf<Alert>() // 빈 리스트를 생성 (최종적으로 반환할 리스트)

        for (reading in readings) {
            delay(50) // 실시간 수신 지연 시뮬레이션, 50ms라고 가정.
            val alert = processReading(reading) // 판정 결과를 보관
            result.add(alert) // 해당 결과를 리스트에 보관

            // 만약 경고(HighWarning)라면 넘겨받은 람다 콜백 즉시 실행
            if (alert is Alert.HighWarning) {
                onAlertDetected(alert)
            }
        }

        return result // 순회가 끝나면 결과 리스트를 반환
    }

    // 입력된 데이터들을 센서 종류별로 그룹화, 각 타입별 평균 측정값을 담은 맵을 반환하는 함수.
    fun getAverageBySensorType(readings: List<SensorReading>): Map<String, Double> =
        readings.groupBy { it.sensorType } // 센서 종류별로 그룹화하여 맵을 만듦 (Map<String, List<SensorReading>)
            .mapValues { (_, readings) -> readings.map { it.value }.average() } // 이후 해당 타입별 평균 값을 계산하여 반환.
}

// 테스트 케이스
fun main() = runBlocking {
    val thresholds = mapOf(
        "TEMPERATURE" to 30.0,
        "HUMIDITY" to 70.0
    )
    val monitor = SensorMonitor(thresholds)

    val testReadings = listOf(
        SensorReading("TEMP-01", "TEMPERATURE", 25.5, 1000L),
        SensorReading("TEMP-01", "TEMPERATURE", 32.8, 2000L), // 온도 경보
        SensorReading("HUMID-01", "HUMIDITY", 65.0, 3000L),
        SensorReading("HUMID-01", "HUMIDITY", 75.2, 4000L),  // 습도 경보
        SensorReading("TEMP-02", "TEMPERATURE", 28.0, 5000L)
    )

    println("=== 실시간 센서 스트림 분석 시작 ===")
    val allAlerts = monitor.streamAndAnalyze(testReadings) { warning ->
        println("[즉시 알림 발송] 경고! 센서 ${warning.sensorId} 수치 초과: ${warning.currentValue} (기준: ${warning.threshold})")
    }

    println("\n=== 전체 판정 결과 목록 ===")
    allAlerts.forEach { println(it) }

    println("\n=== 센서 타입별 평균 수치 ===")
    val averages = monitor.getAverageBySensorType(testReadings)
    averages.forEach { (type, avg) ->
        println("$type 평균: ${String.format("%.2f", avg)}")
    }
}