package Practice.Day76.engine

import Practice.Day76.model.*
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay

class ImagePipelineEngine {
    // 내부 프로퍼티로 맵(캐시)을 가짐
    private val processedCache = mutableMapOf<String, ImagePayload>()
    // 중위 함수
    infix fun containsInCache(imageId: String): Boolean = processedCache.containsKey(imageId)

    // 단일 이미지 처리 메서드
    private suspend fun processSingle(image: ImagePayload, filter: ImageFilter): ProcessResult {
        delay(50) // 가상 I/O 시뮬레이션
        // 파일 손상 여부 판정
        if (image.isCorrupted) return ProcessResult.Failure(image, "디코딩 에러(파일 손상)")
        // 정상이라면 필터 적용
        val transformed = filter.action(image)
        // 이후 copy를 이용하여 용량만 새로 계산된 값으로 교체한 새 객체를 생성
        val finalPayload = transformed.copy(sizeBytes = (image.sizeBytes * filter.sizeFactor).toLong())
        processedCache[image.id] = finalPayload // 이후 캐시에 등록
        // 성공 객체 반환
        return ProcessResult.Success(image, finalPayload, filter.name)
    }

    // 비동기 병렬 배치 실행 메서드
    suspend fun processBatch(images: List<ImagePayload>, filter: ImageFilter): BatchProcessingReport = coroutineScope {
        // 전달받은 이미지 목록을 async로 병렬 실행 후 awaitAll로 수집
        val processedImages = images.map { image -> async { processSingle(image, filter) } }.awaitAll()
        val succeedList = processedImages.filterIsInstance<ProcessResult.Success>()
        BatchProcessingReport(
            totalCount = processedImages.size,
            successCount = succeedList.size,
            failureCount = processedImages.filterIsInstance<ProcessResult.Failure>().size,
            totalSavedBytes = succeedList.sumOf { it.original.sizeBytes - it.processed.sizeBytes },
            resultsByFormat = succeedList
                .groupBy { it.processed.fileName.substringAfterLast('.', "") }
                .mapValues { it.value.size } // mapValues를 사용해 value를 리스트에서 해당 리스트의 크기로 변경
        )
    }
}