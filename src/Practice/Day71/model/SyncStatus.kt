package Practice.Day71.model

// 동기화 상태를 나타내는 인터페이스
sealed interface SyncStatus<out T> {
    // 성공 시 데이터를 담아 반환하고 실패하면 에러 메시지를 담는다.
    data class Success<T>(val data: T) : SyncStatus<T>
    data class Error(val message: String) : SyncStatus<Nothing>
}