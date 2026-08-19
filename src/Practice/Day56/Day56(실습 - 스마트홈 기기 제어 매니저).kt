package Practice.Day56

// 응용 과제 #5: 스마트 홈 기기 제어 매니저 (Smart Home IoT Manager)
// 오늘 다룰 영역은 [5주 차: 객체지향/인터페이스 설계]와 [4주 차: 함수], 그리고 [6주 차: 컬렉션/타입 캐스팅]입니다.
// 스마트 홈에 등록된 다양한 IoT 기기(조명, 에어컨, 스마트 플러그)를 추상화하고, 각 기기의 전원 제어 및 일괄 명령을 처리하는 매니저 시스템을 직접 모델링하고 구현하세요.

// 스마트 기기 최상위 부모 클래스
abstract class SmartDevice(
    val id: String,
    val name: String,
    var isPowerOn: Boolean
) {
    fun turnOn() { isPowerOn = true }
    fun turnOff() { isPowerOn = false }
    // 현재 기기 상태 요약 문자열 반환
    abstract fun getStatusSummary(): String
}

// 스마트 조명, 밝기를 추가 멤버 프로퍼티로 가짐.
class Light(id: String, name: String, isPowerOn: Boolean = false, var brightness: Int = 50) : SmartDevice(id, name, isPowerOn) {
    init { brightness = brightness.coerceIn(1, 100) } // 밝기는 1에서 100으로 제한.
    override fun getStatusSummary(): String = "[조명: $name] 전원: ${if (isPowerOn) "ON" else "OFF"}, 밝기: ${brightness}%"
}

// 에어컨, 희망 온도를 추가 멤버 프로퍼티로 가짐.
class AirConditioner(id: String, name: String, isPowerOn: Boolean = false, var targetTemp: Int = 24) : SmartDevice(id, name, isPowerOn) {
    override fun getStatusSummary(): String = "[에어컨: $name] 전원: ${if (isPowerOn) "ON" else "OFF"}, 설정온도: ${targetTemp}℃"
}

// 스마트 플러그, 소비 전력을 추가 멤버 프로퍼티로 가짐.
class SmartPlug(id: String, name: String, isPowerOn: Boolean = false, val powerUsageWatt: Int) : SmartDevice(id, name, isPowerOn) {
    override fun getStatusSummary(): String = "[플러그: $name] 전원: ${if (isPowerOn) "ON" else "OFF"}, 소비전력: ${powerUsageWatt}W"
}

// 오늘의 메인 메뉴, 스마트 홈 매니저 클래스
class SmartHomeManager {
    // 내부 프로퍼티로 등록된 기기 목록을 관리하는 컬렉션을 보유.
    val smartHomeList = mutableListOf<SmartDevice>()

    // 기기 추가 함수
    fun registerDevice(device: SmartDevice) { smartHomeList.add(device) }
    // 등록된 모든 기기를 종료
    fun turnAllOff() { for (device in smartHomeList) { device.turnOff() } }
    // 켜져 있는 것만 필터링하여 반환
    fun getPoweredOnDevices(): List<SmartDevice> = smartHomeList.filter { it.isPowerOn }
    // 등록된 모든 기기의 getStatusSummary()를 콘솔에 출력
    fun printAllStatus() { for (device in smartHomeList) { println(device.getStatusSummary()) } }

    // 등록된 기기 중 현재 전원이 켜져 있는 SmartPlug 기기들의 powerUsageWatt 총합을 계산하여 반환
    fun getTotalPlugPowerUsage(): Int =
        smartHomeList.filterIsInstance<SmartPlug>()
            .filter { it.isPowerOn }
            .sumOf { it.powerUsageWatt }
}

// 테스트 케이스
fun main() {
    val manager = SmartHomeManager()

    val livingRoomLight = Light("DEV-01", "거실 조명", brightness = 80)
    val bedroomAc = AirConditioner("DEV-02", "안방 에어컨", targetTemp = 22)
    val tvPlug = SmartPlug("DEV-03", "TV 플러그", powerUsageWatt = 150)
    val deskPlug = SmartPlug("DEV-04", "컴퓨터 플러그", powerUsageWatt = 200)

    // 기기 등록
    manager.registerDevice(livingRoomLight)
    manager.registerDevice(bedroomAc)
    manager.registerDevice(tvPlug)
    manager.registerDevice(deskPlug)

    // 일부 기기 켜기
    livingRoomLight.turnOn()
    tvPlug.turnOn()
    deskPlug.turnOn()

    println("=== 전체 기기 상태 출력 ===")
    manager.printAllStatus()

    println("\n=== 켜져 있는 기기 목록 ===")
    manager.getPoweredOnDevices().forEach { println(it.name) }

    println("\n현재 사용 중인 플러그 총 소비전력: ${manager.getTotalPlugPowerUsage()}W")

    // 일괄 끄기
    println("\n=== 전체 일괄 끄기 실행 ===")
    manager.turnAllOff()
    println("켜져 있는 기기 수: ${manager.getPoweredOnDevices().size}개")
}