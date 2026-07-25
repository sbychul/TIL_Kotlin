package Practice.Day37

data class Transaction(val id: Long, val category: String, val amount: Long, val isCanceled: Boolean = false)

class TransactionAnalyzer {
    fun getValidTotalAmount(transactions: List<Transaction>): Long =
        transactions.filter { !it.isCanceled } // 취소되지 않은 거래만 골라내
            .sumOf { it.amount } // amount의 합을 반환

    fun groupByCategory(transactions: List<Transaction>) : Map<String, List<Transaction>> =
        transactions.groupBy { it.category } // 리스트의 요소의 카테고리를 Key 삼은 Map을 반환

    fun getTopNLargeTransactionsLazy(transactions: List<Transaction>, n: Int): List<Long> =
        transactions.asSequence() // 체이닝 연산 중 최종 연산(toList, count 등)이 호출될 때까지 실제 연산을 미룸 (지연 연산 처리)
        .filter { !it.isCanceled } // 취소되지 않은 거래 중
        .sortedByDescending { it.amount } // amount를 기준으로 내림차순 정렬
        .take(n) // 상위 n개를 추출
        .map { it.id } // id만을 남겨서
        .toList() // List<Long> 타입으로 반환.
}

// 테스트 케이스
fun main() {
    val transactions = listOf(
        Transaction(1L, "ELECTRONICS", 1500000L),
        Transaction(2L, "CLOTHING", 50000L),
        Transaction(3L, "ELECTRONICS", 2000000L, isCanceled = true), // 취소됨
        Transaction(4L, "FOOD", 12000L),
        Transaction(5L, "ELECTRONICS", 800000L)
    )

    val analyzer = TransactionAnalyzer()

    println("=== 1. 유효 거래 총금액 ===")
    println(analyzer.getValidTotalAmount(transactions))
    // 출력: 2362000 (150만 + 5만 + 1.2만 + 80만)

    println("\n=== 2. 카테고리별 그룹화 ===")
    val grouped = analyzer.groupByCategory(transactions)
    println("ELECTRONICS 건수: ${grouped["ELECTRONICS"]?.size}") // 출력: 3

    println("\n=== 3. 지연 연산(Sequence) 상위 N개 추출 ===")
    val topIds = analyzer.getTopNLargeTransactionsLazy(transactions, 2)
    println("상위 2개 거래 ID: $topIds")
    // 출력: [1, 5] (취소된 2번 200만 원 제외, 150만(1번)과 80만(5번) 추출)
}