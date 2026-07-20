package Practice.Day32

class SecureVault {
    // 내부 변수
    private val _items = mutableListOf<String>("Gold", "Diamond")

    // 외부 공개용 프로퍼티 1, 참조를 공유하는 단순 캐스팅
    val sharedItems: List<String>
        get() = _items
    // 내부 변수를 타입만 MutableList에서 List로 변환하여 반환.
    // 메모리 주소를 그대로 넘겨주되, 코틀린 타입 시스템 상 불변으로 만들어둔 형태이다.
    // 자바에서는 이 포인터로 조작할 여지가 생김.

    // 외부 공개용 프로퍼티 2, 완전 방어적 복사
    val copiedItems: List<String>
        get() = _items.toList()
    // 내부 변수를 그대로 복사, 새 객체를 생성한다.

    // 아이템 추가용 함수.
    fun addItem(item: String) {
        _items.add(item)
        println("[보안] 금고에 항목이 안전하게 추가되었습니다: $item")
    }

    // 금고 내 항목 중 특정 키워드가 포함된 항목만 필터링하여 새로운 읽기 전용 리스트로 반환.
    // 키워드를 포함하는지 확인, 대소문자 차이는 무시한다.
    fun getFilteredItems(keyword: String) : List<String> = _items.filter { it.contains(keyword, ignoreCase = true) }
}

fun main() {
    val vault = SecureVault()

    println("=== 1. 초기 상태 조회 ===")
    val shared1 = vault.sharedItems
    val copied1 = vault.copiedItems

    println("\n=== 2. 데이터 변경 (Bitcoin 추가) ===")
    vault.addItem("Bitcoin")

    println("\n=== 3. 변경 후 비교 (핵심 테스트!) ===")
    println("31일차 방식(shared1): $shared1") // 같은 메모리 주소(변수)를 참조하고 있기 때문에 변경된 값이 반영됨.
    println("32일차 방식(copied1): $copied1") // 복사된 별개의 객체이기 때문에 변경된 값이 반영되지 않음.

    println("\n=== 4. 필터링 기능 테스트 ===")
    val filtered = vault.getFilteredItems("oi") // "Bitcoin" 검색
    println("필터링 결과('oi' 포함): $filtered")
}