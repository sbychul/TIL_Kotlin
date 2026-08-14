package Practice.Day53

import kotlin.times

// 응용 과제 #2: 장바구니 할인 적용기 (Cart Discount Processor)
// 강의계획서의 [3주 차: 조건문/제어문] 및 [6주 차: 컬렉션 가공]을 활용.
// 쇼핑몰 앱에서 사용자가 담은 상품 리스트와 등급, 할인 쿠폰 정보를 받아 최종 결제 금액과 상세 영수증 객체를 반환하는 함수를 작성하세요.

// 기본 데이터 구조
// 상품 정보
data class CartItem(
    val name: String,
    val price: Int,
    val quantity: Int,
    val category: String // "ELECTRONICS", "FOOD", "CLOTHING" 등
)

// 회원 등급
enum class UserTier { VIP, GOLD, SILVER }

// 최종 계산된 영수증 정보
data class Receipt(
    val originalTotal: Int,    // 할인 전 총액
    val discountTotal: Int,    // 총 할인 금액
    val finalTotal: Int,       // 최종 결제 금액 (originalTotal - discountTotal)
    val freeShipping: Boolean  // 무료 배송 여부 (최종 결제 금액 30,000원 이상이면 true)
)

// 오늘의 메인 메뉴, 함수 구현하기
fun calculateReceipt(items: List<CartItem>, userTier: UserTier, couponCode: String?): Receipt {
    // 각 상품의 price와 quantity를 items 리스트에서 꺼내 sumOf로 합산.
    val ogTotal = items.sumOf { it.price * it.quantity }

    // 할인 금액 합계를 담을 변수
    var dcTotal = 0
    // 리스트를 순회하며 전자제품(ELECTRONICS) 카테고리 상품이 하나라도 있다면 5000원 추가 할인 적용 후 반복문 탈출.
    for (item in items) { if (item.category == "ELECTRONICS") { dcTotal += 5000; break; }}
    // couponCode가 "WELCOME1000"일 경우 1000원 추가 할인.
    if (couponCode == "WELCOME1000") { dcTotal += 1000 }

    // when문으로 enum을 활용, 등급별 비율 할인을 적용
    dcTotal += when (userTier) {
        UserTier.VIP -> ogTotal * 15 / 100
        UserTier.GOLD -> ogTotal * 10 / 100
        UserTier.SILVER -> ogTotal * 5 / 100
    }

    // 총 할인 금액이 원금을 초과할 수 없도록 함.
    dcTotal = minOf(ogTotal, dcTotal)

    // 총 지불 금액
    val total = ogTotal - dcTotal

    // 총 금액이 30000원 이상이면 무료배송 적용까지 판별하여 객체 반환
    return Receipt(ogTotal, dcTotal, total, total >= 30000)
}

// 테스트 케이스
fun main() {
    val cart = listOf(
        CartItem("키보드", 40000, 1, "ELECTRONICS"),
        CartItem("음료수", 2000, 5, "FOOD")
    )

    // 원금: 40,000 + 10,000 = 50,000원
    // GOLD 등급 할인(10%): 5,000원
    // ELECTRONICS 보유 추가 할인: 5,000원
    // WELCOME1000 쿠폰: 1,000원
    // 총 할인: 11,000원 -> 최종 결제: 39,000원 (3만원 이상이므로 무료배송 true)

    val receipt = calculateReceipt(cart, UserTier.GOLD, "WELCOME1000")
    println(receipt)
}