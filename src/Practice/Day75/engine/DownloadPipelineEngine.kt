package Practice.Day75.engine

import Practice.Day75.model.DownloadStatus
import Practice.Day75.model.DownloadSummary
import Practice.Day75.model.DownloadTask
import kotlinx.coroutines.delay

// 오늘의 메인 메뉴
class DownloadPipelineEngine {
    // 단일 파일 다운로드 메서드
    suspend fun downloadSingleTask(task: DownloadTask, onProgress: (percent: Int) -> Unit) : DownloadStatus {
        onProgress(0); // 일단 0%로 시작이라는 거임
        // 가상 다운로드 시뮬레이션
        delay(60) // 0.06초마다 50%가 참
        onProgress(50)
        delay(60)
        onProgress(100)

        // 가상 무결성 검사, 체크섬이 CORRUPTED라면 실패.
        if (task.checksum == "CORRUPTED") return DownloadStatus.Failure(task, "파일 체크섬이 일치하지 않습니다.")
        return DownloadStatus.Success(task, task.sizeBytes)
    }

    // 배치 다운로드 및 통계 산출 메서드
    suspend fun executeBatch(tasks: List<DownloadTask>, onTaskProgress: (taskId: String, percent: Int) -> Unit) : DownloadSummary {
        // 각각의 Task 요소를 순차적으로 순회하며 단일 파일 다운로드 메서드를 실행.
        // 실행 시 람다를 연결하여 외부로 진행률을 전달한다.
        val downloadResults: List<DownloadStatus> = tasks.map { downloadSingleTask(it, { percent -> onTaskProgress(it.id, percent)}) }
        val succeedDownloads = downloadResults.filterIsInstance<DownloadStatus.Success>()

        return DownloadSummary(
            downloadResults.size,
            succeedDownloads.size,
            downloadResults.filterIsInstance<DownloadStatus.Failure>().size,
            succeedDownloads.sumOf { it.downloadedBytes },
            succeedDownloads.groupBy (
                keySelector = { it.task.fileName.substringAfterLast('.', "") },
                valueTransform = { it.task.fileName }
            )
        )
    }
}