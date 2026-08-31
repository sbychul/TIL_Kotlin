package Practice.Day63

// 응용 과제 #13: 이벤트 버스 및 알림 디스패처 (Event Bus & Notification Dispatcher)
// 강의계획서의 [5주 차: 객체지향/상속 및 다형성], [6주 차: 컬렉션 데이터 변환 및 그룹 집계],
// 그리고 [4주 차: 함수/람다]를 아우르는 "안드로이드 앱 이벤트 버스 및 알림 디스패처 (Event Bus & Notification Dispatcher)"입니다.
// 앱 내에서 발생하는 다양한 이벤트(채팅, 시스템 공지, 보안 경보)를 등록된 구독자들에게 브로드캐스팅하고,
// 각 사용자의 관심 필터(태그, 최소 중요도)에 부합하는 이벤트만 선별하여 전달하는 이벤트 버스를 구현하세요.

// 이벤트 중요도 enum 클래스
enum class EventPriority { LOW, NORMAL, URGENT }

// 이벤트를 나타내는 클래스
data class AppEvent(
    val id: String, // 이벤트 고유 ID
    val tag: String, // 카테고리 태그
    val title: String, // 제목
    val content: String, // 내용
    val priority: EventPriority // 중요도
)

// 알림 구독자 클래스
data class Subscriber(
    val subscriberName: String, // 구독자명
    val interestedTag: String? = null, // 관심 있는 카테고리 (null일 경우 전부)
    val minPriority: EventPriority = EventPriority.LOW // 수신할 최소 중요도 기준, 기본값은 LOW(전부)
)

// 이벤트 버스 클래스
class SimpleEventBus {
    // 내부 프로퍼티로 구독자 목록과 과거 이벤트 이력을 가짐.
    private val subScriberList = mutableListOf<Subscriber>()
    private val events = mutableListOf<AppEvent>()

    // 추가/삭제 메서드
    fun registerSubscriber(subscriber: Subscriber) { subScriberList.add(subscriber) }
    fun unregisterSubscriber(subscriber: Subscriber) : Boolean {
        return if (subScriberList.contains(subscriber)) {subScriberList.remove(subscriber); true} else false
    }

    // 이벤트를 이벤트 이력에 추가하는 메서드
    fun publishEvent(event: AppEvent): List<Pair<String, AppEvent>> {
        events.add(event) // 추가 후
        // 관심 태그인지 && 최소 중요도 이상인지 확인해서 <구독자명, 이벤트>의 Pair로 이루어진 리스트 만든 후 반환
        val result = subScriberList.filter { (it.interestedTag == null || it.interestedTag == event.tag) && event.priority.ordinal >= it.minPriority.ordinal }
            .map { it.subscriberName to event }
        return result
    }

    // 누적된 이벤트 이력을 그룹화하여 태그별 발생 횟수를 담은 map을 반환하는 메서드
    fun getEventCountByTag(): Map<String, Int> = events.groupBy { it.tag } // 태그별로 그룹화 (Map<String, List<AppEvent>>)
        .mapValues { (_, events) -> events.count() } // 개수 세기
}

// 테스트 케이스
fun main() {
    val bus = SimpleEventBus()

    // 구독자 등록
    // subA: 모든 태그 수신, LOW 이상 (모든 이벤트 수신)
    val subA = Subscriber("UserA", interestedTag = null, minPriority = EventPriority.LOW)
    // subB: CHAT 태그만 수신, LOW 이상
    val subB = Subscriber("UserB", interestedTag = "CHAT", minPriority = EventPriority.LOW)
    // subAdmin: SYSTEM 태그만 수신, URGENT 이상만 수신
    val subAdmin = Subscriber("Admin", interestedTag = "SYSTEM", minPriority = EventPriority.URGENT)

    bus.registerSubscriber(subA)
    bus.registerSubscriber(subB)
    bus.registerSubscriber(subAdmin)

    println("=== 1. 일반 채팅 이벤트 발생 (CHAT, LOW) ===")
    val e1 = AppEvent("EV-01", "CHAT", "새 메시지", "친구로부터 메시지가 도착했습니다.", EventPriority.LOW)
    val dispatched1 = bus.publishEvent(e1)
    // UserA(모든 태그), UserB(CHAT 수신)에게 전달되어야 함
    dispatched1.forEach { (sub, event) -> println("-> [수신자: $sub] ${event.title}") }

    println("\n=== 2. 시스템 일반 공지 이벤트 발생 (SYSTEM, NORMAL) ===")
    val e2 = AppEvent("EV-02", "SYSTEM", "정기 점검 안내", "새벽 2시 점검 예정입니다.", EventPriority.NORMAL)
    val dispatched2 = bus.publishEvent(e2)
    // UserA(모든 태그 수신)만 전달 (Admin은 URGENT 이상만 수신하므로 제외)
    dispatched2.forEach { (sub, event) -> println("-> [수신자: $sub] ${event.title}") }

    println("\n=== 3. 시스템 긴급 점검 이벤트 발생 (SYSTEM, URGENT) ===")
    val e3 = AppEvent("EV-03", "SYSTEM", "서버 과부하 경보", "즉시 조치가 필요합니다.", EventPriority.URGENT)
    val dispatched3 = bus.publishEvent(e3)
    // UserA(모든 태그 수신), Admin(SYSTEM & URGENT 수신)에게 전달
    dispatched3.forEach { (sub, event) -> println("-> [수신자: $sub] ${event.title}") }

    println("\n=== 4. 태그별 이벤트 누적 발생 통계 ===")
    // CHAT: 1회, SYSTEM: 2회
    println(bus.getEventCountByTag())
}