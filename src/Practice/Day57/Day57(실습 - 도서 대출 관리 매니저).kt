package Practice.Day57

// 응용 과제 #6: 도서 대출 관리 및 연체료 정산 엔진 (Library Lending Manager)
// 이번 과제는 [6주 차: 컬렉션 가공]과 [4주 차: 함수/람다], 그리고 [5주 차: 클래스/인터페이스 설계]를 융합한
// "도서 대출 관리 및 연체료 계산 시스템 (Library Lending System)"입니다.
// 도서관의 도서 목록과 사용자 대출 기록을 기반으로 현재 대출 현황을 파악하고, 반납 지연에 따른 연체료 정산 및 우수/연체 회원을 분류하는 시스템을 구현하세요.

// 기본 도서 정보
data class Book(val id: String, val title: String, val category: String)
// 대출 기록
data class LoanRecord(val loanId: String, val bookId: String, val userName: String, val loanDays: Int, var isReturned: Boolean)
// 연체료 정산서
data class OverdueReport(val userName: String, val overdueBookTitles: List<String>, val totalLateFee: Int)

// 오늘의 메인 메뉴
class LibraryManager {
    // 내부 프로퍼티로 도서 목록과 도서 대출 기록 목록을 가짐.
    private var bookList: List<Book> = emptyList()
    private var loanList: List<LoanRecord> = emptyList()

    // 도서 목록과 대출 기록을 초기화(할당)하는 함수.
    fun registerData(books: List<Book>, loans: List<LoanRecord>) {
        this.bookList = books
        this.loanList = loans
    }

    // 반납되지 않은 기록을 찾아 (도서명, 대출자명) 형태의 Pair 리스트로 반환하는 함수.
    fun getCurrentlyBorrowedBooks(): List<Pair<String, String>> =
        loanList.filter { !it.isReturned }
            // 대출자명과 책 이름을 묶어 Pair로 이루어진 리스트를 반환.
            // loanList에 있는 빌려진 책의 id와 bookList에 있는 원본 id는 무조건 같기에 !!를 붙임.
            .map { loaned -> bookList.find { it.id == loaned.bookId }!!.title to loaned.userName }
            .sortedBy { it.second } // 도서명을 기준으로 정렬

    // 대출 기준 일수(standardDays)와 1일당 연체료(dailyFee)를 입력받아
    // 현재 미반납 상태이면서 대출 기간이 기준 일수를 초과한(loanDays > standardDays) 회원들의 연체 리포트를 생성하는 함수.
    fun calculateOverdueReports(standardDays: Int, dailyFee: Int): List<OverdueReport> {
        // id를 이용해 도서명을 찾을 수 있는 맵을 만듦.
        val bookMap = bookList.associateBy({ it.id }, { it.title })
        // 연체된 기록을 필터링
        val overdueLoans = loanList.filter { !it.isReturned && it.loanDays > standardDays }

        val reports = overdueLoans // 리포트의 리스트를 생성
            .groupBy { it.userName } // 이름으로 분류하고
            .map { (userName, loans) ->
                // 연체 도서 제목 리스트
                val titles = loans.mapNotNull { bookMap[it.bookId] }

                // 한 사람당 모든 누적 연체료를 계산: sumOf { (대출일수 - 기준일수) * 일일연체료 }
                val totalFee = loans.sumOf { (it.loanDays - standardDays) * dailyFee }

                // 객체 생성하여 리스트에 넣음.
                OverdueReport(userName, titles, totalFee)
            }

        // 연체료 내림차순, 이름 오름차순으로 정렬.
        return reports.sortedWith(
            compareByDescending<OverdueReport> { it.totalLateFee }
                .thenBy { it.userName }
        )
    }
}

// 테스트 케이스
fun main() {
    val books = listOf(
        Book("B-01", "코틀린 완벽 가이드", "CS"),
        Book("B-02", "클린 코드", "CS"),
        Book("B-03", "어린 왕자", "NOVEL"),
        Book("B-04", "자바의 정석", "CS")
    )

    val loans = listOf(
        LoanRecord("L-01", "B-01", "김철수", loanDays = 10, isReturned = false), // 연체 (기준 7일)
        LoanRecord("L-02", "B-02", "김철수", loanDays = 8, isReturned = false),  // 연체 (기준 7일)
        LoanRecord("L-03", "B-03", "이영희", loanDays = 14, isReturned = false), // 연체 (기준 7일)
        LoanRecord("L-04", "B-04", "박민수", loanDays = 5, isReturned = false),  // 정상 대출 중
        LoanRecord("L-05", "B-01", "최유저", loanDays = 20, isReturned = true)   // 반납 완료
    )

    val manager = LibraryManager()
    manager.registerData(books, loans)

    println("=== 현재 대출 중인 도서 목록 ===")
    // 기대: [(어린 왕자, 이영희), (자바의 정석, 박민수), (코틀린 완벽 가이드, 김철수), (클린 코드, 김철수)] (도서명 오름차순)
    println(manager.getCurrentlyBorrowedBooks())

    println("\n=== 연체자 정산 리포트 (기준 7일, 일 500원) ===")
    // 김철수: (10-7)*500 + (8-7)*500 = 1500 + 500 = 2000원
    // 이영희: (14-7)*500 = 3500원
    // 기대 순서: 이영희(3500원) -> 김철수(2000원)
    val reports = manager.calculateOverdueReports(standardDays = 7, dailyFee = 500)
    reports.forEach { println(it) }
}