package Practice.Day55

// 응용 과제 #4: 학생 성적 통계 및 그룹화 분석기 (Grade Analyzer)
// 강의계획서의 [6주 차: 컬렉션 가공] 및 [4주 차: 함수/람다]를 집중적으로 훈련하는 "학생 성적 집계 및 그룹 통계 분석기"입니다.
// 학생들의 과목별 시험 점수 데이터를 입력받아, 과목별 평균/최고 득점자를 계산하고 등급별로 학생 그룹을 분류하는 통계 엔진을 구현하세요.

// 학생 시험 성적 데이터 클래스
data class StudentScore(
    val studentId: String, // 학번
    val name: String, // 이름
    val subject: String, // 과목명
    val score: Int // 점수
)

// 과목별 요약 리포트 데이터 클래스
data class SubjectReport(
    val subject: String, // 과목명
    val avgScore: Double, // 해당 과목 평균 점수
    val topStudentName: String // 최고 득점자 이름, 동점자 발생 시 이름 가나다순 정렬
)

// 최종 분석 결과 데이터 클래스
data class GradeAnalysisResult(
    val reports: List<SubjectReport>, // 과목별 요약 리포트 리스트
    val gradeGroups: Map<String, List<String>> // 등급별 학생 이름 리스트 맵
    // 키: 등급, 값: 해당 등급에 속한 학생들의 중복 없는 이름 리스트 (오름차순 정렬)
)

// 오늘의 메인 메뉴
fun analyzeScores(scores: List<StudentScore>): GradeAnalysisResult {
    // 리스트를 과목별로 그룹화, Map<String, List<StudentScore>> 생성.
    val scoresBySubject = scores.groupBy { it.subject }

    // 맵을 순회하며 리포트 객체로 변환.
    val reports = scoresBySubject.map { (subject, scoreList) ->
        val avg = scoreList.map { it.score }.average() // 과목별 평균 점수를 계산.

        // 점수 내림차순 -> 이름 오름차순으로 정렬 후 첫 번째 학생을 선택
        val topStudent = scoreList.minWithOrNull (
            compareByDescending<StudentScore> { it.score }
                .thenBy { it.name }
        )!!.name

        SubjectReport(subject, avg, topStudent)
    }.sortedBy { it.subject } // 과목명 오름차순 정렬

    // 학생(학번)별로 그룹화, Map<String, List<StudentScore>> 생성.
    val scoresByStudent = scores.groupBy { it.studentId }

// 각 학생의 이름과 등급(A/B/C/F) 쌍을 계산
    val studentGrades: List<Pair<String, String>> = scoresByStudent.map { (_, studentScores) ->
        val studentName = studentScores.first().name
        val studentAvg = studentScores.map { it.score }.average()

        val grade = when {
            studentAvg >= 90.0 -> "A"
            studentAvg >= 80.0 -> "B"
            studentAvg >= 70.0 -> "C"
            else -> "F"
        }

        grade to studentName // Pair("A", "김철수") 형태
    }

// 등급("A", "B" 등)을 기준으로 다시 그룹화하고, 이름 리스트만 오름차순으로 추출.
    val gradeGroups = studentGrades
        .groupBy({ it.first }, { it.second }) // Key: 등급, Value: 이름 리스트
        .mapValues { (_, names) -> names.distinct().sorted() } // 중복 제거 및 이름 오름차순

    return GradeAnalysisResult(reports, gradeGroups)
}

// 테스트 케이스
fun main() {
    val scores = listOf(
        StudentScore("2026001", "김철수", "Kotlin", 95),
        StudentScore("2026001", "김철수", "Java", 85),   // 김철수 평균: 90.0 (A)

        StudentScore("2026002", "이영희", "Kotlin", 80),
        StudentScore("2026002", "이영희", "Java", 70),   // 이영희 평균: 75.0 (C)

        StudentScore("2026003", "박민수", "Kotlin", 95),  // Kotlin 최고점 동점 (김철수 vs 박민수 -> 가나다순 '김철수')
        StudentScore("2026003", "박민수", "Java", 90)    // 박민수 평균: 92.5 (A)
    )

    val result = analyzeScores(scores)

    println("=== 과목별 리포트 ===")
    result.reports.forEach { println(it) }

    println("\n=== 등급별 그룹 ===")
    result.gradeGroups.forEach { (grade, names) ->
        println("$grade 등급: $names")
    }
}