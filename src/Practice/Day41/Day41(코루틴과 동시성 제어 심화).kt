package Practice.Day41

import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlin.system.measureTimeMillis

// 1. 가상 불안정 API (3의 배수일 때 에러 발생)
suspend fun fetchUnstableData(userId: Int): String {
    delay(500L)
    if (userId % 3 == 0) { // 3의 배수라면 런타임 예외를 던져버린다.
        throw RuntimeException("Server Error on ID $userId")
    }
    return "Data-$userId"
}

// 2. supervisorScope + Semaphore + runCatching 조합
// supervisorScope: 자식 코루틴에서 예외가 발생하더라도 다른 형제 코루틴에게 취소가 전파되지 않음, 일부 실패를 허용해야 하는 배치 작업에 필수.
// semaphore.withPermit { ... }로 감싸면 동시에 블록 안으로 진입할 수 있는 코루틴의 개수를 지정된 수로 제어, 스레드를 블로킹하지 않고 코루틴을 대기(Suspend)시키는 방식
// runCatching: 알잖아
// 각 userId에 대해 비동기(async)로 작업을 실행하되,
// 예외가 발생할 수 있으므로 runCatching을 활용해 Result<String> 형태로 감싸서 Map<Int, Result<String>> (유저 ID -> 성공/실패 결과) 형태로 반환.
suspend fun fetchBatchUserData(
    userIds: List<Int>,
    maxConcurrency: Int = 3
): Map<Int, Result<String>> = supervisorScope {
    val semaphore = Semaphore(maxConcurrency)
    userIds.map { id ->
        async {
            id to semaphore.withPermit { // id to Result 페어를 만들어 줌
                runCatching {
                    fetchUnstableData(id)
                }
            }
        }
    }.awaitAll().toMap() // Pair(id, Result) 목록을 Map으로 깔끔하게 변환!
}

fun main() = runBlocking {
    println("=== Day 41 (심화): Supervisor & Semaphore 비동기 수집 테스트 ===")

    val userIds = (1..10).toList() // 1부터 10까지 총 10명의 유저

    val time = measureTimeMillis {
        // 동시 실행 수를 최대 3개로 제한하여 배치 실행
        val results = fetchBatchUserData(userIds, maxConcurrency = 3)

        println("\n[수집 결과 요약]")
        results.forEach { (id, result) ->
            result.onSuccess { data ->
                println("User $id: 성공 -> $data")
            }.onFailure { error ->
                println("User $id: 실패 -> ${error.message}")
            }
        }
    }

    println("\n총 소요 시간: ${time}ms")
    // 💡 소요 시간 예상:
    // 10개 작업, 각각 500ms 소요, 최대 동시 3개 실행
    // -> [3개(500ms)] + [3개(500ms)] + [3개(500ms)] + [1개(500ms)] = 약 2000ms 안팎 소요되어야 함!
}