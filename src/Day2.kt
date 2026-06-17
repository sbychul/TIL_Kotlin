// 2일차, when문을 이용한 조건 분기
fun main() {
    // 점수
    val score = 98
    // 학점 변수, 조건 분기에 따른 값 변동 예정.
    var grade = ""

    // 점수에 따른 학점 조건 분기, if문을 활용한 기본적인 방법
    if (score >= 90 && score <= 100) { grade = "A" }
    else if (score >= 80) { grade = "B" }
    else if (score >= 70) { grade = "C" }
    else { grade = "F" }

    // 점수에 따른 학점 조건 분기, when문 + 범위 표현식
    // .. 연산자와 in 키워드를 활용하여 표현: in 90..100 = 90 ≤ score ≤ 100. 직관적인 표현 가능.
    grade = when (score) {
        in 90..100 -> "A"
        in 80..89 -> "B"
        in 70..79 -> "C"
        else -> "F"
    }

    println("점수 [${score}]점, 학점: [${grade}]")

    // if문을 통한 출력, 자바와 다르게 문자열에도 == 연산자를 사용할 수 있음.
    if (grade == "A" || grade == "B") { println("우수 학생 축하 대상자입니다.") }

    // when문을 통한 출력
    when (grade) { "A", "B" -> println("우수 학생 축하 대상자입니다.") }
}