package Practice.Day58

// 응용 과제 #7: 스마트 할 일(To-Do) 매니저 (Todo Task Manager)
// 이번 과제는 강의계획서의 [4주 차: 함수/확장 함수], [5주 차: 클래스/상속], [6주 차: 컬렉션 표준 함수] 범위를 다루는 "할 일 목록(To-Do) 관리 및 우선순위 필터링 엔진"입니다.
// 할 일의 우선순위와 완료 상태, 마감 기한을 관리하고 조건에 맞춰 목록을 필터링 및 요약하는 시스템을 구현하세요.

// 우선순위
enum class Priority {
    HIGH, MEDIUM, LOW
}

// 할 일 클래스
data class TodoItem(
    val id: Long, // 고유 ID
    val title: String, // 제목
    val priority: Priority, // 우선순위
    var isCompleted: Boolean = false, // 완료 여부, 기본값은 false
    val daysRemaining: Int // 데드라인까지 남은 기간
)

// 매니저 클래스
class TodoManager {
    val todoList = mutableListOf<TodoItem>() // 내부 프로퍼티로 리스트를 가짐.

    // 할 일 추가 함수
    fun addTask(task: TodoItem) { todoList.add(task) }

    // 할 일 완료 함수, 전달받은 id를 가진 할 일을 찾아 isCompleted를 true로 변환 후 true를 반환
    // 만약 id에 대응하는 일이 없다면 false를 반환
    fun completeTask(id: Long) : Boolean =
        // 찾으면 TodoItem이 됨, 못 찾으면 null.
        // 찾았다면 let문이 실행되고, 못 찾았다면 그대로 false가 반환.
        todoList.find { it.id == id }?.let { it.isCompleted = true; true } ?: false

    // 긴급하게 할 일 리스트를 반환하는 함수
    fun getUrgentTasks(): List<TodoItem> =
        // 안 끝난 것들 중에서 우선순위가 높거나, 마감 기한이 3일 이하인 것만
        todoList.filter { !it.isCompleted && (it.priority == Priority.HIGH || it.daysRemaining <= 3) }
            .sortedBy { it.daysRemaining } // 마감 기한 임박 우선으로 정렬.

    // 전체 등록된 할 일 중 완료된 할 일의 비율을 백분율로 계산하여 반환.
    fun getCompletionRate(): Double {
        // 아무것도 없다면 0.0을 반환
        if (todoList.isEmpty()) { return 0.0 }
        // 끝난 것의 개수를 구함
        val completedCount = todoList.count{ it.isCompleted }
        // 백분율 구하기
        return completedCount.toDouble() / todoList.size * 100
    }
}

// TodoItem의 확장 함수
// 완료 시: "[V] [HIGH] 과제 제출하기 (D-1)"
// 미완료 시: "[ ] [HIGH] 과제 제출하기 (D-1)" 형식의 문자열을 반환
// 체크박스 차이가 있음.
fun TodoItem.toDisplayText(): String {
    return "[${if (isCompleted) "V" else " "}] [$priority] $title (D-$daysRemaining)"
}

// 테스트 케이스
fun main() {
    val manager = TodoManager()

    val task1 = TodoItem(1L, "알고리즘 과제 제출", Priority.HIGH, daysRemaining = 1)
    val task2 = TodoItem(2L, "헬스장 가기", Priority.LOW, daysRemaining = 5)
    val task3 = TodoItem(3L, "안드로이드 강의 예습", Priority.MEDIUM, daysRemaining = 2)
    val task4 = TodoItem(4L, "방 청소", Priority.LOW, daysRemaining = 7, isCompleted = true)

    manager.addTask(task1)
    manager.addTask(task2)
    manager.addTask(task3)
    manager.addTask(task4)

    println("=== 등록된 전체 할 일 목록 ===")
    // getUrgentTasks() 또는 전체 순회 시 확장 함수 사용
    println(task1.toDisplayText())
    println(task4.toDisplayText())

    println("\n=== 긴급 할 일 목록 (미완료 & (HIGH 또는 D-3 이하)) ===")
    // 기대 순서: 알고리즘 과제(D-1) -> 안드로이드 예습(D-2)
    manager.getUrgentTasks().forEach { println(it.toDisplayText()) }

    println("\n=== 완료율 계산 ===")
    // 4개 중 1개 완료 -> 25.0%
    println("현재 완료율: ${manager.getCompletionRate()}%")

    // 과제 1 완료 처리
    manager.completeTask(1L)
    // 4개 중 2개 완료 -> 50.0%
    println("Task 1 완료 후 완료율: ${manager.getCompletionRate()}%")
}