package Practice.Day65

// 스마트폰 다운로드 매니저 및 상태 파이프라인 (Download Manager)
// [5주 차: 객체지향/실드 인터페이스 및 상태 캡슐화], [4주 차: 고차 함수/함수 타입], 그리고 [6주 차: 컬렉션 데이터 변환]에 집중합니다.
// 모바일 환경에서 여러 파일의 다운로드 요청을 큐(Queue)로 관리하고,
// 다운로드 진행도 및 상태 전이(대기 -> 다운로드 중 -> 완료/실패)를 실드 인터페이스로 캡슐화하여 UI 이벤트로 전달하는 시스템을 구현하세요.

data class DownloadTask(
    val id: String, // 작업 ID
    val filename: String, // 다운받을 파일명
    val fileSizeBytes: Long, // 전체 파일 크기, 바이트 단위
    var downloadedBytes: Long = 0L // 현재까지 내려받은 바이트, 기본값 0
)

// 다운로드 상태를 나타낼 sealed 인터페이스
sealed interface DownloadState {
    class Idle : DownloadState // 대기 상태
    data class Downloading(val taskId: String, val progressPercent: Int) : DownloadState // 다운로드 중
    data class Completed(val taskId: String, val fileName: String, val totalBytes: Long) : DownloadState // 다운로드 완료
    data class Failed(val taskId: String, val reason: String) : DownloadState // 실패
}

// 다운로드 매니저 클래스
class DownloadManager {
    // 내부 프로퍼티로 다운로드 작업 리스트를 가짐
    val taskList = mutableListOf<DownloadTask>()

    // 추가 메서드
    fun addTask(task: DownloadTask) { taskList.add(task) }
    // 리스트에 있는 다운로드 작업 시행 메서드
    fun processDownload(taskId: String, chunkSize: Long, onStateChanged: (DownloadState) -> Unit): DownloadState {
        // find를 이용하여 입력받은 id와 동일한 id의 작업이 있는지 확인, 없다면 실패 반환
        val task = taskList.find { it.id == taskId } ?: return DownloadState.Failed(taskId, "존재하지 않는 작업입니다.")
        // 파일의 크기가 0 이하라면 유효한 작업이 아니므로 실패 반환.
        if (task.fileSizeBytes <= 0) {return DownloadState.Failed(taskId, "유효하지 않은 파일 크기입니다.").also(onStateChanged) }

        // 유효한 작업임이 확인, 다운로드 시뮬레이션
        while (task.downloadedBytes < task.fileSizeBytes) {
            task.downloadedBytes += chunkSize // 다운받은 용량을 chunkSize만큼 계속 누적
            // 누적할 때마다 onStateChanged 람다에 Downloading 객체를 넘김.
            onStateChanged(DownloadState.Downloading(taskId, (task.downloadedBytes.toDouble() / task.fileSizeBytes * 100).toInt().coerceAtMost(100)))
        }
        // 다운로드 완료 시 람다에 넘기고 완료 객체 반환하며 마무리.
        val completed = DownloadState.Completed(taskId, task.filename, task.fileSizeBytes)
        onStateChanged(completed)
        return completed
    }

    // 동록된 모든 작업에 대해 taskId를 Key, 진행률을 Value로 갖는 Map을 반환하는 메서드
    fun getOverallProgress(): Map<String, Int> = taskList.associate { task ->
        val progress = if (task.fileSizeBytes <= 0L) 0 // 0일 경우 그냥 0 반환.
        else (task.downloadedBytes.toDouble() / task.fileSizeBytes * 100).toInt().coerceAtMost(100)
        task.id to progress
    }
}

// 테스트 케이스
fun main() {
    val manager = DownloadManager()

    val task1 = DownloadTask("T-01", "Kotlin_Standard_Library.pdf", fileSizeBytes = 1000L)
    val task2 = DownloadTask("T-02", "App_Update_Patch.apk", fileSizeBytes = 0L) // 실패 케이스 (크기 0)

    manager.addTask(task1)
    manager.addTask(task2)

    println("=== 1. 정상 다운로드 시뮬레이션 (T-01, Chunk: 300) ===")
    // 300씩 증가하며 Downloading(30%), Downloading(60%), Downloading(90%), Downloading(100%) -> Completed 호출
    val finalState1 = manager.processDownload("T-01", chunkSize = 300L) { state ->
        when (state) {
            is DownloadState.Downloading ->
                println("[진행 중] Task ${state.taskId}: ${state.progressPercent}%")
            is DownloadState.Completed ->
                println("[완료] ${state.fileName} 다운로드 완료! (${state.totalBytes} bytes)")
            is DownloadState.Failed ->
                println("[실패] Task ${state.taskId}: ${state.reason}")
            is DownloadState.Idle -> {}
        }
    }
    println("최종 반환 상태: $finalState1")

    println("\n=== 2. 유효하지 않은 파일 다운로드 시도 (T-02) ===")
    val finalState2 = manager.processDownload("T-02", chunkSize = 100L) { state ->
        if (state is DownloadState.Failed) {
            println("[알림] 다운로드 중단: ${state.reason}")
        }
    }
    println("최종 반환 상태: $finalState2")

    println("\n=== 3. 전체 작업별 현재 진행률 집계 ===")
    println(manager.getOverallProgress())
}