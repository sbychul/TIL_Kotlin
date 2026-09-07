package Practice.Day69

import Practice.Day69.engine.*
import Practice.Day69.model.*
import kotlinx.coroutines.*
import java.io.IOException

// 비동기 작업 큐 및 지수 백오프 파이프라인 (Concurrent Work Engine)
// 이번 과제는 안드로이드의 대표적인 네트워크/데이터 처리 패턴인
// "모바일 배치 다운로더 및 동시성 작업 큐 (Concurrent Batch Work Pipeline Engine)"입니다.
// 대용량 미디어 파일이나 데이터 청크를 다운로드할 때 시스템 자원 고갈을 막기 위해 최대 동시 실행 작업 수(maxConcurrency)를 제한하고,
// 실패한 작업의 지수 백오프(Exponential Backoff) 재시도, 제네릭 결과 캡슐화, 그리고 고차 함수 기반의 DSL/파이프라인 변환을 직접 구현합니다.

// 오늘부터는 교수님께서 패키지 분리 계층형 방식을 선호하시기 때문에 패키지를 분리, 해당 Main에는 테스트 케이스만 있을 예정.

fun main() = runBlocking {
    // 최대 2개씩 동시 처리, 기본 백오프 딜레이 50ms
    val engine = ConcurrentWorkQueueEngine(maxConcurrency = 2, baseDelayMs = 50L)

    var failCounter = 0

    // 작업 리스트 생성
    val tasks = listOf(
        // 우선순위 1: 즉시 성공하는 일반 작업
        WorkItem(id = "TASK-01", priority = 1, maxRetries = 2) {
            println("-> [실행 중] TASK-01")
            delay(100)
            "Result-01"
        },
        // 우선순위 10: 최우선 작업, 1회 실패 후 2회차에 재시도로 성공
        WorkItem(id = "TASK-02", priority = 10, maxRetries = 3) {
            println("-> [실행 중] TASK-02 (시도 ${failCounter + 1})")
            if (failCounter++ < 1) {
                throw IOException("일시적 네트워크 타임아웃")
            }
            delay(50)
            "Result-02"
        },
        // 우선순위 5: 최대 재시도(2회)를 모두 초과하여 무조건 최종 실패하는 작업
        WorkItem(id = "TASK-03", priority = 5, maxRetries = 2) {
            println("-> [실행 중] TASK-03 항상 실패")
            throw IllegalStateException("치명적인 데이터 손상")
        },
        // 우선순위 8: 정상 작업
        WorkItem(id = "TASK-04", priority = 8, maxRetries = 2) {
            println("-> [실행 중] TASK-04")
            delay(80)
            "Result-04"
        }
    )

    println("=== 동시성 배치 파이프라인 가동 (우선순위 정렬 및 최대 2개 병렬 처리) ===")
    val summary = engine.processBatch(tasks)

    println("\n=== 처리 결과 통계 요약 ===")
    println("총 작업 수: ${summary.totalTasks}")
    println("성공 건수: ${summary.successCount}")
    println("실패 건수: ${summary.failureCount}")
    println("성공 데이터 목록: ${summary.successfulData}")
}