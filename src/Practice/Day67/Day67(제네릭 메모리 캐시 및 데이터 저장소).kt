package Practice.Day67

import kotlinx.coroutines.*

// 제네릭 LRU 메모리 캐시 및 데이터 저장소 (Generic LRU Cache Engine)
// 강의계획서의 [5주 차: 제네릭(Generic) 및 데이터 캡슐화], [6주 차: 컬렉션 파이프라인/LinkedHashMap 순서 제어],
// 그리고 [7주 차: 비동기 코루틴 (delay, runBlocking)]을 결합한 "모바일 제네릭 LRU 캐시 매니저 (Generic LRU Cache Engine)"입니다.
// 안드로이드 이미지 캐싱(Glide/Coil)이나 모바일 로컬 데이터 저장소에서 메모리 부족(OOM)을 방지하기 위해 사용하는
// LRU(Least Recently Used) 교체 알고리즘을 제네릭으로 직접 구현해 볼 수 있습니다.

// 제한된 용량(maxCapacity)을 유지하며, 용량 초과 시 가장 오랫동안 참조되지 않은 항목을 우선 제거(Evict)하고 최신 참조 항목의 순서를 갱신하는 제네릭 캐시 엔진을 구현하세요.

// 캐시 조회 결과 제네릭 sealed 인터페이스
sealed interface CacheResult<out T> {
    data class Hit<T>(val value: T, val hitCount: Int) : CacheResult<T> // 캐시 적중 성공(조회 횟수 포함)
    data object Miss : CacheResult<Nothing> // 실패

    // Q: 왜 CacheResult<out T>인가?
    // A: 모든 타입의 최하위 타입 Nothing이 Miss일 때 인터페이스에 넘겨짐.
    // 그러나, Nothing이 다른 타입들의 하위 타입으로 인정받으려면 인터페이스에 out 키워드를 붙여줘야 한다.

    // out 키워드 설명(지난 20일자 자료에서 따옴)
    // out 키워드: 하위 타입을 상위 타입처럼 사용할 수 있게 하는 공변성(읽기 전용) 키워드.
    // 데이터의 흐름이 외부로 나가는 방향임을 컴파일러에게 보장한다.
}

// 캐시 엔트리 제네릭 클래스
// value의 타입을 나타내는 것이기 때문에 Type Parameter를 V로 작성하도록 합시다.
data class CacheEntry<V>(val key: String, val value: V, var hitCount: Int)

// 제네릭 LRU 캐시 클래스
// 생성자로 최대 크기를 전달받아 내부 프로퍼티로 Map을 관리함.
class LruMemoryCache<V>(val maxCapacity: Int) {
    private val cacheMap = LinkedHashMap<String, CacheEntry<V>>()

    // 추가 메서드
    fun put(key: String, value: V) {
        if (cacheMap.containsKey(key)) { // 이미 있는 Key라면
            cacheMap.remove(key) // 순서 갱신을 위해 이미 있던 것을 제거
        } else if (cacheMap.size >= maxCapacity) { // 최대 크기를 초과한다면
            // cacheMap의 key들 중에서 가장 오래된 첫 번째 것을 제거
            cacheMap.keys.firstOrNull()?.let { cacheMap.remove(it) }
        }
        // 맨 뒤에 입력받은 값을 CacheEntry로 감싸서 삽입.
        cacheMap[key] = CacheEntry(key, value, 0)
    }

    // get 메서드
    fun get(key: String): CacheResult<V> {
        val entry = cacheMap[key] ?: return CacheResult.Miss // 없으면 Miss
        entry.hitCount++ // 카운트 상승
        cacheMap.remove(key) // 순서 갱신을 위해 제거 후 다시 넣기
        cacheMap[key] = entry
        return CacheResult.Hit(entry.value, entry.hitCount) // 성공 결과 반환
    }

    // hitCount 내림차순 정렬 후 상위 limit개의 Pair<key, hitCount> 리스트 반환 메서드
    fun getMostFrequentlyUsedKeys(limit: Int): List<Pair<String, Int>> {
        if (limit <= 0) { throw IllegalArgumentException("올바르지 않은 limit 입력입니다.") }

        // limit개만큼 집어넣은 리스트 반환
        return cacheMap.values
            .sortedByDescending { it.hitCount }
            .take(limit)
            .map { it.key to it.hitCount }
    }

    suspend fun getOrFetch(key: String, fetcher: suspend () -> V): V =
        when (val result = get(key)) {
            is CacheResult.Hit -> result.value // 성공 시 그대로 반환
            is CacheResult.Miss -> { // 실패 시
                val fetchedValue = fetcher() // 람다 함수 실행
                put(key, fetchedValue) // 가져온 값을 저장
                fetchedValue // 이후 가져온 값을 반환
            }
        }
}

// 테스트 케이스
fun main() = runBlocking {
    // 최대 3개까지만 보관 가능한 String 캐시 생성
    val cache = LruMemoryCache<String>(maxCapacity = 3)

    println("=== 1. 기본 캐시 삽입 및 용량 초과 방출(Eviction) 테스트 ===")
    cache.put("user:1", "Alice")
    cache.put("user:2", "Bob")
    cache.put("user:3", "Charlie")

    // user:1을 조회 -> 최근 사용된 항목이 됨 (가장 오래된 항목은 user:2가 됨)
    val hit1 = cache.get("user:1")
    println("user:1 조회: $hit1")

    // 새 항목 추가 -> 용량 3 초과로 가장 오래 참조되지 않은 user:2가 제거되어야 함
    cache.put("user:4", "David")

    println("user:2 조회 (방출 확인): ${cache.get("user:2")}")
    println("user:3 조회: ${cache.get("user:3")}")
    println("user:4 조회: ${cache.get("user:4")}")

    println("\n=== 2. 누적 조회수(Hit Count) 상위 랭킹 확인 ===")
    // user:1을 2번 더 조회 (총 3회 조회)
    cache.get("user:1")
    cache.get("user:1")
    // user:3을 1번 더 조회 (총 2회 조회)
    cache.get("user:3")

    val topKeys = cache.getMostFrequentlyUsedKeys(limit = 2)
    topKeys.forEach { (key, count) -> println("키: $key, 조회수: ${count}회") }

    println("\n=== 3. getOrFetch (비동기 데이터 로더 연계) 테스트 ===")
    // "user:5"는 캐시에 없으므로 100ms 지연 후 네트워크 페치 시뮬레이션
    val user5 = cache.getOrFetch("user:5") {
        println("-> [Network] 원격 서버에서 user:5 프로필 다운로드 중...")
        delay(100)
        "Eve (Remote)"
    }
    println("가져온 값: $user5")

    // 두 번째 호출 시에는 캐시 Hit으로 네트워크를 타지 않고 즉시 반환
    val user5Cached = cache.getOrFetch("user:5") {
        println("-> [Network] 이 문구는 출력되지 않아야 합니다.")
        "Eve (New)"
    }
    println("캐시에서 재조회: $user5Cached")
}