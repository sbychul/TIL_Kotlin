fun main() {
    // Nullable 타입 ?를 사용. 기본적으로 코틀린의 변수엔 null을 담을 수 없음.
    // String - null 불가 / String? - null도 가능. 반드시 자료형 뒤에 물음표를 붙일 것.
    // 사실 자동으로 타입 추론 해줘서 안 써도 상관 없긴 하다.
    val list = listOf<String?>("kotlin", null, "java", "python", null)

    for (i in list) {
        // ?. - null일 수 있는 변수의 메소드를 호출할 때 사용하는 연산자.
        // null이 아니면 뒤의 메소드를 실행. null이면 실행하지 않고 그대로 null 반환.
        // ?: - 엘비스 연산자, 앞의 연산 결과가 null이라면 뒤에 지정한 기본값을 반환.
        // i?.uppercase()가 null일 때 "EMPTY"를 출력하게 된다.
        println(i?.uppercase() ?: "EMPTY")
    }
}