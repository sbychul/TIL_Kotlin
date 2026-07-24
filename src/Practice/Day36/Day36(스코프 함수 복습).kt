package Practice.Day36

// 데이터 클래스 Order 선언
data class Order(val id: Long, val itemName: String, val price: Int, var status: String = "PENDING")

class OrderProcessor {
    // 주문 준비 메서드
    // 널이 들어올 수 있는 order를 받아 null이 아니면 apply 함수로 status 값을 변경 후, 해당 order 객체를 그대로 반환
    // null이면 그대로 null을 반환
    fun prepareOrder(order: Order?): Order? {
        if (order == null) { return null }
        order.apply { status = "PREPARING" }
        return order
    }

    // 영수증 메서드
    // 널이면 "주문 정보가 없습니다"를 반환
    // 아니면 영수증 문자열을 생성하여 반환
    // let과 엘비스 연산자를 이용하여 구현, null이 아닐 때만 let문이 실행.
    fun formatReceipt(order: Order?): String =
        order?.let {"주문번호 [${it.id}]: ${it.itemName} (${it.price}원) - 상태: ${it.status}"} ?: "주문 정보가 없습니다."
}

// 테스트 케이스
fun main() {
    val processor = OrderProcessor()
    val validOrder = Order(id = 101L, itemName = "맥북 프로", price = 2500000)

    println("=== 1. 주문 준비 (apply 활용) ===")
    val prepared = processor.prepareOrder(validOrder)
    println("준비된 주문 상태: ${prepared?.status}") // 출력: PREPARING
    println("null 입력 시: ${processor.prepareOrder(null)}") // 출력: null

    println("\n=== 2. 영수증 포맷팅 (let 활용) ===")
    val receipt = processor.formatReceipt(validOrder)
    println(receipt)
    // 출력: 주문번호 [101]: 맥북 프로 (2500000원) - 상태: PREPARING

    val nullReceipt = processor.formatReceipt(null)
    println(nullReceipt)
    // 출력: 주문 정보가 없습니다.
}