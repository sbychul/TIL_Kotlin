package Practice.Day59

import kotlinx.coroutines.*

// 응용 과제 #8: 비동기 이미지 다운로더 및 캐시 매니저 (Async Image Loader)
// 오늘 준비한 실전 응용 과제 #8은 강의계획서의 [7주 차: 코루틴(Coroutines)]과 [5주 차: 객체지향/상태 모델링]을 결합한
// "비동기 이미지 다운로더 및 캐시 매니저 (Async Image Loader)"입니다.
// 안드로이드 실무나 Jetpack Compose 환경에서 네트워크 이미지를 비동기로 불러와 캐싱하는 라이브러리(Coil, Glide 등)의 핵심 동작 원리를 모방한 과제입니다.

// 다운로드 결과 상태를 나타내는 sealed 인터페이스
sealed interface ImageResult {
    // 다운로드 성공 시 URL, 데이터 크기(바이트), 캐시 적중 여부를 담음
    data class Success(val url: String, val dataSize: Int, val isFromCache: Boolean) : ImageResult
    // 실패 시 URL과 실패 사유를 담음.
    data class Failure(val url: String, val errorReason: String) : ImageResult
}

// 이미지 다운로더 클래스
class AsyncImageLoader{
    // 내부에 다운로드 완료된 이미지 크기를 보관하는 메모리 캐시 맵을 가짐
    private val cacheMap = mutableMapOf<String, Int>()

    // 이미지를 다운받는 함수
    suspend fun loadImage(url: String): ImageResult {
        // url이 캐시 맵에 이미 존재하는 경우 다운로드 절차를 건너뛰고 즉시 성공 객체를 반환함.
        if (cacheMap.contains(url)) { return ImageResult.Success(url, cacheMap.getValue(url), isFromCache = true) }
        // url이 https://로 시작하지 않으면 실패.
        if (!url.startsWith("https://")) { return ImageResult.Failure(url, "보안되지 않거나 잘못된 URL 프로토콜입니다.") }

        // 네트워크 다운로드 시뮬레이션
        delay(100)
        val size = url.length * 100 // 가상으로 길이 * 100바이트로 계산.
        cacheMap.put(url, size) // 계산된 크기를 내부 캐시 맵에 저장
        return ImageResult.Success(url, size, isFromCache = false) // 성공 객체를 반환.
    }

    // coroutineScope와 async / awaitAll을 사용하여 전달받은 모든 urls의 이미지를 동시에(병렬로) 다운로드하고, 완료된 List<ImageResult>를 반환.
    suspend fun loadAllImagesConcurrently(urls: List<String>): List<ImageResult> = coroutineScope {
        urls.map { url -> async { loadImage(url) } }.awaitAll().toList()
    }
}

// 테스트 케이스
fun main() = runBlocking {
    val loader = AsyncImageLoader()

    val urls = listOf(
        "https://example.com/image1.png",
        "http://insecure.com/image2.png", // 실패 케이스 (http)
        "https://example.com/image3.png",
        "https://example.com/image1.png"  // 캐시 적중 케이스 (중복 요청)
    )

    println("=== 비동기 병렬 이미지 다운로드 시작 ===")
    val results = loader.loadAllImagesConcurrently(urls)

    println("\n=== 처리 결과 리스트 ===")
    results.forEach { println(it) }

    println("\n=== 캐시 재요청 단일 테스트 ===")
    val reloaded = loader.loadImage("https://example.com/image1.png")
    println(reloaded) // isFromCache = true 여야 함
}