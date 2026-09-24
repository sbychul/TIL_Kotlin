package Practice.Day76.model

// 처리 결과
sealed interface ProcessResult {
    data class Success(val original: ImagePayload, val processed: ImagePayload, val appliedFilter: String) : ProcessResult
    data class Failure(val payload: ImagePayload, val reason: String) : ProcessResult
}