// 함수 정의, fun 함수명(매개변수명: 자료형): 반환 자료형
// 단일 표현식 함수, return 키워드가 없어도 우변의 식의 결과값을 자동으로 반환.
// 단일 표현식이 아니라면 return 키워드가 있어야 함.
fun calculate(num1: Int, num2: Int, operator: String): Int = when (operator) {
    "+" -> num1 + num2
    "-" -> num1 - num2
    "*" -> num1 * num2
    "/" -> {
        if (num2 == 0) 0  // num2일 시 예외 처리
        else num1 / num2
    }
    else -> -1 // 잘못된 연산자 입력 시 -1 반환
}

fun main() {
    println(calculate(1, 2, "+"))
    println(calculate(2, 1, "-"))
    println(calculate(6, 7, "*"))
    println(calculate(9, 0, "/"))
    println(calculate(9, 3, "/"))
}