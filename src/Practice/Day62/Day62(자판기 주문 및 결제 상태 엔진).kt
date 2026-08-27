package Practice.Day62

// 응용 과제 #12: 스마트 자판기 주문 및 결제 상태 머신 (Vending Machine Engine)
// [5주 차: 객체지향/상태 캡슐화 및 실드 계층]과 [4주 차: 함수/스코프 함수]에 집중하여 머리를 정돈할 수 있는 '중' 난이도의 실전 응용 과제 #12를 준비했습니다.
// 이번 과제는 모바일 앱이나 키오스크 시스템의 핵심 로직인 "스마트 자판기 주문 및 결제 상태 머신 (Vending Machine State Engine)"입니다.
// 음료 상품 재고와 사용자 투입 금액을 관리하며, 상품 선택에 따른 성공/실패 상태를 명확히 분기하여 거스름돈을 반환하는 자판기 엔진을 구현하세요.

data class Beverage(
    val id: String, // 음료 고유 ID
    val name: String, // 음료명
    val price: Int, // 가격
    var stock: Int // 남은 재고 수량
)

// 구매 결과 상태
sealed interface PurchaseResult {
    // 구매 성공 시
    data class Success(val beverageName: String, val change: Int) : PurchaseResult
    // 재고가 없을 시
    data class OutOfStock(val beverageName: String) : PurchaseResult
    // 잔액이 부족할 경우 필요 금액과 현재 잔액을 담음
    data class InsufficientFunds(val beverageName: String, val requiredPrice: Int, val currentBalance: Int) : PurchaseResult
    // 등록되지 않은 음료의 ID일 경우
    data class ItemNotFound(val beverageId: String) : PurchaseResult
}

// 자판기 매니저 클래스(메인 메뉴)
class VendingMachine {
    // 내부 프로퍼티로 내부 음료 목록과 현재 투입된 금액을 가짐.
    private val beverages = mutableListOf<Beverage>()
    var currentBalance: Int = 0

    // 음료 등록 함수
    fun addBeverage(beverage: Beverage) { beverages.add(beverage) }
    // 금액 투입 함수
    fun insertMoney(amount: Int) {
        if (amount <= 0) { return } // 0원 이하의 경우 무시
        currentBalance += amount
    }

    // 결제 함수
    fun purchase(beverageId: String) : PurchaseResult {
        // 해당 음료를 id를 이용하여 탐색, 없으면 ItemNotFound 객체 반환.
        val beverage = beverages.find { it.id == beverageId } ?: return PurchaseResult.ItemNotFound(beverageId)
        // 재고 부족 시
        if (beverage.stock <= 0) { return PurchaseResult.OutOfStock(beverage.name) }
        // 돈이 없을 시
        if (beverage.price > currentBalance) { return PurchaseResult.InsufficientFunds(beverage.name, beverage.price, currentBalance) }

        // 결제 성공 시
        beverage.stock-- // 재고를 1 줄임
        currentBalance -= beverage.price // 잔액 차감
        // 거스름돈을 담아 성공 객체를 반환
        return PurchaseResult.Success(beverage.name, currentBalance)
    }

    // 잔액 반환 함수
    fun refund(): Int { val refunds = currentBalance; currentBalance = 0; return refunds }
    // 몰랐던 것이라 주석 처리) also 스코프 함수를 활용한 경우: return currentBalance.also { currentBalance = 0 }

    // 재고가 1 이상인 음료의 리스트를 반환
    fun getAvailableBeverages(): List<Beverage> = beverages.filter { it.stock > 0 }
}

// 테스트 케이스
fun main() {
    val machine = VendingMachine()

    val coffee = Beverage("B-01", "아메리카노", price = 2000, stock = 2)
    val latte = Beverage("B-02", "카페라떼", price = 2500, stock = 1)
    val juice = Beverage("B-03", "오렌지주스", price = 1500, stock = 0) // 품절

    machine.addBeverage(coffee)
    machine.addBeverage(latte)
    machine.addBeverage(juice)

    println("=== 1. 현재 구매 가능 음료 목록 ===")
    machine.getAvailableBeverages().forEach { println("${it.name} (${it.price}원) - 재고: ${it.stock}개") }

    println("\n=== 2. 금액 투입 및 구매 시도 ===")
    machine.insertMoney(3000) // 3000원 투입

    // 품절 상품 구매 시도
    val r1 = machine.purchase("B-03")
    printResult(r1)

    // 정상 구매 (아메리카노 2000원, 잔액 1000원 남음)
    val r2 = machine.purchase("B-01")
    printResult(r2)

    // 잔액 부족 구매 시도 (라떼 2500원인데 현재 잔액 1000원)
    val r3 = machine.purchase("B-02")
    printResult(r3)

    // 존재하지 않는 상품
    val r4 = machine.purchase("B-99")
    printResult(r4)

    println("\n=== 3. 거스름돈 반환 ===")
    val change = machine.refund()
    println("반환된 거스름돈: ${change}원 (자판기 남은 잔액: ${machine.currentBalance}원)")
}

fun printResult(result: PurchaseResult) {
    when (result) {
        is PurchaseResult.Success ->
            println("[구매 성공] ${result.beverageName} 완료! (남은 잔액: ${result.change}원)")
        is PurchaseResult.OutOfStock ->
            println("[품절] ${result.beverageName}은(는) 현재 재고가 없습니다.")
        is PurchaseResult.InsufficientFunds ->
            println("[잔액 부족] ${result.beverageName}(${result.requiredPrice}원)을(를) 사기에 잔액(${result.currentBalance}원)이 부족합니다.")
        is PurchaseResult.ItemNotFound ->
            println("[오류] ID '${result.beverageId}'에 해당하는 상품이 없습니다.")
    }
}