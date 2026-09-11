package Practice.Day73

import Practice.Day73.engine.UserDashboardSyncEngine
import Practice.Day73.model.ScreenState
import kotlinx.coroutines.runBlocking

// 응용 과제: 모바일 커뮤니티 활성 사용자 대시보드 동기화 엔진 (Active User Dashboard Engine)
// 단순히 비동기 함수를 호출하는 수준을 넘어, 실제 안드로이드 아키텍처의 비즈니스 로직(MVI/MVVM 패턴의 StateFlow/ViewModel 백엔드 엔진)과 동일한 구조를 설계합니다.
// 원격 API에서 받아온 원시(Raw) 데이터를 검증하고 필터링한 뒤, 각 유저별 세부 통계를 병렬로 수집하여 최종 UI 상태(sealed interface ScreenState)로 환원하는 파이프라인입니다.

fun main() = runBlocking {
    val engine = UserDashboardSyncEngine()

    println("=== 모바일 대시보드 데이터 동기화 파이프라인 가동 ===")
    val resultState = engine.syncDashboard()

    when (resultState) {
        is ScreenState.Loading -> {
            println("[화면 상태] 로딩 중...")
        }
        is ScreenState.Success -> {
            println("\n[화면 상태] 동기화 성공! (총 소요 시간: ${resultState.totalElapsedMs}ms)")
            println("--- 활성 유저 랭킹 (점수 내림차순) ---")
            resultState.data.forEachIndexed { index, user ->
                println("${index + 1}위: [${user.tier}] ${user.username} (ID: ${user.id}) -> 점수: ${user.activityScore}점")
            }
        }
        is ScreenState.Error -> {
            println("\n[화면 상태] 에러 발생: ${resultState.msg}")
        }
    }
}