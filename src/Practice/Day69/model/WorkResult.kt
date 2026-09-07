package Practice.Day69.model

// 작업 상태 및 결과를 나타낼 sealed 인터페이스
interface WorkResult<out T> {
    // 성공 시 데이터와 시도 횟수를 담아 반환
    data class Success<T>(val data: T, val attempts: Int) : WorkResult<T>
    // 실패 시 Throwable 객체와 시도 횟수를 담아 반환
    data class Failure(val error: Throwable, val attempts: Int) : WorkResult<Nothing>
}