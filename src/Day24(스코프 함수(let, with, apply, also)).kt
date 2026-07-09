class SmartphoneOrder {
    // 세 가지 가변 프로퍼티를 가짐
    var model: String = ""
    var price: Int = 0
    var request: String = ""
}

// 주문 진행하는 함수
fun processOrder() : String {
    // 객체를 생성하자마자 apply 함수 사용.
    // apply 함수, 참조 방식: this (내부에서 this를 생략 가능하다) / 반환값: 수신 객체 자신(SmartphoneOrder)
    // 객체 생성 직후 프로퍼티들을 한꺼번에 초기화 할 때 사용한다.
    // 람다 블록 내 작성된 것을 그대로 바로 객체에 적용한다.
    val order = SmartphoneOrder().apply { model = "Galaxy S26 Ultra"; price = 1797400 }
        // also 함수, 참조 방식: it (이름을 바꿀 수 있다) / 반환값: 수신 객체 자신(SmartphoneOrder)
        // 객체 흐름 중간에 프린트 로그를 찍거나 객체의 유효성을 검증할 때 사용한다.
        .also { println("[로그] 모델명: ${it.model}, 금액: ${it.price}") }
        // let 함수, 참조 방식: it / 반환값: 블록의 맨 마지막 줄 결과값
        // 널 체크 후 작업하거나, 객체를 다른 타입의 값으로 변환하여 내보낼 때 사용한다.
        .let {
            it.request = "안전한 배송 부탁드립니다."
            "[주문 완료] 모델명: [${it.model}] / 요구사항: [${it.request}]" // 해당 문자열(String)이 order 프로퍼티에 대입된다.
        }
    // 안 쓰긴 했지만 with 함수, 참조 방식: this (생략 가능) / 반환값: 블록의 맨 마지막 줄 결과값
    // 확장 함수 형태가 아닌 with(object) {...} 형태로 사용, 이미 존재하는 객체의 필드를 연속해서 호출할 때 고정 스코프로 사용한다.

    return order
}

fun main() {
    // 의도대로 출력되는지 확인하자.
    println(processOrder())
}