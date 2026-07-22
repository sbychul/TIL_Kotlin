package Practice.Day34

import java.lang.Thread.sleep

sealed class PaymentState {
    // 대기 상태를 나타내는 data object
    // object 앞에도 data를 붙일 수 있는데, 이를 출력(toString())하면 객체 이름이 그대로 나온다.
    data object Idle : PaymentState()
    // 성공 상태를 나타내는 data class, 본문에 timestamp 프로퍼티를 선언
    data class Success(val amount: Long) : PaymentState() { val timestamp: Long = System.currentTimeMillis() }
    // 실패 상태를 나타내는 data class
    data class Failure(val errorCode: Int, val errorMessage: String) : PaymentState()
}

fun main() {
    println("=== 1. 본문 프로퍼티와 copy() / equals()의 관계 ===")
    val success1 = PaymentState.Success(10000L)
    sleep(100) // 0.1초 대기 (timestamp 변수 차이 발생)
    val success2 = success1.copy() // data class에서 기본적으로 구현된 copy() 호출

    // 두 success 객체의 amount는 같지만 timestamp는 다르다.
    // data class의 equals(==)는 본문 변수인 timestamp는 비교하지 않는다.
    // 오직 주 생성자에 선언된 프로퍼티(amount)만 비교한다.
    // 컴파일러는 오직 주 생성자의 매개변수만을 사용하여 equals(), hashCode(), toString(), copy()를 합성하기 때문이다.
    println("success1: $success1")
    println("success2: $success2")
    println("success1 == success2 (equals 결과): ${success1 == success2}") // true

    println("\n=== 2. 구조 분해 할당 (Destructuring Declaration) ===")
    val failure = PaymentState.Failure(404, "잔액이 부족합니다.")

    // data class가 자동 생성해 주는 component1(), component2() 덕분에 아래와 같이 분해 가능하다.
    val (code, msg) = failure
    // 컴파일 시 내부적으로
    // val code = failure.component1()
    // val msg = failure.component2()
    // 로 처리된다. 주 생성자의 프로퍼티 순서대로 component1(), component2(), ... 메서드를 자동으로 생성해 준다.
    println("추출된 에러 코드: $code")
    println("추출된 에러 메시지: $msg")

    println("\n=== 3. Sealed Class + when 패턴 매칭 ===")
    val state: PaymentState = failure

    // when 표현식으로 결과를 출력하는 코드를 작성.
    val resultMessage = when (state) {
        is PaymentState.Idle -> "결제 대기 중..."
        is PaymentState.Success -> "결제 성공! 금액: ${state.amount}원"
        is PaymentState.Failure -> "결제 실패 [${state.errorCode}]: ${state.errorMessage}"
    }
    println("처리 결과: $resultMessage")
}