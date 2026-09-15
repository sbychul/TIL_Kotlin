package Practice.Day74

// 모바일 스마트 에셋 캐시 매니저 (Smart Asset Cache Manager)
// 강의계획서(3~7주 차: 제네릭, Sealed 클래스, MutableMap 상태 저장소, 코루틴 비동기 로딩 및 병렬 async/awaitAll) 범위를 준수하며,
// 메모리 캐시 적중(즉시 반환, 0ms)과 네트워크 폴백(비동기 지연 다운로드 및 캐시 갱신) 로직을 직접 조립하는 실전 과제입니다.
// 안드로이드의 Glide, Coil 같은 이미지 로더나 네트워크 SDK의 핵심 원리는 단순합니다.
// "있으면 메모리에서 바로 꺼내고(0ms), 없으면 서버에서 받아와서 메모리에 넣어둔 뒤 돌려준다"는 Cache-Aside 패턴입니다.

import Practice.Day74.engine.SmartAssetCacheEngine
import Practice.Day74.model.*
import kotlinx.coroutines.runBlocking

// 테스트 케이스
fun main() = runBlocking {
    val engine = SmartAssetCacheEngine()

    // 1. "APP_LOGO"는 사전에 캐시에 워밍업(사전 등록)
    engine.primeCache(AssetData("APP_LOGO", "https://cdn.site.com/logo.png", 512L))

    println("=== 1회차 요청: [APP_LOGO, BANNER_01, BANNER_02, USER_AVATAR] ===")
    // APP_LOGO는 Hit(0ms), 나머지 3개는 병렬 Miss(동시 다운로드로 약 100ms 기대)
    val firstReport = engine.getOrFetchAll(listOf("APP_LOGO", "BANNER_01", "BANNER_02", "USER_AVATAR"))

    println("총 요청 수: ${firstReport.totalCount} (Hit: ${firstReport.hitCount}, Miss: ${firstReport.missCount})")
    println("총 소요 시간: ${firstReport.totalElapsedMs}ms (약 100~120ms 기대)")
    firstReport.results.forEach { res ->
        when (res) {
            is CacheResult.Hit -> println("  [HIT] ${res.key} -> 캐시에서 즉시 로드 (0ms)")
            is CacheResult.Miss -> println("  [MISS] ${res.key} -> 원격 다운로드 완료 (${res.elapsedMs}ms 소요)")
        }
    }

    println("\n=== 2회차 요청 (완전 동일한 4개 키 재요청) ===")
    // 1회차에서 전부 캐시에 저장되었으므로 4개 모두 Hit(0ms 내외)여야 함
    val secondReport = engine.getOrFetchAll(listOf("APP_LOGO", "BANNER_01", "BANNER_02", "USER_AVATAR"))

    println("총 요청 수: ${secondReport.totalCount} (Hit: ${secondReport.hitCount}, Miss: ${secondReport.missCount})")
    println("총 소요 시간: ${secondReport.totalElapsedMs}ms (전부 캐시 적중이므로 0~10ms 기대)")
    secondReport.results.forEach { res ->
        when (res) {
            is CacheResult.Hit -> println("  [HIT] ${res.key} -> 캐시에서 즉시 로드 (0ms)")
            is CacheResult.Miss -> println("  [MISS] ${res.key} -> 원격 다운로드 완료 (${res.elapsedMs}ms 소요)")
        }
    }
}