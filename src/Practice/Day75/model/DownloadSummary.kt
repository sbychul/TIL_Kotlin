package Practice.Day75.model

data class DownloadSummary(
    val totalCount: Int, // 전체 요청 작업 수
    val successCount: Int, // 성공 작업 수
    val failureCount: Int, // 실패 작업 수
    val totalDownloadedBytes: Long, // 성공한 파일들의 총 다운로드 바이트 합계
    val filesByExtension: Map<String, List<String>> // 성공한 파일들을 확장자별로 그룹화한 맵
)
