package Practice.Day76.model

data class BatchProcessingReport(
    val totalCount: Int,
    val successCount: Int,
    val failureCount: Int,
    val totalSavedBytes: Long, // 성공한 이미지들의 original.sizeBytes - processed.sizeBytes 합계
    val resultsByFormat: Map<String, Int> // 성공한 파일 확장자별 처리 건수
)
