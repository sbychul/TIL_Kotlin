package Practice.Day72.engine

import Practice.Day72.model.BatchReport
import Practice.Day72.model.ProcessResult
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

// 메인이 되는 엔진 클래스
class ParallelTransformEngine {
    // 원본 데이터 리스트를 받아 각 아이템마다 worker 함수를 실행, 결과를 담은 리스트를 반환하는 메서드.
    // 일단 스코프를 시작
    suspend fun <T, R> transformAll(items: List<T>, worker: suspend (T) -> R) : BatchReport<R> = coroutineScope {
        // Deferred<ProcessResult<R>>를 담을 변수 선언
        val deferredList: List<Deferred<ProcessResult<R>>>
        // 결과를 담을 변수 선언
        val processResultList: List<ProcessResult<R>>

        val time = measureTimeMillis { // 시간 측정
            // map { async {...} }로 각 item에 대해 async로 코루틴을 생성하여 작업을 시작.
            // 작업의 결과인 Deferred<ProcessResult<R>>를 리스트르 만듦.
            deferredList = items.map { item ->
                async {
                    // try-catch 블록 내에서 상황 분기
                    try {
                        // try 블록 내에서 worker() 함수 실행, 성공 시 성공 객체를 그대로 반환
                        ProcessResult.Success(worker(item))
                    } catch (e: Throwable) { // catch (e: Throwable) : 일단 무언가 에러가 발생하면 뭐든지 잡겠다는 의미.
                        // 예외가 발생하면 Error 객체를 반환한다.
                        ProcessResult.Error(e.message ?: "알 수 없는 오류 발생")
                    }
                }
            }
            // 이후 해당 deferredList에 awaitAll을 시전(리스트 내 모든 Deferred<ProcessResult<R>> 객체에 await())
            processResultList = deferredList.awaitAll()
        } // awaitAll()까지 모든 실행 시간 측정 종료.

        // 이후 BatchReport 객체를 생성하여 반환한다.
        BatchReport(processResultList.size, time, processResultList)
    }
}