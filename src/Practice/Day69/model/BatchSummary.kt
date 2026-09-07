package Practice.Day69.model

// 작업 실행 요약 통계 데이터 클래스
data class BatchSummary<T> (
    val totalTasks: Int,
    val successCount: Int,
    val failureCount: Int,
    val successfulData: List<T>
)