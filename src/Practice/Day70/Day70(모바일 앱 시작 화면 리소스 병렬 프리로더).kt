package Practice.Day70

// 모바일 앱 시작 화면 리소스 병렬 프리로더 (App Splash Resource Preloader)
// 오늘은 체감 난이도를 '중하' 수준으로 완화하여, 복잡한 비트 연산이나 재시도 루프 대신
// "코루틴의 동작 흐름(순차 실행 vs 병렬 실행)이 머릿속에 직관적으로 그려지는 과제"로 준비했습니다.
// 안드로이드 앱을 처음 켤 때 스플래시 화면(Splash Screen)에서 원격 설정(Remote Config), 사용자 세션(User Session), 필수 UI 에셋(Asset)을 로딩하는 파이프라인입니다.
// 순차적으로 로딩하면 시간이 너무 오래 걸리므로, 각 리소스를 async로 동시에 병렬 로딩한 뒤 총 소요 시간을 단축하고 결과를 집계하는 엔진을 만듭니다.

import Practice.Day70.engine.*
import kotlinx.coroutines.*

fun main() = runBlocking {
    val preloader = SplashPreloaderEngine()

    println("=== 1. 모든 리소스 정상 병렬 로딩 테스트 ===")
    val report1 = preloader.preloadAll(shouldAssetFail = false)
    println("전체 로딩 성공 여부: ${report1.isAllSuccess}")
    println("총 소요 시간: ${report1.totalElapsedMs}ms (순차 실행 시 370ms 이상 걸림)")
    println("로드 완료된 데이터:")
    report1.loadedDataMap.forEach { (type, data) ->
        println("  -> [$type] $data")
    }
    println("실패한 리소스: ${report1.failedTypes}")

    println("\n=== 2. 특정 리소스(Assets) 로딩 실패 시나리오 테스트 ===")
    val report2 = preloader.preloadAll(shouldAssetFail = true)
    println("전체 로딩 성공 여부: ${report2.isAllSuccess}")
    println("로드 완료된 데이터: ${report2.loadedDataMap}")
    println("실패한 리소스: ${report2.failedTypes}")
}