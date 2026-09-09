package Practice.Day71

// 모바일 뉴스 리더 동기화 매니저 (News Sync Engine)
// 융합 범위 (3~7주 차)
// [3~4주 차] 기본 함수 분기 및 측정 블록
// [5주 차] 제네릭 Sealed 인터페이스 (SyncStatus<out T>)
// [6주 차] 패키지 분리 (model, engine) 및 컬렉션 가공
// [7주 차] 코루틴: 순차 실행 파이프라인 vs 병렬(async/await) 파이프라인 비교

// 동일한 두 개의 비동기 데이터(헤드라인 뉴스, 날씨 요약)를 가져올 때,
// 순차 동기화 (syncSequential): 헤드라인을 다 가져온 '후'에 날씨를 가져옴 (총 소요 시간 = A + B)
// 병렬 동기화 (syncParallel): 헤드라인과 날씨를 async로 '동시에' 출발시켜 모음 (총 소요 시간 = max(A, B))
// 이 두 메서드를 나란히 구현하면서 코루틴이 실제로 코드상에서 어떻게 엮이고 시간 차이가 어떻게 나는지 손으로 직접 비교해 볼 수 있습니다.

// 테스트 케이스
import Practice.Day71.engine.*
import kotlinx.coroutines.*

// runBlocking 블록: 이 블록 안에서 시작한 코루틴 작업들이 다 끝날 때까지 기다림.
fun main() = runBlocking {
    val engine = NewsSyncEngine()

    println("=== 1. 순차 동기화 (Sequential) 실행 ===")
    val seqSummary = engine.syncSequential()
    println("모드: ${seqSummary.syncMode}")
    println("총 소요 시간: ${seqSummary.totalElapsedMs}ms (약 300ms 기대)")
    seqSummary.items.forEach { println("  -> [${it.category}] ${it.content}") }

    println("\n=== 2. 병렬 동기화 (Parallel) 실행 ===")
    val parSummary = engine.syncParallel()
    println("모드: ${parSummary.syncMode}")
    println("총 소요 시간: ${parSummary.totalElapsedMs}ms (약 150ms 기대)")
    parSummary.items.forEach { println("  -> [${it.category}] ${it.content}") }
}