package Practice.Day33

// amount 값(기본값: 0)을 받아 프로퍼티로 저장.
class GamePoint(val amount: Int = 0) {
    // 연산자 오버로딩은 미리 약속된 함수 이름과 매핑해야 하며
    // 반드시 함수 선언 앞에 operator 키워드를 붙여야 한다.

    // + 오버로딩, 기존 객체의 amount를 수정하지 않고, 합을 담은 새 GamePoint 인스턴스를 생성해 반환
    operator fun plus(other: GamePoint) : GamePoint = GamePoint(this.amount + other.amount)
    // - 오버로딩, 차이값이 0보다 작아지면 0포인트를 가진 새로운 GamePoint를 반환.
    operator fun minus(other: GamePoint) : GamePoint = GamePoint(maxOf(0, this.amount - other.amount))
    // * 오버로딩, 정수 배수를 받아 포인트를 N배 증폭시킨 새 GamePoint를 반환.
    operator fun times(other: Int) : GamePoint = GamePoint(this.amount * other)
}

// 연산 동작 및 불변성 검증
fun main() {
    val p1 = GamePoint(100)
    val p2 = GamePoint(50)

    println("=== 1. 기본 연산 테스트 (+, -, *) ===")
    val sum = p1 + p2       // internal: p1.plus(p2)
    val diff = p1 - p2      // internal: p1.minus(p2)
    val doubleP1 = p1 * 2   // internal: p1.times(2)

    println("p1 + p2 = ${sum.amount} pt")       // Expected: 150 pt
    println("p1 - p2 = ${diff.amount} pt")      // Expected: 50 pt
    println("p1 * 2  = ${doubleP1.amount} pt")  // Expected: 200 pt

    println("\n=== 2. 음수 방어 및 불변성 검증 ===")
    val overSpend = p2 - p1
    println("50pt - 100pt (음수 방어) = ${overSpend.amount} pt") // Expected: 0 pt

    println("p1의 원본 값 보존 여부 (100pt 유지?): ${p1.amount == 100}")
}