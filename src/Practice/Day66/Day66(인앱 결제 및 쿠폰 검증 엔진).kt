package Practice.Day66

import kotlin.math.min

// 모바일 인앱 결제 트랜잭션 및 프로모션 검증기 (Billing & Promo Engine)
// 강의계획서의 [4주 차: 고차 함수/람다], [5주 차: 객체지향/상태 캡슐화 및 Sealed 계층],
// [6주 차: 컬렉션 데이터 가공/조회]를 아우르는 "모바일 인앱 결제 및 쿠폰 검증 엔진 (In-App Billing & Promo Engine)"입니다.
// 실제 안드로이드 인앱 결제 플로우에서 사용자의 잔액, 할인 쿠폰의 유효성,
// 최소 결제 조건 등을 다각도로 검증하고 최종 결제 트랜잭션을 승인/반려하는 비즈니스 엔진을 모델링했습니다.

// 디지털 아이템 구매 요청 시 상품 정보와 적용된 프로모션 쿠폰을 확인하여,
// 할인 적용 후 최종 청구 금액을 산출하고 사용자의 포인트/캐시 잔액을 안전하게 차감하는 트랜잭션 엔진을 구현하세요.

// 구매할 상품 데이터 클래스
data class DigitalItem (
    val id: String, // 상품 ID
    val name: String, // 상품명
    val price: Int // 상품 정가(KRW)
)

// 프로모션 쿠폰을 나타내는 sealed 인터페이스
sealed interface PromoCoupon {
    // 최소 결제액(minSpend) 이상일 때 discountAmount만큼 할인
    data class FlatDiscount(val code: String, val discountAmount: Int, val minSpend: Int) : PromoCoupon
    // 비율 할인(rate 0.2 == 20% 할인), 최대 한도는 maxLimit
    data class PercentageDiscount(val code: String, val rate: Double, val maxLimit: Int) : PromoCoupon
    // 쿠폰 미적용
    data object None : PromoCoupon
}

// 결제 트랜잭선 결과를 나타낼 sealed 인터페이스
sealed interface TransactionResult {
    // 성공 시 결제 금액과 잔액을 담음
    data class Success(val transactionId: String, val item: DigitalItem, val finalPaidAmount: Int, val remainingBalance: Int) : TransactionResult
    // 실패 시 실패 사유를 담음
    data class Failed(val item: DigitalItem, val reason: String) : TransactionResult
}

// 결제 매니저 클래스, 생성자는 초기 잔액을 받아 초기화
class InAppBillingManager(val initialBalance: Int) {
    // 내부에서 현재 잔액과 등록된 아이템 카탈로그를 관리
    var userBalance = initialBalance
    val itemList = mutableListOf<DigitalItem>()

    // 아이템 추가 메서드
    fun registerItem(item: DigitalItem) {itemList.add(item)}
    // 잔액 충전 메서드 (0 이하일 경우 무시)
    fun depositBalance(amount: Int) { if (amount <= 0) return; userBalance += amount }

    // 결제 진행 메서드, 쿠폰 없는 상태가 기본값.
    fun processPurchase(itemId: String, coupon: PromoCoupon = PromoCoupon.None) : TransactionResult {
        // 일단 아이템을 찾아보고 없다면 실패를 반환.
        val item = itemList.find { it.id == itemId } ?:
        return TransactionResult.Failed(DigitalItem(itemId, "Unknown", 0), "존재하지 않는 상품입니다.")

        // 쿠폰에 따른 할인 금액 계산
        var discountAmount = 0
        when (coupon) {
            // 쿠폰이 없다면 당연히 할인도 없다
            is PromoCoupon.None -> { } // 없다고

            // 일정 금액 할인의 경우
            is PromoCoupon.FlatDiscount -> {
                // 아이템 가격이 최소 결제 금액보다 낮다면 실패
                if (item.price < coupon.minSpend) { return TransactionResult.Failed(item, "최소 결제 금액을 만족하지 못했습니다.") }
                // 아니라면 일정 금액 할인 (아이템 정가를 넘을 순 없음)
                discountAmount = min(item.price, coupon.discountAmount)
            }

            // 비율 할인의 경우
            is PromoCoupon.PercentageDiscount -> {
                // 할인율을 계산, 그러나 최대치를 초과할 순 없음.
                discountAmount = min((item.price * coupon.rate).toInt(), coupon.maxLimit)
            }
        }

        // 최종 결제 금액 확인 및 잔액 부족 여부 판별
        val finalAmount = item.price - discountAmount
        if (userBalance < finalAmount) { return TransactionResult.Failed(item, "잔액이 부족합니다.") }

        userBalance -= finalAmount
        // 임의의 트랜잭션 ID 생성 후 결제 성공 반환
        return TransactionResult.Success("INAPPPURCHASE-${System.currentTimeMillis()}", item, finalAmount, userBalance)
    }

    // 카탈로그의 아이템 중 가격 범위를 만족하는 것들을 오름차순 정렬 반환하는 메서드
    fun getItemListByPriceRange(minPrice: Int, maxPrice: Int): List<DigitalItem> =
        itemList.filter { minPrice <= it.price && it.price <= maxPrice } // 필터링
            .sortedBy { it.price } // 정렬 끝
}

// 테스트 케이스
fun main() {
    // 초기 잔액 10,000원 충전된 매니저 생성
    val billing = InAppBillingManager(initialBalance = 10000)

    val item1 = DigitalItem("SKU-01", "시즌 패스권", price = 12000)
    val item2 = DigitalItem("SKU-02", "경험치 부스터", price = 3000)
    val item3 = DigitalItem("SKU-03", "캐릭터 스킨", price = 8000)

    billing.registerItem(item1)
    billing.registerItem(item2)
    billing.registerItem(item3)

    println("=== 1. 3,000원 ~ 10,000원 범위 상품 목록 ===")
    billing.getItemListByPriceRange(3000, 10000).forEach {
        println("${it.name} - ${it.price}원")
    }

    println("\n=== 2. 최소 결제 금액 미달 쿠폰 적용 시도 ===")
    // 5000원 이상 결제 시 2000원 할인 쿠폰 -> 경험치 부스터(3000원)에 적용 시 조건 미달 실패
    val flatCoupon = PromoCoupon.FlatDiscount("MIN5000", discountAmount = 2000, minSpend = 5000)
    val result1 = billing.processPurchase("SKU-02", coupon = flatCoupon)
    printResult(result1)

    println("\n=== 3. 20% 할인 쿠폰 정상 결제 시도 ===")
    // 캐릭터 스킨(8000원) - 20%(1600원 할인, 최대 3000원 한도 내) = 최종 6400원 결제
    // 잔액: 10000원 -> 3600원 남음
    val percentCoupon = PromoCoupon.PercentageDiscount("SALE20", rate = 0.2, maxLimit = 3000)
    val result2 = billing.processPurchase("SKU-03", coupon = percentCoupon)
    printResult(result2)

    println("\n=== 4. 잔액 부족 결제 시도 ===")
    // 시즌 패스권(12000원) 구매 시도 -> 현재 잔액 3600원으로 잔액 부족 실패
    val result3 = billing.processPurchase("SKU-01", coupon = PromoCoupon.None)
    printResult(result3)
}

fun printResult(result: TransactionResult) {
    when (result) {
        is TransactionResult.Success ->
            println("[결제 승인] ${result.item.name} | 결제금액: ${result.finalPaidAmount}원 | 남은 잔액: ${result.remainingBalance}원")
        is TransactionResult.Failed ->
            println("[결제 거절] ${result.item.name} | 사유: ${result.reason}")
    }
}