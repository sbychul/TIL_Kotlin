// 추상 클래스 선언
abstract class Log(val message: String)

// 상속받는 데이터 클래스
data class ErrorLog(val msg: String, val code: Int) : Log(msg)
data class InfoLog(val msg: String) : Log(msg)

// inline과 reified를 활용한 필터링 확장 함수
// inline - 컴파일러가 이 함수를 호출하는 곳에 복사가 아닌 함수의 실제 알맹이 코드를 그대로 복사해서 붙여넣음 (호출 오버헤드 사라짐)
// reified - 타입 실체화, 코드가 호출부에 그대로 복사되어 컴파일러가 실시간으로 입력된 타입이 무엇인지 인지할 수 있게 가공.
inline fun <reified T : Log> List<Log>.filterLog() : List<String> {
    val result = this.asSequence() // 리스트를 시퀀스로 변환
        .filterIsInstance<T>() // 원하는 타입 T로 필터링
        .map { "[로그 감지] ${it.message}" } // 매핑
        .toList()

    // 새 리스트 반환
    return result
}

fun main() {
    // 기초 데이터 준비(로그 리스트)
    val logs = listOf(
        ErrorLog("데이터베이스 연결 실패", 500),
        InfoLog("사용자 로그인 완료"),
        ErrorLog("인증 토큰 만료", 401),
        InfoLog("페이지 조회 수 증가")
    )

    // 결과 확인
    val errLogs = logs.filterLog<ErrorLog>()
    println(errLogs)
}