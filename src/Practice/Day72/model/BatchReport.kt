package Practice.Day72.model

// 결과 리포트 클래스
data class BatchReport<T>(
    val totalCount: Int,
    val totalElapsedMs: Long,
    val results: List<ProcessResult<T>>
)
