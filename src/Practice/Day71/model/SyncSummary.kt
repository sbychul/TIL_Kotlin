package Practice.Day71.model

// 동기화 요약 데이터 클래스
data class SyncSummary(
    val syncMode: String, // SEQUENTIAL 또는 PARALLEL
    val totalElapsedMs: Long,
    val items: List<NewsItem>
)
