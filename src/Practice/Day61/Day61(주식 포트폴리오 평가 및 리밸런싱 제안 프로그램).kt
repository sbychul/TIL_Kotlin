package Practice.Day61

// 응용 과제 #10: 주식 포트폴리오 수익률 및 리밸런싱 엔진 (Stock Portfolio Analyzer)
// 오늘 준비한 실전 응용 과제 #11은 체감 난이도 '중' 수준으로 머리를 정돈하면서,
// 강의계획서의 [4주 차: 확장 함수/스코프 함수], [5주 차: 객체지향/실드 클래스], [6주 차: 컬렉션 데이터 변환]을
// 균형 있게 복습할 수 있는 "주식 포트폴리오 수익률 계산기 및 리밸런싱 분석기 (Stock Portfolio Analyzer)"입니다.
// 보유 주식 종목들의 매수가, 현재가, 보유 수량을 기반으로 종목별 평가손익을 계산하고,
// 전체 포트폴리오의 비중과 수익률을 분석하여 매도/매수/유지 추천 액션을 생성하는 시스템을 구현하세요.

// 종목 섹터 enum 클래스
enum class Sector{TECH, FINANCE, HEALTHCARE, CONSUMER}

// 주식 정보를 담을 데이터 클래스
data class StockHolding(
    val ticker: String, // 티커 심볼
    val name: String, // 종목명
    val sector: Sector, // 섹터
    var quantity: Int, // 보유 수량
    val purchasePrice: Double, // 평단가
    var currentPrice: Double // 현재가
)

// 주식 정보 데이터 클래스의 확장 프로퍼티
val StockHolding.totalPurchaseAmount: Double // 매수 총액
    get() = purchasePrice * quantity

val StockHolding.totalCurrentAmount: Double // 현재 평가 총액
    get() = currentPrice * quantity

val StockHolding.returnRate: Double // 개별 수익률
    get() = (currentPrice - purchasePrice) / purchasePrice * 100.0

// 투자 제안 액션 (RebalanceAction) 실드 인터페이스
sealed interface RebalanceAction {
    // 수익률이 +20% 이상일 때 익절 권유
    data class TakeProfit(val ticker: String, val profitRate: Double) : RebalanceAction
    // -10% 이하일 때 손절 권유 / 주의
    data class StopLoss(val ticker: String, val lossRate: Double) : RebalanceAction
    // 그 외 존버 권유
    data class Hold(val ticker: String, val returnRate: Double) : RebalanceAction
}

// 포폴 분석 결과 데이터 클래스
data class PortfolioSummary(
    val totalInvested: Double, // 총 매수 원금
    val totalEvaluation: Double, // 총 평가 금액
    val totalProfitRate: Double, // 전체 수익률 (백분율)
    val sectorWeights: Map<Sector, Double>, // 섹터별 평가금액 비중 맵
    val actions: List<RebalanceAction> // 종목별 투자 제안 액션 리스트
)

// 포트폴리오 분석 함수.
fun analyzePortfolio(holdings: List<StockHolding>): PortfolioSummary {
    // 보유 주식 목록이 아예 비어 있다면 그냥 기본값을 반환하고 끝낸다.
    if (holdings.isEmpty()) {
        return PortfolioSummary(
            totalInvested = 0.0,
            totalEvaluation = 0.0,
            totalProfitRate = 0.0,
            sectorWeights = emptyMap(),
            actions = emptyList()
        )
    }

    // 모든 종목의 totalPurchaseAmount 합산 및 totalCurrentAmount 합산
    val totalPurchaseAmount = holdings.sumOf { it.totalPurchaseAmount }
    val totalCurrentAmount = holdings.sumOf { it.totalCurrentAmount }

    // 전체 수익률 계산
    val profitRate = (totalCurrentAmount - totalPurchaseAmount) / totalPurchaseAmount * 100

    val sectorWeights = holdings.groupBy { it.sector } // 종목들을 섹터별로 그룹화 (Map<String, List<StockHolding>>)
        // 각 섹터별 평가액의 합을 구하여 전체 평가 금액 대비 해당 섹터의 비중을 계산하여 매핑. (Map<String, Double>)
        .mapValues { (_, holdings) -> holdings.sumOf { it.totalCurrentAmount / totalCurrentAmount * 100 } }

    // 각 종목의 수익률에 따라 투자 제안을 담은 리스트를 생성
    val actions = holdings.map {
        // 수익률이 20% 이상일 때
        if (it.returnRate >= 20.0) { RebalanceAction.TakeProfit(it.ticker, it.returnRate) }
        else if (it.returnRate <= -10.0) { RebalanceAction.StopLoss(it.ticker, it.returnRate) }
        else RebalanceAction.Hold(it.ticker, it.returnRate)
    }

    // 최종 객체를 만들어 반환.
    return PortfolioSummary(totalPurchaseAmount, totalCurrentAmount, profitRate, sectorWeights, actions)
}

// 테스트 케이스
fun main() {
    val myHoldings = listOf(
        StockHolding("AMD", "Advanced Micro Devices", Sector.TECH, quantity = 10, purchasePrice = 100.0, currentPrice = 130.0), // 수익률 +30.0% (TakeProfit)
        StockHolding("INTC", "Intel Corporation", Sector.TECH, quantity = 20, purchasePrice = 40.0, currentPrice = 30.0),      // 수익률 -25.0% (StopLoss)
        StockHolding("JNJ", "Johnson & Johnson", Sector.HEALTHCARE, quantity = 5, purchasePrice = 150.0, currentPrice = 155.0)  // 수익률 +3.33% (Hold)
    )

    val summary = analyzePortfolio(myHoldings)

    println("=== 포트폴리오 요약 ===")
    println("총 매수 원금: $${summary.totalInvested}")
    println("총 평가 금액: $${summary.totalEvaluation}")
    println("전체 수익률: ${String.format("%.2f", summary.totalProfitRate)}%")

    println("\n=== 섹터별 비중 ===")
    summary.sectorWeights.forEach { (sector, weight) ->
        println("$sector: ${String.format("%.2f", weight)}%")
    }

    println("\n=== 리밸런싱 제안 ===")
    summary.actions.forEach { action ->
        when (action) {
            is RebalanceAction.TakeProfit ->
                println("[익절 권유] ${action.ticker}: +${String.format("%.2f", action.profitRate)}% 달성 (수익 실현 고려)")
            is RebalanceAction.StopLoss ->
                println("[손절 주의] ${action.ticker}: ${String.format("%.2f", action.lossRate)}% 하락 (리스크 관리 필요)")
            is RebalanceAction.Hold ->
                println("[보유 유지] ${action.ticker}: ${String.format("%.2f", action.returnRate)}% (안정권 유지)")
        }
    }
}