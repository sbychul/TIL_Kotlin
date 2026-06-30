// 가시성 변경자 private. 자바의 접근 지정자와 유사.
// private와 protected는 자바와 동일. 지정하지 않는다면 기본값은 public.
// 새 개념 internal: 같은 모듈(프로젝트/빌드) 내에서만 자유롭게 접근 가능.
class ServerManager(private val serverUrl: String) {
    // lateinit: 코틀린은 기본적으로 non-null이기 때문에 선언과 동시에 값을 주어야 하나,
    // 초기화 시점을 미루고 싶다면 lateinit 키워드를 사용한다.
    lateinit var status: String

    // by lazy: 변수를 선언해 두되, 프로그램 내에서 실제로 호출되어 사용되는 순간
    // 중괄호 블록이 실행되며 초기화되는 방식.
    val token: String by lazy {
        println("토큰이 최초 1회 생성되었습니다.")
        "SECRET_TOKEN_123" // 마지막 줄이 변수에 대입됨.
    }

    fun connect() {
        status = "CONNECTED" // 함수 실행과 동시에 변수를 초기화.
        println("[서버 연결] ${serverUrl} 에 접속했습니다.")
    }

    fun checkStatus() {
        if (!::status.isInitialized) { println("서버가 아직 준비되지 않았습니다."); return }
        println("서버에 정상적으로 연결되어 있습니다.")
    }
}

fun main() {
    val server = ServerManager("https://api.sbc.com/")

    server.checkStatus() // 서버 연결 이전 호출 결과 확인
    server.connect() // 서버 연결
    server.checkStatus() // 연결 이후 호출 결과 확인
    println(server.token) // token 프로퍼티를 1회차 출력.
    println(server.token) // token 프로퍼티를 2회차 출력. (토큰 생성 메시지가 뜨지 않음)
}