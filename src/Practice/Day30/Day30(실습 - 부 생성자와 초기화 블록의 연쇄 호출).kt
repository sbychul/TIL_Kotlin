package Practice.Day30

class GameUser (val nickname: String, val level: Int) {
    // 객체가 생성될 때 실행되는 init 블록
    // 놀랍게도 여러 개 작성할 수 있다. 여러 개가 있다면 위에서 아래로 작성된 순서대로 실행.
    init { println("[init 블록] 회원 생성 프로세스 개시 (닉네임: ${nickname})") }

    // 부 생성자 1: 닉네임만 받았을 때, 레벨은 기본값(1)으로 설정.
    // this()를 통해 주 생성자에 값을 넘겨준다.
    constructor(nickname: String) : this(nickname, 1) {
        println("[부 생성자 1 호출] 신규 가입 회원 세팅 완료")
    }

    // 부 생성자 2: 소셜 가입 회원용 생성자, 닉네임 뒤에 소셜 태그를 강제로 붙임
    // Q. this를 통해 level을 안 넘겨줬는데 어쩌죠?
    // A. 레벨 안 넣는 부 생성자 1로 넘어가서 그게 다시 주 생성자를 호출해 완전히 세팅. 출력 결과를 보도록 하자.
    constructor(rawNickName: String, platform: String) : this("${rawNickName}_${platform}") {
        println("[부 생성자 2 호출] 소셜 연동 가입 완료 (${platform})")
    }
}

fun main() {
    println("=== Case 1: 주 생성자로 직접 생성 ===")
    val user1 = GameUser("Faker", 99)

    println("\n=== Case 2: 부 생성자 1 (닉네임만 제공) ===")
    val user2 = GameUser("Keria")

    println("\n=== Case 3: 부 생성자 2 (소셜 연동 가입) ===")
    val user3 = GameUser("마크 주커버그", "Instagram")
}