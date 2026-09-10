package Practice.Day72.model

interface ProcessResult<out T> {
    // 성공 시 데이터를 담고 실패 시 메시지를 담음
    data class Success<T>(val data: T) : ProcessResult<T>
    data class Error(val message: String) : ProcessResult<Nothing>
}