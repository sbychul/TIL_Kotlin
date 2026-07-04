// 제네릭 클래스. 자바와 개념적으로 동일하다, 설명 생략.
class SafeBox<T>(val content: T) {
    // try-catch문을 식으로 활용하여 요소를 숫자로 반환하는 함수.
    // 식으로 활용할 경우 마지막 문장이 바로 변수에 대입, 함수이므로 마지막 문장이 return된다.
    fun convertToInt() : Int = try {
        content.toString().toInt()
    } catch (e: NumberFormatException) {
        -1 // NumberFormatException을 잡아낸다면 -1을 반환한다.
    }
}

fun main() {
    // 두 개의 박스 객체 생성
    // 타입 추론이 아주 잘 되므로 SafeBox<String> 이런 식으로 쓸 필요가 딱히 없다.
    val box1 = SafeBox("123") // 숫자 형태의 문자열
    val box2 = SafeBox("아이고난") // 숫자로 바꿀 수 없는 문자열

    // 결과 확인용 출력부
    println("box1 변환 결과: [${box1.convertToInt()}]")
    println("box2 변환 결과: [${box2.convertToInt()}]")
}