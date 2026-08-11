package Practice.Day50

// 문제: JSON / HTML 스타일 프로필 빌더 (Type-Safe Profile Builder)
// 수신 객체 지정 람다: 람다 블록 내부에서 특정 객체를 this로 지정하여, 그 객체의 프로퍼티나 메서드에 this. 생략 후 직접 접근할 수 있게 해주는 특별한 람다 표현식.
// 이를 활용해, 아래와 같은 DSL 문법으로 사용자 프로필 데이터를 생성하고 최종 문자열로 출력하는 빌더를 구현하세요.

// 기본 프로필 데이터 구조
data class UserProfile(
    var name: String = "",
    var age: Int = 0,
    val skills: MutableList<String> = mutableListOf()
)

// 수신 객체 지정 람다 타입 표기법: ReceiverType.() -> Unit (일반 람다 (ReceiverType) -> Unit)
// UserProfile 객체를 생성한 뒤, 전달받은 init 람다 블록을 해당 객체의 수신 객체(this)로 실행하고 완성된 UserProfile을 반환하는 빌더 함수
fun profile(init: UserProfile.() -> Unit): UserProfile {
    val profile = UserProfile()
    // init을 실행함으로서 람다 블록 내부에서 profile이 this가 된다.
    profile.init()
    return profile
}

// UserProfile 클래스의 확장 함수로, skills 리스트에 새로운 기술 이름을 추가하는 함수.
fun UserProfile.skill(name: String) { this.skills.add(name) }

// UserProfile 클래스의 확장 함수로, 포맷팅된 문자열을 반환하는 함수.
fun UserProfile.toFormattedString(): String =
    "=== 사용자 프로필 ===\n이름: ${this.name}\n나이: ${this.age}\n보유 기술: ${skills}\n"

// 테스트 케이스
fun main() {
    // 우리가 직접 만든 DSL 문법으로 프로필 생성
    val user = profile { // 람다 내부에서 this를 생략해도 자동으로 매칭됨.
        name = "Faker"
        age = 30
        skill("6x World Champion")
        skill("2x MSI Titles")
        skill("10x LCK Titles")
    }

    println(user.toFormattedString())
}