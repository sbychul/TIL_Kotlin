// 학생 객체, 점수는 시험 미응시의 경우가 있을 수 있으므로 Nullable.
class Student(val name: String, val score: Int?) {}

fun main() {
    // Student 객체를 담은 리스트
    val t1 = listOf(Student("Doran", null),
        Student("Oner", 90),
        Student("Faker", 100),
        Student("Peyz", null),
        Student("Keria", 95))

    // 평균 연산. mapNotNull : score가 null이 아닌 값만 골라내 새 리스트를 만듦
    val avgOfScores = t1.mapNotNull { it.score }
        .average() // average() 내장 함수를 이용하여 점수의 평균을 반환

    // 빈 리스트의 평균값이 반환되었다면 0.0이 출력되도록 하고 아니라면 기존 평균값을 그대로 출력
    val finalAvg = if (avgOfScores.isNaN()) 0.0 else avgOfScores

    // 출력부
    println("평균 점수: ${finalAvg}")
}