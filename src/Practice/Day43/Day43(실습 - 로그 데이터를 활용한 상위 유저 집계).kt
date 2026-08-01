package Practice.Day43

// 문제: 게임 로그 데이터를 활용한 Top-K 유저 집계
// 게임 서버에서 수집된 유저들의 로그 데이터 리스트가 주어집니다.
// 제시된 조건에 맞게 데이터를 가공하고 집계하여 가장 높은 총점(Total Score)을 기록한 상위 N명의 유저 ID 목록을 반환하는 함수를 작성하세요.

data class GameLog(val userId: String, val score: Int, val isSuccess: Boolean)

// isSuccess가 true인 로그만 점수 집계에 포함하여 총점을 합산해
// 내림차순 정렬하여 입력받은 유저 수만큼 userId를 리스트로 추출하는 함수
fun getTopUsers(logs: List<GameLog>, topK: Int): List<String> =
    logs.filter { it.isSuccess } // 성공한 것만 필터
        .groupBy { it.userId } // id를 기준으로 그룹화 (Map<String, List<GameLog>> 형태의 Map이 만들어짐)
        // 맵에서 userLogs 리스트를 빼내어 유저의 점수들을 합산
        // 언더바(_)는 Map의 Key는 필요 없다(사용하지 않는 매개변수)는 뜻.
        .mapValues { (_, userLogs) -> userLogs.sumOf { it.score } }
        .entries.sortedByDescending { it.value } // Map의 엔트리를 Value(점수의 총합)를 기준으로 내림차순 정렬
        .take(topK) // 상위 K개만을 잘라내
        .map { it.key } // key만을 추출

// 테스트 케이스
fun main() {
    val logs = listOf(
        GameLog("UserA", 100, true),
        GameLog("UserB", 50, false),
        GameLog("UserA", 150, true),
        GameLog("UserC", 300, true),
        GameLog("UserB", 200, true),
        GameLog("UserC", 50, false)
    )

    val top2Users = getTopUsers(logs, topK = 2)
    println("상위 2명 유저: $top2Users") // [UserC, UserA]
}