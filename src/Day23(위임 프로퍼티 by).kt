import kotlin.properties.Delegates // 위임자를 사용하기 위한 import문

// 주식명과 해당 주식의 가격을 보관하는 클래스.
// 생성자 매개변수로 이름을 받는다.
class Stock(val name: String) {
    // 주식 가격을 의미하는 price 프로퍼티 선언.
    // 변동이 있을 예정이기에 var.
    // Delegates.observable : 위임자. 변수에 새로운 값이 대입되는 순간을 실시간으로 감지하여,
    // 세 가지 인자 (property, oldValue, newValue)를 람다 블록으로 넘겨 준다.
    var price: Int by Delegates.observable(0) {prop, oldValue, newValue ->
        println("[알림] ${name}의 가격이 ${oldValue}원에서 ${newValue}원으로 변경되었습니다.")
    }
}

class StockManager { // 대충 주식들을 총괄 관리하는 클래스라고 가정. by lazy의 복습 용도이다.
    val systemLog: String by lazy { // by lazy를 이용한 지연 초기화
        println("로그 시스템이 최초로 초기화되었습니다.")
        "System_Log_Active" // 마지막 줄의 값이 프로퍼티에 대입된다.
    }
}

fun main() {
    val samsung = Stock("삼성전자")
    samsung.price = 291000 // 2026년 7월 7일 종가
    samsung.price = 267000 // 2026년 7월 8일 종가(...)
    // 내일은 좀 올라다오..

    val manager = StockManager() // Manager 객체 생성
    manager.systemLog // 프로퍼티 호출, 최초 호출 시에만 지연 초기화 블록이 실행되는지 확인하자.
    manager.systemLog // 여기서는 안 돼야 정상.
}