package Practice.Day69.engine

import Practice.Day69.model.*
import kotlinx.coroutines.*

// 동시성 작업 큐 매니저
// 생성자 파라미터로 동시 실행 제한 수(기본값 2)와 기본 딜레이 시간(기본값 50ms)을 받는다.
class ConcurrentWorkQueueEngine(val maxConcurrency: Int = 2, val baseDelayMs: Long = 50L) {
    // item을 실행(실패 시 최대 재시도 횟수까지 재시도)하고 결과를 반환하는 함수.
    suspend fun <T> executeWithRetry(item: WorkItem<T>) : WorkResult<T> {
        var attempts = 0 // item의 task()를 실행 시도한 횟수.
        while (attempts < item.maxRetries) {
            attempts++
            try {
                val result = item.task() // 작업 실행
                return WorkResult.Success(result, attempts) // 성공한다면 그대로 반환.
            } catch (e: kotlinx.coroutines.CancellationException) {
                throw e // 코루틴 취소 예외는 재시도하지 않고 전파, 그대로 함수 종료.
            } catch (e: Throwable) { // 코루틴 취소 예외가 아니라 그냥 실패 시
                // 최대 재시도 횟수를 넘었다면 그대로 실패 반환
                if (attempts >= item.maxRetries) { return WorkResult.Failure(e, attempts) }
                // 지수 백오프(Exponential Backoff)를 사용, 실패할 때마다 대기 시간을 2배씩 늘려가며 재시도.
                // 비트 시프트 연산을 사용(shl: 왼쪽 시프트 연산 '<<')하여 attempts에 따라 대기 시간을 늘린다.
                val backOffDelay = baseDelayMs * (1L shl (attempts - 1))
                delay(backOffDelay) // 대기 후 while문 처음으로 넘어가 재시도.
                continue
            }
        }
        // 반복문을 빠져나올 리가 없는데 Missing return statement 컴파일 에러가 발생하길래 최후의 방어선
        return WorkResult.Failure(Exception("오류가 발생했습니다."), attempts)
    }

    suspend fun <T> processBatch(items: List<WorkItem<T>>): BatchSummary<T> = coroutineScope {
        // 우선순위 높은 순으로 정렬
        val sortedItems = items.sortedByDescending { it.priority }
        val allResults = mutableListOf<WorkResult<T>>() // 모든 결과를 담을 빈 리스트

        // maxConcurrency 크기만큼 청크로 분할 (예: 2개씩)
        val chunks = sortedItems.chunked(maxConcurrency)

        // 청크 단위로 순차 진행하되, 청크 내 작업들은 병렬로 실행
        for (chunk in chunks) {
            // 청크 내 각 작업을 async로 동시에(병렬로) 실행
            val deferredList = chunk.map { item ->
                async {
                    executeWithRetry(item)
                }
            }
            // 청크 내 모든 작업이 끝날 때까지 대기 후 awaitAll로 결과 리스트 수집
            val chunkResults: List<WorkResult<T>> = deferredList.awaitAll()
            allResults.addAll(chunkResults) // 모든 청크 결과를 빈 리스트에 옮겨담음.
        }

        // allResults를 바탕으로 각각의 BatchSummary 생성
        val successList = allResults.filterIsInstance<WorkResult.Success<T>>()
        val failureList = allResults.filterIsInstance<WorkResult.Failure>()

        BatchSummary(
            totalTasks = allResults.size,
            successCount = successList.size,
            failureCount = failureList.size,
            successfulData = successList.map { it.data }
        )
    }
}