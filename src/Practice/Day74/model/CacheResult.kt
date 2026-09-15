package Practice.Day74.model

sealed interface CacheResult<out T> {
    // 메모리에서 즉시 획득한 경우
    data class Hit<T>(val key: String, val data: T) : CacheResult<T>
    // Miss 시 네트워크에서 새로 다운로드
    data class Miss<T>(val key: String, val fetchedData: T, val elapsedMs: Long) : CacheResult<T>
}