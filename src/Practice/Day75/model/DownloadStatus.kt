package Practice.Day75.model

sealed interface DownloadStatus {
    data object Pending : DownloadStatus // 대기 중
    data class InProgress(val percent: Int) : DownloadStatus // 진행 중
    data class Success(val task: DownloadTask, val downloadedBytes: Long) : DownloadStatus // 성공
    data class Failure(val task: DownloadTask, val reason: String) : DownloadStatus // 실패
}