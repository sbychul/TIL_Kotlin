package Practice.Day70.engine

import Practice.Day70.model.*
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class SplashPreloaderEngine {
    // 3개의 리소스 타입 각각 불러오는 메서드.
    suspend fun fetchRemoteConfig(): LoadResult<String> {
        delay(100)
        return LoadResult.Success(ResourceType.REMOTE_CONFIG, "v2.1.0-release", 100L)
    }
    suspend fun fetchUserSession(): LoadResult<String> {
        delay(150)
        return LoadResult.Success(ResourceType.USER_SESSION, "User(id=dev_user, auth=VALID)", 150L)
    }
    suspend fun loadStaticAssets(shouldFail: Boolean = false): LoadResult<Int> {
        delay(120)
        // shouldFail 플래그에 따른 결과 분기
        return if (shouldFail) { LoadResult.Failure(ResourceType.STATIC_ASSETS, "에셋 압축 해제 실패") }
        else { LoadResult.Success(ResourceType.STATIC_ASSETS, 42, 120L) }
    }

    // 모든 리소스를 한번에 불러오는 메서드
    suspend fun preloadAll(shouldAssetFail: Boolean = false): PreloadReport = coroutineScope {
        var results: List<LoadResult<*>> = emptyList()

        // measureTimeMillis 활용 소요 시간 측정
        val elapsed = measureTimeMillis {
            // 3개 작업을 동시에 async로 시작
            val configDeferred = async { fetchRemoteConfig() }
            val sessionDeferred = async { fetchUserSession() }
            val assetDeferred = async { loadStaticAssets(shouldAssetFail) }

            // 3개 결과가 모두 올 때까지 await
            results = listOf(
                configDeferred.await(),
                sessionDeferred.await(),
                assetDeferred.await()
            )
        } // measureTimeMills 블록 종료, 모든 결과를 받는다면 시간 측정이 끝난다.

        // filterIsInstance를 활용하여 Success와 Failure 객체를 따로 나눈 두 리스트를 생성
        val successes = results.filterIsInstance<LoadResult.Success<*>>()
        val failures = results.filterIsInstance<LoadResult.Failure>()

        // PreloadReport 생성 반환
        PreloadReport(
            isAllSuccess = failures.isEmpty(),
            totalElapsedMs = elapsed,
            loadedDataMap = successes.associate { it.type to (it.data as Any) },
            failedTypes = failures.map { it.type }
        )
    }
}