package Practice.Day76.model

// 이미지 데이터 클래스
data class ImagePayload(
    val id: String,
    val fileName: String,
    val width: Int,
    val height: Int,
    val sizeBytes: Long,
    val isCorrupted: Boolean = false
)
