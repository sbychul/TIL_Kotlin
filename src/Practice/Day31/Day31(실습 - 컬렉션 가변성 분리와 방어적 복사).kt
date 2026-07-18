package Practice.Day31

class CourseManager() {
    // 내부 변수: 실제 수강 과목들을 담을 가변 리스트
    // 코틀린의 정석 패턴이다. 언더바를 붙인 가변 프로퍼티를 숨겨두고, 외부에는 언더바 없는 불변 프로퍼티의 게터만 열어둔다.
    private val _courses = mutableListOf("Kotlin", "Java")

    // 외부 공개용 프로퍼티로 리스트를 하나 선언.
    // 외부에서 courses에 접근할 때, 내부 가변 리스트인 _courses를 읽기 전용 불변 타입인 List<String>으로 캐스팅하여 반환
    val courses: List<String>
        get() = _courses

    fun registerCourse(courseName: String) {
        _courses.add(courseName)
        println("[시스템] 과목 등록 완료: $courseName")
    }
}

fun main() {
    val manager = CourseManager()

    println("=== 1. 현재 수강 신청 목록 조회 ===")
    // 외부 공개용 읽기 전용 프로퍼티로 조회
    println("신청 목록: ${manager.courses}")

    println("\n=== 2. 신규 과목 등록 ===")
    manager.registerCourse("Jetpack Compose")
    println("갱신 목록: ${manager.courses}")

    println("\n=== 3. 외부 정적 해킹 시도 ===")
    // manager.courses.add("Python")
    // 컴파일러 환경에서 이 줄의 주석을 풀었을 때 컴파일 에러가 발생한다.
    // 불변 프로퍼티에 대한 접근이기 때문에 불가능하다.
    println("외부 조작이 컴파일 타임에 차단되었습니다.")
}