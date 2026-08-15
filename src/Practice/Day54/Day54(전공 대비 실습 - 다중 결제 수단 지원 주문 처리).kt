package Practice.Day54

// 응용 과제 #3: 다중 결제 수단 지원 주문 처리 시스템 (Payment Pipeline)
// [4주 차: 함수와 람다식] 및 [5주 차: 클래스/인터페이스], [6주 차: 컬렉션 가공]을 아우르는 "주문 결제 파이프라인 & 결제 수단 추상화 시스템"입니다.
// 쇼핑몰에서 다양한 결제 수단(신용카드, 간편결제, 계좌이체)을 지원하고, 주문 상태를 안전하게 갱신하는 객체지향/함수형 결제 시스템을 직접 모델링하고 구현하세요.

// 기본 데이터 구조
// 결제 수단 sealed 클래스
sealed class PaymentMethod {
    // 카드 번호와 수수료율(예: 0.02면 2%)을 가짐.
    data class Card(val cardNumber: String, val feeRate: Double) : PaymentMethod()

    // 간편결제사 이름(예: "NaverPay", "KakaoPay")과 보유 포인트를 가짐.
    data class SimplePay(val provider: String, val pointBalance: Int) : PaymentMethod()

    // 별도 부가 정보 없이 현금/계좌이체를 나타냄.
    data object Cash : PaymentMethod()
}

// 주문 상품 데이터 클래스: 이름, 가격, 수량을 프로퍼티로 가짐.
data class OrderItem(val name: String, val price: Int, val quantity: Int)

// 주문 결과 sealed 인터페이스
sealed interface OrderResult {
    // 결제 성공 시 주문번호, 주문 원금, 수수료, 최종 결제액을 담음.
    data class Success(val orderId: String, val totalAmount: Int, val feeAmount: Int, val finalAmount: Int) : OrderResult
    // 잔액 부족 등 실패 시 사유를 담음
    data class Failure(val reason: String) : OrderResult
}

// 오늘의 메인 메뉴
fun processOrder(orderId: String, items: List<OrderItem>, paymentMethod: PaymentMethod): OrderResult {
    val totalPrice = items.sumOf { it.price * it.quantity }
    // 상품 리스트가 비어있거나 총합이 0원 이하이면 결제 실패.
    if (items.isEmpty() || totalPrice == 0) { return OrderResult.Failure("주문할 상품이 없습니다.") }
    var fee = 0
    var finalPrice = 0

    when (paymentMethod) {
        // 카드일 경우:
        is PaymentMethod.Card -> {
            // 카드 번호가 16자리가 아니면(하이픈 제외) 결제 실패.
            if (paymentMethod.cardNumber.replace("-", "").length != 16) {
                return OrderResult.Failure("유효하지 않은 카드 번호입니다.")
            }
            fee = (paymentMethod.feeRate * totalPrice).toInt() // 수수료율을 계산하여
            finalPrice = fee + totalPrice // 최종 결제액을 연산
        }

        // 간편결제일 경우: 수수료는 0.
        is PaymentMethod.SimplePay -> {
            // 보유 포인트(pointBalance)가 총합 이상이어야 결제 성공. 부족하면
            if (paymentMethod.pointBalance < totalPrice) { return OrderResult.Failure("간편결제 포인트가 부족합니다.") }
            finalPrice = totalPrice
        }

        // 현금일 경우: 수수료는 0. 절대 실패하지 않는다.
        is PaymentMethod.Cash -> { finalPrice = totalPrice }
    }

    return OrderResult.Success(orderId, totalPrice, fee, finalPrice)
}

// 테스트 케이스
fun main() {
    val items = listOf(
        OrderItem("모니터", 300000, 1),
        OrderItem("마우스패드", 15000, 2)
    ) // 총합: 330,000원

    // 1. 카드 결제 테스트 (수수료 2%)
    val cardPayment = PaymentMethod.Card("1234-5678-9012-3456", 0.02)
    val result1 = processOrder("ORD-001", items, cardPayment)
    println(result1)

    // 2. 간편결제 포인트 부족 실패 테스트
    val simplePayment = PaymentMethod.SimplePay("NaverPay", pointBalance = 200000)
    val result2 = processOrder("ORD-002", items, simplePayment)
    println(result2)
}