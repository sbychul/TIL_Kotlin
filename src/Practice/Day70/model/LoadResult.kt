package Practice.Day70.model

// 결과를 나타내는 sealed 인터페이스
sealed interface LoadResult<out T> {
    // 성공 시 소요 시간을 담아 반환
    data class Success<T>(val type: ResourceType, val data: T, val loadTimeMs: Long) : LoadResult<T>
    // 실패 시 에러 메시지를 담아 반환
    data class Failure(val type: ResourceType, val errorMsg: String) : LoadResult<Nothing>
}