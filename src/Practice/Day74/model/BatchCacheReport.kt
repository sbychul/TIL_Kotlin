package Practice.Day74.model

data class BatchCacheReport<T>(
    val totalCount: Int,
    val hitCount: Int,
    val missCount: Int,
    val totalElapsedMs: Long,
    val results: List<CacheResult<T>>
)
