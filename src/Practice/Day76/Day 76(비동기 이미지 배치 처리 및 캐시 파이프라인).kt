package Practice.Day76

import Practice.Day76.engine.ImagePipelineEngine
import Practice.Day76.model.ImageFilter
import Practice.Day76.model.ImagePayload
import kotlinx.coroutines.runBlocking

// 비동기 이미지 배치 처리 및 캐시 파이프라인 엔진 (Image Processing Pipeline)
// 강의계획서의 [4주 차: 고차 함수/함수 타입/확장 프로퍼티], [5주 차: 연산자 오버로딩/Infix 함수/Sealed 계층],
// [6주 차: 컬렉션 통계 집계/Grouping], [7주 차: Coroutine 비동기 병렬 처리(async)/시간 측정]를 통합한
// "모바일 배치 이미지 처리 및 파이프라인 캐시 엔진 (Async Image Batch Pipeline & Cache Engine)"을 구현합니다.
// + 연산자로 파이프라인에 처리 필터를 합성하고, async를 활용해 이미지를 병렬 가공하며, infix 함수를 통해 캐시 적중 여부를 검사하는 실무형 이미지 프로세서입니다.

// 테스트 케이스
fun main() = runBlocking {
    val engine = ImagePipelineEngine()

    val images = listOf(
        ImagePayload("IMG-01", "cover.png", width = 1920, height = 1080, sizeBytes = 4_000_000L),
        ImagePayload("IMG-02", "avatar.jpg", width = 800, height = 800, sizeBytes = 800_000L),
        ImagePayload("IMG-03", "broken_file.png", width = 1000, height = 1000, sizeBytes = 1_200_000L, isCorrupted = true),
        ImagePayload("IMG-04", "banner.webp", width = 1200, height = 400, sizeBytes = 600_000L)
    )

    // 1. 개별 필터 정의
    // 리사이징 필터 (해상도 절반, 용량 50%)
    val resizeFilter = ImageFilter("HalfResize", sizeFactor = 0.5) { img ->
        img.copy(width = img.width / 2, height = img.height / 2)
    }
    // 압축 필터 (용량 60%로 추가 압축)
    val compressFilter = ImageFilter("LossyCompress", sizeFactor = 0.6) { img ->
        img // 해상도 유지, 용량 계수만 추가 적용
    }

    // 2. 연산자 오버로딩(+)으로 두 필터 합성: HalfResize 후 LossyCompress 적용 (최종 용량 0.5 * 0.6 = 0.3)
    val compositeFilter = resizeFilter + compressFilter
    println("합성 필터 이름: ${compositeFilter.name}, 최종 용량 배율: ${compositeFilter.sizeFactor}")

    println("\n=== 비동기 병렬 이미지 배치 파이프라인 가동 ===")
    val report = engine.processBatch(images, compositeFilter)

    println("\n=== 처리 결과 리포트 ===")
    println("총 이미지 수: ${report.totalCount}")
    println("성공: ${report.successCount}건, 실패: ${report.failureCount}건")
    println("절감된 총 데이터 용량: ${report.totalSavedBytes} Bytes")
    println("포맷별 성공 처리 수: ${report.resultsByFormat}")

    // 3. 중위 함수 (infix) 기반 캐시 적중 여부 확인
    println("\n=== 캐시 적중 확인 (infix) ===")
    println("IMG-01 캐시 존재 여부: ${engine containsInCache "IMG-01"}")
    println("IMG-03(손상 파일) 캐시 존재 여부: ${engine containsInCache "IMG-03"}")
}