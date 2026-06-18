// 자바랑 기본적으로 유사하나, 더 간결함.
// 클래스를 따로 작성해줄 필요가 없고, 파일에 main()함수만 바로 적어도 작동.
// public static void main(String[] args) == fun main()

fun main() {
    // 변수 선언 및 점수 할당.
    // 자료형을 앞에 적지 않고, 우변의 할당된 값을 보고 타입을 추론함.
    // val - value, 한 번 초기화하면 값을 변경할 수 없는 불변 변수(권장)
    val korean = 90
    val math = 80
    val english = 70

    // var - variable, 값을 자유롭게 변경할 수 있는 가변 변수
    var sum = korean + math + english
    var avg = sum / 3.0

    // if-else 표현식을 사용한 값 대입
    val result = if (avg >= 60 && (korean >= 40 && math >= 40 && english >= 40)) "Pass" else "Fail"

    println("평균: " + avg + "점 / 결과: " + result)
    // 문자열 템플릿 사용 시:
    println("평균: ${avg}점 / 결과: $result")
}