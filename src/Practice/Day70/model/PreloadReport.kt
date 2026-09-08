package Practice.Day70.model

// 스플래시 사전 로딩 종합 보고서 클래스
data class PreloadReport(
    val isAllSuccess: Boolean, // 모든 리소스가 성공했는지 여부
    val totalElapsedMs: Long, // 총 소요 시간
    val loadedDataMap: Map<ResourceType, Any>, // 성공한 리소스들의 타입과 데이터 맵
    val failedTypes: List<ResourceType> // 로딩에 실패한 데이터 타입들을 담을 리스트
)
