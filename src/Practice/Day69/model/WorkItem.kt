package Practice.Day69.model

// 작업 명세 데이터 클래스
data class WorkItem<out T> (
    val id: String,
    val priority: Int, // 우선 순위, 숫자가 클수록 먼저 실행.
    val maxRetries: Int = 3, // 최대 재시도 횟수, 기본값은 3
    val task: suspend () -> T // 실제 실행할 중단 함수
)