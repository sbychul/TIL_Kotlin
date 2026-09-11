package Practice.Day73.model

// 화면 표시용 UI 상태를 나타낼 인터페이스
interface ScreenState<out T> {
    object Loading : ScreenState<Nothing>
    data class Success<T>(val data: T, val totalElapsedMs: Long) : ScreenState<T>
    data class Error(val msg: String, val cause: Throwable? = null) : ScreenState<Nothing>
}