package Practice.Day71.engine

import Practice.Day71.model.NewsItem
import Practice.Day71.model.SyncSummary
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class NewsSyncEngine {
    // 가상 API 메서드 2종
    suspend fun fetchHeadlines(): NewsItem {
        delay(150)
        return NewsItem("N-01", "HEADLINE", "맨체스터 시티, UCL 리그 페이즈 첫 경기에서 포르투를 2-0으로 제압")
    }
    suspend fun fetchWeather(): NewsItem {
        delay(150)
        return NewsItem("W-01", "WEATHER", "전국 대체로 맑음, 기온 22도")
    }

    // 순차 동기화 시
    suspend fun syncSequential(): SyncSummary {
        val result: SyncSummary
        val items: List<NewsItem>
        val time = measureTimeMillis {
            // 코루틴 스코프 없이 순차 실행. 150ms의 작업이 두 개이므로 예상 소요 시간 약 300ms
            items = listOf(fetchHeadlines(), fetchWeather())
        }
        result = SyncSummary(syncMode = "SEQUENTIAL", totalElapsedMs = time, items = items)
        return result
    }

    suspend fun syncParallel(): SyncSummary = coroutineScope {
        val items: List<NewsItem>
        val time = measureTimeMillis {
            // 150ms의 두 작업이 동시 시행, 예상 소요 시간 150ms.
            val headlineDeferred = async { fetchHeadlines() }
            val weatherDeferred = async { fetchWeather() }
            // .await을 통하여 Deferred<NewsItem> 객체에서 NewsItem만을 뽑아냄.
            items = listOf(headlineDeferred.await(), weatherDeferred.await())
        }
        SyncSummary(syncMode = "PARALLEL", totalElapsedMs = time, items = items)
    }
}