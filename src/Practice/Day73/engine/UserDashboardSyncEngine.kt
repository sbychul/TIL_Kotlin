package Practice.Day73.engine

import Practice.Day73.model.ProcessedUserData
import Practice.Day73.model.ScreenState
import Practice.Day73.model.UserAccount
import Practice.Day73.model.UserActivityStats
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlin.system.measureTimeMillis

class UserDashboardSyncEngine {
    // 가상 API 메서드
    // 단지 서버나 DB 역할을 흉내내는 메서드, 5명의 유저 리스트를 반환한 한다.
    suspend fun fetchRawAccounts() : List<UserAccount> {
        delay(120) // 딜레이 시뮬레이션
        return listOf(
            UserAccount("U-01", "Faker", isBanned = false, tier = "VIP"), // Finals Weekend 파이팅
            UserAccount("U-02", "헬퍼", isBanned = true, tier = "STANDARD"), // 1단계에서 걸러질 예정
            UserAccount("U-03", "브론즈", isBanned = false, tier = "STANDARD"),
            UserAccount("U-04", "네트워크불안정", isBanned = false, tier = "NEWBIE"), // 2단계 통계 조회 시 예외 발생 -> 0점으로 복구
            UserAccount("U-05", "Keria", isBanned = false, tier = "VIP") // 결승은 꼭 가자
        )
    }
    // 유저 한 명의 스탯을 확인하는 메서드.
    suspend fun fetchActivityStats(userId: String): UserActivityStats {
        delay(80) // 의도적 딜레이 시뮬레이션

        // U-04 조회했을 때 의도적으로 예외를 던진다 (이름부터 네트워크 불안정이시잖아)
        if (userId == "U-04") { throw IllegalStateException("네트워크 타임아웃") }

        // 그냥 서버가 따로 있다고 치고 가상의 통계를 만들어서 반환하도록 합시다.
        return when (userId) {
            "U-01" -> UserActivityStats(userId = "U-01", postCount = 50, likeCount = 10) // 50*10 + 10*2 = 520점
            "U-03" -> UserActivityStats(userId = "U-03", postCount = 25, likeCount = 30) // 25*10 + 30*2 = 310점
            "U-05" -> UserActivityStats(userId = "U-05", postCount = 10, likeCount = 25) // 10*10 + 25*2 = 150점
            else -> UserActivityStats(userId = userId, postCount = 0, likeCount = 0)
        }
    }

    // 핵심, 동기화 파이프라인 메서드 구현
    suspend fun syncDashboard(): ScreenState<List<ProcessedUserData>> = coroutineScope {
        val userDataList: List<ProcessedUserData> // 유저 데이터를 담을 변수 선언

        // 전체 시간 측정 블록.
        val time = measureTimeMillis {
            // 가상 API 메서드를 통해 계정 목록을 전달받음.
            userDataList = fetchRawAccounts().filter { it.isBanned == false } // 정지 먹은 친구를 첫 단계에서 걸러냄.
                .map { account -> async { // 나머지 계정들에 조회 메서드를 병렬 실행
                    try {
                        val accStat = fetchActivityStats(account.id) // 스탯 fetch 함수를 호출
                        // 점수를 계산하여 UserData 객체 생성.
                        ProcessedUserData(account.id, account.username, account.tier, accStat.postCount * 10 + accStat.likeCount * 2)
                    } catch (e: CancellationException) {
                        throw e // 취소 예외는 자동으로 밖으로 던짐
                    } catch (e: Throwable) { // 통계 조회 실패 시 activityScore가 0인 기본 객체로 대체.
                        ProcessedUserData(account.id, account.username, account.tier, 0)
                    }
                } }.awaitAll() // awaitAll로 Deferred<List<ProcessedUserData>> 객체를 List<ProcessedUserData>로 변환.
                .sortedByDescending { it.activityScore }
        }
        if (userDataList.isNullOrEmpty()) { ScreenState.Error("표시할 수 있는 유효 사용자가 없습니다.") }
        else { ScreenState.Success(userDataList, time) }
    }
}