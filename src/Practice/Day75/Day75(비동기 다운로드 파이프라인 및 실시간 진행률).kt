package Practice.Day75

// 비동기 파일 다운로드 및 통계 엔진 (Download Pipeline & Progress Tracker)
// 모바일소프트웨어 강의계획서의 [4주 차: 고차 함수/람다 콜백], [5주 차: Sealed 인터페이스/상태 모델링], [6주 차: 컬렉션 가공/확장자별 Grouping],
// [7주 차: Coroutine 비동기 흐름]을 결합한 "비동기 다운로드 파이프라인 및 실시간 진행률 트래커 (Download Pipeline & Progress Tracker)"입니다.
// 안드로이드 앱 개발(예: 대용량 리소스 패치, 미디어 다운로더)에서 실시간으로 0% $\to$ 50% $\to$ 100% 진행 상황을 UI에 쏴주고,
// 완료된 파일의 무결성 검증과 메타데이터 통계를 뽑아내는 핵심 실무 패턴입니다.

import Practice.Day75.engine.DownloadPipelineEngine
import Practice.Day75.model.DownloadTask
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val engine = DownloadPipelineEngine()

    val taskList = listOf(
        DownloadTask("T-01", "intro_video.mp4", sizeBytes = 50_000_000L, checksum = "VALID"),
        DownloadTask("T-02", "app_icon.png", sizeBytes = 150_000L, checksum = "VALID"),
        DownloadTask("T-03", "corrupted_patch.zip", sizeBytes = 12_000_000L, checksum = "CORRUPTED"),
        DownloadTask("T-04", "banner.png", sizeBytes = 320_000L, checksum = "VALID")
    )

    println("=== 모바일 다운로드 파이프라인 가동 ===")

    val summary = engine.executeBatch(taskList) { taskId, percent ->
        println("-> [작업 $taskId] 진행률: $percent%")
    }

    println("\n=== 다운로드 완료 요약 리포트 ===")
    println("총 작업 수: ${summary.totalCount}")
    println("성공: ${summary.successCount}건, 실패: ${summary.failureCount}건")
    println("총 수신 용량: ${summary.totalDownloadedBytes} Bytes")
    println("확장자별 파일 분류: ${summary.filesByExtension}")
}