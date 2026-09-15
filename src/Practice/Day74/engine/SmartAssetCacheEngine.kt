package Practice.Day74.engine

import Practice.Day74.model.*
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

// 메인 메뉴, 비즈니스 로직
class SmartAssetCacheEngine {
    // 데이터의 Key를 key로, 데이터 자체를 Value로 가지는 map을 내부 프로퍼티로 관리
    private val memoryCache = mutableMapOf<String, AssetData>()

    // 전달받은 에셋을 memoryCache에 미리 등록하는 메서드
    fun primeCache(asset: AssetData) { memoryCache[asset.key] = asset }
    // 가상 네트워크 다운로드 메서드 (key를 입력받아 에셋을 반환(다운로드)) (가상 API 함수)
    private suspend fun fetchFromRemote(key: String) : AssetData {
        delay(100) // 네트워크 지연 시뮬레이션
        return AssetData(key = key, url = "[https://cdn.example.com/assets/$key.png](https://cdn.example.com/assets/$key.png)", sizeBytes = 2048L)
    }

    // 단일 에셋 조회 메서드
    suspend fun getOrFetch(key: String) : CacheResult<AssetData> {
        val cachedData = memoryCache[key]
        // 캐시에 있다면 즉시 반환 (인덱스 참조 연산자로 접근했을 때 없다면 null이 나온다.)
        if (cachedData != null) { return CacheResult.Hit(key, cachedData) }
        val remoteData: AssetData
        // 시간 측정
        val elapsed = measureTimeMillis {
            remoteData = fetchFromRemote(key) // 내부에서 가상 fetch API 메서드 호출
            primeCache(remoteData) // Map에 저장
        }
        // Miss 객체 반환
        return CacheResult.Miss(key, remoteData, elapsed)
    }

    // 다중 에셋 일괄 병렬 로드 메서드
    suspend fun getOrFetchAll(keys: List<String>) : BatchCacheReport<AssetData> = coroutineScope {
        val resultList: List<CacheResult<AssetData>>
        val elapsed = measureTimeMillis { 
            // 모든 key에 대하여 getOrFetch 시행, 이후 awaitAll로 모든 결과 수집
            resultList = keys.map { key -> async { getOrFetch(key) } }.awaitAll()
        }
        // 결과 반환
        BatchCacheReport(
            totalCount = keys.size,
            hitCount = resultList.count { it is CacheResult.Hit },
            missCount = resultList.count { it is CacheResult.Miss },
            totalElapsedMs = elapsed,
            results = resultList
        )
    }
}