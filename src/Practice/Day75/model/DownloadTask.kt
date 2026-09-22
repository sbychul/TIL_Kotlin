package Practice.Day75.model

data class DownloadTask(
    val id: String,
    val fileName: String,
    val sizeBytes: Long, // 파일 용량, 예: 50_000_000L
    val checksum: String // 가상 무결성 체크섬, 예: "VALID" 또는 "CORRUPTED"
)
