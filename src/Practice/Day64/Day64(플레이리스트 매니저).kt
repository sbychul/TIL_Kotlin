package Practice.Day64

import kotlinx.coroutines.*

// 스마트 플레이리스트 매니저 및 비동기 트랙 프리페처 (Music Playlist Engine)
// [4주 차: 확장 함수 및 고차 함수], [5주 차: 객체지향/상태 캡슐화 및 Sealed 인터페이스]
// [6주 차: 컬렉션 다중 필터링, 그룹화, 통계 집계], [7주 차: 코루틴 비동기 병렬 처리 (coroutineScope, async, awaitAll)] 영역을 유기적으로 엮었습니다.
// 사용자의 플레이리스트에 담긴 트랙들의 장르와 재생 시간을 분석하고,
// 특정 조건(장르/재생시간)의 맞춤 믹스를 생성하며, 네트워크 환경에서 음원 메타데이터를 병렬 비동기로 로드하는 오디오 엔진을 구현하세요.

// 장르 enum
enum class Genre{ POP, ROCK, HIPHOP, JAZZ, INDIE }

// 트랙 데이터 클래스
data class Track(
    val id: String, // 트랙 ID
    val title: String, // 곡명
    val artist: String, // 아티스트명
    val genre: Genre, // 장르
    val durationSeconds: Int, // 재생 시간(초 단위)
    val isFavorite: Boolean = false // 좋아요 누름 여부 (기본값 false)
)

// 초 단위 재생 시간을 분:초 형식으로 변환하는 확장 함수
fun Track.toFormattedTime(): String {
    val minutes = durationSeconds / 60
    val seconds = durationSeconds - (minutes * 60)
    // 초가 10초 미만일 때 앞이 0이 채워지도록 하여 반환 (예: 6:09)
    return String.format("%d:%02d", minutes, seconds)
}

// 트랙 로드 상태 sealed 인터페이스
sealed interface TrackLoadResult {
    // 성공
    data class Success(val track: Track, val loadedAt: Long) : TrackLoadResult
    // 실패
    data class Failure(val trackId: String, val reason: String) : TrackLoadResult
}

// 플레이리스트 매니저 클래스
class PlaylistManager {
    // 내부 프로퍼티로 보관 중인 트랙 목록을 가짐
    private val trackList = mutableListOf<Track>()

    // 트랙 추가 메서드
    fun addTrack(track: Track) { trackList.add(track) }

    // 트랙 장르별 그룹화, 각 장르별 총 재생 시간의 합을 Map<Genre, Int>로 반환하는 메서드
    fun getGenrePlaytime(): Map<Genre, Int> =
        trackList.groupBy { it.genre } // 장르별로 그룹화 (Map<Genre, List<Track>>)
            .mapValues { (_, track) -> track.sumOf { it.durationSeconds } } // track들의 재생 시간을 Value로 만든 Map 반환

    // 조건에 맞는 추천 믹스 트랙 리스트를 생성하는 메서드
    fun createCustomMix(targetGenre: Genre?, maxTotalSeconds: Int): List<Track> {
        // targetGenre가 null이면 전부 통과, 아니라면 장르에 맞춤.
        val sortedList = trackList.filter { targetGenre == null || it.genre == targetGenre }
            .sortedWith(
                // Descending인 이유: true > false로 취급이기에 좋아요 우선으로 하려면 내림차순 정렬을 해야 함.
                compareByDescending<Track> { it.isFavorite }
                    .thenBy { it.durationSeconds } // 이후 재생 시간 오름차순 정렬
            )

        // 결과를 담을 리스트
        val result = mutableListOf<Track>()
        var totalSeconds = 0
        for (track in sortedList) { // 반복문 순회하며 최대 재생 시간을 초과하지 않도록 담음
            if (totalSeconds + track.durationSeconds <= maxTotalSeconds) {
                result.add(track)
                totalSeconds += track.durationSeconds
            }
        }

        return result
    }

    // 전달받은 trackIds를 병렬로 동시 로드하는 메서드
    suspend fun prefetchTracksConcurrently(trackIds: List<String>): List<TrackLoadResult> =
        coroutineScope {
            // map { async {...} }로 비동기 작업 동시 수행
            trackIds.map { trackId ->
                async {
                    delay(50) // 비동기 지연 시뮬레이션용
                    val track = trackList.find { it.id == trackId }
                    // 찾았는지 못 찾았는지 확인
                    if (track != null) {
                        TrackLoadResult.Success(track, System.currentTimeMillis())
                    } else {
                        TrackLoadResult.Failure(trackId, "트랙을 찾을 수 없습니다.")
                    }
                }
            }.awaitAll() // map의 결과로 List<Deferred<TrackLoadResult>>가 나오는 것에 awaitAll 호출, List<TrackLoadResult>로 반환
        }
}

// 테스트 케이스
fun main() = runBlocking {
    val manager = PlaylistManager()

    val t1 = Track("TR-01", "Hype Boy", "NewJeans", Genre.POP, durationSeconds = 179, isFavorite = true)
    val t2 = Track("TR-02", "고스트", "amazarashi", Genre.ROCK, durationSeconds = 395, isFavorite = true)
    val t3 = Track("TR-03", "Ditto", "NewJeans", Genre.POP, durationSeconds = 185, isFavorite = false)
    val t4 = Track("TR-04", "All the Stars", "Kendrick Lamar", Genre.HIPHOP, durationSeconds = 232, isFavorite = false)
    val t5 = Track("TR-05", "계절은 차례차례 죽어간다", "amazarashi", Genre.ROCK, durationSeconds = 339, isFavorite = false)

    manager.addTrack(t1)
    manager.addTrack(t2)
    manager.addTrack(t3)
    manager.addTrack(t4)
    manager.addTrack(t5)

    println("=== 1. 트랙 재생 시간 포맷팅 확인 ===")
    println("${t1.title} (${t1.toFormattedTime()})")
    println("${t2.title} (${t2.toFormattedTime()})")

    println("\n=== 2. 장르별 총 재생 시간 집계 ===")
    val genreTime = manager.getGenrePlaytime()
    genreTime.forEach { (genre, sec) -> println("$genre: ${sec}초") }

    println("\n=== 3. 맞춤 믹스 생성 (ROCK 장르, 최대 600초) ===")
    // ROCK 트랙: t2(좋아요, 275초), t5(337초) -> 합 612초이므로 t5까지 넣으면 600초 초과. 따라서 t2만 포함.
    val rockMix = manager.createCustomMix(targetGenre = Genre.ROCK, maxTotalSeconds = 600)
    rockMix.forEach { println("-> [Mix] ${it.title} (${it.toFormattedTime()})") }

    println("\n=== 4. 코루틴 병렬 트랙 프리페치 ===")
    val fetchIds = listOf("TR-01", "TR-03", "TR-99") // TR-99는 실패 케이스
    val results = manager.prefetchTracksConcurrently(fetchIds)
    results.forEach { res ->
        when (res) {
            is TrackLoadResult.Success ->
                println("[로드 성공] ${res.track.title} by ${res.track.artist}")
            is TrackLoadResult.Failure ->
                println("[로드 실패] ID: ${res.trackId} - ${res.reason}")
        }
    }
}